# Dashboard Generator 设计文档

## 项目简介

Dashboard Generator 是一个基于人工智能的智能仪表板代码生成系统。用户通过简单的4步向导配置（用途选择 → 布局选择 → 主题组件选择 → AI生成看板），AI 自动生成完整的 HTML/CSS/JavaScript 仪表板代码。

## 核心特性

🎯 **智能生成**: 基于 LangChain4j 集成 GPT-4 等大语言模型，生成专业可用的前端代码  
📱 **响应式设计**: 支持多种屏幕尺寸的自适应布局  
🎨 **多样主题**: 提供现代蓝、深紫夜、自然绿、暖橙色等精美主题  
📊 **丰富组件**: 支持柱状图、折线图、KPI卡片、数据表格等多种数据可视化组件  
💾 **历史管理**: 自动保存生成历史，支持配置重用和管理  
⚡ **高性能**: 优化的AI调用机制，支持重试和容错处理

## 技术架构

### 整体架构
```
前端 Vue3 + Element Plus → SpringBoot API → LangChain4j → AI模型 (GPT-4)
                                    ↓
                            PostgreSQL 数据库
```

### 核心技术栈
- **后端框架**: Spring Boot 2.7+
- **AI集成**: LangChain4j 0.25+  
- **数据库**: PostgreSQL 13+
- **构建工具**: Maven
- **Java版本**: JDK 11+

## 设计原则

本项目严格遵循以下软件设计原则：

### KISS (Keep It Simple, Stupid)
- 单控制器处理所有相关请求
- JSON字段存储复杂配置，避免多表关联
- 最小化API设计，仅实现3个核心接口

### YAGNI (You Aren't Gonna Need It)  
- 避免过度设计权限系统、缓存等复杂功能
- **PostgreSQL高级特性**: 充分利用JSONB类型和GIN索引优化
- 简单字符串模板而非复杂模板引擎

### SOLID 原则
- **SRP**: 每个服务类职责单一明确
- **OCP**: 通过接口支持不同AI模型扩展
- **DIP**: 依赖AI抽象接口，而非具体实现

### DRY (Don't Repeat Yourself)
- 统一的响应格式和异常处理
- 复用的数据模型和工具类
- 模板化的AI提示词管理

## 文档结构

本设计文档包含以下核心文件：

### [01-architecture-design.md](./01-architecture-design.md)
**后端架构设计文档**
- 整体架构设计和技术选型
- 核心组件设计 (Controller/Service/Config)
- 项目结构和代码组织
- 性能优化和安全考虑

### [02-api-design.md](./02-api-design.md) 
**API接口设计文档**
- 完整的RESTful API规范
- 详细的请求/响应参数说明
- 错误码定义和异常处理
- 接口调用示例和性能规范

### [03-data-model.md](./03-data-model.md)
**数据模型设计文档**  
- 数据库表结构设计
- Java实体类和DTO对象定义
- 数据验证规则和约束
- 模板配置数据示例

### [04-implementation-plan.md](./04-implementation-plan.md)
**实施计划文档**
- 6个阶段的详细开发计划
- 任务分解和验收标准  
- 风险管控和资源配置
- 里程碑计划和成功指标

## 核心API接口

### 1. 仪表板生成
```http
POST /api/dashboard/generate
```
根据用户配置调用AI生成完整的仪表板代码

### 2. 历史记录管理
```http
GET  /api/dashboard/history/{userId}
POST /api/dashboard/history
```
用户历史记录的查询和保存

### 3. 模板配置获取
```http
GET  /api/dashboard/templates
```
获取用途、布局、主题、组件等配置选项

## 数据模型概览

### 核心实体
- `DashboardHistory`: 历史记录实体，JSON存储配置信息
- `DashboardTemplate`: 模板配置实体，支持动态配置管理

### 主要DTO
- `DashboardRequest`: 生成请求参数
- `GenerationResult`: AI生成结果
- `GenerationOptions`: 生成选项配置

## 开发进度规划

### Phase 1: 基础框架 (2-3天)
项目骨架、依赖配置、基础组件

### Phase 2: 核心API (4-5天)  
AI生成接口、数据模型、服务逻辑

### Phase 3: 历史记录 (2-3天)
历史记录CRUD、分页查询、定时清理

### Phase 4: 模板配置 (2天)
模板数据管理、配置接口、数据初始化

### Phase 5: 优化测试 (3-4天)
AI优化、测试编写、性能调优

### Phase 6: 部署监控 (1-2天)
生产配置、健康检查、监控日志

## 质量保证

### 测试策略
- **单元测试**: 核心业务逻辑覆盖率 > 80%
- **集成测试**: API接口和数据库操作
- **性能测试**: AI调用响应时间 < 30秒

### 代码规范
- 遵循阿里巴巴Java开发手册
- 统一的代码格式化配置
- 完整的Javadoc注释

### 监控指标
- AI生成成功率 > 95%
- 系统并发支持 > 50请求/分钟
- 生产环境零故障运行

## 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- PostgreSQL 13+ (推荐14+)
- 稳定的互联网连接 (AI API调用)

### 关键配置
```yaml
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

dashboard:
  openai:
    api-key: ${OPENAI_API_KEY}
    model-name: gpt-4
  generation:
    timeout: 30s
    max-retries: 3
```

### 核心依赖
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>0.25.0</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>0.25.0</version>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 贡献指南

### Git工作流
- 功能分支: `feature/dashboard-{feature-name}`
- 提交格式: `feat/fix/docs/test: 描述信息`
- 代码审查: 所有代码变更必须经过审查

### 开发规范
1. 遵循既定的设计原则和架构模式
2. 编写完整的测试用例
3. 保持代码简洁和可维护性
4. 及时更新相关文档

## 版本历史

- **v1.0** (2025-01-15): 初始设计文档发布
- **v1.1** (计划): 核心功能实现完成  
- **v2.0** (计划): 高级功能和性能优化

## 联系方式

如有问题或建议，请通过以下方式联系：

- **项目仓库**: [libre-ai-admin](https://github.com/your-org/libre-ai-admin)
- **技术支持**: 待定
- **文档维护**: AI Assistant

---

**文档版本**: v1.0  
**最后更新**: 2025-01-15  
**状态**: 设计阶段