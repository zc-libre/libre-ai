import { http } from '@/utils/http';

export type ThemeConfig = {
  name: string;
  colors: {
    primary: string;
    secondary: string;
    accent: string;
    background?: string;
    surface?: string;
    text?: string;
  };
};

export type DashboardRequest = {
  purpose: string;
  purposeDetail?: string; // 场景细节描述
  focusMetrics?: string; // 重点监控指标
  customRequirements?: string; // 用户补充需求
  layout: string;
  theme: ThemeConfig;
  components: string[];
  componentConfigs?: any[]; // 组件配置列表
  options?: {
    codeStyle?: string;
    responsive?: boolean;
    includeData?: boolean;
    additionalRequirements?: string; // 保留向后兼容
  };
};

// 优化请求接口
export type OptimizeRequest = {
  conversationId: string;
  currentHtml: string;
  userRequest: string;
  originalConfig?: DashboardRequest;
};

export type GenerationResult = {
  html: string;
  css: string;
  javascript: string;
  previewUrl?: string;
  metadata?: {
    generatedAt: string;
    modelUsed: string;
    tokensUsed: number;
    generationTime: number;
  };
};

export type DashboardTemplates = {
  purposes: PurposeOption[];
  layouts: LayoutOption[];
  themes: ThemeOption[];
  components: ComponentOption[];
};

export type PurposeOption = {
  id: string;
  icon: string;
  title: string;
  description: string;
  components: string[];
};

export type LayoutOption = {
  id: string;
  preview: string;
  title: string;
  description: string;
  responsive: boolean;
};

export type ThemeOption = {
  id: string;
  name: string;
  primary: string;
  secondary: string;
  accent: string;
  background: string;
  text: string;
};

export type ComponentOption = {
  id: string;
  icon: string;
  title: string;
  purposes: string[];
  previewCode: string;
};

export type DashboardHistory = {
  id: string;
  config: DashboardRequest;
  generatedHtml: string;
  generatedCss?: string;
  generatedJs?: string;
  createdAt: string;
  previewImage?: string;
};

export type HistoryParams = {
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
};

export type HistoryResponse = {
  data: DashboardHistory[];
  total: number;
  page: number;
  size: number;
  pages: number;
};

/**
 * 生成仪表板代码
 */
export function generateDashboard(data: DashboardRequest) {
  return http.request('post', '/dashboard/generate', { data });
}

/**
 * 流式生成仪表板代码
 * @param data 请求参数
 * @param onChunk 接收数据块的回调函数
 * @param onComplete 完成回调
 * @param onError 错误回调
 */
export async function generateDashboardStream(
  data: DashboardRequest,
  onChunk: (chunk: string) => void,
  onComplete?: (fullContent: string) => void,
  onError?: (error: Error) => void
): Promise<AbortController> {
  const abortController = new AbortController();

  try {
    // 1. 发起 POST 请求
    const response = await fetch('/api/dashboard/generate-stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream' // 明确告诉服务器期望流式响应
      },
      body: JSON.stringify(data),
      signal: abortController.signal
    });

    // 2. 检查响应是否成功
    if (!response.ok) {
      let errorMessage = `请求失败: ${response.status}`;
      try {
        const errorText = await response.text();
        if (errorText) {
          const errorJson = JSON.parse(errorText);
          errorMessage = errorJson.message || errorJson.error || errorMessage;
        }
      } catch {
        // 忽略解析错误
      }
      throw new Error(errorMessage);
    }

    // 3. 获取响应体的可读流 (ReadableStream)
    const reader = response.body?.getReader();
    if (!reader) {
      throw new Error('Response body is not readable');
    }

    // 4. 使用 TextDecoder 将 Uint8Array 块解码为字符串
    const decoder = new TextDecoder('utf-8');
    let fullContent = '';
    let buffer = ''; // 用于存储不完整的数据行

    // 5. 循环读取流中的数据
    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        // 处理剩余的缓冲区数据
        if (buffer.startsWith('data:')) {
          let content = buffer.slice(5);
          // *** 添加：将转义的换行符和回车符还原 ***
          content = content.replace(/\\n/g, '\n').replace(/\\r/g, '\r');
          fullContent += content;
          onChunk(content);
        }

        // 后处理：修复可能的 SVG 路径换行问题
        // const processedContent = postProcessSvgPaths(fullContent);
        console.log('Dashboard generation stream finished.');
        onComplete?.(fullContent);
        break;
      }

      // 将接收到的数据块 (Uint8Array) 解码成字符串
      const chunk = decoder.decode(value, { stream: true });

      // 将新数据添加到缓冲区
      buffer += chunk;
      // console.log("bufferValue: ", buffer);
      // 处理完整的行
      const lines = buffer.split('\n\n');
      // 保留最后一个可能不完整的行
      buffer = lines.pop() || '';

      // 处理每个完整的行
      for (const line of lines) {
        if (line.startsWith('data:')) {
          // 提取 data: 后面的实际内容
          let content = line.slice(5);
          // *** 添加：将转义的换行符和回车符还原 ***
          content = content.replace(/\\n/g, '\n').replace(/\\r/g, '\r');
          fullContent += content;
          onChunk(content);
        }
      }
    }

    console.log('Full dashboard content length:', fullContent.length);
    return abortController;
  } catch (error: any) {
    if (error.name !== 'AbortError') {
      console.error('Error fetching streaming dashboard:', error);
      const errorMessage = error.message || '生成失败，请稍后重试';
      onError?.(new Error(errorMessage));
    }
  }

  return abortController;
}

/**
 * 后处理函数：修复 SVG path 属性中的换行符问题
 */
function postProcessSvgPaths(content: string): string {
  // 修复 SVG path 属性中的换行符问题
  // 使用更精确的正则表达式来处理 d 属性内的换行符
  return content.replace(/(<path[^>]*\bd\s*=\s*"[^"]*?)(\n)([^"]*?")/g, '$1$3');
}

/**
 * 获取所有模板配置
 */
export function getTemplates() {
  return http.request('get', '/dashboard/templates');
}

/**
 * 获取看板用途选项
 */
export function getPurposes() {
  return http.request('get', '/dashboard/purposes');
}

/**
 * 获取布局选项
 */
export function getLayouts() {
  return http.request('get', '/dashboard/layouts');
}

/**
 * 获取主题选项
 */
export function getThemes() {
  return http.request('get', '/dashboard/themes');
}

/**
 * 获取组件选项
 */
export function getComponents() {
  return http.request('get', '/dashboard/components');
}

/**
 * 保存生成历史
 */
export function saveHistory(data: {
  config: DashboardRequest;
  generatedHtml: string;
  generatedCss?: string;
  generatedJs?: string;
  previewImage?: string;
}) {
  return http.request('post', '/dashboard/history', { data });
}

/**
 * 获取历史记录
 */
export function getHistory(params?: HistoryParams) {
  return http.request('get', '/dashboard/history', { params });
}

/**
 * 流式优化Dashboard
 * @param request 优化请求
 * @param onChunk 接收数据片段的回调
 * @param onComplete 完成回调
 * @param onError 错误回调
 * @returns AbortController 用于中止请求
 */
export async function optimizeDashboardStream(
  request: OptimizeRequest,
  onChunk: (chunk: string) => void,
  onComplete: (fullContent: string) => void,
  onError: (error: Error) => void
): Promise<AbortController> {
  const abortController = new AbortController();

  try {
    const response = await fetch('/api/dashboard/optimize-stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      signal: abortController.signal,
      body: JSON.stringify(request)
    });

    if (!response.ok) {
      throw new Error(`优化请求失败: ${response.statusText}`);
    }

    const reader = response.body?.getReader();
    if (!reader) {
      throw new Error('无法获取响应流');
    }

    const decoder = new TextDecoder();
    let fullContent = '';
    let buffer = '';

    // 异步处理流数据
    (async () => {
      try {
        while (true) {
          const { done, value } = await reader.read();
          if (done) {
            // 处理最后可能剩余的数据
            if (buffer.trim()) {
              if (buffer.startsWith('data:')) {
                let content = buffer.slice(5);
                content = content.replace(/\\n/g, '\n').replace(/\\r/g, '\r');
                fullContent += content;
                onChunk(content);
              }
            }
            break;
          }

          const chunk = decoder.decode(value, { stream: true });
          
          // 将新数据添加到缓冲区
          buffer += chunk;
          
          // 处理完整的行
          const lines = buffer.split('\n\n');
          // 保留最后一个可能不完整的行
          buffer = lines.pop() || '';

          // 处理每个完整的行
          for (const line of lines) {
            if (line.startsWith('data:')) {
              // 提取 data: 后面的实际内容
              let content = line.slice(5);
              // 将转义的换行符和回车符还原
              content = content.replace(/\\n/g, '\n').replace(/\\r/g, '\r');
              fullContent += content;
              onChunk(content);
            }
          }
        }

        onComplete(fullContent);
      } catch (error: any) {
        if (error.name !== 'AbortError') {
          onError(error);
        }
      }
    })();

    return abortController;
  } catch (error: any) {
    onError(error);
    return abortController;
  }
}

/**
 * 批量删除历史记录
 */
export function deleteHistory(ids: string[]) {
  return http.request('delete', '/dashboard/history/batch', { data: ids });
}

/**
 * 健康检查
 */
export function checkHealth() {
  return http.request('get', '/dashboard/health');
}
