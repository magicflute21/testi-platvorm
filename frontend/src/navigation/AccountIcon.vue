<script>
import { PhUserCircle } from '@phosphor-icons/vue'

export default {
  name: 'AccountIcon',
  components: { PhUserCircle },
  methods: {
    openAccountMenu() {
      console.log('open menu')
    },
  },
}
</script>

<template>
  <button
    type="button"
    class="me-3 btn p-1 text-secondary"
    aria-label="Account"
    @click="openAccountMenu"
  >
    <PhUserCircle :size="24" />
  </button>
</template>
