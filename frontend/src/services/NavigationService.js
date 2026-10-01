import router from '@/router/index.js'

export default {
  navigateToDashboard() {
    router.push({
      name: 'dashboardRoute',
    })
  },
  navigateToTestAttempt(testId) {
    router.push({
      name: 'testAttemptRoute',
      params: { testId },
    })
  },
  navigateToTestResult(userTestId) {
    router.push({
      name: 'testResultRoute',
      query: { userTestId: userTestId },
    })
  },
}
