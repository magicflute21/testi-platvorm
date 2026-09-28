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

  <div class="d-flex layout">
    <AppSidebar />
    <main class="flex-grow-1 p-3">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>

.layout {
  min-height: calc(100vh - 57px);
  background-color: #f5f8f6;
  background-image:
    radial-gradient(circle at 100% 0%, rgba(146, 227, 169, 0.22), transparent 45%),
    radial-gradient(rgba(55, 71, 79, 0.08) 1px, transparent 1px);
  background-size:
    100% 100%,
    22px 22px;
  background-attachment: fixed;
}
</style>
