package org.libre.ai.modules.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 仪表板优化请求DTO 用于对话式迭代优化功能
 *
 * 遵循KISS原则：只包含必要的字段 遵循SRP原则：专注于优化请求的数据传输
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
public class OptimizeRequest {

	/**
	 * 对话会话ID 用于保持对话上下文，支持连续优化
	 */
	@NotBlank(message = "会话ID不能为空")
	private String conversationId;

	/**
	 * 当前的HTML代码 包含完整的HTML文档，内嵌CSS和JavaScript
	 */
	@NotBlank(message = "当前代码不能为空")
	private String currentHtml;

	/**
	 * 用户的优化需求 自然语言描述的优化要求
	 */
	@NotBlank(message = "优化需求不能为空")
	private String userRequest;

	/**
	 * 原始配置（可选） 用于参考原始生成配置
	 */
	private DashboardRequest originalConfig;

}