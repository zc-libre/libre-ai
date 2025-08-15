<template>
  <div class="vue-component-preview">
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-content">
        <el-icon class="spinning">
          <Loading />
        </el-icon>
        <span>正在初始化Vue预览...</span>
      </div>
    </div>

    <div v-else-if="error" class="error-display">
      <div class="error-content">
        <el-icon color="#F56C6C" :size="48">
          <Warning />
        </el-icon>
        <h3>预览错误</h3>
        <div class="error-message">
          <pre>{{ error }}</pre>
        </div>
        <el-button type="primary" @click="retryInit"> 重新加载 </el-button>
      </div>
    </div>

    <div v-else class="repl-container">
      <Sandbox
        :store="store"
        :show-compile-output="false"
        :autoResize="true"
        :clearConsole="false"
        @error="onReplError"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, nextTick } from 'vue';
import { Loading, Warning } from '@element-plus/icons-vue';
import { Sandbox, useStore, useVueImportMap } from '@vue/repl';

// Props
interface Props {
  vueCode: string;
  config?: any;
}

const props = withDefaults(defineProps<Props>(), {
  vueCode: '',
  config: () => ({})
});

// Emits
const emit = defineEmits<{
  'compilation-complete': [success: boolean];
  'runtime-error': [error: string];
}>();

// 状态
const isLoading = ref(true);
const error = ref('');
const store = ref();

// 获取基础URL（开发环境和生产环境兼容）
const getBaseUrl = () => {
  // 在生产环境中，使用相对路径或配置的基础URL
  // 如果设置了 VITE_BASE_URL，使用它；否则使用默认值
  const baseUrl = import.meta.env.VITE_BASE_URL || '';
  return baseUrl;
};

// 检查是否使用本地依赖（默认使用CDN，避免404错误）
const useLocalDeps = import.meta.env.VITE_USE_LOCAL_DEPS === 'true';

// CDN依赖配置（作为回退）
const CDN_IMPORT_MAP = {
  imports: {
    // Element Plus相关
    'element-plus':
      'https://cdn.jsdelivr.net/npm/element-plus@2/dist/index.full.min.mjs',
    '@element-plus/icons-vue':
      'https://cdn.jsdelivr.net/npm/@element-plus/icons-vue@2/dist/index.esm.js',

    // 图表库
    echarts: 'https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.esm.min.js',
    'vue-echarts':
      'https://cdn.jsdelivr.net/npm/vue-echarts@7/dist/index.esm.min.js',

    // 工具库
    'lodash-es': 'https://cdn.jsdelivr.net/npm/lodash-es@4/lodash.js',
    dayjs: 'https://cdn.jsdelivr.net/npm/dayjs@1/esm/index.js'
  }
};

// 本地依赖配置 - 兼容开发和生产环境
const LOCAL_IMPORT_MAP = {
  imports: {
    // Vue核心（这些会被useVueImportMap覆盖，但保留作为备用）
    vue: `${getBaseUrl()}/preview-libs/vue/vue.esm-browser.js`,
    '@vue/compiler-sfc': `${getBaseUrl()}/preview-libs/vue/compiler-sfc.esm-browser.js`,

    // Element Plus相关
    'element-plus': `${getBaseUrl()}/preview-libs/element-plus/index.full.js`,
    '@element-plus/icons-vue': `${getBaseUrl()}/preview-libs/element-plus/icons.js`,

    // 图表库
    echarts: `${getBaseUrl()}/preview-libs/echarts/echarts.min.js`,
    'vue-echarts': `${getBaseUrl()}/preview-libs/echarts/vue-echarts.esm.min.js`,

    // 工具库（这些通常较小，可以继续使用CDN）
    'lodash-es': 'https://cdn.jsdelivr.net/npm/lodash-es@4/lodash.js',
    dayjs: 'https://cdn.jsdelivr.net/npm/dayjs@1/esm/index.js'
  }
};

// 检查本地依赖是否可用
const checkLocalDepsAvailable = async () => {
  if (!useLocalDeps) return false;

  try {
    // 检查一个关键文件是否可访问
    const testUrl = `${getBaseUrl()}/preview-libs/vue/vue.esm-browser.js`;
    const response = await fetch(testUrl, { method: 'HEAD' });
    return response.ok;
  } catch (err) {
    console.warn('本地依赖不可用，将使用CDN:', err);
    return false;
  }
};

// 根据配置和可用性选择使用本地依赖还是CDN
let IMPORT_MAP = CDN_IMPORT_MAP; // 默认使用CDN

// 初始化store
const initStore = async () => {
  try {
    isLoading.value = true;
    error.value = '';

    console.log('开始初始化Vue REPL Store...');

    // 动态检查并选择依赖类型
    const localDepsAvailable = await checkLocalDepsAvailable();
    if (localDepsAvailable) {
      IMPORT_MAP = LOCAL_IMPORT_MAP;
      console.log('使用本地依赖');
    } else {
      IMPORT_MAP = CDN_IMPORT_MAP;
      console.log('使用CDN依赖');
    }

    console.log('ImportMap配置:', IMPORT_MAP);

    // 使用useVueImportMap来正确初始化导入映射
    const {
      importMap: builtinImportMap,
      vueVersion,
      productionMode
    } = useVueImportMap({
      // 指定Vue运行时的CDN链接
      runtimeDev: 'https://cdn.jsdelivr.net/npm/vue@3/dist/vue.esm-browser.js',
      runtimeProd:
        'https://cdn.jsdelivr.net/npm/vue@3/dist/vue.esm-browser.prod.js',
      serverRenderer:
        'https://cdn.jsdelivr.net/npm/@vue/server-renderer@3/dist/server-renderer.esm-browser.js'
    });

    // 合并自定义导入映射
    const customImportMap = {
      imports: {
        ...builtinImportMap.value.imports,
        ...IMPORT_MAP.imports
      }
    };

    console.log('使用的依赖类型:', useLocalDeps ? '本地依赖' : 'CDN依赖');
    console.log('合并后的导入映射:', customImportMap);

    // 设置Vue版本
    vueVersion.value = '3.5.13';
    productionMode.value = false; // 开发模式

    // 创建store实例 - 使用官方文档推荐的方式
    store.value = useStore(
      {
        // 使用合并后的导入映射
        builtinImportMap: ref(customImportMap),
        vueVersion,
        // 显示输出面板
        showOutput: ref(true),
        // 输出模式设置为预览
        outputMode: ref('preview')
      },
      // 第二个参数是序列化状态，用于恢复之前的状态，这里传入空字符串表示新建
      ''
    );

    console.log('Store创建完成:', {
      store: !!store.value,
      storeType: typeof store.value,
      hasState: !!store.value?.state,
      hasFiles: !!store.value?.state?.files
    });

    // 检查store是否正确初始化
    if (!store.value) {
      throw new Error('Store实例创建失败');
    }

    // 手动初始化store.state（这是@vue/repl的一个已知问题）
    if (!store.value.state) {
      console.log('手动初始化store.state...');
      // 创建完整的state结构，参考@vue/repl的内部结构
      store.value.state = {
        files: {},
        activeFile: 'App.vue',
        mainFile: 'App.vue',
        errors: [],
        runtimeError: null,
        vueVersion: vueVersion.value,
        // 添加其他可能需要的属性
        template: null,
        sfcOptions: {
          script: {},
          style: {},
          template: {}
        },
        typescriptVersion: 'latest',
        dependencyVersion: {},
        reloadLanguageTools: null
      };
    }

    // 确保files对象存在
    if (!store.value.state.files) {
      console.log('初始化files对象...');
      store.value.state.files = {};
    }

    console.log('Store状态手动初始化完成:', {
      hasState: !!store.value.state,
      hasFiles: !!store.value.state.files,
      stateKeys: Object.keys(store.value.state),
      filesKeys: Object.keys(store.value.state.files)
    });

    // 等待Vue的响应式系统更新
    await nextTick();

    // 验证store状态
    const stateCheck = checkStoreState(store.value);
    if (!stateCheck.isValid) {
      console.error('Store状态验证失败:', stateCheck.details);
      throw new Error(`Store状态无效: ${JSON.stringify(stateCheck.details)}`);
    }

    console.log('Store状态验证成功:', stateCheck.details);

    console.log('Store初始化完成，准备设置Vue代码...');

    // 设置初始文件
    if (props.vueCode) {
      console.log('开始更新Vue代码...');
      await updateVueCode();
    }

    // 最后等待一次确保所有异步操作完成
    await nextTick();

    isLoading.value = false;
    emit('compilation-complete', true);
    console.log('Vue预览初始化成功完成');
  } catch (err) {
    console.error('初始化Vue预览失败:', err);
    console.error(
      '错误堆栈:',
      err instanceof Error ? err.stack : 'No stack trace'
    );
    error.value = err instanceof Error ? err.message : String(err);
    isLoading.value = false;
    emit('compilation-complete', false);
  }
};

// 更新Vue代码
const updateVueCode = async () => {
  if (!store.value || !props.vueCode) return;

  try {
    // 检查传入的vueCode
    console.log('收到的vueCode类型:', typeof props.vueCode);
    console.log(
      'vueCode内容预览:',
      props.vueCode?.substring?.(0, 100) || '无内容'
    );

    // 处理Vue代码，添加必要的样式
    const processedCode = processVueCode(props.vueCode);

    console.log('处理后的代码预览:', processedCode.substring(0, 200) + '...');

    // 确保files对象存在
    if (!store.value.state.files) {
      store.value.state.files = {};
    }

    // 使用最简单的方式设置文件，避免复杂的对象结构
    const files = {
      'App.vue': processedCode
    };

    // 尝试使用store的setFiles方法
    if (typeof store.value.setFiles === 'function') {
      try {
        store.value.setFiles(files);
        console.log('使用setFiles方法设置文件成功');
      } catch (err) {
        console.warn('setFiles方法失败:', err);
        // 如果setFiles失败，直接设置到state
        store.value.state.files['App.vue'] = {
          filename: 'App.vue',
          code: processedCode
        };
      }
    } else {
      // 直接设置到state
      store.value.state.files['App.vue'] = {
        filename: 'App.vue',
        code: processedCode
      };
    }

    // 设置活动文件
    store.value.state.activeFile = 'App.vue';
    store.value.state.mainFile = 'App.vue';

    // 尝试调用setActive
    if (typeof store.value.setActive === 'function') {
      try {
        store.value.setActive('App.vue');
        console.log('设置活动文件成功');
      } catch (err) {
        console.warn('setActive失败:', err);
      }
    }

    // 等待一个tick让变化生效
    await nextTick();

    console.log('文件设置完成:', {
      activeFile: store.value.state.activeFile,
      mainFile: store.value.state.mainFile,
      filesCount: Object.keys(store.value.state.files).length,
      fileNames: Object.keys(store.value.state.files)
    });
  } catch (err) {
    console.error('更新Vue代码失败:', err);
    emit('runtime-error', err instanceof Error ? err.message : String(err));
  }
};

// 检查Store状态的辅助函数
const checkStoreState = (store: any): { isValid: boolean; details: any } => {
  const details = {
    hasStore: !!store,
    storeType: typeof store,
    hasState: false,
    hasFiles: false,
    hasSetActive: false,
    hasSetFiles: false,
    stateKeys: [],
    filesKeys: []
  };

  if (!store) {
    return { isValid: false, details };
  }

  details.hasState = !!store.state;
  details.hasSetActive = typeof store.setActive === 'function';
  details.hasSetFiles = typeof store.setFiles === 'function';

  if (store.state) {
    details.stateKeys = Object.keys(store.state);
    details.hasFiles = !!store.state.files;

    if (store.state.files) {
      details.filesKeys = Object.keys(store.state.files);
    }
  }

  const isValid = details.hasStore && details.hasState && details.hasFiles;
  return { isValid, details };
};

// 处理Vue代码，注入样式和依赖
const processVueCode = (code: string | any): string => {
  // 确保code是字符串类型
  if (typeof code !== 'string') {
    console.error('processVueCode收到非字符串参数:', typeof code, code);
    return ''; // 返回空字符串作为默认值
  }

  let processedCode = code.trim(); // 先trim处理

  // 检查是否使用传统的export default语法，如果是，则保持不变
  // @vue/repl可以处理两种格式
  if (processedCode.includes('export default')) {
    // 传统Vue组件格式，保持原样
  } else if (
    !processedCode.includes('<script setup>') &&
    processedCode.includes('<script>')
  ) {
    // 如果是普通script标签但没有setup，可能需要处理
  }

  // 如果代码中没有<style>标签，添加必要的样式
  if (!processedCode.includes('<style')) {
    const baseUrl = getBaseUrl();
    const styleTag = `
<style>
@import url('${baseUrl}/preview-libs/element-plus/index.css');
@import url('${baseUrl}/preview-libs/tailwind/tailwind-full.min.css');
</style>`;

    // 在组件最后添加样式
    processedCode = `${processedCode}\n${styleTag}`;
  }

  return processedCode;
};

// 错误处理
const onReplError = (err: any) => {
  console.error('REPL运行时错误详情:', {
    error: err,
    message: err?.message || 'No message',
    stack: err?.stack || 'No stack trace',
    type: typeof err,
    timestamp: new Date().toISOString()
  });

  // 尝试提取更有用的错误信息
  let errorMessage = 'Unknown REPL error';

  if (err?.message) {
    errorMessage = err.message;
  } else if (typeof err === 'string') {
    errorMessage = err;
  } else if (err?.toString && typeof err.toString === 'function') {
    errorMessage = err.toString();
  }

  // 发送错误事件
  emit('runtime-error', errorMessage);
};

// 重试初始化 - 增强版本
const retryInit = async () => {
  console.log('用户触发重试初始化...');

  // 清理之前的store实例
  if (store.value) {
    console.log('清理之前的store实例');
    store.value = null;
  }

  // 重置状态
  isLoading.value = true;
  error.value = '';

  // 短暂延迟后重新初始化
  await new Promise(resolve => setTimeout(resolve, 100));

  // 重新初始化
  await initStore();
};

// 监听代码变化
watch(
  () => props.vueCode,
  () => {
    if (store.value) {
      updateVueCode();
    }
  },
  { immediate: false }
);

// 组件挂载
onMounted(() => {
  nextTick(() => {
    initStore();
  });
});
</script>

<style scoped>
@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.vue-component-preview {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #fff;
  border-radius: 8px;
}

.loading-overlay,
.error-display {
  position: absolute;
  inset: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgb(255 255 255 / 90%);
}

.loading-content,
.error-content {
  padding: 2rem;
  text-align: center;
}

.loading-content .spinning {
  margin-bottom: 1rem;
  font-size: 2rem;
  color: #409eff;
  animation: spin 1s linear infinite;
}

.error-content h3 {
  margin: 1rem 0;
  color: #f56c6c;
}

.error-message {
  max-width: 500px;
  max-height: 200px;
  padding: 1rem;
  margin: 1rem 0;
  overflow: auto;
  text-align: left;
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
}

.error-message pre {
  margin: 0;
  font-size: 12px;
  color: #f56c6c;
  word-break: break-word;
  white-space: pre-wrap;
}

.repl-container {
  width: 100%;
  height: 100%;
}

/* 覆盖@vue/repl的默认样式 */
:deep(.vue-repl) {
  height: 100% !important;
  border: none !important;
}

:deep(.output-container) {
  border: none !important;
}

:deep(.preview-container) {
  background: #fff !important;
}
</style>
