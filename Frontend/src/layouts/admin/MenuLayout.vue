<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <i class="fas fa-cog text-blue-600"></i>
          <h4>ADMIN PANEL</h4>
        </div>
        <div class="user-info">
          <i class="fas fa-user-circle text-gray-500"></i>
          <span>Quản trị viên</span>
        </div>
      </div>

      <nav class="nav-menu">
        <ul>
          <!-- Dashboard -->
          <li>
            <RouterLink to="/admin/don-hang" class="nav-link">
              <i class="fas fa-chart-line"></i>
              <span>Dashboard</span>
            </RouterLink>
          </li>

          <!-- Bán hàng -->
          <li>
            <RouterLink to="/admin/ban-hang" class="nav-link">
              <i class="fas fa-cash-register"></i>
              <span>Bán hàng</span>
            </RouterLink>
          </li>

          <!-- Sản phẩm -->
          <li class="has-submenu">
            <a @click="toggleSubmenu('sanpham')" class="nav-link">
              <i class="fas fa-box"></i>
              <span>Sản phẩm</span>
              <i class="fas fa-chevron-down arrow" :class="{'rotated': openSubmenus.sanpham}"></i>
            </a>
            <ul class="submenu" :class="{'open': openSubmenus.sanpham}">
              <li><RouterLink to="/admin/danh-muc-san-pham">Danh mục sản phẩm</RouterLink></li>
              <li><RouterLink to="/admin/san-pham">Sản phẩm</RouterLink></li>
              <li><RouterLink to="/admin/bien-the-san-pham">IMEI Sản Phẩm</RouterLink></li>
               <li><RouterLink to="/admin/thong-so">Thông số Sản Phẩm</RouterLink></li>
            </ul>
          </li>

          <!-- Phụ kiện -->
          <li class="has-submenu">
          <a @click="toggleSubmenu('phukien')" class="nav-link">
            <i class="fas fa-puzzle-piece"></i>
            <span>Phụ kiện</span>
            <i class="fas fa-chevron-down arrow" :class="{'rotated': openSubmenus.phukien}"></i>
          </a>
          <ul class="submenu" :class="{'open': openSubmenus.phukien}">
            <li><RouterLink to="/admin/danh-muc-phu-kien">Danh mục phụ kiện</RouterLink></li>
            <li><RouterLink to="/admin/phu-kien">Phụ kiện</RouterLink></li>
            <li><RouterLink to="/admin/imei-phu-kien">IMEI/Serial Phụ kiện</RouterLink></li> 
            <li><RouterLink to="/admin/thong-so-phu-kien">Thông số Phụ kiện</RouterLink></li>
          </ul>
        </li>

          <!-- Thống kê doanh thu -->
          <li>
            <RouterLink to="/admin/thongke" class="nav-link">
              <i class="fas fa-chart-bar"></i>
              <span>Thống kê doanh thu</span>
            </RouterLink>
          </li>

          <!-- Khuyến mãi -->
          <li>
            <RouterLink to="/admin/khuyen-mai" class="nav-link">
              <i class="fas fa-tags"></i>
              <span>Khuyến mãi</span>
            </RouterLink>
          </li>

          <!-- Voucher -->
          <li>
            <RouterLink to="/admin/voucher" class="nav-link">
              <i class="fas fa-ticket-alt"></i>
              <span>Voucher</span>
            </RouterLink>
          </li>

          <!-- Khách hàng -->
          <li>
            <RouterLink to="/admin/khach-hang" class="nav-link">
              <i class="fas fa-users"></i>
              <span>Khách hàng</span>
            </RouterLink>
          </li>

          <!-- Nhân viên -->
          <li>
            <RouterLink to="/admin/nhan-vien" class="nav-link">
              <i class="fas fa-user-tie"></i>
              <span>Nhân viên</span>
            </RouterLink>
          </li>
        </ul>
      </nav>

      <!-- Logout -->
      <div class="sidebar-footer">
        <RouterLink to="/admin/dang-xuat" class="logout-btn">
          <i class="fas fa-sign-out-alt"></i>
          <span>Đăng xuất</span>
        </RouterLink>
      </div>
    </aside>
<br></br>

<br></br>

    <!-- Main Content -->
    
  </div>
 <div style="width: 100%;">
   <main class="main-content" >
      <slot></slot>
    </main>
 </div>
</template>

<script setup>
import { reactive } from 'vue'
import { RouterLink } from 'vue-router'

// Quản lý submenu
const openSubmenus = reactive({
  sanpham: false,
  phukien: false
})

// Toggle submenu
const toggleSubmenu = (menuName) => {
  openSubmenus[menuName] = !openSubmenus[menuName]
  
  // Đóng submenu khác
  Object.keys(openSubmenus).forEach(key => {
    if (key !== menuName) {
      openSubmenus[key] = false
    }
  })
}
</script>

<style scoped>

/* Layout chính */
.admin-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  height: 100vh;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* Sidebar */
.sidebar {
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  display: grid;
  grid-template-rows: auto 1fr auto;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.08);
}

/* Header */
.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid #f3f4f6;
}

.logo {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.logo h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #374151;
  letter-spacing: 0.5px;
}

.logo i {
  font-size: 20px;
}

.user-info {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 8px;
  align-items: center;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  font-size: 14px;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

/* Navigation */
.nav-menu {
  padding: 20px 16px;
  overflow-y: auto;
}

.nav-menu ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-menu li {
  margin-bottom: 3px;
}

/* Indicator cho menu có submenu */
.has-submenu > .nav-link {
  position: relative;
}

.has-submenu > .nav-link::before {
  content: '';
  position: absolute;
  left: 4px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 3px;
  background: #3b82f6;
  border-radius: 50%;
  opacity: 0.6;
}

/* Style cơ bản cho nav-link */
.nav-link {
  display: grid;
  grid-template-columns: 20px 1fr auto;
  gap: 12px;
  align-items: center;
  padding: 14px 16px;
  color: #64748b;
  text-decoration: none;
  border-radius: 10px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  font-size: 14px;
  position: relative;
  cursor: pointer;
  border: 2px solid transparent;
}

/* Trạng thái normal */
.nav-link {
  background: transparent;
}

/* Trạng thái hover */
.nav-link:hover {
  background: #f1f5f9;
  color: #475569;
  transform: translateX(2px);
  border-color: #e2e8f0;
}

/* Trạng thái active (đang chọn) */
.nav-link.router-link-active {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: #ffffff;
  border-color: #2563eb;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
  font-weight: 600;
}

.nav-link.router-link-active i {
  color: #ffffff;
}

/* Trạng thái click/pressed */
.nav-link:active {
  transform: translateX(1px) scale(0.98);
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.nav-link.router-link-active:active {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  transform: translateX(1px) scale(0.98);
}

/* Icon styling */
.nav-link i {
  font-size: 16px;
  text-align: center;
  transition: all 0.2s ease;
}

.nav-link:hover i {
  transform: scale(1.1);
}

/* Submenu Arrow với animation */
.arrow {
  font-size: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #94a3b8;
}

.has-submenu .nav-link:hover .arrow {
  color: #64748b;
  transform: scale(1.2);
}

.arrow.rotated {
  transform: rotate(180deg);
  color: #3b82f6;
}

/* Submenu với animation mượt mà */
.submenu {
  list-style: none;
  margin: 0;
  padding: 0;
  max-height: 0;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 8px;
  margin-top: 6px;
  border: 1px solid transparent;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
}

.submenu.open {
  max-height: 250px;
  padding: 12px 0;
  border-color: #e2e8f0;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.08);
}

.submenu li {
  margin: 2px 0;
}

/* Submenu links với trạng thái rõ ràng */
.submenu a {
  display: block;
  padding: 10px 16px 10px 52px;
  color: #64748b;
  text-decoration: none;
  font-size: 13px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 6px;
  margin: 0 8px;
  position: relative;
  font-weight: 500;
  border: 1px solid transparent;
}

/* Submenu hover */
.submenu a:hover {
  background: #e2e8f0;
  color: #475569;
  transform: translateX(4px);
  border-color: #cbd5e1;
}

/* Submenu active */
.submenu a.router-link-active {
  background: linear-gradient(135deg, #1d4ed8 0%, #1e40af 100%);
  color: #ffffff;
  border-color: #2563eb;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(29, 78, 216, 0.25);
}

/* Submenu pressed */
.submenu a:active {
  transform: translateX(2px) scale(0.98);
  background: #cbd5e1;
}

.submenu a.router-link-active:active {
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  transform: translateX(2px) scale(0.98);
}

/* Indicator cho submenu items */
.submenu a::before {
  content: '◦';
  position: absolute;
  left: 32px;
  color: #94a3b8;
  font-size: 12px;
  transition: all 0.2s ease;
}

.submenu a:hover::before {
  color: #64748b;
  transform: scale(1.2);
}

.submenu a.router-link-active::before {
  content: '●';
  color: #ffffff;
}

/* Footer */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #f3f4f6;
}

/* Logout button với styling đặc biệt */
.logout-btn {
  display: grid;
  grid-template-columns: 20px 1fr;
  gap: 12px;
  align-items: center;
  padding: 14px 16px;
  color: #dc2626;
  text-decoration: none;
  border-radius: 10px;
  border: 2px solid #fecaca;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 600;
  font-size: 14px;
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  position: relative;
  overflow: hidden;
}

.logout-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(220, 38, 38, 0.1), transparent);
  transition: left 0.5s ease;
}

.logout-btn:hover::before {
  left: 100%;
}

.logout-btn:hover {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border-color: #f87171;
  color: #b91c1c;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2);
}

.logout-btn:active {
  transform: translateY(0) scale(0.98);
  background: linear-gradient(135deg, #fecaca 0%, #fca5a5 100%);
}

/* Main Content */
.main-content {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  overflow-y: auto;
  padding: 24px;
  min-height: 100vh;
}

/* Focus states cho accessibility */
.nav-link:focus,
.submenu a:focus,
.logout-btn:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

/* Animation cho menu mở */
.has-submenu.menu-opening .nav-link {
  background: #f1f5f9;
  border-color: #e2e8f0;
}

/* Responsive */
@media (max-width: 1024px) {
  .admin-layout {
    grid-template-columns: 260px 1fr;
  }
}

@media (max-width: 768px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    position: fixed;
    top: 0;
    left: -280px;
    width: 280px;
    height: 100vh;
    z-index: 1000;
    transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 0 25px rgba(0, 0, 0, 0.15);
  }
  
  .sidebar.open {
    left: 0;
  }
  
  .main-content {
    padding: 16px;
  }
}

/* Scrollbar với styling tốt hơn */
.nav-menu::-webkit-scrollbar {
  width: 6px;
}

.nav-menu::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 3px;
}

.nav-menu::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
  border-radius: 3px;
}

.nav-menu::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
}

/* Dark mode support (optional) */
@media (prefers-color-scheme: dark) {
  .sidebar {
    background: #1e293b;
    border-right-color: #334155;
  }
  
  .logo h4 {
    color: #f1f5f9;
  }
  
  .user-info {
    background: #334155;
    color: #cbd5e1;
    border-color: #475569;
  }
  
  .nav-link {
    color: #cbd5e1;
  }
  
  .nav-link:hover {
    background: #334155;
    color: #f1f5f9;
  }
}
</style>