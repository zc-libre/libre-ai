<template>
  <div v-if="show && !hideOnSinglePage || (hideOnSinglePage && total > pageSize)" class="table-pagination">
    <div class="pagination-info">
      <slot name="info">
        <span v-if="showInfo" class="pagination-info-text">
          共 {{ total }} 条，每页 {{ pageSize }} 条
          <template v-if="total > 0">
            ，第 {{ startIndex }}-{{ endIndex }} 条
          </template>
        </span>
      </slot>
    </div>
    
    <div class="pagination-main">
      <el-pagination
        v-model:current-page="internalCurrentPage"
        v-model:page-size="internalPageSize"
        :page-sizes="pageSizes"
        :total="total"
        :layout="layout"
        :background="background"
        :small="small"
        :disabled="disabled"
        :hide-on-single-page="false"
        :prev-text="prevText"
        :next-text="nextText"
        :pager-count="pagerCount"
        :page-count="pageCount"
        :default-current-page="defaultCurrentPage"
        :default-page-size="defaultPageSize"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        @prev-click="handlePrevClick"
        @next-click="handleNextClick"
        class="pagination-component"
      >
        <!-- 自定义插槽 -->
        <template v-if="$slots.default" #default>
          <slot />
        </template>
      </el-pagination>
    </div>
    
    <div class="pagination-extra">
      <slot name="extra">
        <!-- 跳转功能 -->
        <div v-if="showJumper" class="pagination-jumper">
          <span class="jumper-text">跳至</span>
          <el-input
            v-model="jumpPage"
            :size="small ? 'small' : 'default'"
            class="jumper-input"
            @keyup.enter="handleJump"
            @blur="handleJump"
          />
          <span class="jumper-text">页</span>
        </div>
        
        <!-- 页面大小选择器 -->
        <div v-if="showSizer && !layout.includes('sizes')" class="pagination-sizer">
          <el-select
            v-model="internalPageSize"
            :size="small ? 'small' : 'default'"
            class="sizer-select"
            @change="handleSizeChange"
          >
            <el-option
              v-for="size in pageSizes"
              :key="size"
              :label="`${size} 条/页`"
              :value="size"
            />
          </el-select>
        </div>
        
        <!-- 刷新按钮 -->
        <el-button
          v-if="showRefresh"
          :size="small ? 'small' : 'default'"
          :loading="refreshing"
          circle
          @click="handleRefresh"
        >
          <Refresh />
        </el-button>
      </slot>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import { ElPagination, ElInput, ElSelect, ElOption, ElButton } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

interface Props {
  // 基础分页属性
  total: number
  currentPage?: number
  pageSize?: number
  pageSizes?: number[]
  layout?: string
  background?: boolean
  small?: boolean
  disabled?: boolean
  hideOnSinglePage?: boolean
  
  // 自定义属性
  show?: boolean
  showInfo?: boolean
  showJumper?: boolean
  showSizer?: boolean
  showRefresh?: boolean
  refreshing?: boolean
  
  // 高级配置
  prevText?: string
  nextText?: string
  pagerCount?: number
  pageCount?: number
  defaultCurrentPage?: number
  defaultPageSize?: number
  
  // 本地化
  infoTemplate?: string
  jumperText?: string
}

const props = withDefaults(defineProps<Props>(), {
  currentPage: 1,
  pageSize: 20,
  pageSizes: () => [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  background: true,
  small: false,
  disabled: false,
  hideOnSinglePage: false,
  show: true,
  showInfo: false,
  showJumper: false,
  showSizer: false,
  showRefresh: false,
  refreshing: false,
  pagerCount: 7,
  defaultCurrentPage: 1,
  defaultPageSize: 20
})

const emit = defineEmits([
  'update:currentPage',
  'update:pageSize', 
  'current-change',
  'size-change',
  'prev-click',
  'next-click',
  'refresh'
])

// 响应式数据
const jumpPage = ref('')

// 内部状态管理
const internalCurrentPage = computed({
  get: () => props.currentPage,
  set: (value) => {
    emit('update:currentPage', value)
    emit('current-change', value)
  }
})

const internalPageSize = computed({
  get: () => props.pageSize,
  set: (value) => {
    emit('update:pageSize', value)
    emit('size-change', value)
  }
})

// 计算属性
const startIndex = computed(() => {
  if (props.total === 0) return 0
  return (props.currentPage - 1) * props.pageSize + 1
})

const endIndex = computed(() => {
  const end = props.currentPage * props.pageSize
  return end > props.total ? props.total : end
})

const totalPages = computed(() => {
  return Math.ceil(props.total / props.pageSize)
})

// 事件处理
const handleCurrentChange = (page: number) => {
  internalCurrentPage.value = page
}

const handleSizeChange = (size: number) => {
  internalPageSize.value = size
  // 如果当前页超出范围，调整到最后一页
  const maxPage = Math.ceil(props.total / size)
  if (props.currentPage > maxPage && maxPage > 0) {
    internalCurrentPage.value = maxPage
  }
}

const handlePrevClick = (page: number) => {
  emit('prev-click', page)
}

const handleNextClick = (page: number) => {
  emit('next-click', page)
}

const handleJump = () => {
  const page = parseInt(jumpPage.value)
  if (page && page > 0 && page <= totalPages.value) {
    internalCurrentPage.value = page
    jumpPage.value = ''
  } else {
    jumpPage.value = ''
  }
}

const handleRefresh = () => {
  emit('refresh')
}

// 跳转到指定页
const jumpTo = (page: number) => {
  if (page > 0 && page <= totalPages.value) {
    internalCurrentPage.value = page
  }
}

// 上一页
const prev = () => {
  if (props.currentPage > 1) {
    internalCurrentPage.value = props.currentPage - 1
  }
}

// 下一页
const next = () => {
  if (props.currentPage < totalPages.value) {
    internalCurrentPage.value = props.currentPage + 1
  }
}

// 第一页
const first = () => {
  internalCurrentPage.value = 1
}

// 最后一页
const last = () => {
  internalCurrentPage.value = totalPages.value
}

// 监听跳转输入
watch(jumpPage, (newValue) => {
  // 只允许输入数字
  const numericValue = newValue.replace(/\D/g, '')
  if (numericValue !== newValue) {
    jumpPage.value = numericValue
  }
})

// 暴露方法
defineExpose({
  jumpTo,
  prev,
  next,
  first,
  last,
  totalPages
})
</script>

<style lang="scss" scoped>
.table-pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px 0;
  
  .pagination-info {
    flex-shrink: 0;
    
    .pagination-info-text {
      font-size: 14px;
      color: var(--el-text-color-regular);
      white-space: nowrap;
    }
  }
  
  .pagination-main {
    flex: 1;
    display: flex;
    justify-content: center;
    
    .pagination-component {
      :deep(.el-pagination) {
        .el-pagination__total {
          font-weight: 500;
        }
        
        .el-pager li {
          transition: all 0.2s ease;
          
          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 2px 8px rgb(0 0 0 / 10%);
          }
          
          &.is-active {
            background: var(--el-color-primary);
            border-color: var(--el-color-primary);
            transform: translateY(-1px);
            box-shadow: 0 3px 12px rgb(64 158 255 / 30%);
          }
        }
        
        .btn-prev,
        .btn-next {
          transition: all 0.2s ease;
          
          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 2px 8px rgb(0 0 0 / 10%);
          }
        }
      }
    }
  }
  
  .pagination-extra {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    gap: 12px;
    
    .pagination-jumper {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .jumper-text {
        font-size: 14px;
        color: var(--el-text-color-regular);
        white-space: nowrap;
      }
      
      .jumper-input {
        width: 60px;
        
        :deep(.el-input__inner) {
          text-align: center;
        }
      }
    }
    
    .pagination-sizer {
      .sizer-select {
        width: 120px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .table-pagination {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    
    .pagination-info {
      text-align: center;
      order: 3;
    }
    
    .pagination-main {
      order: 1;
      
      .pagination-component {
        :deep(.el-pagination) {
          justify-content: center;
          
          // 移动端隐藏不必要的元素
          .el-pagination__total,
          .el-pagination__sizes {
            display: none;
          }
        }
      }
    }
    
    .pagination-extra {
      order: 2;
      justify-content: center;
      flex-wrap: wrap;
    }
  }
}

@media (max-width: 480px) {
  .table-pagination {
    .pagination-main {
      .pagination-component {
        :deep(.el-pagination) {
          .el-pagination__jump {
            display: none;
          }
          
          .el-pager {
            li {
              min-width: 32px;
              height: 32px;
              line-height: 32px;
              font-size: 12px;
            }
          }
          
          .btn-prev,
          .btn-next {
            min-width: 32px;
            height: 32px;
            line-height: 32px;
          }
        }
      }
    }
  }
}

// 暗色主题适配
html.dark {
  .table-pagination {
    .pagination-info-text {
      color: var(--el-text-color-regular);
    }
    
    .pagination-main {
      .pagination-component {
        :deep(.el-pagination) {
          .el-pager li {
            &:hover {
              box-shadow: 0 2px 8px rgb(255 255 255 / 10%);
            }
            
            &.is-active {
              box-shadow: 0 3px 12px rgb(64 158 255 / 40%);
            }
          }
        }
      }
    }
  }
}

// 紧凑模式
.table-pagination {
  &.is-small {
    padding: 12px 0;
    gap: 12px;
    
    .pagination-info-text {
      font-size: 13px;
    }
    
    .pagination-extra {
      gap: 8px;
      
      .pagination-jumper {
        gap: 6px;
        
        .jumper-input {
          width: 50px;
        }
      }
    }
  }
}
</style>