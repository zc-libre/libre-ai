<script lang="ts" setup>
import { ref } from 'vue';
import { Icon } from '@iconify/vue';
import ChatProvider from './components/chat/index.vue';
import EmbedProvider from './components/embedding/index.vue';
import ImageProvider from './components/image/index.vue';

const active = ref('chat');

const modelTabs = [
  {
    value: 'chat',
    label: 'Chat对话模型',
    shortLabel: 'Chat',
    icon: 'lets-icons:chat',
    color: 'text-blue-500'
  },
  {
    value: 'embedding',
    label: 'Embedding向量模型',
    shortLabel: '向量',
    icon: 'ph:database',
    color: 'text-green-500'
  },
  {
    value: 'image',
    label: 'Image文生图模型',
    shortLabel: '图像',
    icon: 'ph:image',
    color: 'text-purple-500'
  }
];
</script>

<template>
  <div class="view-container aigc-model-app bg-bg_color">
    <!-- 顶部标题区域 -->
    <div class="header-section flex-shrink-0 w-full px-4 sm:px-6 lg:px-8 py-4">
      <div class="flex justify-between items-center">
        <div class="flex items-center gap-3">
          <div class="header-icon bg-blue-50 dark:bg-blue-900/20 p-2 rounded-lg">
            <Icon icon="carbon:machine-learning" class="text-2xl text-blue-600" />
          </div>
          <div>
            <h1 class="text-xl font-bold text-gray-900 dark:text-white">AI模型管理</h1>
            <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">配置和管理各类AI模型供应商</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content flex-1 flex flex-col px-4 sm:px-6 lg:px-8 pb-4 min-h-0">
      <div class="model-container flex-1 bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden">
        <!-- 模型类型选择器 -->
        <div class="model-nav-section bg-gray-50 dark:bg-gray-900/50 px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <div class="flex flex-wrap gap-2">
            <button
              v-for="tab in modelTabs"
              :key="tab.value"
              :class="[
                'model-nav-btn px-4 py-2.5 rounded-lg font-medium transition-all duration-200 flex items-center gap-2',
                active === tab.value 
                  ? 'bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 shadow-sm' 
                  : 'bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700'
              ]"
              @click="active = tab.value"
            >
              <Icon :icon="tab.icon" :class="['text-lg', tab.color]" />
              <span class="text-sm font-medium hidden sm:inline">{{ tab.label }}</span>
              <span class="text-sm font-medium sm:hidden">{{ tab.shortLabel }}</span>
            </button>
          </div>
        </div>

        <!-- 内容区域 -->
        <div class="model-content flex-1 min-h-0 p-6">
          <keep-alive>
            <ChatProvider v-if="active === 'chat'" />
            <EmbedProvider v-else-if="active === 'embedding'" />
            <ImageProvider v-else-if="active === 'image'" />
          </keep-alive>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.aigc-model-app {
  box-sizing: border-box;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header-section {
  border-bottom: 1px solid rgb(229 231 235 / 0.1);
}

.header-icon {
  transition: all 0.2s ease;
}

.header-icon:hover {
  transform: scale(1.05);
}

.main-content {
  box-sizing: border-box;
  overflow: hidden;
}

.model-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.model-nav-section {
  flex-shrink: 0;
}

.model-nav-btn {
  border: 1px solid transparent;
  user-select: none;
  cursor: pointer;
}

.model-nav-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.model-content {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

/* 响应式设计优化 */
@media (max-width: 960px) {
  .view-container {
    padding: 8px 12px;
  }

  .aigc-model-card {
    :deep(.el-card__body) {
      padding: 16px;
    }
  }
}

@media (max-width: 760px) {
  .view-container {
    padding: 4px 8px;
  }

  .aigc-model-card {
    margin: 0;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

    :deep(.el-card__body) {
      padding: 12px;
    }
  }

  .aigc-model-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 12px;
    }

    :deep(.el-tabs__item) {
      padding: 6px 8px;
      font-size: 13px;
      min-width: 80px;
    }
  }

  .aigc-model-content {
    min-height: 300px;
  }
}

@media (max-width: 640px) {
  .view-container {
    padding: 2px 4px;
  }

  .aigc-model-card {
    border-radius: 4px;

    :deep(.el-card__body) {
      padding: 8px;
    }
  }

  .aigc-model-tabs {
    :deep(.el-tabs__nav-scroll) {
      display: flex;
    }

    :deep(.el-tabs__item) {
      flex: 1;
      text-align: center;
      padding: 6px 4px;
      font-size: 12px;
      min-width: 60px;
    }
  }

  .aigc-model-content {
    min-height: 250px;
  }
}
</style>
