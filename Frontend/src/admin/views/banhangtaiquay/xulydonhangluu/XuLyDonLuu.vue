<template>
  <div class="xu-ly-don-luu">
    <!-- Header Section -->
    <div class="header-section">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="page-title">
            <i class="fas fa-save me-2"></i>
            Đơn Hàng Đã Lưu
          </h2>
          <p class="text-muted">
            Quản lý và xử lý các đơn hàng đã được lưu từ bán hàng tại quầy
          </p>
        </div>
        <div class="header-actions">
          <!-- Order Scope Filter -->
          <div class="order-scope-filter me-3">
            <div class="btn-group" role="group">
              <button
                @click="orderScope = 'all'"
                :class="[
                  'btn',
                  orderScope === 'all' ? 'btn-primary' : 'btn-outline-primary',
                ]"
                :disabled="loading"
              >
                <i class="fas fa-users me-1"></i>
                Tất cả nhân viên
              </button>
              <button
                @click="orderScope = 'mine'"
                :class="[
                  'btn',
                  orderScope === 'mine' ? 'btn-primary' : 'btn-outline-primary',
                ]"
                :disabled="loading"
              >
                <i class="fas fa-user me-1"></i>
                Chỉ của tôi
              </button>
            </div>
          </div>

          <!-- Action Buttons -->
          <button
            @click="refreshData"
            class="btn btn-outline-primary"
            :disabled="loading"
          >
            <i class="fas fa-sync-alt" :class="{ 'fa-spin': loading }"></i>
            Làm mới
          </button>
        </div>
      </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section mb-4">
      <div class="row g-3">
        <div class="col-md-4">
          <div class="input-group">
            <span class="input-group-text">
              <i class="fas fa-search"></i>
            </span>
            <input
              v-model="searchQuery"
              type="text"
              class="form-control"
              placeholder="Tìm kiếm theo mã đơn hàng, SĐT..."
              @input="handleSearch"
            />
          </div>
        </div>
        <div class="col-md-3">
          <select
            v-model="selectedDateRange"
            @change="filterByDate"
            class="form-select"
          >
            <option value="">Tất cả</option>
            <option value="today">Hôm nay</option>
            <option value="yesterday">Hôm qua</option>
            <option value="thisWeek">Tuần này</option>
            <option value="before7days">7 ngày trước</option>
          </select>
        </div>
        <div class="col-md-3">
          <select v-model="sortBy" @change="handleSort" class="form-select">
            <option value="ngayDat_desc">Mới nhất</option>
            <option value="ngayDat_asc">Cũ nhất</option>
            <option value="tongTien_desc">Giá cao nhất</option>
            <option value="tongTien_asc">Giá thấp nhất</option>
          </select>
        </div>
        <div class="col-md-2">
          <button
            @click="clearFilters"
            class="btn btn-outline-secondary w-100"
            title="Xóa tìm kiếm, thời gian và sắp xếp (giữ nguyên phạm vi nhân viên)"
          >
            <i class="fas fa-times"></i>
            Xóa bộ lọc
          </button>
        </div>
      </div>
    </div>

    <!-- Orders Table -->
    <div class="orders-table-section">
      <div class="card">
        <div
          class="card-header d-flex justify-content-between align-items-center"
        >
          <h5 class="mb-0">
            <i class="fas fa-list me-2"></i>
            Danh Sách Đơn Hàng Đã Lưu
          </h5>
        </div>

        <div class="card-body p-0">
          <!-- Loading State -->
          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2">Đang tải dữ liệu...</p>
          </div>

          <!-- Empty State -->
          <div v-else-if="!filteredOrders.length" class="text-center py-5">
            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
            <h5 class="text-muted">Không có đơn hàng nào</h5>
            <p class="text-muted">
              {{
                searchQuery
                  ? "Không tìm thấy đơn hàng phù hợp với từ khóa tìm kiếm"
                  : "Chưa có đơn hàng nào được lưu"
              }}
            </p>
            <!-- ✅ DEBUG: Hiển thị thông tin debug -->

            <button
              v-if="!searchQuery"
              @click="refreshData"
              class="btn btn-primary"
            >
              <i class="fas fa-sync-alt"></i>
              Làm mới
            </button>
          </div>

          <!-- Orders Table -->
          <div v-else class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th>Mã Đơn Hàng</th>
                  <th>Tên Khách Hàng</th>
                  <th>Số Điện Thoại</th>
                  <th>Nhân Viên</th>
                  <th>Tổng Tiền</th>
                  <th>Ngày Lưu</th>
                  <th>Trạng Thái</th>
                  <th width="120">Thao Tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="order in paginatedOrders" :key="order.maDonHang">
                  <td>
                    <span class="order-code">#{{ order.maDonHang }}</span>
                  </td>
                  <td>
                    <span class="customer-name">{{
                      order.tenNguoiNhan || "Không có tên"
                    }}</span>
                  </td>
                  <td>
                    <span class="phone-number">{{
                      order.soDienThoai || "N/A"
                    }}</span>
                  </td>
                  <td>
                    <div class="employee-info">
                      <strong>{{ order.tenNhanVien || "N/A" }}</strong>
                      <small class="text-muted d-block">{{
                        order.username || "N/A"
                      }}</small>
                    </div>
                  </td>
                  <td>
                    <span class="order-total">{{
                      formatCurrency(order.tongTien)
                    }}</span>
                  </td>
                  <td>
                    <div class="date-info">
                      <span class="date">{{ formatDate(order.ngayDat) }}</span>
                      <small class="text-muted d-block">{{
                        formatTime(order.ngayDat)
                      }}</small>
                    </div>
                  </td>
                  <td>
                    <span class="badge bg-warning">
                      <i class="fas fa-clock me-1"></i>
                      Đã lưu
                    </span>
                  </td>
                  <td>
                    <div class="action-buttons d-flex gap-1">
                      <button
                        @click="processOrder(order)"
                        class="btn btn-sm btn-success"
                        title="Xử lý đơn hàng"
                      >
                        <i class="fas fa-play"></i>
                      </button>
                      <button
                        @click="viewOrderDetails(order)"
                        class="btn btn-sm btn-info"
                        title="Xem chi tiết"
                      >
                        <i class="fas fa-eye"></i>
                      </button>
                      <button
                        @click="deleteOrder(order)"
                        class="btn btn-sm btn-danger"
                        title="Xóa đơn hàng"
                      >
                        <i class="fas fa-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="card-footer">
          <nav aria-label="Page navigation">
            <ul class="pagination pagination-sm justify-content-center mb-0">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button @click="goToPage(1)" class="page-link">
                  <i class="fas fa-angle-double-left"></i>
                </button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button @click="goToPage(currentPage - 1)" class="page-link">
                  <i class="fas fa-angle-left"></i>
                </button>
              </li>

              <li
                v-for="page in visiblePages"
                :key="page"
                class="page-item"
                :class="{ active: page === currentPage }"
              >
                <button @click="goToPage(page)" class="page-link">
                  {{ page }}
                </button>
              </li>

              <li
                class="page-item"
                :class="{ disabled: currentPage === totalPages }"
              >
                <button @click="goToPage(currentPage + 1)" class="page-link">
                  <i class="fas fa-angle-right"></i>
                </button>
              </li>
              <li
                class="page-item"
                :class="{ disabled: currentPage === totalPages }"
              >
                <button @click="goToPage(totalPages)" class="page-link">
                  <i class="fas fa-angle-double-right"></i>
                </button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>

    <!-- Order Details Modal -->
    <div
      v-if="selectedOrder"
      class="modal fade show d-block"
      tabindex="-1"
      @click.self="closeModal"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="fas fa-receipt me-2"></i>
              Chi Tiết Đơn Hàng #{{ selectedOrder.maDonHang }}
            </h5>
            <button @click="closeModal" class="btn-close"></button>
          </div>
          <div class="modal-body">
            <div class="row">
              <div class="col-md-6">
                <h6>Thông Tin Khách Hàng</h6>
                <table class="table table-sm">
                  <tr>
                    <td><strong>Tên khách hàng:</strong></td>
                    <td>{{ selectedOrder.tenNguoiNhan || "Không có tên" }}</td>
                  </tr>
                  <tr>
                    <td><strong>Số điện thoại:</strong></td>
                    <td>{{ selectedOrder.soDienThoai || "N/A" }}</td>
                  </tr>
                  <tr>
                    <td><strong>Địa chỉ:</strong></td>
                    <td>{{ selectedOrder.diaChiGiaoHang || "N/A" }}</td>
                  </tr>
                  <tr>
                    <td><strong>Ghi chú:</strong></td>
                    <td>{{ selectedOrder.ghiChu || "Không có" }}</td>
                  </tr>
                  <tr>
                    <td><strong>Nhân viên:</strong></td>
                    <td>{{ selectedOrder.tenNhanVien || "N/A" }}</td>
                  </tr>
                  <tr>
                    <td><strong>Username:</strong></td>
                    <td>{{ selectedOrder.username || "N/A" }}</td>
                  </tr>
                </table>
              </div>
              <div class="col-md-6">
                <h6>Thông Tin Đơn Hàng</h6>
                <table class="table table-sm">
                  <tr>
                    <td><strong>Ngày lưu:</strong></td>
                    <td>{{ formatDateTime(selectedOrder.ngayDat) }}</td>
                  </tr>
                  <tr>
                    <td><strong>Tổng tiền:</strong></td>
                    <td class="text-success fw-bold">
                      {{ formatCurrency(selectedOrder.tongTien) }}
                    </td>
                  </tr>
                  <tr>
                    <td><strong>Trạng thái:</strong></td>
                    <td>
                      <span class="badge bg-warning">Đã lưu</span>
                    </td>
                  </tr>
                </table>
              </div>
            </div>

            <div class="mt-4">
              <h6>Danh Sách Sản Phẩm</h6>
              <div class="table-responsive">
                <table class="table table-sm">
                  <thead>
                    <tr>
                      <th>Sản phẩm</th>
                      <th>Số lượng</th>
                      <th>IMEI/Serial</th>
                      <th>Đơn giá</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="item in selectedOrder.chiTietDonHangs"
                      :key="item.id"
                    >
                      <td>
                        <div>
                          <strong>{{ item.tenSanPham }}</strong>
                          <br />
                          <small class="text-muted">{{ item.maSKU }}</small>
                          <br />
                          <!-- Hiển thị thuộc tính -->
                          <div v-if="item.thuocTinh" class="thuoc-tinh-display">
                            <small class="text-info">
                              {{ item.thuocTinh }}
                            </small>
                          </div>
                          <div v-else class="thuoc-tinh-display">
                            <small class="text-muted"
                              >Không có thuộc tính</small
                            >
                          </div>
                        </div>
                      </td>
                      <td>{{ item.soLuong }}</td>
                      <td>
                        <div v-if="item.imei" class="imei-info">
                          <span class="imei-code">{{ item.imei }}</span>
                        </div>
                        <span v-else class="text-muted">Không có IMEI</span>
                      </td>
                      <td>{{ formatCurrency(item.gia) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button @click="closeModal" class="btn btn-secondary">
              <i class="fas fa-times me-1"></i>
              Đóng
            </button>
            <button
              @click="processOrder(selectedOrder)"
              class="btn btn-success"
            >
              <i class="fas fa-cogs me-1"></i>
              Xử lý đơn hàng
            </button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="selectedOrder" class="modal-backdrop fade show"></div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from "vue";
import {
  getDonHangLuu,
  getDonHangLuuByMaDonHang,
  xoaDonHangLuu,
  getCurrentUser,
} from "@/service/api";

export default {
  name: "XuLyDonLuu",
  setup() {
    // Reactive data
    const savedOrders = ref([]);
    const loading = ref(false);
    const searchQuery = ref("");
    const selectedDateRange = ref("");
    const sortBy = ref("ngayDat_desc");
    const orderScope = ref("mine"); // 'all' hoặc 'mine' - Mặc định là "Chỉ của tôi"
    const selectedOrder = ref(null);
    const currentPage = ref(1);
    const itemsPerPage = 10;
    const currentUser = ref(null); // Thông tin user hiện tại

    // Computed properties - Tối ưu để giảm tính toán
    const filteredOrders = computed(() => {
      let filtered = savedOrders.value;

      // Early return nếu không có dữ liệu
      if (!filtered || filtered.length === 0) {
        console.log("🔍 DEBUG: filteredOrders - No data, returning []");
        return [];
      }

      // Search filter
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        filtered = filtered.filter(
          (order) =>
            order.maDonHang.toString().includes(query) ||
            (order.soDienThoai && order.soDienThoai.includes(query))
        );
      }

      // Scope filter (tất cả nhân viên hoặc chỉ của tôi)
      if (orderScope.value === "mine" && currentUser.value) {
        filtered = filtered.filter(
          (order) => order.username === currentUser.value.username
        );
      }

      // Date filter - Tối ưu bằng cách cache date objects
      if (selectedDateRange.value) {
        const now = new Date();
        const today = new Date(
          now.getFullYear(),
          now.getMonth(),
          now.getDate()
        );

        filtered = filtered.filter((order) => {
          const orderDate = new Date(order.ngayDat);

          switch (selectedDateRange.value) {
            case "today":
              return orderDate >= today;
            case "yesterday": {
              const yesterday = new Date(today);
              yesterday.setDate(yesterday.getDate() - 1);
              return orderDate >= yesterday && orderDate < today;
            }
            case "thisWeek": {
              const startOfWeek = new Date(today);
              startOfWeek.setDate(today.getDate() - today.getDay());
              return orderDate >= startOfWeek;
            }
            case "lastWeek": {
              const startOfLastWeek = new Date(today);
              startOfLastWeek.setDate(today.getDate() - today.getDay() - 7);
              const endOfLastWeek = new Date(today);
              endOfLastWeek.setDate(today.getDate() - today.getDay());
              return orderDate >= startOfLastWeek && orderDate < endOfLastWeek;
            }
            case "before7days": {
              const sevenDaysAgo = new Date(today);
              sevenDaysAgo.setDate(today.getDate() - 7);
              return orderDate < sevenDaysAgo; //  Hiển thị đơn hàng trước 7 ngày (cũ hơn 7 ngày)
            }
            case "thisMonth": {
              const startOfMonth = new Date(
                now.getFullYear(),
                now.getMonth(),
                1
              );
              return orderDate >= startOfMonth;
            }
            default:
              return true;
          }
        });
      }

      // Sort - Tối ưu bằng cách cache sort function
      const [field, direction] = sortBy.value.split("_");
      const isDesc = direction === "desc";

      if (field === "ngayDat") {
        filtered.sort((a, b) => {
          const dateA = new Date(a.ngayDat);
          const dateB = new Date(b.ngayDat);
          return isDesc ? dateB - dateA : dateA - dateB;
        });
      } else if (field === "tongTien") {
        filtered.sort((a, b) => {
          return isDesc ? b.tongTien - a.tongTien : a.tongTien - b.tongTien;
        });
      }

      return filtered;
    });

    const totalPages = computed(() =>
      Math.ceil(filteredOrders.value.length / itemsPerPage)
    );

    const paginatedOrders = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage;
      const end = start + itemsPerPage;
      return filteredOrders.value.slice(start, end);
    });

    const visiblePages = computed(() => {
      const pages = [];
      const total = totalPages.value;
      const current = currentPage.value;

      if (total <= 7) {
        for (let i = 1; i <= total; i++) {
          pages.push(i);
        }
      } else {
        if (current <= 4) {
          for (let i = 1; i <= 5; i++) pages.push(i);
          pages.push("...");
          pages.push(total);
        } else if (current >= total - 3) {
          pages.push(1);
          pages.push("...");
          for (let i = total - 4; i <= total; i++) pages.push(i);
        } else {
          pages.push(1);
          pages.push("...");
          for (let i = current - 1; i <= current + 1; i++) pages.push(i);
          pages.push("...");
          pages.push(total);
        }
      }

      return pages;
    });

    // ✅ TỐI ƯU: Load thông tin user hiện tại từ API
    const loadCurrentUser = async () => {
      try {
        const response = await getCurrentUser();
        // API returns { username, role, user: UserDTO }
        if (response && response.user) {
          currentUser.value = {
            id: response.user.id, // ✅ Sử dụng numeric ID từ API
            username: response.user.username || response.username,
            tenHienThi: response.user.tenHienThi || "Admin",
          };
        } else {
          // Fallback nếu API không trả về đúng format
          currentUser.value = {
            id: null,
            username: response?.username || "admin",
            tenHienThi: "Admin",
          };
        }
      } catch (error) {
        console.error("❌ ERROR: Lỗi khi load thông tin user:", error);
        // Fallback: Set giá trị mặc định khi API lỗi
        currentUser.value = {
          id: null,
          username: "admin",
          tenHienThi: "Admin",
        };
      }
    };

    const loadSavedOrders = async () => {
      loading.value = true;
      try {
        // Lấy đơn hàng theo phạm vi (tất cả hoặc chỉ của user hiện tại)
        let userId = null;
        if (orderScope.value === "mine" && currentUser.value?.id) {
          //  Đảm bảo userId là số, không phải chuỗi
          const userIdValue = currentUser.value.id;
          if (typeof userIdValue === "number" && userIdValue > 0) {
            userId = userIdValue;
          } else if (
            typeof userIdValue === "string" &&
            !isNaN(parseInt(userIdValue))
          ) {
            userId = parseInt(userIdValue);
          } else {
            console.warn("⚠️ User ID không hợp lệ:", userIdValue);
            // Nếu không có user ID hợp lệ, chỉ lấy tất cả đơn hàng
            userId = null;
          }
        }
        const response = await getDonHangLuu(userId);
        savedOrders.value = response || [];

        // Không sử dụng mock data nữa - chỉ dùng API thực tế
        if (!response || response.length === 0) {
          savedOrders.value = [];
          console.log("🔍 DEBUG: Không có dữ liệu, reset savedOrders về []");
        }
      } catch (error) {
        console.error("❌ Lỗi khi tải đơn hàng đã lưu:", error);
        const errorMessage =
          error.response?.data?.message ||
          error.message ||
          "Có lỗi xảy ra khi tải dữ liệu từ server";
        alert(`Lỗi: ${errorMessage}`);
        savedOrders.value = [];
      } finally {
        loading.value = false;
      }
    };

    const refreshData = () => {
      loadSavedOrders();
    };

    // Debounce search để tránh gọi quá nhiều
    let searchTimeout = null;
    const handleSearch = () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }
      searchTimeout = setTimeout(() => {
        currentPage.value = 1;
      }, 300); // Debounce 300ms
    };

    // Watch for orderScope changes - Reload data khi thay đổi scope
    watch(orderScope, () => {
      currentPage.value = 1;
      // ✅ QUAN TRỌNG: Reload data khi thay đổi scope
      loadSavedOrders();
    });

    const filterByDate = () => {
      currentPage.value = 1;
    };

    const handleSort = () => {
      currentPage.value = 1;
    };

    const clearFilters = () => {
      searchQuery.value = "";
      selectedDateRange.value = "";
      sortBy.value = "ngayDat_desc";
      // Không reset orderScope - giữ nguyên lựa chọn hiện tại
      currentPage.value = 1;
    };

    const processOrder = async (order) => {
      if (
        confirm(`Bạn có chắc chắn muốn xử lý đơn hàng #${order.maDonHang}?`)
      ) {
        try {
          //  KIỂM TRA: Đơn hàng đã tồn tại trong bán hàng tại quầy chưa
          const existingOrders = JSON.parse(
            localStorage.getItem("danhSachDonHang") || "[]"
          );

          const existingOrder = existingOrders.find(
            (existingOrder) => existingOrder.maDonHang === order.maDonHang
          );

          if (existingOrder) {
            // ✅ Đơn giản hóa: Chỉ thông báo và hỏi có muốn chuyển hay không
            const confirmNavigate = confirm(
              `⚠️ Đơn hàng #${order.maDonHang} đã tồn tại trong bán hàng tại quầy!\n\n` +
                `Bạn có muốn chuyển sang trang bán hàng để xem đơn hàng này?\n\n` +
                `Nhấn OK để chuyển, Cancel để hủy.`
            );

            if (confirmNavigate) {
              // Chuyển sang đơn hàng hiện tại (giữ nguyên dữ liệu)
              window.location.href = `/admin/ban-hang-tai-quay/ban-hang?loadOrder=saved_${order.maDonHang}`;
            }
            // Nếu không chuyển, không làm gì cả (không thay đổi gì)
            return;
          }

          // ✅ Đơn hàng chưa tồn tại → Lấy dữ liệu mới từ server
          const orderDetails = await getDonHangLuuByMaDonHang(order.maDonHang);
          if (!orderDetails) {
            alert("Không tìm thấy chi tiết đơn hàng");
            return;
          }

          // Chuyển đổi dữ liệu từ database format sang cart format
          const cartItems = convertOrderToCartFormat(orderDetails);

          // ✅ Lưu vào localStorage (đã kiểm tra không tồn tại ở trên)
          const currentOrders = JSON.parse(
            localStorage.getItem("danhSachDonHang") || "[]"
          );

          // ✅ Double-check: Kiểm tra lại một lần nữa (phòng trường hợp có đơn hàng khác thêm vào giữa chừng)
          const existingIndex = currentOrders.findIndex(
            (od) =>
              od.maDonHang === order.maDonHang ||
              od.id === `saved_${order.maDonHang}`
          );

          // ✅ SỬA LỖI: Giữ lại customerInfo từ đơn hàng cũ nếu có (khi thay thế)
          const oldOrder =
            existingIndex !== -1 ? currentOrders[existingIndex] : null;
          const oldCustomerInfo =
            oldOrder?.thongTinKhachHang?.customerInfo || null;

          // Lưu dữ liệu vào localStorage để bán hàng tại quầy có thể load
          const orderData = {
            id: `saved_${order.maDonHang}`,
            maDonHang: order.maDonHang,
            gioHang: cartItems,
            thongTinKhachHang: {
              tenKhachHang: orderDetails.tenNguoiNhan || "", // ✅ SỬA: Sử dụng tenNguoiNhan thay vì tenNhanVien
              soDienThoai: orderDetails.soDienThoai || "",
              diaChi: orderDetails.diaChiGiaoHang || "",
              customerInfo: oldCustomerInfo, // ✅ Giữ lại customerInfo từ đơn hàng cũ nếu có
            },
            tongTien: orderDetails.tongTien,
            ngayTao: new Date().toISOString(),
            trangThai: "dang_xu_ly",
          };

          if (existingIndex !== -1) {
            // Nếu đã tồn tại (do race condition), thay thế bằng dữ liệu mới từ server
            currentOrders[existingIndex] = orderData;
          } else {
            // Đơn hàng chưa tồn tại, thêm mới
            currentOrders.push(orderData);
          }

          localStorage.setItem(
            "danhSachDonHang",
            JSON.stringify(currentOrders)
          );

          // Chuyển sang bán hàng tại quầy
          window.location.href = `/admin/ban-hang-tai-quay/ban-hang?loadOrder=saved_${order.maDonHang}`;
        } catch (error) {
          console.error("Lỗi khi xử lý đơn hàng:", error);
          alert("Có lỗi xảy ra khi xử lý đơn hàng");
        }
      }
    };

    // Function chuyển đổi từ database format sang cart format
    const convertOrderToCartFormat = (orderDetails) => {
      const cartItems = [];
      const groupedItems = {};

      // Nhóm các chi tiết đơn hàng theo SKU
      orderDetails.chiTietDonHangs.forEach((item, index) => {
        //  SỬA LỖI: Phân biệt sản phẩm và phụ kiện
        const isPhuKien = item.loaiSanPham === "phukien";
        const sku = item.maSKU; // Backend trả về SKU trong field maSKU cho cả sản phẩm và phụ kiện

        if (!sku) {
          return;
        }

        if (!groupedItems[sku]) {
          //  SỬA LỖI: Tạo object với đúng field cho sản phẩm và phụ kiện
          groupedItems[sku] = {
            sanPham: {
              //  Quan trọng: Phụ kiện dùng maSKUPhuKien, sản phẩm dùng maSKU
              maSKU: isPhuKien ? null : sku,
              maSKUPhuKien: isPhuKien ? sku : null,
              tenSanPham: item.tenSanPham,
              gia: item.gia,
              loai: isPhuKien ? "Phụ kiện" : "Sản phẩm chính",
              thuocTinh: item.thuocTinh,
            },
            //  Đặt field đúng cho maSKU/maSKUPhuKien ở level root
            maSKU: isPhuKien ? null : sku,
            maSKUPhuKien: isPhuKien ? sku : null,
            tenSanPham: item.tenSanPham,
            gia: item.gia,
            loai: isPhuKien ? "Phụ kiện" : "Sản phẩm chính",
            thuocTinh: item.thuocTinh,
            soLuongMua: 0,
            thanhTien: 0,
            imeiList: [],
          };
        }

        groupedItems[sku].soLuongMua += item.soLuong;
        groupedItems[sku].thanhTien += item.gia * item.soLuong;

        if (item.imei) {
          // Tạo object IMEI từ string
          const imeiObject = {
            id: `temp_${Date.now()}_${Math.random()}`, // Tạo ID tạm thời để tránh mất IMEI
            imei: item.imei,
            trangThai: 5, // Trạng thái tạm giữ
            maSKU: isPhuKien ? null : sku,
            maSKUPhuKien: isPhuKien ? sku : null,
            tenSanPham: item.tenSanPham,
            tenPhuKien: isPhuKien ? item.tenSanPham : null,
            sanPham: {
              maSKU: isPhuKien ? null : sku,
              maSKUPhuKien: isPhuKien ? sku : null,
              tenSanPham: item.tenSanPham,
              gia: item.gia,
              thuocTinh: item.thuocTinh,
              soLuong: 1,
            },
          };
          groupedItems[sku].imeiList.push(imeiObject);
        }
      });

      // Chuyển đổi thành array
      const result = Object.values(groupedItems);
      return result;
    };

    const viewOrderDetails = (order) => {
      selectedOrder.value = order;
    };

    const closeModal = () => {
      selectedOrder.value = null;
    };

    const deleteOrder = async (order) => {
      //  KIỂM TRA: Đơn hàng đã tồn tại trong bán hàng tại quầy chưa
      const existingOrders = JSON.parse(
        localStorage.getItem("danhSachDonHang") || "[]"
      );

      const existingOrder = existingOrders.find(
        (existingOrder) => existingOrder.maDonHang === order.maDonHang
      );
      if (existingOrder) {
        // ✅ Đơn giản hóa: Chỉ thông báo và hỏi có muốn chuyển hay không
        const confirmNavigate = confirm(
          `⚠️ Đơn hàng #${order.maDonHang} đã tồn tại trong bán hàng tại quầy!\n\n` +
            `Bạn Không thể xóa\n\n` +
            `Nhấn OK để chuyển, Cancel để hủy.`
        );

        if (confirmNavigate) {
          // Chuyển sang đơn hàng hiện tại (giữ nguyên dữ liệu)
          window.location.href = `/admin/ban-hang-tai-quay/ban-hang?loadOrder=saved_${order.maDonHang}`;
        }
        // Nếu không chuyển, không làm gì cả (không thay đổi gì)
        return;
      }

      if (
        confirm(
          `Bạn có chắc chắn muốn xóa đơn hàng #${order.maDonHang}?\n\nĐơn hàng sẽ được chuyển sang trạng thái "Đã hủy" và tất cả IMEI (nếu có) sẽ được giải phóng về trạng thái "Có sẵn".`
        )
      ) {
        try {
          // Gọi API xóa đơn hàng
          await xoaDonHangLuu(order.maDonHang);

          // Reload danh sách đơn hàng
          await loadSavedOrders();

          alert("Đã xóa đơn hàng thành công!");
        } catch (error) {
          console.error("❌ Lỗi khi xóa đơn hàng:", error);
          alert(
            "Có lỗi xảy ra khi xóa đơn hàng: " +
              (error.response?.data || error.message)
          );
        }
      }
    };

    const goToPage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
      }
    };

    // Utility functions
    const formatCurrency = (amount) => {
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(amount);
    };

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleDateString("vi-VN");
    };

    const formatTime = (dateString) => {
      return new Date(dateString).toLocaleTimeString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
      });
    };

    const formatDateTime = (dateString) => {
      return new Date(dateString).toLocaleString("vi-VN");
    };

    // Lifecycle
    onMounted(async () => {
      await loadCurrentUser();
      await loadSavedOrders();
    });

    return {
      // Data
      savedOrders,
      loading,
      searchQuery,
      selectedDateRange,
      sortBy,
      orderScope,
      selectedOrder,
      currentPage,

      // Computed
      filteredOrders,
      totalPages,
      paginatedOrders,
      visiblePages,

      // Methods
      refreshData,
      handleSearch,
      filterByDate,
      handleSort,
      clearFilters,
      processOrder,
      viewOrderDetails,
      closeModal,
      deleteOrder,
      goToPage,
      formatCurrency,
      formatDate,
      formatTime,
      formatDateTime,
    };
  },
};
</script>

<style scoped>
.xu-ly-don-luu {
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.page-title {
  color: #2c3e50;
  font-weight: 600;
  margin-bottom: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-actions .btn {
  border-radius: 8px;
  font-weight: 500;
}

.order-scope-filter .btn-group {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.order-scope-filter .btn {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.2s;
}

.order-scope-filter .btn:first-child {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.order-scope-filter .btn:last-child {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.order-scope-filter .btn:hover {
  transform: translateY(-1px);
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.orders-table-section .card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.orders-table-section .card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
  border: none;
}

.table th {
  border: none;
  font-weight: 600;
  color: #495057;
  background-color: #f8f9fa;
}

.table td {
  border: none;
  vertical-align: middle;
  padding: 12px;
}

.table-hover tbody tr:hover {
  background-color: rgba(0, 123, 255, 0.05);
}

.order-code {
  font-family: "Courier New", monospace;
  font-weight: 600;
  color: #495057;
}

.employee-info strong {
  color: #2c3e50;
}

.phone-number {
  font-family: "Courier New", monospace;
  color: #495057;
}

.customer-name {
  font-weight: 500;
  color: #2c3e50;
}

.imei-info {
  font-size: 14px;
}

.imei-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.imei-item {
  display: flex;
  align-items: center;
}

.imei-code {
  font-family: "Courier New", monospace;
  font-weight: 600;
  color: #2c3e50;
  background-color: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-size: 12px;
}

.product-count {
  color: #6c757d;
  font-size: 14px;
}

.order-total {
  font-weight: 600;
  color: #28a745;
  font-size: 16px;
}

.date-info .date {
  font-weight: 500;
  color: #495057;
}

.action-buttons .btn {
  border-radius: 6px;
  padding: 6px 10px;
}

.badge {
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 20px;
}

.modal-content {
  border: none;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
  border: none;
}

.modal-backdrop {
  background-color: rgba(0, 0, 0, 0.5);
}

.pagination .page-link {
  border: none;
  color: #495057;
  padding: 8px 12px;
  margin: 0 2px;
  border-radius: 6px;
}

.pagination .page-item.active .page-link {
  background-color: #007bff;
  color: white;
}

.pagination .page-link:hover {
  background-color: #e9ecef;
  color: #495057;
}

/* Responsive */
@media (max-width: 768px) {
  .xu-ly-don-luu {
    padding: 10px;
  }

  .header-section .d-flex {
    flex-direction: column;
    gap: 15px;
  }

  .header-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .order-scope-filter {
    margin-right: 0 !important;
  }

  .order-scope-filter .btn-group {
    width: 100%;
  }

  .order-scope-filter .btn {
    flex: 1;
  }

  .table-responsive {
    font-size: 14px;
  }

  .action-buttons {
    display: flex;
    flex-direction: row;
    gap: 4px;
    justify-content: center;
    align-items: center;
  }

  .action-buttons .btn {
    padding: 4px 8px;
    font-size: 12px;
    min-width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

@media (max-width: 576px) {
  .filter-section .row {
    gap: 10px;
  }

  .filter-section .col-md-4,
  .filter-section .col-md-3,
  .filter-section .col-md-2 {
    margin-bottom: 10px;
  }

  .table th,
  .table td {
    padding: 8px 4px;
    font-size: 12px;
  }

  .order-code,
  .phone-number {
    font-size: 11px;
  }
}
</style>
