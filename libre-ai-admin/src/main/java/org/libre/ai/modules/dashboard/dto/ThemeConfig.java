package org.libre.ai.modules.dashboard.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主题配置
 * 
 * 统一的主题结构，无论预设还是自定义都使用相同格式
 * 遵循KISS原则：简单统一的数据结构
 * 
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeConfig {

	/**
	 * 主题名称（如：现代蓝、自定义等）
	 */
	@NotBlank(message = "主题名称不能为空")
	private String name;

	/**
	 * 主题颜色配置
	 */
	@NotNull(message = "主题颜色不能为空")
	@Valid
	private ThemeColors colors;

	/**
	 * 主题颜色定义
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ThemeColors {

		/**
		 * 主色调
		 */
		@NotBlank(message = "主色调不能为空")
		private String primary;

		/**
		 * 辅助色
		 */
		@NotBlank(message = "辅助色不能为空")
		private String secondary;

		/**
		 * 强调色
		 */
		@NotBlank(message = "强调色不能为空")
		private String accent;

		/**
		 * 背景色（可选）
		 */
		private String background;

		/**
		 * 表面色（可选）
		 */
		private String surface;

		/**
		 * 文本色（可选）
		 */
		private String text;

	}

}