package org.libre.ai.modules.rag.service;

import org.libre.ai.modules.rag.entity.AigcDocs;

import java.util.List;
import java.util.Map;

/**
 * @author tycoding
 * @since 2024/6/6
 */
public interface EmbeddingService {

	void clearDocSlices(String docsId);

	void embedDocsSlice(AigcDocs data, String url);

	List<Map<String, Object>> search(AigcDocs data);

}
