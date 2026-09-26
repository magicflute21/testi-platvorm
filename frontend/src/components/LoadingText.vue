<script>
export default {
  name: 'LoadingText',
}
</script>

<template>
  <div class="loading-text text-primary text-lg mt-3" role="status">
    Loading<span class="dot">.</span><span class="dot dot-two">.</span
    ><span class="dot dot-three">.</span>
  </div>
</template>

<style scoped>
.dot-two {
  animation: dot-two 1.6s step-end infinite;
}

.dot-three {
  animation: dot-three 1.6s step-end infinite;
}

/* Tsükkel: . → .. → ... → .. → . */
@keyframes dot-two {
  0% {
    opacity: 0;
  }
  25% {
    opacity: 1;
  }
}

@keyframes dot-three {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  75% {
    opacity: 0;
  }
}
</style>
