package org.libre.ai.modules.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI生成过程元数据
 *
 * 用于记录生成过程的详细信息，便于监控和优化
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationMetadata {

	/**
	 * 生成时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime generatedAt;

	/**
	 * 使用的AI模型
	 */
	private String modelUsed;

	/**
	 * 消耗的Token数量
	 */
	private Integer tokensUsed;

	/**
	 * 生成耗时（秒）
	 */
	private Double generationTime;

	/**
	 * 生成的代码行数
	 */
	private Integer linesOfCode;

	/**
	 * 代码文件大小
	 */
	private String fileSize;

}