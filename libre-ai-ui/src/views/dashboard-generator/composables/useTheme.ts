export interface ThemeOption {
  id: string;
  name: string;
  description: string;
  colors: {
    primary: string;
    secondary: string;
    accent: string;
    background?: string;
    surface?: string;
    text?: string;
  };
}

export const useTheme = () => {
  // 主题选项
  const themeOptions: ThemeOption[] = [
    {
      id: 'modern-blue',
      name: '现代蓝',
      description: '专业稳重的蓝色主题',
      colors: {
        primary: '#409EFF',
        secondary: '#79BBFF',
        accent: '#A0CFFF',
        background: '#f5f7fa',
        surface: '#ffffff',
        text: '#303133'
      }
    },
    {
      id: 'dark-purple',
      name: '深紫夜',
      description: '神秘优雅的紫色主题',
      colors: {
        primary: '#8B5CF6',
        secondary: '#A78BFA',
        accent: '#C4B5FD',
        background: '#1a1a2e',
        surface: '#16213e',
        text: '#ffffff'
      }
    },
    {
      id: 'green-nature',
      name: '自然绿',
      description: '清新自然的绿色主题',
      colors: {
        primary: '#67C23A',
        secondary: '#85CE61',
        accent: '#B3E19D',
        background: '#f0f9f0',
        surface: '#ffffff',
        text: '#303133'
      }
    },
    {
      id: 'orange-warm',
      name: '暖橙色',
      description: '温暖活力的橙色主题',
      colors: {
        primary: '#E6A23C',
        secondary: '#EEBE77',
        accent: '#F3D19E',
        background: '#fdf6ec',
        surface: '#ffffff',
        text: '#303133'
      }
    },
    {
      id: 'red-energy',
      name: '活力红',
      description: '充满活力的红色主题',
      colors: {
        primary: '#F56C6C',
        secondary: '#F78989',
        accent: '#FAB6B6',
        background: '#fef0f0',
        surface: '#ffffff',
        text: '#303133'
      }
    },
    {
      id: 'cyan-fresh',
      name: '清新青',
      description: '清新淡雅的青色主题',
      colors: {
        primary: '#17A2B8',
        secondary: '#46B5D1',
        accent: '#7CC7D8',
        background: '#e8f8fc',
        surface: '#ffffff',
        text: '#303133'
      }
    },
    {
      id: 'indigo-deep',
      name: '深邃靛',
      description: '深邃沉稳的靛色主题',
      colors: {
        primary: '#6366F1',
        secondary: '#818CF8',
        accent: '#A5B4FC',
        background: '#f0f0ff',
        surface: '#ffffff',
        text: '#303133'
      }
    },
    {
      id: 'pink-soft',
      name: '柔和粉',
      description: '温柔浪漫的粉色主题',
      colors: {
        primary: '#EC4899',
        secondary: '#F472B6',
        accent: '#FBCFE8',
        background: '#fdf2f8',
        surface: '#ffffff',
        text: '#303133'
      }
    }
  ];

  // 获取主题选项
  const getThemeOptions = () => themeOptions;

  // 根据ID获取主题
  const getThemeById = (themeId: string): ThemeOption | undefined => {
    return themeOptions.find(theme => theme.id === themeId);
  };

  // 获取主题名称
  const getThemeName = (themeId: string): string => {
    const theme = getThemeById(themeId);
    return theme?.name || themeId;
  };

  // 获取主题颜色
  const getThemeColors = (themeId: string) => {
    const theme = getThemeById(themeId);
    return theme?.colors || {};
  };

  // 应用主题到CSS变量
  const applyTheme = (themeId: string) => {
    const theme = getThemeById(themeId);
    if (!theme) return;

    const root = document.documentElement;
    Object.entries(theme.colors).forEach(([key, value]) => {
      root.style.setProperty(`--theme-${key}`, value);
    });
  };

  // 生成主题CSS
  const generateThemeCSS = (themeId: string): string => {
    const theme = getThemeById(themeId);
    if (!theme) return '';

    const cssVars = Object.entries(theme.colors)
      .map(([key, value]) => `  --theme-${key}: ${value};`)
      .join('\n');

    return `:root {\n${cssVars}\n}`;
  };

  // 获取对比色（用于文本）
  const getContrastColor = (backgroundColor: string): string => {
    // 简单的对比度计算
    const hex = backgroundColor.replace('#', '');
    const r = parseInt(hex.substr(0, 2), 16);
    const g = parseInt(hex.substr(2, 2), 16);
    const b = parseInt(hex.substr(4, 2), 16);
    const brightness = (r * 299 + g * 587 + b * 114) / 1000;
    return brightness > 128 ? '#000000' : '#ffffff';
  };

  // 检查是否为深色主题
  const isDarkTheme = (themeId: string): boolean => {
    const darkThemes = ['dark-purple'];
    return darkThemes.includes(themeId);
  };

  return {
    themeOptions,
    getThemeOptions,
    getThemeById,
    getThemeName,
    getThemeColors,
    applyTheme,
    generateThemeCSS,
    getContrastColor,
    isDarkTheme
  };
};
