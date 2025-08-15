<script lang="ts" setup>
import { ref, reactive, nextTick, computed } from 'vue';
import { ElMessage, ElDialog } from 'element-plus';
import {
  Close,
  Upload,
  Picture,
  InfoFilled,
  Document,
  Connection
} from '@element-plus/icons-vue';
import { add, getById, update } from '@/api/aigc/app';
import { uploadApi } from '@/api/aigc/oss';
import ModelSelect from '@/views/common/ModelSelect.vue';

const emit = defineEmits(['success']);

const dialogVisible = ref(false);
const loading = ref(false);
const uploading = ref(false);
const isEdit = ref(false);

const formData = reactive({
  id: '',
  name: '',
  modelId: '',
  cover: '',
  des: ''
});

const formRules = {
  name: [
    { required: true, message: '请输入应用名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  modelId: [{ required: true, message: '请选择关联模型', trigger: 'change' }]
};

const dialogTitle = computed(() => (isEdit.value ? '编辑应用' : '创建新应用'));

async function open(app?: any) {
  dialogVisible.value = true;
  isEdit.value = !!app;

  // 重置表单
  Object.keys(formData).forEach(key => {
    formData[key] = '';
  });

  if (app?.id) {
    loading.value = true;
    try {
      const response = await getById(app.id);
      // 处理响应数据结构
      const data = response?.result || response;

      // 确保数据正确赋值
      formData.id = data.id || app.id;
      formData.name = data.name || '';
      formData.modelId = data.modelId || data.model?.id || '';
      formData.cover = data.cover || '';
      formData.des = data.des || data.description || '';
    } catch (error) {
      console.error('加载应用信息失败:', error);
      ElMessage.error('加载应用信息失败');
    } finally {
      loading.value = false;
    }
  }
}

async function handleSubmit() {
  if (!formData.name.trim()) {
    ElMessage.warning('请输入应用名称');
    return;
  }

  if (!formData.modelId) {
    ElMessage.warning('请选择关联模型');
    return;
  }

  loading.value = true;
  try {
    if (isEdit.value) {
      await update(formData);
      ElMessage.success('应用更新成功');
    } else {
      await add(formData);
      ElMessage.success('应用创建成功');
    }
    dialogVisible.value = false;
    emit('success');
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error(isEdit.value ? '更新失败，请重试' : '创建失败，请重试');
  } finally {
    loading.value = false;
  }
}

async function handleFileUpload(event: Event) {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];

  if (!file) return;

  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!validTypes.includes(file.type)) {
    ElMessage.error('只支持 JPG、PNG、GIF、WEBP 格式的图片');
    return;
  }

  // 验证文件大小（最大 5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB');
    return;
  }

  uploading.value = true;
  try {
    const res = await uploadApi({ file });
    formData.cover = res.url;
    ElMessage.success('封面上传成功');
  } catch (error) {
    console.error('上传失败:', error);
    ElMessage.error('上传失败，请重试');
  } finally {
    uploading.value = false;
  }
}

function onModelChange(model: any) {
  formData.modelId = model.id;
}

function removeCover() {
  formData.cover = '';
}

defineExpose({ open });
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    class="app-edit-dialog"
  >
    <div v-loading="loading" class="dialog-content">
      <!-- 表单卡片区域 -->
      <div class="form-sections">
        <!-- 基础信息卡片 -->
        <div class="form-card">
          <div class="card-header">
            <el-icon class="card-icon" :size="20" color="#6366F1">
              <InfoFilled />
            </el-icon>
            <h3 class="card-title">基础信息</h3>
          </div>

          <div class="card-body">
            <!-- 应用名称 -->
            <div class="form-item">
              <label class="form-label required">应用名称</label>
              <el-input
                v-model="formData.name"
                placeholder="给您的应用起个名字"
                maxlength="50"
                show-word-limit
                clearable
                class="form-input"
              />
              <span class="form-hint">建议使用简洁明了的名称，便于识别</span>
            </div>

            <!-- 应用描述 -->
            <div class="form-item">
              <label class="form-label">应用描述</label>
              <el-input
                v-model="formData.des"
                type="textarea"
                :rows="4"
                placeholder="简要描述应用的功能和用途"
                maxlength="200"
                show-word-limit
                class="form-input"
              />
              <span class="form-hint">详细的描述有助于用户理解应用的作用</span>
            </div>
          </div>
        </div>

        <!-- 模型配置卡片 -->
        <div class="form-card">
          <div class="card-header">
            <el-icon class="card-icon" :size="20" color="#8B5CF6">
              <Connection />
            </el-icon>
            <h3 class="card-title">模型配置</h3>
          </div>

          <div class="card-body">
            <div class="form-item">
              <label class="form-label required">关联模型</label>
              <ModelSelect
                :id="formData.modelId"
                class="form-input"
                @update="onModelChange"
                @load="onModelChange"
              />
              <span class="form-hint">选择适合您应用场景的 AI 模型</span>
            </div>
          </div>
        </div>

        <!-- 视觉设置卡片 -->
        <div class="form-card">
          <div class="card-header">
            <el-icon class="card-icon" :size="20" color="#EC4899">
              <Picture />
            </el-icon>
            <h3 class="card-title">视觉设置</h3>
          </div>

          <div class="card-body">
            <div class="form-item">
              <label class="form-label">应用封面</label>

              <!-- 图片上传区域 -->
              <div class="upload-area">
                <div v-if="!formData.cover" class="upload-trigger">
                  <input
                    type="file"
                    accept="image/*"
                    class="file-input"
                    :disabled="uploading"
                    @change="handleFileUpload"
                  />
                  <div class="upload-placeholder">
                    <el-icon class="upload-icon" :size="32" color="#C0C4CC">
                      <Upload />
                    </el-icon>
                    <p class="upload-text">
                      {{ uploading ? '上传中...' : '点击或拖拽上传图片' }}
                    </p>
                    <p class="upload-tips">
                      支持 JPG、PNG、GIF、WEBP 格式，最大 5MB
                    </p>
                  </div>
                </div>

                <!-- 图片预览 -->
                <div v-else class="image-preview">
                  <img
                    :src="formData.cover"
                    alt="封面预览"
                    class="preview-image"
                  />
                  <div class="preview-overlay">
                    <el-button
                      type="danger"
                      :icon="Close"
                      circle
                      size="small"
                      @click="removeCover"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '创建应用' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
:deep(.app-edit-dialog) {
  .el-dialog__header {
    padding: 20px 24px;
    border-bottom: 1px solid #e4e7ed;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
  }

  .el-dialog__body {
    max-height: 60vh;
    padding: 0;
    overflow-y: auto;
  }

  .el-dialog__footer {
    padding: 16px 24px;
    border-top: 1px solid #e4e7ed;
  }
}

.dialog-content {
  padding: 24px;
}

.form-sections {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// 表单卡片样式
.form-card {
  overflow: hidden;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 12px rgb(0 0 0 / 8%);
  }

  .card-header {
    display: flex;
    gap: 8px;
    align-items: center;
    padding: 16px 20px;
    background: linear-gradient(135deg, #f5f7fa, #fafbfc);
    border-bottom: 1px solid #e4e7ed;

    .card-icon {
      filter: drop-shadow(0 2px 4px rgb(0 0 0 / 10%));
    }

    .card-title {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .card-body {
    padding: 20px;
  }
}

// 表单项样式
.form-item {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }

  .form-label {
    display: block;
    margin-bottom: 8px;
    font-size: 14px;
    font-weight: 500;
    color: #606266;

    &.required::after {
      margin-left: 4px;
      color: #f56c6c;
      content: '*';
    }
  }

  .form-input {
    width: 100%;
  }

  .form-hint {
    display: block;
    margin-top: 6px;
    font-size: 12px;
    line-height: 1.4;
    color: #909399;
  }
}

// 上传区域样式
.upload-area {
  .upload-trigger {
    position: relative;
    width: 100%;
    height: 160px;
    cursor: pointer;
    border: 2px dashed #dcdfe6;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      background: #f5f7fa;
      border-color: #409eff;
    }

    .file-input {
      position: absolute;
      width: 100%;
      height: 100%;
      cursor: pointer;
      opacity: 0;
    }

    .upload-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      pointer-events: none;

      .upload-icon {
        margin-bottom: 8px;
      }

      .upload-text {
        margin: 0 0 4px;
        font-size: 14px;
        color: #606266;
      }

      .upload-tips {
        margin: 0;
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .image-preview {
    position: relative;
    width: 100%;
    height: 200px;
    overflow: hidden;
    border-radius: 8px;

    .preview-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .preview-overlay {
      position: absolute;
      inset: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgb(0 0 0 / 50%);
      opacity: 0;
      transition: opacity 0.3s;

      &:hover {
        opacity: 1;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>
