export interface CustomThemeColors {
  primary: string;
  secondary: string;
  accent: string;
  background?: string;
  surface?: string;
  text?: string;
}

export interface ThemeOption {
  id: string;
  name: string;
  description: string;
  colors: CustomThemeColors;
  isCustom?: boolean;
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
    },
    {
      id: 'teal-calm',
      name: '静谧蓝绿',
      description: '平静舒适的蓝绿主题',
      colors: {
        primary: '#14B8A6',
        secondary: '#5EEAD4',
        accent: '#99F6E4',
        background: '#f0fdfa',
        surface: '#ffffff',
        text: '#303133'
      }
    },
    {
      id: 'amber-golden',
      name: '金色光辉',
      description: '高贵典雅的金色主题',
      colors: {
        primary: '#F59E0B',
        secondary: '#FBBF24',
        accent: '#FCD34D',
        background: '#fffbeb',
        surface: '#ffffff',
        text: '#303133'
      }
    },
    {
      id: 'custom',
      name: '自定义',
      description: '创建您的专属配色',
      colors: {
        primary: '#6366F1',
        secondary: '#818CF8',
        accent: '#A5B4FC',
        background: '#f5f5f5',
        surface: '#ffffff',
        text: '#303133'
      },
      isCustom: true
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
  const getThemeColors = (themeId: string, customColors?: any) => {
    if (themeId === 'custom' && customColors) {
      return {
        primary: customColors.primary,
        secondary: customColors.secondary,
        accent: customColors.accent,
        background: customColors.background || '#f5f5f5',
        surface: customColors.surface || '#ffffff',
        text: customColors.text || '#303133'
      };
    }
    const theme = getThemeById(themeId);
    return theme?.colors || {};
  };

  // 应用主题到CSS变量
  const applyTheme = (themeId: string, customColors?: any) => {
    const colors = getThemeColors(themeId, customColors);
    if (!colors) return;

    const root = document.documentElement;
    Object.entries(colors).forEach(([key, value]) => {
      root.style.setProperty(`--theme-${key}`, value as string);
    });
  };

  // 生成主题CSS
  const generateThemeCSS = (themeId: string, customColors?: any): string => {
    const colors = getThemeColors(themeId, customColors);
    if (!colors) return '';

    const cssVars = Object.entries(colors)
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
  const isDarkTheme = (themeId: string, customColors?: any): boolean => {
    if (themeId === 'custom' && customColors) {
      // 根据背景色判断是否为深色主题
      const bgColor = customColors.background || '#f5f5f5';
      const hex = bgColor.replace('#', '');
      const r = parseInt(hex.substr(0, 2), 16);
      const g = parseInt(hex.substr(2, 2), 16);
      const b = parseInt(hex.substr(4, 2), 16);
      const brightness = (r * 299 + g * 587 + b * 114) / 1000;
      return brightness < 128;
    }
    const darkThemes = ['dark-purple'];
    return darkThemes.includes(themeId);
  };

  // 创建自定义主题
  const createCustomTheme = (colors: CustomThemeColors): ThemeOption => {
    return {
      id: 'custom',
      name: '自定义',
      description: '用户自定义配色方案',
      colors: {
        primary: colors.primary,
        secondary: colors.secondary,
        accent: colors.accent,
        background: colors.background || '#f5f5f5',
        surface: colors.surface || '#ffffff',
        text: colors.text || '#303133'
      },
      isCustom: true
    };
  };

  // 更新自定义主题（保留现有主题结构）
  const updateCustomTheme = (
    theme: ThemeOption,
    colors: CustomThemeColors
  ): ThemeOption => {
    return {
      ...theme,
      colors: {
        primary: colors.primary,
        secondary: colors.secondary,
        accent: colors.accent,
        background: colors.background || theme.colors.background || '#f5f5f5',
        surface: colors.surface || theme.colors.surface || '#ffffff',
        text: colors.text || theme.colors.text || '#303133'
      }
    };
  };

  // 获取默认自定义颜色
  const getDefaultCustomColors = (): CustomThemeColors => {
    return {
      primary: '#6366F1',
      secondary: '#818CF8',
      accent: '#A5B4FC',
      background: '#f5f5f5',
      surface: '#ffffff',
      text: '#303133'
    };
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
    isDarkTheme,
    createCustomTheme,
    updateCustomTheme,
    getDefaultCustomColors
  };
};
