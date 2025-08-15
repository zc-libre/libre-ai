<template>
  <div class="fullscreen-layout-preview">
    <div class="fullscreen-container">
      <!-- 顶部标题栏 -->
      <div class="header-bar">
        <div class="title-placeholder" />
        <div class="time-placeholder" />
      </div>

      <!-- 主体内容区 -->
      <div class="main-content">
        <!-- 左侧大图表 -->
        <div class="left-section">
          <div class="large-chart">
            <div class="chart-title" />
            <div class="chart-body" />
          </div>
        </div>

        <!-- 右侧指标区 -->
        <div class="right-section">
          <div v-for="i in 3" :key="i" class="metric-card">
            <div class="metric-value" />
            <div class="metric-label" />
          </div>
        </div>
      </div>

      <!-- 底部滚动区 -->
      <div class="footer-bar">
        <div v-for="i in 5" :key="i" class="scroll-item" />
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
  display: flex;
  flex-direction: column;
  gap: 3px;
  width: 100%;
  height: 100%;
  padding: 4px;
  background: #f0f2f5;
  border-radius: 4px;
}

.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 12%;
  padding: 0 6px;
  background: white;
  border-radius: 2px;
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
  background: #c0c4cc;
  border-radius: 2px;
}

.main-content {
  display: flex;
  flex: 1;
  gap: 3px;
}

.left-section {
  flex: 2;
  padding: 4px;
  background: white;
  border-radius: 2px;
}

.large-chart {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
  height: 100%;
}

.chart-title {
  width: 40%;
  height: 8px;
  background: #c0c4cc;
  border-radius: 2px;
}

.chart-body {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  opacity: 0.3;
}

.right-section {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 3px;
}

.metric-card {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 3px;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 2px;
}

.metric-value {
  width: 50%;
  height: 8px;
  background: #409eff;
  border-radius: 2px;
}

.metric-card:nth-child(2) .metric-value {
  background: #67c23a;
}

.metric-card:nth-child(3) .metric-value {
  background: #e6a23c;
}

.metric-label {
  width: 30%;
  height: 3px;
  background: #c0c4cc;
  border-radius: 1px;
}

.footer-bar {
  display: flex;
  gap: 3px;
  align-items: center;
  height: 10%;
  padding: 0 4px;
  overflow: hidden;
  background: white;
  border-radius: 2px;
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
