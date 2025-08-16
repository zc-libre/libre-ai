<template>
  <div class="crud-search">
    <el-form
      ref="formRef"
      :model="form"
      :label-width="labelWidth"
      :inline="inline"
      :size="size"
      class="search-form"
    >
      <template v-for="column in displayColumns" :key="column.key">
        <el-form-item
          :label="column.title"
          :prop="column.key"
          :class="column.className"
          :style="{ width: column.search?.span ? `${column.search.span}%` : 'auto' }"
        >
          <component
            :is="getComponent(column)"
            v-model="form[column.key]"
            :placeholder="column.search?.placeholder || `请输入${column.title}`"
            v-bind="getComponentProps(column)"
            style="width: 100%"
          />
        </el-form-item>
      </template>

      <!-- 操作按钮 -->
      <el-form-item class="search-actions">
        <el-button type="primary" :icon="Search" @click="handleSearch">
          搜索
        </el-button>
        <el-button v-if="showReset" :icon="Refresh" @click="handleReset">
          重置
        </el-button>
        <el-button
          v-if="showCollapse && collapsibleColumns.length > 0"
          type="text"
          :icon="expanded ? ArrowUp : ArrowDown"
          @click="toggleExpanded"
        >
          {{ expanded ? '收起' : '展开' }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, reactive } from 'vue'
import { ElForm, ElFormItem, ElButton, ElInput, ElSelect, ElOption, ElDatePicker, ElInputNumber } from 'element-plus'
import { Search, Refresh, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { DictSelect, DictRadio, DictCheckbox, DictCascader } from './dict'
import type { CrudColumnConfig } from './crud-types'

interface Props {
  modelValue?: Record<string, any>
  columns: CrudColumnConfig[]
  config?: {
    labelWidth?: string
    inline?: boolean
    size?: 'large' | 'default' | 'small'
    showReset?: boolean
    showCollapse?: boolean
    collapsed?: boolean
  }
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({}),
  config: () => ({})
})

const emit = defineEmits(['update:modelValue', 'search', 'reset'])

// 表单引用
const formRef = ref()

// 表单数据
const form = reactive<Record<string, any>>({ ...props.modelValue })

// 展开状态
const expanded = ref(!props.config.collapsed)

// 计算属性
const labelWidth = computed(() => props.config.labelWidth || '100px')
const inline = computed(() => props.config.inline !== false)
const size = computed(() => props.config.size || 'default')
const showReset = computed(() => props.config.showReset !== false)
const showCollapse = computed(() => props.config.showCollapse && collapsibleColumns.value.length > 0)

// 可折叠的列（第二行及以后的列）
const collapsibleColumns = computed(() => {
  return props.columns.slice(3) // 假设前3个字段始终显示
})

// 显示的列
const displayColumns = computed(() => {
  if (!showCollapse.value || expanded.value) {
    return props.columns
  }
  return props.columns.slice(0, 3) // 只显示前3个字段
})

// 获取组件
const getComponent = (column: CrudColumnConfig) => {
  const component = column.search?.component
  
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
  const searchType = column.search?.type || 'input'
  
  const componentMap = {
    input: ElInput,
    select: ElSelect,
    'dict-select': DictSelect,
    'dict-radio': DictRadio,
    'dict-checkbox': DictCheckbox,
    'dict-cascader': DictCascader,
    date: ElDatePicker,
    daterange: ElDatePicker,
    number: ElInputNumber,
    textarea: ElInput
  }
  
  return componentMap[searchType] || ElInput
}

// 获取组件属性
const getComponentProps = (column: CrudColumnConfig) => {
  const props: any = {
    ...column.search?.props
  }
  
  // 字典配置
  if (column.dict) {
    props.dict = column.dict
    props.dictKey = `search-${column.key}`
  }
  
  // 特殊类型的属性配置
  const searchType = column.search?.type
  
  if (searchType === 'daterange') {
    props.type = 'daterange'
    props.rangeSeparator = '至'
    props.startPlaceholder = '开始日期'
    props.endPlaceholder = '结束日期'
    props.valueFormat = 'YYYY-MM-DD'
  } else if (searchType === 'date') {
    props.type = 'date'
    props.valueFormat = 'YYYY-MM-DD'
  } else if (searchType === 'textarea') {
    props.type = 'textarea'
    props.rows = 3
  }
  
  return props
}

// 搜索处理
const handleSearch = () => {
  emit('search', form)
}

// 重置处理
const handleReset = () => {
  formRef.value?.resetFields()
  
  // 清空表单数据
  Object.keys(form).forEach(key => {
    delete form[key]
  })
  
  emit('update:modelValue', {})
  emit('reset')
}

// 切换展开状态
const toggleExpanded = () => {
  expanded.value = !expanded.value
}

// 监听表单数据变化
watch(form, (newForm) => {
  emit('update:modelValue', newForm)
}, { deep: true })

// 监听外部数据变化
watch(() => props.modelValue, (newValue) => {
  Object.assign(form, newValue)
}, { deep: true })
</script>

<style lang="scss" scoped>
.crud-search {
  .search-form {
    .el-form-item {
      margin-right: 16px;
      margin-bottom: 16px;
      
      &.search-actions {
        margin-left: auto;
      }
    }
    
    &.el-form--inline {
      .el-form-item {
        display: inline-flex;
        vertical-align: top;
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
  .crud-search {
    .search-form {
      .el-form-item {
        width: 100% !important;
        margin-right: 0;
        
        .el-form-item__content {
          width: 100%;
        }
      }
      
      &.el-form--inline {
        .el-form-item {
          display: block;
        }
      }
    }
  }
}
</style>