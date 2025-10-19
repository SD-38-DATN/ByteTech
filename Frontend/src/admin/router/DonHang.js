import DonHang from "../views/donhang/DonHang.vue"
const donHangRouter = {
  path: 'don-hang',       // note: không có '/' đầu, vì là child của /admin
  name: ' DonHang',
  component:  DonHang
}

export default donHangRouter