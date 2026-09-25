import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/navigation/MainLayout.vue'
import DashboardView from '@/views/DashboardView.vue'
import TestView from '@/views/TestView.vue'
import LoginView from '@/views/LoginView.vue'
import TestOverview from "@/views/TestOverview.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: 'dashboard', name: 'dashboardRoute', component: DashboardView },
        { path: 'tests', name: 'testsRoute', component: TestOverview}
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
