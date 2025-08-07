<script lang="ts" setup>
import { IconifyIconOffline as SvgIcon } from '@/components/ReIcon';
import KnowledgeList from './KnowledgeList.vue';
import ModelSelect from '@/views/common/ModelSelect.vue';
import { ref } from 'vue';
import { useAppStore } from '@/views/app/store';
import { ElCollapse, ElCollapseItem, ElButton } from 'element-plus';

const emit = defineEmits(['update']);
const appStore = useAppStore();
const knowledgeRef = ref();
const activeNames = ref(['0', '1']);

async function onSaveModel(val) {
  appStore.modelId = val.id;
  emit('update');
}

function onShowKbPane() {
  knowledgeRef.value.show();
}

function onRemove(item) {
  appStore.removeKnowledge(item);
}
</script>

<template>
  <div class="p-2 py-4 flex flex-col gap-3">
    <ElCollapse v-model="activeNames">
      <ElCollapseItem name="0" title="基础配置">
        <div class="flex items-center">
          <div class="w-24">对话模型：</div>
          <ModelSelect :id="appStore.modelId" class="" @update="onSaveModel" />
        </div>
      </ElCollapseItem>

      <ElCollapseItem name="1">
        <template #title>
          <div class="flex items-center justify-between w-full pr-4">
            <span>知识库</span>
            <ElButton text @click.stop="onShowKbPane">
              <SvgIcon class="text-lg" icon="ic:round-plus" />
            </ElButton>
          </div>
        </template>

        <div v-if="appStore.knowledges">
          <div class="space-y-2">
            <div
              v-for="item in appStore.knowledges"
              :key="item.id"
              class="w-full bg-white overflow-hidden rounded-lg hover:bg-gray-100 p-3 transition-colors cursor-pointer"
            >
              <div class="flex items-center justify-between">
                <div class="flex gap-1 items-center">
                  <SvgIcon class="text-3xl" icon="flat-color-icons:document" />
                  <div>{{ item.name }}</div>
                </div>
                <ElButton text @click="onRemove(item)">
                  <SvgIcon icon="gg:remove" />
                </ElButton>
              </div>
            </div>
          </div>

          <div
            v-if="appStore.knowledges.length == 0"
            class="text-gray-400 text-md"
          >
            将文档、URL、三方数据源上传为文本知识库后，用户发送消息时，Bot
            能够引用文本知识中的内容回答用户问题。
          </div>
        </div>
      </ElCollapseItem>
    </ElCollapse>

    <KnowledgeList ref="knowledgeRef" />
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-collapse-item__header) {
  font-weight: 600 !important;
  color: #060709cc;
}

:deep(.el-collapse-item__wrap) {
  background-color: transparent;
}
</style>
