<script lang="ts" setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  ArrowLeft,
  Setting,
  Connection,
  DataAnalysis,
  Refresh,
  Check,
  ChatLineRound,
  EditPen
} from '@element-plus/icons-vue';
import { getAppInfo } from '@/api/aigc/chat';
import AppBase from './base/index.vue';
import ApiChannel from './channel-api/index.vue';
import PromptConfig from './base/prompt/PromptConfigPage.vue';
import { useAppStore } from './store';

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();

const loading = ref(false);
const appInfo = ref<any>({});
const activeTab = ref('settings');
const isSaving = ref(false);
const lastSaveTime = ref('');

// 提示词组件引用
const promptConfigRef = ref<any>(null);
// 标签切换状态
const isTabSwitching = ref(false);

const tabs = [
  {
    key: 'settings',
    label: '应用配置',
    icon: Setting,
    color: '#6366F1',
    description: '配置应用基础信息和知识库'
  },
  {
    key: 'prompt',
    label: '提示词配置',
    icon: EditPen,
    color: '#10B981',
    description: '配置系统提示词和用户提示词模板'
  },
  {
    key: 'api',
    label: 'API 接入',
    icon: Connection,
    color: '#8B5CF6',
    description: '管理应用的 API 接入渠道和权限'
  }
];

onMounted(() => {
  loadAppInfo();
  updateSaveTime();
});

async function loadAppInfo() {
  loading.value = true;
  try {
    const id = route.params.id;
    const res = await getAppInfo({
      appId: id,
      conversationId: null
    });
    const data = res.result || res;
    appInfo.value = data || {};

    // 同步数据到 store，供子组件使用
    if (data) {
      // 确保关键字段有默认值，防止null值导致md-editor-v3错误
      appStore.info = {
        ...data,
        systemPrompt: data.systemPrompt || '',
        userPromptTemplate: data.userPromptTemplate || ''
      };
      appStore.knowledgeIds = data.knowledgeIds || [];
      appStore.modelId = data.modelId || null;
      appStore.knowledges = data.knowledges || [];
      appStore.model = data.model || null; // 保存模型信息
    }
  } catch (error) {
    console.error('加载应用信息失败:', error);
    ElMessage.error('加载应用信息失败');
    appInfo.value = {};
  } finally {
    loading.value = false;
  }
}

function updateSaveTime() {
  const now = new Date();
  lastSaveTime.value = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  });
}

async function handleSave() {
  isSaving.value = true;
  try {
    await appStore.updateInfo();
    updateSaveTime();
    ElMessage.success('保存成功');
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败，请重试');
  } finally {
    isSaving.value = false;
  }
}

function handleBack() {
  router.push('/aigc/app');
}

function handleRefresh() {
  loadAppInfo();
}

const appGradient = computed(() => {
  const gradients = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  ];
  const id = appInfo.value?.id || '0';
  return gradients[parseInt(id) % gradients.length];
});

function getCurrentTabInfo() {
  return tabs.find(tab => tab.key === activeTab.value);
}

// 检查提示词是否有未保存的更改
function checkPromptUnsavedChanges(): boolean {
  if (promptConfigRef.value && typeof promptConfigRef.value.hasUnsavedChanges === 'function') {
    return promptConfigRef.value.hasUnsavedChanges();
  }
  return false;
}

// 标签切换前确认
async function handleTabSwitch(newTab: string): Promise<boolean> {
  // 如果当前不是提示词标签，直接允许切换
  if (activeTab.value !== 'prompt') {
    return true;
  }

  // 检查是否有未保存的更改
  if (!checkPromptUnsavedChanges()) {
    return true;
  }

  try {
    await ElMessageBox.confirm(
      '提示词配置有未保存的更改，切换标签页将丢失这些更改。',
      '确认切换',
      {
        confirmButtonText: '切换',
        cancelButtonText: '取消',
        type: 'warning',
        showClose: false,
        closeOnClickModal: false,
        closeOnPressEscape: false
      }
    );
    return true;
  } catch {
    return false;
  }
}

// 处理标签点击
async function onTabClick(tabKey: string) {
  if (tabKey === activeTab.value) return;

  isTabSwitching.value = true;
  const canSwitch = await handleTabSwitch(tabKey);

  if (canSwitch) {
    activeTab.value = tabKey;
  }

  await nextTick();
  isTabSwitching.value = false;
}
</script>

<template>
  <div class="view-container app-detail-page bg-bg_color">
    <!-- 顶部导航栏 -->
    <div class="header-section flex-shrink-0 w-full">
      <div class="header-content">
        <!-- 主导航行 -->
        <div class="nav-row">
          <div class="nav-left">
            <button class="back-btn" @click="handleBack">
              <el-icon :size="20">
                <ArrowLeft />
              </el-icon>
            </button>

            <div v-if="appInfo.name" class="app-info">
              <div
                class="app-icon-wrapper"
                :style="{ background: appGradient }"
              >
                <el-icon :size="24" color="white">
                  <DataAnalysis />
                </el-icon>
              </div>

              <div class="app-details">
                <h1 class="app-name">{{ appInfo.name }}</h1>
                <div class="app-meta">
                  <span class="meta-item">
                    <el-icon :size="12"><Check /></el-icon>
                    自动保存
                  </span>
                  <span class="meta-time">{{ lastSaveTime }}</span>
                  <span v-if="isSaving" class="saving-indicator">
                    <el-icon class="animate-spin" :size="12">
                      <Refresh />
                    </el-icon>
                    保存中...
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div class="nav-right">
            <el-button
              :icon="Refresh"
              :loading="loading"
              circle
              @click="handleRefresh"
            />
            <el-button
              type="primary"
              :icon="ChatLineRound"
              @click="router.push(`/aigc/chat/${appInfo.id}`)"
            >
              开始对话
            </el-button>
          </div>
        </div>

        <!-- Tab 导航行 -->
        <div class="tab-row">
          <div class="tabs-container">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              class="tab-item"
              :class="{ active: activeTab === tab.key }"
              :disabled="isTabSwitching"
              @click="onTabClick(tab.key)"
            >
              <div class="tab-content">
                <el-icon
                  :size="18"
                  :color="activeTab === tab.key ? tab.color : '#909399'"
                  class="tab-icon"
                >
                  <component :is="tab.icon" />
                </el-icon>
                <span class="tab-label">{{ tab.label }}</span>
              </div>
              <div
                v-if="activeTab === tab.key"
                class="tab-indicator"
                :style="{ background: tab.color }"
              />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content flex-1 overflow-auto">
      <!-- 加载状态 -->
      <div v-if="loading && !appInfo.name" class="loading-state">
        <el-icon class="loading-icon" :size="48" color="#C0C4CC">
          <Refresh />
        </el-icon>
        <p class="loading-text">加载中...</p>
      </div>

      <!-- Tab 内容面板 -->
      <div v-else-if="appInfo.name" class="content-panel">
        <!-- Tab 描述卡片 -->
        <div class="tab-description-card">
          <el-icon
            :size="20"
            :color="getCurrentTabInfo()?.color"
            class="desc-icon"
          >
            <component :is="getCurrentTabInfo()?.icon" />
          </el-icon>
          <span class="desc-text">{{ getCurrentTabInfo()?.description }}</span>
        </div>

        <!-- Tab 内容区域 -->
        <div class="tab-content-area">
          <!-- 应用配置 -->
          <div v-show="activeTab === 'settings'" class="config-container">
            <AppBase />
          </div>

          <!-- 提示词配置 -->
          <div v-show="activeTab === 'prompt'" class="prompt-container">
            <PromptConfig ref="promptConfigRef" />
          </div>

          <!-- API 接入 -->
          <div v-show="activeTab === 'api'" class="api-container">
            <ApiChannel />
          </div>
        </div>
      </div>

      <!-- 错误状态 -->
      <div v-else class="error-state">
        <div class="error-content">
          <div class="error-icon-wrapper">
            <el-icon class="error-icon" :size="48" color="#F56C6C">
              <Setting />
            </el-icon>
          </div>
          <h3 class="error-title">加载失败</h3>
          <p class="error-description">无法加载应用信息，请检查网络连接</p>
          <el-button type="primary" :icon="Refresh" @click="loadAppInfo">
            重新加载
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.app-detail-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  overflow: hidden;
}

// 头部样式
.header-section {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border-bottom: 1px solid #e4e7ed;

  .header-content {
    width: 100%;
    padding: 0 24px;
  }

  .nav-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #f0f2f5;

    .nav-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .back-btn {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        border: 1px solid #e4e7ed;
        background: white;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #f5f7fa;
          border-color: #409eff;
        }
      }

      .app-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .app-icon-wrapper {
          width: 40px;
          height: 40px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .app-details {
          .app-name {
            font-size: 18px;
            font-weight: 600;
            color: #303133;
            margin: 0 0 4px 0;
          }

          .app-meta {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 12px;
            color: #909399;

            .meta-item {
              display: flex;
              align-items: center;
              gap: 4px;
            }

            .saving-indicator {
              display: flex;
              align-items: center;
              gap: 4px;
              color: #409eff;
            }
          }
        }
      }
    }

    .nav-right {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }

  .tab-row {
    padding: 0;

    .tabs-container {
      display: flex;
      gap: 0;

      .tab-item {
        position: relative;
        padding: 12px 24px;
        background: transparent;
        border: none;
        cursor: pointer;
        transition: all 0.3s;

        &:hover:not(:disabled) {
          background: #f5f7fa;
        }

        &:disabled {
          cursor: not-allowed;
          opacity: 0.6;
        }

        &.active {
          background: #f5f7fa;

          .tab-content {
            .tab-label {
              color: #303133;
              font-weight: 600;
            }
          }
        }

        .tab-content {
          display: flex;
          align-items: center;
          gap: 8px;

          .tab-icon {
            filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
          }

          .tab-label {
            font-size: 14px;
            color: #606266;
            font-weight: 500;
          }
        }

        .tab-indicator {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 2px;
          transition: all 0.3s;
        }
      }
    }
  }
}

// 主内容区域 - 修复宽度问题
.main-content {
  background: #f5f7fa;
  width: 100%;
  padding: 24px;

  .loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 400px;

    .loading-icon {
      animation: spin 1s linear infinite;
    }

    .loading-text {
      margin-top: 16px;
      font-size: 14px;
      color: #909399;
    }
  }

  .content-panel {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    gap: 20px;

    .tab-description-card {
      background: white;
      border-radius: 12px;
      padding: 16px 20px;
      display: flex;
      align-items: center;
      gap: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

      .desc-icon {
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
      }

      .desc-text {
        font-size: 14px;
        color: #606266;
      }
    }

    .tab-content-area {
      background: white;
      border-radius: 12px;
      flex: 1;
      min-height: 600px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      overflow: hidden;
      padding: 0;

      .config-container,
      .prompt-container,
      .api-container {
        width: 100%;
        height: 100%;
        overflow: auto;
      }
    }
  }

  .error-state {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 400px;

    .error-content {
      text-align: center;

      .error-icon-wrapper {
        width: 100px;
        height: 100px;
        margin: 0 auto 20px;
        background: linear-gradient(135deg, #fee, #fff);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .error-title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 8px 0;
      }

      .error-description {
        font-size: 14px;
        color: #909399;
        margin: 0 0 20px 0;
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

.animate-spin {
  animation: spin 1s linear infinite;
}

// 响应式
@media (max-width: 1024px) {
  .main-content {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .header-section {
    .header-content {
      padding: 0 16px;
    }

    .nav-row {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;

      .nav-right {
        width: 100%;
        justify-content: flex-end;
      }
    }

    .tab-row {
      .tabs-container {
        overflow-x: auto;

        .tab-item {
          padding: 12px 16px;
          white-space: nowrap;
        }
      }
    }
  }

  .main-content {
    padding: 12px;
  }
}
</style>
