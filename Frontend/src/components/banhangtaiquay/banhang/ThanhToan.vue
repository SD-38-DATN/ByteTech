<template>
  <div class="banhangsanpham-container">
    <!-- 💰 Thông tin tổng -->
    <div class="summary-section">
      <div class="summary-header">
        <h4>💰 Tổng kết thanh toán</h4>
        <span class="order-status">Đơn hàng hiện tại</span>
      </div>
      
      <div class="summary-row">
        <span class="label">Tổng tiền hàng:</span>
        <span class="value">{{ formatCurrency(tongTienHang) }}</span>
      </div>

      <!-- 🎫 Voucher Section -->
      <VoucherBanHangTaiQuay 
        :tongTienDonHang="tongTienHang"
        @voucher-applied="onVoucherApplied"
        @voucher-removed="onVoucherRemoved"
      />

      <div v-if="voucherApplied" class="summary-row voucher-discount">
        <span class="label">Giảm voucher:</span>
        <span class="value discount">-{{ formatCurrency(soTienGiamVoucher) }}</span>
      </div>

      <div class="summary-row total">
        <span class="label">Cần thanh toán:</span>
        <span class="value-total">{{ formatCurrency(canThanhToan) }}</span>
      </div>
    </div>

    <!-- 💳 Thanh toán -->
    <div class="section">
      <h4 class="section-title">💳 Thanh toán</h4>

      <div class="payment-field">
        <label class="field-label">Phương thức:</label>
        <select v-model="phuongThuc" class="select-field">
          <option value="">-- Chọn phương thức --</option>
          <option value="tienmat">💵 Tiền mặt</option>
          <option value="chuyenkhoan">🏦 Chuyển khoản</option>
          <option value="the">💳 Thẻ</option>
          <option value="khac">📱 Khác</option>
        </select>
      </div>

    </div>

    <!-- 📝 Ghi chú -->
    <div class="section">
      <h4 class="section-title">📝 Ghi chú</h4>
      <textarea
        v-model="ghiChu"
        placeholder="Nhập ghi chú cho đơn hàng..."
        class="textarea-field"
        rows="3"
      ></textarea>
    </div>

    <!-- 🎯 Hành động -->
    <div class="action-section">
      <button 
        class="btn-save" 
        @click="handleSave"
        :disabled="!canSave"
      >
        💾 Lưu đơn
      </button>
      
      <button 
        class="btn-submit" 
        @click="handleSubmit"
        :disabled="!canSubmit"
      >
        💳 Thanh toán
      </button>
      
      <button 
        class="btn-print" 
        @click="handlePrint"
        :disabled="!canPrint"
      >
        🖨️ In hóa đơn
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useGioHangBanHangTaiQuay } from '@/components/banhangtaiquay/useGioHangBanHangTaiQuay'
import VoucherBanHangTaiQuay from './VoucherBanHangTaiQuay.vue'

const { tongTienHang, tongKhuyenMai, tongGiam, tongThanhToan } = useGioHangBanHangTaiQuay()

// Form data
const phuongThuc = ref('')
const ghiChu = ref('')

// 🎫 Voucher data
const voucherApplied = ref(false)
const soTienGiamVoucher = ref(0)
const voucherInfo = ref(null)

// Computed
const canThanhToan = computed(() => {
  const tongTien = tongThanhToan.value
  const giamVoucher = voucherApplied.value ? soTienGiamVoucher.value : 0
  return Math.max(0, tongTien - giamVoucher) // Không được âm
})


const canSave = computed(() => {
  return tongTienHang.value > 0
})

const canSubmit = computed(() => {
  return tongTienHang.value > 0 && phuongThuc.value
})

const canPrint = computed(() => {
  return tongTienHang.value > 0
})

// Methods
function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value || 0)
}


function handleSave() {
  console.log('💾 Lưu đơn hàng...')
  // Emit event to parent
  emit('save')
}

function handleSubmit() {
  console.log('✅ Chốt đơn hàng...')
  // Emit event to parent
  emit('submit')
}

function handlePrint() {
  console.log('🖨️ In hóa đơn...')
  // Emit event to parent
  emit('print')
}

// 🎫 Voucher functions
function onVoucherApplied(voucherData) {
  voucherInfo.value = voucherData.voucher
  soTienGiamVoucher.value = voucherData.soTienGiam
  voucherApplied.value = true
  
  console.log('✅ Voucher đã áp dụng:', voucherData)
}

function onVoucherRemoved() {
  voucherApplied.value = false
  soTienGiamVoucher.value = 0
  voucherInfo.value = null
  
  console.log('🗑️ Đã xóa voucher')
}

// Emits
const emit = defineEmits(['save', 'submit', 'print'])
</script>

<style scoped>
.banhangsanpham-container {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
}

.summary-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  border: 1px solid #dee2e6;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.summary-header h4 {
  margin: 0;
  color: #495057;
  font-size: 15px;
  font-weight: 600;
}

.order-status {
  background: #28a745;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 500;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
  border-bottom: 1px solid #dee2e6;
}

.summary-row:last-child {
  border-bottom: none;
}

.summary-row.total {
  background: #e3f2fd;
  margin: 8px -12px 0;
  padding: 12px;
  border-radius: 6px;
  font-weight: 600;
}

.label {
  color: #6c757d;
  font-weight: 500;
}

.value {
  color: #495057;
  font-weight: 600;
}

.value-total {
  color: #1976d2;
  font-weight: 700;
  font-size: 16px;
}

.section {
  margin-bottom: 16px;
}

.section-title {
  color: #495057;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e9ecef;
}

.payment-field {
  margin-bottom: 12px;
}

.field-label {
  display: block;
  color: #495057;
  font-weight: 500;
  margin-bottom: 6px;
  font-size: 13px;
}

.select-field, .input-field, .textarea-field {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.2s;
  background: #ffffff;
}

.select-field:focus, .input-field:focus, .textarea-field:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}


.action-section {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.btn-save, .btn-submit, .btn-print {
  flex: 1;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-save {
  background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
  color: white;
  box-shadow: 0 2px 4px rgba(23, 162, 184, 0.3);
}

.btn-save:hover:not(:disabled) {
  background: linear-gradient(135deg, #138496 0%, #117a8b 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(23, 162, 184, 0.4);
}

.btn-submit {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
  box-shadow: 0 2px 4px rgba(40, 167, 69, 0.3);
}

.btn-submit:hover:not(:disabled) {
  background: linear-gradient(135deg, #20c997 0%, #1e7e34 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(40, 167, 69, 0.4);
}

.btn-print {
  background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
  color: white;
  box-shadow: 0 2px 4px rgba(108, 117, 125, 0.3);
}

.btn-print:hover:not(:disabled) {
  background: linear-gradient(135deg, #5a6268 0%, #495057 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(108, 117, 125, 0.4);
}

.btn-save:disabled, .btn-submit:disabled, .btn-print:disabled {
  background: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 🎫 Voucher Styles */
.voucher-discount {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  padding: 6px 12px;
  margin: 4px 0;
}

.voucher-discount .label {
  color: #856404;
  font-weight: 600;
}

.voucher-discount .value.discount {
  color: #dc3545;
  font-weight: 700;
}
</style>
