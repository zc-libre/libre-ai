import { ProviderEnum } from '@/views/aigc/model/provider';

export const LLMProviders: any[] = [
  {
    model: ProviderEnum.OPENAI,
    name: 'OpenAI',
    icon: 'simple-icons:openai',
    models: ['dall-e-2', 'dall-e-3']
  },
  {
    model: ProviderEnum.GEMINI,
    name: 'Gemini',
    icon: 'simple-icons:google',
    models: ['dall-e-2', 'dall-e-3']
  }
];
