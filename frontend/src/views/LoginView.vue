<script>
import AlertDanger from '@/components/AlertDanger.vue'
import LoginService from "@/services/LoginService.js";

export default {
  name: 'LoginView',
  components: { AlertDanger },

  data() {
    return {
      errorMessage: '',
      showSpinner: false,

      loginRequest: {
        email: '',
        password: '',
      },

      loginResponse: {
        userId: '',
        roleName: '',
      },


    }
  },
  methods: {
    login() {
      this.showSpinner = true
      if (this.loginRequest.email === '' || this.loginRequest.password === '') {
        this.errorMessage = 'Täida kõik väljad!'

        LoginService.postLoginRequest(this.loginRequest)
            .then(response => this.handleLoginResponse(response))
            .catch(error => this.handleLoginErrorResponse(error))
            .finally()
      }

    },
    handleLoginResponse(response) {
      this.loginResponse = response.data

    },
    handleLoginErrorResponse(error) {
      return undefined;
    }
  },
}
</script>

<template>
  <div>
    <div class="container text-start col-5 mt-5">
      <AlertDanger :error-message="errorMessage"/>
    </div>
    <div class="container text-start col-3 mt-5">
      <div class="row">
        <h1>Logi sisse</h1>
        <p>Sisesta oma konto email ja salasõna.</p>
        <form>
          <div class="mb-3">
            <input
              v-model="loginRequest.email"
              type="email"
              class="form-control"
              aria-describedby="emailHelp"
              placeholder="Email"
            />
            <div id="emailHelp" class="form-text">Me ei jaga su emaili teistega.</div>
          </div>
          <div class="mb-3">
            <input
              v-model="loginRequest.password"
              type="password"
              class="form-control"
              placeholder="Salasõna"
            />
          </div>
          <button v-if="showSpinner" class="btn btn-primary" type="button" disabled>
            <span class="spinner-border spinner-border-sm" aria-hidden="true"></span>
            <span role="status">Login sisse</span>
          </button>
          <button v-else @click="login" type="submit" class="btn btn-primary">Logi sisse</button>
        </form>
      </div>
    </div>
  </div>
</template>
