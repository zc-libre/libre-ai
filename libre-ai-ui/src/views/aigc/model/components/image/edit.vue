<script lang="ts" setup>
import { computed, nextTick, ref } from 'vue';
import { BasicForm, useForm } from '@/components/Form';
import { getSchemas } from './schemas';
import { isNullOrWhitespace } from '@/utils/is';
import { add, update } from '@/api/aigc/model';
import { ElMessage } from 'element-plus';

const props = defineProps<{
  provider: string;
}>();
const emit = defineEmits(['reload']);
const isShow = ref(false);
const info = ref();
const title = computed(() => {
  return info.value == undefined || info.value.provider == undefined
    ? 'Add Model'
    : info.value.provider;
});
const form: any = {
  responseLimit: 2000,
  temperature: 0.8,
  topP: 1
};

const schemas = computed(() => {
  nextTick();
  return getSchemas(props.provider);
});

async function show(record?: any) {
  isShow.value = true;
  await nextTick();

  // 判断是新增还是编辑
  if (!record?.id) {
    // 新增模式：清空 info 并设置默认值
    info.value = undefined;
    setFieldsValue({
      ...form,
      provider: record?.provider || props.provider,
      // 清空其他可能存在的字段
      id: undefined,
      name: undefined,
      model: undefined,
      baseUrl: undefined,
      apiKey: undefined,
      secretKey: undefined
    });
  } else {
    // 编辑模式：保存记录并设置表单值
    info.value = record;
    // 处理模型字段，如果是对象则提取值
    const formData = { ...form, ...record };
    if (typeof formData.model === 'object' && formData.model !== null) {
      // 如果model是对象，尝试提取value或其他合适的属性
      formData.model =
        formData.model.value ||
        formData.model.label ||
        JSON.stringify(formData.model);
    }
    setFieldsValue(formData);
  }
}

const [register, { setFieldsValue }] = useForm({
  labelWidth: 120,
  colProps: { cols: 1 },
  layout: 'horizontal',
  submitButtonText: '提交'
});

async function onSubmit(values: any) {
  if (values !== false) {
    isShow.value = false;
    const data = { ...values };
    if (isNullOrWhitespace(data.id)) {
      await add(data);
      emit('reload');
      ElMessage.success('新增成功');
    } else {
      await update(data);
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
  <el-drawer v-model="isShow" :title="title" direction="rtl" size="40%">
    <BasicForm
      :schemas="schemas"
      class="mt-5"
      @register="register"
      @submit="onSubmit"
    />
  </el-drawer>
</template>

<style lang="scss" scoped>
:deep(.el-drawer) {
  .el-drawer__header {
    padding: 16px 20px;
    margin-bottom: 20px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .el-drawer__title {
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .el-drawer__body {
    padding: 0 20px 20px;
    overflow-y: auto;
  }

  .el-form {
    .el-form-item {
      margin-bottom: 24px;

      &__label {
        font-weight: 500;
        line-height: 32px;
        color: var(--el-text-color-regular);
      }

      &__content {
        display: flex;
        align-items: center;
        min-height: 32px;
      }
    }

    .el-input {
      width: 100%;
      font-size: 14px;

      .el-input__wrapper {
        display: flex;
        align-items: center;
        width: 100%;
        min-height: 32px;
        background-color: var(--el-fill-color-blank);
        border: 1px solid var(--el-border-color);
        border-radius: var(--el-border-radius-base);
        transition: all 0.2s ease-in-out;

        &:hover {
          border-color: var(--el-border-color-hover);
        }

        &.is-focus {
          border-color: var(--el-color-primary);
          box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
        }
      }

      .el-input__inner {
        width: 100%;
        height: 30px;
        padding: 0 12px;
        line-height: 30px;
        color: var(--el-text-color-regular);
        background: transparent;
        border: none;
        outline: none;

        &::placeholder {
          color: var(--el-text-color-placeholder);
        }
      }
    }

    .el-select {
      width: 100%;

      .el-select__wrapper {
        display: flex;
        align-items: center;
        width: 100%;
        min-height: 32px;
        background-color: var(--el-fill-color-blank);
        border: 1px solid var(--el-border-color);
        border-radius: var(--el-border-radius-base);
        transition: all 0.2s ease-in-out;

        &:hover {
          border-color: var(--el-border-color-hover);
        }

        &.is-focus {
          border-color: var(--el-color-primary);
          box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
        }
      }

      .el-select__selection {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        width: 100%;
        min-height: 30px;
        padding: 0 12px;
      }

      .el-select__selected-item {
        line-height: 30px;
        color: var(--el-text-color-regular);
      }

      .el-select__placeholder {
        line-height: 30px;
        color: var(--el-text-color-placeholder);
      }
    }

    .el-slider {
      width: 100%;
      padding: 0 12px;

      &__runway {
        position: relative;
        height: 6px;
        margin: 16px 0;
        background-color: var(--el-border-color-lighter);
        border-radius: 3px;
      }

      &__bar {
        height: 6px;
        background-color: var(--el-color-primary);
        border-radius: 3px;
      }

      &__button-wrapper {
        width: 20px;
        height: 20px;
        background-color: var(--el-color-primary);
        border: 2px solid #fff;
        border-radius: 50%;
        box-shadow: 0 2px 6px 0 rgb(0 0 0 / 12%);

        &:hover {
          transform: scale(1.2);
        }
      }
    }
  }

  .el-button--primary {
    width: 100%;
    height: 40px;
    margin-top: 30px;
    font-size: 16px;
    border-radius: var(--el-border-radius-base);
  }
}

@media (width <= 768px) {
  :deep(.el-drawer) {
    width: 90% !important;

    .el-drawer__body {
      padding: 0 16px 16px;
    }

    .el-form {
      .el-form-item {
        margin-bottom: 20px;

        &__label {
          font-size: 14px;
        }
      }
    }
  }
}
</style>
