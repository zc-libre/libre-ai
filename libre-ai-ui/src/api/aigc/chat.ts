import { http } from '@/utils/http';

export function getAppInfo(params: any) {
  return http.request('get', '/aigc/app/info', { params });
}

export function chat(
  data: any,
  controller: AbortController,
  onDownloadProgress?: (progressEvent: any) => void
) {
  return http.request('post', '/aigc/chat/completions', {
    data,
    signal: controller.signal,
    onDownloadProgress: onDownloadProgress
  });
}

export function chatMessage(params: any) {
  return http.request('post', '/aigc/chat/completions', { data: params });
}

export function chatMessageStream(params: any) {
  return http.request('post', '/aigc/chat/completions', { data: params });
}

export function clean(conversationId: string | null) {
  return http.request('delete', `/aigc/chat/messages/clean/${conversationId}`);
}

export function getMessages(conversationId?: string) {
  return http.request('get', `/aigc/chat/messages/${conversationId}`);
}

export function getImageModels() {
  return http.request('get', '/aigc/chat/getImageModels');
}

export function genImage(data: any) {
  return http.request('post', '/aigc/chat/image', { data });
}

export function genMindMap(data: any) {
  return http.request('post', '/aigc/chat/mindmap', { data });
}

export function fetchRenameChatRoom(roomId: number, title: string) {
  return http.request('put', `/aigc/chat/room/${roomId}`, { data: { title } });
}
