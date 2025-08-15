<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import {
  ElMessage,
  ElMessageBox,
  ElCard,
  ElTabs,
  ElTabPane,
  ElTable,
  ElTableColumn,
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { del, page as getPage } from '@/api/aigc/message';
import { columns, searchSchemas } from './columns';
import ConversationList from './conversation/index.vue';

const actionRef = ref();
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

function handleDelete(record: any) {
  ElMessageBox.confirm('您确定删除这条数据吗', '提示', {
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

onMounted(() => {
  loadDataTable();
});
</script>

<template>
  <div
    class="view-container view-scrollable p-2 sm:p-4 bg-gray-50 dark:bg-gray-900 flex flex-col"
  >
    <div class="w-full flex flex-col flex-1 min-h-0">
      <!-- 主要内容卡片 -->
      <el-card
        shadow="hover"
        class="message-management-card border-0 shadow-lg flex-1 flex flex-col min-h-0"
        :body-style="{
          padding: '0',
          height: '100%',
          display: 'flex',
          flexDirection: 'column'
        }"
      >
        <el-tabs class="message-tabs flex-1 flex flex-col" tab-position="top">
          <el-tab-pane class="flex-1 flex flex-col min-h-0">
            <template #label>
              <div class="flex items-center gap-2 px-2 py-1">
                <Icon class="text-lg text-blue-500" icon="ep:chat-line-round" />
                <span class="font-medium hidden sm:inline">会话消息列表</span>
                <span class="font-medium sm:hidden">消息</span>
              </div>
            </template>

            <div class="p-4 sm:p-6 flex-1 flex flex-col min-h-0">
              <!-- 搜索表单 -->
              <div
                class="bg-gray-50 dark:bg-gray-800/50 rounded-lg p-4 mb-6 flex-shrink-0"
              >
                <el-form
                  :model="searchForm"
                  class="message-search-form"
                  @submit.prevent="handleSearch"
                >
                  <div
                    class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4"
                  >
                    <template
                      v-for="schema in searchSchemas"
                      :key="schema.field"
                    >
                      <el-form-item :label="schema.label" class="mb-0">
                        <el-input
                          v-if="schema.component === 'NInput'"
                          v-model="searchForm[schema.field]"
                          :placeholder="schema.componentProps?.placeholder"
                          clearable
                        />
                        <el-select
                          v-else-if="schema.component === 'NSelect'"
                          v-model="searchForm[schema.field]"
                          :placeholder="schema.componentProps?.placeholder"
                          clearable
                          class="w-full"
                        >
                          <el-option
                            v-for="option in schema.componentProps?.options"
                            :key="option.value"
                            :label="option.label"
                            :value="option.value"
                          />
                        </el-select>
                      </el-form-item>
                    </template>
                    <div class="flex gap-2 items-end">
                      <el-button
                        type="primary"
                        class="flex-1 sm:flex-none"
                        @click="handleSearch"
                      >
                        <Icon icon="ep:search" class="mr-1" />
                        查询
                      </el-button>
                      <el-button
                        class="flex-1 sm:flex-none"
                        @click="handleReset"
                        >重置</el-button
                      >
                    </div>
                  </div>
                </el-form>
              </div>

              <!-- 数据表格 -->
              <div
                class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 overflow-hidden flex-1 min-h-0 flex flex-col"
              >
                <el-table
                  v-loading="loading"
                  :data="tableData"
                  class="message-table flex-1"
                  size="default"
                  height="100%"
                  :header-cell-style="{
                    backgroundColor: 'var(--el-bg-color)',
                    color: 'var(--el-text-color-primary)',
                    fontWeight: '600'
                  }"
                >
                  <el-table-column
                    v-for="column in columns.filter(
                      col => col.key !== 'action'
                    )"
                    :key="column.key"
                    :prop="column.key"
                    :label="column.title"
                    :width="column.width"
                    :align="column.align || 'left'"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    label="操作"
                    width="80"
                    align="center"
                    fixed="right"
                  >
                    <template #default="{ row }">
                      <el-button
                        type="danger"
                        link
                        size="small"
                        class="hover:bg-red-50 dark:hover:bg-red-900/20 rounded px-2 py-1"
                        @click="handleDelete(row)"
                      >
                        <Icon icon="ep:delete" />
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane class="flex-1 flex flex-col min-h-0">
            <template #label>
              <div class="flex items-center gap-2 px-2 py-1">
                <Icon class="text-lg text-green-500" icon="ep:chat-dot-round" />
                <span class="font-medium hidden sm:inline">会话窗口列表</span>
                <span class="font-medium sm:hidden">会话</span>
              </div>
            </template>
            <div class="p-4 sm:p-6 flex-1 flex flex-col min-h-0">
              <ConversationList />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<style lang="scss" scoped>
// 响应式设计
@media (width <= 768px) {
  .message-management-card {
    margin: 0 -8px;
    border-top: 1px solid var(--pure-border-color);
    border-radius: 0;
    box-shadow: none;
  }

  .message-tabs {
    :deep(.el-tabs__header) {
      padding: 0 16px;
    }

    :deep(.el-tabs__item) {
      padding: 8px 12px;
      font-size: 14px;
    }
  }
}

@media (width <= 640px) {
  .message-tabs {
    :deep(.el-tabs__nav-scroll) {
      display: flex;
      justify-content: space-around;
    }

    :deep(.el-tabs__item) {
      flex: 1;
      padding: 8px 4px;
      text-align: center;
    }
  }
}

.message-management-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  transition: var(--pure-transition-duration);

  :deep(.el-card__body) {
    border-radius: 12px;
  }
}

.message-tabs {
  height: 100%;

  :deep(.el-tabs__header) {
    flex-shrink: 0;
    padding: 0 20px;
    margin: 0;
    border-bottom: 1px solid var(--pure-border-color);
  }

  :deep(.el-tabs__content) {
    display: flex;
    flex: 1;
    flex-direction: column;
    min-height: 0;
  }

  :deep(.el-tab-pane) {
    display: flex;
    flex-direction: column;
    height: 100%;
  }

  :deep(.el-tabs__nav-wrap) {
    padding: 0;
  }

  :deep(.el-tabs__item) {
    padding: 12px 16px;
    font-weight: 500;
    color: var(--el-text-color-regular);
    border-bottom: 2px solid transparent;
    transition: all 0.3s ease;

    &:hover {
      color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);
    }

    &.is-active {
      color: var(--el-color-primary);
      background-color: var(--el-color-primary-light-9);
      border-bottom-color: var(--el-color-primary);
    }
  }

  :deep(.el-tabs__active-bar) {
    display: none;
  }
}

.message-search-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--el-text-color-regular);
  }
}

.message-table {
  height: 100%;

  :deep(.el-table) {
    height: 100%;
    border: none;
  }

  :deep(.el-table__header-wrapper) {
    border-radius: 8px 8px 0 0;
  }

  :deep(.el-table__body-wrapper) {
    flex: 1;
    border-radius: 0 0 8px 8px;
  }
}
</style>
