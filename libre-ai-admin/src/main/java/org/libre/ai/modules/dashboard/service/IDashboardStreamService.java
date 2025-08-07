package org.libre.ai.modules.dashboard.service;

import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import reactor.core.publisher.Flux;

/**
 * 仪表板流式生成服务接口
 *
 * 使用响应式编程模型，支持流式数据处理 遵循响应式流规范（Reactive Streams）
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
public interface IDashboardStreamService {

	/**
	 * 流式生成仪表板代码
	 *
	 * 返回响应式流，实时推送生成的代码片段
	 * @param request 生成请求参数
	 * @return 代码片段流
	 */
	Flux<String> generateDashboardStream(DashboardRequest request);

}