export default {
  path: '/dashboard-generator',
  redirect: '/dashboard-generator/index',
  meta: {
    icon: 'ri:dashboard-3-line',
    title: '看板生成',
    rank: 2
  },
  children: [
    {
      path: '/dashboard-generator/index',
      name: 'DashboardGenerator',
      component: () => import('@/views/dashboard-generator/index.vue'),
      meta: {
        title: '看板生成器',
        keepAlive: true
      }
    }
  ]
} satisfies RouteConfigsTable;
