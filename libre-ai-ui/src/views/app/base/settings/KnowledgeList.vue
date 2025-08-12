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
  try {
    const res = await getList({});
    // 处理后端返回的 R 对象
    knowledges.value = res?.result || res || [];
    await nextTick();
    dialogVisible.value = true;
  } catch (error) {
    console.error('获取知识库列表失败:', error);
    ms.error('获取知识库列表失败');
    knowledges.value = [];
  }
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
      <div v-if="knowledges && knowledges.length > 0" class="space-y-3">
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
      <div v-else class="text-center text-gray-500 py-8">暂无可用的知识库</div>
    </ElScrollbar>
  </ElDialog>
</template>

<style lang="scss" scoped></style>
