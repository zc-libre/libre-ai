-- Dashboard Generator 数据库表结构
-- 遵循KISS原则：简单的表设计，使用JSONB优化复杂数据存储
-- 遵循PostgreSQL最佳实践：合理的索引设计和数据类型选择

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

-- 为dashboard_history创建索引
CREATE INDEX IF NOT EXISTS idx_dashboard_history_user_created 
ON dashboard_history (user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_dashboard_history_created 
ON dashboard_history (created_at DESC);

-- 为JSONB字段创建GIN索引以支持高效查询
CREATE INDEX IF NOT EXISTS idx_dashboard_history_config_gin 
ON dashboard_history USING GIN (config_json);

-- 2. 仪表板模板配置表
CREATE TABLE IF NOT EXISTS dashboard_templates (
    type VARCHAR(32) NOT NULL PRIMARY KEY,
    data_json JSONB NOT NULL,
    version VARCHAR(16) DEFAULT '1.0',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 为dashboard_templates创建索引
CREATE INDEX IF NOT EXISTS idx_dashboard_templates_type_enabled 
ON dashboard_templates (type, enabled);

-- 为JSONB字段创建GIN索引
CREATE INDEX IF NOT EXISTS idx_dashboard_templates_data_gin 
ON dashboard_templates USING GIN (data_json);

-- 添加表和字段注释
COMMENT ON TABLE dashboard_history IS '仪表板生成历史记录';
COMMENT ON COLUMN dashboard_history.id IS '记录ID';
COMMENT ON COLUMN dashboard_history.user_id IS '用户ID';
COMMENT ON COLUMN dashboard_history.config_json IS '仪表板配置JSON';
COMMENT ON COLUMN dashboard_history.generated_html IS '生成的HTML代码';
COMMENT ON COLUMN dashboard_history.preview_image IS 'Base64预览图片';

COMMENT ON TABLE dashboard_templates IS '仪表板模板配置';
COMMENT ON COLUMN dashboard_templates.type IS '模板类型(purposes/layouts/themes/components)';
COMMENT ON COLUMN dashboard_templates.data_json IS '模板数据JSON';
COMMENT ON COLUMN dashboard_templates.version IS '模板版本';
COMMENT ON COLUMN dashboard_templates.enabled IS '是否启用';

-- 数据完整性约束
ALTER TABLE dashboard_history 
ADD CONSTRAINT IF NOT EXISTS chk_id_format 
CHECK (id ~ '^[a-zA-Z0-9_-]{1,64}$');

ALTER TABLE dashboard_history 
ADD CONSTRAINT IF NOT EXISTS chk_config_json_not_empty 
CHECK (config_json IS NOT NULL AND config_json != '{}');

ALTER TABLE dashboard_templates 
ADD CONSTRAINT IF NOT EXISTS uk_type_version 
UNIQUE (type, version);

-- PostgreSQL特有的JSON验证约束
ALTER TABLE dashboard_templates 
ADD CONSTRAINT IF NOT EXISTS chk_valid_json 
CHECK (jsonb_typeof(data_json) = 'array' OR jsonb_typeof(data_json) = 'object');