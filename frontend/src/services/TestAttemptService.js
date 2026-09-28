import axios from 'axios'

export default {
  getTestAttemptRequest(testId) {
    return axios.get(`/api/tests/${testId}/attempt`)
  },
  postCompleteTest(testId, submittedAnswers) {
    return axios.post(`/api/tests/${testId}/complete`, submittedAnswers)
  }
}
