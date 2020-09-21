<template>
  <div class="background" :style="{ backgroundImage: `url('${background}')` }">
    <Nav />
    <Nuxt />
  </div>
</template>

<script>
import Nav from '~/components/Nav'

export default {
  components: {
    Nav,
  },
  computed: {
    background() {
      return require('~/assets/background.jpg')
    },
  },
}
</script>

<style>
.background {
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  height: 100vh;
}
</style>
