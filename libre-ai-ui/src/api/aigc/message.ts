import { http } from '@/utils/http';

export function list(params: any) {
  return http.request('get', '/aigc/message/list', { params });
}

export function page(params: any) {
  return http.request('get', '/aigc/message/page', { params });
}

export function add(params: any) {
  return http.request('post', '/aigc/message', { data: params });
}

export function update(params: any) {
  return http.request('put', '/aigc/message', { data: params });
}

export function del(id: string) {
  return http.request('delete', `/aigc/message/${id}`);
}
