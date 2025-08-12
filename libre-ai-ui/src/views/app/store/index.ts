import { defineStore } from 'pinia';
import { update } from '@/api/aigc/app';

export interface AppState {
  activeMenu: string;
  info: any;
  modelId: string | null;
  model: any;
  knowledgeIds: any[];
  knowledges: any[];
}

export const useAppStore = defineStore('app-store', {
  state: (): AppState =>
    <AppState>{
      activeMenu: 'setting',
      info: {},
      modelId: '',
      model: null,
      knowledgeIds: [],
      knowledges: []
    },

  getters: {},

  actions: {
    setActiveMenu(active: string) {
      this.activeMenu = active;
    },
    addKnowledge(item: any) {
      this.knowledgeIds.push(item.id);
      this.knowledges.push(item);
      this.updateInfo();
    },

    removeKnowledge(item: any) {
      this.knowledgeIds = this.knowledgeIds.filter(i => i !== item.id);
      this.knowledges = this.knowledges.filter(i => i.id !== item.id);
      this.updateInfo();
    },

    async updateInfo() {
      try {
        if (!this.info.id) {
          console.warn('应用信息未初始化，跳过更新');
          return;
        }

        // 只发送后端需要的字段
        const { model, knowledges, ...baseInfo } = this.info;
        const updateData = {
          ...baseInfo,
          modelId: this.modelId,
          knowledgeIds: this.knowledgeIds
        };

        await update(updateData);
      } catch (error) {
        console.error('更新应用信息失败:', error);
      }
    }
  }
});
