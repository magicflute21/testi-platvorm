<script>
import { PhPlus, PhStack, PhStairs, PhTarget, PhTimer } from '@phosphor-icons/vue'
import CompetenceService from '@/services/CompetenceService.js'
import CompetenceLevelService from '@/services/CompetenceLevelService.js'
import QuestionService from '@/services/QuestionService.js'
import TestCreateService from '@/services/TestCreateService.js'
import AlertDanger from '@/components/AlertDanger.vue'
import AlertSuccess from '@/components/AlertSuccess.vue'

export default {
  name: 'TestCreateView',
  components: {
    AlertSuccess,
    PhPlus,
    AlertDanger,
    PhStack,
    PhStairs,
    PhTarget,
    PhTimer,
  },

  beforeMount() {
    this.getCompetences()
  },

  data() {
    return {
      errorMessage: '',
      successMessage: '',

      testName: '',
      testShortDescription: '',
      testDescription: '',
      // Andmebaasi veergude piirid (test.name varchar(255), test.short_description varchar(150))
      testNameMaxLength: 255,
      testShortDescriptionMaxLength: 150,
      competences: [],
      competenceId: 0,

      competenceLevels: [],
      competenceLevelId: 0,
      roundScoreUp: true,
      timerMin: null,
      passPercent: 0,
      questions: [],
      selectedQuestionIds: [0],
    }
  },

  computed: {
    canAddQuestionRow() {
      const lastQuestionId = this.selectedQuestionIds[this.selectedQuestionIds.length - 1]
      return lastQuestionId !== 0 && this.selectedQuestionIds.length < this.questions.length
    },
    isTimed() {
      return this.timerMin > 0
    },
    chosenQuestionIds() {
      return this.selectedQuestionIds.filter((id) => id !== 0)
    },
  },

  methods: {
    getCompetences() {
      CompetenceService.getCompetencesRequest()
        .then((response) => (this.competences = response.data))
        .catch()
        .finally()
    },
    getCompetenceLevels() {
      this.competenceLevelId = 0
      this.questions = []
      this.selectedQuestionIds = [0]

      CompetenceLevelService.getCompetenceLevelsRequest(this.competenceId)
        .then((response) => (this.competenceLevels = response.data))
        .catch()
        .finally()
    },
    getQuestions() {
      this.selectedQuestionIds = [0]

      QuestionService.getQuestionsRequest(this.competenceLevelId)
        .then((response) => (this.questions = response.data))
        .catch()
        .finally()
    },
    isSelectedInOtherRow(questionId, index) {
      return this.selectedQuestionIds.some((id, i) => i !== index && id === questionId)
    },
    addQuestionRow() {
      this.selectedQuestionIds.push(0)
    },
    removeQuestionRow(index) {
      if (this.selectedQuestionIds.length === 1) {
        this.selectedQuestionIds = [0]
      } else {
        this.selectedQuestionIds.splice(index, 1)
      }
    },
    createTest() {
      this.resetErrorMessage()
      this.checkTestFormForErrors()

      if (this.errorMessageIsEmpty()) {
        const test = {
          userId: sessionStorage.getItem('userId'),
          competenceId: this.competenceId,
          competenceLevelId: this.competenceLevelId,
          testName: this.testName,
          testShortDescription: this.testShortDescription,
          testDescription: this.testDescription,
          isTimed: this.isTimed,
          timerMin: this.timerMin,
          passPercent: this.passPercent,
          roundScoreUp: this.roundScoreUp,
          questions: this.chosenQuestionIds.map((id) => ({ questionId: id })),
        }

        TestCreateService.postNewTest(test)
          .then(() => this.handleCreateTestResponse())
          .catch((error) => this.handleTestCreateError(error))
      }
    },

    handleCreateTestResponse() {
      this.successMessage = 'Test "' + this.testName + '" on edukalt lisatud!'
      this.resetAllFields()
    },

    handleTestCreateError(error) {
      this.errorMessage = error.response?.data?.message ?? 'Testi loomine ebaõnnestus'
    },

    checkTestFormForErrors() {
      if (this.testName === '') {
        this.errorMessage = 'Lisa testi nimi'
      } else if (this.testShortDescription === '') {
        this.errorMessage = 'Lisa testi lühikirjeldus'
      } else if (this.testDescription === '') {
        this.errorMessage = 'Lisa testi kirjeldus'
      } else if (this.competenceId === 0) {
        this.errorMessage = 'Vali testile kompetents'
      } else if (this.competenceLevelId === 0) {
        this.errorMessage = 'Lisa testile kompetentsi tase'
      } else if (this.passPercent === 0) {
        this.errorMessage = 'Lisa testile läbimise %'
      } else if (this.chosenQuestionIds.length === 0) {
        this.errorMessage = 'Lisa testile vähemalt 1 küsimus'
      }
    },
    resetAllFields() {
      this.testName = ''
      this.testShortDescription = ''
      this.testDescription = ''
      this.competenceId = 0
      this.competenceLevelId = 0
      this.passPercent = 0
      this.timerMin = null
      this.selectedQuestionIds = [0]
    },
    errorMessageIsEmpty() {
      return this.errorMessage === ''
    },
    resetErrorMessage() {
      this.errorMessage = ''
    },
  },
}
</script>

<template>
  <div class="container d-flex flex-column align-items-center">
    <div class="card shadow p-4 my-4 create-card">
      <div class="card-body">
      <h1 class="text-uppercase fw-bold h3">Loo uus test</h1>
      <p class="text-body-secondary mt-3 mb-4">
        Pane kokku test, mis aitab kasutajatel oma oskusi proovile panna. Vali kompetents ja tase,
        lisa küsimused ning määra, kui palju on vaja testi läbimiseks õigesti vastata. Kõik väljad
        on kohustuslikud.
      </p>

      <form>
        <div class="row mb-4">
          <label for="inputTestName" class="col-sm-3 col-form-label">Pealkiri</label>
          <div class="col-sm-9 counter-field">
            <input
              v-model="testName"
              type="text"
              class="form-control"
              id="inputTestName"
              :maxlength="testNameMaxLength"
              placeholder="Sisesta testi pealkiri"
            />
            <span class="char-counter">{{ testName.length }} / {{ testNameMaxLength }}</span>
          </div>
        </div>
        <div class="row mb-4">
          <label for="inputTestShortDescription" class="col-sm-3 col-form-label"
            >Lühikirjeldus</label
          >
          <div class="col-sm-9 counter-field">
            <input
              v-model="testShortDescription"
              type="text"
              class="form-control"
              id="inputTestShortDescription"
              :maxlength="testShortDescriptionMaxLength"
              placeholder="Lisa testi lühikirjeldus"
            />
            <span class="char-counter">
              {{ testShortDescription.length }} / {{ testShortDescriptionMaxLength }}
            </span>
          </div>
        </div>
        <div class="row mb-4">
          <label for="inputTestDescription" class="col-sm-3 col-form-label">Kirjeldus</label>
          <div class="col-sm-9">
            <textarea
              v-model="testDescription"
              class="form-control"
              id="inputTestDescription"
              rows="4"
              placeholder="Lisa testi kirjeldus"
            ></textarea>
          </div>
        </div>

        <div class="setting-grid mb-4">
          <div class="setting-box d-flex align-items-center gap-4 rounded-3 py-3 px-4">
            <PhStack :size="28" class="text-primary flex-shrink-0" />
            <div class="flex-grow-1 min-w-0">
              <label for="selectTestCompetence" class="setting-label">Kompetents</label>
              <select
                v-model="competenceId"
                id="selectTestCompetence"
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
          </div>

          <div class="setting-box d-flex align-items-center gap-4 rounded-3 py-3 px-4">
            <PhStairs :size="28" class="text-primary flex-shrink-0" />
            <div class="flex-grow-1 min-w-0">
              <label for="selectTestLevel" class="setting-label">Tase</label>
              <select
                v-model="competenceLevelId"
                id="selectTestLevel"
                class="form-select"
                :disabled="competenceId === 0"
                @change="getQuestions"
              >
                <option :value="0" disabled>Vali tase</option>
                <option
                  v-for="competenceLevel in competenceLevels"
                  :key="competenceLevel.competenceLevelId"
                  :value="competenceLevel.competenceLevelId"
                >
                  {{ competenceLevel.levelName }}
                </option>
              </select>
            </div>
          </div>

          <div class="setting-box d-flex align-items-center gap-4 rounded-3 py-3 px-4">
            <PhTarget :size="28" class="text-primary flex-shrink-0" />
            <div class="flex-grow-1 min-w-0">
              <label for="selectTestPassPercent" class="setting-label">Läbimise %</label>
              <select
                v-model="passPercent"
                id="selectTestPassPercent"
                class="form-select"
              >
                <option :value="0" disabled>Vali läbimise %</option>
                <option :value="50">50%</option>
                <option :value="60">60%</option>
                <option :value="70">70%</option>
                <option :value="80">80%</option>
                <option :value="90">90%</option>
              </select>
            </div>
          </div>

          <div class="setting-box d-flex align-items-center gap-4 rounded-3 py-3 px-4">
            <PhTimer :size="28" class="text-primary flex-shrink-0" />
            <div class="flex-grow-1 min-w-0">
              <label for="selectTestTimer" class="setting-label">Taimer</label>
              <select v-model="timerMin" id="selectTestTimer" class="form-select">
                <option :value="null">Ilma taimerita</option>
                <option :value="15">15 min</option>
                <option :value="30">30 min</option>
                <option :value="45">45 min</option>
                <option :value="60">60 min</option>
                <option :value="90">90 min</option>
                <option :value="120">120 min</option>
              </select>
            </div>
          </div>
        </div>

        <fieldset class="row mb-4">
          <legend class="col-form-label col-sm-3 pt-0 text-nowrap">Punktide ümardamine</legend>
          <div class="col-sm-9">
            <div class="form-check form-check-inline">
              <input
                v-model="roundScoreUp"
                :value="true"
                class="form-check-input"
                type="radio"
                name="roundScoreRadioInput"
                id="radioRoundScoreUp"
              />
              <label class="form-check-label" for="radioRoundScoreUp">Üles</label>
            </div>
            <div class="form-check form-check-inline">
              <input
                v-model="roundScoreUp"
                :value="false"
                class="form-check-input"
                type="radio"
                name="roundScoreRadioInput"
                id="radioRoundScoreDown"
              />
              <label class="form-check-label" for="radioRoundScoreDown">Alla</label>
            </div>
          </div>
        </fieldset>

        <div class="row mb-4">
          <label for="selectTestQuestion" class="col-sm-3 col-form-label">Küsimused</label>
          <div class="col-sm-9">
            <div
              v-for="(questionId, index) in selectedQuestionIds"
              :key="index"
              class="d-flex align-items-center gap-2 mb-2"
            >
              <select
                :id="index === 0 ? 'selectTestQuestion' : undefined"
                v-model="selectedQuestionIds[index]"
                class="form-select"
                :disabled="competenceLevelId === 0"
              >
                <option :value="0" disabled>Vali küsimus</option>
                <option
                  v-for="question in questions"
                  :key="question.questionId"
                  :value="question.questionId"
                  :disabled="isSelectedInOtherRow(question.questionId, index)"
                >
                  {{ question.questionTitle }}
                </option>
              </select>
              <button
                v-if="questionId !== 0 || selectedQuestionIds.length > 1"
                type="button"
                class="btn btn-link text-body text-decoration-none"
                title="Eemalda küsimus"
                @click="removeQuestionRow(index)"
              >
                ×
              </button>
            </div>

            <button
              type="button"
              class="btn btn-outline-secondary w-100"
              title="Lisa küsimus"
              :disabled="!canAddQuestionRow"
              @click="addQuestionRow"
            >
              <PhPlus :size="16" />
            </button>
          </div>
        </div>

        <div class="mt-4">
          <AlertDanger :error-message="errorMessage" />
          <AlertSuccess :success-message="successMessage" />
        </div>

        <div class="d-flex justify-content-center mt-4">
          <button type="button" @click="createTest" class="btn btn-primary fw-bold rounded-2 px-5">
            Loo test
          </button>
        </div>
      </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Sama kaardistiil nagu testi vaatel ja testi alustamise lehel */
.create-card {
  width: 100%;
  max-width: 48rem;
  border: none;
  border-radius: 1rem;
}

/* Tähemärkide loendur lahtri sees paremal; padding-right hoiab teksti loendurist eemal */
.counter-field {
  position: relative;
}

.counter-field .form-control {
  padding-right: 5.5rem;
}

.char-counter {
  position: absolute;
  top: 50%;
  right: calc(var(--bs-gutter-x) * 0.5 + 0.75rem);
  transform: translateY(-50%);
  font-size: 0.75rem;
  color: var(--bs-secondary-color);
  pointer-events: none;
}

/* Väiksemad seaded infokastidena — sama stiil nagu testi vaate infokastidel */
.setting-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(min(14rem, 100%), 1fr));
  gap: 1rem;
}

.setting-box {
  background-color: rgba(var(--bs-primary-rgb), 0.06);
  min-width: 0;
}

/* Valikuväli ei veni üle kogu kasti laiuse */
.setting-box .form-select {
  max-width: 12rem;
}

.setting-label {
  display: block;
  margin-bottom: 0.25rem;
  font-size: 0.875rem;
  color: var(--bs-secondary-color);
}

.min-w-0 {
  min-width: 0;
}
</style>
