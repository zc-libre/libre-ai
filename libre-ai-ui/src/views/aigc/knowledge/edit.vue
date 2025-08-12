<script lang="ts" setup>
import { nextTick, ref, reactive } from 'vue';
import { add, getById, update } from '@/api/aigc/knowledge';
import { list as getModelStores } from '@/api/aigc/embed-store';
import { list as getEmbedModels } from '@/api/aigc/model';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { isNullOrWhitespace } from '@/utils/is';
import { ModelTypeEnum } from '@/api/models';
import { Icon } from '@iconify/vue';

const emit = defineEmits(['reload']);
const embedStoreList = ref([]);
const embedModelList = ref([]);
const formRef = ref<FormInstance>();
const dialogVisible = ref(false);
const submitLoading = ref(false);

const formData = reactive({
  id: '',
  name: '',
  embedStoreId: '',
  embedModelId: '',
  des: ''
});

const rules: FormRules = {
  name: [{ required: true, message: '请输入知识库名称', trigger: 'blur' }],
  embedStoreId: [
    { required: true, message: '请选择关联向量数据库', trigger: 'change' }
  ],
  embedModelId: [
    { required: true, message: '请选择关联向量模型', trigger: 'change' }
  ],
  des: [{ required: true, message: '请输入知识库描述', trigger: 'blur' }]
};

async function show(id?: string) {
  try {
    // 先打开对话框
    dialogVisible.value = true;

    // 重置表单数据
    Object.assign(formData, {
      id: '',
      name: '',
      embedStoreId: '',
      embedModelId: '',
      des: ''
    });

    // 加载向量数据库列表
    try {
      const response = await getModelStores({});
      if (response?.result && Array.isArray(response.result)) {
        embedStoreList.value = response.result.map((item: any) => {
          return {
            label: item.name,
            value: item.id
          };
        });
      } else {
        embedStoreList.value = [];
      }
    } catch (error) {
      console.error('加载向量数据库列表失败:', error);
      ElMessage.warning('加载向量数据库列表失败，请检查网络连接');
      embedStoreList.value = [];
    }

    // 加载向量模型列表
    try {
      const response = await getEmbedModels({ type: ModelTypeEnum.EMBEDDING });
      if (response?.result && Array.isArray(response.result)) {
        embedModelList.value = response.result.map((item: any) => {
          return {
            label: item.name,
            value: item.id
          };
        });
      } else {
        embedModelList.value = [];
      }
    } catch (error) {
      console.error('加载向量模型列表失败:', error);
      ElMessage.warning('加载向量模型列表失败，请检查网络连接');
      embedModelList.value = [];
    }

    await nextTick();

    // 如果是编辑模式，加载现有数据
    if (id) {
      try {
        const response = await getById(id);
        Object.assign(formData, response.result || response);
      } catch (error) {
        console.error('加载知识库数据失败:', error);
        ElMessage.error('加载知识库数据失败');
        dialogVisible.value = false;
        return;
      }
    }

    // 检查必要的数据是否加载成功
    if (embedStoreList.value.length === 0) {
      ElMessage.warning('未找到可用的向量数据库，请先配置向量数据库');
    }
    if (embedModelList.value.length === 0) {
      ElMessage.warning('未找到可用的向量模型，请先配置向量模型');
    }
  } catch (error) {
    console.error('打开知识库编辑窗口失败:', error);
    ElMessage.error('打开编辑窗口失败，请重试');
    dialogVisible.value = false;
  }
}

async function handleSubmit() {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    submitLoading.value = true;

    if (isNullOrWhitespace(formData.id)) {
      await add(formData);
      dialogVisible.value = false;
      emit('reload');
      ElMessage.success('知识库创建成功');
    } else {
      await update(formData);
      dialogVisible.value = false;
      emit('reload');
      ElMessage.success('知识库修改成功');
    }
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message);
    } else {
      ElMessage.error('请完善表单信息');
    }
  } finally {
    submitLoading.value = false;
  }
}

function handleCancel() {
  dialogVisible.value = false;
}
defineExpose({ show });
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="formData.id ? '编辑知识库' : '创建知识库'"
    width="600px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    class="knowledge-edit-dialog"
  >
    <div class="dialog-content">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        class="knowledge-form"
      >
        <!-- 基础信息区域 -->
        <div class="form-section">
          <div class="section-header">
            <Icon icon="tabler:info-circle" class="section-icon" />
            <span class="section-title">基础信息</span>
          </div>

          <el-form-item label="知识库名称" prop="name">
            <el-input
              v-model="formData.name"
              placeholder="请输入知识库名称"
              size="large"
              clearable
            >
              <template #prefix>
                <Icon icon="tabler:database" class="text-gray-400" />
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="知识库描述" prop="des">
            <el-input
              v-model="formData.des"
              type="textarea"
              :rows="4"
              placeholder="请输入知识库描述，帮助他人了解知识库的用途和内容"
              show-word-limit
              maxlength="200"
            />
          </el-form-item>
        </div>

        <!-- 配置信息区域 -->
        <div class="form-section">
          <div class="section-header">
            <Icon icon="tabler:settings" class="section-icon" />
            <span class="section-title">配置信息</span>
          </div>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="向量数据库" prop="embedStoreId">
                <el-select
                  v-model="formData.embedStoreId"
                  placeholder="请选择向量数据库"
                  size="large"
                  style="width: 100%"
                >
                  <el-option
                    v-for="item in embedStoreList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                    <div class="flex items-center justify-between">
                      <span>{{ item.label }}</span>
                      <Icon icon="tabler:database" class="text-gray-400" />
                    </div>
                  </el-option>
                  <template #empty>
                    <div class="text-center py-4 text-gray-400">
                      <Icon icon="tabler:database-off" class="text-2xl mb-2" />
                      <p>暂无可用的向量数据库</p>
                    </div>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <el-form-item label="向量模型" prop="embedModelId">
                <el-select
                  v-model="formData.embedModelId"
                  placeholder="请选择向量模型"
                  size="large"
                  style="width: 100%"
                >
                  <el-option
                    v-for="item in embedModelList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                    <div class="flex items-center justify-between">
                      <span>{{ item.label }}</span>
                      <Icon icon="tabler:brain" class="text-gray-400" />
                    </div>
                  </el-option>
                  <template #empty>
                    <div class="text-center py-4 text-gray-400">
                      <Icon icon="tabler:brain-off" class="text-2xl mb-2" />
                      <p>暂无可用的向量模型</p>
                    </div>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button size="large" @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          size="large"
          :loading="submitLoading"
          @click="handleSubmit"
        >
          {{ formData.id ? '保存修改' : '创建知识库' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.knowledge-edit-dialog {
  :deep(.el-dialog__header) {
    padding: 20px 24px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    margin-bottom: 0;
  }

  :deep(.el-dialog__body) {
    padding: 0;
  }

  :deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}

.dialog-content {
  padding: 24px;
  max-height: 60vh;
  overflow-y: auto;
}

.knowledge-form {
  .form-section {
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .section-icon {
    font-size: 20px;
    color: var(--el-color-primary);
  }

  .section-title {
    font-size: 16px;
    font-weight: 500;
    color: var(--el-text-color-primary);
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--el-text-color-regular);
    margin-bottom: 8px;
  }

  :deep(.el-select) {
    width: 100%;
  }

  :deep(.el-textarea__inner) {
    resize: none;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
