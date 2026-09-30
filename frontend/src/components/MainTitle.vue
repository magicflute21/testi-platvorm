<script>
export default {
  name: 'MainTitle',
  props: {
    title: String,
  },
}
</script>

<template>
  <h1 class="text-uppercase fw-bold h3 mb-5">{{ title }}</h1>
</template>

<style scoped></style>
