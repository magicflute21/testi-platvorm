<script>
import TestQuestionCard from '@/components/TestQuestionCard.vue'
import TestAttemptService from '@/services/TestAttemptService.js'

export default {
  name: 'TestAttemptView',
  components: { TestQuestionCard },
  methods: {
    getTestAttempt() {
      // todo replace with real user test assignment id
      TestAttemptService.getTestAttemptRequest(1)
        .then((response) => (this.testAttempt = response.data))
        .catch((error) => console.log(error))
        .finally(() => (this.isLoading = false))
    },
    updateSelectedIds(questionId, selectedAnswerIds) {
      const question = this.testAttempt.questions.find(
        (question) => question.questionId === questionId,
      )
      if (question) {
        question.selectedQuestionAnswerIds = selectedAnswerIds
      }
    },
    submitAnswer(question) {

      //   todo send user answer to backend
      const totalQuestions = this.testAttempt.questions.length
      const currentQuestionNumber = this.currentQuestionIndex + 1
      if (currentQuestionNumber < totalQuestions) {
        this.goToNextQuestion()
      }
    },
    goToNextQuestion() {
      this.currentQuestionIndex += 1
    },
    goToPreviousQuestion() {
      this.currentQuestionIndex -= 1
    },
  },
  data() {
    return {
      isLoading: true,
      currentQuestionIndex: 0,
      testAttempt: {
        questions: [],
      },
    }
  },
  beforeMount() {
    this.getTestAttempt()
  },
}
</script>

<template>
  <div class="container">
    <h1 class="text-center h3">{{ testAttempt.testName }}</h1>
    <div class="d-flex flex-column align-items-center">
      <div v-if="isLoading" class="loading-text text-primary text-lg mt-3" role="status">
        Loading<span class="dot">.</span><span class="dot dot-two">.</span
        ><span class="dot dot-three">.</span>
      </div>
      <div v-else>
            <TestQuestionCard
              :question="testAttempt.questions[currentQuestionIndex]"
              :total-questions="testAttempt.questions.length"
              :question-number="currentQuestionIndex + 1"
              @event-submit-answer="submitAnswer"
              @event-go-to-previous="goToPreviousQuestion"
              @event-update-selectedIds="updateSelectedIds"
            />
      </div>
    </div>
  </div>
</template>

<style scoped>
.dot-two {
  animation: dot-two 1.6s step-end infinite;
}

.dot-three {
  animation: dot-three 1.6s step-end infinite;
}

/* Tsükkel: . → .. → ... → .. → . */
@keyframes dot-two {
  0% {
    opacity: 0;
  }
  25% {
    opacity: 1;
  }
}

@keyframes dot-three {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  75% {
    opacity: 0;
  }
}
</style>
