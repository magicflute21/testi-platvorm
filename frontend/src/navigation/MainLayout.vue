<script>
import AppSidebar from '@/navigation/AppSidebar.vue'
import AppNavbar from '@/navigation/AppNavbar.vue'

export default {
  name: 'MainLayout',
  components: { Navbar: AppNavbar, AppSidebar },
}
</script>

<template>
  <Navbar />
  <div class="d-flex">
    <AppSidebar />
    <main class="flex-grow-1 p-3 sidebar">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.sidebar {
  width: 220px;
  min-height: 100vh;
}
</style>
