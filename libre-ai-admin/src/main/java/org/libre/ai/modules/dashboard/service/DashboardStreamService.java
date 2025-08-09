package org.libre.ai.modules.dashboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.assistant.StreamDashboardAiAssistant;
import org.libre.ai.modules.dashboard.dto.ComponentConfig;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.dto.ThemeConfig;
import org.libre.ai.modules.dashboard.enums.DashboardComponent;
import org.libre.ai.modules.dashboard.enums.DashboardPurpose;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 仪表板流式生成服务实现
 * <p>
 * 使用WebFlux响应式编程模型 集成LangChain4j的TokenStream实现流式AI生成
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardStreamService implements IDashboardStreamService {

	private final StreamDashboardAiAssistant streamDashboardAiAssistant;

	/**
	 * 流式生成仪表板代码
	 *
	 * 将LangChain4j的TokenStream转换为Reactor的Flux 实现背压控制和错误处理
	 * @param request 生成请求
	 * @return 代码片段流
	 */
	@Override
	public Flux<String> generateDashboardStream(DashboardRequest request) {
		log.info("开始流式生成仪表板，配置: {}", request);

		// 构建AI提示词
		String prompt = buildDashboardPrompt(request);
		log.debug("完整提示词： {}", prompt);
		return streamDashboardAiAssistant.generateDashboardFlux(prompt);
	}

	/**
	 * 构建AI提示词
	 * <p>
	 * 根据用户配置生成详细的提示词，结合预设场景和用户自定义内容
	 * @param request 请求对象
	 * @return 提示词字符串
	 */
	private String buildDashboardPrompt(DashboardRequest request) {
		// 构建场景描述，结合预设和用户自定义
		String purposeDescription = buildPurposeDescription(request);

		// 构建组件约束条件
		String componentConstraints = buildComponentConstraints(request.getComponents());

		return String.format(
				"""
						# 角色定义
						你是一个资深的前端架构师，精通HTML5、CSS3、JavaScript和现代前端框架，专注于物流仓储行业的可视化解决方案。

						# 任务要求
						根据以下用户配置，生成一个完整、专业、可用的大屏监控页面，输出结果绝对禁止使用markdown语法包裹代码块，直接输出完整代码：

						## 用户配置
						- 业务场景: %s
						- 布局风格: %s
						- 视觉主题: %s
						- 功能组件: %s
						- 代码风格: %s
						- 响应式要求: %s
						- 包含数据: %s
						%s

						# 组件使用限制（必须严格遵守）
						%s

						# 技术要求
						1. 使用语义化HTML5标签
						2. CSS使用现代布局技术(Flexbox/Grid),tailwindcss
						3. JavaScript使用ES6+语法
						4. 确保代码结构清晰、注释完整
						5. 遵循Web标准和最佳实践
						6. 确保跨浏览器兼容性
						%s
						%s

						# 输出格式
						请生成一个完整的、可直接运行的HTML文件，包含所有内容。
						返回标准html格式,完整的HTML文档，必须包含<!DOCTYPE html>声明，<html>标签，<head>中内嵌完整的<style>标签包含所有CSS，<body>中包含所有HTML内容，</body>前内嵌<script>标签包含所有JavaScript代码

						# 重要要求：
						- 生成一个完整的、独立的HTML文件，可以直接在浏览器中打开运行
						- 所有CSS必须放在<head>中的<style>标签内
						- 所有JavaScript必须放在</body>前的<script>标签内
						- 不要分开返回css和javascript字段，全部内嵌在html字段中
						- 确保代码有良好的缩进和可读性
						- 包含合理的示例数据和交互效果
						- **严格按照用户选择的组件生成，不允许添加或删除任何组件**

						# 格式要求：
						- 输出为标准的html，禁止添加任何解释性文字,禁止使用markdown语法，返回和输出格式示例必须完全一致
						- 输出的代码合适需要符合html的最佳实现，包含必要的缩进符和换行符等
						- 绝对禁止使用markdown语法包裹代码块
						""",
				purposeDescription, request.getLayoutText(), buildThemeDescription(request),
				buildComponentsDescription(request.getComponents()), "现代风格 (简洁清爽的现代设计)",
				buildBooleanDescription(request.getOptions() != null ? request.getOptions().getResponsive() : true,
						"支持响应式设计", "不支持响应式设计"),
				buildBooleanDescription(request.getOptions() != null ? request.getOptions().getIncludeData() : true,
						"包含示例数据", "不包含示例数据"),
				buildCustomRequirements(request), componentConstraints, buildComponentDataStructures(request),
				request.getOptions() != null && request.getOptions().getResponsive() ? "\n7. 实现完整的响应式设计" : "",
				request.getOptions() != null && request.getOptions().getIncludeData() ? "\n8. 包含合理的示例数据" : "");
	}

	/**
	 * 构建场景描述，结合预设和用户自定义内容
	 * @param request 请求对象
	 * @return 完整的场景描述
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
	 * 构建自定义需求描述
	 * @param request 请求对象
	 * @return 自定义需求描述
	 */
	private String buildCustomRequirements(DashboardRequest request) {
		StringBuilder requirements = new StringBuilder();

		// 优先使用新的 customRequirements 字段
		if (request.getCustomRequirements() != null && !request.getCustomRequirements().trim().isEmpty()) {
			requirements.append("\n- 特殊需求: ").append(request.getCustomRequirements());
		}
		return requirements.toString();
	}

	/**
	 * 构建主题的详细描述信息
	 * <p>
	 * 遵循KISS原则：简单直观的主题信息构建 支持预设主题和自定义主题
	 * @param request 请求对象
	 * @return 包含颜色信息的主题描述
	 */
	private String buildThemeDescription(DashboardRequest request) {
		// 使用统一的主题配置结构
		if (request.getTheme() != null) {
			ThemeConfig theme = request.getTheme();
			ThemeConfig.ThemeColors colors = theme.getColors();
			return String.format("%s (主色调: %s, 辅助色: %s, 强调色: %s)", theme.getName(), colors.getPrimary(),
					colors.getSecondary(), colors.getAccent());
		}
		return "默认主题";
	}

	/**
	 * 构建组件列表的语义化描述
	 *
	 * 遵循DRY原则：统一的组件描述构建逻辑
	 * @param components 组件代码列表
	 * @return 语义化的组件描述
	 */
	private String buildComponentsDescription(List<String> components) {
		if (components == null || components.isEmpty()) {
			return "无组件（仅生成页面框架）";
		}

		// 生成更详细的组件描述，包含实现要求
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
				description.append(getComponentImplementationHint(code));
			}
			else {
				description.append(code).append(" - 自定义组件");
			}
		}

		return description.toString();
	}

	/**
	 * 获取组件的实现提示
	 * @param componentCode 组件代码
	 * @return 实现提示
	 */
	private String getComponentImplementationHint(String componentCode) {
		switch (componentCode) {
			case "line_chart":
				return "（使用ECharts实现折线图）";
			case "bar_chart":
				return "（使用ECharts实现柱状图）";
			case "pie_chart":
				return "（使用ECharts实现饼图）";
			case "area_chart":
				return "（使用ECharts实现面积图）";
			case "scatter_chart":
				return "（使用ECharts实现散点图）";
			case "radar_chart":
				return "（使用ECharts实现雷达图）";
			case "gauge":
				return "（使用ECharts实现仪表盘）";
			case "kpi_card":
				return "（使用卡片组件展示关键指标）";
			case "data_table":
				return "（使用表格组件展示数据）";
			case "map":
				return "（使用百度地图或高德地图API）";
			case "heatmap":
				return "（使用ECharts实现热力图）";
			case "timeline":
				return "（使用时间轴组件展示时序数据）";
			case "progress":
				return "（使用进度条组件）";
			case "ranking":
				return "（使用排行榜组件）";
			case "alert_list":
				return "（使用列表组件展示告警信息）";
			case "carousel":
				return "（使用轮播组件展示多项内容）";
			case "video":
				return "（使用视频播放器组件）";
			case "clock":
				return "（使用时钟组件显示实时时间）";
			case "weather":
				return "（使用天气组件展示天气信息）";
			case "text_scroll":
				return "（使用滚动文字组件）";
			default:
				return "";
		}
	}

	/**
	 * 构建组件使用约束
	 *
	 * 生成明确的组件使用限制，确保AI只生成用户选择的组件
	 * @param components 用户选择的组件列表
	 * @return 组件约束描述
	 */
	private String buildComponentConstraints(List<String> components) {
		if (components == null || components.isEmpty()) {
			return "本次不生成任何数据组件，仅生成基础页面框架和布局。";
		}

		StringBuilder constraints = new StringBuilder();
		constraints.append("**必须且只能生成以下组件，不得自行添加其他组件：**\n");

		// 列出允许的组件
		for (int i = 0; i < components.size(); i++) {
			String code = components.get(i);
			DashboardComponent component = DashboardComponent.fromCode(code);
			String componentName = component != null ? component.getName() : code;
			constraints.append(String.format("%d. %s - %s\n", i + 1, componentName,
					component != null ? component.getDescription() : "自定义组件"));
		}

		constraints.append("\n**禁止规则：**\n");
		constraints.append("- 不得添加用户未选择的组件（如用户未选择地图，则不生成地图）\n");
		constraints.append("- 不得将一个组件拆分成多个相似组件\n");
		constraints.append("- 不得因为\"更好的效果\"而自行添加额外组件\n");
		constraints.append("- 每个选择的组件必须且只能出现一次\n");
		constraints.append("- 组件的实现必须符合其定义的功能特性\n");

		return constraints.toString();
	}

	/**
	 * 构建组件数据结构描述
	 *
	 * 根据组件配置生成详细的数据结构说明
	 * @param request 请求对象
	 * @return 数据结构描述
	 */
	private String buildComponentDataStructures(DashboardRequest request) {
		if (request.getComponentConfigs() == null || request.getComponentConfigs().isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("## 组件详细配置\n");

		for (ComponentConfig config : request.getComponentConfigs()) {
			// 从基础配置获取组件ID
			String componentId = config.getBase() != null ? config.getBase().getComponentId() : "";
			DashboardComponent component = DashboardComponent.fromCode(componentId);
			String componentName = component != null ? component.getName() : componentId;

			sb.append(String.format("### %s (%s)\n", componentName, componentId));

			// 基础配置信息
			if (config.getBase() != null) {
				ComponentConfig.BaseConfig base = config.getBase();
				if (base.getDataSource() != null && !base.getDataSource().isEmpty()) {
					sb.append("- 数据源: ").append(base.getDataSource()).append("\n");
				}
				if (base.getRefreshInterval() != null && base.getRefreshInterval() > 0) {
					sb.append("- 刷新频率: 每").append(base.getRefreshInterval() / 1000).append("秒\n");
				}
			}

			// 从specific字段获取组件特定配置
			if (config.getSpecific() != null) {
				Map<String, Object> specific = config.getSpecific();

				// 图表类配置
				if (specific.containsKey("xAxis")) {
					Object xAxisObj = specific.get("xAxis");
					if (xAxisObj instanceof Map) {
						Map<String, Object> xAxis = (Map<String, Object>) xAxisObj;
						if (xAxis.get("field") != null) {
							sb.append("- X轴字段: ").append(xAxis.get("field")).append("\n");
						}
					}
				}
				if (specific.containsKey("yAxis")) {
					Object yAxisObj = specific.get("yAxis");
					if (yAxisObj instanceof Map) {
						Map<String, Object> yAxis = (Map<String, Object>) yAxisObj;
						if (yAxis.get("field") != null) {
							sb.append("- Y轴字段: ").append(yAxis.get("field")).append("\n");
						}
					}
				}
				if (specific.containsKey("series")) {
					Object seriesObj = specific.get("series");
					if (seriesObj instanceof List) {
						List<Map<String, Object>> series = (List<Map<String, Object>>) seriesObj;
						if (!series.isEmpty() && series.get(0).get("field") != null) {
							sb.append("- 系列字段: ").append(series.get(0).get("field")).append("（多系列数据）\n");
						}
					}
				}

				// 饼图配置
				if (specific.containsKey("nameField")) {
					sb.append("- 名称字段: ").append(specific.get("nameField")).append("\n");
				}
				if (specific.containsKey("valueField")) {
					sb.append("- 数值字段: ").append(specific.get("valueField")).append("\n");
				}

				// KPI卡片配置
				if (specific.containsKey("title")) {
					sb.append("- 指标名称: ").append(specific.get("title")).append("\n");
				}
				if (specific.containsKey("unit")) {
					sb.append("- 单位: ").append(specific.get("unit")).append("\n");
				}
				if (specific.containsKey("comparison")) {
					Object comparisonObj = specific.get("comparison");
					if (comparisonObj instanceof Map) {
						Map<String, Object> comparison = (Map<String, Object>) comparisonObj;
						if (comparison.get("type") != null) {
							String comparisonType = comparison.get("type").toString();
							String comparisonText = "chain".equals(comparisonType) ? "环比" : "同比";
							sb.append("- 对比方式: ").append(comparisonText).append("\n");
						}
					}
				}
				if (specific.containsKey("trend")) {
					Object trendObj = specific.get("trend");
					if (trendObj instanceof Map) {
						Map<String, Object> trend = (Map<String, Object>) trendObj;
						if (Boolean.TRUE.equals(trend.get("show"))) {
							sb.append("- 趋势: 显示趋势\n");
						}
					}
				}

				// 表格配置
				if (specific.containsKey("columns")) {
					Object columnsObj = specific.get("columns");
					if (columnsObj instanceof List) {
						List<Map<String, Object>> columns = (List<Map<String, Object>>) columnsObj;
						if (!columns.isEmpty()) {
							sb.append("- 表格列配置:\n");
							for (Map<String, Object> column : columns) {
								if (column.get("field") != null && !column.get("field").toString().isEmpty()) {
									sb.append("  - ")
										.append(column.get("title"))
										.append("（字段: ")
										.append(column.get("field"))
										.append("，宽度: ")
										.append(column.get("width"))
										.append(Boolean.TRUE.equals(column.get("sortable")) ? "，可排序" : "")
										.append("）\n");
								}
							}
						}
					}
				}
				if (specific.containsKey("pagination")) {
					Object paginationObj = specific.get("pagination");
					if (paginationObj instanceof Map) {
						Map<String, Object> pagination = (Map<String, Object>) paginationObj;
						if (Boolean.TRUE.equals(pagination.get("enabled"))) {
							sb.append("- 分页: 启用");
							if (pagination.get("pageSize") != null) {
								sb.append("，每页").append(pagination.get("pageSize")).append("条");
							}
							sb.append("\n");
						}
					}
					else if (Boolean.TRUE.equals(paginationObj)) {
						sb.append("- 分页: 启用\n");
					}
				}
			}

			// 从数据映射配置获取示例数据
			if (config.getDataMapping() != null && config.getDataMapping().getSampleData() != null) {
				sb.append("- 示例数据: ").append(config.getDataMapping().getSampleData()).append("\n");
			}

			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * 构建布尔值的中文描述
	 * <p>
	 * 遵循KISS原则：简单的布尔值转换
	 * @param value 布尔值
	 * @param trueText 真值描述
	 * @param falseText 假值描述
	 * @return 中文描述
	 */
	private String buildBooleanDescription(Boolean value, String trueText, String falseText) {
		return Boolean.TRUE.equals(value) ? trueText : falseText;
	}

}