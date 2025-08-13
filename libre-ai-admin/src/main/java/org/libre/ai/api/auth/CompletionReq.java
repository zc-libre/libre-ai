package org.libre.ai.api.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/7/30
 */
@Data
@Builder
public class CompletionReq {

	private final String model;

	private final List<Message> messages;

	private final Double temperature;

	private final Double topP;

	private final Integer n;

	private final Boolean stream;

	private final List<String> stop;

	private final Integer maxTokens;

	private final Double presencePenalty;

	private final Double frequencyPenalty;

	private final String user;

	private final Integer seed;

	@Data
	@Builder
	public static class Message {

		String role;

		String content;

	}

}
