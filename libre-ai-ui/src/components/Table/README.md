# Table 组件使用指南

## 概览

Table 组件库提供了完整的表格解决方案，支持多种列样式自定义、搜索、分页、操作等功能。

## 组件列表

- `BasicTable` - 基础表格组件
- `AdvancedTable` - 高级表格组件（推荐）
- `TableAction` - 操作按钮组件
- `TableSearch` - 搜索组件
- `TablePagination` - 分页组件

## 列样式自定义

### 1. 内置状态样式

使用 `type` 属性快速设置状态样式：

```vue
<template>
  <AdvancedTable :columns="columns" :data="data" />
</template>

<script setup>
const columns = [
  {
    key: 'status',
    title: '状态',
    type: 'tag',
    tagOptions: {
      'active': { label: '活跃', type: 'success' },
      'inactive': { label: '未激活', type: 'info' },
      'banned': { label: '已封禁', type: 'danger' }
    }
  }
]
</script>
```

### 2. 支持的内置类型

| 类型 | 说明 | 配置选项 |
|------|------|----------|
| `tag` | 标签样式 | `tagOptions`, `tagSize` |
| `switch` | 开关样式 | `disabled` |
| `image` | 图片样式 | `imageWidth`, `imageHeight` |
| `progress` | 进度条 | `progressType`, `strokeWidth` |
| `link` | 链接样式 | `linkType`, `target` |
| `date` | 日期格式化 | `dateFormat` |
| `number` | 数字格式化 | `numberFormat` |

### 3. 自定义渲染函数

```vue
<script setup>
import { h } from 'vue'
import { ElTag, ElIcon } from 'element-plus'
import { CircleCheck, Warning } from '@element-plus/icons-vue'

const columns = [
  {
    key: 'status',
    title: '状态',
    render: ({ row, value }) => {
      const config = {
        'success': { icon: CircleCheck, color: '#67c23a', text: '成功' },
        'warning': { icon: Warning, color: '#e6a23c', text: '警告' }
      }[value]
      
      return h('div', { class: 'flex items-center gap-2' }, [
        h(ElIcon, { style: { color: config.color } }, () => h(config.icon)),
        h(ElTag, { type: value }, () => config.text)
      ])
    }
  }
]
</script>
```

### 4. 插槽自定义

```vue
<template>
  <AdvancedTable :columns="columns" :data="data">
    <template #cell_status="{ row, value, index }">
      <div class="custom-status" :class="`status-${value}`">
        <span class="status-dot"></span>
        <span class="status-text">{{ getStatusText(value) }}</span>
      </div>
    </template>
  </AdvancedTable>
</template>

<style scoped>
.custom-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-active .status-dot {
  background-color: #67c23a;
  box-shadow: 0 0 6px rgba(103, 194, 58, 0.4);
}

.status-pending .status-dot {
  background-color: #e6a23c;
  animation: pulse 2s infinite;
}
</style>
```

### 5. CSS 类自定义

通过 `className` 属性为列添加自定义样式：

```vue
<script setup>
const columns = [
  {
    key: 'priority',
    title: '优先级',
    className: 'priority-column',
    formatter: (row, column, cellValue) => {
      return ['低', '中', '高', '紧急'][cellValue - 1]
    }
  }
]
</script>

<style>
.priority-column .cell {
  font-weight: 600;
}

.priority-column .cell[data-priority="4"] {
  color: #f56c6c;
  background-color: rgba(245, 108, 108, 0.1);
}
</style>
```

## 高级用法示例

### 复合状态显示

```vue
<template>
  <AdvancedTable :columns="columns" :data="data">
    <template #cell_healthStatus="{ row }">
      <div class="health-metrics">
        <div class="metric" :class="getMetricClass(row.cpu)">
          <span class="metric-label">CPU</span>
          <span class="metric-value">{{ row.cpu }}%</span>
        </div>
        <div class="metric" :class="getMetricClass(row.memory)">
          <span class="metric-label">内存</span>
          <span class="metric-value">{{ row.memory }}%</span>
        </div>
      </div>
    </template>
  </AdvancedTable>
</template>

<script setup>
const getMetricClass = (value) => {
  if (value > 80) return 'metric-critical'
  if (value > 60) return 'metric-warning'
  return 'metric-normal'
}
</script>

<style scoped>
.health-metrics {
  display: flex;
  gap: 10px;
}

.metric {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.metric-normal {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.metric-warning {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.metric-critical {
  background-color: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}
</style>
```

### 动态样式

```vue
<script setup>
const columns = [
  {
    key: 'trend',
    title: '趋势',
    render: ({ row, value }) => {
      const isPositive = value > 0
      const icon = isPositive ? 'ArrowUp' : 'ArrowDown'
      const color = isPositive ? '#67c23a' : '#f56c6c'
      
      return h('div', { 
        class: 'trend-indicator',
        style: { color }
      }, [
        h(ElIcon, () => h(resolveComponent(icon))),
        h('span', `${Math.abs(value)}%`)
      ])
    }
  }
]
</script>
```

## 最佳实践

1. **状态一致性**: 在整个应用中保持状态颜色和样式的一致性
2. **可访问性**: 确保颜色对比度符合可访问性标准
3. **响应式设计**: 考虑移动端的显示效果
4. **性能优化**: 避免在渲染函数中进行复杂计算
5. **主题适配**: 支持明暗主题切换

## 常见问题

### Q: 如何实现条件样式？

```vue
<script setup>
const columns = [
  {
    key: 'score',
    title: '评分',
    render: ({ value }) => {
      const getScoreStyle = (score) => {
        if (score >= 90) return { color: '#67c23a', bg: 'rgba(103, 194, 58, 0.1)' }
        if (score >= 70) return { color: '#e6a23c', bg: 'rgba(230, 162, 60, 0.1)' }
        return { color: '#f56c6c', bg: 'rgba(245, 108, 108, 0.1)' }
      }
      
      const style = getScoreStyle(value)
      return h('div', {
        style: {
          padding: '4px 8px',
          borderRadius: '4px',
          backgroundColor: style.bg,
          color: style.color,
          fontWeight: '600'
        }
      }, value)
    }
  }
]
</script>
```

### Q: 如何添加动画效果？

使用 CSS 动画：

```css
.status-loading .status-dot {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.status-success .status-dot {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.8); }
  to { opacity: 1; transform: scale(1); }
}
```

## 响应式设计

### 列响应式配置

通过列配置属性控制在不同设备上的显示：

```vue
<script setup>
const columns = [
  {
    key: 'name',
    title: '姓名',
    required: true, // 必要列，所有设备都显示
    fixed: 'left'
  },
  {
    key: 'email',
    title: '邮箱',
    hideOnMobile: true, // 移动端隐藏
    showOverflowTooltip: true
  },
  {
    key: 'department',
    title: '部门',
    hideOnTablet: true // 平板端也隐藏
  }
]
</script>
```

### 响应式断点

- **超小屏** (`≤ 480px`): 只显示 `required: true` 的列
- **移动端** (`≤ 768px`): 隐藏 `hideOnMobile: true` 的列
- **平板端** (`≤ 1024px`): 隐藏 `hideOnTablet: true` 的列
- **桌面端** (`> 1024px`): 显示所有列

### 移动端优化功能

1. **横向滚动**: 自动启用横向滚动，支持触摸滑动
2. **滚动阴影**: 显示滚动提示阴影
3. **列宽自适应**: 移动端自动调整列宽
4. **紧凑模式**: 减少内边距，优化显示空间
5. **分页优化**: 移动端分页组件布局调整

### 完整响应式示例

```vue
<template>
  <AdvancedTable
    :columns="responsiveColumns"
    :data="data"
    :search-config="searchConfig"
    stripe
    show-index
  />
</template>

<script setup>
import { AdvancedTable } from '@/components/Table'

const responsiveColumns = [
  {
    key: 'name',
    title: '姓名',
    width: 120,
    required: true, // 所有设备显示
    fixed: 'left'
  },
  {
    key: 'email',
    title: '邮箱',
    width: 200,
    hideOnMobile: true, // 移动端隐藏
    showOverflowTooltip: true
  },
  {
    key: 'phone',
    title: '电话',
    width: 140,
    hideOnMobile: true // 移动端隐藏
  },
  {
    key: 'department',
    title: '部门',
    width: 120,
    hideOnTablet: true // 平板端和移动端都隐藏
  },
  {
    key: 'status',
    title: '状态',
    width: 100,
    type: 'tag'
  }
]
</script>
```

这样你就可以根据需要灵活地自定义表格列的样式和响应式行为了！