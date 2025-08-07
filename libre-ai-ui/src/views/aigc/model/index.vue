<script lang="ts" setup>
import { ref } from 'vue';
import { Icon } from '@iconify/vue';
import { ElCard, ElTabs, ElTabPane, ElDivider } from 'element-plus';
import ChatProvider from './components/chat/index.vue';
import EmbedProvider from './components/embedding/index.vue';
import ImageProvider from './components/image/index.vue';

const active = ref('1');
</script>

<template>
  <div class="view-container view-scrollable p-2 sm:p-4">
    <el-card
      shadow="never"
      class="aigc-model-card rounded-lg border-0 shadow-sm hover:shadow-md transition-shadow duration-300"
    >
      <el-tabs v-model="active" class="aigc-model-tabs">
        <el-tab-pane name="1">
          <template #label>
            <div class="flex items-center gap-1 sm:gap-2 px-1 sm:px-2">
              <Icon
                class="text-base sm:text-lg text-blue-500"
                icon="lets-icons:chat"
              />
              <span class="font-medium text-sm sm:text-base hidden sm:inline"
                >Chat模型供应商</span
              >
              <span class="font-medium text-sm sm:hidden">Chat</span>
            </div>
          </template>
        </el-tab-pane>
        <el-tab-pane name="2">
          <template #label>
            <div class="flex items-center gap-1 sm:gap-2 px-1 sm:px-2">
              <Icon
                class="text-base sm:text-lg text-green-500"
                icon="ph:database"
              />
              <span class="font-medium text-sm sm:text-base hidden sm:inline"
                >Embedding向量模型</span
              >
              <span class="font-medium text-sm sm:hidden">Embedding</span>
            </div>
          </template>
        </el-tab-pane>
        <el-tab-pane name="3">
          <template #label>
            <div class="flex items-center gap-1 sm:gap-2 px-1 sm:px-2">
              <Icon
                class="text-base sm:text-lg text-purple-500"
                icon="ph:image"
              />
              <span class="font-medium text-sm sm:text-base hidden sm:inline"
                >Image文生图模型</span
              >
              <span class="font-medium text-sm sm:hidden">Image</span>
            </div>
          </template>
        </el-tab-pane>
      </el-tabs>
      <el-divider class="my-3 sm:my-4" />

      <div class="aigc-model-content min-h-[400px]">
        <keep-alive>
          <ChatProvider v-if="active === '1'" />
          <EmbedProvider v-else-if="active === '2'" />
          <ImageProvider v-else-if="active === '3'" />
        </keep-alive>
      </div>
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.aigc-model-card {
  background: var(--el-bg-color);
  transition: var(--pure-transition-duration);

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.aigc-model-tabs {
  :deep(.el-tabs__header) {
    margin: 0 0 16px 0;
  }

  :deep(.el-tabs__item) {
    padding: 12px 16px;
    font-weight: 500;
    color: var(--el-text-color-regular);
    transition: all var(--pure-transition-duration);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(
        135deg,
        transparent 0%,
        var(--el-color-primary-light-9) 100%
      );
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover {
      color: var(--el-color-primary);

      &::before {
        opacity: 0.5;
      }
    }

    &.is-active {
      color: var(--el-color-primary);
      font-weight: 600;

      &::before {
        opacity: 0.8;
        background: linear-gradient(
          135deg,
          var(--el-color-primary-light-8) 0%,
          var(--el-color-primary-light-9) 100%
        );
      }
    }

    span {
      position: relative;
      z-index: 1;
    }

    .iconify {
      position: relative;
      z-index: 1;
    }
  }

  :deep(.el-tabs__active-bar) {
    background-color: var(--el-color-primary);
  }
}

.aigc-model-content {
  min-height: 400px;
}

// 响应式设计优化
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
