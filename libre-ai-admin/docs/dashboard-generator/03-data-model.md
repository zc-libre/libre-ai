# Dashboard Generator 数据模型设计文档

## 设计原则

基于 **KISS (Keep It Simple, Stupid)** 和 **DRY (Don't Repeat Yourself)** 原则，采用简化的数据模型设计：

- **JSON存储**: 复杂配置数据使用JSON字段存储，避免多表关联
- **最小表设计**: 仅创建必要的数据表，避免过度规范化
- **类型安全**: 使用强类型的Java对象确保数据一致性
- **PostgreSQL支持**: 充分利用PostgreSQL的JSON类型和高级特性

## 数据库设计

### 表结构设计

#### 1. dashboard_history (历史记录表)

```sql
CREATE TABLE dashboard_history (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    config_json JSONB NOT NULL,
    generated_html TEXT,
    preview_image TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_dashboard_history_user_created ON dashboard_history (user_id, created_at DESC);
CREATE INDEX idx_dashboard_history_created ON dashboard_history (created_at DESC);

-- 为JSON字段创建GIN索引以支持高效查询
CREATE INDEX idx_dashboard_history_config_gin ON dashboard_history USING GIN (config_json);

-- 添加表和字段注释
COMMENT ON TABLE dashboard_history IS '仪表板生成历史记录';
COMMENT ON COLUMN dashboard_history.id IS '记录ID';
COMMENT ON COLUMN dashboard_history.user_id IS '用户ID';
COMMENT ON COLUMN dashboard_history.config_json IS '仪表板配置JSON';
COMMENT ON COLUMN dashboard_history.generated_html IS '生成的HTML代码';
COMMENT ON COLUMN dashboard_history.preview_image IS 'Base64预览图片';
```

#### 2. dashboard_templates (模板配置表，可选)

```sql
CREATE TABLE dashboard_templates (
    type VARCHAR(32) NOT NULL PRIMARY KEY,
    data_json JSONB NOT NULL,
    version VARCHAR(16) DEFAULT '1.0',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_dashboard_templates_type_enabled ON dashboard_templates (type, enabled);

-- 为JSON字段创建GIN索引
CREATE INDEX idx_dashboard_templates_data_gin ON dashboard_templates USING GIN (data_json);

-- 添加表和字段注释
COMMENT ON TABLE dashboard_templates IS '仪表板模板配置';
COMMENT ON COLUMN dashboard_templates.type IS '模板类型(purposes/layouts/themes/components)';
COMMENT ON COLUMN dashboard_templates.data_json IS '模板数据JSON';
COMMENT ON COLUMN dashboard_templates.version IS '模板版本';
COMMENT ON COLUMN dashboard_templates.enabled IS '是否启用';
```

### PostgreSQL 特性优化

#### JSON类型选择
- 使用 `JSONB` 而非 `JSON` 类型，获得更好的查询性能
- `JSONB` 支持索引，可以进行高效的JSON查询
- 自动去除重复键和空白字符，节省存储空间

#### GIN索引优势
```sql
-- 支持复杂的JSON查询
SELECT * FROM dashboard_history 
WHERE config_json @> '{"purpose": "analytics"}';

-- 支持JSON路径查询
SELECT * FROM dashboard_history 
WHERE config_json #>> '{options,codeStyle}' = 'modern';

-- 支持JSON数组查询
SELECT * FROM dashboard_history 
WHERE config_json -> 'components' ? 'bar-chart';
```

## Java数据模型

### 实体类设计

#### 1. DashboardHistory (历史记录实体)

```java
@TableName("dashboard_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardHistory {
    
    /**
     * 记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;
    
    /**
     * 仪表板配置JSON
     */
    @TableField("config_json")
    private String configJson;
    
    /**
     * 生成的HTML代码
     */
    @TableField("generated_html")
    private String generatedHtml;
    
    /**
     * Base64预览图片
     */
    @TableField("preview_image")
    private String previewImage;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 辅助方法：JSON序列化和反序列化
    @TableField(exist = false)
    private DashboardRequest config;
    
    public DashboardRequest getConfig() {
        if (config == null && configJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                config = mapper.readValue(configJson, DashboardRequest.class);
            } catch (Exception e) {
                log.error("解析配置JSON失败", e);
            }
        }
        return config;
    }
    
    public void setConfig(DashboardRequest config) {
        this.config = config;
        if (config != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.configJson = mapper.writeValueAsString(config);
            } catch (Exception e) {
                log.error("序列化配置JSON失败", e);
            }
        }
    }
}
```

#### 2. DashboardTemplate (模板配置实体)

```java
@TableName("dashboard_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTemplate {
    
    /**
     * 模板类型
     */
    @TableId(type = IdType.INPUT)
    private String type;
    
    /**
     * 模板数据JSON
     */
    @TableField("data_json")
    private String dataJson;
    
    /**
     * 模板版本
     */
    @TableField("version")
    private String version = "1.0";
    
    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled = true;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
```

### DTO数据传输对象

#### 1. DashboardRequest (请求对象)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class DashboardRequest {
    
    /**
     * 仪表板用途
     */
    @NotBlank(message = "用途不能为空")
    @Pattern(regexp = "^(analytics|project|sales|monitoring)$", 
             message = "用途必须为: analytics, project, sales, monitoring 之一")
    private String purpose;
    
    /**
     * 布局样式
     */
    @NotBlank(message = "布局样式不能为空")
    @Pattern(regexp = "^(grid|sidebar|fullscreen)$",
             message = "布局必须为: grid, sidebar, fullscreen 之一") 
    private String layout;
    
    /**
     * 主题配色
     */
    @NotBlank(message = "主题配色不能为空")
    @Pattern(regexp = "^(modern-blue|dark-purple|green-nature|orange-warm)$",
             message = "主题必须为指定选项之一")
    private String theme;
    
    /**
     * 组件列表
     */
    @NotEmpty(message = "组件列表不能为空")
    @Size(min = 1, max = 10, message = "组件数量必须在1-10个之间")
    private List<@NotBlank String> components;
    
    /**
     * 生成选项
     */
    @Valid
    private GenerationOptions options;
    
    /**
     * 获取用途显示文本
     */
    public String getPurposeText() {
        Map<String, String> purposeMap = Map.of(
            "analytics", "数据分析",
            "project", "项目管理", 
            "sales", "销售监控",
            "monitoring", "系统监控"
        );
        return purposeMap.getOrDefault(purpose, purpose);
    }
    
    /**
     * 获取布局显示文本
     */
    public String getLayoutText() {
        Map<String, String> layoutMap = Map.of(
            "grid", "网格布局",
            "sidebar", "侧边栏布局",
            "fullscreen", "全屏布局"
        );
        return layoutMap.getOrDefault(layout, layout);
    }
    
    /**
     * 获取主题显示文本
     */
    public String getThemeText() {
        Map<String, String> themeMap = Map.of(
            "modern-blue", "现代蓝",
            "dark-purple", "深紫夜",
            "green-nature", "自然绿", 
            "orange-warm", "暖橙色"
        );
        return themeMap.getOrDefault(theme, theme);
    }
}
```

#### 2. GenerationOptions (生成选项)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationOptions {
    
    /**
     * 代码风格
     */
    @Pattern(regexp = "^(modern|minimal|enterprise)$",
             message = "代码风格必须为: modern, minimal, enterprise 之一")
    private String codeStyle = "modern";
    
    /**
     * 是否响应式设计
     */
    private Boolean responsive = true;
    
    /**
     * 是否包含示例数据
     */
    private Boolean includeData = true;
    
    /**
     * 额外需求描述
     */ 
    @Size(max = 500, message = "额外需求描述不能超过500字符")
    private String additionalRequirements = "";
}
```

#### 3. GenerationResult (生成结果)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationResult {
    
    /**
     * 生成的完整HTML文档
     * 包含内嵌的CSS和JavaScript，可直接在浏览器中运行
     */
    private String html;
    
    /**
     * 生成过程元数据
     */
    private GenerationMetadata metadata;
}
```

#### 4. GenerationMetadata (生成元数据)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationMetadata {
    
    /**
     * 生成时间
     */
    private LocalDateTime generatedAt;
    
    /**
     * 使用的AI模型
     */
    private String modelUsed;
    
    /**
     * 消耗的Token数量
     */
    private Integer tokensUsed;
    
    /**
     * 生成耗时（秒）
     */
    private Double generationTime;
    
    /**
     * 生成的代码行数
     */
    private Integer linesOfCode;
    
    /**
     * 代码文件大小
     */
    private String fileSize;
}
```

### 配置选项数据模型

#### 1. PurposeOption (用途选项)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurposeOption {
    
    private String id;
    private String title;
    private String description;
    private String icon;
    private List<String> components;
}
```

#### 2. LayoutOption (布局选项)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayoutOption {
    
    private String id;
    private String title;
    private String description;
    private String preview;
    private Boolean responsive;
}
```

#### 3. ThemeOption (主题选项)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeOption {
    
    private String id;
    private String name;
    private String description;
    private String primary;
    private String secondary;
    private String accent;
    private String background;
    private String text;
}
```

#### 4. ComponentOption (组件选项)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentOption {
    
    private String id;
    private String title;
    private String description;
    private String icon;
    private List<String> purposes;
    private String previewCode;
}
```

#### 5. DashboardTemplates (模板配置集合)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTemplates {
    
    private List<PurposeOption> purposes;
    private List<LayoutOption> layouts;
    private List<ThemeOption> themes;
    private List<ComponentOption> components;
}
```

### 响应包装类

#### R (统一响应包装)

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    private Long timestamp;
    
    // 静态工厂方法
    public static <T> R<T> ok() {
        return R.<T>builder()
            .success(true)
            .code(200)
            .message("操作成功")
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    public static <T> R<T> ok(T data) {
        return R.<T>builder()
            .success(true)
            .code(200) 
            .message("操作成功")
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    public static <T> R<T> fail(String message) {
        return R.<T>builder()
            .success(false)
            .code(500)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    public static <T> R<T> fail(Integer code, String message) {
        return R.<T>builder()
            .success(false)
            .code(code)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
```

## 数据访问层设计

### MyBatis-Plus Mapper接口

#### DashboardHistoryMapper

```java
@Mapper
public interface DashboardHistoryMapper extends BaseMapper<DashboardHistory> {
    
    /**
     * 根据用户ID分页查询历史记录 (使用MyBatis-Plus分页)
     */
    default IPage<DashboardHistory> selectHistoryByUserId(IPage<DashboardHistory> page, String userId) {
        QueryWrapper<DashboardHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("created_at");
        return selectPage(page, wrapper);
    }
    
    /**
     * 根据JSON条件查询历史记录 (PostgreSQL JSONB查询)
     */
    @Select("SELECT * FROM dashboard_history WHERE user_id = #{userId} " +
            "AND config_json @> #{jsonCondition}::jsonb ORDER BY created_at DESC")
    List<DashboardHistory> selectByJsonCondition(
        @Param("userId") String userId,
        @Param("jsonCondition") String jsonCondition
    );
    
    /**
     * 统计用户最近历史记录数量
     */
    @Select("SELECT COUNT(*) FROM dashboard_history WHERE user_id = #{userId} " +
            "AND created_at >= #{startDate}")
    Long countRecentHistory(
        @Param("userId") String userId,
        @Param("startDate") LocalDateTime startDate
    );
    
    /**
     * 根据用途统计历史记录
     */
    @Select("SELECT config_json->>'purpose' as purpose, COUNT(*) as count " +
            "FROM dashboard_history WHERE user_id = #{userId} " +
            "GROUP BY config_json->>'purpose'")
    List<Map<String,Object>> countByPurpose(@Param("userId") String userId);
}
```

#### DashboardTemplateMapper

```java
@Mapper
public interface DashboardTemplateMapper extends BaseMapper<DashboardTemplate> {
    
    /**
     * 根据类型查询启用的模板 (使用QueryWrapper)
     */
    default DashboardTemplate selectByTypeAndEnabled(String type) {
        QueryWrapper<DashboardTemplate> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type).eq("enabled", true);
        return selectOne(wrapper);
    }
    
    /**
     * 查询所有启用的模板 (使用QueryWrapper)
     */
    default List<DashboardTemplate> selectAllEnabled() {
        QueryWrapper<DashboardTemplate> wrapper = new QueryWrapper<>();
        wrapper.eq("enabled", true).orderByAsc("type");
        return selectList(wrapper);
    }
    
    /**
     * 根据JSON条件查询模板 (PostgreSQL JSONB查询)
     */
    @Select("SELECT * FROM dashboard_templates WHERE data_json @> #{jsonCondition}::jsonb")
    List<DashboardTemplate> selectByJsonCondition(@Param("jsonCondition") String jsonCondition);
    
    /**
     * 批量更新模板状态
     */
    default int batchUpdateEnabled(List<String> types, Boolean enabled) {
        UpdateWrapper<DashboardTemplate> wrapper = new UpdateWrapper<>();
        wrapper.in("type", types).set("enabled", enabled);
        return update(null, wrapper);
    }
}
```

## JSON配置数据示例

### 模板配置JSON示例

#### purposes模板数据
```json
[
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
  }
]
```

#### themes模板数据
```json
[
  {
    "id": "modern-blue",
    "name": "现代蓝",
    "description": "简洁现代的蓝色主题，适合商务场景",
    "primary": "#409EFF",
    "secondary": "#67C23A",
    "accent": "#E6A23C", 
    "background": "#F5F7FA",
    "text": "#303133"
  }
]
```

## 数据初始化

### 初始化SQL脚本

```sql
-- 插入默认模板数据
INSERT INTO dashboard_templates (type, data_json, version) VALUES
('purposes', '[{"id":"analytics","title":"数据分析","description":"用于数据可视化和业务分析的仪表板","icon":"chart-bar","components":["bar-chart","line-chart","kpi-card","data-table"]},{"id":"project","title":"项目管理","description":"项目进度跟踪和团队协作管理","icon":"project","components":["progress-bar","task-list","team-card","timeline"]},{"id":"sales","title":"销售监控","description":"销售数据监控和业绩分析","icon":"trending-up","components":["pie-chart","sales-funnel","kpi-card","region-map"]},{"id":"monitoring","title":"系统监控","description":"服务器和应用程序性能监控","icon":"monitor","components":["gauge-chart","alert-panel","log-table","metrics-card"]}]', '1.0'),

('layouts', '[{"id":"grid","title":"网格布局","description":"灵活的网格式组件排列，适合多种屏幕尺寸","preview":"/images/layout-grid.png","responsive":true},{"id":"sidebar","title":"侧边栏布局","description":"左侧导航栏配合主内容区域的经典布局","preview":"/images/layout-sidebar.png","responsive":true},{"id":"fullscreen","title":"全屏布局","description":"单页面全屏显示，适合大屏展示","preview":"/images/layout-fullscreen.png","responsive":false}]', '1.0'),

('themes', '[{"id":"modern-blue","name":"现代蓝","description":"简洁现代的蓝色主题，适合商务场景","primary":"#409EFF","secondary":"#67C23A","accent":"#E6A23C","background":"#F5F7FA","text":"#303133"},{"id":"dark-purple","name":"深紫夜","description":"深色紫色主题，护眼且富有科技感","primary":"#722ED1","secondary":"#52C41A","accent":"#FA8C16","background":"#1F1F1F","text":"#FFFFFF"},{"id":"green-nature","name":"自然绿","description":"清新自然的绿色主题，舒适养眼","primary":"#52C41A","secondary":"#1890FF","accent":"#FAAD14","background":"#F6FFED","text":"#262626"},{"id":"orange-warm","name":"暖橙色","description":"温暖活力的橙色主题，充满活力","primary":"#FA8C16","secondary":"#13C2C2","accent":"#EB2F96","background":"#FFF7E6","text":"#434343"}]', '1.0'),

('components', '[{"id":"bar-chart","title":"柱状图","description":"用于展示分类数据的对比","icon":"chart-bar","purposes":["analytics","sales"],"previewCode":"<div class=\"chart-container\"><canvas></canvas></div>"},{"id":"line-chart","title":"折线图","description":"用于展示数据随时间的变化趋势","icon":"chart-line","purposes":["analytics","monitoring"],"previewCode":"<div class=\"chart-container\"><canvas></canvas></div>"},{"id":"pie-chart","title":"饼图","description":"用于展示各部分占整体的比例关系","icon":"chart-pie","purposes":["analytics","sales"],"previewCode":"<div class=\"chart-container\"><canvas></canvas></div>"},{"id":"kpi-card","title":"KPI指标卡","description":"展示关键业务指标的卡片组件","icon":"card","purposes":["analytics","sales","project","monitoring"],"previewCode":"<div class=\"kpi-card\"><div class=\"kpi-value\">1,234</div></div>"},{"id":"data-table","title":"数据表格","description":"结构化数据的表格展示组件","icon":"table","purposes":["analytics","project","monitoring"],"previewCode":"<table class=\"data-table\"><thead><tr><th>列名</th></tr></thead></table>"},{"id":"progress-bar","title":"进度条","description":"展示任务或项目完成进度","icon":"progress","purposes":["project"],"previewCode":"<div class=\"progress-bar\"><div class=\"progress-fill\"></div></div>"}]', '1.0');
```

## 数据验证规则

### 字段验证规则

| 字段 | 规则 | 说明 |
|------|------|------|
| purpose | @NotBlank, @Pattern | 必填，枚举值验证 |
| layout | @NotBlank, @Pattern | 必填，枚举值验证 |
| theme | @NotBlank, @Pattern | 必填，枚举值验证 |
| components | @NotEmpty, @Size(1-10) | 必填，数量限制 |
| codeStyle | @Pattern | 可选，枚举值验证 |
| additionalRequirements | @Size(max=500) | 可选，长度限制 |
| userId | @NotBlank, @Size(max=64) | 必填，长度限制 |

### 数据完整性约束

```sql
-- 外键约束（如果需要）
ALTER TABLE dashboard_history 
ADD CONSTRAINT fk_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 检查约束
ALTER TABLE dashboard_history 
ADD CONSTRAINT chk_id_format 
CHECK (id ~ '^[a-zA-Z0-9_-]{1,64}$');

-- 检查JSON字段不为空
ALTER TABLE dashboard_history 
ADD CONSTRAINT chk_config_json_not_empty 
CHECK (config_json IS NOT NULL AND config_json != '{}');

-- 唯一约束
ALTER TABLE dashboard_templates 
ADD CONSTRAINT uk_type_version 
UNIQUE (type, version);

-- PostgreSQL特有的JSON验证约束
ALTER TABLE dashboard_templates 
ADD CONSTRAINT chk_valid_json 
CHECK (data_json IS JSON);
```

---

**文档版本**: v1.0  
**创建时间**: 2025-01-15  
**更新时间**: 2025-01-15  
**作者**: AI Assistant