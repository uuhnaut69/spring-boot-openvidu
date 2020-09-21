<template>
  <b-navbar v-if="loggedIn" toggleable="lg" type="dark">
    <b-navbar-brand href="#">Video Conference Demo</b-navbar-brand>

    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

    <b-collapse id="nav-collapse" is-nav>
      <!-- Right aligned nav items -->
      <b-navbar-nav v-if="loggedIn" class="ml-auto">
        <b-nav-item-dropdown right>
          <!-- Using 'button-content' slot -->
          <template v-slot:button-content>
            <b-avatar :src="user.avatarUrl"></b-avatar>
          </template>
          <b-dropdown-item @click="logout()">Sign Out</b-dropdown-item>
        </b-nav-item-dropdown>
      </b-navbar-nav>
    </b-collapse>
  </b-navbar>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Nav',
  computed: {
    ...mapState('auth', ['loggedIn', 'user']),
  },

  methods: {
    async logout() {
      await this.$auth.logout()
      this.$router.push('/login')
    },
  },
}
</script>

<style></style>
