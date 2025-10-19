<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="modal fade show d-block" tabindex="-1" @click.self="handleBackdropClick">
        <div :class="modalDialogClasses" role="document">
          <div class="modal-content">
            <!-- Header -->
            <div v-if="!hideHeader" class="modal-header" :class="headerClass">
              <slot name="header">
                <h5 class="modal-title">{{ title }}</h5>
              </slot>
              <button 
                v-if="!hideClose" 
                type="button" 
                class="btn-close" 
                @click="close"
                aria-label="Close"
              ></button>
            </div>

            <!-- Body -->
            <div class="modal-body" :class="bodyClass">
              <slot></slot>
            </div>

            <!-- Footer -->
            <div v-if="!hideFooter" class="modal-footer" :class="footerClass">
              <slot name="footer">
                <button type="button" class="btn btn-secondary" @click="close">Đóng</button>
                <button v-if="!hideConfirm" type="button" class="btn btn-primary" @click="confirm">
                  {{ confirmText }}
                </button>
              </slot>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Backdrop -->
    <Transition name="backdrop">
      <div v-if="modelValue" class="modal-backdrop fade show"></div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: 'Modal Title'
  },
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg', 'xl'].includes(value)
  },
  centered: {
    type: Boolean,
    default: false
  },
  scrollable: {
    type: Boolean,
    default: false
  },
  fullscreen: {
    type: Boolean,
    default: false
  },
  hideHeader: {
    type: Boolean,
    default: false
  },
  hideFooter: {
    type: Boolean,
    default: false
  },
  hideClose: {
    type: Boolean,
    default: false
  },
  hideConfirm: {
    type: Boolean,
    default: false
  },
  confirmText: {
    type: String,
    default: 'Xác nhận'
  },
  backdropDismiss: {
    type: Boolean,
    default: true
  },
  headerClass: {
    type: String,
    default: ''
  },
  bodyClass: {
    type: String,
    default: ''
  },
  footerClass: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'close', 'confirm'])

const modalDialogClasses = computed(() => {
  const classes = ['modal-dialog']
  
  if (props.size !== 'md') classes.push(`modal-${props.size}`)
  if (props.centered) classes.push('modal-dialog-centered')
  if (props.scrollable) classes.push('modal-dialog-scrollable')
  if (props.fullscreen) classes.push('modal-fullscreen')
  
  return classes.join(' ')
})

const close = () => {
  emit('update:modelValue', false)
  emit('close')
}

const confirm = () => {
  emit('confirm')
}

const handleBackdropClick = () => {
  if (props.backdropDismiss) {
    close()
  }
}

// Prevent body scroll when modal is open
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
})
</script>

<style scoped>
.modal {
  background-color: rgba(0, 0, 0, 0.5);
}

/* Modal transitions */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-dialog {
  transition: transform 0.3s ease;
}

.modal-enter-from .modal-dialog {
  transform: translateY(-50px);
}

/* Backdrop transitions */
.backdrop-enter-active,
.backdrop-leave-active {
  transition: opacity 0.15s ease;
}

.backdrop-enter-from,
.backdrop-leave-to {
  opacity: 0;
}
</style>

