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
  <div class="container-fluid d-flex flex-wrap p-3">
    <div class="row d-flex flex-wrap p-2">
      <div class="justify-content-start mb-4">
        <h1>Testid</h1>
      </div>
      <div
        v-for="testSummary in testSummaries"
        :key="testSummary.testId"
        class="col-12 col-sm-12 col-md-4 gap-3 mb-sm-4"
      >
        <div class="card h-100 shadow-sm rounded-4 fixed-card card:hover">
          <div class="card-body d-flex flex-column">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <div class="d-flex flex-wrap align-items-center flex-grow-1 gap-2">
                <h4 class="card-title">
                  {{ testSummary.testName }}
                </h4>
                <span
                  class="badge flex-shink-0 ms-2"
                  :class="testStatus[testSummary.testStatus]?.badgeClass"
                >
                  {{ testStatus[testSummary.testStatus]?.name }}</span
                >
              </div>
              <button class="btn"><PhDotsThree :size="22" /></button>
            </div>
            <p class="card-text flex-grow-6">{{ testSummary.testShortDescription }}</p>
            <div class="d-flex flex-wrap gap-2 mt-auto">
              <button class="btn btn-outline-primary btn-sm">Vaata testi</button>
              <button class="btn btn-primary btn-sm bt-xs">Vaata tulemusi</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.fixed-card {
  max-width: 820px;
  min-width: 420px;
}

.btn-xs {
  padding-top: 0.1rem;
  padding-bottom: 0.1rem;
}

.badge-active {
  background-color: #96b888;
  text-decoration-color: #394733;
}

.badge-inactive {
  background-color: #e07670;
  colour: #663330;
}

.badge-in-progress {
  background-color: #f7e794;
  colour: #c7ae30;
}

.card {
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}
</style>
