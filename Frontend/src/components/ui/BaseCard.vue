<template>
  <div :class="cardClasses">
    <div v-if="hasHeader" class="card-header" :class="headerClass">
      <slot name="header">
        <h5 class="mb-0">{{ title }}</h5>
      </slot>
    </div>
    
    <img v-if="image" :src="image" class="card-img-top" :alt="imageAlt">
    
    <div class="card-body" :class="bodyClass">
      <h5 v-if="!hasHeader && title" class="card-title">{{ title }}</h5>
      <h6 v-if="subtitle" class="card-subtitle mb-2 text-muted">{{ subtitle }}</h6>
      <slot></slot>
    </div>
    
    <div v-if="hasFooter" class="card-footer" :class="footerClass">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
import { computed, useSlots } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  subtitle: {
    type: String,
    default: ''
  },
  image: {
    type: String,
    default: ''
  },
  imageAlt: {
    type: String,
    default: 'Card image'
  },
  variant: {
    type: String,
    default: '',
    validator: (value) => ['', 'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'light', 'dark'].includes(value)
  },
  border: {
    type: String,
    default: '',
    validator: (value) => ['', 'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'light', 'dark'].includes(value)
  },
  shadow: {
    type: Boolean,
    default: false
  },
  hover: {
    type: Boolean,
    default: false
  },
  bodyClass: {
    type: String,
    default: ''
  },
  headerClass: {
    type: String,
    default: ''
  },
  footerClass: {
    type: String,
    default: ''
  }
})

const slots = useSlots()

const hasHeader = computed(() => slots.header || props.title)
const hasFooter = computed(() => slots.footer)

const cardClasses = computed(() => {
  const classes = ['card']
  
  if (props.variant) classes.push(`text-bg-${props.variant}`)
  if (props.border) classes.push(`border-${props.border}`)
  if (props.shadow) classes.push('shadow')
  if (props.hover) classes.push('card-hover')
  
  return classes.join(' ')
})
</script>

<style scoped>
.card-hover {
  transition: all 0.3s ease;
  cursor: pointer;
}

.card-hover:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1) !important;
}
</style>

