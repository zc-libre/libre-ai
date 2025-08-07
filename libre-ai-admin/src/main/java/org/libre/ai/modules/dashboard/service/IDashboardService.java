package org.libre.ai.modules.dashboard.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.libre.ai.modules.dashboard.dto.DashboardRequest;
import org.libre.ai.modules.dashboard.dto.DashboardTemplates;
import org.libre.ai.modules.dashboard.dto.GenerationResult;
import org.libre.ai.modules.dashboard.entity.DashboardHistory;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * 仪表板核心业务服务接口
 *
 * 遵循 SOLID 原则中的 DIP（依赖倒置原则）：依赖抽象而非具体实现 遵循 ISP（接口隔离原则）：定义专门的业务接口
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
public interface IDashboardService {

	/**
	 * 生成仪表板代码
	 * @param request 生成请求
	 * @return 生成结果
	 */
	GenerationResult generateDashboard(DashboardRequest request);

	/**
	 * 流式生成仪表板代码
	 * @param request 生成请求
	 * @return Flux<String> 响应式流
	 */
	Flux<String> generateDashboardStream(DashboardRequest request);

	/**
	 * 保存历史记录
	 * @param history 历史记录对象
	 */
	void saveHistory(DashboardHistory history);

	/**
	 * 获取用户历史记录（分页）
	 * @param userId 用户ID
	 * @param page 页码
	 * @param size 每页大小
	 * @return 分页结果
	 */
	IPage<DashboardHistory> getHistory(String userId, int page, int size);

	/**
	 * 获取模板配置
	 * @return 模板配置集合
	 */
	DashboardTemplates getTemplates();

	/**
	 * 根据用途查询历史记录
	 * @param userId 用户ID
	 * @param purpose 用途
	 * @return 历史记录列表
	 */
	List<DashboardHistory> getHistoryByPurpose(String userId, String purpose);

	/**
	 * 获取用户统计数据
	 * @param userId 用户ID
	 * @return 统计结果
	 */
	Map<String, Object> getUserStatistics(String userId);

	/**
	 * 批量删除历史记录
	 * @param ids 记录ID列表
	 * @return 删除数量
	 */
	int batchDeleteHistory(List<String> ids);

}