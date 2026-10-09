<script>
import AiQuestionService from '@/services/AiQuestionService.js'
import CompetenceService from '@/services/CompetenceService.js'
import CompetenceLevelService from '@/services/CompetenceLevelService.js'
import AlertDanger from '@/components/AlertDanger.vue'
import QuestionBankCard from '@/components/QuestionBankCard.vue'
import QuestionAnswerOption from '@/components/QuestionAnswerOption.vue'
import { PhPaperPlaneRight, PhSparkle } from '@phosphor-icons/vue'

const MAX_INPUT_LENGTH = 500
// Mitu varasemat sõnumit saadetakse AI-le kaasa ja kui pikk võib üks sõnum olla (vt AiChatWidget)
const MAX_HISTORY_MESSAGES = 10
const MAX_HISTORY_MESSAGE_LENGTH = 1000

// Backendi veateated, mida võib kasutajale otse näidata
const STATUSES_WITH_USER_MESSAGE = [400, 403]

export default {
  name: 'AiQuestionGenerator',
  components: { AlertDanger, QuestionBankCard, QuestionAnswerOption, PhPaperPlaneRight, PhSparkle },

  beforeMount() {
    this.getCompetences()
  },

  data() {
    return {
      maxInputLength: MAX_INPUT_LENGTH,
      questionCountOptions: [1, 2, 3, 4, 5],
      isLoading: false,
      errorMessage: '',

      competences: [],
      competenceLevels: [],
      competenceId: 0,
      competenceLevelId: 0,
      questionCount: 3,
      userInput: '',

      // Vestluse ajalugu AI jaoks (kasutaja juhised, AI täpsustavad küsimused ja loodud küsimuste kokkuvõtted)
      conversationHistory: [],
      // Ekraanil kuvatavad sõnumid; AI sõnumil võivad olla kaasas loodud küsimused
      messages: [],

      questionTypeLabels: {
        SINGLE_CHOICE: 'Ühe õige vastusega',
        MULTIPLE_CHOICE: 'Mitme õige vastusega',
        TRUE_FALSE: 'Tõene või väär',
      },
      questionStatusBadges: {
        saved: { name: 'Salvestatud, ootab ülevaatust', badgeClass: 'badge-active' },
        rejected: { name: 'Ei salvestatud', badgeClass: 'badge-inactive' },
      },
    }
  },

  computed: {
    isCompetenceLevelSelected() {
      return this.competenceLevelId !== 0
    },
    canSend() {
      return this.isCompetenceLevelSelected && this.userInput.trim() !== '' && !this.isLoading
    },
    selectedCompetenceName() {
      const competence = this.competences.find(
        (competence) => competence.competenceId === this.competenceId,
      )
      return competence ? competence.competenceName : ''
    },
    selectedLevelName() {
      const competenceLevel = this.competenceLevels.find(
        (competenceLevel) => competenceLevel.competenceLevelId === this.competenceLevelId,
      )
      return competenceLevel ? competenceLevel.levelName : ''
    },
    inputPlaceholder() {
      if (!this.isCompetenceLevelSelected) {
        return 'Vali enne kompetents ja tase'
      }
      return `Kirjelda, milliseid küsimusi soovid — nt "massiivide ja tsüklite kohta, üks võiks olla tõene/väär"`
    },
  },

  methods: {
    getCompetences() {
      CompetenceService.getCompetencesRequest()
        .then((response) => (this.competences = response.data))
        .catch()
    },
    getCompetenceLevels() {
      this.competenceLevelId = 0
      this.competenceLevels = []

      CompetenceLevelService.getCompetenceLevelsRequest(this.competenceId)
        .then((response) => (this.competenceLevels = response.data))
        .catch()
    },

    sendMessage() {
      if (!this.canSend) {
        return
      }
      const userText = this.userInput.trim()
      this.userInput = ''
      this.errorMessage = ''
      this.addMessage('user', userText)
      this.generateQuestions(userText)
    },

    // Viimane juhis saadetakse eraldi, varasemad sõnumid kaasa, et AI saaks aru täpsustustest
    generateQuestions(userText) {
      this.isLoading = true
      const aiQuestionGenerateRequestDto = {
        competenceLevelId: this.competenceLevelId,
        questionCount: this.questionCount,
        instructions: userText,
        previousMessages: this.conversationHistory.slice(-MAX_HISTORY_MESSAGES),
      }
      this.addToHistory('user', userText)
      AiQuestionService.sendGenerateLevelQuestionsRequest(aiQuestionGenerateRequestDto)
        .then((response) => this.handleGenerateQuestionsResponse(response.data))
        .catch((error) => this.handleRequestError(error))
        .finally(() => (this.isLoading = false))
    },

    handleGenerateQuestionsResponse(generationResponse) {
      if (generationResponse.clarifyingQuestion) {
        this.addToHistory('ai', generationResponse.clarifyingQuestion)
        this.addMessage('ai', generationResponse.clarifyingQuestion)
        return
      }

      this.addToHistory('ai', this.createGeneratedQuestionsSummary(generationResponse.questions))
      const questions = generationResponse.questions.map((question) => ({
        ...question,
        status: 'pending',
      }))
      const text =
        questions.length === 1
          ? 'Koostasin sellise küsimuse. Kas soovid selle ülevaatusele lisada?'
          : `Koostasin ${questions.length} küsimust. Vali, milliseid soovid ülevaatusele lisada.`
      this.addMessage('ai', text, questions)
    },

    // Loodud küsimused lisatakse ajalukku lühidalt, et kasutaja saaks öelda nt "veel üks sama kohta"
    createGeneratedQuestionsSummary(questions) {
      const questionLines = questions.map(
        (question) => `- ${question.questionTypeName}: ${question.title}`,
      )
      return `Koostasin küsimused:\n${questionLines.join('\n')}`
    },

    addToHistory(sender, text) {
      this.conversationHistory.push({ sender, text: text.slice(0, MAX_HISTORY_MESSAGE_LENGTH) })
    },

    addMessage(sender, text, questions = []) {
      this.messages.push({ sender, text, questions })
      this.$nextTick(() => this.scrollToInput())
    },

    scrollToInput() {
      this.$refs.promptInput?.scrollIntoView({ behavior: 'smooth', block: 'end' })
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
        this.errorMessage = backendMessage
        return
      }
      this.errorMessage = 'Midagi läks valesti. Palun proovi uuesti.'
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
  <div class="d-flex flex-column gap-4">
    <div class="card app-surface rounded-4">
      <div class="card-body generator-card-body">
        <h2 class="h5 fw-bold d-flex align-items-center gap-2 mb-1">
          <PhSparkle :size="20" />
          Loo küsimusi AI-ga
        </h2>
        <p class="text-secondary mb-4">
          Vali kompetents ja tase, millele küsimusi lood. AI arvestab valikuga igas vastuses.
        </p>

        <div class="row g-3">
          <div class="col-md-4">
            <label for="selectGeneratorCompetence" class="form-label fw-semibold">
              Kompetents
            </label>
            <select
              id="selectGeneratorCompetence"
              v-model="competenceId"
              class="form-select"
              @change="getCompetenceLevels"
            >
              <option :value="0" disabled>Vali kompetents</option>
              <option
                v-for="competence in competences"
                :key="competence.competenceId"
                :value="competence.competenceId"
              >
                {{ competence.competenceName }}
              </option>
            </select>
          </div>
          <div class="col-md-4">
            <label for="selectGeneratorLevel" class="form-label fw-semibold">Tase</label>
            <select
              id="selectGeneratorLevel"
              v-model="competenceLevelId"
              class="form-select"
              :disabled="competenceId === 0"
            >
              <option :value="0" disabled>
                {{ competenceId === 0 ? 'Vali enne kompetents' : 'Vali tase' }}
              </option>
              <option
                v-for="competenceLevel in competenceLevels"
                :key="competenceLevel.competenceLevelId"
                :value="competenceLevel.competenceLevelId"
              >
                {{ competenceLevel.levelName }}
              </option>
            </select>
          </div>
          <div class="col-md-4">
            <span id="labelQuestionCount" class="form-label fw-semibold d-block">
              Küsimuste arv
            </span>
            <div
              class="btn-group question-count-group w-100"
              role="group"
              aria-labelledby="labelQuestionCount"
            >
              <template v-for="count in questionCountOptions" :key="count">
                <input
                  :id="`questionCount${count}`"
                  v-model="questionCount"
                  type="radio"
                  class="btn-check"
                  name="questionCount"
                  :value="count"
                  autocomplete="off"
                />
                <label class="btn btn-outline-primary fw-bold" :for="`questionCount${count}`">
                  {{ count }}
                </label>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!messages.length" class="card app-surface rounded-4 p-5 text-center">
      <PhSparkle :size="40" class="empty-icon mx-auto mb-3" />
      <h3 class="h5 fw-bold mb-2">Alusta juhisega</h3>
      <p class="text-secondary mb-0">
        Kirjuta allolevasse lahtrisse, millest küsimused võiksid olla. Loodud küsimused saad enne
        salvestamist üle vaadata — salvestatud küsimused ilmuvad vahekaardile „AI küsimused".
      </p>
    </div>

    <template v-for="(message, messageIndex) in messages" :key="messageIndex">
      <div
        class="generator-message d-flex"
        :class="message.sender === 'user' ? 'justify-content-end' : 'justify-content-start'"
      >
        <div class="generator-bubble rounded-4 py-2 px-3" :class="`from-${message.sender}`">
          {{ message.text }}
        </div>
      </div>

      <QuestionBankCard
        v-for="(question, questionIndex) in message.questions"
        :key="`${messageIndex}-${questionIndex}`"
        :status-badge="questionStatusBadges[question.status] ?? null"
        :competence-name="question.competenceName"
        :competence-level-name="question.levelName"
        :question-type-label="
          questionTypeLabels[question.questionTypeName] ?? question.questionTypeName
        "
        is-initially-open
      >
        <template #title>{{ question.title }}</template>
        <template #description>{{ question.description }}</template>
        <template #answers>
          <QuestionAnswerOption
            v-for="(answer, answerIndex) in question.answers"
            :key="answerIndex"
            :answer-text="answer.answerText"
            :correct-choice="answer.isCorrect"
          />
        </template>
        <template v-if="question.status === 'pending' || question.status === 'saving'" #actions>
          <div class="d-flex justify-content-end gap-2">
            <button
              type="button"
              class="btn btn-light fw-bold rounded-2"
              :disabled="question.status === 'saving'"
              @click="rejectQuestion(question)"
            >
              Ei soovi
            </button>
            <button
              type="button"
              class="btn btn-primary fw-bold rounded-2"
              :disabled="question.status === 'saving'"
              @click="saveQuestion(question)"
            >
              {{ question.status === 'saving' ? 'Salvestan…' : 'Lisa ülevaatusele' }}
            </button>
          </div>
        </template>
      </QuestionBankCard>
    </template>

    <div v-if="isLoading" class="generator-message d-flex justify-content-start">
      <div class="generator-bubble from-ai rounded-4 py-2 px-3 text-secondary fst-italic">
        AI mõtleb…
      </div>
    </div>

    <div ref="promptInput" class="card app-surface rounded-4">
      <div class="card-body generator-card-body">
        <AlertDanger :error-message="errorMessage" class="mb-3" />
        <div v-if="isCompetenceLevelSelected" class="d-flex flex-wrap gap-2 mb-3">
          <span class="badge rounded-pill badge-neutral">{{ selectedLevelName }}</span>
          <span class="badge rounded-pill badge-competence">{{ selectedCompetenceName }}</span>
          <span class="badge rounded-pill badge-neutral">
            {{ questionCount }} {{ questionCount === 1 ? 'küsimus' : 'küsimust' }}
          </span>
        </div>
        <label for="inputGeneratorPrompt" class="visually-hidden">Juhis AI-le</label>
        <div class="d-flex gap-2 align-items-end">
          <textarea
            id="inputGeneratorPrompt"
            v-model="userInput"
            class="form-control"
            rows="3"
            :maxlength="maxInputLength"
            :placeholder="inputPlaceholder"
            :disabled="!isCompetenceLevelSelected"
            @keydown.enter="handleEnterKey"
          ></textarea>
          <button
            type="button"
            class="btn btn-primary rounded-2 send-button"
            :disabled="!canSend"
            aria-label="Saada juhis"
            @click="sendMessage"
          >
            <PhPaperPlaneRight :size="20" />
          </button>
        </div>
        <div class="d-flex justify-content-between small text-secondary mt-1">
          <span>Enter saadab, Shift+Enter teeb uue rea</span>
          <span>{{ userInput.length }}/{{ maxInputLength }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.generator-card-body {
  padding: 1.75rem 2rem;
}

.question-count-group .btn {
  flex: 1 1 0;
}

.generator-bubble {
  max-width: 75%;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.generator-bubble.from-user {
  background: var(--brand-slate);
  color: #fff;
}

.generator-bubble.from-ai {
  background: var(--bs-primary-bg-subtle);
}

.empty-icon {
  color: var(--bs-primary);
}

/* Samad mullid nagu QuestionBankCard-is */
.badge-competence {
  background-color: var(--bs-info-bg-subtle);
  color: var(--bs-info-text-emphasis);
  border: var(--bs-border-width) solid var(--bs-info-border-subtle);
}

.badge-neutral {
  background-color: var(--bs-gray-100);
  color: var(--bs-gray-700);
  border: var(--bs-border-width) solid var(--bs-gray-300);
}

.send-button {
  height: 3rem;
  width: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
</style>
