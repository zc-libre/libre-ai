import { http } from '@/utils/http';

export function list(params: any) {
  return http.request('get', '/aigc/docs/slice/list', { params });
}

export function page(params: any) {
  return http.request('get', '/aigc/docs/slice/page', { params });
}

export function getById(id: string) {
  return http.request('get', `/aigc/docs/slice/${id}`);
}

export function add(params: any) {
  return http.request('post', '/aigc/docs/slice', { data: params });
}

export function update(params: any) {
  return http.request('put', '/aigc/docs/slice', { data: params });
}

export function del(id: string) {
  return http.request('delete', `/aigc/docs/slice/${id}`);
}
