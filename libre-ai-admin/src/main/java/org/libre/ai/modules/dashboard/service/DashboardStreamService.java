package org.libre.ai.modules.dashboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.enums.CodeStyle;
import org.libre.ai.modules.dashboard.enums.DashboardComponent;
import org.libre.ai.modules.dashboard.enums.DashboardTheme;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表板流式生成服务实现
 *
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
		return streamDashboardAiAssistant.generateDashboardFlux(prompt);
	}

	/**
	 * 构建AI提示词
	 *
	 * 根据用户配置生成详细的提示词，使用语义化的描述文本
	 * @param request 请求对象
	 * @return 提示词字符串
	 */
	private String buildDashboardPrompt(DashboardRequest request) {
		return String.format(
				"""
						# 角色定义
						你是一个资深的前端架构师，精通HTML5、CSS3、JavaScript和现代前端框架。

						# 任务要求
						根据以下用户配置，生成一个完整、专业、可用的仪表板页面，输出结果绝对禁止使用markdown语法包裹代码块，直接输出完整代码：

						## 用户配置
						- 业务场景: %s
						- 布局风格: %s
						- 视觉主题: %s
						- 功能组件: %s
						- 代码风格: %s
						- 响应式要求: %s
						- 包含数据: %s
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

						# 格式要求：
						- 输出为标准的html，禁止添加任何解释性文字,禁止使用markdown语法，返回和输出格式示例必须完全一致
						- 输出的代码合适需要符合html的最佳实现，包含必要的缩进符和换行符等
						- 绝对禁止使用markdown语法包裹代码块
						- 必须是下面标准的html结构, 禁止使用其他格式,返回结构示例：
						  <!DOCTYPE html>
						           <html lang="zh-CN">
						           <head>
						               <meta charset="UTF-8">
						               <meta name="viewport" content="width=device-width, initial-scale=1.0">
						               <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
						               <style>
						               </style>
						           </head>
						           <body>
						               <div></div>
						           </body>
						           <!-- JavaScript -->
						           <script>
						           </script>
						           </html>
						""",
				request.getPurposeText(), request.getLayoutText(), buildThemeDescription(request.getTheme()),
				buildComponentsDescription(request.getComponents()),
				buildCodeStyleDescription(
						request.getOptions() != null ? request.getOptions().getCodeStyle() : "modern"),
				buildBooleanDescription(request.getOptions() != null ? request.getOptions().getResponsive() : true,
						"支持响应式设计", "不支持响应式设计"),
				buildBooleanDescription(request.getOptions() != null ? request.getOptions().getIncludeData() : true,
						"包含示例数据", "不包含示例数据"),
				request.getOptions() != null && request.getOptions().getAdditionalRequirements() != null
						? "\n- 特殊需求: " + request.getOptions().getAdditionalRequirements() : "",
				request.getOptions() != null && request.getOptions().getResponsive() ? "\n7. 实现完整的响应式设计" : "",
				request.getOptions() != null && request.getOptions().getIncludeData() ? "\n8. 包含合理的示例数据" : "");
	}

	/**
	 * 构建主题的详细描述信息
	 *
	 * 遵循KISS原则：简单直观的主题信息构建
	 * @param themeCode 主题代码
	 * @return 包含颜色信息的主题描述
	 */
	private String buildThemeDescription(String themeCode) {
		DashboardTheme theme = DashboardTheme.fromCode(themeCode);
		if (theme != null) {
			return String.format("%s (主色调: %s, 辅助色: %s, 强调色: %s)", theme.getName(), theme.getPrimaryColor(),
					theme.getSecondaryColor(), theme.getAccentColor());
		}
		return themeCode;
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
			return "无组件";
		}

		return components.stream().map(code -> {
			DashboardComponent component = DashboardComponent.fromCode(code);
			return component != null ? component.getName() + "(" + component.getDescription() + ")" : code;
		}).collect(Collectors.joining(", "));
	}

	/**
	 * 构建代码风格的语义化描述
	 * @param codeStyleCode 代码风格代码
	 * @return 语义化的风格描述
	 */
	private String buildCodeStyleDescription(String codeStyleCode) {
		CodeStyle style = CodeStyle.fromCode(codeStyleCode);
		if (style != null) {
			return String.format("%s (%s)", style.getName(), style.getDescription());
		}
		return codeStyleCode;
	}

	/**
	 * 构建布尔值的中文描述
	 *
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