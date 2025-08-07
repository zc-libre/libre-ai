<script lang="ts" setup>
import { ref } from 'vue';
import { IconifyIconOffline as SvgIcon } from '@/components/ReIcon';
import { embeddingSearch } from '@/api/aigc/embedding';
import router from '@/router';
import { ElMessage } from 'element-plus';

const content = ref('');
const loading = ref(false);
const list = ref<any>([]);

async function onSearch() {
  if (content.value === '') {
    list.value = [];
    ElMessage.warning('请先输入搜索内容');
    return;
  }
  loading.value = true;
  list.value = await embeddingSearch({
    content: content.value,
    knowledgeId: router.currentRoute.value.params.id
  }).finally(() => {
    loading.value = false;
  });
}
</script>

<template>
  <el-card class="h-full w-full">
    <div class="flex h-full gap-4">
      <div class="w-1/3 flex flex-col gap-3">
        <el-button :loading="loading" type="primary" @click="onSearch"
          >向量搜索</el-button
        >
        <el-input
          v-model="content"
          placeholder="请输入关键词查询向量文本"
          :rows="10"
          type="textarea"
        />
      </div>
      <div class="w-full">
        <div v-loading="loading">
          <div class="grid grid-cols-3 gap-4">
            <el-card
              v-for="item in list"
              :key="item.index"
              class="rounded-lg cursor-pointer mb-4"
              shadow="hover"
            >
              <template #header>
                <el-skeleton v-if="loading" :rows="1" />
                <div v-else class="flex items-center gap-1">
                  <SvgIcon class="text-3xl" icon="flat-color-icons:document" />
                  <span class="truncate">{{ item.docsName }}</span>
                </div>
              </template>
              <div style="max-height: 200px; overflow-y: auto">
                {{ item.text }}
              </div>
            </el-card>
          </div>
          <el-empty v-if="list.length === 0" class="my-4" />
        </div>
      </div>
    </div>
  </el-card>
</template>

<style lang="less" scoped></style>
