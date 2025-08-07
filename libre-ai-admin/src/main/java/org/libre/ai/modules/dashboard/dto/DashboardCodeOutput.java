package org.libre.ai.modules.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI生成的仪表板代码输出结构 用于LangChain4j结构化输出
 *
 * 遵循SRP原则：专注于定义AI输出结构 遵循KISS原则：只包含一个完整的HTML字段 支持JSON Schema验证，确保输出格式正确
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCodeOutput {

	@JsonPropertyDescription("完整的HTML文档，包含<!DOCTYPE html>声明，内嵌所有CSS和JavaScript，可直接在浏览器中运行")
	private String html;

}