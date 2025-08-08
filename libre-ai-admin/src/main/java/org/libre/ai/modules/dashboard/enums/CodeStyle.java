package org.libre.ai.modules.dashboard.enums;

import lombok.Getter;

/**
 * 代码风格枚举
 * <p>
 * 遵循 OCP（开闭原则）：可扩展新风格而无需修改现有代码
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Getter
public enum CodeStyle {

	MODERN("modern", "现代风格", "简洁清爽，使用最新CSS特性和现代设计语言"), MINIMAL("minimal", "极简风格", "专注内容，去除多余装饰，强调功能性"),
	ENTERPRISE("enterprise", "企业风格", "稳重专业，适合商务环境，注重可读性");

	private final String code;

	private final String name;

	private final String description;

	CodeStyle(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	/**
	 * 根据代码获取枚举
	 * @param code 代码
	 * @return 枚举对象
	 */
	public static CodeStyle fromCode(String code) {
		for (CodeStyle style : values()) {
			if (style.code.equals(code)) {
				return style;
			}
		}
		return null;
	}

}