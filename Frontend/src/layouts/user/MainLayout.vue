<template>
  <div class="user-layout">
    <router-view />
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background-color: #f8f9fa;
}
</style>

