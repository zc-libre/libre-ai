// 表格列定义
export interface Column {
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
  hideOnMobile?: boolean
  hideOnTablet?: boolean
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

// 操作项定义
export interface ActionItem {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  icon?: any
  label?: string
  tooltip?: string
  loading?: boolean
  disabled?: boolean | (() => boolean)
  show?: boolean | (() => boolean)
  divided?: boolean
  onClick: (action?: ActionItem) => void
  permission?: string | string[]
  confirm?: {
    title?: string
    content?: string
    type?: 'warning' | 'info' | 'success' | 'error'
    confirmButtonText?: string
    cancelButtonText?: string
  }
}

// 搜索字段定义
export interface SearchField {
  key: string
  label: string
  type?: 'input' | 'select' | 'date' | 'daterange' | 'time' | 'timerange' | 'number' | 'cascader' | 'switch' | 'slider' | 'rate' | 'color'
  placeholder?: string
  width?: string
  clearable?: boolean
  required?: boolean
  defaultValue?: any
  
  // 输入框相关
  maxlength?: number
  showWordLimit?: boolean
  
  // 选择框相关
  options?: Array<{ label: string; value: any; disabled?: boolean }>
  multiple?: boolean
  filterable?: boolean
  allowCreate?: boolean
  
  // 日期相关
  format?: string
  valueFormat?: string
  rangeSeparator?: string
  startPlaceholder?: string
  endPlaceholder?: string
  
  // 数字相关
  min?: number
  max?: number
  step?: number
  precision?: number
  controls?: boolean
  
  // 级联相关
  showAllLevels?: boolean
  
  // 开关相关
  activeText?: string
  inactiveText?: string
  activeValue?: any
  inactiveValue?: any
  
  // 滑块相关
  range?: boolean
  showTooltip?: boolean
  
  // 评分相关
  allowHalf?: boolean
  showText?: boolean
  texts?: string[]
  
  // 颜色选择器相关
  showAlpha?: boolean
  colorFormat?: 'hsl' | 'hsv' | 'hex' | 'rgb'
  predefine?: string[]
  
  // 自定义组件
  component?: any
  componentProps?: object
  
  // 显示控制
  show?: boolean | ((form: any) => boolean)
  collapsed?: boolean
}

// 下拉触发器定义
export interface DropdownTrigger {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  icon?: any
  text?: string
  circle?: boolean
}

// 分页配置
export interface PaginationConfig {
  show?: boolean
  total?: number
  currentPage?: number
  pageSize?: number
  pageSizes?: number[]
  layout?: string
  background?: boolean
  small?: boolean
  hideOnSinglePage?: boolean
  showInfo?: boolean
  showJumper?: boolean
  showSizer?: boolean
  showRefresh?: boolean
}

// 搜索配置
export interface SearchConfig {
  show?: boolean
  fields?: SearchField[]
  labelWidth?: string
  inline?: boolean
  size?: 'large' | 'default' | 'small'
  showReset?: boolean
  showCollapse?: boolean
  defaultExpanded?: boolean
  defaultValues?: Record<string, any>
}

// 操作列配置
export interface ActionColumnConfig {
  show?: boolean
  title?: string
  width?: number | string
  fixed?: boolean | 'left' | 'right'
  align?: 'left' | 'center' | 'right'
  style?: 'text' | 'button' | 'circle' | 'dropdown'
  size?: 'large' | 'default' | 'small'
  actions?: ActionItem[]
}

// 表格请求参数
export interface TableRequestParams {
  page?: number
  size?: number
  sort?: string
  order?: 'asc' | 'desc'
  [key: string]: any
}

// 表格响应数据
export interface TableResponse<T = any> {
  data?: T[]
  records?: T[]
  list?: T[]
  result?: T[]
  total?: number
  pages?: number
  current?: number
  size?: number
}

// 表格事件类型
export interface TableEvents {
  'row-click': (row: any, column: any, event: Event) => void
  'row-dblclick': (row: any, column: any, event: Event) => void
  'current-change': (currentRow: any, oldCurrentRow: any) => void
  'select': (selection: any[], row: any) => void
  'select-all': (selection: any[]) => void
  'selection-change': (selection: any[]) => void
  'sort-change': (data: { column: any; prop: string; order: string }) => void
  'filter-change': (filters: any) => void
  'expand-change': (row: any, expandedRows: any[]) => void
  'page-change': (page: number) => void
  'size-change': (size: number) => void
  'search': (params: any) => void
  'reset': () => void
  'refresh': () => void
  'switch-change': (row: any, key: string, value: boolean) => void
}