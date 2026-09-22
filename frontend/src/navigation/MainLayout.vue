<script>
import AppSidebar from '@/navigation/AppSidebar.vue'

export default {
  name: 'MainLayout',
  components: { AppSidebar },
}
</script>

<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light px-3">
    <RouterLink class="navbar-brand" to="/">SkillScope</RouterLink>
  </nav>

  <div class="d-flex">
    <AppSidebar />
    <main class="flex-grow-1 p-3">
      <RouterView />
    </main>
  </div>
</template>
