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
        Töölaud
      </RouterLink>
      <RouterLink class="nav-link d-flex align-items-center gap-2" to="/tests">
        <PhFlask :size="20" />
        Testid
      </RouterLink>
    </div>
      <ul class="navbar-nav">
        <li class="nav-item dropdown">
          <button
            class="btn btn-dark dropdown-toggle"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            Dropdown
          </button>
          <ul class="dropdown-menu dropdown-menu-">
            <li><a class="dropdown-item" href="#">Action</a></li>
            <li><a class="dropdown-item" href="#">Another action</a></li>
            <li><a class="dropdown-item" href="#">Something else here</a></li>
          </ul>
        </li>
      </ul>
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
