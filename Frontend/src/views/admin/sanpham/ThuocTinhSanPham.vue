<template>
  <div class="container-fluid py-4 px-md-4">
    <div class="d-flex flex-wrap align-items-center justify-content-between mb-4">
      <div class="d-flex align-items-center">
          <i class="bi bi-upc-scan fs-2 me-3 text-primary"></i>
          <h2 class="mb-0">Quản lý Kho IMEI/Serial</h2>
      </div>
      <button class="btn btn-primary d-flex align-items-center" @click="openModal()">
        <i class="bi bi-plus-circle me-2"></i>Nhập IMEI mới
      </button>
    </div>

    <div class="card p-3 mb-4 shadow-sm border-0">
      <div class="row g-3 align-items-center">
        <div class="col">
          <label class="form-label mb-1 fw-semibold text-muted">Tìm kiếm</label>
          <div class="input-group">
            <span class="input-group-text bg-light border-end-0"><i class="bi bi-search"></i></span>
            <input v-model="searchQuery" type="text" class="form-control border-start-0" placeholder="Tên sản phẩm, SKU, hoặc IMEI..." />
            <button v-if="searchQuery" class="btn btn-outline-secondary" @click="searchQuery = ''"><i class="bi bi-x-lg"></i></button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="Object.keys(filteredData).length > 0">
        <div v-for="(product, maSanPham) in paginatedData" :key="maSanPham" class="card shadow-sm mb-3 border-0 product-card">
            <div class="card-header bg-white p-3" @click="toggleProductExpansion(maSanPham)" style="cursor: pointer;">
                <div class="d-flex justify-content-between w-100 align-items-center">
                    <div class="d-flex flex-column">
                        <h5 class="mb-0 fw-bold">{{ product.tenSanPham }}</h5>
                        <small class="text-muted">Mã SP: {{ maSanPham }}</small>
                    </div>
                    <div class="d-flex align-items-center">
                        <div class="text-end me-3">
                            <span class="fw-bold fs-5">{{ product.totalImeiCount }}</span>
                            <span class="text-muted">/ {{ product.totalSoLuong }}</span>
                            <div class="progress mt-1" style="height: 6px; width: 100px;">
                               <div class="progress-bar" role="progressbar" :style="{ width: (product.totalSoLuong > 0 ? (product.totalImeiCount / product.totalSoLuong * 100) : 0) + '%' }"></div>
                            </div>
                        </div>
                        <i class="bi fs-4 transition-transform" :class="isProductExpanded(maSanPham) ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
                    </div>
                </div>
            </div>
            <div :id="'collapse-product-' + maSanPham" class="accordion-collapse collapse" :class="{ show: isProductExpanded(maSanPham) }">
                <div class="card-body p-2">
                    <div v-for="(variant, maSKU) in product.variants" :key="maSKU" class="variant-block">
                        <div class="d-flex flex-wrap justify-content-between align-items-center mb-2 gap-2 p-2">
                            <h6 class="mb-0">SKU: <code class="fs-6">{{ maSKU }}</code></h6>
                            <div class="d-flex align-items-center gap-3">
                                <div style="width: 180px;">
                                    <select v-model="variantStatusFilters[maSKU]" class="form-select form-select-sm">
                                        <option value="">Tất cả trạng thái</option>
                                        <option value="1">Còn trong kho</option>
                                        <option value="0">Đã bán</option>
                                    </select>
                                </div>
                                <span class="badge bg-light text-dark border">
                                    {{ getFilteredImeisForVariant(maSKU, true) }} / {{ variant.soLuongBienThe }}
                                </span>
                            </div>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-sm table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>IMEI / Serial</th>
                                        <th>Ngày nhập</th>
                                        <th class="text-center">Trạng thái</th>
                                        <th class="text-center">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="imei in getFilteredImeisForVariant(maSKU)" :key="imei.id">
                                        <td class="fw-bold align-middle">{{ imei.imei }}</td>
                                        <td class="align-middle">{{ formatDateTime(imei.ngayNhapKho) }}</td>
                                        <td class="text-center align-middle">
                                            <span :class="getStatusClass(imei.trangThai)" class="badge d-inline-flex align-items-center">
                                                <i class="bi me-1" :class="getStatusIcon(imei.trangThai)"></i>
                                                {{ getStatusText(imei.trangThai) }}
                                            </span>
                                        </td>
                                        <td class="text-center align-middle">
                                            <button class="btn btn-sm btn-icon" :class="{ 'disabled-button': imei.trangThai === 0 }" :title="imei.trangThai === 0 ? 'Không thể sửa' : 'Sửa IMEI'" @click="openModal(imei)">
                                                <i class="bi bi-pencil-square"></i>
                                            </button>
                                            <button class="btn btn-sm btn-icon text-danger" :class="{ 'disabled-button': imei.trangThai === 0 }" :title="imei.trangThai === 0 ? 'Không thể xóa' : 'Xóa IMEI'" @click="handleDelete(imei)">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                    <tr v-if="getFilteredImeisForVariant(maSKU).length === 0">
                                        <td colspan="4" class="text-center text-muted fst-italic py-3">Không có IMEI nào phù hợp.</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
       </div>
       <div v-else class="text-center p-5 bg-light rounded">
         <i class="bi bi-inbox fs-1"></i>
         <p class="mt-2 mb-0">Không tìm thấy sản phẩm hoặc IMEI nào.</p>
       </div>
    </div>

     <div v-if="totalPages > 1" class="d-flex align-items-center justify-content-between mt-4">
        <div class="text-muted small">
            Hiển thị <b>{{ Object.keys(paginatedData).length }}</b> sản phẩm trên tổng số <b>{{ Object.keys(filteredData).length }}</b>
        </div>
        <nav>
            <ul class="pagination mb-0">
                <li class="page-item" :class="{ disabled: page === 1 }"><button class="page-link" @click="page--">Trước</button></li>
                <li v-for="p in displayedPages" :key="p" class="page-item" :class="{ active: page === p, disabled: p === '...' }"><button class="page-link" @click="page = p">{{ p }}</button></li>
                <li class="page-item" :class="{ disabled: page === totalPages }"><button class="page-link" @click="page++">Sau</button></li>
            </ul>
        </nav>
    </div>
 
  <div class="modal fade" id="imeiModal" tabindex="-1" ref="imeiModalRef">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content border-0 shadow">
        <form @submit.prevent="handleSubmit">
          <div class="modal-header border-0">
            <h5 class="modal-title">{{ form.id ? 'Cập nhật IMEI' : 'Thêm IMEI mới' }}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">IMEI / Serial (*)</label>
              <input v-model.trim="form.imei" type="text" class="form-control" required>
            </div>
            <div class="mb-3">
              <label class="form-label">Biến thể sản phẩm (SKU) (*)</label>
              <v-select
                v-model="form.maSKU"
                :options="activeVariantOptions"
                :reduce="option => option.maSKU"
                label="tenHienThi"
                placeholder="Tìm kiếm sản phẩm hoặc SKU..."
                :disabled="!!form.id"
                class="v-select-custom"
              ></v-select>
              <small v-if="form.id" class="form-text text-muted mt-1 d-block">Không thể thay đổi SKU của IMEI đã tồn tại.</small>
            </div>
            <div class="mb-3">
              <label class="form-label">Trạng thái</label>
              <select v-model.number="form.trangThai" class="form-select">
                <option value="1">Còn trong kho</option>
                <option value="0">Đã bán</option>
              </select>
            </div>
          </div>
          <div class="modal-footer border-0">
            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style>
/* Style cho vue-select */
@import "vue-select/dist/vue-select.css";

.v-select-custom .vs__dropdown-toggle {
    border-radius: var(--bs-border-radius);
    border-color: var(--bs-border-color);
    padding: 0.2rem 0.5rem;
}

.v-select-custom.vs--disabled .vs__dropdown-toggle {
    background-color: var(--bs-tertiary-bg);
}

.disabled-button {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: auto !important; 
}
.product-card .card-header {
    transition: background-color 0.2s ease-in-out;
}
.product-card .card-header:hover {
    background-color: #f8f9fa !important;
}
.transition-transform {
    transition: transform 0.2s ease-in-out;
}
.variant-block {
    border: 1px solid #dee2e6;
    border-radius: 0.375rem;
    margin-bottom: 0.5rem;
    background: #fff;
}
.variant-block table {
    border-top: 1px solid #dee2e6;
}
.btn-icon {
    background: transparent;
    border: none;
    color: var(--bs-secondary-color);
}
.btn-icon:hover {
    color: var(--bs-primary);
}
.btn-icon.text-danger:hover {
    color: var(--bs-danger-dark) !important;
}
</style>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { Modal } from 'bootstrap';
import { fetchAllImeis, createImei, updateImei, deleteImei, fetchAllProductVariants } from '@/service/api';
import VSelect from 'vue-select';

const imeiList = ref([]);
const variantOptions = ref([]);
const searchQuery = ref('');
const page = ref(1);
const pageSize = ref(5);
let imeiModal = null;
const expandedProducts = ref(new Set());
const initialFormState = { id: null, imei: '', maSKU: '', trangThai: 1 };
const form = ref({ ...initialFormState });
const variantStatusFilters = ref({});

const toggleProductExpansion = (maSanPham) => {
    if (expandedProducts.value.has(maSanPham)) {
        expandedProducts.value.delete(maSanPham);
    } else {
        expandedProducts.value.add(maSanPham);
    }
};
const isProductExpanded = (maSanPham) => {
    return expandedProducts.value.has(maSanPham);
};

const activeVariantOptions = computed(() => {
    return variantOptions.value;
});

const groupedData = computed(() => {
    const groups = {};
    for (const imei of imeiList.value) {
        if (!imei.maSanPham) continue;

        if (!groups[imei.maSanPham]) {
            groups[imei.maSanPham] = {
                maSanPham: imei.maSanPham,
                tenSanPham: imei.tenSanPham,
                variants: {},
                totalSoLuong: 0,
                totalImeiCount: 0,
            };
        }
        const productGroup = groups[imei.maSanPham];
        const sku = imei.maSKU || imei.maSKUPhuKien;

        if (!productGroup.variants[sku]) {
            productGroup.variants[sku] = {
                soLuongBienThe: imei.soLuongBienThe,
                imeis: [],
            };
        }
        productGroup.variants[sku].imeis.push(imei);
    }
    for(const maSanPham in groups) {
        const product = groups[maSanPham];
        const uniqueVariants = new Set();
        let totalImeiCount = 0;
        for(const maSKU in product.variants) {
            uniqueVariants.add(maSKU);
            totalImeiCount += product.variants[maSKU].imeis.length;
        }
        
        product.totalSoLuong = Array.from(uniqueVariants).reduce((sum, sku) => {
            const variant = product.variants[sku];
            return sum + (variant ? variant.soLuongBienThe : 0);
        }, 0);
        
        product.totalImeiCount = totalImeiCount;
    }
    return groups;
});

const filteredData = computed(() => {
    page.value = 1;
    const q = searchQuery.value.toLowerCase();
    if (!q) {
        return groupedData.value;
    }
    const filteredGroups = {};
    for (const maSanPham in groupedData.value) {
        const product = groupedData.value[maSanPham];
        const productMatches = product.tenSanPham.toLowerCase().includes(q);
        const matchingVariants = {};
        for(const maSKU in product.variants) {
            const variant = product.variants[maSKU];
            const skuMatches = maSKU.toLowerCase().includes(q);
            const imeiMatches = variant.imeis.some(imei => imei.imei.toLowerCase().includes(q));
            if (productMatches || skuMatches || imeiMatches) {
                matchingVariants[maSKU] = variant;
            }
        }
        if (Object.keys(matchingVariants).length > 0) {
             filteredGroups[maSanPham] = { ...product, variants: matchingVariants };
        }
    }
    return filteredGroups;
});


const getFilteredImeisForVariant = (maSKU, returnCount = false) => {
    const variant = findVariantBySKU(maSKU);
    if (!variant) return returnCount ? 0 : [];
    const status = variantStatusFilters.value[maSKU] || '';
    const result = status === ''
        ? variant.imeis
        : variant.imeis.filter(imei => imei.trangThai === Number(status));
    return returnCount ? result.length : result;
};

const findVariantBySKU = (skuToFind) => {
    for (const sp in groupedData.value) {
        if (groupedData.value[sp].variants[skuToFind]) {
            return groupedData.value[sp].variants[skuToFind];
        }
    }
    return null;
}

const totalPages = computed(() => Math.ceil(Object.keys(filteredData.value).length / pageSize.value));
const paginatedData = computed(() => {
    const keys = Object.keys(filteredData.value);
    const start = (page.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    const paginatedKeys = keys.slice(start, end);
    const result = {};
    for (const key of paginatedKeys) {
        result[key] = filteredData.value[key];
    }
    return result;
});

const displayedPages = computed(() => {
    const total = totalPages.value;
    if (total <= 1) return [];
    const current = page.value;
    const delta = 1;
    const range = [];
    const rangeWithDots = [];
    let l;
    range.push(1);
    for (let i = current - delta; i <= current + delta; i++) {
        if (i >= 2 && i < total) range.push(i);
    }
    if (total > 1) range.push(total);
    for (const i of range) {
        if (l) {
            if (i - l === 2) rangeWithDots.push(l + 1);
            else if (i - l !== 1) rangeWithDots.push('...');
        }
        rangeWithDots.push(i);
        l = i;
    }
    return rangeWithDots;
});

const loadData = async () => {
  try {
    imeiList.value = await fetchAllImeis();
    const initialFilters = {};
    imeiList.value.forEach(imei => {
        const sku = imei.maSKU || imei.maSKUPhuKien;
        if(sku && !initialFilters[sku]) {
            initialFilters[sku] = '';
        }
    });
    variantStatusFilters.value = initialFilters;
  } catch (error) {
    console.error("Lỗi tải danh sách IMEI:", error);
  }
};

const loadVariantOptions = async () => {
    try {
        variantOptions.value = await fetchAllProductVariants();
    } catch(error) {
        console.error("Lỗi tải danh sách biến thể:", error);
    }
};

const openModal = (item = null) => {
  if (item && item.trangThai === 0) {
      alert("Không được phép sửa IMEI đã bán.");
      return;
  }
  if (item) {
    form.value = { ...item };
    form.value.maSKU = item.maSKU || item.maSKUPhuKien;
  } else {
    form.value = { ...initialFormState };
  }
  imeiModal.show();
};

const handleSubmit = async () => {
  try {
    const payload = { ...form.value };
    if (!payload.maSKU) {
        alert("Vui lòng chọn biến thể sản phẩm.");
        return;
    }
    if (form.value.id) {
      await updateImei(form.value.id, payload);
    } else {
      await createImei(payload);
    }
    imeiModal.hide();
    await loadData();
  } catch (error) {
    console.error("Lỗi khi lưu IMEI:", error);
    alert("Lỗi: " + (error.response?.data?.message || "Thao tác thất bại."));
  }
};

const handleDelete = async (item) => {
  if (item.trangThai === 0) {
      alert("Không được phép xóa IMEI đã bán.");
      return;
  }
  if (confirm(`Bạn chắc chắn muốn xóa IMEI [${item.imei}]?`)) {
    try {
      await deleteImei(item.id);
      await loadData();
    } catch (error) {
      console.error("Lỗi khi xóa IMEI:", error);
      alert("Không thể xóa IMEI.");
    }
  }
};

const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
    return new Date(dateTimeString).toLocaleString('vi-VN', options);
};

// ✅ SỬA GIAO DIỆN Ở ĐÂY: Thay đổi class màu sắc
const getStatusClass = (status) => status === 1 ? 'bg-success text-white' : 'bg-secondary text-white';
const getStatusText = (status) => status === 1 ? 'Trong kho' : 'Đã bán';
// ✅ SỬA GIAO DIỆN Ở ĐÂY: Thêm hàm lấy icon
const getStatusIcon = (status) => status === 1 ? 'bi-box-seam-fill' : 'bi-check-circle-fill';

onMounted(async () => {
  imeiModal = new Modal(document.getElementById('imeiModal'));
  await Promise.all([
      loadData(),
      loadVariantOptions()
  ]);
});
</script>