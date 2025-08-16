<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="width"
    :fullscreen="fullscreen"
    :destroy-on-close="true"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      :label-width="labelWidth"
      :size="size"
      :disabled="readonly"
    >
      <el-row :gutter="20">
        <template v-for="column in displayColumns" :key="column.key">
          <el-col :span="getColSpan(column)">
            <el-form-item
              :label="column.title"
              :prop="column.key"
              :required="isRequired(column)"
            >
              <!-- 只读模式 -->
              <template v-if="readonly">
                <crud-cell
                  :value="form[column.key]"
                  :row="form"
                  :column="column"
                  :index="0"
                />
              </template>
              
              <!-- 编辑模式 -->
              <template v-else>
                <component
                  :is="getComponent(column)"
                  v-model="form[column.key]"
                  :placeholder="(column[props.mode as keyof CrudColumnConfig] as any)?.placeholder || `请输入${column.title}`"
                  v-bind="getComponentProps(column)"
                  :disabled="isDisabled(column)"
                  style="width: 100%"
                />
                
                <!-- 帮助文本 -->
                <div v-if="column.helper" class="form-helper-text">
                  {{ column.helper }}
                </div>
              </template>
            </el-form-item>
          </el-col>
        </template>
      </el-row>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          v-if="!readonly"
          type="primary"
          :loading="submitting"
          @click="handleSubmit"
        >
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, watch, reactive, nextTick } from 'vue'
import { ElDialog, ElForm, ElFormItem, ElButton, ElRow, ElCol, ElInput, ElSelect, ElOption, ElDatePicker, ElInputNumber, ElSwitch, ElRadioGroup, ElRadio, ElCheckboxGroup, ElCheckbox, ElUpload, ElMessage } from 'element-plus'
import CrudCell from './CrudCell.vue'
import { DictSelect, DictRadio, DictCheckbox, DictCascader } from './dict'
import type { CrudColumnConfig } from './crud-types'

interface Props {
  modelValue: boolean
  mode: 'add' | 'edit' | 'view'
  title: string
  columns: CrudColumnConfig[]
  formData?: Record<string, any>
  rules?: Record<string, any[]>
  config?: {
    width?: string
    fullscreen?: boolean
    labelWidth?: string
    size?: 'large' | 'default' | 'small'
    columns?: number
  }
}

const props = withDefaults(defineProps<Props>(), {
  formData: () => ({}),
  rules: () => ({}),
  config: () => ({})
})

const emit = defineEmits(['update:modelValue', 'submit', 'cancel'])

// 表单引用
const formRef = ref()

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 表单数据
const form = reactive<Record<string, any>>({})

// 提交状态
const submitting = ref(false)

// 计算属性
const readonly = computed(() => props.mode === 'view')
const width = computed(() => props.config.width || '600px')
const fullscreen = computed(() => props.config.fullscreen || false)
const labelWidth = computed(() => props.config.labelWidth || '120px')
const size = computed(() => props.config.size || 'default')
const formColumns = computed(() => props.config.columns || 1)

// 显示的列
const displayColumns = computed(() => {
  return props.columns.filter(column => {
    const modeConfig = column[props.mode as keyof CrudColumnConfig] as any
    const showConfig = modeConfig?.show
    if (typeof showConfig === 'function') {
      return showConfig(form)
    }
    return showConfig !== false
  }).sort((a, b) => {
    const aModeConfig = a[props.mode as keyof CrudColumnConfig] as any
    const bModeConfig = b[props.mode as keyof CrudColumnConfig] as any
    const aOrder = aModeConfig?.order || 0
    const bOrder = bModeConfig?.order || 0
    return aOrder - bOrder
  })
})

// 获取列跨度
const getColSpan = (column: CrudColumnConfig) => {
  const modeConfig = column[props.mode as keyof CrudColumnConfig] as any
  const span = modeConfig?.span
  if (span) return span
  
  return Math.floor(24 / formColumns.value)
}

// 获取组件
const getComponent = (column: CrudColumnConfig) => {
  const modeConfig = column[props.mode as keyof CrudColumnConfig] as any
  const component = modeConfig?.component
  
  if (component) {
    return component
  }
  
  // 根据字典配置自动选择组件
  if (column.dict) {
    if (column.dict.multiple) {
      return DictCheckbox
    }
    return DictSelect
  }
  
  // 根据类型自动选择组件
  const formType = modeConfig?.type || 'input'
  
  const componentMap = {
    input: ElInput,
    textarea: ElInput,
    number: ElInputNumber,
    select: ElSelect,
    'dict-select': DictSelect,
    'dict-radio': DictRadio,
    'dict-checkbox': DictCheckbox,
    'dict-cascader': DictCascader,
    date: ElDatePicker,
    datetime: ElDatePicker,
    time: ElDatePicker,
    daterange: ElDatePicker,
    switch: ElSwitch,
    radio: ElRadioGroup,
    checkbox: ElCheckboxGroup,
    upload: ElUpload
  }
  
  return componentMap[formType] || ElInput
}

// 获取组件属性
const getComponentProps = (column: CrudColumnConfig) => {
  const modeConfig = column[props.mode as keyof CrudColumnConfig] as any
  const baseProps = {
    ...modeConfig?.props
  }
  
  // 字典配置
  if (column.dict) {
    baseProps.dict = column.dict
    baseProps.dictKey = `form-${props.mode}-${column.key}`
  }
  
  // 特殊类型的属性配置
  const formType = modeConfig?.type
  
  if (formType === 'textarea') {
    baseProps.type = 'textarea'
    baseProps.rows = 4
  } else if (formType === 'datetime') {
    baseProps.type = 'datetime'
    baseProps.valueFormat = 'YYYY-MM-DD HH:mm:ss'
  } else if (formType === 'date') {
    baseProps.type = 'date'
    baseProps.valueFormat = 'YYYY-MM-DD'
  } else if (formType === 'daterange') {
    baseProps.type = 'daterange'
    baseProps.rangeSeparator = '至'
    baseProps.startPlaceholder = '开始日期'
    baseProps.endPlaceholder = '结束日期'
    baseProps.valueFormat = 'YYYY-MM-DD'
  }
  
  return baseProps
}

// 检查是否必填
const isRequired = (column: CrudColumnConfig) => {
  const rules = column.rules || props.rules[column.key]
  return rules?.some((rule: any) => rule.required)
}

// 检查是否禁用
const isDisabled = (column: CrudColumnConfig) => {
  const modeConfig = column[props.mode as keyof CrudColumnConfig] as any
  const disabled = modeConfig?.disabled
  if (typeof disabled === 'function') {
    return disabled(form)
  }
  return disabled
}

// 表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    submitting.value = true
    
    emit('submit', { ...form })
  } catch (error) {
    console.error('表单验证失败:', error)
    ElMessage.error('请检查表单输入')
  } finally {
    submitting.value = false
  }
}

// 取消操作
const handleCancel = () => {
  emit('cancel')
}

// 关闭对话框
const handleClose = () => {
  emit('cancel')
}

// 监听表单数据变化
watch(() => props.formData, (newData) => {
  Object.assign(form, newData)
}, { immediate: true, deep: true })

// 监听对话框显示状态
watch(visible, (show) => {
  if (show) {
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }
})
</script>

<style lang="scss" scoped>
.form-helper-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
  line-height: 1.4;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

// 移动端适配
@media (max-width: 768px) {
  :deep(.el-dialog) {
    width: 95% !important;
    margin: 0 auto;
    
    .el-dialog__body {
      padding: 16px;
    }
  }
  
  .form-helper-text {
    font-size: 11px;
  }
}
</style>