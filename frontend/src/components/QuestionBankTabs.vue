<script>
import AiQuestionService from '@/services/AiQuestionService.js'
import { PhListBullets, PhSparkle } from '@phosphor-icons/vue'

export default {
  name: 'QuestionBankTabs',
  components: { PhListBullets, PhSparkle },

  beforeMount() {
    this.getPendingAiQuestionCount()
  },

  data() {
    return {
      pendingAiQuestionCount: 0,
    }
  },

  methods: {
    getPendingAiQuestionCount() {
      AiQuestionService.getAiQuestionsRequest('P')
        .then((response) => (this.pendingAiQuestionCount = response.data.length))
        .catch()
    },
  },
}
</script>

<template>
  <nav class="nav nav-underline question-bank-tabs mb-4" aria-label="Küsimuste panga vaated">
    <RouterLink
      class="nav-link d-flex align-items-center gap-2"
      exact-active-class="active"
      :to="{ name: 'questionBankView' }"
    >
      <PhListBullets :size="18" />
      Kõik küsimused
    </RouterLink>
    <RouterLink
      class="nav-link d-flex align-items-center gap-2"
      exact-active-class="active"
      :to="{ name: 'aiQuestionBankView' }"
    >
      <PhSparkle :size="18" />
      AI küsimused
      <span
        v-if="pendingAiQuestionCount > 0"
        class="badge rounded-pill badge-in-progress"
        :title="`${pendingAiQuestionCount} AI küsimust ootab ülevaatust`"
      >
        {{ pendingAiQuestionCount }}
      </span>
    </RouterLink>
  </nav>
</template>

<style scoped>
.question-bank-tabs {
  border-bottom: var(--bs-border-width) solid var(--bs-border-color);
  --bs-nav-link-color: var(--bs-gray-600);
  --bs-nav-link-hover-color: var(--bs-body-color);
  --bs-nav-underline-link-active-color: var(--bs-primary);
}

.nav-link {
  font-weight: 600;
  margin-bottom: -1px;
}

/* Kirjutab üle main.css globaalse a:hover rohelise tausta */
.nav-link:hover {
  background-color: transparent;
}
</style>
