<template>
  <Transition name="alert">
    <div v-if="modelValue" :class="alertClasses" role="alert">
      <i v-if="icon" :class="iconClass" class="me-2"></i>
      <div class="flex-grow-1">
        <strong v-if="title" class="d-block mb-1">{{ title }}</strong>
        <slot>{{ message }}</slot>
      </div>
      <button 
        v-if="dismissible" 
        type="button" 
        class="btn-close" 
        @click="close"
        aria-label="Close"
      ></button>
    </div>
  </Transition>
</template>

<script setup>
import { computed, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: true
  },
  variant: {
    type: String,
    default: 'info',
    validator: (value) => ['primary', 'secondary', 'success', 'danger', 'warning', 'info', 'light', 'dark'].includes(value)
  },
  title: {
    type: String,
    default: ''
  },
  message: {
    type: String,
    default: ''
  },
  icon: {
    type: Boolean,
    default: true
  },
  dismissible: {
    type: Boolean,
    default: true
  },
  autoDismiss: {
    type: Number,
    default: 0 // 0 means no auto dismiss
  }
})

const emit = defineEmits(['update:modelValue', 'close'])

const alertClasses = computed(() => {
  return [
    'alert',
    `alert-${props.variant}`,
    props.dismissible ? 'alert-dismissible fade show' : '',
    'd-flex align-items-center'
  ].join(' ')
})

const iconClass = computed(() => {
  const icons = {
    success: 'bi bi-check-circle-fill',
    danger: 'bi bi-exclamation-triangle-fill',
    warning: 'bi bi-exclamation-circle-fill',
    info: 'bi bi-info-circle-fill',
    primary: 'bi bi-star-fill',
    secondary: 'bi bi-app',
    light: 'bi bi-lightbulb',
    dark: 'bi bi-moon-fill'
  }
  return icons[props.variant] || 'bi bi-info-circle-fill'
})

const close = () => {
  emit('update:modelValue', false)
  emit('close')
}

// Auto dismiss
watch(() => props.modelValue, (newVal) => {
  if (newVal && props.autoDismiss > 0) {
    setTimeout(() => {
      close()
    }, props.autoDismiss)
  }
})
</script>

<style scoped>
.alert {
  border-radius: 0.5rem;
  border-left: 4px solid currentColor;
}

/* Alert transitions */
.alert-enter-active,
.alert-leave-active {
  transition: all 0.3s ease;
}

.alert-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.alert-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>

