import axios from 'axios'

export default {
  getQuestionsRequest(competenceLevelId) {
    return axios.get('/api/questions', { params: { competenceLevelId } })
  },

  getQuestionBankRequest(competenceId) {
    return axios.get('/api/question-bank', { params: { competenceId } })
  },
}
