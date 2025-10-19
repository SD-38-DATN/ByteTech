<template>
  <div class="container-fluid py-4 px-md-4">
    <div class="d-flex flex-wrap align-items-center justify-content-between mb-4">
      <div class="d-flex align-items-center gap-3">
        <h2 class="mb-0">Quản lý Sản phẩm</h2>
        <span class="badge bg-primary-subtle text-primary border fs-6">
          {{ totalSanPhams }} sản phẩm
        </span>
      </div>
      <div class="d-flex align-items-center gap-2 mt-2 mt-md-0">
        <button class="btn btn-outline-secondary" @click="reloadPage">
          <i class="bi bi-arrow-clockwise me-2"></i>Tải lại
        </button>
        <button class="btn btn-primary" @click="openSanPhamModal()">
          <i class="bi bi-plus-circle me-2"></i>Thêm sản phẩm mới
        </button>
      </div>
    </div>
    <div class="card p-3 mb-4 shadow-sm">
      <div class="row g-3 align-items-end">
        <div class="col-lg-3 col-md-6">
          <label class="form-label mb-1 fw-semibold">Tìm kiếm</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-search"></i></span>
            <input v-model="query" type="text" class="form-control" placeholder="Tên/Mã SP, SKU..." />
            <button class="btn btn-outline-secondary" type="button" @click="query = ''">Xóa</button>
          </div>
        </div>
        <div class="col-lg-3 col-md-6">
          <label class="form-label mb-1 fw-semibold">Lọc theo trạng thái</label>
          <select v-model="statusFilter" class="form-select">
            <option value="">Tất cả trạng thái</option>
            <option value="1">Hoạt động</option>
            <option value="0">Ẩn</option>
          </select>
        </div>
        <div class="col-lg-3 col-md-6">
          <label class="form-label mb-1 fw-semibold">Lọc theo thương hiệu</label>
          <select v-model="brandFilter" class="form-select">
            <option value="">Tất cả thương hiệu</option>
            <option v-for="brand in brands" :key="brand" :value="brand">
              {{ brand || '—' }}
            </option>
          </select>
        </div>
        <div class="col-lg-3 col-md-6">
          <label class="form-label mb-1 fw-semibold">Lọc theo danh mục</label>
          <select v-model="categoryFilter" class="form-select">
            <option value="">Tất cả danh mục</option>
            <option v-for="opt in categoryOptions" :key="opt.value" :value="opt.value">
              {{ opt.label || ('#' + opt.value) }}
            </option>
          </select>
        </div>
      </div>
    </div>
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <div v-if="filtered.length" class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
              <tr>
                <th style="width: 48px"></th>
                <th role="button" @click="toggleSort('maSanPham')">Mã SP <SortIcon :active="sortBy==='maSanPham'" :dir="sortDir" /></th>
                <th style="min-width: 320px;" role="button" @click="toggleSort('tenSanPham')">Tên sản phẩm <SortIcon :active="sortBy==='tenSanPham'" :dir="sortDir" /></th>
                <th role="button" @click="toggleSort('thuongHieu')">Thương hiệu <SortIcon :active="sortBy==='thuongHieu'" :dir="sortDir" /></th>
                <th role="button" @click="toggleSort('tenDanhMuc')">Danh mục <SortIcon :active="sortBy==='tenDanhMuc'" :dir="sortDir" /></th>
                <th class="text-center" role="button" @click="toggleSort('soLuong')">Tổng SL <SortIcon :active="sortBy==='soLuong'" :dir="sortDir" /></th>
                <th class="text-center" role="button" @click="toggleSort('gia')">Giá thấp nhất <SortIcon :active="sortBy==='gia'" :dir="sortDir" /></th>
                <th class="text-center" style="width: 140px;">Trạng thái</th>
                <th class="text-center">Biến thể</th>
                <th style="width: 280px; text-align: center;">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="sp in paginated" :key="sp.maSanPham">
                <tr
                  class="product-row"
                  :class="{ 'product-row-expanded': expanded.has(sp.maSanPham) }"
                  @click="toggleExpand(sp.maSanPham)"
                >
                  <td>
                    <button class="btn btn-sm btn-outline-secondary border-0" :title="expanded.has(sp.maSanPham) ? 'Thu gọn' : 'Xem biến thể'">
                      <i :class="expanded.has(sp.maSanPham) ? 'bi bi-chevron-up' : 'bi bi-chevron-down'"></i>
                    </button>
                  </td>
                  <td><code>{{ sp.maSanPham }}</code></td>
                  <td>
                    <div class="d-flex flex-column">
                      <span class="fw-semibold">{{ sp.tenSanPham }}</span>
                      <small v-if="sp.moTa" class="text-muted d-none d-xl-inline">{{ sp.moTa }}</small>
                    </div>
                  </td>
                  <td><span class="badge bg-light text-dark border">{{ sp.thuongHieu || '—' }}</span></td>
                  <td>
                    <span class="badge bg-white text-dark border">{{ displayDanhMuc(sp) }}</span>
                  </td>
                  <td class="text-center">{{ sp.soLuong }}</td>
                  <td class="text-center">{{ Number(sp.gia || 0).toLocaleString() }}₫</td>
                  <td @click.stop>
                    <StatusToggle
                      :model-value="sp.trangThai === 1"
                      :loading="loadingStates['product-' + sp.maSanPham]"
                      @change="updateSanPhamStatus(sp, $event)"
                    />
                  </td>
                  <td class="text-center">
                    <span class="badge rounded-pill bg-info text-dark fs-6 fw-bold">
                      {{ (sp.bienTheList && sp.bienTheList.length) ? sp.bienTheList.length : 0 }}
                    </span>
                  </td>
                  <td class="text-center" @click.stop>
                    <div class="btn-group">
                      <button class="btn btn-sm btn-primary" @click="openBienTheModal(sp)"><i class="bi bi-diagram-3 me-1"></i> Quản lý biến thể</button>
                      <button type="button" class="btn btn-sm btn-primary dropdown-toggle dropdown-toggle-split" data-bs-toggle="dropdown" aria-expanded="false">
                        <span class="visually-hidden">Toggle Dropdown</span>
                      </button>
                      <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#" @click.prevent="openSanPhamModal(sp)"><i class="bi bi-pencil-square me-2"></i>Sửa thông tin</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="#" @click.prevent="removeSanPham(sp.maSanPham)"><i class="bi bi-trash me-2"></i>Xóa sản phẩm</a></li>
                      </ul>
                    </div>
                  </td>
                </tr>

                <tr v-if="expanded.has(sp.maSanPham)">
                  <td colspan="10" class="p-0">
                    <div class="variant-container-wrapper">
                      <div class="p-3">
                        <h6 class="mb-3">
                          <i class="bi bi-diagram-3 me-2"></i>
                          Các biến thể của sản phẩm: <span class="fw-normal">{{ sp.tenSanPham }}</span>
                        </h6>
                        <div v-if="sp.bienTheList && sp.bienTheList.length" class="table-responsive">
                          <table class="table table-bordered table-light table-sm mb-0">
                            <thead class="table-dark">
                              <tr>
                                <th>SKU</th>
                                <th>Giá bán</th>
                                <th>Giá gốc</th>
                                <th class="text-center">Tồn kho</th>
                                <th class="text-center" style="width: 140px;">Trạng thái</th>
                                <th>Thuộc tính</th>
                                <th style="width: 200px">Thao tác</th>
                              </tr>
                            </thead>
                            <tbody>
                              <tr v-for="bt in sp.bienTheList" :key="bt.maSKU">
                                <td><code>{{ bt.maSKU }}</code></td>
                                <td class="text-primary fw-semibold">{{ Number(bt.gia || 0).toLocaleString() }}₫</td>
                                <td class="text-muted"><del>{{ Number(bt.giaKhongKhuyenMai || 0).toLocaleString() }}₫</del></td>
                                <td class="text-center">{{ bt.soLuong }}</td>
                                <td>
                                  <StatusToggle
                                    :model-value="bt.trangThai === 1"
                                    :loading="loadingStates['variant-' + bt.maSKU]"
                                    @change="updateBienTheStatus(sp, bt, $event)"
                                  />
                                </td>
                                <td>
                                  <div class="d-flex flex-wrap align-items-center gap-2">
                                    <span 
                                      v-for="tt in (bt.thuocTinhList || [])" 
                                      :key="tt.maThuocTinh" 
                                      class="badge bg-white text-dark border position-relative pe-4"
                                      role="button"
                                      title="Nhấn để sửa thuộc tính"
                                      @click="openThuocTinhModal(sp, bt, tt)"
                                    >
                                      {{ tt.tenThuocTinh }}: {{ tt.tenThuocTinhBienThe }}
                                      <button @click.stop="removeThuocTinh(sp, bt, tt.maThuocTinh)" class="btn-close btn-sm position-absolute top-50 translate-middle-y" style="right: 2px;" title="Xóa thuộc tính"></button>
                                    </span>
                                    <button class="btn btn-sm btn-outline-primary py-0 px-1" @click="openThuocTinhModal(sp, bt)" title="Thêm thuộc tính">
                                      <i class="bi bi-plus"></i>
                                    </button>
                                  </div>
                                </td>
                                <td>
                                  <div class="btn-group">
                                    <button class="btn btn-sm btn-outline-warning" @click="openBienTheModal(sp, bt)"><i class="bi bi-pencil-square me-1"></i>Sửa</button>
                                    <button class="btn btn-sm btn-outline-info" @click="openThuocTinhModal(sp, bt)"><i class="bi bi-tags me-1"></i>Thêm Thuộc tính</button>
                                  </div>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                        <div v-else class="alert alert-secondary py-2 mt-2 mb-0 d-flex justify-content-between align-items-center">
                          <span>Sản phẩm này chưa có biến thể nào.</span>
                            <button class="btn btn-sm btn-primary" @click="openBienTheModal(sp)">
                            <i class="bi bi-plus-circle me-1"></i>Thêm biến thể ngay
                          </button>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
        <div v-else class="alert alert-info m-3">
          Chưa có sản phẩm nào hoặc không tìm thấy kết quả phù hợp.
        </div>
      </div>
    </div>
    <div v-if="filtered.length > pageSize" class="d-flex align-items-center justify-content-between mt-3">
      <div class="text-muted small">
        Hiển thị {{ pageStart + 1 }}–{{ Math.min(pageStart + pageSize, filtered.length) }} trên tổng số {{ filtered.length }} sản phẩm
      </div>
      <nav>
        <ul class="pagination mb-0">
          <li class="page-item" :class="{ disabled: page===1 }">
            <button class="page-link" @click="page = Math.max(1, page-1)">Trước</button>
          </li>
          <li class="page-item" v-for="p in totalPages" :key="p" :class="{ active: page===p }">
            <button class="page-link" @click="page = p">{{ p }}</button>
          </li>
          <li class="page-item" :class="{ disabled: page===totalPages }">
            <button class="page-link" @click="page = Math.min(totalPages, page+1)">Sau</button>
          </li>
        </ul>
      </nav>
    </div>
    </div>

  <div class="modal fade" id="sanPhamModal" tabindex="-1" ref="sanPhamModalRef">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <form @submit.prevent="saveSanPham">
          <div class="modal-header">
            <h5 class="modal-title">{{ formSanPham.maSanPham ? "Cập nhật Sản phẩm" : "Thêm Sản phẩm mới" }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Tên sản phẩm</label>
              <input v-model="formSanPham.tenSanPham" type="text" class="form-control" required />
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Danh mục</label>
                <select v-model.number="formSanPham.maDanhMuc" class="form-select" required>
                  <option value="" disabled>-- Chọn danh mục --</option>
                  <option v-for="opt in categoryOptions" :key="opt.value" :value="opt.value">
                    {{ opt.label || ('#' + opt.value) }}
                  </option>
                </select>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Thương hiệu</label>
                <input v-model="formSanPham.thuongHieu" type="text" class="form-control" required />
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                  <label class="form-label">Trạng thái</label>
                  <select v-model.number="formSanPham.trangThai" class="form-select">
                    <option :value="1">Hoạt động</option>
                    <option :value="0">Ẩn</option>
                  </select>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Người tạo (UserID)</label>
                <input v-model.number="formSanPham.userId" type="number" class="form-control" required />
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">Mô tả</label>
              <textarea v-model="formSanPham.moTa" class="form-control" rows="3"></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="submit" class="btn btn-primary">
              <i :class="formSanPham.maSanPham ? 'bi bi-check-circle' : 'bi bi-plus-circle'" class="me-2"></i>
              {{ formSanPham.maSanPham ? "Lưu thay đổi" : "Thêm mới" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <div class="modal fade" id="bienTheModal" tabindex="-1" ref="bienTheModalRef">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <form @submit.prevent="saveBienThe">
          <div class="modal-header">
            <div>
              <h5 class="modal-title">{{ formBienThe.originalMaSKU ? 'Cập nhật biến thể' : 'Thêm biến thể mới' }}</h5>
              <div class="small text-muted mt-1">
                <i class="bi bi-box-seam"></i>
                Thuộc sản phẩm:
                <span class="badge bg-secondary ms-1">{{ parentProduct.maSanPham || '—' }}</span>
                <span class="ms-2">{{ parentProduct.tenSanPham || '—' }}</span>
              </div>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <h6 class="text-primary">Thông tin cơ bản</h6>
            <hr class="mt-1">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Mã SKU</label>
                <input v-model="formBienThe.maSKU" type="text" class="form-control" :disabled="!!formBienThe.originalMaSKU" required>
                <small v-if="!!formBienThe.originalMaSKU" class="form-text text-muted">Không thể thay đổi SKU.</small>
              </div>
               <div class="col-md-6 mb-3">
                <label class="form-label">Trạng thái</label>
                <select v-model.number="formBienThe.trangThai" class="form-select">
                  <option :value="1">Hoạt động</option>
                  <option :value="0">Ẩn</option>
                </select>
              </div>
            </div>

            <h6 class="text-primary mt-3">Giá & Kho hàng</h6>
            <hr class="mt-1">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Giá bán</label>
                <div class="input-group">
                  <input v-model.number="formBienThe.gia" type="number" class="form-control" min="0" required>
                    <span class="input-group-text">₫</span>
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Giá gốc (chưa khuyến mãi)</label>
                <div class="input-group">
                    <input v-model.number="formBienThe.giaKhongKhuyenMai" type="number" class="form-control" min="0" required>
                  <span class="input-group-text">₫</span>
                </div>
              </div>
            </div>
             <div class="row">
               <div class="col-md-6 mb-3">
                <label class="form-label">Số lượng tồn kho</label>
                <input v-model.number="formBienThe.soLuong" type="number" class="form-control" min="0" required>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button type="submit" class="btn btn-primary" :disabled="isSaving">
              <span v-if="isSaving" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Lưu thay đổi
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <div class="modal fade" id="thuocTinhModal" tabindex="-1" ref="thuocTinhModalRef">
    <div class="modal-dialog">
      <div class="modal-content">
        <form @submit.prevent="saveThuocTinh">
          <div class="modal-header">
            <div>
              <h5 class="modal-title">{{ formThuocTinh.maThuocTinh ? 'Cập nhật thuộc tính' : 'Thêm thuộc tính mới' }}</h5>
              <div class="small text-muted mt-1">
                Thuộc SKU: <code class="ms-1">{{ currentBienTheSKU || '—' }}</code>
              </div>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Tên thuộc tính (VD: Màu sắc, Kích cỡ)</label>
              <input v-model="formThuocTinh.tenThuocTinh" type="text" class="form-control" required>
            </div>
            <div class="mb-3">
              <label class="form-label">Giá trị thuộc tính (VD: Đỏ, Xanh, XXL)</label>
              <input v-model="formThuocTinh.tenThuocTinhBienThe" type="text" class="form-control" required>
            </div>
              <div class="mb-3">
                <label class="form-label">Trạng thái</label>
                <select v-model.number="formThuocTinh.trangThai" class="form-select">
                  <option :value="1">Hoạt động</option>
                  <option :value="0">Ẩn</option>
                </select>
              </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <div class="modal fade" id="notifyModal" tabindex="-1" ref="notifyModalRef">
    <div class="modal-dialog modal-sm modal-dialog-centered">
      <div class="modal-content" :style="notifyGradientStyle">
        <div class="modal-header border-0 text-white">
          <h5 class="modal-title">
            {{ notify.type === 'success' ? 'Thành công' : 'Lỗi' }}
          </h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body text-white">
          <p class="mb-2">{{ notify.message }}</p>
          <div class="small">Tự đóng sau: <strong>{{ notify.secondsLeft }}</strong>s</div>
          <div class="progress mt-2" style="height: 6px;">
            <div class="progress-bar" role="progressbar"
                 :style="{ width: notifyProgress + '%' }"
                 :aria-valuenow="notifyProgress" aria-valuemin="0" aria-valuemax="100"></div>
          </div>
        </div>
        <div class="modal-footer border-0">
          <button type="button" class="btn btn-light btn-sm" data-bs-dismiss="modal">Đóng ngay</button>
        </div>
      </div>
    </div>
  </div>
  </template>

<script setup>
import { ref, computed, onMounted, h } from "vue";
import { Modal } from 'bootstrap';
import {
  fetchAllAdminProducts, createAdminProduct, updateAdminProduct, deleteAdminProduct,
  deleteAdminVariant, deleteAdminAttribute
} from "@/service/api";
import StatusToggle from '@/components/StatusToggle.vue';

// Sort icon component
const SortIcon = ({ active, dir }) => {
  if (!active) return h('i', { class: 'bi bi-arrow-down-up ms-1 text-black-50', style: 'font-size: 0.8em;' });
  return dir === 'asc'
    ? h('i', { class: 'bi bi-sort-up ms-1 text-primary' })
    : h('i', { class: 'bi bi-sort-down ms-1 text-primary' });
};

// Data
const sanPhams = ref([]);
const totalSanPhams = computed(() => sanPhams.value.length);

// UI state
const query = ref("");
const brandFilter = ref("");
const categoryFilter = ref("");
const statusFilter = ref(""); // State cho bộ lọc trạng thái mới
const sortBy = ref("tenSanPham");
const sortDir = ref("asc");
const isSaving = ref(false);
const loadingStates = ref({});

// Pagination, Expand rows, Computed properties
const page = ref(1);
const pageSize = ref(8);
const pageStart = computed(() => (page.value - 1) * pageSize.value);
const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize.value)));
const paginated = computed(() => sorted.value.slice(pageStart.value, pageStart.value + pageSize.value));
const expanded = ref(new Set());
const toggleExpand = (id) => {
  if (expanded.value.has(id)) expanded.value.delete(id);
  else expanded.value.add(id);
};
const brands = computed(() => Array.from(new Set(sanPhams.value.map(sp => sp.thuongHieu || ""))).sort());
const categoryOptions = computed(() => {
  const map = new Map();
  sanPhams.value.forEach(sp => {
    if (sp.maDanhMuc) map.set(sp.maDanhMuc, sp.tenDanhMuc || `Danh mục #${sp.maDanhMuc}`);
  });
  return Array.from(map.entries()).map(([value, label]) => ({ value, label })).sort((a, b) => a.label.localeCompare(b.label));
});
const displayDanhMuc = (sp) => sp.tenDanhMuc || (sp.maDanhMuc ? `#${sp.maDanhMuc}` : '—');

// Search & filter, Sort
const normalized = (v) => (v ?? "").toString().toLowerCase();
const filtered = computed(() => {
  page.value = 1;
  const q = normalized(query.value);
  return sanPhams.value.filter(sp => {
    const matchQuery = !q ||
      normalized(sp.tenSanPham).includes(q) ||
      normalized(sp.maSanPham).includes(q) ||
      normalized(sp.thuongHieu).includes(q) ||
      (sp.bienTheList || []).some(bt => normalized(bt.maSKU).includes(q));
    const matchBrand = !brandFilter.value || sp.thuongHieu === brandFilter.value;
    const matchCategory = !categoryFilter.value || sp.maDanhMuc === categoryFilter.value;
    // Thêm logic lọc theo trạng thái
    const matchStatus = statusFilter.value === "" || sp.trangThai === Number(statusFilter.value);
    
    return matchQuery && matchBrand && matchCategory && matchStatus;
  });
});
const sorted = computed(() => {
  return [...filtered.value].sort((a, b) => {
    const va = a[sortBy.value];
    const vb = b[sortBy.value];
    if (typeof va === 'number' && typeof vb === 'number') {
      return sortDir.value === 'asc' ? va - vb : vb - va;
    }
    const sa = normalized(va);
    const sb = normalized(vb);
    return sortDir.value === 'asc' ? sa.localeCompare(sb) : sb.localeCompare(sa);
  });
});
const toggleSort = (key) => {
  if (sortBy.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortBy.value = key;
    sortDir.value = 'asc';
  }
};

const sanPhamModalRef = ref(null);
let sanPhamModal = null;
const initialSanPhamState = { maSanPham: null, tenSanPham: "", moTa: "", trangThai: 1, maDanhMuc: '', userId: 1, thuongHieu: "", bienTheList: [] };
const formSanPham = ref({ ...initialSanPhamState });

const openSanPhamModal = (sp = null) => {
  formSanPham.value = sp ? JSON.parse(JSON.stringify(sp)) : { ...initialSanPhamState };
  sanPhamModal.show();
};

// Modal thông báo
const notifyModalRef = ref(null);
let notifyModal = null;
const notify = ref({ type: 'success', message: '', secondsLeft: 5 });
let notifyTimer = null;
const notifyDuration = 5;
const notifyProgress = computed(() => (notify.value.secondsLeft / notifyDuration) * 100);
const notifyGradientStyle = computed(() => {
  const [start, end] = notify.value.type === 'success' ? ['#4caf50', '#2e7d32'] : ['#f44336', '#b71c1c'];
  return { background: `linear-gradient(135deg, ${start}, ${end})`, color: '#fff' };
});
const showNotify = (type, message) => {
  notify.value = { type, message, secondsLeft: notifyDuration };
  notifyModal.show();
  clearInterval(notifyTimer);
  notifyTimer = setInterval(() => {
    notify.value.secondsLeft--;
    if (notify.value.secondsLeft <= 0) {
      clearInterval(notifyTimer);
      notifyModal.hide();
    }
  }, 1000);
};

// API Calls
const loadSanPhams = async () => {
  try {
    sanPhams.value = await fetchAllAdminProducts();
  } catch (error) {
    console.error("Lỗi khi tải danh sách sản phẩm:", error);
    showNotify('error', 'Lỗi tải dữ liệu sản phẩm.');
  }
};

// Hàm mới để reload toàn bộ trang
const reloadPage = () => {
  window.location.reload();
};

const saveSanPham = async () => {
  try {
    const payload = formSanPham.value;
    if (payload.maSanPham) {
      const updatedProduct = await updateAdminProduct(payload.maSanPham, payload);
      const index = sanPhams.value.findIndex(p => p.maSanPham === updatedProduct.maSanPham);
      if (index !== -1) sanPhams.value[index] = updatedProduct;
      showNotify('success', 'Cập nhật sản phẩm thành công.');
    } else {
      const newProduct = await createAdminProduct(payload);
      sanPhams.value.unshift(newProduct);
      showNotify('success', 'Thêm sản phẩm thành công.');
    }
    sanPhamModal.hide();
  } catch(error) {
    console.error("Lỗi khi lưu sản phẩm:", error);
    showNotify('error', 'Đã xảy ra lỗi khi lưu sản phẩm.');
  }
};

const removeSanPham = async (id) => {
  if (confirm(`Bạn chắc chắn muốn xóa sản phẩm có mã [${id}]? Hành động này sẽ xóa cả các biến thể con.`)) {
    try {
      await deleteAdminProduct(id);
      await loadSanPhams();
      showNotify('success', 'Xóa sản phẩm thành công.');
    } catch(error) {
      console.error("Lỗi khi xóa sản phẩm:", error);
      showNotify('error', 'Đã xảy ra lỗi khi xóa sản phẩm.');
    }
  }
};

const updateSanPhamStatus = async (product, newStatusBool) => {
  const loadingKey = `product-${product.maSanPham}`;
  loadingStates.value[loadingKey] = true;
  const newStatus = newStatusBool ? 1 : 0;

  try {
    const payload = JSON.parse(JSON.stringify(product));
    payload.trangThai = newStatus;
    const updatedProduct = await updateAdminProduct(product.maSanPham, payload);
    const index = sanPhams.value.findIndex(p => p.maSanPham === updatedProduct.maSanPham);
    if (index !== -1) {
      sanPhams.value[index] = updatedProduct;
    }
    showNotify('success', `Cập nhật trạng thái sản phẩm [${product.tenSanPham}] thành công.`);
  } catch (error) {
    console.error("Lỗi khi cập nhật trạng thái sản phẩm:", error);
    showNotify('error', 'Lỗi cập nhật trạng thái sản phẩm.');
  } finally {
    loadingStates.value[loadingKey] = false;
  }
};

// Biến thể
const bienTheModalRef = ref(null);
let bienTheModal = null;
const initialBienTheState = { maSKU: '', originalMaSKU: null, gia: 0, giaKhongKhuyenMai: 0, soLuong: 0, trangThai: 1, thuocTinhList: [] };
const formBienThe = ref({ ...initialBienTheState });
const parentProduct = ref({});

const openBienTheModal = (sp, bt = null) => {
  parentProduct.value = sp;
  formBienThe.value = bt ? JSON.parse(JSON.stringify({ ...bt, originalMaSKU: bt.maSKU })) : { ...initialBienTheState };
  bienTheModal.show();
};

const saveBienThe = async () => {
  isSaving.value = true;
  try {
    const productToUpdate = sanPhams.value.find(p => p.maSanPham === parentProduct.value.maSanPham);
    if (!productToUpdate) throw new Error("Không tìm thấy sản phẩm cha!");

    const { originalMaSKU, ...variantData } = formBienThe.value;
    const productPayload = JSON.parse(JSON.stringify(productToUpdate));

    if (originalMaSKU) {
      const variantIndex = productPayload.bienTheList.findIndex(v => v.maSKU === originalMaSKU);
      if (variantIndex !== -1) {
        productPayload.bienTheList[variantIndex] = variantData;
      }
    } else {
      if (!productPayload.bienTheList) productPayload.bienTheList = [];
      if (productPayload.bienTheList.some(v => v.maSKU === variantData.maSKU)) {
        showNotify('error', `Mã SKU [${variantData.maSKU}] đã tồn tại.`);
        isSaving.value = false;
        return;
      }
      productPayload.bienTheList.push(variantData);
    }

    const updatedProduct = await updateAdminProduct(productPayload.maSanPham, productPayload);
    const index = sanPhams.value.findIndex(p => p.maSanPham === updatedProduct.maSanPham);
    if (index !== -1) sanPhams.value[index] = updatedProduct;
    
    bienTheModal.hide();
    showNotify('success', 'Lưu biến thể thành công.');
  } catch(error) {
    console.error("Lỗi khi lưu biến thể:", error);
    showNotify('error', 'Đã xảy ra lỗi khi lưu biến thể.');
  } finally {
    isSaving.value = false;
  }
};

const updateBienTheStatus = async (product, variant, newStatusBool) => {
  const loadingKey = `variant-${variant.maSKU}`;
  loadingStates.value[loadingKey] = true;
  const newStatus = newStatusBool ? 1 : 0;

  try {
    const productToUpdate = sanPhams.value.find(p => p.maSanPham === product.maSanPham);
    if (!productToUpdate) throw new Error("Không tìm thấy sản phẩm cha!");

    const productPayload = JSON.parse(JSON.stringify(productToUpdate));
    
    const variantToUpdate = productPayload.bienTheList.find(v => v.maSKU === variant.maSKU);
    if (variantToUpdate) {
        variantToUpdate.trangThai = newStatus;
    } else {
      throw new Error("Không tìm thấy biến thể trong sản phẩm payload!");
    }
    
    const updatedProduct = await updateAdminProduct(productPayload.maSanPham, productPayload);
    const index = sanPhams.value.findIndex(p => p.maSanPham === updatedProduct.maSanPham);
    if (index !== -1) {
      sanPhams.value[index] = updatedProduct;
    }

    showNotify('success', `Cập nhật trạng thái SKU [${variant.maSKU}] thành công.`);
  } catch (error) {
    console.error("Lỗi khi cập nhật trạng thái biến thể:", error);
    showNotify('error', 'Lỗi cập nhật trạng thái biến thể.');
  } finally {
    loadingStates.value[loadingKey] = false;
  }
};

// Thuộc tính
const thuocTinhModalRef = ref(null);
let thuocTinhModal = null;
const initialThuocTinhState = { maThuocTinh: null, tenThuocTinh: '', tenThuocTinhBienThe: '', trangThai: 1 };
const formThuocTinh = ref({ ...initialThuocTinhState });
const currentBienTheSKU = ref(null);

const openThuocTinhModal = (sp, bt, tt = null) => {
  parentProduct.value = sp;
  currentBienTheSKU.value = bt.maSKU;
  formThuocTinh.value = tt ? JSON.parse(JSON.stringify(tt)) : { ...initialThuocTinhState };
  thuocTinhModal.show();
};
const saveThuocTinh = async () => {
  try {
    const productToUpdate = sanPhams.value.find(p => p.maSanPham === parentProduct.value.maSanPham);
    if (!productToUpdate) throw new Error("Không tìm thấy sản phẩm cha!");

    const productPayload = JSON.parse(JSON.stringify(productToUpdate));
    
    const variantToUpdate = productPayload.bienTheList.find(v => v.maSKU === currentBienTheSKU.value);
    if (!variantToUpdate) throw new Error("Không tìm thấy biến thể cha!");

    const attributeData = formThuocTinh.value;

    if (attributeData.maThuocTinh) {
      const attrIndex = variantToUpdate.thuocTinhList.findIndex(a => a.maThuocTinh === attributeData.maThuocTinh);
      if (attrIndex !== -1) {
        variantToUpdate.thuocTinhList[attrIndex] = attributeData;
      }
    } else {
      if (!variantToUpdate.thuocTinhList) variantToUpdate.thuocTinhList = [];
      variantToUpdate.thuocTinhList.push(attributeData);
    }

    const updatedProduct = await updateAdminProduct(productPayload.maSanPham, productPayload);
    const index = sanPhams.value.findIndex(p => p.maSanPham === updatedProduct.maSanPham);
    if (index !== -1) sanPhams.value[index] = updatedProduct;

    thuocTinhModal.hide();
    showNotify('success', 'Lưu thuộc tính thành công.');
  } catch (error) {
    console.error("Lỗi khi lưu thuộc tính:", error);
    showNotify('error', 'Đã xảy ra lỗi khi lưu thuộc tính.');
  }
};
const removeThuocTinh = async (parentSp, parentBt, attributeId) => {
  if (confirm(`Bạn chắc chắn muốn xóa thuộc tính này?`)) {
    try {
      await deleteAdminAttribute(attributeId);
      await loadSanPhams();
      showNotify('success', 'Xóa thuộc tính thành công.');
    } catch(error) {
      console.error("Lỗi khi xóa thuộc tính:", error);
      showNotify('error', 'Đã xảy ra lỗi khi xóa thuộc tính.');
    }
  }
};

onMounted(() => {
  loadSanPhams();
  // Khởi tạo tất cả các modal
  sanPhamModal = new Modal(sanPhamModalRef.value);
  bienTheModal = new Modal(bienTheModalRef.value);
  thuocTinhModal = new Modal(thuocTinhModalRef.value);
  notifyModal = new Modal(notifyModalRef.value, { backdrop: 'static' });
});
</script>

<style scoped>
/* YÊU CẦU 1 & 2: Cải thiện UX/UI cho việc click vào dòng */

/* Thêm con trỏ tay và hiệu ứng chuyển động mượt mà cho dòng sản phẩm */
.product-row {
  cursor: pointer;
  transition: background-color 0.15s ease-in-out;
}

/* Thêm hiệu ứng hover để người dùng biết dòng này có tương tác */
.product-row:hover {
  background-color: var(--bs-table-hover-bg);
}

/* Làm nổi bật dòng đang được chọn (đã xổ ra) */
.product-row.product-row-expanded {
  background-color: var(--bs-primary-bg-subtle);
  --bs-table-accent-bg: var(--bs-primary-bg-subtle); /* Ghi đè màu của Bootstrap để đảm bảo hiển thị */
}

/* Định dạng vùng chứa bảng biến thể con */
.variant-container-wrapper {
  background-color: #f8f9fa; /* Màu nền hơi xám để phân biệt */
  border-bottom: 1px solid var(--bs-border-color-translucent);
  border-top: 1px solid var(--bs-border-color-translucent);
}

.table > :not(caption) > * > * {
  vertical-align: middle;
}
</style>