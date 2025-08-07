<script lang="ts" setup>
import { h, reactive, ref } from 'vue';
import { BasicTable, TableAction } from '@/components/Table';
import { BasicForm, useForm } from '@/components/Form';
import { del, page as getPage, reEmbed } from '@/api/aigc/docs';
import { columns, searchSchemas } from './columns';
import Edit from './edit.vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter();
const actionRef = ref();
const editRef = ref();

const actionColumn = reactive({
  width: 150,
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
          icon: 'ep:refresh',
          onClick: handleReEmbed.bind(null, record)
        },
        {
          type: 'info',
          icon: 'ep:edit',
          onClick: handleEdit.bind(null, record)
        },
        {
          type: 'danger',
          icon: 'ep:delete',
          onClick: handleDelete.bind(null, record)
        }
      ]
    });
  }
});

const [register, { getFieldsValue }] = useForm({
  colProps: { cols: '1 s:1 m:2 l:3 xl:4 2xl:4' },
  labelWidth: 80,
  showAdvancedButton: false,
  schemas: searchSchemas
});

const loadDataTable = async (res: any) => {
  const knowledgeId = router.currentRoute.value.params.id;
  return await getPage({ ...getFieldsValue(), ...res, knowledgeId });
};

function reloadTable() {
  actionRef.value.reload();
}

function handleEdit(record: Recordable) {
  editRef.value.show('', record.id);
}

function handleReEmbed(record: Recordable) {
  ElMessageBox.confirm(
    `您确定重新向量化该文档？此操作将会删除Vector向量库中旧数据`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      await reEmbed(record.id);
      ElMessage.success('文档向量化解析成功');
      reloadTable();
    })
    .catch(() => {});
}

function handleDelete(record: Recordable) {
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

function handleReset(values: Recordable) {
  reloadTable();
}
</script>

<template>
  <el-card>
    <BasicForm
      @register="register"
      @reset="handleReset"
      @submit="reloadTable"
    />

    <BasicTable
      ref="actionRef"
      :actionColumn="actionColumn"
      :columns="columns"
      :request="loadDataTable"
      :row-key="(row: any) => row.id"
      :single-line="false"
      :size="'small'"
    />

    <Edit ref="editRef" @reload="reloadTable" />
  </el-card>
</template>

<style lang="less" scoped></style>
