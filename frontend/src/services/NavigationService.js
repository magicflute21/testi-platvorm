import router from '@/router/index.js'

export default {
  navigateToDashboard() {
    router.push({
      name: 'dashboardRoute',
    })
  },
}
