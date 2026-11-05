<script setup>
import TestService from '@/services/TestService.js'
import { ref, onMounted } from 'vue'
import Status from '@/Status.js'
import MainTitle from '@/components/MainTitle.vue'
import PreviewCard from '@/components/PreviewCard.vue'
import LoadingText from '@/components/LoadingText.vue'
import NavigationService from '@/services/NavigationService.js'

const isLoading = ref(true)
const testStatus = Status
const testSummaries = ref([
  {
    testId: 0,
    testName: '',
    testShortDescription: '',
    testStatus: '',
  },
])

const handleGetAllTests = (response) => {
  testSummaries.value = response.data
}

const getAllTests = () => {
  TestService.getAllTests()
    .then((response) => handleGetAllTests(response))
    .catch()
    .finally(() => (isLoading.value = false))
}

const navigateToTestDetail = (testId) => {
  NavigationService.navigateToTestDetail(testId)
}

onMounted(getAllTests)

</script>

<template>
  <div class="container py-4">
    <LoadingText v-if="isLoading" />
    <div v-else>
      <MainTitle title="Testid" />
      <div class="preview-test-card-grid">
        <PreviewCard
          v-for="testSummary in testSummaries"
          :key="testSummary.testId"
          :status-badge="testStatus[testSummary.testStatus]"
        >
          <template #title>{{ testSummary.testName }}</template>
          <template #description>{{ testSummary.testShortDescription }}</template>
          <template #menu>
            <li>
              <button class="dropdown-item" @click="assignTestToUser(testSummary.testId)">
                Määra test
              </button>
            </li>
          </template>
          <template #actions>
            <button
              class="btn btn-primary fw-bold rounded-2"
              @click="navigateToTestDetail(testSummary.testId)"
            >
              Vaata testi
            </button>
            <button class="btn btn-light fw-bold rounded-2">Vaata tulemusi</button>
          </template>
        </PreviewCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-test-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(450px, 100%), 1fr));
  gap: 1rem;
}

@media (min-width: 1800px) {
  .container {
    max-width: 1760px;
  }
}

@media (min-width: 2400px) {
  .container {
    max-width: 2340px;
  }
}

@media (min-width: 3000px) {
  .container {
    max-width: 2900px;
  }
}
</style>
