<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import { del, page as getPage, reEmbed } from '@/api/aigc/docs';
import Edit from './edit.vue';
import {
  ElMessage,
  ElMessageBox,
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElRow,
  ElCol,
  ElEmpty,
  ElTooltip,
  ElTag
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { useRoute, useRouter } from 'vue-router';
import { formatToDateTime } from '@/utils/dateUtil';
import {
  Search,
  Plus,
  Refresh,
  Edit as EditIcon,
  Delete
} from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const editRef = ref();
const loading = ref(false);
const tableData = ref<any[]>([]);
const searchForm = reactive({
  name: '',
  sliceStatus: ''
});
const pagination = ref({
  total: 0,
  page: 1,
  limit: 10
});

onMounted(() => {
  loadData();
});

async function loadData() {
  loading.value = true;
  try {
    const knowledgeId = route.params.id;
    const response = await getPage({
      ...searchForm,
      ...pagination.value,
      knowledgeId
    });
    tableData.value = (response as any).result?.rows || [];
    pagination.value.total = (response as any).result?.total || 0;
  } catch (error) {
    ElMessage.error('加载文档列表失败');
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pagination.value.page = 1;
  loadData();
}

function handleReset() {
  searchForm.name = '';
  searchForm.sliceStatus = '';
  handleSearch();
}

function handleAdd() {
  if (editRef.value?.show) {
    editRef.value.show(route.params.id, '');
  } else {
    ElMessage.error('编辑组件未就绪，请稍后重试');
  }
}

function handleEdit(row: any) {
  if (editRef.value?.show) {
    editRef.value.show(route.params.id, row.id);
  } else {
    ElMessage.error('编辑组件未就绪，请稍后重试');
  }
}

function handleReEmbed(row: any) {
  ElMessageBox.confirm(
    '重新向量化将会删除Vector向量库中的旧数据，确定要继续吗？',
    '确认操作',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true;
    try {
      await reEmbed(row.id);
      ElMessage.success('文档向量化解析成功');
      loadData();
    } catch (error) {
      ElMessage.error('向量化失败，请重试');
    } finally {
      loading.value = false;
    }
  });
}

function handleDelete(row: any) {
  ElMessageBox.confirm(`确定要删除文档 "${row.name}" 吗？`, '删除确认', {
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

function getStatusType(status: number) {
  return status === 1 ? 'success' : 'info';
}

function getStatusText(status: number) {
  return status === 1 ? '已向量化' : '待向量化';
}
</script>

<template>
  <div class="docs-list-container">
    <!-- 搜索区域 -->
    <div
      class="search-section bg-gradient-to-r from-white via-white to-blue-50/30 dark:from-gray-800 dark:via-gray-800 dark:to-blue-900/20 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 p-6 mb-6"
    >
      <div class="flex items-center gap-3 mb-4">
        <div
          class="bg-gradient-to-br from-blue-100 to-indigo-100 dark:from-blue-900/30 dark:to-indigo-900/30 p-2 rounded-lg"
        >
          <Icon
            icon="tabler:search"
            class="text-lg text-blue-600 dark:text-blue-400"
          />
        </div>
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
          文档搜索
        </h3>
      </div>

      <el-form :model="searchForm" @submit.prevent="handleSearch">
        <el-row :gutter="16" align="middle">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-input
              v-model="searchForm.name"
              placeholder="搜索文档名称..."
              clearable
              size="large"
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <Icon icon="tabler:file-search" class="text-gray-400" />
              </template>
            </el-input>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="flex gap-3 mt-4 sm:mt-0">
              <el-button
                type="primary"
                :icon="Search"
                size="large"
                class="shadow-md hover:shadow-lg transition-all duration-300"
                @click="handleSearch"
              >
                搜索
              </el-button>
              <el-button
                :icon="Refresh"
                size="large"
                class="shadow-md hover:shadow-lg transition-all duration-300"
                @click="handleReset"
              >
                重置
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div
      class="table-section bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden"
    >
      <div
        class="table-header px-6 py-4 bg-gradient-to-r from-gray-50 to-blue-50/30 dark:from-gray-800/50 dark:to-blue-900/10 border-b border-gray-200 dark:border-gray-700"
      >
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-3">
            <div
              class="bg-gradient-to-br from-blue-100 to-indigo-100 dark:from-blue-900/30 dark:to-indigo-900/30 p-2 rounded-lg"
            >
              <Icon
                icon="tabler:file-text"
                class="text-lg text-blue-600 dark:text-blue-400"
              />
            </div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
              文档列表
            </h3>
          </div>
          <el-button
            type="primary"
            :icon="Plus"
            size="large"
            class="shadow-md hover:shadow-lg transition-all duration-300"
            @click="handleAdd"
          >
            添加文档
          </el-button>
        </div>
      </div>

      <div class="table-body">
        <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%"
          :empty-text="'暂无文档数据'"
        >
          <el-table-column prop="name" label="文档名称" min-width="200">
            <template #default="{ row }">
              <div class="flex items-center gap-3">
                <div class="bg-blue-100 dark:bg-blue-900/30 p-1.5 rounded">
                  <Icon
                    icon="tabler:file-text"
                    class="text-blue-600 dark:text-blue-400 text-sm"
                  />
                </div>
                <span class="font-medium text-gray-900 dark:text-white">{{
                  row.name
                }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            prop="sliceStatus"
            label="状态"
            width="120"
            align="center"
          >
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.sliceStatus)" size="small">
                {{ getStatusText(row.sliceStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            prop="size"
            label="文件大小"
            width="120"
            align="right"
          >
            <template #default="{ row }">
              <span class="text-gray-600 dark:text-gray-400">
                {{ (row.size / 1024).toFixed(2) }} KB
              </span>
            </template>
          </el-table-column>

          <el-table-column
            prop="sliceNum"
            label="切片数"
            width="100"
            align="center"
          >
            <template #default="{ row }">
              <el-tag type="info" size="small">
                {{ row.sliceNum || 0 }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              <span class="text-gray-600 dark:text-gray-400 text-sm">
                {{ formatToDateTime(row.createTime) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column
            label="操作"
            width="160"
            fixed="right"
            align="center"
          >
            <template #default="{ row }">
              <div class="flex items-center justify-center gap-2">
                <el-tooltip content="重新向量化" placement="top">
                  <el-button
                    text
                    type="primary"
                    size="small"
                    class="!p-2 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-all duration-300"
                    @click="handleReEmbed(row)"
                  >
                    <Icon icon="tabler:refresh" class="text-base" />
                  </el-button>
                </el-tooltip>
                <el-tooltip content="编辑" placement="top">
                  <el-button
                    text
                    type="primary"
                    size="small"
                    class="!p-2 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-lg transition-all duration-300"
                    @click="handleEdit(row)"
                  >
                    <Icon icon="tabler:edit" class="text-base" />
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button
                    text
                    type="danger"
                    size="small"
                    class="!p-2 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-all duration-300"
                    @click="handleDelete(row)"
                  >
                    <Icon icon="tabler:trash" class="text-base" />
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态 -->
        <div v-if="!loading && tableData.length === 0" class="py-16">
          <div class="text-center">
            <div
              class="bg-gradient-to-br from-gray-50 to-blue-50/30 dark:from-gray-800/50 dark:to-blue-900/20 p-8 rounded-full w-32 h-32 flex items-center justify-center mx-auto mb-6"
            >
              <Icon
                icon="tabler:file-text"
                class="text-5xl text-gray-400 dark:text-gray-500"
              />
            </div>
            <h3
              class="text-xl font-semibold text-gray-900 dark:text-white mb-2"
            >
              暂无文档
            </h3>
            <p class="text-gray-500 dark:text-gray-400 mb-6 max-w-md mx-auto">
              开始上传您的第一个文档，构建知识库内容
            </p>
            <el-button
              type="primary"
              :icon="Plus"
              size="large"
              class="shadow-lg hover:shadow-xl transition-all duration-300"
              @click="handleAdd"
            >
              添加文档
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div
        v-if="tableData.length > 0"
        class="table-footer px-6 py-4 border-t border-gray-200 dark:border-gray-700 bg-gradient-to-r from-gray-50 to-blue-50/30 dark:from-gray-800/50 dark:to-blue-900/10"
      >
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.limit"
          :total="pagination.total"
          :layout="'total, sizes, prev, pager, next, jumper'"
          :page-sizes="[10, 20, 50, 100]"
          background
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </div>

    <Edit ref="editRef" @reload="loadData" />
  </div>
</template>

<style lang="scss" scoped>
.docs-list-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.search-section {
  flex-shrink: 0;
}

.search-input {
  :deep(.el-input__wrapper) {
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    }

    &.is-focus {
      box-shadow: 0 4px 20px rgba(59, 130, 246, 0.15);
    }
  }
}

.table-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-body {
  flex: 1;
  overflow: auto;
}

.table-footer {
  flex-shrink: 0;

  :deep(.el-pagination) {
    justify-content: center;

    @media (min-width: 640px) {
      justify-content: flex-end;
    }
  }
}

:deep(.el-table) {
  .el-table__header {
    th {
      background-color: var(--el-bg-color-page);
      font-weight: 600;
    }
  }

  .el-table__row {
    &:hover {
      background-color: var(--el-fill-color-lighter);
    }
  }
}
</style>
