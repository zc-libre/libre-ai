<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Icon } from '@iconify/vue';

export interface Prompt {
  id: string;
  title: string;
  content: string;
  category: string;
  tags: string[];
  isSystem?: boolean;
  usage: number;
  createdAt: string;
  updatedAt: string;
}

interface Props {
  visible: boolean;
}

interface Emit {
  (ev: 'update:visible', value: boolean): void;
  (ev: 'select-prompt', prompt: Prompt): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emit>();

const searchQuery = ref('');
const selectedCategory = ref('all');
const editingPrompt = ref<Prompt | null>(null);
const showPromptForm = ref(false);

// 提示词分类
const categories = [
  { label: '全部', value: 'all' },
  { label: '写作', value: 'writing' },
  { label: '编程', value: 'coding' },
  { label: '分析', value: 'analysis' },
  { label: '翻译', value: 'translation' },
  { label: '创意', value: 'creative' },
  { label: '商务', value: 'business' },
  { label: '学习', value: 'education' },
  { label: '其他', value: 'other' }
];

// 内置提示词库
const defaultPrompts: Prompt[] = [
  {
    id: '1',
    title: '代码审查助手',
    content:
      '请帮我审查以下代码，重点关注：\n1. 代码逻辑是否正确\n2. 是否存在潜在的bug\n3. 代码风格和最佳实践\n4. 性能优化建议\n\n代码：\n{code}',
    category: 'coding',
    tags: ['代码审查', '编程', 'bug修复'],
    isSystem: true,
    usage: 156,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-01'
  },
  {
    id: '2',
    title: '文章写作助手',
    content:
      '请帮我写一篇关于"{topic}"的文章，要求：\n1. 文章长度：{length}字\n2. 目标读者：{audience}\n3. 写作风格：{style}\n4. 包含要点：{points}\n\n请确保文章结构清晰、论点明确、语言流畅。',
    category: 'writing',
    tags: ['写作', '文章', '内容创作'],
    isSystem: true,
    usage: 89,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-01'
  },
  {
    id: '3',
    title: '数据分析专家',
    content:
      '我有以下数据需要分析，请帮我：\n1. 识别数据中的关键趋势和模式\n2. 提供统计分析结果\n3. 给出可视化建议\n4. 总结主要发现和洞察\n\n数据：\n{data}',
    category: 'analysis',
    tags: ['数据分析', '统计', '洞察'],
    isSystem: true,
    usage: 73,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-01'
  },
  {
    id: '4',
    title: '专业翻译助手',
    content:
      '请将以下文本从{source_lang}翻译成{target_lang}，要求：\n1. 保持原文的语气和风格\n2. 确保专业术语的准确性\n3. 适应目标语言的表达习惯\n4. 如有歧义，请提供多个翻译选项\n\n原文：\n{text}',
    category: 'translation',
    tags: ['翻译', '语言', '本地化'],
    isSystem: true,
    usage: 124,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-01'
  },
  {
    id: '5',
    title: '商业计划分析',
    content:
      '请帮我分析以下商业计划/想法：\n\n计划概述：{plan}\n\n请从以下角度进行分析：\n1. 市场机会和竞争分析\n2. 可行性评估\n3. 潜在风险和挑战\n4. 改进建议\n5. 实施策略',
    category: 'business',
    tags: ['商业分析', '计划', '策略'],
    isSystem: true,
    usage: 45,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-01'
  }
];

// 本地存储的提示词
const prompts = ref<Prompt[]>([]);

// 过滤后的提示词
const filteredPrompts = computed(() => {
  let filtered = prompts.value;

  // 按分类筛选
  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter(p => p.category === selectedCategory.value);
  }

  // 按搜索关键词筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(
      p =>
        p.title.toLowerCase().includes(query) ||
        p.content.toLowerCase().includes(query) ||
        p.tags.some(tag => tag.toLowerCase().includes(query))
    );
  }

  // 按使用次数排序
  return filtered.sort((a, b) => b.usage - a.usage);
});

// 加载提示词
onMounted(() => {
  loadPrompts();
});

function loadPrompts() {
  const stored = localStorage.getItem('libre-ai-prompts');
  if (stored) {
    try {
      const userPrompts = JSON.parse(stored);
      prompts.value = [...defaultPrompts, ...userPrompts];
    } catch (error) {
      console.error('加载提示词失败:', error);
      prompts.value = [...defaultPrompts];
    }
  } else {
    prompts.value = [...defaultPrompts];
  }
}

function savePrompts() {
  const userPrompts = prompts.value.filter(p => !p.isSystem);
  localStorage.setItem('libre-ai-prompts', JSON.stringify(userPrompts));
}

// 选择提示词
function selectPrompt(prompt: Prompt) {
  // 增加使用次数
  prompt.usage++;
  savePrompts();

  emit('select-prompt', prompt);
  emit('update:visible', false);
  ElMessage.success(`已选择提示词: ${prompt.title}`);
}

// 新建提示词
function createPrompt() {
  editingPrompt.value = {
    id: '',
    title: '',
    content: '',
    category: 'other',
    tags: [],
    usage: 0,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };
  showPromptForm.value = true;
}

// 编辑提示词
function editPrompt(prompt: Prompt) {
  if (prompt.isSystem) {
    ElMessage.warning('系统内置提示词不能编辑');
    return;
  }
  editingPrompt.value = { ...prompt };
  showPromptForm.value = true;
}

// 保存提示词
function savePrompt() {
  if (!editingPrompt.value) return;

  const { title, content, category } = editingPrompt.value;

  if (!title.trim() || !content.trim()) {
    ElMessage.warning('标题和内容不能为空');
    return;
  }

  if (editingPrompt.value.id) {
    // 更新现有提示词
    const index = prompts.value.findIndex(
      p => p.id === editingPrompt.value!.id
    );
    if (index > -1) {
      prompts.value[index] = {
        ...editingPrompt.value,
        updatedAt: new Date().toISOString()
      };
    }
  } else {
    // 创建新提示词
    const newPrompt: Prompt = {
      ...editingPrompt.value,
      id: `custom_${Date.now()}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    prompts.value.push(newPrompt);
  }

  savePrompts();
  showPromptForm.value = false;
  editingPrompt.value = null;
  ElMessage.success('提示词保存成功');
}

// 删除提示词
function deletePrompt(prompt: Prompt) {
  if (prompt.isSystem) {
    ElMessage.warning('系统内置提示词不能删除');
    return;
  }

  ElMessageBox.confirm('确认删除这个提示词吗？', '删除确认', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = prompts.value.findIndex(p => p.id === prompt.id);
    if (index > -1) {
      prompts.value.splice(index, 1);
      savePrompts();
      ElMessage.success('删除成功');
    }
  });
}

// 导入提示词
function importPrompts() {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = '.json';
  input.onchange = (e: any) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event: any) => {
        try {
          const imported = JSON.parse(event.target.result);
          if (Array.isArray(imported)) {
            const newPrompts = imported.map(p => ({
              ...p,
              id: `imported_${Date.now()}_${Math.random().toString(36).slice(2, 9)}`,
              isSystem: false,
              usage: p.usage || 0
            }));
            prompts.value.push(...newPrompts);
            savePrompts();
            ElMessage.success(`成功导入 ${newPrompts.length} 个提示词`);
          }
        } catch (error) {
          ElMessage.error('导入失败，文件格式不正确');
        }
      };
      reader.readAsText(file);
    }
  };
  input.click();
}

// 导出提示词
function exportPrompts() {
  const userPrompts = prompts.value.filter(p => !p.isSystem);
  const dataStr = JSON.stringify(userPrompts, null, 2);
  const blob = new Blob([dataStr], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `prompts-export-${new Date().toISOString().slice(0, 10)}.json`;
  a.click();
  URL.revokeObjectURL(url);
}

// 处理标签输入
function handleTagInput(value: string) {
  if (!editingPrompt.value) return;
  editingPrompt.value.tags = value
    .split(',')
    .map(tag => tag.trim())
    .filter(Boolean);
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    title="提示词库"
    width="80%"
    :modal="true"
    @update:model-value="emit('update:visible', $event)"
  >
    <div class="prompt-store-container">
      <!-- 搜索和筛选栏 -->
      <div class="flex items-center space-x-4 mb-6">
        <el-input
          v-model="searchQuery"
          placeholder="搜索提示词..."
          class="flex-1"
          clearable
        >
          <template #prefix>
            <Icon icon="ri:search-line" />
          </template>
        </el-input>

        <el-select
          v-model="selectedCategory"
          placeholder="选择分类"
          style="width: 120px"
        >
          <el-option
            v-for="category in categories"
            :key="category.value"
            :label="category.label"
            :value="category.value"
          />
        </el-select>

        <el-button type="primary" @click="createPrompt">
          <template #icon>
            <Icon icon="ri:add-line" />
          </template>
          新建
        </el-button>

        <el-dropdown>
          <el-button>
            更多操作
            <Icon icon="ri:arrow-down-s-line" class="ml-1" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="importPrompts">
                <Icon icon="ri:upload-line" class="mr-2" />
                导入
              </el-dropdown-item>
              <el-dropdown-item @click="exportPrompts">
                <Icon icon="ri:download-line" class="mr-2" />
                导出
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <!-- 提示词列表 -->
      <div
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 max-h-96 overflow-y-auto"
      >
        <div
          v-for="prompt in filteredPrompts"
          :key="prompt.id"
          class="prompt-card border border-gray-200 rounded-lg p-4 hover:shadow-md transition-all cursor-pointer"
          @click="selectPrompt(prompt)"
        >
          <div class="flex items-start justify-between mb-2">
            <h3 class="font-semibold text-gray-800 text-sm truncate flex-1">
              {{ prompt.title }}
            </h3>
            <div class="flex items-center space-x-1 ml-2">
              <el-badge
                v-if="prompt.isSystem"
                value="系统"
                type="info"
                size="small"
              />
              <el-dropdown @click.stop>
                <el-button link size="small" class="!p-1">
                  <Icon icon="ri:more-2-line" class="text-gray-400" />
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click.stop="editPrompt(prompt)">
                      <Icon icon="ri:edit-line" class="mr-2" />
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item @click.stop="deletePrompt(prompt)">
                      <Icon icon="ri:delete-bin-line" class="mr-2" />
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <p class="text-gray-600 text-xs mb-3 line-clamp-3">
            {{ prompt.content }}
          </p>

          <div class="flex items-center justify-between text-xs text-gray-500">
            <div class="flex items-center space-x-2">
              <span class="bg-gray-100 px-2 py-1 rounded">
                {{ categories.find(c => c.value === prompt.category)?.label }}
              </span>
              <span>使用 {{ prompt.usage }} 次</span>
            </div>
          </div>

          <div v-if="prompt.tags.length > 0" class="flex flex-wrap gap-1 mt-2">
            <span
              v-for="tag in prompt.tags.slice(0, 3)"
              :key="tag"
              class="inline-block bg-blue-50 text-blue-600 px-2 py-0.5 rounded text-xs"
            >
              {{ tag }}
            </span>
            <span v-if="prompt.tags.length > 3" class="text-xs text-gray-400">
              +{{ prompt.tags.length - 3 }}
            </span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredPrompts.length === 0" class="text-center py-12">
        <Icon icon="ri:file-text-line" class="text-4xl text-gray-300 mb-4" />
        <p class="text-gray-500">没有找到符合条件的提示词</p>
        <el-button type="primary" class="mt-4" @click="createPrompt">
          创建第一个提示词
        </el-button>
      </div>
    </div>

    <template #footer>
      <el-button @click="emit('update:visible', false)">关闭</el-button>
    </template>
  </el-dialog>

  <!-- 提示词编辑对话框 -->
  <el-dialog
    v-model="showPromptForm"
    :title="editingPrompt?.id ? '编辑提示词' : '新建提示词'"
    width="60%"
  >
    <el-form v-if="editingPrompt" label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="editingPrompt.title" placeholder="输入提示词标题" />
      </el-form-item>

      <el-form-item label="分类">
        <el-select v-model="editingPrompt.category" style="width: 200px">
          <el-option
            v-for="category in categories.filter(c => c.value !== 'all')"
            :key="category.value"
            :label="category.label"
            :value="category.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="标签">
        <el-input
          :model-value="editingPrompt.tags.join(', ')"
          placeholder="用逗号分隔多个标签"
          @input="handleTagInput"
        />
      </el-form-item>

      <el-form-item label="内容">
        <el-input
          v-model="editingPrompt.content"
          type="textarea"
          :rows="8"
          placeholder="输入提示词内容，可以使用 {变量名} 作为占位符"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="showPromptForm = false">取消</el-button>
      <el-button type="primary" @click="savePrompt">保存</el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.prompt-store-container {
  max-height: 70vh;
  overflow: hidden;
}

.prompt-card {
  &:hover {
    border-color: #3b82f6;
    transform: translateY(-2px);
  }
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

// 深色模式
.dark {
  .prompt-card {
    background: #374151;
    border-color: #4b5563;
    color: #f3f4f6;

    &:hover {
      border-color: #3b82f6;
      background: #4b5563;
    }
  }

  .text-gray-800 {
    color: #f3f4f6 !important;
  }

  .text-gray-600 {
    color: #d1d5db !important;
  }

  .text-gray-500 {
    color: #9ca3af !important;
  }
}
</style>
