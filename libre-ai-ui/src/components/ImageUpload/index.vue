<script lang="ts" setup>
import { ref, computed } from 'vue';
import { ElMessage, ElUpload } from 'element-plus';
import { Icon } from '@iconify/vue';
import type { UploadProps, UploadUserFile } from 'element-plus';

interface Props {
  modelValue?: string[];
  disabled?: boolean;
  maxCount?: number;
  maxSize?: number; // MB
  accept?: string;
}

interface Emit {
  (ev: 'update:modelValue', value: string[]): void;
  (ev: 'change', value: string[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  disabled: false,
  maxCount: 5,
  maxSize: 10,
  accept: 'image/*'
});

const emit = defineEmits<Emit>();

const fileList = ref<UploadUserFile[]>([]);
const uploading = ref(false);

// 图片预览
const dialogImageUrl = ref('');
const dialogVisible = ref(false);

const imageUrls = computed({
  get: () => props.modelValue || [],
  set: (value: string[]) => {
    emit('update:modelValue', value);
    emit('change', value);
  }
});

// 上传前验证
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt10M = file.size / 1024 / 1024 < props.maxSize;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  if (!isLt10M) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB!`);
    return false;
  }
  
  return true;
};

// 上传成功
const handleSuccess = (response: any, file: any) => {
  uploading.value = false;
  
  // 这里应该处理后端返回的图片URL
  // 暂时使用本地预览URL
  const url = URL.createObjectURL(file.raw);
  imageUrls.value = [...imageUrls.value, url];
  
  ElMessage.success('图片上传成功');
};

// 上传失败
const handleError = (error: any) => {
  uploading.value = false;
  console.error('上传失败:', error);
  ElMessage.error('图片上传失败，请重试');
};

// 开始上传
const handleProgress = () => {
  uploading.value = true;
};

// 移除图片
const handleRemove = (file: any, fileList: UploadUserFile[]) => {
  const index = props.modelValue.findIndex(url => url === file.url);
  if (index > -1) {
    const newUrls = [...props.modelValue];
    newUrls.splice(index, 1);
    imageUrls.value = newUrls;
  }
};

// 预览图片
const handlePreview = (file: UploadUserFile) => {
  dialogImageUrl.value = file.url!;
  dialogVisible.value = true;
};

// 超出数量限制
const handleExceed = () => {
  ElMessage.warning(`最多只能上传 ${props.maxCount} 张图片`);
};

// 自定义上传
const customUpload = (options: any) => {
  const { file } = options;
  
  // 创建本地预览URL（实际项目中应该上传到服务器）
  const url = URL.createObjectURL(file);
  
  // 模拟上传过程
  setTimeout(() => {
    imageUrls.value = [...imageUrls.value, url];
    uploading.value = false;
    ElMessage.success('图片上传成功');
  }, 1000);
  
  uploading.value = true;
};
</script>

<template>
  <div class="image-upload-container">
    <el-upload
      v-model:file-list="fileList"
      :disabled="disabled || uploading"
      :multiple="true"
      :limit="maxCount"
      :accept="accept"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-progress="handleProgress"
      :on-remove="handleRemove"
      :on-preview="handlePreview"
      :on-exceed="handleExceed"
      :http-request="customUpload"
      list-type="picture-card"
      class="image-uploader"
    >
      <div v-if="!uploading" class="upload-trigger">
        <Icon icon="ri:image-add-line" class="text-2xl text-gray-400" />
        <div class="text-xs text-gray-500 mt-1">点击上传</div>
      </div>
      <div v-else class="upload-loading">
        <Icon icon="ri:loader-4-line" class="text-2xl text-blue-500 animate-spin" />
        <div class="text-xs text-blue-500 mt-1">上传中...</div>
      </div>
    </el-upload>

    <!-- 图片预览对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="图片预览"
      width="70%"
      :modal="true"
    >
      <div class="text-center">
        <img 
          :src="dialogImageUrl" 
          alt="预览图片"
          class="max-w-full max-h-96 object-contain"
        />
      </div>
    </el-dialog>

    <!-- 上传提示 -->
    <div v-if="maxCount > 1" class="upload-tip">
      <div class="text-xs text-gray-500">
        <Icon icon="ri:information-line" class="mr-1" />
        支持 {{ accept === 'image/*' ? '图片' : accept }} 格式，单个文件不超过 {{ maxSize }}MB，最多 {{ maxCount }} 张
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.image-upload-container {
  .image-uploader {
    :deep(.el-upload) {
      border: 2px dashed #d1d5db;
      border-radius: 8px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s ease;

      &:hover {
        border-color: #3b82f6;
        background-color: #f8fafc;
      }
    }

    :deep(.el-upload-list__item) {
      border-radius: 8px;
      overflow: hidden;
    }
  }

  .upload-trigger,
  .upload-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    min-height: 80px;
  }

  .upload-tip {
    margin-top: 8px;
    display: flex;
    align-items: center;
  }
}

// 深色模式
.dark {
  .image-upload-container {
    .image-uploader {
      :deep(.el-upload) {
        border-color: #4b5563;
        background-color: #374151;

        &:hover {
          border-color: #3b82f6;
          background-color: #4b5563;
        }
      }
    }

    .upload-tip {
      color: #9ca3af;
    }
  }
}
</style>