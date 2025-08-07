<script lang="ts" setup>
import { DownloadOutline } from '@vicons/ionicons5';
import { useRouter } from 'vue-router';
import { ElMessage, type UploadRequestOptions } from 'element-plus';
import { embeddingDocs } from '@/api/aigc/embedding';
import { ref } from 'vue';

const router = useRouter();
const fileList = ref<any[]>([]);

const handleImport = (options: UploadRequestOptions) => {
  const { file, onProgress, onSuccess, onError } = options;
  const kbId = router.currentRoute.value.params.id;
  embeddingDocs(
    String(kbId),
    {
      file: file
    },
    progressEvent => {
      onProgress?.({
        percent: Math.round(
          (progressEvent.loaded * 100) / Number(progressEvent.total)
        )
      });
    }
  )
    .then(res => {
      fileList.value.push(res);
      ElMessage.success('上传成功，文档解析中...');
      onSuccess?.(res);
    })
    .catch(err => {
      console.error(err);
      ElMessage.error('上传失败');
      onError?.(err);
    });
};
</script>

<template>
  <div class="space-y-4">
    <el-upload
      :http-request="handleImport"
      drag
      accept=".doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,.pdf,.txt,.md"
    >
      <div class="el-upload__text">
        <div style="margin-bottom: 12px">
          <el-icon size="48">
            <DownloadOutline />
          </el-icon>
        </div>
        <div style="font-size: 16px">点击或者拖动文件到该区域来上传</div>
        <div style="margin: 8px 0 0 0; color: #999">
          请上传文档文本类型的文件，文本类型文件将被单独处理和向量化，支持的文件格式有：.txt、
          .md、 .docx、 .doc、.pdf
        </div>
      </div>
    </el-upload>
  </div>
</template>

<style lang="less" scoped></style>
