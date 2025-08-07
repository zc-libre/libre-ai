/*
 * Copyright (c) 2024 LangChat. TyCoding All Rights Reserved.
 *
 * Licensed under the GNU Affero General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.libre.ai.modules.rag.core.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.core.provider.EmbeddingProvider;
import org.libre.ai.modules.rag.core.provider.ModelProvider;
import org.libre.ai.modules.rag.core.service.Agent;
import org.libre.ai.modules.rag.core.service.LangChatService;
import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.ImageRequest;
import org.libre.ai.modules.rag.exception.ServiceException;
import org.libre.ai.modules.rag.properties.ChatProperties;
import org.libre.ai.modules.rag.utils.PromptUtil;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static org.libre.ai.modules.rag.core.consts.EmbedConst.KNOWLEDGE;

/**
 * @author tycoding
 * @since 2024/3/8
 */
@Slf4j
@Service
@AllArgsConstructor
public class LangChatServiceImpl implements LangChatService {

	private final ModelProvider provider;

	private final EmbeddingProvider embeddingProvider;

	private final ChatProperties chatProperties;

	private AiServices<Agent> build(StreamingChatModel streamModel, ChatModel model, ChatRequest req) {
		AiServices<Agent> aiServices = AiServices.builder(Agent.class)
			.chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
				.id(req.getConversationId())
				.chatMemoryStore(new PersistentChatMemoryStore())
				.maxMessages(chatProperties.getMemoryMaxMessage())
				.build());
		if (StrUtil.isNotBlank(req.getPromptText())) {
			aiServices.systemMessageProvider(memoryId -> req.getPromptText());
		}
		if (streamModel != null) {
			aiServices.streamingChatModel(streamModel);
		}
		if (model != null) {
			aiServices.chatModel(model);
		}
		return aiServices;
	}

	@Override
	public TokenStream chat(ChatRequest req) {
		StreamingChatModel model = provider.stream(req.getModelId());
		if (StrUtil.isBlank(req.getConversationId())) {
			req.setConversationId(IdUtil.simpleUUID());
		}

		AiServices<Agent> aiServices = build(model, null, req);

		if (StrUtil.isNotBlank(req.getKnowledgeId())) {
			req.getKnowledgeIds().add(req.getKnowledgeId());
		}

		if (req.getKnowledgeIds() != null && !req.getKnowledgeIds().isEmpty()) {
			Function<Query, Filter> filter = (query) -> metadataKey(KNOWLEDGE).isIn(req.getKnowledgeIds());
			ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
				.embeddingStore(embeddingProvider.getEmbeddingStore(req.getKnowledgeIds()))
				.embeddingModel(embeddingProvider.getEmbeddingModel(req.getKnowledgeIds()))
				.dynamicFilter(filter)
				.build();
			aiServices
				.retrievalAugmentor(DefaultRetrievalAugmentor.builder().contentRetriever(contentRetriever).build());
		}
		Agent agent = aiServices.build();
		return agent.stream(req.getConversationId(), req.getMessage());
	}

	@Override
	public TokenStream singleChat(ChatRequest req) {
		StreamingChatModel model = provider.stream(req.getModelId());
		if (StrUtil.isBlank(req.getConversationId())) {
			req.setConversationId(IdUtil.simpleUUID());
		}

		Agent agent = build(model, null, req).build();
		if (req.getPrompt() == null) {
			req.setPrompt(PromptUtil.build(req.getMessage(), req.getPromptText()));
		}
		return agent.stream(req.getConversationId(), req.getPrompt().text());
	}

	@Override
	public String text(ChatRequest req) {
		if (StrUtil.isBlank(req.getConversationId())) {
			req.setConversationId(IdUtil.simpleUUID());
		}

		try {
			ChatModel model = provider.text(req.getModelId());
			Agent agent = build(null, model, req).build();
			String text = agent.text(req.getConversationId(), req.getMessage());
			return text;
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public Response<Image> image(ImageRequest req) {
		try {
			ImageModel model = provider.image(req.getModelId());
			return model.generate(req.getPrompt().text());
		}
		catch (Exception e) {

			throw new ServiceException("图片生成失败");
		}
	}

}
