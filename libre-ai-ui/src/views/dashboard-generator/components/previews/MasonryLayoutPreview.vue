<template>
  <div class="masonry-layout-preview">
    <div class="masonry-container">
      <div class="masonry-column">
        <div class="masonry-item item-tall">
          <div class="item-header" />
          <div class="item-content tall" />
        </div>
        <div class="masonry-item item-short">
          <div class="item-header" />
          <div class="item-content short" />
        </div>
      </div>
      <div class="masonry-column">
        <div class="masonry-item item-short">
          <div class="item-header" />
          <div class="item-content short" />
        </div>
        <div class="masonry-item item-medium">
          <div class="item-header" />
          <div class="item-content medium" />
        </div>
        <div class="masonry-item item-tiny">
          <div class="item-header" />
          <div class="item-content tiny" />
        </div>
      </div>
      <div class="masonry-column">
        <div class="masonry-item item-medium">
          <div class="item-header" />
          <div class="item-content medium" />
        </div>
        <div class="masonry-item item-medium">
          <div class="item-header" />
          <div class="item-content medium" />
        </div>
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
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 4px;
}

.masonry-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 3px;
  background: #f0f2f5;
  border-radius: 4px;
}

.item-header {
  position: relative;
  height: 6px;
  background: white;
  border-radius: 2px;
}

.item-header::after {
  position: absolute;
  top: 50%;
  left: 4px;
  width: 35%;
  height: 3px;
  content: '';
  background: #409eff;
  border-radius: 1px;
  transform: translateY(-50%);
}

.item-content {
  position: relative;
  overflow: hidden;
  background: white;
  border-radius: 2px;
}

.item-content::after {
  position: absolute;
  right: 10%;
  bottom: 15%;
  left: 10%;
  height: 40%;
  content: '';
  border-radius: 2px;
  opacity: 0.3;
}

.item-content.tall {
  height: 50px;
}

.item-content.tall::after {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.item-content.medium {
  height: 30px;
}

.item-content.medium::after {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.item-content.short {
  height: 20px;
}

.item-content.short::after {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.item-content.tiny {
  height: 15px;
}

.item-content.tiny::after {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.item-tall {
  flex: 0 0 auto;
}

.item-medium {
  flex: 0 0 auto;
}

.item-short {
  flex: 0 0 auto;
}

.item-tiny {
  flex: 0 0 auto;
}
</style>
