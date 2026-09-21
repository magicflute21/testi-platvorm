<script>
export default {
  name: 'LoginView',
  data() {
    return {
      state: { email: '', password: '' },
    }
  },
  methods: {
    onSubmit() {
      // login loogika siia
      console.log(this.state)
    },
  },
}
</script>

<template>
  <UContainer class="mt-10 max-w-sm">
    <div>
      <h1 class="text-2xl font-bold mb-1">Logi sisse</h1>
      <p class="text-muted mb-6">Sisesta oma konto email ja salasõna.</p>

      <UForm :state="state" class="space-y-4" @submit="onSubmit">
        <UFormField name="email" help="Sinu emaili ei jagata teiste osapooltega.">
          <UInput
            v-model="state.email"
            type="email"
            placeholder="Email"
            icon="i-heroicons-envelope"
            required
            class="w-full"
          />
        </UFormField>

        <UFormField name="password">
          <UInput
            v-model="state.password"
            type="password"
            placeholder="Salasõna"
            icon="i-heroicons-lock-closed"
            required
            class="w-full"
          />
        </UFormField>

        <UButton type="submit">Logi sisse</UButton>
      </UForm>
    </div>
  </UContainer>
</template>

<!--<template>-->
<!--  <div class="container text-start col-3 mt-5">-->
<!--    <div class="row">-->
<!--      <h1>Logi sisse</h1>-->
<!--      <p>Sisesta oma konto email ja salasõna.</p>-->
<!--      <form>-->
<!--        <div class="mb-3">-->
<!--          <input-->
<!--            type="email"-->
<!--            class="form-control"-->
<!--            aria-describedby="emailHelp"-->
<!--            placeholder="Email"-->
<!--          />-->
<!--          <div id="emailHelp" class="form-text">Me ei jaga su emaili teistega.</div>-->
<!--        </div>-->
<!--        <div class="mb-3">-->
<!--          <input type="password" class="form-control" placeholder="Salasõna" />-->
<!--        </div>-->
<!--        <button type="submit" class="btn btn-primary">Logi sisse</button>-->
<!--      </form>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->
