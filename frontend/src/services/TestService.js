import axios from 'axios'

export default {

  getAllTests() {
    return axios.get('/api/tests')
  },
  getTestDetail(testId) {
    return axios.get(`/api/tests/${testId}`)
  },
  getTestResult(userTestId) {
    return axios.get('/api/result', { params : { userTestId }})
  }
}
