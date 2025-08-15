import { computed } from 'vue';
import { useBreakpoint, sizeEnum } from '@/hooks/web/useBreakpoint';

export function useBasicLayout() {
  const { screenRef } = useBreakpoint();

  const isMobile = computed(() => {
    return screenRef.value === sizeEnum.XS || screenRef.value === sizeEnum.SM;
  });

  return {
    isMobile
  };
}
