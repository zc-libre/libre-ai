<script lang="ts" setup>
import { nextTick, ref, reactive } from 'vue';
import { add, getById, update } from '@/api/aigc/app';
import {
  ElMessage,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElUpload
} from 'element-plus';
import type { UploadProps, FormInstance, FormRules } from 'element-plus';
import { uploadApi } from '@/api/aigc/oss';
import ModelSelect from '@/views/common/ModelSelect.vue';
import { useAppStore } from '@/views/app/store';
import { useChatStore } from '@/views/chat/store/useChatStore';

const appStore = useAppStore();
const chatStore = useChatStore();
const emit = defineEmits(['reload']);
const message = ElMessage;
const fileList = ref<any>([]);

// 对话框控制
const dialogVisible = ref(false);
const dialogTitle = ref('新增/编辑');

// 表单相关
const formRef = ref<FormInstance>();
const formData = reactive({
  id: '',
  name: '',
  modelId: '',
  cover: '',
  des: ''
});

// 表单验证规则
const formRules = reactive<FormRules>({
  name: [{ required: true, message: '请输入应用名称', trigger: 'blur' }],
  modelId: [{ required: true, message: '请选择关联模型', trigger: 'blur' }]
});

// 显示对话框
async function show(id?: string) {
  dialogVisible.value = true;
  await nextTick();

  // 重置表单
  Object.keys(formData).forEach(key => {
    formData[key] = '';
  });
  fileList.value = [];

  if (id) {
    const data = await getById(id);
    Object.assign(formData, data);
    if (data.cover) {
      fileList.value = [
        {
          name: 'cover',
          url: data.cover
        }
      ];
    }
  }
}

// 关闭对话框
function closeDialog() {
  dialogVisible.value = false;
  formRef.value?.resetFields();
  fileList.value = [];
}

// 提交表单
async function handleSubmit() {
  await formRef.value?.validate(async valid => {
    if (valid) {
      try {
        if (!formData.id) {
          await add(formData);
          message.success('新增成功');
        } else {
          await update(formData);
          message.success('修改成功');
        }
        closeDialog();
        emit('reload');
      } catch (error) {
        console.error(error);
      }
    }
  });
}

// 自定义上传
const customUpload: UploadProps['httpRequest'] = async options => {
  try {
    const res = await uploadApi({
      file: options.file
    });
    formData.cover = res.url;
    message.success('上传成功');
    options.onSuccess(res);
  } catch (error) {
    console.error(error);
    message.error('上传失败');
    options.onError(error);
  }
};

// 保存模型选择
async function onSaveModel(val: any) {
  appStore.modelId = val.id;
  chatStore.modelId = val.id;
  formData.modelId = val.id;
}

defineExpose({ show });
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="45%"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="mt-5"
    >
      <ElFormItem label="应用名称" prop="name">
        <ElInput v-model="formData.name" placeholder="请输入应用名称" />
      </ElFormItem>

      <ElFormItem label="关联模型" prop="modelId">
        <ModelSelect
          :id="formData.modelId"
          @load="onSaveModel"
          @update="onSaveModel"
        />
      </ElFormItem>

      <ElFormItem label="应用封面" prop="cover">
        <ElUpload
          v-model:file-list="fileList"
          :http-request="customUpload"
          accept=".jpg,.jpeg,.png,.gif,.bmp,.webp"
          list-type="picture-card"
          :limit="1"
        >
          <div class="flex flex-col items-center justify-center">
            <i class="el-icon-plus" />
            <span class="text-xs mt-1">上传图片</span>
          </div>
        </ElUpload>
      </ElFormItem>

      <ElFormItem label="应用描述" prop="des">
        <ElInput
          v-model="formData.des"
          type="textarea"
          :rows="5"
          placeholder="请输入应用描述"
        />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </span>
    </template>
  </ElDialog>
</template>

<style lang="scss" scoped></style>
