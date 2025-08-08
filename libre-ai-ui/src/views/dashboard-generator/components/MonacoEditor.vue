<template>
  <div ref="editorContainer" class="monaco-editor-container"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import * as monaco from 'monaco-editor';
import type { editor } from 'monaco-editor';
import editorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker';
import jsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker';
import cssWorker from 'monaco-editor/esm/vs/language/css/css.worker?worker';
import htmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker';
import tsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker';

// 配置 Monaco Editor Workers
if (typeof window !== 'undefined') {
  (self as any).MonacoEnvironment = {
    getWorker(_: any, label: string) {
      if (label === 'json') {
        return new jsonWorker();
      }
      if (label === 'css' || label === 'scss' || label === 'less') {
        return new cssWorker();
      }
      if (label === 'html' || label === 'handlebars' || label === 'razor') {
        return new htmlWorker();
      }
      if (label === 'typescript' || label === 'javascript') {
        return new tsWorker();
      }
      return new editorWorker();
    }
  };

  // 设置 TypeScript 编译选项
  monaco.languages.typescript.typescriptDefaults.setCompilerOptions({
    target: monaco.languages.typescript.ScriptTarget.ES2020,
    allowNonTsExtensions: true,
    moduleResolution: monaco.languages.typescript.ModuleResolutionKind.NodeJs,
    module: monaco.languages.typescript.ModuleKind.CommonJS,
    noEmit: true,
    esModuleInterop: true,
    jsx: monaco.languages.typescript.JsxEmit.React,
    reactNamespace: 'React',
    allowJs: true,
    typeRoots: ['node_modules/@types']
  });

  // 启用 TypeScript 语言服务
  monaco.languages.typescript.typescriptDefaults.setEagerModelSync(true);
  monaco.languages.typescript.javascriptDefaults.setEagerModelSync(true);

  // 设置诊断选项
  monaco.languages.typescript.typescriptDefaults.setDiagnosticsOptions({
    noSemanticValidation: false,
    noSyntaxValidation: false
  });
}

interface Props {
  modelValue: string;
  language?: string;
  theme?: string;
  readOnly?: boolean;
  options?: editor.IStandaloneEditorConstructionOptions;
  height?: string | number;
  width?: string | number;
}

const props = withDefaults(defineProps<Props>(), {
  language: 'html',
  theme: 'vs',
  readOnly: true,
  height: '100%',
  width: '100%'
});

const emit = defineEmits<{
  'update:modelValue': [value: string];
  change: [value: string];
  blur: [];
  focus: [];
}>();

const editorContainer = ref<HTMLElement>();
let monacoEditor: editor.IStandaloneCodeEditor | null = null;

// 初始化 Monaco Editor
const initEditor = async () => {
  if (!editorContainer.value) return;

  try {
    // 确保容器有高度
    if (editorContainer.value) {
      const height =
        typeof props.height === 'number' ? `${props.height}px` : props.height;
      const width =
        typeof props.width === 'number' ? `${props.width}px` : props.width;
      editorContainer.value.style.height = height;
      editorContainer.value.style.width = width;
    }

    await nextTick();

    // 创建编辑器实例
    monacoEditor = monaco.editor.create(editorContainer.value, {
      value: props.modelValue || '',
      language: props.language,
      theme: props.theme,
      readOnly: props.readOnly,
      automaticLayout: true,
      minimap: {
        enabled: true,
        maxColumn: 120
      },
      scrollBeyondLastLine: false,
      wordWrap: 'on',
      lineNumbers: 'on',
      renderWhitespace: 'selection',
      fontSize: 14,
      fontFamily:
        "'JetBrains Mono', 'Consolas', 'Monaco', 'Courier New', monospace",
      fontLigatures: true,
      tabSize: 2,
      insertSpaces: true,
      formatOnPaste: true,
      formatOnType: true,
      quickSuggestions: {
        other: true,
        comments: false,
        strings: false
      },
      parameterHints: {
        enabled: true
      },
      suggest: {
        showIcons: true,
        showSnippets: true,
        showWords: true
      },
      folding: true,
      foldingStrategy: 'auto',
      showFoldingControls: 'mouseover',
      matchBrackets: 'always',
      bracketPairColorization: {
        enabled: true
      },
      guides: {
        bracketPairs: true,
        indentation: true
      },
      cursorBlinking: 'blink',
      cursorSmoothCaretAnimation: 'on',
      renderLineHighlight: 'line',
      scrollbar: {
        vertical: 'visible',
        horizontal: 'visible',
        useShadows: false,
        verticalHasArrows: false,
        horizontalHasArrows: false,
        verticalScrollbarSize: 12,
        horizontalScrollbarSize: 12,
        arrowSize: 11
      },
      contextmenu: true,
      mouseWheelZoom: true,
      multiCursorModifier: 'ctrlCmd',
      accessibilitySupport: 'auto',
      find: {
        addExtraSpaceOnTop: false,
        autoFindInSelection: 'never',
        seedSearchStringFromSelection: 'always'
      },
      ...props.options
    });

    // 监听内容变化
    monacoEditor.onDidChangeModelContent(() => {
      const value = monacoEditor?.getValue() || '';
      emit('update:modelValue', value);
      emit('change', value);
    });

    // 监听焦点事件
    monacoEditor.onDidFocusEditorText(() => {
      emit('focus');
    });

    monacoEditor.onDidBlurEditorText(() => {
      emit('blur');
    });

    // 设置容器尺寸
    updateEditorSize();
  } catch (error) {
    console.error('Failed to initialize Monaco Editor:', error);
  }
};

// 更新编辑器尺寸
const updateEditorSize = () => {
  if (editorContainer.value) {
    const height =
      typeof props.height === 'number' ? `${props.height}px` : props.height;
    const width =
      typeof props.width === 'number' ? `${props.width}px` : props.width;

    editorContainer.value.style.height = height;
    editorContainer.value.style.width = width;

    // 触发编辑器布局更新
    nextTick(() => {
      monacoEditor?.layout();
    });
  }
};

// 监听 modelValue 变化
watch(
  () => props.modelValue,
  newValue => {
    if (monacoEditor && monacoEditor.getValue() !== newValue) {
      monacoEditor.setValue(newValue || '');
    }
  }
);

// 监听语言变化
watch(
  () => props.language,
  newLanguage => {
    if (monacoEditor) {
      const model = monacoEditor.getModel();
      if (model) {
        monaco.editor.setModelLanguage(model, newLanguage);
      }
    }
  }
);

// 监听主题变化
watch(
  () => props.theme,
  newTheme => {
    monaco.editor.setTheme(newTheme);
  }
);

// 监听只读状态变化
watch(
  () => props.readOnly,
  newReadOnly => {
    if (monacoEditor) {
      monacoEditor.updateOptions({ readOnly: newReadOnly });
    }
  }
);

// 监听尺寸变化
watch([() => props.height, () => props.width], () => {
  updateEditorSize();
});

// 组件挂载时初始化
onMounted(() => {
  initEditor();
});

// 组件卸载时销毁编辑器
onUnmounted(() => {
  if (monacoEditor) {
    monacoEditor.dispose();
    monacoEditor = null;
  }
});

// 暴露方法给父组件
defineExpose({
  getEditor: () => monacoEditor,
  getValue: () => monacoEditor?.getValue() || '',
  setValue: (value: string) => monacoEditor?.setValue(value || ''),
  focus: () => monacoEditor?.focus(),
  layout: () => monacoEditor?.layout(),
  formatDocument: () => {
    if (monacoEditor) {
      monacoEditor.getAction('editor.action.formatDocument')?.run();
    }
  },
  insertText: (text: string, position?: monaco.Position) => {
    if (monacoEditor) {
      if (position) {
        monacoEditor.setPosition(position);
      }
      monacoEditor.trigger('keyboard', 'type', { text });
    }
  },
  getSelection: () => monacoEditor?.getSelection(),
  setSelection: (selection: monaco.ISelection) => {
    if (monacoEditor) {
      monacoEditor.setSelection(selection);
    }
  },
  revealLine: (lineNumber: number) => {
    if (monacoEditor) {
      monacoEditor.revealLine(lineNumber);
    }
  },
  revealLineInCenter: (lineNumber: number) => {
    if (monacoEditor) {
      monacoEditor.revealLineInCenter(lineNumber);
    }
  },
  updateOptions: (newOptions: editor.IStandaloneEditorConstructionOptions) => {
    if (monacoEditor) {
      monacoEditor.updateOptions(newOptions);
    }
  }
});
</script>

<style scoped>
.monaco-editor-container {
  width: 100%;
  height: 100%;
  min-height: 200px;
  border: 1px solid #3e3e42;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  background-color: #1e1e1e;
  transition: border-color 0.3s ease;
}

.monaco-editor-container:hover {
  border-color: #007acc;
}

.monaco-editor-container:focus-within {
  border-color: #007acc;
  box-shadow: 0 0 0 2px rgba(0, 122, 204, 0.2);
}
</style>
