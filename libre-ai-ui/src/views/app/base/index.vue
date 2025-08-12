<script lang="ts" setup>
import PromptPage from '@/views/app/base/prompt/index.vue';
import SettingsPage from '@/views/app/base/settings/index.vue';
import Chat from '@/views/chat/Chat.vue';
import { useRoute } from 'vue-router';
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useAppStore } from '../store';
import { useChatStore } from '@/views/chat/store/useChatStore';
import { getAppInfo } from '@/api/aigc/chat';
import { formatToDateTime } from '@/utils/dateUtil';

const route = useRoute();
const appStore = useAppStore();
const chatStore = useChatStore();
const form = ref<any>({});
const loading = ref(false);
const ms = ElMessage;

// 分割面板尺寸
const leftSize = ref(30);
const middleSize = ref(40);

onMounted(async () => {
  await fetchData();
});

async function fetchData() {
  loading.value = true;
  const id = route.params.id;
  const res = await getAppInfo({
    appId: id,
    conversationId: null
  });
  const data = res.result || res;
  form.value = data;
  appStore.info = data;
  appStore.knowledgeIds = data.knowledgeIds == null ? [] : data.knowledgeIds;
  appStore.modelId = data.modelId == null ? null : data.modelId;
  appStore.model = data.model == null ? null : data.model;
  appStore.knowledges = data.knowledges == null ? [] : data.knowledges;
  chatStore.modelId = data.modelId == null ? null : data.modelId;
  chatStore.appId = data.id;
  // 对于应用调试页面，不存储聊天记录
  // chatStore.conversationId = data.id;
  // chatStore.messages = await getMessages(chatStore.conversationId!);
  loading.value = false;
}

async function onSave() {
  loading.value = true;
  form.value.saveTime = formatToDateTime(new Date());
  await appStore.updateInfo();
  ms.success('应用配置保存成功');
  loading.value = false;
}
</script>

<template>
  <div class="flex h-full">
    <!-- 左侧面板 - Prompt -->
    <div class="h-full p-2" :style="{ width: leftSize + '%' }">
      <div class="p-2 h-full bg-white rounded-lg">
        <PromptPage @update="onSave" />
      </div>
    </div>

    <!-- 分隔条 -->
    <div
      class="w-px bg-gray-300 cursor-col-resize hover:bg-gray-400 transition-colors"
    />

    <!-- 中间面板 - Settings -->
    <div class="h-full p-2" :style="{ width: middleSize + '%' }">
      <div class="p-2 h-full bg-white rounded-lg">
        <SettingsPage @update="onSave" />
      </div>
    </div>

    <!-- 分隔条 -->
    <div
      class="w-px bg-gray-300 cursor-col-resize hover:bg-gray-400 transition-colors"
    />

    <!-- 右侧面板 - Chat -->
    <div class="flex-1 h-full p-2">
      <div class="pb-10 h-full w-full bg-white rounded-xl">
        <Chat />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.cursor-col-resize {
  cursor: col-resize;
}
</style>
