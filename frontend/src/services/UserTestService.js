import axios from 'axios'


export default {
  getUserTests() {
    return axios.get('/api/me/user-tests')
  },
}
