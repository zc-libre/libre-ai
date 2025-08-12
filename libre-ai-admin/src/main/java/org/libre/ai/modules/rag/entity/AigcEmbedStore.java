package org.libre.ai.modules.rag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author tycoding
 * @since 2024/10/28
 */
@Data
@TableName("aigc_embed_store")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AigcEmbedStore implements Serializable {

	private static final long serialVersionUID = 548724967827903685L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 数据库别名
	 */
	private String name;

	/**
	 * 提供商类型
	 */
	private String provider;

	/**
	 * 服务器地址
	 */
	private String host;

	/**
	 * 端口号
	 */
	private Integer port;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 数据库名
	 */
	private String databaseName;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 向量维度
	 */
	private Integer dimension;

	/**
	 * 是否启用
	 */
	private Boolean isPerms;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
