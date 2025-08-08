<template>
  <div class="dashboard-preview">
    <div class="gauge-container">
      <div class="gauge gauge-large">
        <div class="gauge-circle">
          <div class="gauge-pointer"></div>
        </div>
      </div>
      <div class="gauge gauge-small">
        <div class="gauge-circle">
          <div class="gauge-pointer"></div>
        </div>
      </div>
      <div class="gauge gauge-small">
        <div class="gauge-circle">
          <div class="gauge-pointer"></div>
        </div>
      </div>
    </div>
    <div class="status-bar">
      <div class="status-item" v-for="i in 4" :key="i"></div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.gauge-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.gauge {
  background: #f0f2f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
}

.gauge-large {
  width: 40%;
  height: 80%;
}

.gauge-small {
  width: 25%;
  height: 60%;
}

.gauge-circle {
  width: 100%;
  aspect-ratio: 1;
  background: white;
  border-radius: 50%;
  position: relative;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.gauge-circle::before {
  content: '';
  position: absolute;
  top: 10%;
  left: 10%;
  right: 10%;
  bottom: 10%;
  border: 3px solid #409eff;
  border-radius: 50%;
  border-bottom-color: transparent;
  border-right-color: transparent;
  transform: rotate(-45deg);
}

.gauge-pointer {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 2px;
  height: 35%;
  background: #e6a23c;
  transform-origin: bottom center;
  transform: translate(-50%, -100%) rotate(30deg);
  border-radius: 1px;
}

.gauge-pointer::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 6px;
  background: #303133;
  border-radius: 50%;
}

.status-bar {
  height: 25px;
  display: flex;
  gap: 4px;
}

.status-item {
  flex: 1;
  background: #f0f2f5;
  border-radius: 4px;
  position: relative;
}

.status-item::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 10px;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  background: #67c23a;
  border-radius: 50%;
  box-shadow: 0 0 4px rgba(103, 194, 58, 0.5);
}

.status-item:nth-child(3)::after {
  background: #e6a23c;
  box-shadow: 0 0 4px rgba(230, 162, 60, 0.5);
}
</style>
