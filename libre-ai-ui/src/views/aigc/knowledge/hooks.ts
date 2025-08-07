import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { del } from '@/api/aigc/knowledge';

export function useColumns() {
  const columns = [
    { type: 'selection', width: 50 },
    {
      label: '知识库名称',
      prop: 'name',
      minWidth: 150
    },
    {
      label: '描述',
      prop: 'des',
      minWidth: 200
    },
    {
      label: '文档数量',
      prop: 'docCount',
      width: 100
    },
    {
      label: '向量模型',
      prop: 'embedModel',
      width: 150,
      formatter: ({ embedModel }) => embedModel?.name || '-'
    },
    {
      label: '向量数据库',
      prop: 'embedStore',
      width: 150,
      formatter: ({ embedStore }) => embedStore?.name || '-'
    },
    {
      label: '创建时间',
      prop: 'createTime',
      width: 160
    },
    {
      label: '操作',
      width: 200,
      slot: 'operation'
    }
  ];

  return { columns };
}

export function useKnowledge() {
  const loading = ref(false);
  const formRef = ref();
  const formVisible = ref(false);

  async function handleDelete(row: any, fetchData: () => void) {
    try {
      await ElMessageBox.confirm(
        `确定要删除知识库 "${row.name}" 吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      );
      await del(row.id);
      ElMessage.success('删除成功');
      fetchData();
    } catch {
      // 用户取消
    }
  }

  return {
    loading,
    formRef,
    formVisible,
    handleDelete
  };
}
