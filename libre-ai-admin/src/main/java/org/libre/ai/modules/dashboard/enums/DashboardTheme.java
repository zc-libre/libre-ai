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
	ORANGE_WARM("orange-warm", "暖橙色", "#E6A23C", "#EEBE77", "#F3D19E"),
	RED_ENERGY("red-energy", "活力红", "#F56C6C", "#F78989", "#FAB6B6"),
	CYAN_FRESH("cyan-fresh", "清新青", "#17A2B8", "#46B5D1", "#7CC7D8"),
	INDIGO_DEEP("indigo-deep", "深邃靛", "#6366F1", "#818CF8", "#A5B4FC"),
	PINK_SOFT("pink-soft", "柔和粉", "#EC4899", "#F472B6", "#FBCFE8"),
	TEAL_CALM("teal-calm", "静谧蓝绿", "#14B8A6", "#5EEAD4", "#99F6E4"),
	AMBER_GOLDEN("amber-golden", "金色光辉", "#F59E0B", "#FBBF24", "#FCD34D");

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