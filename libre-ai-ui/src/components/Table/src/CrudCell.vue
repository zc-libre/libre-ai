<template>
  <div class="crud-cell">
    <!-- 自定义渲染函数 -->
    <component
      v-if="column.table?.render"
      :is="column.table.render"
      :value="value"
      :row="row"
      :column="column"
      :index="index"
    />
    
    <!-- 自定义组件 -->
    <component
      v-else-if="column.table?.component"
      :is="column.table.component"
      :model-value="value"
      :row="row"
      :column="column"
      :index="index"
      v-bind="column.table.props"
    />
    
    <!-- 根据字典显示 -->
    <dict-tag
      v-else-if="column.dict"
      :value="value"
      :dict="column.dict"
      :dict-key="`table-${column.key}`"
    />
    
    <!-- 根据类型渲染 -->
    <template v-else-if="column.table?.type">
      <!-- 标签类型 -->
      <el-tag
        v-if="column.table.type === 'tag'"
        :type="getTagType(value) as any"
        :size="column.table?.tagSize || 'small'"
      >
        {{ getTagLabel(value) }}
      </el-tag>
      
      <!-- 开关类型 -->
      <el-switch
        v-else-if="column.table.type === 'switch'"
        :model-value="value"
        :disabled="true"
      />
      
      <!-- 图片类型 -->
      <el-image
        v-else-if="column.table.type === 'image'"
        :src="value"
        :style="{ 
          width: column.table.imageWidth || '40px', 
          height: column.table.imageHeight || '40px' 
        }"
        fit="cover"
        :preview-src-list="[value]"
        preview-teleported
      />
      
      <!-- 进度条类型 -->
      <el-progress
        v-else-if="column.table.type === 'progress'"
        :percentage="value"
        :type="column.table.progressType || 'line'"
        :stroke-width="column.table.strokeWidth || 6"
        :show-text="column.table.showText !== false"
      />
      
      <!-- 链接类型 -->
      <el-link
        v-else-if="column.table.type === 'link'"
        :href="value"
        :type="column.table.linkType || 'primary'"
        :target="column.table.target || '_blank'"
      >
        {{ value }}
      </el-link>
      
      <!-- 日期格式化 -->
      <span v-else-if="column.table.type === 'date'">
        {{ formatDate(value) }}
      </span>
      
      <!-- 数字格式化 -->
      <span v-else-if="column.table.type === 'number'">
        {{ formatNumber(value) }}
      </span>
      
      <!-- 货币格式化 -->
      <span v-else-if="column.table.type === 'currency'">
        {{ formatCurrency(value) }}
      </span>
      
      <!-- 百分比格式化 -->
      <span v-else-if="column.table.type === 'percent'">
        {{ formatPercent(value) }}
      </span>
      
      <!-- 枚举显示 -->
      <span v-else-if="column.table.type === 'enum'">
        {{ getEnumLabel(value) }}
      </span>
      
      <!-- 默认文本 -->
      <span v-else>{{ value }}</span>
    </template>
    
    <!-- 格式化函数 -->
    <span v-else-if="column.table?.formatter">
      {{ column.table.formatter(value, row) }}
    </span>
    
    <!-- 默认显示 -->
    <span v-else>{{ displayValue }}</span>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { ElTag, ElSwitch, ElImage, ElProgress, ElLink } from 'element-plus'
import DictTag from './DictTag.vue'
import { formatDate as utilFormatDate, formatNumber as utilFormatNumber } from '../../../utils/format'
import type { CrudColumnConfig } from './crud-types'

interface Props {
  value: any
  row: any
  column: CrudColumnConfig
  index: number
}

const props = defineProps<Props>()

// 显示值
const displayValue = computed(() => {
  if (props.value === null || props.value === undefined) {
    return '-'
  }
  return String(props.value)
})

// 标签类型
const getTagType = (value: any) => {
  const tagOptions = props.column.table?.tagOptions
  return tagOptions?.[value]?.type || 'default'
}

const getTagLabel = (value: any) => {
  const tagOptions = props.column.table?.tagOptions
  return tagOptions?.[value]?.label || value
}

// 枚举标签
const getEnumLabel = (value: any) => {
  const enumOptions = props.column.table?.enumOptions
  return enumOptions?.[value] || value
}

// 格式化函数
const formatDate = (value: any) => {
  if (!value) return '-'
  const format = props.column.table?.dateFormat || 'YYYY-MM-DD'
  return utilFormatDate(value, format)
}

const formatNumber = (value: any) => {
  if (value === null || value === undefined || value === '') return '-'
  const options = props.column.table?.numberFormat || {}
  return utilFormatNumber(value, options)
}

const formatCurrency = (value: any) => {
  if (value === null || value === undefined || value === '') return '-'
  const options: Intl.NumberFormatOptions = {
    style: 'currency',
    currency: props.column.table?.currency || 'CNY',
    ...props.column.table?.currencyFormat
  }
  return new Intl.NumberFormat('zh-CN', options).format(value)
}

const formatPercent = (value: any) => {
  if (value === null || value === undefined || value === '') return '-'
  const options: Intl.NumberFormatOptions = {
    style: 'percent',
    minimumFractionDigits: props.column.table?.percentDecimals || 2,
    maximumFractionDigits: props.column.table?.percentDecimals || 2
  }
  return new Intl.NumberFormat('zh-CN', options).format(value / 100)
}
</script>

<style lang="scss" scoped>
.crud-cell {
  display: flex;
  align-items: center;
  min-height: 24px;
  
  .el-tag {
    margin: 0;
  }
  
  .el-image {
    border-radius: 4px;
    overflow: hidden;
    flex-shrink: 0;
  }
  
  .el-progress {
    flex: 1;
  }
}
</style>