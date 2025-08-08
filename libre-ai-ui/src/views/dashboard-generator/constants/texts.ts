// AI看板生成器 - 中文文本常量
// 基于原始index.html中的i18nData['zh-CN']内容提取和改造

import type { AppTexts } from '../types';

// 应用主体文本 - 从原始UI提示词生成器改造为看板生成器
export const APP_TEXTS: AppTexts = {
  app: {
    title: 'AI 看板生成器 Pro',
    subtitle: '智能看板设计，轻松创建专业数据可视化界面',
    themeToggleTooltip: '切换主题',
    settingsTooltip: '设置',
    helpTooltip: '帮助/教程',
    footerText: 'AI 看板生成器 Pro - 让数据可视化更简单'
  },

  wizard: {
    // 第一步：看板用途选择（替换原始的"设计目的"）
    step1: {
      title: '看板用途',
      description:
        '首先选择您的看板使用场景和目的，这将帮助我们为您生成更符合需求的专业看板界面。',
      purposeQuestion: '您的看板用于什么场景？',
      purposes: {
        shelf: {
          title: '货架监控看板',
          description: '货架状态、库存分布、拣选效率'
        },
        location: {
          title: '仓位监控看板',
          description: '仓位利用率、占用状态、物料分布'
        },
        transport: {
          title: '搬运任务监控',
          description: 'AGV任务、路径规划、设备状态'
        },
        mixed: {
          title: '混合监控看板',
          description: '全局监控、多维分析、综合指标'
        },
        custom: {
          title: '自定义用途',
          description: '自定义配置、灵活定制、个性化'
        }
      }
    },

    // 第二步：样式和布局选择（替换原始的"风格选择"）
    step2: {
      title: '样式布局',
      description:
        '选择您喜欢的看板布局样式，我们将据此生成更符合您需求的专业界面。',
      layoutQuestion: '选择看板布局样式',
      layouts: {
        gridLayout: {
          title: '网格布局',
          description: '经典的网格排列，适合展示多个数据面板'
        },
        sidebarLayout: {
          title: '侧边栏布局',
          description: '左侧导航栏配合主内容区域，适合功能丰富的看板'
        },
        fullscreenLayout: {
          title: '全屏布局',
          description: '最大化显示空间，适合单一焦点的数据展示'
        },
        columnLayout: {
          title: '分栏布局',
          description: '多列分区显示，适合分类数据的对比展示'
        },
        cardFlowLayout: {
          title: '卡片流布局',
          description: '响应式卡片流，适合移动端和动态内容'
        },
        masonyLayout: {
          title: '瀑布流布局',
          description: '不等高卡片排列，适合内容长度差异较大的数据'
        },
        tabLayout: {
          title: '标签页布局',
          description: '多标签页切换，适合大量数据的分类展示'
        },
        splitLayout: {
          title: '分屏布局',
          description: '左右或上下分屏，适合对比分析场景'
        }
      }
    },

    // 第三步：主题和组件选择（替换原始的"颜色和组件"）
    step3: {
      title: '主题组件',
      description:
        '选择您喜欢的主题配色和所需组件，这将帮助我们生成符合品牌风格且功能完整的看板界面。',
      themeQuestion: '主题配色',
      componentQuestion: '选择需要的看板组件',
      themes: {
        businessBlue: {
          name: '商务蓝',
          description: '专业稳重的商务风格，适合企业级应用'
        },
        modernGreen: {
          name: '现代绿',
          description: '清新现代的绿色调，适合科技和环保主题'
        },
        elegantPurple: {
          name: '优雅紫',
          description: '高贵优雅的紫色系，适合创意和设计行业'
        },
        warmOrange: {
          name: '活力橙',
          description: '温暖活跃的橙色调，适合营销和电商'
        },
        classicGray: {
          name: '经典灰',
          description: '简约经典的灰色系，适合金融和法律行业'
        },
        technologyDark: {
          name: '科技暗黑',
          description: '酷炫的暗黑主题，适合科技和游戏行业'
        },
        freshMint: {
          name: '清新薄荷',
          description: '清新淡雅的薄荷色，适合医疗和教育'
        },
        luxuryGold: {
          name: '奢华金',
          description: '高端奢华的金色调，适合珠宝和奢侈品'
        }
      },
      components: {
        barChart: {
          title: '柱状图',
          description: '适合展示数据对比和趋势分析'
        },
        pieChart: {
          title: '饼图',
          description: '适合展示数据组成和占比关系'
        },
        lineChart: {
          title: '折线图',
          description: '适合展示时间序列和趋势变化'
        },
        dataTable: {
          title: '数据表格',
          description: '详细的数据列表和明细展示'
        },
        statisticsCard: {
          title: '统计卡片',
          description: '关键指标的数值展示和状态提示'
        },
        progressBar: {
          title: '进度条',
          description: '目标完成情况和进度展示'
        },
        kpiIndicator: {
          title: 'KPI指标',
          description: '核心业务指标的监控和预警'
        },
        calendarComponent: {
          title: '日历组件',
          description: '时间相关的事件和计划展示'
        },
        notificationList: {
          title: '通知列表',
          description: '系统通知和重要消息展示'
        },
        mapVisualization: {
          title: '地图可视化',
          description: '地理数据和区域分析展示'
        },
        gaugeChart: {
          title: '仪表盘',
          description: '实时数据的可视化监控'
        },
        trendAnalysis: {
          title: '趋势分析',
          description: '数据趋势和预测分析图表'
        }
      }
    },

    // 第四步：AI看板生成（替换原始的"生成提示词"）
    step4: {
      title: '生成看板',
      description: '根据您的选择，AI 将生成最适合您需求的专业看板界面代码。',
      generateButton: '生成看板',
      generatingButton: '生成中...',
      previewTitle: '看板预览',
      exportButton: '导出代码'
    }
  },

  common: {
    nextButton: '下一步',
    prevButton: '上一步',
    applyButton: '应用',
    editButton: '编辑',
    saveButton: '保存',
    addButton: '添加',
    removeButton: '移除',
    resetButton: '重置',
    notSelected: '未选择',
    countUnit: '个'
  },

  messages: {
    success: {
      configSaved: '配置已保存！',
      dashboardGenerated: '看板生成成功！',
      codeCopied: '代码已复制到剪贴板！',
      configExported: '配置已导出！',
      configImported: '配置导入成功！'
    },
    error: {
      generationFailed: '看板生成失败，请重试',
      networkError: '网络连接错误',
      invalidConfig: '无效的配置参数',
      exportFailed: '导出失败，请重试',
      importFailed: '导入失败，请检查文件格式'
    },
    info: {
      selectPurpose: '请先选择看板用途',
      selectLayout: '请选择布局样式',
      selectTheme: '请选择主题配色',
      selectComponents: '请至少选择一个组件',
      generatingDashboard: 'AI正在生成您的专业看板，请稍等...',
      loadingPreview: '正在加载预览...'
    }
  }
};

// 导出便捷访问函数
export const getText = (path: string): string => {
  const keys = path.split('.');
  let result: any = APP_TEXTS;

  for (const key of keys) {
    if (result && typeof result === 'object' && key in result) {
      result = result[key];
    } else {
      return path; // 如果路径不存在，返回原始路径作为fallback
    }
  }

  return typeof result === 'string' ? result : path;
};

// 导出主要文本对象供直接使用
export default APP_TEXTS;
