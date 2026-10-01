import axios from 'axios'

export default {
  sendGenerateQuestionRequest(instructions, previousMessages) {
    return axios.post('/api/ai-questions/generate', { instructions, previousMessages })
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
