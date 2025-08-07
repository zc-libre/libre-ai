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
    key: 'name'
  },
  {
    title: '文档链接',
    key: 'url'
  },
  {
    title: '文档来源',
    key: 'sliceNum',
    width: 90,
    align: 'center',
    render(row) {
      return h(
        ElTag,
        {
          size: 'small',
          type: row.type == 'UPLOAD' ? 'success' : 'info'
        },
        {
          default: () => (row.type == 'UPLOAD' ? '上传' : '录入')
        }
      );
    }
  },
  {
    title: '切片数量',
    key: 'sliceNum',
    width: 90,
    align: 'center'
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
          type: row.sliceStatus == true ? 'success' : 'info'
        },
        {
          default: () => (row.sliceStatus == true ? '已训练' : '未训练')
        }
      );
    }
  },
  {
    title: '文件大小',
    key: 'size',
    width: 100,
    align: 'center',
    render(rowData) {
      return (Number(rowData.size) / 1000000).toFixed(2) + ' MB';
    }
  }
];

export const searchSchemas: FormSchema[] = [
  {
    field: 'name',
    component: 'ElInput',
    label: '文档名称',
    componentProps: {
      placeholder: '请输入文档名称'
    }
  }
];

export const formSchemas: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    component: 'ElInput',
    isHidden: true
  },
  {
    field: 'name',
    component: 'ElInput',
    label: '文档名称',
    componentProps: {
      placeholder: '请输入文档名称'
    },
    rules: [{ required: true, message: '请输入文档名称', trigger: ['blur'] }]
  },
  {
    field: 'content',
    component: 'ElInput',
    label: '文档内容',
    componentProps: {
      placeholder: '请输入文档内容',
      type: 'textarea',
      autosize: {
        minRows: 8,
        maxRows: 12
      }
    }
  }
];
