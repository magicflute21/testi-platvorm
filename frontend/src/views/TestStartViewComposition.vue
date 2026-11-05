<script setup>
import { onMounted, ref } from 'vue'
import NavigationService from '@/services/NavigationService.js'
import TestStartService from '@/services/TestStartService.js'
import LoadingText from '@/components/LoadingText.vue'
import TestNotFoundIllustration from '@/components/TestNotFoundIllustration.vue'
import { PhTimer, PhInfinity, PhPlay } from '@phosphor-icons/vue'

const props = defineProps({
  testId: { type: Number, required: true },
})
const isLoading = ref(true)
const errorMessage = ref('')
const test = ref({
  title: '',
  description: '',
  shortDescription: '',
  competence: '',
  competenceLevel: '',
  status: '',
  testId: '',
  timerMin: 0,
  isTimed: false,
})

const startTheTest = () => {
  NavigationService.navigateToTestAttempt(props.testId)
}
const getTestStart = () => {
  TestStartService.getTestStart(props.testId)
    .then((response) => (test.value = response.data))
    .catch((error) => handleGetTestStartErrorResponse(error))
    .finally(() => (isLoading.value = false))
}
const handleGetTestStartErrorResponse = (error) => {
  const data = error.response?.data
  const noAssignment =
    error.response?.status === 403 && data?.errorCode === 'NO_TEST_ASSIGNMENT_FOR_THIS_USER'

  errorMessage.value = noAssignment ? data.message : 'Testi laadimine ebaõnnestus'
}

onMounted(getTestStart)
</script>

<template>
  <div class="container d-flex flex-column align-items-center">
    <LoadingText v-if="isLoading" />

    <TestNotFoundIllustration v-else-if="errorMessage" :message="errorMessage" />

    <div v-else class="card my-4 app-surface p-4 start-card">
      <div class="card-body">
        <p class="eyebrow text-brand mb-2">{{ test.competence }}</p>
        <h1 class="text-uppercase fw-bold h3">{{ test.title }}</h1>

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
          @click="startTheTest"
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
