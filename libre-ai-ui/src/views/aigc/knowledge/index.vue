<script lang="ts" setup>
import { onMounted, ref, reactive } from 'vue';
import { del, page as getPage } from '@/api/aigc/knowledge';
import Edit from './edit.vue';
import {
  ElMessage,
  ElMessageBox,
  ElEmpty,
  ElDivider,
  ElButton,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElPagination,
  ElForm,
  ElFormItem,
  ElInput,
  ElRow,
  ElCol
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { useRouter } from 'vue-router';
import { Plus } from '@element-plus/icons-vue';
import { useAppStoreHook } from '@/store/modules/app';

const router = useRouter();
const appStore = useAppStoreHook();
const editRef = ref();
const loading = ref(true);
const pagination = ref({
  total: 0,
  page: 1,
  limit: 10
});
const list = ref<any>([]);
const searchForm = reactive({
  name: ''
});
const actionOptions = [
  {
    label: '修改',
    value: 'edit'
  },
  {
    label: '删除',
    value: 'delete'
  }
];

onMounted(async () => {
  await fetch();
});

async function fetch() {
  const response: any = await getPage({ ...searchForm, ...pagination.value });
  pagination.value.total = response.result.total;
  list.value = response.result.rows;
  loading.value = false;
}

function resetSearch() {
  searchForm.name = '';
  fetch();
}

async function handleInfo(record: any) {
  await router.push('/aigc/knowledge/' + record.id);
}

function handleAdd() {
  if (!editRef.value) {
    ElMessage.error('组件初始化失败，请刷新页面重试');
    return;
  }

  try {
    editRef.value.show();
  } catch (error) {
    console.error('打开创建窗口失败:', error);
    ElMessage.error('打开创建窗口失败，请重试');
  }
}

function onSelectAction(key: string, item: any) {
  if (key === 'edit') {
    handleEdit(item);
  }
  if (key === 'delete') {
    handleDelete(item);
  }
}

function handleEdit(record: any) {
  editRef.value.show(record.id);
}

function handleDelete(record: any) {
  ElMessageBox.confirm(`您想删除 ${record.name}`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await del(String(record.id));
      ElMessage.success('删除成功');
      await fetch();
    })
    .catch(() => {
      // 用户取消删除
    });
}
</script>

<template>
  <div class="view-container knowledge-app bg-bg_color h-full">
    <!-- 头部区域 -->
    <div class="header-section flex-shrink-0 w-full px-4 sm:px-6 lg:px-8 py-4">
      <div
        class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4"
      >
        <div class="flex-1">
          <div class="flex items-center gap-3 mb-2">
            <div
              class="bg-gradient-to-br from-blue-100 to-indigo-100 dark:from-blue-900/30 dark:to-indigo-900/30 p-3 rounded-xl"
            >
              <Icon
                icon="fluent:document-database-24-regular"
                class="text-2xl text-blue-600 dark:text-blue-400"
              />
            </div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
              知识库管理
            </h1>
          </div>
          <p class="text-gray-600 dark:text-gray-400">
            创建和管理您的知识库，构建智能问答系统
          </p>
        </div>
        <!-- 操作按钮组 -->
        <div class="flex gap-3">
          <el-tooltip content="创建新的知识库" placement="bottom">
            <el-button
              type="primary"
              :icon="Plus"
              size="large"
              class="shadow-lg hover:shadow-xl transition-all duration-300"
              @click="handleAdd"
            >
              新建知识库
            </el-button>
          </el-tooltip>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div
      class="main-content flex-1 flex flex-col px-4 sm:px-6 lg:px-8 pb-3 pt-4 min-h-0"
    >
      <!-- 搜索区域 -->
      <div
        class="search-container bg-white dark:bg-gray-800 rounded-xl shadow-lg p-4 sm:p-6 mb-4 border border-gray-200 dark:border-gray-700"
      >
        <el-form :model="searchForm" class="knowledge-search-form">
          <el-row :gutter="16" align="middle">
            <el-col :xs="24" :sm="16" :md="12" :lg="8">
              <el-input
                v-model="searchForm.name"
                placeholder="搜索知识库名称..."
                clearable
                size="large"
                class="search-input"
                @keyup.enter="fetch"
              >
                <template #prefix>
                  <Icon icon="tabler:search" class="text-gray-400" />
                </template>
              </el-input>
            </el-col>
            <el-col :xs="24" :sm="8" :md="6" :lg="4">
              <div class="flex gap-3 mt-4 sm:mt-0">
                <el-button
                  type="primary"
                  size="large"
                  class="shadow-md hover:shadow-lg transition-all duration-300"
                  @click="fetch"
                >
                  <Icon icon="tabler:search" class="mr-1" />
                  搜索
                </el-button>
                <el-button
                  size="large"
                  class="shadow-md hover:shadow-lg transition-all duration-300"
                  @click="resetSearch"
                >
                  <Icon icon="tabler:refresh" class="mr-1" />
                  重置
                </el-button>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 知识库卡片区域 -->
      <div
        class="knowledge-content flex-1 bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden min-h-0 flex flex-col border border-gray-200 dark:border-gray-700"
      >
        <div v-loading="loading" class="flex-1 p-4 sm:p-6 overflow-y-auto">
          <!-- 空状态 -->
          <div
            v-if="!loading && (!list || list.length === 0)"
            class="flex items-center justify-center h-full min-h-[400px]"
          >
            <div class="text-center">
              <div
                class="bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 p-8 rounded-full w-32 h-32 flex items-center justify-center mx-auto mb-6"
              >
                <Icon
                  icon="fluent:document-database-24-regular"
                  class="text-5xl text-blue-500 dark:text-blue-400"
                />
              </div>
              <h3
                class="text-xl font-semibold text-gray-900 dark:text-white mb-2"
              >
                暂无知识库
              </h3>
              <p class="text-gray-500 dark:text-gray-400 mb-6 max-w-md">
                开始创建您的第一个知识库，构建智能问答系统
              </p>
              <el-button
                type="primary"
                size="large"
                :icon="Plus"
                class="shadow-lg hover:shadow-xl transition-all duration-300"
                @click="handleAdd"
              >
                创建知识库
              </el-button>
            </div>
          </div>

          <!-- 知识库网格 -->
          <div
            v-else
            class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
          >
            <!-- 知识库卡片 -->
            <div
              v-for="item in list"
              :key="item.id"
              class="knowledge-card bg-gradient-to-br from-white via-white to-blue-50/30 dark:from-gray-800 dark:via-gray-800 dark:to-blue-900/20 border border-gray-200 dark:border-gray-700 hover:border-blue-400 dark:hover:border-blue-500 hover:shadow-2xl transition-all duration-300 cursor-pointer rounded-xl overflow-hidden group backdrop-blur-sm"
              @click="handleInfo(item)"
            >
              <!-- 卡片顶部色条 -->
              <div
                class="h-1.5 bg-gradient-to-r from-blue-500 via-indigo-500 to-purple-500"
              />

              <!-- 卡片内容 -->
              <div class="p-6">
                <!-- 卡片头部 -->
                <div class="flex items-start justify-between mb-4">
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-3 mb-3">
                      <div
                        class="bg-gradient-to-br from-blue-100 to-indigo-100 dark:from-blue-900/30 dark:to-indigo-900/30 p-3 rounded-xl group-hover:scale-110 group-hover:rotate-3 transition-transform duration-300 shadow-lg"
                      >
                        <Icon
                          class="text-xl text-blue-600 dark:text-blue-400"
                          icon="fluent:document-database-24-regular"
                        />
                      </div>
                      <div class="flex-1 min-w-0">
                        <h3
                          class="font-semibold text-gray-900 dark:text-white text-base truncate group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors duration-300"
                        >
                          {{ item.name }}
                        </h3>
                      </div>
                    </div>
                    <p
                      class="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 min-h-[2.5rem] leading-relaxed"
                    >
                      {{ item.des || '暂无描述' }}
                    </p>
                  </div>

                  <!-- 操作菜单 -->
                  <el-dropdown
                    trigger="click"
                    placement="bottom-end"
                    @command="key => onSelectAction(key, item)"
                    @click.stop
                  >
                    <el-button
                      text
                      size="small"
                      class="!p-2 opacity-0 group-hover:opacity-100 transition-all duration-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
                      @click.stop
                    >
                      <Icon
                        class="text-lg text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                        icon="tabler:dots-vertical"
                      />
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item
                          v-for="option in actionOptions"
                          :key="option.value"
                          :command="option.value"
                          class="flex items-center gap-2"
                        >
                          <Icon
                            :icon="
                              option.value === 'edit'
                                ? 'tabler:edit'
                                : 'tabler:trash'
                            "
                            class="text-sm"
                          />
                          {{ option.label }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>

                <!-- 统计信息 -->
                <div
                  class="flex items-center justify-between pt-4 mt-4 border-t border-gray-100 dark:border-gray-700"
                >
                  <div class="flex items-center gap-4">
                    <div
                      class="flex items-center gap-1.5 text-xs text-gray-500 dark:text-gray-400"
                    >
                      <div class="bg-blue-100 dark:bg-blue-900/30 p-1 rounded">
                        <Icon
                          icon="tabler:files"
                          class="text-blue-600 dark:text-blue-400"
                        />
                      </div>
                      <span class="font-medium">{{ item.docsNum || 0 }}</span>
                    </div>
                    <div
                      class="flex items-center gap-1.5 text-xs text-gray-500 dark:text-gray-400"
                    >
                      <div
                        class="bg-green-100 dark:bg-green-900/30 p-1 rounded"
                      >
                        <Icon
                          icon="tabler:database"
                          class="text-green-600 dark:text-green-400"
                        />
                      </div>
                      <span class="font-medium"
                        >{{
                          (Number(item.totalSize || 0) / 1000000).toFixed(1)
                        }}MB</span
                      >
                    </div>
                  </div>
                  <div
                    v-if="item.embedStore"
                    class="text-xs px-2.5 py-1 bg-gradient-to-r from-blue-100 to-indigo-100 dark:from-blue-900/30 dark:to-indigo-900/30 text-blue-600 dark:text-blue-400 rounded-full font-medium"
                  >
                    {{ item.embedStore.name }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div
          v-if="list && list.length > 0"
          class="border-t border-gray-200 dark:border-gray-700 p-6 bg-gradient-to-r from-gray-50 to-blue-50/30 dark:from-gray-800/50 dark:to-blue-900/10"
        >
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.limit"
            :total="pagination.total"
            :layout="
              appStore.device === 'mobile'
                ? 'prev, pager, next'
                : 'total, sizes, prev, pager, next, jumper'
            "
            :small="appStore.device === 'mobile'"
            background
            class="justify-center sm:justify-end"
            @current-change="fetch"
            @size-change="fetch"
          />
        </div>
      </div>
    </div>

    <Edit ref="editRef" @reload="fetch" />
  </div>
</template>

<style lang="scss" scoped>
.knowledge-app {
  box-sizing: border-box;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
  max-height: calc(100vh - 60px);
}

.header-section {
  background: linear-gradient(
    135deg,
    rgba(59, 130, 246, 0.08),
    rgba(99, 102, 241, 0.08),
    rgba(168, 85, 247, 0.06)
  );

  :global(.dark) & {
    background: linear-gradient(
      135deg,
      rgba(59, 130, 246, 0.15),
      rgba(99, 102, 241, 0.15),
      rgba(168, 85, 247, 0.1)
    );
  }
}

.main-content {
  box-sizing: border-box;
}

.knowledge-card {
  &:hover {
    transform: translateY(-6px) scale(1.02);
  }
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

// 响应式优化
@media (max-width: 640px) {
  .knowledge-app {
    height: calc(100vh - 120px);
    max-height: calc(100vh - 120px);
  }

  .knowledge-card {
    .opacity-0 {
      opacity: 1 !important;
    }
  }
}
</style>
