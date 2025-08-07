import { ProviderEnum } from '@/views/aigc/model/provider';

export const baseColumns = [
  {
    title: '模型别名',
    key: 'name'
  },
  {
    title: '模型版本',
    key: 'model',
    width: '140'
  },
  {
    title: '回复上限',
    key: 'responseLimit',
    width: '100'
  },
  {
    title: '生成随机性',
    key: 'temperature',
    width: '100'
  },
  {
    title: 'Top P',
    key: 'topP',
    width: '100'
  }
];

export const openaiColumns = [
  ...baseColumns,
  {
    title: 'Api Key',
    key: 'apiKey'
  }
];

export const ollamaColumns = [
  ...baseColumns,
  {
    title: 'Base Url',
    key: 'baseUrl'
  }
];

export const qfanColumns = [
  ...baseColumns,
  {
    title: 'Api Key',
    key: 'apiKey'
  }
];

export const qwenColumns = [
  ...baseColumns,
  {
    title: 'Api Key',
    key: 'apiKey'
  }
];
export const zhipuColumns = [
  ...baseColumns,
  {
    title: 'Api Key',
    key: 'apiKey'
  }
];

export function getColumns(provider: string) {
  switch (provider) {
    case ProviderEnum.OLLAMA: {
      return ollamaColumns;
    }
    case ProviderEnum.Q_FAN: {
      return qfanColumns;
    }
    case ProviderEnum.Q_WEN: {
      return qwenColumns;
    }
    case ProviderEnum.ZHIPU: {
      return zhipuColumns;
    }
  }
  return openaiColumns;
}
