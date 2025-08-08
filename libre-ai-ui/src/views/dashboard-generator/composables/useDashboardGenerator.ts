import { computed, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useDashboardStore } from './useDashboardStore';
import type { DashboardRequest } from '@/api/dashboard-generator';
import {
  generateDashboard as apiGenerateDashboard,
  generateDashboardStream,
  saveHistory
} from '@/api/dashboard-generator';

export interface GenerationProgress {
  step: number;
  total: number;
  message: string;
  percentage: number;
}

export interface GenerationResult {
  html: string;
  css: string;
  javascript: string;
  linesOfCode: number;
  components: number;
  fileSize: string;
  description: string;
  timestamp: number;
}

export const useDashboardGenerator = () => {
  const store = useDashboardStore();

  // 生成状态
  const generationProgress = ref<GenerationProgress>({
    step: 0,
    total: 6,
    message: '',
    percentage: 0
  });

  const generationResult = ref<GenerationResult | null>(null);
  const streamingCode = ref<string>(''); // 流式接收的代码
  const isStreaming = ref(false); // 是否正在流式接收
  const abortController = ref<AbortController | null>(null); // 用于中止流式请求

  // 生成步骤
  const generationSteps = [
    '分析配置信息',
    '生成布局结构',
    '应用主题样式',
    '集成组件代码',
    '优化代码质量',
    '生成完成'
  ];

  // 计算属性
  const isGenerating = computed(() => store.isGenerating);
  const canGenerate = computed(() => store.isConfigComplete);

  // 生成看板代码（流式版本）
  const generateDashboardWithStream =
    async (): Promise<GenerationResult | null> => {
      if (!canGenerate.value) {
        ElMessage.warning('请完成所有必要的配置');
        return null;
      }

      store.setGenerating(true);
      store.setIsStreaming(true);
      store.setStreamingCode('');
      generationResult.value = null;
      streamingCode.value = '';
      isStreaming.value = true;

      try {
        // 更新进度 - 开始生成
        generationProgress.value = {
          step: 0,
          total: generationSteps.length,
          message: '准备生成看板...',
          percentage: 10
        };

        // 准备API请求参数
        const { wizardData, generationOptions } = store;
        
        // 构建组件配置数组
        const componentConfigs = wizardData.componentIds.map((id: string) => {
          const config = wizardData.componentDataConfigs?.[id] || {};
          return {
            componentId: id,
            componentType: id,
            dataSource: config.dataSource,
            refreshInterval: config.refreshInterval,
            dataStructure: {
              xField: config.xField,
              yField: config.yField,
              seriesField: config.seriesField,
              nameField: config.nameField,
              valueField: config.valueField,
              title: config.title,
              unit: config.unit,
              comparison: config.comparison,
              trend: config.trend,
              columns: config.columns,
              pagination: config.pagination,
              pageSize: config.pageSize,
              sampleData: config.sampleData
            }
          };
        });
        
        const request: DashboardRequest = {
          purpose: wizardData.purpose,
          purposeDetail: wizardData.purposeDetail || '',
          focusMetrics: wizardData.focusMetrics || '',
          customRequirements: wizardData.customRequirements || '',
          layout: wizardData.layout,
          theme: {
            name: wizardData.themeText || wizardData.theme,
            colors: {
              primary: wizardData.themeColors?.primary || '#409EFF',
              secondary: wizardData.themeColors?.secondary || '#79BBFF',
              accent: wizardData.themeColors?.accent || '#A0CFFF'
            }
          },
          components: wizardData.componentIds,
          componentConfigs: componentConfigs,
          options: {
            codeStyle: generationOptions.codeStyle || 'modern',
            responsive: generationOptions.responsive !== false,
            includeData: generationOptions.includeData !== false,
            additionalRequirements:
              generationOptions.additionalRequirements || ''
          }
        };

        // 调用流式API
        generationProgress.value = {
          step: 2,
          total: generationSteps.length,
          message: 'AI正在生成代码...',
          percentage: 30
        };

        // 使用流式接口
        abortController.value = await generateDashboardStream(
          request,
          // onChunk - 接收每个代码片段
          (chunk: string) => {
            streamingCode.value += chunk;
            console.log('chunk', chunk);
            console.log(
              'containsNewLine',
              chunk.includes('\n') || chunk.includes('\r')
            );
            store.setStreamingCode(streamingCode.value); // 更新store中的流式代码

            // 动态更新进度
            const currentLength = streamingCode.value.length;
            const estimatedTotal = 5000; // 估计的总长度
            generationProgress.value.percentage = Math.min(
              90,
              (currentLength / estimatedTotal) * 60 + 30
            );
          },
          // onComplete - 完成时的处理
          (fullContent: string) => {
            // 转换为结果格式
            const result: GenerationResult = {
              html: fullContent,
              css: '', // 已内嵌在HTML中
              javascript: '', // 已内嵌在HTML中
              linesOfCode: fullContent.split('\n').length,
              components: wizardData.componentIds.length,
              fileSize: `${Math.round(fullContent.length / 1024)}KB`,
              description: `基于${wizardData.themeText || '默认'}主题的${wizardData.layoutText}看板已生成完成，包含${wizardData.componentIds.length}个组件。`,
              timestamp: Date.now()
            };

            generationResult.value = result;
            isStreaming.value = false;
            store.setIsStreaming(false);

            // 更新进度 - 保存历史
            generationProgress.value = {
              step: 4,
              total: generationSteps.length,
              message: '保存生成历史...',
              percentage: 95
            };

            // 保存到后端历史记录
            saveHistory({
              config: request,
              generatedHtml: result.html,
              generatedCss: result.css,
              generatedJs: result.javascript
            }).catch(error => {
              console.error('保存历史记录失败:', error);
            });

            // 保存到本地存储
            store.updateWizardData({ generatedResult: result });
            store.saveToHistory();

            // 完成
            generationProgress.value = {
              step: generationSteps.length - 1,
              total: generationSteps.length,
              message: '生成完成！',
              percentage: 100
            };

            store.setGenerating(false);
            ElMessage.success('看板生成成功！');
          },
          // onError - 错误处理
          (error: Error) => {
            ElMessage.error(error.message || '生成失败，请重试');
            console.error('Generation error:', error);
            isStreaming.value = false;
            store.setIsStreaming(false);
            store.setGenerating(false);
          }
        );

        return generationResult.value;
      } catch (error: any) {
        ElMessage.error(error.message || '生成失败，请重试');
        console.error('Generation error:', error);
        isStreaming.value = false;
        store.setIsStreaming(false);
        store.setGenerating(false);
        return null;
      }
    };

  // 生成看板代码（原有非流式版本，保留作为备用）
  const generateDashboard = async (): Promise<GenerationResult | null> => {
    if (!canGenerate.value) {
      ElMessage.warning('请完成所有必要的配置');
      return null;
    }

    store.setGenerating(true);
    generationResult.value = null;

    try {
      // 更新进度 - 开始生成
      generationProgress.value = {
        step: 0,
        total: generationSteps.length,
        message: '准备生成看板...',
        percentage: 10
      };

      // 准备API请求参数
      const { wizardData, generationOptions } = store;
      const request: DashboardRequest = {
        purpose: wizardData.purpose,
        purposeDetail: wizardData.purposeDetail || '',
        focusMetrics: wizardData.focusMetrics || '',
        customRequirements: wizardData.customRequirements || '',
        layout: wizardData.layout,
        theme: {
          name: wizardData.themeText || wizardData.theme,
          colors: {
            primary: wizardData.themeColors?.primary || '#409EFF',
            secondary: wizardData.themeColors?.secondary || '#79BBFF',
            accent: wizardData.themeColors?.accent || '#A0CFFF'
          }
        },
        components: wizardData.componentIds,
        options: {
          codeStyle: generationOptions.codeStyle || 'modern',
          responsive: generationOptions.responsive !== false,
          includeData: generationOptions.includeData !== false,
          additionalRequirements: generationOptions.additionalRequirements || ''
        }
      };

      // 调用后端API生成代码
      generationProgress.value = {
        step: 2,
        total: generationSteps.length,
        message: 'AI正在生成代码...',
        percentage: 50
      };

      const response = (await apiGenerateDashboard(request)) as {
        result?: any;
      };

      // 后端返回的是 R<T> 格式，数据在 result 字段中
      if (!response.result) {
        throw new Error('生成失败：未收到有效响应');
      }

      const apiResult = response.result;

      // 转换API响应为前端格式
      const result: GenerationResult = {
        html: apiResult.html,
        css: '', // 已内嵌在HTML中
        javascript: '', // 已内嵌在HTML中
        linesOfCode: apiResult.html.split('\n').length,
        components: wizardData.componentIds.length,
        fileSize: `${Math.round(apiResult.html.length / 1024)}KB`,
        description: `基于${wizardData.themeText || '默认'}主题的${wizardData.layoutText}看板已生成完成，包含${wizardData.componentIds.length}个组件。`,
        timestamp: Date.now()
      };

      generationResult.value = result;

      // 更新进度 - 保存历史
      generationProgress.value = {
        step: 4,
        total: generationSteps.length,
        message: '保存生成历史...',
        percentage: 80
      };

      // 保存到后端历史记录
      try {
        await saveHistory({
          config: request,
          generatedHtml: result.html,
          generatedCss: result.css,
          generatedJs: result.javascript
        });
      } catch (saveError) {
        console.error('保存历史记录失败:', saveError);
        // 不影响主流程
      }

      // 保存到本地存储
      store.updateWizardData({ generatedResult: result });
      store.saveToHistory();

      // 完成
      generationProgress.value = {
        step: generationSteps.length - 1,
        total: generationSteps.length,
        message: '生成完成！',
        percentage: 100
      };

      ElMessage.success('看板生成成功！');
      return result;
    } catch (error: any) {
      ElMessage.error(error.message || '生成失败，请重试');
      console.error('Generation error:', error);
      return null;
    } finally {
      store.setGenerating(false);
    }
  };

  // 下载代码
  const downloadCode = (result: GenerationResult) => {
    // 只需要下载一个完整的HTML文件
    const blob = new Blob([result.html], { type: 'text/html' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'dashboard.html';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    ElMessage.success('看板文件下载完成');
  };

  // 预览代码
  const previewCode = (result: GenerationResult) => {
    // HTML已经是完整的文档，直接打开预览
    const previewWindow = window.open('', '_blank');
    if (previewWindow) {
      previewWindow.document.write(result.html);
      previewWindow.document.close();
    }
  };

  // 中止流式生成
  const abortGeneration = () => {
    if (abortController.value) {
      abortController.value.abort();
      abortController.value = null;
      streamingCode.value = '';
      isStreaming.value = false;
      store.setGenerating(false);
      store.setIsStreaming(false);
      store.setStreamingCode('');
      ElMessage.info('已中止生成');
    }
  };

  return {
    // 状态
    generationProgress,
    generationResult,
    isGenerating,
    canGenerate,
    streamingCode,
    isStreaming,

    // 方法
    generateDashboard: generateDashboardWithStream, // 默认使用流式版本
    generateDashboardNonStream: generateDashboard, // 非流式版本作为备用
    downloadCode,
    previewCode,
    abortGeneration
  };
};
