<template>
  <div class="step1-purpose-selector">
    <div class="step-header">
      <h2 class="step-title">
        <el-icon class="step-icon" :size="24" color="#6366F1">
          <DataAnalysis />
        </el-icon>
        选择看板用途
      </h2>
      <p class="step-description">
        请选择您要创建的看板类型，我们将根据您的选择推荐合适的布局和组件。
      </p>
    </div>

    <div class="purpose-grid">
      <div
        v-for="option in purposeOptions"
        :key="option.id"
        class="purpose-card"
        :class="{ selected: selectedPurpose === option.id }"
        @click="selectPurpose(option)"
      >
        <div class="card-icon">
          <el-icon
            :size="32"
            :color="selectedPurpose === option.id ? '#fff' : option.color"
          >
            <component :is="option.icon" />
          </el-icon>
        </div>
        <h3 class="card-title">{{ option.title }}</h3>
        <p class="card-description">{{ option.description }}</p>
        <div class="card-features">
          <el-tag
            v-for="feature in option.features"
            :key="feature"
            size="small"
            :type="selectedPurpose === option.id ? 'info' : 'default'"
          >
            {{ feature }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 自定义用途输入 -->
    <div v-if="selectedPurpose === 'custom'" class="custom-purpose">
      <el-input
        v-model="customPurposeText"
        placeholder="请描述您的看板用途..."
        type="textarea"
        :rows="3"
        maxlength="200"
        show-word-limit
        @input="updateCustomPurpose"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { Box, Location, Van, Grid, More } from '@element-plus/icons-vue';

// Props
interface Props {
  wizardData: any;
}

const props = defineProps<Props>();

// Emits
const emit = defineEmits<{
  update: [data: any];
}>();

// 状态
const selectedPurpose = ref(props.wizardData.purpose || '');
const customPurposeText = ref(props.wizardData.purposeText || '');

// 看板用途选项 - 物流仓储监控
const purposeOptions = [
  {
    id: 'shelf',
    icon: Box,
    title: '货架监控看板',
    description: '实时监控货架状态、库存分布和拣选效率',
    color: '#409EFF',
    features: ['货架状态', '库存分布', '拣选效率']
  },
  {
    id: 'location',
    icon: Location,
    title: '仓位监控看板',
    description: '监控仓位利用率、占用状态和物料分布',
    color: '#67C23A',
    features: ['仓位利用率', '占用状态', '物料分布']
  },
  {
    id: 'transport',
    icon: Van,
    title: '搬运任务监控',
    description: 'AGV搬运任务跟踪、路径规划和设备状态',
    color: '#E6A23C',
    features: ['AGV任务', '路径规划', '设备状态']
  },
  {
    id: 'mixed',
    icon: Grid,
    title: '混合监控看板',
    description: '综合展示仓储、物料和搬运全流程监控',
    color: '#F56C6C',
    features: ['全局监控', '多维分析', '综合指标']
  },
  {
    id: 'custom',
    icon: More,
    title: '自定义用途',
    description: '根据您的具体需求定制专属监控看板',
    color: '#909399',
    features: ['自定义配置', '灵活定制', '个性化']
  }
];

// 计算属性
const selectedOption = computed(() => {
  return purposeOptions.find(option => option.id === selectedPurpose.value);
});

// 方法
const selectPurpose = (option: any) => {
  selectedPurpose.value = option.id;

  const updateData = {
    purpose: option.id,
    purposeText:
      option.id === 'custom' ? customPurposeText.value : option.title,
    purposeOption: option
  };

  emit('update', updateData);
};

const updateCustomPurpose = () => {
  if (selectedPurpose.value === 'custom') {
    emit('update', {
      purpose: 'custom',
      purposeText: customPurposeText.value
    });
  }
};
</script>

<style scoped>
.step1-purpose-selector {
  margin: 0 auto;
}

.step-header {
  text-align: center;
  margin-bottom: 40px;
}

.step-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.step-description {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.purpose-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 30px;
  justify-content: flex-start;
}

.purpose-card {
  flex: 0 0 calc(20% - 13px);
  min-width: 180px;
  border: 2px solid #ebeef5;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  text-align: center;
}

.purpose-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.purpose-card.selected {
  border-color: #409eff;
  background: linear-gradient(135deg, #409eff, #6366f1);
  color: white;
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.3);
}

.card-icon {
  margin-bottom: 16px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: inherit;
}

.card-description {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 16px;
  opacity: 0.9;
}

.card-features {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
}

.custom-purpose {
  margin-top: 20px;
}

/* 响应式设计 - 与项目整体断点保持一致 */
@media (max-width: 990px) {
  .purpose-card {
    flex: 0 0 calc(50% - 8px);
  }
}

@media (max-width: 760px) {
  .purpose-grid {
    flex-direction: column;
    gap: 12px;
  }

  .purpose-card {
    flex: 1 1 auto;
    min-width: unset;
    padding: 16px;
  }

  .step-title {
    font-size: 20px;
  }
}
</style>
