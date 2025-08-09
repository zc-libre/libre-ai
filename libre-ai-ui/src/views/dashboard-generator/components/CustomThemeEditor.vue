<template>
  <el-dialog
    v-model="visible"
    title="自定义主题"
    width="600px"
    :before-close="handleClose"
    class="custom-theme-dialog"
  >
    <div class="theme-editor">
      <!-- 主题名称 -->
      <div class="editor-section">
        <label class="section-label">主题名称</label>
        <el-input
          v-model="themeName"
          placeholder="请输入主题名称"
          maxlength="20"
          show-word-limit
        />
      </div>

      <!-- 颜色配置 -->
      <div class="editor-section">
        <label class="section-label">颜色配置</label>
        <div class="color-grid">
          <div
            v-for="(colorInfo, key) in colorConfig"
            :key="key"
            class="color-item"
          >
            <label class="color-label">{{ colorInfo.label }}</label>
            <div class="color-input-group">
              <el-color-picker
                v-model="customColors[key]"
                :predefine="colorInfo.presets"
                @change="updatePreview"
              />
              <el-input
                v-model="customColors[key]"
                size="small"
                class="color-hex-input"
                @input="updatePreview"
              />
            </div>
            <p class="color-description">{{ colorInfo.description }}</p>
          </div>
        </div>
      </div>

      <!-- 预览区域 -->
      <div class="editor-section">
        <label class="section-label">预览效果</label>
        <div class="theme-preview-container">
          <div class="preview-card" :style="previewStyles">
            <div class="preview-header">
              <h3 class="preview-title">{{ themeName || '自定义主题' }}</h3>
              <div class="preview-actions">
                <div class="preview-button primary">主要按钮</div>
                <div class="preview-button secondary">次要按钮</div>
              </div>
            </div>
            <div class="preview-content">
              <div class="preview-chart">
                <div class="chart-bar primary" />
                <div class="chart-bar secondary" />
                <div class="chart-bar accent" />
              </div>
              <div class="preview-text">
                <p class="text-primary">这是主要文本内容</p>
                <p class="text-secondary">这是次要文本内容</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 预设模板 -->
      <div class="editor-section">
        <label class="section-label">快速模板</label>
        <div class="template-grid">
          <div
            v-for="template in colorTemplates"
            :key="template.name"
            class="template-item"
            @click="applyTemplate(template)"
          >
            <div class="template-preview">
              <div
                class="template-color"
                :style="{ backgroundColor: template.colors.primary }"
              />
              <div
                class="template-color"
                :style="{ backgroundColor: template.colors.secondary }"
              />
              <div
                class="template-color"
                :style="{ backgroundColor: template.colors.accent }"
              />
            </div>
            <span class="template-name">{{ template.name }}</span>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="info" @click="resetColors">重置</el-button>
        <el-button type="primary" :disabled="!canSave" @click="saveTheme">
          {{ editingTheme ? '更新主题' : '保存主题' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import {
  useTheme,
  type CustomThemeColors,
  type ThemeOption
} from '../composables/useTheme';

// Props
interface Props {
  modelValue: boolean;
  editingTheme?: ThemeOption | null;
}

const props = withDefaults(defineProps<Props>(), {
  editingTheme: null
});

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  'theme-saved': [theme: ThemeOption];
}>();

// 使用主题 composable
const { createCustomTheme, updateCustomTheme, getDefaultCustomColors } =
  useTheme();

// 状态
const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
});

const themeName = ref('');
const customColors = reactive<CustomThemeColors>(getDefaultCustomColors());

// 颜色配置
const colorConfig = {
  primary: {
    label: '主色调',
    description: '用于主要按钮、链接等重要元素',
    presets: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
  },
  secondary: {
    label: '辅助色',
    description: '用于次要按钮、辅助信息等',
    presets: ['#79BBFF', '#85CE61', '#EEBE77', '#F78989', '#A6A9AD']
  },
  accent: {
    label: '强调色',
    description: '用于高亮、提示等需要突出的元素',
    presets: ['#A0CFFF', '#B3E19D', '#F3D19E', '#FAB6B6', '#C8C9CC']
  },
  background: {
    label: '背景色',
    description: '页面主要背景颜色',
    presets: ['#f5f7fa', '#f0f9f0', '#fdf6ec', '#fef0f0', '#f4f4f5']
  },
  surface: {
    label: '表面色',
    description: '卡片、面板等表面颜色',
    presets: ['#ffffff', '#fafafa', '#f9f9f9', '#fcfcfc', '#f8f8f8']
  },
  text: {
    label: '文本色',
    description: '主要文本颜色',
    presets: ['#303133', '#606266', '#909399', '#C0C4CC', '#E4E7ED']
  }
};

// 快速模板
const colorTemplates = [
  {
    name: '海洋蓝',
    colors: {
      primary: '#1890ff',
      secondary: '#40a9ff',
      accent: '#69c0ff',
      background: '#f0f8ff',
      surface: '#ffffff',
      text: '#262626'
    }
  },
  {
    name: '森林绿',
    colors: {
      primary: '#52c41a',
      secondary: '#73d13d',
      accent: '#95de64',
      background: '#f6ffed',
      surface: '#ffffff',
      text: '#262626'
    }
  },
  {
    name: '日落橙',
    colors: {
      primary: '#fa8c16',
      secondary: '#ffa940',
      accent: '#ffc069',
      background: '#fff7e6',
      surface: '#ffffff',
      text: '#262626'
    }
  },
  {
    name: '樱花粉',
    colors: {
      primary: '#eb2f96',
      secondary: '#f759ab',
      accent: '#ff85c0',
      background: '#fff0f6',
      surface: '#ffffff',
      text: '#262626'
    }
  }
];

// 计算属性
const canSave = computed(() => {
  return themeName.value.trim().length > 0;
});

const previewStyles = computed(() => ({
  '--preview-primary': customColors.primary,
  '--preview-secondary': customColors.secondary,
  '--preview-accent': customColors.accent,
  '--preview-background': customColors.background,
  '--preview-surface': customColors.surface,
  '--preview-text': customColors.text
}));

const editingTheme = computed(() => props.editingTheme);

// 监听编辑主题变化
watch(
  editingTheme,
  theme => {
    if (theme) {
      themeName.value = theme.name;
      Object.assign(customColors, theme.colors);
    } else {
      resetColors();
    }
  },
  { immediate: true }
);

// 方法
const updatePreview = () => {
  // 预览更新逻辑已通过响应式实现
};

const applyTemplate = (template: any) => {
  Object.assign(customColors, template.colors);
  if (!themeName.value) {
    themeName.value = template.name;
  }
};

const resetColors = () => {
  themeName.value = '';
  Object.assign(customColors, getDefaultCustomColors());
};

const saveTheme = () => {
  if (!canSave.value) return;

  try {
    let savedTheme: ThemeOption;

    if (editingTheme.value) {
      // 更新现有主题
      savedTheme = updateCustomTheme(editingTheme.value, customColors);
    } else {
      // 创建新主题
      savedTheme = createCustomTheme(customColors);
    }

    emit('theme-saved', savedTheme);
    ElMessage.success(editingTheme.value ? '主题更新成功！' : '主题保存成功！');
    handleClose();
  } catch (error) {
    ElMessage.error('保存失败，请重试');
    console.error('Save theme error:', error);
  }
};

const handleClose = () => {
  visible.value = false;
  if (!editingTheme.value) {
    resetColors();
  }
};
</script>

<style scoped>
.custom-theme-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.theme-editor {
  max-height: 70vh;
  overflow-y: auto;
}

.editor-section {
  margin-bottom: 24px;
}

.section-label {
  display: block;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  font-size: 14px;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.color-item {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
}

.color-label {
  display: block;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
  font-size: 13px;
}

.color-input-group {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 6px;
}

.color-hex-input {
  flex: 1;
  max-width: 120px;
}

.color-description {
  font-size: 12px;
  color: #909399;
  margin: 0;
  line-height: 1.4;
}

.theme-preview-container {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.preview-card {
  background: var(--preview-surface);
  color: var(--preview-text);
  padding: 20px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--preview-accent);
}

.preview-title {
  margin: 0;
  color: var(--preview-text);
  font-size: 18px;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

.preview-button {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.preview-button.primary {
  background: var(--preview-primary);
  color: white;
}

.preview-button.secondary {
  background: var(--preview-secondary);
  color: white;
}

.preview-content {
  display: flex;
  gap: 20px;
  align-items: center;
}

.preview-chart {
  display: flex;
  gap: 4px;
  align-items: end;
}

.chart-bar {
  width: 20px;
  border-radius: 2px 2px 0 0;
}

.chart-bar.primary {
  height: 40px;
  background: var(--preview-primary);
}

.chart-bar.secondary {
  height: 30px;
  background: var(--preview-secondary);
}

.chart-bar.accent {
  height: 35px;
  background: var(--preview-accent);
}

.preview-text {
  flex: 1;
}

.text-primary {
  color: var(--preview-text);
  margin: 0 0 8px 0;
  font-weight: 500;
}

.text-secondary {
  color: var(--preview-text);
  opacity: 0.7;
  margin: 0;
  font-size: 14px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 12px;
}

.template-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.template-item:hover {
  border-color: #409eff;
  background: #f0f8ff;
}

.template-preview {
  display: flex;
  margin-bottom: 8px;
}

.template-color {
  width: 16px;
  height: 16px;
  border-radius: 2px;
  margin-right: 2px;
}

.template-name {
  font-size: 12px;
  color: #606266;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
