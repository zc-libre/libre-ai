# Dashboard Generator 实施计划文档

## 项目概述

本文档详细规划 Dashboard Generator 后端功能的开发实施过程，遵循敏捷开发理念，分阶段递进式交付。

### 项目目标
- 实现基于AI的智能仪表板代码生成功能
- 提供完整的RESTful API接口
- 支持历史记录管理和模板配置
- 确保系统稳定性和可扩展性

### 技术栈
- **后端框架**: Spring Boot 2.7+
- **AI集成**: LangChain4j
- **数据库**: PostgreSQL 13+
- **构建工具**: Maven
- **Java版本**: JDK 11+

## 开发阶段规划

### Phase 1: Dashboard模块开发准备 (1天)

#### 目标
在现有项目基础上，创建Dashboard Generator模块并配置相关依赖

#### 任务清单
- [ ] 在现有模块结构中创建dashboard子模块
- [ ] 配置LangChain4j相关依赖
- [ ] 创建数据库表结构
- [ ] 配置MyBatis-Plus字段填充策略
- [ ] 创建统一响应格式(如果不存在)

#### 详细实施步骤

##### 1.1 模块结构创建
```bash
# 在现有rag模块同级创建dashboard模块
mkdir -p src/main/java/org/libre/ai/modules/dashboard/{controller,service,mapper,entity,dto,config}
```

##### 1.2 Maven依赖配置(在现有pom.xml中添加)
```xml
<!-- LangChain4j核心依赖 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>0.25.0</version>
</dependency>

<!-- OpenAI集成 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>0.25.0</version>
</dependency>
```

##### 1.3 数据库表创建
```sql
-- 在PostgreSQL中执行
CREATE TABLE dashboard_history (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    config_json JSONB NOT NULL,
    generated_html TEXT,
    preview_image TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dashboard_templates (
    type VARCHAR(32) NOT NULL PRIMARY KEY,
    data_json JSONB NOT NULL,
    version VARCHAR(16) DEFAULT '1.0',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_dashboard_history_user_created ON dashboard_history (user_id, created_at DESC);
CREATE INDEX idx_dashboard_history_config_gin ON dashboard_history USING GIN (config_json);
CREATE INDEX idx_dashboard_templates_data_gin ON dashboard_templates USING GIN (data_json);
```

##### 1.4 MyBatis-Plus字段填充配置
```java
@Configuration
public class DashboardMybatisPlusConfig {
    
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
```

#### 验收标准
- [ ] dashboard模块目录结构创建完成
- [ ] 数据库表创建成功，索引正常
- [ ] LangChain4j依赖添加成功
- [ ] MyBatis-Plus字段填充配置生效

---

### Phase 2: 核心API开发 (3-4天)

#### 目标
实现核心的仪表板生成API和基础数据操作

#### 任务清单
- [ ] 创建MyBatis-Plus实体类和DTO
- [ ] 实现Mapper接口(继承BaseMapper)
- [ ] 开发核心服务层
- [ ] 实现AI代码生成服务
- [ ] 创建REST API控制器
- [ ] 编写AI提示词模板

#### 详细实施步骤

##### 2.1 实体类和DTO创建
```java
// MyBatis-Plus实体类
@TableName("dashboard_history")
@Data
public class DashboardHistory {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("user_id")
    private String userId;
    // ... 其他字段
}

// DTO类
@Data
@Valid
public class DashboardRequest {
    @NotBlank
    private String purpose;
    // ... 其他字段
}
```

##### 2.2 MyBatis-Plus Mapper接口
```java
@Mapper
public interface DashboardHistoryMapper extends BaseMapper<DashboardHistory> {
    
    // 自定义分页查询
    IPage<DashboardHistory> selectHistoryByUserId(
        IPage<DashboardHistory> page, 
        @Param("userId") String userId
    );
    
    // 使用PostgreSQL JSONB查询
    @Select("SELECT * FROM dashboard_history WHERE config_json @> #{jsonCondition}::jsonb")
    List<DashboardHistory> selectByJsonCondition(@Param("jsonCondition") String jsonCondition);
}
```

##### 2.3 服务层实现
```java
@Service
@Slf4j
public class DashboardService {
    
    @Autowired
    private DashboardHistoryMapper historyMapper;
    
    @Autowired 
    private AIGenerationService aiGenerationService;
    
    public GenerationResult generateDashboard(DashboardRequest request) {
        // 1. 参数验证
        // 2. 调用AI生成
        // 3. 保存历史记录
        return aiGenerationService.generateCode(request);
    }
    
    public IPage<DashboardHistory> getHistory(String userId, int page, int size) {
        Page<DashboardHistory> pageParam = new Page<>(page, size);
        return historyMapper.selectHistoryByUserId(pageParam, userId);
    }
}
```

##### 2.4 LangChain4j AI服务
```java
@Service
public class AIGenerationService {
    
    @Autowired
    private ChatLanguageModel chatModel;
    
    public GenerationResult generateCode(DashboardRequest request) {
        // 构建AI提示词
        String prompt = buildPrompt(request);
        
        // 调用AI模型
        AiMessage response = chatModel.generate(prompt);
        
        // 解析响应并返回结果
        return parseResponse(response.text());
    }
}
```

##### 2.5 REST控制器
```java
@RestController
@RequestMapping("/api/dashboard")
@Slf4j
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @PostMapping("/generate")
    public R<GenerationResult> generate(@RequestBody @Valid DashboardRequest request) {
        return R.ok(dashboardService.generateDashboard(request));
    }
    
    @GetMapping("/history/{userId}")
    public R<IPage<DashboardHistory>> getHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(dashboardService.getHistory(userId, page, size));
    }
}
```

#### 验收标准
- [ ] POST /api/dashboard/generate 接口可用
- [ ] MyBatis-Plus CRUD操作正常
- [ ] AI能够成功生成基础HTML/CSS/JavaScript代码
- [ ] PostgreSQL JSONB查询功能正常
- [ ] 分页查询和字段填充工作正常

---

### Phase 3: 历史记录功能优化 (1-2天)

#### 目标
完善历史记录功能和实现高级查询

#### 任务清单  
- [ ] 实现基于JSONB的条件查询
- [ ] 添加历史记录统计功能
- [ ] 实现批量操作(批量删除等)
- [ ] 添加定时清理机制
- [ ] 优化分页查询性能

#### 详细实施步骤

##### 3.1 高级查询功能
```java
// 在DashboardHistoryMapper中添加
@Select("SELECT * FROM dashboard_history WHERE user_id = #{userId} " +
        "AND config_json @> #{purposeFilter}::jsonb ORDER BY created_at DESC")
List<DashboardHistory> selectByPurpose(
    @Param("userId") String userId, 
    @Param("purposeFilter") String purposeFilter
);

// 统计查询
@Select("SELECT COUNT(*) FROM dashboard_history WHERE user_id = #{userId} " +
        "AND created_at >= #{startDate}")
Long countRecentHistory(
    @Param("userId") String userId, 
    @Param("startDate") LocalDateTime startDate
);
```

##### 3.2 批量操作实现
```java
@Service
public class DashboardService {
    
    public void batchDeleteHistory(List<String> ids) {
        if (!ids.isEmpty()) {
            historyMapper.deleteBatchIds(ids);
        }
    }
    
    public Map<String, Object> getHistoryStatistics(String userId) {
        // 使用MyBatis-Plus QueryWrapper实现统计
        QueryWrapper<DashboardHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        
        long total = historyMapper.selectCount(wrapper);
        // ... 其他统计逻辑
        
        return statisticsResult;
    }
}
```

##### 3.3 定时清理任务
```java
@Component
@Slf4j
public class DashboardCleanupTask {
    
    @Autowired
    private DashboardHistoryMapper historyMapper;
    
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredRecords() {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(30);
        
        QueryWrapper<DashboardHistory> wrapper = new QueryWrapper<>();
        wrapper.lt("created_at", expireTime);
        
        int deleted = historyMapper.delete(wrapper);
        log.info("清理过期历史记录: {} 条", deleted);
    }
}
```

#### 验收标准
- [ ] JSONB条件查询功能正常
- [ ] 批量操作功能可用
- [ ] 统计功能返回正确数据
- [ ] 定时清理任务正常工作

---

### Phase 4: 模板配置功能 (2天)

#### 目标
实现仪表板模板配置的管理和获取功能

#### 任务清单
- [ ] 创建模板配置数据模型
- [ ] 实现模板数据初始化
- [ ] 开发模板获取接口
- [ ] 支持模板动态更新

#### 详细实施步骤

##### 4.1 模板配置实现
```java
@GetMapping("/templates")
public R<DashboardTemplates> getTemplates() {
    return R.ok(dashboardService.getTemplates());
}
```

##### 4.2 模板数据初始化
```sql
-- 插入默认模板数据
INSERT INTO dashboard_templates (type, data_json) VALUES
('purposes', '[...]'),
('layouts', '[...]'),
('themes', '[...]'),
('components', '[...]');
```

#### 验收标准
- [ ] 模板数据能够正常加载
- [ ] GET /api/dashboard/templates 接口返回完整配置
- [ ] 支持模板的动态更新

---

### Phase 5: AI优化和测试 (3-4天)

#### 目标
优化AI生成质量，完善系统测试和性能调优

#### 任务清单
- [ ] 优化AI提示词模板
- [ ] 实现代码质量检测
- [ ] 添加重试和容错机制
- [ ] 编写单元测试
- [ ] 编写集成测试
- [ ] 性能测试和优化

#### 详细实施步骤

##### 5.1 AI提示词优化
```java
@Bean
public PromptTemplate enhancedDashboardPromptTemplate() {
    return PromptTemplate.from("""
        # 角色定义
        你是一个资深的前端架构师，精通HTML5、CSS3、JavaScript和现代前端框架。
        
        # 任务要求
        根据以下用户配置，生成一个完整、专业、可用的仪表板页面：
        
        ## 用户配置
        - 业务场景: {{purpose}} ({{purposeText}})
        - 布局风格: {{layout}} ({{layoutText}})
        - 视觉主题: {{theme}} ({{themeText}})
        - 功能组件: {{components}}
        - 代码风格: {{codeStyle}}
        - 响应式要求: {{responsive}}
        - 包含数据: {{includeData}}
        {{#if additionalRequirements}}
        - 特殊需求: {{additionalRequirements}}
        {{/if}}
        
        # 技术要求
        1. 使用语义化HTML5标签
        2. CSS使用现代布局技术(Flexbox/Grid)
        3. JavaScript使用ES6+语法
        4. 确保代码结构清晰、注释完整
        5. 遵循Web标准和最佳实践
        6. 确保跨浏览器兼容性
        {{#if responsive}}
        7. 实现完整的响应式设计
        {{/if}}
        {{#if includeData}}
        8. 包含合理的示例数据
        {{/if}}
        
        # 输出格式
        请返回标准JSON格式，确保代码可以直接使用：
        ```json
        {
          "html": "完整的HTML代码，包含DOCTYPE和完整结构",
          "css": "完整的CSS样式代码，包含所有必要样式",
          "javascript": "完整的JavaScript代码，包含交互逻辑"
        }
        ```
        
        注意：
        - 确保生成的代码是完整可运行的
        - HTML中要包含对CSS和JS文件的正确引用
        - 代码要有良好的可读性和维护性
        - 组件要有合理的默认数据展示
        """);
}
```

##### 5.2 重试机制实现
```java
@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
public GenerationResult generateCodeWithRetry(DashboardRequest request) {
    // AI调用逻辑
}
```

##### 5.3 测试用例编写
```java
@SpringBootTest
class DashboardServiceTest {
    
    @Test
    void testGenerateDashboard() {
        // 测试用例
    }
    
    @Test
    void testSaveHistory() {
        // 测试用例
    }
}
```

#### 验收标准
- [ ] AI生成的代码质量达到可用标准
- [ ] 重试机制工作正常
- [ ] 单元测试覆盖率 > 80%
- [ ] 集成测试通过
- [ ] 性能满足要求(生成时间 < 30s)

---

### Phase 6: 部署和监控 (1-2天)

#### 目标
完成生产环境部署准备和监控配置

#### 任务清单
- [ ] 配置生产环境数据库
- [ ] 优化应用配置
- [ ] 添加健康检查接口
- [ ] 配置日志输出
- [ ] 编写部署文档

#### 详细实施步骤

##### 6.1 生产配置
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/libre_ai
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.PostgreSQLDialect
  
dashboard:
  openai:
    api-key: ${OPENAI_API_KEY}
    model-name: gpt-4
  generation:
    timeout: 30s
    max-retries: 3

logging:
  level:
    org.libre.ai.modules.dashboard: INFO
  file:
    name: logs/dashboard-generator.log
```

##### 6.2 健康检查
```java
@RestController
public class HealthController {
    
    @GetMapping("/health/dashboard")
    public R<Map<String, Object>> checkHealth() {
        // 健康检查逻辑
    }
}
```

#### 验收标准
- [ ] 生产环境配置完成
- [ ] 健康检查接口正常
- [ ] 日志配置合理
- [ ] 部署文档完整

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化配置
- 必要的类和方法添加Javadoc注释
- 遵循SOLID设计原则

### Git工作流
```bash
# 功能分支命名规范
feature/dashboard-generator-phase1
feature/dashboard-api-implementation  
feature/dashboard-history-management

# 提交信息规范
feat: 实现仪表板生成核心API
fix: 修复AI调用超时问题
docs: 更新API接口文档
test: 添加历史记录功能测试用例
```

### 测试策略
- **单元测试**: 核心业务逻辑方法
- **集成测试**: API接口和数据库操作
- **端到端测试**: 完整的生成流程
- **性能测试**: AI调用响应时间

## 风险管控

### 技术风险
| 风险项 | 影响程度 | 应对措施 |
|--------|----------|----------|
| AI API调用失败 | 高 | 实现重试机制和降级方案 |
| 生成代码质量不佳 | 中 | 优化提示词和代码检测 |
| 性能问题 | 中 | 设置合理超时和缓存策略 |
| 数据库性能 | 低 | 合理的索引设计和分页查询 |

### 进度风险
| 风险项 | 应对措施 |
|--------|----------|
| AI集成复杂度超预期 | 预留缓冲时间，简化初期功能 |
| 前后端联调问题 | 提前制定接口规范，Mock数据测试 |
| 性能优化耗时 | 分阶段优化，先保证功能可用 |

## 资源配置

### 开发环境要求
- **CPU**: 4核心以上
- **内存**: 8GB以上  
- **存储**: 50GB可用空间
- **网络**: 稳定的互联网连接(AI API调用)
- **数据库**: PostgreSQL 13+

### 生产环境建议
- **CPU**: 8核心
- **内存**: 16GB
- **存储**: 200GB SSD
- **数据库**: PostgreSQL 13+ (推荐14+)
- **Java**: JDK 11+

## 里程碑计划

### 第1周 (Phase 1-2)
- **Day 1-3**: 基础框架搭建
- **Day 4-7**: 核心API开发

### 第2周 (Phase 3-4)  
- **Day 1-3**: 历史记录功能
- **Day 4-5**: 模板配置功能

### 第3周 (Phase 5-6)
- **Day 1-4**: AI优化和测试
- **Day 5-7**: 部署和监控

### 关键时间节点
- **Week 1结束**: 核心生成功能可用
- **Week 2结束**: 完整功能交付
- **Week 3结束**: 生产环境就绪

## 成功指标

### 功能指标
- [ ] 所有API接口功能正常
- [ ] AI生成成功率 > 95%
- [ ] 生成代码可用性 > 90%

### 性能指标  
- [ ] API响应时间 < 30秒
- [ ] 系统并发支持 > 50请求/分钟
- [ ] 数据库查询响应 < 200ms

### 质量指标
- [ ] 代码覆盖率 > 80%
- [ ] 生产环境零故障运行
- [ ] 用户满意度 > 85%

## 后续优化方向

### 短期优化 (1个月内)
- 支持更多组件类型
- 优化AI生成质量
- 添加代码预览功能
- 支持自定义主题

### 中期规划 (3个月内)
- 支持多种AI模型切换
- 实现代码版本管理
- 添加协作功能
- 性能监控和告警

### 长期展望 (6个月+)
- 支持复杂业务场景
- 集成更多前端框架
- 智能代码优化建议
- 企业级功能扩展

---

**文档版本**: v1.0  
**创建时间**: 2025-01-15  
**更新时间**: 2025-01-15  
**作者**: AI Assistant  
**审核**: 待定