package org.libre.ai.modules.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.core.service.LangChatService;
import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.ChatResponse;
import org.libre.ai.modules.rag.dto.ImageRequest;
import org.libre.ai.modules.rag.entity.AigcApp;
import org.libre.ai.modules.rag.entity.AigcMessage;
import org.libre.ai.modules.rag.entity.AigcOss;
import org.libre.ai.modules.rag.service.AigcMessageService;
import org.libre.ai.modules.rag.service.ChatService;
import org.libre.ai.modules.rag.store.AppStore;
import org.libre.ai.modules.rag.utils.StreamEmitter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author tycoding
 * @since 2024/1/4
 */
@Slf4j
@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

	private final LangChatService langChatService;

	private final AigcMessageService aigcMessageService;

	private final AppStore appStore;

	@Override
	public void chat(ChatRequest req) {
		StreamEmitter emitter = req.getEmitter();
		long startTime = System.currentTimeMillis();
		StringBuilder text = new StringBuilder();

		if (StrUtil.isNotBlank(req.getAppId())) {
			AigcApp app = appStore.get(req.getAppId());
			if (app != null) {
				req.setModelId(app.getModelId());
				req.setSystemPrompt(app.getSystemPrompt());
				req.setKnowledgeIds(app.getKnowledgeIds());
			}
		}

		// save user message
		// req.setRole(RoleEnum.USER.getName());
		saveMessage(req, 0, 0);

		try {
			langChatService.chat(req).onPartialResponse(e -> {
				text.append(e);
				emitter.send(new ChatResponse(e));
			}).onCompleteResponse((e) -> {
				TokenUsage tokenUsage = e.tokenUsage();
				ChatResponse res = new ChatResponse(tokenUsage.totalTokenCount(), startTime);
				emitter.send(res);
				emitter.complete();

				// save assistant message
				req.setMessage(text.toString());
				// req.setRole(RoleEnum.ASSISTANT.getName());
				saveMessage(req, tokenUsage.inputTokenCount(), tokenUsage.outputTokenCount());
			}).onError((e) -> {
				emitter.error(e.getMessage());
				throw new RuntimeException(e.getMessage());
			}).start();
		}
		catch (Exception e) {
			e.printStackTrace();
			emitter.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	private void saveMessage(ChatRequest req, Integer inputToken, Integer outputToken) {
		if (req.getConversationId() != null) {
			AigcMessage message = new AigcMessage();
			BeanUtils.copyProperties(req, message);
			// message.setIp(ServletUtil.getIpAddr());
			message.setPromptTokens(inputToken);
			message.setTokens(outputToken);
			aigcMessageService.addMessage(message);
		}
	}

	@Override
	public String text(ChatRequest req) {
		String text;
		try {
			text = langChatService.text(req);
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return text;
	}

	@Override
	public AigcOss image(ImageRequest req) {
		Response<Image> res = langChatService.image(req);

		String path = res.content().url().toString();
		AigcOss oss = new AigcOss();
		oss.setUrl(path);
		return oss;
	}

}
