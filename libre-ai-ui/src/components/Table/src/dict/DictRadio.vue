<template>
  <el-radio-group
    :model-value="modelValue"
    :size="size"
    :disabled="disabled"
    @update:model-value="handleChange"
    v-bind="$attrs"
  >
    <component
      :is="buttonStyle ? 'el-radio-button' : 'el-radio'"
      v-for="item in options"
      :key="item.value"
      :value="item.value"
      :disabled="item.disabled"
      :border="border && !buttonStyle"
    >
      {{ item.label }}
    </component>
  </el-radio-group>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { ElRadioGroup, ElRadio, ElRadioButton } from 'element-plus'
import { useDict } from './DictManager'
import type { DictConfig } from '../crud-types'

interface Props {
  modelValue?: any
  dict?: DictConfig
  dictKey?: string
  size?: 'large' | 'default' | 'small'
  disabled?: boolean
  border?: boolean
  buttonStyle?: boolean
  // 兼容旧的配置方式
  options?: Array<{ label: string; value: any; disabled?: boolean }>
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  disabled: false,
  border: false,
  buttonStyle: false
})

const emit = defineEmits(['update:modelValue', 'change'])

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
const dictKey = computed(() => props.dictKey || `radio-${Date.now()}-${Math.random()}`)
const { data: dictData, loading, getLabel } = useDict(dictKey.value, dictConfig.value)

// 计算选项
const options = computed(() => {
  if (props.options) {
    return props.options
  }
  return dictData.value
})

// 事件处理
const handleChange = (value: any) => {
  emit('update:modelValue', value)
  emit('change', value)
}

// 暴露方法
defineExpose({
  getLabel: (value: any) => getLabel(value),
  options: computed(() => options.value),
  loading: computed(() => loading.value)
})
</script>