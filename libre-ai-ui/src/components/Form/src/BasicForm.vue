<template>
  <el-form ref="formElRef" v-bind="getFormProps" :model="formModel">
    <el-row :gutter="16">
      <template v-for="schema in getSchema" :key="schema.field">
        <el-col
          v-if="!schema.isHidden"
          v-bind="schema.colProps || { span: 24 }"
        >
          <el-form-item
            :label="schema.label"
            :prop="schema.field"
            :rules="schema.rules"
          >
            <template v-if="schema.labelMessage" #label>
              <span style="display: inline-flex; align-items: center">
                {{ schema.label }}
                <el-tooltip :content="schema.labelMessage" placement="top">
                  <el-icon size="16" class="ml-1 text-gray-400">
                    <QuestionFilled />
                  </el-icon>
                </el-tooltip>
              </span>
            </template>

            <template v-if="schema.slot">
              <slot
                :name="schema.slot"
                :model="formModel"
                :field="schema.field"
                :value="formModel[schema.field]"
              />
            </template>

            <template v-else-if="schema.component === 'ElCheckboxGroup'">
              <el-checkbox-group v-model="formModel[schema.field]">
                <el-checkbox
                  v-for="item in getOptions(schema)"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                />
              </el-checkbox-group>
            </template>

            <template v-else-if="schema.component === 'ElRadioGroup'">
              <el-radio-group v-model="formModel[schema.field]">
                <el-radio
                  v-for="item in getOptions(schema)"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                />
              </el-radio-group>
            </template>

            <template v-else-if="schema.component === 'ElSelect'">
              <el-select
                v-model="formModel[schema.field]"
                v-bind="getComponentProps(schema)"
                style="width: 100%"
              >
                <el-option
                  v-for="item in getOptions(schema)"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                />
              </el-select>
            </template>

            <component
              :is="schema.component"
              v-else
              v-model="formModel[schema.field]"
              v-bind="getComponentProps(schema)"
              style="width: 100%"
            />

            <template v-if="schema.suffix">
              <slot
                :name="schema.suffix"
                :model="formModel"
                :field="schema.field"
                :value="formModel[schema.field]"
              />
            </template>
          </el-form-item>
        </el-col>
      </template>

      <el-col v-if="showActionButtonGroup" :span="24">
        <div :style="getActionStyle">
          <el-button
            v-if="showSubmitButton"
            type="primary"
            :loading="loading"
            v-bind="submitButtonOptions"
            @click="handleSubmit"
          >
            {{ submitButtonText }}
          </el-button>
          <el-button
            v-if="showResetButton"
            v-bind="resetButtonOptions"
            @click="handleReset"
          >
            {{ resetButtonText }}
          </el-button>
        </div>
      </el-col>
    </el-row>
  </el-form>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { QuestionFilled } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import type { FormProps, FormSchema } from './types/form';
import { deepMerge } from '@/utils';

const props = withDefaults(defineProps<FormProps>(), {
  labelWidth: 120,
  layout: 'horizontal',
  inline: false,
  size: 'default',
  showActionButtonGroup: true,
  showSubmitButton: true,
  showResetButton: true,
  submitButtonText: '提交',
  resetButtonText: '重置'
});

const emit = defineEmits<{
  submit: [values: any];
  reset: [];
  register: [actions: any];
}>();

const formElRef = ref<FormInstance>();
const formModel = reactive<Record<string, any>>({});
const propsRef = ref<Partial<FormProps>>({});
const loading = ref(false);

const getProps = computed(() => ({
  ...props,
  ...propsRef.value
}));

const getFormProps = computed(() => ({
  labelWidth: getProps.value.labelWidth,
  inline: getProps.value.inline,
  size: getProps.value.size,
  ...getProps.value.formProps
}));

const getSchema = computed(() => {
  const schemas = getProps.value.schemas || [];
  return schemas.map(schema => ({
    ...schema,
    rules: schema.rules || []
  }));
});

const getActionStyle = computed(() => ({
  textAlign: getProps.value.layout === 'inline' ? 'right' : 'left',
  marginLeft:
    getProps.value.layout === 'inline'
      ? '12px'
      : `${getProps.value.labelWidth}px`
}));

function getOptions(schema: FormSchema) {
  const { componentProps } = schema;
  if (!componentProps) return [];
  return componentProps.options || [];
}

function getComponentProps(schema: FormSchema) {
  const defaultProps = {
    clearable: true,
    placeholder: getPlaceholder(schema)
  };
  return { ...defaultProps, ...schema.componentProps };
}

function getPlaceholder(schema: FormSchema): string {
  const { component } = schema;
  if (!component) return '';

  if (component.includes('Input')) {
    return `请输入${schema.label}`;
  } else if (component.includes('Select')) {
    return `请选择${schema.label}`;
  }
  return '';
}

async function setFieldsValue(values: Record<string, any>) {
  Object.keys(values).forEach(key => {
    formModel[key] = values[key];
  });
}

function getFieldsValue() {
  return { ...formModel };
}

async function validate() {
  if (!formElRef.value) return;
  return await formElRef.value.validate();
}

async function validateField(field: string | string[]) {
  if (!formElRef.value) return;
  return await formElRef.value.validateField(field);
}

async function resetFields() {
  if (!formElRef.value) return;
  await formElRef.value.resetFields();
  emit('reset');
}

async function clearValidate(field?: string | string[]) {
  if (!formElRef.value) return;
  await formElRef.value.clearValidate(field);
}

async function handleSubmit() {
  try {
    loading.value = true;
    const valid = await validate();
    if (valid) {
      emit('submit', getFieldsValue());
    }
  } catch (error) {
    console.error('Form validation failed:', error);
  } finally {
    loading.value = false;
  }
}

async function handleReset() {
  await resetFields();
}

async function setProps(formProps: Partial<FormProps>) {
  propsRef.value = deepMerge(propsRef.value, formProps);
}

const methods = {
  setFieldsValue,
  getFieldsValue,
  validate,
  validateField,
  resetFields,
  clearValidate,
  setProps,
  submit: handleSubmit
};

function initFormValues() {
  const schemas = getSchema.value;
  schemas.forEach(schema => {
    if (schema.defaultValue !== undefined) {
      formModel[schema.field] = schema.defaultValue;
    }
  });
}

watch(
  () => getSchema.value,
  () => {
    initFormValues();
  },
  { immediate: true }
);

onMounted(() => {
  emit('register', methods);
});
</script>

<style lang="scss" scoped>
:deep(.el-form-item__label) {
  font-weight: normal;
}
</style>
