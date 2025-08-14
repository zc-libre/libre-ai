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
import { Plus, Search, Refresh, Grid, List } from '@element-plus/icons-vue';
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
const searchQuery = ref('');
const viewMode = ref<'grid' | 'list'>('grid');
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
  const response: any = await getPage({ name: searchQuery.value, ...pagination.value });
  pagination.value.total = response.result.total;
  list.value = response.result.rows;
  loading.value = false;
}

function resetSearch() {
  searchQuery.value = '';
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
  <div class="view-container knowledge-app bg-bg_color">
    <!-- 顶部操作栏 -->
    <div class="header-section flex-shrink-0 w-full px-4 sm:px-6 lg:px-8 py-4">
      <div
        class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4"
      >
        <!-- 左侧标题 -->
        <div class="header-info">
          <h1 class="page-title flex items-center gap-2">
            <el-icon class="title-icon" :size="28" color="#6366F1">
              <Icon icon="fluent:document-database-24-regular" />
            </el-icon>
            知识库管理
          </h1>
          <p class="page-subtitle">创建和管理您的知识库，构建智能问答系统</p>
        </div>

        <!-- 右侧操作区 -->
        <div class="header-actions flex flex-wrap items-center gap-3">
          <!-- 搜索框 -->
          <div class="search-wrapper">
            <el-input
              v-model="searchQuery"
              placeholder="搜索知识库..."
              :prefix-icon="Search"
              clearable
              class="search-input"
              @keyup.enter="fetch"
            />
          </div>

          <!-- 视图切换 -->
          <el-button-group class="view-toggle">
            <el-button
              :type="viewMode === 'grid' ? 'primary' : 'default'"
              :icon="Grid"
              @click="viewMode = 'grid'"
            />
            <el-button
              :type="viewMode === 'list' ? 'primary' : 'default'"
              :icon="List"
              @click="viewMode = 'list'"
            />
          </el-button-group>

          <!-- 刷新按钮 -->
          <el-button :icon="Refresh" :loading="loading" @click="fetch">
            刷新
          </el-button>

          <!-- 创建按钮 -->
          <el-button type="primary" :icon="Plus" @click="handleAdd">
            新建知识库
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content flex-1 px-4 sm:px-6 lg:px-8 pb-4 overflow-auto flex justify-between flex-col">

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="knowledge-grid">
          <div
            v-for="i in 6"
            :key="`skeleton-${i}`"
            class="knowledge-card skeleton-card"
          >
            <el-skeleton :rows="3" animated>
              <template #template>
                <div class="skeleton-header">
                  <el-skeleton-item variant="circle" class="skeleton-icon" />
                </div>
                <div class="skeleton-body">
                  <el-skeleton-item variant="h3" style="width: 60%" />
                  <el-skeleton-item variant="text" />
                  <el-skeleton-item variant="text" style="width: 80%" />
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>
      </div>

      <!-- 知识库网格视图 -->
      <div
        v-else-if="viewMode === 'grid' && list && list.length > 0"
        class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 pt-5"
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

      <!-- 知识库列表视图 -->
      <div
        v-else-if="viewMode === 'list' && list && list.length > 0"
        class="knowledge-list pt-5"
      >
        <div
          v-for="item in list"
          :key="item.id"
          class="list-item bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 hover:border-blue-400 dark:hover:border-blue-500 hover:shadow-lg transition-all duration-300 cursor-pointer rounded-xl p-6 mb-4"
          @click="handleInfo(item)"
        >
          <div class="flex items-center gap-4">
            <div
              class="bg-gradient-to-br from-blue-100 to-indigo-100 dark:from-blue-900/30 dark:to-indigo-900/30 p-3 rounded-xl flex-shrink-0"
            >
              <Icon
                class="text-xl text-blue-600 dark:text-blue-400"
                icon="fluent:document-database-24-regular"
              />
            </div>

            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-3 mb-2">
                <h3
                  class="font-semibold text-gray-900 dark:text-white text-lg truncate"
                >
                  {{ item.name }}
                </h3>
                <el-tag
                  v-if="item.embedStore"
                  size="small"
                  type="info"
                  class="flex-shrink-0"
                >
                  {{ item.embedStore.name }}
                </el-tag>
              </div>
              <p
                class="text-sm text-gray-600 dark:text-gray-400 line-clamp-1 mb-3"
              >
                {{ item.des || '暂无描述' }}
              </p>
              <div class="flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400">
                <div class="flex items-center gap-1.5">
                  <div class="bg-blue-100 dark:bg-blue-900/30 p-1 rounded">
                    <Icon
                      icon="tabler:files"
                      class="text-blue-600 dark:text-blue-400"
                    />
                  </div>
                  <span class="font-medium">文档: {{ item.docsNum || 0 }}</span>
                </div>
                <div class="flex items-center gap-1.5">
                  <div class="bg-green-100 dark:bg-green-900/30 p-1 rounded">
                    <Icon
                      icon="tabler:database"
                      class="text-green-600 dark:text-green-400"
                    />
                  </div>
                  <span class="font-medium">
                    大小: {{ (Number(item.totalSize || 0) / 1000000).toFixed(1) }}MB
                  </span>
                </div>
              </div>
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
                class="!p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg"
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
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else-if="list.length === 0" class="flex items-center justify-center h-full min-h-[400px]">
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
            {{ searchQuery ? '没有找到匹配的知识库' : '暂无知识库' }}
          </h3>
          <p class="text-gray-500 dark:text-gray-400 mb-6 max-w-md">
            {{
              searchQuery
                ? '请尝试其他搜索关键词'
                : '开始创建您的第一个知识库，构建智能问答系统'
            }}
          </p>
          <el-button
            v-if="!searchQuery"
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

      <!-- 分页 -->
      <div
        v-if="list && list.length > 0"
        class="pagination-wrapper pt-6"
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

    <Edit ref="editRef" @reload="fetch" />
  </div>
</template>

<style lang="scss" scoped>
.view-container {
  height: 100%;
  overflow: hidden;
}

.knowledge-app {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

// 头部样式
.header-section {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-info {
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
    display: flex;
    align-items: center;
    gap: 8px;

    .title-icon {
      filter: drop-shadow(0 2px 4px rgba(99, 102, 241, 0.2));
    }
  }

  .page-subtitle {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.header-actions {
  .search-wrapper {
    .search-input {
      width: 240px;
    }
  }

  .view-toggle {
    :deep(.el-button) {
      padding: 8px 12px;
    }
  }
}

// 主内容区域
.main-content {
  background: #f5f7fa;
  box-sizing: border-box;
}

// 知识库卡片
.knowledge-card {
  &:hover {
    transform: translateY(-6px) scale(1.02);
  }
}


// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;

  @media (min-width: 640px) {
    justify-content: flex-end;
  }
}

// 响应式优化
@media (max-width: 640px) {
  .header-section {
    .header-actions {
      width: 100%;

      .search-wrapper {
        flex: 1;

        .search-input {
          width: 100%;
        }
      }
    }
  }
}
</style>
