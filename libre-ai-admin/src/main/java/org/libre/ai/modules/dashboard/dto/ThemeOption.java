package org.libre.ai.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主题选项配置
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeOption {

	private String id;

	private String name;

	private String description;

	private String primary;

	private String secondary;

	private String accent;

	private String background;

	private String text;

}