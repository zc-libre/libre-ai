<template>
  <div class="grid-layout-preview">
    <div class="grid-container">
      <div v-for="i in 6" :key="i" class="grid-item">
        <div class="item-header" />
        <div class="item-content">
          <div class="chart-placeholder" />
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
  grid-template-rows: repeat(2, 1fr);
  grid-template-columns: repeat(3, 1fr);
  gap: 4px;
  width: 100%;
  height: 100%;
}

.grid-item {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: 4px;
  background: #f0f2f5;
  border-radius: 4px;
}

.item-header {
  position: relative;
  height: 15%;
  background: white;
  border-radius: 2px;
}

.item-header::after {
  position: absolute;
  top: 50%;
  left: 8px;
  width: 30%;
  height: 4px;
  content: '';
  background: #409eff;
  border-radius: 2px;
  transform: translateY(-50%);
}

.item-content {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  padding: 4px;
  background: white;
  border-radius: 2px;
}

.chart-placeholder {
  position: relative;
  width: 80%;
  height: 60%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  opacity: 0.3;
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
