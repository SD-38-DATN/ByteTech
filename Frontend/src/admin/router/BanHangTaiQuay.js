import { RouterView } from "vue-router";
import BanHang from "../views/banhangtaiquay/banhang/BanHang.vue";
import XuLyDonLuu from "../views/banhangtaiquay/xulydonhangluu/XuLyDonLuu.vue";

const banHangTaiQuayRouter = {
  path: "ban-hang-tai-quay",
  component: RouterView,
  children: [
    {
      path: "ban-hang",
      name: "banhang",
      component: BanHang,
    },
    {
      path: "xu-ly-don-luu", // SỬA LỖI: Bỏ dấu phẩy thừa ở đây
      name: "xuulydonhangluu",
      component: XuLyDonLuu,
    },
  ],
};

export default banHangTaiQuayRouter;
