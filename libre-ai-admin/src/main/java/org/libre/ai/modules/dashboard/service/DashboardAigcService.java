package org.libre.ai.modules.dashboard.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.libre.ai.modules.dashboard.assistant.StreamDashboardAiAssistant;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.prompt.DashboardPromptBuilder;
import org.libre.ai.modules.rag.core.provider.ModelProvider;
import org.libre.ai.modules.rag.entity.AigcApp;
import org.libre.ai.modules.rag.service.AigcAppService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dashboard与AIGC集成服务 负责将Dashboard功能与AIGC模块的应用配置集成
 *
 * @author AI Assistant
 * @since 2025-01-16
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardAigcService {

	private static final String DASHBOARD_APP_ID = "1955447497396629506";

	private final AigcAppService aigcAppService;

	private final ModelProvider modelProvider;

	private final DashboardPromptBuilder dashboardPromptBuilder;

	/**
	 * 使用AIGC应用配置生成Dashboard
	 * @param request Dashboard请求
	 * @return 流式响应
	 */
	public Flux<String> generateDashboardWithAigcApp(DashboardRequest request) {
		log.debug("使用AIGC应用配置生成Dashboard，应用ID: {}, request: {}", DASHBOARD_APP_ID, request);
		// 获取Dashboard应用配置
		AigcApp dashboardApp = aigcAppService.getById(DASHBOARD_APP_ID);
		if (Objects.isNull(dashboardApp)) {
			log.error("Dashboard应用未找到，请确保已执行初始化脚本");
			throw new RuntimeException("Dashboard应用未初始化，请执行dashboard_app_init.sql脚本");
		}
		// 获取模型
		String modelId = dashboardApp.getModelId();
		if (StringUtils.isBlank(modelId)) {
			log.error("Dashboard应用未配置模型");
			throw new RuntimeException("Dashboard应用未配置模型");
		}
		// 获取流式模型
		StreamingChatModel streamingChatModel = modelProvider.stream(modelId);
		// 构建提示词
		String systemPrompt = dashboardApp.getSystemPrompt();
		String prompt = dashboardPromptBuilder.buildPrompt(request, dashboardApp.getUserPromptTemplate());
		// 创建流式AI助手
		StreamDashboardAiAssistant assistant = AiServices.builder(StreamDashboardAiAssistant.class)
			.streamingChatModel(streamingChatModel)
			.systemMessageProvider((obj) -> systemPrompt)
			.chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
			.build();
		// 调用AI生成
		return assistant.generateDashboardFlux(prompt);
	}

}