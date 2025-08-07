import { computed } from 'vue';

export function useRootSetting() {
  const getDarkMode = computed(() => {
    // 简单实现，返回light模式
    return 'light';
  });

  return {
    getDarkMode
  };
}
