package org.libre.ai.modules.dashboard.enums;

import lombok.Getter;

/**
 * 仪表板布局枚举
 * <p>
 * 遵循 OCP（开闭原则）：可扩展新布局而无需修改现有代码
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Getter
public enum DashboardLayout {

	GRID("grid", "网格布局", "最常用的看板布局，将数据卡片整齐排列成网格，每个卡片展示一个关键指标或图表，布局清晰，信息密度适中。适合通用数据看板"),
	HEADER_MAIN("header-main", "KPI顶置布局", "顶部横向展示3-5个核心KPI指标，下方区域展示详细的图表分析。用户可以快速了解关键指标，同时查看详细数据。适合运营/销售看板"),
	SPLIT_SCREEN("split-screen", "左右分屏布局", "将屏幕分为左右两个独立区域，可以同时展示两个维度的数据，方便对比分析。支持调整左右比例。适合对比分析看板"),
	RADIAL("radial", "中心辐射布局", "将最重要的指标或图表放在中心位置，相关的次要指标环绕排列。强调核心数据，展示数据关联性。适合关键指标监控"),
	TIMELINE("timeline", "时间轴布局", "数据按时间顺序排列，可以展示业务发展历程、项目进度、历史趋势等。支持时间轴缩放和滚动。适合进度/趋势看板"),
	DASHBOARD("dashboard", "仪表盘布局", "使用仪表盘、环形进度条等组件，模拟物理仪表盘效果。直观展示当前状态和阈值，适合实时监控。适合性能监控看板"),
	FULLSCREEN("fullscreen", "全屏大屏布局", "专为大屏展示设计，去除多余装饰，最大化数据展示区域。支持自动轮播、动画效果和深色主题。适合指挥中心大屏"),
	MASONRY("masonry", "瀑布流布局", "卡片根据内容自适应高度，自动填充空间形成瀑布流效果。适合内容长度不固定的场景，空间利用率高。适合信息汇总看板");

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