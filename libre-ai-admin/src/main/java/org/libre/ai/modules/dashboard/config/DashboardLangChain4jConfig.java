package org.libre.ai.modules.dashboard.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.assistant.DashboardAiAssistant;
import org.libre.ai.modules.dashboard.assistant.StreamDashboardAiAssistant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Dashboard模块 LangChain4j配置
 *
 * 遵循KISS原则：简单的AI模型配置，专门用于Dashboard生成 遵循SRP原则：专注于Dashboard模块的AI配置 遵循DIP原则：依赖配置属性抽象而非直接注入值
 * 体现Spring Boot最佳实践：使用@ConfigurationProperties进行配置管理
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Configuration
@EnableConfigurationProperties(DashboardProperties.class)
@RequiredArgsConstructor
@Slf4j
public class DashboardLangChain4jConfig {

	private final DashboardProperties dashboardProperties;

	/**
	 * 配置OpenAI ChatModel用于Dashboard生成
	 *
	 * 体现YAGNI原则：仅配置当前需要的功能 体现容错设计：合理的超时和重试配置
	 * @return ChatLanguageModel实例
	 */
	@Bean
	public ChatModel chatModel() {
		String apiKey = dashboardProperties.getOpenai().getApiKey();
		if (apiKey == null || apiKey.trim().isEmpty()) {
			log.warn("Dashboard OpenAI API Key未配置，Dashboard生成功能将不可用");
			return null;
		}

		DashboardProperties.OpenAi openaiConfig = dashboardProperties.getOpenai();
		DashboardProperties.Generation generationConfig = dashboardProperties.getGeneration();

		log.info("配置Dashboard OpenAI ChatModel: model={}, baseUrl={}", openaiConfig.getModelName(),
				openaiConfig.getBaseUrl());

		return OpenAiChatModel.builder()
			.apiKey(openaiConfig.getApiKey())
			.baseUrl(openaiConfig.getBaseUrl())
			.modelName(openaiConfig.getModelName())
			.temperature(openaiConfig.getTemperature())
			.maxRetries(generationConfig.getMaxRetries())
			.timeout(Duration.ofMinutes(10))
			.logRequests(true)
			.logResponses(true)
			.strictJsonSchema(true)
			.build();
	}

	@Bean
	public StreamingChatModel streamChatLanguageModel() {
		DashboardProperties.OpenAi openaiConfig = dashboardProperties.getOpenai();
		return OpenAiStreamingChatModel.builder()
			.baseUrl(openaiConfig.getBaseUrl())
			.apiKey(openaiConfig.getApiKey())
			.modelName(openaiConfig.getModelName())
			.timeout(Duration.ofMinutes(5))
			.parallelToolCalls(true)
			.logRequests(false)
			.logResponses(false)
			.build();
	}

	/**
	 * 配置Dashboard AI Assistant服务 使用LangChain4j AiServices创建声明式AI服务
	 *
	 * 体现KISS原则：简化AI服务的创建和使用 体现DIP原则：依赖接口而非具体实现
	 * @param chatModel 聊天模型
	 * @return Dashboard AI助手服务
	 */
	@Bean
	public DashboardAiAssistant dashboardAiAssistant(ChatModel chatModel) {
		if (chatModel == null) {
			log.warn("ChatModel未配置，返回null的AI Assistant");
			return null;
		}
		log.info("创建Dashboard AI Assistant服务");
		return AiServices.builder(DashboardAiAssistant.class).chatModel(chatModel).build();
	}

	@Bean
	public StreamDashboardAiAssistant streamDashboardAiAssistant(StreamingChatModel chatModel) {
		return AiServices.builder(StreamDashboardAiAssistant.class)
			.streamingChatModel(chatModel)
			// .chatMemoryProvider(memoryId ->
			// MessageWindowChatMemory.withMaxMessages(10))
			.build();
	}

}