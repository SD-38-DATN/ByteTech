<template>
  <div class="dashboard-wrapper p-3">
    <div class="container-fluid">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-12">
          <h1 class="display-5 fw-bold text-dark mb-2">Dashboard Thống Kê</h1>
        </div>
      </div>

      <!-- Row 1: 2 ô lớn hàng ngang -->
      <div class="row mb-4">
        <div class="col-md-6 mb-3">
          <div class="card shadow-sm border-0 h-100 stat-large">
            <div class="card-body">
              <div
                class="d-flex justify-content-between align-items-start mb-3"
              >
                <div class="icon-box-lg bg-primary-subtle text-primary">
                  <i class="bi bi-graph-up-arrow fs-3"></i>
                </div>
                <span class="badge bg-primary-subtle text-primary">
                  Toàn hệ thống
                </span>
              </div>
              <h6 class="text-muted text-uppercase small fw-semibold mb-2">
                Tổng Doanh Thu
              </h6>
              <h2 class="fw-bold mb-1">
                {{ formatCurrency(revenueAll) }}
              </h2>
              <p class="text-muted mb-0">
                {{ productsAll.toLocaleString() }} sản phẩm ·
                {{ donHangAll.toLocaleString() }} đơn hàng
              </p>
            </div>
          </div>
        </div>

        <div class="col-md-6 mb-3">
          <div class="card shadow-sm border-0 h-100 stat-large">
            <div class="card-body">
              <div
                class="d-flex justify-content-between align-items-start mb-3"
              >
                <div class="icon-box-lg bg-success-subtle text-success">
                  <i class="bi bi-bar-chart-line fs-3"></i>
                </div>
                <span class="badge bg-success-subtle text-success">
                  Năm {{ currentYear }}
                </span>
              </div>
              <h6 class="text-muted text-uppercase small fw-semibold mb-2">
                Doanh Thu Năm {{ currentYear }}
              </h6>
              <h2 class="fw-bold mb-1">
                {{ formatCurrency(revenueYear) }}
              </h2>
              <p class="text-muted mb-0">
                {{ productsYear.toLocaleString() }} sản phẩm ·
                {{ donHangYear.toLocaleString() }} đơn hàng
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Bộ lọc thời gian -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="card border-0 shadow-sm date-filter-card">
            <div class="card-body">
              <div class="d-flex align-items-center gap-2 flex-wrap">
                <select
                  v-model="selectedDayWeek"
                  @change="updateFilterType('day-week', selectedDayWeek)"
                  class="form-select"
                  style="width: 180px"
                >
                  <option value="today" selected>Hôm nay</option>
                  <option value="yesterday">Hôm qua</option>
                  <option value="this-week">Tuần này</option>
                  <option value="last-week">Tuần trước</option>
                </select>

                <select
                  v-model="selectedMonth"
                  @change="updateFilterType('month', selectedMonth)"
                  class="form-select"
                  style="width: 180px"
                >
                  <option value="" disabled selected>Chọn tháng</option>
                  <option
                    v-for="month in monthOptions"
                    :key="month.value"
                    :value="month.value"
                  >
                    {{ month.label }}
                  </option>
                </select>

                <select
                  v-model="selectedYear"
                  @change="updateFilterType('year', selectedYear)"
                  class="form-select"
                  style="width: 150px"
                >
                  <option value="" disabled selected>Chọn năm</option>
                  <option
                    v-for="year in yearOptions"
                    :key="year"
                    :value="year.toString()"
                  >
                    {{ year }}
                  </option>
                </select>

                <span class="text-white fw-semibold">Hoặc:</span>
                <input
                  type="date"
                  v-model="dateFrom"
                  @change="updateFilterType('date-range', 'custom')"
                  class="form-control"
                  style="width: 150px"
                  placeholder="Từ ngày"
                />
                <span class="text-white fw-bold">→</span>
                <input
                  type="date"
                  v-model="dateTo"
                  @change="updateFilterType('date-range', 'custom')"
                  class="form-control"
                  style="width: 150px"
                  placeholder="Đến ngày"
                />
                <button
                  @click="resetFilter"
                  class="btn btn-light btn-sm ms-2"
                  type="button"
                >
                  <i class="bi bi-arrow-clockwise me-1"></i>
                  Reset
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Row 2: 4 ô nhỏ -->
      <div class="row mb-4">
        <div
          v-for="(item, index) in fourSmallCards"
          :key="index"
          class="col-md-6 col-lg-3 mb-3"
        >
          <div class="card stat-small shadow-sm border-0 h-100">
            <div
              class="card-body d-flex justify-content-between align-items-center"
            >
              <div>
                <h6 class="fw-semibold text-muted text-uppercase small mb-2">
                  {{ item.title }}
                </h6>
                <h5 class="fw-bold mb-0">
                  {{ item.value }}
                </h5>
                <p class="text-muted mb-0 small">{{ item.sub }}</p>
              </div>
              <div
                :class="`icon-box bg-${item.color}-subtle text-${item.color}`"
              >
                <i :class="`bi ${item.icon} fs-4`"></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Row 2 & 3: Biểu đồ (2 hàng x 3 cột) -->
      <div class="row">
        <div
          v-for="chart in charts"
          :key="chart.title"
          class="col-md-6 col-lg-4 mb-4"
        >
          <div class="card shadow-sm border-0 h-100 chart-card">
            <div class="card-body">
              <div class="d-flex align-items-center mb-3">
                <div :class="`stripe-${chart.color} me-2`"></div>
                <h5 class="fw-bold mb-0">{{ chart.title }}</h5>
              </div>
              <!-- Chỉ hiển thị biểu đồ doanh thu, các biểu đồ khác giữ placeholder -->
              <div v-if="chart.title === 'Biểu Đồ Doanh Thu'" class="chart-container">
                <DoanhThuChart :filterParams="chartFilterParams" />
              </div>
              <div v-else class="chart-placeholder">
                <i
                  :class="`bi ${chart.icon} fs-1 text-${chart.color} opacity-50 mb-2`"
                ></i>
                <p class="text-muted mb-0">Biểu đồ sẽ được vẽ ở đây</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import {
  getTongDoanhThu,
  getDoanhThuNamHienTai,
  getDanhSachNam,
  getThongKeFilter,
} from "../../../service/api";
import DoanhThuChart from "../../../components/thongke/charts/DoanhThuChart.vue";

const filterType = ref("today");

// Combo box selections
const selectedDayWeek = ref("today");
const selectedMonth = ref("");
const selectedYear = ref("");

// Date range filter
const dateFrom = ref("");
const dateTo = ref("");

// Get current year
const currentYear = computed(() => new Date().getFullYear());

// Data cho 2 ô lớn - chuyển thành reactive ref
const revenueAll = ref(0);
const productsAll = ref(0);
const donHangAll = ref(0);
const revenueYear = ref(0);
const productsYear = ref(0);
const donHangYear = ref(0);

// Gọi API khi component được mount
onMounted(() => {
  fetchThongKeData();
  fetchDanhSachNam();
  fetchThongKeFilter(); // Load dữ liệu ban đầu cho 4 ô nhỏ
});
// Loading state
const loading = ref(false);

// Hàm gọi API để lấy dữ liệu thống kê
const fetchThongKeData = async () => {
  loading.value = true;
  try {
    // Gọi 2 API song song
    const [tongQuanData, namHienTaiData] = await Promise.all([
      getTongDoanhThu(),
      getDoanhThuNamHienTai(),
    ]);

    // Cập nhật dữ liệu tổng quan (toàn hệ thống)
    if (tongQuanData) {
      // Convert BigDecimal sang number nếu cần
      revenueAll.value = Number(tongQuanData.tongDoanhThu) || 0;
      productsAll.value = Number(tongQuanData.soLuongSanPham) || 0;
      donHangAll.value = Number(tongQuanData.soLuongDonHang) || 0;
    }

    // Cập nhật dữ liệu năm hiện tại
    if (namHienTaiData) {
      // Convert BigDecimal sang number nếu cần
      revenueYear.value = Number(namHienTaiData.tongDoanhThu) || 0;
      productsYear.value = Number(namHienTaiData.soLuongSanPham) || 0;
      donHangYear.value = Number(namHienTaiData.soLuongDonHang) || 0;
    }
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu thống kê:", error);
    // Giữ nguyên giá trị mặc định nếu có lỗi
  } finally {
    loading.value = false;
  }
};

// Danh sách năm từ API
const yearOptions = ref([]);

// Hàm lấy danh sách năm từ API
const fetchDanhSachNam = async () => {
  try {
    const data = await getDanhSachNam();
    if (data && Array.isArray(data)) {
      yearOptions.value = data;
    }
  } catch (error) {
    console.error("Lỗi khi lấy danh sách năm:", error);
    // Fallback: dùng năm hiện tại nếu API lỗi
    const currentYear = new Date().getFullYear();
    yearOptions.value = [currentYear, currentYear - 1, currentYear - 2];
  }
};

// Hàm gọi API để cập nhật 4 ô nhỏ theo filter
const fetchThongKeFilter = async () => {
  try {
    const params = {
      dayWeekFilter: selectedDayWeek.value || null,
      month: selectedMonth.value ? parseInt(selectedMonth.value) : null,
      year: selectedYear.value ? parseInt(selectedYear.value) : null,
      dateFrom: dateFrom.value || null,
      dateTo: dateTo.value || null,
    };

    // Loại bỏ các param null/empty
    Object.keys(params).forEach((key) =>
      params[key] === null || params[key] === "" || params[key] === undefined
        ? delete params[key]
        : {}
    );

    const data = await getThongKeFilter(params);
    if (data) {
      // Cập nhật Doanh Thu
      const doanhThuIndex = fourSmallCards.value.findIndex(
        (card) => card.title === "Doanh Thu"
      );
      if (doanhThuIndex !== -1) {
        fourSmallCards.value[doanhThuIndex].value = formatCurrency(
          Number(data.tongDoanhThu) || 0
        );
      }

      // Cập nhật Đơn Thành Công
      const donThanhCongIndex = fourSmallCards.value.findIndex(
        (card) => card.title === "Đơn Thành Công"
      );
      if (donThanhCongIndex !== -1) {
        fourSmallCards.value[donThanhCongIndex].value = Number(
          data.soLuongDonHangThanhCong || 0
        ).toLocaleString("vi-VN");
      }

      // Cập nhật Đơn Hủy
      const donHuyIndex = fourSmallCards.value.findIndex(
        (card) => card.title === "Đơn Hủy"
      );
      if (donHuyIndex !== -1) {
        fourSmallCards.value[donHuyIndex].value = Number(
          data.soLuongDonHangHuy || 0
        ).toLocaleString("vi-VN");
      }

      // Cập nhật Sản Phẩm
      const sanPhamIndex = fourSmallCards.value.findIndex(
        (card) => card.title === "Sản Phẩm"
      );
      if (sanPhamIndex !== -1) {
        fourSmallCards.value[sanPhamIndex].value = Number(
          data.soLuongSanPham || 0
        ).toLocaleString("vi-VN");
      }
    }
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu thống kê filter:", error);
  }
};

// Update filter function
const updateFilterType = (type, value) => {
  if (type === "year") {
    // Chọn năm: reset ngày/tuần và khoảng thời gian, giữ tháng
    selectedDayWeek.value = "";
    dateFrom.value = "";
    dateTo.value = "";
  } else if (type === "month") {
    // Chọn tháng: reset ngày/tuần và khoảng thời gian, giữ năm
    selectedDayWeek.value = "";
    dateFrom.value = "";
    dateTo.value = "";
  } else if (type === "day-week") {
    // Chọn ngày/tuần: reset tháng, năm và khoảng thời gian
    selectedMonth.value = "";
    selectedYear.value = "";
    dateFrom.value = "";
    dateTo.value = "";
  } else if (type === "date-range") {
    // Chọn khoảng thời gian: reset tất cả select khác
    selectedDayWeek.value = "";
    selectedMonth.value = "";
    selectedYear.value = "";
  }

  // Set active filter
  filterType.value = value;
  console.log("Filter changed:", type, value);

  // Gọi API để cập nhật 4 ô nhỏ
  fetchThongKeFilter();
};

// Hàm reset tất cả filter về mặc định (hôm nay)
const resetFilter = () => {
  // Reset tất cả filter về giá trị mặc định
  selectedDayWeek.value = "today";
  selectedMonth.value = "";
  selectedYear.value = "";
  dateFrom.value = "";
  dateTo.value = "";
  filterType.value = "today";

  // Gọi API để cập nhật 4 ô nhỏ với filter mặc định
  fetchThongKeFilter();
};

// 4 ô nhỏ - chuyển thành reactive ref để có thể cập nhật từ API
const fourSmallCards = ref([
  {
    title: "Doanh Thu",
    value: "0 đ",
    icon: "bi-graph-down",
    color: "info",
  },
  {
    title: "Đơn Thành Công",
    value: "0",
    sub: "đơn hàng",
    icon: "bi-cart-check",
    color: "success",
  },
  {
    title: "Đơn Hủy",
    value: "0",
    sub: "đơn hàng",
    icon: "bi-x-circle",
    color: "danger",
  },
  {
    title: "Sản Phẩm",
    value: "0",
    sub: "đang kinh doanh",
    icon: "bi-box-seam",
    color: "warning",
  },
]);

// Generate month options (1-12)
const monthOptions = [
  { value: "1", label: "Tháng 1" },
  { value: "2", label: "Tháng 2" },
  { value: "3", label: "Tháng 3" },
  { value: "4", label: "Tháng 4" },
  { value: "5", label: "Tháng 5" },
  { value: "6", label: "Tháng 6" },
  { value: "7", label: "Tháng 7" },
  { value: "8", label: "Tháng 8" },
  { value: "9", label: "Tháng 9" },
  { value: "10", label: "Tháng 10" },
  { value: "11", label: "Tháng 11" },
  { value: "12", label: "Tháng 12" },
];

// 6 biểu đồ (2 hàng × 3 cột)
const charts = [
  { title: "Biểu Đồ Doanh Thu", icon: "bi-graph-up", color: "primary" },
  { title: "Biểu Đồ Sản Phẩm", icon: "bi-box-seam", color: "info" },
  { title: "Phân Bố Theo Danh Mục", icon: "bi-pie-chart", color: "warning" },
  { title: "Biểu Đồ Đơn Hàng", icon: "bi-cart-check", color: "success" },
  { title: "Biểu Đồ Khách Hàng", icon: "bi-people", color: "danger" },
  { title: "Biểu Đồ Nhân Viên", icon: "bi-person-badge", color: "secondary" },
];

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    value
  );

// Computed property để truyền filter params vào biểu đồ
const chartFilterParams = computed(() => {
  const params = {
    dayWeekFilter: selectedDayWeek.value || null,
    month: selectedMonth.value ? parseInt(selectedMonth.value) : null,
    year: selectedYear.value ? parseInt(selectedYear.value) : null,
    dateFrom: dateFrom.value || null,
    dateTo: dateTo.value || null,
  };

  // Nếu có dayWeekFilter (today, yesterday, this-week, last-week), bỏ qua month và year
  if (params.dayWeekFilter) {
    params.month = null;
    params.year = null;
  }

  // Loại bỏ các param null/empty
  Object.keys(params).forEach((key) => {
    if (params[key] === null || params[key] === "" || params[key] === undefined) {
      delete params[key];
    }
  });

  return params;
});
</script>

<style scoped>
.dashboard-wrapper {
  background: linear-gradient(to bottom right, #f8f9fa, #e9ecef);
  min-height: 100vh;
}

.stat-small {
  border-radius: 0.75rem;
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-small:hover {
  transform: translateY(-3px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1);
}

.stat-large {
  border-radius: 0.75rem;
}

.icon-box {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-box-lg {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.date-filter-card {
  background: linear-gradient(to right, #0d6efd, #6f42c1);
  border: none !important;
}

/* Chart cards */
.chart-placeholder {
  height: 230px;
  background: #f8f9fa;
  border-radius: 0.75rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.chart-container {
  height: 230px;
  width: 100%;
}
.chart-card {
  border-radius: 1rem;
  transition: transform 0.2s, box-shadow 0.2s;
}
.chart-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

/* Color stripes */
.stripe-primary {
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #0d6efd, #0a58ca);
}
.stripe-info {
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #6f42c1, #5a32a3);
}
.stripe-warning {
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #ffc107, #ff9800);
}
.stripe-success {
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #198754, #157347);
}
.stripe-danger {
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #dc3545, #bb2d3b);
}
.stripe-secondary {
  width: 4px;
  height: 24px;
  background: linear-gradient(to bottom, #6c757d, #5c636a);
}
</style>
