<template>
  <div class="step2-layout-selector">
    <div class="step-header">
      <h2 class="step-title">
        <el-icon class="step-icon" :size="24" color="#6366F1">
          <Grid />
        </el-icon>
        选择布局样式
      </h2>
      <p class="step-description">
        选择最适合您看板内容的布局方式，不同布局适用于不同的数据展示需求。
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
import SidebarLayoutPreview from '../previews/SidebarLayoutPreview.vue';
import FullscreenLayoutPreview from '../previews/FullscreenLayoutPreview.vue';
import MasonryLayoutPreview from '../previews/MasonryLayoutPreview.vue';

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
const selectedLayout = ref(props.wizardData.layout || '');

// 布局选项
const layoutOptions = [
  {
    id: 'grid',
    title: '网格布局',
    description: '规整的卡片网格排列，适合展示多个同等重要的数据模块',
    detailedDescription:
      '适合展示多个数据指标、图表组件，每个组件占用相等的空间，整体布局规整美观',
    bestFor: '多指标展示',
    responsive: true,
    previewComponent: GridLayoutPreview,
    recommendedComponents: ['KPI卡片', '图表组件', '数据表格', '进度条']
  },
  {
    id: 'sidebar',
    title: '侧边栏布局',
    description: '左侧导航栏配合主内容区域，适合有层级结构的数据展示',
    detailedDescription:
      '左侧提供导航菜单，右侧展示详细内容，适合有多个数据分类或需要切换视图的场景',
    bestFor: '分类数据展示',
    responsive: true,
    previewComponent: SidebarLayoutPreview,
    recommendedComponents: ['导航菜单', '详情面板', '数据列表', '筛选器']
  },
  {
    id: 'fullscreen',
    title: '全屏布局',
    description: '单一大型组件占据全屏，适合展示重要的核心数据',
    detailedDescription:
      '整个屏幕专注展示一个核心内容，适合重要指标监控、大屏展示等场景',
    bestFor: '核心指标监控',
    responsive: true,
    previewComponent: FullscreenLayoutPreview,
    recommendedComponents: ['大型图表', '实时监控', '地图组件', '视频展示']
  },
  {
    id: 'masonry',
    title: '瀑布流布局',
    description: '不规则卡片排列，适合展示不同尺寸的内容模块',
    detailedDescription:
      '组件可以有不同的高度，自动排列形成瀑布流效果，适合内容长度不一的场景',
    bestFor: '动态内容展示',
    responsive: true,
    previewComponent: MasonryLayoutPreview,
    recommendedComponents: ['动态卡片', '新闻列表', '产品展示', '活动信息']
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
.step2-layout-selector {
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
  min-width: 280px;
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
