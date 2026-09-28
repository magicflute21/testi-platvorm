<script>
import { PhPlus } from '@phosphor-icons/vue'
import CompetenceService from '@/services/CompetenceService.js'
import CompetenceLevelService from '@/services/CompetenceLevelService.js'
import QuestionService from '@/services/QuestionService.js'

export default {
  name: 'TestCreateView',
  components: { PhPlus },

  beforeMount() {
    this.getCompetences()
  },

  data() {
    return {
      errorMessage: '',

      competences: [],
      competenceId: 0,

      competenceLevels: [],
      competenceLevelId: 0,

      questions: [],
      selectedQuestionIds: [0],

      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },

  computed: {
    canAddQuestionRow() {
      const lastQuestionId = this.selectedQuestionIds[this.selectedQuestionIds.length - 1]
      return lastQuestionId !== 0 && this.selectedQuestionIds.length < this.questions.length
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
        this.selectedQuestionIds = [0] // always keep one row
      } else {
        this.selectedQuestionIds.splice(index, 1)
      }
    },
  },
}
</script>

<template>
  <div>
    <div class="container text-start col-6 mt-5 mb-5">
      <h1>Loo uus test</h1>
      <p class="mb-5">Täida kõik väljad ja loo uus test.</p>

      <form>
        <div class="row mb-3">
          <label for="inputTestHeading" class="col-sm-3 col-form-label">Pealkiri</label>
          <div class="col-sm-9">
            <input
              type="text"
              class="form-control"
              id="inputTestHeading"
              placeholder="Sisesta testi pealkiri"
            />
          </div>
        </div>
        <div class="row mb-3">
          <label for="inputTestShortDescription" class="col-sm-3 col-form-label"
            >Lühikirjeldus</label
          >
          <div class="col-sm-9">
            <input
              type="text"
              class="form-control"
              id="inputTestShortDescription"
              placeholder="Lisa testi lühikirjeldus"
            />
          </div>
        </div>
        <div class="row mb-3">
          <label for="inputTestDescription" class="col-sm-3 col-form-label">Kirjeldus</label>
          <div class="col-sm-9">
            <textarea class="form-control" rows="4" placeholder="Lisa testi kirjeldus"></textarea>
          </div>
        </div>

        <div class="row mb-3">
          <label for="selectTestCompetence" class="col-sm-3 col-form-label">Kompetents</label>
          <div class="col-sm-5">
            <select v-model="competenceId" class="form-select" @change="getCompetenceLevels">
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

        <div class="row mb-3">
          <label for="selectTestLevel" class="col-sm-3 col-form-label">Tase</label>
          <div class="col-sm-5">
            <select
              v-model="competenceLevelId"
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

        <div class="row mb-3">
          <label for="selectTestPassPercent" class="col-sm-3 col-form-label">Läbimise %</label>
          <div class="col-sm-5">
            <select class="form-select">
              <option selected>Vali läbimise %</option>
              <option>50%</option>
              <option>60%</option>
              <option>70%</option>
              <option>80%</option>
              <option>90%</option>
            </select>
          </div>
        </div>

        <fieldset class="row mb-3">
          <legend class="col-form-label col-sm-3 pt-0 text-nowrap">Punktide ümardamine</legend>
          <div class="col-sm-9">
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="roundScore" value="true" />
              <label class="form-check-label" for="radioRoundScoreUp">Üles</label>
            </div>
            <div class="form-check form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                name="roundScore"
                id="radioRoundScoreDown"
                value="false"
              />
              <label class="form-check-label" for="radioRoundScoreDown">Alla</label>
            </div>
          </div>
        </fieldset>

        <div class="row mb-3">
          <label for="selectTestTimer" class="col-sm-3 col-form-label">Taimer</label>
          <div class="col-sm-5">
            <select id="selectTestTimer" class="form-select">
              <option selected>Ilma taimerita</option>
              <option>15 min</option>
              <option>30 min</option>
              <option>45 min</option>
              <option>60 min</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
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
                v-if="questionId !== 0"
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

        <div class="d-flex justify-content-center gap-3 mt-4">
          <button type="button" class="btn btn-link text-body text-decoration-none">Tühista</button>
          <button type="submit" class="btn btn-info text-white">Loo test</button>
        </div>
      </form>
    </div>
  </div>
</template>
