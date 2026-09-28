<script>
import { PhHouse, PhFlask } from '@phosphor-icons/vue'

export default {
  name: 'AppSidebar',
  components: { PhHouse, PhFlask },
}
</script>

<template>
  <aside class="sidebar-wrapper">
    <nav class="sidebar">
      <p class="sidebar-label">Navigatsioon</p>
      <div class="nav flex-column gap-1">
        <RouterLink class="nav-link d-flex align-items-center gap-2" to="/">
          <PhHouse :size="20" />
          Töölaud
        </RouterLink>
        <RouterLink class="nav-link d-flex align-items-center gap-2" to="/tests">
          <PhFlask :size="20" />
          Testid
        </RouterLink>
      </div>
    </nav>
  </aside>
</template>

<style scoped>
.sidebar-wrapper {
  flex-shrink: 0;
  padding: 1.5rem 0 1.5rem 1.5rem;
}

.sidebar {
  position: sticky;
  top: 1.5rem;
  width: 220px;
  padding: 1.25rem 0.75rem;
  background: #fff;
  border: 1px solid rgba(26, 26, 46, 0.06);
  border-radius: 1rem;
  overflow: hidden;
  box-shadow:
    0 1px 2px rgba(26, 26, 46, 0.04),
    0 8px 24px rgba(26, 26, 46, 0.08);
}

/* Aaloe illustratsiooni tumeda kivi värvi päiseriba üle kogu kaardi laiuse (negatiivsed marginid tühistavad .sidebar paddingu) */
.sidebar-label {
  margin: -1.25rem -0.75rem 0.75rem;
  padding: 0.85rem 1.5rem;
  background: #37474f;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #fff;
}

.nav-link {
  padding: 0.6rem 0.75rem;
  border-radius: 0.6rem;
  color: var(--bs-gray-600);
  font-weight: 500;
  transition:
    background-color 0.2s ease,
    color 0.2s ease;
}

/* Kirjutab üle main.css globaalse a:hover rohelise tausta */
.nav-link:hover {
  background-color: rgba(var(--bs-primary-rgb), 0.06);
  color: #1a1a2e;
}

.nav-link.router-link-exact-active {
  background-color: rgba(var(--bs-primary-rgb), 0.1);
  color: var(--bs-primary);
}
</style>
