<script>
import MainTitle from '@/components/MainTitle.vue'

export default {
  name: 'CompetenceView',
  components: { MainTitle },
}
</script>

<template>
  <div class="container-fluid py-4">
    <MainTitle title="Kompetentsid" />
    <div class="preview-test-card-grid">
    </div>
  </div>
</template>

<style scoped>
.preview-test-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(500px, 100%), 1fr));
  gap: 1rem;
}
</style>
