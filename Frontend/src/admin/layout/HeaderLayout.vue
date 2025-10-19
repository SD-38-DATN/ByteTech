<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";

// --- Import ---
// Sửa dòng 6:
import defaultAvatar from '@/admin/assets/img/bannerLogin.png'; // <-- Thêm đuôi .png

// --- State & Router ---
const router = useRouter();
const username = ref("");
const role = ref("");
const avatarUrl = ref(null);
const isDropdownOpen = ref(false);
const userMenuRef = ref(null); // Ref để theo dõi element menu

// --- Dữ liệu người dùng động ---
// Lấy dữ liệu từ localStorage khi component được tạo
onMounted(() => {
  username.value = localStorage.getItem("username") || "Khách";
  role.value = localStorage.getItem("role") || "";
  avatarUrl.value = localStorage.getItem("avatar"); // Lấy link avatar
  
  // Lắng nghe click bên ngoài để đóng dropdown
  document.addEventListener("click", handleClickOutside);
});

// Dọn dẹp listener khi component bị hủy
onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});

// --- Dữ liệu tính toán (Computed) ---
// Tự động chọn avatar để hiển thị
const avatarSrc = computed(() => {
  return avatarUrl.value || defaultAvatar;
});

// Chuyển đổi 'role' từ mã kỹ thuật sang tên thân thiện
const displayRole = computed(() => {
  switch (role.value) {
    case "ROLE_ADMIN":
      return "Quản trị viên";
    case "ROLE_STAFF":
      return "Nhân viên";
    case "ROLE_USER":
      return "Khách hàng";
    default:
      return "Chưa xác định";
  }
});

// --- Methods ---
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

const logout = () => {
  // Xóa tất cả thông tin đăng nhập khỏi localStorage
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  localStorage.removeItem("role");
  localStorage.removeItem("avatar");
  localStorage.removeItem("id");
  localStorage.removeItem("refreshToken");
  
  // Chuyển hướng về trang đăng nhập
  router.push("/login"); 
};

// Hàm xử lý khi click ra ngoài menu
const handleClickOutside = (event) => {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
    isDropdownOpen.value = false;
  }
};
</script>

<template>
  <header class="header">
    <div class="header-content">
      <div class="logo">
        <img src="@/admin/assets/img/bannerLogin.png" alt="ByteTech Logo" />
        <span class="logo-text">ByteTech Admin</span>
      </div>

      <div class="user-menu" ref="userMenuRef">
        <div class="user-menu-trigger" @click="toggleDropdown">
          <div class="user-info text-end me-3">
            <strong class="user-name">{{ username }}</strong>
            <div class="user-role">{{ displayRole }}</div>
          </div>
          <div class="avatar-container">
            <img :src="avatarSrc" alt="Avatar" class="avatar-img" />
            <span class="online-indicator"></span>
          </div>
        </div>
        
        <transition name="dropdown-fade">
          <div v-if="isDropdownOpen" class="dropdown-menu">
            <RouterLink to="/admin/thong-tin-ca-nhan" class="dropdown-item">
              <i class="bi bi-person-circle"></i>
              <span>Thông tin cá nhân</span>
            </RouterLink>
            <button @click="logout" class="dropdown-item logout">
              <i class="bi bi-box-arrow-right"></i>
              <span>Đăng xuất</span>
            </button>
          </div>
        </transition>
      </div>
    </div>
  </header>
</template>

<style scoped>
/* --- Import Icons --- */
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css");

/* --- Biến màu đồng bộ với trang Login --- */
:root {
  --app-orange: #ff6f00;
  --text-primary: #e0e0e0;
  --text-secondary: #8e8e93;
  --bg-dark: #101012;
  --bg-card: #1c1c1e;
  --border-color: rgba(255, 255, 255, 0.1);
}

/* --- Tổng thể Header --- */
.header {
  height: 70px;
  background-color: var(--bg-card);
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  padding: 0 2rem; /* Thêm padding ngang */
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* --- Logo --- */
.logo {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.logo img {
  max-height: 45px;
  transition: transform 0.3s ease, filter 0.3s ease;
}
.logo:hover img {
  transform: rotate(-10deg) scale(1.05);
  filter: drop-shadow(0 0 10px rgba(255, 111, 0, 0.4));
}
.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 1px;
}

/* --- Khối người dùng và Dropdown --- */
.user-menu {
  position: relative;
}

.user-menu-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 50px;
  transition: background-color 0.3s ease;
}

.user-menu-trigger:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.user-info {
  line-height: 1.4;
}
.user-name {
  font-weight: 700;
  font-size: 0.95rem;
  color: var(--text-primary);
}
.user-role {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

/* --- Avatar --- */
.avatar-container {
  position: relative;
}
.avatar-img {
  height: 45px;
  width: 45px;
  border-radius: 50%;
  border: 2px solid var(--border-color);
  object-fit: cover;
  transition: all 0.3s ease;
}
.user-menu-trigger:hover .avatar-img {
  border-color: var(--app-orange);
  box-shadow: 0 0 15px rgba(255, 111, 0, 0.5);
  transform: scale(1.05);
}
.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 10px;
  height: 10px;
  background-color: #28a745; /* Màu xanh online */
  border-radius: 50%;
  border: 2px solid var(--bg-card);
}

/* --- Menu Dropdown --- */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 10px); /* Vị trí dưới avatar */
  right: 0;
  width: 220px;
  background-color: var(--bg-card);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  padding: 0.5rem 0;
  display: flex;
  flex-direction: column;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  text-decoration: none;
  color: var(--text-primary);
  font-size: 0.9rem;
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.dropdown-item i {
  font-size: 1.1rem;
  color: var(--text-secondary);
  transition: color 0.2s ease;
}

.dropdown-item:hover {
  background-color: var(--app-orange);
  color: #fff;
}

.dropdown-item:hover i {
  color: #fff;
}

.dropdown-item.logout:hover {
  background-color: #ff4d4f; /* Màu đỏ cho logout */
}

/* --- Hiệu ứng chuyển động cho dropdown --- */
.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>