package org.libre.ai.modules.rag.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.libre.ai.modules.rag.entity.AigcConversation;
import org.libre.ai.modules.rag.entity.AigcMessage;
import org.libre.ai.modules.rag.utils.QueryPage;

import java.util.List;

public interface AigcMessageService extends IService<AigcMessage> {

	/**
	 * 获取会话列表
	 */
	List<AigcConversation> conversations(String userId);

	/**
	 * 获取会话分页列表
	 */
	IPage<AigcConversation> conversationPages(AigcConversation data, QueryPage queryPage);

	/**
	 * 新增会话
	 */
	AigcConversation addConversation(AigcConversation conversation);

	/**
	 * 修改会话
	 */
	void updateConversation(AigcConversation conversation);

	/**
	 * 删除会话
	 */
	void delConversation(String conversationId);

	/**
	 * 获取会话详情
	 */
	AigcConversation getConversation(String conversationId);

	AigcMessage addMessage(AigcMessage message);

	void clearMessage(String conversationId);

	List<AigcMessage> getMessages(String conversationId);

	List<AigcMessage> getMessages(String conversationId, String userId);

}
