# Dashboard Generator API 接口设计文档

## 接口概述

Dashboard Generator 后端提供 RESTful API 接口，支持仪表板代码生成、历史记录管理和模板配置获取等核心功能。

### 基础信息
- **Base URL**: `/api/dashboard`
- **Content-Type**: `application/json`
- **字符编码**: UTF-8
- **API版本**: v1.0

## 统一响应格式

所有接口均使用统一的响应格式：

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {}, 
  "timestamp": 1642248000000
}
```

### 响应字段说明
- `success`: 布尔值，表示请求是否成功
- `code`: 数字，HTTP状态码
- `message`: 字符串，响应消息
- `data`: 对象，具体的响应数据
- `timestamp`: 长整型，响应时间戳

## 核心接口设计

### 1. 仪表板代码生成

**接口描述**: 根据用户配置，使用AI生成完整的仪表板代码

#### 请求信息
```http
POST /api/dashboard/generate
Content-Type: application/json
```

#### 请求参数
```json
{
  "purpose": "analytics",
  "layout": "grid", 
  "theme": "modern-blue",
  "components": ["bar-chart", "kpi-card", "data-table"],
  "options": {
    "codeStyle": "modern",
    "responsive": true,
    "includeData": true,
    "additionalRequirements": "需要实时数据更新功能"
  }
}
```

##### 请求字段说明
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| purpose | String | 是 | 仪表板用途 (analytics/project/sales/monitoring) |
| layout | String | 是 | 布局样式 (grid/sidebar/fullscreen) |
| theme | String | 是 | 主题配色 (modern-blue/dark-purple/green-nature/orange-warm) |
| components | String[] | 是 | 组件列表，至少选择一个组件 |
| options | Object | 否 | 生成选项配置 |
| options.codeStyle | String | 否 | 代码风格 (modern/minimal/enterprise)，默认 modern |
| options.responsive | Boolean | 否 | 是否响应式设计，默认 true |
| options.includeData | Boolean | 否 | 是否包含示例数据，默认 true |
| options.additionalRequirements | String | 否 | 额外需求描述，最大500字符 |

#### 响应示例
```json
{
  "success": true,
  "code": 200,
  "message": "代码生成成功",
  "data": {
    "html": "<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head>\n    <meta charset=\"UTF-8\">\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n    <title>数据分析看板</title>\n    <style>\n        /* 所有CSS样式内嵌在这里 */\n        * { margin: 0; padding: 0; box-sizing: border-box; }\n        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }\n        /* ... 更多样式 ... */\n    </style>\n</head>\n<body>\n    <!-- HTML内容 -->\n    <div class=\"dashboard-container\">\n        <!-- ... 看板内容 ... -->\n    </div>\n    <script>\n        // 所有JavaScript代码内嵌在这里\n        document.addEventListener('DOMContentLoaded', function() {\n            console.log('Dashboard loaded');\n            // ... 更多交互逻辑 ...\n        });\n    </script>\n</body>\n</html>",
    "metadata": {
      "generatedAt": "2025-01-15T10:30:00Z",
      "modelUsed": "gpt-4",
      "tokensUsed": 1500,
      "generationTime": 3.2,
      "linesOfCode": 450,
      "fileSize": "28KB"
    }
  },
  "timestamp": 1642248000000
}
```

##### 响应字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| html | String | 生成的完整HTML文档，包含内嵌的CSS和JavaScript，可直接在浏览器中运行 |
| metadata | Object | 生成过程的元数据信息 |
| metadata.generatedAt | String | 生成时间 (ISO 8601格式) |
| metadata.modelUsed | String | 使用的AI模型名称 |
| metadata.tokensUsed | Integer | 消耗的Token数量 |
| metadata.generationTime | Double | 生成耗时 (秒) |
| metadata.linesOfCode | Integer | 生成的代码行数 |
| metadata.fileSize | String | 代码文件大小 |

#### 错误响应
```json
{
  "success": false,
  "code": 400,
  "message": "参数验证失败: components不能为空",
  "data": null,
  "timestamp": 1642248000000
}
```

### 2. 获取历史记录

**接口描述**: 获取指定用户的仪表板生成历史记录

#### 请求信息
```http
GET /api/dashboard/history/{userId}?page=0&size=10
```

#### 路径参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户ID |

#### 查询参数  
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|-------|------|
| page | Integer | 否 | 0 | 页码，从0开始 |
| size | Integer | 否 | 10 | 每页大小，最大100 |

#### 响应示例
```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": "hist_001",
        "config": {
          "purpose": "analytics",
          "layout": "grid",
          "theme": "modern-blue",
          "components": ["bar-chart", "kpi-card"]
        },
        "previewImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhE...",
        "createdAt": "2025-01-15T10:30:00Z"
      }
    ],
    "total": 25,
    "page": 0,
    "size": 10
  },
  "timestamp": 1642248000000
}
```

### 3. 保存历史记录

**接口描述**: 保存仪表板生成的历史记录

#### 请求信息
```http
POST /api/dashboard/history
Content-Type: application/json
```

#### 请求参数
```json
{
  "userId": "user_123",
  "config": {
    "purpose": "analytics",
    "layout": "grid", 
    "theme": "modern-blue",
    "components": ["bar-chart", "kpi-card", "data-table"],
    "options": {
      "codeStyle": "modern",
      "responsive": true,
      "includeData": true
    }
  },
  "generatedHtml": "<!DOCTYPE html>...",
  "previewImage": "data:image/png;base64,..."
}
```

##### 请求字段说明
| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户ID |
| config | Object | 是 | 仪表板配置信息 |  
| generatedHtml | String | 否 | 生成的HTML代码，用于预览 |
| previewImage | String | 否 | Base64格式的预览图片 |

#### 响应示例
```json
{
  "success": true,
  "code": 200,
  "message": "保存成功",
  "data": {
    "id": "hist_002"
  },
  "timestamp": 1642248000000
}
```

### 4. 获取模板配置

**接口描述**: 获取仪表板配置选项数据，包括用途、布局、主题、组件等

#### 请求信息
```http
GET /api/dashboard/templates
```

#### 响应示例
```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": {
    "purposes": [
      {
        "id": "analytics",
        "title": "数据分析",
        "description": "用于数据可视化和业务分析的仪表板",
        "icon": "chart-bar",
        "components": ["bar-chart", "line-chart", "kpi-card", "data-table"]
      },
      {
        "id": "project", 
        "title": "项目管理",
        "description": "项目进度跟踪和团队协作管理",
        "icon": "project",
        "components": ["progress-bar", "task-list", "team-card", "timeline"]
      },
      {
        "id": "sales",
        "title": "销售监控", 
        "description": "销售数据监控和业绩分析",
        "icon": "trending-up",
        "components": ["pie-chart", "sales-funnel", "kpi-card", "region-map"]
      },
      {
        "id": "monitoring",
        "title": "系统监控",
        "description": "服务器和应用程序性能监控",
        "icon": "monitor",
        "components": ["gauge-chart", "alert-panel", "log-table", "metrics-card"]
      }
    ],
    "layouts": [
      {
        "id": "grid",
        "title": "网格布局",
        "description": "灵活的网格式组件排列，适合多种屏幕尺寸",
        "preview": "/images/layout-grid.png",
        "responsive": true
      },
      {
        "id": "sidebar",
        "title": "侧边栏布局", 
        "description": "左侧导航栏配合主内容区域的经典布局", 
        "preview": "/images/layout-sidebar.png",
        "responsive": true
      },
      {
        "id": "fullscreen",
        "title": "全屏布局",
        "description": "单页面全屏显示，适合大屏展示",
        "preview": "/images/layout-fullscreen.png", 
        "responsive": false
      }
    ],
    "themes": [
      {
        "id": "modern-blue",
        "name": "现代蓝",
        "description": "简洁现代的蓝色主题，适合商务场景",
        "primary": "#409EFF",
        "secondary": "#67C23A", 
        "accent": "#E6A23C",
        "background": "#F5F7FA",
        "text": "#303133"
      },
      {
        "id": "dark-purple",
        "name": "深紫夜",
        "description": "深色紫色主题，护眼且富有科技感",
        "primary": "#722ED1",
        "secondary": "#52C41A",
        "accent": "#FA8C16", 
        "background": "#1F1F1F",
        "text": "#FFFFFF"
      },
      {
        "id": "green-nature", 
        "name": "自然绿",
        "description": "清新自然的绿色主题，舒适养眼",
        "primary": "#52C41A",
        "secondary": "#1890FF",
        "accent": "#FAAD14",
        "background": "#F6FFED", 
        "text": "#262626"
      },
      {
        "id": "orange-warm",
        "name": "暖橙色",
        "description": "温暖活力的橙色主题，充满活力",
        "primary": "#FA8C16", 
        "secondary": "#13C2C2",
        "accent": "#EB2F96",
        "background": "#FFF7E6",
        "text": "#434343"
      }
    ],
    "components": [
      {
        "id": "bar-chart",
        "title": "柱状图",
        "description": "用于展示分类数据的对比",
        "icon": "chart-bar",
        "purposes": ["analytics", "sales"],
        "previewCode": "<div class=\"chart-container\"><canvas></canvas></div>"
      },
      {
        "id": "line-chart", 
        "title": "折线图",
        "description": "用于展示数据随时间的变化趋势",
        "icon": "chart-line",
        "purposes": ["analytics", "monitoring"],
        "previewCode": "<div class=\"chart-container\"><canvas></canvas></div>"
      },
      {
        "id": "pie-chart",
        "title": "饼图",
        "description": "用于展示各部分占整体的比例关系", 
        "icon": "chart-pie",
        "purposes": ["analytics", "sales"],
        "previewCode": "<div class=\"chart-container\"><canvas></canvas></div>"
      },
      {
        "id": "kpi-card",
        "title": "KPI指标卡",
        "description": "展示关键业务指标的卡片组件",
        "icon": "card",
        "purposes": ["analytics", "sales", "project", "monitoring"],
        "previewCode": "<div class=\"kpi-card\"><div class=\"kpi-value\">1,234</div></div>"
      },
      {
        "id": "data-table",
        "title": "数据表格", 
        "description": "结构化数据的表格展示组件",
        "icon": "table",
        "purposes": ["analytics", "project", "monitoring"],
        "previewCode": "<table class=\"data-table\"><thead><tr><th>列名</th></tr></thead></table>"
      },
      {
        "id": "progress-bar",
        "title": "进度条",
        "description": "展示任务或项目完成进度",
        "icon": "progress", 
        "purposes": ["project"],
        "previewCode": "<div class=\"progress-bar\"><div class=\"progress-fill\"></div></div>"
      }
    ]
  },
  "timestamp": 1642248000000
}
```

## 错误码定义

| 错误码 | HTTP状态码 | 说明 |
|--------|------------|------|
| 200 | 200 | 操作成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未授权访问 |
| 403 | 403 | 权限不足 |
| 404 | 404 | 资源不存在 |
| 429 | 429 | 请求频率超限 |
| 500 | 500 | 服务器内部错误 |
| 502 | 502 | AI服务调用失败 |
| 503 | 503 | 服务暂时不可用 |

## 常见错误响应示例

### 参数验证失败
```json
{
  "success": false,
  "code": 400,
  "message": "参数验证失败",
  "data": {
    "errors": [
      {
        "field": "components",
        "message": "组件列表不能为空"
      },
      {
        "field": "purpose", 
        "message": "用途必须为: analytics, project, sales, monitoring 之一"
      }
    ]
  },
  "timestamp": 1642248000000
}
```

### AI服务调用失败
```json
{
  "success": false,
  "code": 502,
  "message": "AI代码生成服务暂时不可用，请稍后重试",
  "data": null,
  "timestamp": 1642248000000
}
```

### 请求频率超限
```json
{
  "success": false,
  "code": 429,
  "message": "请求频率过高，请稍后再试",
  "data": {
    "retryAfter": 60
  },
  "timestamp": 1642248000000
}
```

## 接口调用示例

### curl 示例

```bash
# 生成仪表板代码
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

# 获取历史记录
curl -X GET "http://localhost:8080/api/dashboard/history/user_123?page=0&size=5"

# 获取模板配置
curl -X GET http://localhost:8080/api/dashboard/templates
```

### JavaScript 示例

```javascript
// 生成仪表板代码
const generateDashboard = async (config) => {
  try {
    const response = await fetch('/api/dashboard/generate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(config)
    });
    
    const result = await response.json();
    if (result.success) {
      console.log('生成成功:', result.data);
      return result.data;
    } else {
      console.error('生成失败:', result.message);
      throw new Error(result.message);
    }
  } catch (error) {
    console.error('请求失败:', error);
    throw error;
  }
};

// 使用示例
const config = {
  purpose: 'analytics',
  layout: 'grid',
  theme: 'modern-blue',
  components: ['bar-chart', 'kpi-card', 'data-table'],
  options: {
    codeStyle: 'modern',
    responsive: true,
    includeData: true,
    additionalRequirements: '需要支持数据导出功能'
  }
};

generateDashboard(config)
  .then(result => {
    // 处理生成结果
    document.getElementById('html-preview').innerHTML = result.html;
  })
  .catch(error => {
    // 处理错误
    alert('生成失败: ' + error.message);
  });
```

## 接口性能规范

| 接口 | 预期响应时间 | 最大响应时间 |
|------|-------------|-------------|
| POST /generate | < 10s | 30s |
| GET /history | < 200ms | 1s |
| POST /history | < 100ms | 500ms |
| GET /templates | < 100ms | 500ms |

## 版本控制

- **当前版本**: v1.0
- **版本策略**: 采用语义化版本控制
- **向后兼容**: 保证次版本号内的向后兼容性
- **废弃通知**: 接口废弃前会提前30天通知

---

**文档版本**: v1.0  
**创建时间**: 2025-01-15  
**更新时间**: 2025-01-15  
**作者**: AI Assistant