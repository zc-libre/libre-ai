<template>
  <div class="masonry-layout-preview">
    <div class="masonry-container">
      <div class="masonry-column">
        <div class="masonry-item item-tall">
          <div class="item-header"></div>
          <div class="item-content tall"></div>
        </div>
        <div class="masonry-item item-short">
          <div class="item-header"></div>
          <div class="item-content short"></div>
        </div>
      </div>
      <div class="masonry-column">
        <div class="masonry-item item-short">
          <div class="item-header"></div>
          <div class="item-content short"></div>
        </div>
        <div class="masonry-item item-medium">
          <div class="item-header"></div>
          <div class="item-content medium"></div>
        </div>
        <div class="masonry-item item-tiny">
          <div class="item-header"></div>
          <div class="item-content tiny"></div>
        </div>
      </div>
      <div class="masonry-column">
        <div class="masonry-item item-medium">
          <div class="item-header"></div>
          <div class="item-content medium"></div>
        </div>
        <div class="masonry-item item-medium">
          <div class="item-header"></div>
          <div class="item-content medium"></div>
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
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.masonry-item {
  background: #f0f2f5;
  border-radius: 4px;
  padding: 3px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.item-header {
  height: 6px;
  background: white;
  border-radius: 2px;
  position: relative;
}

.item-header::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 4px;
  transform: translateY(-50%);
  width: 35%;
  height: 3px;
  background: #409eff;
  border-radius: 1px;
}

.item-content {
  background: white;
  border-radius: 2px;
  position: relative;
  overflow: hidden;
}

.item-content::after {
  content: '';
  position: absolute;
  bottom: 15%;
  left: 10%;
  right: 10%;
  height: 40%;
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
