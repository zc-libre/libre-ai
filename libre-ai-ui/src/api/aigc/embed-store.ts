import { http } from '@/utils/http';

export function list(params: any) {
  return http.request('get', '/aigc/embed-store/list', { params });
}

export function page(params: any) {
  return http.request('get', '/aigc/embed-store/page', { params });
}

export function getById(id: string) {
  return http.request('get', `/aigc/embed-store/${id}`);
}

export function add(params: any) {
  return http.request('post', '/aigc/embed-store', { data: params });
}

export function update(params: any) {
  return http.request('put', '/aigc/embed-store', { data: params });
}

export function del(id?: string) {
  return http.request('delete', `/aigc/embed-store/${id}`);
}
