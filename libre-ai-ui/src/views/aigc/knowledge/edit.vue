<script lang="ts" setup>
import { nextTick, ref, reactive } from 'vue';
import { add, getById, update } from '@/api/aigc/knowledge';
import { list as getModelStores } from '@/api/aigc/embed-store';
import { list as getEmbedModels } from '@/api/aigc/model';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { basicModal, useModal } from '@/components/Modal';
import { isNullOrWhitespace } from '@/utils/is';
import { ModelTypeEnum } from '@/api/models';

const emit = defineEmits(['reload']);
const embedStoreList = ref([]);
const embedModelList = ref([]);
const formRef = ref<FormInstance>();

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

const [modalRegister, { openModal, closeModal }] = useModal({
  title: '新增/编辑知识库',
  closable: true,
  maskClosable: false,
  showCloseBtn: false,
  showSubBtn: false
});

async function show(id?: string) {
  try {
    // 先打开模态框
    openModal();

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
      const stores = await getModelStores({});
      if (stores != null && Array.isArray(stores)) {
        embedStoreList.value = stores.map((item: any) => {
          return {
            label: item.name,
            value: item.id
          };
        });
      } else if (stores?.data && Array.isArray(stores.data)) {
        embedStoreList.value = stores.data.map((item: any) => {
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
      const models = await getEmbedModels({ type: ModelTypeEnum.EMBEDDING });
      if (models != null && Array.isArray(models)) {
        embedModelList.value = models.map((item: any) => {
          return {
            label: item.name,
            value: item.id
          };
        });
      } else if (models?.data && Array.isArray(models.data)) {
        embedModelList.value = models.data.map((item: any) => {
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
        const data = await getById(id);
        Object.assign(formData, data);
      } catch (error) {
        console.error('加载知识库数据失败:', error);
        ElMessage.error('加载知识库数据失败');
        closeModal();
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
    closeModal();
  }
}

async function handleSubmit() {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    if (isNullOrWhitespace(formData.id)) {
      await add(formData);
      closeModal();
      emit('reload');
      ElMessage.success('新增成功');
    } else {
      await update(formData);
      closeModal();
      emit('reload');
      ElMessage.success('修改成功');
    }
  } catch (error) {
    ElMessage.error('请完善表单');
  }
}
defineExpose({ show });
</script>

<template>
  <basicModal style="width: 35%" @register="modalRegister">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      class="mt-5"
    >
      <el-form-item label="知识库名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入知识库名称" />
      </el-form-item>

      <el-form-item label="向量数据库" prop="embedStoreId">
        <el-select
          v-model="formData.embedStoreId"
          placeholder="请选择关联向量数据库"
          style="width: 100%"
        >
          <el-option
            v-for="item in embedStoreList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="向量模型" prop="embedModelId">
        <el-select
          v-model="formData.embedModelId"
          placeholder="请选择关联向量化模型"
          style="width: 100%"
        >
          <el-option
            v-for="item in embedModelList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="知识库描述" prop="des">
        <el-input
          v-model="formData.des"
          type="textarea"
          :rows="3"
          placeholder="请输入知识库描述"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </el-form-item>
    </el-form>
  </basicModal>
</template>

<style lang="less" scoped></style>
