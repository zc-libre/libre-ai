package org.libre.ai.modules.dashboard.exception;

/**
 * AI服务异常
 * <p>
 * 专门处理AI调用相关的异常情况 遵循异常设计最佳实践：明确的异常类型和信息
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
public class AIServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AIServiceException(String message) {
		super(message);
	}

	public AIServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public AIServiceException(Throwable cause) {
		super(cause);
	}

}