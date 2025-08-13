package org.libre.ai.modules.rag.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.rag.entity.AigcConversation;
import org.libre.ai.modules.rag.entity.AigcMessage;
import org.libre.ai.modules.rag.service.AigcMessageService;
import org.libre.ai.modules.rag.utils.MybatisUtil;
import org.libre.ai.modules.rag.utils.QueryPage;
import org.libre.ai.modules.rag.utils.R;
import org.libre.ai.modules.rag.utils.ServletUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 * @since 2024/1/30
 */
@Slf4j
@RestController
@RequestMapping("/api/aigc/conversation")
@AllArgsConstructor
public class AigcConversationController {

	private final AigcMessageService aigcMessageService;

	/**
	 * conversation list, filter by user
	 */
	@GetMapping("/list")
	public R conversations() {
		return R.ok(aigcMessageService.conversations("admin"));
	}

	/**
	 * conversation page
	 */
	@GetMapping("/page")
	public R list(AigcConversation data, QueryPage queryPage) {
		return R.ok(MybatisUtil.getData(aigcMessageService.conversationPages(data, queryPage)));
	}

	@PostMapping
	public R addConversation(@RequestBody AigcConversation conversation) {
		conversation.setUserId("admin");
		return R.ok(aigcMessageService.addConversation(conversation));
	}

	@PutMapping
	public R updateConversation(@RequestBody AigcConversation conversation) {
		if (conversation.getId() == null) {
			return R.fail("conversation id is null");
		}
		aigcMessageService.updateConversation(conversation);
		return R.ok();
	}

	@DeleteMapping("/{conversationId}")
	public R delConversation(@PathVariable String conversationId) {
		aigcMessageService.delConversation(conversationId);
		return R.ok();
	}

	@DeleteMapping("/message/{conversationId}")
	public R clearMessage(@PathVariable String conversationId) {
		aigcMessageService.clearMessage(conversationId);
		return R.ok();
	}

	/**
	 * get messages with conversationId
	 */
	@GetMapping("/messages/{conversationId}")
	public R getMessages(@PathVariable String conversationId) {
		List<AigcMessage> list = aigcMessageService.getMessages(conversationId);
		return R.ok(list);
	}

	/**
	 * add message in conversation
	 */
	@PostMapping("/message")
	public R addMessage(@RequestBody AigcMessage message) {
		message.setIp(ServletUtil.getIpAddr());
		return R.ok(aigcMessageService.addMessage(message));
	}

}
