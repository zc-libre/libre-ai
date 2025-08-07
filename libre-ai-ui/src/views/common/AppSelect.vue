<script lang="ts" setup>
import { onMounted, ref, toRaw } from 'vue';
import { list } from '@/api/aigc/app';

const props = defineProps<{
  id: any;
}>();
const emit = defineEmits(['update']);
const options = ref([]);
const appId = ref('');

onMounted(async () => {
  options.value = await list({});
  appId.value = props.id;
});

function onUpdate(val: any) {
  const obj = options.value.find(item => item.id === val);
  if (obj == null) {
    emit('update', {
      id: ''
    });
  } else {
    emit('update', {
      id: obj.id
    });
  }
}
</script>

<template>
  <el-select
    v-model="appId"
    clearable
    placeholder="请选择关联应用"
    @change="onUpdate"
  >
    <el-option
      v-for="item in options"
      :key="item.id"
      :label="item.name"
      :value="item.id"
    />
  </el-select>
</template>

<style lang="scss" scoped></style>
