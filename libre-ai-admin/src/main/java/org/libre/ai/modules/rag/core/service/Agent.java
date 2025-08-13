package org.libre.ai.modules.rag.core.service;

import dev.langchain4j.http.client.sse.ServerSentEvent;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * @author tycoding
 * @since 2024/3/8
 */
public interface Agent {

	TokenStream stream(@MemoryId String id, @UserMessage String message);

	Flux<String> streamFlux(@MemoryId String id, @UserMessage String message);

	String text(@MemoryId String id, @UserMessage String message);

}
