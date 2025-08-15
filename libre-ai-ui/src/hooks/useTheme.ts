import { ref, computed, watchEffect } from 'vue';

export type ThemeMode = 'light' | 'dark' | 'auto';

const THEME_KEY = 'chat-theme-mode';

// 主题状态
const themeMode = ref<ThemeMode>((localStorage.getItem(THEME_KEY) as ThemeMode) || 'light');

// 系统是否为深色模式
const systemDark = ref(false);

// 当前是否为深色模式
const isDark = computed(() => {
  if (themeMode.value === 'auto') {
    return systemDark.value;
  }
  return themeMode.value === 'dark';
});

// 监听系统主题变化
if (typeof window !== 'undefined') {
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  systemDark.value = mediaQuery.matches;
  
  mediaQuery.addEventListener('change', (e) => {
    systemDark.value = e.matches;
  });
}

// 应用主题到DOM
watchEffect(() => {
  if (typeof document !== 'undefined') {
    const htmlElement = document.documentElement;
    
    if (isDark.value) {
      htmlElement.classList.add('dark');
    } else {
      htmlElement.classList.remove('dark');
    }
    
    // 保存到localStorage
    localStorage.setItem(THEME_KEY, themeMode.value);
  }
});

export function useTheme() {
  const setThemeMode = (mode: ThemeMode) => {
    themeMode.value = mode;
  };
  
  const toggleTheme = () => {
    const modes: ThemeMode[] = ['light', 'dark', 'auto'];
    const currentIndex = modes.indexOf(themeMode.value);
    const nextIndex = (currentIndex + 1) % modes.length;
    setThemeMode(modes[nextIndex]);
  };
  
  const getThemeIcon = () => {
    switch (themeMode.value) {
      case 'light':
        return 'ri:sun-line';
      case 'dark':
        return 'ri:moon-line';
      case 'auto':
        return 'ri:computer-line';
      default:
        return 'ri:sun-line';
    }
  };
  
  const getThemeText = () => {
    switch (themeMode.value) {
      case 'light':
        return '浅色模式';
      case 'dark':
        return '深色模式';
      case 'auto':
        return '跟随系统';
      default:
        return '浅色模式';
    }
  };
  
  return {
    themeMode: readonly(themeMode),
    isDark: readonly(isDark),
    systemDark: readonly(systemDark),
    setThemeMode,
    toggleTheme,
    getThemeIcon,
    getThemeText
  };
}