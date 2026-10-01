<script>
export default {
  name: 'CreateQuestionModal',

  data() {
    return {
      questionTypeId: 0,



      questionCreateRequest: [
        {
          competenceLevelId: 0,
          title: '',
          description: '',
          questionTypeId: 0,
          score: 0,
          answers: [
            {
              answerText: '',
              isCorrect: true,
            },
          ],
        },
      ],
    }
  },
}
</script>

<template>



</template>

<style scoped></style>
