# Dashboard Generator 模块

基于AI的智能仪表板代码生成系统，通过简单的4步配置自动生成完整的HTML/CSS/JavaScript仪表板代码。

## 功能特性

🎯 **智能生成**: 基于LangChain4j集成GPT-4等大语言模型生成专业前端代码  
📱 **响应式设计**: 支持多种屏幕尺寸的自适应布局  
🎨 **多样主题**: 提供现代蓝、深紫夜、自然绿、暖橙色等精美主题  
📊 **丰富组件**: 支持柱状图、折线图、KPI卡片、数据表格等多种数据可视化组件  
💾 **历史管理**: 自动保存生成历史，支持配置重用和管理  
⚡ **高性能**: 优化的AI调用机制，支持重试和容错处理

## 设计原则

严格遵循以下软件设计原则：

- **KISS (Keep It Simple, Stupid)**: 简化异常处理，统一的接口设计
- **YAGNI (You Aren't Gonna Need It)**: 仅实现当前需要的功能，避免过度设计
- **SOLID**: 
  - **SRP**: 每个服务单一职责
  - **OCP**: 通过枚举和接口支持扩展
  - **LSP**: 实现类可替换接口
  - **ISP**: 最小化接口定义
  - **DIP**: 依赖接口而非实现
- **DRY (Don't Repeat Yourself)**: 统一的JSON工具类，可复用的枚举定义

## 核心API接口

### 1. 仪表板生成
```http
POST /api/dashboard/generate
Content-Type: application/json

{
  "purpose": "analytics",
  "layout": "grid",
  "theme": "modern-blue",
  "components": ["bar-chart", "kpi-card", "data-table"],
  "options": {
    "codeStyle": "modern",
    "responsive": true,
    "includeData": true
  }
}
```

### 2. 历史记录管理
```http
GET  /api/dashboard/history/{userId}?page=1&size=10
POST /api/dashboard/history
```

### 3. 模板配置获取
```http
GET  /api/dashboard/templates
```

## 环境配置

### 必需配置

在 `application.yml` 中添加以下配置：

```yaml
dashboard:
  openai:
    api-key: ${OPENAI_API_KEY}
    model-name: gpt-4
  generation:
    timeout: 120s
    max-retries: 3
```

### 环境变量

```bash
export OPENAI_API_KEY=your_openai_api_key
export OPENAI_BASE_URL=https://api.openai.com/v1  # 可选
```

### 数据库初始化

执行以下SQL文件：
1. `src/main/resources/sql/dashboard_schema.sql` - 创建表结构
2. `src/main/resources/sql/dashboard_data.sql` - 初始化模板数据

## 项目结构

```
src/main/java/org/libre/ai/modules/dashboard/
├── controller/
│   └── DashboardController.java          # REST API控制器
├── service/
│   ├── IDashboardService.java           # 核心业务服务接口
│   ├── DashboardService.java            # 核心业务服务实现
│   ├── IAIGenerationService.java        # AI生成服务接口
│   ├── AIGenerationService.java         # AI代码生成服务实现
│   ├── IDashboardTemplateService.java   # 模板服务接口
│   └── DashboardTemplateService.java    # 模板配置服务实现
├── dto/
│   ├── DashboardRequest.java            # 请求对象
│   ├── GenerationResult.java            # 生成结果对象
│   └── ...                              # 其他DTO
├── entity/
│   ├── DashboardHistory.java            # 历史记录实体
│   └── DashboardTemplate.java           # 模板配置实体
├── enums/
│   ├── DashboardPurpose.java            # 用途枚举
│   ├── DashboardLayout.java             # 布局枚举
│   └── DashboardTheme.java              # 主题枚举
├── utils/
│   └── JsonUtils.java                   # JSON工具类
├── mapper/
│   ├── DashboardHistoryMapper.java      # 历史记录Mapper
│   └── DashboardTemplateMapper.java     # 模板配置Mapper
├── config/
│   ├── DashboardMybatisPlusConfig.java  # MyBatis-Plus配置
│   └── DashboardLangChain4jConfig.java  # LangChain4j配置
└── exception/
    ├── DashboardExceptionHandler.java   # 统一异常处理器
    └── AIServiceException.java          # AI服务异常
```

## 使用示例

### 1. Java代码调用

```java
@Autowired
private DashboardService dashboardService;

public void generateDashboard() {
    DashboardRequest request = DashboardRequest.builder()
        .purpose("analytics")
        .layout("grid")
        .theme("modern-blue")
        .components(Arrays.asList("bar-chart", "kpi-card"))
        .options(GenerationOptions.builder()
            .codeStyle("modern")
            .responsive(true)
            .includeData(true)
            .build())
        .build();
    
    GenerationResult result = dashboardService.generateDashboard(request);
    System.out.println("生成的HTML: " + result.getHtml());
}
```

### 2. curl调用示例

```bash
curl -X POST http://localhost:8080/api/dashboard/generate \
  -H "Content-Type: application/json" \
  -d '{
    "purpose": "analytics",
    "layout": "grid",
    "theme": "modern-blue",
    "components": ["bar-chart", "kpi-card"],
    "options": {
      "codeStyle": "modern",
      "responsive": true,
      "includeData": true
    }
  }'
```

## 数据模型

### 支持的配置选项

#### 用途类型 (purpose)
- `analytics`: 数据分析
- `project`: 项目管理
- `sales`: 销售监控
- `monitoring`: 系统监控

#### 布局类型 (layout)
- `grid`: 网格布局
- `sidebar`: 侧边栏布局
- `fullscreen`: 全屏布局

#### 主题类型 (theme)
- `modern-blue`: 现代蓝
- `dark-purple`: 深紫夜
- `green-nature`: 自然绿
- `orange-warm`: 暖橙色

#### 组件类型 (components)
- `bar-chart`: 柱状图
- `line-chart`: 折线图
- `pie-chart`: 饼图
- `kpi-card`: KPI指标卡
- `data-table`: 数据表格
- `progress-bar`: 进度条

## 性能规范

| 接口 | 预期响应时间 | 最大响应时间 |
|------|-------------|-------------|
| POST /generate | < 10s | 30s |
| GET /history | < 200ms | 1s |
| POST /history | < 100ms | 500ms |
| GET /templates | < 100ms | 500ms |

## 错误处理

系统提供完整的错误处理机制：

- **参数验证错误** (400): 输入参数不符合要求
- **AI服务错误** (502): AI调用失败
- **数据库错误** (500): 数据操作失败
- **系统错误** (500): 其他系统异常

## 监控和日志

### 关键指标
- AI生成成功率 > 95%
- 系统并发支持 > 50请求/分钟
- 平均响应时间 < 10秒

### 日志级别
- INFO: 正常业务流程
- WARN: 参数验证失败、配置问题
- ERROR: AI调用失败、数据库异常

## 扩展指南

### 添加新主题
1. 在 `dashboard_data.sql` 中添加主题配置
2. 更新 `DashboardRequest` 的验证规则
3. 在AI提示词中添加主题描述

### 添加新组件
1. 在 `dashboard_data.sql` 中添加组件配置
2. 更新前端选择界面
3. 在AI提示词中添加组件描述和示例

### 集成新的AI模型
1. 在 `DashboardLangChain4jConfig` 中添加新的Bean配置
2. 更新 `AIGenerationService` 支持模型切换
3. 添加相应的配置参数

## 故障排除

### 常见问题

1. **AI生成失败**
   - 检查OpenAI API Key是否正确配置
   - 确认网络连接和API访问权限
   - 查看日志中的详细错误信息

2. **数据库连接失败**
   - 确认PostgreSQL服务正常运行
   - 检查数据库连接配置
   - 验证表结构是否正确创建

3. **模板数据加载失败**
   - 确认模板数据已正确初始化
   - 检查JSON格式是否正确
   - 验证数据库索引是否创建

## 版本历史

- **v1.1** (2025-01-16): 重构优化版本
  - 提取服务接口，遵循 DIP 原则
  - 使用枚举替代硬编码，遵循 OCP 原则
  - 统一 JSON 处理逻辑，遵循 DRY 原则
  - 简化异常处理，遵循 KISS 原则
  - 添加事务管理支持
  - 修复安全日志问题
  - 移除未实现的接口，遵循 YAGNI 原则

- **v1.0** (2025-01-15): 初始版本发布
  - 核心AI生成功能
  - 历史记录管理
  - 模板配置系统
  - 完整的REST API