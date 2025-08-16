<template>
  <div class="table-search">
    <el-form
      ref="formRef"
      :model="searchForm"
      :label-width="labelWidth"
      :inline="inline"
      :size="size"
      class="search-form"
    >
      <!-- 展开的搜索字段 -->
      <template v-for="(field, index) in visibleFields" :key="field.key">
        <el-form-item :label="field.label" :prop="field.key">
          <!-- 输入框 -->
          <el-input
            v-if="field.type === 'input' || !field.type"
            v-model="searchForm[field.key]"
            :placeholder="field.placeholder || `请输入${field.label}`"
            :clearable="field.clearable !== false"
            :maxlength="field.maxlength"
            :show-word-limit="field.showWordLimit"
            @keyup.enter="handleSearch"
          />
          
          <!-- 选择框 -->
          <el-select
            v-else-if="field.type === 'select'"
            v-model="searchForm[field.key]"
            :placeholder="field.placeholder || `请选择${field.label}`"
            :clearable="field.clearable !== false"
            :multiple="field.multiple"
            :filterable="field.filterable"
            :allow-create="field.allowCreate"
            :style="{ width: field.width || '180px' }"
          >
            <el-option
              v-for="option in field.options"
              :key="option.value"
              :label="option.label"
              :value="option.value"
              :disabled="option.disabled"
            />
          </el-select>
          
          <!-- 日期选择器 -->
          <el-date-picker
            v-else-if="field.type === 'date'"
            v-model="searchForm[field.key]"
            type="date"
            :placeholder="field.placeholder || `请选择${field.label}`"
            :clearable="field.clearable !== false"
            :format="field.format || 'YYYY-MM-DD'"
            :value-format="field.valueFormat || 'YYYY-MM-DD'"
            :style="{ width: field.width || '180px' }"
          />
          
          <!-- 日期范围选择器 -->
          <el-date-picker
            v-else-if="field.type === 'daterange'"
            v-model="searchForm[field.key]"
            type="daterange"
            :range-separator="field.rangeSeparator || '至'"
            :start-placeholder="field.startPlaceholder || '开始日期'"
            :end-placeholder="field.endPlaceholder || '结束日期'"
            :clearable="field.clearable !== false"
            :format="field.format || 'YYYY-MM-DD'"
            :value-format="field.valueFormat || 'YYYY-MM-DD'"
            :style="{ width: field.width || '240px' }"
          />
          
          <!-- 时间选择器 -->
          <el-time-picker
            v-else-if="field.type === 'time'"
            v-model="searchForm[field.key]"
            :placeholder="field.placeholder || `请选择${field.label}`"
            :clearable="field.clearable !== false"
            :format="field.format || 'HH:mm:ss'"
            :value-format="field.valueFormat || 'HH:mm:ss'"
            :style="{ width: field.width || '180px' }"
          />
          
          <!-- 时间范围选择器 -->
          <el-time-picker
            v-else-if="field.type === 'timerange'"
            v-model="searchForm[field.key]"
            is-range
            :range-separator="field.rangeSeparator || '至'"
            :start-placeholder="field.startPlaceholder || '开始时间'"
            :end-placeholder="field.endPlaceholder || '结束时间'"
            :clearable="field.clearable !== false"
            :format="field.format || 'HH:mm:ss'"
            :value-format="field.valueFormat || 'HH:mm:ss'"
            :style="{ width: field.width || '240px' }"
          />
          
          <!-- 数字输入框 -->
          <el-input-number
            v-else-if="field.type === 'number'"
            v-model="searchForm[field.key]"
            :placeholder="field.placeholder || `请输入${field.label}`"
            :min="field.min"
            :max="field.max"
            :step="field.step"
            :precision="field.precision"
            :controls="field.controls !== false"
            :style="{ width: field.width || '180px' }"
          />
          
          <!-- 级联选择器 -->
          <el-cascader
            v-else-if="field.type === 'cascader'"
            v-model="searchForm[field.key]"
            :options="field.options"
            :placeholder="field.placeholder || `请选择${field.label}`"
            :clearable="field.clearable !== false"
            :filterable="field.filterable"
            :show-all-levels="field.showAllLevels !== false"
            :style="{ width: field.width || '200px' }"
          />
          
          <!-- 开关 -->
          <el-switch
            v-else-if="field.type === 'switch'"
            v-model="searchForm[field.key]"
            :active-text="field.activeText"
            :inactive-text="field.inactiveText"
            :active-value="field.activeValue"
            :inactive-value="field.inactiveValue"
          />
          
          <!-- 滑块 -->
          <el-slider
            v-else-if="field.type === 'slider'"
            v-model="searchForm[field.key]"
            :min="field.min || 0"
            :max="field.max || 100"
            :step="field.step || 1"
            :range="field.range"
            :show-tooltip="field.showTooltip !== false"
            :style="{ width: field.width || '200px' }"
          />
          
          <!-- 评分 -->
          <el-rate
            v-else-if="field.type === 'rate'"
            v-model="searchForm[field.key]"
            :max="field.max || 5"
            :allow-half="field.allowHalf"
            :show-text="field.showText"
            :texts="field.texts"
          />
          
          <!-- 颜色选择器 -->
          <el-color-picker
            v-else-if="field.type === 'color'"
            v-model="searchForm[field.key]"
            :show-alpha="field.showAlpha"
            :color-format="field.colorFormat"
            :predefine="field.predefine"
          />
          
          <!-- 自定义组件 -->
          <component
            v-else-if="field.component"
            :is="field.component"
            v-model="searchForm[field.key]"
            v-bind="field.componentProps"
          />
        </el-form-item>
      </template>
      
      <!-- 操作按钮 -->
      <el-form-item class="search-actions">
        <el-button
          type="primary"
          :icon="Search"
          :loading="searching"
          @click="handleSearch"
        >
          搜索
        </el-button>
        
        <el-button
          v-if="showReset"
          :icon="Refresh"
          @click="handleReset"
        >
          重置
        </el-button>
        
        <!-- 展开/收起按钮 -->
        <el-button
          v-if="showCollapse && collapsibleFields.length > 0"
          type="text"
          :icon="expanded ? ArrowUp : ArrowDown"
          @click="toggleExpanded"
          class="ml-2"
        >
          {{ expanded ? '收起' : '展开' }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import {
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption,
  ElDatePicker,
  ElTimePicker,
  ElInputNumber,
  ElCascader,
  ElSwitch,
  ElSlider,
  ElRate,
  ElColorPicker,
  ElButton
} from 'element-plus'
import { Search, Refresh, ArrowUp, ArrowDown } from '@element-plus/icons-vue'

interface SearchField {
  key: string
  label: string
  type?: 'input' | 'select' | 'date' | 'daterange' | 'time' | 'timerange' | 'number' | 'cascader' | 'switch' | 'slider' | 'rate' | 'color'
  placeholder?: string
  width?: string
  clearable?: boolean
  required?: boolean
  defaultValue?: any
  
  // 输入框相关
  maxlength?: number
  showWordLimit?: boolean
  
  // 选择框相关
  options?: Array<{ label: string; value: any; disabled?: boolean }>
  multiple?: boolean
  filterable?: boolean
  allowCreate?: boolean
  
  // 日期相关
  format?: string
  valueFormat?: string
  rangeSeparator?: string
  startPlaceholder?: string
  endPlaceholder?: string
  
  // 数字相关
  min?: number
  max?: number
  step?: number
  precision?: number
  controls?: boolean
  
  // 级联相关
  showAllLevels?: boolean
  
  // 开关相关
  activeText?: string
  inactiveText?: string
  activeValue?: any
  inactiveValue?: any
  
  // 滑块相关
  range?: boolean
  showTooltip?: boolean
  
  // 评分相关
  allowHalf?: boolean
  showText?: boolean
  texts?: string[]
  
  // 颜色选择器相关
  showAlpha?: boolean
  colorFormat?: 'hsl' | 'hsv' | 'hex' | 'rgb'
  predefine?: string[]
  
  // 自定义组件
  component?: any
  componentProps?: object
  
  // 显示控制
  show?: boolean | ((form: any) => boolean)
  collapsed?: boolean // 是否在收起状态下隐藏
}

interface Props {
  fields: SearchField[]
  labelWidth?: string
  inline?: boolean
  size?: 'large' | 'default' | 'small'
  showReset?: boolean
  showCollapse?: boolean
  defaultExpanded?: boolean
  defaultValues?: Record<string, any>
  searching?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  labelWidth: '80px',
  inline: true,
  size: 'default',
  showReset: true,
  showCollapse: true,
  defaultExpanded: false,
  searching: false
})

const emit = defineEmits(['search', 'reset', 'change'])

// 响应式数据
const formRef = ref()
const searchForm = ref<Record<string, any>>({})
const expanded = ref(props.defaultExpanded)

// 初始化表单数据
const initForm = () => {
  const form: Record<string, any> = {}
  
  props.fields.forEach(field => {
    if (field.defaultValue !== undefined) {
      form[field.key] = field.defaultValue
    } else if (props.defaultValues?.[field.key] !== undefined) {
      form[field.key] = props.defaultValues[field.key]
    } else {
      // 根据类型设置默认值
      switch (field.type) {
        case 'select':
          form[field.key] = field.multiple ? [] : ''
          break
        case 'daterange':
        case 'timerange':
          form[field.key] = []
          break
        case 'number':
          form[field.key] = undefined
          break
        case 'switch':
          form[field.key] = field.inactiveValue ?? false
          break
        case 'slider':
          form[field.key] = field.range ? [field.min || 0, field.max || 100] : field.min || 0
          break
        case 'rate':
          form[field.key] = 0
          break
        default:
          form[field.key] = ''
      }
    }
  })
  
  searchForm.value = form
}

// 计算属性
const visibleFields = computed(() => {
  return props.fields.filter(field => {
    // 检查显示条件
    if (typeof field.show === 'function') {
      if (!field.show(searchForm.value)) return false
    } else if (field.show === false) {
      return false
    }
    
    // 检查展开/收起状态
    if (!expanded.value && field.collapsed) {
      return false
    }
    
    return true
  })
})

const collapsibleFields = computed(() => {
  return props.fields.filter(field => field.collapsed)
})

// 方法
const handleSearch = () => {
  emit('search', { ...searchForm.value })
}

const handleReset = () => {
  formRef.value?.resetFields()
  initForm()
  emit('reset')
  emit('search', { ...searchForm.value })
}

const toggleExpanded = () => {
  expanded.value = !expanded.value
}

const getFormData = () => {
  return { ...searchForm.value }
}

const setFormData = (data: Record<string, any>) => {
  Object.assign(searchForm.value, data)
}

const resetForm = () => {
  handleReset()
}

const validateForm = () => {
  return formRef.value?.validate()
}

// 监听表单变化
watch(
  searchForm,
  (newForm) => {
    emit('change', newForm)
  },
  { deep: true }
)

// 初始化
initForm()

// 暴露方法
defineExpose({
  getFormData,
  setFormData,
  resetForm,
  validateForm,
  formRef
})
</script>

<style lang="scss" scoped>
.table-search {
  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 16px;
      
      &.search-actions {
        margin-bottom: 0;
        
        .el-form-item__content {
          display: flex;
          align-items: center;
          flex-wrap: wrap;
          gap: 8px;
        }
      }
    }
    
    // 内联模式调整
    &.el-form--inline {
      :deep(.el-form-item) {
        margin-right: 16px;
        margin-bottom: 16px;
        
        &:last-child {
          margin-right: 0;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .table-search {
    .search-form {
      &.el-form--inline {
        :deep(.el-form-item) {
          display: block;
          margin-right: 0;
          
          .el-form-item__content {
            margin-left: 0 !important;
            
            .el-input,
            .el-select,
            .el-date-editor,
            .el-cascader {
              width: 100% !important;
            }
          }
        }
      }
    }
  }
}

// 暗色主题适配
html.dark {
  .table-search {
    .search-form {
      :deep(.el-form-item__label) {
        color: var(--el-text-color-primary);
      }
    }
  }
}
</style>