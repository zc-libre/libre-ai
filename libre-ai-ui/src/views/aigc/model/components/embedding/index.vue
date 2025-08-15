<script lang="ts" setup>
import { BasicTable, TableAction } from '@/components/Table';
import Edit from './edit.vue';
import { computed, h, reactive, ref } from 'vue';
import { getColumns } from './columns';
import { LLMProviders } from './consts';
import { del, list as getModels } from '@/api/aigc/model';
import { ElMessage, ElMessageBox, ElAlert, ElButton } from 'element-plus';
import { Icon } from '@iconify/vue';
import { Plus, Edit as EditIcon, Delete } from '@element-plus/icons-vue';
import { ModelTypeEnum } from '@/api/models';

const provider = ref(LLMProviders[0]?.model || '');
const actionRef = ref();
const editRef = ref();

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
          type: 'primary',
          icon: EditIcon,
          tooltip: '编辑配置',
          onClick: handleEdit.bind(null, record)
        },
        {
          type: 'danger',
          icon: Delete,
          tooltip: '删除',
          onClick: handleDel.bind(null, record)
        }
      ]
    });
  }
});

const columns = computed(() => {
  return getColumns();
});

const loadDataTable = async (params: any) => {
  if (provider.value === '') {
    provider.value = LLMProviders[0].model;
  }
  return await getModels({
    ...params,
    provider: provider.value,
    type: ModelTypeEnum.EMBEDDING
  });
};

async function handleAdd() {
  console.log('handleAdd called', provider.value, editRef.value);
  if (!editRef.value) {
    console.error('editRef is not available');
    return;
  }
  editRef.value.show({ provider: provider.value });
}

function handleEdit(record: any) {
  editRef.value.show(record);
}

function reloadTable() {
  actionRef.value.reload();
}

function handleDel(record: any) {
  ElMessageBox.confirm(
    `你确定删除 [${record.name}] 模型吗？删除之后不可再用该模型对话`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      await del(record.id);
      reloadTable();
      ElMessage.success('模型删除成功');
    })
    .catch(() => {
      // 用户取消删除
    });
}
</script>

<template>
  <div class="embedding-provider-container h-full flex flex-col">
    <!-- 顶部配置区域 -->
    <div
      class="config-header border-b border-gray-200 dark:border-gray-700 p-4 sm:p-6 bg-gradient-to-r from-gray-50 to-slate-50 dark:from-gray-800 dark:to-gray-900"
    >
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-3">
          <div
            class="icon-box p-2.5 bg-white dark:bg-gray-700 rounded-lg shadow-sm"
          >
            <Icon
              icon="ph:database"
              class="text-xl text-slate-600 dark:text-slate-400"
            />
          </div>
          <div>
            <h3 class="text-lg font-bold text-gray-900 dark:text-white">
              Embedding 向量模型配置
            </h3>
            <p class="text-sm text-gray-600 dark:text-gray-400 mt-0.5">
              配置文本向量化和语义搜索模型
            </p>
          </div>
        </div>
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          新增模型
        </el-button>
      </div>

      <!-- 提示信息 -->
      <el-alert class="custom-alert" type="info" :closable="false" show-icon>
        <template #title>
          <span class="text-sm"
            >为了实现向量数据库的动态切换，Embedding 供应商统一选择支持 1024
            维度的模型</span
          >
        </template>
      </el-alert>
    </div>

    <!-- 内容区域 -->
    <div class="config-content flex-1 flex gap-4 p-4 sm:p-6 min-h-0">
      <!-- 左侧供应商列表 -->
      <div class="provider-list-container w-64 flex-shrink-0 hidden lg:block">
        <div
          class="provider-card bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 h-full"
        >
          <div class="p-4 border-b border-gray-200 dark:border-gray-700">
            <h4 class="font-semibold text-gray-900 dark:text-white">
              模型供应商
            </h4>
          </div>
          <div class="p-3">
            <div class="space-y-2">
              <div
                v-for="item in LLMProviders"
                :key="item.model"
                :class="[
                  'provider-item px-3 py-2.5 rounded-lg cursor-pointer transition-all duration-200',
                  provider === item.model
                    ? 'bg-slate-100 dark:bg-slate-900/30 text-slate-600 dark:text-slate-400 font-semibold shadow-sm'
                    : 'hover:bg-gray-100 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-300'
                ]"
                @click="
                  () => {
                    provider = item.model;
                    reloadTable();
                  }
                "
              >
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-2">
                    <Icon
                      v-if="item.icon"
                      :icon="item.icon"
                      class="text-base"
                    />
                    <span class="text-sm">{{ item.name }}</span>
                  </div>
                  <Icon
                    v-if="provider === item.model"
                    icon="ep:check"
                    class="text-sm"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 移动端供应商选择器 -->
      <div class="provider-mobile-selector w-full mb-4 lg:hidden">
        <el-select
          v-model="provider"
          placeholder="选择供应商"
          class="w-full"
          @change="reloadTable"
        >
          <el-option
            v-for="item in LLMProviders"
            :key="item.model"
            :label="item.name"
            :value="item.model"
          />
        </el-select>
      </div>

      <!-- 右侧表格 - 与embed-store保持一致的结构 -->
      <div class="table-section flex-1 min-w-0">
        <BasicTable
          ref="actionRef"
          :actionColumn="actionColumn"
          :columns="columns"
          :pagination="false"
          :request="loadDataTable"
          :row-key="(row: any) => row.model"
          :single-line="false"
          theme="model-management"
        />
      </div>
    </div>

    <Edit ref="editRef" :provider="provider" @reload="reloadTable" />
  </div>
</template>

<style lang="scss" scoped>
@keyframes pulse {
  0%,
  100% {
    opacity: 0.5;
    transform: scale(1) rotate(0deg);
  }

  50% {
    opacity: 0.8;
    transform: scale(1.1) rotate(180deg);
  }
}

/* 响应式设计 */
@media (width <= 1024px) {
  .config-content {
    flex-direction: column;
  }
}

@media (width <= 768px) {
  .config-header {
    padding: 1rem;
  }

  .config-content {
    padding: 1rem;
  }
}

.embedding-provider-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 头部配置区域 */
.config-header {
  position: relative;
  flex-shrink: 0;
  overflow: hidden;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.config-header::before {
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  content: '';
  background: radial-gradient(
    circle,
    rgb(100 116 139 / 5%) 0%,
    transparent 70%
  );
  animation: pulse 20s ease-in-out infinite;
}

.icon-box {
  position: relative;
  z-index: 1;
  background: linear-gradient(135deg, #fff 0%, #f8fafc 100%);
  transition: all 0.3s ease;
}

.icon-box:hover {
  box-shadow: 0 4px 12px rgb(100 116 139 / 15%);
  transform: scale(1.1) rotate(-5deg);
}

/* 自定义提示框 */
.custom-alert {
  background: rgb(239 246 255 / 60%);
  backdrop-filter: blur(8px);
  border: 1px solid rgb(147 197 253 / 30%);
}

:deep(.custom-alert) {
  .el-alert__icon {
    color: #3b82f6;
  }
}

/* 内容区域 */
.config-content {
  overflow: hidden;
  background: linear-gradient(145deg, #fff 0%, #fafbfc 100%);
}

/* 供应商卡片 */
.provider-card {
  background: linear-gradient(145deg, #fff 0%, #f9fafb 100%);
  transition: all 0.3s ease;
}

.provider-card:hover {
  box-shadow: 0 8px 16px rgb(100 116 139 / 10%);
  transform: translateY(-2px);
}

.provider-item {
  position: relative;
  overflow: hidden;
}

.provider-item::before {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  content: '';
  background: linear-gradient(
    90deg,
    transparent,
    rgb(100 116 139 / 10%),
    transparent
  );
  transition: left 0.5s ease;
}

.provider-item:hover::before {
  left: 100%;
}

/* 表格容器 */
.model-table-wrapper {
  display: flex;
  flex-direction: column;
  background: linear-gradient(145deg, #fff 0%, #f8fafc 100%);
}

/* 表格样式现在由 BasicTable 的 model-management 主题提供 */

/* 暗色模式适配 */
html.dark {
  .config-header {
    background: linear-gradient(135deg, #1f2937 0%, #374151 100%);
  }
}
</style>
