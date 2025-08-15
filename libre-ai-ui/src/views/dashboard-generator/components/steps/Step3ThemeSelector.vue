<template>
  <div class="step3-theme-selector">
    <div class="step-header">
      <h2 class="step-title">
        <el-icon class="step-icon" :size="24" color="#6366F1">
          <Brush />
        </el-icon>
        选择主题配色
      </h2>
      <p class="step-description">
        为您的看板选择合适的配色方案，打造专业美观的视觉效果。
      </p>
    </div>

    <!-- 主题选择 -->
    <div class="section">
      <h3 class="section-title">选择配色方案</h3>
      <p class="section-subtitle">选择预设主题或创建自定义配色</p>

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
              :style="{
                backgroundColor:
                  theme.isCustom && theme.id === selectedTheme
                    ? customColors.primary
                    : theme.colors.primary
              }"
            />
            <div
              class="color-bar secondary"
              :style="{
                backgroundColor:
                  theme.isCustom && theme.id === selectedTheme
                    ? customColors.secondary
                    : theme.colors.secondary
              }"
            />
            <div
              class="color-bar accent"
              :style="{
                backgroundColor:
                  theme.isCustom && theme.id === selectedTheme
                    ? customColors.accent
                    : theme.colors.accent
              }"
            />
          </div>
          <div class="theme-info">
            <h4 class="theme-name">{{ theme.name }}</h4>
            <p class="theme-description">{{ theme.description }}</p>
          </div>
          <div v-if="theme.isCustom" class="custom-indicator">
            <el-icon :size="18" color="#6366F1">
              <Edit />
            </el-icon>
          </div>
          <div v-if="selectedTheme === theme.id" class="selected-indicator">
            <el-icon :size="20" color="#409EFF">
              <Check />
            </el-icon>
          </div>
        </div>
      </div>

      <!-- 自定义颜色面板 -->
      <el-collapse-transition>
        <div v-if="selectedTheme === 'custom'" class="custom-color-panel">
          <el-card>
            <template #header>
              <span class="panel-title">自定义配色方案</span>
            </template>
            <div class="color-pickers">
              <div class="color-picker-item">
                <label>主色调</label>
                <div class="color-input-group">
                  <el-color-picker
                    v-model="customColors.primary"
                    show-alpha
                    :predefine="[
                      '#409EFF',
                      '#6366F1',
                      '#8B5CF6',
                      '#EC4899',
                      '#F56C6C',
                      '#E6A23C',
                      '#67C23A',
                      '#17A2B8'
                    ]"
                    @change="updateCustomTheme"
                  />
                  <el-input
                    v-model="customColors.primary"
                    placeholder="#6366F1"
                    size="small"
                    @input="updateCustomTheme"
                  />
                </div>
              </div>
              <div class="color-picker-item">
                <label>辅助色</label>
                <div class="color-input-group">
                  <el-color-picker
                    v-model="customColors.secondary"
                    show-alpha
                    :predefine="[
                      '#79BBFF',
                      '#818CF8',
                      '#A78BFA',
                      '#F472B6',
                      '#F78989',
                      '#EEBE77',
                      '#85CE61',
                      '#46B5D1'
                    ]"
                    @change="updateCustomTheme"
                  />
                  <el-input
                    v-model="customColors.secondary"
                    placeholder="#818CF8"
                    size="small"
                    @input="updateCustomTheme"
                  />
                </div>
              </div>
              <div class="color-picker-item">
                <label>强调色</label>
                <div class="color-input-group">
                  <el-color-picker
                    v-model="customColors.accent"
                    show-alpha
                    :predefine="[
                      '#A0CFFF',
                      '#A5B4FC',
                      '#C4B5FD',
                      '#FBCFE8',
                      '#FAB6B6',
                      '#F3D19E',
                      '#B3E19D',
                      '#7CC7D8'
                    ]"
                    @change="updateCustomTheme"
                  />
                  <el-input
                    v-model="customColors.accent"
                    placeholder="#A5B4FC"
                    size="small"
                    @input="updateCustomTheme"
                  />
                </div>
              </div>
            </div>
            <div class="color-preview">
              <div class="preview-title">预览效果</div>
              <div class="preview-content">
                <div
                  class="preview-bar"
                  :style="{ backgroundColor: customColors.primary }"
                >
                  <span>主色调</span>
                </div>
                <div
                  class="preview-bar"
                  :style="{ backgroundColor: customColors.secondary }"
                >
                  <span>辅助色</span>
                </div>
                <div
                  class="preview-bar"
                  :style="{ backgroundColor: customColors.accent }"
                >
                  <span>强调色</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </el-collapse-transition>
    </div>

    <!-- 配色预览 -->
    <div v-if="selectedTheme" class="section">
      <h3 class="section-title">配色应用预览</h3>
      <div class="preview-demo">
        <el-card>
          <div
            class="demo-header"
            :style="{ borderBottomColor: getCurrentColors().primary }"
          >
            <span
              class="demo-title"
              :style="{ color: getCurrentColors().primary }"
              >看板标题示例</span
            >
          </div>
          <div class="demo-content">
            <div class="demo-stats">
              <div
                class="stat-card"
                :style="{ borderLeftColor: getCurrentColors().primary }"
              >
                <div
                  class="stat-value"
                  :style="{ color: getCurrentColors().primary }"
                >
                  12,345
                </div>
                <div class="stat-label">主要指标</div>
              </div>
              <div
                class="stat-card"
                :style="{ borderLeftColor: getCurrentColors().secondary }"
              >
                <div
                  class="stat-value"
                  :style="{ color: getCurrentColors().secondary }"
                >
                  67.8%
                </div>
                <div class="stat-label">次要指标</div>
              </div>
              <div
                class="stat-card"
                :style="{ borderLeftColor: getCurrentColors().accent }"
              >
                <div
                  class="stat-value"
                  :style="{ color: getCurrentColors().accent }"
                >
                  +23.5
                </div>
                <div class="stat-label">辅助指标</div>
              </div>
            </div>
            <div class="demo-chart">
              <div class="chart-bars">
                <div
                  class="chart-bar"
                  :style="{
                    backgroundColor: getCurrentColors().primary,
                    height: '80%'
                  }"
                />
                <div
                  class="chart-bar"
                  :style="{
                    backgroundColor: getCurrentColors().secondary,
                    height: '60%'
                  }"
                />
                <div
                  class="chart-bar"
                  :style="{
                    backgroundColor: getCurrentColors().accent,
                    height: '45%'
                  }"
                />
                <div
                  class="chart-bar"
                  :style="{
                    backgroundColor: getCurrentColors().primary,
                    height: '70%'
                  }"
                />
                <div
                  class="chart-bar"
                  :style="{
                    backgroundColor: getCurrentColors().secondary,
                    height: '55%'
                  }"
                />
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 选择总结 -->
    <div v-if="selectedTheme" class="selection-summary">
      <el-card>
        <template #header>
          <span class="summary-title">当前选择</span>
        </template>
        <div class="summary-content">
          <div class="summary-item">
            <strong>主题方案：</strong>{{ selectedThemeOption?.name }}
          </div>
          <div class="summary-item">
            <strong>配色详情：</strong>
            <div class="color-tags">
              <el-tag
                :style="{
                  backgroundColor: getCurrentColors().primary,
                  color: '#fff'
                }"
              >
                主色: {{ getCurrentColors().primary }}
              </el-tag>
              <el-tag
                :style="{
                  backgroundColor: getCurrentColors().secondary,
                  color: '#fff'
                }"
              >
                辅助: {{ getCurrentColors().secondary }}
              </el-tag>
              <el-tag
                :style="{
                  backgroundColor: getCurrentColors().accent,
                  color: '#fff'
                }"
              >
                强调: {{ getCurrentColors().accent }}
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
import { Check, Edit, Brush } from '@element-plus/icons-vue';

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
const selectedTheme = ref(props.wizardData.theme || '');
const customColors = ref({
  primary: props.wizardData.customColors?.primary || '#6366F1',
  secondary: props.wizardData.customColors?.secondary || '#818CF8',
  accent: props.wizardData.customColors?.accent || '#A5B4FC'
});

import { themeOptions as THEME_OPTIONS } from '../../constants/options';

// 主题选项
// 从 constants/options 引入，避免重复定义
const themeOptions = THEME_OPTIONS;

// 计算属性
const selectedThemeOption = computed(() => {
  return themeOptions.find(theme => theme.id === selectedTheme.value);
});

// 获取当前颜色
const getCurrentColors = () => {
  if (selectedTheme.value === 'custom') {
    return customColors.value;
  }
  return selectedThemeOption.value?.colors || customColors.value;
};

// 方法
const selectTheme = (theme: any) => {
  selectedTheme.value = theme.id;
  updateData();
};

const updateCustomTheme = () => {
  if (selectedTheme.value === 'custom') {
    updateData();
  }
};

const updateData = () => {
  const isCustom = selectedTheme.value === 'custom';
  const themeColors = isCustom
    ? customColors.value
    : selectedThemeOption.value?.colors || {};
  const themeName = isCustom ? '自定义' : selectedThemeOption.value?.name || '';

  const updateData = {
    theme: selectedTheme.value,
    themeText: themeName,
    themeColors: themeColors,
    customColors: isCustom ? customColors.value : undefined
  };

  emit('update', updateData);
};
</script>

<style scoped>
/* 响应式设计 */
@media (width <= 990px) {
  .color-pickers {
    flex-direction: column;
    gap: 16px;
  }

  .theme-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}

@media (width <= 760px) {
  .theme-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .demo-stats {
    flex-direction: column;
  }

  .step-title {
    font-size: 20px;
  }
}

.step3-theme-selector {
  margin: 0 auto;
}

.step-header {
  margin-bottom: 40px;
  text-align: center;
}

.step-title {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.step-description {
  margin: 0;
  font-size: 16px;
  line-height: 1.6;
  color: #606266;
}

.section {
  margin-bottom: 40px;
}

.section-title {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.section-subtitle {
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.theme-card {
  position: relative;
  padding: 16px;
  cursor: pointer;
  background: white;
  border: 2px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.theme-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgb(64 158 255 / 15%);
  transform: translateY(-2px);
}

.theme-card.selected {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgb(64 158 255 / 30%);
}

.custom-indicator,
.selected-indicator {
  position: absolute;
  top: 8px;
  right: 8px;
}

.theme-preview {
  display: flex;
  height: 40px;
  margin-bottom: 12px;
  overflow: hidden;
  border-radius: 4px;
}

.color-bar {
  flex: 1;
}

.theme-info {
  text-align: center;
}

.theme-name {
  margin-bottom: 4px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.theme-description {
  font-size: 12px;
  color: #606266;
}

.custom-color-panel {
  margin-top: 20px;
}

.panel-title {
  font-weight: 600;
  color: #303133;
}

.color-pickers {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
}

.color-picker-item {
  flex: 1;
}

.color-picker-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.color-input-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.color-input-group .el-input {
  flex: 1;
  max-width: 120px;
}

.color-preview {
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.preview-title {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.preview-content {
  display: flex;
  gap: 8px;
  height: 60px;
}

.preview-bar {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 500;
  color: white;
  text-shadow: 0 1px 2px rgb(0 0 0 / 10%);
  border-radius: 4px;
}

/* 配色预览部分 */
.preview-demo {
  max-width: 600px;
  margin: 0 auto;
}

.demo-header {
  padding-bottom: 12px;
  margin-bottom: 20px;
  border-bottom: 2px solid;
}

.demo-title {
  font-size: 18px;
  font-weight: 600;
}

.demo-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  padding: 12px;
  background: #f5f7fa;
  border-left: 3px solid;
  border-radius: 4px;
}

.stat-value {
  margin-bottom: 4px;
  font-size: 24px;
  font-weight: 600;
}

.stat-label {
  font-size: 12px;
  color: #606266;
}

.demo-chart {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.chart-bars {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  height: 100px;
}

.chart-bar {
  flex: 1;
  border-radius: 4px 4px 0 0;
  transition: all 0.3s ease;
}

/* 选择总结 */
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

.color-tags {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
</style>
