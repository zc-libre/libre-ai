import { ref } from 'vue';

export function useColumns() {
  const columns = ref([
    {
      title: '秘钥',
      key: 'apiKeyDisplay',
      width: 200,
      align: 'center'
    },
    {
      title: '创建时间',
      key: 'createTime',
      align: 'center'
    }
  ]);

  return {
    columns
  };
}
