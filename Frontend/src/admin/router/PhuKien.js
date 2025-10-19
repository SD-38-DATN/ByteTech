import PhuKien from "../views/phukien/PhuKien.vue"
import DanhMucPhuKien from "../views/phukien/DanhMucPhuKien.vue"
import ThongSoPhuKien from "../views/phukien/ThongSoPhuKien.vue"

import PhuKienIMEI from "../views/phukien/PhuKienIMEI.vue"
import { RouterView } from "vue-router"

const phuKienRouter = {
  path: '',
  component: RouterView,
  children: [
    {
      path: 'phu-kien',
      name: 'PhuKien',
      component: PhuKien
    },
    {
      path: 'danh-muc-phu-kien',  // SỬA LỖI: Bỏ dấu phẩy thừa ở đây
      name: 'DanhMucPhuKien',
      component: DanhMucPhuKien
    },
    {
      path: 'thong-so-phu-kien',
      name: 'ThongSoPhuKien',
      component: ThongSoPhuKien
    },
    {
      path: 'imei-phu-kien',
      name: 'PhuKienIMEI',
      component: PhuKienIMEI
      // meta: { requiresAuth: true, adminOnly: true }
    }
  ]
}

export default phuKienRouter