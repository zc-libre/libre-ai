package org.libre.ai.modules.dashboard.enums;

import lombok.Getter;

/**
 * 仪表板组件枚举
 *
 * 遵循 KISS 原则：简单明了的组件定义
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Getter
public enum DashboardComponent {

	// 图表组件
	BAR_CHART("bar-chart", "柱状图", "展示数据对比"), LINE_CHART("line-chart", "折线图", "展示趋势变化"),
	PIE_CHART("pie-chart", "饼图", "展示数据占比"), AREA_CHART("area-chart", "面积图", "展示数据分布"),

	// 数据组件
	DATA_TABLE("data-table", "数据表格", "展示详细数据"), DATA_LIST("data-list", "数据列表", "展示列表信息"),
	KPI_CARD("kpi-card", "KPI卡片", "展示关键指标");

	private final String code;

	private final String name;

	private final String description;

	DashboardComponent(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	/**
	 * 根据代码获取枚举
	 * @param code 代码
	 * @return 枚举对象
	 */
	public static DashboardComponent fromCode(String code) {
		for (DashboardComponent component : values()) {
			if (component.code.equals(code)) {
				return component;
			}
		}
		return null;
	}

}