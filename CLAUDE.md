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
mvn test

# 单独编译某个模块
mvn -pl libre-ai-admin clean compile
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
```

## 架构设计原则

### 分层架构
- **Controller层**：REST API端点，使用`@RestController`
- **Service层**：业务逻辑，接口+实现分离
- **Mapper层**：MyBatis-Plus数据访问
- **Entity层**：数据库实体，使用`@TableName`注解

### 模块设计
1. **Dashboard模块**：4步向导式AI仪表板生成
   - `DashboardGeneratorController` - API入口
   - `DashboardGeneratorService` - 生成逻辑
   - 支持流式响应（Flux<String>）

2. **RAG模块**：知识库和对话管理
   - 知识库管理：文档上传、切片、向量化
   - 对话管理：多轮对话、记忆存储
   - 应用配置：多应用隔离

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

1. **代码提交前**必须运行`mvn validate`确保格式正确
2. 新增API接口使用`@Tag`注解添加Swagger文档
3. 复杂JSON配置使用PostgreSQL的JSONB类型存储
4. AI生成内容使用流式响应（Flux<String>）提升用户体验
5. 敏感配置通过环境变量注入，不要硬编码
6. 前端使用pnpm作为包管理器，不要使用npm或yarn
7. 数据库连接默认配置：PostgreSQL 端口5432，数据库名libre_ai

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