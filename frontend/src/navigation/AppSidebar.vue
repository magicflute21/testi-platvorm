<script>
import { PhHouse, PhFlask, PhFilePlus, PhGraduationCap, PhStack } from '@phosphor-icons/vue'

export default {
  name: 'AppSidebar',
  components: { PhStack, PhGraduationCap, PhFilePlus, PhHouse, PhFlask },
}
</script>

<template>
  <nav class="sidebar navbar-light d-flex flex-column p-1 pt-3">
    <div class="nav nav-pills flex-column">
      <RouterLink class="nav-link d-flex align-items-center gap-2" to="/">
        <PhHouse :size="20" />
        Töölaud
      </RouterLink>
      <RouterLink class="nav-link d-flex align-items-center gap-2" to="/tests">
        <PhFlask :size="20" />
        Testid
      </RouterLink>
      <RouterLink class="nav-link d-flex align-items-center gap-2" to="/tests/new">
        <PhFilePlus :size="20" />
        Loo uus test
      </RouterLink>
      <RouterLink class="nav-link d-flex align-items-center gap-2" to="/competences">
        <PhStack :size="20" />
        Kompetentsid
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
