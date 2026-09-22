import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/navigation/MainLayout.vue'
import HomeView from '@/views/HomeView.vue'
import TestView from '@/views/TestView.vue'
import LoginView from '@/views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', name: 'homeRoute', component: HomeView },
        { path: 'test', name: 'testRoute', component: TestView },
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
