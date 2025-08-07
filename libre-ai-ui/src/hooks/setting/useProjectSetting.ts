import { computed } from 'vue';
import { useBreakpoint, sizeEnum } from '@/hooks/web/useBreakpoint';

export function useProjectSetting() {
  const { screenRef } = useBreakpoint();

  const isMobile = computed(() => {
    return screenRef.value === sizeEnum.XS || screenRef.value === sizeEnum.SM;
  });

  return {
    isMobile
  };
}
