<script>
import { PhStar } from '@phosphor-icons/vue'

const MAX_STARS = 5

export default {
  name: 'StarRating',
  components: { PhStar },
  props: {
    modelValue: { type: Number, default: null },
    readonly: { type: Boolean, default: false },
    size: { type: Number, default: 28 },
  },
  emits: ['update:modelValue'],
  data() {
    return {
      stars: Array.from({ length: MAX_STARS }, (_, index) => index + 1),
      hoveredStar: null,
    }
  },
  methods: {
    isFilled(star) {
      const shownValue = this.hoveredStar ?? this.modelValue ?? 0
      return star <= shownValue
    },
    selectStar(star) {
      this.$emit('update:modelValue', star)
    },
  },
}
</script>

<template>
  <span
    v-if="readonly"
    class="star-rating d-inline-flex gap-1"
    :aria-label="`Hinnang ${modelValue} / 5`"
    role="img"
  >
    <PhStar
      v-for="star in stars"
      :key="star"
      :size="size"
      :weight="isFilled(star) ? 'fill' : 'regular'"
      class="star"
      :class="{ filled: isFilled(star) }"
    />
  </span>
  <span
    v-else
    class="star-rating d-inline-flex gap-1"
    role="radiogroup"
    aria-label="Hinnang"
    @mouseleave="hoveredStar = null"
  >
    <button
      v-for="star in stars"
      :key="star"
      type="button"
      class="star-button btn p-0 border-0"
      role="radio"
      :aria-checked="modelValue === star"
      :aria-label="`${star} / 5`"
      @mouseenter="hoveredStar = star"
      @click="selectStar(star)"
    >
      <PhStar
        :size="size"
        :weight="isFilled(star) ? 'fill' : 'regular'"
        class="star"
        :class="{ filled: isFilled(star) }"
      />
    </button>
  </span>
</template>

<style scoped>
.star {
  color: var(--bs-gray-400);
  transition: color 0.1s ease;
}

.star.filled {
  color: var(--bs-warning);
}

.star-button:focus-visible {
  outline: 2px solid var(--bs-primary);
  outline-offset: 2px;
  border-radius: 0.25rem;
}
</style>
