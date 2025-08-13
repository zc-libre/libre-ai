package org.libre.ai.modules.dashboard.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.dto.DashboardTemplates;
import org.libre.ai.modules.dashboard.dto.GenerationResult;
import org.libre.ai.modules.dashboard.entity.DashboardHistory;
import org.libre.ai.modules.dashboard.mapper.DashboardHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 仪表板核心业务服务
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class DashboardService implements IDashboardService {

	private final IDashboardTemplateService templateService;

	private final DashboardHistoryMapper historyMapper;

	/**
	 * 保存历史记录
	 *
	 * 体现数据持久化：结构化数据存储
	 * @param history 历史记录对象
	 */
	@Override
	public void saveHistory(DashboardHistory history) {
		try {
			// 设置基本信息
			if (history.getId() == null) {
				history.setId(UUID.randomUUID().toString());
			}
			history.setCreatedAt(LocalDateTime.now());
			history.setUpdatedAt(LocalDateTime.now());

			// 保存到数据库
			historyMapper.insert(history);

			log.info("历史记录保存成功，ID: {}", history.getId());

		}
		catch (Exception e) {
			log.error("保存历史记录失败", e);
			throw new RuntimeException("保存历史记录失败: " + e.getMessage());
		}
	}

	/**
	 * 获取用户历史记录
	 *
	 * 体现分页查询：高效的数据检索
	 * @param userId 用户ID
	 * @param page 页码（从1开始）
	 * @param size 每页大小
	 * @return 分页结果
	 */
	@Override
	@Transactional(readOnly = true)
	public IPage<DashboardHistory> getHistory(String userId, int page, int size) {
		try {
			// 创建分页参数
			Page<DashboardHistory> pageParam = new Page<>(page, size);

			// 构建查询条件
			QueryWrapper<DashboardHistory> wrapper = new QueryWrapper<>();
			wrapper.eq("user_id", userId).orderByDesc("created_at");

			// 执行分页查询
			IPage<DashboardHistory> result = historyMapper.selectPage(pageParam, wrapper);

			log.info("获取用户历史记录成功，用户: {}, 总数: {}", userId, result.getTotal());

			return result;

		}
		catch (Exception e) {
			log.error("获取历史记录失败", e);
			throw new RuntimeException("获取历史记录失败: " + e.getMessage());
		}
	}

	/**
	 * 获取模板配置数据
	 *
	 * 委托给专门的模板服务处理
	 * @return 模板配置集合
	 */
	@Override
	@Transactional(readOnly = true)
	public DashboardTemplates getTemplates() {
		return templateService.getTemplates();
	}

	/**
	 * 根据条件查询历史记录
	 *
	 * 利用PostgreSQL JSONB查询能力
	 * @param userId 用户ID
	 * @param purpose 用途条件
	 * @return 历史记录列表
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DashboardHistory> getHistoryByPurpose(String userId, String purpose) {
		try {
			String jsonCondition = String.format("{\"purpose\": \"%s\"}", purpose);
			return historyMapper.selectByJsonCondition(userId, jsonCondition);
		}
		catch (Exception e) {
			log.error("按用途查询历史记录失败", e);
			throw new RuntimeException("查询失败: " + e.getMessage());
		}
	}

	/**
	 * 获取用户统计数据
	 *
	 * 体现数据分析：统计用户使用情况
	 * @param userId 用户ID
	 * @return 统计结果
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getUserStatistics(String userId) {
		try {
			// 总记录数
			QueryWrapper<DashboardHistory> wrapper = new QueryWrapper<>();
			wrapper.eq("user_id", userId);
			Long totalCount = historyMapper.selectCount(wrapper);

			// 最近7天记录数
			LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
			Long recentCount = historyMapper.countRecentHistory(userId, sevenDaysAgo);

			// 按用途统计
			List<Map<String, Object>> purposeStats = historyMapper.countByPurpose(userId);

			// 按主题统计
			List<Map<String, Object>> themeStats = historyMapper.countByTheme(userId);

			return Map.of("totalCount", totalCount, "recentCount", recentCount, "purposeStats", purposeStats,
					"themeStats", themeStats);

		}
		catch (Exception e) {
			log.error("获取用户统计失败", e);
			throw new RuntimeException("获取统计失败: " + e.getMessage());
		}
	}

	/**
	 * 批量删除历史记录
	 * @param ids 记录ID列表
	 * @return 删除数量
	 */
	@Override
	public int batchDeleteHistory(List<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return 0;
		}

		try {
			int deleted = historyMapper.deleteByIds(ids);
			log.info("批量删除历史记录成功，删除数量: {}", deleted);
			return deleted;
		}
		catch (Exception e) {
			log.error("批量删除历史记录失败", e);
			throw new RuntimeException("删除失败: " + e.getMessage());
		}
	}

}