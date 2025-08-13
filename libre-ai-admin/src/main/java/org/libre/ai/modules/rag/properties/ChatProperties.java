package org.libre.ai.modules.rag.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tycoding
 * @since 2024/8/21
 */
@Data
@ConfigurationProperties("langchat.chat")
public class ChatProperties {

	/**
	 * 上下文的长度
	 */
	private Integer memoryMaxMessage = 20;

	/**
	 * 前端渲染的消息长度（过长会导致页面渲染卡顿）
	 */
	private Integer previewMaxMessage = 100;

}
