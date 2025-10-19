<template>
  <div class="checkout-container container py-4">
    <h2 class="mb-4 fw-bold text-primary d-flex align-items-center">
      <i class="bi bi-cart-check me-2"></i> Thông tin đặt hàng
    </h2>

    <div class="row">
      <div class="col-md-8">
        <div v-if="items.length" class="card shadow mb-4 border-0 rounded-4 overflow-hidden">
          <div class="card-header bg-gradient bg-primary text-white fw-bold fs-5 d-flex align-items-center">
            <i class="bi bi-bag-check-fill me-2"></i> Sản phẩm đã chọn
          </div>

          <ul class="list-group list-group-flush">
            <li
              v-for="item in items"
              :key="item.id"
              class="list-group-item py-3 product-item"
            >
              <div class="d-flex align-items-center">
                <img
                  :src="item.hinhAnh || 'https://via.placeholder.com/80'"
                  class="rounded border me-3"
                  alt="Ảnh sản phẩm"
                  style="width: 80px; height: 80px; object-fit: cover"
                />

                <div class="flex-grow-1">
                  <div class="fw-bold fs-6">
                    {{ item.ten }}
                    <small class="text-muted"> ({{ item.maSKU }})</small>
                  </div>

                  <div v-if="item.thuocTinh" class="small text-muted mt-1">
                    <span
                      v-for="(value, key) in item.thuocTinh"
                      :key="key"
                      class="me-3"
                    >
                      <strong>{{ key }}:</strong> {{ value }}
                    </span>
                  </div>

                  <div class="mt-2">
                    <span class="badge bg-secondary px-2 py-1">SL: {{ item.soLuong }}</span>
                  </div>
                </div>

                <div class="text-end fw-bold fs-5 text-danger">
                  {{ formatCurrency(item.gia) }}
                </div>
              </div>
            </li>
          </ul>

          <div class="card-footer bg-light">
            <p class="mb-1 d-flex justify-content-between">
              <strong>Tổng tiền gốc:</strong>
              <span>{{ formatCurrency(totalPrice) }}</span>
            </p>
            <p v-if="selectedVoucher" class="mb-1 text-success d-flex justify-content-between">
              <span>
                  <strong>Voucher:</strong> {{ selectedVoucher.voucher.codeVoucher }}
              </span>
              <span>-{{ formatCurrency(discountAmount) }}</span>
            </p>
            <p class="fw-bold fs-5 text-primary d-flex justify-content-between align-items-center border-top pt-2 mt-2">
              <span>Tổng thanh toán:</span>
              <span>{{ formatCurrency(finalPrice) }}</span>
            </p>
          </div>
        </div>
        <div v-else class="alert alert-warning">❌ Bạn chưa chọn sản phẩm nào!</div>
      </div>

      <div class="col-md-4">
        <form v-if="items.length" @submit.prevent="submitOrder" class="card shadow-sm p-4 rounded-4">
          <h5 class="fw-bold mb-3 d-flex align-items-center">
            <i class="bi bi-truck me-2"></i> Thông tin giao hàng
          </h5>

          <div class="mb-3">
            <label class="form-label">Địa chỉ:</label>
            <input v-model="order.diaChiGiaoHang" class="form-control rounded-3" required />
          </div>

          <div class="mb-3">
            <label class="form-label">Số điện thoại:</label>
            <input v-model="order.soDienThoai" class="form-control rounded-3" required />
          </div>

          <div class="mb-3">
            <label class="form-label">Phương thức thanh toán:</label>
            <select v-model="order.phuongThucThanhToan" class="form-select rounded-3">
              <option value="COD">Thanh toán khi nhận hàng (COD)</option>
              <option value="BANK">Chuyển khoản ngân hàng</option>
            </select>
          </div>

          <div class="mb-3">
            <label class="form-label">Ghi chú:</label>
            <textarea v-model="order.ghiChu" class="form-control rounded-3" rows="2"></textarea>
          </div>

          <div class="mb-3">
            <label class="form-label">🎟️ Voucher:</label>
            <div>
              <button type="button" class="btn btn-outline-primary rounded-3 w-100" @click="showVoucherModal = true">
                 <span v-if="!selectedVoucher">Chọn hoặc nhập mã</span>
                 <span v-else>Đã chọn: {{ selectedVoucher.voucher.codeVoucher }}</span>
              </button>
            </div>
          </div>

          <button type="submit" class="btn btn-primary w-100 rounded-3 fw-bold btn-lg">
            ✅ Xác nhận đặt hàng
          </button>
        </form>
      </div>
    </div>

    <div v-if="showVoucherModal" class="modal-backdrop">
      <div class="modal-dialog">
        <div class="modal-content p-3 rounded-4 shadow-lg">
          <div class="modal-header border-0">
            <h5 class="modal-title fw-bold">Chọn Voucher</h5>
            <button type="button" class="btn-close" @click="showVoucherModal = false"></button>
          </div>

          <div class="modal-body" style="max-height: 400px; overflow-y: auto">
             <div
              v-for="vc in vouchers"
              :key="vc.id"
              class="voucher-item-wrapper"
              @click="!getVoucherDisableReason(vc) && (selectedVoucherId = vc.id)"
            >
              <div
                class="voucher-item"
                :class="{
                  'selected': selectedVoucherId === vc.id,
                  'disabled': getVoucherDisableReason(vc)
                }"
              >
                <div class="voucher-brand">
                    <i v-if="vc.voucher.loaiGiam === 1" class="bi bi-percent fs-1"></i>
                    <i v-else class="bi bi-tag-fill fs-1"></i>
                </div>
                <div class="voucher-content">
                  <h6 class="fw-bold mb-1">{{ vc.voucher.tenVoucher }}</h6>
                  <p class="small text-muted mb-2">Mã: {{ vc.voucher.codeVoucher }}</p>
                  
                  <p class="mb-1 small">
                    <strong>Giảm: </strong>
                    <span v-if="vc.voucher.loaiGiam === 1">
                      {{ vc.voucher.giaTriGiam * 100 }}% (tối đa {{ formatCurrency(vc.voucher.giamToiDa) }})
                    </span>
                    <span v-else>{{ formatCurrency(vc.voucher.giaTriGiam) }}</span>
                  </p>
                  
                  <p v-if="vc.voucher.dieuKienGiam > 0" class="mb-1 small">
                    <strong>Điều kiện: </strong> Đơn tối thiểu {{ formatCurrency(vc.voucher.dieuKienGiam) }}
                  </p>
                  
                   <div class="voucher-expiry mt-2 pt-2 border-top">
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="badge bg-info-subtle text-info-emphasis rounded-pill">
                            <i class="bi bi-ticket-perforated me-1"></i>
                            Còn lại: {{ vc.soLanSuDung }} lượt
                        </span>
                        <span class="d-flex align-items-center">
                            <i class="bi bi-clock me-2"></i>
                            <span v-if="daysLeft(vc.voucher.ngayKetThuc) > 0" class="text-success fw-bold">
                                Còn {{ daysLeft(vc.voucher.ngayKetThuc) }} ngày
                            </span>
                            <span v-else-if="daysLeft(vc.voucher.ngayKetThuc) === 0" class="text-warning fw-bold">
                                Hết hạn hôm nay
                            </span>
                            <span v-else class="text-danger fw-bold">
                                Đã hết hạn
                            </span>
                        </span>

                        <small class="text-muted">
                            HSD: {{ formatDate(vc.voucher.ngayKetThuc) }}
                        </small>
                        
                    </div>
                  </div>

                </div>
                <div v-if="getVoucherDisableReason(vc)" class="voucher-disabled-overlay">
                  <span>{{ getVoucherDisableReason(vc) }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer border-0">
            <button class="btn btn-secondary rounded-3" @click="showVoucherModal = false">Trở lại</button>
            <button class="btn btn-primary rounded-3 fw-bold" @click="confirmVoucher">OK</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { createOrder, fetchMyVouchers } from "../service/api.js";

export default {
  name: "Checkout",
  data() {
    return {
      items: [],
      order: {
        diaChiGiaoHang: "",
        soDienThoai: "",
        phuongThucThanhToan: "COD",
        ghiChu: ""
      },
      vouchers: [],
      selectedVoucherId: "",
      showVoucherModal: false,
    };
  },
  computed: {
    totalPrice() {
      return this.items.reduce((sum, item) => sum + item.gia * item.soLuong, 0);
    },
    selectedVoucher() {
      return this.vouchers.find(vc => vc.id === this.selectedVoucherId) || null;
    },
    discountAmount() {
      if (!this.selectedVoucher) return 0;
      const v = this.selectedVoucher.voucher;
      if (this.totalPrice < v.dieuKienGiam) return 0;
      let giamGia = v.loaiGiam === 0 ? v.giaTriGiam : this.totalPrice * v.giaTriGiam;
      return v.giamToiDa ? Math.min(giamGia, v.giamToiDa) : giamGia;
    },
    finalPrice() {
      return Math.max(this.totalPrice - this.discountAmount, 0);
    }
  },
  async created() {
    if (this.$route.query.items) {
      try {
        const parsedItems = JSON.parse(this.$route.query.items);
        this.items = parsedItems.map(item => {
          return {
            ...item,
            ten: item.tenSanPham || item.tenPhuKien, 
            maSKU: item.maSKU || item.maSKUPhuKien 
          };
        });
      } catch (e) {
        console.error("Lỗi phân tích JSON", e);
        this.items = [];
      }
    }
    try {
      this.vouchers = await fetchMyVouchers();
    } catch (err) {
      console.error("❌ Lỗi fetch voucher:", err);
    }
  },
  methods: {
    async submitOrder() {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          alert("Bạn cần đăng nhập để đặt hàng!");
          this.$router.push("/login");
          return;
        }
        if (!this.items.length) {
          alert("❌ Không có sản phẩm nào để đặt hàng!");
          return;
        }
        
        const gioHangChiTietSanPhamIds = this.items
          .filter(item => item.maSKU)
          .map(item => item.id);
        
        const gioHangChiTietPhuKienIds = this.items
          .filter(item => item.maSKUPhuKien) 
          .map(item => item.id);
          
        const payload = {
          ...this.order,
          gioHangChiTietSanPhamIds: gioHangChiTietSanPhamIds,
          gioHangChiTietPhuKienIds: gioHangChiTietPhuKienIds,
          userVoucherId: this.selectedVoucherId || null
        };
        
        const res = await createOrder(payload);

        this.$router.push({ 
          name: "TrangChu", 
          query: { 
            order_success: 'true', 
            order_code: res.maDonHang 
          } 
        });

      } catch (err) {
        console.error("❌ Lỗi đặt hàng:", err);
        alert("Đặt hàng thất bại!");
      }
    },
    confirmVoucher() {
      this.showVoucherModal = false;
    },
    formatCurrency(value) {
      if (!value) return "0 ₫";
      return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value);
    },
    formatDate(dateStr) {
      if (!dateStr) return "";
      return new Date(dateStr).toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
      });
    },
    getVoucherDisableReason(vc) {
        const today = new Date();
        const end = new Date(vc.voucher.ngayKetThuc);
        
        // Cần setHours để so sánh ngày chính xác
        today.setHours(0, 0, 0, 0);
        end.setHours(0, 0, 0, 0);

        if (vc.trangThai === 2) {
            return "Đã hết lượt sử dụng";
        }
        if (this.totalPrice < (vc.voucher.dieuKienGiam || 0)) {
            return `Cần đơn hàng tối thiểu ${this.formatCurrency(vc.voucher.dieuKienGiam)}`;
        }
        if (today > end) {
            return "Đã hết hạn";
        }
        if (vc.voucher.soLanSuDung <= 0) {
            return "Đã hết lượt";
        }
        return null;
    }
  }
};
</script>

<script setup>
// HÀM TÍNH SỐ NGÀY CÒN LẠI
function daysLeft(endDate) {
  if (!endDate) return null;
  const today = new Date();
  const end = new Date(endDate);
  
  // Chuẩn hóa về 0 giờ để so sánh ngày
  today.setHours(0, 0, 0, 0);
  end.setHours(0, 0, 0, 0);

  const diffTime = end.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays;
}
</script>


<style scoped>
.product-item {
    transition: all 0.2s ease-in-out;
}
.product-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.modal-backdrop {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-dialog {
  background: #fff;
  border-radius: 12px;
  width: 500px;
  max-width: 95%;
}

/* --- CSS NÂNG CẤP CHO VOUCHER --- */
.voucher-item-wrapper {
  position: relative;
  margin-bottom: 1rem;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
}

.voucher-item {
  display: flex;
  background-color: #fff;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}
.voucher-item::before { /* Đường đục lỗ */
    content: '';
    position: absolute;
    left: 80px;
    top: -10px;
    bottom: -10px;
    width: 2px;
    background-image: linear-gradient(to bottom, #ccc 50%, transparent 50%);
    background-size: 1px 10px;
    background-repeat: repeat-y;

}

.voucher-item-wrapper:hover .voucher-item:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-color: #0d6efd;
}

.voucher-brand {
  flex-shrink: 0;
  width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f8ff;
  color: #0d6efd;
}

.voucher-content {
  padding: 1rem;
  flex-grow: 1;
}

.voucher-item.selected {
  border-color: #0d6efd;
  box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.25);
}

/* Dấu tick cho voucher được chọn */
.voucher-item.selected::after {
    content: '✅';
    position: absolute;
    top: 5px;
    right: 10px;
    font-size: 1.2rem;
}

.voucher-item.disabled {
    cursor: not-allowed;
}
.voucher-item.disabled .voucher-brand {
    background-color: #f8f9fa;
    color: #adb5bd;
}

.voucher-item.disabled .voucher-content {
    opacity: 0.6;
}

.voucher-disabled-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #dc3545;
    font-weight: bold;
    text-align: center;
    padding: 0 1rem;
    font-size: 0.9rem;
}

.voucher-expiry {
    font-size: 0.85rem;
}

</style>