<script>
export default {
  name: 'TestResultView',

  data() {
    return {
      userTestId: Number(this.$route.query.userTestId),
    }
  },
}
</script>

<template>
  <div class="container text-center mt-5">
    <div class="alert alert-success" role="alert">
      Aitäh! Test on edukalt lõpetatud ja sinu vastused on salvestatud.
    </div>
  </div>
</template>
