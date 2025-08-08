package org.libre.ai.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义主题颜色配置
 *
 * 遵循KISS/YAGNI：仅包含当前需要的颜色字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomTheme {

	/** 主色调 */
	private String primary;

	/** 辅助色 */
	private String secondary;

	/** 强调色 */
	private String accent;

	/** 背景色（可选） */
	private String background;

	/** 文本色（可选） */
	private String text;

}
