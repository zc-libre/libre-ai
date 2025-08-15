<!-- 这是一个示例Vue组件，用于测试Vue组件预览功能 -->
<template>
  <div
    class="dashboard-container p-6 bg-gradient-to-br from-blue-50 to-indigo-100 min-h-screen"
  >
    <div class="max-w-7xl mx-auto">
      <!-- 页面标题 -->
      <div class="mb-8 text-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">智能数据看板</h1>
        <p class="text-lg text-gray-600">实时数据监控与可视化分析</p>
      </div>

      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div
          v-for="stat in stats"
          :key="stat.title"
          class="bg-white rounded-lg shadow-card p-6 hover:shadow-card-hover transition-shadow"
        >
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500">{{ stat.title }}</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">
                {{ stat.value }}
              </p>
              <div class="flex items-center mt-2">
                <span
                  :class="
                    stat.trend === 'up' ? 'text-green-600' : 'text-red-600'
                  "
                  class="text-sm font-medium"
                >
                  {{ stat.change }}
                </span>
                <span class="text-sm text-gray-500 ml-2">vs 上月</span>
              </div>
            </div>
            <div
              :class="stat.iconBg"
              class="w-12 h-12 rounded-lg flex items-center justify-center"
            >
              <el-icon :size="24" :color="stat.iconColor">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <!-- 销售趋势图 -->
        <el-card class="chart-card">
          <template #header>
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">销售趋势</h3>
              <el-button size="small" type="primary">查看详情</el-button>
            </div>
          </template>
          <div class="chart-container h-64 flex items-center justify-center">
            <div class="text-center text-gray-500">
              <el-icon :size="48" color="#c0c4cc">
                <TrendCharts />
              </el-icon>
              <p class="mt-2">ECharts 图表将在这里显示</p>
            </div>
          </div>
        </el-card>

        <!-- 用户分析 -->
        <el-card class="chart-card">
          <template #header>
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-semibold text-gray-900">用户分析</h3>
              <el-select
                v-model="selectedPeriod"
                size="small"
                style="width: 120px"
              >
                <el-option label="本周" value="week" />
                <el-option label="本月" value="month" />
                <el-option label="本年" value="year" />
              </el-select>
            </div>
          </template>
          <div class="chart-container h-64 flex items-center justify-center">
            <div class="text-center text-gray-500">
              <el-icon :size="48" color="#c0c4cc">
                <PieChart />
              </el-icon>
              <p class="mt-2">用户分析图表</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 数据表格 -->
      <el-card>
        <template #header>
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">最新订单</h3>
            <div class="flex gap-2">
              <el-button size="small" :icon="Refresh" @click="refreshData">
                刷新
              </el-button>
              <el-button size="small" type="primary" :icon="Download">
                导出
              </el-button>
            </div>
          </div>
        </template>

        <el-table :data="tableData" stripe style="width: 100%">
          <el-table-column prop="id" label="订单ID" width="120" />
          <el-table-column prop="customer" label="客户名称" />
          <el-table-column prop="product" label="产品" />
          <el-table-column prop="amount" label="金额">
            <template #default="scope">
              <span class="font-semibold text-green-600">
                ¥{{ scope.row.amount.toLocaleString() }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="date" label="创建时间" />
          <el-table-column label="操作" width="150">
            <template #default>
              <el-button size="small" type="primary" link> 查看 </el-button>
              <el-button size="small" type="warning" link> 编辑 </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import {
  TrendCharts,
  PieChart,
  Refresh,
  Download,
  User,
  Goods,
  Money,
  DocumentCopy
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 响应式数据
const selectedPeriod = ref('month');

// 统计数据
const stats = reactive([
  {
    title: '总用户数',
    value: '12,345',
    change: '+12.5%',
    trend: 'up',
    icon: User,
    iconColor: '#409EFF',
    iconBg: 'bg-blue-100'
  },
  {
    title: '总订单数',
    value: '8,967',
    change: '+8.2%',
    trend: 'up',
    icon: DocumentCopy,
    iconColor: '#67C23A',
    iconBg: 'bg-green-100'
  },
  {
    title: '销售额',
    value: '¥234,567',
    change: '+15.3%',
    trend: 'up',
    icon: Money,
    iconColor: '#E6A23C',
    iconBg: 'bg-yellow-100'
  },
  {
    title: '产品数量',
    value: '456',
    change: '-2.1%',
    trend: 'down',
    icon: Goods,
    iconColor: '#F56C6C',
    iconBg: 'bg-red-100'
  }
]);

// 表格数据
const tableData = reactive([
  {
    id: 'ORD001',
    customer: '张三',
    product: 'iPhone 14 Pro',
    amount: 8999,
    status: '已完成',
    date: '2024-01-15 10:30'
  },
  {
    id: 'ORD002',
    customer: '李四',
    product: 'MacBook Pro',
    amount: 15999,
    status: '处理中',
    date: '2024-01-15 09:45'
  },
  {
    id: 'ORD003',
    customer: '王五',
    product: 'iPad Air',
    amount: 4299,
    status: '已发货',
    date: '2024-01-15 08:20'
  },
  {
    id: 'ORD004',
    customer: '赵六',
    product: 'AirPods Pro',
    amount: 1899,
    status: '已完成',
    date: '2024-01-14 16:55'
  }
]);

// 方法
const getStatusType = status => {
  const statusMap = {
    已完成: 'success',
    处理中: 'warning',
    已发货: 'info',
    已取消: 'danger'
  };
  return statusMap[status] || 'info';
};

const refreshData = () => {
  ElMessage.success('数据已刷新');
  // 这里可以添加实际的数据刷新逻辑
};
</script>

<style scoped>
/* 响应式调整 */
@media (width <= 768px) {
  .chart-container {
    height: 200px;
  }
}

.chart-card {
  height: 100%;
}

.chart-container {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border-radius: 8px;
}

/* 自定义表格样式 */
:deep(.el-table) {
  overflow: hidden;
  border-radius: 8px;
}

:deep(.el-table__header) {
  background-color: #f8fafc;
}

/* 卡片悬停效果 */
.shadow-card {
  box-shadow:
    0 2px 4px 0 rgb(0 0 0 / 12%),
    0 0 6px 0 rgb(0 0 0 / 4%);
}

.shadow-card-hover {
  box-shadow:
    0 4px 8px 0 rgb(0 0 0 / 12%),
    0 2px 4px 0 rgb(0 0 0 / 8%);
}
</style>
