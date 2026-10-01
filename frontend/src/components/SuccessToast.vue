<script>
import { PhCheckCircle, PhX } from '@phosphor-icons/vue'

// Teavitus kaob ise selle aja möödudes (millisekundites)
const AUTO_CLOSE_MS = 4000

export default {
  name: 'SuccessToast',
  components: { PhCheckCircle, PhX },
  props: {
    successMessage: { type: String, default: '' },
  },
  emits: ['event-close'],
  data() {
    return {
      closeTimer: null,
    }
  },
  watch: {
    successMessage(newMessage) {
      clearTimeout(this.closeTimer)
      if (newMessage !== '') {
        this.closeTimer = setTimeout(() => this.$emit('event-close'), AUTO_CLOSE_MS)
      }
    },
  },
  beforeUnmount() {
    clearTimeout(this.closeTimer)
  },
}
</script>

<template>
  <Transition name="toast">
    <div
      v-if="successMessage !== ''"
      class="success-toast d-flex align-items-start gap-2 rounded-3 shadow p-3"
      role="status"
      aria-live="polite"
    >
      <PhCheckCircle :size="20" weight="fill" class="toast-icon flex-shrink-0" />
      <span class="flex-grow-1">{{ successMessage }}</span>
      <button
        type="button"
        class="btn btn-sm p-0 flex-shrink-0 toast-close"
        aria-label="Sulge teavitus"
        @click="$emit('event-close')"
      >
        <PhX :size="16" />
      </button>
    </div>
  </Transition>
</template>

<style scoped>
/* Paremal all, AI chati nupu (56px kõrge, bottom 1.5rem) kohal ja sellest kõrgemal kihil */
.success-toast {
  position: fixed;
  right: 1.5rem;
  bottom: calc(1.5rem + 56px + 1rem);
  z-index: 1060;
  width: 360px;
  max-width: calc(100vw - 3rem);
  background-color: var(--bs-success-bg-subtle);
  color: var(--bs-success-text-emphasis);
  border: var(--bs-border-width) solid var(--bs-success-border-subtle);
  overflow-wrap: anywhere;
}

.toast-icon {
  color: var(--bs-success);
}

.toast-close {
  color: var(--bs-success-text-emphasis);
}

.toast-enter-active,
.toast-leave-active {
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(0.5rem);
}
</style>
