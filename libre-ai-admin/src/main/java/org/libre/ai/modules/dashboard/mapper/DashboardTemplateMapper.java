package org.libre.ai.modules.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.libre.ai.modules.dashboard.entity.DashboardTemplate;

import java.util.List;

/**
 * 仪表板模板配置数据访问层
 * <p>
 * 遵循KISS原则：简单的模板数据查询接口
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Mapper
public interface DashboardTemplateMapper extends BaseMapper<DashboardTemplate> {

	/**
	 * 根据JSON条件查询模板 (PostgreSQL JSONB查询)
	 * @param jsonCondition JSON查询条件
	 * @return 模板列表
	 */
	@Select("SELECT * FROM dashboard_templates WHERE data_json @> #{jsonCondition}::jsonb")
	List<DashboardTemplate> selectByJsonCondition(@Param("jsonCondition") String jsonCondition);

}