import axios from 'axios'

export default {
  sendGenerateQuestionRequest(instructions) {
    return axios.post('/api/ai-questions/generate', { instructions })
  },

  sendSaveQuestionRequest(question) {
    return axios.post('/api/ai-questions', question)
  },

  getAiQuestionsRequest(status, competenceId) {
    return axios.get('/api/ai-questions', { params: { status, competenceId } })
  },

  reviewAiQuestionRequest(aiQuestionId, aiQuestionReviewRequestDto) {
    return axios.post(`/api/ai-questions/${aiQuestionId}/review`, aiQuestionReviewRequestDto)
  },
}
