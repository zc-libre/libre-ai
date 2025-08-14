package org.libre.ai.modules.dashboard.prompt;

import com.google.common.collect.Maps;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.dto.*;
import org.libre.ai.modules.dashboard.enums.DashboardComponent;
import org.libre.ai.modules.dashboard.enums.DashboardLayout;
import org.libre.ai.modules.dashboard.enums.DashboardPurpose;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 仪表板提示词构建器 使用 LangChain4j PromptTemplate 实现模板化的提示词构建
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Slf4j
@Component
public class DashboardPromptBuilder {

	// 默认值常量
	private static final boolean DEFAULT_RESPONSIVE = true;

	private static final boolean DEFAULT_INCLUDE_DATA = true;

	private static final String DEFAULT_CODE_STYLE = "现代风格 (简洁清爽的现代设计)";

	private static final String DEFAULT_LAYOUT = "默认网格布局";

	private static final String DEFAULT_THEME = "默认主题";

	/**
	 * 构建仪表板生成的提示词
	 * @param request 仪表板请求
	 * @return 生成的提示词
	 */
	public String buildPrompt(DashboardRequest request, String promptTemplateStr) {
		// 构建模板变量映射
		Map<String, Object> variables = buildTemplateVariables(request);

		// 使用 PromptTemplate 生成提示词
		PromptTemplate promptTemplate = PromptTemplate.from(promptTemplateStr);
		Prompt prompt = promptTemplate.apply(variables);

		String result = prompt.text();
		log.debug("生成的提示词: {}", result);

		return result;
	}

	public String buildOptimizeDashboardPrompt(OptimizeRequest request, String promptTemplateStr) {
		PromptTemplate promptTemplate = PromptTemplate.from(promptTemplateStr);
		Map<Object, Object> variables = Maps.newLinkedHashMap();

		String currentHtml = request.getCurrentHtml();
		variables.put("currentHtml", currentHtml);
		variables.put("userRequest", request.getUserRequest());
		Prompt prompt = promptTemplate.apply(variables);
		return prompt.text();
	}

	/**
	 * 安全获取Map类型值
	 * @param obj 待转换对象
	 * @return Optional包装的Map
	 */
	@SuppressWarnings("unchecked")
	private Optional<Map<String, Object>> getAsMap(Object obj) {
		if (obj instanceof Map) {
			try {
				return Optional.of((Map<String, Object>) obj);
			}
			catch (ClassCastException e) {
				log.warn("类型转换失败: {}", e.getMessage());
			}
		}
		return Optional.empty();
	}

	/**
	 * 安全获取List类型值
	 * @param obj 待转换对象
	 * @return Optional包装的List
	 */
	@SuppressWarnings("unchecked")
	private Optional<List<Map<String, Object>>> getAsList(Object obj) {
		if (obj instanceof List) {
			try {
				return Optional.of((List<Map<String, Object>>) obj);
			}
			catch (ClassCastException e) {
				log.warn("类型转换失败: {}", e.getMessage());
			}
		}
		return Optional.empty();
	}

	/**
	 * 构建模板变量映射
	 * @param request 仪表板请求
	 * @return 变量映射
	 */
	private Map<String, Object> buildTemplateVariables(DashboardRequest request) {
		Map<String, Object> variables = new LinkedHashMap<>();

		// 分组处理变量
		addBasicVariables(variables, request);
		addOptionVariables(variables, request);
		addCustomRequirements(variables, request);
		addComponentVariables(variables, request);
		addTechnicalRequirements(variables, request);

		return variables;
	}

	/**
	 * 添加基础变量
	 */
	private void addBasicVariables(Map<String, Object> variables, DashboardRequest request) {
		variables.put("purposeDescription", buildPurposeDescription(request));
		variables.put("layoutDescription", buildLayoutDescription(request));
		variables.put("themeDescription", buildThemeDescription(request));
		variables.put("componentsDescription", buildComponentsDescription(request.getComponents()));
		variables.put("codeStyle", DEFAULT_CODE_STYLE);
	}

	/**
	 * 添加选项变量
	 */
	private void addOptionVariables(Map<String, Object> variables, DashboardRequest request) {
		GenerationOptions options = request.getOptions();
		boolean isResponsive = options != null ? options.getResponsive() : DEFAULT_RESPONSIVE;
		boolean includeData = options != null ? options.getIncludeData() : DEFAULT_INCLUDE_DATA;

		variables.put("responsiveDescription", isResponsive ? "支持响应式设计" : "不支持响应式设计");
		variables.put("includeDataDescription", includeData ? "包含示例数据" : "不包含示例数据");
	}

	/**
	 * 添加自定义需求
	 */
	private void addCustomRequirements(Map<String, Object> variables, DashboardRequest request) {
		String customRequirements = buildCustomRequirements(request);
		variables.put("customRequirements", customRequirements.isEmpty() ? "" : "\n- 特殊需求: " + customRequirements);
	}

	/**
	 * 添加组件相关变量
	 */
	private void addComponentVariables(Map<String, Object> variables, DashboardRequest request) {
		variables.put("componentConstraints", buildComponentConstraints(request.getComponents()));
		variables.put("componentDataStructures", buildComponentDataStructures(request));
	}

	/**
	 * 添加技术要求
	 */
	private void addTechnicalRequirements(Map<String, Object> variables, DashboardRequest request) {
		GenerationOptions options = request.getOptions();
		boolean isResponsive = options != null ? options.getResponsive() : DEFAULT_RESPONSIVE;
		boolean includeData = options != null ? options.getIncludeData() : DEFAULT_INCLUDE_DATA;

		variables.put("responsiveTechRequirement",
				isResponsive ? DashboardPromptTemplate.RESPONSIVE_TECH_REQUIREMENT : "");
		variables.put("includeDataTechRequirement",
				includeData ? DashboardPromptTemplate.INCLUDE_DATA_TECH_REQUIREMENT : "");
	}

	/**
	 * 构建场景描述
	 */
	private String buildPurposeDescription(DashboardRequest request) {
		StringBuilder description = new StringBuilder();

		// 获取基础场景名称
		DashboardPurpose purposeEnum = DashboardPurpose.fromCode(request.getPurpose());
		String basePurpose = purposeEnum != null ? purposeEnum.getName() : request.getPurpose();
		description.append(basePurpose);

		// 添加场景细节
		if (request.getPurposeDetail() != null && !request.getPurposeDetail().trim().isEmpty()) {
			description.append(" - ").append(request.getPurposeDetail());
		}

		// 添加重点监控指标
		if (request.getFocusMetrics() != null && !request.getFocusMetrics().trim().isEmpty()) {
			description.append("，重点监控：").append(request.getFocusMetrics());
		}

		return description.toString();
	}

	/**
	 * 构建布局描述
	 */
	private String buildLayoutDescription(DashboardRequest request) {
		if (request.getLayoutText() != null) {
			DashboardLayout layout = DashboardLayout.fromCode(request.getLayoutText());
			if (layout != null) {
				return String.format("%s - %s", layout.getName(), layout.getDescription());
			}
			return request.getLayoutText();
		}
		return DEFAULT_LAYOUT;
	}

	/**
	 * 构建主题描述
	 */
	private String buildThemeDescription(DashboardRequest request) {
		if (request.getTheme() != null) {
			ThemeConfig theme = request.getTheme();
			ThemeConfig.ThemeColors colors = theme.getColors();
			if (colors != null) {
				return String.format("%s (主色调: %s, 辅助色: %s, 强调色: %s)",
						theme.getName() != null ? theme.getName() : DEFAULT_THEME, colors.getPrimary(),
						colors.getSecondary(), colors.getAccent());
			}
			return theme.getName() != null ? theme.getName() : DEFAULT_THEME;
		}
		return DEFAULT_THEME;
	}

	/**
	 * 构建组件描述
	 */
	private String buildComponentsDescription(List<String> components) {
		if (components == null || components.isEmpty()) {
			return "无组件（仅生成页面框架）";
		}

		StringBuilder description = new StringBuilder();
		description.append("【必须实现的组件列表】\n");

		for (int i = 0; i < components.size(); i++) {
			String code = components.get(i);
			DashboardComponent component = DashboardComponent.fromCode(code);

			if (i > 0) {
				description.append("\n");
			}

			description.append(String.format("%d. ", i + 1));

			if (component != null) {
				description.append(component.getName()).append(" - ").append(component.getDescription());

				// 添加组件的具体实现要求
				String hint = getComponentImplementationHint(code);
				if (!hint.isEmpty()) {
					description.append(hint);
				}
			}
			else {
				description.append(code).append(" - 自定义组件");
			}
		}

		return description.toString();
	}

	/**
	 * 获取组件实现提示
	 */
	private String getComponentImplementationHint(String componentCode) {
		return switch (componentCode) {
			case "line_chart" -> "（使用ECharts实现折线图）";
			case "bar_chart" -> "（使用ECharts实现柱状图）";
			case "pie_chart" -> "（使用ECharts实现饼图）";
			case "area_chart" -> "（使用ECharts实现面积图）";
			case "scatter_chart" -> "（使用ECharts实现散点图）";
			case "radar_chart" -> "（使用ECharts实现雷达图）";
			case "gauge" -> "（使用ECharts实现仪表盘）";
			case "kpi_card" -> "（使用卡片组件展示关键指标）";
			case "data_table" -> "（使用表格组件展示数据）";
			case "map" -> "（使用百度地图或高德地图API）";
			case "heatmap" -> "（使用ECharts实现热力图）";
			case "timeline" -> "（使用时间轴组件展示时序数据）";
			case "progress" -> "（使用进度条组件）";
			case "ranking" -> "（使用排行榜组件）";
			case "alert_list" -> "（使用列表组件展示告警信息）";
			case "carousel" -> "（使用轮播组件展示多项内容）";
			case "video" -> "（使用视频播放器组件）";
			case "clock" -> "（使用时钟组件显示实时时间）";
			case "weather" -> "（使用天气组件展示天气信息）";
			case "text_scroll" -> "（使用滚动文字组件）";
			default -> "";
		};
	}

	/**
	 * 构建自定义需求
	 */
	private String buildCustomRequirements(DashboardRequest request) {
		if (request.getCustomRequirements() != null && !request.getCustomRequirements().trim().isEmpty()) {
			return request.getCustomRequirements();
		}
		return "";
	}

	/**
	 * 构建组件约束
	 */
	private String buildComponentConstraints(List<String> components) {
		if (components == null || components.isEmpty()) {
			return DashboardPromptTemplate.NO_COMPONENT_CONSTRAINTS;
		}

		// 构建组件列表
		StringBuilder componentList = new StringBuilder();
		for (int i = 0; i < components.size(); i++) {
			String code = components.get(i);
			DashboardComponent component = DashboardComponent.fromCode(code);

			Map<String, Object> itemVars = new HashMap<>();
			itemVars.put("index", String.valueOf(i + 1));
			itemVars.put("componentName", component != null ? component.getName() : code);
			itemVars.put("componentDescription", component != null ? component.getDescription() : "自定义组件");

			PromptTemplate itemTemplate = PromptTemplate.from(DashboardPromptTemplate.COMPONENT_LIST_ITEM);
			componentList.append(itemTemplate.apply(itemVars).text());
		}

		// 构建完整的约束
		Map<String, Object> constraintVars = new HashMap<>();
		constraintVars.put("componentList", componentList.toString());

		PromptTemplate constraintTemplate = PromptTemplate.from(DashboardPromptTemplate.COMPONENT_CONSTRAINTS_TEMPLATE);
		return constraintTemplate.apply(constraintVars).text();
	}

	/**
	 * 构建组件数据结构
	 */
	private String buildComponentDataStructures(DashboardRequest request) {
		if (request.getComponentConfigs() == null || request.getComponentConfigs().isEmpty()) {
			return "";
		}

		StringBuilder configs = new StringBuilder();

		for (ComponentConfig config : request.getComponentConfigs()) {
			String componentId = config.getBase() != null ? config.getBase().getComponentId() : "";
			DashboardComponent component = DashboardComponent.fromCode(componentId);

			// 构建单个组件的配置详情
			StringBuilder configDetails = new StringBuilder();

			// 基础配置信息
			if (config.getBase() != null) {
				ComponentConfig.BaseConfig base = config.getBase();
				if (base.getDataSource() != null && !base.getDataSource().isEmpty()) {
					configDetails.append("- 数据源: ").append(base.getDataSource()).append("\n");
				}
				if (base.getRefreshInterval() != null && base.getRefreshInterval() > 0) {
					configDetails.append("- 刷新频率: 每").append(base.getRefreshInterval() / 1000).append("秒\n");
				}
			}

			// 特定配置（保持原有逻辑，简化部分代码）
			if (config.getSpecific() != null) {
				appendSpecificConfigs(configDetails, config.getSpecific());
			}

			// 示例数据
			if (config.getDataMapping() != null && config.getDataMapping().getSampleData() != null) {
				configDetails.append("- 示例数据: ").append(config.getDataMapping().getSampleData()).append("\n");
			}

			// 使用模板生成单个组件配置
			Map<String, Object> componentVars = new HashMap<>();
			componentVars.put("componentName", component != null ? component.getName() : componentId);
			componentVars.put("componentId", componentId);
			componentVars.put("configDetails", configDetails.toString());

			PromptTemplate componentTemplate = PromptTemplate.from(DashboardPromptTemplate.SINGLE_COMPONENT_CONFIG);
			configs.append(componentTemplate.apply(componentVars).text());
		}

		// 使用模板生成完整的组件配置部分
		Map<String, Object> configVars = new HashMap<>();
		configVars.put("componentConfigs", configs.toString());

		PromptTemplate configTemplate = PromptTemplate.from(DashboardPromptTemplate.COMPONENT_CONFIG_TEMPLATE);
		return configTemplate.apply(configVars).text();
	}

	/**
	 * 添加特定配置（提取的辅助方法）
	 */
	private void appendSpecificConfigs(StringBuilder configDetails, Map<String, Object> specific) {
		// 图表类配置
		appendAxisConfig(configDetails, specific, "xAxis", "X轴字段");
		appendAxisConfig(configDetails, specific, "yAxis", "Y轴字段");
		appendSeriesConfig(configDetails, specific);

		// 饼图配置
		appendFieldConfig(configDetails, specific, "nameField", "名称字段");
		appendFieldConfig(configDetails, specific, "valueField", "数值字段");

		// KPI卡片配置
		appendFieldConfig(configDetails, specific, "title", "指标名称");
		appendFieldConfig(configDetails, specific, "unit", "单位");
		appendComparisonConfig(configDetails, specific);
		appendTrendConfig(configDetails, specific);

		// 表格配置
		appendTableConfig(configDetails, specific);
		appendPaginationConfig(configDetails, specific);
	}

	private void appendAxisConfig(StringBuilder configDetails, Map<String, Object> specific, String axisKey,
			String label) {
		getAsMap(specific.get(axisKey)).flatMap(axis -> Optional.ofNullable(axis.get("field")))
			.ifPresent(field -> configDetails.append("- ").append(label).append(": ").append(field).append("\n"));
	}

	private void appendSeriesConfig(StringBuilder configDetails, Map<String, Object> specific) {
		getAsList(specific.get("series")).ifPresent(series -> {
			if (!series.isEmpty()) {
				Optional.ofNullable(series.getFirst().get("field"))
					.ifPresent(field -> configDetails.append("- 系列字段: ").append(field).append("（多系列数据）\n"));
			}
		});
	}

	private void appendFieldConfig(StringBuilder configDetails, Map<String, Object> specific, String fieldKey,
			String label) {
		if (specific.containsKey(fieldKey)) {
			configDetails.append("- ").append(label).append(": ").append(specific.get(fieldKey)).append("\n");
		}
	}

	private void appendComparisonConfig(StringBuilder configDetails, Map<String, Object> specific) {
		getAsMap(specific.get("comparison"))
			.flatMap(comparison -> Optional.ofNullable(comparison.get("type")).map(Object::toString))
			.ifPresent(type -> {
				String comparisonText = "chain".equals(type) ? "环比" : "同比";
				configDetails.append("- 对比方式: ").append(comparisonText).append("\n");
			});
	}

	private void appendTrendConfig(StringBuilder configDetails, Map<String, Object> specific) {
		getAsMap(specific.get("trend")).ifPresent(trend -> {
			if (Boolean.TRUE.equals(trend.get("show"))) {
				configDetails.append("- 趋势: 显示趋势\n");
			}
		});
	}

	private void appendTableConfig(StringBuilder configDetails, Map<String, Object> specific) {
		getAsList(specific.get("columns")).ifPresent(columns -> {
			if (!columns.isEmpty()) {
				configDetails.append("- 表格列配置:\n");
				columns.stream()
					.filter(column -> column.get("field") != null && !column.get("field").toString().isEmpty())
					.forEach(column -> {
						configDetails.append("  - ")
							.append(Optional.ofNullable(column.get("title")).orElse("未命名"))
							.append("（字段: ")
							.append(column.get("field"))
							.append("，宽度: ")
							.append(Optional.ofNullable(column.get("width")).orElse("auto"));

						if (Boolean.TRUE.equals(column.get("sortable"))) {
							configDetails.append("，可排序");
						}
						configDetails.append("）\n");
					});
			}
		});
	}

	private void appendPaginationConfig(StringBuilder configDetails, Map<String, Object> specific) {
		Object paginationObj = specific.get("pagination");
		if (paginationObj != null) {
			// 处理Map类型的分页配置
			getAsMap(paginationObj).ifPresentOrElse(pagination -> {
				if (Boolean.TRUE.equals(pagination.get("enabled"))) {
					configDetails.append("- 分页: 启用");
					Optional.ofNullable(pagination.get("pageSize"))
						.ifPresent(pageSize -> configDetails.append("，每页").append(pageSize).append("条"));
					configDetails.append("\n");
				}
			},
					// 处理Boolean类型的分页配置
					() -> {
						if (Boolean.TRUE.equals(paginationObj)) {
							configDetails.append("- 分页: 启用\n");
						}
					});
		}
	}

}