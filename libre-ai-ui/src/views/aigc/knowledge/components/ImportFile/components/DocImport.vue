<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import {
  ElMessage,
  ElUpload,
  ElButton,
  ElProgress,
  ElCard,
  ElTag,
  ElEmpty,
  ElTooltip,
  type UploadRequestOptions,
  type UploadFile
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { embeddingDocs } from '@/api/aigc/embedding';
import {
  UploadFilled,
  Delete,
  Document,
  Check,
  Close
} from '@element-plus/icons-vue';

interface Props {
  data?: any;
}
defineProps<Props>();

const route = useRoute();
const fileList = ref<UploadFile[]>([]);
const uploadingFiles = ref<Map<string, number>>(new Map());

const acceptFormats = '.doc,.docx,.pdf,.txt,.md,.json,.csv';
const maxFileSize = 10 * 1024 * 1024; // 10MB

const supportedFormats = [
  { ext: '.txt', icon: 'tabler:file-text', color: '#666' },
  { ext: '.md', icon: 'tabler:markdown', color: '#000' },
  { ext: '.pdf', icon: 'tabler:file-type-pdf', color: '#d93831' },
  { ext: '.doc', icon: 'tabler:file-type-doc', color: '#2b5797' },
  { ext: '.docx', icon: 'tabler:file-type-docx', color: '#2b5797' },
  { ext: '.json', icon: 'tabler:json', color: '#f39c12' },
  { ext: '.csv', icon: 'tabler:file-type-csv', color: '#27ae60' }
];

const handleImport = async (options: UploadRequestOptions) => {
  const { file, onProgress, onSuccess, onError } = options;
  const kbId = route.params.id;

  // 文件大小检查
  if (file.size > maxFileSize) {
    ElMessage.error(`文件 ${file.name} 超过10MB限制`);
    onError?.(new Error('文件大小超出限制'));
    return;
  }

  // 设置上传进度
  uploadingFiles.value.set(file.uid, 0);

  try {
    const response = await embeddingDocs(
      String(kbId),
      { file: file },
      progressEvent => {
        const percent = Math.round(
          (progressEvent.loaded * 100) / Number(progressEvent.total)
        );
        uploadingFiles.value.set(file.uid, percent);
        onProgress?.({ percent });
      }
    );

    uploadingFiles.value.delete(file.uid);
    ElMessage.success(`文档 ${file.name} 上传成功，正在解析...`);
    onSuccess?.(response);
  } catch (error) {
    uploadingFiles.value.delete(file.uid);
    console.error('上传失败:', error);
    ElMessage.error(`文档 ${file.name} 上传失败`);
    onError?.(error as Error);
  }
};

const beforeUpload = (file: File) => {
  const fileExt = '.' + file.name.split('.').pop()?.toLowerCase();
  const isSupported = supportedFormats.some(f => f.ext === fileExt);

  if (!isSupported) {
    ElMessage.error(`不支持的文件格式: ${fileExt}`);
    return false;
  }

  if (file.size > maxFileSize) {
    ElMessage.error(`文件大小不能超过 10MB`);
    return false;
  }

  return true;
};

const handleRemove = (file: UploadFile) => {
  uploadingFiles.value.delete(file.uid);
};

const getFileIcon = (fileName: string) => {
  const ext = '.' + fileName.split('.').pop()?.toLowerCase();
  const format = supportedFormats.find(f => f.ext === ext);
  return format || { icon: 'tabler:file', color: '#666' };
};

const totalFiles = computed(() => fileList.value.length);
const uploadingCount = computed(() => uploadingFiles.value.size);
</script>

<template>
  <div class="doc-import-container">
    <!-- 上传区域 -->
    <div
      class="upload-section bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6"
    >
      <div class="section-header mb-4">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">
          <Icon icon="tabler:file-upload" class="mr-2" />
          文档上传
        </h3>
        <p class="text-sm text-gray-600 dark:text-gray-400">
          支持批量上传文档，系统会自动解析并向量化处理
        </p>
      </div>

      <el-upload
        v-model:file-list="fileList"
        :http-request="handleImport"
        :before-upload="beforeUpload"
        :on-remove="handleRemove"
        :accept="acceptFormats"
        multiple
        drag
        class="upload-area"
      >
        <div class="upload-content">
          <Icon
            icon="tabler:cloud-upload"
            class="text-6xl text-blue-400 mb-4"
          />
          <div
            class="text-lg font-medium text-gray-700 dark:text-gray-300 mb-2"
          >
            点击或拖拽文件到此处上传
          </div>
          <div class="text-sm text-gray-500 dark:text-gray-400 mb-4">
            支持批量上传，单个文件不超过 10MB
          </div>

          <!-- 支持的格式展示 -->
          <div class="supported-formats flex flex-wrap gap-2 justify-center">
            <el-tag
              v-for="format in supportedFormats"
              :key="format.ext"
              effect="plain"
            >
              <div class="flex flex-row gap-1">
                <Icon :icon="format.icon" :style="{ color: format.color }" />
                <span> {{ format.ext }}</span>
              </div>
            </el-tag>
          </div>
        </div>
      </el-upload>
    </div>

    <!-- 文件列表 -->
    <div
      v-if="fileList.length > 0"
      class="files-section bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6 mt-4"
    >
      <div class="section-header mb-4">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
            <Icon icon="tabler:files" class="mr-2" />
            上传列表
          </h3>
          <el-tag type="info">
            共 {{ totalFiles }} 个文件
            <span v-if="uploadingCount > 0"
              >，{{ uploadingCount }} 个上传中</span
            >
          </el-tag>
        </div>
      </div>

      <div class="files-list space-y-3">
        <el-card
          v-for="file in fileList"
          :key="file.uid"
          class="file-card"
          shadow="hover"
        >
          <div class="flex items-center gap-4">
            <!-- 文件图标 -->
            <div class="file-icon">
              <Icon
                :icon="getFileIcon(file.name).icon"
                :style="{ color: getFileIcon(file.name).color }"
                class="text-3xl"
              />
            </div>

            <!-- 文件信息 -->
            <div class="file-info flex-1">
              <div class="flex items-center gap-2">
                <span class="font-medium text-gray-900 dark:text-white">
                  {{ file.name }}
                </span>
                <el-tag
                  v-if="file.status === 'success'"
                  type="success"
                  size="small"
                  :icon="Check"
                >
                  已上传
                </el-tag>
                <el-tag
                  v-else-if="file.status === 'fail'"
                  type="danger"
                  size="small"
                  :icon="Close"
                >
                  上传失败
                </el-tag>
              </div>
              <div class="text-sm text-gray-500 dark:text-gray-400 mt-1">
                大小: {{ (file.size! / 1024).toFixed(2) }} KB
              </div>

              <!-- 上传进度 -->
              <div v-if="uploadingFiles.has(file.uid)" class="mt-2">
                <el-progress
                  :percentage="uploadingFiles.get(file.uid)"
                  :stroke-width="6"
                  :format="(percent: number) => `${percent}%`"
                />
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="file-actions">
              <el-tooltip content="删除" placement="top">
                <el-button
                  text
                  type="danger"
                  size="small"
                  :icon="Delete"
                  @click="
                    () => {
                      const index = fileList.findIndex(f => f.uid === file.uid);
                      if (index > -1) fileList.splice(index, 1);
                      uploadingFiles.delete(file.uid);
                    }
                  "
                />
              </el-tooltip>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.doc-import-container {
  height: 100%;
  overflow-y: auto;
}

.upload-area {
  :deep(.el-upload) {
    width: 100%;

    .el-upload-dragger {
      border: 2px dashed var(--el-border-color-lighter);
      border-radius: 12px;
      background: linear-gradient(to bottom, #f8fafc, #f1f5f9);
      transition: all 0.3s;

      .dark & {
        background: linear-gradient(
          to bottom,
          rgba(30, 41, 59, 0.5),
          rgba(15, 23, 42, 0.5)
        );
      }

      &:hover {
        border-color: var(--el-color-primary);
        background: linear-gradient(to bottom, #eff6ff, #dbeafe);

        .dark & {
          background: linear-gradient(
            to bottom,
            rgba(59, 130, 246, 0.1),
            rgba(37, 99, 235, 0.1)
          );
        }
      }

      &.is-dragover {
        border-color: var(--el-color-primary);
        background: linear-gradient(to bottom, #dbeafe, #bfdbfe);

        .dark & {
          background: linear-gradient(
            to bottom,
            rgba(59, 130, 246, 0.2),
            rgba(37, 99, 235, 0.2)
          );
        }
      }
    }
  }
}

.upload-content {
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.file-card {
  :deep(.el-card__body) {
    padding: 12px 16px;
  }

  &:hover {
    transform: translateY(-2px);
    border-color: var(--el-color-primary-light-3);
  }
}

.file-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background: var(--el-fill-color-light);
  border-radius: 8px;
}

.supported-formats {
  :deep(.el-tag) {
    border-radius: 6px;
  }
}
</style>
