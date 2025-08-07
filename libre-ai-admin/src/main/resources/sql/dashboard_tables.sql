-- Dashboard Generator 数据库表结构
-- 基于PostgreSQL 13+ JSONB特性优化设计

-- 1. 仪表板历史记录表
CREATE TABLE IF NOT EXISTS dashboard_history (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    config_json JSONB NOT NULL,
    generated_html TEXT,
    preview_image TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引优化查询性能
CREATE INDEX IF NOT EXISTS idx_dashboard_history_user_created ON dashboard_history (user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_dashboard_history_created ON dashboard_history (created_at DESC);

-- 为JSON字段创建GIN索引支持高效查询
CREATE INDEX IF NOT EXISTS idx_dashboard_history_config_gin ON dashboard_history USING GIN (config_json);

-- 添加表和字段注释
COMMENT ON TABLE dashboard_history IS '仪表板生成历史记录';
COMMENT ON COLUMN dashboard_history.id IS '记录ID';
COMMENT ON COLUMN dashboard_history.user_id IS '用户ID';
COMMENT ON COLUMN dashboard_history.config_json IS '仪表板配置JSON数据';
COMMENT ON COLUMN dashboard_history.generated_html IS '生成的HTML代码';
COMMENT ON COLUMN dashboard_history.preview_image IS 'Base64预览图片';

-- 2. 仪表板模板配置表(可选)
CREATE TABLE IF NOT EXISTS dashboard_templates (
    type VARCHAR(32) NOT NULL PRIMARY KEY,
    data_json JSONB NOT NULL,
    version VARCHAR(16) DEFAULT '1.0',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_dashboard_templates_type_enabled ON dashboard_templates (type, enabled);

-- 为JSON字段创建GIN索引
CREATE INDEX IF NOT EXISTS idx_dashboard_templates_data_gin ON dashboard_templates USING GIN (data_json);

-- 添加表和字段注释
COMMENT ON TABLE dashboard_templates IS '仪表板模板配置';
COMMENT ON COLUMN dashboard_templates.type IS '模板类型(purposes/layouts/themes/components)';
COMMENT ON COLUMN dashboard_templates.data_json IS '模板数据JSON';
COMMENT ON COLUMN dashboard_templates.version IS '模板版本';
COMMENT ON COLUMN dashboard_templates.enabled IS '是否启用';

-- 数据完整性约束
ALTER TABLE dashboard_history 
ADD CONSTRAINT IF NOT EXISTS chk_id_format 
CHECK (id ~ '^[a-zA-Z0-9_-]{1,64}$');

-- 检查JSON字段不为空
ALTER TABLE dashboard_history 
ADD CONSTRAINT IF NOT EXISTS chk_config_json_not_empty 
CHECK (config_json IS NOT NULL AND config_json != '{}');

-- 唯一约束
ALTER TABLE dashboard_templates 
ADD CONSTRAINT IF NOT EXISTS uk_type_version 
UNIQUE (type, version);

-- 插入默认模板数据
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
]', '1.0'),

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
]', '1.0'),

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
]', '1.0'),

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
  }
]', '1.0')
ON CONFLICT (type) DO NOTHING;