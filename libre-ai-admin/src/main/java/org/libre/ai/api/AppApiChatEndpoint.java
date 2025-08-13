package org.libre.ai.api;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.api.auth.CompletionReq;
import org.libre.ai.api.auth.CompletionRes;
import org.libre.ai.api.auth.OpenapiAuth;
import org.libre.ai.modules.rag.core.consts.AppConst;
import org.libre.ai.modules.rag.core.service.LangChatService;
import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.entity.AigcApp;
import org.libre.ai.modules.rag.entity.AigcAppApi;
import org.libre.ai.modules.rag.exception.ServiceException;
import org.libre.ai.modules.rag.store.AppChannelStore;
import org.libre.ai.modules.rag.store.AppStore;
import org.libre.ai.modules.rag.utils.StreamEmitter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/7/26
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AppApiChatEndpoint {

	private final LangChatService langChatService;

	private final AppStore appStore;

	@OpenapiAuth(AppConst.CHANNEL_API)
	@PostMapping(value = "/chat/completions")
	public SseEmitter completions(@RequestBody CompletionReq req) {
		StreamEmitter emitter = new StreamEmitter();
		AigcAppApi appApi = AppChannelStore.getApiChannel();

		return handler(emitter, appApi.getAppId(), req.getMessages());
	}

	private SseEmitter handler(StreamEmitter emitter, String appId, List<CompletionReq.Message> messages) {
		if (messages == null || messages.isEmpty() || StrUtil.isBlank(appId)) {
			throw new RuntimeException("聊天消息为空，或者没有配置模型信息");
		}

		CompletionReq.Message message = messages.getFirst();
		AigcApp app = appStore.get(appId);
		if (app == null) {
			throw new ServiceException("没有配置应用信息");
		}

		ChatRequest req = new ChatRequest().setMessage(message.getContent())
			.setRole(message.getRole())
			.setModelId(app.getModelId())
			.setSystemPrompt(app.getSystemPrompt())
			.setUserPromptTemplate(app.getUserPromptTemplate())
			.setKnowledgeIds(app.getKnowledgeIds());

		langChatService.singleChat(req).onPartialResponse(token -> {
			CompletionRes res = CompletionRes.process(token);
			emitter.send(res);
		}).onCompleteResponse(c -> {
			CompletionRes res = CompletionRes.end(c);
			emitter.send(res);
			emitter.complete();
		}).onError(e -> {
			emitter.error(e.getMessage());
		}).start();

		return emitter.get();
	}

}
