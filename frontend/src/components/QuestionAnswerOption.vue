<script>
import { PhCheckCircle } from '@phosphor-icons/vue'

export default {
  name: 'QuestionAnswerOption',
  components: { PhCheckCircle },
  props: {
    answerText: { type: String, default: '' },
    correctChoice: { type: Boolean, default: false },
  },
}
</script>

<template>
  <div
    class="answer-option d-flex align-items-center gap-3 rounded-3 py-2 px-3"
    :class="{ correct: correctChoice }"
  >
    <span class="answer-text flex-grow-1">{{ answerText }}</span>
    <span v-if="correctChoice" class="answer-correct flex-shrink-0">
      <PhCheckCircle :size="20" weight="fill" />
      <span class="visually-hidden">Õige vastus</span>
    </span>
  </div>
</template>

<style scoped>
.answer-option {
  border: 1px solid var(--bs-border-color);
  background-color: var(--bs-gray-100);
}

.answer-option.correct {
  border-color: var(--bs-success);
  background-color: var(--bs-success-bg-subtle);
}

.answer-text {
  min-width: 0;
  overflow-wrap: anywhere;
}

.answer-correct {
  color: var(--bs-success);
}
</style>
