<template>
  <div class="status-column-examples">
    <h2>状态列样式示例</h2>

    <!-- 示例1: 使用内置标签类型 -->
    <div class="example-section">
      <h3>1. 内置标签类型</h3>
      <AdvancedTable :columns="tagColumns" :data="sampleData" />
    </div>

    <!-- 示例2: 使用插槽自定义 -->
    <div class="example-section">
      <h3>2. 插槽自定义样式</h3>
      <AdvancedTable :columns="slotColumns" :data="sampleData">
        <!-- 状态列自定义插槽 -->
        <template #cell_status="{ row, value }">
          <div class="custom-status" :class="`status-${value}`">
            <span class="status-dot"></span>
            <span class="status-text">{{ getStatusText(value) }}</span>
          </div>
        </template>

        <!-- 优先级列自定义插槽 -->
        <template #cell_priority="{ row, value }">
          <div class="priority-indicator">
            <div class="priority-bars">
              <div
                v-for="i in 5"
                :key="i"
                class="priority-bar"
                :class="{ active: i <= value }"
              ></div>
            </div>
            <span class="priority-text">{{ getPriorityText(value) }}</span>
          </div>
        </template>

        <!-- 进度列自定义插槽 -->
        <template #cell_progress="{ row, value }">
          <div class="progress-container">
            <div class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: `${value}%`, backgroundColor: getProgressColor(value) }"
              ></div>
            </div>
            <span class="progress-text">{{ value }}%</span>
          </div>
        </template>
      </AdvancedTable>
    </div>

    <!-- 示例3: 使用渲染函数 -->
    <div class="example-section">
      <h3>3. 渲染函数自定义</h3>
      <AdvancedTable :columns="renderColumns" :data="sampleData" />
    </div>

    <!-- 示例4: 复杂状态组合 -->
    <div class="example-section">
      <h3>4. 复杂状态组合</h3>
      <AdvancedTable :columns="complexColumns" :data="complexData">
        <template #cell_healthStatus="{ row }">
          <div class="health-status">
            <!-- CPU 状态 -->
            <div class="metric-item">
              <span class="metric-label">CPU</span>
              <div class="metric-value" :class="getHealthClass(row.cpu)">
                {{ row.cpu }}%
              </div>
            </div>

            <!-- 内存状态 -->
            <div class="metric-item">
              <span class="metric-label">内存</span>
              <div class="metric-value" :class="getHealthClass(row.memory)">
                {{ row.memory }}%
              </div>
            </div>

            <!-- 磁盘状态 -->
            <div class="metric-item">
              <span class="metric-label">磁盘</span>
              <div class="metric-value" :class="getHealthClass(row.disk)">
                {{ row.disk }}%
              </div>
            </div>
          </div>
        </template>
      </AdvancedTable>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { h } from 'vue'
import { ElTag, ElBadge, ElProgress, ElIcon } from 'element-plus'
import {
  CircleCheck,
  Warning,
  CircleClose,
  Clock,
  Star,
  StarFilled
} from '@element-plus/icons-vue'
import { AdvancedTable } from '../../index'

// 示例数据
const sampleData = [
  { id: 1, name: '任务A', status: 'completed', priority: 5, progress: 100 },
  { id: 2, name: '任务B', status: 'running', priority: 3, progress: 65 },
  { id: 3, name: '任务C', status: 'pending', priority: 1, progress: 0 },
  { id: 4, name: '任务D', status: 'error', priority: 4, progress: 30 },
  { id: 5, name: '任务E', status: 'paused', priority: 2, progress: 45 }
]

const complexData = [
  { id: 1, name: '服务器A', cpu: 45, memory: 60, disk: 30 },
  { id: 2, name: '服务器B', cpu: 85, memory: 75, disk: 90 },
  { id: 3, name: '服务器C', cpu: 20, memory: 40, disk: 55 },
  { id: 4, name: '服务器D', cpu: 95, memory: 90, disk: 85 }
]

// 1. 内置标签类型配置
const tagColumns = [
  { key: 'name', title: '任务名称', width: 150 },
  {
    key: 'status',
    title: '状态',
    type: 'tag',
    width: 120,
    tagOptions: {
      'completed': { label: '已完成', type: 'success' },
      'running': { label: '运行中', type: 'primary' },
      'pending': { label: '待处理', type: 'warning' },
      'error': { label: '错误', type: 'danger' },
      'paused': { label: '已暂停', type: 'info' }
    }
  },
  { key: 'progress', title: '进度', type: 'progress', width: 150 }
]

// 2. 插槽自定义列配置
const slotColumns = [
  { key: 'name', title: '任务名称', width: 150 },
  { key: 'status', title: '状态', slot: 'cell_status', width: 120 },
  { key: 'priority', title: '优先级', slot: 'cell_priority', width: 150 },
  { key: 'progress', title: '进度', slot: 'cell_progress', width: 180 }
]

// 3. 渲染函数列配置
const renderColumns = [
  { key: 'name', title: '任务名称', width: 150 },
  {
    key: 'status',
    title: '状态',
    width: 140,
    render: ({ value }) => {
      const statusConfig = {
        'completed': { icon: CircleCheck, color: '#67c23a', text: '已完成' },
        'running': { icon: Clock, color: '#409eff', text: '运行中' },
        'pending': { icon: Warning, color: '#e6a23c', text: '待处理' },
        'error': { icon: CircleClose, color: '#f56c6c', text: '错误' },
        'paused': { icon: Warning, color: '#909399', text: '已暂停' }
      }

      const config = statusConfig[value]

      return h('div', {
        class: 'flex items-center gap-2',
        style: { color: config.color }
      }, [
        h(ElIcon, { size: 16 }, () => h(config.icon)),
        h('span', { style: { fontWeight: '500' } }, config.text)
      ])
    }
  },
  {
    key: 'priority',
    title: '优先级',
    width: 120,
    render: ({ value }) => {
      return h('div', { class: 'flex items-center gap-1' },
        Array.from({ length: 5 }, (_, i) =>
          h(ElIcon, {
            size: 14,
            style: {
              color: i < value ? '#f7ba2a' : '#e4e7ed'
            }
          }, () => i < value ? h(StarFilled) : h(Star))
        )
      )
    }
  }
]

// 4. 复杂状态列配置
const complexColumns = [
  { key: 'name', title: '服务器名称', width: 150 },
  { key: 'healthStatus', title: '健康状态', slot: 'cell_healthStatus', width: 300 }
]

// 工具函数
const getStatusText = (status: string) => {
  const statusMap = {
    'completed': '已完成',
    'running': '运行中',
    'pending': '待处理',
    'error': '错误',
    'paused': '已暂停'
  }
  return statusMap[status] || status
}

const getPriorityText = (priority: number) => {
  const priorities = ['', '低', '中', '中', '高', '紧急']
  return priorities[priority] || '未知'
}

const getProgressColor = (value: number) => {
  if (value < 30) return '#f56c6c'
  if (value < 70) return '#e6a23c'
  return '#67c23a'
}

const getHealthClass = (value: number) => {
  if (value > 80) return 'health-critical'
  if (value > 60) return 'health-warning'
  return 'health-good'
}
</script>

<style lang="scss" scoped>
.status-column-examples {
  padding: 20px;

  .example-section {
    margin-bottom: 40px;

    h3 {
      margin: 20px 0 15px 0;
      color: var(--el-text-color-primary);
      font-size: 16px;
      font-weight: 600;
    }
  }
}

// 自定义状态样式
.custom-status {
  display: flex;
  align-items: center;
  gap: 8px;

  .status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }

  .status-text {
    font-weight: 500;
    font-size: 13px;
  }

  &.status-completed {
    .status-dot {
      background-color: #67c23a;
      box-shadow: 0 0 6px rgba(103, 194, 58, 0.4);
    }
    .status-text {
      color: #67c23a;
    }
  }

  &.status-running {
    .status-dot {
      background-color: #409eff;
      animation: pulse 2s infinite;
    }
    .status-text {
      color: #409eff;
    }
  }

  &.status-pending {
    .status-dot {
      background-color: #e6a23c;
    }
    .status-text {
      color: #e6a23c;
    }
  }

  &.status-error {
    .status-dot {
      background-color: #f56c6c;
      animation: blink 1s infinite;
    }
    .status-text {
      color: #f56c6c;
    }
  }

  &.status-paused {
    .status-dot {
      background-color: #909399;
    }
    .status-text {
      color: #909399;
    }
  }
}

// 优先级指示器
.priority-indicator {
  display: flex;
  align-items: center;
  gap: 8px;

  .priority-bars {
    display: flex;
    gap: 2px;

    .priority-bar {
      width: 3px;
      height: 16px;
      background-color: #e4e7ed;
      border-radius: 1px;
      transition: all 0.3s ease;

      &.active {
        background: linear-gradient(to top, #f7ba2a, #ffd700);
      }
    }
  }

  .priority-text {
    font-size: 12px;
    color: var(--el-text-color-regular);
    font-weight: 500;
  }
}

// 进度容器
.progress-container {
  display: flex;
  align-items: center;
  gap: 10px;

  .progress-bar {
    flex: 1;
    height: 8px;
    background-color: #f0f2f5;
    border-radius: 4px;
    overflow: hidden;

    .progress-fill {
      height: 100%;
      transition: all 0.3s ease;
      border-radius: 4px;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(
          90deg,
          transparent,
          rgba(255, 255, 255, 0.3),
          transparent
        );
        animation: shimmer 2s infinite;
      }
    }
  }

  .progress-text {
    font-size: 12px;
    font-weight: 500;
    color: var(--el-text-color-regular);
    min-width: 35px;
  }
}

// 健康状态
.health-status {
  display: flex;
  gap: 15px;

  .metric-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;

    .metric-label {
      font-size: 11px;
      color: var(--el-text-color-placeholder);
      font-weight: 500;
    }

    .metric-value {
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 600;
      min-width: 35px;
      text-align: center;

      &.health-good {
        background-color: rgba(103, 194, 58, 0.1);
        color: #67c23a;
        border: 1px solid rgba(103, 194, 58, 0.2);
      }

      &.health-warning {
        background-color: rgba(230, 162, 60, 0.1);
        color: #e6a23c;
        border: 1px solid rgba(230, 162, 60, 0.2);
      }

      &.health-critical {
        background-color: rgba(245, 108, 108, 0.1);
        color: #f56c6c;
        border: 1px solid rgba(245, 108, 108, 0.2);
        animation: pulse-red 2s infinite;
      }
    }
  }
}

// 动画效果
@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

@keyframes blink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0.3;
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@keyframes pulse-red {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(245, 108, 108, 0.4);
  }
  50% {
    box-shadow: 0 0 0 4px rgba(245, 108, 108, 0);
  }
}

// 暗色主题适配
html.dark {
  .custom-status {
    .status-text {
      filter: brightness(1.2);
    }
  }

  .progress-container {
    .progress-bar {
      background-color: #2c2c2c;
    }
  }

  .health-status {
    .metric-value {
      &.health-good {
        background-color: rgba(103, 194, 58, 0.2);
      }

      &.health-warning {
        background-color: rgba(230, 162, 60, 0.2);
      }

      &.health-critical {
        background-color: rgba(245, 108, 108, 0.2);
      }
    }
  }
}
</style>
