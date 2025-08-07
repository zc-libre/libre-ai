package org.libre.ai.modules.rag.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class AigcMessage implements Serializable {

	@Serial
	private static final long serialVersionUID = -19545329638997333L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_UUID)
	private String id;

	/**
	 * 消息ID
	 */
	private String chatId;

	/**
	 * 会话ID
	 */
	private String conversationId;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 请求IP
	 */
	private String ip;

	private Integer tokens;

	private Integer promptTokens;

	/**
	 * 角色，user、assistant
	 */
	private String role;

	/**
	 * 消息内容
	 */
	private String model;

	/**
	 * 消息内容
	 */
	private String message;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Date createTime;

}
