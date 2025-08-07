<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import {
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption
} from 'element-plus';
import { Icon } from '@iconify/vue';
import { del, page as getPage } from '@/api/aigc/conversation';
import { columns, searchSchemas } from './columns';
import InfoList from './components/InfoList.vue';

const actionRef = ref();
const infoRef = ref();
const tableData = ref([]);
const loading = ref(false);
const searchForm = ref({});

const loadDataTable = async () => {
  loading.value = true;
  try {
    const result = await getPage({ ...searchForm.value });
    tableData.value = result.records || [];
  } catch (error) {
    ElMessage.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

function reloadTable() {
  loadDataTable();
}

function handleShowInfo(row: any) {
  infoRef.value.show(row);
}

function handleDelete(record: any) {
  ElMessageBox.confirm(`您想删除 ${record.title}`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await del(record.id);
      ElMessage.success('删除成功');
      reloadTable();
    })
    .catch(() => {});
}

function handleReset() {
  searchForm.value = {};
  reloadTable();
}

function handleSearch() {
  reloadTable();
}

onMounted(() => {
  loadDataTable();
});
</script>

<template>
  <div class="mt-2">
    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline @submit.prevent="handleSearch">
      <template v-for="schema in searchSchemas" :key="schema.field">
        <el-form-item :label="schema.label">
          <el-input
            v-if="schema.component === 'NInput'"
            v-model="searchForm[schema.field]"
            :placeholder="schema.componentProps?.placeholder"
            clearable
          />
          <el-select
            v-else-if="schema.component === 'NSelect'"
            v-model="searchForm[schema.field]"
            :placeholder="schema.componentProps?.placeholder"
            clearable
          >
            <el-option
              v-for="option in schema.componentProps?.options"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
      </template>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      size="small"
      style="width: 100%"
    >
      <el-table-column
        v-for="column in columns.filter(col => col.key !== 'action')"
        :key="column.key"
        :prop="column.key"
        :label="column.title"
        :width="column.width"
        :align="column.align || 'left'"
      />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            link
            size="small"
            @click="handleShowInfo(row)"
          >
            <Icon icon="ep:view" class="mr-1" />
            查看
          </el-button>
          <el-button type="danger" link size="small" @click="handleDelete(row)">
            <Icon icon="ep:delete" class="mr-1" />
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <InfoList ref="infoRef" />
  </div>
</template>

<style lang="less" scoped></style>
