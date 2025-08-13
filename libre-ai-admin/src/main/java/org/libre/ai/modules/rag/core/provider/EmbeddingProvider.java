package org.libre.ai.modules.rag.core.provider;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.modality.nlp.preprocess.Tokenizer;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenCountEstimator;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.entity.AigcKnowledge;
import org.libre.ai.modules.rag.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author tycoding
 * @since 2024/3/8
 */
@Slf4j
@Component
@AllArgsConstructor
public class EmbeddingProvider {

	private final EmbeddingStoreFactory embeddingStoreFactory;

	private final KnowledgeStoreFactory knowledgeStoreFactory;

	private final ModelStoreFactory modelStoreFactory;

	public static DocumentSplitter splitter() {
		HuggingFaceTokenCountEstimator tokenCountEstimator = new HuggingFaceTokenCountEstimator();
		return new DocumentBySentenceSplitter(100, 0,
				tokenCountEstimator);
	}

	public EmbeddingModel getEmbeddingModel(List<String> knowledgeIds) {
		List<String> storeIds = new ArrayList<>();
		knowledgeIds.forEach(id -> {
			if (knowledgeStoreFactory.containsKnowledge(id)) {
				AigcKnowledge data = knowledgeStoreFactory.getKnowledge(id);
				if (data.getEmbedModelId() != null) {
					storeIds.add(data.getEmbedModelId());
				}
			}
		});
		if (storeIds.isEmpty()) {
			throw new ServiceException("知识库缺少Embedding Model配置，请先检查配置");
		}

		HashSet<String> filterIds = new HashSet<>(storeIds);
		if (filterIds.size() > 1) {
			throw new ServiceException("存在多个不同Embedding Model的知识库，请先检查配置");
		}

		return modelStoreFactory.getEmbeddingModel(storeIds.getFirst());
	}

	public EmbeddingModel getEmbeddingModel(String knowledgeId) {
		if (knowledgeStoreFactory.containsKnowledge(knowledgeId)) {
			AigcKnowledge data = knowledgeStoreFactory.getKnowledge(knowledgeId);
			if (modelStoreFactory.containsEmbeddingModel(data.getEmbedModelId())) {
				return modelStoreFactory.getEmbeddingModel(data.getEmbedModelId());
			}
		}
		throw new ServiceException("没有找到匹配的Embedding向量数据库");
	}

	public EmbeddingStore<TextSegment> getEmbeddingStore(String knowledgeId) {
		if (knowledgeStoreFactory.containsKnowledge(knowledgeId)) {
			AigcKnowledge data = knowledgeStoreFactory.getKnowledge(knowledgeId);
			if (embeddingStoreFactory.containsEmbeddingStore(data.getEmbedStoreId())) {
				return embeddingStoreFactory.getEmbeddingStore(data.getEmbedStoreId());
			}
		}
		throw new ServiceException("没有找到匹配的Embedding向量数据库");
	}

	public EmbeddingStore<TextSegment> getEmbeddingStore(List<String> knowledgeIds) {
		List<String> storeIds = new ArrayList<>();
		knowledgeIds.forEach(id -> {
			if (knowledgeStoreFactory.containsKnowledge(id)) {
				AigcKnowledge data = knowledgeStoreFactory.getKnowledge(id);
				if (data.getEmbedStoreId() != null) {
					storeIds.add(data.getEmbedStoreId());
				}
			}
		});
		if (storeIds.isEmpty()) {
			throw new ServiceException("知识库缺少Embedding Store配置，请先检查配置");
		}

		HashSet<String> filterIds = new HashSet<>(storeIds);
		if (filterIds.size() > 1) {
			throw new ServiceException("存在多个不同Embedding Store数据源的知识库，请先检查配置");
		}

		return embeddingStoreFactory.getEmbeddingStore(storeIds.getFirst());
	}

}
