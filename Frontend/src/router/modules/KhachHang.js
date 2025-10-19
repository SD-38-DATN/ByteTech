import KhachHang from "@/views/admin/khachhang/KhachHang.vue"
const khachHangRouter = {
  path: 'khach-hang',       // note: không có '/' đầu, vì là child của /admin
  name: 'KhachHang',
  component: KhachHang
}

export default khachHangRouter