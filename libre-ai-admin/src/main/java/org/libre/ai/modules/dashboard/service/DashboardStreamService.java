package org.libre.ai.modules.dashboard.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.libre.ai.modules.dashboard.assistant.OptimizeDashboardAiAssistant;
import org.libre.ai.modules.dashboard.assistant.StreamDashboardAiAssistant;
import org.libre.ai.modules.dashboard.dto.OptimizeRequest;
import org.libre.ai.modules.dashboard.prompt.DashboardPromptBuilder;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.prompt.DashboardPromptTemplate;
import org.libre.ai.modules.rag.core.provider.ModelProvider;
import org.libre.ai.modules.rag.entity.AigcApp;
import org.libre.ai.modules.rag.exception.ServiceException;
import org.libre.ai.modules.rag.service.AigcAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Objects;

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

	private static final String DASHBOARD_APP_ID = "1955447497396629506";

	private static final String DASHBOARD_OPTIMIZE_APP_ID = "1955818928569774082";

	private final AigcAppService aigcAppService;

	private final ModelProvider modelProvider;

	private final DashboardPromptBuilder dashboardPromptBuilder;

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
		log.debug("使用AIGC应用配置生成Dashboard，应用ID: {}, request: {}", DASHBOARD_APP_ID, request);
		// 获取Dashboard应用配置
		AigcApp dashboardApp = aigcAppService.getById(DASHBOARD_APP_ID);
		if (Objects.isNull(dashboardApp)) {
			log.error("Dashboard应用未找到，请确保已执行初始化脚本");
			throw new RuntimeException("Dashboard应用未初始化，请执行dashboard_app_init.sql脚本");
		}
		// 获取模型
		String modelId = dashboardApp.getModelId();
		checkModel(modelId);
		// 获取流式模型
		StreamingChatModel streamingChatModel = modelProvider.stream(modelId);
		// 构建提示词
		String systemPrompt = dashboardApp.getSystemPrompt();
		String prompt = dashboardPromptBuilder.buildPrompt(request, dashboardApp.getUserPromptTemplate());
		// 创建流式AI助手
		StreamDashboardAiAssistant assistant = AiServices.builder(StreamDashboardAiAssistant.class)
			.streamingChatModel(streamingChatModel)
			.systemMessageProvider((obj) -> systemPrompt)
		//	.chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
			.build();
		// 调用AI生成
		return assistant.generateDashboardFlux(prompt);
	}

	@Override
	public Flux<String> optimizeDashboard(OptimizeRequest request) {
		AigcApp optimizeDashboardApp = aigcAppService.getById(DASHBOARD_OPTIMIZE_APP_ID);
		if (Objects.isNull(optimizeDashboardApp)) {
			log.error("应用未找到，请确保已执行初始化脚本");
			throw new ServiceException("应用未初始化，请执行dashboard_app_init.sql脚本");
		}
		// 获取模型
		String modelId = optimizeDashboardApp.getModelId();
		checkModel(modelId);
		StreamingChatModel streamingChatModel = modelProvider.stream(modelId);
		String systemPrompt = optimizeDashboardApp.getSystemPrompt();
		OptimizeDashboardAiAssistant assistant = AiServices.builder(OptimizeDashboardAiAssistant.class)
			.streamingChatModel(streamingChatModel)
			.systemMessageProvider((obj) -> systemPrompt)
			.chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
			.build();

		String prompt = dashboardPromptBuilder.buildOptimizeDashboardPrompt(request,
				optimizeDashboardApp.getUserPromptTemplate());

		return assistant.optimizeDashboardStream(request.getConversationId(), prompt);
	}

	private static void checkModel(String modelId) {
		if (StringUtils.isBlank(modelId)) {
			log.error("应用未配置模型");
			throw new RuntimeException("应用未配置模型");
		}
	}

}