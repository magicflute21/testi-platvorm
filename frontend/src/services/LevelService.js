import axios from 'axios'

export default {
  getAllLevels() {
    return axios.get('/api/levels')
  },
}
