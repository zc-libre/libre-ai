// BasicColumn type definition
type BasicColumn = {
  title: string;
  key: string;
  align?: 'left' | 'center' | 'right';
  width?: string | number;
  render?: (row: any) => any;
};
import type { FormSchema } from '@/components/Form';
import { h } from 'vue';
import { ElTag } from 'element-plus';

export enum ProviderEnum {
  Redis = 'REDIS',
  PgVector = 'PGVECTOR',
  Milvus = 'MILVUS'
}

export const ProviderConst = [
  { label: 'Redis', value: ProviderEnum.Redis },
  { label: 'PgVector', value: ProviderEnum.PgVector },
  { label: 'Milvus', value: ProviderEnum.Milvus }
];

export function getProviderLabel(value: any) {
  const arr = ProviderConst.filter(i => i.value === value);
  if (arr === undefined || arr.length === 0) {
    return value;
  }
  return arr[0].label;
}

export const columns: BasicColumn[] = [
  {
    title: '数据库别名',
    key: 'name',
    align: 'center'
  },
  {
    title: '供应商',
    key: 'provider',
    align: 'center',
    width: '120',
    render(row) {
      return h(
        ElTag,
        {
          size: 'small',
          type: 'success'
        },
        {
          default: () => getProviderLabel(row.provider)
        }
      );
    }
  },
  {
    title: '向量纬度',
    key: 'dimension',
    align: 'center',
    width: '80',
    render(row) {
      return h(
        ElTag,
        {
          size: 'small',
          type: 'danger'
        },
        {
          default: () => row.dimension
        }
      );
    }
  },
  {
    title: '数据库地址',
    key: 'host',
    align: 'center',
    width: '110'
  },
  {
    title: '数据库端口',
    key: 'port',
    align: 'center',
    width: '100'
  },
  {
    title: '数据库用户名',
    key: 'username',
    align: 'center'
  },
  {
    title: '数据库密码',
    key: 'password',
    align: 'center'
  },
  {
    title: '数据库名',
    key: 'databaseName',
    align: 'center'
  },
  {
    title: '表名称',
    key: 'tableName',
    align: 'center'
  }
];

export const searchSchemas: FormSchema[] = [
  {
    field: 'name',
    component: 'ElInput',
    label: '数据库别名',
    componentProps: {
      placeholder: '请输入数据库别名'
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
    label: '数据库别名',
    component: 'ElInput',
    componentProps: {
      placeholder: '请输入数据库别名'
    },
    rules: [{ required: true, message: '请输入数据库别名', trigger: ['blur'] }]
  },
  {
    field: 'provider',
    component: 'ElSelect',
    label: '供应商',
    componentProps: {
      disabled: true,
      placeholder: '请选择供应商',
      options: ProviderConst
    },
    rules: [{ required: true, message: '请选择供应商', trigger: ['blur'] }]
  },
  {
    field: 'host',
    label: '数据库地址',
    component: 'ElInput',
    componentProps: {
      placeholder: '请输入数据库地址'
    },
    rules: [{ required: true, message: '请输入数据库地址', trigger: ['blur'] }]
  },
  {
    field: 'port',
    label: '数据库端口',
    component: 'ElInputNumber',
    componentProps: {
      placeholder: '请输入数据库端口'
    },
    rules: [
      {
        type: 'number',
        required: true,
        message: '请输入数据库端口',
        trigger: ['blur']
      }
    ]
  }
];

export function getSchemas(provider: string) {
  const schemas = JSON.parse(JSON.stringify(formSchemas));
  const authArr: any[] = [
    {
      field: 'username',
      label: '数据库用户名',
      component: 'ElInput',
      componentProps: {
        placeholder: '请输入数据库用户名'
      },
      rules: [
        { required: true, message: '请输入数据库用户名', trigger: ['blur'] }
      ]
    },
    {
      field: 'password',
      label: '数据库密码',
      component: 'ElInput',
      componentProps: {
        type: 'password',
        placeholder: '请输入数据库密码'
      },
      rules: [
        { required: true, message: '请输入数据库密码', trigger: ['blur'] }
      ]
    }
  ];
  const dimension: any = {
    field: 'dimension',
    label: '向量纬度',
    component: 'ElSelect',
    defaultValue: 1024,
    labelMessage:
      '慎重修改此参数，纬度高会消耗更多的算力，但纬度高并不代表搜索更精确',
    componentProps: {
      placeholder: '请输入向量纬度',
      options: [
        {
          label: '512',
          value: 512
        },
        {
          label: '768',
          value: 768
        },
        {
          label: '1024',
          value: 1024
        },
        {
          label: '1536',
          value: 1536
        }
      ]
    },
    rules: [
      {
        type: 'number',
        required: true,
        message: '请输入向量纬度',
        trigger: ['blur']
      }
    ]
  };

  if (provider === ProviderEnum.Redis) {
    authArr.forEach(i => (i.rules = undefined));
    const arr: any = [
      {
        field: 'databaseName',
        label: 'Redis库索引名',
        component: 'ElInput',
        componentProps: {
          placeholder: '请输入Redis库索引名'
        },
        rules: [
          { required: true, message: '请输入Redis库索引名', trigger: ['blur'] }
        ]
      }
    ];
    schemas.push(...arr, ...authArr, dimension);
  }

  if (provider === ProviderEnum.PgVector) {
    const arr: any = [
      {
        field: 'databaseName',
        label: '数据库名',
        component: 'ElInput',
        componentProps: {
          placeholder: '请输入数据库名'
        },
        rules: [
          { required: true, message: '请输入数据库名', trigger: ['blur'] }
        ]
      },
      {
        field: 'tableName',
        label: '表名称',
        component: 'ElInput',
        componentProps: {
          placeholder: '请输入表名称'
        },
        rules: [{ required: true, message: '请输入表名称', trigger: ['blur'] }]
      }
    ];
    schemas.push(...arr, ...authArr, dimension);
  }
  if (provider === ProviderEnum.Milvus) {
    const arr: any = [
      {
        field: 'databaseName',
        label: '数据库名',
        component: 'ElInput',
        componentProps: {
          placeholder: '请输入数据库名'
        },
        rules: [
          { required: true, message: '请输入数据库名', trigger: ['blur'] }
        ]
      },
      {
        field: 'tableName',
        label: '表名称',
        component: 'ElInput',
        componentProps: {
          placeholder: '请输入表名称'
        }
        // rules: [{ required: true, message: '请输入表名称', trigger: ['blur'] }],
      }
    ];
    schemas.push(...arr, dimension);
  }
  return schemas;
}
