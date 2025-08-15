<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useChatStore } from '@/views/chat/store/useChatStore';
import { ElMessage, ElMessageBox, ElTooltip } from 'element-plus';
import { clean } from '@/api/aigc/chat';
import ModelSelect from '@/views/common/ModelSelect.vue';

defineProps<{
  title: string;
}>();

const emits = defineEmits(['reload']);
const chatStore = useChatStore();
const isLoading = ref(false);

// 计算消息数量用于显示
const messageCount = computed(() => {
  return chatStore.messages?.length || 0;
});

// 计算在线状态
const isOnline = ref(navigator.onLine);

// 监听网络状态
window.addEventListener('online', () => isOnline.value = true);
window.addEventListener('offline', () => isOnline.value = false);

async function handleClear() {
  if (!chatStore.conversationId) {
    ElMessage.warning('没有可清除的聊天记录');
    return;
  }
  
  if (messageCount.value === 0) {
    ElMessage.info('聊天记录为空，无需清除');
    return;
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要清除全部 ${messageCount.value} 条聊天记录吗？操作不可撤销。`,
      '清除聊天记录',
      {
        confirmButtonText: '清除',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'modern-message-box',
        distinguishCancelAndClose: true
      }
    );
    
    isLoading.value = true;
    await clean(chatStore.conversationId);
    emits('reload');
    ElMessage.success('聊天记录已成功清除');
  } catch (error: any) {
    if (error !== 'cancel' && error !== 'close') {
      console.error('Clear chat error:', error);
      ElMessage.error('清除失败，请稍后重试');
    }
  } finally {
    isLoading.value = false;
  }
}

// 刷新功能
function handleRefresh() {
  emits('reload');
  ElMessage.success('已刷新聊天记录');
}

// 切换全屏
function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen();
    ElMessage.info('已进入全屏模式');
  } else {
    document.exitFullscreen();
    ElMessage.info('已退出全屏模式');
  }
}
</script>

<template>
  <div class="modern-chat-header">
    <div class="header-container">
      <!-- 左侧标题区 -->
      <div class="header-left">
        <div class="brand-section">
          <div class="brand-icon">
            <div class="icon-gradient">🤖</div>
            <div class="status-indicator" :class="{ online: isOnline, offline: !isOnline }">
              <span class="status-dot"></span>
            </div>
          </div>
          <div class="brand-info">
            <h1 class="brand-title">{{ title }}</h1>
            <div class="brand-subtitle">
              <span class="message-count">{{ messageCount }} 条对话</span>
              <span class="separator">•</span>
              <span class="status-text">
                {{ isOnline ? '在线' : '离线' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧工具栏 -->
      <div class="header-right">
        <div class="header-actions">
          <!-- 模型选择器 -->
          <div class="model-selector">
            <ModelSelect
              :id="chatStore.modelId"
              class="modern-model-select"
            />
          </div>

          <!-- 工具按钮组 -->
          <div class="tool-buttons">
            <!-- 刷新按钮 -->
            <el-tooltip content="刷新聊天记录" placement="bottom">
              <button 
                class="action-btn refresh-btn" 
                @click="handleRefresh"
              >
                <span class="btn-icon">🔄</span>
              </button>
            </el-tooltip>
            
            <!-- 全屏按钮 -->
            <el-tooltip content="全屏模式" placement="bottom">
              <button 
                class="action-btn fullscreen-btn" 
                @click="toggleFullscreen"
              >
                <span class="btn-icon">📺</span>
              </button>
            </el-tooltip>
            
            <!-- 清空按钮 -->
            <el-tooltip content="清空所有聊天记录" placement="bottom">
              <button 
                class="action-btn clear-btn" 
                :class="{ loading: isLoading }"
                :disabled="isLoading || messageCount === 0"
                @click="handleClear"
              >
                <span v-if="!isLoading" class="btn-icon">🗑️</span>
                <span v-else class="loading-spinner"></span>
                <span class="btn-text">清空</span>
              </button>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.modern-chat-header {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0 24px;
  min-height: 72px;
  display: flex;
  align-items: center;
  position: relative;
  z-index: 10;
  
  // 添加细微的光效
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  }
  
  .header-container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 24px;
  }
  
  .header-left {
    display: flex;
    align-items: center;
    flex: 1;
    
    .brand-section {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .brand-icon {
        position: relative;
        
        .icon-gradient {
          width: 48px;
          height: 48px;
          border-radius: 16px;
          background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.05));
          border: 1px solid rgba(255, 255, 255, 0.15);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          transition: all 0.3s ease;
          backdrop-filter: blur(10px);
          
          &:hover {
            transform: scale(1.05) rotate(3deg);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
          }
        }
        
        .status-indicator {
          position: absolute;
          bottom: 2px;
          right: 2px;
          width: 14px;
          height: 14px;
          border-radius: 50%;
          border: 2px solid rgba(255, 255, 255, 0.8);
          backdrop-filter: blur(5px);
          
          .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            display: block;
            transition: all 0.3s ease;
          }
          
          &.online .status-dot {
            background: #4ade80;
            box-shadow: 0 0 8px rgba(74, 222, 128, 0.6);
            animation: statusPulse 2s infinite;
          }
          
          &.offline .status-dot {
            background: #f87171;
            box-shadow: 0 0 8px rgba(248, 113, 113, 0.6);
          }
        }
      }
      
      .brand-info {
        .brand-title {
          font-size: 20px;
          font-weight: 700;
          color: rgba(255, 255, 255, 0.95);
          margin: 0;
          text-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
          letter-spacing: -0.02em;
        }
        
        .brand-subtitle {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-top: 4px;
          font-size: 12px;
          color: rgba(255, 255, 255, 0.7);
          font-weight: 500;
          
          .message-count {
            background: rgba(255, 255, 255, 0.15);
            padding: 2px 8px;
            border-radius: 12px;
            backdrop-filter: blur(5px);
            border: 1px solid rgba(255, 255, 255, 0.1);
          }
          
          .separator {
            opacity: 0.5;
          }
          
          .status-text {
            font-weight: 600;
          }
        }
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
  }
  
  .header-actions {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .model-selector {
      :deep(.modern-model-select) {
        min-width: 200px;
        
        .el-select {
          .el-input__wrapper {
            background: rgba(255, 255, 255, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
            
            &:hover {
              background: rgba(255, 255, 255, 0.2);
              border-color: rgba(255, 255, 255, 0.3);
              transform: translateY(-1px);
              box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
            }
            
            &.is-focus {
              background: rgba(255, 255, 255, 0.25);
              border-color: rgba(255, 255, 255, 0.4);
              box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15), 0 0 0 3px rgba(255, 255, 255, 0.1);
            }
            
            .el-input__inner {
              color: rgba(255, 255, 255, 0.95);
              font-weight: 500;
              
              &::placeholder {
                color: rgba(255, 255, 255, 0.6);
              }
            }
            
            .el-input__suffix {
              .el-input__suffix-inner {
                .el-select__caret {
                  color: rgba(255, 255, 255, 0.8);
                }
              }
            }
          }
        }
      }
    }
    
    .tool-buttons {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .action-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 10px 16px;
        background: rgba(255, 255, 255, 0.15);
        border: 1px solid rgba(255, 255, 255, 0.2);
        border-radius: 12px;
        color: rgba(255, 255, 255, 0.9);
        cursor: pointer;
        font-size: 14px;
        font-weight: 500;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        backdrop-filter: blur(10px);
        min-height: 42px;
        
        &:hover:not(:disabled) {
          background: rgba(255, 255, 255, 0.25);
          border-color: rgba(255, 255, 255, 0.35);
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
          color: white;
        }
        
        &:active:not(:disabled) {
          transform: translateY(-1px);
        }
        
        &:disabled {
          opacity: 0.5;
          cursor: not-allowed;
          transform: none;
        }
        
        &.refresh-btn:hover:not(:disabled) {
          .btn-icon {
            transform: rotate(180deg);
          }
        }
        
        &.clear-btn {
          &:hover:not(:disabled) {
            background: rgba(248, 113, 113, 0.2);
            border-color: rgba(248, 113, 113, 0.3);
            
            .btn-icon {
              transform: scale(1.1);
            }
          }
          
          &.loading {
            pointer-events: none;
            
            .loading-spinner {
              width: 16px;
              height: 16px;
              border: 2px solid rgba(255, 255, 255, 0.3);
              border-top: 2px solid rgba(255, 255, 255, 0.8);
              border-radius: 50%;
              animation: spin 1s linear infinite;
            }
          }
        }
        
        .btn-icon {
          font-size: 16px;
          line-height: 1;
          transition: all 0.3s ease;
        }
        
        .btn-text {
          font-weight: 600;
          letter-spacing: 0.02em;
        }
        
        // 响应式隐藏文本
        @media (max-width: 768px) {
          .btn-text {
            display: none;
          }
          
          padding: 10px 12px;
          min-width: 42px;
          justify-content: center;
        }
      }
    }
  }
}


// 动画关键帧
@keyframes statusPulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 移动端优化 */
@media (max-width: 768px) {
  .modern-chat-header {
    padding: 0 16px;
    min-height: 64px;
    
    .header-container {
      gap: 16px;
    }
    
    .header-left {
      .brand-section {
        gap: 12px;
        
        .brand-icon .icon-gradient {
          width: 40px;
          height: 40px;
          font-size: 20px;
          border-radius: 12px;
        }
        
        .brand-info {
          .brand-title {
            font-size: 17px;
          }
          
          .brand-subtitle {
            font-size: 11px;
            
            .message-count {
              padding: 1px 6px;
            }
          }
        }
      }
    }
    
    .header-right {
      .header-actions {
        gap: 8px;
        
        .model-selector {
          :deep(.modern-model-select) {
            min-width: 120px;
          }
        }
        
        .tool-buttons {
          gap: 8px;
          
          .action-btn {
            padding: 8px 10px;
            min-height: 36px;
            
            .btn-icon {
              font-size: 14px;
            }
          }
        }
      }
    }
  }
}

/* 平板端优化 */
@media (max-width: 1024px) and (min-width: 769px) {
  .modern-chat-header {
    padding: 0 20px;
    min-height: 68px;
    
    .header-left {
      .brand-section {
        .brand-icon .icon-gradient {
          width: 44px;
          height: 44px;
          font-size: 22px;
        }
        
        .brand-info {
          .brand-title {
            font-size: 18px;
          }
        }
      }
    }
    
    .header-right {
      .header-actions {
        .model-selector {
          :deep(.modern-model-select) {
            min-width: 160px;
          }
        }
      }
    }
  }
}

/* 超大屏幕优化 */
@media (min-width: 1440px) {
  .modern-chat-header {
    padding: 0 32px;
    min-height: 80px;
    
    .header-left {
      .brand-section {
        gap: 20px;
        
        .brand-icon .icon-gradient {
          width: 52px;
          height: 52px;
          font-size: 26px;
        }
        
        .brand-info {
          .brand-title {
            font-size: 22px;
          }
          
          .brand-subtitle {
            font-size: 13px;
          }
        }
      }
    }
    
    .header-right {
      .header-actions {
        gap: 24px;
        
        .model-selector {
          :deep(.modern-model-select) {
            min-width: 240px;
          }
        }
        
        .tool-buttons {
          gap: 16px;
          
          .action-btn {
            padding: 12px 18px;
            min-height: 48px;
            
            .btn-icon {
              font-size: 18px;
            }
          }
        }
      }
    }
  }
}

/* 减少动画效果（用户偏好） */
@media (prefers-reduced-motion: reduce) {
  .modern-chat-header {
    * {
      animation: none !important;
      transition: none !important;
    }
  }
}
</style>