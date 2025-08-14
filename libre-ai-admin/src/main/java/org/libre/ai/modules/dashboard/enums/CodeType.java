package org.libre.ai.modules.dashboard.enums;

import lombok.Getter;

/**
 * 代码类型枚举
 * 
 * 遵循KISS原则：简单明了的代码类型定义
 * 遵循OCP原则：方便扩展新的代码类型
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Getter
public enum CodeType {

	/**
	 * HTML代码（默认，向下兼容）
	 */
	HTML("html", "HTML", "完整的HTML页面，包含内嵌CSS和JavaScript"),

	/**
	 * Vue组件代码
	 */
	VUE("vue", "Vue组件", "Vue 3 + TypeScript + Element Plus组件");

	private final String code;

	private final String name;

	private final String description;

	CodeType(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	/**
	 * 根据代码获取枚举
	 * @param code 代码
	 * @return 枚举对象，找不到时返回默认HTML类型
	 */
	public static CodeType fromCode(String code) {
		if (code == null) {
			return HTML;
		}
		for (CodeType type : values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}
		return HTML; // 默认返回HTML，保持向下兼容
	}

	/**
	 * 是否为Vue类型
	 */
	public boolean isVue() {
		return this == VUE;
	}

	/**
	 * 是否为HTML类型
	 */
	public boolean isHtml() {
		return this == HTML;
	}

}