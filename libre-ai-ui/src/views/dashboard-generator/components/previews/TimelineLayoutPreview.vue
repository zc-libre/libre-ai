<template>
  <div class="timeline-preview">
    <div class="timeline-container">
      <div class="timeline-line" />
      <div
        v-for="i in 5"
        :key="i"
        class="timeline-item"
        :style="{ left: `${(i - 1) * 22}%` }"
      >
        <div class="timeline-point" />
        <div class="timeline-content" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.timeline-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  padding: 8px;
}

.timeline-container {
  position: relative;
  width: 100%;
  height: 60%;
}

.timeline-line {
  position: absolute;
  top: 50%;
  right: 5%;
  left: 5%;
  height: 2px;
  background: #dcdfe6;
  transform: translateY(-50%);
}

.timeline-item {
  position: absolute;
  top: 50%;
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  transform: translateY(-50%);
}

.timeline-point {
  z-index: 2;
  width: 10px;
  height: 10px;
  background: #409eff;
  border: 2px solid white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgb(0 0 0 / 10%);
}

.timeline-content {
  position: relative;
  width: 30px;
  height: 20px;
  margin-top: 8px;
  background: #f0f2f5;
  border-radius: 4px;
}

.timeline-content::after {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60%;
  height: 4px;
  content: '';
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  opacity: 0.5;
  transform: translate(-50%, -50%);
}

.timeline-item:nth-child(even) .timeline-content {
  margin-top: -40px;
}
</style>
