<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import VisiTab from './components/VisiTab.vue';
import { CountTo } from '@/components/CountTo';
import { getHomeData } from '@/api/aigc/statictic';
import { Icon } from '@iconify/vue';

const loading = ref(true);
const list = ref([
  {
    key: 'req',
    label: 'LLM请求量',
    value: 0,
    totalLabel: 'LLM总请求量',
    totalValue: 0,
    category: '日',
    type: 'success'
  },
  {
    key: 'token',
    label: 'Token消耗量',
    value: 0,
    totalLabel: 'Token总消耗量',
    totalValue: 0,
    category: '日',
    type: 'primary'
  },
  {
    key: 'user',
    label: '用户增长量',
    value: 0,
    totalLabel: '平台用户总量',
    totalValue: 0,
    category: '月',
    type: 'warning'
  },
  {
    key: 'knowledge',
    label: '知识库数量',
    value: 0,
    totalLabel: 'AI应用数量',
    totalValue: 0,
    category: '合',
    type: 'danger'
  }
]);

onMounted(async () => {
  const {
    totalReq,
    curReq,
    totalToken,
    curToken,
    totalUser,
    curUser,
    totalKnowledge,
    totalPrompt
  } = await getHomeData();
  list.value.forEach(i => {
    if (i.key === 'req') {
      i.value = Number(curReq);
      i.totalValue = Number(totalReq);
    }
    if (i.key === 'token') {
      i.value = Number(curToken);
      i.totalValue = Number(totalToken);
    }
    if (i.key === 'user') {
      i.value = Number(curUser);
      i.totalValue = Number(totalUser);
    }
    if (i.key === 'knowledge') {
      i.value = Number(totalKnowledge);
      i.totalValue = Number(totalPrompt);
    }
  });
  loading.value = false;
});
</script>

<template>
  <div class="view-container view-scrollable">
    <!--数据卡片-->
    <el-row :gutter="12">
      <el-col
        v-for="item in list"
        :key="item.key"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
        :xl="6"
      >
        <el-card shadow="hover" :body-style="{ padding: '20px' }">
          <template #header>
            <div class="flex justify-between items-center">
              <span>{{ item.label }}</span>
              <el-tag :type="item.type" effect="plain">{{
                item.category
              }}</el-tag>
            </div>
          </template>
          <div class="flex justify-between px-1 py-1">
            <el-skeleton v-if="loading" animated>
              <template #template>
                <el-skeleton-item variant="text" style="width: 100px" />
              </template>
            </el-skeleton>
            <CountTo
              v-else
              :end-val="item.value"
              :start-val="0"
              class="text-2xl"
            />
          </div>
          <div class="flex justify-between px-1 py-1 mt-2">
            <div class="text-gray-600 flex items-center">
              <el-skeleton v-if="loading" animated>
                <template #template>
                  <el-skeleton-item variant="text" style="width: 100px" />
                </template>
              </el-skeleton>
              <template v-else>
                <span class="mr-2">{{ item.totalLabel }}</span>
                <CountTo :end-val="item.totalValue" :start-val="0" suffix=" " />
                <Icon
                  icon="ant-design:caret-up-outlined"
                  :width="12"
                  :color="'#00ff6f'"
                />
              </template>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <VisiTab />
  </div>
</template>

<style lang="scss" scoped>
.el-card {
  margin-bottom: 12px;
}
</style>
