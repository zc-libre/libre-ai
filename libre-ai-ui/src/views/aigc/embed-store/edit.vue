<script lang="ts" setup>
import { computed, nextTick, ref } from 'vue';
import { add, getById, update } from '@/api/aigc/embed-store';
import {
  ElMessage,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElSwitch,
  ElButton,
  ElAlert
} from 'element-plus';
import { getProviderLabel, getSchemas } from './columns';

const props = defineProps<{
  provider: string;
}>();
const emit = defineEmits(['reload']);
const visible = ref(false);
const loading = ref(false);
const formRef = ref();
const formData = ref({});
const formRules = ref({});

const schemas = computed(() => {
  return getSchemas(props.provider);
});

const title = computed(() => {
  return getProviderLabel(props.provider) + ' 新增/编辑';
});

async function show(id?: string) {
  visible.value = true;
  await nextTick();
  if (id) {
    formData.value = await getById(id);
  } else {
    formData.value = { isPerms: true, provider: props.provider };
  }
}

async function handleSubmit() {
  try {
    await formRef.value.validate();
    loading.value = true;
    if (!formData.value.id) {
      await add(formData.value);
      ElMessage.success('新增成功');
    } else {
      await update(formData.value);
      ElMessage.success('修改成功');
    }
    visible.value = false;
    emit('reload');
  } catch (error) {
    ElMessage.error('请完善表单');
  } finally {
    loading.value = false;
  }
}

function handleClose() {
  visible.value = false;
  formData.value = {};
}

defineExpose({ show });
</script>

<template>
  <el-dialog v-model="visible" :title="title" width="45%" @close="handleClose">
    <el-alert
      class="mb-4"
      title="注意：请慎重修改模型的向量纬度参数（Dimension），此参数需要和向量库匹配（错误修改可能将影响已有的向量数据）"
      type="info"
      show-icon
    />

    <el-form
      ref="formRef"
      :model="formData"
      label-width="120px"
      :rules="formRules"
    >
      <template v-for="schema in schemas" :key="schema.field">
        <el-form-item
          :label="schema.label"
          :prop="schema.field"
          :required="schema.required"
        >
          <el-input
            v-if="schema.component === 'NInput'"
            v-model="formData[schema.field]"
            :placeholder="schema.componentProps?.placeholder"
            :disabled="schema.componentProps?.disabled"
          />
          <el-input-number
            v-else-if="schema.component === 'NInputNumber'"
            v-model="formData[schema.field]"
            :min="schema.componentProps?.min"
            :max="schema.componentProps?.max"
            :disabled="schema.componentProps?.disabled"
            style="width: 100%"
          />
          <el-switch
            v-else-if="schema.component === 'NSwitch'"
            v-model="formData[schema.field]"
            :disabled="schema.componentProps?.disabled"
          />
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit"
        >提交</el-button
      >
    </template>
  </el-dialog>
</template>

<style lang="less" scoped></style>
