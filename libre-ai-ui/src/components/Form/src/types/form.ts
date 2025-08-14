import type { ComponentType } from './index';
import type { ButtonProps, FormItemRule } from 'element-plus';
import type { Recordable } from '../../../../../types/utils';

export interface FormSchema {
  field: string;
  label: string;
  labelMessage?: string;
  defaultValue?: any;
  component?: ComponentType;
  componentProps?: Recordable;
  slot?: string;
  rules?: FormItemRule | FormItemRule[];
  colProps?: Recordable;
  isHidden?: boolean;
  suffix?: string;
  // 新增样式增强属性
  section?: string; // 分组名称
  icon?: string; // 字段图标
  helpTooltip?: string; // 帮助提示内容
}

export interface FormProps {
  model?: Recordable;
  labelWidth?: number | string;
  schemas?: FormSchema[];
  inline?: boolean;
  layout?: 'horizontal' | 'inline';
  size?: 'large' | 'default' | 'small';
  showActionButtonGroup?: boolean;
  showResetButton?: boolean;
  resetButtonOptions?: Partial<ButtonProps>;
  showSubmitButton?: boolean;
  submitButtonOptions?: Partial<ButtonProps>;
  submitButtonText?: string;
  resetButtonText?: string;
  formProps?: Recordable;
}

export interface FormActionType {
  submit: () => Promise<any>;
  setProps: (formProps: Partial<FormProps>) => void;
  setFieldsValue: (values: Recordable) => void;
  clearValidate: (name?: string | string[]) => void;
  getFieldsValue: () => Recordable;
  resetFields: () => void;
  validate: () => Promise<any>;
  validateField: (field: string | string[]) => Promise<any>;
}

export type RegisterFn = (formInstance: FormActionType) => void;

export type UseFormReturnType = [RegisterFn, FormActionType];
