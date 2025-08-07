package org.libre.ai.modules.dashboard.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.utils.JsonUtils;

import java.time.LocalDateTime;

/**
 * 仪表板历史记录实体
 *
 * 遵循KISS原则：使用JSONB字段存储复杂配置，避免多表关联 遵循SRP原则：单一职责，仅负责历史记录数据存储
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@TableName("dashboard_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardHistory {

	/**
	 * 记录ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 用户ID
	 */
	@TableField("user_id")
	private String userId;

	/**
	 * 仪表板配置JSON 使用JSONB类型存储复杂配置，体现KISS原则
	 */
	@TableField("config_json")
	private String configJson;

	/**
	 * 生成的HTML代码
	 */
	@TableField("generated_html")
	private String generatedHtml;

	/**
	 * 生成的CSS代码
	 */
	@TableField("generated_css")
	private String generatedCss;

	/**
	 * Base64预览图片
	 */
	@TableField("preview_image")
	private String previewImage;

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

	// 辅助字段：反序列化配置对象，不存储到数据库
	@TableField(exist = false)
	private DashboardRequest config;

	/**
	 * 获取配置对象 - 懒加载反序列化 体现DRY原则：统一的JSON处理逻辑
	 */
	public DashboardRequest getConfig() {
		if (config == null && configJson != null) {
			config = JsonUtils.fromJson(configJson, DashboardRequest.class);
		}
		return config;
	}

	/**
	 * 设置配置对象 - 自动序列化为JSON 体现DRY原则：统一的JSON处理逻辑
	 */
	public void setConfig(DashboardRequest config) {
		this.config = config;
		if (config != null) {
			this.configJson = JsonUtils.toJson(config);
		}
	}

}