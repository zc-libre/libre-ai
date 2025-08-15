import html2canvas from 'html2canvas';

export interface ExportOptions {
  format?: 'png' | 'jpeg';
  quality?: number;
  filename?: string;
  includeHeader?: boolean;
  backgroundColor?: string;
}

/**
 * 导出聊天记录为图片
 */
export async function exportChatAsImage(options: ExportOptions = {}) {
  const {
    format = 'png',
    quality = 1.0,
    filename = `chat-export-${new Date().toISOString().slice(0, 10)}`,
    includeHeader = true,
    backgroundColor = '#ffffff'
  } = options;

  try {
    // 找到聊天容器
    const chatContainer = document.getElementById('image-wrapper');
    if (!chatContainer) {
      throw new Error('找不到聊天容器');
    }

    // 创建临时容器用于截图
    const tempContainer = document.createElement('div');
    tempContainer.style.position = 'absolute';
    tempContainer.style.left = '-9999px';
    tempContainer.style.top = '0';
    tempContainer.style.width = '800px';
    tempContainer.style.backgroundColor = backgroundColor;
    tempContainer.style.padding = '20px';
    tempContainer.style.fontFamily =
      '"Inter", "Helvetica Neue", Arial, sans-serif';

    document.body.appendChild(tempContainer);

    // 添加标题（如果需要）
    if (includeHeader) {
      const header = document.createElement('div');
      header.style.marginBottom = '20px';
      header.style.paddingBottom = '15px';
      header.style.borderBottom = '2px solid #e5e7eb';
      header.innerHTML = `
        <div style="display: flex; align-items: center; margin-bottom: 10px;">
          <div style="width: 24px; height: 24px; background: linear-gradient(135deg, #3b82f6, #8b5cf6); border-radius: 6px; margin-right: 12px; display: flex; align-items: center; justify-content: center;">
            <span style="color: white; font-size: 14px; font-weight: bold;">AI</span>
          </div>
          <h2 style="margin: 0; font-size: 20px; font-weight: 600; color: #1f2937;">AI 聊天记录</h2>
        </div>
        <div style="font-size: 14px; color: #6b7280;">
          导出时间：${new Date().toLocaleString('zh-CN')}
        </div>
      `;
      tempContainer.appendChild(header);
    }

    // 克隆聊天内容
    const chatClone = chatContainer.cloneNode(true) as HTMLElement;

    // 清理克隆元素的样式，确保在截图容器中正确显示
    chatClone.style.width = '100%';
    chatClone.style.maxWidth = 'none';
    chatClone.style.padding = '0';
    chatClone.style.margin = '0';

    // 移除一些可能影响截图的元素
    const elementsToRemove = chatClone.querySelectorAll(
      '.el-loading-mask, .el-overlay, .el-message, .el-notification'
    );
    elementsToRemove.forEach(el => el.remove());

    // 确保按钮等交互元素在截图中不显示
    const buttons = chatClone.querySelectorAll('button, .el-button');
    buttons.forEach((btn: any) => {
      if (
        btn.textContent?.includes('停止响应') ||
        btn.textContent?.includes('重新生成')
      ) {
        btn.style.display = 'none';
      }
    });

    tempContainer.appendChild(chatClone);

    // 等待一下确保样式加载完成
    await new Promise(resolve => setTimeout(resolve, 100));

    // 生成截图
    const canvas = await html2canvas(tempContainer, {
      backgroundColor: backgroundColor,
      scale: 2, // 提高分辨率
      useCORS: true,
      allowTaint: true,
      foreignObjectRendering: true,
      logging: false,
      width: tempContainer.scrollWidth,
      height: tempContainer.scrollHeight
    });

    // 清理临时容器
    document.body.removeChild(tempContainer);

    // 转换为下载链接
    const mimeType = format === 'jpeg' ? 'image/jpeg' : 'image/png';
    const dataUrl = canvas.toDataURL(mimeType, quality);

    // 创建下载链接并触发下载
    const link = document.createElement('a');
    link.download = `${filename}.${format}`;
    link.href = dataUrl;
    link.click();

    return dataUrl;
  } catch (error) {
    console.error('导出聊天记录失败:', error);
    throw error;
  }
}

/**
 * 复制聊天记录为纯文本
 */
export function exportChatAsText(): string {
  const chatContainer = document.getElementById('image-wrapper');
  if (!chatContainer) {
    throw new Error('找不到聊天容器');
  }

  const messages = chatContainer.querySelectorAll('.message-container');
  const chatText = Array.from(messages)
    .map((msg: any) => {
      const isUser = msg.querySelector('.message-bubble-user');
      const text = msg.querySelector('.text-content')?.textContent || '';
      const time = msg.querySelector('.text-xs')?.textContent || '';

      const role = isUser ? '用户' : 'AI助手';
      return `[${time}] ${role}:\n${text}\n`;
    })
    .join('\n');

  const fullText = `AI 聊天记录
导出时间：${new Date().toLocaleString('zh-CN')}
=====================================

${chatText}`;

  // 复制到剪贴板
  navigator.clipboard.writeText(fullText).catch(console.error);

  return fullText;
}

/**
 * 导出聊天记录为JSON格式
 */
export function exportChatAsJSON() {
  const chatContainer = document.getElementById('image-wrapper');
  if (!chatContainer) {
    throw new Error('找不到聊天容器');
  }

  const messages = chatContainer.querySelectorAll('.message-container');
  const chatData = Array.from(messages).map((msg: any) => {
    const isUser = msg.querySelector('.message-bubble-user');
    const text = msg.querySelector('.text-content')?.textContent || '';
    const time = msg.querySelector('.text-xs')?.textContent || '';
    const searchQuery =
      msg.querySelector('[class*="search-query"]')?.textContent || '';
    const reasoning =
      msg.querySelector('[class*="reasoning"]')?.textContent || '';

    return {
      role: isUser ? 'user' : 'assistant',
      content: text,
      timestamp: time,
      searchQuery: searchQuery || undefined,
      reasoning: reasoning || undefined
    };
  });

  const exportData = {
    exportTime: new Date().toISOString(),
    messages: chatData,
    metadata: {
      totalMessages: chatData.length,
      userMessages: chatData.filter(m => m.role === 'user').length,
      assistantMessages: chatData.filter(m => m.role === 'assistant').length
    }
  };

  const jsonString = JSON.stringify(exportData, null, 2);

  // 创建下载链接
  const blob = new Blob([jsonString], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.download = `chat-export-${new Date().toISOString().slice(0, 10)}.json`;
  link.href = url;
  link.click();
  URL.revokeObjectURL(url);

  return exportData;
}
