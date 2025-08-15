import { getToken } from '@/utils/auth';
import { ErrorHandler, ErrorType } from '@/utils/error-handler';

interface SSECallbacks {
  onSearchQuery?: (data: { searchQuery: string }) => void;
  onSearchResults?: (data: {
    searchResults: Chat.SearchResult[];
    searchUsageTime: number;
  }) => void;
  onDelta?: (delta: Chat.DeltaResponse) => void;
  onMessage?: (data: Chat.SSEResponse) => void;
  onComplete?: (data: Chat.SSEResponse) => void;
  onError?: (error: string) => void;
  onEnd?: () => void;
}

export async function fetchChatAPIProcessSSE(
  params: {
    roomId: number;
    uuid: number;
    prompt: string;
    uploadFileKeys?: string[];
    options?: Chat.ConversationRequest;
    regenerate?: boolean;
    signal?: AbortSignal;
  },
  callbacks: SSECallbacks
) {
  // 模拟假数据响应
  try {
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500));

    // 检查是否取消
    if (params.signal?.aborted) {
      callbacks.onError?.('canceled');
      return;
    }

    // 模拟搜索功能（如果prompt包含搜索关键词）
    if (
      params.prompt.toLowerCase().includes('搜索') ||
      params.prompt.toLowerCase().includes('search')
    ) {
      // 模拟搜索查询
      callbacks.onSearchQuery?.({ searchQuery: `搜索: ${params.prompt}` });

      await new Promise(resolve => setTimeout(resolve, 800));

      // 模拟搜索结果
      callbacks.onSearchResults?.({
        searchResults: [
          {
            title: '相关文档1',
            content: '这是搜索到的相关内容...',
            url: 'https://example.com/doc1',
            score: 0.95
          },
          {
            title: '相关文档2',
            content: '这是另一个相关内容...',
            url: 'https://example.com/doc2',
            score: 0.87
          }
        ],
        searchUsageTime: 800
      });
    }

    // 模拟思考过程（如果prompt包含复杂问题）
    const shouldReason =
      params.prompt.length > 20 ||
      params.prompt.includes('为什么') ||
      params.prompt.includes('如何') ||
      params.prompt.includes('解释');
    let reasoning = '';

    if (shouldReason) {
      const reasoningSteps = [
        '让我来分析一下这个问题...',
        '首先，我需要考虑几个关键要素：',
        '基于以上分析，我可以得出以下结论：'
      ];

      for (let step of reasoningSteps) {
        reasoning += step + '\n';
        callbacks.onDelta?.({ reasoning: step + '\n' });
        await new Promise(resolve => setTimeout(resolve, 300));

        if (params.signal?.aborted) {
          callbacks.onError?.('canceled');
          return;
        }
      }
    }

    // 生成模拟回答
    const mockResponses = [
      '这是一个很好的问题。让我为您详细解答...',
      '根据您的问题，我可以提供以下信息：',
      '基于我的理解，这里有几个要点需要说明：',
      '感谢您的提问，以下是我的回答：',
      '让我来帮您分析这个问题：'
    ];

    const baseResponse =
      mockResponses[Math.floor(Math.random() * mockResponses.length)];
    const additionalContent = `
    
1. 首先，这个问题涉及到多个方面的考虑
2. 其次，我们需要从实际应用的角度来看
3. 最后，建议您可以尝试以下几种方法

具体来说：
- 方法一：从基础开始，逐步深入
- 方法二：结合实践，理论与实际相结合  
- 方法三：持续学习和改进

希望这些信息对您有所帮助！如果您还有其他问题，随时可以继续提问。`;

    const fullResponse = baseResponse + additionalContent;
    const words = fullResponse.split('');
    let accumulatedText = '';

    // 模拟流式输出
    for (let i = 0; i < words.length; i++) {
      if (params.signal?.aborted) {
        callbacks.onError?.('canceled');
        return;
      }

      const char = words[i];
      accumulatedText += char;

      // 模拟打字效果，每几个字符发送一次
      if (i % 3 === 0 || i === words.length - 1) {
        callbacks.onDelta?.({ text: char.repeat((i % 3) + 1) });
        await new Promise(resolve =>
          setTimeout(resolve, Math.random() * 50 + 10)
        );
      }
    }

    // 模拟最终完成
    const mockUsage = {
      completion_tokens: Math.floor(Math.random() * 500) + 200,
      prompt_tokens: Math.floor(Math.random() * 100) + 50,
      total_tokens: 0,
      estimated: false
    };
    mockUsage.total_tokens =
      mockUsage.completion_tokens + mockUsage.prompt_tokens;

    const finalResponse = {
      text: accumulatedText,
      reasoning: reasoning || undefined,
      conversationId: `mock_conv_${Date.now()}`,
      id: `msg_${params.uuid}`,
      finish_reason: 'stop',
      detail: {
        usage: mockUsage,
        choices: [{ finish_reason: 'stop' }]
      }
    };

    // 先发送完整消息（兼容模式）
    callbacks.onMessage?.(finalResponse);

    await new Promise(resolve => setTimeout(resolve, 100));

    // 然后发送完成事件
    callbacks.onComplete?.(finalResponse);
  } catch (error: any) {
    if (error.name === 'AbortError' || params.signal?.aborted) {
      callbacks.onError?.('canceled');
    } else {
      callbacks.onError?.(error.message || '模拟请求失败');
    }
  } finally {
    callbacks.onEnd?.();
  }
}

export async function fetchChatResponseHistory(
  roomId: number,
  uuid: number,
  historyIndex: number
) {
  // 从本地存储获取历史记录
  const savedChats = localStorage.getItem(`chat_${roomId}`);
  if (savedChats) {
    const chats = JSON.parse(savedChats);
    const chat = chats.find((c: Chat.Chat) => c.uuid === uuid);
    if (chat) {
      return { data: chat };
    }
  }
  return { data: null };
}
