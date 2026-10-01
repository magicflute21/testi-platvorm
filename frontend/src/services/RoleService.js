import axios from 'axios'

export default {
  getAllRoles() {
    return axios.get('/api/roles')
  },
}
