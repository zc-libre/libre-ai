export default {
  path: '/dashboard-generator',
  redirect: '/dashboard-generator/index',
  meta: {
    icon: 'ri:dashboard-3-line',
    title: '仪表盘生成器',
    rank: 2
  },
  children: [
    {
      path: '/dashboard-generator/index',
      name: 'DashboardGenerator',
      component: () => import('@/views/dashboard-generator/index.vue'),
      meta: {
        title: '仪表盘生成器',
        keepAlive: true
      }
    }
  ]
} satisfies RouteConfigsTable;
