package org.libre.ai.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 组件配置对象 - 采用分层架构设计
 *
 * 遵循 SRP 原则：每层配置专注于特定职责 遵循 OCP 原则：通过Map扩展specific配置，无需修改核心结构 遵循 KISS 原则：清晰的分层结构，易于理解和扩展
 *
 * @author AI Assistant
 * @since 2025-01-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentConfig {

	/**
	 * 基础配置
	 */
	private BaseConfig base;

	/**
	 * 数据映射配置
	 */
	private DataMappingConfig dataMapping;

	/**
	 * 组件特定配置（使用Map支持灵活扩展）
	 */
	private Map<String, Object> specific;

	/**
	 * 基础配置 - 所有组件通用
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BaseConfig {

		/**
		 * 组件ID
		 */
		private String componentId;

		/**
		 * 组件类型
		 */
		private String componentType;

		/**
		 * 数据源描述
		 */
		private String dataSource;

		/**
		 * 刷新频率（毫秒）
		 */
		private Integer refreshInterval;

	}

	/**
	 * 数据映射配置
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DataMappingConfig {

		/**
		 * 示例数据
		 */
		private Object sampleData;

	}

}