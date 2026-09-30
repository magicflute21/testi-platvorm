<script>
import { PhSparkle, PhX, PhPaperPlaneRight, PhCheckCircle } from '@phosphor-icons/vue'
import AiQuestionService from '@/services/AiQuestionService.js'

const WELCOME_MESSAGE =
  'Tere! Kirjelda, millist küsimust soovid luua — nt "JavaScripti algajatele valikvastustega küsimus massiivide kohta".'

export default {
  name: 'AiChatWidget',
  components: { PhSparkle, PhX, PhPaperPlaneRight, PhCheckCircle },
  data() {
    return {
      isOpen: false,
      isLoading: false,
      userInput: '',
      // Kui AI küsis täpsustust, pannakse eelmine soov ja vastus kokku üheks päringuks
      pendingInstructions: '',
      messages: [{ sender: 'ai', text: WELCOME_MESSAGE, question: null, isDecided: false }],
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

      const instructions =
        this.pendingInstructions === '' ? userText : `${this.pendingInstructions}\n${userText}`
      this.generateQuestion(instructions)
    },

    generateQuestion(instructions) {
      this.isLoading = true
      AiQuestionService.sendGenerateQuestionRequest(instructions)
        .then((response) => this.handleGenerateQuestionResponse(response.data, instructions))
        .catch((error) => this.handleRequestError(error))
        .finally(() => (this.isLoading = false))
    },

    handleGenerateQuestionResponse(generatedQuestion, instructions) {
      if (generatedQuestion.clarifyingQuestion) {
        this.pendingInstructions = instructions
        this.addMessage('ai', generatedQuestion.clarifyingQuestion)
        return
      }
      this.pendingInstructions = ''
      this.addMessage(
        'ai',
        'Koostasin sellise küsimuse. Kas soovid selle andmebaasi lisada?',
        generatedQuestion,
      )
    },

    saveQuestion(message) {
      message.isDecided = true
      this.isLoading = true
      const question = message.question
      AiQuestionService.sendSaveQuestionRequest({
        competenceLevelId: question.competenceLevelId,
        questionTypeId: question.questionTypeId,
        title: question.title,
        description: question.description,
        answers: question.answers,
      })
        .then(() => this.handleSaveQuestionResponse())
        .catch((error) => this.handleSaveQuestionError(error, message))
        .finally(() => (this.isLoading = false))
    },

    handleSaveQuestionResponse() {
      this.addMessage(
        'ai',
        'Küsimus on salvestatud ja ootab ülevaatust. Millist küsimust järgmiseks teeme?',
      )
    },

    handleSaveQuestionError(error, message) {
      // Lubame uuesti proovida
      message.isDecided = false
      this.handleRequestError(error)
    },

    rejectQuestion(message) {
      message.isDecided = true
      this.addMessage('ai', 'Selge, seda küsimust ei salvestatud. Kirjelda, mida soovid teisiti.')
    },

    handleRequestError(error) {
      // 401 korral suunab globaalne axios interceptor sisselogimise lehele
      if (error.response?.status === 401) {
        return
      }
      if (error.response?.status === 403) {
        this.addMessage('ai', error.response.data.message)
        return
      }
      this.addMessage('ai', 'Midagi läks valesti. Palun proovi uuesti.')
    },

    addMessage(sender, text, question = null) {
      this.messages.push({ sender, text, question, isDecided: false })
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

          <div v-if="message.question" class="ai-question-preview card mt-2">
            <div class="card-body p-2">
              <div class="small text-muted mb-1">
                {{ message.question.competenceName }} · {{ message.question.levelName }} ·
                {{ message.question.questionTypeName }}
              </div>
              <div class="fw-semibold">{{ message.question.title }}</div>
              <div class="small mb-2">{{ message.question.description }}</div>
              <ul class="list-unstyled small mb-2">
                <li
                  v-for="(answer, answerIndex) in message.question.answers"
                  :key="answerIndex"
                  class="d-flex align-items-start gap-1"
                  :class="{ 'fw-semibold text-success': answer.isCorrect }"
                >
                  <PhCheckCircle v-if="answer.isCorrect" :size="16" class="flex-shrink-0 mt-1" />
                  <span v-else class="answer-bullet flex-shrink-0">•</span>
                  <span>{{ answer.answerText }}</span>
                </li>
              </ul>
              <div v-if="!message.isDecided" class="d-flex gap-2">
                <button
                  type="button"
                  class="btn btn-sm btn-success"
                  :disabled="isLoading"
                  @click="saveQuestion(message)"
                >
                  Lisa andmebaasi
                </button>
                <button
                  type="button"
                  class="btn btn-sm btn-outline-secondary"
                  :disabled="isLoading"
                  @click="rejectQuestion(message)"
                >
                  Ei soovi
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="isLoading" class="ai-chat-message from-ai">
          <div class="ai-chat-bubble text-muted fst-italic">AI mõtleb…</div>
        </div>
      </div>

      <div class="card-footer d-flex gap-2">
        <textarea
          v-model="userInput"
          class="form-control form-control-sm"
          rows="2"
          maxlength="1000"
          placeholder="Kirjelda soovitud küsimust…"
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
  width: 360px;
  max-width: calc(100vw - 2rem);
  height: 520px;
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

.answer-bullet {
  width: 16px;
  text-align: center;
}
</style>
