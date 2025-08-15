/**
 * 聊天室数据持久化工具
 */

export interface ChatRoom {
  id: string;
  title: string;
  createdAt: string;
  updatedAt: string;
  messageCount: number;
  lastMessage?: string;
  pinned?: boolean;
  tags?: string[];
  settings?: {
    modelId?: string;
    modelName?: string;
    systemPrompt?: string;
    temperature?: number;
    maxTokens?: number;
    usingContext?: boolean;
    maxContextCount?: number;
    searchEnabled?: boolean;
    thinkEnabled?: boolean;
  };
}

export interface ChatMessage {
  id: string;
  roomId: string;
  chatId: string;
  role: 'user' | 'assistant' | 'system';
  message: string;
  createTime: string;
  images?: string[];
  searchQuery?: string;
  searchResults?: any[];
  reasoning?: string;
  responseCount?: number;
  isError?: boolean;
}

const STORAGE_KEYS = {
  CHAT_ROOMS: 'chat_rooms',
  CHAT_MESSAGES: 'chat_messages',
  ACTIVE_ROOM: 'active_chat_room',
  CHAT_SETTINGS: 'chat_settings'
};

class ChatStorage {
  private storagePrefix = 'libre-ai-chat:';

  private getStorageKey(key: string): string {
    return `${this.storagePrefix}${key}`;
  }

  private saveToStorage<T>(key: string, data: T): void {
    try {
      localStorage.setItem(this.getStorageKey(key), JSON.stringify(data));
    } catch (error) {
      console.error('保存数据到本地存储失败:', error);
    }
  }

  private loadFromStorage<T>(key: string, defaultValue: T): T {
    try {
      const data = localStorage.getItem(this.getStorageKey(key));
      return data ? JSON.parse(data) : defaultValue;
    } catch (error) {
      console.error('从本地存储加载数据失败:', error);
      return defaultValue;
    }
  }

  // 聊天室管理
  getChatRooms(): ChatRoom[] {
    return this.loadFromStorage(STORAGE_KEYS.CHAT_ROOMS, []);
  }

  saveChatRooms(rooms: ChatRoom[]): void {
    this.saveToStorage(STORAGE_KEYS.CHAT_ROOMS, rooms);
  }

  createChatRoom(title: string, settings?: ChatRoom['settings']): ChatRoom {
    const rooms = this.getChatRooms();
    const newRoom: ChatRoom = {
      id: `room_${Date.now()}_${Math.random().toString(36).slice(2, 9)}`,
      title: title || `聊天室 ${rooms.length + 1}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      messageCount: 0,
      pinned: false,
      tags: [],
      settings: settings || {}
    };

    rooms.unshift(newRoom);
    this.saveChatRooms(rooms);
    return newRoom;
  }

  updateChatRoom(roomId: string, updates: Partial<ChatRoom>): void {
    const rooms = this.getChatRooms();
    const index = rooms.findIndex(room => room.id === roomId);

    if (index !== -1) {
      rooms[index] = {
        ...rooms[index],
        ...updates,
        updatedAt: new Date().toISOString()
      };
      this.saveChatRooms(rooms);
    }
  }

  deleteChatRoom(roomId: string): void {
    const rooms = this.getChatRooms();
    const filteredRooms = rooms.filter(room => room.id !== roomId);
    this.saveChatRooms(filteredRooms);

    // 同时删除该聊天室的所有消息
    this.clearRoomMessages(roomId);
  }

  // 消息管理
  getRoomMessages(roomId: string): ChatMessage[] {
    const allMessages = this.loadFromStorage<ChatMessage[]>(
      STORAGE_KEYS.CHAT_MESSAGES,
      []
    );
    return allMessages.filter(msg => msg.roomId === roomId);
  }

  saveMessage(message: ChatMessage): void {
    const allMessages = this.loadFromStorage<ChatMessage[]>(
      STORAGE_KEYS.CHAT_MESSAGES,
      []
    );
    allMessages.push(message);
    this.saveToStorage(STORAGE_KEYS.CHAT_MESSAGES, allMessages);

    // 更新聊天室的最后消息和消息计数
    this.updateRoomStats(message.roomId, message.message);
  }

  updateMessage(messageId: string, updates: Partial<ChatMessage>): void {
    const allMessages = this.loadFromStorage<ChatMessage[]>(
      STORAGE_KEYS.CHAT_MESSAGES,
      []
    );
    const index = allMessages.findIndex(msg => msg.id === messageId);

    if (index !== -1) {
      allMessages[index] = { ...allMessages[index], ...updates };
      this.saveToStorage(STORAGE_KEYS.CHAT_MESSAGES, allMessages);

      // 如果更新的是消息内容，也更新聊天室统计
      if (updates.message) {
        this.updateRoomStats(allMessages[index].roomId, updates.message);
      }
    }
  }

  deleteMessage(messageId: string): void {
    const allMessages = this.loadFromStorage<ChatMessage[]>(
      STORAGE_KEYS.CHAT_MESSAGES,
      []
    );
    const filteredMessages = allMessages.filter(msg => msg.id !== messageId);
    this.saveToStorage(STORAGE_KEYS.CHAT_MESSAGES, filteredMessages);
  }

  clearRoomMessages(roomId: string): void {
    const allMessages = this.loadFromStorage<ChatMessage[]>(
      STORAGE_KEYS.CHAT_MESSAGES,
      []
    );
    const filteredMessages = allMessages.filter(msg => msg.roomId !== roomId);
    this.saveToStorage(STORAGE_KEYS.CHAT_MESSAGES, filteredMessages);

    // 重置聊天室统计
    this.updateChatRoom(roomId, { messageCount: 0, lastMessage: undefined });
  }

  private updateRoomStats(roomId: string, lastMessage: string): void {
    const rooms = this.getChatRooms();
    const room = rooms.find(r => r.id === roomId);

    if (room) {
      const messageCount = this.getRoomMessages(roomId).length;
      this.updateChatRoom(roomId, {
        messageCount,
        lastMessage:
          lastMessage.slice(0, 50) + (lastMessage.length > 50 ? '...' : '')
      });
    }
  }

  // 当前活跃聊天室
  getActiveRoomId(): string | null {
    return this.loadFromStorage(STORAGE_KEYS.ACTIVE_ROOM, null);
  }

  setActiveRoomId(roomId: string | null): void {
    this.saveToStorage(STORAGE_KEYS.ACTIVE_ROOM, roomId);
  }

  // 搜索功能
  searchMessages(query: string, roomId?: string): ChatMessage[] {
    const allMessages = this.loadFromStorage<ChatMessage[]>(
      STORAGE_KEYS.CHAT_MESSAGES,
      []
    );
    const messages = roomId
      ? allMessages.filter(msg => msg.roomId === roomId)
      : allMessages;

    const lowerQuery = query.toLowerCase();
    return messages.filter(
      msg =>
        msg.message.toLowerCase().includes(lowerQuery) ||
        msg.searchQuery?.toLowerCase().includes(lowerQuery) ||
        msg.reasoning?.toLowerCase().includes(lowerQuery)
    );
  }

  // 数据导出/导入
  exportData(): { rooms: ChatRoom[]; messages: ChatMessage[] } {
    return {
      rooms: this.getChatRooms(),
      messages: this.loadFromStorage<ChatMessage[]>(
        STORAGE_KEYS.CHAT_MESSAGES,
        []
      )
    };
  }

  importData(
    data: { rooms: ChatRoom[]; messages: ChatMessage[] },
    merge = false
  ): void {
    if (merge) {
      // 合并数据
      const existingRooms = this.getChatRooms();
      const existingMessages = this.loadFromStorage<ChatMessage[]>(
        STORAGE_KEYS.CHAT_MESSAGES,
        []
      );

      const newRooms = [...existingRooms];
      data.rooms.forEach(room => {
        if (!newRooms.find(r => r.id === room.id)) {
          newRooms.push(room);
        }
      });

      const newMessages = [...existingMessages];
      data.messages.forEach(msg => {
        if (!newMessages.find(m => m.id === msg.id)) {
          newMessages.push(msg);
        }
      });

      this.saveChatRooms(newRooms);
      this.saveToStorage(STORAGE_KEYS.CHAT_MESSAGES, newMessages);
    } else {
      // 覆盖数据
      this.saveChatRooms(data.rooms);
      this.saveToStorage(STORAGE_KEYS.CHAT_MESSAGES, data.messages);
    }
  }

  // 清理过期数据
  cleanupOldData(daysToKeep = 30): void {
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - daysToKeep);
    const cutoffTime = cutoffDate.toISOString();

    // 清理过期消息
    const allMessages = this.loadFromStorage<ChatMessage[]>(
      STORAGE_KEYS.CHAT_MESSAGES,
      []
    );
    const recentMessages = allMessages.filter(
      msg => msg.createTime > cutoffTime
    );
    this.saveToStorage(STORAGE_KEYS.CHAT_MESSAGES, recentMessages);

    // 更新聊天室统计
    const rooms = this.getChatRooms();
    rooms.forEach(room => {
      const roomMessages = recentMessages.filter(msg => msg.roomId === room.id);
      this.updateChatRoom(room.id, {
        messageCount: roomMessages.length,
        lastMessage:
          roomMessages.length > 0
            ? roomMessages[roomMessages.length - 1].message.slice(0, 50) + '...'
            : undefined
      });
    });
  }

  // 获取存储统计信息
  getStorageStats(): {
    totalRooms: number;
    totalMessages: number;
    storageSize: string;
  } {
    const rooms = this.getChatRooms();
    const messages = this.loadFromStorage<ChatMessage[]>(
      STORAGE_KEYS.CHAT_MESSAGES,
      []
    );

    // 计算存储大小（近似值）
    const storageData = JSON.stringify({ rooms, messages });
    const sizeInBytes = new Blob([storageData]).size;
    const sizeInMB = (sizeInBytes / (1024 * 1024)).toFixed(2);

    return {
      totalRooms: rooms.length,
      totalMessages: messages.length,
      storageSize: `${sizeInMB} MB`
    };
  }
}

export const chatStorage = new ChatStorage();
