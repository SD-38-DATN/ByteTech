<template>
  <div class="base-input-wrapper">
    <label v-if="label" :for="inputId" class="form-label">
      {{ label }}
      <span v-if="required" class="text-danger ms-1">*</span>
    </label>
    
    <div 
      class="input-container" 
      :class="{ 
        'has-error': error, 
        'is-focused': isFocused,
        'is-disabled': disabled
      }"
    >
      <span v-if="prependIcon" class="input-icon prepend-icon">
        <i :class="prependIcon"></i>
      </span>
      
      <input
        :id="inputId"
        ref="inputRef"
        :type="computedType"
        class="form-input"
        :placeholder="placeholder"
        :value="modelValue"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
      />
      
      <button
        v-if="type === 'password' && showPasswordToggle"
        type="button"
        class="password-toggle-btn"
        @click="togglePassword"
        aria-label="Toggle password visibility"
        tabindex="-1"
      >
        <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
      </button>
      
      <span v-if="appendIcon" class="input-icon append-icon">
        <i :class="appendIcon"></i>
      </span>
    </div>
    
    <Transition name="error-slide">
      <div v-if="error" class="error-message">
        <i class="bi bi-exclamation-circle-fill"></i>
        <span>{{ error }}</span>
      </div>
    </Transition>
    
    <small v-if="!error && hint" class="hint-text">
      {{ hint }}
    </small>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  type: {
    type: String,
    default: 'text',
    validator: (value) => ['text', 'password', 'email', 'number', 'tel', 'url', 'search'].includes(value)
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  error: {
    type: String,
    default: ''
  },
  hint: {
    type: String,
    default: ''
  },
  prependIcon: {
    type: String,
    default: ''
  },
  appendIcon: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  required: {
    type: Boolean,
    default: false
  },
  maxlength: {
    type: [String, Number],
    default: null
  },
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg'].includes(value)
  },
  showPasswordToggle: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'blur', 'focus'])

const inputRef = ref(null)
const showPassword = ref(false)
const isFocused = ref(false)

const inputId = computed(() => `input-${Math.random().toString(36).substr(2, 9)}`)

const computedType = computed(() => {
  if (props.type === 'password' && showPassword.value) {
    return 'text'
  }
  return props.type
})

const inputClasses = computed(() => {
  const classes = []
  if (props.size === 'sm') classes.push('input-sm')
  if (props.size === 'lg') classes.push('input-lg')
  return classes.join(' ')
})

const handleInput = (event) => {
  emit('update:modelValue', event.target.value)
}

const handleBlur = (event) => {
  isFocused.value = false
  emit('blur', event)
}

const handleFocus = (event) => {
  isFocused.value = true
  emit('focus', event)
}

const togglePassword = () => {
  showPassword.value = !showPassword.value
}

const focus = () => {
  nextTick(() => {
    inputRef.value?.focus()
  })
}

defineExpose({ focus })
</script>

<style scoped>
.base-input-wrapper {
  margin-bottom: 1rem;
}

.input-group {
  position: relative;
  transition: all 0.3s ease;
}

.input-group.has-error {
  border-color: var(--bs-danger);
}

.input-group-text {
  background-color: #f8f9fa;
  border-color: #dee2e6;
  transition: all 0.3s ease;
}

.input-group:focus-within .input-group-text {
  border-color: #0d6efd;
  background-color: #e7f1ff;
  color: #0d6efd;
}

.form-control.focused {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.password-toggle {
  cursor: pointer;
  user-select: none;
}

.password-toggle:hover {
  background-color: #e9ecef;
}

.invalid-feedback {
  display: flex;
  align-items: center;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.form-label {
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #495057;
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .input-group-text {
    background-color: #2b3035;
    border-color: #495057;
    color: #e0e0e0;
  }
  
  .form-control {
    background-color: #1c1c1e;
    border-color: #495057;
    color: #e0e0e0;
  }
  
  .form-control:focus {
    background-color: #2b3035;
    border-color: #0d6efd;
    color: #e0e0e0;
  }
  
  .form-label {
    color: #e0e0e0;
  }
}
</style>

