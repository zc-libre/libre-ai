package org.libre.ai.modules.dashboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.assistant.StreamDashboardAiAssistant;
import org.libre.ai.modules.dashboard.prompt.DashboardPromptBuilder;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.prompt.DashboardPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 仪表板流式生成服务实现
 * <p>
 * 使用WebFlux响应式编程模型 集成LangChain4j的TokenStream实现流式AI生成 现在使用 DashboardPromptBuilder 和
 * LangChain4j PromptTemplate 来构建提示词
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardStreamService implements IDashboardStreamService {


	private final DashboardPromptBuilder dashboardPromptBuilder;

	private final DashboardAigcService dashboardAigcService;

	@Value("${dashboard.use-aigc-app:false}")
	private boolean useAigcApp;

	/**
	 * 流式生成仪表板代码
	 *
	 * 将LangChain4j的TokenStream转换为Reactor的Flux 实现背压控制和错误处理 使用 DashboardPromptBuilder
	 * 构建模板化的提示词
	 * @param request 生成请求
	 * @return 代码片段流
	 */
	@Override
	public Flux<String> generateDashboardStream(DashboardRequest request) {
		log.info("开始流式生成仪表板，配置: {}", request);

		return dashboardAigcService.generateDashboardWithAigcApp(request);
	}

}