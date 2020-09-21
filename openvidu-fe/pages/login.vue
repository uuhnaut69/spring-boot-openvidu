<template>
  <div class="text-center">
    <b-form class="form-signin">
      <b-img
        class="mb-4 mt-4"
        src="~/assets/openvidu_logo.png"
        width="72"
        height="72"
      ></b-img>
      <h1 class="h3 mb-3 text-white font-weight-normal">Sign In</h1>
      <b-form-group>
        <b-form-input v-model="username" required placeholder="Enter usename">
        </b-form-input>
      </b-form-group>

      <b-form-group>
        <b-form-input
          v-model="password"
          required
          placeholder="Enter password"
          type="password"
        ></b-form-input>
      </b-form-group>

      <b-button
        id="register"
        variant="success"
        class="btn-lg btn-block"
        @click="authenticate(username, password)"
        >Sign In</b-button
      >
      <a class="text-white font-weight-normal" href="/register">Register</a>
    </b-form>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      username: undefined,
      password: undefined,
    }
  },
  methods: {
    async authenticate(username, password) {
      try {
        await this.$auth.loginWith('local', {
          data: {
            username,
            password,
          },
        })
      } catch (err) {
        console.log(err)
      }
    },
  },
}
</script>

<style>
.form-signin {
  width: 100%;
  max-width: 330px;
  padding: 15px;
  margin: 0 auto;
}
</style>
