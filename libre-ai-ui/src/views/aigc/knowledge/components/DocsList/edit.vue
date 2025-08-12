<script lang="ts" setup>
import { nextTick, ref } from 'vue';
import { add, getById, update } from '@/api/aigc/docs';
import { ElMessage } from 'element-plus';
import { formSchemas } from './columns';
import { BasicForm, useForm } from '@/components/Form';
import { isNullOrWhitespace } from '@/utils/is';

const emit = defineEmits(['reload']);
const showModal = ref(false);

const [register, { setFieldsValue }] = useForm({
  colProps: { cols: 1 },
  labelWidth: 120,
  layout: 'horizontal',
  submitButtonText: '提交',
  schemas: formSchemas
});

async function show(kbId: string, id: string) {
  showModal.value = true;
  await nextTick();
  if (id) {
    setFieldsValue(await getById(id));
  }
  setFieldsValue({ knowledgeId: kbId });
}

async function handleSubmit(values: any) {
  if (values !== false) {
    showModal.value = false;
    if (isNullOrWhitespace(values.id)) {
      await add(values);
      emit('reload');
      ElMessage.success('新增成功');
    } else {
      await update(values);
      emit('reload');
      ElMessage.success('修改成功');
    }
  } else {
    ElMessage.error('请完善表单');
  }
}

defineExpose({ show });
</script>

<template>
  <el-dialog v-model="showModal" title="编辑文档" width="40%">
    <BasicForm class="mt-5" @register="register" @submit="handleSubmit" />
  </el-dialog>
</template>

<style lang="less" scoped></style>
