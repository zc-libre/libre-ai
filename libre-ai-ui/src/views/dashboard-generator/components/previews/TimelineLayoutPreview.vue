<template>
  <div class="timeline-preview">
    <div class="timeline-container">
      <div class="timeline-line"></div>
      <div
        class="timeline-item"
        v-for="i in 5"
        :key="i"
        :style="{ left: `${(i - 1) * 22}%` }"
      >
        <div class="timeline-point"></div>
        <div class="timeline-content"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.timeline-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.timeline-container {
  width: 100%;
  height: 60%;
  position: relative;
}

.timeline-line {
  position: absolute;
  top: 50%;
  left: 5%;
  right: 5%;
  height: 2px;
  background: #dcdfe6;
  transform: translateY(-50%);
}

.timeline-item {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.timeline-point {
  width: 10px;
  height: 10px;
  background: #409eff;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 2;
}

.timeline-content {
  width: 30px;
  height: 20px;
  background: #f0f2f5;
  border-radius: 4px;
  position: relative;
  margin-top: 8px;
}

.timeline-content::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60%;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  opacity: 0.5;
}

.timeline-item:nth-child(even) .timeline-content {
  margin-top: -40px;
}
</style>
