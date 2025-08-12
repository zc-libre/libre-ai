package org.libre.ai.modules.rag.endpoint;

import cn.hutool.core.util.StrUtil;
import dev.langchain4j.data.message.SystemMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.core.service.impl.PersistentChatMemoryStore;
import org.libre.ai.modules.rag.dto.ChatRequest;
import org.libre.ai.modules.rag.dto.ChatResponse;
import org.libre.ai.modules.rag.dto.ImageRequest;
import org.libre.ai.modules.rag.dto.PromptConstant;
import org.libre.ai.modules.rag.entity.AigcApp;
import org.libre.ai.modules.rag.entity.AigcModel;
import org.libre.ai.modules.rag.service.AigcAppService;
import org.libre.ai.modules.rag.service.AigcMessageService;
import org.libre.ai.modules.rag.service.AigcModelService;
import org.libre.ai.modules.rag.service.ChatService;
import org.libre.ai.modules.rag.utils.PromptUtil;
import org.libre.ai.modules.rag.utils.R;
import org.libre.ai.modules.rag.utils.StreamEmitter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tycoding
 * @since 2024/1/30
 */
@Slf4j
@RequestMapping("/api/aigc")
@RestController
@AllArgsConstructor
public class ChatEndpoint {

	private final ChatService chatService;

	private final AigcMessageService messageService;

	private final AigcModelService aigcModelService;

	private final AigcAppService appService;

	// private final ChatProps chatProps;

	@PostMapping("/chat/completions")
	public SseEmitter chat(@RequestBody ChatRequest req) {
		StreamEmitter emitter = new StreamEmitter();
		req.setEmitter(emitter);
		// req.setUserId(AuthUtil.getUserId());
		// req.setUsername(AuthUtil.getUsername());
		ExecutorService executor = Executors.newSingleThreadExecutor();
		req.setExecutor(executor);
		return emitter.streaming(executor, () -> {
			chatService.chat(req);
		});
	}

	@GetMapping("/app/info")
	public R<AigcApp> appInfo(@RequestParam String appId, String conversationId) {
		AigcApp app = appService.getById(appId);
		if (StrUtil.isBlank(conversationId)) {
			conversationId = app.getId();
		}

		// 填充模型信息
		if (StrUtil.isNotBlank(app.getModelId())) {
			AigcModel model = aigcModelService.getById(app.getModelId());
			if (model != null) {
				// 清除敏感信息
				model.setApiKey(null);
				model.setSecretKey(null);
				app.setModel(model);
			}
		}

		if (StrUtil.isNotBlank(app.getPrompt())) {
			// initialize chat memory
			SystemMessage message = new SystemMessage(app.getPrompt());
			PersistentChatMemoryStore.init(conversationId, message);
		}

		return R.ok(app);
	}

	// @GetMapping("/chat/messages/{conversationId}")
	// public R messages(@PathVariable String conversationId) {
	// List<AigcMessage> list = messageService.getMessages(conversationId,
	// String.valueOf(AuthUtil.getUserId()));
	//
	// // initialize chat memory
	// List<ChatMessage> chatMessages = new ArrayList<>();
	// list.forEach(item -> {
	// if (chatMessages.size() >= chatProps.getMemoryMaxMessage()) {
	// return;
	// }
	// if (item.getRole().equals(RoleEnum.ASSISTANT.getName())) {
	// chatMessages.add(new AiMessage(item.getMessage()));
	// }
	// else {
	// chatMessages.add(new UserMessage(item.getMessage()));
	// }
	// });
	// PersistentChatMemoryStore.init(conversationId, chatMessages);
	// return R.ok(list);
	// }

	@DeleteMapping("/chat/messages/clean/{conversationId}")
	public R cleanMessage(@PathVariable String conversationId) {
		messageService.clearMessage(conversationId);

		// clean chat memory
		PersistentChatMemoryStore.clean(conversationId);
		return R.ok();
	}

	@PostMapping("/chat/mindmap")
	public R mindmap(@RequestBody ChatRequest req) {
		req.setPrompt(PromptUtil.build(req.getMessage(), PromptConstant.MINDMAP));
		return R.ok(new ChatResponse(chatService.text(req)));
	}

	@PostMapping("/chat/image")
	public R image(@RequestBody ImageRequest req) {
		req.setPrompt(PromptUtil.build(req.getMessage(), PromptConstant.IMAGE));
		return R.ok(chatService.image(req));
	}

	@GetMapping("/chat/getImageModels")
	public R<List<AigcModel>> getImageModels() {
		List<AigcModel> list = aigcModelService.getImageModels();
		list.forEach(i -> {
			i.setApiKey(null);
			i.setSecretKey(null);
		});
		return R.ok(list);
	}

}
