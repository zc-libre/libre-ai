<script lang="ts" setup>
import { h, nextTick, reactive, ref, onMounted } from 'vue';
import {
  ElMessage,
  ElMessageBox,
  ElCard,
  ElButton,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElAlert,
  ElTable,
  ElTableColumn,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { del, page as getPage } from '@/api/aigc/embed-store';
import { columns, ProviderConst, searchSchemas } from './columns';
import Edit from './edit.vue';

const provider = ref('');
const actionRef = ref();
const editRef = ref();
const tableData = ref([]);
const loading = ref(false);
const searchForm = ref({});

const loadDataTable = async () => {
  loading.value = true;
  try {
    const result = await getPage({ ...searchForm.value });
    tableData.value = result.records || [];
  } catch (error) {
    ElMessage.error('加载数据失败');
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
    milvus: 'simple-icons:milvus'
  };
  return iconMap[provider] || 'ep:database';
}

onMounted(() => {
  loadDataTable();
});
</script>

<template>
  <div
    class="view-container view-scrollable p-2 sm:p-4 bg-gray-50 dark:bg-gray-900 h-full flex flex-col"
  >
    <!-- 主要内容卡片 -->
    <el-card
      shadow="hover"
      class="embed-store-card border-0 shadow-lg flex-1 flex flex-col"
    >
      <!-- 搜索表单 -->
      <div class="bg-gray-50 dark:bg-gray-800/50 rounded-lg p-4 mb-6">
        <el-form
          :model="searchForm"
          class="embed-store-search-form"
          @submit.prevent="handleSearch"
        >
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <el-form-item label="名称" class="mb-0">
              <el-input
                v-model="searchForm.name"
                placeholder="请输入名称"
                clearable
              />
            </el-form-item>
            <el-form-item label="提供商" class="mb-0">
              <el-select
                v-model="searchForm.provider"
                placeholder="请选择提供商"
                clearable
                class="w-full"
              >
                <el-option
                  v-for="item in ProviderConst"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <div class="flex gap-2 items-end">
              <el-button
                type="primary"
                class="flex-1 sm:flex-none"
                @click="handleSearch"
              >
                <Icon icon="ep:search" class="mr-1" />
                查询
              </el-button>
              <el-button class="flex-1 sm:flex-none" @click="handleReset"
                >重置
              </el-button>
            </div>
          </div>
        </el-form>
      </div>

      <!-- 警告提示 -->
      <el-alert
        class="mb-6"
        title="注意：请慎重修改模型的向量纬度参数（Dimension），此参数需要和向量库匹配（错误修改可能将影响已有的向量数据）"
        type="warning"
        show-icon
        :closable="false"
      />

      <!-- 操作按钮 -->
      <div
        class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6"
      >
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
          向量数据库列表
        </h3>
        <el-dropdown placement="bottom-end" @command="handleAdd">
          <el-button type="primary" size="default">
            <Icon icon="ep:plus" class="mr-1" />
            <span class="hidden sm:inline">新增向量数据库</span>
            <span class="sm:hidden">新增</span>
            <Icon icon="ep:arrow-down" class="ml-1" />
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

      <!-- 数据表格 -->
      <div
        class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 overflow-hidden flex-1 flex flex-col"
      >
        <el-table
          v-loading="loading"
          :data="tableData"
          class="embed-store-table"
          size="default"
          style="width: 100%"
          height="100%"
          :header-cell-style="{
            backgroundColor: 'var(--el-bg-color)',
            color: 'var(--el-text-color-primary)',
            fontWeight: '600'
          }"
        >
          <el-table-column
            v-for="column in columns.filter(col => col.key !== 'action')"
            :key="column.key"
            :prop="column.key"
            :label="column.title"
            :width="column.width"
            :align="column.align || 'left'"
            show-overflow-tooltip
          />
          <el-table-column
            label="操作"
            width="140"
            align="center"
            fixed="right"
          >
            <template #default="{ row }">
              <div class="flex items-center justify-center gap-1">
                <el-button
                  type="primary"
                  link
                  size="small"
                  class="hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded px-2 py-1"
                  @click="handleEdit(row)"
                >
                  <Icon icon="ep:edit" class="mr-1" />
                  <span class="hidden sm:inline">编辑</span>
                </el-button>
                <el-button
                  type="danger"
                  link
                  size="small"
                  class="hover:bg-red-50 dark:hover:bg-red-900/20 rounded px-2 py-1"
                  @click="handleDelete(row)"
                >
                  <Icon icon="ep:delete" class="mr-1" />
                  <span class="hidden sm:inline">删除</span>
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <Edit ref="editRef" :provider="provider" @reload="reloadTable" />
  </div>
</template>

<style lang="scss" scoped>
.embed-store-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  transition: var(--pure-transition-duration);
  display: flex;
  flex-direction: column;
  height: 100%;

  :deep(.el-card__body) {
    padding: 20px;
    border-radius: 12px;
    flex: 1;
    display: flex;
    flex-direction: column;

    @media (max-width: 640px) {
      padding: 16px;
    }
  }
}

.embed-store-search-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--el-text-color-regular);
  }
}

.embed-store-table {
  height: 100%;

  :deep(.el-table) {
    border: none;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  :deep(.el-table__header-wrapper) {
    border-radius: 8px 8px 0 0;
  }

  :deep(.el-table__body-wrapper) {
    border-radius: 0 0 8px 8px;
    flex: 1;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .embed-store-card {
    margin: 0 -8px;
    border-radius: 0;
    box-shadow: none;
    border-top: 1px solid var(--pure-border-color);
  }
}

// 暗色主题适配
html.dark {
  .embed-store-card {
    background: var(--el-bg-color);
    border: 1px solid var(--pure-border-color);
  }
}
</style>
