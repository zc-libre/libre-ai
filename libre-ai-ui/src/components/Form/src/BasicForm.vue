<template>
  <el-form
    ref="formElRef"
    v-bind="getFormProps"
    :model="formModel"
    class="enhanced-form"
  >
    <!-- 渲染分组表单 -->
    <template v-if="hasGroupedSchemas">
      <div
        v-for="(group, sectionName) in groupedSchemas"
        :key="sectionName"
        class="form-section"
      >
        <!-- 分组标题 -->
        <div v-if="sectionName !== 'default'" class="section-header">
          <el-icon><Icon icon="ep:setting" /></el-icon>
          <span>{{ sectionName }}</span>
        </div>
        <el-divider v-if="sectionName !== 'default'" />

        <el-row :gutter="16">
          <template v-for="schema in group" :key="schema.field">
            <el-col
              v-if="!schema.isHidden"
              v-bind="schema.colProps || { span: 24 }"
            >
              <el-form-item
                :label="schema.label"
                :prop="schema.field"
                :rules="schema.rules"
                class="form-item-enhanced"
              >
                <!-- 增强的标签设计 -->
                <template #label>
                  <div class="label-container">
                    <el-icon v-if="schema.icon">
                      <Icon :icon="schema.icon" />
                    </el-icon>
                    <span>{{ schema.label }}</span>
                    <el-tooltip
                      v-if="schema.labelMessage || schema.helpTooltip"
                      :content="schema.helpTooltip || schema.labelMessage"
                      placement="top"
                    >
                      <el-icon class="help-icon">
                        <QuestionFilled />
                      </el-icon>
                    </el-tooltip>
                  </div>
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
        </el-row>
      </div>
    </template>

    <!-- 非分组表单（保持向后兼容） -->
    <template v-else>
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
              class="form-item-enhanced"
            >
              <!-- 增强的标签设计 -->
              <template #label>
                <div class="label-container">
                  <el-icon v-if="schema.icon">
                    <Icon :icon="schema.icon" />
                  </el-icon>
                  <span>{{ schema.label }}</span>
                  <el-tooltip
                    v-if="schema.labelMessage || schema.helpTooltip"
                    :content="schema.helpTooltip || schema.labelMessage"
                    placement="top"
                  >
                    <el-icon class="help-icon">
                      <QuestionFilled />
                    </el-icon>
                  </el-tooltip>
                </div>
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
          <div :style="getActionStyle" class="action-buttons">
            <el-button
              v-if="showSubmitButton"
              type="primary"
              :loading="loading"
              v-bind="submitButtonOptions"
              @click="handleSubmit"
            >
              <el-icon v-if="!loading" class="mr-1">
                <Icon icon="ep:check" />
              </el-icon>
              {{ loading ? '提交中...' : submitButtonText }}
            </el-button>
            <el-button
              v-if="showResetButton"
              v-bind="resetButtonOptions"
              @click="handleReset"
            >
              <el-icon class="mr-1">
                <Icon icon="ep:refresh" />
              </el-icon>
              {{ resetButtonText }}
            </el-button>
          </div>
        </el-col>
      </el-row>
    </template>
  </el-form>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { QuestionFilled } from '@element-plus/icons-vue';
import { Icon } from '@iconify/vue';
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

// 分组表单逻辑
const groupedSchemas = computed(() => {
  const schemas = getSchema.value;
  const groups: Record<string, FormSchema[]> = {};

  schemas.forEach(schema => {
    const sectionName = schema.section || 'default';
    if (!groups[sectionName]) {
      groups[sectionName] = [];
    }
    groups[sectionName].push(schema);
  });

  return groups;
});

const hasGroupedSchemas = computed(() => {
  const schemas = getSchema.value;
  return schemas.some(schema => schema.section);
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
// 表单整体样式
.enhanced-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #374151;
  }
}

// 表单分组样式
.form-section {
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 12px;
  }

  :deep(.el-divider) {
    margin: 12px 0 20px;
  }
}

// 表单项增强样式
.form-item-enhanced {
  :deep(.el-form-item__label) {
    height: auto;
    line-height: 32px;
  }

  .label-container {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;

    .help-icon {
      color: #94a3b8;
      cursor: help;
      font-size: 14px;

      &:hover {
        color: #6366f1;
      }
    }
  }

  // 输入框样式增强
  :deep(.el-input) {
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #e2e8f0 inset;
      transition: all 0.3s ease;
      border-radius: 6px;

      &:hover {
        box-shadow: 0 0 0 1px #cbd5e1 inset;
      }

      &.is-focus {
        box-shadow: 0 0 0 2px #6366f1 inset;
      }
    }
  }

  :deep(.el-input-number) {
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #e2e8f0 inset;
      transition: all 0.3s ease;
      border-radius: 6px;

      &:hover {
        box-shadow: 0 0 0 1px #cbd5e1 inset;
      }

      &.is-focus {
        box-shadow: 0 0 0 2px #6366f1 inset;
      }
    }
  }

  :deep(.el-select) {
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #e2e8f0 inset;
      transition: all 0.3s ease;
      border-radius: 6px;

      &:hover {
        box-shadow: 0 0 0 1px #cbd5e1 inset;
      }

      &.is-focus {
        box-shadow: 0 0 0 2px #6366f1 inset;
      }
    }
  }

  :deep(.el-textarea) {
    .el-textarea__inner {
      box-shadow: 0 0 0 1px #e2e8f0 inset;
      transition: all 0.3s ease;
      border-radius: 6px;
      border: none;

      &:hover {
        box-shadow: 0 0 0 1px #cbd5e1 inset;
      }

      &:focus {
        box-shadow: 0 0 0 2px #6366f1 inset;
      }
    }
  }
}

// 操作按钮样式
.action-buttons {
  display: flex;
  gap: 12px;

  :deep(.el-button) {
    min-width: 100px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .form-section {
    .section-header {
      font-size: 14px;
    }
  }

  .enhanced-form {
    :deep(.el-form-item) {
      .el-form-item__label {
        width: 100px !important;
        font-size: 13px;
      }
    }
  }

  .action-buttons {
    flex-direction: column;

    :deep(.el-button) {
      width: 100%;
    }
  }
}

@media (max-width: 480px) {
  .form-section {
    margin-bottom: 24px;

    .section-header {
      font-size: 13px;
    }
  }

  .enhanced-form {
    :deep(.el-form-item) {
      .el-form-item__label {
        width: 80px !important;
        font-size: 12px;
      }
    }
  }
}

// 暗色主题适配
html.dark {
  .enhanced-form {
    :deep(.el-form-item__label) {
      color: #e2e8f0;
    }
  }

  .form-section {
    .section-header {
      color: #f1f5f9;
    }
  }

  .form-item-enhanced {
    .label-container {
      .help-icon {
        color: #64748b;

        &:hover {
          color: #818cf8;
        }
      }
    }

    // 暗色主题下的输入框样式
    :deep(.el-input) {
      .el-input__wrapper {
        box-shadow: 0 0 0 1px #475569 inset;
        background-color: #1e293b;

        &:hover {
          box-shadow: 0 0 0 1px #64748b inset;
        }

        &.is-focus {
          box-shadow: 0 0 0 2px #818cf8 inset;
        }
      }
    }

    :deep(.el-input-number) {
      .el-input__wrapper {
        box-shadow: 0 0 0 1px #475569 inset;
        background-color: #1e293b;

        &:hover {
          box-shadow: 0 0 0 1px #64748b inset;
        }

        &.is-focus {
          box-shadow: 0 0 0 2px #818cf8 inset;
        }
      }
    }

    :deep(.el-select) {
      .el-input__wrapper {
        box-shadow: 0 0 0 1px #475569 inset;
        background-color: #1e293b;

        &:hover {
          box-shadow: 0 0 0 1px #64748b inset;
        }

        &.is-focus {
          box-shadow: 0 0 0 2px #818cf8 inset;
        }
      }
    }

    :deep(.el-textarea) {
      .el-textarea__inner {
        box-shadow: 0 0 0 1px #475569 inset;
        background-color: #1e293b;
        color: #e2e8f0;

        &:hover {
          box-shadow: 0 0 0 1px #64748b inset;
        }

        &:focus {
          box-shadow: 0 0 0 2px #818cf8 inset;
        }
      }
    }
  }
}
</style>
