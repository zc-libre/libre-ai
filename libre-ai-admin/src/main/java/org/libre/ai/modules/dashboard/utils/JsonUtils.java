package org.libre.ai.modules.dashboard.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON处理工具类
 *
 * 遵循 DRY（Don't Repeat Yourself）原则：统一的JSON处理逻辑 遵循 KISS（Keep It Simple, Stupid）原则：简单直接的工具方法
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Slf4j
public class JsonUtils {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * 对象转JSON字符串
	 * @param object 要转换的对象
	 * @return JSON字符串
	 */
	public static String toJson(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		}
		catch (Exception e) {
			log.error("对象转JSON失败: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * JSON字符串转对象
	 * @param json JSON字符串
	 * @param clazz 目标类型
	 * @param <T> 泛型
	 * @return 转换后的对象
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		if (json == null || json.trim().isEmpty()) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(json, clazz);
		}
		catch (Exception e) {
			log.error("JSON转对象失败: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * JSON字符串转复杂类型对象
	 * @param json JSON字符串
	 * @param typeReference 类型引用
	 * @param <T> 泛型
	 * @return 转换后的对象
	 */
	public static <T> T fromJson(String json, TypeReference<T> typeReference) {
		if (json == null || json.trim().isEmpty()) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readValue(json, typeReference);
		}
		catch (Exception e) {
			log.error("JSON转复杂类型失败: {}", e.getMessage());
			throw new RuntimeException("JSON解析失败", e);
		}
	}

	/**
	 * 安全地从JSON字符串提取特定字段
	 * @param json JSON字符串
	 * @param field 字段名
	 * @return 字段值
	 */
	public static String extractField(String json, String field) {
		try {
			var node = OBJECT_MAPPER.readTree(json);
			var fieldNode = node.get(field);
			return fieldNode != null ? fieldNode.asText() : null;
		}
		catch (Exception e) {
			log.error("提取JSON字段失败: {}", e.getMessage());
			return null;
		}
	}

}