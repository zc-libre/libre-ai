import { http } from '@/utils/http';

export function page(params: any) {
  return http.request('get', '/aigc/model/page', { params });
}

export function list(params: any) {
  return http.request('get', '/aigc/model/list', { params });
}

export function getById(id: any) {
  return http.request('get', `/aigc/model/${id}`);
}

export function add(params: any) {
  return http.request('post', '/aigc/model', { data: params });
}

export function update(params: any) {
  return http.request('put', '/aigc/model', { data: params });
}

export function del(id: string) {
  return http.request('delete', `/aigc/model/${id}`);
}
