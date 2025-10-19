import NhanVien from "../views/nhanvien/NhanVien.vue"

const nhanVienRouter = {
  path: 'nhan-vien',       // note: không có '/' đầu, vì là child của /admin
  name: 'NhanVien',
  component: NhanVien
}

export default nhanVienRouter