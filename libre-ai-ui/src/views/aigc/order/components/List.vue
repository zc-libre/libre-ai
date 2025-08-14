<script lang="ts" setup>
import { h, reactive, ref } from 'vue';
import { BasicTable, TableAction } from '@/components/Table';
import { BasicForm, useForm } from '@/components/Form/index';
import { del, page as getPage } from '@/api/aigc/message';
import { columns, searchSchemas } from './columns';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';

const actionRef = ref();

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
          type: 'danger',
          icon: Delete,
          tooltip: '删除',
          onClick: handleDelete.bind(null, record)
        }
      ]
    });
  }
});

const [register, { getFieldsValue }] = useForm({
  colProps: { cols: '1 s:1 m:2 l:3 xl:4 2xl:4' },
  labelWidth: 80,
  schemas: searchSchemas,
  showAdvancedButton: false
});

const loadDataTable = async (res: any) => {
  return await getPage({ ...getFieldsValue(), ...res });
};

function reloadTable() {
  actionRef.value.reload();
}

function handleDelete(record: Recordable) {
  ElMessageBox.confirm('您想删除此条记录？', '提示', {
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

function handleReset(values: Recordable) {
  reloadTable();
}
</script>

<template>
  <div class="list-container">
    <!-- 搜索表单 -->
    <div class="bg-gray-50 dark:bg-gray-800/50 rounded-lg p-4 mb-6">
      <BasicForm
        class="order-search-form"
        @register="register"
        @reset="handleReset"
        @submit="reloadTable"
      />
    </div>

    <!-- 数据表格 -->
    <div
      class="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 overflow-hidden"
    >
      <BasicTable
        ref="actionRef"
        :actionColumn="actionColumn"
        :columns="columns"
        :request="loadDataTable"
        :row-key="(row: any) => row.id"
        :single-line="false"
        size="default"
        class="order-table"
      />
    </div>
  </div>
</template>

<style lang="scss" scoped>
.list-container {
  .order-search-form {
    :deep(.el-form-item__label) {
      font-weight: 500;
      color: var(--el-text-color-regular);
    }
  }

  .order-table {
    :deep(.el-table) {
      border: none;
    }

    :deep(.el-table__header-wrapper) {
      border-radius: 8px 8px 0 0;
    }

    :deep(.el-table__body-wrapper) {
      border-radius: 0 0 8px 8px;
    }

    :deep(.el-table th) {
      background-color: var(--el-bg-color);
      color: var(--el-text-color-primary);
      font-weight: 600;
    }
  }
}
</style>
