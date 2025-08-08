<template>
  <div class="fullscreen-layout-preview">
    <div class="fullscreen-container">
      <!-- 顶部标题栏 -->
      <div class="header-bar">
        <div class="title-placeholder"></div>
        <div class="time-placeholder"></div>
      </div>

      <!-- 主体内容区 -->
      <div class="main-content">
        <!-- 左侧大图表 -->
        <div class="left-section">
          <div class="large-chart">
            <div class="chart-title"></div>
            <div class="chart-body"></div>
          </div>
        </div>

        <!-- 右侧指标区 -->
        <div class="right-section">
          <div class="metric-card" v-for="i in 3" :key="i">
            <div class="metric-value"></div>
            <div class="metric-label"></div>
          </div>
        </div>
      </div>

      <!-- 底部滚动区 -->
      <div class="footer-bar">
        <div class="scroll-item" v-for="i in 5" :key="i"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 全屏布局预览组件
</script>

<style scoped>
.fullscreen-layout-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
}

.fullscreen-container {
  width: 100%;
  height: 100%;
  background: #2c3e50;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  padding: 4px;
  gap: 3px;
}

.header-bar {
  height: 12%;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 6px;
}

.title-placeholder {
  width: 30%;
  height: 6px;
  background: #409eff;
  border-radius: 2px;
}

.time-placeholder {
  width: 15%;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
}

.main-content {
  flex: 1;
  display: flex;
  gap: 3px;
}

.left-section {
  flex: 2;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
  padding: 4px;
}

.large-chart {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chart-title {
  height: 8px;
  width: 40%;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.chart-body {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  opacity: 0.3;
}

.right-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.metric-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 3px;
}

.metric-value {
  width: 50%;
  height: 8px;
  background: #67c23a;
  border-radius: 2px;
}

.metric-card:nth-child(2) .metric-value {
  background: #e6a23c;
}

.metric-card:nth-child(3) .metric-value {
  background: #f56c6c;
}

.metric-label {
  width: 30%;
  height: 3px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 1px;
}

.footer-bar {
  height: 10%;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 0 4px;
  overflow: hidden;
}

.scroll-item {
  flex: 0 0 auto;
  width: 40px;
  height: 60%;
  background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
  border-radius: 2px;
  opacity: 0.3;
}
</style>
