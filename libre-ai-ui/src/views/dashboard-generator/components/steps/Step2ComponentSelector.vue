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
      <p class="section-subtitle">
        可以选择多个组件，我们将基于您的选择推荐合适的布局
      </p>

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

      <div class="component-configs">
        <div
          v-for="componentId in selectedComponents"
          :key="componentId"
          class="component-config-card"
        >
          <div class="config-header">
            <div class="config-title">
              <el-icon :size="18" style="margin-right: 8px">
                <Setting />
              </el-icon>
              <span>{{ getComponentName(componentId) }}</span>
            </div>
            <el-tag type="info" size="small">{{ componentId }}</el-tag>
          </div>

          <el-tabs
            v-model="activeConfigTab[componentId]"
            type="border-card"
            class="config-tabs"
          >
            <!-- 基础配置 -->
            <el-tab-pane label="基础配置" name="basic">
              <el-form label-width="100px" size="small" class="config-form">
                <el-form-item label="数据源">
                  <el-input
                    v-model="componentDataConfigs[componentId].dataSource"
                    placeholder="API端点或数据源描述"
                  >
                    <template #append>
                      <el-button
                        :icon="QuestionFilled"
                        @click="showDataSourceHelp"
                      />
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item label="刷新频率">
                  <el-input-number
                    v-model="componentDataConfigs[componentId].refreshInterval"
                    :min="0"
                    :max="300000"
                    :step="5000"
                    placeholder="毫秒"
                    class="refresh-input"
                  />
                  <span class="form-hint">毫秒（0表示不自动刷新）</span>
                </el-form-item>
                <el-form-item label="状态">
                  <el-switch
                    v-model="componentDataConfigs[componentId].enabled"
                    active-text="启用"
                    inactive-text="禁用"
                  />
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 数据配置 -->
            <el-tab-pane label="数据配置" name="data">
              <el-form label-width="100px" size="small" class="config-form">
                <!-- 图表类配置 -->
                <template v-if="isChartComponent(componentId)">
                  <div class="config-section">
                    <h4 class="section-header">图表数据映射</h4>
                    <template v-if="componentId !== 'pie-chart'">
                      <el-form-item label="X轴字段">
                        <el-input
                          v-model="componentDataConfigs[componentId].xField"
                          placeholder="如：date, month, category"
                        />
                      </el-form-item>
                      <el-form-item label="Y轴字段">
                        <el-input
                          v-model="componentDataConfigs[componentId].yField"
                          placeholder="如：value, amount, count"
                        />
                      </el-form-item>
                      <el-form-item
                        v-if="supportsMultiSeries(componentId)"
                        label="系列字段"
                      >
                        <el-input
                          v-model="
                            componentDataConfigs[componentId].seriesField
                          "
                          placeholder="如：type, category（可选，多系列）"
                        />
                      </el-form-item>
                    </template>
                    <template v-else>
                      <!-- 饼图配置 -->
                      <el-form-item label="名称字段">
                        <el-input
                          v-model="componentDataConfigs[componentId].nameField"
                          placeholder="如：category, region"
                        />
                      </el-form-item>
                      <el-form-item label="数值字段">
                        <el-input
                          v-model="componentDataConfigs[componentId].valueField"
                          placeholder="如：value, percentage"
                        />
                      </el-form-item>
                    </template>
                  </div>
                </template>

                <!-- KPI卡片配置 -->
                <template v-if="componentId === 'kpi-card'">
                  <div class="config-section">
                    <h4 class="section-header">KPI指标配置</h4>
                    <el-form-item label="指标名称">
                      <el-input
                        v-model="componentDataConfigs[componentId].title"
                        placeholder="如：今日订单数、库存总量"
                      />
                    </el-form-item>
                    <el-form-item label="数值字段">
                      <el-input
                        v-model="componentDataConfigs[componentId].valueField"
                        placeholder="如：total_orders, inventory_count"
                      />
                    </el-form-item>
                    <el-form-item label="单位">
                      <el-input
                        v-model="componentDataConfigs[componentId].unit"
                        placeholder="如：单、元、%、件"
                      />
                    </el-form-item>
                    <el-form-item label="对比配置">
                      <el-select
                        v-model="
                          componentDataConfigs[componentId].comparisonType
                        "
                        placeholder="选择对比类型"
                      >
                        <el-option label="无对比" value="none" />
                        <el-option label="环比" value="chain" />
                        <el-option label="同比" value="year" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="趋势显示">
                      <el-switch
                        v-model="componentDataConfigs[componentId].showTrend"
                        active-text="显示"
                        inactive-text="隐藏"
                      />
                    </el-form-item>
                  </div>
                </template>

                <!-- 表格配置 -->
                <template v-if="componentId === 'data-table'">
                  <div class="config-section">
                    <h4 class="section-header">表格列配置</h4>
                    <div class="table-columns">
                      <div
                        v-for="(column, index) in componentDataConfigs[
                          componentId
                        ].columns"
                        :key="index"
                        class="column-config"
                      >
                        <el-input v-model="column.field" placeholder="字段名" />
                        <el-input v-model="column.title" placeholder="列标题" />
                        <el-input-number
                          v-model="column.width"
                          :min="50"
                          :max="500"
                          placeholder="宽度"
                          controls-position="right"
                        />
                        <el-select v-model="column.align" placeholder="对齐">
                          <el-option label="左对齐" value="left" />
                          <el-option label="居中" value="center" />
                          <el-option label="右对齐" value="right" />
                        </el-select>
                        <div class="column-options">
                          <el-checkbox v-model="column.sortable" size="small"
                            >排序</el-checkbox
                          >
                          <el-button
                            type="danger"
                            size="small"
                            :icon="Delete"
                            circle
                            @click="removeTableColumn(componentId, index)"
                          />
                        </div>
                      </div>
                      <el-button
                        type="primary"
                        size="small"
                        :icon="Plus"
                        class="add-column-btn"
                        @click="addTableColumn(componentId)"
                      >
                        添加列
                      </el-button>
                    </div>

                    <el-divider />

                    <h4 class="section-header">表格功能</h4>
                    <el-form-item label="分页">
                      <el-switch
                        v-model="componentDataConfigs[componentId].pagination"
                        active-text="启用"
                        inactive-text="禁用"
                      />
                      <el-input-number
                        v-if="componentDataConfigs[componentId].pagination"
                        v-model="componentDataConfigs[componentId].pageSize"
                        :min="5"
                        :max="100"
                        placeholder="每页条数"
                        style="margin-left: 12px"
                        controls-position="right"
                      />
                    </el-form-item>
                    <el-form-item label="表格功能">
                      <el-checkbox-group
                        v-model="
                          componentDataConfigs[componentId].tableFeatures
                        "
                      >
                        <el-checkbox label="export">导出功能</el-checkbox>
                        <el-checkbox label="filter">筛选功能</el-checkbox>
                        <el-checkbox label="search">搜索功能</el-checkbox>
                        <el-checkbox label="stripe">斑马纹</el-checkbox>
                        <el-checkbox label="border">边框</el-checkbox>
                      </el-checkbox-group>
                    </el-form-item>
                  </div>
                </template>

                <!-- 示例数据 -->
                <div class="config-section">
                  <h4 class="section-header">示例数据</h4>
                  <el-form-item>
                    <div class="sample-data-section">
                      <div class="sample-data-actions">
                        <el-button
                          size="small"
                          type="primary"
                          @click="generateSampleData(componentId)"
                        >
                          自动生成
                        </el-button>
                      </div>
                      <el-input
                        v-model="componentDataConfigs[componentId].sampleData"
                        type="textarea"
                        :rows="4"
                        placeholder='输入JSON格式的示例数据，如：[{"date": "2024-01", "value": 100}]'
                        class="sample-data-input"
                      />
                      <div
                        v-if="getSampleDataError(componentId)"
                        class="error-hint"
                      >
                        {{ getSampleDataError(componentId) }}
                      </div>
                    </div>
                  </el-form-item>
                </div>
              </el-form>
            </el-tab-pane>

            <!-- 高级配置 -->
            <el-tab-pane label="高级配置" name="advanced">
              <el-form label-width="120px" size="small" class="config-form">
                <!-- 图表高级配置 -->
                <template v-if="isChartComponent(componentId)">
                  <div class="config-section">
                    <h4 class="section-header">图表显示</h4>
                    <el-form-item label="显示图例">
                      <el-switch
                        v-model="componentDataConfigs[componentId].showLegend"
                      />
                      <el-select
                        v-if="componentDataConfigs[componentId].showLegend"
                        v-model="
                          componentDataConfigs[componentId].legendPosition
                        "
                        placeholder="图例位置"
                        style="margin-left: 12px"
                      >
                        <el-option label="顶部" value="top" />
                        <el-option label="底部" value="bottom" />
                        <el-option label="左侧" value="left" />
                        <el-option label="右侧" value="right" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="显示工具提示">
                      <el-switch
                        v-model="componentDataConfigs[componentId].showTooltip"
                      />
                    </el-form-item>
                    <el-form-item label="显示数据标签">
                      <el-switch
                        v-model="
                          componentDataConfigs[componentId].showDataLabels
                        "
                      />
                    </el-form-item>
                  </div>
                </template>

                <!-- KPI高级配置 -->
                <template v-if="componentId === 'kpi-card'">
                  <div class="config-section">
                    <h4 class="section-header">阈值配置</h4>
                    <el-form-item label="警告阈值">
                      <el-input-number
                        v-model="
                          componentDataConfigs[componentId].warningThreshold
                        "
                        placeholder="警告值"
                      />
                    </el-form-item>
                    <el-form-item label="危险阈值">
                      <el-input-number
                        v-model="
                          componentDataConfigs[componentId].dangerThreshold
                        "
                        placeholder="危险值"
                      />
                    </el-form-item>
                  </div>
                </template>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
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
import { ElMessageBox } from 'element-plus';
import {
  Check,
  DataAnalysis,
  TrendCharts,
  PieChart,
  DataLine,
  Grid,
  List,
  Setting,
  Delete,
  Plus,
  QuestionFilled
} from '@element-plus/icons-vue';

// Props
import type { DashboardConfig as StoreDashboardConfig } from '../../composables/useDashboardStore';

interface Props {
  wizardData: StoreDashboardConfig;
}

const props = defineProps<Props>();

// Emits
const emit = defineEmits<{
  update: [data: any];
}>();

// 状态
const selectedComponents = ref(props.wizardData.componentIds || []);
const activeComponentTab = ref('charts');
const activeConfigTab = ref<Record<string, string>>({});

// 组件数据配置
const componentDataConfigs = reactive<Record<string, any>>(
  props.wizardData.componentDataConfigs || {}
);

// 初始化组件配置
const initComponentConfig = (componentId: string) => {
  if (!componentDataConfigs[componentId]) {
    componentDataConfigs[componentId] = {
      componentId,
      componentType: componentId,
      dataSource: '',
      refreshInterval: 5000,
      enabled: true,
      sampleData: ''
    };

    // 根据组件类型设置默认配置
    if (componentId.includes('chart')) {
      componentDataConfigs[componentId].xField = '';
      componentDataConfigs[componentId].yField = '';
      componentDataConfigs[componentId].showLegend = true;
      componentDataConfigs[componentId].legendPosition = 'bottom';
      componentDataConfigs[componentId].showTooltip = true;
      componentDataConfigs[componentId].showDataLabels = false;
      if (componentId !== 'pie-chart') {
        componentDataConfigs[componentId].seriesField = '';
      }
    } else if (componentId === 'pie-chart') {
      componentDataConfigs[componentId].nameField = '';
      componentDataConfigs[componentId].valueField = '';
      componentDataConfigs[componentId].showLegend = true;
      componentDataConfigs[componentId].legendPosition = 'right';
    } else if (componentId === 'kpi-card') {
      componentDataConfigs[componentId].title = '';
      componentDataConfigs[componentId].valueField = '';
      componentDataConfigs[componentId].unit = '';
      componentDataConfigs[componentId].comparisonType = 'none';
      componentDataConfigs[componentId].showTrend = true;
      componentDataConfigs[componentId].warningThreshold = 0;
      componentDataConfigs[componentId].dangerThreshold = 0;
    } else if (componentId === 'data-table') {
      componentDataConfigs[componentId].columns = [];
      componentDataConfigs[componentId].pagination = true;
      componentDataConfigs[componentId].pageSize = 10;
      componentDataConfigs[componentId].tableFeatures = ['stripe', 'border'];
    }

    // 设置默认激活的配置标签
    activeConfigTab.value[componentId] = 'basic';
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
    sortable: false,
    align: 'left',
    format: ''
  });
};

// 删除表格列
const removeTableColumn = (componentId: string, index: number) => {
  componentDataConfigs[componentId].columns?.splice(index, 1);
};

// 工具方法
const isChartComponent = (componentId: string) => {
  return componentId.includes('chart');
};

const supportsMultiSeries = (componentId: string) => {
  return ['line-chart', 'area-chart', 'bar-chart'].includes(componentId);
};

const showDataSourceHelp = () => {
  ElMessageBox.alert(
    '数据源可以是：\n1. API端点URL\n2. 数据库查询描述\n3. 文件路径或名称\n4. 其他数据来源说明',
    '数据源说明',
    { confirmButtonText: '确定' }
  );
};

const generateSampleData = async (componentId: string) => {
  const { sampleDataTemplates } = await import('../../constants/options');
  const template = sampleDataTemplates[componentId] || [];
  componentDataConfigs[componentId].sampleData = JSON.stringify(
    template,
    null,
    2
  );
};

const getSampleDataError = (componentId: string) => {
  try {
    const data = componentDataConfigs[componentId].sampleData;
    if (data) {
      JSON.parse(data);
    }
    return null;
  } catch (e) {
    return 'JSON格式错误';
  }
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


/* 响应式设计 */
@media (width <= 990px) {
  .component-card {
    flex: 0 0 calc(25% - 12px);
  }
}

@media (width <= 760px) {
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

.step2-component-selector {
  margin: 0 auto;
}

.step-header {
  margin-bottom: 40px;
  text-align: center;
}

.step-title {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.step-description {
  margin: 0;
  font-size: 16px;
  line-height: 1.6;
  color: #606266;
}

.section {
  margin-bottom: 40px;
}

.section-title {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.section-subtitle {
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.component-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: flex-start;
}

.component-card {
  position: relative;
  flex: 0 0 calc(20% - 13px);
  min-width: 140px;
  padding: 16px;
  text-align: center;
  cursor: pointer;
  background: white;
  border: 2px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.component-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgb(64 158 255 / 15%);
}

.component-card.selected {
  color: white;
  background: linear-gradient(135deg, #409eff, #6366f1);
  border-color: #409eff;
}

.component-icon {
  margin-bottom: 8px;
}

.component-name {
  margin-bottom: 4px;
  font-size: 14px;
  font-weight: 600;
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

/* 新增样式 - 组件配置卡片 */
.component-configs {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.component-config-card {
  overflow: hidden;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgb(0 0 0 / 6%);
}

.config-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

.config-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.config-tabs {
  background: transparent;
  border: none;
}

.config-tabs :deep(.el-tabs__header) {
  margin: 0;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.config-tabs :deep(.el-tabs__content) {
  padding: 20px;
}

.config-form .el-form-item {
  margin-bottom: 20px;
}

.form-hint {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}

.refresh-input {
  width: 150px;
}

/* 配置分组样式 */
.config-section {
  margin-bottom: 24px;
}

.section-header {
  padding-bottom: 8px;
  margin-bottom: 16px;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  border-bottom: 1px solid #f0f0f0;
}

/* 表格列配置 */
.table-columns {
  margin-bottom: 16px;
}

.column-config {
  display: grid;
  grid-template-columns: 1fr 1fr 120px 100px auto;
  gap: 12px;
  align-items: center;
  padding: 12px;
  margin-bottom: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.column-options {
  display: flex;
  gap: 8px;
  align-items: center;
}

.add-column-btn {
  align-self: flex-start;
}

/* 示例数据区域 */
.sample-data-section {
  width: 100%;
}

.sample-data-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.sample-data-input {
  width: 100%;
}

.error-hint {
  margin-top: 4px;
  font-size: 12px;
  color: #f56c6c;
}
</style>
