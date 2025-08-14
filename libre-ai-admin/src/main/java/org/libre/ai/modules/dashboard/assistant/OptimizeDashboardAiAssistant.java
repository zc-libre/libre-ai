package org.libre.ai.modules.dashboard.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import reactor.core.publisher.Flux;

public interface OptimizeDashboardAiAssistant {


    /**
     * 对话式优化仪表板代码 支持基于上下文的连续对话优化
     * @param conversationId 对话会话ID，用于保持上下文
     * @param currentHtml 当前的HTML代码
     * @param userRequest 用户的优化需求
     * @return 优化后的代码流
     */
    @SystemMessage("""
			你是一个专业的前端优化专家。
			你正在帮助用户优化他的仪表板代码。
			基于之前的对话历史和当前代码，根据用户需求进行精确的优化。

			优化原则：
			1. 保持代码的完整性和功能性
			2. 只修改与用户需求相关的部分,严禁修改其他部分
			3. 保留原有的数据和交互逻辑
			4. 优化后的代码应该是完整可运行的HTML文件

			输出要求：
			- 返回完整的HTML文档，包含所有内嵌的CSS和JavaScript
			- 确保代码格式正确，可以直接在浏览器中运行
			- 不要使用markdown代码块包裹
			""")
    @UserMessage("""
			当前代码：
			{{currentHtml}}

			用户需求：
			{{userRequest}}

			请根据用户需求优化代码，并返回完整的HTML代码。
			""")
    Flux<String> optimizeDashboardStream(@MemoryId String conversationId, String prompt);
}
