# Vue组件预览依赖库

这个目录包含了Vue组件预览功能所需的所有离线依赖库，确保在没有网络连接的环境中也能正常工作。

## 📁 目录结构

```
preview-libs/
├── vue/                    # Vue 3核心库
│   ├── vue.esm-browser.js     # Vue 3 ES模块版本
│   ├── vue.global.js          # Vue 3 全局版本
│   └── compiler-sfc.esm-browser.js  # Vue SFC编译器
├── element-plus/           # Element Plus UI组件库
│   ├── index.full.js          # Element Plus完整版
│   ├── index.css              # Element Plus样式
│   └── icons.js               # Element Plus图标
├── echarts/               # ECharts图表库
│   ├── echarts.min.js         # ECharts核心库
│   └── vue-echarts.esm.min.js # Vue-ECharts适配器
├── tailwind/              # TailwindCSS样式框架
│   ├── tailwind-play.js       # TailwindCSS Play版本（JIT）
│   ├── tailwind-config.js     # 自定义配置
│   ├── tailwind-loader.js     # 加载器
│   ├── tailwind-full.css      # 完整预编译版本
│   └── tailwind-full.min.css  # 压缩版本
├── lib-loader.js          # 统一依赖加载器
├── import-map.json        # ES模块导入映射
└── versions.json          # 版本信息
```

## 🔧 版本信息

- **Vue**: 3.5.13
- **Element Plus**: 2.9.0  
- **ECharts**: 5.5.1
- **Vue-ECharts**: 6.7.3
- **TailwindCSS**: 3.4.1

## 📦 依赖大小

| 库名称 | 文件大小 | 说明 |
|--------|----------|------|
| Vue 3 | ~1.4MB | 包含编译器和运行时 |
| Element Plus | ~2.1MB | 完整UI组件库 |
| ECharts | ~900KB | 图表库 |
| TailwindCSS | ~400KB | 样式框架（Play版本） |
| **总计** | **~4.8MB** | 完全离线支持 |

## 🚀 使用方法

### 1. 构建依赖库

```bash
# 下载所有依赖
pnpm run build:preview-libs

# 构建TailwindCSS（简化版）
pnpm run build:tailwind-simple

# 构建TailwindCSS（完整版，较慢）
pnpm run build:tailwind-offline
```

### 2. 在Vue组件中使用

```javascript
import { loadDependencies } from '@/assets/preview-libs/lib-loader.js'

// 加载所需依赖
await loadDependencies(['vue', 'element-plus', 'tailwind', 'echarts'])
```

### 3. 预览组件配置

```javascript
const previewConfig = {
  dependencies: {
    vue: '/src/assets/preview-libs/vue/vue.esm-browser.js',
    'element-plus': '/src/assets/preview-libs/element-plus/index.full.js',
    echarts: '/src/assets/preview-libs/echarts/echarts.min.js'
  },
  styles: [
    '/src/assets/preview-libs/element-plus/index.css',
    '/src/assets/preview-libs/tailwind/tailwind-play.js'
  ]
}
```

## 🎨 TailwindCSS支持

### 特性
- ✅ 支持所有TailwindCSS类（40000+）
- ✅ 实时JIT编译，AI生成代码完全兼容
- ✅ Element Plus主题集成
- ✅ 暗色模式支持
- ✅ 自定义动画和工具类

### 配置示例
```javascript
// 在预览iframe中应用TailwindCSS
window.tailwind.config = {
  theme: {
    extend: {
      colors: {
        primary: '#409EFF',  // Element Plus主色
        success: '#67C23A',
        warning: '#E6A23C',
        danger: '#F56C6C'
      }
    }
  }
}
```

## 🔄 更新依赖

当需要更新依赖版本时：

1. 修改 `scripts/build-preview-libs.js` 中的版本号
2. 运行 `pnpm run build:preview-libs`
3. 检查 `versions.json` 确认更新成功

## 🌐 离线保证

所有依赖都已本地化，不依赖任何外部CDN：
- ❌ 无需Internet连接
- ❌ 无需外部CDN服务
- ✅ 完全离线运行
- ✅ 内网环境友好

## 🛡️ 安全性

- 所有依赖来源于官方CDN
- 版本锁定，确保一致性
- 无恶意代码，安全可靠
- 沙箱化预览环境

## 📄 许可证

各依赖库遵循其原始许可证：
- Vue 3: MIT License
- Element Plus: MIT License  
- ECharts: Apache License 2.0
- TailwindCSS: MIT License