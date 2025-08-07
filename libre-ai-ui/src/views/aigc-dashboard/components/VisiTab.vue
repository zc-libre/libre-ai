<script lang="ts" setup>
import AiReqChart from './AiReqChart.vue';
import AiTokenChart from './AiTokenChart.vue';
</script>

<template>
  <div class="mt-4 w-full">
    <el-card :body-style="{ padding: '0' }" shadow="never">
      <el-tabs>
        <el-tab-pane label="Token消耗量统计" name="token">
          <div style="padding: 10px">
            <AiTokenChart />
          </div>
        </el-tab-pane>
        <el-tab-pane label="AI请求量统计" name="request">
          <div style="padding: 10px">
            <AiReqChart />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>
