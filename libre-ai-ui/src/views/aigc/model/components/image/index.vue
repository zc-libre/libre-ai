<script lang="ts" setup>
import { BasicTable, TableAction } from '@/components/Table';
import Edit from './edit.vue';
import { computed, h, nextTick, reactive, ref } from 'vue';
import { getColumns } from './columns';
import { LLMProviders } from './data';
import { del, list as getModels } from '@/api/aigc/model';
import {
  ElMessage,
  ElMessageBox,
  ElMenu,
  ElMenuItem,
  ElAlert,
  ElButton
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { ModelTypeEnum } from '@/api/models';

const provider = ref(LLMProviders[0]?.model || '');
const actionRef = ref();
const editRef = ref();

const actionColumn = reactive({
  width: 100,
  title: '操作',
  key: 'action',
  fixed: 'right',
  align: 'center',
  render(record: any) {
    return h(TableAction as any, {
      actionStyle: 'text',
      actions: [
        {
          type: 'info',
          icon: 'ep:edit',
          onClick: handleEdit.bind(null, record)
        },
        {
          type: 'danger',
          icon: 'ep:delete',
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
    type: ModelTypeEnum.TEXT_IMAGE
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
  <div class="image-provider-container">
    <div class="flex flex-col lg:flex-row gap-4 lg:gap-6">
      <!-- 侧边栏 - 模型供应商列表 -->
      <div class="w-full lg:w-64 flex-shrink-0">
        <div class="model-provider-sidebar rounded-lg p-3 sm:p-4">
          <h3 class="font-semibold mb-3 flex items-center gap-2">
            <Icon icon="ph:image" class="text-purple-500 text-lg" />
            <span class="hidden sm:inline">文生图模型列表</span>
            <span class="sm:hidden">Image</span>
          </h3>
          <el-menu
            :default-active="provider"
            class="model-provider-menu border-none bg-transparent"
            @select="
              index => {
                provider = index;
                reloadTable();
              }
            "
          >
            <el-menu-item
              v-for="item in LLMProviders"
              :key="item.model"
              :index="item.model"
              class="rounded-md mb-1 text-sm font-medium"
            >
              {{ item.name }}
            </el-menu-item>
          </el-menu>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="flex-1 min-w-0">
        <el-alert
          class="mb-4"
          title="鉴于很多模型的文生图效果很差甚至没有，这里只建议使用OpenAI的DALL-E模型"
          type="info"
          :closable="false"
          show-icon
        />

        <!-- 表格容器 -->
        <div class="model-table-container rounded-lg overflow-hidden">
          <BasicTable
            ref="actionRef"
            :actionColumn="actionColumn"
            :columns="columns"
            :pagination="false"
            :request="loadDataTable"
            :row-key="(row: any) => row.model"
            :single-line="false"
            class="model-custom-table"
          >
            <template #tableTitle>
              <div
                class="model-table-header flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 p-3 sm:p-4"
              >
                <h4 class="font-semibold text-base">模型配置列表</h4>
                <el-button type="primary" @click="handleAdd">
                  <Icon icon="ep:plus" class="mr-1" />
                  <span class="hidden sm:inline">新增模型</span>
                  <span class="sm:hidden">新增</span>
                </el-button>
              </div>
            </template>
          </BasicTable>
        </div>

        <Edit ref="editRef" :provider="provider" @reload="reloadTable" />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '../styles.scss';

.image-provider-container {
  padding: 0;
}
</style>
