<template>
  <el-cascader
    :model-value="modelValue"
    :options="options"
    :placeholder="placeholder"
    :size="size"
    :disabled="disabled"
    :clearable="clearable"
    :show-all-levels="showAllLevels"
    :collapse-tags="collapseTags"
    :separator="separator"
    :filterable="filterable"
    :props="cascaderProps"
    @update:model-value="handleChange"
    @visible-change="handleVisibleChange"
    @clear="handleClear"
    v-bind="$attrs"
  >
    <template v-if="$slots.default" #default="{ node, data }">
      <slot name="default" :node="node" :data="data" />
    </template>
  </el-cascader>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { ElCascader } from 'element-plus'
import { useDict } from './DictManager'
import type { DictConfig } from '../crud-types'

interface Props {
  modelValue?: any
  dict?: DictConfig
  dictKey?: string
  placeholder?: string
  size?: 'large' | 'default' | 'small'
  disabled?: boolean
  clearable?: boolean
  showAllLevels?: boolean
  collapseTags?: boolean
  separator?: string
  filterable?: boolean
  multiple?: boolean
  checkStrictly?: boolean
  emitPath?: boolean
  lazy?: boolean
  lazyLoad?: (node: any, resolve: Function) => void
  // 兼容旧的配置方式
  options?: any[]
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '请选择',
  size: 'default',
  disabled: false,
  clearable: true,
  showAllLevels: true,
  collapseTags: false,
  separator: ' / ',
  filterable: false,
  multiple: false,
  checkStrictly: false,
  emitPath: true,
  lazy: false
})

const emit = defineEmits(['update:modelValue', 'change', 'visible-change', 'clear'])

// 计算字典配置
const dictConfig = computed<DictConfig>(() => {
  if (props.dict) {
    return props.dict
  }
  
  return {
    data: props.options || []
  }
})

// 使用字典管理器
const dictKey = computed(() => props.dictKey || `cascader-${Date.now()}-${Math.random()}`)
const { data: dictData, loading, getLabel } = useDict(dictKey.value, dictConfig.value)

// 计算选项
const options = computed(() => {
  if (props.options) {
    return props.options
  }
  return dictData.value
})

// 级联选择器属性配置
const cascaderProps = computed(() => ({
  multiple: props.multiple,
  checkStrictly: props.checkStrictly,
  emitPath: props.emitPath,
  lazy: props.lazy,
  lazyLoad: props.lazyLoad,
  value: 'value',
  label: 'label',
  children: 'children',
  disabled: 'disabled'
}))

// 事件处理
const handleChange = (value: any) => {
  emit('update:modelValue', value)
  emit('change', value)
}

const handleVisibleChange = (visible: boolean) => {
  emit('visible-change', visible)
}

const handleClear = () => {
  emit('clear')
}

// 获取完整路径标签
const getPathLabels = (values: any[]) => {
  if (!values || !Array.isArray(values)) {
    return []
  }
  
  const findPath = (options: any[], targetValue: any, path: string[] = []): string[] | null => {
    for (const option of options) {
      const currentPath = [...path, option.label]
      
      if (option.value === targetValue) {
        return currentPath
      }
      
      if (option.children && option.children.length > 0) {
        const found = findPath(option.children, targetValue, currentPath)
        if (found) {
          return found
        }
      }
    }
    return null
  }
  
  return values.map(value => {
    const path = findPath(options.value, value)
    return path ? path.join(props.separator) : String(value)
  })
}

// 暴露方法
defineExpose({
  getLabel: (value: any) => getLabel(value),
  getPathLabels,
  options: computed(() => options.value),
  loading: computed(() => loading.value)
})
</script>