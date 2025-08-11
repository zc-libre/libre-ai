<template>
  <div ref="previewPanelRef" class="preview-panel h-full flex flex-col">
    <el-card
      class="h-full flex flex-col"
      body-class="h-full"
      :class="{ 'fullscreen-card': isFullscreen }"
    >
      <template #header>
        <div
          class="preview-header flex flex-col sm:flex-row gap-3 sm:gap-0 sm:justify-between sm:items-center"
        >
          <span
            class="preview-title text-lg font-semibold text-gray-900 dark:text-white"
          >
            {{ viewMode === 'code' ? '代码预览' : '看板预览' }}
          </span>
          <div
            class="preview-controls flex flex-col sm:flex-row gap-3 sm:gap-4 items-stretch sm:items-center"
          >
            <!-- 视图切换按钮组（只在代码生成完成后显示） -->
            <el-button-group
              v-if="generatedHtml && !isStreaming"
              class="view-switcher"
            >
              <el-button
                :type="viewMode === 'code' ? 'primary' : 'default'"
                :icon="Document"
                size="small"
                @click="viewMode = 'code'"
              >
                代码
              </el-button>
              <el-button
                :type="viewMode === 'preview' ? 'primary' : 'default'"
                :icon="View"
                size="small"
                @click="viewMode = 'preview'"
              >
                预览
              </el-button>
            </el-button-group>

            <!-- 设备预览模式（只在预览模式下显示） -->
            <el-button-group
              v-if="viewMode === 'preview'"
              class="flex-1 sm:flex-none"
            >
              <el-button
                :type="previewMode === 'desktop' ? 'primary' : 'default'"
                :icon="Monitor"
                size="small"
                class="flex-1 sm:flex-none"
                @click="previewMode = 'desktop'"
              >
                <span class="hidden sm:inline">桌面</span>
                <span class="sm:hidden">桌面端</span>
              </el-button>
              <el-button
                :type="previewMode === 'tablet' ? 'primary' : 'default'"
                :icon="Grid"
                size="small"
                class="flex-1 sm:flex-none"
                @click="previewMode = 'tablet'"
              >
                平板
              </el-button>
              <el-button
                :type="previewMode === 'mobile' ? 'primary' : 'default'"
                :icon="Cellphone"
                size="small"
                class="flex-1 sm:flex-none"
                @click="previewMode = 'mobile'"
              >
                手机
              </el-button>
            </el-button-group>
            <el-tooltip
              :content="isFullscreen ? '退出全屏 (ESC)' : '全屏预览 (F11)'"
              placement="bottom"
            >
              <el-button
                :icon="isFullscreen ? Close : FullScreen"
                size="small"
                @click="toggleFullscreen"
              />
            </el-tooltip>
            <el-button
              v-if="!isFullscreen"
              :icon="Close"
              size="small"
              @click="$emit('close')"
            />
          </div>
        </div>
      </template>
      <div class="preview-content flex-1 h-full flex flex-col min-h-0">
        <!-- 代码视图 -->
        <div v-if="viewMode === 'code' || isStreaming" class="code-view h-full">
          <CodeStreamPreview
            :code="streamingHtml || generatedHtml || ''"
            :is-streaming="isStreaming"
            @abort="$emit('abort-generation')"
            @preview="handleSwitchToPreview"
            @new-dashboard="$emit('new-dashboard')"
          />
        </div>

        <!-- 预览视图 -->
        <div
          v-else
          class="preview-container flex justify-center flex-1 min-h-0 overflow-auto p-4"
          :class="`preview-${previewMode}`"
        >
          <div
            class="preview-frame w-full max-w-full bg-white dark:bg-gray-900 rounded-lg shadow-lg overflow-hidden flex flex-col"
            :class="{
              'max-w-7xl': previewMode === 'desktop',
              'max-w-2xl': previewMode === 'tablet',
              'max-w-sm': previewMode === 'mobile'
            }"
          >
            <div
              class="frame-header bg-gray-100 dark:bg-gray-800 px-4 py-2 flex items-center gap-3"
            >
              <div class="frame-controls flex gap-2">
                <div class="control-dot w-3 h-3 rounded-full bg-red-500" />
                <div class="control-dot w-3 h-3 rounded-full bg-yellow-500" />
                <div class="control-dot w-3 h-3 rounded-full bg-green-500" />
              </div>
              <div
                class="frame-title text-sm text-gray-600 dark:text-gray-300 truncate"
              >
                {{ dashboardConfig.purposeText || '看板预览' }}
              </div>
            </div>
            <div
              class="frame-content bg-gray-50 dark:bg-gray-900 flex flex-col flex-1 min-h-0 overflow-auto"
            >
              <DashboardPreview
                :config="dashboardConfig"
                :generated-html="generatedHtml"
              />
            </div>
          </div>
        </div>

        <!-- 底部状态栏（只在预览模式显示） -->
        <div
          v-if="viewMode === 'preview'"
          class="preview-footer flex-shrink-0 px-4 py-3 border-t border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 flex flex-col sm:flex-row gap-3 sm:gap-0 sm:justify-between sm:items-center"
        >
          <div class="preview-info flex items-center gap-2">
            <el-tag type="info" size="small">
              {{
                previewMode === 'desktop'
                  ? '1920x1080'
                  : previewMode === 'tablet'
                    ? '768x1024'
                    : '375x667'
              }}
            </el-tag>
            <span class="info-text text-sm text-gray-600 dark:text-gray-300"
              >当前预览尺寸</span
            >
          </div>
          <div class="preview-actions flex gap-2">
            <el-button :icon="Refresh" size="small" @click="refreshPreview">
              刷新预览
            </el-button>
            <el-button
              v-if="generatedHtml"
              type="warning"
              :icon="Edit"
              size="small"
              @click="toggleOptimization"
            >
              {{ showOptimization ? '关闭优化' : '迭代优化' }}
            </el-button>
            <el-button
              type="success"
              :icon="Plus"
              size="small"
              @click="createNewDashboard"
            >
              新建看板
            </el-button>
            <el-button
              type="primary"
              :icon="Download"
              size="small"
              @click="exportDashboard"
            >
              导出看板
            </el-button>
          </div>
        </div>

        <!-- 优化对话区域 -->
        <div
          v-if="showOptimization && generatedHtml"
          class="optimization-panel border-t border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800"
        >
          <div class="optimization-container p-4">
            <div class="optimization-header mb-3 flex justify-between items-center">
              <div class="flex items-center gap-2">
                <el-icon :size="20" color="#F56C6C">
                  <ChatDotRound />
                </el-icon>
                <span class="text-sm font-semibold text-gray-700 dark:text-gray-300">
                  对话式优化
                </span>
              </div>
              <el-tag v-if="conversationId" type="info" size="small">
                会话ID: {{ conversationId.substring(0, 8) }}...
              </el-tag>
            </div>

            <!-- 对话历史 -->
            <div
              v-if="chatHistory.length > 0"
              class="chat-history mb-3 max-h-48 overflow-y-auto border rounded-lg p-3 bg-gray-50 dark:bg-gray-900"
            >
              <div
                v-for="(msg, index) in chatHistory"
                :key="index"
                class="chat-message mb-3 last:mb-0"
              >
                <div class="user-message mb-2">
                  <span class="text-xs text-gray-500 dark:text-gray-400">您:</span>
                  <div class="mt-1 p-2 rounded bg-blue-100 dark:bg-blue-900 text-sm">
                    {{ msg.user }}
                  </div>
                </div>
                <div class="ai-message">
                  <span class="text-xs text-gray-500 dark:text-gray-400">AI:</span>
                  <div class="mt-1 p-2 rounded bg-gray-100 dark:bg-gray-800 text-sm">
                    {{ msg.ai }}
                  </div>
                </div>
              </div>
            </div>

            <!-- 输入区域 -->
            <div class="chat-input flex gap-2">
              <el-input
                v-model="optimizationRequest"
                type="textarea"
                :rows="2"
                placeholder="描述您想要的修改，例如：调整配色为深色主题、增大字体、调整布局..."
                :disabled="isOptimizing"
                @keyup.enter.ctrl="sendOptimization"
              />
              <el-button
                type="primary"
                :loading="isOptimizing"
                :disabled="!optimizationRequest.trim()"
                @click="sendOptimization"
              >
                {{ isOptimizing ? '优化中' : '发送' }}
              </el-button>
            </div>

            <!-- 快捷建议 -->
            <div class="suggestions mt-3 flex flex-wrap gap-2">
              <el-tag
                v-for="suggestion in optimizationSuggestions"
                :key="suggestion"
                class="cursor-pointer"
                @click="applyquickSuggestion(suggestion)"
              >
                {{ suggestion }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import {
  Document,
  View,
  Monitor,
  Grid,
  Cellphone,
  FullScreen,
  Close,
  Refresh,
  Download,
  Plus,
  Edit,
  ChatDotRound
} from '@element-plus/icons-vue';
import DashboardPreview from './DashboardPreview.vue';
import CodeStreamPreview from './CodeStreamPreview.vue';

// Props
interface Props {
  dashboardConfig: any;
  generatedHtml?: string;
  streamingHtml?: string; // 流式生成的HTML
  isStreaming?: boolean; // 是否正在流式生成
}

const props = defineProps<Props>();

// Emits
const emit = defineEmits<{
  close: [];
  'abort-generation': [];
  'new-dashboard': [];
  optimize: [data: { conversationId: string; userRequest: string; currentHtml: string }];
}>();

// 状态
const viewMode = ref<'code' | 'preview'>('code'); // 默认显示代码视图
const previewMode = ref('desktop');
const isFullscreen = ref(false);
const previewPanelRef = ref<HTMLElement>();

// 优化相关状态
const showOptimization = ref(false);
const optimizationRequest = ref('');
const isOptimizing = ref(false);
const chatHistory = ref<Array<{ user: string; ai: string }>>([]);
const conversationId = ref('');

// 优化建议
const optimizationSuggestions = [
  '调整为深色主题',
  '增大字体大小',
  '优化移动端适配',
  '添加动画效果',
  '调整间距和布局'
];

// 监听流式生成状态变化
watch(
  () => props.isStreaming,
  newVal => {
    if (newVal) {
      // 开始流式生成时，自动切换到代码视图
      viewMode.value = 'code';
    }
  }
);

// 监听生成完成
watch(
  () => props.generatedHtml,
  newVal => {
    if (newVal && !props.isStreaming) {
      // 生成完成后，自动切换到预览视图
      setTimeout(() => {
        viewMode.value = 'preview';
      }, 500); // 短暂延迟，让用户看到生成完成
    }
  }
);

// 方法
const handleSwitchToPreview = () => {
  viewMode.value = 'preview';
};

const refreshPreview = () => {
  ElMessage.success('预览已刷新');
};

const exportDashboard = () => {
  ElMessage.info('导出功能开发中...');
};

const createNewDashboard = () => {
  // 重置视图模式到代码视图，以便用户看到空的编辑器
  viewMode.value = 'code';
  ElMessage.success('正在为您准备新看板配置界面...');
  emit('new-dashboard');
};

// 优化相关方法
const toggleOptimization = () => {
  showOptimization.value = !showOptimization.value;
  if (showOptimization.value && !conversationId.value) {
    // 生成新的会话ID
    conversationId.value = generateUUID();
  }
};

const applyquickSuggestion = (suggestion: string) => {
  optimizationRequest.value = suggestion;
};

const sendOptimization = async () => {
  if (!optimizationRequest.value.trim()) {
    ElMessage.warning('请输入优化需求');
    return;
  }

  // 触发优化事件，由父组件处理
  emit('optimize', {
    conversationId: conversationId.value,
    userRequest: optimizationRequest.value,
    currentHtml: props.generatedHtml
  });

  // 添加到对话历史
  chatHistory.value.push({
    user: optimizationRequest.value,
    ai: '正在优化中...'
  });

  // 清空输入
  optimizationRequest.value = '';
  isOptimizing.value = true;
};

// 生成UUID
const generateUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
};

// 接收优化完成事件（由父组件调用）
const onOptimizationComplete = (success: boolean, message?: string) => {
  isOptimizing.value = false;
  if (chatHistory.value.length > 0) {
    chatHistory.value[chatHistory.value.length - 1].ai = message || (
      success ? '优化完成！' : '优化失败，请重试'
    );
  }
};

// 暴露方法供父组件调用
defineExpose({
  onOptimizationComplete
});

// 保存进入全屏前的预览模式
let previousPreviewMode = 'desktop';

// 全屏功能
const toggleFullscreen = async () => {
  if (!document.fullscreenElement) {
    try {
      if (previewPanelRef.value) {
        // 保存当前预览模式
        previousPreviewMode = previewMode.value;
        // 全屏时切换到桌面模式
        previewMode.value = 'desktop';

        await previewPanelRef.value.requestFullscreen();
        isFullscreen.value = true;
        ElMessage.success('已进入全屏模式，按 ESC 退出');
      }
    } catch (err) {
      console.error('无法进入全屏模式:', err);
      ElMessage.error('全屏模式不可用');
    }
  } else {
    try {
      await document.exitFullscreen();
      isFullscreen.value = false;
      // 恢复之前的预览模式
      previewMode.value = previousPreviewMode;
    } catch (err) {
      console.error('退出全屏失败:', err);
    }
  }
};

// 监听全屏变化事件
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement;
  // 如果通过 ESC 或其他方式退出全屏，恢复之前的预览模式
  if (!isFullscreen.value && previousPreviewMode) {
    previewMode.value = previousPreviewMode;
  }
};

// 监听键盘快捷键
const handleKeyPress = (event: KeyboardEvent) => {
  // ESC 键退出全屏
  if (event.key === 'Escape' && isFullscreen.value) {
    isFullscreen.value = false;
  }
  // F11 或 Cmd/Ctrl + Shift + F 切换全屏
  if (
    event.key === 'F11' ||
    (event.key === 'f' && (event.metaKey || event.ctrlKey) && event.shiftKey)
  ) {
    event.preventDefault();
    toggleFullscreen();
  }
};

// 生命周期
onMounted(() => {
  document.addEventListener('fullscreenchange', handleFullscreenChange);
  document.addEventListener('keydown', handleKeyPress);
});

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange);
  document.removeEventListener('keydown', handleKeyPress);
  // 确保退出全屏
  if (document.fullscreenElement) {
    document.exitFullscreen();
  }
});
</script>

<style scoped>
/* 响应式设计已通过Tailwind CSS类名实现 */

/* 全屏样式 */
.preview-panel:fullscreen {
  background: white;
  padding: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
}

.preview-panel:fullscreen .fullscreen-card {
  border-radius: 0;
  height: 100vh;
  max-height: 100vh;
}

.preview-panel:fullscreen .el-card__header {
  border-radius: 0;
}

.preview-panel:fullscreen .el-card__body {
  height: calc(100vh - 60px);
  max-height: calc(100vh - 60px);
  padding: 0;
}

.preview-panel:fullscreen .preview-content {
  height: 100%;
  padding: 20px;
}

.preview-panel:fullscreen .frame-content {
  height: calc(100vh - 200px) !important;
}

/* 代码视图样式 */
.code-view {
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

/* Webkit 浏览器全屏样式 */
.preview-panel:-webkit-full-screen {
  background: white;
  padding: 0;
  width: 100vw;
  height: 100vh;
}

.preview-panel:-webkit-full-screen .fullscreen-card {
  border-radius: 0;
  height: 100vh;
}

/* Mozilla 浏览器全屏样式 */
.preview-panel:-moz-full-screen {
  background: white;
  padding: 0;
  width: 100vw;
  height: 100vh;
}

.preview-panel:-moz-full-screen .fullscreen-card {
  border-radius: 0;
  height: 100vh;
}

/* IE/Edge 浏览器全屏样式 */
.preview-panel:-ms-fullscreen {
  background: white;
  padding: 0;
  width: 100vw;
  height: 100vh;
}

.preview-panel:-ms-fullscreen .fullscreen-card {
  border-radius: 0;
  height: 100vh;
}
</style>
