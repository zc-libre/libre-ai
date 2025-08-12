import { ProviderEnum } from '@/views/aigc/model/provider';

export const LLMProviders: any[] = [
  {
    model: ProviderEnum.OPENAI,
    name: 'OpenAI',
    models: [
      'text-embedding-3-small',
      'text-embedding-3-large',
      'text-embedding-ada-002'
    ]
  },
  {
    model: ProviderEnum.GEMINI,
    name: 'Gemini',
    models: ['gemini-embedding-001']
  },
  {
    model: ProviderEnum.OLLAMA,
    name: 'Ollama',
    models: ['text2vec-bge-large-chinese:latest']
  }
];
