<script>
import AppSidebar from '@/navigation/AppSidebar.vue'
import AppNavbar from '@/navigation/AppNavbar.vue'
import AiChatWidget from '@/components/AiChatWidget.vue'

export default {
  name: 'MainLayout',
  components: { Navbar: AppNavbar, AppSidebar, AiChatWidget },
  computed: {
    // AI küsimuste loomine on mõeldud ainult küsimuste koostajatele
    canCreateQuestions() {
      const roleName = sessionStorage.getItem('roleName')
      return roleName === 'ADMIN' || roleName === 'HALDUR'
    },
  },
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

  <AiChatWidget v-if="canCreateQuestions" />
</template>
