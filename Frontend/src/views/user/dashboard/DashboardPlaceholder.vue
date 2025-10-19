<template>
  <div class="bg-white rounded shadow-sm p-4">
    <h2 class="mb-3">{{ title }}</h2>
    <p>Nội dung cho mục này đang được phát triển. Vui lòng quay lại sau.</p>
    <p>aaaaaaaaaaaaaaaaaaa</p>
  </div>
</template>

<script>
export default {
  name: 'DashboardPlaceholder',
  props: {
    title: {
      type: String,
      default: 'Chức năng'
    }
  }
}
</script>