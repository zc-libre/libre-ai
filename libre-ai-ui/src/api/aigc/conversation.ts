import { http } from '@/utils/http';

export function page(params: any) {
  return http.request('get', '/aigc/conversation/page', { params });
}

export function list() {
  return http.request('get', '/aigc/conversation/list');
}

export function add(data: Chat.ChatRoom) {
  return http.request('post', '/aigc/conversation', { data });
}

export function update(data: Chat.ChatRoom) {
  return http.request('put', '/aigc/conversation', { data });
}

export function del(id: string) {
  return http.request('delete', `/aigc/conversation/${id}`);
}

export function clearMessages(conversationId: string) {
  return http.request('delete', `/aigc/conversation/message/${conversationId}`);
}

export function getMessages(conversationId: string) {
  return http.request('get', `/aigc/conversation/messages/${conversationId}`);
}

export function updateSettings(
  conversationId: string,
  settings: Partial<Chat.ChatRoom>
) {
  return http.request('put', `/aigc/conversation/${conversationId}/settings`, {
    data: settings
  });
}

export function getSettings(conversationId: string) {
  return http.request('get', `/aigc/conversation/${conversationId}/settings`);
}
