<script lang="ts" setup>
import { computed, nextTick, ref } from 'vue';
import { add, getById, update } from '@/api/aigc/embed-store';
import {
  ElMessage,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElSwitch,
  ElButton,
  ElAlert,
  ElDivider,
  ElTooltip,
  ElTag
} from 'element-plus';
import { Icon } from '@iconify/vue';
import {
  InfoFilled,
  CircleCheck,
  Warning,
  Key,
  Setting
} from '@element-plus/icons-vue';
// 提供商配置常量
enum ProviderEnum {
  Redis = 'redis',
  PgVector = 'pgvector',
  Milvus = 'milvus'
}

const ProviderConst = [
  { label: 'Redis', value: ProviderEnum.Redis },
  { label: 'PgVector', value: ProviderEnum.PgVector },
  { label: 'Milvus', value: ProviderEnum.Milvus }
];

function getProviderLabel(value: any) {
  const arr = ProviderConst.filter(i => i.value === value);
  if (arr === undefined || arr.length === 0) {
    return value;
  }
  return arr[0].label;
}

function getSchemas(provider: string) {
  const baseSchemas = [
    {
      field: 'name',
      label: '数据库别名',
      component: 'Input',
      required: true,
      componentProps: {
        placeholder: '请输入数据库别名'
      }
    },
    {
      field: 'provider',
      component: 'Input',
      label: '供应商',
      componentProps: {
        disabled: true,
        placeholder: '请选择供应商'
      }
    },
    {
      field: 'host',
      label: '数据库地址',
      component: 'Input',
      required: true,
      componentProps: {
        placeholder: '请输入数据库地址'
      }
    },
    {
      field: 'port',
      label: '数据库端口',
      component: 'InputNumber',
      required: true,
      componentProps: {
        placeholder: '请输入数据库端口',
        min: 1,
        max: 65535
      }
    }
  ];
  const schemas = JSON.parse(JSON.stringify(baseSchemas));
  const authArr: any[] = [
    {
      field: 'username',
      label: '数据库用户名',
      component: 'Input',
      required: true,
      componentProps: {
        placeholder: '请输入数据库用户名'
      }
    },
    {
      field: 'password',
      label: '数据库密码',
      component: 'Input',
      required: true,
      componentProps: {
        type: 'password',
        placeholder: '请输入数据库密码'
      }
    }
  ];
  const dimension: any = {
    field: 'dimension',
    label: '向量维度',
    component: 'InputNumber',
    required: true,
    componentProps: {
      placeholder: '请输入向量维度',
      min: 128,
      max: 4096
    }
  };

  if (provider === ProviderEnum.Redis) {
    authArr.forEach(i => (i.required = false));
    const arr: any = [
      {
        field: 'databaseName',
        label: 'Redis库索引名',
        component: 'Input',
        required: true,
        componentProps: {
          placeholder: '请输入Redis库索引名'
        }
      }
    ];
    schemas.push(...arr, ...authArr, dimension);
  }

  if (provider === ProviderEnum.PgVector) {
    const arr: any = [
      {
        field: 'databaseName',
        label: '数据库名',
        component: 'Input',
        required: true,
        componentProps: {
          placeholder: '请输入数据库名'
        }
      },
      {
        field: 'tableName',
        label: '表名称',
        component: 'Input',
        required: true,
        componentProps: {
          placeholder: '请输入表名称'
        }
      }
    ];
    schemas.push(...arr, ...authArr, dimension);
  }
  if (provider === ProviderEnum.Milvus) {
    const arr: any = [
      {
        field: 'databaseName',
        label: '数据库名',
        component: 'Input',
        required: true,
        componentProps: {
          placeholder: '请输入数据库名'
        }
      },
      {
        field: 'tableName',
        label: '表名称',
        component: 'Input',
        required: true,
        componentProps: {
          placeholder: '请输入表名称'
        }
      }
    ];
    schemas.push(...arr, dimension);
  }
  return schemas;
}

const props = defineProps<{
  provider: string;
}>();
const emit = defineEmits(['reload']);
const visible = ref(false);
const loading = ref(false);
const formRef = ref();
const formData = ref<any>({});
const formRules = ref({});

const schemas = computed(() => {
  return getSchemas(props.provider);
});

const title = computed(() => {
  const action = formData.value.id ? '编辑' : '新增';
  return `${action}${getProviderLabel(props.provider)}配置`;
});

const dialogWidth = computed(() => {
  // 根据屏幕大小调整弹窗宽度
  if (window.innerWidth < 768) return '95%';
  if (window.innerWidth < 1024) return '80%';
  return '600px';
});

const getProviderIcon = (provider: string) => {
  const iconMap: Record<string, string> = {
    redis: 'logos:redis',
    chroma: 'simple-icons:chromadb',
    pinecone: 'simple-icons:pinecone',
    weaviate: 'simple-icons:weaviate',
    qdrant: 'simple-icons:qdrant',
    milvus: 'simple-icons:milvus',
    pgvector: 'logos:postgresql'
  };
  return iconMap[provider] || 'ep:database';
};

const getFieldIcon = (fieldName: string) => {
  if (
    fieldName.toLowerCase().includes('key') ||
    fieldName.toLowerCase().includes('password')
  ) {
    return 'ep:key';
  }
  if (
    fieldName.toLowerCase().includes('url') ||
    fieldName.toLowerCase().includes('host')
  ) {
    return 'ep:link';
  }
  if (fieldName.toLowerCase().includes('port')) {
    return 'ep:connection';
  }
  return 'ep:setting';
};

async function show(id?: string) {
  visible.value = true;
  await nextTick();
  if (id) {
    try {
      const response = await getById(id);
      console.log('获取编辑数据:', response);
      // 根据后端返回的数据结构：{code, message, result: {...}}
      const data = response.result || {};
      formData.value = {
        ...data,
        // 确保 provider 字段正确设置，用于表单显示
        provider: data.provider || props.provider
      };
      console.log('表单数据回显:', formData.value);
    } catch (error) {
      ElMessage.error('获取数据失败');
      console.error('获取数据失败:', error);
      formData.value = { provider: props.provider };
    }
  } else {
    formData.value = {
      isPerms: true,
      provider: props.provider,
      dimension: 1536
    };
  }
}

async function handleSubmit() {
  try {
    await formRef.value.validate();
    loading.value = true;
    if (!formData.value.id) {
      await add(formData.value);
      ElMessage.success('新增成功');
    } else {
      await update(formData.value);
      ElMessage.success('修改成功');
    }
    visible.value = false;
    emit('reload');
  } catch (error) {
    ElMessage.error('请完善表单');
  } finally {
    loading.value = false;
  }
}

function handleClose() {
  visible.value = false;
  // 重置表单数据
  formData.value = {};
  // 清空表单验证
  if (formRef.value) {
    formRef.value.resetFields();
  }
}

defineExpose({ show });
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="dialogWidth"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :lock-scroll="true"
    :append-to-body="true"
    :modal-append-to-body="false"
    destroy-on-close
    top="5vh"
    class="embed-store-dialog fixed-dialog"
    @close="handleClose"
  >
    <!-- 弹窗头部自定义 -->
    <template #header>
      <div class="dialog-header">
        <div class="header-content">
          <Icon
            :icon="getProviderIcon(provider)"
            class="provider-icon"
            :size="24"
          />
          <span class="dialog-title">{{ title }}</span>
        </div>
        <el-tag v-if="formData.id" type="info" size="small">
          ID: {{ formData.id }}
        </el-tag>
      </div>
    </template>

    <!-- 提示信息 -->
    <div class="alert-container">
      <el-alert type="warning" :closable="false" class="config-alert">
        <template #title>
          <div class="alert-title">
            <el-icon :size="18"><Warning /></el-icon>
            <span>重要提示</span>
          </div>
        </template>
        <div class="alert-content">
          <ul class="alert-list">
            <li>向量维度（Dimension）参数需要与向量库匹配</li>
            <li>错误修改可能影响已有的向量数据</li>
            <li>请确保所有连接参数正确配置</li>
          </ul>
        </div>
      </el-alert>
    </div>

    <!-- 表单内容 -->
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="formData"
        label-width="140px"
        :rules="formRules"
        label-position="left"
        class="config-form"
      >
        <!-- 基础信息分组 -->
        <div class="form-section">
          <div class="section-header">
            <el-icon><InfoFilled /></el-icon>
            <span>基础信息</span>
          </div>
          <el-divider />

          <template
            v-for="schema in schemas.filter((s: any) =>
              ['name', 'remark'].includes(s.field)
            )"
            :key="schema.field"
          >
            <el-form-item
              :label="schema.label"
              :prop="schema.field"
              :required="schema.required"
              class="form-item-enhanced"
            >
              <template #label>
                <div class="label-container">
                  <Icon :icon="getFieldIcon(schema.field)" />
                  <span>{{ schema.label }}</span>
                  <el-tooltip
                    v-if="schema.field === 'name'"
                    content="为数据库配置起一个易于识别的名称"
                    placement="top"
                  >
                    <el-icon class="help-icon"><InfoFilled /></el-icon>
                  </el-tooltip>
                </div>
              </template>
              <el-input
                v-model="formData[schema.field]"
                :placeholder="
                  schema.componentProps?.placeholder || `请输入${schema.label}`
                "
                :disabled="schema.componentProps?.disabled"
                clearable
              />
            </el-form-item>
          </template>
        </div>

        <!-- 连接配置分组 -->
        <div class="form-section">
          <div class="section-header">
            <el-icon><Setting /></el-icon>
            <span>连接配置</span>
          </div>
          <el-divider />

          <template
            v-for="schema in schemas.filter(
              (s: any) => !['name', 'remark', 'isPerms'].includes(s.field)
            )"
            :key="schema.field"
          >
            <el-form-item
              :label="schema.label"
              :prop="schema.field"
              :required="schema.required"
              class="form-item-enhanced"
            >
              <template #label>
                <div class="label-container">
                  <el-icon><Icon :icon="getFieldIcon(schema.field)" /></el-icon>
                  <span>{{ schema.label }}</span>
                  <el-tooltip
                    v-if="schema.field === 'dimension'"
                    content="向量维度必须与模型和存储库一致"
                    placement="top"
                  >
                    <el-icon class="help-icon"><Warning /></el-icon>
                  </el-tooltip>
                </div>
              </template>

              <el-input
                v-if="schema.component === 'Input'"
                v-model="formData[schema.field]"
                :placeholder="
                  schema.componentProps?.placeholder || `请输入${schema.label}`
                "
                :disabled="schema.componentProps?.disabled"
                :type="
                  schema.field.toLowerCase().includes('password')
                    ? 'password'
                    : 'text'
                "
                :show-password="schema.field.toLowerCase().includes('password')"
                clearable
              />
              <el-input-number
                v-else-if="schema.component === 'InputNumber'"
                v-model="formData[schema.field]"
                :min="schema.componentProps?.min"
                :max="schema.componentProps?.max"
                :disabled="schema.componentProps?.disabled"
                :precision="0"
                :controls-position="'right'"
                style="width: 100%"
              />
            </el-form-item>
          </template>
        </div>

        <!-- 权限设置 -->
        <div class="form-section">
          <div class="section-header">
            <el-icon><CircleCheck /></el-icon>
            <span>权限设置</span>
          </div>
          <el-divider />

          <el-form-item
            label="启用状态"
            prop="isPerms"
            class="form-item-enhanced"
          >
            <template #label>
              <div class="label-container">
                <el-icon><CircleCheck /></el-icon>
                <span>启用状态</span>
              </div>
            </template>
            <el-switch
              v-model="formData.isPerms"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              :active-icon="CircleCheck"
              style="

                --el-switch-on-color: #10b981;
                --el-switch-off-color: #ef4444;
              "
            />
          </el-form-item>
        </div>
      </el-form>
    </div>

    <!-- 底部操作按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">
          <el-icon class="mr-1"><Icon icon="ep:close" /></el-icon>
          取消
        </el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          <el-icon v-if="!loading" class="mr-1">
            <Icon icon="ep:check" />
          </el-icon>
          {{ loading ? '保存中...' : '保存配置' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>


// 响应式设计
@media (width <= 768px) {
  .dialog-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .form-container {
    padding: 16px;
  }

  .form-section {
    .section-header {
      font-size: 14px;
    }
  }

  :deep(.el-form) {
    .el-form-item__label {
      width: 100px !important;
    }
  }
}

:deep(.fixed-dialog) {
  .el-dialog {
    display: flex;
    flex-direction: column;
    max-height: 90vh;
    margin: 5vh auto !important;
    overflow: hidden;
  }

  .el-dialog__header {
    flex-shrink: 0;
    padding: 0;
    margin: 0;
  }

  .el-dialog__body {
    display: flex;
    flex: 1;
    flex-direction: column;
    min-height: 0;
    padding: 0;
    overflow: hidden;
  }

  .el-dialog__footer {
    flex-shrink: 0;
    padding: 16px 24px;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
  }
}

// 头部样式
.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  color: #1f2937;
  border-bottom: 1px solid #e5e7eb;

  .header-content {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .provider-icon {
    color: #6366f1;
  }

  .dialog-title {
    font-size: 18px;
    font-weight: 600;
  }
}

// 提示区域
.alert-container {
  padding: 16px 24px 0;

  .config-alert {
    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
    border: 1px solid #fbbf24;
    border-radius: 8px;

    .alert-title {
      display: flex;
      gap: 8px;
      align-items: center;
      font-weight: 600;
      color: #92400e;
    }

    .alert-content {
      margin-top: 8px;
      font-size: 13px;
      color: #78350f;
    }

    .alert-list {
      padding-left: 20px;
      margin: 0;
      line-height: 1.8;

      li {
        margin-bottom: 4px;
      }
    }
  }
}

// 表单容器
.form-container {
  flex: 1;
  min-height: 0;
  padding: 24px;
  overflow: hidden auto;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 3px;

    &:hover {
      background: #94a3b8;
    }
  }
}

// 表单分组
.form-section {
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;
  }

  .section-header {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 12px;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
  }

  :deep(.el-divider) {
    margin: 12px 0 20px;
  }
}

// 表单项增强
.form-item-enhanced {
  :deep(.el-form-item__label) {
    height: auto;
    line-height: 32px;
  }

  .label-container {
    display: flex;
    gap: 6px;
    align-items: center;
    font-size: 14px;

    .help-icon {
      font-size: 14px;
      color: #94a3b8;
      cursor: help;

      &:hover {
        color: #6366f1;
      }
    }
  }

  :deep(.el-input) {
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #e2e8f0 inset;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 0 0 1px #cbd5e1 inset;
      }

      &.is-focus {
        box-shadow: 0 0 0 2px #6366f1 inset;
      }
    }
  }

  :deep(.el-input-number) {
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #e2e8f0 inset;
    }
  }
}

// 底部按钮
.dialog-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;

  :deep(.el-button) {
    min-width: 100px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgb(0 0 0 / 15%);
      transform: translateY(-2px);
    }
  }
}

// 暗色主题适配
html.dark {
  .dialog-header {
    color: #f3f4f6;
    border-bottom-color: #374151;

    .provider-icon {
      color: #818cf8;
    }
  }

  .alert-container {
    .config-alert {
      background: linear-gradient(135deg, #7c2d12 0%, #9a3412 100%);
      border-color: #dc2626;

      .alert-title {
        color: #fef3c7;
      }

      .alert-content {
        color: #fde68a;
      }
    }
  }

  .form-section {
    .section-header {
      color: #f1f5f9;
    }
  }

  .form-container {
    &::-webkit-scrollbar-track {
      background: #334155;
    }

    &::-webkit-scrollbar-thumb {
      background: #64748b;

      &:hover {
        background: #94a3b8;
      }
    }
  }

  :deep(.el-dialog__footer) {
    background: #1e293b;
    border-top-color: #334155;
  }
}// 弹窗样式
</style>
