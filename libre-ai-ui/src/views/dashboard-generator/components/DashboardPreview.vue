<template>
  <div class="dashboard-preview" :class="`theme-${config.theme}`">
    <div v-if="!generatedCode" class="preview-placeholder">
      <div class="placeholder-content">
        <el-icon :size="48" color="#c0c4cc">
          <Monitor />
        </el-icon>
        <h3>看板预览</h3>
        <p>完成配置后，您的看板预览将在这里显示</p>
        <div class="config-preview">
          <div v-if="config.purpose" class="config-item">
            <el-tag type="primary" size="small">{{
              config.purposeText
            }}</el-tag>
          </div>
          <div v-if="config.layout" class="config-item">
            <el-tag type="success" size="small">{{ config.layoutText }}</el-tag>
          </div>
          <div v-if="config.theme" class="config-item">
            <el-tag type="warning" size="small">{{ getThemeName() }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- Vue组件预览 -->
    <VueComponentPreview
      v-else-if="codeType === 'vue'"
      :vue-code="generatedCode"
      :config="config"
      @compilation-complete="onCompilationComplete"
      @runtime-error="onRuntimeError"
    />

    <!-- HTML预览 -->
    <iframe
      v-else
      ref="previewIframe"
      class="preview-iframe"
      :srcdoc="getCompleteHtml()"
      frameborder="0"
      width="100%"
      height="100%"
      sandbox="allow-scripts"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Monitor } from '@element-plus/icons-vue';
import VueComponentPreview from './VueComponentPreview.vue';

// Props
interface Props {
  config: any;
  generatedCode?: string; // 支持HTML和Vue代码
  codeType?: 'html' | 'vue'; // 代码类型
}

const props = withDefaults(defineProps<Props>(), {
  codeType: 'html'
});

// Emits
const emit = defineEmits<{
  'compilation-complete': [success: boolean, error?: string];
  'runtime-error': [error: Error];
}>();

// 为了兼容现有代码，保留 generatedHtml 计算属性
const generatedHtml = computed(() => {
  return props.codeType === 'html' ? props.generatedCode : '';
});

// 方法
const getThemeName = () => {
  const themeNames = {
    'modern-blue': '现代蓝',
    'dark-purple': '深紫夜',
    'green-nature': '自然绿',
    'orange-warm': '暖橙色'
  };
  return themeNames[props.config.theme] || '默认主题';
};

// Vue组件编译完成处理
const onCompilationComplete = (success: boolean, error?: string) => {
  emit('compilation-complete', success, error);
};

// Vue组件运行时错误处理
const onRuntimeError = (error: Error) => {
  emit('runtime-error', error);
};

// 获取完整的HTML内容（包含样式）
const getCompleteHtml = () => {
  if (!generatedHtml.value) return '';

  // 如果生成的HTML已经是完整的HTML文档，直接返回
  if (generatedHtml.value.includes('<!DOCTYPE html>')) {
    return generatedHtml.value;
  }

  // 否则包装成完整的HTML文档
  return `<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${props.config.purposeText || '数据看板'}</title>
    <style>
      * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
      }
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      }
      body {
        display: flex;
        flex-direction: column;
      }
      body > * {
        flex: 1;
      }
    </style>
</head>
<body>
    ${generatedHtml.value}
</body>
</html>`;
};
</script>

<style scoped>
.dashboard-preview {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.preview-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 400px;
}

.placeholder-content {
  text-align: center;
  color: #909399;
}

.placeholder-content h3 {
  margin: 16px 0 8px;
  color: #606266;
}

.placeholder-content p {
  margin-bottom: 20px;
  font-size: 14px;
}

.config-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
  background: white;
}
</style>
