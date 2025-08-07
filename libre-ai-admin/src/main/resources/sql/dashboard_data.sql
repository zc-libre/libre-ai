-- Dashboard Generator 模板数据初始化
-- 遵循设计文档中的模板配置数据结构

-- 清空现有模板数据（可选）
-- DELETE FROM dashboard_templates WHERE type IN ('purposes', 'layouts', 'themes', 'components');

-- 插入用途模板数据
INSERT INTO dashboard_templates (type, data_json, version) VALUES
('purposes', '[
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
]', '1.0') ON CONFLICT (type) DO UPDATE SET data_json = EXCLUDED.data_json;

-- 插入布局模板数据
INSERT INTO dashboard_templates (type, data_json, version) VALUES
('layouts', '[
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
]', '1.0') ON CONFLICT (type) DO UPDATE SET data_json = EXCLUDED.data_json;

-- 插入主题模板数据
INSERT INTO dashboard_templates (type, data_json, version) VALUES
('themes', '[
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
]', '1.0') ON CONFLICT (type) DO UPDATE SET data_json = EXCLUDED.data_json;

-- 插入组件模板数据
INSERT INTO dashboard_templates (type, data_json, version) VALUES
('components', '[
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
  },
  {
    "id": "gauge-chart",
    "title": "仪表盘图",
    "description": "以仪表盘形式展示数值和阈值",
    "icon": "gauge",
    "purposes": ["monitoring"],
    "previewCode": "<div class=\"gauge-container\"><svg></svg></div>"
  },
  {
    "id": "alert-panel",
    "title": "告警面板",
    "description": "系统告警信息展示面板",
    "icon": "alert",
    "purposes": ["monitoring"],
    "previewCode": "<div class=\"alert-panel\"><div class=\"alert-item\"></div></div>"
  }
]', '1.0') ON CONFLICT (type) DO UPDATE SET data_json = EXCLUDED.data_json;