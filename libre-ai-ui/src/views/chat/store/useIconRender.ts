import { h } from 'vue';
import { Icon } from '@iconify/vue';

export function useIconRender() {
  const iconRender = (props: { icon: string }) => {
    return () => h(Icon, { icon: props.icon });
  };

  return {
    iconRender
  };
}
