<template>
  <div class="radial-preview">
    <div class="center-circle">
      <div class="center-content" />
    </div>
    <div class="satellite satellite-top" />
    <div class="satellite satellite-right" />
    <div class="satellite satellite-bottom" />
    <div class="satellite satellite-left" />
    <div class="satellite satellite-top-right" />
    <div class="satellite satellite-bottom-right" />
    <div class="satellite satellite-bottom-left" />
    <div class="satellite satellite-top-left" />
  </div>
</template>

<style scoped>
.radial-preview {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.center-circle {
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40%;
  height: 40%;
  background: #f0f2f5;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgb(0 0 0 / 10%);
}

.center-content {
  width: 60%;
  height: 60%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  opacity: 0.4;
}

.satellite {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18%;
  height: 18%;
  background: #f0f2f5;
  border-radius: 8px;
}

.satellite::after {
  width: 50%;
  height: 6px;
  content: '';
  background: #409eff;
  border-radius: 2px;
  opacity: 0.6;
}

.satellite-top {
  top: 5%;
  left: 50%;
  transform: translateX(-50%);
}

.satellite-right {
  top: 50%;
  right: 5%;
  transform: translateY(-50%);
}

.satellite-bottom {
  bottom: 5%;
  left: 50%;
  transform: translateX(-50%);
}

.satellite-left {
  top: 50%;
  left: 5%;
  transform: translateY(-50%);
}

.satellite-top-right {
  top: 15%;
  right: 15%;
  width: 15%;
  height: 15%;
}

.satellite-bottom-right {
  right: 15%;
  bottom: 15%;
  width: 15%;
  height: 15%;
}

.satellite-bottom-left {
  bottom: 15%;
  left: 15%;
  width: 15%;
  height: 15%;
}

.satellite-top-left {
  top: 15%;
  left: 15%;
  width: 15%;
  height: 15%;
}
</style>
