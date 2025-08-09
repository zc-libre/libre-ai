import { ref, reactive, computed } from 'vue';
import { defineStore } from 'pinia';

export interface DashboardConfig {
  purpose: string;
  purposeText: string;
  purposeOption?: any;
  purposeDetail?: string;
  focusMetrics?: string;
  customRequirements?: string;
  layout: string;
  layoutText: string;
  layoutOption?: any;
  theme: string;
  themeText?: string;
  themeColors: Record<string, string>;
  components: string[];
  componentIds: string[];
  componentDataConfigs?: Record<string, any>; // 添加组件数据配置
  additionalDetails: string;
  generatedResult?: any;
  generationOptions?: any;
  timestamp?: number; // 添加时间戳字段
  customColors?: Record<string, string>; // 添加自定义颜色字段
}

export interface GenerationOptions {
  responsive: boolean;
  includeData: boolean;
  additionalRequirements: string;
}

export const useDashboardStore = defineStore('dashboard-generator', () => {
  // 状态
  const currentStep = ref(1);
  const isGenerating = ref(false);
  const showPreview = ref(false);
  const showHistory = ref(false);
  const streamingCode = ref(''); // 流式生成的代码
  const isStreaming = ref(false); // 是否正在流式生成

  // 向导数据
  const wizardData = reactive<DashboardConfig>({
    purpose: '',
    purposeText: '',
    purposeOption: null,
    layout: '',
    layoutText: '',
    layoutOption: null,
    theme: '',
    themeColors: {},
    components: [],
    componentIds: [],
    additionalDetails: '',
    generatedResult: null,
    generationOptions: null
  });

  // 生成选项
  const generationOptions = reactive<GenerationOptions>({
    responsive: true,
    includeData: true,
    additionalRequirements: ''
  });

  // 历史记录
  const history = ref<DashboardConfig[]>([]);

  // 计算属性
  const canProceedToStep = computed(() => (step: number) => {
    switch (step) {
      case 2:
        return !!wizardData.purpose;
      case 3:
        return (
          !!wizardData.purpose &&
          wizardData.componentIds &&
          wizardData.componentIds.length > 0
        );
      case 4:
        return (
          !!wizardData.purpose &&
          wizardData.componentIds &&
          wizardData.componentIds.length > 0 &&
          !!wizardData.theme
        );
      case 5:
        return (
          !!wizardData.purpose &&
          wizardData.componentIds &&
          wizardData.componentIds.length > 0 &&
          !!wizardData.theme &&
          !!wizardData.layout
        );
      default:
        return true;
    }
  });

  const isConfigComplete = computed(() => {
    return (
      wizardData.purpose &&
      wizardData.componentIds &&
      wizardData.componentIds.length > 0 &&
      wizardData.theme &&
      wizardData.layout
    );
  });

  const selectedComponentsCount = computed(() => {
    return wizardData.componentIds.length;
  });

  // Actions
  const setCurrentStep = (step: number) => {
    if (step >= 1 && step <= 5) {
      currentStep.value = step;
    }
  };

  const updateWizardData = (data: Partial<DashboardConfig>) => {
    Object.assign(wizardData, data);
  };

  const updateGenerationOptions = (options: Partial<GenerationOptions>) => {
    Object.assign(generationOptions, options);
  };

  const nextStep = () => {
    if (
      currentStep.value < 4 &&
      canProceedToStep.value(currentStep.value + 1)
    ) {
      currentStep.value++;
      if (currentStep.value === 4) {
        showPreview.value = true;
      }
    }
  };

  const prevStep = () => {
    if (currentStep.value > 1) {
      currentStep.value--;
    }
  };

  const resetWizard = () => {
    currentStep.value = 1;
    showPreview.value = false;
    isGenerating.value = false;
    isStreaming.value = false;
    streamingCode.value = '';

    // 重置向导数据
    Object.assign(wizardData, {
      purpose: '',
      purposeText: '',
      purposeOption: null,
      layout: '',
      layoutText: '',
      layoutOption: null,
      theme: '',
      themeText: '',
      themeColors: {},
      components: [],
      componentIds: [],
      additionalDetails: '',
      generatedResult: null,
      generationOptions: null
    });

    // 重置生成选项
    Object.assign(generationOptions, {
      responsive: true,
      includeData: true,
      additionalRequirements: ''
    });
  };

  const saveToHistory = () => {
    if (isConfigComplete.value) {
      const configCopy = JSON.parse(JSON.stringify(wizardData));
      configCopy.timestamp = Date.now();
      history.value.unshift(configCopy);

      // 限制历史记录数量
      if (history.value.length > 10) {
        history.value = history.value.slice(0, 10);
      }
    }
  };

  const loadFromHistory = (config: DashboardConfig) => {
    Object.assign(wizardData, config);
    currentStep.value = 1;
    showHistory.value = false;
  };

  const randomizeConfig = () => {
    const purposes = ['analytics', 'project', 'sales', 'monitoring'];
    const layouts = ['grid', 'sidebar', 'fullscreen'];
    const themes = [
      'modern-blue',
      'dark-purple',
      'green-nature',
      'orange-warm'
    ];

    wizardData.purpose = purposes[Math.floor(Math.random() * purposes.length)];
    wizardData.layout = layouts[Math.floor(Math.random() * layouts.length)];
    wizardData.theme = themes[Math.floor(Math.random() * themes.length)];

    currentStep.value = 4;
    showPreview.value = true;
  };

  const setGenerating = (generating: boolean) => {
    isGenerating.value = generating;
  };

  const setShowPreview = (show: boolean) => {
    showPreview.value = show;
  };

  const setShowHistory = (show: boolean) => {
    showHistory.value = show;
  };

  const setStreamingCode = (code: string) => {
    streamingCode.value = code;
  };

  const setIsStreaming = (streaming: boolean) => {
    isStreaming.value = streaming;
  };

  return {
    // 状态
    currentStep,
    isGenerating,
    showPreview,
    showHistory,
    wizardData,
    generationOptions,
    history,
    streamingCode,
    isStreaming,

    // 计算属性
    canProceedToStep,
    isConfigComplete,
    selectedComponentsCount,

    // Actions
    setCurrentStep,
    updateWizardData,
    updateGenerationOptions,
    nextStep,
    prevStep,
    resetWizard,
    saveToHistory,
    loadFromHistory,
    randomizeConfig,
    setGenerating,
    setShowPreview,
    setShowHistory,
    setStreamingCode,
    setIsStreaming
  };
});
