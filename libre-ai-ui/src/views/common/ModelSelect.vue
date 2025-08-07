<template>
  <el-select
    v-model="selectedModel"
    placeholder="请选择模型"
    class="w-full"
    @change="handleChange"
  >
    <el-option-group
      v-for="group in modelGroups"
      :key="group.name"
      :label="group.name"
    >
      <el-option
        v-for="item in group.children"
        :key="item.id || item.model"
        :label="item.name || item.model"
        :value="String(item.id || item.model)"
      >
        <div class="flex items-center gap-2">
          <span>{{ item.name || item.model }}</span>
          <el-tag v-if="item.provider" size="small">{{ item.provider }}</el-tag>
        </div>
      </el-option>
    </el-option-group>
  </el-select>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue';
import { list as getModels } from '@/api/aigc/model';

const props = defineProps({
  id: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update', 'load']);

const selectedModel = ref(props.id);
const modelGroups = ref([]);
const allModels = ref([]);

// 模型提供商配置
const providers = [
  { id: 'openai', name: 'OpenAI', model: 'openai' },
  { id: 'anthropic', name: 'Anthropic', model: 'anthropic' },
  { id: 'zhipu', name: '智谱AI', model: 'zhipu' },
  { id: 'qwen', name: '通义千问', model: 'qwen' }
];

watch(
  () => props.id,
  newVal => {
    selectedModel.value = newVal;
  }
);

async function loadModels() {
  try {
    const result = await getModels({ type: 'CHAT' });
    const models = Array.isArray(result?.data)
      ? result.data
      : Array.isArray(result)
        ? result
        : [];

    allModels.value = models;

    // 按提供商分组
    const groups = [];
    providers.forEach(provider => {
      const children = models.filter(m => m.provider === provider.model);
      if (children.length > 0) {
        groups.push({
          name: provider.name,
          children: children
        });
      }
    });

    modelGroups.value = groups;

    // 如果没有选中的模型且有可用模型，选择第一个
    if (!selectedModel.value && models.length > 0) {
      const firstModel = models[0];
      selectedModel.value = String(firstModel.id || firstModel.model);
      emit('load', firstModel);
    }
  } catch (error) {
    console.error('加载模型列表失败:', error);
    // 提供默认模型
    modelGroups.value = [
      {
        name: '默认',
        children: [
          {
            id: '1',
            name: 'GPT-3.5-Turbo',
            model: 'gpt-3.5-turbo',
            provider: 'openai'
          }
        ]
      }
    ];
  }
}

function handleChange(value: string) {
  const model = allModels.value.find(m => String(m.id || m.model) === value);
  if (model) {
    emit('update', model);
  }
}

onMounted(async () => {
  await loadModels();

  // 如果有预设ID，触发load事件
  if (props.id) {
    const model = allModels.value.find(
      m => String(m.id || m.model) === props.id
    );
    if (model) {
      emit('load', model);
    }
  }
});
</script>

<style lang="scss" scoped></style>
