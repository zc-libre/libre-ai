<template>
  <el-select
    :model-value="modelValue"
    :placeholder="placeholder"
    :multiple="multiple"
    :clearable="clearable"
    :filterable="filterable"
    :disabled="disabled"
    :size="size"
    :loading="loading"
    :remote="remote"
    :remote-method="remoteMethod"
    :reserve-keyword="reserveKeyword"
    :no-data-text="noDataText"
    :no-match-text="noMatchText"
    @update:model-value="handleChange"
    @visible-change="handleVisibleChange"
    @clear="handleClear"
    v-bind="$attrs"
  >
    <el-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
      :disabled="item.disabled"
    >
      <template v-if="$slots.option" #default="{ item: optionItem }">
        <slot name="option" :item="optionItem" />
      </template>
    </el-option>
    
    <template v-if="$slots.prefix" #prefix>
      <slot name="prefix" />
    </template>
    
    <template v-if="$slots.empty" #empty>
      <slot name="empty" />
    </template>
  </el-select>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElSelect, ElOption } from 'element-plus'
import { useDict } from './DictManager'
import type { DictConfig } from '../crud-types'

interface Props {
  modelValue?: any
  dict?: DictConfig
  dictKey?: string
  placeholder?: string
  multiple?: boolean
  clearable?: boolean
  filterable?: boolean
  disabled?: boolean
  size?: 'large' | 'default' | 'small'
  remote?: boolean
  reserveKeyword?: boolean
  noDataText?: string
  noMatchText?: string
  // 兼容旧的配置方式
  options?: Array<{ label: string; value: any; disabled?: boolean }>
  url?: string
  transform?: (data: any) => Array<{ label: string; value: any }>
}

const props = withDefaults(defineProps<Props>(), {
  clearable: true,
  filterable: false,
  multiple: false,
  disabled: false,
  size: 'default',
  remote: false,
  reserveKeyword: false,
  noDataText: '无数据',
  noMatchText: '无匹配数据'
})

const emit = defineEmits(['update:modelValue', 'change', 'visible-change', 'clear'])

// 计算字典配置
const dictConfig = computed<DictConfig>(() => {
  if (props.dict) {
    return props.dict
  }
  
  // 兼容旧的配置方式
  const config: DictConfig = {}
  
  if (props.options) {
    config.data = props.options
  }
  
  if (props.url) {
    config.url = props.url
  }
  
  if (props.transform) {
    config.transform = props.transform
  }
  
  config.multiple = props.multiple
  
  return config
})

// 使用字典管理器
const dictKey = computed(() => props.dictKey || `select-${Date.now()}-${Math.random()}`)
const { data: dictData, loading, error, getLabel } = useDict(dictKey.value, dictConfig.value)

// 计算选项
const options = computed(() => {
  if (props.options) {
    return props.options
  }
  return dictData.value
})

// 远程搜索方法
const remoteMethod = (query: string) => {
  if (props.remote && dictConfig.value.url) {
    // 实现远程搜索逻辑
    console.log('远程搜索:', query)
  }
}

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

// 暴露方法
defineExpose({
  getLabel: (value: any) => getLabel(value),
  options: computed(() => options.value),
  loading: computed(() => loading.value),
  error: computed(() => error.value)
})
</script>