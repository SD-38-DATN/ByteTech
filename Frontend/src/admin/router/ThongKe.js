import { RouterView } from "vue-router";
import ThongKeTongQuan from "../views/thongke/ThongKeTongQuan.vue";
import ThongKeDoanhThu from "../views/thongke/ThongKeDoanhThu.vue";
import ThongKeDonHang from "../views/thongke/ThongKeDonHang.vue";
import ThongKeSanPham from "../views/thongke/ThongKeSanPham.vue";

const thongKeRouter = {
  path: "thong-ke",
  component: RouterView,
  children: [
    {
      path: "tong-quan",
      name: "thongketongquan",
      component: ThongKeTongQuan,
    },
    {
      path: "doanh-thu",
      name: "thongkedoanhthu",
      component: ThongKeDoanhThu,
    },
    {
      path: "don-hang",
      name: "thongkedonhang",
      component: ThongKeDonHang,
    },
    {
      path: "san-pham",
      name: "thongkesanpham",
      component: ThongKeSanPham,
    },
  ],
};

export default thongKeRouter;
