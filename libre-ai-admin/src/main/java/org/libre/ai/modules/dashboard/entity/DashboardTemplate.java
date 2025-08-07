package org.libre.ai.modules.dashboard.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 仪表板模板配置实体
 * <p>
 * 遵循KISS原则：简单的模板配置存储 遵循SRP原则：专注于模板数据管理
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@TableName("dashboard_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTemplate {

	/**
	 * 模板类型 (purposes/layouts/themes/components)
	 */
	@TableId(type = IdType.INPUT)
	private String type;

	/**
	 * 模板数据JSON 使用JSONB存储模板配置数据
	 */
	@TableField("data_json")
	private String dataJson;

	/**
	 * 模板版本
	 */
	@TableField("version")
	private String version = "1.0";

	/**
	 * 是否启用
	 */
	@TableField("enabled")
	private Boolean enabled = true;

	/**
	 * 创建时间
	 */
	@TableField(value = "created_at", fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	/**
	 * 更新时间
	 */
	@TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

}