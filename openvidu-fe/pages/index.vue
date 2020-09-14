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
        <b-button variant="primary" @click="getToken(conversation.id)"
          >Make a call</b-button
        >
      </b-card>
    </div>

    <div id="session">
      <div id="session-header">
        <!-- <h1 id="session-title">{{ mySessionId }}</h1>
        <input
          class="btn btn-large btn-danger"
          type="button"
          id="buttonLeaveSession"
          @click="leaveSession"
          value="Leave session"
        /> -->
      </div>
      <div id="main-video" class="col-md-6">
        <user-video :stream-manager="mainStreamManager" />
      </div>
      <div id="video-container" class="col-md-6">
        <user-video
          v-for="(sub, index) in subscribers"
          :key="index"
          :stream-manager="sub"
          @click.native="updateMainVideoStreamManager(sub)"
        />
      </div>
    </div>

    <b-modal
      v-model="modalShow"
      title="Incomming Call"
      ok-title="Join"
      cancel-title="Decline"
      cancel-variant="danger"
      @ok="getToken(inCommingConversationId)"
      @cancel="modalShow = false"
    >
      <p class="my-4">Hi {{ user.username }}! You have a incomming call</p>
    </b-modal>
  </div>
</template>

<script>
import { Client } from '@stomp/stompjs'
import { OpenVidu } from 'openvidu-browser'
import { mapState } from 'vuex'
import UserVideo from '~/components/UserVideo'

export default {
  name: 'HomePage',
  components: {
    UserVideo,
  },
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
      OV: undefined,
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      sessionId: undefined,
      inCommingConversationId: undefined,
      modalShow: false,
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
        const data = JSON.parse(message.body)
        this.inCommingConversationId = data.conversationId
        this.modalShow = true
      })
    }
    client.onStompError = (frame) => {}
    client.activate()
  },

  methods: {
    async getToken(conversationId) {
      this.modalShow = false
      try {
        const response = await this.$axios.$post(
          '/conversations/' + conversationId + '/generate'
        )
        this.joinSession(response.data)
      } catch (error) {
        console.log(error)
      }
    },

    joinSession(token) {
      // --- Get an OpenVidu object ---
      this.OV = new OpenVidu()
      // --- Init a session ---
      this.session = this.OV.initSession()
      // --- Specify the actions when events take place in the session ---
      // On every new Stream received...
      this.session.on('streamCreated', ({ stream }) => {
        const subscriber = this.session.subscribe(stream)
        this.subscribers.push(subscriber)
      })
      // On every Stream destroyed...
      this.session.on('streamDestroyed', ({ stream }) => {
        const index = this.subscribers.indexOf(stream.streamManager, 0)
        if (index >= 0) {
          this.subscribers.splice(index, 1)
        }
      })

      this.session
        .connect(token, {
          clientData: this.user.username,
        })
        .then(() => {
          // --- Get your own camera stream with the desired properties ---
          const publisher = this.OV.initPublisher(undefined, {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, // The source of video. If undefined default webcam
            publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: true, // Whether you want to start publishing with your video enabled or not
            resolution: '640x480', // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: 'APPEND', // How the video is inserted in the target element 'video-container'
            mirror: false, // Whether to mirror your local video or not
          })

          this.mainStreamManager = publisher
          this.publisher = publisher

          // --- Publish your stream ---

          this.session.publish(this.publisher)
        })
        .catch((error) => {
          console.log(
            'There was an error connecting to the session:',
            error.code,
            error.message
          )
        })
    },
    updateMainVideoStreamManager(stream) {
      if (this.mainStreamManager === stream) return
      this.mainStreamManager = stream
    },
  },
}
</script>
