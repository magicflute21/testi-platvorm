import router from '@/router/index.js'
import SessionStorageService from '@/services/SessionStorageService.js'

export default {
  navigateToLogin() {
    router.push({
      name: 'loginRoute',
    })
  },
  // Admin ja haldur alustavad töölaualt, tavakasutaja oma testide lehelt
  navigateToStartPage() {
    if (SessionStorageService.hasRole(['ADMIN', 'HALDUR'])) {
      this.navigateToDashboard()
    } else {
      router.push({ name: 'myTestsRoute' })
    }
  },
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
  navigateToQuestionCreate() {
    router.push({
      path: '/questions/new',
    })
  },
  navigateToTestStartView(testId) {
    router.push({
      name: 'testStartRoute',
      params: { testId },
    })
  },
  navigateToTestDetail(testId) {
    router.push({
      name: 'testDetailRoute',
      params: { testId },
    })
  },
  navigateToTestsView() {
    router.push({
      name: 'testsRoute',
    })
  },
}
