import { http } from '@/utils/http';

export function list(params: any) {
  return http.request('get', '/aigc/app/api/list', { params });
}

export function page(params: any) {
  return http.request('get', '/aigc/app/api/page', { params });
}

export function getById(id: any) {
  return http.request('get', `/aigc/app/api/${id}`);
}

export function createApi(id: any, channel: any) {
  return http.request('get', `/aigc/app/api/create/${id}/${channel}`);
}

export function add(params: any) {
  return http.request('post', '/aigc/app/api', { data: params });
}

export function update(params: any) {
  return http.request('put', '/aigc/app/api', { data: params });
}

export function del(id?: string) {
  return http.request('delete', `/aigc/app/api/${id}`);
}
