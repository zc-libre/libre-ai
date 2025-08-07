<template>
  <div class="basic-table">
    <div v-if="$slots.tableTitle" class="table-title mb-4">
      <slot name="tableTitle" />
    </div>
    <el-table
      :data="tableData"
      :loading="loading"
      :row-key="rowKey"
      border
      style="width: 100%"
    >
      <el-table-column
        v-for="column in allColumns"
        :key="column.key"
        :prop="column.key"
        :label="column.title"
        :width="column.width"
        :fixed="column.fixed"
        :align="column.align || 'left'"
      >
        <template #default="scope">
          <component :is="column.render(scope.row)" v-if="column.render" />
          <span v-else>{{ scope.row[column.key] }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from 'vue';
import { ElTable, ElTableColumn } from 'element-plus';

interface Column {
  key: string;
  title: string;
  width?: number;
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
}

const props = withDefaults(defineProps<Props>(), {
  pagination: true,
  singleLine: true,
  rowKey: 'id'
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
.basic-table {
  .table-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
</style>
