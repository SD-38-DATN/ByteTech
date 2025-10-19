
import ThongTinCaNhan from "../views/thongtincanhan/ThongTinCaNhan.vue"
const caNhanRouter = {
  path: 'thong-tin-ca-nhan',       // note: không có '/' đầu, vì là child của /admin
  name: 'ThongTinCaNhan',
  component: ThongTinCaNhan
}

export default caNhanRouter