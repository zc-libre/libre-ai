<template>
  <div class="warehouse-dashboard">
    <!-- 左侧区域 -->
    <div class="left-panel">
      <!-- KPI卡片区域 -->
      <div class="kpi-section">
        <div v-for="(kpi, index) in kpiData" :key="index" class="kpi-card">
          <div class="kpi-header">{{ kpi.title }}</div>
          <div class="kpi-value">{{ kpi.value }}</div>
          <div class="kpi-footer">
            <span :class="['trend', kpi.trend]">
              {{ kpi.trend === 'up' ? '↑' : '↓' }} {{ kpi.change }}
            </span>
          </div>
        </div>
      </div>

      <!-- 柱状图区域 -->
      <div class="chart-section">
        <div class="bar-chart">
          <div class="chart-title">{{ barChartData.title }}</div>
          <div class="chart-container">
            <div
              v-for="(value, index) in barChartData.values"
              :key="index"
              class="bar-container"
            >
              <div
                class="bar"
                :style="{ height: (value / maxValue) * 100 + '%' }"
              />
              <div class="bar-label">{{ barChartData.labels[index] }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧区域 -->
    <div class="right-panel">
      <!-- 数据表格区域 -->
      <div class="table-section">
        <div class="data-table">
          <div class="table-header">
            <div
              v-for="column in tableColumns"
              :key="column.key"
              class="table-cell header-cell"
            >
              {{ column.title }}
            </div>
          </div>
          <div class="table-body">
            <div
              v-for="(row, index) in paginatedData"
              :key="index"
              class="table-row"
            >
              <div
                v-for="column in tableColumns"
                :key="column.key"
                class="table-cell"
              >
                {{ row[column.key] }}
              </div>
            </div>
          </div>
          <div v-if="pagination" class="table-pagination">
            <button
              :disabled="currentPage === 1"
              class="pagination-btn"
              @click="prevPage"
            >
              上一页
            </button>
            <span class="page-info">
              {{ currentPage }} / {{ totalPages }}
            </span>
            <button
              :disabled="currentPage === totalPages"
              class="pagination-btn"
              @click="nextPage"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';

// 数据定义
const kpiData = ref([
  { title: '总仓位数', value: '1,248', trend: 'up', change: '2.3%' },
  { title: '已占用仓位', value: '867', trend: 'up', change: '1.8%' },
  { title: '空闲仓位', value: '381', trend: 'down', change: '0.5%' },
  { title: '仓位利用率', value: '69.5%', trend: 'up', change: '1.2%' }
]);

const barChartData = ref({
  title: '各区域仓位使用情况',
  labels: ['A区', 'B区', 'C区', 'D区', 'E区'],
  values: [120, 98, 142, 87, 103]
});

const tableColumns = [
  { key: 'position', title: '仓位编号' },
  { key: 'area', title: '所属区域' },
  { key: 'status', title: '状态' },
  { key: 'goods', title: '存储货物' },
  { key: 'updateTime', title: '更新时间' }
];

const tableData = ref([
  {
    position: 'A-001',
    area: 'A区',
    status: '已占用',
    goods: '电子产品',
    updateTime: '2023-06-15 14:30'
  },
  {
    position: 'A-002',
    area: 'A区',
    status: '空闲',
    goods: '-',
    updateTime: '2023-06-15 14:25'
  },
  {
    position: 'B-015',
    area: 'B区',
    status: '已占用',
    goods: '服装',
    updateTime: '2023-06-15 14:20'
  },
  {
    position: 'C-023',
    area: 'C区',
    status: '已占用',
    goods: '食品',
    updateTime: '2023-06-15 14:15'
  },
  {
    position: 'D-008',
    area: 'D区',
    status: '维护中',
    goods: '-',
    updateTime: '2023-06-15 14:10'
  },
  {
    position: 'E-012',
    area: 'E区',
    status: '已占用',
    goods: '图书',
    updateTime: '2023-06-15 14:05'
  },
  {
    position: 'A-003',
    area: 'A区',
    status: '已占用',
    goods: '化妆品',
    updateTime: '2023-06-15 14:00'
  },
  {
    position: 'B-018',
    area: 'B区',
    status: '空闲',
    goods: '-',
    updateTime: '2023-06-15 13:55'
  },
  {
    position: 'C-025',
    area: 'C区',
    status: '已占用',
    goods: '家具',
    updateTime: '2023-06-15 13:50'
  },
  {
    position: 'D-011',
    area: 'D区',
    status: '已占用',
    goods: '运动器材',
    updateTime: '2023-06-15 13:45'
  }
]);

// 表格分页逻辑
const currentPage = ref(1);
const pageSize = 5;
const pagination = true;

const totalPages = computed(() => Math.ceil(tableData.value.length / pageSize));

const paginatedData = computed(() => {
  if (!pagination) return tableData.value;
  const start = (currentPage.value - 1) * pageSize;
  return tableData.value.slice(start, start + pageSize);
});

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

// 图表计算属性
const maxValue = computed(() => Math.max(...barChartData.value.values));

// 定时器
let kpiTimer = null;
let barTimer = null;
let tableTimer = null;

// 模拟数据更新
const updateKpiData = () => {
  kpiData.value = kpiData.value.map(item => {
    const change = (Math.random() * 3).toFixed(1);
    const trend = Math.random() > 0.5 ? 'up' : 'down';
    const valueNum = parseFloat(item.value.replace(/,/g, ''));
    const newValue =
      trend === 'up'
        ? Math.round(valueNum * (1 + parseFloat(change) / 100))
        : Math.round(valueNum * (1 - parseFloat(change) / 100));

    return {
      ...item,
      value: newValue.toLocaleString(),
      trend,
      change: change + '%'
    };
  });
};

const updateBarData = () => {
  barChartData.value.values = barChartData.value.values.map(value => {
    const change = Math.floor(Math.random() * 20) - 10;
    return Math.max(0, value + change);
  });
};

const updateTableData = () => {
  // 模拟数据更新（实际项目中应从API获取）
  const statuses = ['已占用', '空闲', '维护中'];
  tableData.value = tableData.value.map(row => {
    if (Math.random() > 0.7) {
      return {
        ...row,
        status: statuses[Math.floor(Math.random() * statuses.length)],
        updateTime: new Date()
          .toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
          })
          .replace(/\//g, '-')
      };
    }
    return row;
  });
};

onMounted(() => {
  // 设置定时器更新数据
  kpiTimer = setInterval(updateKpiData, 5000);
  barTimer = setInterval(updateBarData, 5000);
  tableTimer = setInterval(updateTableData, 5000);
});

onBeforeUnmount(() => {
  // 清除定时器
  clearInterval(kpiTimer);
  clearInterval(barTimer);
  clearInterval(tableTimer);
});
</script>

<style scoped>
.warehouse-dashboard {
  display: flex;
  height: 100vh;
  background-color: #f5f7fa;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.left-panel {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-panel {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.kpi-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.kpi-card {
  background: linear-gradient(135deg, #409eff 0%, #79bbff 100%);
  border-radius: 10px;
  padding: 20px;
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: transform 0.3s ease;
}

.kpi-card:hover {
  transform: translateY(-5px);
}

.kpi-header {
  font-size: 16px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.kpi-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
}

.kpi-footer {
  display: flex;
  justify-content: flex-end;
}

.trend {
  font-size: 14px;
  font-weight: bold;
}

.trend.up {
  color: #67c23a;
}

.trend.down {
  color: #f56c6c;
}

.chart-section {
  flex: 1;
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.bar-chart {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.chart-container {
  flex: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  gap: 20px;
}

.bar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  max-width: 80px;
}

.bar {
  width: 100%;
  background: linear-gradient(to top, #409eff, #79bbff);
  border-radius: 4px 4px 0 0;
  min-height: 5px;
  transition: height 0.5s ease;
}

.bar-label {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

.table-section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.data-table {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.table-header {
  display: flex;
  background-color: #409eff;
  color: white;
  border-radius: 4px 4px 0 0;
  overflow: hidden;
}

.table-row {
  display: flex;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.2s;
}

.table-row:hover {
  background-color: #f5f7fa;
}

.table-cell {
  flex: 1;
  padding: 12px 15px;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-cell {
  font-weight: bold;
}

.table-body {
  flex: 1;
  overflow-y: auto;
}

.table-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0 10px;
  gap: 15px;
}

.pagination-btn {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #79bbff;
}

.pagination-btn:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .warehouse-dashboard {
    flex-direction: column;
  }

  .left-panel,
  .right-panel {
    width: 100%;
  }

  .kpi-section {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}

@media (max-width: 768px) {
  .warehouse-dashboard {
    padding: 10px;
  }

  .left-panel,
  .right-panel {
    padding: 10px;
  }

  .kpi-section {
    grid-template-columns: 1fr;
  }

  .chart-container {
    gap: 10px;
  }

  .bar-container {
    max-width: 60px;
  }

  .table-cell {
    padding: 8px 10px;
    font-size: 14px;
  }
}
</style>
