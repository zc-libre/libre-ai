<template>
  <div class="step5-dashboard-generator">
    <div class="step-header">
      <h2 class="step-title">
        <el-icon class="step-icon" :size="24" color="#6366F1">
          <View />
        </el-icon>
        AI 看板生成
      </h2>
      <p class="step-description">
        基于您的配置，AI
        将为您生成专业的看板代码。您可以预览效果并下载完整代码。
      </p>
    </div>

    <!-- 配置总结 -->
    <div class="config-summary">
      <el-card>
        <template #header>
          <span class="summary-title">配置总结</span>
        </template>
        <div class="summary-grid">
          <div class="summary-item">
            <div class="item-label">看板用途</div>
            <div class="item-value">
              {{ wizardData.purposeText || '未选择' }}
            </div>
          </div>
          <div class="summary-item">
            <div class="item-label">布局样式</div>
            <div class="item-value">
              {{ wizardData.layoutText || '未选择' }}
            </div>
          </div>
          <div class="summary-item">
            <div class="item-label">主题配色</div>
            <div class="item-value">{{ getThemeName() || '未选择' }}</div>
          </div>
          <div class="summary-item">
            <div class="item-label">选择组件</div>
            <div class="item-value">
              <div
                v-if="wizardData.components && wizardData.components.length > 0"
                class="component-tags"
              >
                <el-tag
                  v-for="component in wizardData.components"
                  :key="component"
                  size="small"
                  type="info"
                >
                  {{ component }}
                </el-tag>
              </div>
              <span v-else>未选择</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 生成控制 -->
    <div class="generation-controls">
      <div class="control-section">
        <h3 class="control-title">生成选项</h3>
        <div class="control-grid">
          <div class="control-item">
            <label class="control-label">代码风格</label>
            <el-select
              v-model="generationOptions.codeStyle"
              placeholder="选择代码风格"
            >
              <el-option label="现代化 (推荐)" value="modern" />
              <el-option label="简洁风格" value="minimal" />
              <el-option label="企业级" value="enterprise" />
            </el-select>
          </div>
          <div class="control-item">
            <label class="control-label">响应式设计</label>
            <el-switch
              v-model="generationOptions.responsive"
              active-text="启用"
              inactive-text="禁用"
            />
          </div>
          <div class="control-item">
            <label class="control-label">包含示例数据</label>
            <el-switch
              v-model="generationOptions.includeData"
              active-text="包含"
              inactive-text="不包含"
            />
          </div>
        </div>
      </div>

      <!-- 额外需求 -->
      <div class="control-section">
        <label class="control-label">额外需求 (可选)</label>
        <el-input
          v-model="generationOptions.additionalRequirements"
          type="textarea"
          :rows="3"
          placeholder="请描述您的特殊需求，例如：需要实时数据更新、特定的交互效果等..."
          maxlength="500"
          show-word-limit
        />
      </div>
    </div>

    <!-- 生成进度 -->
    <div v-if="isGenerating" class="generation-progress">
      <el-card>
        <div class="progress-content">
          <div class="progress-text">{{ currentProgressText }}</div>
          <el-progress
            :percentage="progressPercentage"
            :stroke-width="8"
            :show-text="false"
          />
          <div class="progress-steps">
            <div
              v-for="(step, index) in progressSteps"
              :key="index"
              class="progress-step"
              :class="{
                completed: index < currentProgressStep,
                active: index === currentProgressStep
              }"
            >
              <el-icon :size="16">
                <Check v-if="index < currentProgressStep" />
                <Loading v-else-if="index === currentProgressStep" />
                <Clock v-else />
              </el-icon>
              <span>{{ step }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 生成结果 -->
    <div v-if="generatedResult && !isStreaming" class="generation-result">
      <el-card>
        <template #header>
          <div class="result-header">
            <span class="result-title">生成完成</span>
            <div class="result-actions">
              <el-button type="success" :icon="View" @click="previewDashboard">
                预览看板
              </el-button>
              <el-button type="primary" :icon="Download" @click="downloadCode">
                下载代码
              </el-button>
            </div>
          </div>
        </template>
        <div class="result-content">
          <div class="result-stats">
            <div class="stat-item">
              <div class="stat-value">{{ generatedResult.linesOfCode }}</div>
              <div class="stat-label">代码行数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ generatedResult.components }}</div>
              <div class="stat-label">组件数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ generatedResult.fileSize }}</div>
              <div class="stat-label">文件大小</div>
            </div>
          </div>
          <div class="result-description">
            <p>{{ generatedResult.description }}</p>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { Check, Loading, Clock, View, Download } from '@element-plus/icons-vue';
import { useDashboardGenerator } from '../../composables/useDashboardGenerator';
import { useDashboardStore } from '../../composables/useDashboardStore';
import CodeStreamPreview from '../CodeStreamPreview.vue';

// Props
interface Props {
  wizardData: any;
}

const props = defineProps<Props>();

// Emits
const emit = defineEmits<{
  update: [data: any];
}>();

// 使用 composables
const store = useDashboardStore();
const {
  generateDashboard: doGenerate,
  generationProgress,
  generationResult: generatedResultData,
  isGenerating: generating,
  downloadCode: doDownload,
  previewCode,
  streamingCode,
  isStreaming,
  abortGeneration
} = useDashboardGenerator();

// 状态
const isGenerating = computed(() => generating.value);
const currentProgressStep = computed(() => generationProgress.value.step);
const progressPercentage = computed(() => generationProgress.value.percentage);
const generatedResult = computed(() => generatedResultData.value);

// 生成选项
const generationOptions = reactive({
  codeStyle: 'modern' as 'modern' | 'minimal' | 'enterprise',
  responsive: true,
  includeData: true,
  additionalRequirements: ''
});

// 进度步骤
const progressSteps = [
  '分析配置信息',
  '生成布局结构',
  '应用主题样式',
  '集成组件代码',
  '优化代码质量',
  '生成完成'
];

const currentProgressText = computed(() => {
  return (
    generationProgress.value.message ||
    progressSteps[currentProgressStep.value] ||
    '生成完成'
  );
});

const canGenerate = computed(() => {
  return (
    props.wizardData.purpose &&
    props.wizardData.layout &&
    props.wizardData.theme
  );
});

// 方法
const getThemeName = () => {
  const themeNames = {
    'modern-blue': '现代蓝',
    'dark-purple': '深紫夜',
    'green-nature': '自然绿',
    'orange-warm': '暖橙色'
  };
  return themeNames[props.wizardData.theme] || props.wizardData.theme;
};

const generateDashboard = async () => {
  if (!canGenerate.value) {
    ElMessage.warning('请完成所有必要的配置');
    return;
  }

  // 更新store中的生成选项
  store.updateGenerationOptions(generationOptions);

  // 开始生成时显示预览面板
  store.setShowPreview(true);

  // 调用真实的API生成看板
  const result = await doGenerate();

  if (result) {
    // 更新数据
    emit('update', {
      generatedResult: result,
      generationOptions: { ...generationOptions }
    });
  }
};

const previewDashboard = () => {
  if (generatedResult.value) {
    previewCode(generatedResult.value);
  } else {
    ElMessage.warning('请先生成看板');
  }
};

const downloadCode = () => {
  if (generatedResult.value) {
    doDownload(generatedResult.value);
  } else {
    ElMessage.warning('请先生成看板');
  }
};

// 预览流式生成的代码
const handlePreviewStream = () => {
  if (streamingCode.value) {
    const previewWindow = window.open('', '_blank');
    if (previewWindow) {
      previewWindow.document.write(streamingCode.value);
      previewWindow.document.close();
    }
  }
};

// 重新生成看板
const regenerateDashboard = async () => {
  if (!canGenerate.value) {
    ElMessage.warning('请完成所有必要的配置');
    return;
  }

  // 清空旧的生成结果
  emit('update', {
    generatedResult: null,
    generationOptions: { ...generationOptions }
  });

  // 清空store中的流式代码和生成结果
  store.setStreamingCode('');
  generatedResultData.value = null;

  ElMessage.info('正在重新生成看板...');

  // 短暂延迟后重新生成，让用户看到清空效果
  setTimeout(async () => {
    // 更新store中的生成选项
    store.updateGenerationOptions(generationOptions);

    // 确保预览面板显示
    store.setShowPreview(true);

    // 调用真实的API生成看板
    const result = await doGenerate();

    if (result) {
      // 更新数据
      emit('update', {
        generatedResult: result,
        generationOptions: { ...generationOptions }
      });
    }
  }, 300);
};

// 暴露生成方法供父组件调用
defineExpose({
  generateDashboard,
  regenerateDashboard
});
</script>

<style scoped>
.step5-dashboard-generator {
  margin: 0 auto;
}

.step-header {
  text-align: center;
  margin-bottom: 30px;
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

.config-summary {
  margin-bottom: 30px;
}

.summary-title {
  font-weight: 600;
  color: #303133;
}

.summary-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: flex-start;
}

.summary-item {
  flex: 0 0 calc(25% - 12px);
  min-width: 180px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.item-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.item-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.component-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.generation-controls {
  margin-bottom: 30px;
}

.control-section {
  margin-bottom: 20px;
}

.control-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.control-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
  justify-content: flex-start;
}

.control-item {
  flex: 0 0 calc(33.333% - 11px);
  min-width: 180px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.control-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.generation-action {
  text-align: center;
  margin-bottom: 30px;
}

.generate-btn {
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 600;
}

.btn-icon {
  margin-right: 8px;
}

.generation-progress {
  margin-bottom: 30px;
}

.progress-content {
  text-align: center;
}

.progress-text {
  font-size: 16px;
  color: #303133;
  margin-bottom: 16px;
  font-weight: 500;
}

.progress-steps {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-step {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.progress-step.completed {
  background: #f0f9ff;
  color: #67c23a;
}

.progress-step.active {
  background: #e6f7ff;
  color: #409eff;
}

.generation-result {
  margin-top: 30px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-title {
  font-weight: 600;
  color: #303133;
}

.result-actions {
  display: flex;
  gap: 12px;
}

.result-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.result-description {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 6px;
  color: #606266;
  line-height: 1.6;
}

/* 流式代码预览样式 */
.code-stream-section {
  margin-top: 30px;
}

.code-stream-section :deep(.el-card) {
  background: transparent;
  border: none;
  box-shadow: none;
}

.code-stream-section :deep(.el-card__body) {
  padding: 0;
  height: 500px;
}

/* 响应式设计 - 与项目整体断点保持一致 */
@media (max-width: 990px) {
  .summary-item {
    flex: 0 0 calc(50% - 8px);
  }

  .control-item {
    flex: 0 0 calc(50% - 8px);
  }
}

@media (max-width: 760px) {
  .summary-grid {
    flex-direction: column;
    gap: 12px;
  }

  .summary-item {
    flex: 1 1 auto;
    min-width: unset;
  }

  .control-grid {
    flex-direction: column;
    gap: 12px;
  }

  .control-item {
    flex: 1 1 auto;
    min-width: unset;
  }

  .result-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .result-actions {
    justify-content: center;
  }

  .result-stats {
    flex-direction: column;
    gap: 16px;
  }

  .step-title {
    font-size: 20px;
  }
}
</style>
