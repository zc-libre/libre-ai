# Dashboard Generator 后端架构设计文档

## 项目概述

Dashboard Generator 是一个基于 AI 的智能仪表板代码生成系统，用户通过简单的配置选择，AI 自动生成完整的 HTML/CSS/JavaScript 仪表板代码。

### 核心功能
- 4步向导式配置：用途选择 → 布局选择 → 主题组件选择 → AI生成看板
- 实时预览生成结果
- 历史记录管理
- 多种布局和主题支持

## 技术架构

### 整体架构设计

```
┌─────────────────┐    HTTP/REST     ┌──────────────────┐    LangChain4j    ┌─────────────┐
│   Vue3 前端     │ ───────────────► │  SpringBoot API  │ ─────────────────► │  AI 模型    │
│  (Element Plus) │ ◄─────────────── │   (Controller)   │ ◄───────────────── │ (GPT-4/等)  │
└─────────────────┘     JSON         └──────────────────┘                    └─────────────┘
                                              │
                                              ▼
                                      ┌──────────────────┐
                                      │  PostgreSQL DB   │
                                      │  (历史记录存储)   │
                                      └──────────────────┘
```

### 核心设计原则

#### KISS (Keep It Simple, Stupid)
- **单控制器设计**: 使用一个 `DashboardController` 处理所有相关请求
- **简化数据存储**: 使用 JSON 字段存储复杂配置，避免多表关联
- **最小API设计**: 仅实现3个核心接口，满足前端所有需求

#### YAGNI (You Aren't Gonna Need It)
- **避免过度设计**: 不预先实现权限系统、缓存、分布式等复杂功能
- **渐进式数据库**: 开发阶段使用 H2，生产环境可切换到 MySQL
- **简单AI集成**: 使用字符串模板而非复杂的模板引擎

#### SOLID 原则
- **SRP**: 每个服务类职责单一明确
- **OCP**: 通过接口支持不同 AI 模型扩展
- **DIP**: 依赖 AI 抽象接口，而非具体实现

## 项目结构

```
src/main/java/org/libre/ai/modules/dashboard/
├── controller/
│   └── DashboardController.java          # REST API 控制器
├── service/
│   ├── DashboardService.java            # 核心业务逻辑
│   └── AIGenerationService.java         # AI 代码生成服务
├── dto/
│   ├── DashboardRequest.java            # 请求对象
│   ├── GenerationResult.java            # 生成结果对象
│   └── GenerationOptions.java           # 生成选项配置
├── entity/
│   ├── DashboardHistory.java            # 历史记录实体
│   └── DashboardTemplate.java           # 模板配置实体
├── config/
│   └── LangChain4jConfig.java           # AI 模型配置
└── mapper/
    └── DashboardHistoryMapper.java      # 数据访问层
```

## 核心组件设计

### 1. Controller 层 (REST API)

```java
@RestController
@RequestMapping("/api/dashboard")
@Slf4j
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * AI 生成仪表板代码
     */
    @PostMapping("/generate")
    public R<GenerationResult> generate(@RequestBody @Valid DashboardRequest request) {
        return R.ok(dashboardService.generateDashboard(request));
    }
    
    /**
     * 获取用户历史记录
     */
    @GetMapping("/history/{userId}")
    public R<List<DashboardHistory>> getHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(dashboardService.getHistory(userId, page, size));
    }
    
    /**
     * 保存历史记录
     */
    @PostMapping("/history")
    public R<Void> saveHistory(@RequestBody @Valid DashboardHistory history) {
        dashboardService.saveHistory(history);
        return R.ok();
    }
    
    /**
     * 获取模板配置数据
     */
    @GetMapping("/templates")
    public R<DashboardTemplates> getTemplates() {
        return R.ok(dashboardService.getTemplates());
    }
}
```

### 2. Service 层 (业务逻辑)

```java
@Service
@Slf4j
public class DashboardService {
    
    @Autowired
    private AIGenerationService aiGenerationService;
    
    @Autowired
    private DashboardHistoryMapper historyMapper;
    
    /**
     * 生成仪表板代码
     */
    public GenerationResult generateDashboard(DashboardRequest request) {
        log.info("开始生成仪表板，配置: {}", request);
        
        // 1. 参数验证
        validateRequest(request);
        
        // 2. 调用 AI 生成代码
        GenerationResult result = aiGenerationService.generateCode(request);
        
        // 3. 记录生成日志
        log.info("仪表板生成完成，耗时: {}ms", result.getMetadata().getGenerationTime());
        
        return result;
    }
    
    /**
     * 历史记录管理
     */
    public void saveHistory(DashboardHistory history) {
        history.setId(UUID.randomUUID().toString());
        history.setCreatedAt(LocalDateTime.now());
        historyMapper.insert(history);
    }
    
    public List<DashboardHistory> getHistory(String userId, int page, int size) {
        return historyMapper.selectByUserIdWithPage(userId, page * size, size);
    }
}
```

### 3. AI Generation Service (AI集成)

```java
@Service
@Slf4j
public class AIGenerationService {
    
    @Autowired
    private ChatLanguageModel chatModel;
    
    @Autowired
    private PromptTemplate dashboardPromptTemplate;
    
    /**
     * 使用 LangChain4j 生成仪表板代码
     */
    public GenerationResult generateCode(DashboardRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 构建 AI 提示词
            String prompt = buildPrompt(request);
            
            // 2. 调用 AI 模型
            AiMessage response = chatModel.generate(prompt);
            
            // 3. 解析 AI 响应
            CodeGenerationResult aiResult = parseAIResponse(response.text());
            
            // 4. 构建返回结果
            return GenerationResult.builder()
                .html(aiResult.getHtml())
                .css(aiResult.getCss())
                .javascript(aiResult.getJavascript())
                .metadata(GenerationMetadata.builder()
                    .generatedAt(LocalDateTime.now())
                    .modelUsed("gpt-4")
                    .generationTime((System.currentTimeMillis() - startTime) / 1000.0)
                    .build())
                .build();
                
        } catch (Exception e) {
            log.error("AI 代码生成失败", e);
            throw new ServiceException("代码生成失败，请重试");
        }
    }
    
    private String buildPrompt(DashboardRequest request) {
        Map<String, Object> variables = Map.of(
            "purpose", request.getPurpose(),
            "layout", request.getLayout(),
            "theme", request.getTheme(),
            "components", String.join(", ", request.getComponents()),
            "codeStyle", request.getOptions().getCodeStyle(),
            "responsive", request.getOptions().isResponsive(),
            "additionalRequirements", request.getOptions().getAdditionalRequirements()
        );
        
        return dashboardPromptTemplate.apply(variables).text();
    }
}
```

## 配置设计

### LangChain4j 配置

```java
@Configuration
@EnableConfigurationProperties(DashboardProperties.class)
public class LangChain4jConfig {
    
    @Bean
    public ChatLanguageModel chatLanguageModel(DashboardProperties properties) {
        return OpenAiChatModel.builder()
            .apiKey(properties.getOpenai().getApiKey())
            .modelName(properties.getOpenai().getModelName())
            .temperature(0.7)
            .maxRetries(3)
            .timeout(Duration.ofMinutes(2))
            .logRequests(true)
            .logResponses(true)
            .build();
    }
    
    @Bean
    public PromptTemplate dashboardPromptTemplate() {
        return PromptTemplate.from("""
            作为一个专业的前端开发工程师，请根据以下配置生成一个完整的仪表板页面：
            
            ## 配置信息
            - 用途场景: {{purpose}}
            - 布局样式: {{layout}}
            - 主题配色: {{theme}}
            - 包含组件: {{components}}
            
            ## 技术要求
            - 代码风格: {{codeStyle}}
            - 响应式设计: {{responsive}}
            {{#if additionalRequirements}}
            - 额外需求: {{additionalRequirements}}
            {{/if}}
            
            ## 输出要求
            请返回标准的JSON格式，包含完整的HTML、CSS、JavaScript代码：
            {
              "html": "完整的HTML代码",
              "css": "完整的CSS样式代码", 
              "javascript": "完整的JavaScript交互代码"
            }
            
            注意：代码要求结构清晰、注释完整、遵循最佳实践。
            """);
    }
}
```

### 应用配置

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/libre_ai
    username: ${DB_USERNAME:libre_ai}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true

dashboard:
  openai:
    api-key: ${OPENAI_API_KEY}
    model-name: gpt-4
    base-url: https://api.openai.com/v1
  generation:
    timeout: 120s
    max-retries: 3
    enable-cache: true
```

## 数据存储设计

### 数据库表结构

```sql
-- 历史记录表
CREATE TABLE dashboard_history (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    config_json TEXT NOT NULL,
    generated_html TEXT,
    preview_image TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_dashboard_history_user_created ON dashboard_history (user_id, created_at DESC);
CREATE INDEX idx_dashboard_history_created ON dashboard_history (created_at DESC);

-- 模板配置表 (可选)
CREATE TABLE dashboard_templates (
    type VARCHAR(32) PRIMARY KEY,
    data_json TEXT NOT NULL,
    version VARCHAR(16) DEFAULT '1.0',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_dashboard_templates_type_enabled ON dashboard_templates (type, enabled);

-- 添加表注释
COMMENT ON TABLE dashboard_history IS '仪表板生成历史记录';
COMMENT ON COLUMN dashboard_history.config_json IS '配置JSON数据';
COMMENT ON COLUMN dashboard_history.generated_html IS '生成的HTML代码';
COMMENT ON COLUMN dashboard_history.preview_image IS 'Base64预览图片';

COMMENT ON TABLE dashboard_templates IS '仪表板模板配置';
COMMENT ON COLUMN dashboard_templates.type IS '模板类型(purposes/layouts/themes/components)';
COMMENT ON COLUMN dashboard_templates.data_json IS '模板数据JSON';
```

## 异常处理

```java
@ControllerAdvice
@Slf4j
public class DashboardExceptionHandler {
    
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<R<Void>> handleServiceException(ServiceException e) {
        log.error("业务异常", e);
        return ResponseEntity.ok(R.fail(e.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<R<Void>> handleValidationException(ValidationException e) {
        log.warn("参数验证失败", e);
        return ResponseEntity.badRequest().body(R.fail("参数验证失败: " + e.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Void>> handleGenericException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(500).body(R.fail("系统内部错误"));
    }
}
```

## 性能优化考虑

1. **AI调用优化**: 设置合理的超时时间和重试机制
2. **数据库优化**: 为常用查询字段创建索引
3. **缓存策略**: 对模板数据进行缓存
4. **异步处理**: 对于耗时的AI生成操作考虑异步处理

## 安全性考虑

1. **输入验证**: 严格验证所有用户输入
2. **SQL注入防护**: 使用参数化查询
3. **XSS防护**: 对生成的HTML代码进行安全检查
4. **API限流**: 防止恶意调用AI接口

## 监控和日志

1. **请求日志**: 记录所有API调用
2. **性能监控**: 监控AI调用耗时
3. **错误告警**: 对系统异常进行告警
4. **使用统计**: 统计功能使用情况

---

**文档版本**: v1.0  
**创建时间**: 2025-01-15  
**更新时间**: 2025-01-15  
**作者**: AI Assistant