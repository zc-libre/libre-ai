import { http } from '@/utils/http';

export function list(params: any) {
  return http.request('get', '/aigc/app/list', { params });
}

export function getAppApiChannel(appId: any) {
  return http.request('get', `/aigc/app/channel/api/${appId}`);
}

export function page(params: any) {
  return http.request('get', '/aigc/app/page', { params });
}

export function getById(id: string) {
  return http.request('get', `/aigc/app/${id}`);
}

export function getByModelId(id: string) {
  return http.request('get', `/aigc/app/byModelId/${id}`);
}

export function add(params: any) {
  return http.request('post', '/aigc/app', { params });
}

export function update(params: any) {
  return http.request('put', '/aigc/app', { params });
}

export function del(id: string) {
  return http.request('delete', `/aigc/app/${id}`);
}
