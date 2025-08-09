<template>
  <div class="step4-layout-selector">
    <div class="step-header">
      <h2 class="step-title">
        <el-icon class="step-icon" :size="24" color="#6366F1">
          <Grid />
        </el-icon>
        选择布局样式
      </h2>
      <p class="step-description">
        基于您选择的组件，选择最适合的布局方式。我们已根据您的组件类型优先推荐合适的布局。
      </p>
    </div>

    <div class="layout-grid">
      <div
        v-for="layout in layoutOptions"
        :key="layout.id"
        class="layout-card"
        :class="{ selected: selectedLayout === layout.id }"
        @click="selectLayout(layout)"
      >
        <div class="layout-preview">
          <div class="preview-container" :class="`preview-${layout.id}`">
            <component :is="layout.previewComponent" />
          </div>
        </div>

        <div class="layout-info">
          <h3 class="layout-title">{{ layout.title }}</h3>
          <p class="layout-description">{{ layout.description }}</p>

          <div class="layout-features">
            <div class="feature-item">
              <el-icon :size="16" color="#67C23A">
                <Check />
              </el-icon>
              <span>{{ layout.bestFor }}</span>
            </div>
            <div class="feature-item">
              <el-icon :size="16" color="#409EFF">
                <InfoFilled />
              </el-icon>
              <span>{{ layout.responsive ? '响应式设计' : '固定布局' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 布局详细说明 -->
    <div v-if="selectedLayoutOption" class="layout-details">
      <el-card>
        <template #header>
          <span class="details-title"
            >{{ selectedLayoutOption.title }} - 详细说明</span
          >
        </template>
        <div class="details-content">
          <p>
            <strong>适用场景：</strong
            >{{ selectedLayoutOption.detailedDescription }}
          </p>
          <p><strong>推荐组件：</strong></p>
          <div class="recommended-components">
            <el-tag
              v-for="component in selectedLayoutOption.recommendedComponents"
              :key="component"
              type="info"
              size="small"
            >
              {{ component }}
            </el-tag>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { Grid, Check, InfoFilled } from '@element-plus/icons-vue';
import GridLayoutPreview from '../previews/GridLayoutPreview.vue';
import FullscreenLayoutPreview from '../previews/FullscreenLayoutPreview.vue';
import MasonryLayoutPreview from '../previews/MasonryLayoutPreview.vue';
// 新增布局预览组件（稍后创建）
import HeaderMainLayoutPreview from '../previews/HeaderMainLayoutPreview.vue';
import SplitScreenLayoutPreview from '../previews/SplitScreenLayoutPreview.vue';
import RadialLayoutPreview from '../previews/RadialLayoutPreview.vue';
import TimelineLayoutPreview from '../previews/TimelineLayoutPreview.vue';
import DashboardLayoutPreview from '../previews/DashboardLayoutPreview.vue';

// Props
import type { DashboardConfig as StoreDashboardConfig } from '../../composables/useDashboardStore';

interface Props {
  wizardData: StoreDashboardConfig;
}

const props = defineProps<Props>();

// Emits
const emit = defineEmits<{
  update: [data: any];
}>();

// 状态
const selectedLayout = ref(props.wizardData.layout || '');

// 布局选项
const layoutOptions = [
  {
    id: 'grid',
    title: '网格布局',
    description: '经典的卡片网格排列，适合展示多个同等重要的指标',
    detailedDescription:
      '最常用的看板布局，将数据卡片整齐排列成网格，每个卡片展示一个关键指标或图表，布局清晰，信息密度适中',
    bestFor: '通用数据看板',
    responsive: true,
    previewComponent: GridLayoutPreview,
    recommendedComponents: ['KPI卡片', '折线图', '柱状图', '饼图', '数据表格']
  },
  {
    id: 'header-main',
    title: 'KPI顶置布局',
    description: '顶部KPI指标栏 + 下方详细图表区域',
    detailedDescription:
      '顶部横向展示3-5个核心KPI指标，下方区域展示详细的图表分析。用户可以快速了解关键指标，同时查看详细数据',
    bestFor: '运营/销售看板',
    responsive: true,
    previewComponent: HeaderMainLayoutPreview,
    recommendedComponents: [
      'KPI指标条',
      '趋势图',
      '区域图',
      '排行榜',
      '数据明细'
    ]
  },
  {
    id: 'split-screen',
    title: '左右分屏布局',
    description: '屏幕垂直分割，适合对比展示两组数据',
    detailedDescription:
      '将屏幕分为左右两个独立区域，可以同时展示两个维度的数据，方便对比分析。支持调整左右比例',
    bestFor: '对比分析看板',
    responsive: true,
    previewComponent: SplitScreenLayoutPreview,
    recommendedComponents: [
      '对比图表',
      '双轴图',
      '平行坐标图',
      'KPI对比卡',
      '趋势对比'
    ]
  },
  {
    id: 'radial',
    title: '中心辐射布局',
    description: '核心指标居中，相关指标环绕四周',
    detailedDescription:
      '将最重要的指标或图表放在中心位置，相关的次要指标环绕排列。强调核心数据，展示数据关联性',
    bestFor: '关键指标监控',
    responsive: true,
    previewComponent: RadialLayoutPreview,
    recommendedComponents: ['雷达图', '关系图', '仪表盘', '环形图', '指标卡片']
  },
  {
    id: 'timeline',
    title: '时间轴布局',
    description: '按时间顺序展示数据变化，支持横向滚动',
    detailedDescription:
      '数据按时间顺序排列，可以展示业务发展历程、项目进度、历史趋势等。支持时间轴缩放和滚动',
    bestFor: '进度/趋势看板',
    responsive: true,
    previewComponent: TimelineLayoutPreview,
    recommendedComponents: ['时间轴', '甘特图', '里程碑', '趋势图', '事件列表']
  },
  {
    id: 'dashboard',
    title: '仪表盘布局',
    description: '模拟汽车仪表盘，适合实时监控场景',
    detailedDescription:
      '使用仪表盘、环形进度条等组件，模拟物理仪表盘效果。直观展示当前状态和阈值，适合实时监控',
    bestFor: '性能监控看板',
    responsive: true,
    previewComponent: DashboardLayoutPreview,
    recommendedComponents: [
      '仪表盘',
      '进度环',
      '状态灯',
      '实时曲线',
      '告警列表'
    ]
  },
  {
    id: 'fullscreen',
    title: '全屏大屏布局',
    description: '充分利用屏幕空间的大屏展示布局',
    detailedDescription:
      '专为大屏展示设计，去除多余装饰，最大化数据展示区域。支持自动轮播、动画效果和深色主题',
    bestFor: '指挥中心大屏',
    responsive: true,
    previewComponent: FullscreenLayoutPreview,
    recommendedComponents: [
      '3D地图',
      '大型图表',
      '实时数据流',
      '动态排行',
      '视频监控'
    ]
  },
  {
    id: 'masonry',
    title: '瀑布流布局',
    description: '自适应高度的卡片流式排列',
    detailedDescription:
      '卡片根据内容自适应高度，自动填充空间形成瀑布流效果。适合内容长度不固定的场景，空间利用率高',
    bestFor: '信息汇总看板',
    responsive: true,
    previewComponent: MasonryLayoutPreview,
    recommendedComponents: [
      '动态卡片',
      '信息列表',
      '社交动态',
      '新闻资讯',
      '日志记录'
    ]
  }
];

// 计算属性
const selectedLayoutOption = computed(() => {
  return layoutOptions.find(layout => layout.id === selectedLayout.value);
});

// 方法
const selectLayout = (layout: any) => {
  selectedLayout.value = layout.id;

  const updateData = {
    layout: layout.id,
    layoutText: layout.title,
    layoutOption: layout
  };

  emit('update', updateData);
};
</script>

<style scoped>
.step4-layout-selector {
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

.layout-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
  justify-content: flex-start;
}

.layout-card {
  flex: 0 0 calc(25% - 15px);
  min-width: 260px;
  border: 2px solid #ebeef5;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.layout-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.layout-card.selected {
  border-color: #409eff;
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.3);
}

.layout-preview {
  height: 180px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.preview-container {
  width: 90%;
  height: 90%;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.layout-info {
  padding: 20px;
}

.layout-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.layout-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 16px;
}

.layout-features {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.layout-details {
  margin-top: 30px;
}

.details-title {
  font-weight: 600;
  color: #303133;
}

.details-content p {
  margin-bottom: 12px;
  line-height: 1.6;
  color: #606266;
}

.recommended-components {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .layout-card {
    flex: 0 0 calc(33.333% - 14px);
  }
}

@media (max-width: 990px) {
  .layout-card {
    flex: 0 0 calc(50% - 10px);
  }
}

@media (max-width: 760px) {
  .layout-grid {
    flex-direction: column;
    gap: 16px;
  }

  .layout-card {
    flex: 1 1 auto;
    min-width: unset;
  }

  .layout-preview {
    height: 150px;
  }

  .layout-info {
    padding: 16px;
  }

  .step-title {
    font-size: 20px;
  }
}
</style>
