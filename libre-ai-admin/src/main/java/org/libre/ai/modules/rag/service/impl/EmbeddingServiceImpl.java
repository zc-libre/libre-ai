package org.libre.ai.modules.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.core.provider.EmbeddingProvider;
import org.libre.ai.modules.rag.core.service.LangEmbeddingService;
import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.EmbeddingResult;
import org.libre.ai.modules.rag.entity.AigcDocs;
import org.libre.ai.modules.rag.entity.AigcDocsSlice;
import org.libre.ai.modules.rag.mapper.AigcDocsMapper;
import org.libre.ai.modules.rag.service.AigcKnowledgeService;
import org.libre.ai.modules.rag.service.EmbeddingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static org.libre.ai.modules.rag.core.consts.EmbedConst.KNOWLEDGE;

/**
 * @author tycoding
 * @since 2024/6/6
 */
@Slf4j
@Service
@AllArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

	private final EmbeddingProvider embeddingProvider;

	private final LangEmbeddingService langEmbeddingService;

	private final AigcKnowledgeService aigcKnowledgeService;

	private final AigcDocsMapper aigcDocsMapper;

	@Override
	@Transactional
	public void clearDocSlices(String docsId) {
		if (StrUtil.isBlank(docsId)) {
			return;
		}
		// remove from embedding store
		List<String> vectorIds = aigcKnowledgeService.listSliceVectorIdsOfDoc(docsId);
		if (vectorIds.isEmpty()) {
			return;
		}
		AigcDocs docs = aigcDocsMapper.selectById(docsId);
		EmbeddingStore<TextSegment> embeddingStore = embeddingProvider.getEmbeddingStore(docs.getKnowledgeId());
		embeddingStore.removeAll(vectorIds);
		// remove from docSlice
		aigcKnowledgeService.removeSlicesOfDoc(docsId);
	}

	@Override
	public void embedDocsSlice(AigcDocs data, String url) {
		List<EmbeddingResult> list = langEmbeddingService.embeddingDocs(
				new ChatRequest().setDocsName(data.getName()).setKnowledgeId(data.getKnowledgeId()).setUrl(url));
		list.forEach(i -> {
			aigcKnowledgeService.addDocsSlice(new AigcDocsSlice().setKnowledgeId(data.getKnowledgeId())
				.setDocsId(data.getId())
				.setVectorId(i.getVectorId())
				.setName(data.getName())
				.setContent(i.getText()));
		});

		aigcKnowledgeService.updateDocs(new AigcDocs().setId(data.getId()).setSliceStatus(1).setSliceNum(list.size()));
	}

	@Override
	public List<Map<String, Object>> search(AigcDocs data) {
		if (StrUtil.isBlank(data.getKnowledgeId()) || StrUtil.isBlank(data.getContent())) {
			return List.of();
		}

		EmbeddingModel embeddingModel = embeddingProvider.getEmbeddingModel(data.getKnowledgeId());
		EmbeddingStore<TextSegment> embeddingStore = embeddingProvider.getEmbeddingStore(data.getKnowledgeId());
		Embedding queryEmbedding = embeddingModel.embed(data.getContent()).content();
		Filter filter = metadataKey(KNOWLEDGE).isEqualTo(data.getKnowledgeId());
		EmbeddingSearchResult<TextSegment> list = embeddingStore
			.search(EmbeddingSearchRequest.builder().queryEmbedding(queryEmbedding).filter(filter).build());

		List<Map<String, Object>> result = new ArrayList<>();
		list.matches().forEach(i -> {
			TextSegment embedded = i.embedded();
			Map<String, Object> map = embedded.metadata().toMap();
			map.put("text", embedded.text());
			map.put("score", i.score());
			result.add(map);
		});
		return result;
	}

}
