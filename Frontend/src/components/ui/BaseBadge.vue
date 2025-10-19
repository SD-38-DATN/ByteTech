<template>
  <span :class="badgeClasses">
    <i v-if="icon" :class="icon" class="me-1"></i>
    <slot></slot>
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'success', 'danger', 'warning', 'info', 'light', 'dark'].includes(value)
  },
  pill: {
    type: Boolean,
    default: false
  },
  icon: {
    type: String,
    default: ''
  }
})

const badgeClasses = computed(() => {
  const classes = ['badge', `bg-${props.variant}`]
  if (props.pill) classes.push('rounded-pill')
  return classes.join(' ')
})
</script>

<style scoped>
.badge {
  font-weight: 500;
  padding: 0.35em 0.65em;
}
</style>

