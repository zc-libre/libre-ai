export default {
  path: '/aigc',
  redirect: '/aigc/dashboard',
  meta: {
    icon: 'ri:ai-generate',
    title: 'AI智能',
    rank: 8
  },
  children: [
    {
      path: '/aigc/dashboard',
      name: 'AigcDashboard',
      component: () => import('@/views/aigc-dashboard/index.vue'),
      meta: {
        icon: 'ri:dashboard-line',
        title: 'AI仪表盘'
      }
    },
    {
      path: '/aigc/knowledge',
      name: 'AigcKnowledge',
      component: () => import('@/views/aigc/knowledge/index.vue'),
      meta: {
        icon: 'ri:book-3-line',
        title: '知识库管理'
      }
    },
    {
      path: '/aigc/knowledge/:id',
      name: 'AigcKnowledgeDetail',
      component: () => import('@/views/aigc/knowledge/components/index.vue'),
      meta: {
        title: '知识库详情',
        showLink: false
      }
    },
    {
      path: '/aigc/message',
      name: 'AigcMessage',
      component: () => import('@/views/aigc/message/index.vue'),
      meta: {
        icon: 'ri:message-3-line',
        title: '消息管理'
      }
    },
    {
      path: '/aigc/order',
      name: 'AigcOrder',
      component: () => import('@/views/aigc/order/index.vue'),
      meta: {
        icon: 'ri:shopping-cart-line',
        title: '订单管理'
      }
    },
    {
      path: '/aigc/chat',
      name: 'AigcChat',
      component: () => import('@/views/chat/index.vue'),
      meta: {
        icon: 'material-symbols:chat-outline',
        title: 'AI聊天助手'
      }
    }
  ]
} as RouteConfigsTable;
