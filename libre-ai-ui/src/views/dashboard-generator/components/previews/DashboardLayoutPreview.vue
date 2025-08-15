<template>
  <div class="dashboard-preview">
    <div class="gauge-container">
      <div class="gauge gauge-large">
        <div class="gauge-circle">
          <div class="gauge-pointer" />
        </div>
      </div>
      <div class="gauge gauge-small">
        <div class="gauge-circle">
          <div class="gauge-pointer" />
        </div>
      </div>
      <div class="gauge gauge-small">
        <div class="gauge-circle">
          <div class="gauge-pointer" />
        </div>
      </div>
    </div>
    <div class="status-bar">
      <div v-for="i in 4" :key="i" class="status-item" />
    </div>
  </div>
</template>

<style scoped>
.dashboard-preview {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%;
  height: 100%;
  padding: 8px;
}

.gauge-container {
  display: flex;
  flex: 1;
  gap: 8px;
  align-items: center;
  justify-content: center;
}

.gauge {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  background: #f0f2f5;
  border-radius: 8px;
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
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  background: white;
  border-radius: 50%;
  box-shadow: inset 0 2px 4px rgb(0 0 0 / 10%);
}

.gauge-circle::before {
  position: absolute;
  inset: 10%;
  content: '';
  border: 3px solid #409eff;
  border-right-color: transparent;
  border-bottom-color: transparent;
  border-radius: 50%;
  transform: rotate(-45deg);
}

.gauge-pointer {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 2px;
  height: 35%;
  background: #e6a23c;
  border-radius: 1px;
  transform: translate(-50%, -100%) rotate(30deg);
  transform-origin: bottom center;
}

.gauge-pointer::after {
  position: absolute;
  bottom: -4px;
  left: 50%;
  width: 6px;
  height: 6px;
  content: '';
  background: #303133;
  border-radius: 50%;
  transform: translateX(-50%);
}

.status-bar {
  display: flex;
  gap: 4px;
  height: 25px;
}

.status-item {
  position: relative;
  flex: 1;
  background: #f0f2f5;
  border-radius: 4px;
}

.status-item::after {
  position: absolute;
  top: 50%;
  left: 10px;
  width: 8px;
  height: 8px;
  content: '';
  background: #67c23a;
  border-radius: 50%;
  box-shadow: 0 0 4px rgb(103 194 58 / 50%);
  transform: translateY(-50%);
}

.status-item:nth-child(3)::after {
  background: #e6a23c;
  box-shadow: 0 0 4px rgb(230 162 60 / 50%);
}
</style>
