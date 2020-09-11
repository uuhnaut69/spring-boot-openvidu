<template>
  <div class="container">
    <div class="row">
      <b-card
        v-for="conversation in conversations"
        :key="conversation.id"
        :title="conversation.title"
        :img-src="conversation.imageUrl"
        img-alt="Image"
        img-top
        tag="article"
        style="max-width: 20rem"
        class="col mr-2"
      >
        <b-avatar-group class="mb-3" size="40px">
          <b-avatar
            v-for="member in conversation.members"
            :key="member.id"
            :src="member.avatarUrl"
            variant="info"
          ></b-avatar>
        </b-avatar-group>
        <b-button href="/" variant="primary">Make a call</b-button>
      </b-card>
    </div>
  </div>
</template>

<script>
import { Client } from '@stomp/stompjs'
import { mapState } from 'vuex'

export default {
  name: 'HomePage',
  async asyncData({ $axios }) {
    try {
      const response = await $axios.$get('/my-conversations')
      return { conversations: response.data }
    } catch (error) {
      console.log(error)
    }
  },
  data() {
    return {
      conversations: [],
    }
  },
  computed: {
    ...mapState('auth', ['loggedIn', 'user']),
  },
  created() {
    const client = new Client({
      brokerURL: 'ws://localhost:15674/ws',
      connectHeaders: {
        login: 'guest',
        passcode: 'guest',
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })
    client.onConnect = (frame) => {
      client.subscribe('/topic/' + this.user.username, (message) => {
        console.log(JSON.parse(message.body))
      })
    }
    client.onStompError = (frame) => {}
    client.activate()
  },
}
</script>
