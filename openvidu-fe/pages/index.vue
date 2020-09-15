<template>
  <div class="container">
    <div v-if="!session" class="row mt-4">
      <div class="col-3">
        <h5>List active users</h5>
        <b-button
          variant="success"
          size="sm"
          @click="addConversationModalShow = true"
        >
          Add new conversation
        </b-button>
        <b-list-group class="mt-3" style="max-width: 300px">
          <b-list-group-item
            v-for="user in users"
            :key="user.id"
            class="d-flex align-items-center"
          >
            <b-avatar
              variant="info"
              :src="user.avatarUrl"
              class="mr-3"
            ></b-avatar>
            <span class="mr-auto"> {{ user.username }}</span>
          </b-list-group-item>
        </b-list-group>
      </div>

      <div class="col-9">
        <h5>Your conversation list</h5>
        <div class="row">
          <b-card
            v-for="conversation in conversations"
            :key="conversation.id"
            :title="conversation.title"
            :img-src="conversation.imageUrl"
            img-alt="Image"
            img-top
            tag="article"
            class="col-3 mr-3 mt-3 text-truncate"
          >
            <b-avatar-group class="mb-3">
              <b-avatar
                v-for="member in conversation.members"
                :key="member.id"
                :src="member.avatarUrl"
                variant="info"
              ></b-avatar>
            </b-avatar-group>
            <b-button
              variant="primary"
              @click="getToken(conversation.id, conversation.title)"
              >Make a call</b-button
            >
          </b-card>
        </div>
      </div>
    </div>

    <div v-if="session">
      <div class="session-header row mt-4 w-100">
        <h3>{{ conversationTitle }}</h3>
        <div>
          <b-button class="mr-2" variant="success" @click="revokeToken"
            >Add user</b-button
          >
          <b-button variant="danger" @click="revokeToken">Leave call</b-button>
        </div>
      </div>
      <div class="row">
        <div id="main-video" class="col-6">
          <user-video :stream-manager="mainStreamManager" />
        </div>
        <div id="video-container" class="col-6">
          <user-video
            v-for="(sub, index) in subscribers"
            :key="index"
            :stream-manager="sub"
            @click.native="updateMainVideoStreamManager(sub)"
          />
        </div>
      </div>
    </div>

    <!-- Incoming Call Popup -->
    <b-modal
      v-model="inCommingCallModalShow"
      title="Incomming Call"
      ok-title="Join"
      cancel-title="Decline"
      cancel-variant="danger"
      @ok="getToken(inCommingConversationId, conversationTitle)"
      @cancel="inCommingCallModalShow = false"
    >
      <p class="my-4">
        Hi {{ user.username }}! You have a incomming call from
        {{ conversationTitle }}
      </p>
    </b-modal>
    <!-- End Incoming Call Popup -->

    <!-- Modal add new conversation -->
    <b-modal
      v-model="addConversationModalShow"
      title="Create new conversation"
      ok-title="Create"
      cancel-title="Cancel"
      cancel-variant="danger"
      @ok="createConversation(conversationReq)"
      @cancel="addConversationModalShow = false"
    >
      <b-form>
        <b-form-group>
          <b-form-input
            v-model="conversationReq.title"
            required
            placeholder="Enter title"
          >
          </b-form-input>
        </b-form-group>
        <b-form-group>
          <b-form-input
            v-model="conversationReq.imageUrl"
            required
            placeholder="Enter image url"
          >
          </b-form-input>
        </b-form-group>

        <b-form-group>
          <b-form-tags
            v-model="conversationReq.usernameList"
            placeholder="Add username"
          ></b-form-tags>
        </b-form-group>
      </b-form>
    </b-modal>

    <!-- End Modal add new conversation -->
  </div>
</template>

<script>
import { Client } from '@stomp/stompjs'
import { mapState } from 'vuex'
import UserVideo from '~/components/UserVideo'

export default {
  name: 'HomePage',
  components: {
    UserVideo,
  },
  async asyncData({ $axios }) {
    try {
      const conversationResp = await $axios.$get('/my-conversations')
      const userResp = await $axios.$get('/users')
      return { conversations: conversationResp.data, users: userResp.data }
    } catch (error) {
      console.log(error)
    }
  },
  data() {
    return {
      inCommingCallModalShow: false,
      addConversationModalShow: false,
      conversations: [],
      users: [],
      OV: undefined,
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      sessionId: undefined,
      inCommingConversationId: undefined,
      conversationTitle: undefined,
      token: undefined,
      conversationReq: {
        title: undefined,
        imageUrl: undefined,
        usernameList: [],
      },
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
        this.conversationTitle = data.conversationTitle
        this.inCommingCallModalShow = true
      })
    }
    client.onStompError = (frame) => {}
    client.activate()
  },

  methods: {
    async createConversation(conversationReq) {
      const response = await this.$axios.$post('/conversations', {
        title: conversationReq.title,
        members: conversationReq.usernameList,
        imageUrl: conversationReq.imageUrl,
      })
      this.conversations.unshift(response.data)
      this.conversationReq.title = undefined
      this.conversationReq.users = []
      this.conversationReq.imageUrl = undefined
    },
    async getToken(conversationId, conversationTitle) {
      this.inCommingCallModalShow = false
      this.conversationTitle = conversationTitle
      this.conversationId = conversationId
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
      if (process.client) {
        import('openvidu-browser').then((OpenViduModule) => {
          this.OV = new OpenViduModule.OpenVidu()
          this.session = this.OV.initSession()
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
          window.addEventListener('beforeunload', this.leaveSession)
        })
      }
    },
    leaveSession() {
      if (this.session) this.session.disconnect()

      this.session = undefined
      this.mainStreamManager = undefined
      this.publisher = undefined
      this.subscribers = []
      this.OV = undefined
      this.sessionId = undefined
      this.conversationId = undefined
      this.conversationTitle = undefined

      window.removeEventListener('beforeunload', this.leaveSession)
    },
    updateMainVideoStreamManager(stream) {
      if (this.mainStreamManager === stream) return
      this.mainStreamManager = stream
    },
    revokeToken() {
      this.$axios.$post('/conversations/' + this.conversationId + '/revoke/')
      this.leaveSession()
    },
  },
}
</script>

<style>
.session-header {
  display: flex;
  justify-content: space-between;
}
</style>
