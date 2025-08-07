-- 创建数据库
CREATE DATABASE langchat;

-- 使用数据库（在 PostgreSQL 中不需要 USE 语句，连接时指定数据库）

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- 禁用外键检查（PostgreSQL 中不需要）

-- ----------------------------
-- Table structure for aigc_app
-- ----------------------------
DROP TABLE IF EXISTS aigc_app;
CREATE TABLE aigc_app (
                            id varchar(50) NOT NULL,
                            model_id varchar(50),
                            knowledge_ids varchar(500),
                            cover varchar(255),
                            name varchar(50),
                            prompt text,
                            des varchar(255),
                            save_time timestamp,
                            create_time timestamp,
                            PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_app IS '提示词表';
COMMENT ON COLUMN aigc_app.id IS '主键';
COMMENT ON COLUMN aigc_app.model_id IS '关联模型';
COMMENT ON COLUMN aigc_app.knowledge_ids IS '关联知识库';
COMMENT ON COLUMN aigc_app.cover IS '封面';
COMMENT ON COLUMN aigc_app.name IS '名称';
COMMENT ON COLUMN aigc_app.prompt IS '提示词';
COMMENT ON COLUMN aigc_app.des IS '描述';
COMMENT ON COLUMN aigc_app.save_time IS '保存时间';
COMMENT ON COLUMN aigc_app.create_time IS '创建时间';

-- ----------------------------
-- Records of aigc_app
-- ----------------------------
BEGIN;
INSERT INTO aigc_app (id, model_id, knowledge_ids, cover, name, prompt, des, save_time, create_time) VALUES ('e16a582b47d3041cf14074d5451dff7a', '0c21c2f8ebd3aa3757ef1bae81154cc4', '["393704ac13f67fde5da674ddd0742b03"]', 'http://cdn.tycoding.cn/tycoding.jpg', 'LangChat官方应用', '你是一个专业的文档分析师，你擅长从文档中提取关键内容并总结分析含义，下面你需要根据用户的问题做出解答。

## 限制
不要回答和文档无关的内容', '快速解答LangChat项目相关的内容，LangChat官方助手', '2024-08-10 11:39:41', '2024-08-04 17:49:24');
COMMIT;

-- ----------------------------
-- Table structure for aigc_app_api
-- ----------------------------
DROP TABLE IF EXISTS aigc_app_api;
CREATE TABLE aigc_app_api (
                                id varchar(50) NOT NULL,
                                app_id varchar(50),
                                channel varchar(50),
                                api_key varchar(50),
                                create_time timestamp,
                                PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_app_api IS '应用';
COMMENT ON COLUMN aigc_app_api.id IS '主键';
COMMENT ON COLUMN aigc_app_api.app_id IS '应用ID';
COMMENT ON COLUMN aigc_app_api.channel IS '应用渠道';
COMMENT ON COLUMN aigc_app_api.api_key IS 'Key';
COMMENT ON COLUMN aigc_app_api.create_time IS '创建时间';

-- ----------------------------
-- Table structure for aigc_conversation
-- ----------------------------
DROP TABLE IF EXISTS aigc_conversation;
CREATE TABLE aigc_conversation (
                                     id varchar(50) NOT NULL,
                                     user_id varchar(50),
                                     prompt_id varchar(50),
                                     title varchar(100),
                                     create_time timestamp,
                                     PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_conversation IS '对话窗口表';
COMMENT ON COLUMN aigc_conversation.id IS '主键';
COMMENT ON COLUMN aigc_conversation.user_id IS '用户ID';
COMMENT ON COLUMN aigc_conversation.prompt_id IS '提示词ID';
COMMENT ON COLUMN aigc_conversation.title IS '标题';
COMMENT ON COLUMN aigc_conversation.create_time IS '创建时间';

-- ----------------------------
-- Table structure for aigc_docs
-- ----------------------------
DROP TABLE IF EXISTS aigc_docs;
CREATE TABLE aigc_docs (
                             id varchar(50) NOT NULL,
                             knowledge_id varchar(50) NOT NULL,
                             name varchar(255),
                             type varchar(50),
                             url varchar(255),
                             origin varchar(50),
                             content text,
                             size int,
                             slice_num int,
                             slice_status smallint,
                             create_time timestamp,
                             PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_docs IS '文档表';
COMMENT ON COLUMN aigc_docs.id IS '主键';
COMMENT ON COLUMN aigc_docs.knowledge_id IS '知识库ID';
COMMENT ON COLUMN aigc_docs.name IS '名称';
COMMENT ON COLUMN aigc_docs.type IS '类型';
COMMENT ON COLUMN aigc_docs.url IS '链接';
COMMENT ON COLUMN aigc_docs.origin IS '来源';
COMMENT ON COLUMN aigc_docs.content IS '内容或链接';
COMMENT ON COLUMN aigc_docs.size IS '文件大小';
COMMENT ON COLUMN aigc_docs.slice_num IS '切片数量';
COMMENT ON COLUMN aigc_docs.slice_status IS '切片状态';
COMMENT ON COLUMN aigc_docs.create_time IS '创建时间';

-- ----------------------------
-- Records of aigc_docs
-- ----------------------------
BEGIN;
INSERT INTO aigc_docs (id, knowledge_id, name, type, url, origin, content, size, slice_num, slice_status, create_time) VALUES ('51ae6d7356eec12b30dceb7975846c4e', '393704ac13f67fde5da674ddd0742b03', 'story-about-happy-carrot.pdf', 'UPLOAD', NULL, NULL, NULL, 35359, NULL, 0, '2024-08-08 17:02:41');
INSERT INTO aigc_docs (id, knowledge_id, name, type, url, origin, content, size, slice_num, slice_status, create_time) VALUES ('8933fc0e6b449a153adc1789a4e1781c', '393704ac13f67fde5da674ddd0742b03', 'guide1', 'INPUT', NULL, NULL, 'LangChat 是一个基于Java生态的企业AI知识库和大模型应用解决方案，帮助企业快速搭建AI大模型应用。 同时，LangChat也集成了RBAC权限体系，为企业提供开箱即用的AI大模型产品解决方案。

LangChat 使用Java生态，前后端分离，并采用最新的技术栈开发。后端基于SpringBoot3，前端基于Vue3。 LangChat不仅为企业提供AI领域的产品解决方案，也是一个完整的Java企业级应用案例。这个系统带你全面了解SpringBoot3和Vue3的前后端开发流程、业务模块化，以及AI应用集成方案。 无论是企业开发，还是个人学习，LangChat都为你提供丰富的学习案例', NULL, 1, 1, '2024-08-04 18:18:46');
INSERT INTO aigc_docs (id, knowledge_id, name, type, url, origin, content, size, slice_num, slice_status, create_time) VALUES ('ec0c960461a615bb7c7648d7ee5801b5', '393704ac13f67fde5da674ddd0742b03', 'story-about-happy-carrot.pdf', 'UPLOAD', 'http://127.0.0.1/langchat/2024080866b4b069cdb262aeea8da409.pdf', NULL, NULL, 35359, 37, 1, '2024-08-08 19:47:54');
INSERT INTO aigc_docs (id, knowledge_id, name, type, url, origin, content, size, slice_num, slice_status, create_time) VALUES ('f4a465ea6bfc25c34707f1e132356192', '393704ac13f67fde5da674ddd0742b03', 'story-about-happy-carrot.pdf', 'UPLOAD', NULL, NULL, NULL, 35359, NULL, 0, '2024-08-06 22:57:32');
COMMIT;

-- ----------------------------
-- Table structure for aigc_docs_slice
-- ----------------------------
DROP TABLE IF EXISTS aigc_docs_slice;
CREATE TABLE aigc_docs_slice (
                                   id varchar(50) NOT NULL,
                                   vector_id varchar(100) NOT NULL,
                                   docs_id varchar(50) NOT NULL,
                                   knowledge_id varchar(50) NOT NULL,
                                   name varchar(255),
                                   content text,
                                   word_num int,
                                   status smallint,
                                   create_time timestamp,
                                   PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_docs_slice IS '文档切片表';
COMMENT ON COLUMN aigc_docs_slice.id IS '主键';
COMMENT ON COLUMN aigc_docs_slice.vector_id IS '向量库的ID';
COMMENT ON COLUMN aigc_docs_slice.docs_id IS '文档ID';
COMMENT ON COLUMN aigc_docs_slice.knowledge_id IS '知识库ID';
COMMENT ON COLUMN aigc_docs_slice.name IS '文档名称';
COMMENT ON COLUMN aigc_docs_slice.content IS '切片内容';
COMMENT ON COLUMN aigc_docs_slice.word_num IS '字符数';
COMMENT ON COLUMN aigc_docs_slice.status IS '状态';
COMMENT ON COLUMN aigc_docs_slice.create_time IS '创建时间';

-- ----------------------------
-- Table structure for aigc_knowledge
-- ----------------------------
DROP TABLE IF EXISTS aigc_knowledge;
CREATE TABLE aigc_knowledge (
                                  id varchar(50) NOT NULL,
                                  user_id varchar(50),
                                  embed_store_id varchar(50),
                                  embed_model_id varchar(50),
                                  name varchar(50),
                                  des varchar(255),
                                  cover varchar(255),
                                  create_time varchar(50),
                                  PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_knowledge IS '知识库表';
COMMENT ON COLUMN aigc_knowledge.id IS '主键';
COMMENT ON COLUMN aigc_knowledge.user_id IS '用户ID';
COMMENT ON COLUMN aigc_knowledge.embed_store_id IS '向量数据库ID';
COMMENT ON COLUMN aigc_knowledge.embed_model_id IS '向量模型ID';
COMMENT ON COLUMN aigc_knowledge.name IS '知识库名称';
COMMENT ON COLUMN aigc_knowledge.des IS '描述';
COMMENT ON COLUMN aigc_knowledge.cover IS '封面';
COMMENT ON COLUMN aigc_knowledge.create_time IS '创建时间';

-- ----------------------------
-- Records of aigc_knowledge
-- ----------------------------
BEGIN;
INSERT INTO aigc_knowledge (id, user_id, embed_store_id, embed_model_id, name, des, cover, create_time) VALUES ('393704ac13f67fde5da674ddd0742b03', NULL, '5d57795705faccdf0ea7095a63c5e463', '1f0525bcf8721689f6a81851e5a0068b', 'LangChat文档', 'LangChat官方文档', NULL, '1722766331165');
COMMIT;

-- ----------------------------
-- Table structure for aigc_message
-- ----------------------------
DROP TABLE IF EXISTS aigc_message;
CREATE TABLE aigc_message (
                                id varchar(50) NOT NULL,
                                user_id varchar(50),
                                conversation_id varchar(50),
                                chat_id varchar(50),
                                username varchar(100),
                                ip varchar(50),
                                role varchar(10),
                                model varchar(50),
                                message text,
                                tokens int,
                                prompt_tokens int,
                                create_time timestamp,
                                PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_message IS '对话消息表';
COMMENT ON COLUMN aigc_message.id IS '主键';
COMMENT ON COLUMN aigc_message.user_id IS '用户ID';
COMMENT ON COLUMN aigc_message.conversation_id IS '会话ID';
COMMENT ON COLUMN aigc_message.chat_id IS '消息的ID';
COMMENT ON COLUMN aigc_message.username IS '用户名';
COMMENT ON COLUMN aigc_message.ip IS 'IP地址';
COMMENT ON COLUMN aigc_message.role IS '角色，user和assistant';
COMMENT ON COLUMN aigc_message.model IS '模型名称';
COMMENT ON COLUMN aigc_message.message IS '消息内容';
COMMENT ON COLUMN aigc_message.tokens IS '总token数';
COMMENT ON COLUMN aigc_message.prompt_tokens IS '提示token数';
COMMENT ON COLUMN aigc_message.create_time IS '创建时间';

CREATE INDEX idx_conversation_id ON aigc_message (conversation_id);
CREATE INDEX idx_role ON aigc_message (role);

-- ----------------------------
-- Table structure for aigc_model
-- ----------------------------
DROP TABLE IF EXISTS aigc_model;
CREATE TABLE aigc_model (
                              id varchar(50) NOT NULL,
                              type varchar(100),
                              model varchar(100),
                              provider varchar(100),
                              name varchar(100),
                              response_limit int,
                              temperature double precision,
                              top_p double precision,
                              api_key varchar(100),
                              base_url varchar(100),
                              secret_key varchar(100),
                              endpoint varchar(100),
                              azure_deployment_name varchar(100),
                              gemini_project varchar(100),
                              gemini_location varchar(100),
                              image_size varchar(50),
                              image_quality varchar(50),
                              image_style varchar(50),
                              dimension int,
                              PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_model IS 'LLM模型配置表';
COMMENT ON COLUMN aigc_model.id IS '主键';
COMMENT ON COLUMN aigc_model.type IS '类型: CHAT、Embedding、Image';
COMMENT ON COLUMN aigc_model.model IS '模型名称';
COMMENT ON COLUMN aigc_model.provider IS '供应商';
COMMENT ON COLUMN aigc_model.name IS '别名';
COMMENT ON COLUMN aigc_model.response_limit IS '响应长度';
COMMENT ON COLUMN aigc_model.temperature IS '温度';
COMMENT ON COLUMN aigc_model.top_p IS 'top_p';
COMMENT ON COLUMN aigc_model.api_key IS 'API密钥';
COMMENT ON COLUMN aigc_model.base_url IS '基础URL';
COMMENT ON COLUMN aigc_model.secret_key IS '密钥';
COMMENT ON COLUMN aigc_model.endpoint IS '端点';
COMMENT ON COLUMN aigc_model.azure_deployment_name IS 'azure模型参数';
COMMENT ON COLUMN aigc_model.gemini_project IS 'gemini模型参数';
COMMENT ON COLUMN aigc_model.gemini_location IS 'gemini模型参数';
COMMENT ON COLUMN aigc_model.image_size IS '图片大小';
COMMENT ON COLUMN aigc_model.image_quality IS '图片质量';
COMMENT ON COLUMN aigc_model.image_style IS '图片风格';
COMMENT ON COLUMN aigc_model.dimension IS '向量维数';

-- ----------------------------
-- Records of aigc_model
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for aigc_oss
-- ----------------------------
DROP TABLE IF EXISTS aigc_oss;
CREATE TABLE aigc_oss (
                            id varchar(50) NOT NULL,
                            user_id varchar(50),
                            oss_id varchar(50),
                            original_filename varchar(50),
                            filename varchar(50),
                            url varchar(100),
                            base_path varchar(100),
                            path varchar(100),
                            size int,
                            ext varchar(50),
                            content_type varchar(100),
                            platform varchar(50),
                            create_time timestamp,
                            PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_oss IS '资源文件表';
COMMENT ON COLUMN aigc_oss.id IS '主键';
COMMENT ON COLUMN aigc_oss.user_id IS '用户ID';
COMMENT ON COLUMN aigc_oss.oss_id IS 'OSS ID';
COMMENT ON COLUMN aigc_oss.original_filename IS '原始文件名称';
COMMENT ON COLUMN aigc_oss.filename IS '文件存储名称';
COMMENT ON COLUMN aigc_oss.url IS '文件地址';
COMMENT ON COLUMN aigc_oss.base_path IS '桶路径';
COMMENT ON COLUMN aigc_oss.path IS '文件的绝对路径';
COMMENT ON COLUMN aigc_oss.size IS '文件大小';
COMMENT ON COLUMN aigc_oss.ext IS '文件后缀';
COMMENT ON COLUMN aigc_oss.content_type IS '文件头';
COMMENT ON COLUMN aigc_oss.platform IS '平台';
COMMENT ON COLUMN aigc_oss.create_time IS '创建时间';

-- ----------------------------
-- Records of aigc_oss
-- ----------------------------
BEGIN;
INSERT INTO aigc_oss (id, user_id, oss_id, original_filename, filename, url, base_path, path, size, ext, content_type, platform, create_time) VALUES ('496a1c3a6798e6b9f52e071d533753d1', '91b4524a46a949601e7f3b004ed76034', NULL, '36946717.JPEG', '66b6df5ecdb26cd406afc109.JPEG', 'http://127.0.0.1/langchat/2024081066b6df5ecdb26cd406afc109.JPEG', 'langchat/', '20240810', 11744, 'JPEG', 'image/jpeg', 'local', '2024-08-10 11:32:47');
INSERT INTO aigc_oss (id, user_id, oss_id, original_filename, filename, url, base_path, path, size, ext, content_type, platform, create_time) VALUES ('55b5b75061c0a229ec0114fc62853a0c', '91b4524a46a949601e7f3b004ed76034', NULL, 'story-about-happy-carrot.pdf', '66b4afeecdb2c038a2624532.pdf', 'http://cdn.tycoding.cn/langchat/2024080866b4afeecdb2c038a2624532.pdf', 'langchat/', '20240808', 35359, 'pdf', 'application/pdf', 'qiniu', '2024-08-08 19:45:51');
INSERT INTO aigc_oss (id, user_id, oss_id, original_filename, filename, url, base_path, path, size, ext, content_type, platform, create_time) VALUES ('6a91df3d44a2fdfe6c8fcc83844757c8', '91b4524a46a949601e7f3b004ed76034', NULL, 'story-about-happy-carrot.pdf', '66b239dbcdb2ff916a0a092c.pdf', 'http://cdn.tycoding.cn/langchat/2024080666b239dbcdb2ff916a0a092c.pdf', 'langchat/', '20240806', 35359, 'pdf', 'application/pdf', 'qiniu', '2024-08-06 22:57:32');
INSERT INTO aigc_oss (id, user_id, oss_id, original_filename, filename, url, base_path, path, size, ext, content_type, platform, create_time) VALUES ('726bc0a42f0753c78672bedb8529c2c4', '91b4524a46a949601e7f3b004ed76034', NULL, 'story-about-happy-carrot.pdf', '66b4b069cdb262aeea8da409.pdf', 'http://127.0.0.1/langchat/2024080866b4b069cdb262aeea8da409.pdf', 'langchat/', '20240808', 35359, 'pdf', 'application/pdf', 'local', '2024-08-08 19:47:54');
INSERT INTO aigc_oss (id, user_id, oss_id, original_filename, filename, url, base_path, path, size, ext, content_type, platform, create_time) VALUES ('7ef543675e89ef3fea19563b667c1454', '91b4524a46a949601e7f3b004ed76034', NULL, 'story-about-happy-carrot.pdf', '66b489b0cdb2a4b1a529719f.pdf', 'http://cdn.tycoding.cn/langchat/2024080866b489b0cdb2a4b1a529719f.pdf', 'langchat/', '20240808', 35359, 'pdf', 'application/pdf', 'qiniu', '2024-08-08 17:02:41');
INSERT INTO aigc_oss (id, user_id, oss_id, original_filename, filename, url, base_path, path, size, ext, content_type, platform, create_time) VALUES ('b572ec6532f03530b8c2b45c93a26141', '91b4524a46a949601e7f3b004ed76034', NULL, '36946717.JPEG', '66b6e0fbcdb220c420fe6bae.JPEG', 'http://127.0.0.1/langchat/2024081066b6e0fbcdb220c420fe6bae.JPEG', 'langchat/', '20240810', 11744, 'JPEG', 'image/jpeg', 'local', '2024-08-10 11:39:40');
INSERT INTO aigc_oss (id, user_id, oss_id, original_filename, filename, url, base_path, path, size, ext, content_type, platform, create_time) VALUES ('cc5bd4fffb8da1296bc87cc40ececb66', '91b4524a46a949601e7f3b004ed76034', NULL, '36946717.JPEG', '66b6e0a2cdb26cd406afc10a.JPEG', 'http://127.0.0.1/langchat/2024081066b6e0a2cdb26cd406afc10a.JPEG', 'langchat/', '20240810', 11744, 'JPEG', 'image/jpeg', 'local', '2024-08-10 11:38:10');
COMMIT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
                            id varchar(50) NOT NULL,
                            parent_id varchar(50),
                            name varchar(20),
                            order_no int,
                            des varchar(100),
                            PRIMARY KEY (id)
);

COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.id IS '部门ID';
COMMENT ON COLUMN sys_dept.parent_id IS '上级部门ID';
COMMENT ON COLUMN sys_dept.name IS '部门名称';
COMMENT ON COLUMN sys_dept.order_no IS '排序';
COMMENT ON COLUMN sys_dept.des IS '描述';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO sys_dept (id, parent_id, name, order_no, des) VALUES ('14b300858a898c6dcfd3dc95dde6df81', 'ece0a14ab891e775ff9f6252731130b7', '事业部', NULL, '事业部');
INSERT INTO sys_dept (id, parent_id, name, order_no, des) VALUES ('16794f488aa3b6f77012749a8160f45e', 'e8017fb290f576f5e1f60be4ab4f166a', '前端研发团队', NULL, '前端研发团队');
INSERT INTO sys_dept (id, parent_id, name, order_no, des) VALUES ('3f7ed841ec5e92ee039fd83bf3fd0ee4', '14b300858a898c6dcfd3dc95dde6df81', '北区事业部', NULL, '北区事业部');
INSERT INTO sys_dept (id, parent_id, name, order_no, des) VALUES ('87388f69e48e53c3771bbd2a56256374', '14b300858a898c6dcfd3dc95dde6df81', '销售团队', NULL, '销售团队');
INSERT INTO sys_dept (id, parent_id, name, order_no, des) VALUES ('da6b0029262feb514ab8c70d7f72c2c7', 'e8017fb290f576f5e1f60be4ab4f166a', '后端研发团队', NULL, '后端研发团队');
INSERT INTO sys_dept (id, parent_id, name, order_no, des) VALUES ('e8017fb290f576f5e1f60be4ab4f166a', 'ece0a14ab891e775ff9f6252731130b7', '产品研发部', NULL, '产品研发部');
INSERT INTO sys_dept (id, parent_id, name, order_no, des) VALUES ('ece0a14ab891e775ff9f6252731130b7', '0', '组织架构', 1, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
                           id varchar(50) NOT NULL,
                           type int,
                           username varchar(20),
                           operation varchar(20),
                           url varchar(255),
                           time bigint,
                           method varchar(100),
                           params varchar(255),
                           ip varchar(20),
                           user_agent varchar(255),
                           create_time timestamp,
                           PRIMARY KEY (id)
);

COMMENT ON TABLE sys_log IS '日志表';
COMMENT ON COLUMN sys_log.id IS '编号';
COMMENT ON COLUMN sys_log.type IS '日志类型，1正常 2异常 ';
COMMENT ON COLUMN sys_log.username IS '操作用户';
COMMENT ON COLUMN sys_log.operation IS '操作描述';
COMMENT ON COLUMN sys_log.url IS '请求URL';
COMMENT ON COLUMN sys_log.time IS '耗时(毫秒)';
COMMENT ON COLUMN sys_log.method IS '操作方法';
COMMENT ON COLUMN sys_log.params IS '操作参数';
COMMENT ON COLUMN sys_log.ip IS 'IP地址';
COMMENT ON COLUMN sys_log.user_agent IS '用户代理';
COMMENT ON COLUMN sys_log.create_time IS '操作时间';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
                            id varchar(50) NOT NULL,
                            name varchar(100),
                            parent_id varchar(50),
                            path varchar(255),
                            perms varchar(255),
                            type varchar(20),
                            order_no int,
                            icon varchar(100),
                            component varchar(255),
                            is_disabled smallint,
                            is_ext smallint,
                            is_keepalive smallint,
                            is_show smallint,
                            PRIMARY KEY (id)
);

COMMENT ON TABLE sys_menu IS '菜单表';
COMMENT ON COLUMN sys_menu.id IS '主键';
COMMENT ON COLUMN sys_menu.name IS '菜单名称';
COMMENT ON COLUMN sys_menu.parent_id IS '父级ID';
COMMENT ON COLUMN sys_menu.path IS '菜单路径';
COMMENT ON COLUMN sys_menu.perms IS '权限标识';
COMMENT ON COLUMN sys_menu.type IS '菜单类型';
COMMENT ON COLUMN sys_menu.order_no IS '排序';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.is_disabled IS '是否禁用';
COMMENT ON COLUMN sys_menu.is_ext IS '是否外链';
COMMENT ON COLUMN sys_menu.is_keepalive IS '是否缓存';
COMMENT ON COLUMN sys_menu.is_show IS '是否显示';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('0597ccbb7b98b2d443bffb3f1785ce1c', '新增知识库', '97a5eac3bfeeabe4013d828b919786f7', NULL, 'aigc:knowledge:add', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('0976afe16e7b328886408f3e117733c1', '新增角色', '6f8aff1f2c458e5add9adb6d284fb451', NULL, 'upms:role:add', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('0f37f45fb15c38de948b17b8a24e431b', '修改菜单', 'b1df787d8af5b728181a4b9acf38da93', NULL, 'upms:menu:update', 'button', 3, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('1440895f54ccae1c1e2706e3dbcf6f5d', '文本向量化', '43563b039d30b990f87af37783115ff4', NULL, 'aigc:embedding:text', 'button', 4, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('2dc3a6e16351901710060fd846ee9f19', '新增菜单', 'b1df787d8af5b728181a4b9acf38da93', NULL, 'upms:menu:add', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('2f5735d125b4537076893a4b4a37a188', '系统管理', '0', 'system', 'system', 'menu', 4, 'SettingsOutline', 'Layout', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('35dcd70c8a4008b554b71bf02ab07b61', '删除聊天记录', 'bdd70f2c1ee068c13bd3288eff07c8e2', NULL, 'chat:messages:clean', 'button', 3, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('374409ab56141b311ccb0f1847dd724a', 'AIGC平台', '0', 'aigc', 'aigc', 'menu', 2, 'CubeOutline', 'Layout', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('3d1700109ece0187ba5e76217cd71995', '删除对话数据', 'f1ad3c056ac91fa5292a99f223155afc', NULL, 'aigc:message:delete', 'button', 2, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('43563b039d30b990f87af37783115ff4', 'AI应用管理', 'a2ccfe694cd91cf159ad35626e4ea202', 'list', 'aigc:app', 'menu', 2, '', '/app/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('4488cb5271b1220647d4a83cfbcb7b15', '文档向量化', '43563b039d30b990f87af37783115ff4', NULL, 'aigc:embedding:docs', 'button', 5, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('510a89f01571d7eaa3b1393c8534ab6f', '删除应用', '43563b039d30b990f87af37783115ff4', NULL, 'aigc:app:delete', 'button', 3, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('5514605bae6ffdad3e4acff3e9e9742c', '新增应用', '43563b039d30b990f87af37783115ff4', NULL, 'aigc:app:add', 'button', 1, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('5ce2349dc38a84cfbf0f5b260b41a2b6', '模型管理', '374409ab56141b311ccb0f1847dd724a', 'model', 'model', 'menu', 0, '', '/aigc/model/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('62beffe9252934b4adeeef3125cab584', '新增模型', '5ce2349dc38a84cfbf0f5b260b41a2b6', NULL, 'aigc:model:add', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('65deeb7aedec5490425ad2572d536ea9', 'Chat权限', '43563b039d30b990f87af37783115ff4', NULL, 'chat:completions', 'button', 6, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('67435b96a82c494b48fc6458b7103d4d', '页面预览', '43563b039d30b990f87af37783115ff4', NULL, 'chat-docs:view', 'button', 1, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('6c27a1ddba0ce10d7e242cb7e568bfc0', '删除模型', '5ce2349dc38a84cfbf0f5b260b41a2b6', NULL, 'aigc:model:delete', 'button', 4, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('6cb25c77d3087d47a26c08d904a442fa', '新增部门', '8fb8756a4587cc4c76401a63ea194568', NULL, 'upms:dept:add', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('6f8aff1f2c458e5add9adb6d284fb451', '角色管理', '7c411c7d41034d6708103c8e0da19ced', 'role', 'role', 'menu', 2, NULL, '/upms/role/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('72215ec9609e546cd56bacf4c29e482d', '修改部门', '8fb8756a4587cc4c76401a63ea194568', NULL, 'upms:dept:update', 'button', 3, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('7b3e324f4470bbd4b8363b379fd3ed3c', '删除部门', '8fb8756a4587cc4c76401a63ea194568', NULL, 'upms:dept:delete', 'button', 4, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('7c411c7d41034d6708103c8e0da19ced', '权限管理', '0', 'upms', 'upms', 'menu', 3, 'KeyOutline', 'Layout', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('7d225cd8d60da156e17e341f86304970', '删除知识库', '97a5eac3bfeeabe4013d828b919786f7', NULL, 'aigc:knowledge:delete', 'button', 4, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('80c1246cff10a470f67b4a58b0fe257e', '修改知识库', '97a5eac3bfeeabe4013d828b919786f7', NULL, 'aigc:knowledge:update', 'button', 3, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('89f1ba9a70e8bf72961f321156361fe6', '删除角色', '6f8aff1f2c458e5add9adb6d284fb451', NULL, 'upms:role:delete', 'button', 4, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('8b2924d753d4e2c1932e1f17e30d0c52', '修改模型', '5ce2349dc38a84cfbf0f5b260b41a2b6', NULL, 'aigc:model:update', 'button', 3, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('8c0eb60ccef367ce7048e5d486aaa3a9', '日志管理', '2f5735d125b4537076893a4b4a37a188', 'log', 'log', 'menu', 1, NULL, '/system/log/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('8fb8756a4587cc4c76401a63ea194568', '部门管理', '7c411c7d41034d6708103c8e0da19ced', 'dept', 'dept', 'menu', 3, NULL, '/upms/dept/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('979631c0fae847a8dd59321b1da7d5e7', '新增用户', 'b29de942eeabc9419185951f57be11f3', NULL, 'upms:user:add', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('97a5eac3bfeeabe4013d828b919786f7', '知识库管理', '374409ab56141b311ccb0f1847dd724a', 'knowledge', 'knowledge', 'menu', 1, 'alert', '/aigc/knowledge/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('9e526a34052ca76cf4f1ec685187e84a', '删除菜单', 'b1df787d8af5b728181a4b9acf38da93', NULL, 'upms:menu:delete', 'button', 4, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('a00ca2926f617715b236c113b2ea14b9', '删除令牌', 'abb7e994494b96797b262cc2c72ea620', NULL, 'system:token:delete', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('a2ccfe694cd91cf159ad35626e4ea202', 'AIGC应用', '0', 'app', 'app', 'menu', 1, 'PaperPlaneOutline', 'Layout', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('a985c800d102da822b59dacc77ee6c9d', '修改用户', 'b29de942eeabc9419185951f57be11f3', NULL, 'upms:user:update', 'button', 3, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('abb7e994494b96797b262cc2c72ea620', '令牌管理', '2f5735d125b4537076893a4b4a37a188', 'token', 'token', 'menu', 2, NULL, '/system/token/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('b1df787d8af5b728181a4b9acf38da93', '菜单管理', '7c411c7d41034d6708103c8e0da19ced', 'menu', 'menu', 'menu', 4, NULL, '/upms/menu/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('b29de942eeabc9419185951f57be11f3', '用户管理', '7c411c7d41034d6708103c8e0da19ced', 'user', 'user', 'menu', 1, NULL, '/upms/user/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('b3331acdd06227088f3fb4b92b8b0365', '删除日志', '8c0eb60ccef367ce7048e5d486aaa3a9', NULL, 'system:log:delete', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('bdd70f2c1ee068c13bd3288eff07c8e2', 'AI聊天助手', 'a2ccfe694cd91cf159ad35626e4ea202', 'chat', 'aigc:chat', 'menu', 1, '', '/chat/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('c212381ae7a2333416a18e486f044777', '账单统计', '374409ab56141b311ccb0f1847dd724a', 'order', 'order', 'menu', 5, NULL, '/aigc/order/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('cac8d8f2f35bd872dcc3652add9bbd08', '修改角色', '6f8aff1f2c458e5add9adb6d284fb451', NULL, 'upms:role:update', 'button', 3, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('d99e460bd02a18eaf15206b09f709bfb', '修改应用', '43563b039d30b990f87af37783115ff4', NULL, 'aigc:app:update', 'button', 2, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('f1ad3c056ac91fa5292a99f223155afc', '对话数据', '374409ab56141b311ccb0f1847dd724a', 'message', 'message', 'menu', 4, NULL, '/aigc/message/index', 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('f5d6cbc1e97e2a87149598f86c1bdbbe', '删除用户', 'b29de942eeabc9419185951f57be11f3', NULL, 'upms:user:delete', 'button', 4, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('fadaa37669c31316d8addac152f1f0ff', '聊天权限', 'bdd70f2c1ee068c13bd3288eff07c8e2', NULL, 'chat:completions', 'button', 2, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('ffca98852cd6faea6b20e2a339578f13', '删除令牌', 'abb7e994494b96797b262cc2c72ea620', NULL, 'system:token:delete', 'button', 2, NULL, NULL, 0, 0, 1, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('f5031ca9ca645316c6eb94f4ea8684f8', '修改文档', '97a5eac3bfeeabe4013d828b919786f7', null, 'aigc:docs:update', 'button', 11, null, null, 0, 0, 1, null);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('51ed9b1f27acc4695667821eac5f35cb', '删除文档', '97a5eac3bfeeabe4013d828b919786f7', null, 'aigc:docs:delete', 'button', 12, null, null, 0, 0, 1, null);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('03917f40dfafba8c7ecb2b8843522a9e', '新增文档', '97a5eac3bfeeabe4013d828b919786f7', null, 'aigc:docs:add', 'button', 10, null, null, 0, 0, 1, null);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('1c1fa2c50ff306144a0ea2528dcec96b', '重置密码', 'b29de942eeabc9419185951f57be11f3', null, 'upms:user:reset', 'button', 5, null, null, 0, 0, 1, null);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('0adfa2c3c4d278aedd88019236c1425e', '向量库管理', '374409ab56141b311ccb0f1847dd724a', 'aigc/embed-store', 'embed-store', 'menu', 1, '', '/aigc/embed-store/index', 0, 0, 0, 1);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('64a1109e89e060bd7018806c62c8e7d3', '修改向量库', '0adfa2c3c4d278aedd88019236c1425e', NULL, 'aigc:embed-store:update', 'button', 2, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('757e0f3fb5e153c15f3355a97f731f1e', '删除向量库', '0adfa2c3c4d278aedd88019236c1425e', NULL, 'aigc:embed-store:delete', 'button', 3, NULL, NULL, 0, 0, 0, NULL);
INSERT INTO sys_menu (id, name, parent_id, path, perms, type, order_no, icon, component, is_disabled, is_ext, is_keepalive, is_show) VALUES ('af8e11cdd57a935bbcf36f8e53cc889f', '新增向量库', '0adfa2c3c4d278aedd88019236c1425e', NULL, 'aigc:embed-store:add', 'button', 1, NULL, NULL, 0, 0, 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
                            id varchar(50) NOT NULL,
                            name varchar(50) NOT NULL,
                            code varchar(50),
                            des varchar(100),
                            PRIMARY KEY (id)
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '主键';
COMMENT ON COLUMN sys_role.name IS '角色名称';
COMMENT ON COLUMN sys_role.code IS '角色别名';
COMMENT ON COLUMN sys_role.des IS '描述';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO sys_role (id, name, code, des) VALUES ('2827e950043adf67b7fe10306d3e94e4', '超级管理员角色', 'administrator', '超级管理员管理员，不受权限控制，不可编辑');
INSERT INTO sys_role (id, name, code, des) VALUES ('bbe1863be68ad07347b1dee0e358f18a', '默认人员角色', 'default_env', '后台新用户注册角色，不可删除');
INSERT INTO sys_role (id, name, code, des) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '演示环境角色', 'demo_env', '演示环境使用角色，拥有页面预览权限，没有操作权限');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
                                 role_id varchar(50) NOT NULL,
                                 menu_id varchar(50) NOT NULL,
                                 PRIMARY KEY (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色资源关联表';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单/按钮ID';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '0825f18b3860f8c01a9b0d8221280e3b');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '2f5735d125b4537076893a4b4a37a188');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '374409ab56141b311ccb0f1847dd724a');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '43563b039d30b990f87af37783115ff4');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '5ce2349dc38a84cfbf0f5b260b41a2b6');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '6f8aff1f2c458e5add9adb6d284fb451');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '7c411c7d41034d6708103c8e0da19ced');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '8c0eb60ccef367ce7048e5d486aaa3a9');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '8fb8756a4587cc4c76401a63ea194568');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '97a5eac3bfeeabe4013d828b919786f7');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', 'a2ccfe694cd91cf159ad35626e4ea202');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', 'abb7e994494b96797b262cc2c72ea620');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', 'b1df787d8af5b728181a4b9acf38da93');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', 'b29de942eeabc9419185951f57be11f3');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', 'bdd70f2c1ee068c13bd3288eff07c8e2');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', 'c212381ae7a2333416a18e486f044777');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', 'f1ad3c056ac91fa5292a99f223155afc');
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('d0d0cab7c147d865d35e70fc62f2f19e', '0adfa2c3c4d278aedd88019236c1425e');
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
                            id varchar(50) NOT NULL,
                            username varchar(50) NOT NULL,
                            password varchar(100),
                            real_name varchar(255),
                            sex varchar(10),
                            phone varchar(20),
                            email varchar(100),
                            dept_id varchar(50),
                            avatar varchar(100),
                            status smallint DEFAULT 0,
                            create_time timestamp,
                            PRIMARY KEY (id)
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '用户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.real_name IS '真实姓名';
COMMENT ON COLUMN sys_user.sex IS '性别';
COMMENT ON COLUMN sys_user.phone IS '手机';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user.avatar IS '头像';
COMMENT ON COLUMN sys_user.status IS '状态 0锁定 1有效';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO sys_user (id, username, password, real_name, sex, phone, email, dept_id, avatar, status, create_time) VALUES ('827450c4a39b3c4c14fdfb06f454bfb3', 'langchat', 'U3lnYOIEGN38KKy0h3KUSA==', '演示环境账号', '男', '19809587831', 'langchat@outlook.com', '14b300858a898c6dcfd3dc95dde6df81', NULL, 1, '2024-08-04 13:55:35');
INSERT INTO sys_user (id, username, password, real_name, sex, phone, email, dept_id, avatar, status, create_time) VALUES ('91b4524a46a949601e7f3b004ed76034', 'administrator', 'U3lnYOIEGN38KKy0h3KUSA==', '超级管理员', '男', '19809587831', 'langchat@outlook.com', NULL, NULL, 0, '2024-08-04 13:55:35');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
                                 user_id varchar(50) NOT NULL,
                                 role_id varchar(50) NOT NULL,
                                 PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO sys_user_role (user_id, role_id) VALUES ('827450c4a39b3c4c14fdfb06f454bfb3', 'd0d0cab7c147d865d35e70fc62f2f19e');
INSERT INTO sys_user_role (user_id, role_id) VALUES ('91b4524a46a949601e7f3b004ed76034', '2827e950043adf67b7fe10306d3e94e4');
COMMIT;

-- ----------------------------
-- Table structure for aigc_embed_store
-- ----------------------------
DROP TABLE IF EXISTS aigc_embed_store;
CREATE TABLE aigc_embed_store (
                                    id varchar(50) NOT NULL,
                                    name varchar(100),
                                    provider varchar(100),
                                    host varchar(100),
                                    port int,
                                    username varchar(100),
                                    password varchar(100),
                                    database_name varchar(100),
                                    table_name varchar(100),
                                    dimension int,
                                    PRIMARY KEY (id)
);

COMMENT ON TABLE aigc_embed_store IS 'Embedding向量数据库配置表';
COMMENT ON COLUMN aigc_embed_store.id IS '主键';
COMMENT ON COLUMN aigc_embed_store.name IS '别名';
COMMENT ON COLUMN aigc_embed_store.provider IS '供应商';
COMMENT ON COLUMN aigc_embed_store.host IS '地址';
COMMENT ON COLUMN aigc_embed_store.port IS '端口';
COMMENT ON COLUMN aigc_embed_store.username IS '用户名';
COMMENT ON COLUMN aigc_embed_store.password IS '密码';
COMMENT ON COLUMN aigc_embed_store.database_name IS '数据库名称';
COMMENT ON COLUMN aigc_embed_store.table_name IS '表名称';
COMMENT ON COLUMN aigc_embed_store.dimension IS '向量维数';

-- 启用外键检查（PostgreSQL 中不需要）