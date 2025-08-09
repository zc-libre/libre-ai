<template>
  <div class="split-screen-preview">
    <div class="left-panel">
      <div class="panel-header" />
      <div class="panel-content">
        <div v-for="i in 3" :key="i" class="chart-item" />
      </div>
    </div>
    <div class="divider" />
    <div class="right-panel">
      <div class="panel-header" />
      <div class="panel-content">
        <div v-for="i in 3" :key="i" class="chart-item" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.split-screen-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
  display: flex;
  gap: 4px;
}

.left-panel,
.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.divider {
  width: 2px;
  background: #dcdfe6;
  border-radius: 1px;
}

.panel-header {
  height: 20px;
  background: #f0f2f5;
  border-radius: 4px;
  position: relative;
}

.panel-header::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 10px;
  transform: translateY(-50%);
  width: 40%;
  height: 6px;
  background: #409eff;
  border-radius: 2px;
}

.panel-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chart-item {
  flex: 1;
  background: #f0f2f5;
  border-radius: 4px;
  position: relative;
}

.chart-item::before {
  content: '';
  position: absolute;
  bottom: 20%;
  left: 15%;
  right: 15%;
  height: 30%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  opacity: 0.3;
}
</style>
