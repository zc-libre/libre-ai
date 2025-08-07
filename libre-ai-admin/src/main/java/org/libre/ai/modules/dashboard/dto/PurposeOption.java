package org.libre.ai.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用途选项配置
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurposeOption {

	private String id;

	private String title;

	private String description;

	private String icon;

	private List<String> components;

}