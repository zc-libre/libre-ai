import { ProviderEnum } from '@/views/aigc/model/provider';

export const LLMProviders: any[] = [
  {
    model: ProviderEnum.OPENAI,
    name: 'OpenAI',
    icon: 'simple-icons:openai',
    models: ['gpt-3.5-turbo', 'gpt-4', 'gpt-4-32k', 'gpt-4-turbo', 'gpt-4o']
  },
  {
    model: ProviderEnum.DEEPSEEK,
    name: 'DeepSeek',
    icon: 'tabler:brand-open-source',
    models: ['deepseek-chat', 'deepseek-coder']
  },
  {
    model: ProviderEnum.GEMINI,
    name: 'Gemini',
    icon: 'simple-icons:google',
    models: ['gemini-1.5-pro']
  },
  {
    model: ProviderEnum.CLAUDE,
    name: 'Claude',
    icon: 'simple-icons:anthropic',
    models: [
      'claude-4-opus',
      'claude-3-opus-20240229',
      'claude-4-sonnet',
      'claude-3-haiku'
    ]
  }
];
