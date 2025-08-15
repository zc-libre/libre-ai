# 聊天界面迁移计划

## 项目概述

本文档规划了将 `chatgpt-web` 项目的聊天界面迁移到当前 `libre-ai` 项目的详细实施方案。目标是保留 chatgpt-web 的丰富功能和优秀用户体验，同时保持与当前后端接口的兼容性。
chatgpt-web项目路径/Users/libre/code/vue/chatgpt-web
## 项目对比分析

### chatgpt-web 项目优势
- **功能丰富**: 支持聊天室管理、上下文控制、搜索功能、思考模式、模型切换
- **UI精致**: 深色模式、移动端适配、消息渲染完善
- **交互先进**: 图片上传、聊天导出、提示词管理、响应历史
- **技术栈**: Vue 3 + TypeScript + Naive UI + Pinia
- **SSE支持**: 完善的流式响应处理机制

### 当前 libre-ai 项目特点
- **后端强大**: Spring Boot 3.5 + LangChain4j + PostgreSQL
- **架构清晰**: 模块化设计，支持 RAG 和多模型
- **界面简洁**: Element Plus + Vue 3 + TypeScript
- **功能专业**: AI 应用平台，仪表板生成

## 实施计划

### 第一阶段：核心组件迁移 (Week 1-2)

#### 1.1 主聊天界面替换
**目标文件**: `libre-ai-ui/src/views/chat/index.vue`

**迁移内容**:
- 复制 chatgpt-web 的主体布局结构
- 将 Naive UI 组件替换为 Element Plus
- 保留原有的头部标题和重载功能

**组件映射表**:
```javascript
// Naive UI → Element Plus
NButton → el-button
NInput → el-input  
NCard → el-card
NDropdown → el-dropdown
NModal → el-dialog
NSpin → el-loading
NSlider → el-slider
NAutoComplete → el-autocomplete
NSelect → el-select
NUpload → el-upload
```

#### 1.2 消息组件重构
**目标文件**: `libre-ai-ui/src/views/chat/message/Message.vue`

**新增功能**:
- Markdown 渲染支持
- 代码语法高亮
- 数学公式渲染
- 推理过程展示 (Reasoning)
- 搜索结果展示 (Search)
- 消息操作菜单 (复制、删除、重新生成)
- 响应历史记录

#### 1.3 聊天室侧边栏
**新建文件**: `libre-ai-ui/src/views/chat/components/Sidebar/`

**功能实现**:
- 聊天室列表管理
- 新增/删除/重命名聊天室
- 聊天室切换
- 移动端适配

### 第二阶段：状态管理重构 (Week 2-3)

#### 2.1 创建新的 Chat Store
**新建文件**: `libre-ai-ui/src/views/chat/store/useChatStore.ts`

**状态管理功能**:
```typescript
interface ChatState {
  // 聊天室管理
  chatRooms: ChatRoom[]
  activeChatRoom: string | null
  
  // 消息管理  
  messages: Record<string, Message[]>
  loading: boolean
  
  // 配置管理
  settings: {
    model: string
    temperature: number
    maxTokens: number
    systemPrompt: string
    usingContext: boolean
    maxContextCount: number
    searchEnabled: boolean
    thinkEnabled: boolean
  }
}
```

#### 2.2 API 接口适配层
**新建文件**: `libre-ai-ui/src/api/chat/`

**接口映射**:
```typescript
// 原 chatgpt-web 接口 → 新 libre-ai 接口
fetchChatAPIProcessSSE() → /api/aigc/chat/completions
fetchChatRooms() → 本地存储 + /api/aigc/app/info  
fetchChatHistory() → /api/aigc/chat/messages/{conversationId}
fetchChatConfig() → /api/aigc/app/info
```

### 第三阶段：高级功能集成 (Week 3-4)

#### 3.1 SSE 流式响应优化
**文件**: `libre-ai-ui/src/utils/sse.ts`

**功能特性**:
- 支持增量文本更新 (onDelta)
- 搜索查询展示 (onSearchQuery)  
- 搜索结果展示 (onSearchResults)
- 推理过程展示 (onReasoning)
- 完整消息处理 (onMessage)
- 错误处理和重试机制

#### 3.2 高级交互功能
**功能清单**:
- [ ] 图片上传支持
- [ ] 聊天导出功能 (PNG/PDF)
- [ ] 提示词模板管理
- [ ] 模型切换界面
- [ ] 上下文长度控制
- [ ] 消息重新生成
- [ ] 响应历史记录
- [ ] 快捷键支持

#### 3.3 UI/UX 优化
**优化项目**:
- 深色模式支持
- 移动端响应式适配
- 消息动画效果
- 加载状态优化
- 错误提示优化

### 第四阶段：后端接口增强 (Week 4-5)

#### 4.1 新增后端接口 (可选)
**Java 控制器扩展**:

```java
@RestController
@RequestMapping("/api/aigc/chat")
public class ChatRoomController {
    
    // 聊天室管理
    @GetMapping("/rooms")
    public R<List<ChatRoom>> getChatRooms();
    
    @PostMapping("/rooms")  
    public R<ChatRoom> createChatRoom(@RequestBody CreateRoomRequest request);
    
    @PutMapping("/rooms/{roomId}")
    public R<Void> updateChatRoom(@PathVariable String roomId, @RequestBody UpdateRoomRequest request);
    
    @DeleteMapping("/rooms/{roomId}")
    public R<Void> deleteChatRoom(@PathVariable String roomId);
    
    // 配置管理
    @PutMapping("/rooms/{roomId}/settings")
    public R<Void> updateRoomSettings(@PathVariable String roomId, @RequestBody RoomSettings settings);
}
```

#### 4.2 数据库表设计
**新增表结构** (可选):

```sql
-- 聊天室表
CREATE TABLE aigc_chat_room (
    id VARCHAR(32) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    model_id VARCHAR(32),
    system_prompt TEXT,
    using_context BOOLEAN DEFAULT TRUE,
    max_context_count INTEGER DEFAULT 10,
    search_enabled BOOLEAN DEFAULT FALSE,
    think_enabled BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 消息历史表扩展
ALTER TABLE aigc_message ADD COLUMN room_id VARCHAR(32);
ALTER TABLE aigc_message ADD COLUMN response_count INTEGER DEFAULT 1;
ALTER TABLE aigc_message ADD COLUMN reasoning TEXT;
ALTER TABLE aigc_message ADD COLUMN search_query VARCHAR(500);
ALTER TABLE aigc_message ADD COLUMN search_results JSONB;
```

## 实施时间线

| 阶段 | 时间 | 主要任务 | 交付物 |
|------|------|----------|---------|
| 第一阶段 | Week 1-2 | 核心组件迁移 | 新的聊天界面基础版本 |
| 第二阶段 | Week 2-3 | 状态管理重构 | 完整的前端状态管理 |
| 第三阶段 | Week 3-4 | 高级功能集成 | 功能完整的聊天界面 |
| 第四阶段 | Week 4-5 | 后端接口增强 | 完整的前后端集成 |

## 技术难点和解决方案

### 难点1: 组件库差异
**问题**: Naive UI 和 Element Plus 在 API 和样式上存在差异

**解决方案**: 
- 创建组件适配器层
- 统一样式变量管理
- 分步骤替换，确保每个组件都经过测试

### 难点2: SSE 流式响应适配
**问题**: 两个项目的 SSE 数据格式不完全一致

**解决方案**:
- 创建 SSE 数据解析中间件
- 统一事件处理接口
- 向后兼容现有接口格式

### 难点3: 状态管理迁移
**问题**: chatgpt-web 使用的 store 结构与当前项目不同

**解决方案**:
- 渐进式迁移策略
- 保持现有 store 接口不变
- 新功能使用新的 store 模块

## 测试策略

### 单元测试
- [ ] 消息组件渲染测试
- [ ] Store 状态管理测试  
- [ ] API 接口适配测试
- [ ] SSE 数据解析测试

### 集成测试
- [ ] 聊天流程端到端测试
- [ ] 多聊天室切换测试
- [ ] 移动端适配测试
- [ ] 深色模式测试

### 性能测试
- [ ] 大量消息渲染性能
- [ ] SSE 连接稳定性
- [ ] 内存泄漏检测

## 风险评估

| 风险 | 影响程度 | 概率 | 缓解措施 |
|------|----------|------|----------|
| 组件迁移不兼容 | 高 | 中 | 充分测试，分步实施 |
| 后端接口调整工作量大 | 中 | 低 | 优先使用现有接口，减少后端改动 |
| 用户体验临时下降 | 中 | 中 | 功能开关，灰度发布 |
| 项目时间延期 | 低 | 中 | 合理安排优先级，核心功能优先 |

## 成功标准

### 功能完整性
- [x] 基础聊天功能正常
- [ ] 聊天室管理功能完整
- [ ] 高级功能 (搜索、思考模式) 可用
- [ ] 移动端适配良好

### 性能指标
- 消息渲染速度 < 100ms
- SSE 连接稳定性 > 99%
- 首屏加载时间 < 2s
- 内存使用增长 < 20%

### 用户体验
- 界面响应流畅
- 操作逻辑直观
- 错误提示友好
- 功能文档完整

## 后续维护

### 代码质量
- 遵循项目现有代码规范
- 添加完整的 TypeScript 类型定义
- 编写充分的单元测试
- 更新相关文档

### 功能扩展
- 预留插件化架构
- 支持自定义主题
- 多语言国际化
- 无障碍访问支持

---

**项目负责人**: 开发团队  
**文档版本**: v1.0  
**最后更新**: 2025-01-XX  
**审核状态**: 待审核