import { computed } from 'vue';
import { useDashboardStore } from './useDashboardStore';

export interface WizardStep {
  id: number;
  title: string;
  description: string;
  component: string;
  required: boolean;
}

export const useWizardNavigation = () => {
  const store = useDashboardStore();

  // 向导步骤定义
  const wizardSteps: WizardStep[] = [
    {
      id: 1,
      title: '看板用途',
      description: '选择看板的使用场景',
      component: 'Step1PurposeSelector',
      required: true
    },
    {
      id: 2,
      title: '布局样式',
      description: '选择看板的布局风格',
      component: 'Step2LayoutSelector',
      required: true
    },
    {
      id: 3,
      title: '主题组件',
      description: '配置主题色彩和组件',
      component: 'Step3ThemeComponentSelector',
      required: true
    },
    {
      id: 4,
      title: '生成看板',
      description: 'AI 智能生成看板代码',
      component: 'Step4DashboardGenerator',
      required: false
    }
  ];

  // 计算属性
  const currentStepInfo = computed(() => {
    return (
      wizardSteps.find(step => step.id === store.currentStep) || wizardSteps[0]
    );
  });

  const isFirstStep = computed(() => {
    return store.currentStep === 1;
  });

  const isLastStep = computed(() => {
    return store.currentStep === wizardSteps.length;
  });

  const canGoNext = computed(() => {
    return !isLastStep.value && store.canProceedToStep(store.currentStep + 1);
  });

  const canGoPrev = computed(() => {
    return !isFirstStep.value;
  });

  const progressPercentage = computed(() => {
    return (store.currentStep / wizardSteps.length) * 100;
  });

  const completedSteps = computed(() => {
    const completed = [];
    for (let i = 1; i < store.currentStep; i++) {
      if (store.canProceedToStep(i + 1)) {
        completed.push(i);
      }
    }
    return completed;
  });

  // 验证步骤完成状态
  const isStepCompleted = (stepId: number): boolean => {
    switch (stepId) {
      case 1:
        return !!store.wizardData.purpose;
      case 2:
        return !!store.wizardData.layout;
      case 3:
        return !!store.wizardData.theme;
      case 4:
        return !!store.wizardData.generatedResult;
      default:
        return false;
    }
  };

  const isStepValid = (stepId: number): boolean => {
    return store.canProceedToStep(stepId);
  };

  // 导航方法
  const goToStep = (stepId: number) => {
    if (stepId >= 1 && stepId <= wizardSteps.length) {
      // 检查是否可以跳转到该步骤
      if (stepId <= store.currentStep || store.canProceedToStep(stepId)) {
        store.setCurrentStep(stepId);
      }
    }
  };

  const nextStep = () => {
    if (canGoNext.value) {
      store.nextStep();
    }
  };

  const prevStep = () => {
    if (canGoPrev.value) {
      store.prevStep();
    }
  };

  const jumpToStep = (stepId: number) => {
    // 只允许跳转到已完成的步骤或下一步
    if (stepId <= store.currentStep + 1 && stepId >= 1) {
      goToStep(stepId);
    }
  };

  // 获取步骤状态
  const getStepStatus = (
    stepId: number
  ): 'completed' | 'current' | 'pending' | 'disabled' => {
    if (stepId < store.currentStep && isStepCompleted(stepId)) {
      return 'completed';
    } else if (stepId === store.currentStep) {
      return 'current';
    } else if (
      stepId === store.currentStep + 1 &&
      store.canProceedToStep(stepId)
    ) {
      return 'pending';
    } else {
      return 'disabled';
    }
  };

  // 获取步骤图标
  const getStepIcon = (stepId: number): string => {
    const status = getStepStatus(stepId);
    switch (status) {
      case 'completed':
        return 'check';
      case 'current':
        return 'edit';
      case 'pending':
        return 'arrow-right';
      default:
        return 'lock';
    }
  };

  // 重置向导
  const resetWizard = () => {
    store.resetWizard();
  };

  // 获取向导进度摘要
  const getProgressSummary = () => {
    const completed = wizardSteps.filter(step =>
      isStepCompleted(step.id)
    ).length;
    const total = wizardSteps.length;
    return {
      completed,
      total,
      percentage: (completed / total) * 100,
      isComplete: completed === total
    };
  };

  return {
    // 数据
    wizardSteps,

    // 计算属性
    currentStepInfo,
    isFirstStep,
    isLastStep,
    canGoNext,
    canGoPrev,
    progressPercentage,
    completedSteps,

    // 方法
    isStepCompleted,
    isStepValid,
    goToStep,
    nextStep,
    prevStep,
    jumpToStep,
    getStepStatus,
    getStepIcon,
    resetWizard,
    getProgressSummary
  };
};
