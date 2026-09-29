import axios from 'axios'

export default {
  getCompetencesRequest() {
    return axios.get('/api/competences')
  },
}
