import { http } from '@/utils/http';

export function list(params: any) {
  return http.request('get', '/aigc/knowledge/list', { params });
}

export function page(params: any) {
  return http.request('get', '/aigc/knowledge/page', { params });
}

export function getById(id: string) {
  return http.request('get', `/aigc/knowledge/${id}`);
}

export function add(params: any) {
  return http.request('post', '/aigc/knowledge', { data: params });
}

export function update(params: any) {
  return http.request('put', '/aigc/knowledge', { data: params });
}

export function del(id: string) {
  return http.request('delete', `/aigc/knowledge/${id}`);
}
