package org.libre.ai.modules.rag.core.service;

import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.EmbeddingResult;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/4/4
 */
public interface LangEmbeddingService {

	EmbeddingResult embeddingText(ChatRequest req);

	List<EmbeddingResult> embeddingDocs(ChatRequest req);

}
