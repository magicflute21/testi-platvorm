<script>
import TestService from '@/services/TestService.js'
import Status from '@/Status.js'
import PreviewCard from '@/components/PreviewCard.vue'
import LoadingText from '@/components/LoadingText.vue'

export default {
  name: 'TestOverview',
  components: { LoadingText, PreviewCard },

  beforeMount() {
    this.getAllTests()
  },

  data() {
    return {
      isLoading: true,
      testStatus: Status,
      testSummaries: [
        {
          testId: 0,
          testName: '',
          testShortDescription: '',
          testStatus: '',
        },
      ],
    }
  },
  methods: {
    getAllTests() {
      TestService.getAllTests()
        .then((response) => this.handleGetAllTests(response))
        .catch()
        .finally(() => (this.isLoading = false))
    },
    handleGetAllTests(response) {
      this.testSummaries = response.data
    },
    assignTestToUser() {},
    // tühi meetod nupuvajutuse näitamiseks
  },
}
</script>

<template>
  <div class="container py-4">
    <LoadingText v-if="isLoading" />
    <div v-else>
      <h1 class="mb-5">Testid</h1>
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
            <button class="btn btn-color rounded-2">Vaata testi</button>
            <button class="btn btn-color rounded-2">Vaata tulemusi</button>
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
