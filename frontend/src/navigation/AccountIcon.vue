<script>
import { PhUserCircle } from '@phosphor-icons/vue'
import SessionStorageService from '@/services/SessionStorageService.js'

export default {
  name: 'AccountIcon',
  components: { PhUserCircle },
  computed: {
    isLoggedIn() {
      return SessionStorageService.userIsLoggedIn()
    },
  },
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
    :class="{ 'is-logged-in': isLoggedIn }"
    :aria-label="isLoggedIn ? 'Konto (sisse logitud)' : 'Konto'"
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

/* Sisselogitud kasutajal brändi roheline (vt assets/theme.css) */
.account-btn.is-logged-in,
.account-btn.is-logged-in:hover {
  color: var(--brand-green);
}
</style>
