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
          <el-tag v-if="item.provider" size="small">{{
            getProviderDisplay(item.provider)
          }}</el-tag>
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

const selectedModel = ref<string>(props.id ? String(props.id) : '');
const modelGroups = ref<Array<{ name: string; children: any[] }>>([]);
const allModels = ref<any[]>([]);

// Provider 显示名映射（兼容大小写与未知枚举）
const providerNameMap: Record<string, string> = {
  OPENAI: 'OpenAI',
  AZURE_OPENAI: 'Azure OpenAI',
  GEMINI: 'Gemini',
  OLLAMA: 'Ollama',
  CLAUDE: 'Claude',
  Q_FAN: 'Q Fan',
  Q_WEN: '通义千问',
  ZHIPU: '智谱AI',
  GITEEAI: 'GiteeAI',
  DEEPSEEK: 'DeepSeek',
  DOUYIN: '抖音',
  SILICON: 'SiliconFlow',
  YI: '零一万物',
  SPARK: '讯飞星火',
  openai: 'OpenAI',
  anthropic: 'Claude',
  zhipu: '智谱AI',
  qwen: '通义千问'
};

function getProviderDisplay(provider: any) {
  const key = String(provider || '').trim();
  const upper = key.toUpperCase();
  return providerNameMap[upper] || providerNameMap[key] || key || '其他';
}

watch(
  () => props.id,
  newVal => {
    selectedModel.value = newVal ? String(newVal) : '';
  }
);

async function loadModels() {
  try {
    const result = await getModels({ type: 'CHAT' });
    // 后端返回的是 R 对象，数据在 result 字段中
    const models = Array.isArray(result?.result)
      ? result.result
      : Array.isArray(result?.data)
        ? result.data
        : Array.isArray(result)
          ? result
          : [];

    allModels.value = models;

    // 按实际返回的 provider 动态分组（兼容大小写），如果无法分组则展示全部
    const groupsMap: Record<string, any[]> = {};
    for (const m of models) {
      const name = getProviderDisplay(m?.provider);
      if (!groupsMap[name]) groupsMap[name] = [];
      groupsMap[name].push(m);
    }
    const groups = Object.keys(groupsMap).map(name => ({
      name,
      children: groupsMap[name]
    }));
    modelGroups.value =
      groups.length > 0 ? groups : [{ name: '全部模型', children: models }];

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
            provider: 'OPENAI'
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

  // 如果有预设ID，触发load事件（统一转为字符串比较）
  if (props.id) {
    const idStr = String(props.id);
    const model = allModels.value.find(m => String(m.id || m.model) === idStr);
    if (model) {
      emit('load', model);
    }
  }
});
</script>

<style lang="scss" scoped></style>
