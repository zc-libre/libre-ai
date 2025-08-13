package org.libre.ai.modules.rag.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author tycoding
 * @since 2024/4/26
 */
@Data
@Accessors(chain = true)
public class EmbeddingResult {

	/**
	 * 写入到vector store的ID
	 */
	private String vectorId;

	/**
	 * 文档ID
	 */
	private String docsId;

	/**
	 * 知识库ID
	 */
	private String knowledgeId;

	/**
	 * Embedding后切片的文本
	 */
	private String text;

}
