<template>
  <div class="container">
    <div class="text-center">
      <b-form class="form-register">
        <b-img
          class="mb-4 mt-4"
          src="https://getbootstrap.com/docs/4.0/assets/brand/bootstrap-solid.svg"
          width="72"
          height="72"
        ></b-img>
        <h1 class="h3 mb-3 font-weight-normal">Register</h1>
        <b-form-group id="username-group">
          <b-form-input
            id="username-input"
            v-model="username"
            required
            placeholder="Enter usename"
          >
          </b-form-input>
        </b-form-group>

        <b-form-group id="password-group">
          <b-form-input
            id="password-input"
            v-model="password"
            required
            placeholder="Enter password"
            type="password"
          ></b-form-input>
        </b-form-group>

        <b-button
          id="register"
          type="summit"
          variant="primary"
          class="btn-lg btn-block"
          >Register</b-button
        >
      </b-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      username: null,
      password: null,
    }
  },
}
</script>

<style>
.form-register {
  width: 100%;
  max-width: 330px;
  padding: 15px;
  margin: 0 auto;
}
</style>
