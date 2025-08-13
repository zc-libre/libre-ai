package org.libre.ai.modules.rag.core.service;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.http.client.sse.ServerSentEvent;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.TokenStream;
import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.ImageRequest;
import reactor.core.publisher.Flux;

/**
 * @author tycoding
 * @since 2024/3/8
 */
public interface LangChatService {

	TokenStream chat(ChatRequest req);

	TokenStream singleChat(ChatRequest req);

	Flux<String> chatFlux(ChatRequest req);

	String text(ChatRequest req);

	Response<Image> image(ImageRequest req);

}
