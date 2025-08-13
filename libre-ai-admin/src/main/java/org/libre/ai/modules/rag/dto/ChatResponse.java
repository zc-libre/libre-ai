package org.libre.ai.modules.rag.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author tycoding
 * @since 2024/1/29
 */
@Data
@Accessors(chain = true)
public class ChatResponse {

	private boolean isDone = false;

	private String message;

	private Integer usedToken;

	private long time;

	public ChatResponse(String message) {
		this.message = message;
	}

	public ChatResponse(Integer usedToken, long startTime) {
		this.isDone = true;
		this.usedToken = usedToken;
		this.time = System.currentTimeMillis() - startTime;
	}

}
