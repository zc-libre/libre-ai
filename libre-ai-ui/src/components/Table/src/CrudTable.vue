<template>
  <div class="crud-table" :class="[`crud-table--${theme}`, customClass]">
    <!-- 搜索区域 -->
    <div v-if="showSearch" class="crud-search mb-4">
      <crud-search
        v-model="searchForm"
        :columns="searchColumns"
        :config="searchConfig"
        @search="handleSearch"
        @reset="handleReset"
      />
    </div>

    <!-- 工具栏 -->
    <div v-if="showToolbar" class="crud-toolbar mb-4 flex justify-between items-center">
      <!-- 左侧操作按钮 -->
      <div class="flex items-center gap-3">
        <el-button
          v-if="toolbarConfig.add"
          type="primary"
          :icon="Plus"
          @click="handleAdd"
          v-auth="addPermission"
        >
          {{ addButtonText }}
        </el-button>
        
        <el-button
          v-if="hasSelection && toolbarConfig.batchDelete"
          type="danger"
          :icon="Delete"
          :disabled="!selection.length"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>

        <!-- 自定义工具栏按钮 -->
        <template v-for="button in toolbarButtons" :key="button.key">
          <el-button
            v-if="button.show !== false"
            :type="button.type"
            :icon="button.icon"
            :disabled="typeof button.disabled === 'function' ? button.disabled() : button.disabled"
            @click="button.onClick"
            v-auth="button.permission"
          >
            {{ button.label }}
          </el-button>
        </template>
      </div>

      <!-- 右侧工具 -->
      <div class="flex items-center gap-2">
        <el-button
          v-if="toolbarConfig.export"
          size="small"
          :icon="Download"
          @click="handleExport"
        >
          导出
        </el-button>
        
        <el-button
          v-if="toolbarConfig.refresh"
          size="small"
          :icon="Refresh"
          @click="handleRefresh"
        >
          刷新
        </el-button>
        
        <el-dropdown v-if="toolbarConfig.columns" trigger="click">
          <el-button size="small" :icon="Setting" circle />
          <template #dropdown>
            <el-dropdown-menu>
              <div class="p-3 w-48">
                <div class="text-sm font-medium mb-2">列显示设置</div>
                <el-checkbox-group v-model="visibleColumns">
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
      </div>
    </div>

    <!-- 批量操作提示 -->
    <div v-if="selection.length > 0" class="selection-bar mb-4 p-3 bg-blue-50 dark:bg-blue-900/20 rounded border">
      <div class="flex items-center justify-between">
        <span class="text-sm">已选择 {{ selection.length }} 项</span>
        <div class="flex items-center gap-2">
          <el-button size="small" @click="clearSelection">取消选择</el-button>
        </div>
      </div>
    </div>

    <!-- 表格主体 -->
    <div class="crud-table-container">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        :row-key="rowKey"
        :height="height"
        :max-height="maxHeight"
        :stripe="stripe"
        :border="border"
        :size="size"
        :show-header="showHeader"
        :highlight-current-row="highlightCurrentRow"
        :empty-text="emptyText"
        @select="onSelect"
        @select-all="onSelectAll"
        @selection-change="onSelectionChange"
        @row-click="onRowClick"
        @row-dblclick="onRowDblClick"
        @sort-change="onSortChange"
        v-bind="$attrs"
      >
        <!-- 选择列 -->
        <el-table-column
          v-if="hasSelection"
          type="selection"
          width="55"
          :selectable="selectableFunction"
        />

        <!-- 序号列 -->
        <el-table-column
          v-if="showIndex"
          type="index"
          label="#"
          width="60"
          :index="indexMethod"
        />

        <!-- 数据列 -->
        <el-table-column
          v-for="column in displayColumns"
          :key="column.key"
          :prop="column.key"
          :label="column.title"
          :width="column.width"
          :min-width="column.minWidth"
          :fixed="column.fixed"
          :align="column.align"
          :sortable="column.sortable"
          :show-overflow-tooltip="column.table?.showOverflowTooltip !== false"
        >
          <template #default="{ row, column: tableColumn, $index }">
            <crud-cell
              :value="row[column.key]"
              :row="row"
              :column="column"
              :index="$index"
            />
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column
          v-if="rowActions.length > 0"
          label="操作"
          :width="actionColumnWidth"
          fixed="right"
          align="center"
        >
          <template #default="{ row, $index }">
            <crud-actions
              :actions="getRowActions(row, $index)"
              :row="row"
              :index="$index"
              :size="actionSize"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div v-if="showPagination" class="crud-pagination mt-4 flex justify-center">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="pageSizes"
        :layout="paginationLayout"
        :background="true"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <!-- 表单对话框 -->
    <crud-form-dialog
      v-model="dialogVisible"
      :mode="dialogMode"
      :title="dialogTitle"
      :columns="formColumns"
      :form-data="currentRow"
      :rules="formRules"
      :config="formConfig"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { ElTable, ElTableColumn, ElButton, ElPagination, ElCheckboxGroup, ElCheckbox, ElDropdown, ElDropdownMenu, ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Download, Refresh, Setting } from '@element-plus/icons-vue'
import CrudSearch from './CrudSearch.vue'
import CrudCell from './CrudCell.vue'
import CrudActions from './CrudActions.vue'
import CrudFormDialog from './CrudFormDialog.vue'
import type { CrudOptions, CrudExpose, CrudColumnConfig, CrudActionConfig, CrudContext } from './crud-types'

interface Props {
  // CRUD 配置
  options: CrudOptions
  // 主题
  theme?: 'default' | 'card'
  // 自定义样式类
  customClass?: string
}

const props = withDefaults(defineProps<Props>(), {
  theme: 'default'
})

const emit = defineEmits([
  'load',
  'add',
  'edit',
  'delete',
  'selection-change',
  'row-click',
  'row-dblclick',
  'search',
  'reset'
])

// 表格引用
const tableRef = ref()

// 数据相关
const tableData = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索相关
const searchForm = ref<Record<string, any>>({})

// 选择相关
const selection = ref<any[]>([])

// 对话框相关
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit' | 'view'>('add')
const currentRow = ref<any>({})

// 列显示控制
const visibleColumns = ref<string[]>([])

// 计算属性
const { columns, request, rowActions: configRowActions, toolbar, search, form, pagination, table } = props.options

// 表格配置
const rowKey = computed(() => table?.rowKey || 'id')
const height = computed(() => table?.height)
const maxHeight = computed(() => table?.maxHeight)
const stripe = computed(() => table?.stripe !== false)
const border = computed(() => table?.border || false)
const size = computed(() => table?.size || 'default')
const showHeader = computed(() => table?.showHeader !== false)
const highlightCurrentRow = computed(() => table?.highlightCurrentRow || false)
const emptyText = computed(() => table?.emptyText || '暂无数据')

// 选择配置
const hasSelection = computed(() => table?.selection?.show || false)
const selectableFunction = computed(() => table?.selection?.selectable)

// 序号配置
const showIndex = computed(() => table?.showIndex || false)
const indexMethod = computed(() => (index: number) => {
  return (currentPage.value - 1) * pageSize.value + index + 1
})

// 搜索配置
const showSearch = computed(() => search?.show !== false)
const searchConfig = computed(() => search || {})
const searchColumns = computed(() => {
  return Object.entries(columns).filter(([key, column]) => 
    !column.hideInSearch && column.search
  ).map(([key, column]) => ({
    key,
    ...column,
    ...column.search
  }))
})

// 工具栏配置
const toolbarConfig = computed(() => toolbar || {})
const showToolbar = computed(() => toolbar?.show !== false)
const addButtonText = computed(() => {
  const addConfig = toolbarConfig.value.add
  if (typeof addConfig === 'object' && addConfig.label) {
    return addConfig.label
  }
  return '新增'
})
const addPermission = computed(() => props.options.permission?.add)
const toolbarButtons = computed(() => toolbarConfig.value.buttons || [])

// 表单配置
const formConfig = computed(() => {
  const currentFormConfig = form?.[dialogMode.value]
  const width = currentFormConfig?.dialog?.width || '600px'
  return {
    width: typeof width === 'number' ? `${width}px` : width,
    fullscreen: currentFormConfig?.dialog?.fullscreen || false,
    labelWidth: currentFormConfig?.labelWidth || '120px',
    size: currentFormConfig?.size || 'default',
    columns: currentFormConfig?.columns || 1
  }
})
const formColumns = computed(() => {
  const mode = dialogMode.value
  return Object.entries(columns).filter(([key, column]) => {
    if (mode === 'add' && column.hideInAdd) return false
    if (mode === 'edit' && column.hideInEdit) return false
    if (mode === 'view' && column.hideInView) return false
    const modeConfig = column[mode as keyof CrudColumnConfig]
    return !column.hideInForm && modeConfig
  }).map(([key, column]) => ({
    key,
    ...column
  }))
})

const formRules = computed(() => {
  const rules: Record<string, any[]> = {}
  Object.entries(columns).forEach(([key, column]) => {
    if (column.rules) {
      rules[key] = column.rules
    }
  })
  return rules
})

// 分页配置
const showPagination = computed(() => pagination?.show !== false)
const pageSizes = computed(() => pagination?.pageSizes || [10, 20, 50, 100])
const paginationLayout = computed(() => pagination?.layout || 'total, sizes, prev, pager, next, jumper')

// 列配置
const allColumns = computed(() => Object.entries(columns).map(([key, column]) => ({ key, ...column })))
const displayColumns = computed(() => {
  return allColumns.value.filter(column => 
    !column.hideInTable && 
    (visibleColumns.value.length === 0 || visibleColumns.value.includes(column.key))
  )
})

// 操作列配置
const rowActions = computed(() => configRowActions || [])
const actionColumnWidth = computed(() => {
  const actionCount = rowActions.value.length
  return Math.max(actionCount * 80, 120)
})
const actionSize = computed(() => size.value === 'small' ? 'small' : 'default')

// 对话框标题
const dialogTitle = computed(() => {
  const titles = {
    add: '新增',
    edit: '编辑',
    view: '查看'
  }
  return titles[dialogMode.value]
})

// 初始化列显示状态
watch(() => props.options.columns, (newColumns) => {
  visibleColumns.value = Object.keys(newColumns).filter(key => !newColumns[key].hideInTable)
}, { immediate: true })

// 数据加载
const loadData = async () => {
  if (!request?.pageRequest) return
  
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      search: searchForm.value
    }
    
    const result = await request.pageRequest(params)
    tableData.value = result.data || []
    total.value = result.total || 0
    
    emit('load', result)
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadData()
  emit('search', searchForm.value)
}

const handleReset = () => {
  searchForm.value = {}
  currentPage.value = 1
  loadData()
  emit('reset')
}

// 分页处理
const handlePageChange = (page: number) => {
  currentPage.value = page
  loadData()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadData()
}

// 新增处理
const handleAdd = () => {
  dialogMode.value = 'add'
  currentRow.value = {}
  dialogVisible.value = true
}

// 编辑处理
const handleEdit = (row: any) => {
  dialogMode.value = 'edit'
  currentRow.value = { ...row }
  dialogVisible.value = true
}

// 查看处理
const handleView = (row: any) => {
  dialogMode.value = 'view'
  currentRow.value = { ...row }
  dialogVisible.value = true
}

// 删除处理
const handleDelete = async (row: any) => {
  if (!request?.delRequest) {
    ElMessage.warning('未配置删除接口')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      type: 'warning'
    })
    
    await request.delRequest(row)
    ElMessage.success('删除成功')
    loadData()
    emit('delete', row)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除处理
const handleBatchDelete = async () => {
  if (!request?.batchDelRequest) {
    ElMessage.warning('未配置批量删除接口')
    return
  }
  
  if (selection.value.length === 0) {
    ElMessage.warning('请选择要删除的记录')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除这 ${selection.value.length} 条记录吗？`, '提示', {
      type: 'warning'
    })
    
    await request.batchDelRequest(selection.value)
    ElMessage.success('删除成功')
    loadData()
    clearSelection()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 导出处理
const handleExport = async () => {
  if (!request?.exportRequest) {
    ElMessage.warning('未配置导出接口')
    return
  }
  
  try {
    await request.exportRequest({ search: searchForm.value })
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 刷新处理
const handleRefresh = () => {
  loadData()
}

// 表单提交处理
const handleFormSubmit = async (formData: any) => {
  const mode = dialogMode.value
  
  try {
    if (mode === 'add' && request?.addRequest) {
      await request.addRequest(formData)
      ElMessage.success('新增成功')
      emit('add', formData)
    } else if (mode === 'edit' && request?.editRequest) {
      await request.editRequest(formData)
      ElMessage.success('编辑成功')
      emit('edit', formData)
    }
    
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 表单取消处理
const handleFormCancel = () => {
  dialogVisible.value = false
  currentRow.value = {}
}

// 获取行操作按钮
const getRowActions = (row: any, index: number) => {
  const actions: CrudActionConfig[] = []
  
  // 默认操作
  if (!props.options.permission?.view) {
    actions.push({
      key: 'view',
      label: '查看',
      type: 'primary',
      onClick: () => handleView(row)
    })
  }
  
  if (!props.options.permission?.edit) {
    actions.push({
      key: 'edit',
      label: '编辑',
      type: 'primary',
      onClick: () => handleEdit(row)
    })
  }
  
  if (!props.options.permission?.remove) {
    actions.push({
      key: 'delete',
      label: '删除',
      type: 'danger',
      onClick: () => handleDelete(row),
      confirm: {
        title: '确认删除',
        content: '确定要删除这条记录吗？'
      }
    })
  }
  
  // 自定义操作
  rowActions.value.forEach(action => {
    if (typeof action.show === 'function' ? action.show(row, index) : action.show !== false) {
      actions.push({
        ...action,
        disabled: typeof action.disabled === 'function' ? action.disabled(row, index) : action.disabled,
        onClick: () => action.onClick(row, index)
      })
    }
  })
  
  return actions
}

// 表格事件处理
const onSelect = (selection: any[], row: any) => {
  // 处理选择事件
}

const onSelectAll = (selection: any[]) => {
  // 处理全选事件
}

const onSelectionChange = (selectedRows: any[]) => {
  selection.value = selectedRows
  emit('selection-change', selectedRows)
}

const onRowClick = (row: any, column: any, event: Event) => {
  emit('row-click', row, column, event)
}

const onRowDblClick = (row: any, column: any, event: Event) => {
  emit('row-dblclick', row, column, event)
}

const onSortChange = ({ column, prop, order }: any) => {
  // 处理排序变化
  loadData()
}

// 选择操作
const clearSelection = () => {
  tableRef.value?.clearSelection()
  selection.value = []
}

// 暴露的方法
const expose: CrudExpose = {
  refresh: loadData,
  reload: loadData,
  getTableData: () => tableData.value,
  setTableData: (data: any[]) => {
    tableData.value = data
  },
  openAddDialog: handleAdd,
  openEditDialog: handleEdit,
  openViewDialog: handleView,
  closeDialog: () => {
    dialogVisible.value = false
  },
  getSelectionRows: () => selection.value,
  clearSelection,
  toggleRowSelection: (row: any, selected?: boolean) => {
    tableRef.value?.toggleRowSelection(row, selected)
  },
  getSearchForm: () => searchForm.value,
  setSearchForm: (form: Record<string, any>) => {
    searchForm.value = form
  },
  resetSearchForm: () => {
    searchForm.value = {}
  },
  doSearch: handleSearch,
  setCurrentPage: (page: number) => {
    currentPage.value = page
    loadData()
  },
  setPageSize: (size: number) => {
    pageSize.value = size
    currentPage.value = 1
    loadData()
  },
  scrollTo: (position: { top?: number; left?: number }) => {
    tableRef.value?.scrollTo(position)
  },
  setCurrentRow: (row: any) => {
    tableRef.value?.setCurrentRow(row)
  },
  toggleRowExpansion: (row: any, expanded?: boolean) => {
    tableRef.value?.toggleRowExpansion(row, expanded)
  },
  doExport: handleExport,
  doImport: async (file: File) => {
    if (!request?.importRequest) {
      ElMessage.warning('未配置导入接口')
      return
    }
    await request.importRequest(file)
  }
}

defineExpose(expose)

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.crud-table {
  .crud-search {
    background: var(--el-bg-color-page);
    border-radius: 8px;
    padding: 16px;
  }

  .crud-toolbar {
    padding: 12px 0;
  }

  .selection-bar {
    border-left: 4px solid var(--el-color-primary);
  }

  .crud-table-container {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }

  .crud-pagination {
    padding: 16px 0;
  }

  &.crud-table--card {
    .crud-search,
    .crud-table-container {
      background: white;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}

// 暗色主题
html.dark {
  .crud-table {
    .crud-search {
      background: var(--el-bg-color-page);
    }

    &.crud-table--card {
      .crud-search,
      .crud-table-container {
        background: var(--el-bg-color);
        box-shadow: 0 2px 8px rgba(255, 255, 255, 0.1);
      }
    }
  }
}
</style>