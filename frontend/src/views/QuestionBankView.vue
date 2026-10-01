<script>
import QuestionService from '@/services/QuestionService.js'
import CompetenceService from '@/services/CompetenceService.js'
import Status from '@/Status.js'
import LoadingText from '@/components/LoadingText.vue'
import QuestionBankCard from '@/components/QuestionBankCard.vue'
import BaseModal from '@/components/modal/BaseModal.vue'
import QuestionCreateForm from '@/components/QuestionCreateForm.vue'
import { PhCaretDown, PhCheckCircle, PhPlus } from '@phosphor-icons/vue'

export default {
  name: 'QuestionBankView',
  components: {
    LoadingText,
    QuestionBankCard,
    BaseModal,
    QuestionCreateForm,
    PhCaretDown,
    PhCheckCircle,
    PhPlus,
  },

  beforeMount() {
    this.getCompetences()
    this.getQuestionBank()
  },

  data() {
    return {
      isLoading: true,
      isQuestionCreateModalOpen: false,
      isQuestionCreatedModalOpen: false,
      createdQuestionTitle: '',
      selectedCompetenceId: null,
      questionStatus: Status,
      questionTypeLabels: {
        SINGLE_CHOICE: 'Ühe õige vastusega',
        MULTIPLE_CHOICE: 'Mitme õige vastusega',
        TRUE_FALSE: 'Tõene või väär',
      },
      questions: [
        {
          questionId: 0,
          questionTitle: '',
          questionDescription: '',
          questionTypeName: '',
          competenceId: 0,
          competenceName: '',
          competenceLevelName: '',
          score: 0,
          questionStatus: '',
          answers: [
            {
              questionAnswerId: 0,
              answerText: '',
              correctChoice: false,
            },
          ],
        },
      ],
      competences: [
        {
          competenceId: 0,
          competenceName: '',
        },
      ],
    }
  },

  computed: {
    selectedCompetenceName() {
      const selectedCompetence = this.competences.find(
        (competence) => competence.competenceId === this.selectedCompetenceId,
      )
      return selectedCompetence ? selectedCompetence.competenceName : 'Kõik kompetentsid'
    },
  },

  methods: {
    getCompetences() {
      CompetenceService.getCompetencesRequest()
        .then((response) => this.handleGetCompetences(response))
        .catch()
    },
    handleGetCompetences(response) {
      this.competences = response.data
    },
    selectCompetence(competenceId) {
      this.selectedCompetenceId = competenceId
      this.getQuestionBank()
    },
    openQuestionCreateModal() {
      this.isQuestionCreateModalOpen = true
    },
    closeQuestionCreateModal() {
      this.isQuestionCreateModalOpen = false
    },
    handleQuestionCreated(createdQuestion) {
      this.createdQuestionTitle = createdQuestion.title
      this.isQuestionCreateModalOpen = false
      this.isQuestionCreatedModalOpen = true
      this.getQuestionBank()
    },
    addNewQuestion() {
      this.isQuestionCreatedModalOpen = false
      this.isQuestionCreateModalOpen = true
    },
    closeQuestionCreatedModal() {
      this.isQuestionCreatedModalOpen = false
    },
    getQuestionBank() {
      QuestionService.getQuestionBankRequest(this.selectedCompetenceId)
        .then((response) => this.handleGetQuestionBank(response))
        .catch()
        .finally(() => (this.isLoading = false))
    },
    handleGetQuestionBank(response) {
      this.questions = response.data
    },
  },
}
</script>

<template>
  <div class="container-fluid py-4">
    <LoadingText v-if="isLoading" />
    <div v-else class="question-bank-content mx-auto">
      <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-5">
        <h1 class="text-uppercase fw-bold h3 mb-0">Küsimuste pank</h1>
        <div class="d-flex flex-wrap gap-2">
          <div class="dropdown">
            <button
              class="btn btn-light border fw-bold rounded-2 d-flex align-items-center gap-2"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <span>{{ selectedCompetenceName }}</span>
              <PhCaretDown :size="14" />
            </button>
            <ul class="dropdown-menu dropdown-menu-custom dropdown-menu-end">
              <li>
                <button
                  class="dropdown-item"
                  :class="{ active: selectedCompetenceId === null }"
                  @click="selectCompetence(null)"
                >
                  Kõik kompetentsid
                </button>
              </li>
              <li><hr class="dropdown-divider" /></li>
              <li v-for="competence in competences" :key="competence.competenceId">
                <button
                  class="dropdown-item"
                  :class="{ active: selectedCompetenceId === competence.competenceId }"
                  @click="selectCompetence(competence.competenceId)"
                >
                  {{ competence.competenceName }}
                </button>
              </li>
            </ul>
          </div>
          <button
            class="btn btn-primary fw-bold rounded-2 d-flex align-items-center gap-2"
            @click="openQuestionCreateModal"
          >
            <span>Lisa uus küsimus</span>
            <PhPlus :size="16" weight="bold" />
          </button>
        </div>
      </div>

      <div v-if="!questions.length" class="card shadow rounded-4 p-5 text-center text-secondary">
        Küsimusi ei leitud
      </div>

      <div class="d-flex flex-column gap-4">
        <QuestionBankCard
          v-for="question in questions"
          :key="question.questionId"
          :status-badge="questionStatus[question.questionStatus]"
          :competence-name="question.competenceName"
          :question-type-label="questionTypeLabels[question.questionTypeName]"
          :competence-level-name="question.competenceLevelName"
          :score="question.score"
        >
          <template #title>{{ question.questionTitle }}</template>
          <template #description>{{ question.questionDescription }}</template>
          <template #answers>
            <div
              v-for="answer in question.answers"
              :key="answer.questionAnswerId"
              class="answer-option d-flex align-items-center gap-3 rounded-3 py-2 px-3"
              :class="{ correct: answer.correctChoice }"
            >
              <span class="answer-text flex-grow-1">{{ answer.answerText }}</span>
              <span v-if="answer.correctChoice" class="answer-correct flex-shrink-0">
                <PhCheckCircle :size="20" weight="fill" />
                <span class="visually-hidden">Õige vastus</span>
              </span>
            </div>
          </template>
        </QuestionBankCard>
      </div>
    </div>

    <BaseModal
      :is-open="isQuestionCreateModalOpen"
      size="lg"
      @event-modal-closed="closeQuestionCreateModal"
    >
      <template #title>Loo uus küsimus</template>
      <template #body>
        <QuestionCreateForm
          @event-cancel="closeQuestionCreateModal"
          @event-question-created="handleQuestionCreated"
        />
      </template>
    </BaseModal>

    <BaseModal
      :is-open="isQuestionCreatedModalOpen"
      @event-modal-closed="closeQuestionCreatedModal"
    >
      <template #title>Küsimus lisatud</template>
      <template #body>
        <div class="text-center py-2">
          <PhCheckCircle :size="56" weight="fill" class="created-icon mb-3" />
          <p class="mb-1">Küsimus on edukalt lisatud!</p>
          <p class="created-title fw-bold mb-0">„{{ createdQuestionTitle }}"</p>
        </div>
      </template>
      <template #buttons>
        <button
          type="button"
          class="btn btn-light rounded-3 px-4"
          @click="closeQuestionCreatedModal"
        >
          Sulge
        </button>
        <button
          type="button"
          class="btn btn-primary rounded-3 px-4 fw-bold"
          @click="addNewQuestion"
        >
          Lisa uus
        </button>
      </template>
    </BaseModal>
  </div>
</template>

<style scoped>
.dropdown-menu-custom {
  --bs-dropdown-min-width: 12rem;
  --bs-dropdown-bg: var(--bs-gray-100);
  --bs-dropdown-border-radius: 0.75rem;
  --bs-dropdown-link-color: var(--bs-black);
}

@media (min-width: 992px) {
  .question-bank-content {
    width: 70%;
  }
}

.answer-option {
  border: 1px solid var(--bs-border-color);
  background-color: var(--bs-gray-100);
}

.answer-option.correct {
  border-color: var(--bs-success);
  background-color: var(--bs-success-bg-subtle);
}

.answer-text {
  min-width: 0;
  overflow-wrap: anywhere;
}

.answer-correct {
  color: var(--bs-success);
}

.created-icon {
  color: var(--bs-success);
}

.created-title {
  color: var(--brand-slate);
  overflow-wrap: anywhere;
}
</style>
