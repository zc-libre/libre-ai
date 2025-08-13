package org.libre.ai.modules.rag.core.provider.build;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import org.libre.ai.modules.rag.entity.AigcModel;

/**
 * @author GB
 * @since 2024-08-18 09:57
 */
public interface ModelBuildHandler {

	/**
	 * 判断是不是当前模型
	 */
	boolean whetherCurrentModel(AigcModel model);

	/**
	 * basic check
	 */
	boolean basicCheck(AigcModel model);

	/**
	 * streaming chat build
	 */
	StreamingChatModel buildStreamingChat(AigcModel model);

	/**
	 * chat build
	 */
	ChatModel buildChatLanguageModel(AigcModel model);

	/**
	 * embedding config
	 */
	EmbeddingModel buildEmbedding(AigcModel model);

	/**
	 * image config
	 */
	ImageModel buildImage(AigcModel model);

}
