import axios from 'axios'

export default {
  getQuestionTypesRequest() {
    return axios.get('/api/question-types')
  },
}
