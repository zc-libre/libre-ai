// Shared options/constants for dashboard generator

// 主题选项（供 Step3ThemeSelector 使用）
export const themeOptions = [
  {
    id: 'modern-blue',
    name: '现代蓝',
    description: '专业稳重的蓝色主题',
    colors: {
      primary: '#409EFF',
      secondary: '#79BBFF',
      accent: '#A0CFFF'
    }
  },
  {
    id: 'dark-purple',
    name: '深紫夜',
    description: '神秘优雅的紫色主题',
    colors: {
      primary: '#8B5CF6',
      secondary: '#A78BFA',
      accent: '#C4B5FD'
    }
  },
  {
    id: 'green-nature',
    name: '自然绿',
    description: '清新自然的绿色主题',
    colors: {
      primary: '#67C23A',
      secondary: '#85CE61',
      accent: '#B3E19D'
    }
  },
  {
    id: 'orange-warm',
    name: '暖橙色',
    description: '温暖活力的橙色主题',
    colors: {
      primary: '#E6A23C',
      secondary: '#EEBE77',
      accent: '#F3D19E'
    }
  },
  {
    id: 'red-energy',
    name: '活力红',
    description: '充满活力的红色主题',
    colors: {
      primary: '#F56C6C',
      secondary: '#F78989',
      accent: '#FAB6B6'
    }
  },
  {
    id: 'cyan-fresh',
    name: '清新青',
    description: '清新淡雅的青色主题',
    colors: {
      primary: '#17A2B8',
      secondary: '#46B5D1',
      accent: '#7CC7D8'
    }
  },
  {
    id: 'indigo-deep',
    name: '深邃靛',
    description: '深邃沉稳的靛色主题',
    colors: {
      primary: '#6366F1',
      secondary: '#818CF8',
      accent: '#A5B4FC'
    }
  },
  {
    id: 'pink-soft',
    name: '柔和粉',
    description: '温柔浪漫的粉色主题',
    colors: {
      primary: '#EC4899',
      secondary: '#F472B6',
      accent: '#FBCFE8'
    }
  },
  {
    id: 'teal-calm',
    name: '静谧蓝绿',
    description: '平静舒适的蓝绿主题',
    colors: {
      primary: '#14B8A6',
      secondary: '#5EEAD4',
      accent: '#99F6E4'
    }
  },
  {
    id: 'amber-golden',
    name: '金色光辉',
    description: '高贵典雅的金色主题',
    colors: {
      primary: '#F59E0B',
      secondary: '#FBBF24',
      accent: '#FCD34D'
    }
  },
  {
    id: 'slate-professional',
    name: '专业灰',
    description: '低调专业的灰色主题',
    colors: {
      primary: '#64748B',
      secondary: '#94A3B8',
      accent: '#CBD5E1'
    }
  },
  {
    id: 'custom',
    name: '自定义',
    description: '创建您的专属配色',
    colors: {
      primary: '#6366F1',
      secondary: '#818CF8',
      accent: '#A5B4FC'
    },
    isCustom: true
  }
];

// 示例数据模板（供 Step2ComponentSelector 使用）
export const sampleDataTemplates: Record<string, any> = {
  'bar-chart': [
    { category: '产品A', value: 100 },
    { category: '产品B', value: 150 },
    { category: '产品C', value: 80 }
  ],
  'line-chart': [
    { date: '2024-01', value: 100 },
    { date: '2024-02', value: 120 },
    { date: '2024-03', value: 90 }
  ],
  'pie-chart': [
    { name: '分类A', value: 35 },
    { name: '分类B', value: 25 },
    { name: '分类C', value: 40 }
  ],
  'kpi-card': {
    value: 1250,
    comparison: 15.6,
    trend: 'up'
  },
  'data-table': [
    { id: 1, name: '项目A', status: '进行中', progress: 75 },
    { id: 2, name: '项目B', status: '已完成', progress: 100 },
    { id: 3, name: '项目C', status: '待开始', progress: 0 }
  ]
};
