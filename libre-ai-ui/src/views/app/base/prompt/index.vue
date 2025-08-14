<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch, onBeforeUnmount } from 'vue';
import { useAppStore } from '@/views/app/store';
import { ElDivider, ElRadioGroup, ElRadioButton, ElIcon, ElMessage, ElMessageBox } from 'element-plus';
import { Edit, View, FullScreen } from '@element-plus/icons-vue';
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
    // 需同步触发，满足浏览器全屏的用户手势限制
    triggerEditorFullscreen('system');
  }
};

const handleUserModeChange = async () => {
  if (userPromptMode.value === 'fullscreen') {
    // 需同步触发，满足浏览器全屏的用户手势限制
    triggerEditorFullscreen('user');
  }
};

// 触发编辑器内置全屏方法（优先使用内置 API）
const triggerEditorFullscreen = (type: 'system' | 'user') => {
  const compRef = type === 'system' ? systemEditorRef : userEditorRef;

  const tryToggle = () => {
    // 优先触发“屏幕全屏”，与工具栏“全屏”按钮对齐
    if (compRef.value?.toggleFullscreen) {
      compRef.value.toggleFullscreen(true);
      return true;
    }
    // 其次尝试“页面全屏”
    if (compRef.value?.togglePageFullscreen) {
      compRef.value.togglePageFullscreen(true);
      return true;
    }
    return false;
  };

  // 立即尝试
  if (tryToggle()) return;
};

// 全屏状态变化监听器
const handleFullscreenChange = () => {
  // 检查是否退出了全屏
  if (!document.fullscreenElement) {
    // 如果系统提示词编辑器处于全屏模式，恢复之前的模式
    if (systemPromptMode.value === 'fullscreen') {
      systemPromptMode.value = previousSystemMode.value;
    }
    // 如果用户提示词编辑器处于全屏模式，恢复之前的模式
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

  // 监听全屏状态变化
  document.addEventListener('fullscreenchange', handleFullscreenChange);
  document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.addEventListener('mozfullscreenchange', handleFullscreenChange);
  document.addEventListener('MSFullscreenChange', handleFullscreenChange);
  document.addEventListener('keydown', handleKeydown);
  window.addEventListener('beforeunload', handleBeforeUnload);
});

onUnmounted(() => {
  // 清理事件监听器
  document.removeEventListener('fullscreenchange', handleFullscreenChange);
  document.removeEventListener('webkitfullscreenchange', handleFullscreenChange);
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

// 监听系统提示词模式变化，保存之前的状态
watch(systemPromptMode, (newMode, oldMode) => {
  if (newMode === 'fullscreen' && oldMode && oldMode !== 'fullscreen') {
    previousSystemMode.value = oldMode;
  }
});

// 监听用户提示词模式变化，保存之前的状态
watch(userPromptMode, (newMode, oldMode) => {
  if (newMode === 'fullscreen' && oldMode && oldMode !== 'fullscreen') {
    previousUserMode.value = oldMode;
  }
});
</script>

<template>
  <div class="h-full flex flex-col gap-2 overflow-y-auto">
    <!-- 系统提示词部分 -->
    <div class="prompt-section">
      <div class="section-header">
        <div class="header-left">
          <div class="text-md font-bold">系统提示词</div>
          <div class="text-xs text-gray-500 mt-1">
            定义AI助手的角色、能力和行为规范，这将作为每次对话的系统级指令
          </div>
        </div>
        <div class="header-right">
          <ElRadioGroup
            v-model="systemPromptMode"
            size="small"
            @change="handleSystemModeChange"
          >
            <ElRadioButton value="preview">
              <ElIcon>
                <View />
              </ElIcon>
              预览
            </ElRadioButton>
            <ElRadioButton value="edit">
              <ElIcon>
                <Edit />
              </ElIcon>
              编辑
            </ElRadioButton>
            <ElRadioButton value="fullscreen">
              <ElIcon>
                <FullScreen />
              </ElIcon>
              全屏
            </ElRadioButton>
          </ElRadioGroup>
        </div>
      </div>

      <!-- 系统提示词编辑器 -->
      <div class="editor-wrapper system-editor">
        <MarkdownEditor
          ref="systemEditorRef"
          v-model="localSystemPrompt"
          :mode="systemPromptMode === 'fullscreen' ? 'split' : systemPromptMode"
          :height="'300px'"
          :placeholder="'例如：你是一位专业的客服助手，需要礼貌、耐心地回答用户的问题...'"
          :toolbars-exclude="['github', 'mermaid', 'katex', 'htmlPreview']"
          @save="handleSystemPromptSave"
          @change="handleContentChange"
        />
      </div>
    </div>

    <ElDivider />

    <!-- 用户提示词模板部分 -->
    <div class="prompt-section">
      <div class="section-header">
        <div class="header-left">
          <div class="text-md font-bold">用户提示词模板（可选）</div>
          <div class="text-xs text-gray-500 mt-1">
            定义用户输入的格式化模板，使用
            <code
              class="px-1 py-0.5 bg-gray-200 dark:bg-gray-700 rounded"
              v-html="'{{question}}'"
            />
            作为用户输入的占位符。 如果不配置，用户输入将直接发送给AI。
          </div>
        </div>
        <div class="header-right">
          <ElRadioGroup
            v-model="userPromptMode"
            size="small"
            @change="handleUserModeChange"
          >
            <ElRadioButton value="preview">
              <ElIcon>
                <View />
              </ElIcon>
              预览
            </ElRadioButton>
            <ElRadioButton value="edit">
              <ElIcon>
                <Edit />
              </ElIcon>
              编辑
            </ElRadioButton>
            <ElRadioButton value="fullscreen">
              <ElIcon>
                <FullScreen />
              </ElIcon>
              全屏
            </ElRadioButton>
          </ElRadioGroup>
        </div>
      </div>

      <!-- 用户提示词模板编辑器 -->
      <div class="editor-wrapper user-editor">
        <MarkdownEditor
          ref="userEditorRef"
          v-model="localUserPromptTemplate"
          :mode="userPromptMode === 'fullscreen' ? 'split' : userPromptMode"
          :height="'200px'"
          :placeholder="'例如：请基于以下问题提供详细解答：{{question}}'"
          :toolbars-exclude="['github', 'mermaid', 'katex', 'htmlPreview']"
          @save="handleUserPromptSave"
          @change="handleContentChange"
        />
      </div>
    </div>

    <!-- 示例说明 -->
    <div class="p-4">
      <div class="example-section">
        <div class="text-sm font-semibold mb-2">使用示例</div>
        <div class="example-card">
          <div class="example-item">
            <strong>系统提示词：</strong>
            <span class="example-text">"你是一位Python编程专家"</span>
          </div>
          <div class="example-item">
            <strong>用户提示词模板：</strong>
            <span
              class="example-text"
              v-html="'请用Python实现以下功能: {{question}}'"
            />
          </div>
          <div class="example-item">
            <strong>用户输入：</strong>
            <span class="example-text">"快速排序算法"</span>
          </div>
          <div class="example-result">
            <strong>最终发送给AI：</strong>
            <div class="result-content">
              <div>系统消息："你是一位Python编程专家"</div>
              <div>用户消息："请用Python实现以下功能：快速排序算法"</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.prompt-section {
  padding: 16px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;

    .header-left {
      flex: 1;
    }

    .header-right {
      margin-left: 16px;
    }
  }
}

.editor-wrapper {
  border-radius: 4px;
  overflow: hidden;

  :deep(.markdown-editor-wrapper) {
    .md-editor {
      border: 1px solid var(--el-border-color);
      border-radius: 4px;
    }
  }
}

.example-section {
  .example-card {
    background: #f0f9ff;
    border: 1px solid #b3d8ff;
    border-radius: 6px;
    padding: 12px;
    font-size: 13px;

    .example-item {
      margin-bottom: 8px;

      strong {
        color: #303133;
        margin-right: 4px;
      }

      .example-text {
        color: #606266;
        font-family: 'Monaco', 'Menlo', monospace;
        background: rgba(255, 255, 255, 0.6);
        padding: 2px 6px;
        border-radius: 3px;
      }
    }

    .example-result {
      margin-top: 12px;
      padding-top: 12px;
      border-top: 1px dashed #b3d8ff;

      .result-content {
        margin-top: 6px;
        padding-left: 16px;
        color: #606266;
        line-height: 1.8;
        font-family: 'Monaco', 'Menlo', monospace;
        font-size: 12px;
      }
    }
  }
}

// 暗色模式支持
:global(.dark) {
  .example-card {
    background: #1a1a1a;
    border-color: #4c4d4f;

    .example-text {
      background: rgba(0, 0, 0, 0.3);
    }

    .example-result {
      border-top-color: #4c4d4f;
    }
  }

  .tips-card {
    background: #1e1e20;
    border-color: #4c4d4f;

    .tips-list li {
      color: #cfd3dc;
    }
  }
}

code {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
}
</style>
