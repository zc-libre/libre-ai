package org.libre.ai.modules.dashboard.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard模块异常处理器
 * <p>
 * 遵循DRY原则：统一的异常处理逻辑 遵循KISS原则：简洁的错误响应格式 体现良好的用户体验：友好的错误提示
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@RestControllerAdvice(basePackages = "org.libre.ai.modules.dashboard")
@Slf4j
public class DashboardExceptionHandler {

	/**
	 * 统一处理参数验证相关异常 体现KISS原则：简化异常处理逻辑
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class,
			IllegalArgumentException.class })
	public R<String> handleValidationException(Exception e) {
		log.warn("参数验证失败: {}", e.getMessage());

		String errorMessage = "参数验证失败";

		if (e instanceof MethodArgumentNotValidException) {
			var errors = ((MethodArgumentNotValidException) e).getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());
			errorMessage = "参数验证失败: " + String.join("; ", errors);
		}
		else if (e instanceof BindException) {
			var errors = ((BindException) e).getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());
			errorMessage = "数据绑定失败: " + String.join("; ", errors);
		}
		else if (e instanceof IllegalArgumentException) {
			errorMessage = e.getMessage();
		}

		return R.fail(HttpStatus.BAD_REQUEST.value(), errorMessage);
	}

	/**
	 * 处理AI服务异常
	 *
	 * 专门处理AI调用相关的异常
	 * @param e AI服务异常
	 * @return 错误响应
	 */
	@ExceptionHandler(AIServiceException.class)
	public R<String> handleAIServiceException(AIServiceException e) {
		log.error("AI服务异常", e);

		return R.fail(HttpStatus.BAD_GATEWAY.value(), "AI代码生成服务暂时不可用，请稍后重试: " + e.getMessage());
	}

	/**
	 * 统一处理系统级异常 体现KISS原则：简化异常分类
	 */
	@ExceptionHandler({ RuntimeException.class, org.springframework.dao.DataAccessException.class })
	public R<String> handleSystemException(Exception e) {
		log.error("系统异常", e);

		if (e instanceof org.springframework.dao.DataAccessException) {
			return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "数据操作失败，请稍后重试");
		}

		return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务处理异常，请稍后重试");
	}

	/**
	 * 处理通用异常
	 *
	 * 兜底异常处理，确保系统不会因为未处理的异常而崩溃
	 * @param e 通用异常
	 * @return 错误响应
	 */
	@ExceptionHandler(Exception.class)
	public R<String> handleGenericException(Exception e) {
		log.error("系统异常", e);

		return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误，请联系管理员");
	}

}