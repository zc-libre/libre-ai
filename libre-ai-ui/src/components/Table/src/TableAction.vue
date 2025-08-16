<template>
  <div class="table-action" :class="[`table-action--${actionStyle}`, { 'is-loading': loading }]">
    <!-- 批量操作模式 -->
    <template v-if="mode === 'batch'">
      <el-dropdown v-if="actions.length > maxVisible" :disabled="loading" trigger="click">
        <el-button :type="primaryAction?.type || 'primary'" :size="size" :loading="loading">
          {{ primaryAction?.label || '批量操作' }}
          <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="(action, index) in actions"
              :key="index"
              :disabled="isActionDisabled(action)"
              :divided="action.divided"
              :icon="action.icon"
              @click="handleActionClick(action)"
            >
              {{ action.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      
      <template v-else>
        <el-button
          v-for="(action, index) in visibleActions"
          :key="index"
          :type="action.type || 'default'"
          :size="size"
          :loading="action.loading"
          :disabled="isActionDisabled(action)"
          @click="handleActionClick(action)"
        >
          <component :is="action.icon" v-if="action.icon" class="mr-1" />
          {{ action.label }}
        </el-button>
      </template>
    </template>
    
    <!-- 行操作模式 -->
    <template v-else>
      <!-- 下拉菜单模式 -->
      <el-dropdown
        v-if="actionStyle === 'dropdown' || (actions.length > maxVisible && actionStyle !== 'text')"
        :disabled="loading"
        trigger="click"
        @command="handleDropdownCommand"
      >
        <el-button
          :type="dropdownTrigger.type || 'default'"
          :size="size"
          :circle="dropdownTrigger.circle"
          :loading="loading"
        >
          <component :is="dropdownTrigger.icon || More" />
          <span v-if="dropdownTrigger.text">{{ dropdownTrigger.text }}</span>
          <el-icon v-if="!dropdownTrigger.circle" class="el-icon--right"><ArrowDown /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <template v-for="(action, index) in actions" :key="index">
              <el-dropdown-item
                v-if="isActionVisible(action)"
                :command="index"
                :disabled="isActionDisabled(action)"
                :divided="action.divided"
                :icon="action.icon"
              >
                {{ action.label }}
              </el-dropdown-item>
            </template>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      
      <!-- 普通按钮模式 -->
      <template v-else>
        <template v-for="(action, index) in visibleActions" :key="index">
          <el-tooltip
            :content="action.tooltip"
            :placement="tooltipPlacement"
            :disabled="!action.tooltip || actionStyle === 'button'"
          >
            <el-button
              :type="action.type || 'default'"
              :size="size"
              :link="actionStyle === 'text'"
              :circle="actionStyle === 'circle'"
              :loading="action.loading"
              :disabled="isActionDisabled(action)"
              :icon="getIconComponent(action.icon)"
              @click="handleActionClick(action)"
            >
              <template v-if="actionStyle === 'button' && action.label">
                {{ action.label }}
              </template>
            </el-button>
          </el-tooltip>
          
          <!-- 分隔符 -->
          <el-divider
            v-if="action.divided && index < visibleActions.length - 1"
            direction="vertical"
            class="action-divider"
          />
        </template>
        
        <!-- 更多操作 -->
        <el-dropdown
          v-if="hiddenActions.length > 0"
          :disabled="loading"
          trigger="click"
          @command="handleDropdownCommand"
        >
          <el-button
            :size="size"
            :link="actionStyle === 'text'"
            :circle="actionStyle === 'circle'"
            type="default"
          >
            <component :is="More" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <template v-for="(action, index) in hiddenActions" :key="index">
                <el-dropdown-item
                  v-if="isActionVisible(action)"
                  :command="getActionIndex(action)"
                  :disabled="isActionDisabled(action)"
                  :divided="action.divided"
                  :icon="action.icon"
                >
                  {{ action.label }}
                </el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import {
  ElButton,
  ElTooltip,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElDivider,
  ElIcon
} from 'element-plus'
import {
  Edit,
  Delete,
  Plus,
  CopyDocument,
  More,
  ArrowDown,
  View,
  Download,
  Share,
  Setting
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

interface ActionItem {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  icon?: any
  label?: string
  tooltip?: string
  loading?: boolean
  disabled?: boolean | (() => boolean)
  show?: boolean | (() => boolean)
  divided?: boolean
  onClick: (action?: ActionItem) => void
  // 权限相关
  permission?: string | string[]
  // 确认相关
  confirm?: {
    title?: string
    content?: string
    type?: 'warning' | 'info' | 'success' | 'error'
    confirmButtonText?: string
    cancelButtonText?: string
  }
}

interface DropdownTrigger {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default'
  icon?: any
  text?: string
  circle?: boolean
}

interface Props {
  actions: ActionItem[]
  actionStyle?: 'text' | 'button' | 'circle' | 'dropdown'
  size?: 'large' | 'default' | 'small'
  mode?: 'normal' | 'batch'
  maxVisible?: number
  loading?: boolean
  tooltipPlacement?: 'top' | 'bottom' | 'left' | 'right'
  dropdownTrigger?: DropdownTrigger
  // 权限检查函数
  hasPermission?: (permission: string | string[]) => boolean
}

const props = withDefaults(defineProps<Props>(), {
  actionStyle: 'circle',
  size: 'small',
  mode: 'normal',
  maxVisible: 3,
  loading: false,
  tooltipPlacement: 'top',
  dropdownTrigger: () => ({ icon: More, circle: true }),
  hasPermission: () => true
})

const emit = defineEmits(['action-click'])

// 计算属性
const visibleActions = computed(() => {
  const filtered = props.actions.filter(action => isActionVisible(action))
  if (props.mode === 'batch' || props.actionStyle === 'dropdown') {
    return filtered
  }
  return filtered.slice(0, props.maxVisible)
})

const hiddenActions = computed(() => {
  if (props.mode === 'batch' || props.actionStyle === 'dropdown') {
    return []
  }
  const filtered = props.actions.filter(action => isActionVisible(action))
  return filtered.slice(props.maxVisible)
})

const primaryAction = computed(() => {
  return props.actions.find(action => action.type === 'primary') || props.actions[0]
})

// 工具函数
const isActionVisible = (action: ActionItem): boolean => {
  // 权限检查
  if (action.permission && !props.hasPermission?.(action.permission)) {
    return false
  }
  
  // 显示状态检查
  if (typeof action.show === 'function') {
    return action.show()
  }
  return action.show !== false
}

const isActionDisabled = (action: ActionItem): boolean => {
  if (typeof action.disabled === 'function') {
    return action.disabled()
  }
  return action.disabled === true || props.loading
}

const getActionIndex = (action: ActionItem): number => {
  return props.actions.findIndex(a => a === action)
}

const getIconComponent = (icon: any) => {
  // 如果已经是 Element Plus 图标组件，直接返回
  if (
    typeof icon === 'function' ||
    (typeof icon === 'object' && (icon.__name || icon.name))
  ) {
    return icon
  }

  // 处理字符串图标名称，转换为对应的 Element Plus 图标
  if (typeof icon === 'string') {
    const iconMap: Record<string, any> = {
      'edit': Edit,
      'delete': Delete,
      'plus': Plus,
      'copy': CopyDocument,
      'view': View,
      'download': Download,
      'share': Share,
      'setting': Setting,
      'more': More
    }
    return iconMap[icon] || Edit
  }

  // 默认返回编辑图标
  return icon || Edit
}

// 事件处理
const handleActionClick = async (action: ActionItem) => {
  if (isActionDisabled(action)) {
    return
  }
  
  // 确认对话框
  if (action.confirm) {
    try {
      await ElMessageBox.confirm(
        action.confirm.content || `确定要执行"${action.label}"操作吗？`,
        action.confirm.title || '确认操作',
        {
          confirmButtonText: action.confirm.confirmButtonText || '确定',
          cancelButtonText: action.confirm.cancelButtonText || '取消',
          type: action.confirm.type || 'warning'
        }
      )
    } catch {
      return
    }
  }
  
  emit('action-click', action)
  action.onClick(action)
}

const handleDropdownCommand = (index: number) => {
  const action = props.actions[index]
  if (action) {
    handleActionClick(action)
  }
}
</script>

<style lang="scss" scoped>
.table-action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s ease;
  
  &.is-loading {
    opacity: 0.6;
    pointer-events: none;
  }
  
  // 不同样式的布局调整
  &.table-action--text {
    gap: 4px;
  }
  
  &.table-action--button {
    gap: 8px;
  }
  
  &.table-action--circle {
    gap: 6px;
  }
  
  &.table-action--dropdown {
    gap: 0;
  }
  
  .action-divider {
    margin: 0 4px;
    height: 16px;
  }
}

// 按钮样式增强
:deep(.el-button) {
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    background: rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    transition: width 0.3s, height 0.3s;
  }
  
  &:hover {
    box-shadow: 0 4px 12px rgb(99 102 241 / 15%);
    transform: translateY(-2px);
    
    &::before {
      width: 100%;
      height: 100%;
    }
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &.is-circle {
    display: flex;
    align-items: center;
    justify-content: center;
    
    &:hover {
      transform: scale(1.1);
    }
    
    &:active {
      transform: scale(0.95);
    }
    
    .el-icon {
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  
  &.is-link {
    padding: 4px 8px;
    
    &:hover {
      transform: none;
      box-shadow: none;
      background-color: var(--el-color-primary-light-9);
    }
  }
  
  // 不同类型按钮的特殊样式
  &.el-button--primary {
    &:hover {
      box-shadow: 0 4px 12px rgb(64 158 255 / 25%);
    }
  }
  
  &.el-button--success {
    &:hover {
      box-shadow: 0 4px 12px rgb(103 194 58 / 25%);
    }
  }
  
  &.el-button--warning {
    &:hover {
      box-shadow: 0 4px 12px rgb(230 162 60 / 25%);
    }
  }
  
  &.el-button--danger {
    &:hover {
      box-shadow: 0 4px 12px rgb(245 108 108 / 25%);
    }
  }
}

// 下拉菜单样式
:deep(.el-dropdown) {
  .el-dropdown__caret-button {
    padding-left: 8px;
    border-left: 1px solid var(--el-border-color-light);
  }
}

:deep(.el-dropdown-menu) {
  .el-dropdown-menu__item {
    transition: all 0.2s ease;
    
    &:hover {
      background-color: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
      transform: translateX(2px);
    }
    
    &.is-disabled {
      opacity: 0.5;
      cursor: not-allowed;
      
      &:hover {
        transform: none;
        background-color: transparent;
        color: var(--el-text-color-placeholder);
      }
    }
    
    .el-icon {
      margin-right: 8px;
      font-size: 14px;
    }
  }
}

// 分割线样式
:deep(.el-divider--vertical) {
  height: 1em;
  margin: 0 8px;
  border-left-color: var(--el-border-color-lighter);
}

// 响应式设计
@media (max-width: 768px) {
  .table-action {
    &.table-action--button {
      flex-direction: column;
      gap: 4px;
      
      :deep(.el-button) {
        width: 100%;
        justify-content: center;
      }
    }
    
    &.table-action--circle {
      .action-divider {
        display: none;
      }
    }
  }
}

// 暗色主题适配
html.dark {
  .table-action {
    :deep(.el-button) {
      &::before {
        background: rgba(255, 255, 255, 0.1);
      }
      
      &:hover {
        box-shadow: 0 4px 12px rgb(255 255 255 / 5%);
      }
      
      &.is-link:hover {
        background-color: var(--el-color-primary-dark-2);
      }
    }
  }
  
  :deep(.el-dropdown-menu) {
    .el-dropdown-menu__item {
      &:hover {
        background-color: var(--el-color-primary-dark-2);
      }
    }
  }
}

// 批量操作模式样式
.table-action {
  &.table-action--batch {
    .el-button {
      min-width: 80px;
    }
  }
}

// 加载状态样式
.table-action.is-loading {
  :deep(.el-button) {
    pointer-events: none;
    
    &:not(.is-loading) {
      opacity: 0.6;
    }
  }
}
</style>
