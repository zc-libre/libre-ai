<template>
  <div class="crud-actions">
    <template v-for="(action, index) in visibleActions" :key="action.key">
      <el-popconfirm
        v-if="action.confirm"
        :title="action.confirm.title || '确认操作'"
        :confirm-button-text="action.confirm.confirmText || '确认'"
        :cancel-button-text="action.confirm.cancelText || '取消'"
        :icon="InfoFilled"
        :icon-color="getConfirmIconColor(action.confirm.type)"
        @confirm="handleAction(action)"
      >
        <template #reference>
          <el-button
            :type="action.type"
            :size="size"
            :disabled="getActionDisabled(action)"
            :loading="action.loading"
            :icon="action.icon"
            :circle="style === 'circle'"
            :text="style === 'text'"
            :link="style === 'text'"
          >
            <span v-if="style !== 'circle'">{{ action.label }}</span>
          </el-button>
        </template>
      </el-popconfirm>
      
      <el-tooltip
        v-else-if="action.tooltip"
        :content="action.tooltip"
        placement="top"
      >
        <el-button
          :type="action.type"
          :size="size"
          :disabled="getActionDisabled(action)"
          :loading="action.loading"
          :icon="action.icon"
          :circle="style === 'circle'"
          :text="style === 'text'"
          :link="style === 'text'"
          @click="handleAction(action)"
        >
          <span v-if="style !== 'circle'">{{ action.label }}</span>
        </el-button>
      </el-tooltip>
      
      <el-button
        v-else
        :type="action.type"
        :size="size"
        :disabled="getActionDisabled(action)"
        :loading="action.loading"
        :icon="action.icon"
        :circle="style === 'circle'"
        :text="style === 'text'"
        :link="style === 'text'"
        @click="handleAction(action)"
      >
        <span v-if="style !== 'circle'">{{ action.label }}</span>
      </el-button>
      
      <!-- 分割线 -->
      <el-divider
        v-if="action.divided && index < visibleActions.length - 1"
        direction="vertical"
      />
    </template>
    
    <!-- 更多操作下拉菜单 -->
    <el-dropdown
      v-if="moreActions.length > 0"
      trigger="click"
      @command="handleMoreAction"
    >
      <el-button
        :size="size"
        :icon="MoreFilled"
        :circle="style === 'circle'"
        :text="style === 'text'"
        :link="style === 'text'"
      >
        <span v-if="style !== 'circle'">更多</span>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <template v-for="action in moreActions" :key="action.key">
            <el-dropdown-item
              :command="action"
              :disabled="getActionDisabled(action)"
              :divided="action.divided"
              :icon="action.icon"
            >
              {{ action.label }}
            </el-dropdown-item>
          </template>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { ElButton, ElPopconfirm, ElTooltip, ElDropdown, ElDropdownMenu, ElDropdownItem, ElDivider } from 'element-plus'
import { InfoFilled, MoreFilled } from '@element-plus/icons-vue'
import type { CrudActionConfig } from './crud-types'

interface Props {
  actions: CrudActionConfig[]
  row?: any
  index?: number
  size?: 'large' | 'default' | 'small'
  style?: 'text' | 'button' | 'circle'
  maxCount?: number
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  style: 'text',
  maxCount: 3
})

// 可见的操作（直接显示的）
const visibleActions = computed(() => {
  return props.actions.slice(0, props.maxCount)
})

// 更多操作（下拉显示的）
const moreActions = computed(() => {
  return props.actions.slice(props.maxCount)
})

// 计算操作按钮的禁用状态
const getActionDisabled = (action: CrudActionConfig) => {
  if (typeof action.disabled === 'function') {
    return action.disabled(props.row, props.index || 0)
  }
  return action.disabled || false
}

// 处理操作点击
const handleAction = async (action: CrudActionConfig) => {
  const isDisabled = typeof action.disabled === 'function' 
    ? action.disabled(props.row, props.index || 0) 
    : action.disabled
    
  if (isDisabled || action.loading) return
  
  try {
    await action.onClick(props.row, props.index || 0)
  } catch (error) {
    console.error('操作执行失败:', error)
  }
}

// 处理更多操作点击
const handleMoreAction = (action: CrudActionConfig) => {
  handleAction(action)
}

// 获取确认框图标颜色
const getConfirmIconColor = (type?: 'warning' | 'info' | 'success' | 'error') => {
  const colorMap = {
    warning: '#E6A23C',
    error: '#F56C6C',
    success: '#67C23A',
    info: '#909399'
  }
  return colorMap[type || 'warning']
}
</script>

<style lang="scss" scoped>
.crud-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
  
  .el-button {
    &.is-circle {
      margin: 0 2px;
    }
    
    &.is-text {
      padding: 4px 8px;
      margin: 0 2px;
    }
  }
  
  .el-divider--vertical {
    margin: 0 4px;
    height: 1em;
  }
}

// 紧凑模式
.crud-actions--compact {
  gap: 2px;
  
  .el-button {
    padding: 2px 6px;
    
    &.is-circle {
      padding: 4px;
    }
  }
}

// 移动端适配
@media (max-width: 768px) {
  .crud-actions {
    .el-button {
      font-size: 12px;
      padding: 2px 6px;
      
      &.is-circle {
        width: 24px;
        height: 24px;
        padding: 0;
      }
    }
  }
}
</style>