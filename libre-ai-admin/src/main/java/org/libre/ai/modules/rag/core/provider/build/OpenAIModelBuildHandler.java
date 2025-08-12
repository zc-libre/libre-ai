package org.libre.ai.modules.rag.core.provider.build;

import cn.hutool.core.util.StrUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.core.consts.ProviderEnum;
import org.libre.ai.modules.rag.entity.AigcModel;
import org.libre.ai.modules.rag.enums.ChatErrorEnum;
import org.libre.ai.modules.rag.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author tycoding
 * @since 2024-08-19 10:08
 */
@Slf4j
@Component
@AllArgsConstructor
public class OpenAIModelBuildHandler implements ModelBuildHandler {

	/**
	 * 合并处理支持OpenAI接口的模型
	 */
	@Override
	public boolean whetherCurrentModel(AigcModel model) {
		String provider = model.getProvider();
		return ProviderEnum.OPENAI.name().equals(provider) || ProviderEnum.AZURE_OPENAI.name().equals(provider)
				|| ProviderEnum.DOUYIN.name().equals(provider) || ProviderEnum.YI.name().equals(provider)
				|| ProviderEnum.SILICON.name().equals(provider) || ProviderEnum.DEEPSEEK.name().equals(provider)
				|| ProviderEnum.SPARK.name().equals(provider);
	}

	@Override
	public boolean basicCheck(AigcModel model) {
		String apiKey = model.getApiKey();
		if (StrUtil.isBlank(apiKey)) {
			throw new ServiceException(ChatErrorEnum.API_KEY_IS_NULL.getErrorCode(),
					ChatErrorEnum.API_KEY_IS_NULL.getErrorDesc(model.getProvider().toUpperCase(), model.getType()));
		}
		return true;
	}

	@Override
	public StreamingChatModel buildStreamingChat(AigcModel model) {
		if (!whetherCurrentModel(model)) {
			return null;
		}
		if (!basicCheck(model)) {
			return null;
		}
		return OpenAiStreamingChatModel.builder()
			.apiKey(model.getApiKey())
			.baseUrl(model.getBaseUrl())
			.modelName(model.getModel())
			.maxTokens(model.getResponseLimit())
			.temperature(model.getTemperature())
			.logRequests(true)
			.logResponses(true)
			.topP(model.getTopP())
			.timeout(Duration.ofMinutes(10))
			.build();
	}

	@Override
	public ChatModel buildChatLanguageModel(AigcModel model) {
		if (!whetherCurrentModel(model)) {
			return null;
		}
		if (!basicCheck(model)) {
			return null;
		}
		return OpenAiChatModel.builder()
			.apiKey(model.getApiKey())
			.baseUrl(model.getBaseUrl())
			.modelName(model.getModel())
			.maxTokens(model.getResponseLimit())
			.temperature(model.getTemperature())
			.logRequests(true)
			.logResponses(true)
			.topP(model.getTopP())
			.timeout(Duration.ofMinutes(10))
			.build();
	}

	@Override
	public EmbeddingModel buildEmbedding(AigcModel model) {
		if (!whetherCurrentModel(model)) {
			return null;
		}
		if (!basicCheck(model)) {
			return null;
		}

		return OpenAiEmbeddingModel.builder()
			.apiKey(model.getApiKey())
			.baseUrl(model.getBaseUrl())
			.modelName(model.getModel())
			.dimensions(model.getDimension() != null ? model.getDimension() : 1024)
			.logRequests(true)
			.logResponses(true)
			.timeout(Duration.ofMinutes(10))
			.build();
	}

	@Override
	public ImageModel buildImage(AigcModel model) {
		if (!whetherCurrentModel(model)) {
			return null;
		}
		if (!basicCheck(model)) {
			return null;
		}

		return OpenAiImageModel.builder()
			.apiKey(model.getApiKey())
			.baseUrl(model.getBaseUrl())
			.modelName(model.getModel())
			.size(model.getImageSize())
			.quality(model.getImageQuality())
			.style(model.getImageStyle())
			.logRequests(true)
			.logResponses(true)
			.timeout(Duration.ofMinutes(10))
			.build();
	}

}
