<template>
  <div :class="['basic-table', themeClass]">
    <div v-if="$slots.tableTitle" class="table-title mb-4">
      <slot name="tableTitle" />
    </div>
    <!-- 使用与embed-store完全一致的Tailwind类 -->
    <div
      class="table-container h-full bg-white dark:bg-gray-900 rounded-lg border border-gray-200 dark:border-gray-700 overflow-hidden"
    >
      <el-table
        :data="tableData"
        :loading="loading"
        :row-key="rowKey"
        class="data-table"
        size="default"
        style="width: 100%"
        height="100%"
        :header-cell-style="{
          backgroundColor: '#f8fafc',
          color: '#1e293b',
          fontWeight: '600',
          borderBottom: '2px solid #e2e8f0'
        }"
        :row-style="{ cursor: 'pointer' }"
      >
        <el-table-column
          v-for="column in allColumns"
          :key="column.key"
          :prop="column.key"
          :label="column.title"
          :width="column.width"
          :fixed="column.fixed"
          :align="column.align || 'left'"
          show-overflow-tooltip
        >
          <template #default="scope">
            <component :is="column.render(scope.row)" v-if="column.render" />
            <span v-else>{{ scope.row[column.key] }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { ElTable, ElTableColumn } from 'element-plus';

interface Column {
  key: string;
  title: string;
  width?: number | string;
  fixed?: string;
  align?: string;
  render?: (record: any) => any;
}

interface Props {
  columns: Column[];
  actionColumn?: Column;
  request: (params: any) => Promise<any>;
  rowKey?: string | ((row: any) => string);
  pagination?: boolean;
  singleLine?: boolean;
  theme?: 'default' | 'model-management';
}

const props = withDefaults(defineProps<Props>(), {
  pagination: true,
  singleLine: true,
  rowKey: 'id',
  theme: 'default'
});

const tableData = ref([]);
const loading = ref(false);

const allColumns = computed(() => {
  const cols = [...props.columns];
  if (props.actionColumn) {
    cols.push(props.actionColumn);
  }
  return cols;
});

const themeClass = computed(() => {
  return `basic-table--${props.theme}`;
});

const reload = async () => {
  loading.value = true;
  try {
    const result = await props.request({});

    // 检查是否返回了 HTML 内容（可能是错误页面）
    if (typeof result === 'string' && result.includes('<!DOCTYPE html>')) {
      console.error(
        'API returned HTML instead of JSON. This might be an authentication or routing issue.'
      );
      tableData.value = [];
      return;
    }

    // 处理不同的响应格式
    if (Array.isArray(result)) {
      tableData.value = result;
    } else if (result && typeof result === 'object') {
      if (Array.isArray(result.data)) {
        tableData.value = result.data;
      } else if (Array.isArray(result.records)) {
        tableData.value = result.records;
      } else if (Array.isArray(result.list)) {
        tableData.value = result.list;
      } else if (Array.isArray(result.result)) {
        tableData.value = result.result;
      } else {
        // 如果没有找到数组数据，返回空数组
        console.warn('Unexpected data format:', result);
        tableData.value = [];
      }
    } else {
      console.error('Invalid response type:', typeof result, result);
      tableData.value = [];
    }
  } catch (error) {
    console.error('Table request error:', error);
    tableData.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  reload();
});

defineExpose({
  reload
});
</script>

<style lang="scss" scoped>
/* 基础表格样式 */
.basic-table {
  .table-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  /* 表格容器悬浮效果 - Tailwind无法直接处理的动态样式 */
  .table-container {
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
  }
}

/* 支持外部 table-section 包装器 - 与embed-store保持一致 */
.table-section {
  .basic-table .table-container {
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
  }
}

/* 表格样式 - 与embed-store完全一致 */
.basic-table .data-table {
  :deep(.el-table) {
    font-size: 14px;
    background: transparent;

    .el-table__row {
      transition: all 0.2s ease;

      &:hover {
        background-color: #f8fafc !important;
        transform: translateX(2px);
      }
    }

    .el-table__header th {
      font-size: 13px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      padding: 16px 12px;
    }

    .el-table__body td {
      padding: 14px 12px;
      font-weight: 500;
    }

    /* 移除默认边框 */
    .el-table__inner-wrapper::before {
      display: none;
    }

    .el-table__border-left-patch {
      display: none;
    }
  }
}

/* 暗色主题下的表格内容样式 */
html.dark {
  .basic-table .data-table {
    :deep(.el-table) {
      .el-table__row:hover {
        background-color: #334155 !important;
      }

      .el-table__header-wrapper th {
        background-color: #0f172a !important;
        color: #e2e8f0 !important;
        border-bottom-color: #475569 !important;
      }

      .el-table__body td {
        color: #e2e8f0;
      }
    }
  }
}

/* 响应式设计 - 简化版本 */
@media (max-width: 990px) {
  .basic-table .data-table :deep(.el-table) {
    font-size: 13px;

    .el-table__header th {
      font-size: 12px;
      padding: 12px 8px;
    }

    .el-table__body td {
      padding: 12px 8px;
    }
  }
}

@media (max-width: 760px) {
  .basic-table .data-table :deep(.el-table) {
    font-size: 12px;

    .el-table__header th {
      font-size: 11px;
      padding: 10px 6px;
    }

    .el-table__body td {
      padding: 10px 6px;
    }

    .el-table__row:hover {
      transform: none; /* 移动端禁用平移效果 */
    }
  }
}

/* 通用交互效果 */
:deep(.el-button) {
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
  }

  &.is-circle:hover {
    transform: scale(1.1);
  }
}

:deep(.el-tag) {
  font-weight: 500;
  border: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
</style>
