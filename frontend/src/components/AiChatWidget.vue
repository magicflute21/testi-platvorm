<script>
import { PhSparkle, PhX, PhPaperPlaneRight, PhCheckCircle } from '@phosphor-icons/vue'
import AiQuestionService from '@/services/AiQuestionService.js'

const MAX_INPUT_LENGTH = 500
const MAX_QUESTIONS_PER_HOUR = 30

const WELCOME_MESSAGE =
  'Tere! Kirjelda, milliseid küsimusi soovid luua — nt "2 küsimust JavaScripti algajatele massiivide kohta ja 1 tõene/väär küsimus SQL algajatele". Korraga saab luua kuni 5 küsimust, ka eri kompetentsidele.'

// Backendi veateated, mida võib kasutajale otse näidata
const STATUSES_WITH_USER_MESSAGE = [400, 403, 429]

export default {
  name: 'AiChatWidget',
  components: { PhSparkle, PhX, PhPaperPlaneRight, PhCheckCircle },
  data() {
    return {
      maxInputLength: MAX_INPUT_LENGTH,
      maxQuestionsPerHour: MAX_QUESTIONS_PER_HOUR,
      isOpen: false,
      isLoading: false,
      userInput: '',
      // Kui AI küsis täpsustust, hoitakse meeles kasutaja esialgne soov
      originalInstructions: '',
      remainingQuestionCount: null,
      messages: [{ sender: 'ai', text: WELCOME_MESSAGE, questions: [] }],
    }
  },
  computed: {
    canSend() {
      return this.userInput.trim() !== '' && !this.isLoading
    },
  },
  methods: {
    toggleChat() {
      this.isOpen = !this.isOpen
    },

    sendMessage() {
      if (!this.canSend) {
        return
      }
      const userText = this.userInput.trim()
      this.userInput = ''
      this.addMessage('user', userText)
      this.generateQuestions(this.createInstructions(userText))
    },

    // Täpsustuse korral saadetakse täpsustus + esialgne soov, kokku kuni 500 märki
    createInstructions(userText) {
      if (this.originalInstructions === '') {
        return userText
      }
      const instructions = `Täpsustus: ${userText}\nAlgne soov: ${this.originalInstructions}`
      return instructions.slice(0, MAX_INPUT_LENGTH)
    },

    generateQuestions(instructions) {
      this.isLoading = true
      AiQuestionService.sendGenerateQuestionRequest(instructions)
        .then((response) => this.handleGenerateQuestionsResponse(response.data, instructions))
        .catch((error) => this.handleRequestError(error))
        .finally(() => (this.isLoading = false))
    },

    handleGenerateQuestionsResponse(generationResponse, instructions) {
      this.remainingQuestionCount = generationResponse.remainingQuestionCount

      if (generationResponse.clarifyingQuestion) {
        if (this.originalInstructions === '') {
          this.originalInstructions = instructions
        }
        this.addMessage('ai', generationResponse.clarifyingQuestion)
        return
      }

      this.originalInstructions = ''
      const questions = generationResponse.questions.map((question) => ({
        ...question,
        status: 'pending',
      }))
      const text =
        questions.length === 1
          ? 'Koostasin sellise küsimuse. Kas soovid selle andmebaasi lisada?'
          : `Koostasin ${questions.length} küsimust. Vali, milliseid soovid andmebaasi lisada.`
      this.addMessage('ai', text, questions)
    },

    saveQuestion(question) {
      question.status = 'saving'
      AiQuestionService.sendSaveQuestionRequest({
        competenceLevelId: question.competenceLevelId,
        questionTypeId: question.questionTypeId,
        title: question.title,
        description: question.description,
        answers: question.answers,
      })
        .then(() => (question.status = 'saved'))
        .catch((error) => this.handleSaveQuestionError(error, question))
    },

    handleSaveQuestionError(error, question) {
      // Lubame uuesti proovida
      question.status = 'pending'
      this.handleRequestError(error)
    },

    rejectQuestion(question) {
      question.status = 'rejected'
    },

    handleRequestError(error) {
      // 401 korral suunab globaalne axios interceptor sisselogimise lehele
      const statusCode = error.response?.status
      if (statusCode === 401) {
        return
      }
      const backendMessage = error.response?.data?.message
      if (STATUSES_WITH_USER_MESSAGE.includes(statusCode) && backendMessage) {
        this.addMessage('ai', backendMessage)
        return
      }
      this.addMessage('ai', 'Midagi läks valesti. Palun proovi uuesti.')
    },

    addMessage(sender, text, questions = []) {
      this.messages.push({ sender, text, questions })
      this.$nextTick(() => this.scrollToBottom())
    },

    scrollToBottom() {
      const messageList = this.$refs.messageList
      if (messageList) {
        messageList.scrollTop = messageList.scrollHeight
      }
    },

    handleEnterKey(event) {
      // Enter saadab, Shift+Enter teeb uue rea
      if (!event.shiftKey) {
        event.preventDefault()
        this.sendMessage()
      }
    },
  },
}
</script>

<template>
  <div class="ai-chat">
    <div v-if="isOpen" class="ai-chat-panel card shadow">
      <div class="card-header d-flex align-items-center justify-content-between">
        <span class="d-flex align-items-center gap-2 fw-semibold">
          <PhSparkle :size="18" />
          AI küsimuste abiline
        </span>
        <button type="button" class="btn btn-sm btn-link text-white p-0" @click="toggleChat">
          <PhX :size="18" />
        </button>
      </div>

      <div ref="messageList" class="ai-chat-messages card-body">
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="ai-chat-message"
          :class="message.sender === 'user' ? 'from-user' : 'from-ai'"
        >
          <div class="ai-chat-bubble">{{ message.text }}</div>

          <div
            v-for="(question, questionIndex) in message.questions"
            :key="questionIndex"
            class="ai-question-preview card mt-2"
          >
            <div class="card-body p-2">
              <div class="small text-muted mb-1">
                {{ questionIndex + 1 }}. {{ question.competenceName }} · {{ question.levelName }} ·
                {{ question.questionTypeName }}
              </div>
              <div class="fw-semibold">{{ question.title }}</div>
              <div class="ai-question-description small mb-2">{{ question.description }}</div>
              <ul class="list-unstyled small mb-2">
                <li
                  v-for="(answer, answerIndex) in question.answers"
                  :key="answerIndex"
                  class="d-flex align-items-start gap-1"
                  :class="{ 'fw-semibold text-success': answer.isCorrect }"
                >
                  <PhCheckCircle v-if="answer.isCorrect" :size="16" class="flex-shrink-0 mt-1" />
                  <span v-else class="answer-bullet flex-shrink-0">•</span>
                  <span>{{ answer.answerText }}</span>
                </li>
              </ul>

              <div v-if="question.status === 'pending'" class="d-flex gap-2">
                <button
                  type="button"
                  class="btn btn-sm btn-success"
                  @click="saveQuestion(question)"
                >
                  Lisa andmebaasi
                </button>
                <button
                  type="button"
                  class="btn btn-sm btn-outline-secondary"
                  @click="rejectQuestion(question)"
                >
                  Ei soovi
                </button>
              </div>
              <div v-else-if="question.status === 'saving'" class="small text-muted fst-italic">
                Salvestan…
              </div>
              <div v-else-if="question.status === 'saved'" class="small text-success fw-semibold">
                Salvestatud, ootab ülevaatust
              </div>
              <div v-else class="small text-muted">Ei salvestatud</div>
            </div>
          </div>
        </div>

        <div v-if="isLoading" class="ai-chat-message from-ai">
          <div class="ai-chat-bubble text-muted fst-italic">AI mõtleb…</div>
        </div>
      </div>

      <div class="card-footer">
        <div class="d-flex gap-2">
          <textarea
            v-model="userInput"
            class="form-control form-control-sm"
            rows="2"
            :maxlength="maxInputLength"
            placeholder="Kirjelda soovitud küsimusi…"
            @keydown.enter="handleEnterKey"
          ></textarea>
          <button
            type="button"
            class="btn btn-primary btn-sm"
            :disabled="!canSend"
            @click="sendMessage"
          >
            <PhPaperPlaneRight :size="18" />
          </button>
        </div>
        <div class="d-flex justify-content-between small text-muted mt-1">
          <span v-if="remainingQuestionCount !== null">
            Sel tunnil alles: {{ remainingQuestionCount }}/{{ maxQuestionsPerHour }}
          </span>
          <span v-else></span>
          <span>{{ userInput.length }}/{{ maxInputLength }}</span>
        </div>
      </div>
    </div>

    <button
      type="button"
      class="ai-chat-toggle btn btn-primary rounded-circle shadow"
      title="AI küsimuste abiline"
      @click="toggleChat"
    >
      <PhX v-if="isOpen" :size="24" />
      <PhSparkle v-else :size="24" />
    </button>
  </div>
</template>

<style scoped>
.ai-chat {
  position: fixed;
  right: 1.5rem;
  bottom: 1.5rem;
  z-index: 1050;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.75rem;
}

.ai-chat-toggle {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-chat-panel {
  width: 380px;
  max-width: calc(100vw - 2rem);
  height: 560px;
  max-height: calc(100vh - 7rem);
  border-radius: 1rem;
  overflow: hidden;
}

.ai-chat-panel .card-header {
  background: var(--brand-slate);
  color: #fff;
}

.ai-chat-messages {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.ai-chat-message {
  display: flex;
  flex-direction: column;
  max-width: 90%;
}

.ai-chat-message.from-user {
  align-self: flex-end;
  align-items: flex-end;
}

.ai-chat-message.from-ai {
  align-self: flex-start;
}

.ai-chat-bubble {
  padding: 0.5rem 0.75rem;
  border-radius: 0.75rem;
  white-space: pre-wrap;
  font-size: 0.9rem;
}

.from-user .ai-chat-bubble {
  background: var(--brand-slate);
  color: #fff;
}

.from-ai .ai-chat-bubble {
  background: var(--bs-primary-bg-subtle);
}

/* Koodinäited kirjelduses säilitavad reavahetused ja taanded */
.ai-question-description {
  white-space: pre-wrap;
}

.answer-bullet {
  width: 16px;
  text-align: center;
}
</style>
