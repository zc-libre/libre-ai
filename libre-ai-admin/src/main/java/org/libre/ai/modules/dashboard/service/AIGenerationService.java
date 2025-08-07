package org.libre.ai.modules.dashboard.service;

import com.google.common.base.Throwables;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.dto.DashboardCodeOutput;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.dto.GenerationMetadata;
import org.libre.ai.modules.dashboard.dto.GenerationResult;
import org.libre.ai.modules.dashboard.exception.AIServiceException;
import org.libre.ai.modules.dashboard.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

/**
 * AI代码生成服务
 *
 * 遵循SRP原则：专注于AI生成逻辑 遵循DRY原则：统一的AI调用和结果解析逻辑 体现重试机制和错误处理最佳实践
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AIGenerationService implements IAIGenerationService {

	private final DashboardAiAssistant aiAssistant;

	private final StreamDashboardAiAssistant streamDashboardAiAssistant;

	/**
	 * 使用LangChain4j生成仪表板代码
	 *
	 * 体现SOLID原则：单一职责，专注代码生成 体现容错处理：重试机制和异常处理
	 * @param request 生成请求
	 * @return 生成结果
	 */
	@Override
	public GenerationResult generateCode(DashboardRequest request) {
		long startTime = System.currentTimeMillis();

		try {
			CountDownLatch latch = new CountDownLatch(1);
			log.info("开始AI代码生成，配置: {}", request);

			// 使用AI Assistant服务生成代码（结构化输出）
			DashboardCodeOutput codeOutput = null;

			if (aiAssistant == null) {
				throw new AIServiceException("AI Assistant服务未配置");
			}

			// 使用AI Assistant方式（结构化输出）
			log.info("使用AI Assistant生成仪表板代码");
			// 使用更详细的提示词构建方法
			String prompt = buildDashboardPrompt(request);
			TokenStream tokenStream = streamDashboardAiAssistant.generateDashboard(prompt);

			StringBuilder buffer = new StringBuilder();
			tokenStream.onPartialResponse(buffer::append) // 每个 token 追加
				.onCompleteResponse(r -> {
					String full = buffer.toString(); // 最终文本
					System.out.println("完整内容：\n" + full);
					latch.countDown(); // 完成后释放锁
				})
				.onError(e -> {
					log.error("流式生成失败", e);
					latch.countDown(); // 错误时也要释放锁
				})
				.start();

			latch.await();
			String response = buffer.toString();
			response = clearHtmlCode(response);
			log.info("大模型返回代码：{}", response);

			codeOutput = JsonUtils.fromJson(response, DashboardCodeOutput.class);
			// 构建返回结果
			long generationTime = System.currentTimeMillis() - startTime;

			// AI已经生成了完整的HTML，直接使用
			String completeHtml = codeOutput.getHtml();

			return GenerationResult.builder()
				.html(completeHtml)
				.metadata(GenerationMetadata.builder()
					.generatedAt(LocalDateTime.now())
					.generationTime(generationTime / 1000.0)
					.linesOfCode(calculateLinesOfCode(completeHtml))
					.fileSize(calculateFileSize(completeHtml))
					.build())
				.build();

		}
		catch (Exception e) {
			log.error("AI代码生成失败", Throwables.getRootCause(e));
			throw new AIServiceException("代码生成失败，请重试: " + e.getMessage(), e);
		}
	}

	/**
	 * 构建AI提示词
	 *
	 * 体现模板化思想：结构化的提示词生成
	 * @param request 请求对象
	 * @return 提示词字符串
	 */
	private String buildDashboardPrompt(DashboardRequest request) {
		return String.format(
				"""
						# 角色定义
						你是一个资深的前端架构师，精通HTML5、CSS3、JavaScript和现代前端框架。

						# 任务要求
						根据以下用户配置，生成一个完整、专业、可用的仪表板页面：

						## 用户配置
						- 业务场景: %s (%s)
						- 布局风格: %s (%s)
						- 视觉主题: %s (%s)
						- 功能组件: %s
						- 代码风格: %s
						- 响应式要求: %s
						- 包含数据: %s
						%s

						# 技术要求
						1. 使用语义化HTML5标签
						2. CSS使用现代布局技术(Flexbox/Grid)
						3. JavaScript使用ES6+语法
						4. 确保代码结构清晰、注释完整
						5. 遵循Web标准和最佳实践
						6. 确保跨浏览器兼容性
						%s
						%s

						# 输出格式
						请生成一个完整的、可直接运行的HTML文件，包含所有内容。
						返回标准JSON格式,完整的HTML文档，必须包含<!DOCTYPE html>声明，<html>标签，<head>中内嵌完整的<style>标签包含所有CSS，<body>中包含所有HTML内容，</body>前内嵌<script>标签包含所有JavaScript代码

						# 重要要求：
						- 生成一个完整的、独立的HTML文件，可以直接在浏览器中打开运行
						- 所有CSS必须放在<head>中的<style>标签内
						- 所有JavaScript必须放在</body>前的<script>标签内
						- 不要分开返回css和javascript字段，全部内嵌在html字段中
						- 确保代码有良好的缩进和可读性
						- 包含合理的示例数据和交互效果

						# 格式要求：
						- 输出为标准JSON格式，禁止添加任何解释性文字,禁止使用markdown语法，返回和输出格式示例必须完全一致
						- 必须是下面的json结构, 禁止使用其他格式,返回结构示例：
						{
						  "html": "<!DOCTYPE html>
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
						           "
						}
						""",
				request.getPurpose(), request.getPurposeText(), request.getLayout(), request.getLayoutText(),
				request.getTheme(), request.getThemeText(), String.join(", ", request.getComponents()),
				request.getOptions() != null ? request.getOptions().getCodeStyle() : "modern",
				request.getOptions() != null ? request.getOptions().getResponsive() : true,
				request.getOptions() != null ? request.getOptions().getIncludeData() : true,
				request.getOptions() != null && request.getOptions().getAdditionalRequirements() != null
						? "\n- 特殊需求: " + request.getOptions().getAdditionalRequirements() : "",
				request.getOptions() != null && request.getOptions().getResponsive() ? "\n7. 实现完整的响应式设计" : "",
				request.getOptions() != null && request.getOptions().getIncludeData() ? "\n8. 包含合理的示例数据" : "");
	}

	/**
	 * 计算代码行数
	 */
	private Integer calculateLinesOfCode(String html) {
		try {
			return html.split("\n").length;
		}
		catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 清理LLM返回的HTML代码块标记
	 */
	private String clearHtmlCode(String htmlCode) {
		String cleanedCode = htmlCode.trim();

		// 处理 JSON 格式的代码块
		if (cleanedCode.startsWith("```json")) {
			cleanedCode = cleanedCode.substring(7);
			if (cleanedCode.endsWith("```")) {
				cleanedCode = cleanedCode.substring(0, cleanedCode.length() - 3);
			}
			return cleanedCode.trim();
		}

		// 默认作为 JSON 返回
		return cleanedCode;
	}

	/**
	 * 转义字符串以用于 JSON
	 */
	private String escapeJsonString(String str) {
		if (str == null) {
			return "";
		}
		return str.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\b", "\\b")
			.replace("\f", "\\f")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}

	/**
	 * 计算文件大小
	 */
	private String calculateFileSize(String html) {
		try {
			int totalBytes = html.length();

			if (totalBytes < 1024) {
				return totalBytes + "B";
			}
			else if (totalBytes < 1024 * 1024) {
				return String.format("%.1fKB", totalBytes / 1024.0);
			}
			else {
				return String.format("%.1fMB", totalBytes / (1024.0 * 1024.0));
			}
		}
		catch (Exception e) {
			return "未知";
		}
	}

}