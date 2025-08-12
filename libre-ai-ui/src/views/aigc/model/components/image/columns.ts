import { ProviderEnum } from '@/views/aigc/model/provider';

export const baseColumns = [
  {
    title: '模型别名',
    key: 'name'
  },
  {
    title: '模型版本',
    key: 'model'
  }
];

export const openaiColumns = [
  ...baseColumns,
  {
    title: 'Api Key',
    key: 'apiKey'
  }
];

export function getColumns(provider: string) {
  switch (provider) {
    case ProviderEnum.OPENAI: {
      return openaiColumns;
    }
    case ProviderEnum.GEMINI: {
      return openaiColumns;
    }
  }
  return [];
}
