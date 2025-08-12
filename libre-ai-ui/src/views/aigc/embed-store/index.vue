<script lang="ts" setup>
import { nextTick, ref, onMounted } from 'vue';
import {
  ElMessage,
  ElMessageBox,
  ElButton,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElTable,
  ElTableColumn,
  ElForm,
  ElInput,
  ElSelect,
  ElOption,
  ElTag,
  ElTooltip
} from 'element-plus';
import { Icon } from '@iconify/vue';
import {
  Plus,
  Search,
  Refresh,
  Edit as EditIcon,
  Delete
} from '@element-plus/icons-vue';
import { del, page as getPage } from '@/api/aigc/embed-store';
import Edit from './edit.vue';

// 提供商配置常量
enum ProviderEnum {
  Redis = 'redis',
  PgVector = 'pgvector',
  Milvus = 'milvus'
}

const ProviderConst = [
  { label: 'Redis', value: ProviderEnum.Redis },
  { label: 'PgVector', value: ProviderEnum.PgVector },
  { label: 'Milvus', value: ProviderEnum.Milvus }
];

const provider = ref('');
const actionRef = ref();
const editRef = ref();
const tableData = ref([]);
const loading = ref(false);
const searchForm = ref({});

const loadDataTable = async () => {
  loading.value = true;
  try {
    const response = await getPage({ ...searchForm.value });
    console.log('API响应数据:', response);
    // 根据后端返回的数据结构：{code, message, result: {rows, total}}
    tableData.value = response.result?.rows || [];
    console.log('表格数据:', tableData.value);
  } catch (error) {
    ElMessage.error('加载数据失败');
    console.error('数据加载失败:', error);
  } finally {
    loading.value = false;
  }
};

function reloadTable() {
  loadDataTable();
}

async function handleAdd(val: any) {
  provider.value = val;
  await nextTick();
  editRef.value.show();
}

async function handleEdit(record: any) {
  provider.value = record.provider;
  await nextTick();
  editRef.value.show(record.id);
}

function handleDelete(record: any) {
  ElMessageBox.confirm(`您想删除 ${record.name}`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await del(record.id);
      ElMessage.success('删除成功');
      reloadTable();
    })
    .catch(() => {});
}

function handleReset() {
  searchForm.value = {};
  reloadTable();
}

function handleSearch() {
  reloadTable();
}

function getProviderIcon(provider: string) {
  const iconMap: Record<string, string> = {
    redis: 'logos:redis',
    chroma: 'simple-icons:chromadb',
    pinecone: 'simple-icons:pinecone',
    weaviate: 'simple-icons:weaviate',
    qdrant: 'simple-icons:qdrant',
    milvus: 'simple-icons:milvus',
    pgvector: 'logos:postgresql'
  };
  return iconMap[provider] || 'ep:database';
}

function getProviderTagType(provider: string) {
  const typeMap: Record<string, string> = {
    redis: 'danger',
    chroma: 'warning',
    pinecone: 'success',
    weaviate: 'primary',
    qdrant: 'info',
    milvus: '',
    pgvector: 'success'
  };
  return typeMap[provider] || '';
}

function getProviderLabel(provider: string) {
  const item = ProviderConst.find(p => p.value === provider);
  return item?.label || provider;
}

onMounted(() => {
  loadDataTable();
});
</script>

<template>
  <div class="view-container embed-store-app bg-bg_color">
    <!-- 头部信息区域 -->
    <div class="header-section flex-shrink-0 w-full px-4 sm:px-6 lg:px-8 py-4">
      <div class="flex justify-between items-center">
        <div class="title-container">
          <h1 class="page-title flex items-center gap-3">
            <Icon icon="mdi:database" :size="28" color="#6366F1" />
            向量数据库管理
          </h1>
          <p class="page-subtitle">
            配置和管理多种向量存储解决方案，为AI应用提供高效的向量检索能力
          </p>
        </div>
        <div class="action-buttons flex gap-2">
          <el-tooltip content="刷新数据" placement="bottom">
            <el-button :icon="Refresh" circle @click="loadDataTable" />
          </el-tooltip>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div
      class="main-content flex-1 flex flex-col gap-3 px-4 sm:px-6 lg:px-8 pb-3 min-h-0"
    >
      <!-- 内容容器 -->
      <div
        class="content-container flex-1 min-w-0 bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden min-h-0 flex flex-col"
      >
        <!-- 搜索和操作区域 -->
        <div
          class="search-section p-4 sm:p-6 border-b border-gray-200 dark:border-gray-700"
        >
          <div
            class="search-header flex flex-col sm:flex-row gap-4 sm:gap-0 sm:justify-between sm:items-center mb-4"
          >
            <h2
              class="section-title text-lg font-semibold text-gray-900 dark:text-white"
            >
              数据库配置列表
            </h2>
            <el-dropdown placement="bottom-end" @command="handleAdd">
              <el-button type="primary" size="default">
                <el-icon class="mr-1">
                  <Plus />
                </el-icon>
                新增向量数据库
                <el-icon class="ml-1">
                  <Icon icon="ep:arrow-down" />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="item in ProviderConst"
                    :key="item.value"
                    :command="item.value"
                  >
                    <div class="flex items-center gap-2">
                      <Icon :icon="getProviderIcon(item.value)" />
                      {{ item.label }}
                    </div>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <!-- 筛选表单 -->
          <div
            class="filter-form bg-gray-50 dark:bg-gray-900/50 rounded-lg p-4"
          >
            <el-form
              :model="searchForm"
              class="search-form"
              @submit.prevent="handleSearch"
            >
              <div class="flex flex-col sm:flex-row gap-4">
                <div class="flex-1 flex flex-col sm:flex-row gap-4">
                  <el-input
                    v-model="searchForm.name"
                    placeholder="请输入数据库名称"
                    clearable
                    :prefix-icon="Search"
                    class="flex-1 sm:max-w-xs"
                  />
                  <el-select
                    v-model="searchForm.provider"
                    placeholder="选择提供商类型"
                    clearable
                    class="flex-1 sm:max-w-xs"
                  >
                    <el-option
                      v-for="item in ProviderConst"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    >
                      <div class="flex items-center gap-2">
                        <Icon :icon="getProviderIcon(item.value)" />
                        <span>{{ item.label }}</span>
                      </div>
                    </el-option>
                  </el-select>
                </div>
                <div class="flex gap-2">
                  <el-button
                    type="primary"
                    :icon="Search"
                    @click="handleSearch"
                  >
                    搜索
                  </el-button>
                  <el-button :icon="Refresh" @click="handleReset">
                    重置
                  </el-button>
                </div>
              </div>
            </el-form>
          </div>
        </div>

        <!-- 数据表格区域 -->
        <div class="table-section flex-1 p-4 sm:p-6 min-h-0">
          <div
            class="table-container h-full bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-700 overflow-hidden"
          >
            <el-table
              v-loading="loading"
              :data="tableData"
              class="data-table"
              size="default"
              style="width: 100%"
              height="100%"
              :header-cell-style="{
                backgroundColor: '#f8fafc',
                color: '#1e293b',
                fontWeight: '600',
                borderBottom: '2px solid #e2e8f0'
              }"
              :row-style="{ cursor: 'pointer' }"
              @row-click="handleEdit"
            >
              <el-table-column
                prop="name"
                label="数据库名称"
                min-width="200"
                show-overflow-tooltip
              >
                <template #default="{ row }">
                  <div class="flex items-center gap-3">
                    <div class="icon-wrapper">
                      <Icon
                        :icon="getProviderIcon(row.provider)"
                        :size="24"
                        color="#6366F1"
                      />
                    </div>
                    <div>
                      <div class="font-semibold text-gray-900 dark:text-white">
                        {{ row.name }}
                      </div>
                      <div class="text-xs text-gray-500 dark:text-gray-400">
                        ID: {{ row.id }}
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>

              <el-table-column
                prop="provider"
                label="提供商"
                width="150"
                align="center"
              >
                <template #default="{ row }">
                  <el-tag
                    :type="getProviderTagType(row.provider)"
                    effect="light"
                    round
                  >
                    <div class="flex items-center gap-1">
                      <Icon :icon="getProviderIcon(row.provider)" :size="14" />
                      {{ getProviderLabel(row.provider) }}
                    </div>
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column
                prop="host"
                label="服务器地址"
                width="160"
                align="center"
                show-overflow-tooltip
              >
                <template #default="{ row }">
                  <span class="text-sm text-gray-700 dark:text-gray-300">{{
                    row.host || '-'
                  }}</span>
                </template>
              </el-table-column>

              <el-table-column
                prop="port"
                label="端口"
                width="80"
                align="center"
              >
                <template #default="{ row }">
                  <el-tag type="info" effect="plain" size="small">{{
                    row.port || '-'
                  }}</el-tag>
                </template>
              </el-table-column>

              <el-table-column
                prop="dimension"
                label="向量维度"
                width="120"
                align="center"
              >
                <template #default="{ row }">
                  <el-tag type="warning" effect="plain" size="small">
                    {{ row.dimension || 'N/A' }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column
                prop="databaseName"
                label="数据库"
                width="140"
                align="center"
                show-overflow-tooltip
              >
                <template #default="{ row }">
                  <span class="text-sm text-gray-700 dark:text-gray-300">{{
                    row.databaseName || '-'
                  }}</span>
                </template>
              </el-table-column>

              <el-table-column
                prop="tableName"
                label="表名"
                width="120"
                align="center"
                show-overflow-tooltip
              >
                <template #default="{ row }">
                  <span class="text-sm text-gray-700 dark:text-gray-300">{{
                    row.tableName || '-'
                  }}</span>
                </template>
              </el-table-column>

              <el-table-column
                prop="status"
                label="状态"
                width="100"
                align="center"
              >
                <template #default="{ row }">
                  <div class="flex items-center justify-center gap-2">
                    <span
                      class="status-dot"
                      :class="row.isPerms ? 'active' : 'inactive'"
                    />
                    <span class="text-sm">{{
                      row.isPerms ? '启用' : '禁用'
                    }}</span>
                  </div>
                </template>
              </el-table-column>

              <el-table-column
                prop="createTime"
                label="创建时间"
                width="180"
                align="center"
                show-overflow-tooltip
              />

              <el-table-column
                label="操作"
                width="160"
                align="center"
                fixed="right"
              >
                <template #default="{ row }">
                  <div class="flex items-center justify-center gap-2">
                    <el-tooltip content="编辑配置" placement="top">
                      <el-button
                        type="primary"
                        :icon="EditIcon"
                        circle
                        size="small"
                        @click.stop="handleEdit(row)"
                      />
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                      <el-button
                        type="danger"
                        :icon="Delete"
                        circle
                        size="small"
                        @click.stop="handleDelete(row)"
                      />
                    </el-tooltip>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <Edit ref="editRef" :provider="provider" @reload="reloadTable" />
  </div>
</template>

<style lang="scss" scoped>
/* 整体布局样式 */
.embed-store-app {
  box-sizing: border-box;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

/* 头部区域样式 */
.header-section {
  background: linear-gradient(to right, #f8fafc, #f1f5f9);
  border-bottom: 1px solid #e2e8f0;

  .page-title {
    font-size: 24px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 4px;
  }

  .page-subtitle {
    font-size: 14px;
    color: #64748b;
    margin: 0;
  }
}

/* 主内容区域 */
.main-content {
  box-sizing: border-box;
}

.content-container {
  backdrop-filter: blur(10px);
}

/* 搜索区域样式 */
.search-section {
  background: white;

  .section-title {
    color: #1e293b;
    font-weight: 600;
  }

  .filter-form {
    border: 1px solid #e2e8f0;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }
  }

  .form-label {
    font-size: 13px;
    font-weight: 500;
    color: #475569;
  }
}

/* 表格区域样式 */
.table-section {
  .table-container {
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
  }
}

.data-table {
  :deep(.el-table) {
    font-size: 14px;

    .el-table__row {
      transition: all 0.2s ease;

      &:hover {
        background-color: #f8fafc !important;
        transform: translateX(2px);
      }
    }

    .el-table__header th {
      font-size: 13px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }

  .icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background: rgba(99, 102, 241, 0.1);
    border-radius: 8px;
  }

  /* 状态点样式 */
  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-right: 6px;
    display: inline-block;

    &.active {
      background: #10b981;
      box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2);
    }

    &.inactive {
      background: #ef4444;
      box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.2);
    }
  }
}

/* 按钮悬浮效果 */
:deep(.el-button) {
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
  }

  &.is-circle:hover {
    transform: scale(1.1);
  }
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu__item) {
  transition: all 0.2s ease;

  &:hover {
    background: linear-gradient(to right, #eff6ff, #dbeafe);
    padding-left: 20px;
  }
}

/* Tag 样式优化 */
:deep(.el-tag) {
  font-weight: 500;
  border: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 990px) {
  .header-section {
    .page-title {
      font-size: 20px;
    }

    .page-subtitle {
      font-size: 12px;
    }
  }
}

@media (max-width: 760px) {
  .header-section {
    padding: 12px 16px;

    .title-container {
      text-align: center;
    }

    .action-buttons {
      justify-content: center;
    }
  }

  .search-section {
    .section-title {
      font-size: 16px;
    }
  }

  .data-table {
    :deep(.el-table) {
      font-size: 12px;
    }
  }
}

/* 暗色主题适配 */
html.dark {
  .header-section {
    background: linear-gradient(to right, #1e293b, #334155);
    border-bottom-color: #475569;

    .page-title {
      color: #f1f5f9;
    }

    .page-subtitle {
      color: #94a3b8;
    }
  }

  .search-section {
    background: #1e293b;

    .section-title {
      color: #f1f5f9;
    }

    .filter-form {
      background: #0f172a;
      border-color: #334155;
    }

    .form-label {
      color: #cbd5e1;
    }
  }

  .table-container {
    background: #1e293b !important;
    border-color: #334155 !important;
  }

  .data-table {
    :deep(.el-table) {
      .el-table__row:hover {
        background-color: #334155 !important;
      }

      .el-table__header-wrapper th {
        background-color: #0f172a !important;
        color: #e2e8f0 !important;
        border-bottom-color: #475569 !important;
      }
    }
  }
}
</style>
