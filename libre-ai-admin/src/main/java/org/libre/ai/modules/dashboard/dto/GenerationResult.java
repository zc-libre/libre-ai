package org.libre.ai.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI代码生成结果
 *
 * 遵循KISS原则：简单直观的结果封装 遵循YAGNI原则：只返回用户真正需要的完整HTML
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationResult {

	/**
	 * 生成的完整HTML代码（包含内嵌的CSS和JavaScript） 可直接在浏览器中打开运行
	 */
	private String html;

	/**
	 * 生成过程元数据
	 */
	private GenerationMetadata metadata;

}