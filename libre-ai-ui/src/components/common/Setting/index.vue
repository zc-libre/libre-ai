<script setup lang="ts">
import { ref, computed } from 'vue';
import {
  ElDialog,
  ElForm,
  ElFormItem,
  ElSwitch,
  ElButton,
  ElDivider
} from 'element-plus';
import { useI18n } from 'vue-i18n';
import { useAppStoreHook } from '@/store/modules/app';
import { useUserStoreHook } from '@/store/modules/user';

const props = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits<{
  'update:visible': [value: boolean];
}>();

const { t } = useI18n();

const appStore = useAppStoreHook();
const userStore = useUserStoreHook();

const formData = ref({
  theme: localStorage.getItem('app-theme') || 'light',
  language: localStorage.getItem('app-language') || 'zh-CN',
  sidebarCollapsed: !(appStore.sidebar?.opened ?? true)
});

const dialogVisible = computed({
  get: () => props.visible,
  set: val => emit('update:visible', val)
});

function handleSave() {
  // 保存设置到localStorage
  localStorage.setItem('app-theme', formData.value.theme);
  localStorage.setItem('app-language', formData.value.language);

  // 更新侧边栏状态
  if (formData.value.sidebarCollapsed !== !appStore.sidebar?.opened) {
    appStore.toggleSideBar();
  }

  // 应用主题
  if (formData.value.theme === 'dark') {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }

  dialogVisible.value = false;
}

function handleCancel() {
  dialogVisible.value = false;
}
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="t('setting.setting')"
    width="500px"
    :close-on-click-modal="false"
  >
    <el-form :model="formData" label-width="120px" label-position="left">
      <!-- 主题设置 -->
      <el-form-item :label="t('setting.theme')">
        <el-switch
          v-model="formData.theme"
          active-value="dark"
          inactive-value="light"
          :active-text="t('setting.dark')"
          :inactive-text="t('setting.light')"
        />
      </el-form-item>

      <el-divider />

      <!-- 语言设置 -->
      <el-form-item :label="t('setting.language')">
        <el-select v-model="formData.language" placeholder="Select Language">
          <el-option label="简体中文" value="zh-CN" />
          <el-option label="English" value="en-US" />
        </el-select>
      </el-form-item>

      <el-divider />

      <!-- 侧边栏设置 -->
      <el-form-item :label="t('setting.sidebar')">
        <el-switch
          v-model="formData.sidebarCollapsed"
          :active-text="t('setting.collapsed')"
          :inactive-text="t('setting.expanded')"
        />
      </el-form-item>

      <el-divider />

      <!-- 用户信息 -->
      <el-form-item :label="t('setting.userInfo')">
        <div class="user-info">
          <div class="user-info__item">
            <span class="user-info__label">{{ t('setting.username') }}:</span>
            <span class="user-info__value">{{
              userStore.username || '-'
            }}</span>
          </div>
          <div class="user-info__item">
            <span class="user-info__label">{{ t('setting.email') }}:</span>
            <span class="user-info__value">{{
              userStore.nickname || '-'
            }}</span>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSave">{{
          t('common.save')
        }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.user-info {
  width: 100%;

  &__item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  &__label {
    color: var(--el-text-color-regular);
    margin-right: 8px;
  }

  &__value {
    color: var(--el-text-color-primary);
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
