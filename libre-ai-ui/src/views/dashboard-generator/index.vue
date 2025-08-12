<template>
  <div class="view-container dashboard-generator-app bg-bg_color">
    <!-- 进度指示器 -->
    <div
      class="progress-section flex-shrink-0 w-full px-4 sm:px-6 lg:px-8 py-3"
    >
      <div class="flex justify-between items-center gap-4">
        <el-steps :active="store.currentStep - 1" align-center class="flex-1">
          <el-step title="看板用途" description="选择看板的使用场景" />
          <el-step title="组件选择" description="选择需要的数据组件" />
          <el-step title="主题配色" description="选择看板的配色方案" />
          <el-step title="布局样式" description="选择看板的布局风格" />
          <el-step title="生成看板" description="AI 智能生成看板代码" />
        </el-steps>

        <!-- 重置按钮（只在有配置数据时显示） -->
        <div
          v-if="store.currentStep > 1 || store.wizardData.generatedResult"
          class="flex gap-2"
        >
          <el-tooltip content="保存当前配置并创建新看板" placement="bottom">
            <el-button
              type="success"
              :icon="Plus"
              size="small"
              @click="handleNewDashboard"
            >
              新建看板
            </el-button>
          </el-tooltip>
          <el-tooltip content="查看历史配置" placement="bottom">
            <el-button
              :icon="Clock"
              size="small"
              @click="store.setShowHistory(true)"
            >
              历史记录
            </el-button>
          </el-tooltip>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div
      class="main-content flex-1 flex flex-col lg:flex-row gap-3 px-4 sm:px-6 lg:px-8 pb-3 min-h-0"
    >
      <!-- 左侧向导容器 -->
      <div
        class="wizard-container flex-1 min-w-0 lg:min-w-[400px] bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden min-h-0"
      >
        <WizardContainer
          :current-step="store.currentStep"
          :wizard-data="store.wizardData"
          :is-generating="store.isStreaming"
          :has-generated="
            !!generatedDashboard || !!store.wizardData.generatedResult
          "
          @step-change="handleStepChange"
          @data-update="handleDataUpdate"
        />
      </div>

      <!-- 右侧预览面板 - 只在第5步且有生成内容时显示 -->
      <div
        v-if="
          store.currentStep === 5 &&
          (store.showPreview ||
            generatedDashboard ||
            store.isStreaming ||
            store.streamingCode)
        "
        class="preview-container flex-1 min-w-0 lg:min-w-[400px] bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden min-h-0 flex flex-col"
      >
        <PreviewPanel
          ref="previewPanelRef"
          :dashboard-config="store.wizardData"
          :generated-html="generatedDashboard"
          :streaming-html="store.streamingCode"
          :is-streaming="store.isStreaming"
          @close="store.setShowPreview(false)"
          @abort-generation="handleAbortGeneration"
          @new-dashboard="handleNewDashboard"
          @optimize="handleOptimize"
        />
      </div>
    </div>

    <!-- 历史记录抽屉 -->
    <el-drawer v-model="store.showHistory" :size="400" direction="rtl">
      <template #header>
        <h4>历史记录</h4>
      </template>
      <HistoryPanel @load-config="handleLoadHistoryConfig" />
    </el-drawer>

    <!-- 帮助对话框 -->
    <el-dialog v-model="showHelp" title="使用帮助" width="500px">
      <div class="help-content">
        <h3>如何使用 AI 看板生成器</h3>
        <ol>
          <li>
            <strong>选择看板用途：</strong
            >根据您的需求选择数据分析、项目管理、销售监控等用途
          </li>
          <li>
            <strong>选择组件与主题：</strong>选择需要的数据组件和喜欢的主题配色
          </li>
          <li>
            <strong>选择布局样式：</strong>基于您的组件选择最合适的布局方式
          </li>
          <li>
            <strong>生成看板：</strong>AI 将根据您的配置自动生成专业的看板代码
          </li>
        </ol>
      </div>
      <template #footer>
        <el-button @click="showHelp = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus, Clock } from '@element-plus/icons-vue';
import WizardContainer from './components/WizardContainer.vue';
import PreviewPanel from './components/PreviewPanel.vue';
import HistoryPanel from './components/HistoryPanel.vue';
import { useDashboardStore } from './composables/useDashboardStore';
import { useDashboardGenerator } from './composables/useDashboardGenerator';

// 使用状态管理
const store = useDashboardStore();
const { optimizeDashboard } = useDashboardGenerator();

// 本地状态
const showHelp = ref(false);
const generatedDashboard = ref('');
const previewPanelRef = ref<InstanceType<typeof PreviewPanel>>();

// 事件处理函数
const handleStepChange = (step: number) => {
  store.setCurrentStep(step);
  // 进入第4步时不再自动显示预览，等待用户生成代码
  // 离开第4步时隐藏预览
  if (step !== 4) {
    store.setShowPreview(false);
    store.setStreamingCode('');
    store.setIsStreaming(false);
  }
};

const handleDataUpdate = (data: any) => {
  store.updateWizardData(data);
  // 更新生成的HTML代码
  if (data.generatedResult) {
    generatedDashboard.value = data.generatedResult.html;
  } else if (data.generatedResult === null) {
    // 当明确设置为 null 时，清空生成的内容
    generatedDashboard.value = '';
  }
};

const handleLoadHistoryConfig = (config: any) => {
  store.loadFromHistory(config);
  ElMessage.success('配置已加载');
};

const handleRandomSetup = () => {
  store.randomizeConfig();
  ElMessage.info('已生成随机配置');
};

const handleAbortGeneration = () => {
  store.setIsStreaming(false);
  store.setStreamingCode('');
  ElMessage.warning('已停止生成');
};

const handleNewDashboard = () => {
  // 先保存当前配置到历史记录
  if (store.isConfigComplete) {
    store.saveToHistory();
  }

  // 立即清空显示数据，避免延迟
  generatedDashboard.value = '';

  // 重置向导状态
  store.resetWizard();

  // 确保所有相关状态都被清空
  store.setStreamingCode('');
  store.setIsStreaming(false);
  store.setShowPreview(false); // 隐藏预览面板

  ElMessage.success('已重置，可以开始新的看板配置');
};

// 处理优化请求
const handleOptimize = async (data: {
  conversationId: string;
  userRequest: string;
  currentHtml: string;
}) => {
  // 调用优化功能
  await optimizeDashboard(
    data.conversationId,
    data.currentHtml,
    data.userRequest,
    // onChunk - 接收优化的代码片段
    (chunk: string) => {
      // 代码片段已经在optimizeDashboard中更新到store了
    },
    // onComplete - 优化完成
    () => {
      // 通知PreviewPanel优化完成
      if (previewPanelRef.value) {
        previewPanelRef.value.onOptimizationComplete(true, '优化完成！');
      }
      // 更新本地的generatedDashboard
      generatedDashboard.value = store.streamingCode;
    },
    // onError - 优化失败
    (error: Error) => {
      if (previewPanelRef.value) {
        previewPanelRef.value.onOptimizationComplete(
          false,
          `优化失败: ${error.message}`
        );
      }
    }
  );
};

// 监听生成结果变化
watch(
  () => store.wizardData.generatedResult,
  newResult => {
    if (newResult?.html) {
      generatedDashboard.value = newResult.html;
      console.log('Generated result updated, length:', newResult.html.length);
    } else {
      // 当结果被清空时，也要清空显示
      generatedDashboard.value = '';
      console.log('Generated result cleared');
    }
  }
);
</script>

<style scoped>
/* 确保没有额外的margin和padding导致高度溢出 */
.dashboard-generator-app {
  box-sizing: border-box;
  overflow: hidden;
}

.progress-section :deep(.el-steps) {
  margin: 0;
}

.main-content {
  box-sizing: border-box;
}

/* 响应式高度调整 */
@media (max-width: 760px) {
  .dashboard-generator-app {
    /* 移动端可能有不同的header高度 */
    height: calc(100vh - 48px - 48px); /* 减去较小的header高度 */
  }
}

/* 保留帮助对话框样式 */

.help-content h3 {
  color: #303133;
  margin-bottom: 16px;
}

.help-content ol {
  padding-left: 20px;
}

.help-content li {
  margin-bottom: 12px;
  line-height: 1.6;
  color: #606266;
}

.help-content strong {
  color: #409eff;
}

/* 响应式设计已通过Tailwind CSS类名实现 */
</style>
