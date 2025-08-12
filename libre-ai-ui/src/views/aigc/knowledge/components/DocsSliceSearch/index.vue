<script lang="ts" setup>
import { ref } from 'vue';
import { embeddingSearch } from '@/api/aigc/embedding';
import { useRoute } from 'vue-router';
import {
  ElMessage,
  ElButton,
  ElInput,
  ElCard,
  ElEmpty,
  ElTag,
  ElTooltip,
  ElBadge
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { Search, DocumentCopy } from '@element-plus/icons-vue';

const route = useRoute();
const content = ref('');
const loading = ref(false);
const list = ref<any>([]);
const searchHistory = ref<string[]>([]);

async function onSearch() {
  if (!content.value.trim()) {
    ElMessage.warning('请输入搜索内容');
    return;
  }

  loading.value = true;
  try {
    const result = await embeddingSearch({
      content: content.value,
      knowledgeId: route.params.id as string
    });
    list.value = result.result || [];

    // 添加到搜索历史
    if (!searchHistory.value.includes(content.value)) {
      searchHistory.value.unshift(content.value);
      if (searchHistory.value.length > 5) {
        searchHistory.value.pop();
      }
    }

    if (list.value.length === 0) {
      ElMessage.info('未找到相关内容');
    } else {
      ElMessage.success(`找到 ${list.value.length} 个相关结果`);
    }
  } catch (error) {
    ElMessage.error('搜索失败，请重试');
  } finally {
    loading.value = false;
  }
}

function handleHistoryClick(keyword: string) {
  content.value = keyword;
  onSearch();
}

function clearHistory() {
  searchHistory.value = [];
  ElMessage.success('搜索历史已清空');
}

function handleCopyContent(text: string) {
  navigator.clipboard.writeText(text);
  ElMessage.success('内容已复制到剪贴板');
}

function getScoreType(score: number) {
  if (score >= 0.9) return 'success';
  if (score >= 0.7) return 'warning';
  return 'info';
}

function getScoreText(score: number) {
  if (score >= 0.9) return '高度相关';
  if (score >= 0.7) return '较相关';
  return '一般相关';
}
</script>

<template>
  <div class="search-container h-full flex flex-col gap-4 min-h-0">
    <!-- 搜索区域 -->
    <div
      class="search-section bg-white dark:bg-gray-800 rounded-xl shadow-lg p-6"
    >
      <div class="flex flex-col lg:flex-row gap-4">
        <!-- 左侧搜索输入 -->
        <div class="flex-1">
          <div class="mb-4">
            <h3
              class="text-lg font-semibold text-gray-900 dark:text-white mb-2"
            >
              <Icon icon="tabler:vector" class="mr-2" />
              向量相似度搜索
            </h3>
            <p class="text-sm text-gray-600 dark:text-gray-400">
              输入关键词或句子，系统将使用向量相似度算法查找最相关的文档片段
            </p>
          </div>

          <div class="search-input-group">
            <el-input
              v-model="content"
              type="textarea"
              :rows="4"
              placeholder="输入您要搜索的内容，例如：如何使用知识库？"
              show-word-limit
              maxlength="500"
              @keyup.enter.ctrl="onSearch"
            />
            <div class="mt-3 flex gap-2">
              <el-button
                type="primary"
                :icon="Search"
                :loading="loading"
                size="large"
                @click="onSearch"
              >
                开始搜索
              </el-button>
              <el-button size="large" @click="content = ''"> 清空 </el-button>
            </div>
          </div>
        </div>

        <!-- 右侧搜索历史 -->
        <div v-if="searchHistory.length > 0" class="w-full lg:w-80">
          <div
            class="history-section bg-gray-50 dark:bg-gray-900 rounded-lg p-4"
          >
            <div class="flex items-center justify-between mb-3">
              <h4 class="text-sm font-medium text-gray-700 dark:text-gray-300">
                <Icon icon="tabler:history" class="mr-1" />
                搜索历史
              </h4>
              <el-button text size="small" @click="clearHistory">
                清空
              </el-button>
            </div>
            <div class="flex flex-wrap gap-2">
              <el-tag
                v-for="(item, index) in searchHistory"
                :key="index"
                class="cursor-pointer hover:shadow-md transition-all"
                @click="handleHistoryClick(item)"
              >
                {{ item }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索结果区域 -->
    <div
      class="results-section flex-1 bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden min-h-0"
    >
      <div
        class="results-header px-6 py-4 border-b border-gray-200 dark:border-gray-700"
      >
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
            搜索结果
          </h3>
          <el-badge
            v-if="list.length > 0"
            :value="list.length"
            type="primary"
          />
        </div>
      </div>

      <div class="results-body p-6 overflow-y-auto flex-1">
        <div v-loading="loading">
          <!-- 结果网格 -->
          <div
            v-if="list.length > 0"
            class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4"
          >
            <el-card
              v-for="(item, index) in list"
              :key="index"
              class="result-card hover:shadow-xl transition-all duration-300"
              shadow="hover"
            >
              <template #header>
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-2 flex-1 min-w-0">
                    <Icon
                      icon="tabler:file-text"
                      class="text-blue-500 flex-shrink-0"
                    />
                    <span
                      class="font-medium text-gray-900 dark:text-white truncate"
                    >
                      {{ item.docsName || `文档片段 ${index + 1}` }}
                    </span>
                  </div>
                  <el-tag
                    v-if="item.score"
                    :type="getScoreType(item.score)"
                    size="small"
                  >
                    {{ getScoreText(item.score) }}
                  </el-tag>
                </div>
              </template>

              <div class="result-content">
                <!-- 相似度分数 -->
                <div v-if="item.score" class="mb-3">
                  <div
                    class="flex items-center justify-between text-sm text-gray-600 dark:text-gray-400 mb-1"
                  >
                    <span>相似度</span>
                    <span class="font-medium"
                      >{{ (item.score * 100).toFixed(1) }}%</span
                    >
                  </div>
                  <div
                    class="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2"
                  >
                    <div
                      class="h-2 rounded-full transition-all duration-500"
                      :class="{
                        'bg-green-500': item.score >= 0.9,
                        'bg-yellow-500': item.score >= 0.7 && item.score < 0.9,
                        'bg-blue-500': item.score < 0.7
                      }"
                      :style="{ width: `${item.score * 100}%` }"
                    />
                  </div>
                </div>

                <!-- 文本内容 -->
                <div class="content-text">
                  <p
                    class="text-sm text-gray-700 dark:text-gray-300 line-clamp-5"
                  >
                    {{ item.text || item.content || '无内容' }}
                  </p>
                </div>

                <!-- 操作按钮 -->
                <div
                  class="mt-4 pt-3 border-t border-gray-100 dark:border-gray-700 flex justify-end"
                >
                  <el-tooltip content="复制内容" placement="top">
                    <el-button
                      text
                      type="primary"
                      size="small"
                      :icon="DocumentCopy"
                      @click="handleCopyContent(item.text || item.content)"
                    >
                      复制
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
            </el-card>
          </div>

          <!-- 空状态 -->
          <el-empty
            v-else-if="!loading"
            description="暂无搜索结果"
            class="py-12"
          >
            <template #image>
              <Icon icon="tabler:search-off" class="text-6xl text-gray-300" />
            </template>
            <p class="text-sm text-gray-500 mt-2">请输入关键词开始搜索</p>
          </el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.search-container {
  height: 100%;
  min-height: 0;
}

.results-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.results-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.result-card {
  :deep(.el-card__header) {
    padding: 12px 16px;
    background: linear-gradient(to right, #f0f9ff, #f5f3ff);

    .dark & {
      background: linear-gradient(
        to right,
        rgba(59, 130, 246, 0.1),
        rgba(99, 102, 241, 0.1)
      );
    }
  }

  &:hover {
    transform: translateY(-2px);
    border-color: var(--el-color-primary-light-3);
  }
}

.line-clamp-5 {
  display: -webkit-box;
  -webkit-line-clamp: 5;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-section {
  :deep(.el-tag) {
    &:hover {
      background-color: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
      border-color: var(--el-color-primary-light-5);
    }
  }
}

.content-text {
  min-height: 100px;
}
</style>
