package org.libre.ai.modules.dashboard.enums;

import lombok.Getter;

/**
 * 仪表板布局枚举
 *
 * 遵循 OCP（开闭原则）：可扩展新布局而无需修改现有代码
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Getter
public enum DashboardLayout {

	GRID("grid", "网格布局", "规整的卡片网格排列，适合展示多个同等重要的数据模块"), SIDEBAR("sidebar", "侧边栏布局", "左侧导航栏配合主内容区域，适合有层级结构的数据展示"),
	FULLSCREEN("fullscreen", "全屏布局", "单一大型组件占据全屏，适合展示重要的核心数据"), MASONRY("masonry", "瀑布流布局", "不规则卡片排列，适合展示不同尺寸的内容模块");

	private final String code;

	private final String name;

	private final String description;

	DashboardLayout(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	/**
	 * 根据代码获取枚举
	 * @param code 代码
	 * @return 枚举对象
	 */
	public static DashboardLayout fromCode(String code) {
		for (DashboardLayout layout : values()) {
			if (layout.code.equals(code)) {
				return layout;
			}
		}
		return null;
	}

}