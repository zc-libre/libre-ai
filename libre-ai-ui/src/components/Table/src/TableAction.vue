<template>
  <div class="table-action">
    <el-button
      v-for="(action, index) in actions"
      :key="index"
      :type="action.type || 'default'"
      :size="size"
      :link="actionStyle === 'text'"
      @click="action.onClick"
    >
      <Icon v-if="action.icon" :icon="getIconName(action.icon)" />
    </el-button>
  </div>
</template>

<script lang="ts" setup>
import { ElButton } from 'element-plus';
import { Icon } from '@iconify/vue';

interface ActionItem {
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'default';
  icon?: any;
  onClick: () => void;
}

interface Props {
  actions: ActionItem[];
  actionStyle?: 'text' | 'button';
  size?: 'large' | 'default' | 'small';
}

withDefaults(defineProps<Props>(), {
  actionStyle: 'button',
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
</script>

<style lang="scss" scoped>
.table-action {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
