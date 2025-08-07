package org.libre.ai.modules.rag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class AigcConversation implements Serializable {

	private static final long serialVersionUID = -19545329638997333L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 提示词ID
	 */
	private String promptId;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 会话标题
	 */
	private String title;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 用户名
	 */
	@TableField(exist = false)
	private String username;

	/**
	 * 对话次数
	 */
	@TableField(exist = false)
	private Integer chatTotal;

	/**
	 * Token消耗量
	 */
	@TableField(exist = false)
	private Integer tokenUsed;

	/**
	 * 最后一次对话时间
	 */
	@TableField(exist = false)
	private Date endTime;

}
