package org.libre.ai.modules.dashboard.service;

import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.dto.GenerationResult;
import reactor.core.publisher.Flux;

/**
 * AI代码生成服务接口 - 使用LangChain4j AiServices
 *
 * 遵循 SRP（单一职责原则）：专注于AI生成功能 遵循 OCP（开闭原则）：通过接口扩展新的AI模型支持 使用 LangChain4j AiServices
 * 实现声明式AI服务
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
public interface IAIGenerationService {

	/**
	 * 使用AI生成仪表板代码
	 * @param request 生成请求
	 * @return 生成结果
	 */
	GenerationResult generateCode(DashboardRequest request);

}