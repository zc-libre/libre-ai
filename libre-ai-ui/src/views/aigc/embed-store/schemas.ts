import type { FormSchema } from '@/components/Form';

// 提供商配置常量
enum ProviderEnum {
  Redis = 'redis',
  PgVector = 'pgvector',
  Milvus = 'milvus'
}

const ProviderConst = [
  { label: 'Redis', value: ProviderEnum.Redis },
  { label: 'PgVector', value: ProviderEnum.PgVector },
  { label: 'Milvus', value: ProviderEnum.Milvus }
];

const baseSchemas: FormSchema[] = [
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
    rules: [{ required: true, message: '请输入数据库别名', trigger: ['blur'] }],
    componentProps: {
      placeholder: '请输入数据库别名'
    }
  },
  {
    field: 'provider',
    label: '供应商',
    component: 'ElSelect',
    componentProps: {
      disabled: true,
      placeholder: '请选择供应商',
      options: ProviderConst.map(item => ({
        label: item.label,
        value: item.value
      }))
    }
  },
  {
    field: 'host',
    label: '数据库地址',
    component: 'ElInput',
    rules: [{ required: true, message: '请输入数据库地址', trigger: ['blur'] }],
    componentProps: {
      placeholder: '请输入数据库地址'
    }
  },
  {
    field: 'port',
    label: '数据库端口',
    component: 'ElInputNumber',
    rules: [
      {
        type: 'number',
        required: true,
        message: '请输入数据库端口',
        trigger: ['blur']
      }
    ],
    componentProps: {
      placeholder: '请输入数据库端口',
      min: 1,
      max: 65535,
      controlsPosition: 'right',
      style: { width: '100%' }
    }
  }
];

export function getSchemas(provider: string): FormSchema[] {
  const schemas = JSON.parse(JSON.stringify(baseSchemas));

  // 认证配置
  const authSchemas: FormSchema[] = [
    {
      field: 'username',
      label: '数据库用户名',
      component: 'ElInput',
      componentProps: {
        placeholder: '请输入数据库用户名'
      }
    },
    {
      field: 'password',
      label: '数据库密码',
      component: 'ElInput',
      componentProps: {
        type: 'password',
        showPassword: true,
        placeholder: '请输入数据库密码'
      }
    }
  ];

  // 向量维度配置
  const dimensionSchema: FormSchema = {
    field: 'dimension',
    label: '向量维度',
    labelMessage: '向量维度必须与模型和存储库一致',
    component: 'ElInputNumber',
    rules: [
      {
        type: 'number',
        required: true,
        message: '请输入向量维度',
        trigger: ['blur']
      }
    ],
    componentProps: {
      placeholder: '请输入向量维度',
      min: 128,
      max: 4096,
      controlsPosition: 'right',
      style: { width: '100%' }
    }
  };

  // 根据不同提供商添加特定字段
  if (provider === ProviderEnum.Redis) {
    // Redis 认证字段可选
    authSchemas.forEach(schema => {
      if (schema.rules) {
        schema.rules = [];
      }
    });

    const redisSchemas: FormSchema[] = [
      {
        field: 'databaseName',
        label: 'Redis库索引名',
        component: 'ElInput',
        rules: [
          { required: true, message: '请输入Redis库索引名', trigger: ['blur'] }
        ],
        componentProps: {
          placeholder: '请输入Redis库索引名'
        }
      }
    ];

    schemas.push(...redisSchemas, ...authSchemas, dimensionSchema);
  }

  if (provider === ProviderEnum.PgVector) {
    const pgvectorSchemas: FormSchema[] = [
      {
        field: 'databaseName',
        label: '数据库名',
        component: 'ElInput',
        rules: [
          { required: true, message: '请输入数据库名', trigger: ['blur'] }
        ],
        componentProps: {
          placeholder: '请输入数据库名'
        }
      },
      {
        field: 'tableName',
        label: '表名称',
        component: 'ElInput',
        rules: [{ required: true, message: '请输入表名称', trigger: ['blur'] }],
        componentProps: {
          placeholder: '请输入表名称'
        }
      }
    ];

    schemas.push(...pgvectorSchemas, ...authSchemas, dimensionSchema);
  }

  if (provider === ProviderEnum.Milvus) {
    const milvusSchemas: FormSchema[] = [
      {
        field: 'databaseName',
        label: '数据库名',
        component: 'ElInput',
        rules: [
          { required: true, message: '请输入数据库名', trigger: ['blur'] }
        ],
        componentProps: {
          placeholder: '请输入数据库名'
        }
      },
      {
        field: 'tableName',
        label: '表名称',
        component: 'ElInput',
        rules: [{ required: true, message: '请输入表名称', trigger: ['blur'] }],
        componentProps: {
          placeholder: '请输入表名称'
        }
      }
    ];

    schemas.push(...milvusSchemas, dimensionSchema);
  }

  // 状态配置
  const statusSchema: FormSchema = {
    field: 'isPerms',
    label: '启用状态',
    component: 'ElSwitch',
    defaultValue: true,
    componentProps: {
      activeText: '启用',
      inactiveText: '禁用',
      inlinePrompt: true
    }
  };

  schemas.push(statusSchema);

  return schemas;
}
