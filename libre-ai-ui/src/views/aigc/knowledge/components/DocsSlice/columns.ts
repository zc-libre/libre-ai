import type { FormSchema } from '@/components/Form';

// Local BasicColumn type definition
type BasicColumn = {
  title: string;
  key: string;
  align?: 'left' | 'center' | 'right';
  width?: string | number;
  render?: (row: any) => any;
};
import { h } from 'vue';
import { ElTag } from 'element-plus';

export const columns: BasicColumn[] = [
  {
    title: '文档名称',
    key: 'name',
    width: 150
  },
  {
    title: '字符数',
    key: 'wordNum',
    width: 110,
    align: 'center'
  },
  {
    title: '切片内容',
    key: 'content'
  },
  {
    title: '切片状态',
    key: 'status',
    width: 100,
    align: 'center',
    render(row) {
      return h(
        ElTag,
        {
          size: 'small',
          type: row.status == 1 ? 'success' : 'info'
        },
        {
          default: () => (row.status == 1 ? '已训练' : '未训练')
        }
      );
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 160
  }
];

export const searchSchemas: FormSchema[] = [
  {
    field: 'docsId',
    component: 'ElInput',
    label: '所属文档',
    slot: 'docsSlot',
    componentProps: {
      placeholder: '请选择文档'
    }
  }
];
