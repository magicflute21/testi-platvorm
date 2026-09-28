import axios from 'axios'

export default {

  getAllTests() {
    return axios.get('/api/tests')

  },
}
