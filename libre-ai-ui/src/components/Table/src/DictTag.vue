<template>
  <span class="dict-tag">
    <el-tag
      v-if="isArray"
      v-for="(item, index) in labelArray"
      :key="index"
      :type="getTagType(item.value)"
      :size="size"
      class="mr-1"
    >
      {{ item.label }}
    </el-tag>
    
    <el-tag
      v-else
      :type="getTagType(value)"
      :size="size"
    >
      {{ label }}
    </el-tag>
  </span>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { ElTag } from 'element-plus'
import { useDict } from './dict'
import type { DictConfig } from './crud-types'

interface Props {
  value: any
  dict: DictConfig
  dictKey: string
  size?: 'large' | 'default' | 'small'
  tagType?: 'default' | 'success' | 'info' | 'warning' | 'danger'
}

const props = withDefaults(defineProps<Props>(), {
  size: 'small',
  tagType: 'default'
})

// 使用字典
const { getLabel, getItem } = useDict(props.dictKey, props.dict)

// 是否为数组
const isArray = computed(() => Array.isArray(props.value))

// 单个值的标签
const label = computed(() => {
  if (isArray.value) return ''
  return getLabel(props.value)
})

// 数组值的标签列表
const labelArray = computed(() => {
  if (!isArray.value) return []
  
  return props.value.map((val: any) => ({
    value: val,
    label: getLabel(val)
  }))
})

// 获取标签类型
const getTagType = (value: any) => {
  const item = getItem(value)
  return item?.tagType || props.tagType
}
</script>

<style lang="scss" scoped>
.dict-tag {
  .el-tag {
    &:last-child {
      margin-right: 0;
    }
  }
}
</style>