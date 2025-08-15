# Vue组件离线预览功能

## 🎯 功能概述

本模块为dashboard-generator添加了完全离线的Vue单文件组件预览功能，支持Element Plus、TailwindCSS、ECharts等依赖，无需外网访问。

## 📁 目录结构

```
dashboard-generator/
├── components/
│   ├── VueComponentPreview.vue      # Vue组件预览核心组件
│   ├── DashboardPreview.vue         # 扩展的预览组件（支持HTML/Vue）
│   ├── PreviewPanel.vue             # 更新的预览面板（支持模式切换）
│   └── ...
├── examples/
│   └── SampleVueComponent.vue       # Vue组件示例
├── test/
│   └── VuePreviewTest.vue           # 预览功能测试页面
└── README.md                        # 本文档
```

## 🚀 快速开始

### 1. 构建依赖库

首次使用前需要下载离线依赖：

```bash
cd libre-ai-ui

# 下载所有前端依赖
pnpm run build:preview-libs

# 构建TailwindCSS（推荐简化版）
pnpm run build:tailwind-simple
```

### 2. 基础使用

```vue
<template>
  <VueComponentPreview
    :vue-code="vueCode"
    :config="previewConfig"
    @compilation-success="onSuccess"
    @compilation-error="onError"
    @runtime-error="onRuntimeError"
  />
</template>

<script setup>
import VueComponentPreview from './components/VueComponentPreview.vue'

const vueCode = ref(`
<template>
  <div class="p-6 bg-blue-50">
    <el-button type="primary" @click="handleClick">
      {{ message }}
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const message = ref('点击我')

const handleClick = () => {
  ElMessage.success('Vue组件运行正常！')
}
</script>

<style scoped>
/* 组件样式 */
</style>
`)

const previewConfig = {
  theme: 'light',
  dependencies: ['vue', 'element-plus', 'tailwind', 'echarts']
}
</script>
```

### 3. 集成到Dashboard生成器

```vue
<template>
  <PreviewPanel
    :dashboard-config="config"
    :generated-code="generatedCode"
    :code-type="codeType"
    :is-streaming="isGenerating"
    @compilation-complete="handleCompilationComplete"
    @runtime-error="handleRuntimeError"
  />
</template>

<script setup>
import PreviewPanel from './components/PreviewPanel.vue';

const codeType = ref('vue'); // 'html' | 'vue'
const generatedCode = ref(''); // Vue组件代码
</script>
```

## 🔧 API参考

### VueComponentPreview

#### Props

| 属性      | 类型     | 默认值 | 说明              |
| --------- | -------- | ------ | ----------------- |
| `vueCode` | `string` | `''`   | Vue单文件组件代码 |
| `config`  | `object` | `{}`   | 预览配置          |

#### Config配置

```typescript
interface PreviewConfig {
  theme?: 'light' | 'dark'; // 主题模式
  dependencies?: string[]; // 依赖库列表
  customConfig?: Record<string, any>; // 自定义配置
}
```

#### Events

| 事件                  | 参数              | 说明       |
| --------------------- | ----------------- | ---------- |
| `compilation-success` | `(html: string)`  | 编译成功   |
| `compilation-error`   | `(error: string)` | 编译失败   |
| `runtime-error`       | `(error: string)` | 运行时错误 |

### DashboardPreview

#### Props

| 属性            | 类型              | 默认值   | 说明          |
| --------------- | ----------------- | -------- | ------------- |
| `config`        | `object`          | `{}`     | Dashboard配置 |
| `generatedCode` | `string`          | `''`     | 生成的代码    |
| `codeType`      | `'html' \| 'vue'` | `'html'` | 代码类型      |

### PreviewPanel

#### Props

| 属性              | 类型              | 默认值   | 说明          |
| ----------------- | ----------------- | -------- | ------------- |
| `dashboardConfig` | `object`          | `{}`     | Dashboard配置 |
| `generatedCode`   | `string`          | `''`     | 生成的代码    |
| `defaultCodeType` | `'html' \| 'vue'` | `'html'` | 默认代码类型  |
| `isStreaming`     | `boolean`         | `false`  | 是否流式生成  |

## 💡 支持的技术栈

### ✅ Vue 3 特性

- ✅ Composition API (`<script setup>`)
- ✅ 响应式系统 (`ref`, `reactive`, `computed`)
- ✅ 生命周期钩子 (`onMounted`, `onUnmounted` 等)
- ✅ 组件通信 (`emit`, `props`)
- ✅ 模板语法 (`v-if`, `v-for`, `v-model` 等)

### ✅ Element Plus 组件

- ✅ 所有基础组件 (`el-button`, `el-input` 等)
- ✅ 表格组件 (`el-table`)
- ✅ 表单组件 (`el-form`)
- ✅ 导航组件 (`el-menu`)
- ✅ 反馈组件 (`el-message`, `el-dialog` 等)
- ✅ 图标库 (`@element-plus/icons-vue`)

### ✅ TailwindCSS 样式

- ✅ 所有工具类 (40000+ 类)
- ✅ 响应式断点 (`sm:`, `md:`, `lg:` 等)
- ✅ 状态变体 (`hover:`, `focus:`, `active:` 等)
- ✅ 暗色模式 (`dark:`)
- ✅ 自定义主题集成

### ✅ ECharts 图表

- ✅ 基础图表 (柱状图、折线图、饼图等)
- ✅ Vue-ECharts 组件集成
- ✅ 主题配置
- ✅ 响应式图表

## 🎨 样式和主题

### 主题配置

```javascript
// TailwindCSS配置自动应用
const config = {
  theme: 'dark', // 或 'light'
  customConfig: {
    colors: {
      primary: '#409EFF',
      success: '#67C23A',
      warning: '#E6A23C',
      danger: '#F56C6C'
    }
  }
};
```

### 与Element Plus集成

预览环境自动配置Element Plus主题：

```css
/* 自动注入的CSS变量 */
:root {
  --el-color-primary: #409eff;
  --el-color-success: #67c23a;
  --el-color-warning: #e6a23c;
  --el-color-danger: #f56c6c;
}
```

## 🔍 调试和错误处理

### 编译错误

```vue
<template>
  <VueComponentPreview :vue-code="vueCode" @compilation-error="handleError" />
</template>

<script setup>
const handleError = error => {
  console.error('Vue编译错误:', error);
  // 显示错误信息给用户
};
</script>
```

### 运行时错误

```vue
<template>
  <VueComponentPreview
    :vue-code="vueCode"
    @runtime-error="handleRuntimeError"
  />
</template>

<script setup>
const handleRuntimeError = error => {
  console.error('Vue运行时错误:', error);
  // 处理运行时错误
};
</script>
```

### 常见问题

1. **编译失败**: 检查Vue语法是否正确
2. **组件不显示**: 确认依赖库已加载
3. **样式问题**: 检查TailwindCSS配置
4. **图标不显示**: 确认Element Plus图标已导入

## 📊 性能优化

### 编译性能

- ✅ 增量编译：只编译变更的代码
- ✅ 缓存机制：编译结果缓存
- ✅ 异步加载：依赖库按需加载

### 内存管理

- ✅ iframe隔离：预览组件在独立环境运行
- ✅ 自动清理：组件销毁时清理资源
- ✅ 错误边界：编译错误不影响主应用

### 文件大小

| 依赖库       | 大小       | 说明               |
| ------------ | ---------- | ------------------ |
| Vue 3        | ~1.4MB     | 包含编译器和运行时 |
| Element Plus | ~2.1MB     | 完整UI组件库       |
| TailwindCSS  | ~400KB     | Play版本（JIT）    |
| ECharts      | ~900KB     | 图表库             |
| **总计**     | **~4.8MB** | 一次下载，永久离线 |

## 🧪 测试

### 运行测试页面

```bash
# 在开发环境中访问测试页面
# http://localhost:3888/dashboard-generator/test
```

### 单元测试

```bash
# 运行Vue组件测试
npm run test:unit

# 运行集成测试
npm run test:integration
```

## 🔄 更新和维护

### 更新依赖版本

1. 修改 `scripts/build-preview-libs.js` 中的版本号
2. 运行 `pnpm run build:preview-libs`
3. 检查 `src/assets/preview-libs/versions.json` 确认更新

### 添加新依赖

```javascript
// 在 build-preview-libs.js 中添加
const dependencies = {
  // ... 现有依赖
  'new-library': {
    version: '1.0.0',
    files: [
      {
        url: 'https://unpkg.com/new-library@1.0.0/dist/index.js',
        output: 'new-library/index.js'
      }
    ]
  }
};
```

## 📄 许可证

本功能基于以下开源项目：

- Vue 3: MIT License
- Element Plus: MIT License
- TailwindCSS: MIT License
- ECharts: Apache License 2.0

## 🤝 贡献

欢迎提交Issue和Pull Request来改进此功能！

### 开发规范

- 遵循现有的代码风格
- 添加必要的类型注解
- 编写单元测试
- 更新相关文档
