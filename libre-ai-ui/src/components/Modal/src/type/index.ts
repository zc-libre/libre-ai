import type { DialogProps } from 'element-plus';

/**
 * @description: 弹窗对外暴露的方法
 */
export interface ModalMethods {
  setProps: (props: any) => void;
  openModal: () => void;
  closeModal: () => void;
  setSubLoading: (status: boolean) => void;
}

/**
 * 支持修改，DialogProps 参数
 */
export interface ModalProps extends Partial<DialogProps> {
  subBtuText?: string;
  subCloseText?: string;
  showIcon?: boolean;
  showCloseBtn?: boolean;
  showSubBtn?: boolean;
}

export type RegisterFn = (ModalInstance: ModalMethods) => void;

export type UseModalReturnType = [RegisterFn, ModalMethods];
