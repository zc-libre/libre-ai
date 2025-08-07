<script lang="ts" setup>
import Chart from './components/Chart.vue';
import List from './components/List.vue';
import { ElCard } from 'element-plus';
import { Icon } from '@iconify/vue';
</script>

<template>
  <div
    class="view-container view-scrollable p-2 sm:p-4 bg-gray-50 dark:bg-gray-900"
  >
    <div class="w-full">
      <!-- 页面标题 -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">
          订单统计
        </h1>
        <p class="text-gray-600 dark:text-gray-400">
          查看 AI 服务使用统计和订单记录
        </p>
      </div>

      <!-- 图表卡片 -->
      <el-card shadow="hover" class="order-chart-card border-0 shadow-lg mb-6">
        <template #header>
          <div class="flex items-center gap-2">
            <Icon icon="ep:trend-charts" class="text-lg text-blue-500" />
            <span class="font-semibold text-gray-900 dark:text-white"
              >使用趋势</span
            >
          </div>
        </template>
        <Chart />
      </el-card>

      <!-- 列表卡片 -->
      <el-card shadow="hover" class="order-list-card border-0 shadow-lg">
        <template #header>
          <div class="flex items-center gap-2">
            <Icon icon="ep:list" class="text-lg text-green-500" />
            <span class="font-semibold text-gray-900 dark:text-white"
              >订单记录</span
            >
          </div>
        </template>
        <List />
      </el-card>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.order-chart-card,
.order-list-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  transition: var(--pure-transition-duration);

  :deep(.el-card__header) {
    padding: 16px 20px;
    border-bottom: 1px solid var(--pure-border-color);
    background: var(--el-bg-color);
  }

  :deep(.el-card__body) {
    padding: 20px;

    @media (max-width: 640px) {
      padding: 16px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .order-chart-card,
  .order-list-card {
    margin: 0 -8px;
    border-radius: 0;
    box-shadow: none;
    border-top: 1px solid var(--pure-border-color);
  }
}

// 暗色主题适配
html.dark {
  .order-chart-card,
  .order-list-card {
    background: var(--el-bg-color);
    border: 1px solid var(--pure-border-color);
  }
}
</style>
