<script>
import NavigationService from '@/services/NavigationService.js'

export default {
  name: 'TestStartView',
  props: {
    testId: { type: Number, required: true },
  },
  methods: {
    goToTestAttempt() {
      NavigationService.navigateToTestAttempt(this.testId)
    },
  },
}
</script>

<template>
  <div>
    <h2>Testi pealkiri</h2>
    <p>Testi kirjeldus</p>
    <button
      @click="goToTestAttempt" class="btn btn-primary">Alusta
    </button>
  </div>
</template>
