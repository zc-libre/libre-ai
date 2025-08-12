package org.libre.ai.modules.dashboard.prompt;

/**
 * 仪表板提示词模板常量
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
public interface DashboardPromptTemplate {

	/**
	 * 主提示词模板 使用 LangChain4j PromptTemplate 的变量占位符格式
	 */
	String DASHBOARD_GENERATION = """
			# 角色定义
			你是一个资深的前端架构师，精通HTML5、CSS3、JavaScript和现代前端框架，专注于物流仓储行业的可视化解决方案。

			# 任务要求
			根据以下用户配置，生成一个完整、专业、可用的大屏监控页面，输出结果绝对禁止使用markdown语法包裹代码块，直接输出完整代码：

			## 用户配置
			- 业务场景: {{purposeDescription}}
			- 布局风格: {{layoutDescription}}
			- 视觉主题: {{themeDescription}}
			- 功能组件: {{componentsDescription}}
			- 代码风格: {{codeStyle}}
			- 响应式要求: {{responsiveDescription}}
			- 包含数据: {{includeDataDescription}}
			{{customRequirements}}

			# 组件使用限制（必须严格遵守）
			{{componentConstraints}}

			# 技术要求
			1. 使用语义化HTML5标签
			2. CSS 使用现代布局技术（Flexbox/Grid），并可使用 Tailwind CSS 实现快速样式开发
			3. JavaScript使用ES6+语法
			4. 确保代码结构清晰、注释完整
			5. 遵循Web标准和最佳实践
			6. 确保跨浏览器兼容性
			{{componentDataStructures}}
			{{responsiveTechRequirement}}
			{{includeDataTechRequirement}}

			# 输出格式
			请生成一个完整的、可直接运行的HTML文件，包含所有内容。
			返回标准html格式,完整的HTML文档，必须包含<!DOCTYPE html>声明，<html>标签，<head>中内嵌完整的<style>标签包含所有CSS，<body>中包含所有HTML内容，</body>前内嵌<script>标签包含所有JavaScript代码

			# 重要要求：
			- 生成一个完整的、独立的HTML文件，可以直接在浏览器中打开运行
			- 所有CSS必须放在<head>中的<style>标签内
			- 如需使用 Tailwind，请在<head> 中通过 <script src="https://cdn.tailwindcss.com"></script> 引入；Tailwind 样式可由该脚本注入，无需写入 <style>，但自定义样式仍需置于 <style> 中
			- 所有JavaScript必须放在</body>前的<script>标签内
			- 不要分开返回css和javascript字段，全部内嵌在html字段中
			- 确保代码有良好的缩进和可读性
			- 包含合理的示例数据和交互效果
			- **严格按照用户选择的组件生成，不允许添加或删除任何组件**
			- 严格按照提供的主题色生成,禁止背景色使用其他颜色
			- 生成的图表一定要包含合理的示例数据, 确保图表组件能够正常显示

			# 格式要求：
			- 输出为标准的html，禁止添加任何解释性文字,禁止使用markdown语法，返回和输出格式示例必须完全一致
			- 输出的代码合适需要符合html的最佳实现，包含必要的缩进符和换行符等
			- 绝对禁止使用markdown语法包裹代码块
			""";

	/**
	 * 自定义需求模板片段
	 */
	String CUSTOM_REQUIREMENTS_SECTION = """

			- 特殊需求: {{customRequirements}}""";

	/**
	 * 响应式技术要求
	 */
	String RESPONSIVE_TECH_REQUIREMENT = """

			7. 实现完整的响应式设计""";

	/**
	 * 包含数据技术要求
	 */
	String INCLUDE_DATA_TECH_REQUIREMENT = """

			8. 包含合理的示例数据""";

	/**
	 * 组件约束模板
	 */
	String COMPONENT_CONSTRAINTS_TEMPLATE = """
			**必须且只能生成以下组件，不得自行添加其他组件：**
			{{componentList}}

			**禁止规则：**
			- 不得添加用户未选择的组件（如用户未选择地图，则不生成地图）
			- 不得将一个组件拆分成多个相似组件
			- 不得因为"更好的效果"而自行添加额外组件
			- 每个选择的组件必须且只能出现一次
			- 组件的实现必须符合其定义的功能特性
			""";

	/**
	 * 无组件时的约束
	 */
	String NO_COMPONENT_CONSTRAINTS = "本次不生成任何数据组件，仅生成基础页面框架和布局。";

	/**
	 * 组件列表项模板
	 */
	String COMPONENT_LIST_ITEM = "{{index}}. {{componentName}} - {{componentDescription}}\n";

	/**
	 * 组件详细配置模板
	 */
	String COMPONENT_CONFIG_TEMPLATE = """
			## 组件详细配置
			{{componentConfigs}}""";

	/**
	 * 单个组件配置模板
	 */
	String SINGLE_COMPONENT_CONFIG = """
			### {{componentName}} ({{componentId}})
			{{configDetails}}
			""";

}