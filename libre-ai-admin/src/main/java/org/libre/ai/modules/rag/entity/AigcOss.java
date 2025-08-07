package org.libre.ai.modules.rag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author tycoding
 * @since 2024/1/6
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class AigcOss extends OssR {

	private static final long serialVersionUID = -250127374910520163L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_UUID)
	private String id;

	/**
	 * 用户ID
	 */
	private String userId;

}
