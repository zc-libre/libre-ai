package org.libre.ai.modules.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.libre.ai.modules.dashboard.entity.DashboardHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 仪表板历史记录数据访问层
 * <p>
 * 遵循SOLID原则中的接口隔离原则：提供专门的查询方法 充分利用PostgreSQL JSONB特性和MyBatis-Plus便利功能
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Mapper
public interface DashboardHistoryMapper extends BaseMapper<DashboardHistory> {

	/**
	 * 根据JSON条件查询历史记录 (PostgreSQL JSONB查询)
	 *
	 * 使用PostgreSQL的JSONB操作符进行高效查询
	 * @param userId 用户ID
	 * @param jsonCondition JSON查询条件
	 * @return 历史记录列表
	 */
	@Select("SELECT * FROM dashboard_history WHERE user_id = #{userId} "
			+ "AND config_json @> #{jsonCondition}::jsonb ORDER BY created_at DESC")
	List<DashboardHistory> selectByJsonCondition(@Param("userId") String userId,
			@Param("jsonCondition") String jsonCondition);

	/**
	 * 统计用户最近历史记录数量
	 * @param userId 用户ID
	 * @param startDate 开始日期
	 * @return 记录数量
	 */
	@Select("SELECT COUNT(*) FROM dashboard_history WHERE user_id = #{userId} " + "AND created_at >= #{startDate}")
	Long countRecentHistory(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate);

	/**
	 * 根据用途统计历史记录
	 *
	 * 利用PostgreSQL JSONB的->>操作符提取JSON字段值进行聚合统计
	 * @param userId 用户ID
	 * @return 统计结果 Map<用途, 数量>
	 */
	@Select("SELECT config_json->>'purpose' as purpose, COUNT(*) as count "
			+ "FROM dashboard_history WHERE user_id = #{userId} " + "GROUP BY config_json->>'purpose'")
	List<Map<String, Object>> countByPurpose(@Param("userId") String userId);

	/**
	 * 根据主题统计历史记录
	 * @param userId 用户ID
	 * @return 统计结果
	 */
	@Select("SELECT config_json->>'theme' as theme, COUNT(*) as count "
			+ "FROM dashboard_history WHERE user_id = #{userId} " + "GROUP BY config_json->>'theme'")
	List<Map<String, Object>> countByTheme(@Param("userId") String userId);

}