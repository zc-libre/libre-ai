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

const router = useRouter();
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
  const data: any = await getPage({ ...searchForm, ...pagination.value });
  pagination.value.total = data.total;
  list.value = data.rows;
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
  <div class="view-container view-scrollable p-6 bg-gray-50 dark:bg-gray-900">
    <div class="w-full">
      <!-- 页面标题 -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">
          知识库管理
        </h1>
        <p class="text-gray-600 dark:text-gray-400">
          创建和管理您的知识库，构建智能问答系统
        </p>
      </div>

      <!-- 搜索表单 -->
      <div
        class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6 mb-6"
      >
        <el-form :model="searchForm" class="knowledge-search-form">
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item label="知识库名称" class="mb-0">
                <el-input
                  v-model="searchForm.name"
                  placeholder="请输入知识库名称"
                  clearable
                  @keyup.enter="fetch"
                />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8" :lg="6">
              <el-form-item class="mb-0">
                <div class="flex gap-2 w-full">
                  <el-button
                    type="primary"
                    class="flex-1 sm:flex-none"
                    @click="fetch"
                  >
                    <Icon icon="ep:search" class="mr-1" />
                    搜索
                  </el-button>
                  <el-button class="flex-1 sm:flex-none" @click="resetSearch"
                    >重置</el-button
                  >
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 知识库卡片网格 -->
      <div v-loading="loading" class="knowledge-grid">
        <div
          class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
        >
          <!-- 创建知识库卡片 -->
          <div
            class="knowledge-card-create bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 border-2 border-dashed border-blue-200 dark:border-blue-700 hover:border-blue-400 dark:hover:border-blue-500 transition-all duration-300 cursor-pointer rounded-xl p-6 flex flex-col items-center justify-center min-h-[200px] group"
            @click="handleAdd"
          >
            <div
              class="text-blue-500 dark:text-blue-400 mb-3 group-hover:scale-110 transition-transform duration-300"
            >
              <Icon icon="line-md:file-plus" class="text-4xl" />
            </div>
            <h3 class="font-semibold text-gray-700 dark:text-gray-300 mb-1">
              创建知识库
            </h3>
            <p class="text-sm text-gray-500 dark:text-gray-400 text-center">
              创建新的知识库来存储文档
            </p>
          </div>

          <!-- 空状态 -->
          <div
            v-if="!loading && (list == null || list.length == 0)"
            class="col-span-full"
          >
            <el-empty description="暂无知识库数据" />
          </div>

          <!-- 知识库卡片 -->
          <div
            v-for="item in list"
            :key="item.id"
            class="knowledge-card bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 hover:border-blue-300 dark:hover:border-blue-600 hover:shadow-lg dark:hover:shadow-gray-900/20 transition-all duration-300 cursor-pointer rounded-xl p-6 group"
            @click="handleInfo(item)"
          >
            <!-- 卡片头部 -->
            <div class="flex items-start justify-between mb-4">
              <div class="flex items-center gap-3">
                <div
                  class="bg-blue-100 dark:bg-blue-900/30 p-3 rounded-lg group-hover:bg-blue-200 dark:group-hover:bg-blue-900/50 transition-colors duration-300"
                >
                  <Icon
                    class="text-2xl text-blue-600 dark:text-blue-400"
                    icon="ep:document"
                  />
                </div>
                <div class="flex-1 min-w-0">
                  <h3
                    class="font-semibold text-gray-900 dark:text-white text-base truncate group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors duration-300"
                  >
                    {{ item.name }}
                  </h3>
                  <p
                    class="text-sm text-gray-500 dark:text-gray-400 mt-1 line-clamp-2"
                  >
                    {{ item.des || '暂无描述' }}
                  </p>
                </div>
              </div>

              <!-- 操作菜单 -->
              <el-dropdown
                trigger="click"
                placement="bottom-end"
                @command="key => onSelectAction(key, item)"
              >
                <el-button
                  text
                  size="small"
                  class="opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex-shrink-0"
                >
                  <Icon
                    class="text-lg text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                    icon="heroicons-outline:dots-horizontal"
                  />
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="option in actionOptions"
                      :key="option.value"
                      :command="option.value"
                    >
                      {{ option.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <!-- 卡片底部统计信息 -->
            <div
              class="flex items-center justify-between pt-4 border-t border-gray-100 dark:border-gray-700"
            >
              <div
                class="flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400"
              >
                <div class="flex items-center gap-1">
                  <Icon icon="mdi:tag-outline" />
                  <span>{{ item.docsNum || 0 }}个文档</span>
                </div>
                <div class="flex items-center gap-1">
                  <Icon icon="mdi:database-outline" />
                  <span
                    >{{
                      (Number(item.totalSize || 0) / 1000000).toFixed(2)
                    }}MB</span
                  >
                </div>
              </div>
              <div
                v-if="item.embedStore"
                class="text-xs text-blue-600 dark:text-blue-400 font-medium"
              >
                {{ item.embedStore.name }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div
        v-if="list && list.length > 0"
        class="flex justify-center sm:justify-end mt-8"
      >
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.limit"
          :total="pagination.total"
          :layout="
            $store.state.app.device === 'mobile'
              ? 'prev, pager, next'
              : 'total, sizes, prev, pager, next, jumper'
          "
          :small="$store.state.app.device === 'mobile'"
          background
          @current-change="fetch"
          @size-change="fetch"
        />
      </div>
    </div>

    <Edit ref="editRef" @reload="fetch" />
  </div>
</template>

<style lang="scss" scoped>
.knowledge-search-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--el-text-color-regular);
  }
}

.knowledge-grid {
  min-height: 300px;
}

.knowledge-card-create {
  &:hover {
    transform: translateY(-2px);
  }
}

.knowledge-card {
  &:hover {
    transform: translateY(-2px);
  }
}

// 响应式优化
@media (max-width: 640px) {
  .knowledge-card {
    .flex.items-start.justify-between {
      flex-direction: column;
      gap: 12px;
    }

    .opacity-0.group-hover\\:opacity-100 {
      opacity: 1;
    }
  }
}
</style>
