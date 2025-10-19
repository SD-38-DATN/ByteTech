


<template>
  <div class="admin-layout">
    <header class="admin-header">
      <HeaderLayout />
    </header>

    <main class="main-content-wrapper">
      <MenuLayout>
        <RouterView v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </RouterView>
      </MenuLayout>
    </main>
  </div>
</template>

<script setup>
import HeaderLayout from '@/admin/layout/HeaderLayout.vue'
import MenuLayout from './MenuLayout.vue'
import { RouterView } from 'vue-router'
</script>

<style scoped>
/* Tổng thể layout */
.admin-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f0f2f5; /* Một màu nền nhẹ nhàng, giảm mỏi mắt */
}

/* Header */
.admin-header {
  /* Thêm đổ bóng để tạo chiều sâu, tách biệt header khỏi nội dung */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  /* Đảm bảo header luôn ở trên cùng */
  z-index: 10;
  background-color: #ffffff;
}

/* Vùng chứa chính (bao gồm sidebar và content) */
.main-content-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden; /* Tránh scrollbar kép ở cấp độ này */
}

/* Hiệu ứng chuyển cảnh cho RouterView */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

<style>
/* CSS không "scoped" để có thể style cho phần content bên trong,
  vì MenuLayout và RouterView là các component con.
*/

/* Giả sử MenuLayout của bạn có cấu trúc:
  <aside class="sidebar">...</aside>
  <div class="content-area">
    <slot></slot> </div>
*/

.content-area {
  flex: 1;
  padding: 24px; /* Thêm không gian thở cho nội dung */
  overflow-y: auto; /* Chỉ cho phép cuộn ở khu vực nội dung */
  height: 100%;
}
</style>