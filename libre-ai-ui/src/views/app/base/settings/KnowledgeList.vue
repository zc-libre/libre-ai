<script lang="ts" setup>
import { nextTick, ref } from 'vue';
import { IconifyIconOffline as SvgIcon } from '@/components/ReIcon';
import { list as getList } from '@/api/aigc/knowledge';
import { useAppStore } from '@/views/app/store';
import {
  ElMessage,
  ElDialog,
  ElAlert,
  ElScrollbar,
  ElButton,
  ElTag,
  ElDivider
} from 'element-plus';

const ms = ElMessage;
const knowledges = ref();
const appStore = useAppStore();
const dialogVisible = ref(false);

async function show() {
  knowledges.value = await getList({});
  await nextTick();
  dialogVisible.value = true;
}

function onAdd(item) {
  appStore.addKnowledge(item);
  ms.success('关联成功');
}

function onRemove(item) {
  appStore.removeKnowledge(item);
  ms.success('移除成功');
}

defineExpose({ show });
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    title="关联知识库"
    width="40%"
    :close-on-click-modal="false"
  >
    <ElAlert
      class="mb-2"
      title="注意：只能选择相同纬度向量库配置（以及相同向量模型）的知识库"
      type="info"
      :closable="false"
    />

    <ElScrollbar max-height="400px">
      <div class="space-y-3">
        <div
          v-for="item in knowledges"
          :key="item.id"
          class="p-4 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
        >
          <div class="flex items-center justify-between">
            <div class="flex gap-1 items-center">
              <SvgIcon class="text-3xl" icon="flat-color-icons:document" />
              <div>{{ item.name }}</div>

              <ElDivider v-if="item.embedModel != null" direction="vertical" />
              <ElTag
                v-if="item.embedModel != null"
                round
                type="success"
                size="small"
              >
                <div class="flex gap-1 px-1.5">
                  <SvgIcon icon="octicon:ai-model-24" />
                  <span>{{ item.embedModel.name }}</span>
                </div>
              </ElTag>

              <ElDivider
                v-if="item.embedStore != null"
                class="!mx-1"
                direction="vertical"
              />
              <ElTag
                v-if="item.embedStore != null"
                round
                type="primary"
                size="small"
              >
                <div class="flex gap-1 px-1.5">
                  <SvgIcon icon="material-symbols:database-outline" />
                  <span>{{ item.embedStore.name }}</span>
                </div>
              </ElTag>
            </div>

            <ElButton
              v-if="!appStore.knowledgeIds.includes(item.id)"
              round
              size="small"
              type="info"
              @click="onAdd(item)"
            >
              关联
            </ElButton>
            <ElButton
              v-else
              round
              size="small"
              type="danger"
              @click="onRemove(item)"
            >
              移除
            </ElButton>
          </div>
        </div>
      </div>
    </ElScrollbar>
  </ElDialog>
</template>

<style lang="scss" scoped></style>
