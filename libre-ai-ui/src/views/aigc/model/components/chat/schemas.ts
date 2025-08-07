import type { FormSchema } from '@/components/Form';
import { LLMProviders } from './consts';
import { getModels, ProviderEnum } from '@/views/aigc/model/provider';
import { ModelTypeEnum } from '@/api/models';
import { isNullOrWhitespace } from '@/utils/is';

const baseSchemas: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    component: 'ElInput',
    isHidden: true
  },
  {
    field: 'type',
    label: 'type',
    component: 'ElInput',
    isHidden: true,
    defaultValue: ModelTypeEnum.CHAT
  },
  {
    field: 'provider',
    label: 'LLM供应商',
    component: 'ElSelect',
    isHidden: true,
    componentProps: {
      placeholder: 'LLM供应商',
      options: LLMProviders.map(item => ({
        label: item.name,
        value: item.model
      }))
    }
    // rules: [{ required: true, message: '请选择LLM供应商', trigger: ['blur'] }],
  },
  {
    field: 'name',
    label: '模型别名',
    component: 'ElInput',
    rules: [{ required: true, message: '请输入模型别名', trigger: ['blur'] }],
    componentProps: {
      placeholder: '请输入模型别名'
    }
  },
  {
    field: 'apiKey',
    label: 'Api Key',
    labelMessage: '模型的ApiKey',
    component: 'ElInput',
    // rules: [{ required: true, message: '请输入API Key', trigger: ['blur'] }],
    componentProps: {
      placeholder: '请输入Api Key'
    }
  },
  {
    field: 'responseLimit',
    label: '回复上限',
    labelMessage:
      '控制模型输出的Tokens长度上限。通常 100 Tokens 约等于150个中文汉字',
    component: 'ElSlider',
    rules: [
      {
        type: 'number',
        required: true,
        message: '请输入回复上限',
        trigger: ['blur']
      }
    ],
    componentProps: {
      showTooltip: true,
      modelValue: 2000,
      step: 1,
      min: 1,
      max: 8192
    }
  },
  {
    field: 'temperature',
    label: '生成随机性',
    labelMessage:
      '调高参数会使得模型的输出更多样性和创新性，反之降低参数将会减少多样性',
    component: 'ElSlider',
    rules: [
      {
        type: 'number',
        required: true,
        message: '请输入生成随机性',
        trigger: ['blur']
      }
    ],
    componentProps: {
      showTooltip: true,
      modelValue: 0.2,
      step: 0.05,
      min: 0,
      max: 2
    }
  },
  {
    field: 'topP',
    label: 'Top P',
    labelMessage:
      '模型在生成输出时会从概率最高的词汇开始选择，直到这些词汇的总概率累积达到Top p值。这样可以限制模型只选择这些高概率的词汇，从而控制输出内容的多样性。建议不要与"生成随机性"同时调整',
    component: 'ElSlider',
    rules: [
      { type: 'number', required: true, message: '请输入', trigger: ['blur'] }
    ],
    componentProps: {
      showTooltip: true,
      modelValue: 0.8,
      step: 0.1,
      min: 0,
      max: 1
    }
  }
];

export function getSchemas(provider: string) {
  const list = JSON.parse(JSON.stringify(baseSchemas));
  const modelSchema: any = {
    field: 'model',
    label: '模型版本',
    labelMessage: '该LLM供应商对应的模型版本号',
    component: 'ElSelect',
    rules: [{ required: true, message: '请选择模型', trigger: ['blur'] }],
    componentProps: {
      placeholder: '请选择模型版本（可以手动输入）',
      filterable: true,
      allowCreate: true,
      options: getModels(provider, LLMProviders)
    }
  };
  list.splice(1, 0, modelSchema);

  if (provider === ProviderEnum.Q_FAN) {
    list.splice(2, 0, {
      field: 'secretKey',
      label: 'Api Secret',
      labelMessage: '模型的ApiSecret',
      component: 'ElInput',
      rules: [
        { required: true, message: '请输入Api Secret', trigger: ['blur'] }
      ],
      componentProps: {
        placeholder: '请输入Api Secret'
      }
    });
  }

  let defaultValue: any = undefined;
  let labelMessage: any = '模型的基础请求URL地址（或中转地址）';
  let disabled = false;
  switch (provider) {
    case ProviderEnum.GITEEAI:
      disabled = true;
      defaultValue = 'https://ai.gitee.com/v1';
      labelMessage = '对于Gitee AI，此Url固定不可修改';
      break;
    case ProviderEnum.DEEPSEEK:
      disabled = true;
      defaultValue = 'https://api.deepseek.com/v1';
      labelMessage = '对于DeepSeek模型，此Url固定不可修改';
      break;
    case ProviderEnum.SILICON:
      disabled = true;
      defaultValue = 'https://api.siliconflow.cn/v1';
      labelMessage = '对于硅基流动模型，此Url固定不可修改';
      break;
    case ProviderEnum.DOUYIN:
      disabled = true;
      defaultValue = 'https://ark.cn-beijing.volces.com/api/v3';
      labelMessage = '对于抖音豆包模型，此Url固定不可修改';
      break;
    case ProviderEnum.YI:
      disabled = true;
      defaultValue = 'https://api.lingyiwanwu.com/v1';
      labelMessage = '对于零一模型，此Url固定不可修改';
      break;
    case ProviderEnum.SPARK:
      disabled = true;
      defaultValue = 'https://spark-api-open.xf-yun.com/v1';
      labelMessage = '对于讯飞星火大模型，此Url固定不可修改';
      break;
  }
  const baseUlrSchema: any = {
    field: 'baseUrl',
    label: 'Base Url',
    labelMessage,
    component: 'ElInput',
    defaultValue,
    componentProps: {
      disabled,
      placeholder: '请输入BaseUrl'
    },
    rules: [
      {
        required: false,
        trigger: ['blur'],
        validator: (_, value: string) => {
          if (!value) {
            return;
          }
          const urlRegex =
            /^(https?:\/\/)?((([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}|localhost|(\d{1,3}\.){3}\d{1,3})(:\d{1,5})?(\/.*)?)$/;
          if (isNullOrWhitespace(value) || urlRegex.test(value)) {
            return true;
          }
          return new Error('URL格式错误');
        }
      }
    ]
  };
  list.splice(3, 0, baseUlrSchema);
  return list;
}
