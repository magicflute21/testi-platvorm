import axios from 'axios'

export default {
  getCompetenceLevelsRequest(competenceId) {
    return axios.get('/api/competence-levels', { params: { competenceId } })
  },
}
