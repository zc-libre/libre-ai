package org.libre.ai.modules.dashboard.enums;

import lombok.Getter;

/**
 * 仪表板用途枚举
 *
 * 遵循 OCP（开闭原则）：通过枚举扩展新的用途，无需修改现有代码 遵循 KISS 原则：简单清晰的枚举定义
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Getter
public enum DashboardPurpose {

	ANALYTICS("analytics", "数据分析", "用于展示业务数据分析和统计"), PROJECT("project", "项目管理", "适合项目进度跟踪和任务管理"),
	SALES("sales", "销售监控", "实时监控销售业绩和趋势"), MONITORING("monitoring", "系统监控", "监控系统性能和运行状态");

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