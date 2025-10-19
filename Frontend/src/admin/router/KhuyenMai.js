import KhuyenMai from "../views/khuyenmai/KhuyenMai.vue"


const khuyenMaiRouter = {
  path: 'khuyen-mai',       // note: không có '/' đầu, vì là child của /admin
  name: 'KhuyenMai',
  component: KhuyenMai
}

export default khuyenMaiRouter