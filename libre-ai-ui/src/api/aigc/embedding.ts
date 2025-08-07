import { http } from '@/utils/http';
import type { AxiosProgressEvent } from 'axios';

export function embeddingText(params: any) {
  return http.request('post', '/aigc/embedding/text', { data: params });
}

export function embeddingSearch(data: any) {
  return http.request('post', '/aigc/embedding/search', { data });
}

export function embeddingDocs(
  knowledgeId: string,
  data: any,
  onUploadProgress?: (progressEvent: AxiosProgressEvent) => void
) {
  return http.request('post', `/aigc/embedding/docs/${knowledgeId}`, {
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  });
}
