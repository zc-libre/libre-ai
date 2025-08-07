import { ref, unref, watch } from 'vue';
import type { ModalMethods, UseModalReturnType } from '../type';

export function useModal(props: any): UseModalReturnType {
  const modalRef = ref<Nullable<ModalMethods>>(null);

  const getInstance = () => {
    const instance = unref(modalRef.value);
    if (!instance) {
      console.error('useModal instance is undefined!');
    }
    return instance;
  };

  const register = (modalInstance: ModalMethods) => {
    modalRef.value = modalInstance;

    watch(
      () => props,
      () => {
        if (props && modalInstance) {
          modalInstance.setProps(props);
        }
      },
      {
        immediate: true,
        deep: true
      }
    );
  };

  const methods: ModalMethods = {
    setProps: (props): void => {
      getInstance()?.setProps(props);
    },
    openModal: () => {
      getInstance()?.openModal();
    },
    closeModal: () => {
      getInstance()?.closeModal();
    },
    setSubLoading: (status: boolean) => {
      getInstance()?.setSubLoading(status);
    }
  };

  return [register, methods];
}
