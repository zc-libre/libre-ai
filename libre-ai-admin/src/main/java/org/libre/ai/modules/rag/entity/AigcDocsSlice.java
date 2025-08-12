package org.libre.ai.modules.rag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@Data
@Accessors(chain = true)
public class AigcDocsSlice implements Serializable {

	private static final long serialVersionUID = -3093489071059867065L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 向量库的ID
	 */
	private String vectorId;

	/**
	 * 文档ID
	 */
	private String docsId;

	/**
	 * 知识库ID
	 */
	private String knowledgeId;

	/**
	 * 文档名称
	 */
	private String name;

	/**
	 * 切片内容
	 */
	private String content;

	/**
	 * 字符数量
	 */
	private Integer wordNum;

	/**
	 * Embedding状态 (0: 未处理, 1: 已完成)
	 */
	private Integer status = 0;

	/**
	 * 创建时间
	 */
	private Date createTime;

}
