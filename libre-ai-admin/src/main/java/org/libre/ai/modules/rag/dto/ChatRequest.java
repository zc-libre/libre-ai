package org.libre.ai.modules.rag.dto;

import dev.langchain4j.model.input.Prompt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.libre.ai.modules.rag.utils.StreamEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author tycoding
 * @since 2024/1/30
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

	private String appId;

	private String modelId;

	private String modelName;

	private String modelProvider;

	private String message;

	private String conversationId;

	private String userId;

	private String username;

	private String chatId;

	private String systemPrompt;

	private String userPromptTemplate;

	private String docsName;

	private String knowledgeId;

	private List<String> knowledgeIds = new ArrayList<>();

	private String docsId;

	private String url;

	private String role;

	private Prompt prompt;

	private StreamEmitter emitter;

	private Executor executor;

}
