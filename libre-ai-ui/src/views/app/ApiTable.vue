<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue';
import SvgIcon from '@/components/ReIcon/src/iconifyIconOffline';
import { createApi, del, list as getApiList } from '@/api/aigc/appApi';
import { ElMessage, ElMessageBox, ElButton } from 'element-plus';
import { useRouter } from 'vue-router';
import { copyToClip } from '@/utils/copy';
import { PureTableBar } from '@/components/RePureTableBar';
import { useColumns } from './hooks';
import { ElTable, ElTableColumn } from 'element-plus';
import { hideKey } from '@/api/models';

const emit = defineEmits(['reload']);
const props = defineProps({
  channel: {
    type: String,
    required: true
  }
});

const ms = ElMessage;
const router = useRouter();
const dataList = ref([]);
const loading = ref(true);

// 表格列配置
const { columns } = useColumns();

// 获取数据
async function fetchData() {
  try {
    loading.value = true;
    const res = await getApiList({
      appId: router.currentRoute.value.params.id,
      channel: props.channel
    });
    dataList.value = res.map(item => ({
      ...item,
      apiKeyDisplay: hideKey(item.apiKey)
    }));
  } finally {
    loading.value = false;
  }
}

// 新增秘钥
async function onSubmit() {
  await createApi(router.currentRoute.value.params.id, props.channel);
  ms.success('新增成功');
  await fetchData();
}

// 复制秘钥
async function onCopy(row: any) {
  await copyToClip(row.apiKey);
  ms.success('秘钥复制成功');
}

// 删除秘钥
function handleDelete(row: any) {
  ElMessageBox.confirm(
    '你确定重置Key吗？删除后原Key将立即失效是，请谨慎操作',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      await del(row.id);
      ms.success('删除成功');
      await fetchData();
    })
    .catch(() => {});
}

onMounted(() => {
  fetchData();
});
</script>

<template>
  <div class="main">
    <PureTableBar title="API秘钥管理" :columns="columns" @refresh="fetchData">
      <template #buttons>
        <ElButton size="small" type="primary" @click="onSubmit">
          <template #icon>
            <SvgIcon icon="ic:round-plus" />
          </template>
          创建秘钥
        </ElButton>
      </template>

      <template v-slot="{ size, dynamicColumns }">
        <ElTable
          v-loading="loading"
          :data="dataList"
          :size="size"
          style="width: 100%"
          :header-cell-style="{
            background: 'var(--el-fill-color-light)',
            color: 'var(--el-text-color-primary)'
          }"
        >
          <template v-for="column in dynamicColumns" :key="column.prop">
            <ElTableColumn
              v-if="column.slot !== 'operation'"
              :prop="column.prop"
              :label="column.label"
              :width="column.width"
              :min-width="column.minWidth"
              :align="column.align"
              :fixed="column.fixed"
              show-overflow-tooltip
            />
            <ElTableColumn
              v-else
              :label="column.label"
              :width="column.width"
              :min-width="column.minWidth"
              :align="column.align"
              :fixed="column.fixed"
            >
              <template #default="{ row }">
                <ElButton
                  class="reset-margin"
                  link
                  type="success"
                  size="small"
                  @click="onCopy(row)"
                >
                  <template #icon>
                    <SvgIcon icon="ep:copy-document" />
                  </template>
                  复制
                </ElButton>
                <ElButton
                  class="reset-margin"
                  link
                  type="danger"
                  size="small"
                  @click="handleDelete(row)"
                >
                  <template #icon>
                    <SvgIcon icon="ep:delete" />
                  </template>
                  删除
                </ElButton>
              </template>
            </ElTableColumn>
          </template>
        </ElTable>
      </template>
    </PureTableBar>
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-button) {
  margin-left: 0;
}

.main {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
