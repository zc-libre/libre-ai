<script lang="ts" setup>
import { reactive, ref, h } from 'vue';
import { Icon } from '@iconify/vue';
import { createApi, del, list as getApiList } from '@/api/aigc/appApi';
import { ElMessage, ElMessageBox, ElButton } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import { copyToClip } from '@/utils/copy';
import { BasicTable, TableAction } from '@/components/Table';
import { useColumns } from './hooks';
import { hideKey } from '@/api/models';
import { Plus, Delete } from '@element-plus/icons-vue';

const emit = defineEmits(['reload']);
const props = defineProps({
  channel: {
    type: String,
    required: true
  }
});

const ms = ElMessage;
const route = useRoute();
const router = useRouter();
const actionRef = ref();

// 表格列配置
const { columns } = useColumns();

// 表格数据加载函数
const loadDataTable = async (params: any) => {
  const res = await getApiList({
    appId: route.params.id,
    channel: props.channel,
    ...params
  });
  // 处理后端返回的 R 对象
  const data = res?.result || res || [];
  return Array.isArray(data)
    ? data.map(item => ({
        ...item,
        apiKeyDisplay: hideKey(item.apiKey)
      }))
    : [];
};

// 新增秘钥
async function handleAdd() {
  ElMessageBox.confirm(
    '确定要创建新的API秘钥吗？创建后请妥善保管，避免泄露。',
    '创建API秘钥',
    {
      confirmButtonText: '确定创建',
      cancelButtonText: '取消',
      type: 'info'
    }
  )
    .then(async () => {
      await createApi(route.params.id, props.channel);
      ms.success('新增成功');
      reloadTable();
    })
    .catch(() => {
      // 用户取消创建
    });
}

// 刷新表格
function reloadTable() {
  actionRef.value?.reload();
}

// 复制秘钥
async function onCopy(row: any) {
  await copyToClip(row.apiKey);
  ms.success('秘钥复制成功');
}

// 删除秘钥
function handleDelete(row: any) {
  ElMessageBox.confirm(
    '你确定重置Key吗？删除后原Key将立即失效是，请谨慎操作',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      await del(row.id);
      ms.success('删除成功');
      reloadTable();
    })
    .catch(() => {});
}

// 操作列配置
const actionColumn = reactive({
  width: 160,
  title: '操作',
  key: 'action',
  fixed: 'right',
  align: 'center',
  render(record: any) {
    return h(TableAction as any, {
      actionStyle: 'circle',
      actions: [
        {
          type: 'success',
          icon: 'ep:copy-document',
          tooltip: '复制',
          onClick: () => onCopy(record)
        },
        {
          type: 'danger',
          icon: Delete,
          tooltip: '删除',
          onClick: () => handleDelete(record)
        }
      ]
    });
  }
});
</script>

<template>
  <div class="api-table-container h-full flex flex-col">
    <!-- 顶部配置区域 -->
    <div
      class="config-header border-b border-gray-200 dark:border-gray-700 p-4 sm:p-6 bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-gray-800 dark:to-gray-900"
    >
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-3">
          <div
            class="icon-box p-2.5 bg-white dark:bg-gray-700 rounded-lg shadow-sm"
          >
            <Icon
              icon="ic:round-key"
              class="text-xl text-blue-600 dark:text-blue-400"
            />
          </div>
          <div>
            <h3 class="text-lg font-bold text-gray-900 dark:text-white">
              API秘钥管理
            </h3>
            <p class="text-sm text-gray-600 dark:text-gray-400 mt-0.5">
              管理您的API访问秘钥
            </p>
          </div>
        </div>
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          创建秘钥
        </el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="config-content flex-1 p-4 sm:p-6 min-h-0">
      <!-- 表格区域 -->
      <div class="table-section flex-1 min-w-0">
        <BasicTable
          ref="actionRef"
          :actionColumn="actionColumn"
          :columns="columns"
          :pagination="false"
          :request="loadDataTable"
          :row-key="(row: any) => row.id"
          :single-line="false"
        />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.api-table-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 头部配置区域 */
.config-header {
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.config-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(59, 130, 246, 0.08) 0%,
    transparent 70%
  );
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.icon-box {
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.icon-box:hover {
  transform: scale(1.1) rotate(5deg);
}

/* 内容区域 */
.config-content {
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .config-header {
    padding: 1rem;
  }

  .config-content {
    padding: 1rem;
  }
}
</style>
