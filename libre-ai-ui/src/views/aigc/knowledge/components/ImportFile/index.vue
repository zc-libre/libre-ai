<script lang="ts" setup>
import DocImport from './components/DocImport.vue';

interface Props {
  data?: any;
}
defineProps<Props>();
</script>

<template>
  <div class="import-container">
    <DocImport :data="data" />
  </div>
</template>

<style lang="scss" scoped>
.import-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
