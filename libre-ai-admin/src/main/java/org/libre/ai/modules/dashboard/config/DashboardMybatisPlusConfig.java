package org.libre.ai.modules.dashboard.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Dashboard模块 MyBatis-Plus配置
 *
 * 遵循KISS原则：简单的配置，自动处理通用字段 遵循DRY原则：统一的字段填充逻辑
 *
 * @author AI Assistant
 * @since 2025-01-15
 */
@Configuration
public class DashboardMybatisPlusConfig {

	/**
	 * 分页插件配置
	 * @return MybatisPlusInterceptor
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 添加分页插件
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
		return interceptor;
	}

	/**
	 * 自动填充字段处理器
	 *
	 * 体现DRY原则：自动填充创建时间和更新时间
	 * @return MetaObjectHandler
	 */
	@Bean
	public MetaObjectHandler dashboardMetaObjectHandler() {
		return new MetaObjectHandler() {
			@Override
			public void insertFill(MetaObject metaObject) {
				// 插入时自动填充创建时间和更新时间
				this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
				this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
			}

			@Override
			public void updateFill(MetaObject metaObject) {
				// 更新时自动填充更新时间
				this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
			}
		};
	}

}