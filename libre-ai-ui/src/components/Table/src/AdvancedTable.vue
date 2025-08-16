<template>
  <div :class="['advanced-table', themeClass]" v-loading="loading">
    <!-- 表格头部：搜索、操作栏、工具栏 -->
    <div v-if="showHeader" class="table-header mb-4">
      <!-- 自定义头部内容 -->
      <div v-if="$slots.header" class="custom-header mb-4">
        <slot name="header" />
      </div>
      
      <!-- 搜索区域 -->
      <table-search
        v-if="searchConfig && searchConfig.show !== false"
        v-bind="searchConfig"
        @search="onSearch"
        @reset="onReset"
        class="mb-4"
      />
      
      <!-- 操作栏和工具栏 -->
      <div class="flex justify-between items-center mb-4">
        <!-- 左侧操作栏 -->
        <div class="flex items-center gap-3">
          <slot name="toolbar-left">
            <div v-if="toolbarActions.length > 0" class="toolbar-actions">
              <el-button
                v-for="(action, index) in toolbarActions"
                :key="index"
                v-bind="action"
                @click="handleToolbarAction(action)"
              >
                <component :is="action.icon" v-if="action.icon" class="mr-1" />
                {{ action.label }}
              </el-button>
            </div>
          </slot>
        </div>
        
        <!-- 右侧工具栏 -->
        <div class="flex items-center gap-2">
          <slot name="toolbar-right">
            <!-- 列设置 -->
            <el-dropdown v-if="columnSettingsEnabled" trigger="click">
              <el-button size="small" circle>
                <Setting />
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <div class="p-3 w-48">
                    <div class="text-sm font-medium mb-2">列显示设置</div>
                    <el-checkbox-group v-model="visibleColumns" @change="onColumnVisibilityChange">
                      <div v-for="col in allColumns" :key="col.key" class="mb-1">
                        <el-checkbox :value="col.key" :disabled="col.required">
                          {{ col.title }}
                        </el-checkbox>
                      </div>
                    </el-checkbox-group>
                  </div>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            
            <!-- 刷新按钮 -->
            <el-button v-if="refreshEnabled" size="small" circle @click="handleRefresh">
              <Refresh />
            </el-button>
            
            <!-- 全屏切换 -->
            <el-button v-if="fullscreenEnabled" size="small" circle @click="toggleFullscreen">
              <FullScreen v-if="!isFullscreen" />
              <component :is="'Close'" v-else />
            </el-button>
          </slot>
        </div>
      </div>
    </div>

    <!-- 表格主体 -->
    <div
      class="table-container bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-700 overflow-hidden"
      :class="{ 'table-fullscreen': isFullscreen }"
    >
      <!-- 移动端横向滚动容器 -->
      <div class="table-scroll-container" :class="{ 'has-scroll-shadow': hasHorizontalScroll }">
      <!-- 批量操作栏 -->
      <div v-if="selection.length > 0 && batchActions.length > 0" class="batch-action-bar">
        <div class="flex items-center justify-between p-3 bg-blue-50 dark:bg-blue-900/20 border-b">
          <span class="text-sm">已选择 {{ selection.length }} 项</span>
          <div class="flex items-center gap-2">
            <el-button
              v-for="(action, index) in batchActions"
              :key="index"
              size="small"
              v-bind="action"
              @click="handleBatchAction(action)"
            >
              {{ action.label }}
            </el-button>
            <el-button size="small" @click="clearSelection">取消选择</el-button>
          </div>
        </div>
      </div>

      <el-table
        ref="tableRef"
        :data="displayData"
        :loading="loading"
        :row-key="rowKey"
        :height="tableHeight"
        :max-height="maxHeight"
        :stripe="stripe"
        :border="border"
        :size="size"
        :show-header="showTableHeader"
        :highlight-current-row="highlightCurrentRow"
        :row-class-name="rowClassName"
        :header-cell-style="headerCellStyle"
        :row-style="rowStyle"
        :cell-style="cellStyle"
        :empty-text="emptyText"
        @select="onSelect"
        @select-all="onSelectAll"
        @selection-change="onSelectionChange"
        @row-click="onRowClick"
        @row-dblclick="onRowDblclick"
        @current-change="onCurrentChange"
        @sort-change="onSortChange"
        @filter-change="onFilterChange"
        @expand-change="onExpandChange"
        v-bind="$attrs"
      >
        <!-- 选择列 -->
        <el-table-column
          v-if="selectable"
          type="selection"
          width="55"
          :selectable="selectableFunction"
          :reserve-selection="reserveSelection"
        />

        <!-- 展开列 -->
        <el-table-column v-if="expandable" type="expand" width="55">
          <template #default="props">
            <slot name="expand" :row="props.row" :index="props.$index" />
          </template>
        </el-table-column>

        <!-- 序号列 -->
        <el-table-column
          v-if="showIndex"
          type="index"
          :label="indexLabel"
          :width="indexWidth"
          :index="indexMethod"
        />

        <!-- 数据列 -->
        <el-table-column
          v-for="column in computedColumns"
          :key="column.key"
          :prop="column.key"
          :label="column.title"
          :width="column.width"
          :min-width="column.minWidth"
          :fixed="column.fixed"
          :align="column.align"
          :header-align="column.headerAlign"
          :sortable="column.sortable"
          :sort-method="column.sortMethod"
          :sort-by="column.sortBy"
          :sort-orders="column.sortOrders"
          :resizable="column.resizable"
          :formatter="column.formatter"
          :show-overflow-tooltip="column.showOverflowTooltip"
          :filters="column.filters"
          :filter-method="column.filterMethod"
          :filtered-value="column.filteredValue"
          :class-name="column.className"
          :label-class-name="column.labelClassName"
        >
          <!-- 自定义表头 -->
          <template v-if="column.headerSlot || $slots[`header_${column.key}`]" #header="scope">
            <slot
              :name="column.headerSlot || `header_${column.key}`"
              :column="scope.column"
              :index="scope.$index"
            />
          </template>

          <!-- 自定义内容 -->
          <template #default="scope">
            <div class="cell-content">
              <!-- 自定义渲染函数 -->
              <component
                v-if="column.render"
                :is="column.render"
                :row="scope.row"
                :column="scope.column"
                :index="scope.$index"
                :value="scope.row[column.key]"
              />
              <!-- 自定义插槽 -->
              <slot
                v-else-if="column.slot || $slots[`cell_${column.key}`]"
                :name="column.slot || `cell_${column.key}`"
                :row="scope.row"
                :column="scope.column"
                :index="scope.$index"
                :value="scope.row[column.key]"
              />
              <!-- 特殊类型渲染 -->
              <template v-else-if="column.type">
                <!-- 标签类型 -->
                <el-tag
                  v-if="column.type === 'tag'"
                  :type="getTagType(scope.row[column.key], column.tagOptions)"
                  :size="column.tagSize || 'small'"
                >
                  {{ getTagLabel(scope.row[column.key], column.tagOptions) }}
                </el-tag>
                <!-- 开关类型 -->
                <el-switch
                  v-else-if="column.type === 'switch'"
                  :model-value="scope.row[column.key]"
                  :disabled="column.disabled"
                  @change="onSwitchChange(scope.row, column.key, $event)"
                />
                <!-- 图片类型 -->
                <el-image
                  v-else-if="column.type === 'image'"
                  :src="scope.row[column.key]"
                  :style="{ width: column.imageWidth || '40px', height: column.imageHeight || '40px' }"
                  fit="cover"
                  :preview-src-list="[scope.row[column.key]]"
                  preview-teleported
                />
                <!-- 进度条类型 -->
                <el-progress
                  v-else-if="column.type === 'progress'"
                  :percentage="scope.row[column.key]"
                  :type="column.progressType"
                  :stroke-width="column.strokeWidth || 6"
                  :show-text="column.showText !== false"
                />
                <!-- 链接类型 -->
                <el-link
                  v-else-if="column.type === 'link'"
                  :href="scope.row[column.key]"
                  :type="column.linkType || 'primary'"
                  :target="column.target || '_blank'"
                >
                  {{ scope.row[column.key] }}
                </el-link>
                <!-- 日期格式化 -->
                <span v-else-if="column.type === 'date'">
                  {{ formatDate(scope.row[column.key], column.dateFormat) }}
                </span>
                <!-- 数字格式化 -->
                <span v-else-if="column.type === 'number'">
                  {{ formatNumber(scope.row[column.key], column.numberFormat) }}
                </span>
                <!-- 默认文本 -->
                <span v-else>{{ scope.row[column.key] }}</span>
              </template>
              <!-- 默认文本显示 -->
              <span v-else>{{ scope.row[column.key] }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column
          v-if="actionColumn && actionColumn.show !== false"
          :label="actionColumn.title || '操作'"
          :width="actionColumn.width || 'auto'"
          :fixed="actionColumn.fixed || 'right'"
          :align="actionColumn.align || 'center'"
          class-name="action-column"
        >
          <template #default="scope">
            <table-action
              :actions="getRowActions(scope.row, scope.$index)"
              :action-style="actionColumn.style"
              :size="actionColumn.size"
            />
          </template>
        </el-table-column>

        <!-- 空状态插槽 -->
        <template #empty>
          <slot name="empty">
            <div class="empty-content py-12">
              <div class="text-gray-400 mb-2">
                <Document class="w-12 h-12 mx-auto" />
              </div>
              <div class="text-gray-500">{{ emptyText || '暂无数据' }}</div>
            </div>
          </slot>
        </template>
      </el-table>
      </div>
    </div>

    <!-- 分页 -->
    <table-pagination
      v-if="paginationConfig && paginationConfig.show !== false"
      v-bind="paginationConfig"
      :total="total"
      :current-page="currentPage"
      :page-size="pageSize"
      @current-change="onPageChange"
      @size-change="onSizeChange"
      class="mt-4"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElTable, ElTableColumn, ElButton, ElDropdown, ElDropdownMenu, ElCheckboxGroup, ElCheckbox, ElTag, ElSwitch, ElImage, ElProgress, ElLink } from 'element-plus'
import { Setting, Refresh, FullScreen, Close, Document } from '@element-plus/icons-vue'
import TableAction from './TableAction.vue'
import TableSearch from './TableSearch.vue'
import TablePagination from './TablePagination.vue'
import { formatDate, formatNumber } from '@/utils/format'

interface Column {
  key: string
  title: string
  width?: number | string
  minWidth?: number | string
  fixed?: boolean | 'left' | 'right'
  align?: 'left' | 'center' | 'right'
  headerAlign?: 'left' | 'center' | 'right'
  sortable?: boolean | 'custom'
  sortMethod?: (a: any, b: any) => number
  sortBy?: string | string[] | ((row: any, index: number) => string)
  sortOrders?: string[]
  resizable?: boolean
  formatter?: (row: any, column: any, cellValue: any, index: number) => string
  showOverflowTooltip?: boolean
  filters?: Array<{ text: string; value: any }>
  filterMethod?: (value: any, row: any, column: any) => boolean
  filteredValue?: any[]
  className?: string
  labelClassName?: string
  required?: boolean
  render?: any
  slot?: string
  headerSlot?: string
  type?: 'tag' | 'switch' | 'image' | 'progress' | 'link' | 'date' | 'number'
  tagOptions?: Record<string, { label: string; type: string }>
  tagSize?: 'large' | 'default' | 'small'
  disabled?: boolean
  imageWidth?: string
  imageHeight?: string
  progressType?: 'line' | 'circle' | 'dashboard'
  strokeWidth?: number
  showText?: boolean
  linkType?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  target?: string
  dateFormat?: string
  numberFormat?: object
}

interface ActionItem {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  icon?: any
  label?: string
  tooltip?: string
  show?: boolean | ((row: any, index: number) => boolean)
  disabled?: boolean | ((row: any, index: number) => boolean)
  onClick: (row: any, index: number) => void
}

interface Props {
  columns: Column[]
  data?: any[]
  loading?: boolean
  request?: (params: any) => Promise<any>
  rowKey?: string | ((row: any) => string)
  height?: number | string
  maxHeight?: number | string
  stripe?: boolean
  border?: boolean
  size?: 'large' | 'default' | 'small'
  showHeader?: boolean
  showTableHeader?: boolean
  highlightCurrentRow?: boolean
  rowClassName?: string | ((row: any, index: number) => string)
  headerCellStyle?: object | ((data: any) => object)
  rowStyle?: object | ((data: any) => object)
  cellStyle?: object | ((data: any) => object)
  emptyText?: string
  theme?: 'default' | 'model-management'
  
  // 选择相关
  selectable?: boolean
  selectableFunction?: (row: any, index: number) => boolean
  reserveSelection?: boolean
  
  // 展开相关
  expandable?: boolean
  
  // 序号相关
  showIndex?: boolean
  indexLabel?: string
  indexWidth?: number
  indexMethod?: (index: number) => number
  
  // 分页相关
  paginationConfig?: {
    show?: boolean
    total?: number
    currentPage?: number
    pageSize?: number
    pageSizes?: number[]
    layout?: string
    background?: boolean
    small?: boolean
    hideOnSinglePage?: boolean
  }
  
  // 搜索相关
  searchConfig?: {
    show?: boolean
    fields?: Array<{
      key: string
      label: string
      type?: 'input' | 'select' | 'date' | 'daterange'
      options?: Array<{ label: string; value: any }>
      placeholder?: string
    }>
    labelWidth?: string
    showReset?: boolean
    showCollapse?: boolean
  }
  
  // 操作列
  actionColumn?: {
    show?: boolean
    title?: string
    width?: number | string
    fixed?: boolean | 'left' | 'right'
    align?: 'left' | 'center' | 'right'
    style?: 'text' | 'button' | 'circle'
    size?: 'large' | 'default' | 'small'
    actions?: ActionItem[]
  }
  
  // 工具栏
  toolbarActions?: ActionItem[]
  batchActions?: ActionItem[]
  columnSettingsEnabled?: boolean
  refreshEnabled?: boolean
  fullscreenEnabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  rowKey: 'id',
  stripe: true,
  border: false,
  size: 'default',
  showHeader: true,
  showTableHeader: true,
  highlightCurrentRow: false,
  selectable: false,
  reserveSelection: true,
  expandable: false,
  showIndex: false,
  indexLabel: '#',
  indexWidth: 50,
  theme: 'default',
  toolbarActions: () => [],
  batchActions: () => [],
  columnSettingsEnabled: true,
  refreshEnabled: true,
  fullscreenEnabled: false
})

const emit = defineEmits([
  'row-click',
  'row-dblclick',
  'current-change', 
  'select',
  'select-all',
  'selection-change',
  'sort-change',
  'filter-change',
  'expand-change',
  'page-change',
  'size-change',
  'search',
  'reset',
  'refresh',
  'switch-change'
])

// 响应式数据
const tableRef = ref()
const displayData = ref(props.data || [])
const selection = ref([])
const currentPage = ref(props.paginationConfig?.currentPage || 1)
const pageSize = ref(props.paginationConfig?.pageSize || 20)
const total = ref(props.paginationConfig?.total || 0)
const searchParams = ref({})
const isFullscreen = ref(false)
const visibleColumns = ref(props.columns.map(col => col.key))
const hasHorizontalScroll = ref(false)

// 计算属性
const themeClass = computed(() => `advanced-table--${props.theme}`)

const allColumns = computed(() => props.columns)

const screenWidth = ref(window.innerWidth)

const computedColumns = computed(() => {
  let cols = props.columns.filter(col => visibleColumns.value.includes(col.key))
  
  // 响应式列处理
  if (screenWidth.value <= 480) {
    // 超小屏：只显示必要列
    cols = cols.filter(col => col.required)
  } else if (screenWidth.value <= 768) {
    // 移动端：只显示必要列和未设置 hideOnMobile 的列
    cols = cols.filter(col => col.required || !col.hideOnMobile)
  } else if (screenWidth.value <= 1024) {
    // 平板端：隐藏 hideOnTablet 的列
    cols = cols.filter(col => !col.hideOnTablet)
  }
  
  // 自适应列宽
  return cols.map(col => {
    const adaptiveCol = { ...col }
    
    // 移动端列宽调整
    if (screenWidth.value <= 768) {
      // 如果设置了宽度，在移动端适当缩小
      if (adaptiveCol.width && typeof adaptiveCol.width === 'number') {
        adaptiveCol.width = Math.max(adaptiveCol.width * 0.8, 80)
      }
      // 设置最小宽度
      if (!adaptiveCol.minWidth) {
        adaptiveCol.minWidth = 80
      }
    }
    
    return adaptiveCol
  })
})

const tableHeight = computed(() => {
  if (isFullscreen.value) return 'calc(100vh - 200px)'
  return props.height
})

// 表格事件处理
const onSelect = (selection: any[], row: any) => {
  emit('select', selection, row)
}

const onSelectAll = (selection: any[]) => {
  emit('select-all', selection)
}

const onSelectionChange = (selection: any[]) => {
  selection.value = selection
  emit('selection-change', selection)
}

const onRowClick = (row: any, column: any, event: Event) => {
  emit('row-click', row, column, event)
}

const onRowDblclick = (row: any, column: any, event: Event) => {
  emit('row-dblclick', row, column, event)
}

const onCurrentChange = (currentRow: any, oldCurrentRow: any) => {
  emit('current-change', currentRow, oldCurrentRow)
}

const onSortChange = ({ column, prop, order }: any) => {
  emit('sort-change', { column, prop, order })
  if (props.request) {
    loadData()
  }
}

const onFilterChange = (filters: any) => {
  emit('filter-change', filters)
  if (props.request) {
    loadData()
  }
}

const onExpandChange = (row: any, expandedRows: any[]) => {
  emit('expand-change', row, expandedRows)
}

const onSwitchChange = (row: any, key: string, value: boolean) => {
  row[key] = value
  emit('switch-change', row, key, value)
}

// 分页事件
const onPageChange = (page: number) => {
  currentPage.value = page
  emit('page-change', page)
  if (props.request) {
    loadData()
  }
}

const onSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  emit('size-change', size)
  if (props.request) {
    loadData()
  }
}

// 搜索事件
const onSearch = (params: any) => {
  searchParams.value = params
  currentPage.value = 1
  emit('search', params)
  if (props.request) {
    loadData()
  }
}

const onReset = () => {
  searchParams.value = {}
  currentPage.value = 1
  emit('reset')
  if (props.request) {
    loadData()
  }
}

// 工具栏操作
const handleToolbarAction = (action: ActionItem) => {
  action.onClick(null, -1)
}

const handleBatchAction = (action: ActionItem) => {
  action.onClick(selection.value, -1)
}

const handleRefresh = () => {
  emit('refresh')
  if (props.request) {
    loadData()
  }
}

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

const onColumnVisibilityChange = () => {
  // 列显示/隐藏变化处理
}

const clearSelection = () => {
  tableRef.value?.clearSelection()
}

// 操作列相关
const getRowActions = (row: any, index: number) => {
  if (!props.actionColumn?.actions) return []
  
  return props.actionColumn.actions.filter(action => {
    if (typeof action.show === 'function') {
      return action.show(row, index)
    }
    return action.show !== false
  }).map(action => ({
    ...action,
    disabled: typeof action.disabled === 'function' ? action.disabled(row, index) : action.disabled,
    onClick: () => action.onClick(row, index)
  }))
}

// 格式化工具函数
const getTagType = (value: any, options?: Record<string, any>) => {
  return options?.[value]?.type || 'default'
}

const getTagLabel = (value: any, options?: Record<string, any>) => {
  return options?.[value]?.label || value
}

// 数据加载
const loadData = async () => {
  if (!props.request) return
  
  const params = {
    page: currentPage.value,
    size: pageSize.value,
    ...searchParams.value
  }
  
  try {
    const result = await props.request(params)
    
    if (Array.isArray(result)) {
      displayData.value = result
      total.value = result.length
    } else if (result && typeof result === 'object') {
      if (Array.isArray(result.data)) {
        displayData.value = result.data
        total.value = result.total || result.data.length
      } else if (Array.isArray(result.records)) {
        displayData.value = result.records
        total.value = result.total || result.records.length
      } else {
        displayData.value = []
        total.value = 0
      }
    }
  } catch (error) {
    console.error('Table request error:', error)
    displayData.value = []
    total.value = 0
  }
}

// 监听数据变化
watch(() => props.data, (newData) => {
  if (newData && !props.request) {
    displayData.value = newData
    total.value = newData.length
  }
}, { immediate: true })

// 检测横向滚动
const checkHorizontalScroll = () => {
  const scrollContainer = document.querySelector('.table-scroll-container')
  if (scrollContainer) {
    hasHorizontalScroll.value = scrollContainer.scrollWidth > scrollContainer.clientWidth
  }
}

// 窗口大小变化处理
const handleResize = () => {
  screenWidth.value = window.innerWidth
  // 重新计算列显示
  nextTick(() => {
    checkHorizontalScroll()
  })
}

// 生命周期
onMounted(() => {
  if (props.request) {
    loadData()
  }
  
  // 检测横向滚动
  nextTick(() => {
    checkHorizontalScroll()
  })
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理事件监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 暴露方法
defineExpose({
  reload: loadData,
  clearSelection,
  getSelectionRows: () => selection.value,
  tableRef
})
</script>

<style lang="scss" scoped>
.advanced-table {
  .table-header {
    .toolbar-actions {
      display: flex;
      gap: 8px;
    }
  }

  .table-container {
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgb(0 0 0 / 8%);
    }

    &.table-fullscreen {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 2000;
      margin: 20px;
      height: calc(100vh - 40px);
    }
  }

  .table-scroll-container {
    position: relative;
    overflow-x: auto;
    overflow-y: hidden;
    
    // 横向滚动阴影效果
    &.has-scroll-shadow {
      &::before,
      &::after {
        content: '';
        position: absolute;
        top: 0;
        bottom: 0;
        width: 20px;
        z-index: 10;
        pointer-events: none;
        transition: opacity 0.3s ease;
      }
      
      &::before {
        left: 0;
        background: linear-gradient(to right, rgba(0, 0, 0, 0.1), transparent);
      }
      
      &::after {
        right: 0;
        background: linear-gradient(to left, rgba(0, 0, 0, 0.1), transparent);
      }
    }
    
    // 移动端滚动优化
    @media (max-width: 768px) {
      -webkit-overflow-scrolling: touch;
      scroll-behavior: smooth;
      
      &::-webkit-scrollbar {
        height: 4px;
      }
      
      &::-webkit-scrollbar-track {
        background: #f0f2f5;
        border-radius: 2px;
      }
      
      &::-webkit-scrollbar-thumb {
        background: #c0c4cc;
        border-radius: 2px;
        
        &:hover {
          background: #a8abb2;
        }
      }
    }
  }

  .batch-action-bar {
    border-bottom: 1px solid var(--el-border-color-light);
  }

  .action-column {
    .cell-content {
      display: flex;
      justify-content: center;
    }
  }

  .empty-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
}

:deep(.el-table) {
  .cell-content {
    display: flex;
    align-items: center;
  }

  .el-table__row:hover {
    background-color: #f8fafc;
    transform: translateX(2px);
    transition: all 0.2s ease;
  }
}

html.dark {
  .advanced-table {
    :deep(.el-table) {
      .el-table__row:hover {
        background-color: #334155;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .advanced-table {
    .table-header {
      .flex {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;
      }
      
      .toolbar-actions {
        flex-direction: column;
        align-items: stretch;
        
        .el-button {
          justify-content: center;
        }
      }
    }
    
    .table-container {
      // 移动端表格最小宽度
      .table-scroll-container {
        min-width: 100%;
        
        :deep(.el-table) {
          min-width: 600px; // 确保表格有足够宽度显示内容
        }
      }
    }
    
    // 移动端分页
    .table-pagination {
      :deep(.el-pagination) {
        justify-content: center;
        flex-wrap: wrap;
        
        .el-pagination__total,
        .el-pagination__sizes {
          order: 3;
          flex-basis: 100%;
          text-align: center;
          margin-top: 8px;
        }
        
        .el-pager {
          li {
            min-width: 32px;
            height: 32px;
            line-height: 32px;
            font-size: 14px;
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .advanced-table {
    .table-header {
      .custom-header {
        text-align: center;
      }
    }
    
    .table-container {
      .table-scroll-container {
        :deep(.el-table) {
          min-width: 500px;
          font-size: 13px;
          
          .el-table__header th {
            padding: 12px 8px;
            font-size: 12px;
          }
          
          .el-table__body td {
            padding: 12px 8px;
          }
          
          // 紧凑模式
          &.el-table--small {
            .el-table__header th {
              padding: 8px 6px;
            }
            
            .el-table__body td {
              padding: 8px 6px;
            }
          }
        }
      }
    }
    
    .batch-action-bar {
      .flex {
        flex-direction: column;
        align-items: stretch;
        gap: 8px;
        
        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>