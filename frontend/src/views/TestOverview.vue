<script>
import TestService from '@/services/TestService.js'
import Status from '@/Status.js'
import { PhDotsThree } from '@phosphor-icons/vue'

export default {
  name: 'TestOverview',
  components: { PhDotsThree },

  beforeMount() {
    this.getAllTests()
  },

  data() {
    return {
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
        .finally()
    },
    handleGetAllTests(response) {
      this.testSummaries = response.data
    },
  },
}
</script>

<template>
  <div class="container py-4">
    <h1 class="mb-5">Testid</h1>

    <div class="test-grid">
      <div
        v-for="testSummary in testSummaries"
        :key="testSummary.testId"
        class="card preview-card shadow-sm rounded-4"
      >
        <div class="card-body d-flex flex-column">
          <div class="d-flex align-items-start gap-4 mb-2">
            <h4 class="card-title flex-grow-1 mb-0">{{ testSummary.testName }}</h4>
            <span
              class="badge flex-shrink-0 mt-1"
              :class="testStatus[testSummary.testStatus]?.badgeClass"
            >
              {{ testStatus[testSummary.testStatus]?.name }}
            </span>
            <button class="btn btn-sm flex-shrink-0 p-0"><PhDotsThree :size="22" /></button>
          </div>
          <p class="card-text flex-grow-1">{{ testSummary.testShortDescription }}</p>
          <div class="d-flex flex-wrap gap-2 mt-auto">
            <button class="btn btn-color">Vaata testi</button>
            <button class="btn btn-color">Vaata tulemusi</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.test-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(550px, 100%), 1fr));
  gap: 1rem;
}

@media (min-width: 1800px) {
  .container {
    max-width: 1760px; /* 3 kaarti */
  }
}

@media (min-width: 2400px) {
  .container {
    max-width: 2340px; /* 4 kaarti */
  }
}

@media (min-width: 3000px) {
  .container {
    max-width: 2900px; /* 4 kaarti */
  }
}

.card-text {
  min-height: 4.5rem; /* hoiab kaardid ühtlase kõrgusega ka lühikese/tühja kirjelduse korral */
}

.card {
  min-width: 0;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease;
}

.preview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

.card-title,
.card-text {
  min-width: 0; /* lubab flex-elemendil kitseneda, et pikk pealkiri ei lükkaks badge'i välja */
  overflow-wrap: anywhere;
}

.btn-color {
  background-color: var(--bs-gray-200);
  color: var(--bs-blue);
  font-weight: bold;
}
</style>
