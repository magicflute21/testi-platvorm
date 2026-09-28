<script>
import NavigationService from '@/services/NavigationService.js'
import TestStartService from '@/services/TestStartService.js'
import LoadingText from '@/components/LoadingText.vue'
import { PhTimer, PhInfinity, PhPlay, PhWarningCircle } from '@phosphor-icons/vue'

export default {
  name: 'TestStartView',
  components: { LoadingText, PhTimer, PhInfinity, PhPlay, PhWarningCircle },
  props: {
    testId: { type: Number, required: true },
  },
  data() {
    return {
      isLoading: true,
      errorMessage: '',
      test: {
        title: '',
        description: '',
        shortDescription: '',
        competence: '',
        competenceLevel: '',
        status: '',
        testId: '',
        timerMin: '',
        isTimed: '',
      },
      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },
  methods: {
    goToTestAttempt() {
      NavigationService.navigateToTestAttempt(this.testId)
    },
    getTestStart() {
      TestStartService.getTestStart(this.testId)
        .then((response) => (this.test = response.data))
        .catch((error) => this.handleGetTestStartErrorResponse(error))
        .finally(() => (this.isLoading = false))
    },
    handleGetTestStartErrorResponse(error) {
      this.errorResponse = error.response.data
      console.log(error.response)
      if (
        error.response.status === 404 &&
        this.errorResponse.errorCode === 'PRIMARY_KEY_NOT_FOUND'
      ) {
        this.errorMessage = this.errorResponse.message
        this.test = {}
      }
    },
  },
  beforeMount() {
    this.getTestStart()
  },
}
</script>

<template>
  <div class="container d-flex flex-column align-items-center">
    <LoadingText v-if="isLoading" />

    <div v-else-if="errorMessage.length" class="card my-4 shadow-sm p-4 text-center start-card">
      <div class="card-body">
        <PhWarningCircle :size="48" class="text-secondary mb-3" />
        <h1 class="h4 fw-semibold mb-2">Testi ei leitud</h1>
        <p class="text-secondary mb-0">{{ errorMessage }}</p>
      </div>
    </div>

    <div v-else class="card my-4 shadow p-4 start-card">
      <div class="card-body">
        <p class="eyebrow text-primary mb-2">{{ test.competence }}</p>
        <h1 class="h2 fw-bold mb-3">{{ test.title }}</h1>

        <div class="d-flex flex-wrap gap-2 mb-4">
          <span class="badge rounded-pill text-bg-light border">
            {{ test.competenceLevel }}
          </span>
        </div>

        <p class="lead text-body-secondary mb-4">{{ test.shortDescription }}</p>

        <hr class="my-4" />

        <h2 class="h6 text-uppercase text-body-secondary fw-semibold mb-2">Testi kirjeldus</h2>
        <p class="description mb-4">{{ test.description }}</p>

        <div class="info-box d-flex align-items-center gap-3 rounded-3 p-3 mb-4">
          <PhTimer v-if="test.isTimed" :size="28" class="text-primary flex-shrink-0" />
          <PhInfinity v-else :size="28" class="text-primary flex-shrink-0" />
          <div>
            <div class="fw-semibold">
              <span v-if="test.isTimed">{{ test.timerMin }} minutit</span>
              <span v-else>Ajapiirang puudub</span>
            </div>
            <small class="text-body-secondary">
              <span v-if="test.isTimed">Taimer käivitub, kui vajutad „Alusta“</span>
              <span v-else>Võid vastata omas tempos</span>
            </small>
          </div>
        </div>

        <button
          @click="goToTestAttempt"
          class="btn btn-primary btn-lg w-100 d-flex align-items-center justify-content-center gap-2"
        >
          <PhPlay :size="20" weight="fill" />
          Alusta testi
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.start-card {
  width: 100%;
  max-width: 36rem;
  border: none;
  border-radius: 1rem;
}

.eyebrow {
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.lead {
  font-size: 1.125rem;
  line-height: 1.6;
}

.description {
  line-height: 1.7;
  white-space: pre-line;
}

.info-box {
  background-color: rgba(var(--bs-primary-rgb), 0.06);
}

.badge {
  font-weight: 500;
  padding: 0.45em 0.9em;
}
</style>
