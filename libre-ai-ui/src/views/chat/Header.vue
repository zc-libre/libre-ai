<script lang="ts" setup>
import SvgIcon from '@/components/ReIcon/src/iconifyIconOffline';
import { useChatStore } from '@/views/chat/store/useChatStore';
import { ElMessage, ElMessageBox } from 'element-plus';
import { clean } from '@/api/aigc/chat';
import ModelSelect from '@/views/common/ModelSelect.vue';

defineProps<{
  title: string;
}>();
const emits = defineEmits(['reload']);
const ms = ElMessage;
const chatStore = useChatStore();

function handleClear() {
  if (chatStore.conversationId == null) {
    return;
  }
  ElMessageBox.confirm('确认清除聊天', '清除聊天', {
    confirmButtonText: '是',
    cancelButtonText: '否',
    type: 'warning'
  })
    .then(async () => {
      await clean(chatStore.conversationId);
      emits('reload');
      ms.success('聊天记录清除成功');
    })
    .catch(() => {});
}
</script>

<template>
  <div class="mb-3 flex flex-wrap justify-between items-center">
    <div class="font-bold flex justify-center items-center flex-wrap gap-2">
      <SvgIcon :icon="'ion:sparkles-outline'" class="text-lg" />
      <span>{{ title }}</span>
    </div>
    <div class="flex items-center gap-2">
      <ModelSelect
        :id="chatStore.modelId"
        class="w-auto"
        style="min-width: 180px"
      />

      <el-button plain size="small" type="warning" @click="handleClear">
        <template #icon>
          <SvgIcon :icon="'fluent:delete-12-regular'" class="text-[14px]" />
        </template>
        清空聊天
      </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
