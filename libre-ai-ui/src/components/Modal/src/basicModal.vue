<template>
  <el-dialog v-model="isModal" v-bind="getBindValue" @close="onCloseModal">
    <template #default>
      <slot name="default" />
    </template>
    <template v-if="!$slots.footer" #footer>
      <div class="dialog-footer">
        <el-button v-if="showCloseBtn" @click="closeModal">{{
          subCloseText
        }}</el-button>
        <el-button
          v-if="showSubBtn"
          type="primary"
          :loading="subLoading"
          @click="handleSubmit"
        >
          {{ subBtuText }}
        </el-button>
      </div>
    </template>
    <template v-else #footer>
      <slot name="footer" />
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { getCurrentInstance, ref, unref, computed, useAttrs } from 'vue';
import { basicProps } from './props';
import { ModalProps, ModalMethods } from './type';

const attrs = useAttrs();
const props = defineProps({ ...basicProps });
const emit = defineEmits(['on-close', 'on-ok', 'register']);

const propsRef = ref<Partial<ModalProps> | null>(null);

const isModal = ref(false);
const subLoading = ref(false);

const getProps = computed(() => {
  return { ...props, ...(unref(propsRef) as any) };
});

const subBtuText = computed(() => {
  const { subBtuText } = (propsRef.value as any) || {};
  return subBtuText || props.subBtuText;
});

const subCloseText = computed(() => {
  const { subCloseText } = (propsRef.value as any) || {};
  return subCloseText || props.subCloseText;
});

const showCloseBtn = computed(() => {
  const { showCloseBtn } = (propsRef.value as any) || {};
  return showCloseBtn === undefined ? props.showCloseBtn : showCloseBtn;
});

const showSubBtn = computed(() => {
  const { showSubBtn } = (propsRef.value as any) || {};
  return showSubBtn === undefined ? props.showSubBtn : showSubBtn;
});

async function setProps(modalProps: Partial<ModalProps>): Promise<void> {
  propsRef.value = { ...(unref(propsRef) || {}), ...modalProps };
}

const getBindValue = computed(() => {
  return {
    ...attrs,
    ...unref(getProps),
    ...unref(propsRef)
  };
});

function setSubLoading(status: boolean) {
  subLoading.value = status;
}

function openModal() {
  isModal.value = true;
}

function closeModal() {
  isModal.value = false;
  subLoading.value = false;
  emit('on-close');
}

function onCloseModal() {
  isModal.value = false;
  emit('on-close');
}

function handleSubmit() {
  subLoading.value = true;
  emit('on-ok');
}

const modalMethods: ModalMethods = {
  setProps,
  openModal,
  closeModal,
  setSubLoading
};

const instance = getCurrentInstance();
if (instance) {
  emit('register', modalMethods);
}
</script>

<style lang="scss" scoped>
.dialog-footer {
  text-align: right;
}
</style>
