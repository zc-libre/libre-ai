package org.libre.ai.modules.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.libre.ai.modules.rag.entity.AigcConversation;
import org.libre.ai.modules.rag.entity.AigcMessage;
import org.libre.ai.modules.rag.mapper.AigcConversationMapper;
import org.libre.ai.modules.rag.mapper.AigcMessageMapper;
import org.libre.ai.modules.rag.service.AigcMessageService;
import org.libre.ai.modules.rag.utils.QueryPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AigcMessageServiceImpl extends ServiceImpl<AigcMessageMapper, AigcMessage> implements AigcMessageService {

	private final AigcConversationMapper aigcConversationMapper;

	// private final SysUserMapper userMapper;

	@Override
	public List<AigcConversation> conversations(String userId) {
		return aigcConversationMapper.selectList(Wrappers.<AigcConversation>lambdaQuery()
			.eq(AigcConversation::getUserId, userId)
			.orderByDesc(AigcConversation::getCreateTime));
	}

	@Override
	public IPage<AigcConversation> conversationPages(AigcConversation data, QueryPage queryPage) {
		Page<AigcConversation> page = new Page<>(queryPage.getPage(), queryPage.getLimit());
		Page<AigcConversation> iPage = aigcConversationMapper.selectPage(page,
				Wrappers.<AigcConversation>lambdaQuery()
					.like(!StrUtil.isBlank(data.getTitle()), AigcConversation::getTitle, data.getTitle())
					.orderByDesc(AigcConversation::getCreateTime));

		if (!iPage.getRecords().isEmpty()) {
			// Map<String, List<SysUser>> map =
			// userMapper.selectList(Wrappers.lambdaQuery()).stream().collect(Collectors.groupingBy(SysUser::getId));
			Set<String> ids = iPage.getRecords().stream().map(AigcConversation::getId).collect(Collectors.toSet());
			List<AigcMessage> messages = baseMapper.selectList(Wrappers.<AigcMessage>lambdaQuery()
				.in(AigcMessage::getConversationId, ids)
				.orderByDesc(AigcMessage::getCreateTime));

			iPage.getRecords().forEach(i -> {
				// List<SysUser> list = map.get(i.getUserId());
				// if (list != null && !list.isEmpty()) {
				// i.setUsername(list.get(0).getUsername());
				// }

				List<AigcMessage> messageList = messages.stream()
					.filter(m -> m.getConversationId() != null && m.getConversationId().equals(i.getId()))
					.toList();
				if (!messageList.isEmpty()) {
					i.setChatTotal(messageList.size());
					i.setEndTime(messageList.get(0).getCreateTime());
					i.setTokenUsed(messageList.stream()
						.filter(m -> m.getTokens() != null)
						.mapToInt(AigcMessage::getTokens)
						.sum());
				}
			});
		}
		return iPage;
	}

	@Override
	@Transactional
	public AigcConversation addConversation(AigcConversation conversation) {
		conversation.setCreateTime(new Date());
		aigcConversationMapper.insert(conversation);
		return conversation;
	}

	@Override
	@Transactional
	public void updateConversation(AigcConversation conversation) {
		aigcConversationMapper
			.updateById(new AigcConversation().setId(conversation.getId()).setTitle(conversation.getTitle()));
	}

	@Override
	@Transactional
	public void delConversation(String conversationId) {
		aigcConversationMapper.deleteById(conversationId);
		baseMapper.delete(Wrappers.<AigcMessage>lambdaQuery().eq(AigcMessage::getConversationId, conversationId));
	}

	@Override
	@Transactional
	public AigcMessage addMessage(AigcMessage message) {
		message.setCreateTime(new Date());
		baseMapper.insert(message);
		return message;
	}

	@Override
	@Transactional
	public void clearMessage(String conversationId) {
		baseMapper.delete(Wrappers.<AigcMessage>lambdaQuery().eq(AigcMessage::getConversationId, conversationId));
	}

	@Override
	public List<AigcMessage> getMessages(String conversationId) {
		// 避免页面渲染压力大，只截取最新的20条数据
		return baseMapper
			.selectPage(new Page<>(0, 20),
					Wrappers.<AigcMessage>lambdaQuery()
						.eq(AigcMessage::getConversationId, conversationId)
						.orderByAsc(AigcMessage::getCreateTime))
			.getRecords();
	}

	@Override
	public List<AigcMessage> getMessages(String conversationId, String userId) {
		// 避免页面渲染压力大，只截取最新的100条数据
		return baseMapper
			.selectPage(new Page<>(0, 100),
					Wrappers.<AigcMessage>lambdaQuery()
						.eq(AigcMessage::getConversationId, conversationId)
						.eq(AigcMessage::getUserId, userId)
						.orderByAsc(AigcMessage::getCreateTime))
			.getRecords();
	}

}
