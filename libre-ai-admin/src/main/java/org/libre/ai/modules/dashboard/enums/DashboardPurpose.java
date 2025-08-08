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

	SHELF("shelf", "货架监控看板",
			"设计一个专业的货架监控看板，需要实时展示：1)货架占用率和空闲位置分布图；2)各货架区域的库存数量、SKU分布和货物周转率；3)拣选作业效率指标，包括拣选路径优化、作业完成率和平均拣选时间；4)货架异常预警，如超重、倾斜、货物错放等；5)热力图展示高频存取货架区域。使用3D可视化展示货架布局，支持钻取查看每个货位详情"),

	LOCATION("location", "仓位监控看板",
			"创建一个智能仓位监控看板，重点监控：1)仓位利用率的实时动态，包括已用、预留、空闲仓位的分布和比例；2)物料存储位置的可视化地图，支持按物料类型、批次、保质期等维度筛选查看；3)仓位周转分析，展示高低频仓位、呆滞料分析；4)仓位分配优化建议，基于ABC分类法的动态仓位推荐；5)仓位异常告警，包括超期存储、错位存放、容量超限等。界面需要清晰的分区展示，支持快速定位和搜索功能"),

	TRANSPORT("transport", "搬运任务监控",
			"构建一个全面的AGV/搬运任务监控看板，包含：1)AGV实时位置和运行轨迹的2D/3D地图展示；2)任务执行状态面板，显示待执行、执行中、已完成任务的详细列表和统计；3)AGV设备状态监控，包括电量、运行时间、故障状态、维护计划；4)任务调度优化指标，如平均等待时间、路径利用率、任务完成率；5)搬运效率分析图表，展示高峰期分布、瓶颈分析、设备利用率；6)异常事件实时告警和历史记录。支持任务优先级调整和手动干预功能"),

	MIXED("mixed", "综合监控看板",
			"设计一个物流仓储综合监控大屏，整合展示：1)全局KPI总览，包括库存总量、出入库流量、订单完成率、设备运行率等核心指标；2)仓库3D全景图，实时展示货架、AGV、作业人员的位置和状态；3)物料流向桑基图，可视化展示从入库到出库的完整流程；4)多维度数据分析，包括时间趋势、区域对比、效率排名；5)预警中心，集中展示各类异常和告警信息；6)关键业务指标趋势预测。采用大屏设计风格，支持多屏联动和数据钻取，适合指挥中心使用"),

	CUSTOM("custom", "自定义监控看板",
			"根据用户特定需求定制专属的物流仓储监控看板。请在生成时充分考虑：1)用户的具体业务场景和监控重点；2)需要展示的关键数据指标和维度；3)适合的可视化图表类型和布局方式；4)实时数据更新频率和数据源接入方式；5)交互功能需求，如筛选、钻取、导出等；6)视觉设计风格和品牌色彩要求。确保生成的看板既满足功能需求，又具有良好的用户体验和视觉效果");

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