<script lang="ts" setup>
import DocList from './DocsList/index.vue';
import DocsSlice from './DocsSlice/index.vue';
import { IconifyIconOffline as SvgIcon } from '@/components/ReIcon';
import DocsSliceSearch from './DocsSliceSearch/index.vue';
import ImportFile from './ImportFile/index.vue';
import { onMounted, ref } from 'vue';
import { Icon } from '@iconify/vue';
import { useRouter } from 'vue-router';
import {
  ElRow,
  ElCol,
  ElButton,
  ElDivider,
  ElInput,
  ElTabs,
  ElTabPane
} from 'element-plus';
import { getById } from '@/api/aigc/knowledge';

const router = useRouter();
const active = ref('import-file');
const menuOptions = ref([
  {
    label: '数据导入',
    key: 'import-file',
    icon: 'ep:upload'
  },
  {
    label: '文档管理',
    key: 'doc-list',
    icon: 'ep:document'
  }
]);

const knowledge = ref<any>({});
onMounted(async () => {
  const id = router.currentRoute.value.params.id;
  knowledge.value = await getById(String(id));
  active.value = menuOptions.value[0].key;

  menuOptions.value.push(
    {
      label: '切片管理',
      key: 'slice-list',
      icon: 'ep:files'
    },
    {
      label: '向量搜索',
      key: 'slice-search',
      icon: 'ep:search'
    }
  );
});

function handleSelect(key: string) {
  active.value = key;
}

function handleReturn() {
  router.back();
}
</script>

<template>
  <div class="mt-2" style="height: calc(100vh - 130px) !important">
    <el-row :gutter="13" class="h-full">
      <el-col :span="5" class="bg-white p-4 rounded-md">
        <el-button
          class="mb-4 w-full"
          plain
          size="small"
          type="primary"
          @click="handleReturn"
        >
          <Icon icon="ep:back" class="mr-1" />
          知识库列表
        </el-button>

        <div class="flex items-center gap-2">
          <div class="relative bg-blue-100 p-2 rounded">
            <SvgIcon class="text-lg" icon="ep:document" />
          </div>
          <span class="font-semibold text-[16px]">{{ knowledge.name }}</span>
        </div>
        <div class="text-[13px] text-gray-400 mt-3">{{ knowledge.des }}</div>
        <el-divider class="my-3" />
        <div class="my-3 flex flex-col gap-2">
          <div class="text-xs">知识库ID</div>
          <el-input v-model="knowledge.id" readonly />
        </div>
        <div class="my-3 flex flex-col gap-2">
          <div class="text-xs">关联向量数据库</div>
          <div v-if="knowledge.embedStore == null" class="py-2 text-gray-400">
            没有配置关联向量数据库
          </div>
          <el-input v-else v-model="knowledge.embedStore.name" readonly />
        </div>
        <div class="my-3 flex flex-col gap-2">
          <div class="text-xs">关联向量化模型</div>
          <div v-if="knowledge.embedModel == null" class="py-2 text-gray-400">
            没有配置关联向量化模型
          </div>
          <el-input v-else v-model="knowledge.embedModel.name" readonly />
        </div>
      </el-col>
      <el-col :span="19" class="h-full bg-white p-4 overflow-y-auto rounded-md">
        <el-tabs v-model="active" class="mb-6" @tab-change="handleSelect">
          <el-tab-pane
            v-for="item in menuOptions"
            :key="item.key"
            :name="item.key"
            :label="item.label"
          >
            <template #label>
              <Icon :icon="item.icon" class="mr-1" />
              <span class="font-bold">{{ item.label }}</span>
            </template>
          </el-tab-pane>
        </el-tabs>
        <DocList v-if="active == 'doc-list'" />
        <DocsSlice v-if="active == 'slice-list'" />
        <DocsSliceSearch v-if="active == 'slice-search'" />
        <ImportFile v-if="active == 'import-file'" :data="knowledge" />
      </el-col>
    </el-row>
  </div>
</template>

<style lang="less" scoped></style>
