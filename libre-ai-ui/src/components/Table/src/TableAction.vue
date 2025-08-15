<template>
  <div class="table-action flex items-center justify-center gap-2">
    <el-tooltip
      v-for="(action, index) in actions"
      :key="index"
      :content="action.tooltip"
      placement="top"
      :disabled="!action.tooltip"
    >
      <el-button
        :type="action.type || 'default'"
        :size="size"
        :link="actionStyle === 'text'"
        :circle="actionStyle === 'circle'"
        :icon="getIconComponent(action.icon)"
        @click="action.onClick"
      />
    </el-tooltip>
  </div>
</template>

<script lang="ts" setup>
import { ElButton, ElTooltip } from 'element-plus';
import { Icon } from '@iconify/vue';
import { Edit, Delete, Plus, CopyDocument } from '@element-plus/icons-vue';

interface ActionItem {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default';
  icon?: any;
  tooltip?: string;
  onClick: () => void;
}

interface Props {
  actions: ActionItem[];
  actionStyle?: 'text' | 'button' | 'circle';
  size?: 'large' | 'default' | 'small';
}

withDefaults(defineProps<Props>(), {
  actionStyle: 'circle',
  size: 'small'
});

const getIconName = (icon: any) => {
  // 处理从 @vicons/antd 导入的图标
  if (typeof icon === 'object' && icon.name) {
    const iconMap: Record<string, string> = {
      EditOutlined: 'ep:edit',
      DeleteOutlined: 'ep:delete',
      PlusOutlined: 'ep:plus'
    };
    return iconMap[icon.name] || 'ep:operation';
  }
  return icon;
};

const getIconComponent = (icon: any) => {
  // 如果已经是 Element Plus 图标组件，直接返回
  if (
    typeof icon === 'function' ||
    (typeof icon === 'object' && (icon.__name || icon.name))
  ) {
    return icon;
  }

  // 处理字符串图标名称，转换为对应的 Element Plus 图标
  if (typeof icon === 'string') {
    const iconMap: Record<string, any> = {
      'ep:edit': Edit,
      'ep:delete': Delete,
      'ep:plus': Plus,
      'ep:copy-document': CopyDocument
    };
    return iconMap[icon] || Edit;
  }

  // 默认返回编辑图标
  return icon || Edit;
};
</script>

<style lang="scss" scoped>
/* 与 embed-store 完全一致的容器样式 */
.table-action {
  /* 使用与 embed-store 相同的 Tailwind 类效果 */
  display: flex;
  gap: 8px; /* 对应 gap-2 */
  align-items: center;
  justify-content: center;
}

/* 与 embed-store 保持一致的按钮样式 */
:deep(.el-button) {
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgb(99 102 241 / 15%);
    transform: translateY(-2px);
  }

  &.is-circle {
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      transform: scale(1.1);
    }

    /* 确保图标在圆形按钮中居中 */
    .el-icon {
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}
</style>
