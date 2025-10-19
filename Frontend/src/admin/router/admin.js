//  import FomAdmin from "../layout/FomAdmin.vue"
// import HomeAdmin from "../views/HomeAdmin.vue"
// import banHangRouter from "./BanHang"
// import danhMucRouter from "./DanhMuc"
// import khachHangRouter from "./KhachHang"
// import khuyenMaiRouter from "./khuyanMai"
// import nhanVienRouter from "./NhanVien"
// import sanPhamRouter from "./SanPham"
// import caNhanRouter from "./ThongTinCaNhan"
// import voucherRouter from "./Vouvher"

// // Tổng hơp cá router cua admin

// const AdminRouter = {
//     path: '/admin',
//     component: FomAdmin,
//     children:[
//         {
//       path: '', // để sau nay cho thêm sửa xóa
//       name: 'HomeAdmin',
//       component: HomeAdmin
//         } ,
//         banHangRouter,
//         danhMucRouter,
//         khachHangRouter,
//         khuyenMaiRouter,
//         nhanVienRouter,
//         caNhanRouter,
//         sanPhamRouter,
//         voucherRouter
//     ] 
// }
// export default AdminRouter 

import FomAdmin from "../layout/FomAdmin.vue"
import HomeAdmin from "../views/HomeAdmin.vue"
import banHangRouter from "./BanHang"
import khachHangRouter from "./KhachHang"
import khuyenMaiRouter from "./KhuyenMai"
import nhanVienRouter from "./NhanVien"
import sanPhamRouter from "./SanPham"
import phukienRouter from "./PhuKien"

import caNhanRouter from "./ThongTinCaNhan"
import voucherRouter from "./Voucher"
import phuKienRouter from "./PhuKien"

import thongKeRouter from "./ThongKe"
import donHangRouter from "./DonHang"
const AdminRouter = {
  path: '/admin',
  component: FomAdmin,
  children: [
    {
      path: '',
      name: 'HomeAdmin',
      component: HomeAdmin
    },
    banHangRouter,
    khachHangRouter,
    khuyenMaiRouter,
    nhanVienRouter,
    caNhanRouter,
    sanPhamRouter,  // chứa cả SanPham + DanhMucSanPham
    phuKienRouter,
    thongKeRouter,
    donHangRouter,
    voucherRouter
  ]
}

export default AdminRouter
