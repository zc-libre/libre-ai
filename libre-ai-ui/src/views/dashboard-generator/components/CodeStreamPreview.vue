<template>
  <div class="code-stream-preview">
    <div class="preview-header">
      <div class="header-left">
        <el-icon v-if="isStreaming" class="streaming-icon">
          <Loading />
        </el-icon>
        <span class="preview-title">
          {{ isStreaming ? '正在生成代码...' : '代码预览' }}
        </span>
        <el-tag v-if="code" size="small" type="info" class="code-size">
          {{ formatSize(code.length) }}
        </el-tag>
      </div>
      <div class="header-actions">
        <el-button
          v-if="isStreaming"
          size="small"
          type="danger"
          :icon="VideoPause"
          @click="$emit('abort')"
        >
          停止生成
        </el-button>
        <el-button
          v-if="code && !isStreaming"
          size="small"
          :icon="CopyDocument"
          @click="copyCode"
        >
          复制代码
        </el-button>
        <el-button
          v-if="code && !isStreaming"
          size="small"
          type="primary"
          :icon="View"
          @click="$emit('preview')"
        >
          预览效果
        </el-button>
        <el-button
          v-if="code && !isStreaming"
          size="small"
          type="success"
          :icon="Plus"
          @click="$emit('new-dashboard')"
        >
          新建看板
        </el-button>
      </div>
    </div>

    <div ref="codeContainer" class="code-container">
      <div v-if="!code && !isStreaming" class="empty-state">
        <el-icon :size="48" color="#909399">
          <Document />
        </el-icon>
        <p>等待生成代码...</p>
      </div>

      <!-- 使用 Monaco Editor 替代原有的代码显示 -->
      <MonacoEditor
        v-else
        ref="monacoEditorRef"
        :model-value="code"
        :language="language"
        theme="vs-dark"
        :read-only="true"
        :options="editorOptions"
        height="100%"
      />
    </div>

    <!-- 进度指示器 -->
    <div v-if="isStreaming" class="streaming-progress">
      <el-progress
        :percentage="streamProgress"
        :stroke-width="2"
        :show-text="false"
        status="success"
      />
      <span class="progress-text">
        已生成 {{ formatSize(code.length) }} 代码
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import {
  Loading,
  Document,
  CopyDocument,
  View,
  VideoPause,
  Plus
} from '@element-plus/icons-vue';
import MonacoEditor from './MonacoEditor.vue';
import type { editor } from 'monaco-editor';

// Props
interface Props {
  code: string;
  isStreaming: boolean;
  language?: string;
}

const props = withDefaults(defineProps<Props>(), {
  language: 'html'
});

// Emits
const emit = defineEmits<{
  abort: [];
  preview: [];
  'new-dashboard': [];
}>();

// Refs
const codeContainer = ref<HTMLElement>();
const monacoEditorRef = ref<InstanceType<typeof MonacoEditor>>();

// Monaco Editor 配置
const editorOptions = computed<editor.IStandaloneEditorConstructionOptions>(
  () => ({
    minimap: {
      enabled: true,
      maxColumn: 80
    },
    scrollBeyondLastLine: false,
    wordWrap: 'on',
    lineNumbers: 'on',
    renderWhitespace: 'none',
    fontSize: 13,
    fontFamily: "'Consolas', 'Monaco', 'Courier New', monospace",
    tabSize: 2,
    insertSpaces: true,
    formatOnPaste: false,
    formatOnType: false,
    scrollbar: {
      vertical: 'visible',
      horizontal: 'visible',
      useShadows: false,
      verticalHasArrows: false,
      horizontalHasArrows: false,
      verticalScrollbarSize: 10,
      horizontalScrollbarSize: 10
    },
    folding: true,
    foldingStrategy: 'indentation',
    showFoldingControls: 'always',
    renderLineHighlight: 'none',
    cursorStyle: 'line',
    smoothScrolling: true,
    contextmenu: true,
    automaticLayout: true,
    quickSuggestions: false,
    suggestOnTriggerCharacters: false,
    acceptSuggestionOnEnter: 'off',
    wordBasedSuggestions: 'off',
    parameterHints: {
      enabled: false
    },
    hover: {
      enabled: true,
      delay: 300
    }
  })
);

// 初始化
onMounted(() => {
  // 初始滚动到顶部
  if (codeContainer.value) {
    codeContainer.value.scrollTop = 0;
  }
});

// 计算属性
const streamProgress = computed(() => {
  if (!props.isStreaming) return 100;
  // 根据代码长度估算进度
  const estimatedTotal = 5000;
  return Math.min(95, (props.code.length / estimatedTotal) * 100);
});

// 方法
const formatSize = (bytes: number): string => {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
};

const copyCode = async () => {
  try {
    await navigator.clipboard.writeText(props.code);
    ElMessage.success('代码已复制到剪贴板');
  } catch (error) {
    ElMessage.error('复制失败，请手动选择复制');
  }
};

// 自动滚动到底部的方法
const scrollToBottom = () => {
  nextTick(() => {
    const editor = monacoEditorRef.value?.getEditor();
    if (editor && props.isStreaming) {
      // 获取编辑器内容的总行数
      const model = editor.getModel();
      if (model) {
        const lineCount = model.getLineCount();
        // 滚动到最后一行
        editor.revealLine(lineCount, 1); // 1 表示滚动到顶部边缘
        // 设置光标位置到文档末尾（可选）
        editor.setPosition({
          lineNumber: lineCount,
          column: model.getLineMaxColumn(lineCount)
        });
      }
    }
  });
};

// 监听代码变化，在流式输出时自动滚动
watch(
  () => props.code,
  () => {
    if (props.isStreaming) {
      scrollToBottom();
    }
  },
  { flush: 'post' }
); // 确保在 DOM 更新后执行

// 监听流式状态变化
watch(
  () => props.isStreaming,
  (newValue, oldValue) => {
    // 当开始流式输出时，也滚动到底部
    if (newValue && !oldValue) {
      scrollToBottom();
    }
  }
);
</script>

<style scoped>
@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

/* 响应式设计 */
@media (width <= 768px) {
  .preview-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .header-actions {
    justify-content: flex-end;
    width: 100%;
  }

  .code-content {
    font-size: 12px;
  }
}

.code-stream-preview {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  background: #1e1e1e;
  border-radius: 8px;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #2d2d30;
  border-bottom: 1px solid #3e3e42;
}

.header-left {
  display: flex;
  gap: 8px;
  align-items: center;
}

.streaming-icon {
  animation: spin 1s linear infinite;
}

.preview-title {
  font-size: 14px;
  font-weight: 500;
  color: #ccc;
}

.code-size {
  color: #ccc;
  background: #3e3e42;
  border-color: #565656;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.code-container {
  position: relative;
  flex: 1;
  padding: 0;
  overflow: hidden;
  background: #1e1e1e;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 16px;
  color: #808080;
}

.empty-state p {
  margin-top: 16px;
  font-size: 14px;
}

/* Monaco Editor 容器样式 */
.code-container :deep(.monaco-editor-container) {
  border: none;
  border-radius: 0;
}

/* 进度条 */
.streaming-progress {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 8px 16px;
  background: #2d2d30;
  border-top: 1px solid #3e3e42;
}

.streaming-progress :deep(.el-progress) {
  flex: 1;
}

.progress-text {
  font-size: 12px;
  color: #969696;
  white-space: nowrap;
}

/* 滚动条样式 */
.code-container::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.code-container::-webkit-scrollbar-track {
  background: #1e1e1e;
}

.code-container::-webkit-scrollbar-thumb {
  background: #565656;
  border-radius: 4px;
}

.code-container::-webkit-scrollbar-thumb:hover {
  background: #707070;
}
</style>
