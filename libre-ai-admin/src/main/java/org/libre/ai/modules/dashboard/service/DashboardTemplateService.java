package org.libre.ai.modules.dashboard.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libre.ai.modules.dashboard.dto.*;
import org.libre.ai.modules.dashboard.entity.DashboardTemplate;
import org.libre.ai.modules.dashboard.mapper.DashboardTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 仪表板模板配置服务
 *
 * 遵循SRP原则：专注于模板数据的管理和转换 遵循DRY原则：统一的JSON解析逻辑
 *
 * @author AI Assistant
 * @since 2025-01-15
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardTemplateService implements IDashboardTemplateService {

	private final DashboardTemplateMapper templateMapper;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 获取所有模板配置数据 体现KISS原则：简单直接的数据获取和转换
	 * @return 模板配置集合
	 */
	@Override
	public DashboardTemplates getTemplates() {
		try {
			// 获取所有启用的模板
			QueryWrapper<DashboardTemplate> wrapper = new QueryWrapper<>();
			wrapper.eq("enabled", true).orderByAsc("type");
			List<DashboardTemplate> templates = templateMapper.selectList(wrapper);

			DashboardTemplates.DashboardTemplatesBuilder builder = DashboardTemplates.builder();

			// 解析不同类型的模板数据
			for (DashboardTemplate template : templates) {
				switch (template.getType()) {
					case "purposes":
						builder.purposes(parseJson(template.getDataJson(), new TypeReference<>() {
						}));
						break;
					case "layouts":
						builder.layouts(parseJson(template.getDataJson(), new TypeReference<>() {
						}));
						break;
					case "themes":
						builder.themes(parseJson(template.getDataJson(), new TypeReference<>() {
						}));
						break;
					case "components":
						builder.components(parseJson(template.getDataJson(), new TypeReference<>() {
						}));
						break;
					default:
						log.warn("未知的模板类型: {}", template.getType());
				}
			}

			return builder.build();

		}
		catch (Exception e) {
			log.error("获取模板配置失败", e);
			throw new RuntimeException("获取模板配置失败: " + e.getMessage());
		}
	}

	/**
	 * 根据类型获取特定模板数据
	 * @param type 模板类型
	 * @return 模板实体
	 */
	@Override
	public DashboardTemplate getTemplateByType(String type) {
		QueryWrapper<DashboardTemplate> wrapper = new QueryWrapper<>();
		wrapper.eq("type", type).eq("enabled", true);
		return templateMapper.selectOne(wrapper);
	}

	/**
	 * 更新模板数据
	 * @param template 模板实体
	 * @return 是否成功
	 */
	@Override
	public boolean updateTemplate(DashboardTemplate template) {
		try {
			return templateMapper.updateById(template) > 0;
		}
		catch (Exception e) {
			log.error("更新模板数据失败: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * 统一的JSON解析方法 体现DRY原则：避免重复的解析逻辑
	 * @param json JSON字符串
	 * @param typeReference 类型引用
	 * @param <T> 泛型类型
	 * @return 解析后的对象
	 */
	private <T> T parseJson(String json, TypeReference<T> typeReference) {
		try {
			return objectMapper.readValue(json, typeReference);
		}
		catch (Exception e) {
			log.error("JSON解析失败: {}", e.getMessage());
			throw new RuntimeException("JSON解析失败", e);
		}
	}

}