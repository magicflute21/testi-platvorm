import axios from 'axios'

export default {
  getQuestionsRequest(competenceLevelId) {
    return axios.get('/api/questions', { params: { competenceLevelId } })
  },

  getQuestionBankRequest(competenceId) {
    return axios.get('/api/question-bank', { params: { competenceId } })
  },

  updateQuestionRequest(questionId, questionUpdateRequestDto) {
    return axios.put(`/api/questions/${questionId}`, questionUpdateRequestDto)
  },

  deleteQuestionRequest(questionId) {
    return axios.delete(`/api/questions/${questionId}`)
  },
}
