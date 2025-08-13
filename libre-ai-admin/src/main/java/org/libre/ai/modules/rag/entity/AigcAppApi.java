
package org.libre.ai.modules.rag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author tycoding
 * @since 2024/7/26
 */
@Data
@Accessors(chain = true)
public class AigcAppApi implements Serializable {

	@Serial
	private static final long serialVersionUID = -94917153262781949L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	private String appId;

	private String apiKey;

	private String channel;

	private LocalDateTime createTime;

	@TableField(exist = false)
	private AigcApp app;

}
