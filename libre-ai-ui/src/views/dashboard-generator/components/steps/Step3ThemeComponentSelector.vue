<template>
  <div class="step3-theme-component-selector">
    <div class="step-header">
      <h2 class="step-title">
        <el-icon class="step-icon" :size="24" color="#6366F1">
          <Setting />
        </el-icon>
        主题配色与组件选择
      </h2>
      <p class="step-description">
        选择您喜欢的主题配色方案，并选择需要的组件来构建您的看板。
      </p>
    </div>

    <!-- 主题选择 -->
    <div class="section">
      <h3 class="section-title">选择主题配色</h3>
      <div class="theme-grid">
        <div
          v-for="theme in themeOptions"
          :key="theme.id"
          class="theme-card"
          :class="{ selected: selectedTheme === theme.id }"
          @click="selectTheme(theme)"
        >
          <div class="theme-preview">
            <div
              class="color-bar primary"
              :style="{ backgroundColor: theme.colors.primary }"
            />
            <div
              class="color-bar secondary"
              :style="{ backgroundColor: theme.colors.secondary }"
            />
            <div
              class="color-bar accent"
              :style="{ backgroundColor: theme.colors.accent }"
            />
          </div>
          <div class="theme-info">
            <h4 class="theme-name">{{ theme.name }}</h4>
            <p class="theme-description">{{ theme.description }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 组件选择 -->
    <div class="section">
      <h3 class="section-title">选择看板组件</h3>
      <p class="section-subtitle">根据您的看板用途，我们推荐以下组件：</p>

      <div class="component-categories">
        <el-tabs v-model="activeComponentTab" type="border-card">
          <el-tab-pane
            v-for="category in componentCategories"
            :key="category.id"
            :label="category.name"
            :name="category.id"
          >
            <div class="component-grid">
              <div
                v-for="component in category.components"
                :key="component.id"
                class="component-card"
                :class="{ selected: selectedComponents.includes(component.id) }"
                @click="toggleComponent(component)"
              >
                <div class="component-icon">
                  <el-icon
                    :size="24"
                    :color="
                      selectedComponents.includes(component.id)
                        ? '#fff'
                        : component.color
                    "
                  >
                    <component :is="component.icon" />
                  </el-icon>
                </div>
                <h4 class="component-name">{{ component.name }}</h4>
                <p class="component-description">{{ component.description }}</p>
                <div
                  v-if="selectedComponents.includes(component.id)"
                  class="selected-indicator"
                >
                  <el-icon :size="16" color="#fff">
                    <Check />
                  </el-icon>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 选择总结 -->
    <div
      v-if="selectedTheme || selectedComponents.length > 0"
      class="selection-summary"
    >
      <el-card>
        <template #header>
          <span class="summary-title">当前选择</span>
        </template>
        <div class="summary-content">
          <div v-if="selectedTheme" class="summary-item">
            <strong>主题：</strong>{{ selectedThemeOption?.name }}
          </div>
          <div v-if="selectedComponents.length > 0" class="summary-item">
            <strong>组件：</strong>
            <div class="selected-components">
              <el-tag
                v-for="componentId in selectedComponents"
                :key="componentId"
                type="primary"
                size="small"
              >
                {{ getComponentName(componentId) }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import {
  Check,
  DataAnalysis,
  TrendCharts,
  PieChart,
  DataLine,
  Grid,
  List,
  Calendar,
  Clock,
  User,
  Setting
} from '@element-plus/icons-vue';

// Props
interface Props {
  wizardData: any;
}

const props = defineProps<Props>();

// Emits
const emit = defineEmits<{
  update: [data: any];
}>();

// 状态
const selectedTheme = ref(props.wizardData.theme || '');
const selectedComponents = ref(props.wizardData.componentIds || []);
const activeComponentTab = ref('charts');

// 主题选项
const themeOptions = [
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
  }
];

// 组件分类
const componentCategories = [
  {
    id: 'charts',
    name: '图表组件',
    components: [
      {
        id: 'bar-chart',
        name: '柱状图',
        description: '展示数据对比',
        icon: DataAnalysis,
        color: '#409EFF'
      },
      {
        id: 'line-chart',
        name: '折线图',
        description: '展示趋势变化',
        icon: TrendCharts,
        color: '#67C23A'
      },
      {
        id: 'pie-chart',
        name: '饼图',
        description: '展示数据占比',
        icon: PieChart,
        color: '#E6A23C'
      },
      {
        id: 'area-chart',
        name: '面积图',
        description: '展示数据分布',
        icon: DataLine,
        color: '#F56C6C'
      }
    ]
  },
  {
    id: 'data',
    name: '数据组件',
    components: [
      {
        id: 'data-table',
        name: '数据表格',
        description: '展示详细数据',
        icon: Grid,
        color: '#909399'
      },
      {
        id: 'data-list',
        name: '数据列表',
        description: '展示列表信息',
        icon: List,
        color: '#606266'
      },
      {
        id: 'kpi-card',
        name: 'KPI卡片',
        description: '展示关键指标',
        icon: DataAnalysis,
        color: '#409EFF'
      }
    ]
  },
  {
    id: 'interactive',
    name: '交互组件',
    components: [
      {
        id: 'calendar',
        name: '日历组件',
        description: '时间选择器',
        icon: Calendar,
        color: '#67C23A'
      },
      {
        id: 'timer',
        name: '时间显示',
        description: '实时时间',
        icon: Clock,
        color: '#E6A23C'
      },
      {
        id: 'user-info',
        name: '用户信息',
        description: '用户状态',
        icon: User,
        color: '#8B5CF6'
      },
      {
        id: 'settings',
        name: '设置面板',
        description: '配置选项',
        icon: Setting,
        color: '#F56C6C'
      }
    ]
  }
];

// 计算属性
const selectedThemeOption = computed(() => {
  return themeOptions.find(theme => theme.id === selectedTheme.value);
});

// 方法
const selectTheme = (theme: any) => {
  selectedTheme.value = theme.id;
  updateData();
};

const toggleComponent = (component: any) => {
  const index = selectedComponents.value.indexOf(component.id);
  if (index > -1) {
    selectedComponents.value.splice(index, 1);
  } else {
    selectedComponents.value.push(component.id);
  }
  updateData();
};

const getComponentName = (componentId: string) => {
  for (const category of componentCategories) {
    const component = category.components.find(c => c.id === componentId);
    if (component) return component.name;
  }
  return componentId;
};

const updateData = () => {
  const updateData = {
    theme: selectedTheme.value,
    themeColors: selectedThemeOption.value?.colors || {},
    componentIds: selectedComponents.value,
    components: selectedComponents.value.map(id => getComponentName(id))
  };

  emit('update', updateData);
};
</script>

<style scoped>
.step3-theme-component-selector {
  margin: 0 auto;
}

.step-header {
  text-align: center;
  margin-bottom: 40px;
}

.step-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.step-description {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.section-subtitle {
  font-size: 14px;
  color: #606266;
  margin-bottom: 20px;
}

.theme-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: flex-start;
}

.theme-card {
  flex: 0 0 calc(25% - 12px);
  min-width: 180px;
  border: 2px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.theme-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.theme-card.selected {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.theme-preview {
  display: flex;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 12px;
}

.color-bar {
  flex: 1;
}

.theme-info {
  text-align: center;
}

.theme-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.theme-description {
  font-size: 12px;
  color: #606266;
}

.component-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: flex-start;
}

.component-card {
  flex: 0 0 calc(20% - 13px);
  min-width: 140px;
  border: 2px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  text-align: center;
  position: relative;
}

.component-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.component-card.selected {
  border-color: #409eff;
  background: linear-gradient(135deg, #409eff, #6366f1);
  color: white;
}

.component-icon {
  margin-bottom: 8px;
}

.component-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.component-description {
  font-size: 12px;
  opacity: 0.8;
}

.selected-indicator {
  position: absolute;
  top: 8px;
  right: 8px;
}

.selection-summary {
  margin-top: 30px;
}

.summary-title {
  font-weight: 600;
  color: #303133;
}

.summary-item {
  margin-bottom: 12px;
  line-height: 1.6;
}

.selected-components {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

/* 响应式设计 - 与项目整体断点保持一致 */
@media (max-width: 990px) {
  .theme-card {
    flex: 0 0 calc(33.333% - 11px);
  }

  .component-card {
    flex: 0 0 calc(25% - 12px);
  }
}

@media (max-width: 760px) {
  .theme-grid {
    flex-direction: column;
    gap: 12px;
  }

  .theme-card {
    flex: 1 1 auto;
    min-width: unset;
    flex: 0 0 calc(50% - 8px);
  }

  .component-grid {
    justify-content: center;
  }

  .component-card {
    flex: 0 0 calc(50% - 8px);
    min-width: 120px;
  }

  .step-title {
    font-size: 20px;
  }
}
</style>
