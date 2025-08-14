export default {
  path: '/base',
  redirect: '/base/dashboard',
  meta: {
    icon: 'ri:ai-generate',
    title: '基础配置',
    rank: 1
  },
  children: [
    {
      path: '/base/model',
      name: 'AigcModel',
      component: () => import('@/views/aigc/model/index.vue'),
      meta: {
        icon: 'ri:robot-line',
        title: '模型管理'
      }
    },
    {
      path: '/base/embed-store',
      name: 'AigcEmbedStore',
      component: () => import('@/views/aigc/embed-store/index.vue'),
      meta: {
        icon: 'ri:database-2-line',
        title: '向量库管理'
      }
    },
    {
      path: '/base/app',
      name: 'AigcApp',
      component: () => import('@/views/app/index.vue'),
      meta: {
        icon: 'ri:apps-line',
        title: '应用管理'
      }
    },
    {
      path: '/base/app/:id',
      name: 'AigcAppDetail',
      component: () => import('@/views/app/info.vue'),
      meta: {
        title: '应用详情',
        showLink: false
      }
    }
  ]
} as RouteConfigsTable;
