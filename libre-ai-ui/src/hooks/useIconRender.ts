import { h } from 'vue';
import { Icon } from '@iconify/vue';

export function useIconRender() {
  const iconRender = (icon: string) => {
    return () => h(Icon, { icon });
  };

  return {
    iconRender
  };
}
