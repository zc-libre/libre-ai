<template>
  <div class="grid-layout-preview">
    <div class="grid-container">
      <div class="grid-item" v-for="i in 6" :key="i">
        <div class="item-header"></div>
        <div class="item-content">
          <div class="chart-placeholder"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 网格布局预览组件
</script>

<style scoped>
.grid-layout-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 4px;
  width: 100%;
  height: 100%;
}

.grid-item {
  background: #f0f2f5;
  border-radius: 4px;
  padding: 4px;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.item-header {
  height: 15%;
  background: white;
  border-radius: 2px;
  position: relative;
}

.item-header::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 8px;
  transform: translateY(-50%);
  width: 30%;
  height: 4px;
  background: #409eff;
  border-radius: 2px;
}

.item-content {
  flex: 1;
  background: white;
  border-radius: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
}

.chart-placeholder {
  width: 80%;
  height: 60%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  opacity: 0.3;
  position: relative;
}

.grid-item:nth-child(2) .chart-placeholder {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.grid-item:nth-child(3) .chart-placeholder {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.grid-item:nth-child(4) .chart-placeholder {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.grid-item:nth-child(5) .chart-placeholder {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.grid-item:nth-child(6) .chart-placeholder {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}
</style>
