
import BanHang from "../views/banhangtaiquay/banhang/BanHang.vue"


const banHangRouter = {
  path: 'ban-hang',       // note: không có '/' đầu, vì là child của /admin
  name: 'BanHang',
  component: BanHang
}

export default banHangRouter