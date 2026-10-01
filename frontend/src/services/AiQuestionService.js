import axios from 'axios'

export default {
  sendGenerateQuestionRequest(instructions) {
    return axios.post('/api/ai-questions/generate', { instructions })
  },

  sendSaveQuestionRequest(question) {
    return axios.post('/api/ai-questions', question)
  },
}
