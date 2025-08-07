import { ref } from 'vue';

export function useColumns() {
  const columns = ref([
    {
      label: '秘钥',
      prop: 'apiKeyDisplay',
      minWidth: 200,
      align: 'center'
    },
    {
      label: '创建时间',
      prop: 'createTime',
      minWidth: 180,
      align: 'center'
    },
    {
      label: '操作',
      fixed: 'right',
      width: 160,
      slot: 'operation',
      align: 'center'
    }
  ]);

  return {
    columns
  };
}
