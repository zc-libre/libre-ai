<template>
  <div class="radial-preview">
    <div class="center-circle">
      <div class="center-content"></div>
    </div>
    <div class="satellite satellite-top"></div>
    <div class="satellite satellite-right"></div>
    <div class="satellite satellite-bottom"></div>
    <div class="satellite satellite-left"></div>
    <div class="satellite satellite-top-right"></div>
    <div class="satellite satellite-bottom-right"></div>
    <div class="satellite satellite-bottom-left"></div>
    <div class="satellite satellite-top-left"></div>
  </div>
</template>

<style scoped>
.radial-preview {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.center-circle {
  width: 40%;
  height: 40%;
  background: #f0f2f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  width: 18%;
  height: 18%;
  background: #f0f2f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.satellite::after {
  content: '';
  width: 50%;
  height: 6px;
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
  bottom: 15%;
  right: 15%;
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
