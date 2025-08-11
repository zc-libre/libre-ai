package org.libre.ai.modules.dashboard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.assistant.StreamDashboardAiAssistant;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.dto.OptimizeRequest;
import org.libre.ai.modules.dashboard.service.IDashboardStreamService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * 仪表板生成器流式API控制器
 * <p>
 * 使用WebFlux实现响应式流处理 支持Server-Sent Events (SSE)格式输出
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@RestController
@RequestMapping("/api/dashboard")
@Slf4j
@RequiredArgsConstructor
public class DashboardStreamController {

	private final IDashboardStreamService dashboardStreamService;

	private final StreamDashboardAiAssistant streamDashboardAiAssistant;

	/**
	 * 流式生成仪表板代码
	 * <p>
	 * 使用WebFlux的Flux实现流式响应 返回SSE格式的事件流，兼容前端接收
	 * @param request 生成请求参数
	 * @return 流式响应
	 */
	@PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> generateStream(@RequestBody @Valid DashboardRequest request) {
		log.info("收到流式仪表板生成请求: {}", request);
		return dashboardStreamService.generateDashboardStream(request).map(item -> {
			item = escapeJson(item);
			log.debug("完整内容：{}", item);
			return item;
		});
	}

	/**
	 * 流式优化仪表板代码
	 * <p>
	 * 支持对话式迭代优化，保持会话上下文
	 * @param request 优化请求参数
	 * @return 流式响应
	 */
	@PostMapping(value = "/optimize-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> optimizeStream(@RequestBody @Valid OptimizeRequest request) {
		log.info("收到仪表板优化请求: conversationId={}, userRequest={}", request.getConversationId(), request.getUserRequest());

		return streamDashboardAiAssistant
			.optimizeDashboardStream(request.getConversationId(), request.getCurrentHtml(), request.getUserRequest())
			.map(item -> {
				item = escapeJson(item);
				log.debug("优化内容片段：{}", item.length() > 100 ? item.substring(0, 100) + "..." : item);
				return item;
			})
			.doOnComplete(() -> log.info("优化完成: conversationId={}", request.getConversationId()))
			.doOnError(error -> log.error("优化失败: conversationId={}, error={}", request.getConversationId(),
					error.getMessage()));
	}

	/**
	 * 转义JSON特殊字符
	 */
	private String escapeJson(String str) {
		if (str == null) {
			return "";
		}
		return str.replace("\n", "\\n").replace("\r", "\\r");
	}

}