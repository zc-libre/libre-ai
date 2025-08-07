# Dashboard Generator API 文档

## 概述
Dashboard Generator 是一个 AI 驱动的看板生成器，可根据用户配置自动生成专业的数据看板代码。本文档描述了支持该功能所需的后端 API 接口。

## 接口清单

> 注意：以下接口为已实现的核心功能接口。遵循YAGNI原则，未实现的接口已被移除。

### 1. 看板生成接口
生成看板的核心接口，接收配置参数并返回生成的代码。

**接口地址：** `POST /api/dashboard/generate`

**请求参数：**
```json
{
  "purpose": "analytics",              // 看板用途：analytics|project|sales|monitoring
  "layout": "grid",                    // 布局样式：grid|sidebar|fullscreen
  "theme": "modern-blue",              // 主题ID
  "components": [                      // 组件列表
    "bar-chart",
    "line-chart",
    "kpi-card"
  ],
  "customOptions": {                   // 自定义选项
    "codeStyle": "modern",            // 代码风格：modern|minimal|enterprise
    "responsive": true,               // 是否支持响应式设计
    "includeData": true,              // 是否包含示例数据
    "additionalRequirements": "需要实时数据更新功能"  // 额外需求描述
  }
}
```

**响应数据：**
```json
{
  "html": "<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head>\n    <meta charset=\"UTF-8\">\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n    <title>数据分析看板</title>\n    <style>\n        /* 所有CSS样式内嵌 */\n        * { margin: 0; padding: 0; }\n        /* ... */\n    </style>\n</head>\n<body>\n    <!-- HTML内容 -->\n    <div class=\"dashboard-container\">...</div>\n    <script>\n        // 所有JavaScript代码内嵌\n        document.addEventListener('DOMContentLoaded', function() {\n            // ...\n        });\n    </script>\n</body>\n</html>",  // 生成的完整HTML文档（包含内嵌的CSS和JavaScript）
  "metadata": {
    "generatedAt": "2024-01-01T00:00:00Z",          // 生成时间
    "modelUsed": "gpt-4",                           // 使用的AI模型
    "tokensUsed": 1500,                             // 消耗的token数
    "generationTime": 3500                          // 生成耗时（毫秒）
  }
}
```

### 2. 获取看板用途选项
获取所有可用的看板用途及其支持的组件。

**接口地址：** `GET /api/dashboard/purposes`

**响应数据：**
```json
[
  {
    "id": "analytics",
    "icon": "ChartLine",
    "title": "数据分析看板",
    "description": "用于展示业务数据分析和统计",
    "components": ["bar-chart", "line-chart", "pie-chart", "kpi-card", "data-table"]
  },
  {
    "id": "project",
    "icon": "Tasks",
    "title": "项目管理看板",
    "description": "适合项目进度跟踪和任务管理",
    "components": ["kanban-board", "timeline", "progress-bar", "task-list"]
  },
  {
    "id": "sales",
    "icon": "ShoppingCart",
    "title": "销售监控看板",
    "description": "实时监控销售业绩和趋势",
    "components": ["kpi-card", "funnel-chart", "area-chart", "ranking-list"]
  },
  {
    "id": "monitoring",
    "icon": "Monitor",
    "title": "系统监控看板",
    "description": "监控系统性能和运行状态",
    "components": ["gauge-chart", "status-card", "alert-list", "metric-card"]
  }
]
```

### 3. 获取布局选项
获取所有支持的布局样式。

**接口地址：** `GET /api/dashboard/layouts`

**响应数据：**
```json
[
  {
    "id": "grid",
    "preview": "https://example.com/previews/grid-layout.png",
    "title": "网格布局",
    "description": "灵活的网格系统，适合多组件展示",
    "responsive": true
  },
  {
    "id": "sidebar",
    "preview": "https://example.com/previews/sidebar-layout.png",
    "title": "侧边栏布局",
    "description": "左侧导航栏，右侧内容区域",
    "responsive": true
  },
  {
    "id": "fullscreen",
    "preview": "https://example.com/previews/fullscreen-layout.png",
    "title": "全屏布局",
    "description": "充分利用屏幕空间，适合大屏展示",
    "responsive": false
  }
]
```

### 4. 获取主题选项
获取所有可用的主题配色方案。

**接口地址：** `GET /api/dashboard/themes`

**响应数据：**
```json
[
  {
    "id": "modern-blue",
    "name": "现代蓝",
    "primary": "#409EFF",
    "secondary": "#79BBFF",
    "accent": "#337ECC",
    "background": "#F5F7FA",
    "text": "#303133"
  },
  {
    "id": "dark-purple",
    "name": "深紫夜",
    "primary": "#6B46C1",
    "secondary": "#9333EA",
    "accent": "#A855F7",
    "background": "#1F2937",
    "text": "#F3F4F6"
  },
  {
    "id": "green-nature",
    "name": "自然绿",
    "primary": "#10B981",
    "secondary": "#34D399",
    "accent": "#059669",
    "background": "#F0FDF4",
    "text": "#064E3B"
  },
  {
    "id": "orange-warm",
    "name": "暖橙色",
    "primary": "#F97316",
    "secondary": "#FB923C",
    "accent": "#EA580C",
    "background": "#FFF7ED",
    "text": "#7C2D12"
  }
]
```

### 5. 获取组件选项
获取所有可用的看板组件。

**接口地址：** `GET /api/dashboard/components`

**响应数据：**
```json
[
  {
    "id": "bar-chart",
    "icon": "ChartBar",
    "title": "柱状图",
    "purposes": ["analytics", "sales"],
    "previewCode": "<div class=\"chart-container\">\n  <canvas id=\"barChart\"></canvas>\n</div>"
  },
  {
    "id": "line-chart",
    "icon": "ChartLine",
    "title": "折线图",
    "purposes": ["analytics", "monitoring"],
    "previewCode": "<div class=\"chart-container\">\n  <canvas id=\"lineChart\"></canvas>\n</div>"
  },
  {
    "id": "kpi-card",
    "icon": "CreditCard",
    "title": "KPI卡片",
    "purposes": ["analytics", "sales", "monitoring"],
    "previewCode": "<div class=\"kpi-card\">\n  <div class=\"kpi-value\">1,234</div>\n  <div class=\"kpi-label\">总销售额</div>\n</div>"
  },
  {
    "id": "data-table",
    "icon": "Table",
    "title": "数据表格",
    "purposes": ["analytics", "project"],
    "previewCode": "<table class=\"data-table\">\n  <thead><tr><th>列1</th><th>列2</th></tr></thead>\n  <tbody><tr><td>数据1</td><td>数据2</td></tr></tbody>\n</table>"
  }
]
```

### 6. 保存生成历史
保存用户生成的看板历史记录。

**接口地址：** `POST /api/dashboard/history`

**请求参数：**
```json
{
  "config": {
    "purpose": "analytics",
    "layout": "grid",
    "theme": "modern-blue",
    "components": ["bar-chart", "line-chart"]
  },
  "generatedHtml": "<!DOCTYPE html>...",
  "previewImage": "data:image/png;base64,..."  // Base64编码的预览图（可选）
}
```

**响应数据：**
```json
{
  "id": "hist_123456",
  "createdAt": "2024-01-01T00:00:00Z",
  "message": "保存成功"
}
```

### 7. 获取历史记录
获取用户的看板生成历史记录。

**接口地址：** `GET /api/dashboard/history`

**查询参数：**
- `page`: 页码，默认 1
- `size`: 每页数量，默认 10
- `sortBy`: 排序字段，默认 createdAt
- `sortOrder`: 排序方向，asc/desc，默认 desc

**响应数据：**
```json
{
  "data": [
    {
      "id": "hist_123456",
      "config": {
        "purpose": "analytics",
        "layout": "grid",
        "theme": "modern-blue",
        "components": ["bar-chart", "line-chart"]
      },
      "generatedHtml": "<!DOCTYPE html>...",
      "createdAt": "2024-01-01T00:00:00Z",
      "previewImage": "https://example.com/previews/hist_123456.png"
    }
  ],
  "total": 50,
  "page": 1,
  "size": 10,
  "pages": 5
}
```

### 8. 批量删除历史记录
批量删除多个历史记录。

**接口地址：** `DELETE /api/dashboard/history/batch`

**请求参数：**
```json
[
  "hist_123456",
  "hist_123457",
  "hist_123458"
]
```

**响应数据：**
```json
{
  "code": 200,
  "message": "成功删除 3 条记录",
  "data": "成功删除 3 条记录"
}
```

### 9. 健康检查
系统健康状态检查接口。

**接口地址：** `GET /api/dashboard/health`

**响应数据：**
```json
{
  "status": "UP",
  "service": "dashboard-generator",
  "timestamp": 1704067200000
}
```

## 错误响应格式
所有接口的错误响应遵循统一格式：

```json
{
  "code": 400,
  "message": "错误描述",
  "error": "INVALID_PARAMETER",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

常见错误码：
- `400` - 请求参数错误
- `401` - 未授权
- `403` - 禁止访问
- `404` - 资源不存在
- `429` - 请求过于频繁
- `500` - 服务器内部错误

## 实施建议

### 第一阶段（核心功能）
1. **看板生成接口** - 实现基于 AI 的代码生成功能
2. **获取配置选项接口** - 提供用途、布局、主题、组件的选项数据
3. **代码文件下载** - 支持用户下载生成的代码

### 第二阶段（体验优化）
1. **历史记录管理** - 保存、查询、删除历史记录
2. **实时预览** - 提供在线预览功能
3. **批量操作** - 支持批量删除历史记录等

### 技术要点
1. **AI 集成** - 集成 GPT 或其他大模型进行代码生成
2. **缓存策略** - 对配置选项数据进行缓存
3. **安全防护** - 限制请求频率，防止滥用
4. **代码质量** - 确保生成的代码符合最佳实践
5. **异步处理** - 代码生成可采用异步方式，使用 WebSocket 或 SSE 推送进度

## 数据库设计建议

### dashboard_history 表
```sql
CREATE TABLE dashboard_history (
  id VARCHAR(32) PRIMARY KEY,
  user_id VARCHAR(32) NOT NULL,
  config JSON NOT NULL,
  generated_html TEXT,
  generated_css TEXT,
  generated_js TEXT,
  preview_image TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_created (user_id, created_at DESC)
);
```

### dashboard_templates 表（可选）
```sql
CREATE TABLE dashboard_templates (
  id VARCHAR(32) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  config JSON NOT NULL,
  preview_image TEXT,
  is_public BOOLEAN DEFAULT TRUE,
  created_by VARCHAR(32),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```