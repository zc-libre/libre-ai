<script lang="ts" setup>
import AppBase from './base/index.vue';
import ApiChannel from './channel-api/index.vue';
import router from '@/router';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useAppStore } from './store';
import { getAppInfo } from '@/api/aigc/chat';
import { ElButton, ElIcon } from 'element-plus';
import { ArrowLeft, Setting, Share } from '@element-plus/icons-vue';

const route = useRoute();
const appStore = useAppStore();
const form = ref<any>({});
const loading = ref(false);
const activeMenus = [
  { key: 'setting', icon: 'uil:setting', label: '应用配置' },
  { key: 'api', icon: 'hugeicons:api', label: 'API接入渠道' }
];

onMounted(async () => {
  await fetchData();
});

async function fetchData() {
  loading.value = true;
  try {
    const id = route.params.id;
    const data = await getAppInfo({
      appId: id,
      conversationId: null
    });
    form.value = data || {};
  } catch (error) {
    console.error('Failed to fetch app info:', error);
    form.value = {};
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div
    v-if="form && Object.keys(form).length > 0"
    class="view-container rounded bg-[#f9f9f9] pb-10"
  >
    <div class="p-4 flex justify-between items-center bg-white rounded">
      <div class="flex gap-5 items-center min-w-20">
        <ElButton text type="primary" @click="router.push('/aigc/app')">
          <ElIcon class="text-xl">
            <ArrowLeft />
          </ElIcon>
        </ElButton>
        <div class="flex gap-2 items-center pr-4">
          <div class="mr-3">
            <div class="relative bg-orange-100 p-4 rounded-lg">
              <ElIcon class="text-3xl" :size="30">
                <Share />
              </ElIcon>

              <div
                class="absolute bottom-[-6px] p-1 right-[-5px] shadow bg-white mx-auto rounded-lg"
              >
                <ElIcon class="text-sm text-orange-500" :size="14">
                  <Setting />
                </ElIcon>
              </div>
            </div>
          </div>

          <div class="flex flex-col justify-between gap-2">
            <div class="font-bold text-lg">{{ form.name || '' }}</div>
            <div v-if="!loading" class="text-gray-400 text-xs">
              自动保存：{{ form.saveTime || '' }}
            </div>
            <div v-else class="flex items-center gap-1 text-gray-400 text-xs">
              <ElIcon class="animate-spin">
                <Setting />
              </ElIcon>
              保存中...
            </div>
          </div>
        </div>
      </div>

      <div class="flex items-center gap-2">
        <ElButton
          v-for="item in activeMenus"
          :key="item.key"
          :type="appStore.activeMenu === item.key ? 'primary' : 'default'"
          class="!px-5 !rounded-2xl"
          @click="appStore.setActiveMenu(item.key)"
        >
          <template #icon>
            <ElIcon>
              <component :is="item.key === 'setting' ? Setting : Share" />
            </ElIcon>
          </template>
          {{ item.label }}
        </ElButton>
      </div>
    </div>

    <template v-if="appStore.activeMenu === 'setting'">
      <AppBase />
    </template>

    <template v-if="appStore.activeMenu === 'api'">
      <ApiChannel />
    </template>
  </div>
</template>

<style lang="scss" scoped></style>
