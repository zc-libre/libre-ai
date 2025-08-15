declare namespace Chat {
  interface ChatRoom {
    id?: string; // 数据库ID
    roomId: number;
    title: string;
    isEdit?: boolean;
    chatModel?: string;
    usingContext?: boolean;
    maxContextCount?: number;
    searchEnabled?: boolean;
    thinkEnabled?: boolean;
    systemPrompt?: string;
    temperature?: number;
    maxTokens?: number;
    loading?: boolean;
    all?: boolean;
  }

  interface Chat {
    uuid?: number;
    dateTime?: string;
    text?: string;
    images?: string[];
    inversion?: boolean;
    error?: boolean;
    loading?: boolean;
    conversationOptions?: ConversationRequest | null;
    requestOptions?: RequestOptions;
    responseCount?: number;
    searchQuery?: string;
    searchResults?: SearchResult[];
    searchUsageTime?: number;
    reasoning?: string;
    finish_reason?: string;
    usage?: {
      completion_tokens: number;
      prompt_tokens: number;
      total_tokens: number;
      estimated: boolean;
    };
  }

  interface ConversationRequest {
    conversationId?: string;
    parentMessageId?: string;
    maxContextCount?: number;
  }

  interface RequestOptions {
    prompt: string;
    options?: ConversationRequest | null;
  }

  interface SearchResult {
    title: string;
    url: string;
    snippet: string;
  }

  interface SSEResponse {
    id?: string;
    conversationId?: string;
    text?: string;
    detail?: {
      usage?: {
        completion_tokens: number;
        prompt_tokens: number;
        total_tokens: number;
        estimated: boolean;
      };
      choices?: Array<{
        finish_reason: string;
      }>;
    };
    searchQuery?: string;
    searchResults?: SearchResult[];
    searchUsageTime?: number;
    reasoning?: string;
    finish_reason?: string;
  }

  interface DeltaResponse {
    text?: string;
    reasoning?: string;
  }
}
