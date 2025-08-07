<script lang="ts" setup>
import { onMounted, ref, Ref } from 'vue';
import { useECharts } from '@/hooks/web/useECharts';
import { getReqChartBy30 } from '@/api/aigc/statictic';

const chartRef = ref<HTMLDivElement | null>(null);
const { setOptions } = useECharts(chartRef as Ref<HTMLDivElement>);

onMounted(async () => {
  const data = await getReqChartBy30();
  const xData: any = [];
  const yData: any = [];
  data.forEach((i: any) => {
    xData.push(i.date);
    yData.push(i.tokens);
  });

  setOptions({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        lineStyle: {
          width: 1,
          color: '#019680'
        }
      }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData,
      splitLine: {
        show: true,
        lineStyle: {
          width: 1,
          type: 'solid',
          color: 'rgba(226,226,226,0.5)'
        }
      },
      axisTick: {
        show: false
      }
    },
    yAxis: [
      {
        type: 'value',
        splitNumber: 4,
        axisTick: {
          show: false
        },
        splitArea: {
          show: true,
          areaStyle: {
            color: ['rgba(255,255,255,0.2)', 'rgba(226,226,226,0.2)']
          }
        }
      }
    ],
    grid: {
      left: '1%',
      right: '1%',
      top: '2  %',
      bottom: 0,
      containLabel: true
    },
    series: [
      {
        smooth: true,
        data: yData,
        type: 'line',
        areaStyle: {},
        itemStyle: {
          color: '#5ab1ef'
        }
      }
    ]
  });
});
</script>

<template>
  <div class="chart-container">
    <div class="mb-4">
      <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">
        近30天请求汇总
      </h3>
      <p class="text-sm text-gray-600 dark:text-gray-400">
        显示最近30天的 AI 服务请求量变化趋势
      </p>
    </div>
    <div
      ref="chartRef"
      class="w-full h-64 sm:h-72 lg:h-80 bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-4"
    />
  </div>
</template>

<style lang="scss" scoped>
.chart-container {
  :deep(.echarts) {
    border-radius: 8px;
  }
}
</style>
