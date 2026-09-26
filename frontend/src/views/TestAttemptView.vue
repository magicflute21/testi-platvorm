<script>
import TestQuestionCard from '@/components/TestQuestionCard.vue'
import TestAttemptService from '@/services/TestAttemptService.js'
import LoadingText from '@/components/LoadingText.vue'

export default {
  name: 'TestAttemptView',
  components: { LoadingText, TestQuestionCard },
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
      <LoadingText v-if="isLoading" />
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
