<template>
  <div class="container py-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-3">
      <h2 class="section-title mb-0">📂 Danh Mục Sản Phẩm</h2>

      <!-- Search + Controls -->
      <div class="d-flex flex-wrap gap-2">
        <div class="input-group">
          <span class="input-group-text" id="searchLabel" title="Tìm kiếm theo tên hoặc mô tả">
            🔎
          </span>
          <input
            type="search"
            class="form-control"
            placeholder="Tìm danh mục..."
            :value="query"
            @input="onQueryInput($event.target.value)"
            aria-label="Tìm danh mục"
            aria-describedby="searchLabel"
          />
        </div>

        <button
          class="btn btn-outline-secondary"
          type="button"
          @click="refreshAll"
          :disabled="loading"
          title="Tải lại dữ liệu"
        >
          🔄 Tải lại
        </button>
      </div>
    </div>

    <!-- Loading categories (skeleton) -->
    <div v-if="loading" class="row g-4" aria-live="polite">
      <div v-for="i in 6" :key="'skeleton-' + i" class="col-lg-4 col-md-6">
        <div class="card h-100 shadow-sm placeholder-glow">
          <div class="card-header d-flex justify-content-between align-items-center">
            <span class="badge bg-primary rounded-pill placeholder col-2">&nbsp;</span>
            <h5 class="mb-0 placeholder col-6">&nbsp;</h5>
          </div>
          <div class="card-body">
            <p class="card-text text-muted mb-3">
              <span class="placeholder col-12"></span>
              <span class="placeholder col-10"></span>
              <span class="placeholder col-8"></span>
            </p>
            <span class="badge bg-success px-3 py-2 placeholder col-6">&nbsp;</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Error categories -->
    <div v-else-if="error" class="alert alert-danger d-flex justify-content-between align-items-center" role="alert">
      <span>{{ error }}</span>
      <button class="btn btn-sm btn-light" @click="refreshAll">Thử lại</button>
    </div>

    <!-- Categories grid -->
    <div v-else>
      <div class="d-flex justify-content-between align-items-center mb-2">
        <small class="text-muted">
          Đang hiển thị {{ filteredDanhMuc.length }} / {{ danhMucList.length }} danh mục
        </small>
        <div v-if="selectedDanhMuc" class="d-flex align-items-center gap-2">
          <span class="badge text-bg-light">Đang chọn: {{ selectedDanhMuc.tenDanhMuc }}</span>
          <button class="btn btn-sm btn-outline-secondary" @click="clearSelection">Bỏ chọn</button>
        </div>
      </div>

      <div
        v-if="filteredDanhMuc.length === 0"
        class="text-center text-muted border rounded-3 p-5"
        aria-live="polite"
      >
        <div class="mb-2" style="font-size: 2rem;">🤔</div>
        <div>Không tìm thấy danh mục nào khớp với từ khóa.</div>
        <div class="mt-2">
          <button class="btn btn-outline-secondary btn-sm" @click="resetQuery">Xóa bộ lọc</button>
        </div>
      </div>

      <div v-else class="row g-4">
        <div
          v-for="(dm, index) in filteredDanhMuc"
          :key="dm.maDanhMuc"
          class="col-lg-4 col-md-6"
        >
          <div
            class="card category-card shadow-sm h-100 cursor-pointer border-0"
            role="button"
            tabindex="0"
            @click="chonDanhMuc(dm)"
            @keydown.enter="chonDanhMuc(dm)"
            :aria-label="`Chọn danh mục ${dm.tenDanhMuc}`"
          >
            <div class="card-header d-flex justify-content-between align-items-center bg-white border-0 pb-0">
              <span class="badge bg-primary rounded-pill">#{{ index + 1 }}</span>
              <h5 class="mb-0 text-truncate" :title="dm.tenDanhMuc">{{ dm.tenDanhMuc }}</h5>
            </div>
            <div class="card-body">
              <p class="card-text text-muted mb-3" :title="dm.moTa || 'Chưa có mô tả'">
                {{ dm.moTa || "Chưa có mô tả" }}
              </p>
              <div class="d-flex justify-content-between align-items-center">
                <span class="badge bg-success px-3 py-2">
                  {{ dm.tongSanPham }} SP đang bán
                </span>
                <button class="btn btn-sm btn-outline-primary" @click.stop="chonDanhMuc(dm)">
                  Xem sản phẩm
                </button>
              </div>
            </div>
            <div class="card-footer bg-transparent border-0">
              <div class="progress" style="height: 6px;" title="Tương đối số lượng SP">
                <div
                  class="progress-bar bg-primary"
                  role="progressbar"
                  :style="{ width: Math.min(100, (dm.tongSanPham || 0) / maxTongSP * 100) + '%' }"
                  :aria-valuenow="dm.tongSanPham || 0"
                  aria-valuemin="0"
                  :aria-valuemax="maxTongSP || 100"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Products in selected category -->
    <div v-if="selectedDanhMuc" ref="productSection" class="mt-5">
      <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h3 class="mb-0">
          📦 Sản phẩm: <strong>{{ selectedDanhMuc.tenDanhMuc }}</strong>
        </h3>

        <!-- Product controls -->
        <div class="d-flex flex-wrap align-items-center gap-2">
          <div class="input-group input-group-sm">
            <span class="input-group-text">🔎</span>
            <input
              type="search"
              class="form-control"
              placeholder="Tìm sản phẩm..."
              :value="querySP"
              @input="onQuerySPInput($event.target.value)"
              aria-label="Tìm sản phẩm trong danh mục"
            />
          </div>

          <div class="input-group input-group-sm">
            <span class="input-group-text">Sắp xếp</span>
            <select class="form-select" v-model="sortBy">
              <option value="tenSanPham">Tên</option>
              <option value="thuongHieu">Thương hiệu</option>
              <option value="soLuong">Số lượng</option>
              <option value="gia">Giá</option>
            </select>
            <button
              class="btn btn-outline-secondary"
              type="button"
              @click="toggleSortDir"
              :title="sortDir === 'asc' ? 'Đang tăng dần' : 'Đang giảm dần'"
            >
              {{ sortDir === 'asc' ? '⬆️' : '⬇️' }}
            </button>
          </div>

          <div class="input-group input-group-sm">
            <span class="input-group-text">Hiển thị</span>
            <select class="form-select" v-model.number="perPage">
              <option :value="10">10</option>
              <option :value="20">20</option>
              <option :value="50">50</option>
            </select>
            <span class="input-group-text">/ trang</span>
          </div>
        </div>
      </div>

      <!-- Loading products (spinner or skeleton) -->
      <div v-if="loadingSanPham" class="text-center my-4" aria-live="polite">
        <div class="spinner-border text-secondary" role="status">
          <span class="visually-hidden">Đang tải sản phẩm...</span>
        </div>
        <div class="mt-2 text-muted small">Đang tải sản phẩm...</div>
      </div>

      <!-- Error products -->
      <div v-else-if="errorSanPham" class="alert alert-warning d-flex justify-content-between align-items-center">
        <span>{{ errorSanPham }}</span>
        <button class="btn btn-sm btn-outline-secondary" @click="retryLoadSanPham">Thử lại</button>
      </div>

      <!-- Products table -->
      <div v-else>
        <div class="table-responsive">
          <table class="table table-striped table-hover align-middle mb-2">
            <thead class="table-dark position-sticky top-0" style="z-index: 1;">
              <tr>
                <th style="width: 56px;">#</th>
                <th @click="setSort('tenSanPham')" role="button">
                  Tên sản phẩm
                  <SortIcon :active="sortBy === 'tenSanPham'" :dir="sortDir" />
                </th>
                <th @click="setSort('thuongHieu')" role="button">
                  Thương hiệu
                  <SortIcon :active="sortBy === 'thuongHieu'" :dir="sortDir" />
                </th>
                <th class="text-end" @click="setSort('soLuong')" role="button" style="width: 120px;">
                  Số lượng
                  <SortIcon :active="sortBy === 'soLuong'" :dir="sortDir" />
                </th>
                <th class="text-end" @click="setSort('gia')" role="button" style="width: 160px;">
                  Giá
                  <SortIcon :active="sortBy === 'gia'" :dir="sortDir" />
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(sp, idx) in pagedSanPham" :key="sp.maSanPham" class="align-middle">
                <td>{{ (currentPage - 1) * perPage + idx + 1 }}</td>
                <td class="text-truncate" :title="sp.tenSanPham">{{ sp.tenSanPham }}</td>
                <td class="text-truncate" :title="sp.thuongHieu">{{ sp.thuongHieu }}</td>
                <td class="text-end">{{ formatNumber(sp.soLuong) }}</td>
                <td class="text-end">{{ formatCurrency(sp.gia) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <p v-if="filteredSanPham.length === 0" class="text-center text-muted my-4">
          Không có sản phẩm nào trong danh mục này.
        </p>

        <!-- Pagination -->
        <div v-else class="d-flex flex-wrap justify-content-between align-items-center gap-2">
          <small class="text-muted">
            Tổng: {{ filteredSanPham.length }} sản phẩm — Trang {{ currentPage }}/{{ totalPages }}
          </small>
          <nav aria-label="Phân trang sản phẩm">
            <ul class="pagination pagination-sm mb-0">
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="goPage(1)" :disabled="currentPage === 1">«</button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === 1 }">
                <button class="page-link" @click="goPage(currentPage - 1)" :disabled="currentPage === 1">‹</button>
              </li>
              <li class="page-item disabled">
                <span class="page-link bg-light">{{ currentPage }}</span>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="goPage(currentPage + 1)" :disabled="currentPage === totalPages">›</button>
              </li>
              <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                <button class="page-link" @click="goPage(totalPages)" :disabled="currentPage === totalPages">»</button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from "vue";
import { fetchDanhMuc, getDanhMuc } from "@/service/api";

// Local sort icon component for visual cue
const SortIcon = {
  props: { active: Boolean, dir: String },
  template: `
    <span class="ms-1 text-nowrap" :class="active ? 'text-warning' : 'text-muted'">
      <span v-if="!active">↕︎</span>
      <span v-else-if="dir==='asc'">⬆️</span>
      <span v-else>⬇️</span>
    </span>
  `
};

const danhMucList = ref([]);
const sanPhamList = ref([]);
const loading = ref(true);
const error = ref(null);

const selectedDanhMuc = ref(null);
const loadingSanPham = ref(false);
const errorSanPham = ref(null);

const productSection = ref(null);

// UX controls
const query = ref("");
const querySP = ref("");
const queryTimer = ref(null);
const querySPTimer = ref(null);

const sortBy = ref("tenSanPham");
const sortDir = ref("asc");
const perPage = ref(10);
const currentPage = ref(1);

// Derived
const maxTongSP = computed(() => {
  if (!danhMucList.value.length) return 100;
  return Math.max(...danhMucList.value.map(d => d.tongSanPham || 0), 100);
});

const filteredDanhMuc = computed(() => {
  const q = query.value.trim().toLowerCase();
  if (!q) return danhMucList.value;
  return danhMucList.value.filter(d =>
    (d.tenDanhMuc || "").toLowerCase().includes(q) ||
    (d.moTa || "").toLowerCase().includes(q)
  );
});

const filteredSanPham = computed(() => {
  const q = querySP.value.trim().toLowerCase();
  let list = [...sanPhamList.value];
  if (q) {
    list = list.filter(sp =>
      (sp.tenSanPham || "").toLowerCase().includes(q) ||
      (sp.thuongHieu || "").toLowerCase().includes(q)
    );
  }
  // sort
  const key = sortBy.value;
  const dir = sortDir.value === "asc" ? 1 : -1;
  list.sort((a, b) => {
    const va = a?.[key] ?? "";
    const vb = b?.[key] ?? "";
    if (typeof va === "number" && typeof vb === "number") return (va - vb) * dir;
    return String(va).localeCompare(String(vb), "vi", { sensitivity: "base" }) * dir;
  });
  return list;
});

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredSanPham.value.length / perPage.value));
});

const pagedSanPham = computed(() => {
  const start = (currentPage.value - 1) * perPage.value;
  return filteredSanPham.value.slice(start, start + perPage.value);
});

// Data loaders
const loadDanhMuc = async () => {
  loading.value = true;
  error.value = null;
  try {
    danhMucList.value = await fetchDanhMuc();
  } catch (err) {
    console.error("Lỗi khi gọi API danh mục:", err);
    error.value = "Không thể tải danh mục. Vui lòng thử lại.";
  } finally {
    loading.value = false;
  }
};

const loadSanPhamByDanhMuc = async (dm) => {
  loadingSanPham.value = true;
  errorSanPham.value = null;
  try {
    const res = await getDanhMuc(dm.maDanhMuc);
    sanPhamList.value = res.data || [];

    // sau khi load xong thì cuộn xuống table
    await nextTick();
    productSection.value?.scrollIntoView({ behavior: "smooth", block: "start" });
  } catch (err) {
    console.error("Lỗi khi gọi API sản phẩm:", err);
    errorSanPham.value = "Không thể tải sản phẩm.";
  } finally {
    loadingSanPham.value = false;
  }
};

// Handlers
const chonDanhMuc = async (dm) => {
  if (!dm) return;
  selectedDanhMuc.value = dm;
  sanPhamList.value = [];
  currentPage.value = 1;
  await loadSanPhamByDanhMuc(dm);
};

const retryLoadSanPham = async () => {
  if (selectedDanhMuc.value) await loadSanPhamByDanhMuc(selectedDanhMuc.value);
};

const onQueryInput = (val) => {
  clearTimeout(queryTimer.value);
  queryTimer.value = setTimeout(() => {
    query.value = val;
  }, 300);
};

const onQuerySPInput = (val) => {
  clearTimeout(querySPTimer.value);
  querySPTimer.value = setTimeout(() => {
    querySP.value = val;
    currentPage.value = 1;
  }, 300);
};

const setSort = (key) => {
  if (sortBy.value === key) {
    toggleSortDir();
  } else {
    sortBy.value = key;
    sortDir.value = "asc";
  }
};

const toggleSortDir = () => {
  sortDir.value = sortDir.value === "asc" ? "desc" : "asc";
};

const goPage = (p) => {
  const clamped = Math.min(Math.max(1, p), totalPages.value);
  currentPage.value = clamped;
};

const clearSelection = () => {
  selectedDanhMuc.value = null;
  sanPhamList.value = [];
};

const resetQuery = () => {
  query.value = "";
};

const refreshAll = async () => {
  await loadDanhMuc();
  if (selectedDanhMuc.value) {
    await loadSanPhamByDanhMuc(selectedDanhMuc.value);
  }
};

// Formatters
const formatCurrency = (value) => {
  const num = Number(value) || 0;
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(num);
};

const formatNumber = (value) => {
  const num = Number(value) || 0;
  return new Intl.NumberFormat("vi-VN").format(num);
};

onMounted(loadDanhMuc);
</script>

<style scoped>
.section-title {
  font-weight: 700;
}
.category-card {
  transition: transform 0.15s ease, box-shadow 0.15s ease;
  border-radius: 12px;
}
.category-card:hover,
.category-card:focus-within {
  transform: translateY(-2px);
  box-shadow: 0 0.6rem 1.2rem rgba(0,0,0,0.08) !important;
}
.table thead th {
  user-select: none;
}
.text-truncate {
  max-width: 320px;
}
@media (max-width: 576px) {
  .text-truncate {
    max-width: 160px;
  }
}
</style>
