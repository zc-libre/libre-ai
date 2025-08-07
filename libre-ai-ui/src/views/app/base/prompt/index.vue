<script lang="ts" setup>
import { useAppStore } from '@/views/app/store';

const emit = defineEmits(['update']);
const appStore = useAppStore();

async function onUpdate() {
  emit('update');
}
</script>

<template>
  <div class="h-full flex flex-col gap-2">
    <div class="p-2 flex justify-between items-center">
      <div class="text-md font-bold">Prompt 提示词</div>
    </div>
    <div class="p-4 pt-0 h-full mb-10">
      <textarea
        v-model="appStore.info.prompt"
        class="h-full w-full bg-transparent outline-none resize-none"
        placeholder="请输入 Prompt 提示词..."
        @blur="onUpdate"
      />
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
