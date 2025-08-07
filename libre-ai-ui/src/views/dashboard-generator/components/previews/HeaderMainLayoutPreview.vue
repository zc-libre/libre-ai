<template>
  <div class="header-main-preview">
    <!-- 顶部 KPI 区域 -->
    <div class="header-section">
      <div class="kpi-card" v-for="i in 4" :key="i">
        <div class="kpi-value"></div>
        <div class="kpi-label"></div>
      </div>
    </div>
    <!-- 主体图表区域 -->
    <div class="main-section">
      <div class="chart-card large"></div>
      <div class="chart-card medium"></div>
      <div class="chart-card medium"></div>
    </div>
  </div>
</template>

<style scoped>
.header-main-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.header-section {
  display: flex;
  gap: 4px;
  height: 25%;
}

.kpi-card {
  flex: 1;
  background: #f0f2f5;
  border-radius: 4px;
  padding: 4px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 2px;
}

.kpi-value {
  width: 60%;
  height: 8px;
  background: #409eff;
  border-radius: 2px;
}

.kpi-label {
  width: 40%;
  height: 4px;
  background: #c0c4cc;
  border-radius: 2px;
}

.main-section {
  flex: 1;
  display: grid;
  grid-template-columns: 2fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 4px;
}

.chart-card {
  background: #f0f2f5;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.chart-card.large {
  grid-row: span 2;
}

.chart-card::after {
  content: '';
  position: absolute;
  bottom: 10%;
  left: 10%;
  right: 10%;
  height: 40%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  opacity: 0.3;
}
</style>