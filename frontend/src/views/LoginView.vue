<script>
export default {
  name: 'LoginView',
}
</script>

<template>
  <div class="container text-start col-3 mt-5">
    <div class="row">
      <h1>Logi sisse</h1>
      <p>Sisesta oma konto email ja salasõna.</p>
      <form>
        <div class="mb-3">
          <input
            type="email"
            class="form-control"
            aria-describedby="emailHelp"
            placeholder="Email"
          />
          <div id="emailHelp" class="form-text">Me ei jaga su emaili teistega.</div>
        </div>
        <div class="mb-3">
          <input type="password" class="form-control" placeholder="Salasõna" />
        </div>
        <button type="submit" class="btn btn-primary">Logi sisse</button>
      </form>
    </div>
  </div>
</template>
