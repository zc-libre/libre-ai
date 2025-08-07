export const CHANNEL = {
  API: 'CHANNEL_API',
  WEB: 'CHANNEL_WEB'
};

// Pure Admin Table 搜索表单配置
export const searchFormConfig = {
  schema: [
    {
      field: 'title',
      label: '标题',
      component: 'el-input',
      componentProps: {
        placeholder: '请输入Prompt标题查询',
        clearable: true
      }
    }
  ]
};

// Element Plus Form 配置（用于编辑对话框）
export const formSchemas = [
  {
    field: 'id',
    label: 'ID',
    component: 'el-input',
    isHidden: true
  },
  {
    field: 'name',
    label: '应用名称',
    component: 'el-input',
    rules: [{ required: true, message: '请输入应用名称', trigger: ['blur'] }]
  },
  {
    field: 'modelId',
    label: '关联模型',
    component: 'el-input',
    slot: 'modelIdSlot',
    rules: [{ required: true, message: '请选择关联模型', trigger: ['blur'] }]
  },
  {
    field: 'cover',
    label: '应用封面',
    slot: 'coverSlot',
    component: 'el-input'
  },
  {
    field: 'des',
    label: '应用描述',
    component: 'el-input',
    componentProps: {
      type: 'textarea',
      autosize: {
        minRows: 5,
        maxRows: 8
      },
      placeholder: '请输入应用描述'
    }
  }
];
