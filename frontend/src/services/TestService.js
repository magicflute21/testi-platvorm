import axios from 'axios'

export default {

  getAllTests() {
    return axios.get('/api/tests')
  },
  getTestResult(userTestId) {
    return axios.get('/api/result', { params : { userTestId }})
  }
}
