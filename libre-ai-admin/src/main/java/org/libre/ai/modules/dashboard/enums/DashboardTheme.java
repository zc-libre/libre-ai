package org.libre.ai.modules.dashboard.enums;

import lombok.Getter;

/**
 * 仪表板主题枚举
 * 
 * 遵循 OCP（开闭原则）：可扩展新主题而无需修改现有代码
 * 
 * @author AI Assistant
 * @since 2025-01-15
 */
@Getter
public enum DashboardTheme {

	MODERN_BLUE("modern-blue", "现代蓝", "#409EFF", "#79BBFF", "#A0CFFF"),
	DARK_PURPLE("dark-purple", "深紫夜", "#8B5CF6", "#A78BFA", "#C4B5FD"),
	GREEN_NATURE("green-nature", "自然绿", "#67C23A", "#85CE61", "#B3E19D"),
	ORANGE_WARM("orange-warm", "暖橙色", "#E6A23C", "#EEBE77", "#F3D19E");

	private final String code;

	private final String name;

	private final String primaryColor;

	private final String secondaryColor;

	private final String accentColor;

	DashboardTheme(String code, String name, String primaryColor, String secondaryColor, String accentColor) {
		this.code = code;
		this.name = name;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.accentColor = accentColor;
	}

	/**
	 * 根据代码获取枚举
	 * @param code 代码
	 * @return 枚举对象
	 */
	public static DashboardTheme fromCode(String code) {
		for (DashboardTheme theme : values()) {
			if (theme.code.equals(code)) {
				return theme;
			}
		}
		return null;
	}

}