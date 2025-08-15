<script setup lang="ts">
import { computed } from 'vue';
import { ElAvatar } from 'element-plus';
import { Icon } from '@iconify/vue';
import { useUserStore } from '@/store';

const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo);

// 生成用户名首字母
const avatarText = computed(() => {
  const name = userInfo.value?.nickname || userInfo.value?.username || '';
  if (!name) return '';

  // 如果是中文，取第一个字
  if (/[\u4e00-\u9fa5]/.test(name)) {
    return name.charAt(0);
  }

  // 如果是英文，取首字母大写
  return name.charAt(0).toUpperCase();
});

// 根据用户名生成背景色
const avatarColor = computed(() => {
  const name = userInfo.value?.username || '';
  if (!name) return '#1890ff';

  const colors = [
    '#f56a00',
    '#7265e6',
    '#ffbf00',
    '#00a2ae',
    '#1890ff',
    '#52c41a',
    '#fa541c',
    '#13c2c2',
    '#2f54eb',
    '#722ed1'
  ];

  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }

  return colors[Math.abs(hash) % colors.length];
});
</script>

<template>
  <div class="user-avatar">
    <el-avatar
      v-if="userInfo?.avatar"
      :src="userInfo.avatar"
      :size="32"
      fit="cover"
    >
      <template #error>
        <div class="user-avatar__fallback">
          <Icon icon="ri:user-3-line" />
        </div>
      </template>
    </el-avatar>

    <el-avatar v-else :size="32" :style="{ backgroundColor: avatarColor }">
      <span v-if="avatarText" class="user-avatar__text">
        {{ avatarText }}
      </span>
      <Icon v-else icon="ri:user-3-line" />
    </el-avatar>
  </div>
</template>

<style lang="scss" scoped>
.user-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  &__fallback {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    background: var(--el-fill-color);
    color: var(--el-text-color-regular);
    font-size: 18px;
  }

  &__text {
    color: #fff;
    font-weight: 500;
    font-size: 14px;
  }
}

:deep(.el-avatar) {
  flex-shrink: 0;
}
</style>
