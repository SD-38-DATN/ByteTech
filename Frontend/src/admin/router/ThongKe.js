
import ThongKe from "../views/thongkedoanhthu/ThongKe.vue"

const thongKeRouter = {
  path: 'thongke',       // note: không có '/' đầu, vì là child của /admin
  name: 'ThongKe',
  component: ThongKe
}

export default thongKeRouter