<template>
  <div class="status-toggle d-flex justify-content-center align-items-center" style="min-height: 38px;">
    <div v-if="loading" class="spinner-border spinner-border-sm text-secondary" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
    <div v-else class="form-check form-switch">
      <input
        class="form-check-input"
        type="checkbox"
        role="switch"
        :id="`switch-${id}`"
        :checked="modelValue"
        @change="$emit('change', $event.target.checked)"
      />
      <label class="form-check-label" :for="`switch-${id}`">
        <span :class="modelValue ? 'text-success' : 'text-danger'" class="fw-semibold">
          {{ modelValue ? 'Hoạt động' : 'Ẩn' }}
        </span>
      </label>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

// Props định nghĩa các dữ liệu component có thể nhận từ bên ngoài
defineProps({
  // modelValue là tên prop mặc định cho v-model
  modelValue: {
    type: Boolean,
    required: true,
  },
  // Trạng thái loading để hiển thị spinner
  loading: {
    type: Boolean,
    default: false,
  },
});

// Emits định nghĩa các sự kiện mà component có thể phát ra
defineEmits(['change']);

// Tạo một ID duy nhất cho mỗi instance của component để liên kết label và input
const id = computed(() => Math.random().toString(36).substring(2, 9));
</script>

<style scoped>
.form-switch .form-check-input {
  width: 3em;
  height: 1.5em;
  cursor: pointer;
}
.form-check-label {
    cursor: pointer;
}
</style>