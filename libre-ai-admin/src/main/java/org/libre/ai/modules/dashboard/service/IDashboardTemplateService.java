package org.libre.ai.modules.dashboard.service;

import org.libre.ai.modules.dashboard.dto.DashboardTemplates;
import org.libre.ai.modules.dashboard.entity.DashboardTemplate;

/**
 * 仪表板模板配置服务接口
 *
 * 遵循 SRP（单一职责原则）：专注于模板管理 遵循 ISP（接口隔离原则）：最小化接口定义
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
public interface IDashboardTemplateService {

	/**
	 * 获取所有模板配置
	 * @return 模板配置集合
	 */
	DashboardTemplates getTemplates();

	/**
	 * 根据类型获取模板
	 * @param type 模板类型
	 * @return 模板实体
	 */
	DashboardTemplate getTemplateByType(String type);

	/**
	 * 更新模板
	 * @param template 模板实体
	 * @return 是否成功
	 */
	boolean updateTemplate(DashboardTemplate template);

}