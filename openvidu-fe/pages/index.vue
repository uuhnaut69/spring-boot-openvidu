<template>
  <div class="container">
    <div v-if="!session" class="row mt-4">
      <div class="col-3">
        <h5 class="text-white">List active users</h5>
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
        <h5 class="text-white">Your conversation list</h5>
        <div class="row">
          <b-card
            v-for="conversation in conversations"
            :key="conversation.id"
            :title="conversation.title"
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
            <div class="w-100">
              <b-button
                variant="primary"
                size="sm"
                @click="getToken(conversation.id, conversation.title)"
                >Make a call</b-button
              >
            </div>
          </b-card>
        </div>
      </div>
    </div>

    <div v-if="session">
      <div class="row mt-4" style="justify-content: space-between">
        <h3 class="text-white">{{ conversationTitle }}</h3>
        <b-button-toolbar>
          <b-button-group class="mx-1">
            <b-button
              :class="isEnabledCamera ? '' : 'active'"
              variant="warning"
              @click="toggleCamera"
              ><b-icon
                :icon="isEnabledCamera ? 'camera-video' : 'camera-video-off'"
                :variant="isEnabledCamera ? '' : 'danger'"
            /></b-button>
            <b-button
              :class="isEnabledMic ? '' : 'active'"
              variant="warning"
              @click="toggleMic"
              ><b-icon
                :icon="isEnabledMic ? 'mic' : 'mic-mute'"
                :variant="isEnabledMic ? '' : 'danger'"
            /></b-button>
            <b-button
              :class="isEnabledScreenShare ? '' : 'active'"
              variant="warning"
              @click="toggleScreenShare"
              ><b-icon
                :icon="isEnabledScreenShare ? 'display' : 'display-fill'"
                :variant="isEnabledScreenShare ? '' : 'danger'"
            /></b-button>
            <b-button variant="warning" @click="revokeToken"
              ><b-icon icon="power" variant="danger"
            /></b-button>
          </b-button-group>
        </b-button-toolbar>
      </div>
      <hr />
      <div class="row mt-4">
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
        usernameList: [],
      },
      wsClient: undefined,
      isEnabledCamera: true,
      isEnabledMic: true,
      isEnabledScreenShare: false,
    }
  },
  computed: {
    ...mapState('auth', ['loggedIn', 'user']),
  },
  created() {
    this.wsClient = new Client({
      brokerURL: process.env.wsUrl,
      connectHeaders: {
        login: process.env.wsUsername,
        passcode: process.env.wsPassword,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })
    this.wsClient.onConnect = (frame) => {
      this.wsClient.subscribe(
        '/topic/' + this.user.username + '.call-event',
        (message) => {
          const data = JSON.parse(message.body)
          this.inCommingConversationId = data.conversationId
          this.conversationTitle = data.conversationTitle
          this.inCommingCallModalShow = true
        }
      )
      this.wsClient.subscribe(
        '/topic/' + this.user.username + '.conversations',
        (message) => {
          const data = JSON.parse(message.body)
          if (data.type === 'CREATE') {
            this.conversations.unshift(data.data)
          } else if (data.type === 'DELETE') {
            this.conversations = this.conversations.filter(
              (conversation) => conversation.id !== data.data.id
            )
          }
        }
      )
    }
    this.wsClient.onStompError = (frame) => {}
    this.wsClient.activate()
  },

  beforeDestroy() {
    if (this.wsClient !== undefined) {
      this.wsClient.deactivate()
    }
    this.revokeToken()
  },

  methods: {
    async createConversation(conversationReq) {
      await this.$axios.$post('/conversations', {
        title: conversationReq.title,
        members: conversationReq.usernameList,
        imageUrl: conversationReq.imageUrl,
      })
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
                publishAudio: this.isEnabledMic, // Whether you want to start publishing with your audio unmuted or not
                publishVideo: this.isEnabledCamera, // Whether you want to start publishing with your video enabled or not
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
    toggleCamera() {
      this.isEnabledCamera = !this.isEnabledCamera
      this.publisher.publishVideo(this.isEnabledCamera)
    },
    toggleMic() {
      this.isEnabledMic = !this.isEnabledMic
      this.publisher.publishAudio(this.isEnabledMic)
    },
    toggleScreenShare() {
      this.isEnabledScreenShare = !this.isEnabledScreenShare
    },
  },
}
</script>
