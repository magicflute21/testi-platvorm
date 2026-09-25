<script>
import TestService from '@/services/TestService.js'
import Status from '@/Status.js'

export default {
  name: 'TestOverview',

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
  <div class="container text">
    <div class="row">
      <div v-for="testSummary in testSummaries" :key="testSummary.testId" class="col-4 mb-3">
        <div class="card h-100 shadow-sm">
          <div class="card-body d-flex flex-column">
            <div class="row d-flex flex-row mb-2">
              <h5 class="card-title justify-content-start">
                {{ testSummary.testName }}<span class="badge ms-5" style="background-color: seagreen">{{testSummary.testStatus }}</span>
              </h5>
            </div>
            <div class="row mb-3">
              <div class="card-subtitle">
                <p>
                  {{ testSummary.testShortDescription }}
                </p>
              </div>
            </div>
            <div class="row">
              <div class="d-flex justify-content-start align-content-end gap-2 mb-0">
                <button class="btn btn-primary mt-auto">Vaata testi</button>
                <button class="btn btn-primary mt-auto">Vaata tulemusi</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
