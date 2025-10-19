<script setup>
import { ref, onMounted, computed } from 'vue';
// Sửa đường dẫn tới file service API của bạn nếu cần
// Bổ sung import các hàm API cần thiết
import { fetchActiveVouchers, addUserVoucher, fetchMyVouchers } from '../service/api';

const vouchers = ref([]);
const myVouchers = ref([]); // State để lưu trữ voucher của người dùng
const loading = ref(true);
const error = ref(null);
const addStatus = ref({}); // State để theo dõi trạng thái thêm voucher cho từng thẻ

// === PHẦN BỔ SUNG LOGIC ===

// Computed property để tạo một Set chứa các mã voucher người dùng đã có
const myVoucherCodes = computed(() => {
    // SỬA ĐỔI: Truy cập vào object con `voucher` để lấy đúng codeVoucher
    // Dữ liệu từ API /me là một mảng UserVoucher, mỗi phần tử có object voucher bên trong.
    return new Set(myVouchers.value.map(userVoucher => userVoucher.voucher.codeVoucher));
});

onMounted(async () => {
  try {
    // Tải danh sách voucher public và voucher của riêng user cùng lúc
    const [activeVouchersData, myVouchersData] = await Promise.all([
      fetchActiveVouchers(),
      // Nếu user chưa đăng nhập, API /voucher/me có thể lỗi
      // Ta sẽ bỏ qua lỗi này và coi như user chưa có voucher nào
      fetchMyVouchers().catch(err => {
        console.warn("Không thể tải danh sách voucher cá nhân, có thể do chưa đăng nhập.");
        return []; // Trả về mảng rỗng để không làm hỏng giao diện
      })
    ]);
    vouchers.value = activeVouchersData;
    myVouchers.value = myVouchersData;

  } catch (err) {
    console.error("Lỗi khi tải voucher:", err);
    error.value = "Không thể tải dữ liệu. Vui lòng thử lại.";
  } finally {
    loading.value = false;
  }
});

// Hàm xử lý khi người dùng nhấn nút "Lưu voucher"
const handleAddVoucher = async (voucher) => {
  // Cập nhật trạng thái loading cho voucher cụ thể này
  addStatus.value[voucher.id] = { loading: true, message: '', error: false };

  try {
    const response = await addUserVoucher(voucher.codeVoucher);
    
    // Cập nhật trạng thái thành công
    addStatus.value[voucher.id] = { loading: false, message: response.message, error: false };
    
    // Cập nhật lại danh sách voucher của người dùng để UI tự động thay đổi
    // Tạo một object UserVoucher giả lập để khớp cấu trúc dữ liệu
    myVouchers.value.push({ voucher: voucher });

  } catch (err) {
    // Lấy thông báo lỗi từ backend
    const errorMessage = err.response?.data?.error || "Đã có lỗi xảy ra.";
    addStatus.value[voucher.id] = { loading: false, message: errorMessage, error: true };
  } finally {
     // Tự động ẩn thông báo sau 3 giây
    setTimeout(() => {
        if(addStatus.value[voucher.id]) {
            addStatus.value[voucher.id].message = '';
        }
    }, 3000);
  }
};

// === CÁC HÀM HELPER HIỆN CÓ ===

const formatCurrency = (num) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num);
};

const formatDiscountDetails = (v) => {
  if (v.loaiGiam === 1) { // Giảm theo %
    return `Giảm ${v.giaTriGiam * 100}%`;
  }
  return `Giảm ${formatCurrency(v.giaTriGiam)}`;
};

const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

const formatStatus = (status) => {
    return status === 1 ? 'Hoạt động' : 'Không hoạt động';
};

</script>

<template>
  <div class="page-container">
    <h1 class="page-title">Chi Tiết Voucher</h1>

    <div v-if="loading" class="message">Đang tải...</div>
    <div v-else-if="error" class="message error">{{ error }}</div>

    <div v-else-if="vouchers.length > 0" class="grid-container">
      <!-- SỬA ĐỔI: Thêm class `claimed` để làm mờ voucher đã lấy -->
      <div v-for="voucher in vouchers" :key="voucher.id" :class="['ticket', { 'claimed': myVoucherCodes.has(voucher.codeVoucher) }]">
        
        <div class="ticket-left" :class="voucher.loaiGiam === 1 ? 'bg-percent' : 'bg-direct'">
          <div class="voucher-id">ID: {{ voucher.id }}</div>
          <div class="code-container">
            <span class="code-label">Mã</span>
            <span class="code-text">{{ voucher.codeVoucher }}</span>
          </div>
          <div class="status-badge" :class="voucher.trangThai === 1 ? 'status-active' : 'status-inactive'">
            {{ formatStatus(voucher.trangThai) }}
          </div>
        </div>

        <div class="ticket-right">
          <h3 class="voucher-title">{{ voucher.tenVoucher }}</h3>
          <p class="voucher-description">{{ voucher.moTa }}</p>
          
          <div class="details-grid">
            <div class="detail-item">
              <span class="detail-label">Ưu đãi</span>
              <span class="detail-value highlight">{{ formatDiscountDetails(voucher) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Giảm tối đa</span>
              <span class="detail-value">{{ formatCurrency(voucher.giamToiDa) }}</span>
            </div>
            <div v-if="voucher.dieuKienGiam > 0" class="detail-item">
                <span class="detail-label">Đơn tối thiểu</span>
                <span class="detail-value">{{ formatCurrency(voucher.dieuKienGiam) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Lượt sử dụng</span>
              <span class="detail-value">{{ voucher.soLanSuDung }}</span>
            </div>
          </div>

          <div class="timeline">
              <div class="timeline-item">
                  <span class="timeline-label">Bắt đầu:</span>
                  <span class="timeline-date">{{ formatDate(voucher.ngayBatDau) }}</span>
              </div>
              <div class="timeline-item">
                  <span class="timeline-label">Kết thúc:</span>
                  <span class="timeline-date">{{ formatDate(voucher.ngayKetThuc) }}</span>
              </div>
          </div>

            <!-- === PHẦN GIAO DIỆN ĐƯỢC CẬP NHẬT === -->
            <div class="action-container">
                <!-- Nếu user chưa có voucher này, hiển thị nút "Lưu" -->
                <button
                v-if="!myVoucherCodes.has(voucher.codeVoucher)"
                @click="handleAddVoucher(voucher)"
                :disabled="addStatus[voucher.id]?.loading"
                class="btn-add"
                >
                {{ addStatus[voucher.id]?.loading ? 'Đang lưu...' : 'Lưu voucher' }}
                </button>
                
                <!-- Nếu user đã có, hiển thị button bị vô hiệu hóa -->
                <button v-else class="btn-claimed" disabled>
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z"/>
                    </svg>
                    <span>Đã lấy voucher</span>
                </button>

                <!-- Hiển thị thông báo thành công hoặc thất bại -->
                <div 
                v-if="addStatus[voucher.id]?.message" 
                :class="['add-status-message', { 'error-message': addStatus[voucher.id]?.error }]">
                {{ addStatus[voucher.id]?.message }}
                </div>
            </div>

        </div>
      </div>
    </div>
    
    <div v-else class="message">Không có voucher nào để hiển thị.</div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.page-container {
  font-family: 'Inter', sans-serif;
  padding: 24px;
  background-color: #f3f4f6;
}
.page-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 32px;
}
.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 24px;
}
.message { text-align: center; font-size: 16px; color: #6b7280; margin-top: 48px; }
.message.error { color: #ef4444; }

/* --- Ticket Styling --- */
.ticket {
  display: flex;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px_6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1);
  transition: all 0.3s ease-in-out;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}
.ticket:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -4px rgba(0,0,0,0.1);
}

/* SỬA ĐỔI: Thêm style để làm mờ voucher đã lấy */
.ticket.claimed {
  opacity: 0.65;
  filter: grayscale(50%);
}
.ticket.claimed:hover {
    transform: none; /* Vô hiệu hóa hiệu ứng hover */
    box-shadow: 0 4px_6px -1px rgba(0,0,0,0.1), 0 2px 4px -2px rgba(0,0,0,0.1);
}

.ticket-left {
  width: 150px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  color: white;
  text-align: center;
  flex-shrink: 0;
}
.bg-percent { background: linear-gradient(145deg, #ef4444, #f87171); }
.bg-direct { background: linear-gradient(145deg, #3b82f6, #60a5fa); }

.voucher-id {
    font-size: 12px;
    font-weight: 500;
    background: rgba(0,0,0,0.2);
    padding: 2px 8px;
    border-radius: 99px;
}
.code-container {
    margin: 12px 0;
}
.code-label {
    font-size: 13px;
    font-weight: 500;
    opacity: 0.8;
    display: block;
}
.code-text {
    font-size: 22px;
    font-weight: 700;
    letter-spacing: 1px;
}
.status-badge {
    font-size: 12px;
    font-weight: 600;
    padding: 4px 12px;
    border-radius: 99px;
    width: fit-content;
}
.status-active { background-color: #16a34a; color: #f0fdf4; }
.status-inactive { background-color: #9ca3af; color: #e5e7eb; }

.ticket-right {
  flex-grow: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}
.voucher-title {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 4px 0;
}
.voucher-description {
    font-size: 14px;
    color: #6b7280;
    margin: 0 0 16px 0;
    border-bottom: 1px solid #f3f4f6;
    padding-bottom: 16px;
}
.details-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    margin-bottom: 16px;
}
.detail-item {
    display: flex;
    flex-direction: column;
}
.detail-label {
    font-size: 12px;
    color: #9ca3af;
    margin-bottom: 2px;
}
.detail-value {
    font-size: 15px;
    font-weight: 500;
    color: #374151;
}
.detail-value.highlight {
    font-weight: 600;
    color: #be123c;
}
.timeline {
    margin-top: auto; /* Đẩy xuống cuối cùng */
    border-top: 1px solid #f3f4f6;
    padding-top: 12px;
}
.timeline-item {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
}
.timeline-label {
    color: #6b7280;
}
.timeline-date {
    font-weight: 500;
    color: #374151;
}

/* === PHẦN BỔ SUNG CSS === */
.action-container {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f3f4f6;
    text-align: right;
    position: relative;
    min-height: 40px; 
}

.btn-add {
    background-color: #3b82f6;
    color: white;
    border: none;
    padding: 10px 20px;
    font-size: 14px;
    font-weight: 600;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.2s ease, transform 0.2s ease;
}

.btn-add:hover {
    background-color: #2563eb;
    transform: scale(1.02);
}

.btn-add:disabled {
    background-color: #9ca3af;
    cursor: not-allowed;
    transform: none;
}

/* SỬA ĐỔI: Thay thế .status-saved bằng .btn-claimed */
.btn-claimed {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background-color: #e5e7eb;
    color: #4b5563;
    border: 1px solid #d1d5db;
    padding: 10px 20px;
    font-size: 14px;
    font-weight: 600;
    border-radius: 8px;
    cursor: not-allowed;
}


.add-status-message {
    position: absolute;
    bottom: -22px;
    right: 0;
    font-size: 12px;
    font-weight: 500;
    color: #16a34a;
}
.add-status-message.error-message {
    color: #ef4444;
}
</style>

