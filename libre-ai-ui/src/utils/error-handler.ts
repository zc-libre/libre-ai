import { ElMessage } from 'element-plus';

// 错误类型枚举
export enum ErrorType {
  NETWORK_ERROR = 'NETWORK_ERROR',
  API_ERROR = 'API_ERROR',
  VALIDATION_ERROR = 'VALIDATION_ERROR',
  SSE_CONNECTION_ERROR = 'SSE_CONNECTION_ERROR',
  TIMEOUT_ERROR = 'TIMEOUT_ERROR'
}

// 错误处理配置
interface ErrorConfig {
  showToast?: boolean;
  logToConsole?: boolean;
  retryable?: boolean;
  maxRetries?: number;
}

const defaultConfig: ErrorConfig = {
  showToast: true,
  logToConsole: true,
  retryable: false,
  maxRetries: 3
};

// 错误信息映射
const errorMessages: Record<ErrorType, string> = {
  [ErrorType.NETWORK_ERROR]: '网络连接异常，请检查网络设置',
  [ErrorType.API_ERROR]: 'API调用失败，请稍后重试',
  [ErrorType.VALIDATION_ERROR]: '数据格式错误，请检查输入内容',
  [ErrorType.SSE_CONNECTION_ERROR]: '实时连接中断，正在尝试重连...',
  [ErrorType.TIMEOUT_ERROR]: '请求超时，请稍后重试'
};

// 全局错误处理器
export class ErrorHandler {
  private static retryMap = new Map<string, number>();

  static handle(
    error: any,
    type: ErrorType = ErrorType.API_ERROR,
    config: ErrorConfig = {}
  ) {
    const finalConfig = { ...defaultConfig, ...config };
    const message = this.getErrorMessage(error, type);

    if (finalConfig.logToConsole) {
      console.error(`[${type}]`, error);
    }

    if (finalConfig.showToast) {
      ElMessage.error(message);
    }

    return {
      type,
      message,
      originalError: error,
      retryable: finalConfig.retryable
    };
  }

  private static getErrorMessage(error: any, type: ErrorType): string {
    // 优先使用服务器返回的错误信息
    if (error?.response?.data?.message) {
      return error.response.data.message;
    }

    if (error?.message) {
      return error.message;
    }

    return errorMessages[type] || '未知错误';
  }

  // SSE连接错误处理
  static handleSSEError(
    error: any,
    onRetry?: () => void,
    maxRetries: number = 3
  ) {
    const retryKey = 'sse-connection';
    const currentRetries = this.retryMap.get(retryKey) || 0;

    if (currentRetries < maxRetries && onRetry) {
      this.retryMap.set(retryKey, currentRetries + 1);
      setTimeout(
        () => {
          ElMessage.info(`正在重连... (${currentRetries + 1}/${maxRetries})`);
          onRetry();
        },
        Math.pow(2, currentRetries) * 1000
      ); // 指数退避
    } else {
      this.retryMap.delete(retryKey);
      this.handle(error, ErrorType.SSE_CONNECTION_ERROR, {
        showToast: true,
        retryable: false
      });
    }
  }

  // 清除重试计数
  static clearRetryCount(key: string = 'sse-connection') {
    this.retryMap.delete(key);
  }

  // 网络状态检查
  static checkNetworkStatus(): boolean {
    if (typeof navigator !== 'undefined' && 'onLine' in navigator) {
      return navigator.onLine;
    }
    return true;
  }

  // API响应验证
  static validateApiResponse(response: any): boolean {
    if (!response) return false;
    if (response.code !== undefined && response.code !== 200) {
      return false;
    }
    return true;
  }
}

// 便捷的错误处理函数
export const handleError = ErrorHandler.handle;
export const handleSSEError = ErrorHandler.handleSSEError;
export const validateApiResponse = ErrorHandler.validateApiResponse;
