<script>
export default {
  name: 'App',
  data() {
    return {
      items: [
        { label: 'Avaleht', to: '/' },
        { label: 'Test', to: '/test' },
      ],
    }
  },
}
</script>

<template>
  <UApp>
    <UHeader title="SkillScope" to="/">
      <UNavigationMenu :items="items" />

      <template #right>
        <!--        <UIcon name="i-lucide-user" class="size-5" />-->
        <UButton to="/login">Logi sisse</UButton>
      </template>

      <template #body>
        <UNavigationMenu :items="items" orientation="vertical" class="-mx-2.5" />
      </template>
    </UHeader>

    <UMain>
      <RouterView />
    </UMain>
  </UApp>
</template>
