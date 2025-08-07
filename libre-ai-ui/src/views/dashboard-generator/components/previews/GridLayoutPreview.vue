<template>
  <div class="grid-layout-preview">
    <div class="grid-container">
      <div class="grid-item item-1" />
      <div class="grid-item item-2" />
      <div class="grid-item item-3" />
      <div class="grid-item item-4" />
    </div>
  </div>
</template>

<script setup lang="ts">
// 网格布局预览组件
</script>

<style scoped>
.grid-layout-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
}

.grid-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 4px;
  width: 100%;
  height: 100%;
}

.grid-item {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  opacity: 0.8;
}

.item-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.item-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.item-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.item-4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}
</style>
