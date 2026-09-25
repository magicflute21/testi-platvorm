<script>
import TestQuestionCard from '@/components/TestQuestionCard.vue'

export default {
  name: 'TestAttemptView',
  components: { TestQuestionCard },
  methods: {
    updateSelectedIds(questionId, selectedAnswerIds) {
      const question = this.testAttempt.questions.find(
        (question) => question.questionId === questionId,
      )
      console.log('selectedAnswerIds', selectedAnswerIds)
      if (question) {
        question.selectedQuestionAnswerIds = selectedAnswerIds
      }
    },
    submitAnswer(question) {
      console.log(question)

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
      currentQuestionIndex: 0,
      testAttempt: {
        userTestId: 1,
        testId: 1,
        testName: 'JavaScripti algtaseme test',
        questions: [
          {
            questionId: 2,
            position: 1,
            title: 'Millised järgnevatest on JS primitiivtüübid?',
            description: 'Vali kõik JavaScripti primitiivtüübid.',
            questionTypeName: 'MULTIPLE_CHOICE',
            answers: [
              {
                questionAnswerId: 4,
                answerText: 'string',
              },
              {
                questionAnswerId: 5,
                answerText: 'number',
              },
              {
                questionAnswerId: 6,
                answerText: 'massiiv',
              },
              {
                questionAnswerId: 7,
                answerText: 'objekt',
              },
            ],
            selectedQuestionAnswerIds: [],
          },
          {
            questionId: 3,
            position: 2,
            title: 'Küsimus kaks?',
            description: 'Vali kõik JavaScripti primitiivtüübid.',
            questionTypeName: 'SINGLE_CHOICE',
            answers: [
              {
                questionAnswerId: 8,
                answerText: '1',
              },
              {
                questionAnswerId: 9,
                answerText: '2',
              },
              {
                questionAnswerId: 10,
                answerText: '3',
              },
              {
                questionAnswerId: 11,
                answerText: '4',
              },
            ],
            selectedQuestionAnswerIds: [],
          },
        ],
      },
    }
  },
}
</script>

<template>
  <div class="container">
    <h1 class="text-center text-primary h3">{{ testAttempt.testName }}</h1>
    <div class="d-flex flex-column align-items-center">
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
</template>
