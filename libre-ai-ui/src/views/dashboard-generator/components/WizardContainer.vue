<template>
  <div class="wizard-container h-full flex flex-col">
    <!-- 步骤内容 -->
    <div class="step-content flex-1 p-4 sm:p-6 overflow-y-auto min-h-0">
      <component
        :is="currentStepComponent"
        ref="currentStepRef"
        :wizard-data="wizardData"
        @update="handleDataUpdate"
        @next="handleNext"
        @prev="handlePrev"
        @generate="handleTriggerGenerate"
      />
    </div>

    <!-- 底部导航 -->
    <div
      class="wizard-navigation border-t border-gray-200 dark:border-gray-700 p-4 sm:p-6 bg-gray-50 dark:bg-gray-800/50"
    >
      <div
        class="nav-buttons flex flex-col sm:flex-row gap-3 sm:gap-4 items-stretch sm:items-center"
      >
        <el-button
          v-if="currentStep > 1"
          :icon="ArrowLeft"
          class="order-2 sm:order-1"
          @click="handlePrev"
        >
          上一步
        </el-button>

        <div class="nav-spacer flex-1 hidden sm:block" />

        <el-button
          v-if="currentStep < 5"
          type="primary"
          :icon="ArrowRight"
          :disabled="!canProceed"
          class="order-1 sm:order-2"
          @click="handleNext"
        >
          下一步
        </el-button>

        <!-- 生成看板按钮 - 只在未生成时显示 -->
        <el-button
          v-if="currentStep === 5 && !props.hasGenerated"
          type="success"
          :icon="Check"
          :loading="props.isGenerating"
          class="order-1 sm:order-2"
          @click="handleGenerate"
        >
          {{ props.isGenerating ? '生成中...' : '生成看板' }}
        </el-button>

        <!-- 重新生成按钮 - 只在已生成时显示 -->
        <el-button
          v-if="currentStep === 5 && props.hasGenerated && !props.isGenerating"
          type="warning"
          :icon="Refresh"
          class="order-1 sm:order-2"
          @click="handleRegenerate"
        >
          重新生成
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { ArrowLeft, ArrowRight, Check, Refresh } from '@element-plus/icons-vue';
import Step1PurposeSelector from './steps/Step1PurposeSelector.vue';
import Step2ComponentSelector from './steps/Step2ComponentSelector.vue';
import Step3ThemeSelector from './steps/Step3ThemeSelector.vue';
import Step4LayoutSelector from './steps/Step4LayoutSelector.vue';
import Step5DashboardGenerator from './steps/Step5DashboardGenerator.vue';

// Props
import type { DashboardConfig as StoreDashboardConfig } from '../composables/useDashboardStore';

interface Props {
  currentStep: number;
  wizardData: StoreDashboardConfig;
  isGenerating?: boolean;
  hasGenerated?: boolean;
}

const props = defineProps<Props>();

// Emits
const emit = defineEmits<{
  'step-change': [step: number];
  'data-update': [data: any];
  generate: [];
}>();

// 状态（已移除 isGenerating，使用 props 传入）
const currentStepRef = ref<any>(null);

// 计算属性
const currentStepComponent = computed(() => {
  const components = {
    1: Step1PurposeSelector,
    2: Step2ComponentSelector,
    3: Step3ThemeSelector,
    4: Step4LayoutSelector,
    5: Step5DashboardGenerator
  };
  return components[props.currentStep] || Step1PurposeSelector;
});

const canProceed = computed(() => {
  switch (props.currentStep) {
    case 1:
      return !!props.wizardData.purpose;
    case 2:
      return (
        props.wizardData.componentIds &&
        props.wizardData.componentIds.length > 0
      );
    case 3:
      return !!props.wizardData.theme;
    case 4:
      return !!props.wizardData.layout;
    default:
      return true;
  }
});

// 事件处理
const handleDataUpdate = (data: any) => {
  emit('data-update', data);
};

const handleNext = () => {
  if (canProceed.value && props.currentStep < 5) {
    emit('step-change', props.currentStep + 1);
  }
};

const handlePrev = () => {
  if (props.currentStep > 1) {
    emit('step-change', props.currentStep - 1);
  }
};

const handleGenerate = () => {
  if (
    !props.wizardData.purpose ||
    !props.wizardData.componentIds ||
    props.wizardData.componentIds.length === 0 ||
    !props.wizardData.theme ||
    !props.wizardData.layout
  ) {
    ElMessage.warning('请完成所有配置步骤');
    return;
  }

  // 如果当前是第5步，直接调用组件的生成方法
  if (props.currentStep === 5 && currentStepRef.value?.generateDashboard) {
    currentStepRef.value.generateDashboard();
  }
};

// 处理组件触发的生成事件
const handleTriggerGenerate = () => {
  handleGenerate();
};

// 处理重新生成
const handleRegenerate = () => {
  if (props.currentStep === 5 && currentStepRef.value?.regenerateDashboard) {
    currentStepRef.value.regenerateDashboard();
  }
};
</script>

<style scoped>
/* 响应式设计已通过Tailwind CSS类名实现 */
</style>
