<template>
  <div class="step2-component-selector">
    <div class="step-header">
      <h2 class="step-title">
        <el-icon class="step-icon" :size="24" color="#6366F1">
          <DataAnalysis />
        </el-icon>
        选择看板组件
      </h2>
      <p class="step-description">
        根据您的看板用途，选择需要展示的数据组件。我们将基于您的选择推荐合适的主题和布局。
      </p>
    </div>

    <!-- 组件选择 -->
    <div class="section">
      <h3 class="section-title">选择您需要的组件</h3>
      <p class="section-subtitle">可以选择多个组件，我们将基于您的选择推荐合适的布局</p>

      <div class="component-categories">
        <el-tabs v-model="activeComponentTab" type="border-card">
          <el-tab-pane
            v-for="category in componentCategories"
            :key="category.id"
            :label="category.name"
            :name="category.id"
          >
            <div class="component-grid">
              <div
                v-for="component in category.components"
                :key="component.id"
                class="component-card"
                :class="{ selected: selectedComponents.includes(component.id) }"
                @click="toggleComponent(component)"
              >
                <div class="component-icon">
                  <el-icon
                    :size="24"
                    :color="
                      selectedComponents.includes(component.id)
                        ? '#fff'
                        : component.color
                    "
                  >
                    <component :is="component.icon" />
                  </el-icon>
                </div>
                <h4 class="component-name">{{ component.name }}</h4>
                <p class="component-description">{{ component.description }}</p>
                <div
                  v-if="selectedComponents.includes(component.id)"
                  class="selected-indicator"
                >
                  <el-icon :size="16" color="#fff">
                    <Check />
                  </el-icon>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 组件数据配置 -->
    <div v-if="selectedComponents.length > 0" class="section">
      <h3 class="section-title">配置组件数据结构</h3>
      <p class="section-subtitle">为每个组件设置数据字段和显示配置</p>
      
      <el-collapse accordion>
        <el-collapse-item v-for="componentId in selectedComponents" :key="componentId">
          <template #title>
            <span class="config-title">
              <el-icon :size="16" style="margin-right: 8px">
                <Setting />
              </el-icon>
              {{ getComponentName(componentId) }}
            </span>
          </template>
          
          <div class="config-content">
            <!-- 通用配置 -->
            <el-form label-width="100px" size="small">
              <el-form-item label="数据源">
                <el-input 
                  v-model="componentDataConfigs[componentId].dataSource" 
                  placeholder="API端点或数据源描述" 
                />
              </el-form-item>
              <el-form-item label="刷新频率">
                <el-input-number 
                  v-model="componentDataConfigs[componentId].refreshInterval" 
                  :min="0" 
                  :max="60000" 
                  :step="1000" 
                  placeholder="毫秒"
                />
                <span style="margin-left: 8px; color: #909399">毫秒（0表示不刷新）</span>
              </el-form-item>
              
              <!-- 图表类配置 -->
              <template v-if="componentId.includes('chart') && componentId !== 'pie-chart'">
                <el-form-item label="X轴字段">
                  <el-input 
                    v-model="componentDataConfigs[componentId].xField" 
                    placeholder="如：日期、月份、类别名称" 
                  />
                </el-form-item>
                <el-form-item label="Y轴字段">
                  <el-input 
                    v-model="componentDataConfigs[componentId].yField" 
                    placeholder="如：销量、金额、数量" 
                  />
                </el-form-item>
                <el-form-item v-if="componentId === 'line-chart' || componentId === 'area-chart'" label="系列字段">
                  <el-input 
                    v-model="componentDataConfigs[componentId].seriesField" 
                    placeholder="如：产品类别（可选，用于多系列）" 
                  />
                </el-form-item>
              </template>
              
              <!-- 饼图配置 -->
              <template v-if="componentId === 'pie-chart'">
                <el-form-item label="名称字段">
                  <el-input 
                    v-model="componentDataConfigs[componentId].nameField" 
                    placeholder="如：产品类别、地区" 
                  />
                </el-form-item>
                <el-form-item label="数值字段">
                  <el-input 
                    v-model="componentDataConfigs[componentId].valueField" 
                    placeholder="如：销售额、占比" 
                  />
                </el-form-item>
              </template>
              
              <!-- KPI卡片配置 -->
              <template v-if="componentId === 'kpi-card'">
                <el-form-item label="指标名称">
                  <el-input 
                    v-model="componentDataConfigs[componentId].title" 
                    placeholder="如：今日订单、库存总量" 
                  />
                </el-form-item>
                <el-form-item label="单位">
                  <el-input 
                    v-model="componentDataConfigs[componentId].unit" 
                    placeholder="如：单、元、%、件" 
                  />
                </el-form-item>
                <el-form-item label="对比类型">
                  <el-select v-model="componentDataConfigs[componentId].comparison">
                    <el-option label="无对比" value="none" />
                    <el-option label="环比" value="chain" />
                    <el-option label="同比" value="year" />
                  </el-select>
                </el-form-item>
                <el-form-item label="趋势">
                  <el-radio-group v-model="componentDataConfigs[componentId].trend">
                    <el-radio-button label="up">上升</el-radio-button>
                    <el-radio-button label="down">下降</el-radio-button>
                    <el-radio-button label="stable">平稳</el-radio-button>
                  </el-radio-group>
                </el-form-item>
              </template>
              
              <!-- 数据表格配置 -->
              <template v-if="componentId === 'data-table'">
                <el-form-item label="表格列">
                  <div style="width: 100%">
                    <div v-for="(column, index) in componentDataConfigs[componentId].columns" :key="index" 
                         style="display: flex; gap: 8px; margin-bottom: 8px">
                      <el-input v-model="column.field" placeholder="字段名" style="flex: 1" />
                      <el-input v-model="column.title" placeholder="列标题" style="flex: 1" />
                      <el-input-number v-model="column.width" :min="50" :max="500" placeholder="宽度" style="width: 100px" />
                      <el-checkbox v-model="column.sortable">排序</el-checkbox>
                      <el-button type="danger" size="small" :icon="Delete" circle 
                                 @click="removeTableColumn(componentId, index)" />
                    </div>
                    <el-button type="primary" size="small" @click="addTableColumn(componentId)">
                      添加列
                    </el-button>
                  </div>
                </el-form-item>
                <el-form-item label="分页">
                  <el-switch v-model="componentDataConfigs[componentId].pagination" />
                  <el-input-number 
                    v-if="componentDataConfigs[componentId].pagination"
                    v-model="componentDataConfigs[componentId].pageSize" 
                    :min="5" 
                    :max="100" 
                    style="margin-left: 12px"
                    placeholder="每页条数"
                  />
                </el-form-item>
              </template>
              
              <!-- 示例数据 -->
              <el-form-item label="示例数据">
                <el-input 
                  v-model="componentDataConfigs[componentId].sampleData" 
                  type="textarea" 
                  :rows="3" 
                  placeholder="输入JSON格式的示例数据，如：[{&quot;date&quot;: &quot;2024-01&quot;, &quot;value&quot;: 100}]" 
                />
              </el-form-item>
            </el-form>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 选择总结 -->
    <div v-if="selectedComponents.length > 0" class="selection-summary">
      <el-card>
        <template #header>
          <span class="summary-title">已选择的组件</span>
        </template>
        <div class="summary-content">
          <div class="selected-components">
            <el-tag
              v-for="componentId in selectedComponents"
              :key="componentId"
              type="primary"
              size="small"
              closable
              @close="removeComponent(componentId)"
            >
              {{ getComponentName(componentId) }}
              <span v-if="componentDataConfigs[componentId]?.title">
                - {{ componentDataConfigs[componentId].title }}
              </span>
            </el-tag>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import {
  Check,
  DataAnalysis,
  TrendCharts,
  PieChart,
  DataLine,
  Grid,
  List,
  Setting,
  Delete
} from '@element-plus/icons-vue';

import type { ComponentDataConfig } from '../../types';

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
const selectedComponents = ref(props.wizardData.componentIds || []);
const activeComponentTab = ref('charts');

// 组件数据配置
const componentDataConfigs = reactive<Record<string, ComponentDataConfig>>(
  props.wizardData.componentDataConfigs || {}
);

// 初始化组件配置
const initComponentConfig = (componentId: string) => {
  if (!componentDataConfigs[componentId]) {
    componentDataConfigs[componentId] = {
      componentId,
      componentType: componentId,
      refreshInterval: 5000,
      sampleData: ''
    };
    
    // 根据组件类型设置默认配置
    if (componentId.includes('chart')) {
      componentDataConfigs[componentId].xField = '';
      componentDataConfigs[componentId].yField = '';
    } else if (componentId === 'pie-chart') {
      componentDataConfigs[componentId].nameField = '';
      componentDataConfigs[componentId].valueField = '';
    } else if (componentId === 'kpi-card') {
      componentDataConfigs[componentId].title = '';
      componentDataConfigs[componentId].unit = '';
      componentDataConfigs[componentId].comparison = 'none';
    } else if (componentId === 'data-table') {
      componentDataConfigs[componentId].columns = [];
      componentDataConfigs[componentId].pagination = true;
      componentDataConfigs[componentId].pageSize = 10;
    }
  }
};

// 组件分类
const componentCategories = [
  {
    id: 'charts',
    name: '图表组件',
    components: [
      {
        id: 'bar-chart',
        name: '柱状图',
        description: '展示数据对比',
        icon: DataAnalysis,
        color: '#409EFF'
      },
      {
        id: 'line-chart',
        name: '折线图',
        description: '展示趋势变化',
        icon: TrendCharts,
        color: '#67C23A'
      },
      {
        id: 'pie-chart',
        name: '饼图',
        description: '展示数据占比',
        icon: PieChart,
        color: '#E6A23C'
      },
      {
        id: 'area-chart',
        name: '面积图',
        description: '展示数据分布',
        icon: DataLine,
        color: '#F56C6C'
      }
    ]
  },
  {
    id: 'data',
    name: '数据组件',
    components: [
      {
        id: 'data-table',
        name: '数据表格',
        description: '展示详细数据',
        icon: Grid,
        color: '#909399'
      },
      {
        id: 'data-list',
        name: '数据列表',
        description: '展示列表信息',
        icon: List,
        color: '#606266'
      },
      {
        id: 'kpi-card',
        name: 'KPI卡片',
        description: '展示关键指标',
        icon: DataAnalysis,
        color: '#409EFF'
      }
    ]
  }
];

// 方法
const toggleComponent = (component: any) => {
  const index = selectedComponents.value.indexOf(component.id);
  if (index > -1) {
    selectedComponents.value.splice(index, 1);
    // 移除对应的配置
    delete componentDataConfigs[component.id];
  } else {
    selectedComponents.value.push(component.id);
    // 初始化配置
    initComponentConfig(component.id);
  }
  updateData();
};

const removeComponent = (componentId: string) => {
  const index = selectedComponents.value.indexOf(componentId);
  if (index > -1) {
    selectedComponents.value.splice(index, 1);
    delete componentDataConfigs[componentId];
    updateData();
  }
};

const getComponentName = (componentId: string) => {
  for (const category of componentCategories) {
    const component = category.components.find(c => c.id === componentId);
    if (component) return component.name;
  }
  return componentId;
};

// 添加表格列
const addTableColumn = (componentId: string) => {
  if (!componentDataConfigs[componentId].columns) {
    componentDataConfigs[componentId].columns = [];
  }
  componentDataConfigs[componentId].columns!.push({
    field: '',
    title: '',
    width: 120,
    sortable: false
  });
};

// 删除表格列
const removeTableColumn = (componentId: string, index: number) => {
  componentDataConfigs[componentId].columns?.splice(index, 1);
};

const updateData = () => {
  const updateData = {
    componentIds: selectedComponents.value,
    components: selectedComponents.value.map(id => getComponentName(id)),
    componentDataConfigs: componentDataConfigs
  };

  emit('update', updateData);
};
</script>

<style scoped>
.step2-component-selector {
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

.section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.section-subtitle {
  font-size: 14px;
  color: #606266;
  margin-bottom: 20px;
}

.component-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: flex-start;
}

.component-card {
  flex: 0 0 calc(20% - 13px);
  min-width: 140px;
  border: 2px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  text-align: center;
  position: relative;
}

.component-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.component-card.selected {
  border-color: #409eff;
  background: linear-gradient(135deg, #409eff, #6366f1);
  color: white;
}

.component-icon {
  margin-bottom: 8px;
}

.component-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
}

.component-description {
  font-size: 12px;
  opacity: 0.8;
}

.selected-indicator {
  position: absolute;
  top: 8px;
  right: 8px;
}

.selection-summary {
  margin-top: 30px;
}

.summary-title {
  font-weight: 600;
  color: #303133;
}

.summary-content {
  padding: 8px 0;
}

.selected-components {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.config-title {
  display: flex;
  align-items: center;
  font-weight: 500;
  color: #303133;
}

.config-content {
  padding: 16px;
}

.config-content .el-form-item {
  margin-bottom: 16px;
}

/* 响应式设计 */
@media (max-width: 990px) {
  .component-card {
    flex: 0 0 calc(25% - 12px);
  }
}

@media (max-width: 760px) {
  .component-grid {
    justify-content: center;
  }

  .component-card {
    flex: 0 0 calc(50% - 8px);
    min-width: 120px;
  }

  .step-title {
    font-size: 20px;
  }
}
</style>