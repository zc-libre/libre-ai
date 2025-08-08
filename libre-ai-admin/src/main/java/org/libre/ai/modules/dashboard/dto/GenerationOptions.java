package org.libre.ai.modules.dashboard.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

/**
 * 仪表板生成选项配置
 *
 * 遵循YAGNI原则：仅包含当前需要的配置选项
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationOptions {

	/**
	 * 代码风格
	 */
	@Pattern(regexp = "^(modern|minimal|enterprise)$", message = "代码风格必须为: modern, minimal, enterprise 之一")
	@Default
	private String codeStyle = "modern";

	/**
	 * 是否响应式设计
	 */
	@Default
	private Boolean responsive = true;

	/**
	 * 是否包含示例数据
	 */
	@Default
	private Boolean includeData = true;

	/**
	 * 额外需求描述
	 */
	@Size(max = 500, message = "额外需求描述不能超过500字符")
	@Default
	private String additionalRequirements = "";

}