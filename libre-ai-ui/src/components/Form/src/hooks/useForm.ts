import type {
  FormActionType,
  FormProps,
  UseFormReturnType
} from '../types/form';
import type {
  DynamicProps,
  Nullable,
  Recordable
} from '../../../../../types/utils';

import { nextTick, onUnmounted, ref, unref, watch } from 'vue';
import { getDynamicProps } from '@/utils';

const isProdMode = () => import.meta.env.PROD;

type Props = Partial<DynamicProps<FormProps>>;

export function useForm(props?: Props): UseFormReturnType {
  // 兼容旧版本的 gridProps
  if (props && 'gridProps' in props) {
    console.warn(
      'gridProps is deprecated, please use colProps in schemas instead'
    );
  }
  const formRef = ref<Nullable<FormActionType>>(null);
  const loadedRef = ref<Nullable<boolean>>(false);

  async function getForm() {
    const form = unref(formRef);
    if (!form) {
      console.error(
        'The form instance has not been obtained, please make sure that the form has been rendered when performing the form operation!'
      );
    }
    await nextTick();
    return form as FormActionType;
  }

  function register(instance: FormActionType) {
    isProdMode() &&
      onUnmounted(() => {
        formRef.value = null;
        loadedRef.value = null;
      });
    if (unref(loadedRef) && isProdMode() && instance === unref(formRef)) return;

    formRef.value = instance;
    loadedRef.value = true;

    watch(
      () => props,
      () => {
        props && instance.setProps(getDynamicProps(props));
      },
      {
        immediate: true,
        deep: true
      }
    );
  }

  const methods: FormActionType = {
    setProps: (formProps: Partial<FormProps>) => {
      const form = unref(formRef);
      form?.setProps(formProps);
    },

    resetFields: () => {
      const form = unref(formRef);
      form?.resetFields();
    },

    clearValidate: (name?: string | string[]) => {
      const form = unref(formRef);
      form?.clearValidate(name);
    },

    getFieldsValue: () => {
      return unref(formRef)?.getFieldsValue() || {};
    },

    setFieldsValue: (values: Recordable) => {
      const form = unref(formRef);
      form?.setFieldsValue(values);
    },

    submit: async (): Promise<any> => {
      const form = await getForm();
      return form.submit();
    },

    validate: async (): Promise<any> => {
      const form = await getForm();
      return form.validate();
    },

    validateField: async (field: string | string[]): Promise<any> => {
      const form = await getForm();
      return form.validateField(field);
    }
  };

  return [register, methods];
}
