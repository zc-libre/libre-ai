# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目背景

本项目是由/Users/libre/code/java/project/libre-ai/libre-ai-ui 这个项目views下的界面迁移过来，UI组件库从原项目改为了 Element Plus。

## 技术栈

- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite 6
- **UI框架**: Element Plus 2.9
- **状态管理**: Pinia 2.3
- **路由**: Vue Router 4.5
- **国际化**: Vue I18n 10
- **CSS**: Tailwind CSS 3.4 + SCSS
- **包管理器**: pnpm (>=9)
- **Node版本**: ^18.18.0 || ^20.9.0 || >=22.0.0

## 开发命令

```bash
# 安装依赖
pnpm install

# 启动开发服务器（端口号由 VITE_PORT 环境变量决定）
pnpm dev

# 构建生产版本
pnpm build

# 构建预发布版本
pnpm build:staging

# 预览构建结果
pnpm preview

# 代码检查和格式化
pnpm lint              # 运行所有lint检查
pnpm lint:eslint      # ESLint检查
pnpm lint:prettier    # Prettier格式化
pnpm lint:stylelint   # 样式检查

# 类型检查
pnpm typecheck

# 清理缓存并重新安装
pnpm clean:cache
```

## 项目结构

```
src/
├── api/               # API接口定义
│   ├── aigc/         # AIGC相关接口
│   ├── models/       # 模型相关接口
│   ├── routes.ts     # 路由接口
│   └── user.ts       # 用户接口
├── assets/           # 静态资源
├── components/       # 全局组件
│   ├── ReAuth/       # 权限认证组件
│   ├── ReDialog/     # 对话框组件
│   ├── ReIcon/       # 图标组件
│   ├── RePerms/      # 权限控制组件
│   └── ...
├── config/           # 全局配置
├── directives/       # 自定义指令
├── enums/            # 枚举定义
├── hooks/            # 组合式函数
├── layout/           # 布局组件
│   ├── components/   # 布局子组件
│   │   ├── lay-navbar/     # 导航栏
│   │   ├── lay-sidebar/    # 侧边栏
│   │   ├── lay-tag/        # 标签页
│   │   └── ...
│   └── index.vue     # 主布局
├── plugins/          # 插件配置
├── router/           # 路由配置
│   ├── modules/      # 路由模块
│   └── index.ts      # 路由入口
├── store/            # 状态管理
│   └── modules/      # store模块
├── style/            # 全局样式
├── utils/            # 工具函数
├── views/            # 页面组件
│   ├── aigc/         # AIGC相关页面
│   ├── app/          # 应用管理
│   ├── chat/         # 聊天功能
│   └── ...
└── main.ts           # 应用入口
```

## 路由架构

项目使用模块化路由设计：
- 静态路由自动导入：`src/router/modules/*.ts`（除remaining.ts外）
- 支持多级路由和动态路由
- 路由权限控制集成

## 状态管理

使用 Pinia 进行状态管理，主要模块：
- `app`: 应用配置状态
- `user`: 用户信息状态
- `permission`: 权限管理状态
- `multiTags`: 多标签页状态
- `settings`: 系统设置状态
- `epTheme`: Element Plus主题状态

## 代码规范

- **ESLint + Prettier**: 代码格式化和检查
- **StyleLint**: CSS/SCSS样式检查
- **Husky + lint-staged**: Git提交前自动检查
- **TypeScript**: 使用TypeScript但strict模式关闭

### 格式化配置
- 单引号
- 无分号
- 2空格缩进
- 无尾随逗号
- 避免箭头函数参数括号

## 重要特性

1. **国际化支持**: 完整的i18n配置，支持多语言切换
2. **权限系统**: 按钮级和页面级权限控制
3. **主题系统**: 支持动态主题切换
4. **图标系统**: 支持Iconify在线/离线图标和字体图标
5. **Mock数据**: 使用vite-plugin-fake-server进行接口模拟
6. **响应式存储**: 自适应不同屏幕尺寸的配置存储

## 环境变量

主要环境变量（通过.env文件配置）：
- `VITE_PORT`: 开发服务器端口
- `VITE_PUBLIC_PATH`: 公共路径
- `VITE_CDN`: 是否启用CDN
- `VITE_COMPRESSION`: 构建时压缩方式

## 注意事项

1. 使用pnpm作为包管理器，不要使用npm或yarn
2. Node版本要求严格，请确保使用指定版本
3. 项目使用ESM模块系统
4. 构建时会自动分割代码和静态资源
5. 修复代码完成后禁止启动开发服务器测试