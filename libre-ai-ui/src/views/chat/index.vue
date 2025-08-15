<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import Chat from '@/views/chat/Chat.vue';
import { getMessages } from '@/api/aigc/chat';
import { useChatStore } from '@/views/chat/store/useChatStore';
import { useUserStore } from '@/store/modules/user';
import Header from '@/views/chat/Header.vue';

const loading = ref(true);
const chatStore = useChatStore();
const userStore = useUserStore();

onMounted(async () => {
});

</script>

<template>
  <div class="modern-chat-container">
    <!-- 现代化全屏聊天界面 -->
    <div class="chat-layout">
      <!-- 优化的头部工具栏 -->
      <!-- 聊天内容区域 -->
      <div class="chat-main-content">
        <Chat/>
        <!-- 加载状态 -->
      </div>

    </div>
  </div>
</template>

<style lang="scss" scoped>
</style>
