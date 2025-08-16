import { Component } from 'vue'

// 字典配置接口
export interface DictConfig {
  // 静态数据
  data?: Array<{ label: string; value: any; [key: string]: any }>
  // 动态数据获取
  url?: string
  // 数据转换函数
  transform?: (data: any) => Array<{ label: string; value: any }>
  // 缓存配置
  cache?: boolean
  // 标签字段名
  label?: string
  // 值字段名  
  value?: string
  // 是否多选
  multiple?: boolean
}

// 表单配置基础接口
export interface BaseFormItemConfig {
  component?: Component | string
  props?: Record<string, any>
  placeholder?: string
  span?: number
  order?: number
  dict?: DictConfig
  disabled?: boolean | ((form: any) => boolean)
  show?: boolean | ((form: any) => boolean)
  type?: string
}

// 新增表单配置
export interface AddFormItemConfig extends BaseFormItemConfig {
  defaultValue?: any
  rules?: any[]
}

// 编辑表单配置
export interface EditFormItemConfig extends BaseFormItemConfig {
  rules?: any[]
}

// 查看表单配置
export interface ViewFormItemConfig {
  component?: Component | string
  props?: Record<string, any>
  formatter?: (value: any, form: any) => string
  span?: number
  order?: number
}

// 搜索表单配置
export interface SearchFormItemConfig {
  component?: Component | string
  props?: Record<string, any>
  defaultValue?: any
  placeholder?: string
  span?: number
  order?: number
  dict?: DictConfig
  type?: string
}

// 表格显示配置
export interface TableColumnConfig {
  render?: (value: any, row: any, index: number) => any
  component?: Component | string
  props?: Record<string, any>
  formatter?: (value: any, row: any) => string
  showOverflowTooltip?: boolean
  type?: 'tag' | 'switch' | 'image' | 'progress' | 'link' | 'date' | 'number' | 'currency' | 'percent' | 'enum'
  tagOptions?: Record<string, { label: string; type: string }>
  tagSize?: 'large' | 'default' | 'small'
  imageWidth?: string
  imageHeight?: string
  progressType?: 'line' | 'circle' | 'dashboard'
  strokeWidth?: number
  showText?: boolean
  linkType?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  target?: string
  dateFormat?: string
  numberFormat?: object
  currency?: string
  currencyFormat?: object
  percentDecimals?: number
  enumOptions?: Record<string, string>
}

// 列配置接口
export interface CrudColumnConfig {
  // 基础属性
  key: string
  title: string
  width?: number | string
  minWidth?: number | string
  fixed?: boolean | 'left' | 'right'
  align?: 'left' | 'center' | 'right'
  sortable?: boolean
  resizable?: boolean
  required?: boolean
  className?: string
  
  // 显示控制
  show?: boolean | ((context: any) => boolean)
  hideInTable?: boolean
  hideInSearch?: boolean
  hideInForm?: boolean
  hideInAdd?: boolean
  hideInEdit?: boolean
  hideInView?: boolean
  
  // 表格显示配置
  table?: TableColumnConfig
  
  // 搜索表单配置
  search?: SearchFormItemConfig
  
  // 新增表单配置
  add?: AddFormItemConfig
  
  // 编辑表单配置
  edit?: EditFormItemConfig
  
  // 查看表单配置
  view?: ViewFormItemConfig
  
  // 字典配置
  dict?: DictConfig
  
  // 表单验证规则
  rules?: any[]
  
  // 帮助信息
  helper?: string
  
  // 自定义渲染
  render?: (value: any, row: any, index: number) => any
  
  // 插槽名称
  slot?: string
}

// 操作按钮配置
export interface CrudActionConfig {
  key: string
  label: string
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  icon?: any
  size?: 'large' | 'default' | 'small'
  disabled?: boolean | ((row: any, index: number) => boolean)
  show?: boolean | ((row: any, index: number) => boolean)
  loading?: boolean
  tooltip?: string
  divided?: boolean
  onClick: (row: any, index: number) => void | Promise<void>
  confirm?: {
    title?: string
    content?: string
    type?: 'warning' | 'info' | 'success' | 'error'
    confirmText?: string
    cancelText?: string
  }
  permission?: string | string[]
}

// 请求配置接口
export interface CrudRequestConfig {
  // 分页查询
  pageRequest?: (params: {
    page: number
    size: number
    search?: Record<string, any>
    sort?: { field: string; order: 'asc' | 'desc' }
  }) => Promise<{
    data: any[]
    total: number
    current?: number
    size?: number
  }>
  
  // 新增
  addRequest?: (row: any) => Promise<any>
  
  // 编辑
  editRequest?: (row: any) => Promise<any>
  
  // 删除
  delRequest?: (row: any) => Promise<any>
  
  // 批量删除
  batchDelRequest?: (rows: any[]) => Promise<any>
  
  // 导出
  exportRequest?: (params: any) => Promise<any>
  
  // 导入
  importRequest?: (file: File) => Promise<any>
}

// 表单配置
export interface CrudFormConfig {
  // 表单布局
  labelWidth?: string
  labelPosition?: 'left' | 'right' | 'top'
  inline?: boolean
  size?: 'large' | 'default' | 'small'
  
  // 表单列数
  columns?: number
  
  // 表单分组
  groups?: Array<{
    title: string
    columns: string[]
    collapsed?: boolean
  }>
  
  // 表单校验
  rules?: Record<string, any[]>
  
  // 表单操作按钮
  submit?: {
    text?: string
    loading?: boolean
    disabled?: boolean
  }
  
  reset?: {
    text?: string
    show?: boolean
  }
  
  cancel?: {
    text?: string
    show?: boolean
  }
}

// 对话框配置
export interface CrudDialogConfig {
  title?: string
  width?: string | number
  fullscreen?: boolean
  draggable?: boolean
  closeOnClickModal?: boolean
  closeOnPressEscape?: boolean
  showClose?: boolean
  appendToBody?: boolean
  destroyOnClose?: boolean
}

// 工具栏配置
export interface CrudToolbarConfig {
  show?: boolean
  buttons?: Array<{
    key: string
    label: string
    type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
    icon?: any
    show?: boolean | (() => boolean)
    disabled?: boolean | (() => boolean)
    onClick: () => void | Promise<void>
    permission?: string | string[]
  }>
  
  // 内置按钮
  add?: boolean | CrudActionConfig
  batchDelete?: boolean
  refresh?: boolean
  export?: boolean
  import?: boolean
  columns?: boolean
  fullscreen?: boolean
}

// 分页配置
export interface CrudPaginationConfig {
  show?: boolean
  pageSize?: number
  pageSizes?: number[]
  layout?: string
  background?: boolean
  small?: boolean
  hideOnSinglePage?: boolean
  showTotal?: boolean
  showJumper?: boolean
  showSizer?: boolean
}

// 主要的 CRUD 配置接口
export interface CrudOptions {
  // 列配置
  columns: Record<string, CrudColumnConfig>
  
  // 请求配置
  request?: CrudRequestConfig
  
  // 行操作配置
  rowActions?: CrudActionConfig[]
  
  // 工具栏配置
  toolbar?: CrudToolbarConfig
  
  // 搜索配置
  search?: {
    show?: boolean
    labelWidth?: string
    inline?: boolean
    collapsed?: boolean
    resetToDefaultValue?: boolean
    autoSearch?: boolean
    showReset?: boolean
    showCollapse?: boolean
  }
  
  // 表单配置
  form?: {
    add?: CrudFormConfig & { dialog?: CrudDialogConfig }
    edit?: CrudFormConfig & { dialog?: CrudDialogConfig }
    view?: CrudFormConfig & { dialog?: CrudDialogConfig }
  }
  
  // 分页配置
  pagination?: CrudPaginationConfig
  
  // 表格配置
  table?: {
    stripe?: boolean
    border?: boolean
    size?: 'large' | 'default' | 'small'
    showHeader?: boolean
    highlightCurrentRow?: boolean
    rowKey?: string | ((row: any) => string)
    height?: number | string
    maxHeight?: number | string
    loading?: boolean
    emptyText?: string
    defaultSort?: { prop: string; order: 'ascending' | 'descending' }
    showIndex?: boolean
    
    // 选择配置
    selection?: {
      show?: boolean
      selectable?: (row: any, index: number) => boolean
      reserveSelection?: boolean
    }
    
    // 展开配置
    expand?: {
      show?: boolean
      expandRowKeys?: any[]
      defaultExpandAll?: boolean
    }
    
    // 树形数据配置
    tree?: {
      children?: string
      hasChildren?: string
      lazy?: boolean
      load?: (row: any, treeNode: any, resolve: Function) => void
    }
  }
  
  // 模式配置
  mode?: {
    name?: 'local' | 'remote'
    isMergeWhenUpdate?: boolean
    isAppendWhenAdd?: boolean
  }
  
  // 权限配置
  permission?: {
    add?: string | string[]
    edit?: string | string[]
    view?: string | string[]
    remove?: string | string[]
    export?: string | string[]
    import?: string | string[]
  }
  
  // 事件回调
  events?: {
    onLoad?: (data: any[]) => void
    onAdd?: (row: any) => void
    onEdit?: (row: any) => void
    onRemove?: (row: any) => void
    onSelect?: (selection: any[]) => void
    onSortChange?: (sort: { prop: string; order: string }) => void
    onFilterChange?: (filters: any) => void
  }
  
  // 自定义配置
  settings?: {
    // 列设置
    columnSettings?: boolean
    // 密度设置
    densitySettings?: boolean
    // 全屏设置
    fullscreenSettings?: boolean
  }
}

// CRUD 组件暴露的方法
export interface CrudExpose {
  // 数据操作
  refresh: () => Promise<void>
  reload: () => Promise<void>
  getTableData: () => any[]
  setTableData: (data: any[]) => void
  
  // 表单操作
  openAddDialog: () => void
  openEditDialog: (row: any) => void
  openViewDialog: (row: any) => void
  closeDialog: () => void
  
  // 选择操作
  getSelectionRows: () => any[]
  clearSelection: () => void
  toggleRowSelection: (row: any, selected?: boolean) => void
  
  // 搜索操作
  getSearchForm: () => Record<string, any>
  setSearchForm: (form: Record<string, any>) => void
  resetSearchForm: () => void
  doSearch: () => void
  
  // 分页操作
  setCurrentPage: (page: number) => void
  setPageSize: (size: number) => void
  
  // 表格操作
  scrollTo: (position: { top?: number; left?: number }) => void
  setCurrentRow: (row: any) => void
  toggleRowExpansion: (row: any, expanded?: boolean) => void
  
  // 导入导出
  doExport: (params?: any) => Promise<void>
  doImport: (file: File) => Promise<void>
}

// 上下文类型
export interface CrudContext {
  mode: 'add' | 'edit' | 'view'
  row?: any
  index?: number
  form: Record<string, any>
  isValid: boolean
}

// 组件配置类型
export interface CrudComponentConfig {
  component: Component | string
  props?: Record<string, any>
  events?: Record<string, Function>
  slots?: Record<string, any>
}