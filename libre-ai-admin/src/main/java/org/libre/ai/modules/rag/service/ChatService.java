package org.libre.ai.modules.rag.service;

import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.ImageRequest;
import org.libre.ai.modules.rag.entity.AigcOss;

/**
 * @author tycoding
 * @since 2024/1/4
 */
public interface ChatService {

	void chat(ChatRequest req);

	/**
	 * 文本请求
	 */
	String text(ChatRequest req);

	/**
	 * 文生图
	 */
	AigcOss image(ImageRequest req);

}
