# Dashboard与AIGC模块集成指南

## 概述

Dashboard模块已成功集成到AIGC模块中，通过创建内置应用的方式实现了模型配置和提示词的统一管理。

## 集成架构

```
Dashboard模块
    ↓
DashboardAigcService（集成服务）
    ↓
AIGC应用配置（aigc_app表）
    ↓
统一模型管理（ModelProvider）
```

## 使用方式

### 1. 初始化Dashboard应用

首次使用前，需要执行初始化SQL脚本：

```bash
# 执行初始化脚本
psql -U libre -d libre_ai -f src/main/resources/sql/dashboard_app_init.sql
```

该脚本会创建：
- Dashboard专用模型配置（ID: `dashboard-model-001`）
- Dashboard内置应用（ID: `dashboard-app-001`）
- 应用API密钥（可选）

### 2. 配置选项

在`application.yml`中配置：

```yaml
dashboard:
  # 使用AIGC应用配置（推荐）
  use-aigc-app: true  # 默认为true，使用集成模式
  
  # 原有配置（当use-aigc-app为false时使用）
  openai:
    api-key: ${OPENAI_API_KEY}
    base-url: ${OPENAI_BASE_URL}
    model-name: ${OPENAI_MODEL_NAME}
```

### 3. 切换模式

#### 使用AIGC应用模式（推荐）
- 设置 `dashboard.use-aigc-app=true`
- 通过AIGC管理界面配置模型和提示词
- 支持动态切换模型，无需重启服务

#### 使用原有模式
- 设置 `dashboard.use-aigc-app=false`
- 使用application.yml中的配置
- 适合快速测试和开发

## 功能特性

### 1. 统一模型管理
- Dashboard使用AIGC模块的模型管理机制
- 支持多种模型提供商（OpenAI、Azure、Gemini等）
- 模型参数可通过UI界面配置

### 2. 提示词管理
- 提示词存储在数据库中
- 支持在线编辑和更新
- 保留原有的变量替换机制

### 3. 服务集成
- `DashboardAigcService`：新增的集成服务类
- 负责桥接Dashboard功能和AIGC配置
- 支持流式响应

## API使用

### 通过代码更新配置

```java
@Autowired
private DashboardAigcService dashboardAigcService;

// 更新提示词
dashboardAigcService.updateDashboardPrompt("新的提示词模板");

// 更新模型
dashboardAigcService.updateDashboardModel("new-model-id");

// 获取当前配置
AigcApp app = dashboardAigcService.getDashboardApp();
```

### 生成Dashboard

原有的API接口保持不变：

```http
POST /api/dashboard/stream/generate
Content-Type: application/json

{
  "purpose": "warehouse_monitor",
  "layoutText": "grid",
  "theme": {...},
  "components": ["line_chart", "bar_chart"],
  "options": {
    "responsive": true,
    "includeData": true
  }
}
```

## 数据模型

### aigc_app表中的Dashboard应用记录

```sql
id: 'dashboard-app-001'
model_id: 'dashboard-model-001'
name: 'Dashboard生成器'
prompt: '...' -- Dashboard提示词模板
des: '智能生成可视化大屏Dashboard页面'
```

### aigc_model表中的模型配置

```sql
id: 'dashboard-model-001'
type: 'CHAT'
provider: 'OPENAI'
name: 'Dashboard生成模型'
model: 'qwen3'
temperature: 1.0
response_limit: 12800
```

## 注意事项

1. **环境变量配置**：初始化脚本使用环境变量配置API密钥，需要确保环境变量已设置
2. **模型兼容性**：确保选择的模型支持流式响应
3. **提示词格式**：保留原有的变量占位符格式（`{{variableName}}`）
4. **数据库依赖**：需要PostgreSQL数据库支持

## 优势

1. **统一管理**：模型和提示词通过AIGC模块统一管理
2. **灵活配置**：支持通过UI界面动态调整配置
3. **无缝切换**：可在集成模式和独立模式间切换
4. **向后兼容**：保留原有的API接口和功能

## 后续优化建议

1. 支持多个Dashboard应用配置（不同场景使用不同模型）
2. 添加Dashboard生成历史与AIGC对话的关联
3. 实现提示词版本管理
4. 添加模型性能监控和统计