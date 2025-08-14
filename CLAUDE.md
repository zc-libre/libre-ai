# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于Spring Boot 3.5和LangChain4j的AI驱动企业级应用平台，提供智能仪表板生成和RAG（检索增强生成）功能。

## 技术栈

- **Java 21** - 使用最新LTS版本特性
- **Spring Boot 3.5.3** - 包含Spring WebFlux响应式编程
- **LangChain4j 1.2.0** - AI集成框架
- **MyBatis-Plus 3.5.12** - ORM框架
- **PostgreSQL** - 主数据库（支持JSONB和向量扩展）
- **Vue 3 + TypeScript** - 前端框架（libre-ai-ui模块）

## 项目结构

```
libre-ai/
├── libre-ai-admin/     # 后端服务模块
│   └── src/main/java/org/libre/ai/
│       ├── modules/
│       │   ├── dashboard/  # AI仪表板生成模块
│       │   └── rag/       # RAG知识库模块
│       └── config/        # 全局配置类
└── libre-ai-ui/       # 前端Vue项目
```

## 常用开发命令

### 后端开发
```bash
# 代码格式验证（提交前必须运行）
mvn validate

# 构建项目
mvn clean package -DskipTests

# 运行后端服务（端口9191）
cd libre-ai-admin
mvn spring-boot:run

# 运行测试
mvn test                                    # 运行所有测试
mvn test -Dtest=DashboardServiceTest       # 运行单个测试类
mvn test -Dtest=*Service*Test              # 运行匹配模式的测试

# 单独编译某个模块
mvn -pl libre-ai-admin clean compile

# 数据库管理
mvn liquibase:update                       # 执行数据库迁移
mvn liquibase:rollback -Dliquibase.rollbackCount=1  # 回滚最后一次迁移
```

### 前端开发
```bash
cd libre-ai-ui

# 安装依赖（必须使用pnpm）
pnpm install

# 启动开发服务器（默认端口3888）
pnpm dev

# 构建生产版本
pnpm build

# 代码检查和格式化
pnpm lint              # 运行所有lint检查
pnpm lint:eslint      # ESLint检查
pnpm lint:prettier    # Prettier格式化
pnpm typecheck        # TypeScript类型检查

# Vue组件预览功能（首次使用或更新依赖时运行）
pnpm run build:preview-libs    # 构建离线预览依赖库
pnpm run build:tailwind-full   # 构建完整TailwindCSS
```

## 架构设计原则

### 分层架构
- **Controller层**：REST API端点，使用`@RestController`
- **Service层**：业务逻辑，接口+实现分离
- **Mapper层**：MyBatis-Plus数据访问
- **Entity层**：数据库实体，使用`@TableName`注解

### 核心模块设计

#### Dashboard模块 (`modules/dashboard/`)
智能仪表板代码生成系统，采用4步向导式设计：
- **控制器层**：`DashboardController` - REST API入口，`DashboardStreamController` - 流式响应
- **服务层**：`DashboardService` - 核心生成逻辑，`DashboardStreamService` - 流式生成服务
- **AI助手**：`StreamDashboardAiAssistant` - LangChain4j集成，支持多AI模型
- **数据管理**：历史记录存储、模板配置管理
- **流式响应**：基于WebFlux的Flux<String>实时生成

#### RAG模块 (`modules/rag/`)
检索增强生成系统，提供企业级知识库能力：
- **知识库管理**：文档上传、切片处理、向量化存储
- **对话管理**：多轮对话、上下文记忆、会话持久化
- **多应用支持**：应用隔离、权限控制、配置管理
- **多模型集成**：支持OpenAI、Cohere、MCP等多种AI提供商
- **向量存储**：PGVector、Redis、Milvus多种选择

#### 模块交互
- Dashboard模块可集成RAG应用配置（通过`dashboard.use-aigc-app`配置）
- 共享LangChain4j基础设施和模型配置
- 统一的异常处理和日志管理

## 数据库规范

### PostgreSQL配置
- 使用JSONB类型存储复杂配置数据
- 为JSONB字段创建GIN索引提升查询性能
- 实体类继承`BaseEntity`获得通用字段

### MyBatis-Plus使用
```java
// 使用LambdaQueryWrapper构建查询
LambdaQueryWrapper<Entity> wrapper = Wrappers.lambdaQuery();
wrapper.eq(Entity::getField, value);

// 使用IService接口方法
service.saveOrUpdate(entity);
```

## AI集成指南

### LangChain4j配置（application.yml）
```yaml
dashboard:
  openai:
    api-key: ${OPENAI_API_KEY}
    base-url: ${OPENAI_BASE_URL}
    model-name: ${OPENAI_MODEL_NAME:claude-sonnet-4-20250514}
    temperature: ${OPENAI_TEMPERATURE:1}
    max-tokens: ${OPENAI_MAX_TOKENS:12800}
  generation:
    timeout: ${DASHBOARD_TIMEOUT:120000}
    max-retries: ${DASHBOARD_MAX_RETRIES:3}
```

### 支持的AI功能
- 多模型切换（OpenAI、Cohere、MCP等）
- 向量存储（PGVector、Redis、Milvus）
- 文档解析（Apache Tika）
- 流式响应（WebFlux集成）

## 代码规范

### Java代码规范
- 使用Spring JavaFormat插件自动格式化
- Lombok注解简化代码（@Data、@Slf4j等）
- 枚举使用`@Getter`和`@AllArgsConstructor`
- 统一使用Hutool工具类库

### 响应式编程
```java
// 使用Flux处理流式响应
public Flux<String> streamResponse() {
    return Flux.create(sink -> {
        // 流式处理逻辑
    });
}
```

## 环境变量配置

必需的环境变量：
- `OPENAI_API_KEY` - OpenAI API密钥
- `OPENAI_BASE_URL` - API基础URL
- `DB_PASSWORD` - 数据库密码（默认配置使用123456）

可选的环境变量：
- `OPENAI_MODEL_NAME` - 模型名称（默认：claude-sonnet-4-20250514）
- `OPENAI_TEMPERATURE` - 温度参数（默认：1）
- `OPENAI_MAX_TOKENS` - 最大token数（默认：12800）

## 开发注意事项

### 代码规范和提交
1. **代码提交前**必须运行`mvn validate`确保格式正确
2. 使用Spring JavaFormat插件统一代码格式
3. 新增API接口使用`@Tag`注解添加Swagger文档
4. 实体类继承`BaseEntity`获得审计字段（创建时间、更新时间等）

### 技术选择和最佳实践
5. 复杂JSON配置使用PostgreSQL的JSONB类型存储，并创建GIN索引
6. AI生成内容使用流式响应（Flux<String>）提升用户体验
7. MyBatis-Plus查询优先使用LambdaQueryWrapper避免硬编码字段名
8. 统一使用Hutool工具类库处理常见操作
9. Lombok注解简化样板代码（@Data、@Slf4j、@Builder等）

### 安全和配置
10. 敏感配置通过环境变量注入，不要硬编码（特别注意API密钥）
11. 数据库连接默认配置：PostgreSQL 端口5432，数据库名libre_ai
12. 开发环境使用application-dev.yml，避免在主配置文件中写死敏感信息

### 前端特定注意事项
13. 前端使用pnpm作为包管理器，不要使用npm或yarn
14. Node版本要求：^18.18.0 || ^20.9.0 || >=22.0.0
15. Vue组件预览功能首次使用需运行`pnpm run build:preview-libs`构建依赖
16. 修改UI组件时检查Element Plus版本兼容性

## 部署相关

### Docker构建
```bash
# 构建镜像
mvn clean package
docker build -t libre-ai:latest .

# 运行容器
docker run -p 9191:9191 \
  -e OPENAI_API_KEY=xxx \
  -e DB_PASSWORD=xxx \
  libre-ai:latest
```

### 健康检查端点
- `/actuator/health` - 应用健康状态
- `/actuator/info` - 应用信息

## 调试和故障排除

### 常见开发问题

#### 后端问题
- **LangChain4j调用失败**：检查OpenAI API密钥和网络连接
- **数据库连接失败**：确认PostgreSQL服务运行，检查连接配置
- **Maven格式验证失败**：运行`mvn spring-javaformat:apply`自动格式化
- **流式响应中断**：检查AI调用超时配置和网络稳定性

#### 前端问题  
- **Vue组件预览不显示**：运行`pnpm run build:preview-libs`构建依赖库
- **TailwindCSS样式不生效**：检查类名是否正确，运行`pnpm run build:tailwind-full`
- **Element Plus组件异常**：确认版本兼容性，检查主题配置
- **开发服务器启动失败**：检查Node版本，清理缓存`pnpm clean:cache`

### 日志和监控
```bash
# 查看后端日志
tail -f libre-ai-admin/logs/application.log

# 开启调试模式
export SPRING_PROFILES_ACTIVE=dev
export LOGGING_LEVEL_ORG_LIBRE_AI=DEBUG

# 前端调试
# 在浏览器控制台中设置
localStorage.setItem('debug', 'true')
```

### 性能分析
- Dashboard生成接口预期响应时间：< 30秒
- RAG对话接口预期响应时间：< 5秒  
- 前端预览编译时间：< 2秒
- 数据库查询优化：使用EXPLAIN分析慢查询