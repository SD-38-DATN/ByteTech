<template>
  <div class="voucher-banhang-container">
    <div class="voucher-header">
      <h5>🎫 Voucher</h5>
      <button 
        @click="loadVouchers" 
        class="btn-refresh"
        :disabled="loading"
        title="Làm mới danh sách voucher"
      >
        🔄
      </button>
    </div>
    
    <div class="voucher-content">
      <!-- Chọn voucher từ combobox -->
      <div class="voucher-field">
        <label class="field-label">Chọn voucher:</label>
        <select 
          v-model="selectedVoucher" 
          @change="onVoucherChange"
          class="select-field"
          :disabled="loading"
        >
          <option 
            v-for="voucher in voucherOptions" 
            :key="voucher.id" 
            :value="voucher.id ? voucher : null"
          >
            {{ voucher.tenVoucher }}
          </option>
        </select>
      </div>

      <!-- Nhập mã voucher -->
      <div class="voucher-field">
        <label class="field-label">Mã voucher:</label>
        <div class="voucher-input-group">
          <input
            v-model="maVoucher"
            placeholder="Nhập mã voucher..."
            class="input-field voucher-input"
            :disabled="loading"
          />
          <button 
            @click="apDungVoucher" 
            class="btn-apply-voucher"
            :disabled="!maVoucher.trim() || loading"
          >
            Áp dụng
          </button>
        </div>
      </div>

      <!-- Hiển thị voucher đã áp dụng -->
      <div v-if="voucherApplied" class="voucher-applied">
        <div class="applied-info">
          <span class="applied-text">✅ Voucher đã áp dụng: {{ appliedVoucher.codeVoucher }}</span>
          <span class="discount-amount">Giảm: {{ formatCurrency(soTienGiam) }}</span>
        </div>
        <button @click="xoaVoucher" class="btn-remove-applied">🗑️</button>
      </div>

      <!-- Thông báo lỗi -->
      <div v-if="errorMessage" class="error-message">
        ❌ {{ errorMessage }}
      </div>

      <!-- Loading state -->
      <div v-if="loading" class="loading-state">
        🔄 Đang tải danh sách voucher...
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { fetchVouchersForBanHangTaiQuay, fetchVouchersForBanHangTaiQuayByAmount, validateVoucherForBanHang } from '@/service/api'

// Props
const props = defineProps({
  tongTienDonHang: {
    type: Number,
    default: 0
  }
})

// Emits
const emit = defineEmits(['voucher-applied', 'voucher-removed'])

// Reactive data
const vouchers = ref([])
const selectedVoucher = ref(null)
const maVoucher = ref('')
const voucherApplied = ref(false)
const appliedVoucher = ref(null)
const soTienGiam = ref(0)
const loading = ref(false)
const errorMessage = ref('')

// Computed
const canApplyVoucher = computed(() => {
  if (!maVoucher.value.trim()) return false
  if (voucherApplied.value) return false
  return true
})

// Computed để đảm bảo combobox luôn có option "-- Chọn voucher --"
const voucherOptions = computed(() => {
  return [
    { id: '', tenVoucher: '-- Chọn voucher --' },
    ...vouchers.value
  ]
})

// Methods
async function loadVouchers() {
  loading.value = true
  errorMessage.value = ''
  
  try {
    // Sử dụng API mới với điều kiện tổng tiền đơn hàng
    const data = await fetchVouchersForBanHangTaiQuayByAmount(props.tongTienDonHang)
    vouchers.value = data
    
    // Reset combobox về "-- Chọn voucher --" khi load lại
    selectedVoucher.value = null
    
    console.log('✅ Đã tải danh sách voucher theo điều kiện:', data)
  } catch (error) {
    console.error('❌ Lỗi khi tải voucher:', error)
    errorMessage.value = 'Không thể tải danh sách voucher. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
}

function onVoucherChange() {
  if (selectedVoucher.value) {
    maVoucher.value = selectedVoucher.value.codeVoucher
    console.log('🎫 Đã chọn voucher:', selectedVoucher.value)
  } else {
    maVoucher.value = ''
  }
}

async function apDungVoucher() {
  if (!maVoucher.value.trim()) {
    errorMessage.value = 'Vui lòng nhập mã voucher!'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const result = await validateVoucherForBanHang(
      maVoucher.value.trim(), 
      props.tongTienDonHang
    )
    
    if (result.success) {
      appliedVoucher.value = result.voucher || { codeVoucher: maVoucher.value.trim() }
      soTienGiam.value = result.soTienGiam
      voucherApplied.value = true
      
      // Emit event to parent
      emit('voucher-applied', {
        voucher: appliedVoucher.value,
        soTienGiam: result.soTienGiam
      })
      
      console.log('✅ Voucher đã được áp dụng:', result)
    } else {
      errorMessage.value = result.message || 'Voucher không hợp lệ!'
    }
  } catch (error) {
    console.error('❌ Lỗi khi áp dụng voucher:', error)
    errorMessage.value = 'Không thể áp dụng voucher. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
}

function xoaVoucher() {
  clearVoucher()
  errorMessage.value = ''
  console.log('🗑️ Đã xóa voucher')
}

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value || 0)
}

function formatVoucherValue(voucher) {
  if (voucher.loaiGiam === 1) {
    return `${voucher.giaTriGiam}%`
  } else {
    return formatCurrency(voucher.giaTriGiam)
  }
}

// Watch tongTienDonHang để load lại voucher khi thay đổi
watch(() => props.tongTienDonHang, (newValue, oldValue) => {
  errorMessage.value = ''
  
  // Tự động load lại voucher khi tổng tiền đơn hàng thay đổi
  if (newValue > 0) {
    loadVouchers()
    
    // Kiểm tra voucher hiện tại có còn hợp lệ không
    if (voucherApplied.value && appliedVoucher.value) {
      checkCurrentVoucherValidity(newValue)
    }
  } else if (newValue === 0 && oldValue > 0) {
    // Khi giỏ hàng trống, xóa voucher đã chọn và load lại
    clearVoucher()
  }
})

// Kiểm tra voucher hiện tại có còn hợp lệ không
async function checkCurrentVoucherValidity(tongTienDonHang) {
  if (!appliedVoucher.value) return
  
  try {
    const result = await validateVoucherForBanHang(
      appliedVoucher.value.codeVoucher, 
      tongTienDonHang
    )
    
    if (!result.success) {
      // Voucher không còn hợp lệ, xóa voucher
      clearVoucher()
      errorMessage.value = 'Voucher không còn hợp lệ với đơn hàng hiện tại'
    }
  } catch (error) {
    console.error('❌ Lỗi khi kiểm tra voucher:', error)
  }
}

// Xóa voucher và reset state
function clearVoucher() {
  selectedVoucher.value = null
  maVoucher.value = ''
  voucherApplied.value = false
  appliedVoucher.value = null
  soTienGiam.value = 0
  // Không xóa vouchers.value để giữ danh sách voucher
  
  // Emit event để parent cập nhật
  emit('voucher-removed')
}

// Lifecycle
onMounted(() => {
  loadVouchers()
})
</script>

<style scoped>
.voucher-banhang-container {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  margin: 12px 0;
}

.voucher-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.voucher-header h5 {
  margin: 0;
  color: #495057;
  font-size: 14px;
  font-weight: 600;
}

.btn-refresh {
  background: #007bff;
  color: white;
  border: none;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-refresh:hover:not(:disabled) {
  background: #0056b3;
  transform: translateY(-1px);
}

.btn-refresh:disabled {
  background: #6c757d;
  cursor: not-allowed;
  opacity: 0.6;
}

.voucher-content {
  space-y: 12px;
}

.voucher-field {
  margin-bottom: 12px;
}

.field-label {
  display: block;
  color: #495057;
  font-weight: 500;
  margin-bottom: 6px;
  font-size: 13px;
}

.select-field {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 13px;
  background: white;
  transition: all 0.2s;
}

.select-field:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.select-field:disabled {
  background: #f8f9fa;
  cursor: not-allowed;
}

.voucher-input-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.voucher-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 12px;
}

.btn-apply-voucher {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-apply-voucher:hover:not(:disabled) {
  background: linear-gradient(135deg, #218838 0%, #1e7e34 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(40, 167, 69, 0.3);
}

.btn-apply-voucher:disabled {
  background: #6c757d;
  cursor: not-allowed;
  opacity: 0.6;
}


.voucher-applied {
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 6px;
  padding: 12px;
  margin: 8px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.applied-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.applied-text {
  color: #155724;
  font-size: 13px;
  font-weight: 600;
}

.discount-amount {
  color: #28a745;
  font-size: 12px;
  font-weight: 700;
}

.btn-remove-applied {
  background: #dc3545;
  color: white;
  border: none;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-remove-applied:hover {
  background: #c82333;
  transform: translateY(-1px);
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  padding: 8px 12px;
  margin: 8px 0;
  font-size: 12px;
  font-weight: 500;
}

.loading-state {
  text-align: center;
  color: #6c757d;
  font-size: 12px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
  margin: 8px 0;
}
</style>
