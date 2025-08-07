import type { FormSchema } from '@/components/Form';

// Local BasicColumn type definition
type BasicColumn = {
  title: string;
  key: string;
  align?: 'left' | 'center' | 'right';
  width?: string | number;
  render?: (row: any) => any;
};

export const columns: BasicColumn[] = [
  {
    title: '用户名',
    key: 'username',
    align: 'center'
  },
  {
    title: '窗口标题',
    key: 'title',
    align: 'center'
  },
  {
    title: '对话次数',
    key: 'chatTotal',
    align: 'center',
    width: 180
  },
  {
    title: 'Token消耗量',
    key: 'tokenUsed',
    align: 'center',
    width: 180
  },
  {
    title: '最后一次对话时间',
    key: 'endTime',
    align: 'center',
    width: 180
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    align: 'center'
  }
];

export const searchSchemas: FormSchema[] = [
  {
    field: 'text',
    component: 'ElInput',
    label: '内容',
    componentProps: {
      placeholder: '请输入内容'
    }
  }
];
