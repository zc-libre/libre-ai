import { defineStore } from 'pinia';
import { ref, reactive } from 'vue';
import type {
  DashboardConfig,
  DashboardHistory
} from '@/views/dashboard-generator/types';

export const useDashboardGeneratorStore = defineStore(
  'dashboardGenerator',
  () => {
    // 当前向导状态
    const currentStep = ref(1);
    const isGenerating = ref(false);
    const showPreview = ref(false);
    const showHistory = ref(false);

    // 向导数据
    const wizardData = reactive<DashboardConfig>({
      purpose: '',
      layout: '',
      theme: '',
      components: []
    });

    // 生成结果
    const generatedDashboard = ref<string>('');
    const generationError = ref<string>('');

    // 历史记录
    const historyRecords = ref<DashboardHistory[]>([]);

    // Actions
    const setCurrentStep = (step: number) => {
      currentStep.value = step;
    };

    const updateWizardData = (data: Partial<DashboardConfig>) => {
      Object.assign(wizardData, data);
    };

    const resetWizardData = () => {
      wizardData.purpose = '';
      wizardData.layout = '';
      wizardData.theme = '';
      wizardData.components = [];
      currentStep.value = 1;
    };

    const setGeneratedDashboard = (html: string) => {
      generatedDashboard.value = html;
    };

    const setGenerating = (generating: boolean) => {
      isGenerating.value = generating;
    };

    const setGenerationError = (error: string) => {
      generationError.value = error;
    };

    const togglePreview = () => {
      showPreview.value = !showPreview.value;
    };

    const toggleHistory = () => {
      showHistory.value = !showHistory.value;
    };

    // 历史记录管理
    const addHistoryRecord = (record: DashboardHistory) => {
      historyRecords.value.unshift(record);
      // 限制历史记录数量为50条
      if (historyRecords.value.length > 50) {
        historyRecords.value = historyRecords.value.slice(0, 50);
      }
      // 保存到本地存储
      saveHistoryToLocal();
    };

    const removeHistoryRecord = (id: string) => {
      const index = historyRecords.value.findIndex(record => record.id === id);
      if (index > -1) {
        historyRecords.value.splice(index, 1);
        saveHistoryToLocal();
      }
    };

    const loadHistoryRecord = (id: string) => {
      const record = historyRecords.value.find(r => r.id === id);
      if (record) {
        updateWizardData(record.config);
        setGeneratedDashboard(record.generatedHtml);
        currentStep.value = 4;
        showPreview.value = true;
      }
    };

    // 本地存储操作
    const saveHistoryToLocal = () => {
      try {
        localStorage.setItem(
          'dashboard_generator_history',
          JSON.stringify(historyRecords.value)
        );
      } catch (error) {
        console.error('Failed to save history to localStorage:', error);
      }
    };

    const loadHistoryFromLocal = () => {
      try {
        const saved = localStorage.getItem('dashboard_generator_history');
        if (saved) {
          historyRecords.value = JSON.parse(saved);
        }
      } catch (error) {
        console.error('Failed to load history from localStorage:', error);
        historyRecords.value = [];
      }
    };

    // 初始化时加载历史记录
    loadHistoryFromLocal();

    return {
      // State
      currentStep,
      isGenerating,
      showPreview,
      showHistory,
      wizardData,
      generatedDashboard,
      generationError,
      historyRecords,

      // Actions
      setCurrentStep,
      updateWizardData,
      resetWizardData,
      setGeneratedDashboard,
      setGenerating,
      setGenerationError,
      togglePreview,
      toggleHistory,
      addHistoryRecord,
      removeHistoryRecord,
      loadHistoryRecord,
      saveHistoryToLocal,
      loadHistoryFromLocal
    };
  }
);
