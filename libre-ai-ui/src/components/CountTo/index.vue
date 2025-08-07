<template>
  <span>{{ displayValue }}</span>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useTransition, TransitionPresets } from '@vueuse/core';

interface Props {
  startVal?: number;
  endVal: number;
  duration?: number;
  autoplay?: boolean;
  decimals?: number;
  prefix?: string;
  suffix?: string;
  separator?: string;
  decimal?: string;
  useEasing?: boolean;
  transition?: string;
}

const props = withDefaults(defineProps<Props>(), {
  startVal: 0,
  duration: 1500,
  autoplay: true,
  decimals: 0,
  prefix: '',
  suffix: '',
  separator: ',',
  decimal: '.',
  useEasing: true,
  transition: 'linear'
});

const source = ref(props.startVal);
const disabled = ref(false);

let outputValue = useTransition(source);

const displayValue = computed(() => {
  const val = formatNumber(outputValue.value);
  return props.prefix + val + props.suffix;
});

onMounted(() => {
  if (props.autoplay) {
    start();
  }
});

function start() {
  disabled.value = false;
  source.value = props.endVal;
}

function formatNumber(num: number | string) {
  if (!num && num !== 0) {
    return '';
  }
  const { decimals, decimal, separator } = props;
  num = Number(num).toFixed(decimals);
  num += '';
  const x = num.split('.');
  let x1 = x[0];
  const x2 = x.length > 1 ? decimal + x[1] : '';
  const rgx = /(\d+)(\d{3})/;
  if (separator && !Number.isNaN(Number(separator))) {
    while (rgx.test(x1)) {
      x1 = x1.replace(rgx, '$1' + separator + '$2');
    }
  }
  return x1 + x2;
}

watch([() => props.startVal, () => props.endVal], () => {
  if (props.autoplay) {
    start();
  }
});

watch(
  () => props.duration,
  () => {
    if (props.duration) {
      source.value = props.startVal;
      outputValue = useTransition(source, {
        duration: props.duration,
        transition:
          TransitionPresets[props.transition] || TransitionPresets.linear
      });
    }
  }
);

onUnmounted(() => {
  source.value = props.startVal;
});
</script>
