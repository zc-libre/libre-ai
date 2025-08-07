<template>
  <div class="masonry-layout-preview">
    <div class="masonry-container">
      <div class="masonry-column">
        <div class="masonry-item item-tall" />
        <div class="masonry-item item-short" />
      </div>
      <div class="masonry-column">
        <div class="masonry-item item-short" />
        <div class="masonry-item item-medium" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 瀑布流布局预览组件
</script>

<style scoped>
.masonry-layout-preview {
  width: 100%;
  height: 100%;
  padding: 8px;
}

.masonry-container {
  display: flex;
  gap: 4px;
  width: 100%;
  height: 100%;
}

.masonry-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.masonry-item {
  border-radius: 4px;
}

.item-tall {
  height: 60%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.item-short {
  height: 25%;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.item-medium {
  height: 40%;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}
</style>
