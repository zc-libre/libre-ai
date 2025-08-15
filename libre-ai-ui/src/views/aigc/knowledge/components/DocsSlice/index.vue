<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import { del, page as getPage } from '@/api/aigc/slice';
import {
  ElMessage,
  ElMessageBox,
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElRow,
  ElCol,
  ElEmpty,
  ElTooltip,
  ElTag,
  ElCard
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { useRouter, useRoute } from 'vue-router';
import { list } from '@/api/aigc/docs';
import { formatToDateTime } from '@/utils/dateUtil';
import { Delete, Search } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const loading = ref(false);
const tableData = ref<any[]>([]);
const docsList = ref<any[]>([]);
const searchForm = reactive({
  content: '',
  docsId: ''
});
const pagination = ref({
  total: 0,
  page: 1,
  limit: 10
});

onMounted(async () => {
  loadDocs();
  loadData();
});

async function loadDocs() {
  try {
    const knowledgeId = route.params.id;
    const response = await list({ knowledgeId });
    docsList.value = response.result || [];
  } catch (error) {
    console.error('加载文档列表失败:', error);
  }
}

async function loadData() {
  loading.value = true;
  try {
    const knowledgeId = route.params.id;
    const response = await getPage({
      ...searchForm,
      ...pagination.value,
      knowledgeId
    });
    tableData.value = response.result?.rows || [];
    pagination.value.total = response.result?.total || 0;
  } catch (error) {
    ElMessage.error('加载切片列表失败');
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pagination.value.page = 1;
  loadData();
}

function handleReset() {
  searchForm.content = '';
  searchForm.docsId = '';
  handleSearch();
}

function handleDelete(row: any) {
  ElMessageBox.confirm(`确定要删除这个文档切片吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true;
    try {
      await del(row.id);
      ElMessage.success('删除成功');
      loadData();
    } catch (error) {
      ElMessage.error('删除失败，请重试');
    } finally {
      loading.value = false;
    }
  });
}

function highlightContent(content: string, keyword?: string) {
  if (!keyword || !content) return content;
  const regex = new RegExp(`(${keyword})`, 'gi');
  return content.replace(
    regex,
    '<mark class="bg-yellow-200 dark:bg-yellow-800">$1</mark>'
  );
}
</script>

<template>
  <div class="docs-slice-container">
    <!-- 搜索区域 -->
    <div
      class="search-section bg-white dark:bg-gray-800 rounded-xl shadow-lg p-4 sm:p-6 mb-4"
    >
      <el-form :model="searchForm" @submit.prevent="handleSearch">
        <el-row :gutter="16" align="middle">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="所属文档" class="mb-4 sm:mb-0">
              <el-select
                v-model="searchForm.docsId"
                placeholder="请选择文档"
                clearable
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="item in docsList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                >
                  <div class="flex items-center gap-2">
                    <Icon icon="tabler:file" class="text-gray-400" />
                    <span>{{ item.name }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="切片内容" class="mb-4 sm:mb-0">
              <el-input
                v-model="searchForm.content"
                placeholder="搜索切片内容..."
                clearable
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <Icon icon="ep:search" class="text-gray-400" />
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label=" " class="mb-0">
              <div class="flex gap-2">
                <el-button type="primary" :icon="Search" @click="handleSearch">
                  搜索
                </el-button>
                <el-button @click="handleReset">重置</el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 切片卡片区域 -->
    <div
      class="slice-content bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden"
    >
      <div
        class="slice-header px-4 sm:px-6 py-4 border-b border-gray-200 dark:border-gray-700"
      >
        <div class="flex justify-between items-center">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
            文档切片列表
          </h3>
          <el-tag type="info"> 共 {{ pagination.total }} 个切片 </el-tag>
        </div>
      </div>

      <div v-loading="loading" class="slice-body p-4 sm:p-6">
        <!-- 切片网格 -->
        <div
          v-if="tableData.length > 0"
          class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4"
        >
          <el-card
            v-for="item in tableData"
            :key="item.id"
            class="slice-card hover:shadow-xl transition-all duration-300"
            shadow="hover"
          >
            <template #header>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <Icon icon="tabler:scissors" class="text-blue-500" />
                  <span
                    class="text-sm font-medium text-gray-700 dark:text-gray-300"
                  >
                    切片 #{{ item.id.slice(-6) }}
                  </span>
                </div>
                <el-tooltip content="删除切片" placement="top">
                  <el-button
                    text
                    type="danger"
                    size="small"
                    :icon="Delete"
                    @click="handleDelete(item)"
                  />
                </el-tooltip>
              </div>
            </template>

            <div class="slice-content-body">
              <!-- 元数据 -->
              <div class="metadata mb-3 flex flex-wrap gap-2">
                <el-tag v-if="item.docsName" size="small" type="info">
                  <Icon icon="tabler:file" class="mr-1" />
                  {{ item.docsName }}
                </el-tag>
                <el-tag size="small">
                  字符数: {{ item.content?.length || 0 }}
                </el-tag>
              </div>

              <!-- 内容预览 -->
              <div class="content-preview">
                <p
                  class="text-sm text-gray-600 dark:text-gray-400 line-clamp-4"
                  v-html="
                    highlightContent(
                      item.content || '无内容',
                      searchForm.content
                    )
                  "
                />
              </div>

              <!-- 创建时间 -->
              <div
                class="mt-3 pt-3 border-t border-gray-100 dark:border-gray-700"
              >
                <span class="text-xs text-gray-500 dark:text-gray-500">
                  <Icon icon="tabler:clock" class="mr-1" />
                  {{ formatToDateTime(item.createTime) }}
                </span>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="py-12">
          <el-empty description="暂无切片数据" />
        </div>
      </div>

      <!-- 分页 -->
      <div
        v-if="tableData.length > 0"
        class="slice-footer px-4 sm:px-6 py-4 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50"
      >
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.limit"
          :total="pagination.total"
          :layout="'total, sizes, prev, pager, next, jumper'"
          :page-sizes="[9, 18, 36, 72]"
          background
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.docs-slice-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.search-section {
  flex-shrink: 0;
}

.slice-content {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
}

.slice-body {
  flex: 1;
  overflow-y: auto;
}

.slice-card {
  :deep(.el-card__header) {
    padding: 12px 16px;
    background: linear-gradient(to right, #f0f9ff, #f5f3ff);

    .dark & {
      background: linear-gradient(
        to right,
        rgb(59 130 246 / 10%),
        rgb(99 102 241 / 10%)
      );
    }
  }

  :deep(.el-card__body) {
    padding: 16px;
  }

  &:hover {
    border-color: var(--el-color-primary-light-3);
    transform: translateY(-2px);
  }
}

.content-preview {
  min-height: 80px;

  :deep(mark) {
    padding: 2px 4px;
    font-weight: 500;
    border-radius: 2px;
  }
}

.line-clamp-4 {
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}

.slice-footer {
  :deep(.el-pagination) {
    justify-content: center;

    @media (width >= 640px) {
      justify-content: flex-end;
    }
  }
}
</style>
