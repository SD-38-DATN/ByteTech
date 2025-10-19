import SanPham from "@/views/admin/sanpham/SanPham.vue"
import DanhMucSanPham from "@/views/admin/sanpham/DanhMucSanPham.vue"
import ThuocTinhSanPham from "@/views/admin/sanpham/ThuocTinhSanPham.vue"
import QuanLyThongSo from "@/views/admin/sanpham/QuanLyThongSo.vue"
import { RouterView } from "vue-router"

const sanPhamRouter = {
  path: '',
  component: RouterView,
  children: [
    {
      path: 'san-pham',
      name: 'SanPham',
      component: SanPham
    },
    {
      path: 'danh-muc-san-pham',  // ✅ sửa path cho khớp URL bạn đang nhập
      name: 'DanhMucSanPham',
      component: DanhMucSanPham
    },
    {
      path: 'bien-the-san-pham',  // ✅ sửa path cho khớp URL bạn đang nhập
      name: 'ThuocTinhSanPham',
      component: ThuocTinhSanPham
    },
    {
      path: 'thong-so',  // ✅ sửa path cho khớp URL bạn đang nhập
      name: 'QuanLyThongSo',
      component: QuanLyThongSo,
        // meta: { requiresAuth: true, adminOnly: true }
    }
  ]
}

export default sanPhamRouter
