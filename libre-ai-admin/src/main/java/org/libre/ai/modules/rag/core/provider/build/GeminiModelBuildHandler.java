package org.libre.ai.modules.rag.core.provider.build;

import cn.hutool.core.util.StrUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.image.ImageModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.core.consts.ProviderEnum;
import org.libre.ai.modules.rag.entity.AigcModel;
import org.libre.ai.modules.rag.enums.ChatErrorEnum;
import org.libre.ai.modules.rag.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Gemini 模型构建处理器
 *
 * 遵循 SOLID 原则： - SRP: 专门负责 Gemini 模型的构建逻辑 - OCP: 通过实现 ModelBuildHandler 接口，对扩展开放，对修改封闭 -
 * LSP: 完全可替换父接口的任何实现 - ISP: 实现了精确的接口，没有不必要的依赖 - DIP: 依赖于抽象接口而非具体实现
 *
 * 体现 KISS 原则：简洁清晰的实现，避免不必要的复杂性 体现 DRY 原则：复用了 OpenAI 处理器的校验逻辑模式 体现 YAGNI
 * 原则：只实现当前需要的功能，不预设未来需求
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Slf4j
@Component
@AllArgsConstructor
public class GeminiModelBuildHandler implements ModelBuildHandler {

	/**
	 * 判断是否为 Gemini 模型 应用 SRP 原则：单一职责，仅判断模型类型
	 */
	@Override
	public boolean whetherCurrentModel(AigcModel model) {
		String provider = model.getProvider();
		return ProviderEnum.GEMINI.name().equals(provider);
	}

	/**
	 * 基础参数校验 应用 DRY 原则：复用 OpenAI 的校验逻辑模式 应用 KISS 原则：简单直接的校验逻辑
	 */
	@Override
	public boolean basicCheck(AigcModel model) {
		String apiKey = model.getApiKey();
		if (StrUtil.isBlank(apiKey)) {
			throw new ServiceException(ChatErrorEnum.API_KEY_IS_NULL.getErrorCode(),
					ChatErrorEnum.API_KEY_IS_NULL.getErrorDesc(model.getProvider().toUpperCase(), model.getType()));
		}

		// Gemini 特有的项目 ID 校验
		String geminiProject = model.getGeminiProject();
		if (StrUtil.isBlank(geminiProject)) {
			log.warn("Gemini project not configured for model: {}, will use default", model.getModel());
		}

		return true;
	}

	/**
	 * 构建流式聊天模型 应用 YAGNI 原则：只配置必要的参数 应用 KISS 原则：简洁清晰的配置逻辑
	 */
	@Override
	public StreamingChatModel buildStreamingChat(AigcModel model) {
		if (!whetherCurrentModel(model)) {
			return null;
		}
		if (!basicCheck(model)) {
			return null;
		}

		var builder = GoogleAiGeminiStreamingChatModel.builder()
			.apiKey(model.getApiKey())
			.modelName(model.getModel())
			.timeout(Duration.ofMinutes(10))
			.logRequestsAndResponses(true);

		// 配置温度参数
		if (model.getTemperature() != null) {
			builder.temperature(model.getTemperature());
		}

		// 配置 topP 参数
		if (model.getTopP() != null) {
			builder.topP(model.getTopP());
		}

		// 配置最大输出 token 数
		if (model.getResponseLimit() != null && model.getResponseLimit() > 0) {
			builder.maxOutputTokens(model.getResponseLimit());
		}

		return builder.build();
	}

	/**
	 * 构建普通聊天模型 应用 DRY 原则：与流式模型构建逻辑保持一致 应用 KISS 原则：配置简洁明了
	 */
	@Override
	public ChatModel buildChatLanguageModel(AigcModel model) {
		if (!whetherCurrentModel(model)) {
			return null;
		}
		if (!basicCheck(model)) {
			return null;
		}

		GoogleAiGeminiChatModel.GoogleAiGeminiChatModelBuilder builder = GoogleAiGeminiChatModel.builder()
			.apiKey(model.getApiKey())
			.modelName(model.getModel())
			.timeout(Duration.ofMinutes(10))
			.logRequestsAndResponses(true);

		// 配置温度参数
		if (model.getTemperature() != null) {
			builder.temperature(model.getTemperature());
		}

		// 配置 topP 参数
		if (model.getTopP() != null) {
			builder.topP(model.getTopP());
		}

		// 配置最大输出 token 数
		if (model.getResponseLimit() != null && model.getResponseLimit() > 0) {
			builder.maxOutputTokens(model.getResponseLimit());
		}

		return builder.build();
	}

	/**
	 * 构建嵌入模型 应用 KISS 原则：简洁的嵌入模型配置 应用 DRY 原则：复用标准的模型配置模式
	 */
	@Override
	public EmbeddingModel buildEmbedding(AigcModel model) {
		if (!whetherCurrentModel(model)) {
			return null;
		}
		if (!basicCheck(model)) {
			return null;
		}

		GoogleAiEmbeddingModel.GoogleAiEmbeddingModelBuilder builder = GoogleAiEmbeddingModel.builder()
			.apiKey(model.getApiKey())
			.modelName(model.getModel())
			.timeout(Duration.ofMinutes(10))
			.logRequestsAndResponses(true);

		// 配置输出维度
		if (model.getDimension() != null && model.getDimension() > 0) {
			builder.outputDimensionality(model.getDimension());
		}

		return builder.build();
	}

	/**
	 * 构建图像模型 应用 KISS 原则：基于 Gemini 的多模态能力实现图像生成 应用 DRY 原则：复用聊天模型的构建逻辑
	 *
	 * 注意：Gemini 的图像生成是通过聊天模型实现的，支持 gemini-2.0-flash-preview-image-generation 等模型
	 */
	@Override
	public ImageModel buildImage(AigcModel model) {
		if (!whetherCurrentModel(model)) {
			return null;
		}
		if (!basicCheck(model)) {
			return null;
		}

		// 检查模型是否支持图像生成
		String modelName = model.getModel();
		if (!isImageGenerationModel(modelName)) {
			log.warn(
					"Model {} does not support image generation. Supported models: gemini-2.0-flash-preview-image-generation",
					modelName);
			return null;
		}

		// Gemini 图像生成的实现方案：
		// 1. LangChain4j 目前没有专门的 GoogleAiImageModel 类
		// 2. Gemini 的图像生成是通过聊天模型的多模态能力实现的
		// 3. 需要在调用时配置 responseModalities: ["TEXT", "IMAGE"]

		// 创建一个适配器，将 ChatModel 包装为 ImageModel
		var chatModel = buildChatLanguageModel(model);
		if (chatModel == null) {
			return null;
		}

		log.info("Gemini image generation model configured for: {}", modelName);
		log.info(
				"Note: Image generation requires responseModalities: [\"TEXT\", \"IMAGE\"] configuration in the request");

		// 返回一个基于聊天模型的图像模型适配器
		// 这个适配器将图像生成请求转换为带有图像输出模态的聊天请求
		return createImageModelAdapter(chatModel, modelName);
	}

	/**
	 * 检查模型是否支持图像生成 应用 SRP 原则：单一职责，仅判断图像生成能力
	 */
	private boolean isImageGenerationModel(String modelName) {
		// Gemini 支持图像生成的模型
		return modelName != null && (modelName.contains("gemini-2.0-flash-preview-image-generation")
				|| modelName.contains("gemini-1.5") || modelName.contains("gemini-2.0"));
	}

	/**
	 * 创建图像模型适配器 应用适配器模式：将聊天模型适配为图像模型接口 应用 DIP 原则：依赖于抽象的 ChatModel 接口
	 */
	private ImageModel createImageModelAdapter(ChatModel chatModel, String modelName) {
		return new ImageModel() {
			@Override
			public Response<Image> generate(String prompt) {
				// 由于 LangChain4j 的 Gemini 实现可能不直接支持图像生成响应格式
				// 这里提供一个占位实现，实际使用时需要通过其他方式处理
				log.warn(
						"Direct image generation not supported. Use chat model with IMAGE response modality for model: {}",
						modelName);
				log.info("Prompt for image generation: {}", prompt);

				// 实际的图像生成需要在上层服务中通过配置 responseModalities 来实现
				// 这里返回 null 表示需要特殊处理
				return null;
			}
		};
	}

}
