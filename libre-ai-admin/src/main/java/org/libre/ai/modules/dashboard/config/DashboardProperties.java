package org.libre.ai.modules.dashboard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Dashboard模块配置属性类
 *
 * 遵循KISS原则：清晰的配置结构 遵循SRP原则：专注于Dashboard配置管理 体现Spring
 * Boot最佳实践：使用@ConfigurationProperties管理配置
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Component
@ConfigurationProperties(prefix = "dashboard")
public class DashboardProperties {

	/**
	 * OpenAI配置
	 */
	private OpenAi openai = new OpenAi();

	/**
	 * 生成配置
	 */
	private Generation generation = new Generation();

	@Data
	public static class OpenAi {

		/**
		 * API密钥
		 */
		private String apiKey;

		/**
		 * API基础URL
		 */
		private String baseUrl = "https://api.openai.com/v1";

		/**
		 * 模型名称
		 */
		private String modelName = "gpt-4";

		/**
		 * 温度参数（创造性）
		 */
		private double temperature = 0.7;

		/**
		 * 最大Token数
		 */
		private int maxTokens = 4000;

	}

	@Data
	public static class Generation {

		/**
		 * 超时时间（秒）
		 */
		private long timeout = 1200;

		/**
		 * 最大重试次数
		 */
		private int maxRetries = 3;

	}

}