<script>
import QuestionService from '@/services/QuestionService.js'
import CompetenceService from '@/services/CompetenceService.js'
import NavigationService from '@/services/NavigationService.js'
import Status from '@/Status.js'
import LoadingText from '@/components/LoadingText.vue'
import SuccessToast from '@/components/SuccessToast.vue'
import AlertDanger from '@/components/AlertDanger.vue'
import QuestionBankCard from '@/components/QuestionBankCard.vue'
import { PhCaretDown, PhCheckCircle, PhPencilSimple, PhPlus, PhTrash } from '@phosphor-icons/vue'

export default {
  name: 'QuestionBankView',
  components: {
    LoadingText,
    SuccessToast,
    AlertDanger,
    QuestionBankCard,
    PhCaretDown,
    PhCheckCircle,
    PhPencilSimple,
    PhPlus,
    PhTrash,
  },

  beforeMount() {
    this.getCompetences()
    this.getQuestionBank()
  },

  data() {
    return {
      isLoading: true,
      selectedCompetenceId: null,
      questionToDelete: null,
      isDeleting: false,
      successMessage: '',
      deleteErrorMessage: '',
      questionToEdit: null,
      isSaving: false,
      editErrorMessage: '',
      questionUpdateRequestDto: {
        questionTitle: '',
        questionDescription: '',
        questionStatus: '',
      },
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
    navigateToQuestionCreate() {
      NavigationService.navigateToQuestionCreate()
    },
    openEditForm(question) {
      this.successMessage = ''
      this.editErrorMessage = ''
      this.questionToEdit = question
      this.questionUpdateRequestDto = {
        questionTitle: question.questionTitle,
        questionDescription: question.questionDescription,
        questionStatus: question.questionStatus,
      }
    },
    closeEditForm() {
      this.questionToEdit = null
    },
    updateQuestion() {
      if (!this.isEditFormValid()) {
        this.editErrorMessage = 'Pealkiri ja kirjeldus peavad olema täidetud'
        return
      }
      this.isSaving = true
      QuestionService.updateQuestionRequest(
        this.questionToEdit.questionId,
        this.questionUpdateRequestDto,
      )
        .then(() => this.handleUpdateQuestion())
        .catch(() => (this.editErrorMessage = 'Küsimuse muutmine ebaõnnestus. Proovi uuesti.'))
        .finally(() => (this.isSaving = false))
    },
    isEditFormValid() {
      return (
        this.questionUpdateRequestDto.questionTitle.trim() !== '' &&
        this.questionUpdateRequestDto.questionDescription.trim() !== ''
      )
    },
    handleUpdateQuestion() {
      this.successMessage = `Küsimus "${this.questionUpdateRequestDto.questionTitle}" on muudetud`
      this.closeEditForm()
      this.getQuestionBank()
    },
    openDeleteConfirmation(question) {
      this.successMessage = ''
      this.deleteErrorMessage = ''
      this.questionToDelete = question
    },
    closeDeleteConfirmation() {
      this.questionToDelete = null
    },
    deleteQuestion() {
      this.isDeleting = true
      QuestionService.deleteQuestionRequest(this.questionToDelete.questionId)
        .then(() => this.handleDeleteQuestion())
        .catch(() => (this.deleteErrorMessage = 'Küsimuse kustutamine ebaõnnestus. Proovi uuesti.'))
        .finally(() => (this.isDeleting = false))
    },
    handleDeleteQuestion() {
      this.successMessage = `Küsimus "${this.questionToDelete.questionTitle}" on kustutatud`
      this.closeDeleteConfirmation()
      this.getQuestionBank()
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
            @click="navigateToQuestionCreate"
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
          <template #menu>
            <li>
              <button
                class="dropdown-item d-flex align-items-center gap-2"
                @click="openEditForm(question)"
              >
                <PhPencilSimple :size="16" />
                Muuda
              </button>
            </li>
            <template v-if="question.questionStatus !== 'I'">
              <li><hr class="dropdown-divider" /></li>
              <li>
                <button
                  class="dropdown-item d-flex align-items-center gap-2 text-danger"
                  @click="openDeleteConfirmation(question)"
                >
                  <PhTrash :size="16" />
                  Kustuta
                </button>
              </li>
            </template>
          </template>
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

    <template v-if="questionToEdit">
      <div class="modal d-block" tabindex="-1" role="dialog" aria-modal="true">
        <div class="modal-dialog modal-dialog-centered modal-lg">
          <form class="modal-content rounded-4 border-0 shadow" @submit.prevent="updateQuestion">
            <div class="modal-body p-4">
              <h5 class="fw-bold mb-4">Muuda küsimust</h5>
              <AlertDanger :error-message="editErrorMessage" class="mb-3" />

              <div class="mb-3">
                <label for="edit-question-title" class="form-label fw-semibold">Pealkiri</label>
                <input
                  id="edit-question-title"
                  v-model="questionUpdateRequestDto.questionTitle"
                  type="text"
                  class="form-control"
                  maxlength="100"
                />
                <div class="form-text text-end">
                  {{ questionUpdateRequestDto.questionTitle.length }} / 100
                </div>
              </div>

              <div class="mb-3">
                <label for="edit-question-description" class="form-label fw-semibold">
                  Kirjeldus
                </label>
                <textarea
                  id="edit-question-description"
                  v-model="questionUpdateRequestDto.questionDescription"
                  class="form-control"
                  rows="6"
                  maxlength="1000"
                ></textarea>
                <div class="form-text text-end">
                  {{ questionUpdateRequestDto.questionDescription.length }} / 1000
                </div>
              </div>

              <div class="mb-3">
                <label for="edit-question-status" class="form-label fw-semibold">Staatus</label>
                <select
                  id="edit-question-status"
                  v-model="questionUpdateRequestDto.questionStatus"
                  class="form-select"
                >
                  <option value="A">Aktiivne</option>
                  <option value="I">Mitteaktiivne</option>
                </select>
              </div>

              <p class="text-secondary small mb-0">
                Vastusevariante muuta ei saa, et olemasolevad testitulemused jääksid õigeks.
              </p>
            </div>
            <div class="modal-footer border-0 px-4 pb-4 pt-0">
              <button
                type="button"
                class="btn btn-light fw-bold rounded-2"
                :disabled="isSaving"
                @click="closeEditForm"
              >
                Tühista
              </button>
              <button type="submit" class="btn btn-primary fw-bold rounded-2" :disabled="isSaving">
                Salvesta
              </button>
            </div>
          </form>
        </div>
      </div>
      <div class="modal-backdrop fade show"></div>
    </template>

    <template v-if="questionToDelete">
      <div
        class="modal d-block"
        tabindex="-1"
        role="dialog"
        aria-modal="true"
        @click.self="closeDeleteConfirmation"
      >
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content rounded-4 border-0 shadow">
            <div class="modal-body p-4">
              <h5 class="fw-bold mb-3">Kustuta küsimus?</h5>
              <p class="mb-2">
                <strong>{{ questionToDelete.questionTitle }}</strong>
              </p>
              <AlertDanger :error-message="deleteErrorMessage" class="mb-3" />
              <p class="text-secondary mb-0">
                Küsimus muutub mitteaktiivseks ja seda ei saa enam uutele testidele lisada.
                Olemasolevad testid ja tulemused jäävad alles.
              </p>
            </div>
            <div class="modal-footer border-0 px-4 pb-4 pt-0">
              <button
                class="btn btn-light fw-bold rounded-2"
                :disabled="isDeleting"
                @click="closeDeleteConfirmation"
              >
                Tühista
              </button>
              <button
                class="btn btn-danger fw-bold rounded-2 d-flex align-items-center gap-2"
                :disabled="isDeleting"
                @click="deleteQuestion"
              >
                <PhTrash :size="16" />
                Kustuta
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-backdrop fade show"></div>
    </template>

    <SuccessToast :success-message="successMessage" @event-close="successMessage = ''" />
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
</style>
