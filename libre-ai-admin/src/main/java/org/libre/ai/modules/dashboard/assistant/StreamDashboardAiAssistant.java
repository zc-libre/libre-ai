package org.libre.ai.modules.dashboard.assistant;

import reactor.core.publisher.Flux;

/**
 * Dashboard AI Assistant 接口 使用LangChain4j AiServices实现声明式AI服务
 *
 * 遵循SRP原则：专注于AI交互接口定义 遵循ISP原则：接口隔离，只定义Dashboard生成相关方法 体现声明式编程：通过注解定义AI行为
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
public interface StreamDashboardAiAssistant {

	/**
	 * 流式生成仪表板代码 - 使用请求对象 使用@SystemMessage定义AI角色和要求 使用@UserMessage定义用户输入 自动处理JSON结构化输出
	 */
	Flux<String> generateDashboardFlux(String prompt);



}