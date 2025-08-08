package org.libre.ai.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 组件配置对象 包含组件的数据结构和显示配置
 *
 * 遵循 SRP 原则：专注于组件配置的数据承载 遵循 KISS 原则：简单清晰的数据结构定义
 *
 * @author AI Assistant
 * @since 2025-01-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentConfig {

	/**
	 * 组件ID
	 */
	private String componentId;

	/**
	 * 组件类型
	 */
	private String componentType;

	/**
	 * 数据源描述
	 */
	private String dataSource;

	/**
	 * 刷新频率（毫秒）
	 */
	private Integer refreshInterval;

	/**
	 * 数据结构配置
	 */
	private DataStructure dataStructure;

	/**
	 * 数据结构定义
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DataStructure {

		// 图表类配置
		/**
		 * X轴字段名
		 */
		private String xField;

		/**
		 * Y轴字段名
		 */
		private String yField;

		/**
		 * 系列字段（用于多系列图表）
		 */
		private String seriesField;

		// 饼图配置
		/**
		 * 名称字段
		 */
		private String nameField;

		/**
		 * 数值字段
		 */
		private String valueField;

		// KPI卡片配置
		/**
		 * 指标标题
		 */
		private String title;

		/**
		 * 单位
		 */
		private String unit;

		/**
		 * 对比类型：chain-环比，year-同比，none-无对比
		 */
		private String comparison;

		/**
		 * 趋势：up-上升，down-下降，stable-平稳
		 */
		private String trend;

		// 表格配置
		/**
		 * 表格列配置
		 */
		private List<ColumnConfig> columns;

		/**
		 * 是否分页
		 */
		private Boolean pagination;

		/**
		 * 每页条数
		 */
		private Integer pageSize;

		/**
		 * 示例数据（JSON字符串）
		 */
		private String sampleData;

		/**
		 * 自定义结构（扩展用）
		 */
		private Object customSchema;

	}

	/**
	 * 表格列配置
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ColumnConfig {

		/**
		 * 字段名
		 */
		private String field;

		/**
		 * 列标题
		 */
		private String title;

		/**
		 * 列宽度
		 */
		private Integer width;

		/**
		 * 是否可排序
		 */
		private Boolean sortable;

	}

}