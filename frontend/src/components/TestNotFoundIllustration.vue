<script>
import aloeVeraImage from '@/assets/images/aloe-vera-cuate-green.svg'

export default {
  name: 'TestNotFoundIllustration',
  props: {
    message: String,
  },
  data() {
    return {
      aloeVeraImage,
    }
  },
}
</script>

<template>
  <div class="not-found text-center my-4">
    <img :src="aloeVeraImage" alt="Aaloe illustratsioon" class="not-found-image mb-3" />
    <h1 class="h3 fw-semibold mb-2">Testi ei leitud</h1>
    <p class="text-body-secondary mb-0">{{ message }}</p>
  </div>
</template>

<style scoped>
.not-found {
  width: 100%;
  max-width: 36rem;
}

.not-found-image {
  width: 100%;
  max-width: 28rem;
  height: auto;
}
</style>
