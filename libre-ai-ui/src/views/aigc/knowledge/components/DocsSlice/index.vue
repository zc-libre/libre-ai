<script lang="ts" setup>
import { h, onMounted, reactive, ref } from 'vue';
import { BasicTable, TableAction } from '@/components/Table';
import { BasicForm, useForm } from '@/components/Form';
import { del, page as getPage } from '@/api/aigc/slice';
import { columns, searchSchemas } from './columns';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import { list } from '@/api/aigc/docs';

const router = useRouter();
const actionRef = ref();
const docsList = ref();

const actionColumn = reactive({
  width: 70,
  title: '操作',
  key: 'action',
  fixed: 'right',
  align: 'center',
  render(record: any) {
    return h(TableAction as any, {
      actionStyle: 'text',
      actions: [
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
onMounted(async () => {
  docsList.value = await list({});
});

const loadDataTable = async (res: any) => {
  const knowledgeId = router.currentRoute.value.params.id;
  return await getPage({ ...getFieldsValue(), ...res, knowledgeId });
};

function reloadTable() {
  actionRef.value.reload();
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
function handleSelectDocs(val: string) {
  console.log(val);
}
</script>

<template>
  <el-card>
    <BasicForm @register="register" @reset="handleReset" @submit="reloadTable">
      <template #docsSlot="{ model, field }">
        <el-select
          v-model="model[field]"
          filterable
          clearable
          placeholder="请选择文档查询"
          @change="handleSelectDocs"
        >
          <el-option
            v-for="item in docsList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </template>
    </BasicForm>

    <BasicTable
      ref="actionRef"
      :actionColumn="actionColumn"
      :columns="columns"
      :request="loadDataTable"
      :row-key="(row: any) => row.id"
      :single-line="false"
      :size="'small'"
    />
  </el-card>
</template>

<style lang="less" scoped></style>
