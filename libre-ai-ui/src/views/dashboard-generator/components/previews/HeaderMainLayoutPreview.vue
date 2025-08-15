<template>
  <div class="header-main-preview">
    <!-- 顶部 KPI 区域 -->
    <div class="header-section">
      <div v-for="i in 4" :key="i" class="kpi-card">
        <div class="kpi-value" />
        <div class="kpi-label" />
      </div>
    </div>
    <!-- 主体图表区域 -->
    <div class="main-section">
      <div class="chart-card large" />
      <div class="chart-card medium" />
      <div class="chart-card medium" />
    </div>
  </div>
</template>

<style scoped>
.header-main-preview {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%;
  height: 100%;
  padding: 8px;
}

.header-section {
  display: flex;
  gap: 4px;
  height: 25%;
}

.kpi-card {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 2px;
  align-items: center;
  justify-content: center;
  padding: 4px;
  background: #f0f2f5;
  border-radius: 4px;
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
  display: grid;
  flex: 1;
  grid-template-rows: 1fr 1fr;
  grid-template-columns: 2fr 1fr;
  gap: 4px;
}

.chart-card {
  position: relative;
  overflow: hidden;
  background: #f0f2f5;
  border-radius: 4px;
}

.chart-card.large {
  grid-row: span 2;
}

.chart-card::after {
  position: absolute;
  right: 10%;
  bottom: 10%;
  left: 10%;
  height: 40%;
  content: '';
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  opacity: 0.3;
}
</style>
