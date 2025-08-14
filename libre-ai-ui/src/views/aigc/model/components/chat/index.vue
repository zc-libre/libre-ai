<script lang="ts" setup>
import { BasicTable, TableAction } from '@/components/Table';
import Edit from './edit.vue';
import { computed, h, reactive, ref } from 'vue';
import { getColumns } from './columns';
import { LLMProviders } from './consts';
import { del, list as getModels } from '@/api/aigc/model';
import {
  ElMessage,
  ElMessageBox,
  ElAlert,
  ElButton
} from 'element-plus';
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
  return getColumns(provider.value);
});

const loadDataTable = async (params: any) => {
  if (provider.value === '') {
    provider.value = LLMProviders[0].model;
  }
  return await getModels({
    ...params,
    provider: provider.value,
    type: ModelTypeEnum.CHAT
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
  <div class="chat-provider-container h-full flex flex-col">
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
              icon="lets-icons:chat"
              class="text-xl text-blue-600 dark:text-blue-400"
            />
          </div>
          <div>
            <h3 class="text-lg font-bold text-gray-900 dark:text-white">
              Chat 对话模型配置
            </h3>
            <p class="text-sm text-gray-600 dark:text-gray-400 mt-0.5">
              配置智能对话和文本生成模型
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
            >完全兼容 OpenAI 接口格式的模型都可在 OpenAI
            配置中使用，只需设置正确的 BaseURL</span
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
                    ? 'bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 font-semibold shadow-sm'
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
        />
      </div>
    </div>

    <Edit ref="editRef" :provider="provider" @reload="reloadTable" />
  </div>
</template>

<style lang="scss" scoped>
.chat-provider-container {
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

/* 自定义提示框 */
.custom-alert {
  background: rgba(239, 246, 255, 0.6);
  border: 1px solid rgba(147, 197, 253, 0.3);
  backdrop-filter: blur(8px);
}

:deep(.custom-alert) {
  .el-alert__icon {
    color: #3b82f6;
  }
}

/* 内容区域 */
.config-content {
  overflow: hidden;
}

/* 供应商卡片 */
.provider-card {
  transition: all 0.3s ease;
}

.provider-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.provider-item {
  position: relative;
  overflow: hidden;
}

.provider-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(59, 130, 246, 0.1),
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
}

/* 表格样式现在由 BasicTable 的 model-management 主题提供 */

/* 响应式设计 */
@media (max-width: 1024px) {
  .config-content {
    flex-direction: column;
  }

  .provider-mobile-selector {
    display: block;
  }
}

@media (max-width: 768px) {
  .config-header {
    padding: 1rem;
  }

  .config-content {
    padding: 1rem;
  }
}
</style>
