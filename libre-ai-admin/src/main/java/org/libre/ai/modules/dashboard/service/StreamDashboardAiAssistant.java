package org.libre.ai.modules.dashboard.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.libre.ai.modules.dashboard.dto.DashboardCodeOutput;
import reactor.core.publisher.Flux;

/**
 * Dashboard AI Assistant 接口 使用LangChain4j AiServices实现声明式AI服务
 *
 * 遵循SRP原则：专注于AI交互接口定义 遵循ISP原则：接口隔离，只定义Dashboard生成相关方法 体现声明式编程：通过注解定义AI行为
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
public interface StreamDashboardAiAssistant {

	/**
	 * 生成仪表板代码 - 使用请求对象 使用@SystemMessage定义AI角色和要求 使用@UserMessage定义用户输入 自动处理JSON结构化输出
	 */
	@SystemMessage(fromResource = "prompt/dashboardPrompt.txt")
	@UserMessage("""
			{{prompt}}
			""")
	TokenStream generateDashboard(@V("prompt") String prompt);

	/**
	 * 流式生成仪表板代码 - 使用请求对象 使用@SystemMessage定义AI角色和要求 使用@UserMessage定义用户输入 自动处理JSON结构化输出
	 */
	@SystemMessage(fromResource = "prompt/dashboardPrompt.txt")
	Flux<String> generateDashboardFlux(String prompt);

	/**
	 * 优化已有的仪表板代码 用于代码改进和性能优化
	 */
	@SystemMessage("""
			你是一个前端性能优化专家。
			分析提供的代码，并进行以下优化：
			1. 性能优化：减少重排重绘，优化JavaScript执行
			2. 代码质量：改进代码结构，增强可维护性
			3. 最佳实践：应用现代前端最佳实践
			4. 无障碍性：改进可访问性
			返回优化后的代码。
			""")
	@UserMessage("""
			请优化以下仪表板代码：

			HTML:
			{{html}}

			CSS:
			{{css}}

			JavaScript:
			{{javascript}}

			优化重点：{{focus}}
			""")
	DashboardCodeOutput optimizeDashboard(@V("html") String html, @V("css") String css,
			@V("javascript") String javascript, @V("focus") String optimizationFocus);

}