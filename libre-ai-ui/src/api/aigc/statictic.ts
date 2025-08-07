import { http } from '@/utils/http';

export function getReqChartBy30() {
  return http.request('get', `/aigc/statistic/requestBy30`);
}

export function getReqChart() {
  return http.request('get', `/aigc/statistic/request`);
}

export function getTokenChartBy30() {
  return http.request('get', `/aigc/statistic/tokenBy30`);
}

export function getTokenChart() {
  return http.request('get', `/aigc/statistic/token`);
}

export function getHomeData() {
  return http.request('get', `/aigc/statistic/home`);
}
