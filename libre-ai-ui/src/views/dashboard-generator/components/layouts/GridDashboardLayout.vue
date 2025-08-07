<template>
  <div class="grid-dashboard-layout">
    <div class="dashboard-grid">
      <div
        v-for="(component, index) in displayComponents"
        :key="index"
        class="grid-item"
        :class="`component-${component.type}`"
      >
        <div class="component-card">
          <div class="card-header">
            <h3 class="card-title">{{ component.title }}</h3>
            <el-icon :size="16" :color="themeColors.primary">
              <component :is="component.icon" />
            </el-icon>
          </div>
          <div class="card-content">
            <component
              :is="component.component"
              :data="component.data"
              :colors="themeColors"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import {
  DataAnalysis,
  TrendCharts,
  PieChart,
  DataLine,
  Grid,
  List,
  Calendar,
  Clock
} from '@element-plus/icons-vue';
// 简单的占位符组件
const PlaceholderComponent = {
  template: `<div class="placeholder-component">
    <div class="placeholder-content">
      <slot>{{ title || '组件占位符' }}</slot>
    </div>
  </div>`,
  props: ['title']
};

// Props
interface Props {
  config: any;
  themeColors: any;
}

const props = defineProps<Props>();

// 组件映射
const componentMap = {
  'bar-chart': {
    type: 'chart',
    title: '柱状图',
    icon: DataAnalysis,
    component: PlaceholderComponent,
    data: { type: 'bar' }
  },
  'line-chart': {
    type: 'chart',
    title: '折线图',
    icon: TrendCharts,
    component: PlaceholderComponent,
    data: { type: 'line' }
  },
  'pie-chart': {
    type: 'chart',
    title: '饼图',
    icon: PieChart,
    component: PlaceholderComponent,
    data: { type: 'pie' }
  },
  'area-chart': {
    type: 'chart',
    title: '面积图',
    icon: DataLine,
    component: PlaceholderComponent,
    data: { type: 'area' }
  },
  'data-table': {
    type: 'data',
    title: '数据表格',
    icon: Grid,
    component: PlaceholderComponent,
    data: {}
  },
  'data-list': {
    type: 'data',
    title: '数据列表',
    icon: List,
    component: PlaceholderComponent,
    data: {}
  },
  'kpi-card': {
    type: 'kpi',
    title: 'KPI指标',
    icon: DataAnalysis,
    component: PlaceholderComponent,
    data: {}
  },
  calendar: {
    type: 'interactive',
    title: '日历',
    icon: Calendar,
    component: PlaceholderComponent,
    data: { type: 'calendar' }
  },
  timer: {
    type: 'interactive',
    title: '时间显示',
    icon: Clock,
    component: PlaceholderComponent,
    data: { type: 'time' }
  }
};

// 计算属性
const displayComponents = computed(() => {
  const selectedIds = props.config.componentIds || [];

  // 如果没有选择组件，显示默认组件
  if (selectedIds.length === 0) {
    return [
      componentMap['kpi-card'],
      componentMap['bar-chart'],
      componentMap['line-chart'],
      componentMap['data-table']
    ];
  }

  // 根据选择的组件ID生成显示组件
  return selectedIds.map(id => componentMap[id] || componentMap['kpi-card']);
});
</script>

<style scoped>
.grid-dashboard-layout {
  width: 100%;
  height: 100%;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  height: 100%;
}

.grid-item {
  min-height: 200px;
}

.component-card {
  height: 100%;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.component-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.card-content {
  padding: 20px;
  height: calc(100% - 60px);
}

/* 组件类型样式 */
.component-chart .card-header {
  background: linear-gradient(135deg, #e6f7ff, #f0f9ff);
}

.component-data .card-header {
  background: linear-gradient(135deg, #f6ffed, #f0f9ff);
}

.component-kpi .card-header {
  background: linear-gradient(135deg, #fff7e6, #fff2e8);
}

.component-interactive .card-header {
  background: linear-gradient(135deg, #f9f0ff, #f5f3ff);
}

/* 占位符组件样式 */
.placeholder-component {
  width: 100%;
  height: 100%;
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f5, #e8e8e8);
  border-radius: 8px;
  border: 2px dashed #d0d0d0;
}

.placeholder-content {
  text-align: center;
  color: #666;
  font-size: 14px;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .card-content {
    padding: 16px;
  }
}
</style>
