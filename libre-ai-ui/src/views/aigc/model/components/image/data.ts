import { ProviderEnum } from '@/views/aigc/model/provider';

export const LLMProviders: any[] = [
  {
    model: ProviderEnum.OPENAI,
    name: 'OpenAI',
    models: ['dall-e-2', 'dall-e-3']
  },
  {
    model: ProviderEnum.GEMINI,
    name: 'Gemini',
    models: ['dall-e-2', 'dall-e-3']
  }
];
