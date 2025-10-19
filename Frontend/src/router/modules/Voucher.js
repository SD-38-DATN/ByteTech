
import VoucherView from "@/views/admin/voucher/VoucherView.vue"

const voucherRouter = {
  path: 'voucher',       // note: không có '/' đầu, vì là child của /admin
  name: 'VoucherView',
  component: VoucherView
}

export default voucherRouter