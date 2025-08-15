<template>
  <div class="sidebar-layout-preview">
    <div class="preview-sidebar-container">
      <div class="preview-sidebar">
        <div class="preview-nav-item" />
        <div class="preview-nav-item" />
        <div class="preview-nav-item" />
      </div>
      <div class="preview-main-content">
        <div class="preview-content-header" />
        <div class="preview-content-body" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 侧边栏布局预览组件
</script>

<style scoped>
.sidebar-layout-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
}

.preview-sidebar-container {
  display: flex;
  gap: 4px;
  width: 100%;
  height: 100%;
}

.preview-sidebar {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 30%;
  padding: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
}

.preview-nav-item {
  height: 20%;
  background: rgb(255 255 255 / 30%);
  border-radius: 2px;
}

.preview-main-content {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 4px;
}

.preview-content-header {
  height: 25%;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 4px;
}

.preview-content-body {
  flex: 1;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 4px;
}
</style>
