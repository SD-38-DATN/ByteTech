import { createRouter, createWebHistory } from "vue-router";
import TrangChu from "../components/TrangChu.vue";
import SanPhamChiTiet from "../components/SanPhamChiTiet.vue";
import Login from "../components/Login.vue";
import Users from "../components/Users.vue";
import Me from "../components/Me.vue";



import GioHang from "../components/GioHang.vue";




import XacNhanDonHang from "../components/XacNhanDonHang.vue";

import DonHang from "../components/DonHang.vue";

import ChiTietPhuKien from '../components/ChiTietPhuKien.vue';
import TrangChuPhuKien from '../components/TrangChuPhuKien.vue';

import DanhMucDienThoai from "../components/DanhMucDienThoai.vue";
import SearchSanPham from "@/components/SearchSanPham.vue";
import Voucher from "@/components/Voucher.vue";
// Import admin & client router riêng
import AdminRouter from '@/admin/router/admin';



const routes = [
  {
    path: "/",
    name: "TrangChu",
    component: TrangChu,
  },
  {
    path: "/sanpham/:id",
    name: "SanPhamChiTiet",
    component: SanPhamChiTiet,
      props: true
  },
  { 
    path: '/login', 
    name: 'Login', 
    component: Login 
  },

  { 
    path: '/user', 
    name: 'Users', 
    component: Users
  },
  {
    path: "/me",
    component: Me,
    beforeEnter: (to, from, next) => {
      const token = localStorage.getItem("token");
      if (!token) {
        if (confirm("Bạn cần đăng nhập để xem thông tin tài khoản. Bạn có muốn đăng nhập ngay không?")) {
          window.location.href = "/login"; // chuyển sang login
        } else {
          alert("Bạn không thể truy cập trang này nếu không đăng nhập."); 
          next(false); // hủy điều hướng, ở lại trang cũ
        }
      } else {
        next(); // có token thì cho vào
      }
    },
  },

  
  { 
    path: '/giohang', 
    name: 'GioHang', 
    component: GioHang
  },

  { 
    path: '/xacnhan', 
    name: 'XacNhanDonHang', 
    component: XacNhanDonHang
  },

   { 
    path: '/donhang', 
    name: 'DonHang', 
    component: DonHang
  },

 
   { path: '/test', name: 'TrangChuPhuKien', component: TrangChuPhuKien },


   
  { path: '/phukien/:id', name: 'ChiTietPhuKien', component: ChiTietPhuKien  , props: true},

  {
    path: "/danh-muc/:maDanhMuc",
    name: "DanhMucDienThoai",
    component: DanhMucDienThoai,
    props: true, // cho phép truyền maDanhMuc vào props
  },

  {
  path: "/search",
  name: "SearchSanPham",
  component: SearchSanPham
},

{
  path: "/voucher",
  name: "Voucher",
  component: Voucher
},


   // Router bên quản lý
  AdminRouter,   // http://localhost:5173/admin

];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Thêm vào đầu file src/router/index.js
import { logoutApi } from '@/service/api';
// =================================================================
// ✨ BẮT ĐẦU KHỐI CODE BẢO VỆ ĐIỀU HƯỚNG (GỌI API LOGOUT)
// =================================================================

function clearAuthData() {
  // Xóa tất cả các thông tin nhạy cảm liên quan đến phiên đăng nhập
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  localStorage.removeItem("role");
  localStorage.removeItem("avatar");
  localStorage.removeItem("id");
  localStorage.removeItem("refreshToken");
}

// ✨ (1) Thêm "async" vào trước hàm callback
router.beforeEach(async (to, from, next) => {
  const userRole = localStorage.getItem('role');

  const isSellerOnUserPage = (userRole === 'ROLE_ADMIN' || userRole === 'ROLE_STAFF') && !to.path.startsWith('/admin');
  const isUserOnAdminPage = (userRole === 'ROLE_USER') && to.path.startsWith('/admin');

  // Gộp chung kịch bản 1 và 2 vì đều xử lý logout
  if (isSellerOnUserPage || isUserOnAdminPage) {
    console.warn(`[AUTH] Unauthorized access by ${userRole}. Logging out.`);
    
    // ✨ (2) Gọi API logout và xử lý lỗi
    try {
      await logoutApi();
      console.log("[AUTH] Server logout successful.");
    } catch (error) {
      console.error("[AUTH] Server logout failed, but proceeding with client-side logout.", error);
    }
    
    // Xóa dữ liệu ở client
    clearAuthData();
    
    // Chuyển hướng về trang đăng nhập
    return next('/login');
    
  } 
  
  // Kịch bản 3: Người dùng chưa đăng nhập cố vào trang admin
  else if (!userRole && to.path.startsWith('/admin')) {
      return next('/login');
  }
  
  // Nếu mọi thứ hợp lệ, cho phép đi tiếp
  else {
    return next();
  }
});

// =================================================================
// ✨ KẾT THÚC KHỐI CODE BẢO VỆ
// =================================================================

export default router;


