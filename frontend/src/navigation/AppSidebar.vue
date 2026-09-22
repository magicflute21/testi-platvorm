<script>
import { PhHouse, PhFlask } from '@phosphor-icons/vue'

export default {
  name: 'AppSidebar',
  components: { PhHouse, PhFlask },
}
</script>

<template>
  <nav class="sidebar navbar-light d-flex flex-column p-3">
    <div class="nav nav-pills flex-column">
      <RouterLink class="nav-link d-flex align-items-center gap-2" to="/">
        <PhHouse :size="20" />
        Home
      </RouterLink>
      <RouterLink class="nav-link d-flex align-items-center gap-2" to="/test">
        <PhFlask :size="20" />
        Test
      </RouterLink>
    </div>
  </nav>
</template>

<style scoped>
.sidebar {
  width: 220px;
  min-height: 100vh;
}

.nav-link {
  color: var(--bs-gray-600);
}

.nav-link.router-link-exact-active {
  background-color: var(--bs-gray-200);
  color: var(--bs-blue);
}
</style>
