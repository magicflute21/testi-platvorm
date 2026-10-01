<script>
import TestQuestionCard from '@/components/TestQuestionCard.vue'
import TestAttemptService from '@/services/TestAttemptService.js'
import LoadingText from '@/components/LoadingText.vue'
import TestNotFoundIllustration from '@/components/TestNotFoundIllustration.vue'
import NavigationService from '@/services/NavigationService.js'

export default {
  name: 'TestAttemptView',
  components: {
    TestNotFoundIllustration,
    LoadingText,
    TestQuestionCard,
  },
  props: {
    testId: { type: Number, required: true },
  },
  data() {
    return {
      isLoading: false,
      errorMessage: '',
      currentQuestionIndex: 0,
      testAttempt: {
        userTestId: 0,
        testId: 0,
        testName: '',
        isTimed: false,
        timerMin: 0,
        passPercent: 0,
        roundScoreUp: true,
        questions: [
          {
            questionId: 0,
            position: 0,
            title: '',
            description: '',
            questionTypeName: '',
            answers: [
              {
                questionAnswerId: 0,
                answerText: '',
              },
            ],
            selectedQuestionAnswerIds: [],
          },
        ],
      },
      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },
  methods: {
    getTestAttempt() {
      this.isLoading = true
      TestAttemptService.getTestAttemptRequest(this.testId)
        .then((response) => (this.testAttempt = response.data))
        .catch((error) => this.handleTestAttemptErrorResponse(error))
        .finally(() => {
          this.isLoading = false
        })
    },
    completeTest() {
      const submittedAnswers = this.testAttempt.questions.map((question) => ({
        questionId: question.questionId,
        answerIds: question.selectedQuestionAnswerIds,
      }))
      console.log(submittedAnswers)
      TestAttemptService.postCompleteTest(this.testId, submittedAnswers)
        .then((response) => this.handleCompleteTestResponse(response))
        .catch((error) => this.handleCompeteTestErrorResponse(error))
    },
    handleCompleteTestResponse() {
      NavigationService.navigateToTestResult(this.testAttempt.userTestId);
    },
    handleCompeteTestErrorResponse(error) {
      console.log(error)
    },
    handleTestAttemptErrorResponse(error) {
      this.errorResponse = error.response.data
      if (
        error.response.status === 403 &&
        this.errorResponse.errorCode === 'NO_TEST_ASSIGNMENT_FOR_THIS_USER'
      ) {
        this.errorMessage = this.errorResponse.message
        this.testAttempt = {}
      }
    },
    updateSelectedIds(questionId, selectedAnswerIds) {
      const question = this.testAttempt.questions.find(
        (question) => question.questionId === questionId,
      )
      if (question) {
        question.selectedQuestionAnswerIds = selectedAnswerIds
      }
    },
    submitAnswer() {
      const totalQuestions = this.testAttempt.questions.length
      const currentQuestionNumber = this.currentQuestionIndex + 1

      if (currentQuestionNumber === totalQuestions) {
        this.completeTest()
      } else if (currentQuestionNumber < totalQuestions) {
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

  beforeMount() {
    this.getTestAttempt()
  },
}
</script>

<template>
  <div class="container d-flex flex-column align-items-center">
    <TestNotFoundIllustration v-if="errorMessage.length" :message="errorMessage" />
    <div v-else>
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
  </div>
</template>
