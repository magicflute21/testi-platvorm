import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/navigation/MainLayout.vue'
import DashboardView from '@/views/DashboardView.vue'
import TestView from '@/views/TestView.vue'
import LoginView from '@/views/LoginView.vue'
import TestResultView from '@/views/TestResultView.vue'
import TestAttemptView from '@/views/TestAttemptView.vue'
import TestStartView from '@/views/TestStartView.vue'

const toNumber = (param) => (route) => ({ [param]: Number(route.params[param]) })

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: 'dashboard', name: 'dashboardRoute', component: DashboardView },
        { path: 'test', name: 'testRoute', component: TestView },
        { path: 'test-result', name: 'testResultRoute', component: TestResultView },
        {
          path: 'tests/:testId/start',
          name: 'testStartRoute',
          component: TestStartView,
          props: toNumber('testId'),
        },
        {
          path: 'tests/:testId/attempt',
          name: 'testAttemptRoute',
          component: TestAttemptView,
          props: toNumber('testId'),
        },
      ],
    },
    {
      path: '/login',
      name: 'loginRoute',
      component: LoginView,
    },
  ],
})

export default router
