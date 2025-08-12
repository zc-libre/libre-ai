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

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.core.consts.EmbedConst;
import org.libre.ai.modules.rag.core.provider.EmbeddingProvider;
import org.libre.ai.modules.rag.core.service.LangEmbeddingService;
import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.EmbeddingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/4
 */
@Slf4j
@Service
@AllArgsConstructor
public class LangEmbeddingServiceImpl implements LangEmbeddingService {

	private final EmbeddingProvider embeddingProvider;

	@Override
	public EmbeddingResult embeddingText(ChatRequest req) {
		log.info(">>>>>>>>>>>>>> Text文本向量解析开始，KnowledgeId={}, DocsName={}", req.getKnowledgeId(), req.getDocsName());
		TextSegment segment = TextSegment.from(req.getMessage(),
				Metadata.metadata(EmbedConst.KNOWLEDGE, req.getKnowledgeId())
					.put(EmbedConst.FILENAME, req.getDocsName()));

		EmbeddingModel embeddingModel = embeddingProvider.getEmbeddingModel(req.getKnowledgeId());
		EmbeddingStore<TextSegment> embeddingStore = embeddingProvider.getEmbeddingStore(req.getKnowledgeId());
		Embedding embedding = embeddingModel.embed(segment).content();
		String id = embeddingStore.add(embedding, segment);

		log.info(">>>>>>>>>>>>>> Text文本向量解析结束，KnowledgeId={}, DocsName={}", req.getKnowledgeId(), req.getDocsName());
		return new EmbeddingResult().setVectorId(id).setText(segment.text());
	}

	@Override
	public List<EmbeddingResult> embeddingDocs(ChatRequest req) {
		log.info(">>>>>>>>>>>>>> Docs文档向量解析开始，KnowledgeId={}, DocsName={}", req.getKnowledgeId(), req.getDocsName());
		Document document = UrlDocumentLoader.load(req.getUrl(), new ApacheTikaDocumentParser());
		document.metadata().put(EmbedConst.KNOWLEDGE, req.getKnowledgeId()).put(EmbedConst.FILENAME, req.getDocsName());

		List<EmbeddingResult> list = new ArrayList<>();
		try {
			DocumentSplitter splitter = EmbeddingProvider.splitter();
			List<TextSegment> segments = splitter.split(document);
			log.info("文档分片完成，共生成 {} 个片段", segments.size());

			EmbeddingModel embeddingModel = embeddingProvider.getEmbeddingModel(req.getKnowledgeId());
			EmbeddingStore<TextSegment> embeddingStore = embeddingProvider.getEmbeddingStore(req.getKnowledgeId());
			List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
			List<String> ids = embeddingStore.addAll(embeddings, segments);

			for (int i = 0; i < ids.size(); i++) {
				list.add(new EmbeddingResult().setVectorId(ids.get(i)).setText(segments.get(i).text()));
			}
		}
		catch (Exception e) {
			log.error("文档向量化处理失败，KnowledgeId={}, DocsName={}", req.getKnowledgeId(), req.getDocsName(), e);
			throw new RuntimeException("文档向量化处理失败: " + e.getMessage(), e);
		}

		log.info(">>>>>>>>>>>>>> Docs文档向量解析结束，KnowledgeId={}, DocsName={}", req.getKnowledgeId(), req.getDocsName());
		return list;
	}

}
