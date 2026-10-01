import axios from 'axios'

export default {
  postLoginRequest(loginRequest) {
    return axios.post('/api/login', loginRequest)
  },
  postLogoutRequest() {
    return axios.post('/api/logout')
  },
}
