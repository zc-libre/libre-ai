import { http } from '@/utils/http';

export function page(params: any) {
  return http.request('get', '/aigc/conversation/page', { params });
}

export function del(id: string) {
  return http.request('delete', `/aigc/conversation/${id}`);
}

export function getMessages(conversationId: string) {
  return http.request('get', `/aigc/conversation/messages/${conversationId}`);
}
