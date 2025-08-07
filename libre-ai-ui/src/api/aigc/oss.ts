import { http } from '@/utils/http';

export function uploadApi(
  params: any,
  onUploadProgress?: (progressEvent: any) => void
) {
  const formData = new FormData();
  formData.append('file', params.file);

  return http.request('post', '/aigc/oss/upload', {
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  });
}
