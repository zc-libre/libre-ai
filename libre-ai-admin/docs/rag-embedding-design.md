## 设计文档：RAG 文档切分与向量化（对标 Dify 的 ETL 流水线）

### 1. 目标与范围
- 引入 Dify 风格的“ETL 式”文档向量化流水线：提取 → 清洗 → 预处理 → 分段（chunking）→ 元数据管理 → 嵌入 → 索引/检索。
- 在现有 `LangChain4j + Apache Tika + EmbeddingStore` 栈上增强工程化能力：可配置化、幂等增量、批量嵌入、限流与重试、可观测性。
- 兼容现有服务接口与 `EmbeddingStore` 抽象，最小侵入修改 `EmbeddingProvider`、`LangEmbeddingServiceImpl`，新增少量辅助组件。

参考与对标：
- Dify 的 RAG/ETL 设计（提取-预处理-分段-嵌入-索引，强调可配置与治理）[链接](https://www.53ai.com/news/RAG/2025061371856.html?utm_source=openai)
- 实战项目流程（分段-嵌入-Chroma-检索）[LLM-Agent-Resume](https://github.com/liangdabiao/LLM-Agent-Resume)
- Java 生态切分与嵌入能力参考：LangChain4j 文档处理综述、Spring AI `TokenTextSplitter` 思路  
  参考：[LangChain4j](https://www.lovelybigduck.top/archives/java-ai-init?utm_source=openai)、[Spring AI TokenTextSplitter](https://www.ewbang.com/community/article/details/1000235819.html?utm_source=openai)  
- 托管式“切片向量化”参考（可选）：阿里云 OpenSearch [文档](https://help.aliyun.com/zh/open-search/text-vectorization-and-slice-vectorization?utm_source=openai)

### 2. 现状评估（问题与改进点）
- 切分：当前 `DocumentBySentenceSplitter(100, 0)`，无 overlap，易产生割裂或过短片段。
- 预处理：缺少统一清洗/去噪、短片合并、去重。
- 元数据：仅 `knowledgeId/filename`，缺乏溯源与幂等关键字段。
- 可靠性：异常即整体失败，嵌入与索引缺少批处理与重试。
- 治理：缺少指标、日志采样、增量重建能力。

### 3. 总体架构
- 文档提取：`ApacheTikaDocumentParser` → `Document`（统一文本）
- 文本清洗：空白规整、移除页眉页脚/分隔线、合并过短片段、去重
- 分段（Chunking）：句子优先 + token 预算 + overlap；极端场景回退字符级/正则切分
- 元数据增强：chunk 索引、token 数、内容哈希、页码、来源、知识库等
- 向量化与索引：批量嵌入 + 限流/重试；入库前幂等查重；部分失败容忍
- 可观测性：日志采样、指标、追踪；异步任务化（可选）

### 4. 关键组件设计

- 配置类：`RagEmbeddingProperties`
  - `chunkSizeTokens`（默认 256）
  - `overlapTokens`（默认 64）
  - `minChars`（默认 80）
  - `maxChars`（默认 1200）
  - `maxChunksPerDoc`（默认 2000）
  - `embedBatchSize`（默认 32）
  - `retry.maxAttempts`（默认 3）
  - `retry.backoffMs`（默认 200..2000 抖动）
  - `rateLimit.permitsPerSecond`（默认 5，按模型/供应商调优）

- 切分器提供器：改造 `EmbeddingProvider`
  - 注入 `RagEmbeddingProperties` 与 `HuggingFaceTokenCountEstimator`
  - 构建“句子优先 + token 预算 + overlap”的主切分器
  - 剩余超长段回退“字符级切分器”（保证上限与重叠）
  - 通过 `DocumentSplitters` 组合策略（先句子，后字符/正则）

- 清洗与去噪：`TextCleaningService`
  - 规则：统一空白、去分隔线/页眉页脚（正则模板）、合并短片（< `minChars`）、去重复片段（Jaccard/哈希）
  - 输出：清洗后的段落列表供切分器消费

- 元数据增强：`MetadataEnricher`
  - 写入：`chunk_index`、`total_chunks`、`num_tokens`、`content_hash`、`page`、`source_url`、`doc_id`、`knowledge_id`、`filename`
  - `content_hash`: SHA-256（文本+关键元数据），用于幂等与增量

- 幂等与增量：`ChunkDedupService`
  - 会话内去重：`content_hash` Set
  - 跨任务增量（可选）：落地 `doc_chunk_registry`（Postgres，Liquibase），字段：`doc_id`,`content_hash`,`vector_id`,`updated_at`；或由向量库侧支持“metadata 唯一键”

- 嵌入流水线：`EmbeddingPipeline`
  - 批处理 `embedAll`（`embedBatchSize`）+ 限流 + 退避重试
  - 失败片段降级单发；汇总部分成功结果
  - 入库前按 `content_hash` 去重；入库后回写 `vector_id`

- 可观测性：Micrometer + 日志
  - 指标：`rag_embedding_chunks_total`、`rag_embedding_failures_total`、`rag_embedding_duration_seconds`、`rag_embedding_skipped_total`（幂等跳过）
  - 采样日志：每 N 个片段打印 50 字摘要 + 关键元数据
  - 追踪：traceId 贯穿任务

- 异步与治理（可选增强）
  - 使用 `@Async` + `TaskExecutor` 或 MQ/队列，将“文档索引”作业化
  - 管理接口：重建/刷新/中止/进度查询

### 5. 与现有类的关系与修改点

- `EmbeddingProvider`
  - 将静态 `splitter()` 改为实例方法，使用配置生成“句子优先 + overlap”的 `DocumentSplitter`
  - 暴露“fallback splitter”构建（字符级）
  - 保持 `getEmbeddingModel(...)` 与 `getEmbeddingStore(...)` 接口不变

- `LangEmbeddingServiceImpl`
  - `embeddingDocs` 流程：
    1) Tika 提取 → 文本清洗  
    2) 主切分器切分 → 超长段回退字符切分  
    3) 过滤/合并过短片段 → 会话内哈希去重  
    4) 元数据增强（哈希、索引、token 数）  
    5) 批量嵌入 → 限流重试 → 部分失败降级  
    6) 入库前查重（`content_hash`）→ add/addAll → 汇总结果  
    7) 指标与采样日志
  - `embeddingText` 同步增强元数据，复用批处理/重试策略（退化为单条）

### 6. 元数据规范
- 必填：`knowledge_id`,`filename`,`chunk_index`,`total_chunks`,`num_tokens`,`content_hash`
- 可选：`doc_id`,`page`,`source_url`,`created_at`
- 生成规则：
  - `content_hash = sha256(normalized_text + knowledge_id + filename + chunk_index)`
  - `num_tokens` 使用 `HuggingFaceTokenCountEstimator`

### 7. 配置示例（application.yml）
```yaml
rag:
  embedding:
    chunk-size-tokens: 256
    overlap-tokens: 64
    min-chars: 80
    max-chars: 1200
    max-chunks-per-doc: 2000
    embed-batch-size: 32
    retry:
      max-attempts: 3
      backoff-ms: 300
    rate-limit:
      permits-per-second: 5
```

### 8. 错误处理与重试策略
- 提取失败：记录错误并中止该文档；返回空结果与错误描述
- 切分失败：回退字符切分；仍失败则丢弃该段并计数
- 嵌入失败：
  - 批处理失败 → 分段重试（指数退避；最多 `maxAttempts`）
  - 单片失败 → 记录失败清单；最终返回部分成功结果
- 入库失败：重试 1-2 次；仍失败则记录；可供任务补偿接口重试

### 9. 性能与并发
- 批量大小：`embedBatchSize = 32`（按模型 TPS 与延迟调优）
- 并行度：由 `TaskExecutor` 控制（CPU × 2 以内），防止 I/O 放大
- 限流：每秒发起 `permitsPerSecond` 个批次；避免供应商限流
- 估算与目标：10 页 PDF（约 5–8k tokens）→ 150–250 chunks；单文档 2–6s（视模型/网络）

### 10. 可观测性与运维
- 指标：chunks 总数/跳过数/失败数、嵌入总时长/分布、每文档耗时
- 日志：采样打印（文档前 2 段、每 50 段 1 条）+ 错误堆栈
- 追踪：traceId 从 API → 服务 → 模型客户端
- 管理：重建/刷新接口（可选）

### 11. 兼容性与数据迁移
- 默认参数保持温和（句子优先 + overlap），对现有数据无破坏
- 如启用“跨任务增量去重”，需增加 `doc_chunk_registry`（可由 Liquibase 新增 changelog）
- 不改动现有 `EmbeddingStore` 抽象；幂等落在服务层或注册表

### 12. 安全与合规
- PII 保护：日志脱敏（邮箱/手机号等）
- 访问控制：知识库维度鉴权（`knowledge_id`）
- 配置密钥：走环境变量；不落盘明文

### 13. 测试计划
- 单元：清洗规则、切分边界（长句/短段/中英混排）、哈希幂等
- 集成：PDF/HTML/Markdown 输入到“向量入库”的端到端
- 稳定性：注入错误（网络/超时/429）验证重试与部分成功
- 性能：批量 100 文档，统计耗时、失败率、资源占用
- 质量评估：小样本检索集，调参 `chunk/overlap` 迭代至稳定

### 14. 里程碑与落地计划
- M1（1–2 天）：新增配置类、改造 `EmbeddingProvider` 切分器、最小可用 overlap
- M2（2–3 天）：清洗/短片合并/会话内去重、元数据增强、批量嵌入+重试
- M3（2 天）：指标与采样日志、限流、部分失败容忍
- M4（可选 2 天）：增量注册表、管理接口（重建/刷新）、异步作业化

### 15. 风险与缓释
- 不同文档格式的解析质量差异 → 强化清洗与回退策略
- 模型限流与不稳定 → 限流+退避重试；支持替换模型/供应商
- 片段过多导致索引膨胀 → `maxChunksPerDoc` 与短片合并策略

### 16. 对标小结
- 与 Dify：对齐 ETL 流水线、可配置、幂等、治理能力；支持异步任务与增量重建  
  参考：[Dify 架构解读](https://www.53ai.com/news/RAG/2025061371856.html?utm_source=openai)
- 与参考项目：流程形态一致（切分-嵌入-索引-检索），可平滑对齐  
  参考：[LLM-Agent-Resume](https://github.com/liangdabiao/LLM-Agent-Resume)
- Java 侧实现策略参考：  
  [LangChain4j 文档处理综述](https://www.lovelybigduck.top/archives/java-ai-init?utm_source=openai)  
  [Spring AI TokenTextSplitter](https://www.ewbang.com/community/article/details/1000235819.html?utm_source=openai)  
  [OpenSearch 切片向量化（可选）](https://help.aliyun.com/zh/open-search/text-vectorization-and-slice-vectorization?utm_source=openai)

### 17. 交付物
- 配置项与 `@ConfigurationProperties` 类
- 改造后的 `EmbeddingProvider`（可配置切分器 + 回退）
- 增强的 `LangEmbeddingServiceImpl`（清洗/去重/批量嵌入/重试/元数据/指标）
- 可选：增量注册表表结构与 Liquibase 变更、管理接口与异步作业


