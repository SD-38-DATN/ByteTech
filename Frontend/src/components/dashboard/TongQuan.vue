<template>
  <div class="bg-white rounded shadow-sm p-4">
    <h2 class="mb-3">Tổng Quan Tài Khoản</h2>
    <p>Chào mừng <strong>Quy Do</strong> quay trở lại!</p>
    <div class="row">
      <div class="col-md-4"><div class="card p-3 text-center"><h5>Đơn hàng chờ xác nhận</h5><p class="fs-2 fw-bold text-primary mb-0">1</p></div></div>
      <div class="col-md-4"><div class="card p-3 text-center"><h5>Đơn hàng đang giao</h5><p class="fs-2 fw-bold text-info mb-0">2</p></div></div>
      <div class="col-md-4"><div class="card p-3 text-center"><h5>Voucher sắp hết hạn</h5><p class="fs-2 fw-bold text-danger mb-0">1</p></div></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TongQuan',
}
</script>