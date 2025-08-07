package org.libre.ai.modules.dashboard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.dto.DashboardTemplates;
import org.libre.ai.modules.dashboard.dto.GenerationResult;
import org.libre.ai.modules.dashboard.service.IDashboardService;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.web.bind.annotation.*;

/**
 * 仪表板生成器核心API控制器
 * 
 * @author AI Assistant
 * @since 2025-01-15
 * @version 1.1 拆分历史记录管理到单独控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

	private final IDashboardService dashboardService;

	/**
	 * AI生成仪表板代码
	 *
	 * 核心功能接口：根据用户配置生成完整的仪表板代码
	 * @param request 生成请求参数
	 * @return 生成结果
	 */
	@PostMapping("/generate")
	public R<GenerationResult> generate(@RequestBody @Valid DashboardRequest request) {
		log.info("收到仪表板生成请求: {}", request);

		try {
			GenerationResult result = dashboardService.generateDashboard(request);
			log.info("仪表板生成成功，耗时: {}秒", result.getMetadata().getGenerationTime());
			return R.ok(result);
		}
		catch (Exception e) {
			log.error("仪表板生成失败", e);
			return R.fail("生成失败: " + e.getMessage());
		}
	}

	/**
	 * 获取模板配置数据
	 * <p>
	 * 前端初始化时获取所有配置选项
	 * @return 模板配置集合
	 */
	@GetMapping("/templates")
	public R<DashboardTemplates> getTemplates() {
		log.info("获取模板配置数据");

		try {
			DashboardTemplates templates = dashboardService.getTemplates();
			return R.ok(templates);

		}
		catch (Exception e) {
			log.error("获取模板配置失败", e);
			return R.fail("获取模板配置失败: " + e.getMessage());
		}
	}

}