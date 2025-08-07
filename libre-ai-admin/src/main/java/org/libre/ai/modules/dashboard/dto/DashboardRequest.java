package org.libre.ai.modules.dashboard.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.libre.ai.modules.dashboard.enums.DashboardLayout;
import org.libre.ai.modules.dashboard.enums.DashboardPurpose;
import org.libre.ai.modules.dashboard.enums.DashboardTheme;

import java.util.List;

/**
 * 仪表板生成请求对象
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRequest {

	/**
	 * 仪表板用途
	 */
	@NotBlank(message = "用途不能为空")
	private String purpose;

	/**
	 * 布局样式
	 */
	@NotBlank(message = "布局样式不能为空")
	private String layout;

	/**
	 * 主题配色
	 */
	@NotBlank(message = "主题配色不能为空")
	private String theme;

	/**
	 * 组件列表
	 */
	@NotEmpty(message = "组件列表不能为空")
	@Size(min = 1, max = 10, message = "组件数量必须在1-10个之间")
	private List<@NotBlank String> components;

	/**
	 * 生成选项
	 */
	@Valid
	private GenerationOptions options;

	/**
	 * 获取用途显示文本 体现OCP原则：使用枚举替代硬编码
	 */
	public String getPurposeText() {
		DashboardPurpose purposeEnum = DashboardPurpose.fromCode(purpose);
		return purposeEnum != null ? purposeEnum.getName() : purpose;
	}

	/**
	 * 获取布局显示文本
	 */
	public String getLayoutText() {
		DashboardLayout layoutEnum = DashboardLayout.fromCode(layout);
		return layoutEnum != null ? layoutEnum.getName() : layout;
	}

	/**
	 * 获取主题显示文本
	 */
	public String getThemeText() {
		DashboardTheme themeEnum = DashboardTheme.fromCode(theme);
		return themeEnum != null ? themeEnum.getName() : theme;
	}

}