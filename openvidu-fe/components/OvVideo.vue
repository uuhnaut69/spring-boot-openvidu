<template>
  <video autoplay />
</template>

<script>
export default {
  name: 'OvVideo',
  props: {
    streamManager: {
      type: Object,
      required: true,
    },
  },
  mounted() {
    this.streamManager.addVideoElement(this.$el)
  },
}
</script>
