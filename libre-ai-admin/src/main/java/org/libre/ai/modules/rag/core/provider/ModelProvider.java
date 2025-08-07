package org.libre.ai.modules.rag.core.provider;

import cn.hutool.core.util.ObjectUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.image.ImageModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author tycoding
 * @since 2024/3/8
 */
@Component
@AllArgsConstructor
public class ModelProvider {

	private final ModelStoreFactory modelStoreFactory;

	public StreamingChatModel stream(String modelId) {
		StreamingChatModel streamingChatModel = modelStoreFactory.getStreamingChatModel(modelId);
		if (ObjectUtil.isNotEmpty(streamingChatModel)) {
			return streamingChatModel;
		}
		throw new RuntimeException("没有匹配到模型，请检查模型配置！");
	}

	public ChatModel text(String modelId) {
		ChatModel chatLanguageModel = modelStoreFactory.getChatLanguageModel(modelId);
		if (ObjectUtil.isNotEmpty(chatLanguageModel)) {
			return chatLanguageModel;
		}
		throw new RuntimeException("没有匹配到模型，请检查模型配置！");
	}

	public ImageModel image(String modelId) {
		ImageModel imageModel = modelStoreFactory.getImageModel(modelId);
		if (ObjectUtil.isNotEmpty(imageModel)) {
			return imageModel;
		}
		throw new RuntimeException("没有匹配到模型，请检查模型配置！");
	}

}
