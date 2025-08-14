package org.libre.ai.modules.dashboard.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.libre.ai.modules.dashboard.enums.CodeType;
import org.libre.ai.modules.dashboard.enums.DashboardLayout;
import org.libre.ai.modules.dashboard.enums.DashboardPurpose;

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
	 * 场景细节描述（如：冷链货架、危险品仓位、高值物料等）
	 */
	@Size(max = 100, message = "场景细节描述不能超过100字符")
	private String purposeDetail;

	/**
	 * 重点监控指标（如：温度湿度、库存周转率、拣选效率等）
	 */
	@Size(max = 100, message = "重点监控指标不能超过100字符")
	private String focusMetrics;

	/**
	 * 用户补充需求（更详细的定制化需求描述）
	 */
	@Size(max = 200, message = "补充需求描述不能超过200字符")
	private String customRequirements;

	/**
	 * 布局样式
	 */
	@NotBlank(message = "布局样式不能为空")
	private String layout;

	/**
	 * 主题配置
	 */
	@NotNull(message = "主题配置不能为空")
	@Valid
	private ThemeConfig theme;

	/**
	 * 组件列表（兼容旧版本）
	 */
	@NotEmpty(message = "组件列表不能为空")
	@Size(min = 1, max = 10, message = "组件数量必须在1-10个之间")
	private List<@NotBlank String> components;

	/**
	 * 组件配置列表（包含数据结构配置）
	 */
	@Valid
	private List<ComponentConfig> componentConfigs;

	/**
	 * 生成选项
	 */
	@Valid
	private GenerationOptions options;

	/**
	 * 代码类型（HTML/Vue）
	 */
	@Builder.Default
	private String codeType = CodeType.HTML.getCode();

	/**
	 * 获取布局显示文本
	 */
	public String getLayoutText() {
		DashboardLayout layoutEnum = DashboardLayout.fromCode(layout);
		return layoutEnum != null ? layoutEnum.getName() : layout;
	}

	/**
	 * 获取代码类型枚举
	 */
	public CodeType getCodeTypeEnum() {
		return CodeType.fromCode(codeType);
	}

}