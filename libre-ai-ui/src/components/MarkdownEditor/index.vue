<script lang="ts" setup>
import { ref, computed, watch } from 'vue';
import { MdEditor, MdPreview, config, type ToolbarNames } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import { useDark } from '@pureadmin/utils';

// 配置中文语言
config({
  editorConfig: {
    languageUserDefined: {
      'zh-CN': {
        toolbarTips: {
          bold: '加粗',
          underline: '下划线',
          italic: '斜体',
          strikeThrough: '删除线',
          title: '标题',
          sub: '下标',
          sup: '上标',
          quote: '引用',
          unorderedList: '无序列表',
          orderedList: '有序列表',
          task: '任务列表',
          codeRow: '行内代码',
          code: '块级代码',
          link: '链接',
          image: '图片',
          table: '表格',
          mermaid: 'Mermaid图',
          katex: 'KaTeX公式',
          revoke: '撤销',
          next: '下一步',
          save: '保存',
          prettier: '美化',
          pageFullscreen: '浏览器全屏',
          fullscreen: '全屏',
          preview: '预览',
          htmlPreview: 'HTML预览',
          catalog: '目录',
          github: '源码'
        },
        titleItem: {
          h1: '一级标题',
          h2: '二级标题',
          h3: '三级标题',
          h4: '四级标题',
          h5: '五级标题',
          h6: '六级标题'
        },
        linkModalTips: {
          linkTitle: '添加链接',
          imageTitle: '添加图片',
          descLabel: '链接描述',
          descLabelPlaceHolder: '请输入描述...',
          urlLabel: '链接地址',
          urlLabelPlaceHolder: '请输入链接...',
          buttonOK: '确定'
        },
        copyCode: {
          text: '复制代码',
          successTips: '已复制',
          failTips: '复制失败'
        },
        mermaid: {
          flow: '流程图',
          sequence: '时序图',
          gantt: '甘特图',
          class: '类图',
          state: '状态图',
          pie: '饼图',
          relationship: '关系图',
          journey: '旅程图'
        },
        katex: {
          inline: '行内公式',
          block: '块级公式'
        },
        footer: {
          markdownTotal: '字数',
          scrollAuto: '同步滚动'
        }
      }
    }
  }
});

interface Props {
  modelValue: string;
  placeholder?: string;
  height?: string;
  mode?: 'edit' | 'preview' | 'split';
  toolbarsExclude?: string[];
  disabled?: boolean;
  showCodeRowNumber?: boolean;
  maxLength?: number;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  placeholder: '请输入内容...',
  height: '400px',
  mode: 'split',
  toolbarsExclude: () => ['github', 'mermaid', 'katex'],
  disabled: false,
  showCodeRowNumber: true
});

const emit = defineEmits(['update:modelValue', 'change', 'save', 'upload-img']);

// 获取暗色模式状态
const { isDark } = useDark();

// 编辑器内容 - 确保始终为字符串，防止null值导致md-editor-v3内部错误
const content = ref(props.modelValue || '');

// 编辑器主题
const theme = computed(() => (isDark.value ? 'dark' : 'light'));

// 预览主题
const previewTheme = computed(() => (isDark.value ? 'vuepress' : 'default'));

// 代码主题
const codeTheme = computed(() => (isDark.value ? 'atom' : 'a11y'));

// 编辑器唯一ID
const editorId = computed(() => `md-editor-${Math.random().toString(36).substr(2, 9)}`);

// 监听外部值变化 - 确保新值始终为字符串
watch(
  () => props.modelValue,
  newVal => {
    const safeNewVal = newVal || '';
    if (safeNewVal !== content.value) {
      content.value = safeNewVal;
    }
  }
);

// 监听内部值变化 - 确保发出的值始终为字符串
watch(content, newVal => {
  const safeVal = newVal || '';
  emit('update:modelValue', safeVal);
  emit('change', safeVal);
});

// 保存事件
const handleSave = (value: string) => {
  emit('save', value);
};

// 上传图片
const handleUploadImg = async (
  files: File[],
  callback: (urls: string[]) => void
) => {
  // 如果父组件提供了上传方法，则使用父组件的方法
  emit('upload-img', files, callback);

  // 否则将图片转为base64
  const res = await Promise.all(
    files.map(file => {
      return new Promise<string>(resolve => {
        const reader = new FileReader();
        reader.onloadend = () => {
          resolve(reader.result as string);
        };
        reader.readAsDataURL(file);
      });
    })
  );

  callback(res);
};

// 工具栏配置
const toolbars: ToolbarNames[] = [
  'bold',
  'underline',
  'italic',
  '-',
  'title',
  'strikeThrough',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  '-',
  'revoke',
  'next',
  'save',
  '=',
  'pageFullscreen',
  'fullscreen',
  'preview',
  'htmlPreview',
  'catalog'
];

// 过滤工具栏
const filteredToolbars = computed<ToolbarNames[]>(() => {
  return toolbars.filter(item => {
    if (item === '-' || item === '=') return true;
    return !props.toolbarsExclude.includes(item as string);
  }) as ToolbarNames[];
});

// 暴露内部 MdEditor 实例方法，便于父组件调用全屏
const mdEditorRef = ref<any>(null);

const togglePageFullscreen = (value?: boolean) => {
  mdEditorRef.value?.togglePageFullscreen?.(value);
};

const toggleFullscreen = (value?: boolean) => {
  mdEditorRef.value?.toggleFullscreen?.(value);
};

defineExpose({ togglePageFullscreen, toggleFullscreen });
</script>

<template>
  <div class="markdown-editor-wrapper">
    <!-- 纯预览模式 - 使用独立的轻量级预览组件 -->
    <div v-if="mode === 'preview'" class="preview-only-container" :style="{ height }">
      <div class="preview-content-wrapper">
        <MdPreview
          :id="editorId"
          :model-value="content || '*(暂无内容)*'"
          :language="'zh-CN'"
          :theme="theme"
          :preview-theme="previewTheme"
          :code-theme="codeTheme"
          :show-code-row-number="showCodeRowNumber"
        />
      </div>
    </div>

    <!-- 编辑模式和分屏模式 - 统一使用一个组件，通过动态属性控制 -->
    <MdEditor
      v-else
      ref="mdEditorRef"
      v-model="content"
      :id="editorId"
      :language="'zh-CN'"
      :theme="theme"
      :preview-theme="previewTheme"
      :code-theme="codeTheme"
      :toolbars="filteredToolbars"
      :preview="mode !== 'edit'"
      :placeholder="placeholder"
      :editor-style="{ height }"
      :disabled="disabled"
      :show-code-row-number="showCodeRowNumber"
      :max-length="maxLength"
      @save="handleSave"
      @upload-img="handleUploadImg"
    />
  </div>
</template>

<style lang="scss" scoped>
.markdown-editor-wrapper {
  width: 100%;

  :deep(.md-editor) {
    &.md-editor-dark {
      background-color: #1e1e20;

      .md-editor-toolbar {
        background-color: #2b2b2d;
        border-bottom-color: #4c4d4f;
      }

      .md-editor-content {
        .md-editor-input-wrapper {
          background-color: #1e1e20;
          color: #cfd3dc;
        }

        .md-editor-preview-wrapper {
          background-color: #1e1e20;
          color: #cfd3dc;
        }
      }
    }

    // 与Element Plus风格保持一致
    .md-editor-toolbar {
      .md-editor-toolbar-item {
        &:hover {
          background-color: var(--el-color-primary-light-9);
        }

        &.active {
          color: var(--el-color-primary);
        }
      }
    }
  }

  // 预览组件样式
  :deep(.md-editor-preview) {
    &.md-editor-preview-dark {
      background-color: #1e1e20;
      color: #cfd3dc;
    }

    &.md-editor-preview-light {
      background-color: #ffffff;
    }
  }

  // 预览容器样式 - 新的实现
  .preview-only-container {
    position: relative;
    width: 100%;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    background-color: #ffffff;
    overflow: hidden;

    .preview-content-wrapper {
      width: 100%;
      height: 100%;
      overflow-y: auto;
      overflow-x: hidden;

      // 让MdPreview内容自然流动
      :deep(.md-editor-preview) {
        height: auto !important;
        min-height: 100%;
        border: none !important;

        .md-editor-preview-wrapper {
          padding: 16px;
          height: auto !important;
          min-height: 100%;
          box-sizing: border-box;
        }
      }

      // 自定义滚动条
      &::-webkit-scrollbar {
        width: 8px;
      }

      &::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 4px;
      }

      &::-webkit-scrollbar-thumb {
        background: #c1c1c1;
        border-radius: 4px;

        &:hover {
          background: #a8a8a8;
        }
      }
    }
  }

  // 暗色模式下的预览容器
  :global(.dark) & {
    .preview-only-container {
      background-color: #1e1e20;
      border-color: #4c4d4f;

      .preview-content-wrapper {
        // 暗色模式滚动条
        &::-webkit-scrollbar-track {
          background: rgba(255, 255, 255, 0.05);
        }

        &::-webkit-scrollbar-thumb {
          background: rgba(255, 255, 255, 0.2);

          &:hover {
            background: rgba(255, 255, 255, 0.3);
          }
        }
      }
    }
  }
}
</style>
