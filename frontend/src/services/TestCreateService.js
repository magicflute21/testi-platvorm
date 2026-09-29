import axios from 'axios'

export default {
  postNewTest(test) {
    return axios.post('/api/tests', test)
  },
}
