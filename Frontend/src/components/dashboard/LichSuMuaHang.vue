<template>
  <div class="container-fluid p-3 p-md-4" style="background-color: #f8f9fa;">
    <!-- Summary -->
    <div class="card border-0 shadow-sm mb-4">
      <div class="card-body p-4">
        <div class="row align-items-center gy-3">
          <div class="col-md-auto d-flex align-items-center gap-3">
            <i class="bi bi-box-seam-fill fs-1 text-primary"></i>
            <div>
              <h3 class="fw-bolder mb-0">{{ allOrders.length }}</h3>
              <small class="text-muted">Tổng số đơn hàng</small>
            </div>
          </div>
          <div class="vr d-none d-md-block"></div>
          <div class="col-md-auto d-flex align-items-center gap-3">
            <i class="bi bi-cash-coin fs-1 text-success"></i>
            <div>
              <h3 class="fw-bolder mb-0">{{ formatCurrency(totalAccumulated) }}</h3>
              <small class="text-muted">Tổng tiền tích lũy</small>
            </div>
          </div>
          <div class="col-md text-md-end">
            <small class="text-muted d-block mb-1">Kênh thành viên</small>
            <span class="badge bg-danger fs-6 px-3 py-2 rounded-pill shadow-sm">CellphoneS</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter tabs -->
    <div class="d-flex justify-content-center mb-4">
      <div class="filter-tabs bg-white rounded-pill shadow-sm p-2 d-inline-flex">
        <button
          v-for="tab in filterTabs"
          :key="tab"
          class="btn btn-sm rounded-pill"
          :class="{'btn-danger active': tab === activeFilter, 'btn-light': tab !== activeFilter}"
          @click.prevent="activeFilter = tab"
        >
          {{ tab }}
        </button>
      </div>
    </div>

    <!-- Content -->
    <div class="content-area">
      <div v-if="isLoading" class="card shadow-sm text-center p-5 border-0">
        <div class="spinner-border text-danger" style="width: 3rem; height: 3rem;" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="text-muted mt-3 fw-bold">Đang tải danh sách đơn hàng...</p>
      </div>

      <div v-else-if="error" class="alert alert-danger shadow-sm d-flex align-items-center border-0">
        <i class="bi bi-exclamation-triangle-fill fs-4 me-3"></i>
        <div>
          <strong class="d-block">Đã xảy ra lỗi!</strong> {{ error }}
        </div>
      </div>

      <div v-else>
        <div v-if="filteredOrders.length > 0">
          <div
            v-for="order in filteredOrders"
            :key="order.maDonHang"
            class="card shadow-sm mb-4 border-0 order-card"
          >
            <div class="card-header bg-white p-3 d-flex justify-content-between align-items-center">
              <div>
                <h5 class="mb-0 fw-bold text-dark">Đơn hàng #{{ order.maDonHang }}</h5>
                <small class="text-muted">Ngày đặt: {{ formatDate(order.ngayDat) }}</small>
              </div>
              <span class="badge fs-6 rounded-pill px-3 py-2" :class="statusClass(order.trangThai)">
                <i :class="statusIcon(order.trangThai)" class="me-1"></i>
                {{ order.trangThai }}
              </span>
            </div>

            <div class="card-body p-3 p-md-4">
              <div class="customer-info mb-4">
                <div class="row g-3">
                  <div class="col-sm-6 col-lg-4 d-flex gap-2">
                    <i class="bi bi-person-circle fs-5 text-muted mt-1"></i>
                    <div><strong class="d-block">Khách hàng:</strong> {{ order.tenKhachHang }}</div>
                  </div>
                  <div class="col-sm-6 col-lg-4 d-flex gap-2">
                    <i class="bi bi-telephone fs-5 text-muted mt-1"></i>
                    <div><strong class="d-block">Số điện thoại:</strong> {{ order.soDienThoai }}</div>
                  </div>
                  <div class="col-sm-12 col-lg-4 d-flex gap-2">
                    <i class="bi bi-geo-alt fs-5 text-muted mt-1"></i>
                    <div><strong class="d-block">Địa chỉ:</strong> {{ order.diaChiGiaoHang }}</div>
                  </div>
                </div>
              </div>

              <div class="product-list-container">
                <h6 class="fw-bold mb-3 border-bottom pb-2">Chi tiết sản phẩm</h6>
                <div
                  v-for="(item, itemIndex) in order.chiTietListGrouped"
                  :key="item.groupKey"
                  class="product-group mb-2"
                >
                  <div class="group-header p-3 rounded-top" @click="toggleItemExpansion(order.maDonHang, item.groupKey)">
                    <div class="d-flex justify-content-between align-items-center">
                      <div class="d-flex align-items-center gap-3">
                        <span class="fw-bold text-danger">#{{ itemIndex + 1 }}</span>
                        <div>
                          <div class="fw-bold">{{ item.tenSanPham || item.tenPhuKien }}</div>
                          <small class="text-muted">SKU: {{ item.maSKU || item.maSKUPhuKien }}</small>
                        </div>
                      </div>
                      <div class="d-flex align-items-center gap-3">
                        <span class="badge bg-secondary-subtle text-secondary-emphasis rounded-pill">
                          Tổng số lượng: {{ item.soLuong }}
                        </span>
                        <i :class="isItemExpanded(order.maDonHang, item.groupKey) ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"></i>
                      </div>
                    </div>
                  </div>

                  <transition name="slide-fade">
                    <div
                      v-if="isItemExpanded(order.maDonHang, item.groupKey)"
                      class="sub-items-container p-3 bg-light rounded-bottom"
                    >
                      <div
                        v-for="(subItem, subIndex) in item.items"
                        :key="subItem.uniqueId"
                        class="sub-item-detail"
                      >
                        <div class="sub-item-info">
                          <div class="fw-medium">Sản phẩm #{{ subIndex + 1 }}</div>
                          <div v-if="subItem.thuocTinh" class="small text-muted">
                            <span
                              v-for="(attr, i) in formatAttributes(subItem.thuocTinh)"
                              :key="i"
                              class="me-2"
                            >
                              <i class="bi bi-dot"></i>{{ attr }}
                            </span>
                          </div>
                        </div>
                        <div class="sub-item-price text-end fw-medium">{{ formatCurrency(subItem.gia) }}</div>
                        <div class="sub-item-imei">
                          <input
                            type="text"
                            class="form-control form-control-sm"
                            placeholder="Nhập IMEI..."
                            v-model="subItem.imei"
                            :disabled="confirmingOrderId === order.maDonHang"
                          >
                        </div>
                      </div>
                    </div>
                  </transition>
                </div>
              </div>

              <div v-if="order.voucher" class="voucher-info mt-4 p-3 rounded">
                <h6 class="fw-bold mb-3 d-flex align-items-center gap-2">
                  <i class="bi bi-ticket-detailed"></i> Thông tin Voucher
                </h6>
                <div class="voucher-details small">
                  <div class="detail-label">Mã</div>
                  <div><span class="badge bg-success-subtle text-success-emphasis">{{ order.voucher.maVoucher }}</span></div>

                  <div class="detail-label">Loại giảm</div>
                  <div>{{ order.voucher.loaiGiam }}</div>

                  <div class="detail-label">Giá trị</div>
                  <div>
                    {{ order.voucher.loaiGiam === 'Giảm theo phần trăm'
                      ? (order.voucher.giaTriGiam * 100) + '%'
                      : formatCurrency(order.voucher.giaTriGiam) }}
                  </div>

                  <div class="detail-label">Giảm tối đa</div>
                  <div>{{ formatCurrency(order.voucher.giamToiDa) }}</div>

                  <div class="detail-label">Hiệu lực</div>
                  <div>
                    {{ new Date(order.voucher.ngayBatDau).toLocaleDateString('vi-VN') }}
                    -
                    {{ new Date(order.voucher.ngayKetThuc).toLocaleDateString('vi-VN') }}
                  </div>
                </div>
              </div>
            </div>

            <div class="card-footer bg-white p-3 d-flex flex-wrap justify-content-between align-items-center gap-3">
              <div>
                <button class="btn btn-sm btn-outline-secondary">In đơn hàng</button>
                <button class="btn btn-sm btn-outline-danger ms-2">Hủy đơn</button>
              </div>
              <div class="d-flex align-items-center gap-3">
                <div class="text-end">
                  <div class="fs-6 text-muted" v-if="order.voucher">
                    <span>Tạm tính:</span>
                    <span class="ms-2 text-decoration-line-through">{{ formatCurrency(calcTotalBeforeVoucher(order)) }}</span>
                  </div>
                  <div class="fs-5">
                    <span class="fw-bold">Thành tiền:</span>
                    <span class="fw-bold text-danger ms-2">{{ formatCurrency(order.tongTien) }}</span>
                  </div>
                </div>
                <div v-if="order.trangThai === 'Chờ xác nhận'" class="vr"></div>
                <div>
                  <button
                    v-if="order.trangThai === 'Chờ xác nhận'"
                    class="btn btn-danger"
                    @click="confirmOrder(order)"
                    :disabled="confirmingOrderId === order.maDonHang"
                  >
                    <span
                      v-if="confirmingOrderId === order.maDonHang"
                      class="spinner-border spinner-border-sm me-1"
                      role="status"
                      aria-hidden="true"
                    ></span>
                    {{ confirmingOrderId === order.maDonHang ? 'Đang xử lý...' : 'Xác nhận đơn hàng' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="card shadow-sm text-center p-5 border-0 bg-white">
          <i class="bi bi-cloud-drizzle fs-1 text-muted"></i>
          <p class="text-muted mt-3">Không có đơn hàng nào ở trạng thái này.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchMyPendingOrders, xacNhanDonHang } from '@/service/api';

export default {
  name: 'LichSuMuaHang',
  data() {
    return {
      // navTabs & activeNavTab đã bị loại bỏ
      filterTabs: ["Tất cả", "Chờ xác nhận", "Đang vận chuyển", "Đã giao hàng", "Đã hủy"],
      activeFilter: "Chờ xác nhận",
      allOrders: [],
      isLoading: false,
      error: null,
      expandedItems: {},
      confirmingOrderId: null,
    }
  },
  computed: {
    filteredOrders() {
      if (this.activeFilter === 'Tất cả') return this.allOrders;
      return this.allOrders.filter(order => order.trangThai === this.activeFilter);
    },
    totalAccumulated() {
      return this.allOrders.reduce((total, order) => total + order.tongTien, 0);
    }
  },
  methods: {
    async loadOrders() {
      this.isLoading = true;
      this.error = null;
      this.expandedItems = {};
      try {
        let data = [];
        if (this.activeFilter === "Chờ xác nhận") {
          data = await fetchMyPendingOrders();
        } else {
          // Chưa có API cho trạng thái khác -> dữ liệu giả
          data = [];
        }
        this.allOrders = (data || []).map(order => ({
          ...order,
          chiTietListGrouped: this.groupItems(order.chiTietList)
        }));
      } catch (err) {
        console.error("Lỗi khi tải đơn hàng:", err);
        this.error = "Không thể tải được danh sách đơn hàng. Vui lòng thử lại sau.";
      } finally {
        this.isLoading = false;
      }
    },
    groupItems(items) {
      const grouped = {};
      (items || []).forEach((item, index) => {
        const key = item.maSKU || item.maSKUPhuKien || `item-${index}`;
        if (!grouped[key]) {
          grouped[key] = {
            ...item,
            soLuong: 0,
            items: [],
            groupKey: key
          };
        }
        const quantity = item.soLuong || 1;
        grouped[key].soLuong += quantity;
        for (let i = 0; i < quantity; i++) {
          grouped[key].items.push({
            ...item,
            imei: '',
            uniqueId: `${item.maChiTietDonHang}-${key}-${i}`
          });
        }
      });
      return Object.values(grouped);
    },
    async confirmOrder(order) {
      const itemsForApi = [];
      let allImeiFilled = true;

      for (const group of order.chiTietListGrouped) {
        for (const subItem of group.items) {
          if (!subItem.imei || subItem.imei.trim() === '') {
            allImeiFilled = false;
            break;
          }
          itemsForApi.push({
            chiTietDonHangId: subItem.maChiTietDonHang,
            imei: subItem.imei.trim()
          });
        }
        if (!allImeiFilled) break;
      }

      if (!allImeiFilled) {
        alert('Vui lòng nhập đầy đủ IMEI cho tất cả sản phẩm.');
        return;
      }

      this.confirmingOrderId = order.maDonHang;
      try {
        const message = await xacNhanDonHang(order.maDonHang, itemsForApi);
        alert(`Thành công: ${message}`);
        this.allOrders = this.allOrders.filter(o => o.maDonHang !== order.maDonHang);
      } catch (err) {
        const errorMessage = err.response?.data?.message || err.response?.data || "Có lỗi xảy ra, vui lòng thử lại.";
        console.error("Lỗi khi xác nhận đơn hàng:", err);
        alert(`Thất bại: ${errorMessage}`);
      } finally {
        this.confirmingOrderId = null;
      }
    },
    toggleItemExpansion(orderId, groupKey) {
      const key = `${orderId}-${groupKey}`;
      this.expandedItems[key] = !this.expandedItems[key];
    },
    isItemExpanded(orderId, groupKey) {
      const key = `${orderId}-${groupKey}`;
      return !!this.expandedItems[key];
    },

    calcTotalBeforeVoucher(order) {
      if (!order || !order.chiTietList) return 0;
      return order.chiTietList.reduce((total, item) => total + (item.gia * item.soLuong), 0);
    },
    formatAttributes(attributesString) {
      if (!attributesString) return [];
      return attributesString.split(',').map(attr => attr.trim());
    },
    formatCurrency(value) {
      if (typeof value !== 'number') return "0đ";
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
      return new Date(dateString).toLocaleDateString('vi-VN', options);
    },
    statusClass(status) {
      const classes = {
        "Đã giao hàng": "bg-success-subtle text-success-emphasis",
        "Đã hủy": "bg-danger-subtle text-danger-emphasis",
        "Đang vận chuyển": "bg-info-subtle text-info-emphasis",
        "Chờ xác nhận": "bg-warning-subtle text-warning-emphasis",
      };
      return classes[status] || "bg-secondary-subtle text-secondary-emphasis";
    },
    statusIcon(status) {
      const icons = {
        "Đã giao hàng": "bi-check-circle-fill",
        "Đã hủy": "bi-x-circle-fill",
        "Đang vận chuyển": "bi-truck",
        "Chờ xác nhận": "bi-clock-history",
      };
      return icons[status] || "bi-question-circle-fill";
    }
  },
  watch: {
    activeFilter: {
      handler() { this.loadOrders(); },
      immediate: true
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;700&display=swap');
body { font-family: 'Be Vietnam Pro', sans-serif; }

.order-card {
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}
.order-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 .5rem 1.5rem rgba(0,0,0,.1)!important;
}

/* Filter pills */
.filter-tabs .btn {
  padding: 0.5rem 1.25rem;
  font-weight: 500;
  border: none;
  transition: all 0.2s ease;
}
.filter-tabs .btn.btn-light {
  color: #6c757d;
}
.filter-tabs .btn.btn-light:hover {
  background-color: #e9ecef;
  color: #000;
}
.filter-tabs .btn.active {
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.2);
}

/* Order card content */
.customer-info { font-size: 0.9rem; }

.voucher-info {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
}
.voucher-details {
  display: grid;
  grid-template-columns: max-content 1fr;
  gap: 8px 16px;
  align-items: center;
}
.voucher-details .detail-label {
  font-weight: 500;
  color: #6c757d;
}

/* Product list */
.product-group .group-header {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  cursor: pointer;
  transition: background-color 0.2s;
}
.product-group .group-header:hover { background-color: #e9ecef; }
.sub-items-container { border: 1px solid #dee2e6; border-top: none; }

.sub-item-detail {
  display: grid;
  grid-template-columns: 2fr 1fr 1.5fr;
  gap: 1rem;
  align-items: center;
  padding: 0.75rem 0;
  border-bottom: 1px dashed #dee2e6;
}
.sub-item-detail:last-child { border-bottom: none; padding-bottom: 0; }
.sub-item-detail:first-child { padding-top: 0; }

@media (max-width: 768px) {
  .sub-item-detail { grid-template-columns: 1fr; gap: 0.5rem; }
  .sub-item-price { text-align: left !important; }
}

/* Transitions */
.slide-fade-enter-active { transition: all 0.3s ease-out; }
.slide-fade-leave-active { transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1); }
.slide-fade-enter-from,
.slide-fade-leave-to { transform: translateY(-10px); opacity: 0; }
</style>