import { defineStore } from 'pinia';
import { update } from '@/api/aigc/app';

export interface AppInfo {
  id?: string;
  name?: string;
  systemPrompt?: string;
  userPromptTemplate?: string;
  modelId?: string | null;
  knowledgeIds?: string[];
  [key: string]: any;
}

export interface AppState {
  activeMenu: string;
  info: AppInfo;
  modelId: string | null;
  model: any;
  knowledgeIds: any[];
  knowledges: any[];
}

export const useAppStore = defineStore('app-store', {
  state: (): AppState =>
    <AppState>{
      activeMenu: 'setting',
      info: {
        systemPrompt: '',
        userPromptTemplate: ''
      },
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
    },

    removeKnowledge(item: any) {
      this.knowledgeIds = this.knowledgeIds.filter(i => i !== item.id);
      this.knowledges = this.knowledges.filter(i => i.id !== item.id);
    },

    async updateInfo() {
      try {
        if (!this.info.id) {
          console.warn('应用信息未初始化，跳过更新');
          return;
        }

        // 只发送后端需要的字段
        const {
          model: _model,
          knowledges: _knowledges,
          ...baseInfo
        } = this.info;
        const updateData = {
          ...baseInfo,
          modelId: this.modelId,
          knowledgeIds: this.knowledgeIds,
          systemPrompt: this.info.systemPrompt,
          userPromptTemplate: this.info.userPromptTemplate
        };

        await update(updateData);
      } catch (error) {
        console.error('更新应用信息失败:', error);
      }
    }
  }
});
