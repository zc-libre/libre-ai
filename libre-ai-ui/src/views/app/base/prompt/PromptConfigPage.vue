<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useAppStore } from '@/views/app/store';
import { ElRadioGroup, ElRadioButton, ElIcon, ElCard, ElMessage } from 'element-plus';
import { Edit, View, FullScreen } from '@element-plus/icons-vue';
import MarkdownEditor from '@/components/MarkdownEditor/index.vue';

const emit = defineEmits(['update']);
const appStore = useAppStore();

// 编辑模式状态 - 默认为编辑模式
const systemPromptMode = ref<'edit' | 'preview' | 'fullscreen'>('edit');
const userPromptMode = ref<'edit' | 'preview' | 'fullscreen'>('edit');

// 保存进入全屏前的模式状态
const previousSystemMode = ref<'edit' | 'preview'>('edit');
const previousUserMode = ref<'edit' | 'preview'>('edit');

// MarkdownEditor 实例引用
const systemEditorRef = ref<any>(null);
const userEditorRef = ref<any>(null);

// 保存状态
const isSaving = ref(false);

async function onUpdate() {
  try {
    isSaving.value = true;
    await appStore.updateInfo();
    ElMessage.success('保存成功');
    emit('update');
  } catch (error) {
    console.error('保存提示词配置失败:', error);
    ElMessage.error('保存失败，请重试');
  } finally {
    isSaving.value = false;
  }
}

// 处理系统提示词保存
const handleSystemPromptSave = async () => {
  await onUpdate();
};

// 处理用户提示词模板保存
const handleUserPromptSave = async () => {
  await onUpdate();
};

// 监听模式变化，触发编辑器全屏
const handleSystemModeChange = async () => {
  if (systemPromptMode.value === 'fullscreen') {
    triggerEditorFullscreen('system');
  }
};

const handleUserModeChange = async () => {
  if (userPromptMode.value === 'fullscreen') {
    triggerEditorFullscreen('user');
  }
};

// 触发编辑器内置全屏方法
const triggerEditorFullscreen = (type: 'system' | 'user') => {
  const compRef = type === 'system' ? systemEditorRef : userEditorRef;

  const tryToggle = () => {
    if (compRef.value?.toggleFullscreen) {
      compRef.value.toggleFullscreen(true);
      return true;
    }
    if (compRef.value?.togglePageFullscreen) {
      compRef.value.togglePageFullscreen(true);
      return true;
    }
    return false;
  };

  if (tryToggle()) return;
};

// 全屏状态变化监听器
const handleFullscreenChange = () => {
  if (!document.fullscreenElement) {
    if (systemPromptMode.value === 'fullscreen') {
      systemPromptMode.value = previousSystemMode.value;
    }
    if (userPromptMode.value === 'fullscreen') {
      userPromptMode.value = previousUserMode.value;
    }
  }
};

// 生命周期钩子
onMounted(() => {
  document.addEventListener('fullscreenchange', handleFullscreenChange);
  document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.addEventListener('mozfullscreenchange', handleFullscreenChange);
  document.addEventListener('MSFullscreenChange', handleFullscreenChange);
});

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange);
  document.removeEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.removeEventListener('mozfullscreenchange', handleFullscreenChange);
  document.removeEventListener('MSFullscreenChange', handleFullscreenChange);
});

// 监听模式变化，保存之前的状态
watch(systemPromptMode, (newMode, oldMode) => {
  if (newMode === 'fullscreen' && oldMode && oldMode !== 'fullscreen') {
    previousSystemMode.value = oldMode;
  }
});

watch(userPromptMode, (newMode, oldMode) => {
  if (newMode === 'fullscreen' && oldMode && oldMode !== 'fullscreen') {
    previousUserMode.value = oldMode;
  }
});
</script>

<template>
  <div class="prompt-config-page">
    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2 class="page-title">提示词配置</h2>
        <p class="page-description">
          配置AI助手的系统提示词和用户提示词模板，定义AI的角色、能力和交互方式
        </p>
        <div v-if="isSaving" class="saving-indicator">
          <el-icon class="animate-spin" :size="14">
            <FullScreen />
          </el-icon>
          <span>保存中...</span>
        </div>
      </div>
      <!-- 提示词配置面板 -->
      <div class="prompt-config-panels">

      <!-- 系统提示词配置卡片 -->
      <ElCard class="config-card system-prompt-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <h3 class="card-title">系统提示词</h3>
              <p class="card-subtitle">
                定义AI助手的角色、能力和行为规范，这将作为每次对话的系统级指令
              </p>
            </div>
            <div class="header-right">
              <ElRadioGroup
                v-model="systemPromptMode"
                size="small"
                @change="handleSystemModeChange"
              >
                <ElRadioButton value="preview">
                  <ElIcon><View /></ElIcon>
                  预览
                </ElRadioButton>
                <ElRadioButton value="edit">
                  <ElIcon><Edit /></ElIcon>
                  编辑
                </ElRadioButton>
                <ElRadioButton value="fullscreen">
                  <ElIcon><FullScreen /></ElIcon>
                  全屏
                </ElRadioButton>
              </ElRadioGroup>
            </div>
          </div>
        </template>

        <div class="editor-container">
          <MarkdownEditor
            ref="systemEditorRef"
            v-model="appStore.info.systemPrompt"
            :mode="systemPromptMode === 'fullscreen' ? 'split' : systemPromptMode"
            :height="'400px'"
            :placeholder="'例如：你是一位专业的客服助手，需要礼貌、耐心地回答用户的问题...'"
            :toolbars-exclude="['github', 'mermaid', 'katex', 'htmlPreview']"
            @save="handleSystemPromptSave"
            @change="onUpdate"
          />
        </div>
      </ElCard>

      <!-- 用户提示词模板配置卡片 -->
      <ElCard class="config-card user-prompt-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <h3 class="card-title">用户提示词模板（可选）</h3>
              <p class="card-subtitle">
                定义用户输入的格式化模板，使用
                <code class="inline-code" v-html="'{{question}}'" />
                作为用户输入的占位符。如果不配置，用户输入将直接发送给AI。
              </p>
            </div>
            <div class="header-right">
              <ElRadioGroup
                v-model="userPromptMode"
                size="small"
                @change="handleUserModeChange"
              >
                <ElRadioButton value="preview">
                  <ElIcon><View /></ElIcon>
                  预览
                </ElRadioButton>
                <ElRadioButton value="edit">
                  <ElIcon><Edit /></ElIcon>
                  编辑
                </ElRadioButton>
                <ElRadioButton value="fullscreen">
                  <ElIcon><FullScreen /></ElIcon>
                  全屏
                </ElRadioButton>
              </ElRadioGroup>
            </div>
          </div>
        </template>

        <div class="editor-container">
          <MarkdownEditor
            ref="userEditorRef"
            v-model="appStore.info.userPromptTemplate"
            :mode="userPromptMode === 'fullscreen' ? 'split' : userPromptMode"
            :height="'300px'"
            :placeholder="'例如：请基于以下问题提供详细解答：{{question}}'"
            :toolbars-exclude="['github', 'mermaid', 'katex', 'htmlPreview']"
            @save="handleUserPromptSave"
            @change="onUpdate"
          />
        </div>
      </ElCard>

      </div> <!-- 结束 prompt-config-panels -->
    </div>
  </div>
</template>

<style lang="scss" scoped>
.prompt-config-page {
  height: 100%;
  overflow-y: auto;
  background: #f5f7fa;
}

.page-container {
  width: 100%;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 8px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
  }

  .page-description {
    font-size: 14px;
    color: #606266;
    margin: 0;
    line-height: 1.6;
  }

  .saving-indicator {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    margin-top: 12px;
    font-size: 13px;
    color: #409eff;

    .animate-spin {
      animation: spin 1s linear infinite;
    }
  }
}

.config-card {
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;

  :deep(.el-card__header) {
    background: #fafbfc;
    border-bottom: 1px solid #e4e7ed;
    padding: 20px 24px;
  }

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.prompt-config-panels {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;

  .system-prompt-card,
  .user-prompt-card {
    margin: 0;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;

  .header-left {
    flex: 1;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 6px 0;
    }

    .card-subtitle {
      font-size: 13px;
      color: #606266;
      margin: 0;
      line-height: 1.6;

      .inline-code {
        background: #f0f2f5;
        color: #e6a23c;
        padding: 2px 6px;
        border-radius: 3px;
        font-family: 'Monaco', 'Menlo', monospace;
        font-size: 12px;
      }
    }
  }

  .header-right {
    flex-shrink: 0;
  }
}

.editor-container {
  :deep(.markdown-editor-wrapper) {
    .md-editor {
      border: 1px solid #e4e7ed;
      border-radius: 8px;
    }
  }
}

.example-content {
  .example-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 12px;
    gap: 12px;

    .example-label {
      font-weight: 600;
      color: #303133;
      min-width: 140px;
      flex-shrink: 0;
    }

    .example-value {
      color: #606266;
      font-family: 'Monaco', 'Menlo', monospace;
      background: #f0f9ff;
      padding: 6px 12px;
      border-radius: 6px;
      border: 1px solid #b3d8ff;
      flex: 1;
    }
  }

  .example-result {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px dashed #e4e7ed;

    .result-label {
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
    }

    .result-content {
      background: #f8f9fa;
      border: 1px solid #e9ecef;
      border-radius: 8px;
      padding: 16px;

      .result-message {
        font-family: 'Monaco', 'Menlo', monospace;
        font-size: 13px;
        line-height: 1.6;
        margin-bottom: 8px;

        &:last-child {
          margin-bottom: 0;
        }

        &.system {
          color: #e6a23c;
        }

        &.user {
          color: #409eff;
        }
      }
    }
  }
}

// 动画
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .prompt-config-panels {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .card-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;

    .header-right {
      align-self: flex-end;
    }
  }

  .example-item {
    flex-direction: column;
    gap: 6px;

    .example-label {
      min-width: auto;
    }
  }
}
</style>
