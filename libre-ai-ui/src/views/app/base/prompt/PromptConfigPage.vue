<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch, onBeforeUnmount } from 'vue';
import { useAppStore } from '@/views/app/store';
import {
  ElRadioGroup,
  ElRadioButton,
  ElIcon,
  ElCard,
  ElMessage,
  ElButton,
  ElMessageBox
} from 'element-plus';
import {
  Edit,
  View,
  FullScreen,
  Document,
  Check,
  EditPen
} from '@element-plus/icons-vue';
import { onBeforeRouteLeave } from 'vue-router';
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

// 本地编辑状态
const localSystemPrompt = ref('');
const localUserPromptTemplate = ref('');

// 保存状态
const isSaving = ref(false);
const lastSaveTime = ref('');
const hasUnsavedChanges = ref(false);

// 初始化本地数据
const initLocalData = () => {
  localSystemPrompt.value = appStore.info.systemPrompt || '';
  localUserPromptTemplate.value = appStore.info.userPromptTemplate || '';
  hasUnsavedChanges.value = false;
};

// 监听内容变化
const handleContentChange = () => {
  hasUnsavedChanges.value = true;
};

// 保存函数
const handleSave = async () => {
  if (!hasUnsavedChanges.value) return;

  try {
    isSaving.value = true;

    // 更新store中的数据
    appStore.info.systemPrompt = localSystemPrompt.value;
    appStore.info.userPromptTemplate = localUserPromptTemplate.value;

    // 调用保存接口
    await appStore.updateInfo();

    // 更新状态
    hasUnsavedChanges.value = false;
    updateSaveTime();
    ElMessage.success('保存成功');
    emit('update');
  } catch (error) {
    console.error('保存提示词配置失败:', error);
    ElMessage.error('保存失败，请重试');
  } finally {
    isSaving.value = false;
  }
};

// 更新保存时间
const updateSaveTime = () => {
  const now = new Date();
  lastSaveTime.value = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 快捷键保存处理
const handleKeydown = (e: KeyboardEvent) => {
  if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault();
    handleSave();
  }
};

// 页面离开前确认
const handleBeforeUnload = (e: BeforeUnloadEvent) => {
  if (hasUnsavedChanges.value) {
    e.preventDefault();
    e.returnValue = '您有未保存的更改，确定要离开吗？';
    return '您有未保存的更改，确定要离开吗？';
  }
};

// 确认离开对话框
const confirmLeave = async (): Promise<boolean> => {
  if (!hasUnsavedChanges.value) {
    return true;
  }

  try {
    await ElMessageBox.confirm(
      '您有未保存的更改，离开页面将丢失这些更改。',
      '确认离开',
      {
        confirmButtonText: '离开',
        cancelButtonText: '取消',
        type: 'warning',
        showClose: false,
        closeOnClickModal: false,
        closeOnPressEscape: false,
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            // 用户确认离开，清除未保存状态避免再次提示
            hasUnsavedChanges.value = false;
          }
          done();
        }
      }
    );
    return true;
  } catch {
    return false;
  }
};

// 处理系统提示词保存（快捷键触发）
const handleSystemPromptSave = async () => {
  await handleSave();
};

// 处理用户提示词模板保存（快捷键触发）
const handleUserPromptSave = async () => {
  await handleSave();
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

// 路由离开守卫
onBeforeRouteLeave(async (to, from, next) => {
  const canLeave = await confirmLeave();
  if (canLeave) {
    next();
  } else {
    next(false);
  }
});

// 生命周期钩子
onMounted(() => {
  // 初始化本地数据
  initLocalData();

  // 添加事件监听器
  document.addEventListener('fullscreenchange', handleFullscreenChange);
  document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.addEventListener('mozfullscreenchange', handleFullscreenChange);
  document.addEventListener('MSFullscreenChange', handleFullscreenChange);
  document.addEventListener('keydown', handleKeydown);
  window.addEventListener('beforeunload', handleBeforeUnload);
});

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange);
  document.removeEventListener(
    'webkitfullscreenchange',
    handleFullscreenChange
  );
  document.removeEventListener('mozfullscreenchange', handleFullscreenChange);
  document.removeEventListener('MSFullscreenChange', handleFullscreenChange);
  document.removeEventListener('keydown', handleKeydown);
  window.removeEventListener('beforeunload', handleBeforeUnload);
});

// 组件卸载前清理
onBeforeUnmount(() => {
  // 清理事件监听器
  window.removeEventListener('beforeunload', handleBeforeUnload);
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

// 暴露方法给父组件
defineExpose({
  hasUnsavedChanges: () => hasUnsavedChanges.value,
  handleSave
});
</script>

<template>
  <div class="prompt-config-page">
    <div class="page-container">
      <!-- 页面标题和操作区 -->
      <div class="page-header">
        <div class="header-left">
          <h2 class="page-title">提示词配置</h2>
          <p class="page-description">
            配置AI助手的系统提示词和用户提示词模板，定义AI的角色、能力和交互方式
          </p>
        </div>
        <div class="header-actions">
          <!-- 保存状态指示 -->
          <div v-if="hasUnsavedChanges" class="save-status">
            <el-icon color="#e6a23c"><EditPen /></el-icon>
            <span>有未保存的更改</span>
          </div>
          <div v-else-if="lastSaveTime" class="save-status">
            <el-icon color="#67c23a"><Check /></el-icon>
            <span>已保存 {{ lastSaveTime }}</span>
          </div>

          <!-- 保存按钮 -->
          <el-button
            type="primary"
            :loading="isSaving"
            :disabled="!hasUnsavedChanges"
            @click="handleSave"
          >
            <el-icon><Document /></el-icon>
            保存配置
          </el-button>
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
              v-model="localSystemPrompt"
              :mode="
                systemPromptMode === 'fullscreen' ? 'split' : systemPromptMode
              "
              :height="'400px'"
              :placeholder="'例如：你是一位专业的客服助手，需要礼貌、耐心地回答用户的问题...'"
              :toolbars-exclude="['github', 'mermaid', 'katex', 'htmlPreview']"
              @save="handleSystemPromptSave"
              @change="handleContentChange"
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
              v-model="localUserPromptTemplate"
              :mode="userPromptMode === 'fullscreen' ? 'split' : userPromptMode"
              :height="'300px'"
              :placeholder="'例如：请基于以下问题提供详细解答：{{question}}'"
              :toolbars-exclude="['github', 'mermaid', 'katex', 'htmlPreview']"
              @save="handleUserPromptSave"
              @change="handleContentChange"
            />
          </div>
        </ElCard>
      </div>
      <!-- 结束 prompt-config-panels -->
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;

  .header-left {
    flex: 1;

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
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 16px;
    flex-shrink: 0;

    .save-status {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 14px;
      color: #666;
      white-space: nowrap;
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

  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;

    .header-actions {
      justify-content: flex-end;
    }
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
