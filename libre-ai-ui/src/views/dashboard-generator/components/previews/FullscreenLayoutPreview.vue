<template>
  <div class="fullscreen-layout-preview">
    <div class="fullscreen-container">
      <div class="fullscreen-content">
        <div class="center-element" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 全屏布局预览组件
</script>

<style scoped>
.fullscreen-layout-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
}

.fullscreen-container {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fullscreen-content {
  width: 80%;
  height: 80%;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.center-element {
  width: 60%;
  height: 60%;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}
</style>
