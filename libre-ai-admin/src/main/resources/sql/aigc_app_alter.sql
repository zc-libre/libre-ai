-- 为 aigc_app 表添加系统提示词和用户提示词模板字段

-- 添加系统提示词字段
ALTER TABLE aigc_app ADD COLUMN IF NOT EXISTS system_prompt TEXT;
COMMENT ON COLUMN aigc_app.system_prompt IS '系统提示词（定义AI的角色和行为）';

-- 添加用户提示词模板字段
ALTER TABLE aigc_app ADD COLUMN IF NOT EXISTS user_prompt_template TEXT;
COMMENT ON COLUMN aigc_app.user_prompt_template IS '用户提示词模板（可包含占位符如 {{question}}）';

-- 删除旧的prompt字段（可选，如果确定不再需要）
-- ALTER TABLE aigc_app DROP COLUMN IF EXISTS prompt;