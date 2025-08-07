package org.libre.ai.modules.dashboard.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.entity.DashboardHistory;
import org.libre.ai.modules.dashboard.service.IDashboardService;
import org.libre.ai.modules.rag.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 仪表板历史记录控制器
 *
 * 遵循 SRP（单一职责原则）：专注于历史记录管理 遵循 RESTful 设计规范
 *
 * @author AI Assistant
 * @since 2025-01-16
 */
@RestController
@RequestMapping("/api/dashboard/history")
@Slf4j
public class DashboardHistoryController {

	@Autowired
	private IDashboardService dashboardService;

	/**
	 * 获取用户历史记录（分页）
	 */
	@GetMapping("/{userId}")
	public R<IPage<DashboardHistory>> getHistory(@PathVariable String userId,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("获取用户历史记录: userId={}, page={}, size={}", userId, page, size);

		try {
			IPage<DashboardHistory> result = dashboardService.getHistory(userId, page, size);
			return R.ok(result);
		}
		catch (Exception e) {
			log.error("获取历史记录失败", e);
			return R.fail("获取历史记录失败: " + e.getMessage());
		}
	}

	/**
	 * 保存历史记录
	 */
	@PostMapping
	public R<String> saveHistory(@RequestBody @Valid DashboardHistory history) {
		log.info("保存历史记录: userId={}", history.getUserId());

		try {
			dashboardService.saveHistory(history);
			return R.ok("保存成功");
		}
		catch (Exception e) {
			log.error("保存历史记录失败", e);
			return R.fail("保存失败: " + e.getMessage());
		}
	}

	/**
	 * 根据用途查询历史记录
	 */
	@GetMapping("/{userId}/purpose/{purpose}")
	public R<List<DashboardHistory>> getHistoryByPurpose(@PathVariable String userId, @PathVariable String purpose) {

		log.info("按用途查询历史记录: userId={}, purpose={}", userId, purpose);

		try {
			List<DashboardHistory> result = dashboardService.getHistoryByPurpose(userId, purpose);
			return R.ok(result);
		}
		catch (Exception e) {
			log.error("按用途查询历史记录失败", e);
			return R.fail("查询失败: " + e.getMessage());
		}
	}

	/**
	 * 批量删除历史记录
	 */
	@DeleteMapping("/batch")
	public R<String> batchDeleteHistory(@RequestBody List<String> ids) {
		log.info("批量删除历史记录: 数量={}", ids != null ? ids.size() : 0);

		try {
			int deletedCount = dashboardService.batchDeleteHistory(ids);
			return R.ok("成功删除 " + deletedCount + " 条记录");
		}
		catch (Exception e) {
			log.error("批量删除历史记录失败", e);
			return R.fail("删除失败: " + e.getMessage());
		}
	}

	/**
	 * 获取用户统计数据
	 */
	@GetMapping("/{userId}/statistics")
	public R<Map<String, Object>> getUserStatistics(@PathVariable String userId) {
		log.info("获取用户统计数据: userId={}", userId);

		try {
			Map<String, Object> statistics = dashboardService.getUserStatistics(userId);
			return R.ok(statistics);
		}
		catch (Exception e) {
			log.error("获取用户统计失败", e);
			return R.fail("获取统计失败: " + e.getMessage());
		}
	}

}