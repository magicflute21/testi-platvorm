import axios from 'axios'

export default {
  getTestAttemptRequest(userTestId) {
    return axios.get(`/api/user-tests/${userTestId}/attempt`)
  }
}
