package org.libre.ai.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 仪表板模板配置集合
 *
 * 遵循KISS原则：简单的数据传输对象，便于前端获取所有配置选项
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTemplates {

	/**
	 * 用途选项列表
	 */
	private List<PurposeOption> purposes;

	/**
	 * 布局选项列表
	 */
	private List<LayoutOption> layouts;

	/**
	 * 主题选项列表
	 */
	private List<ThemeOption> themes;

	/**
	 * 组件选项列表
	 */
	private List<ComponentOption> components;

}