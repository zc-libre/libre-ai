package org.libre.ai.modules.dashboard.config;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.assistant.StreamDashboardAiAssistant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Dashboard模块 LangChain4j配置
 * <p>
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


	@Bean
	public StreamingChatModel streamChatLanguageModel() {
		DashboardProperties.OpenAi openaiConfig = dashboardProperties.getOpenai();
		return OpenAiStreamingChatModel.builder()
			.baseUrl(openaiConfig.getBaseUrl())
			.apiKey(openaiConfig.getApiKey())
			.modelName(openaiConfig.getModelName())
			.timeout(Duration.ofMinutes(5))
			// .returnThinking(true)
			.parallelToolCalls(true)
			.logRequests(false)
			.logResponses(false)
			.build();
	}

	@Bean
	public StreamDashboardAiAssistant streamDashboardAiAssistant(StreamingChatModel chatModel) {
		return AiServices.builder(StreamDashboardAiAssistant.class)
			.streamingChatModel(chatModel)
			.chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
			.build();
	}

}