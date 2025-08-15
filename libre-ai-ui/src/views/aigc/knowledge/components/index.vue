<script lang="ts" setup>
import DocList from './DocsList/index.vue';
import DocsSlice from './DocsSlice/index.vue';
import { IconifyIconOffline as SvgIcon } from '@/components/ReIcon';
import DocsSliceSearch from './DocsSliceSearch/index.vue';
import ImportFile from './ImportFile/index.vue';
import { onMounted, ref } from 'vue';
import { Icon } from '@iconify/vue';
import { useRoute, useRouter } from 'vue-router';
import {
  ElRow,
  ElCol,
  ElButton,
  ElDivider,
  ElInput,
  ElTabs,
  ElTabPane
} from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import { getById } from '@/api/aigc/knowledge';

const route = useRoute();
const router = useRouter();
const active = ref('import-file');
const menuOptions = ref([
  {
    label: '数据导入',
    key: 'import-file',
    icon: 'ep:upload'
  },
  {
    label: '文档管理',
    key: 'doc-list',
    icon: 'ep:document'
  }
]);

const knowledge = ref<any>({});
onMounted(async () => {
  const id = route.params.id;
  const response = await getById(String(id));
  knowledge.value = (response as any).result || response;
  active.value = menuOptions.value[0].key;

  menuOptions.value.push(
    {
      label: '切片管理',
      key: 'slice-list',
      icon: 'ep:files'
    },
    {
      label: '向量搜索',
      key: 'slice-search',
      icon: 'ep:search'
    }
  );
});

function handleSelect(key: string) {
  active.value = key;
}

function handleReturn() {
  router.back();
}
</script>

<template>
  <div class="view-container knowledge-detail-app bg-bg_color h-full">
    <!-- 头部导航区域 -->
    <div class="header-section flex-shrink-0 w-full px-4 sm:px-6 lg:px-8 py-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <el-button
            type="primary"
            plain
            :icon="ArrowLeft"
            size="large"
            class="shadow-md hover:shadow-lg transition-all duration-300"
            @click="handleReturn"
          >
            返回列表
          </el-button>
          <div class="flex items-center gap-3">
            <div
              class="bg-gradient-to-br from-blue-100 to-indigo-100 dark:from-blue-900/30 dark:to-indigo-900/30 p-3 rounded-xl shadow-lg"
            >
              <Icon
                icon="fluent:document-database-24-regular"
                class="text-2xl text-blue-600 dark:text-blue-400"
              />
            </div>
            <div>
              <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
                {{ knowledge.name }}
              </h1>
              <p class="text-gray-600 dark:text-gray-400 text-sm">
                {{ knowledge.des || '暂无描述' }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div
      class="k-main-content flex-1 flex flex-col lg:flex-row gap-4 px-4 sm:px-6 lg:px-8 pb-4 pt-4 min-h-0"
    >
      <!-- 左侧信息面板 -->
      <div
        class="k-sidebar-container flex-shrink-0 w-full lg:w-80 bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden"
      >
        <div class="p-6">
          <!-- 知识库基本信息 -->
          <div class="info-section mb-6">
            <div class="section-header mb-4">
              <Icon icon="tabler:info-circle" class="section-icon" />
              <span class="section-title">基本信息</span>
            </div>

            <div class="info-item">
              <label class="info-label">知识库ID</label>
              <div
                class="info-value bg-gray-50 dark:bg-gray-700 p-3 rounded-lg text-sm font-mono"
              >
                {{ knowledge.id }}
              </div>
            </div>
          </div>

          <!-- 配置信息 -->
          <div class="config-section">
            <div class="section-header mb-4">
              <Icon icon="tabler:settings" class="section-icon" />
              <span class="section-title">配置信息</span>
            </div>

            <div class="info-item">
              <label class="info-label">向量数据库</label>
              <div v-if="knowledge.embedStore" class="config-card">
                <div class="flex items-center gap-2">
                  <div class="bg-blue-100 dark:bg-blue-900/30 p-1.5 rounded">
                    <Icon
                      icon="tabler:database"
                      class="text-blue-600 dark:text-blue-400 text-sm"
                    />
                  </div>
                  <span class="text-sm font-medium">{{
                    knowledge.embedStore.name
                  }}</span>
                </div>
              </div>
              <div v-else class="config-empty">
                <Icon icon="tabler:database-off" class="text-gray-400 mb-1" />
                <span class="text-xs text-gray-400">未配置</span>
              </div>
            </div>

            <div class="info-item">
              <label class="info-label">向量模型</label>
              <div v-if="knowledge.embedModel" class="config-card">
                <div class="flex items-center gap-2">
                  <div class="bg-green-100 dark:bg-green-900/30 p-1.5 rounded">
                    <Icon
                      icon="tabler:brain"
                      class="text-green-600 dark:text-green-400 text-sm"
                    />
                  </div>
                  <span class="text-sm font-medium">{{
                    knowledge.embedModel.name
                  }}</span>
                </div>
              </div>
              <div v-else class="config-empty">
                <Icon icon="tabler:brain-off" class="text-gray-400 mb-1" />
                <span class="text-xs text-gray-400">未配置</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧内容区域 -->
      <div
        class="content-container flex-1 min-w-0 bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden flex flex-col"
      >
        <!-- 标签页导航 -->
        <div
          class="tabs-header bg-gradient-to-r from-gray-50 to-blue-50/30 dark:from-gray-800/50 dark:to-blue-900/10 border-b border-gray-200 dark:border-gray-700 p-4"
        >
          <el-tabs
            v-model="active"
            class="knowledge-tabs"
            @tab-change="handleSelect"
          >
            <el-tab-pane
              v-for="item in menuOptions"
              :key="item.key"
              :name="item.key"
              :label="item.label"
            >
              <template #label>
                <div class="flex items-center gap-2">
                  <Icon :icon="item.icon" class="text-lg" />
                  <span class="font-medium">{{ item.label }}</span>
                </div>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 标签页内容 -->
        <div class="tabs-content flex-1 p-6 overflow-y-auto min-h-0">
          <DocList v-if="active == 'doc-list'" />
          <DocsSlice v-if="active == 'slice-list'" />
          <DocsSliceSearch v-if="active == 'slice-search'" />
          <ImportFile v-if="active == 'import-file'" :data="knowledge" />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>


// 响应式优化
@media (width <= 1024px) {
  .k-main-content {
    flex-direction: column;
  }

  .k-sidebar-container {
    width: 100%;
  }
}

@media (width <= 640px) {
  .knowledge-detail-app {
    height: calc(100vh - 120px);
    max-height: calc(100vh - 120px);
  }

  .header-section {
    padding: 16px;
  }

  .k-main-content {
    gap: 16px;
    padding: 0 16px 16px;
  }
}

.knowledge-detail-app {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  max-height: calc(100vh - 60px);
  overflow: hidden;
}

.header-section {
  background: linear-gradient(
    135deg,
    rgb(59 130 246 / 8%),
    rgb(99 102 241 / 8%),
    rgb(168 85 247 / 6%)
  );

  :global(.dark) & {
    background: linear-gradient(
      135deg,
      rgb(59 130 246 / 15%),
      rgb(99 102 241 / 15%),
      rgb(168 85 247 / 10%)
    );
  }
}

.main-content {
  box-sizing: border-box;
}

.section-header {
  display: flex;
  gap: 8px;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.section-icon {
  font-size: 18px;
  color: var(--el-color-primary);
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.info-item {
  margin-bottom: 16px;

  &:last-child {
    margin-bottom: 0;
  }
}

.info-label {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 500;
  color: var(--el-text-color-regular);
}

.info-value {
  word-break: break-all;
  border: 1px solid var(--el-border-color-light);
  transition: all 0.3s ease;
}

.config-card {
  padding: 12px;
  background: var(--el-bg-color-page);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    border-color: var(--el-color-primary-light-7);
    box-shadow: 0 2px 8px rgb(59 130 246 / 10%);
  }
}

.config-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  text-align: center;
  background: var(--el-bg-color-page);
  border: 1px dashed var(--el-border-color-light);
  border-radius: 8px;
}

.knowledge-tabs {
  :deep(.el-tabs__header) {
    margin: 0;
  }

  :deep(.el-tabs__nav-wrap) {
    &::after {
      display: none;
    }
  }

  :deep(.el-tabs__item) {
    height: 44px;
    padding: 0 20px;
    font-weight: 500;
    line-height: 44px;

    &.is-active {
      color: var(--el-color-primary);
    }
  }

  :deep(.el-tabs__active-bar) {
    height: 3px;
    background-color: var(--el-color-primary);
  }
}
</style>
