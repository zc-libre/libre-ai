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
  await fetch();
});

async function fetch() {
  loading.value = true;
  chatStore.conversationId = userStore.info.id;
  chatStore.messages = await getMessages(userStore.info.id);
  loading.value = false;
}
</script>

<template>
  <div class="chat-container">
    <el-card class="view-container">
      <Header title="AI聊天助手" @reload="fetch" />
      <div class="chat-content">
        <main ref="contentRef" class="view-main overflow-y-auto">
          <Chat />
        </main>
      </div>
    </el-card>
  </div>
</template>

<style lang="scss" scoped></style>
