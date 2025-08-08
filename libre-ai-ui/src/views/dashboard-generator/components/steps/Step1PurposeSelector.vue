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
            :type="selectedPurpose === option.id ? 'info' : ''"
          >
            {{ feature }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 场景细化配置 -->
    <div v-if="selectedPurpose" class="purpose-detail-config">
      <el-divider>
        <span class="divider-text">
          <el-icon><Setting /></el-icon>
          场景细化配置
        </span>
      </el-divider>
      
      <div class="config-grid">
        <!-- 场景细节 -->
        <div class="config-item">
          <label class="config-label">
            <el-icon><InfoFilled /></el-icon>
            场景细节
            <el-tooltip content="描述具体的监控场景，如：冷链货架、危险品仓位、高值物料等" placement="top">
              <el-icon class="help-icon"><QuestionFilled /></el-icon>
            </el-tooltip>
          </label>
          <el-input
            v-model="purposeDetail"
            :placeholder="getPurposeDetailPlaceholder()"
            maxlength="100"
            show-word-limit
            clearable
            @input="updatePurposeDetail"
          />
        </div>

        <!-- 重点指标 -->
        <div class="config-item">
          <label class="config-label">
            <el-icon><DataLine /></el-icon>
            重点指标
            <el-tooltip content="需要重点监控的业务指标，如：温度湿度、库存周转率、拣选效率等" placement="top">
              <el-icon class="help-icon"><QuestionFilled /></el-icon>
            </el-tooltip>
          </label>
          <el-input
            v-model="focusMetrics"
            :placeholder="getFocusMetricsPlaceholder()"
            maxlength="100"
            show-word-limit
            clearable
            @input="updateFocusMetrics"
          />
        </div>

        <!-- 补充需求 -->
        <div class="config-item config-item-full">
          <label class="config-label">
            <el-icon><Document /></el-icon>
            补充需求
            <el-tooltip content="其他特殊需求或定制化要求" placement="top">
              <el-icon class="help-icon"><QuestionFilled /></el-icon>
            </el-tooltip>
          </label>
          <el-input
            v-model="customRequirements"
            type="textarea"
            :placeholder="getCustomRequirementsPlaceholder()"
            :rows="2"
            maxlength="200"
            show-word-limit
            @input="updateCustomRequirements"
          />
        </div>
      </div>

      <!-- 场景示例提示 -->
      <div v-if="selectedPurpose !== 'custom'" class="example-tips">
        <el-alert type="info" :closable="false">
          <template #title>
            <span class="tips-title">
              <el-icon><Promotion /></el-icon>
              {{ getExampleTips() }}
            </span>
          </template>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { 
  Box, Location, Van, Grid, More, Setting, 
  InfoFilled, DataLine, Document, QuestionFilled, Promotion 
} from '@element-plus/icons-vue';

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
const purposeDetail = ref(props.wizardData.purposeDetail || '');
const focusMetrics = ref(props.wizardData.focusMetrics || '');
const customRequirements = ref(props.wizardData.customRequirements || '');

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
    purposeText: option.title,
    purposeOption: option,
    // 清空之前的配置
    purposeDetail: '',
    focusMetrics: '',
    customRequirements: ''
  };

  // 重置本地状态
  purposeDetail.value = '';
  focusMetrics.value = '';
  customRequirements.value = '';

  emit('update', updateData);
};

const updatePurposeDetail = () => {
  emit('update', {
    purposeDetail: purposeDetail.value
  });
};

const updateFocusMetrics = () => {
  emit('update', {
    focusMetrics: focusMetrics.value
  });
};

const updateCustomRequirements = () => {
  emit('update', {
    customRequirements: customRequirements.value
  });
};

// 获取占位符文本
const getPurposeDetailPlaceholder = () => {
  const placeholders: Record<string, string> = {
    shelf: '例如：冷链货架、危险品货架、高值物料货架',
    location: '例如：原料仓、成品仓、立体仓库、露天堆场',
    transport: '例如：AGV小车、叉车、输送线、机械手',
    mixed: '例如：整体仓库、特定区域、跨仓协同',
    custom: '请描述您的具体场景...'
  };
  return placeholders[selectedPurpose.value] || '请输入场景细节...';
};

const getFocusMetricsPlaceholder = () => {
  const placeholders: Record<string, string> = {
    shelf: '例如：占用率、周转率、拣选效率、货位准确率',
    location: '例如：利用率、库存准确率、呆滞料分析、ABC分类',
    transport: '例如：任务完成率、设备利用率、路径优化、故障率',
    mixed: '例如：整体OEE、订单完成率、库存周转、人效分析',
    custom: '请输入需要监控的关键指标...'
  };
  return placeholders[selectedPurpose.value] || '请输入重点监控指标...';
};

const getCustomRequirementsPlaceholder = () => {
  return '请输入其他特殊需求，如数据更新频率、特殊交互功能、品牌色彩等...';
};

const getExampleTips = () => {
  const tips: Record<string, string> = {
    shelf: '💡 提示：可以针对不同货架类型定制监控方案，如冷链需要温度监控，危险品需要安全预警',
    location: '💡 提示：不同仓库类型有不同的管理重点，立体仓注重设备状态，平库注重空间利用',
    transport: '💡 提示：可根据搬运设备类型优化监控重点，AGV关注路径规划，叉车关注作业效率',
    mixed: '💡 提示：综合看板可以整合多个子系统数据，建议明确主要监控维度'
  };
  return tips[selectedPurpose.value] || '';
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

.purpose-detail-config {
  margin-top: 40px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
}

.divider-text {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #606266;
  font-weight: 500;
}

.config-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.config-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.config-item-full {
  grid-column: span 2;
}

.config-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.help-icon {
  color: #909399;
  cursor: help;
  font-size: 14px;
}

.example-tips {
  margin-top: 20px;
}

.tips-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
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
