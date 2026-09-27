<script>
import PreviewCard from '@/components/PreviewCard.vue'

export default {
  name: 'CompetenceView',
  components: { PreviewCard },
}
</script>

<template>
  <div class="container-fluid py-4">
    <div class="preview-test-card-grid">
      <PreviewCard></PreviewCard>
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
