package org.libre.ai.modules.dashboard.enums;

import lombok.Getter;

/**
 * 仪表板用途枚举 - 物流仓储监控场景
 *
 * 遵循 OCP（开闭原则）：通过枚举扩展新的用途，无需修改现有代码 遵循 KISS 原则：简单清晰的枚举定义
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Getter
public enum DashboardPurpose {

	SHELF("shelf", "货架监控看板", "实时监控货架状态，包括占用率、库存分布、拣选效率等关键指标"),

	LOCATION("location", "仓位监控看板", "监控仓位利用率、物料分布、周转情况等仓储核心数据"),

	TRANSPORT("transport", "搬运任务监控", "跟踪AGV/叉车等搬运设备的任务执行、路径规划、设备状态"),

	MIXED("mixed", "综合监控看板", "整合仓储物流全流程数据，提供全局KPI和多维度分析"),

	CUSTOM("custom", "自定义监控看板", "根据特定业务需求定制专属的物流仓储监控看板");

	private final String code;

	private final String name;

	private final String description;

	DashboardPurpose(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	/**
	 * 根据代码获取枚举
	 * @param code 代码
	 * @return 枚举对象
	 */
	public static DashboardPurpose fromCode(String code) {
		for (DashboardPurpose purpose : values()) {
			if (purpose.code.equals(code)) {
				return purpose;
			}
		}
		return null;
	}

}