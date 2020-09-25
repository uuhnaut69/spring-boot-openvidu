<template>
  <div v-if="streamManager">
    <div>
      <h5 class="text-white">{{ clientData }}</h5>
    </div>
    <ov-video :stream-manager="streamManager" />
  </div>
</template>

<script>
import OvVideo from '~/components/OvVideo'
export default {
  name: 'UserVideo',
  components: {
    OvVideo,
  },
  props: {
    // eslint-disable-next-line vue/require-default-prop
    streamManager: Object,
  },
  computed: {
    clientData() {
      return JSON.parse(
        this.streamManager.stream.connection.data.split('%/%')[0]
      ).clientData
    },
  },
}
</script>
