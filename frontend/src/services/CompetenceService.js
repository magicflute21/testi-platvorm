import axios from 'axios'

export default {
  getCompetencesRequest() {
    return axios.get('/api/competences')
  },
  getAllCompetences() {
    return axios.get('/api/competences/all')
  },
}
