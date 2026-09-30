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
    class="me-3 btn p-1 account-btn"
    aria-label="Account"
    @click="openAccountMenu"
  >
    <PhUserCircle :size="28" />
  </button>
</template>

<style scoped>
.account-btn,
.account-btn:hover {
  color: #37474f;
}
</style>
