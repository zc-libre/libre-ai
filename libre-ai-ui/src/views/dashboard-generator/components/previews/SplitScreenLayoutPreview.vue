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
  display: flex;
  gap: 4px;
  width: 100%;
  height: 100%;
  padding: 8px;
}

.left-panel,
.right-panel {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 4px;
}

.divider {
  width: 2px;
  background: #dcdfe6;
  border-radius: 1px;
}

.panel-header {
  position: relative;
  height: 20px;
  background: #f0f2f5;
  border-radius: 4px;
}

.panel-header::after {
  position: absolute;
  top: 50%;
  left: 10px;
  width: 40%;
  height: 6px;
  content: '';
  background: #409eff;
  border-radius: 2px;
  transform: translateY(-50%);
}

.panel-content {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 4px;
}

.chart-item {
  position: relative;
  flex: 1;
  background: #f0f2f5;
  border-radius: 4px;
}

.chart-item::before {
  position: absolute;
  right: 15%;
  bottom: 20%;
  left: 15%;
  height: 30%;
  content: '';
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  opacity: 0.3;
}
</style>
