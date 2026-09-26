import axios from 'axios'

export default {
  getTestStart(testId) {
    return axios.get(`/api/tests/${testId}/start-info`)
  },
}
