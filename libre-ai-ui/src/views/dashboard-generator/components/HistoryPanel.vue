<template>
  <div class="history-panel">
    <div v-if="store.history.length === 0" class="empty-state">
      <el-empty description="暂无历史记录">
        <el-button type="primary" @click="createSampleHistory">
          创建示例记录
        </el-button>
      </el-empty>
    </div>

    <div v-else class="history-list">
      <div
        v-for="(item, index) in store.history"
        :key="index"
        class="history-item"
        @click="loadConfig(item)"
      >
        <div class="history-header">
          <h4 class="history-title">{{ item.purposeText || '未命名配置' }}</h4>
          <div class="history-time">
            {{ formatTime(item.timestamp) }}
          </div>
        </div>

        <div class="history-details">
          <div class="detail-row">
            <span class="detail-label">布局：</span>
            <span class="detail-value">{{ item.layoutText || '未设置' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">主题：</span>
            <span class="detail-value">{{
              getThemeName(item.theme) || '未设置'
            }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">组件：</span>
            <span class="detail-value"
              >{{ item.components?.length || 0 }} 个</span
            >
          </div>
        </div>

        <div class="history-actions">
          <el-button type="primary" size="small" @click.stop="loadConfig(item)">
            加载配置
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click.stop="deleteHistory(index)"
          >
            删除
          </el-button>
        </div>
      </div>
    </div>

    <div class="history-footer">
      <el-button
        type="warning"
        size="small"
        :disabled="store.history.length === 0"
        @click="clearAllHistory"
      >
        清空历史记录
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import { useDashboardStore } from '../composables/useDashboardStore';
import { useTheme } from '../composables/useTheme';

// 使用状态管理
const store = useDashboardStore();
const { getThemeName } = useTheme();

// Emits
const emit = defineEmits<{
  'load-config': [config: any];
}>();

// 方法
const formatTime = (timestamp: number): string => {
  if (!timestamp) return '未知时间';

  const date = new Date(timestamp);
  const now = new Date();
  const diff = now.getTime() - date.getTime();

  // 小于1小时显示分钟
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000));
    return `${minutes}分钟前`;
  }

  // 小于24小时显示小时
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000));
    return `${hours}小时前`;
  }

  // 显示日期
  return date.toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const loadConfig = (config: any) => {
  emit('load-config', config);
};

const deleteHistory = async (index: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条历史记录吗？', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    });

    store.history.splice(index, 1);
    ElMessage.success('历史记录已删除');
  } catch {
    // 用户取消删除
  }
};

const clearAllHistory = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有历史记录吗？此操作不可恢复。',
      '确认清空',
      {
        confirmButtonText: '清空',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    store.history.length = 0;
    ElMessage.success('历史记录已清空');
  } catch {
    // 用户取消清空
  }
};

const createSampleHistory = () => {
  const sampleConfigs = [
    {
      purpose: 'analytics',
      purposeText: '数据分析看板',
      layout: 'grid',
      layoutText: '网格布局',
      theme: 'modern-blue',
      themeColors: {},
      components: ['柱状图', '折线图', 'KPI卡片'],
      componentIds: ['bar-chart', 'line-chart', 'kpi-card'],
      additionalDetails: '',
      timestamp: Date.now() - 2 * 60 * 60 * 1000 // 2小时前
    },
    {
      purpose: 'project',
      purposeText: '项目管理看板',
      layout: 'sidebar',
      layoutText: '侧边栏布局',
      theme: 'green-nature',
      themeColors: {},
      components: ['数据表格', '进度条'],
      componentIds: ['data-table', 'progress-bar'],
      additionalDetails: '',
      timestamp: Date.now() - 24 * 60 * 60 * 1000 // 1天前
    }
  ];

  store.history.push(...sampleConfigs);
  ElMessage.success('示例历史记录已创建');
};
</script>

<style scoped>
.history-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.empty-state {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
}

.history-list {
  flex: 1;
  padding: 0 4px;
  overflow-y: auto;
}

.history-item {
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  background: white;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.history-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgb(64 158 255 / 15%);
}

.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.history-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.history-time {
  font-size: 12px;
  color: #909399;
}

.history-details {
  margin-bottom: 12px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 14px;
}

.detail-label {
  color: #606266;
}

.detail-value {
  font-weight: 500;
  color: #303133;
}

.history-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.history-footer {
  padding: 16px 0;
  text-align: center;
  border-top: 1px solid #ebeef5;
}

/* 滚动条样式 */
.history-list::-webkit-scrollbar {
  width: 6px;
}

.history-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.history-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.history-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
