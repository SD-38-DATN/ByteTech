<template>
  <div>
    <div 
      v-if="toast.visible" 
      class="toast-notification"
      :class="toast.type === 'success' ? 'bg-success' : 'bg-danger'"
    >
      <div class="toast-content">
        <i :class="toast.type === 'success' ? 'bi bi-check-circle-fill' : 'bi bi-x-circle-fill'"></i>
        <span>{{ toast.message }}</span>
      </div>
      <div class="progress-bar" :style="{ width: toast.progress + '%' }"></div>
    </div>

    <div class="container-fluid py-4 px-md-4">
      <h2 class="mb-4">Quản lý Thông số Kỹ thuật</h2>
      <div class="row">
  
        <div class="col-lg-4">
          <div class="card shadow-sm">
            <div class="card-header">
              <h5 class="mb-0">Danh sách sản phẩm (Đang hoạt động)</h5>
            </div>
            <div class="card-body">
              <div class="mb-3">
                <input type="text" v-model="searchQuery" class="form-control" placeholder="Tìm kiếm sản phẩm...">
              </div>
              <div class="list-group" style="max-height: 600px; overflow-y: auto;">
                <div v-if="Object.keys(productGroups).length === 0" class="list-group-item">Không tìm thấy sản phẩm.</div>
                <div v-else v-for="(products, categoryName) in productGroups" :key="categoryName">
                  <div class="list-group-item list-group-item-secondary fw-bold text-uppercase mb-0 mt-2 d-flex justify-content-between align-items-center">
                    <span>{{ categoryName }}</span>
                    <button class="btn btn-sm btn-outline-dark py-0 px-2" @click="openLoaiThongSoManager(products[0].maDanhMuc, categoryName)">Quản lý loại</button>
                  </div>
                  <a v-for="product in products" :key="product.maSanPham" href="#" class="list-group-item list-group-item-action" :class="{ active: selectedProduct && selectedProduct.maSanPham === product.maSanPham }" @click.prevent="selectProduct(product)">
                    {{ product.tenSanPham }}
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <div class="col-lg-8">
          <div v-if="!selectedProduct" class="card shadow-sm">
            <div class="card-body text-center p-5">
              <i class="bi bi-box-seam" style="font-size: 4rem; color: #ccc;"></i>
              <p class="mt-3 text-muted">Vui lòng chọn một sản phẩm từ danh sách bên trái.</p>
            </div>
          </div>
          <div v-else>
            <div class="card shadow-sm">
              <div class="card-header d-flex justify-content-between align-items-center">
                <div><h5 class="mb-0">Thông số cho: <span class="text-primary">{{ selectedProduct.tenSanPham }}</span></h5></div>
                <button class="btn btn-primary btn-sm" @click="openAddModal"><i class="bi bi-plus-circle me-1"></i> Thêm thông số</button>
              </div>
              <div class="card-body">
                <div v-if="!groupedSpecs || Object.keys(groupedSpecs).length === 0" class="alert alert-secondary">Sản phẩm này chưa có thông số kỹ thuật.</div>
                <div v-else>
                  <div v-for="(specs, loai) in groupedSpecs" :key="loai" class="mb-4">
                    <h6 class="text-primary border-bottom pb-2 d-flex justify-content-between"><span>{{ loai }}</span></h6>
                    <table class="table table-sm table-borderless">
                      <tbody>
                        <tr v-for="spec in specs" :key="spec.maThongSo">
                          <td style="width: 30%;" class="fw-semibold">{{ spec.tenThongSo }}</td>
                          <td>{{ spec.giaTriThongSo }}</td>
                          <td style="width: 120px;"><span v-if="spec.trangThai === 1" class="badge bg-success">Hoạt động</span><span v-else class="badge bg-secondary">Ẩn</span></td>
                          <td class="text-end" style="width: 120px;">
                            <button class="btn btn-outline-secondary btn-sm me-2" @click="openEditModal(spec)"><i class="bi bi-pencil"></i></button>
                            <button class="btn btn-outline-danger btn-sm" @click="handleDelete(spec)"><i class="bi bi-trash"></i></button>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" id="thongSoModal" tabindex="-1" ref="thongSoModalRef">
        <div class="modal-dialog">
          <div class="modal-content">
            <form @submit.prevent="handleSubmit">
              <div class="modal-header"><h5 class="modal-title">{{ isEditing ? 'Cập nhật thông số' : 'Thêm thông số mới' }}</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
              <div class="modal-body">
                <div v-if="!isEditing" class="mb-3">
                  <label class="form-label">Loại thông số (*)</label>
                  <select v-model.number="form.loaiThongSoId" class="form-select" required>
                    <option disabled value="">-- Chọn loại thông số --</option>
                    <option v-for="loai in availableLoaiThongSo" :key="loai.loaiThongSoId" :value="loai.loaiThongSoId">{{ loai.tenLoaiThongSo }}</option>
                  </select>
                  <div v-if="availableLoaiThongSo.length === 0" class="form-text text-warning">Tất cả các loại thông số đã được thêm.</div>
                </div>
                <div v-if="isEditing" class="mb-3">
                  <label class="form-label">Loại thông số</label>
                  <input type="text" :value="form.tenLoaiThongSo" class="form-control" disabled />
                </div>
                <div class="mb-3"><label class="form-label">Tên thông số (*)</label><input v-model.trim="form.tenThongSo" type="text" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Giá trị thông số (*)</label><input v-model.trim="form.giaTriThongSo" type="text" class="form-control" required></div>
                <div v-if="isEditing" class="mb-3">
                    <label class="form-label">Trạng thái</label>
                    <select v-model.number="form.trangThai" class="form-select">
                        <option :value="1">Hoạt động</option>
                        <option :value="0">Ẩn</option>
                    </select>
                </div>
              </div>
              <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button><button type="submit" class="btn btn-primary">Lưu</button></div>
            </form>
          </div>
        </div>
    </div>
    
    <div class="modal fade" id="loaiThongSoManagerModal" tabindex="-1" ref="loaiThongSoManagerModalRef">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Quản lý Loại thông số cho Danh mục: <span class="text-primary">{{ currentCategoryName }}</span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form @submit.prevent="handleSaveLoaiThongSo" class="row g-3 align-items-end border p-3 rounded mb-4 bg-light">
                    <div class="col">
                        <label class="form-label fw-semibold">{{ isEditingLoaiThongSo ? 'Sửa tên loại:' : 'Thêm loại mới:' }}</label>
                        <input v-model="loaiThongSoForm.tenLoaiThongSo" type="text" class="form-control" required>
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-success">{{ isEditingLoaiThongSo ? 'Cập nhật' : 'Thêm mới' }}</button>
                        <button v-if="isEditingLoaiThongSo" type="button" class="btn btn-link" @click="cancelEditLoaiThongSo">Hủy</button>
                    </div>
                </form>
                <h6 class="mt-4">Danh sách hiện có</h6>
                <ul class="list-group">
                    <li v-if="currentLoaiThongSoList.length === 0" class="list-group-item">Chưa có loại thông số nào.</li>
                    <li v-for="lts in currentLoaiThongSoList" :key="lts.loaiThongSoId" class="list-group-item d-flex justify-content-between align-items-center">
                        {{ lts.tenLoaiThongSo }}
                        <div>
                            <button class="btn btn-sm btn-outline-primary me-2" @click="editLoaiThongSo(lts)">Sửa</button>
                            <button class="btn btn-sm btn-outline-danger" @click="handleDeleteLoaiThongSo(lts)">Xóa</button>
                        </div>
                    </li>
                </ul>
            </div>
          </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.toast-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  color: white;
  padding: 15px 20px;
  border-radius: 8px;
  z-index: 1060;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  width: 350px;
  max-width: 90%;
  overflow: hidden;
}
.toast-content {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1rem;
}
.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 5px;
  background-color: rgba(255, 255, 255, 0.5);
  transition: width 0.05s linear;
}
</style>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { Modal } from 'bootstrap';
import { 
  fetchAllAdminProducts, 
  fetchThongSoForSanPham, fetchAvailableLoaiThongSo, createThongSo, updateThongSo, deleteThongSo,
  fetchAllLoaiThongSo, createLoaiThongSo, updateLoaiThongSo, deleteLoaiThongSo
} from '@/service/api';

// --- State cho Thông báo ---
const toast = ref({ message: '', type: '', visible: false, progress: 100 });
let toastTimer = null;
let progressInterval = null;

function showToast(message, type = 'success', duration = 5000) {
  clearTimeout(toastTimer);
  clearInterval(progressInterval);

  toast.value = { message, type, visible: true, progress: 100 };
  
  const startTime = Date.now();
  
  progressInterval = setInterval(() => {
    const elapsedTime = Date.now() - startTime;
    toast.value.progress = 100 - (elapsedTime / duration) * 100;
    if (toast.value.progress <= 0) {
      clearInterval(progressInterval);
    }
  }, 50);

  toastTimer = setTimeout(() => {
    toast.value.visible = false;
  }, duration);
}

// --- State của component ---
const allProducts = ref([]);
const searchQuery = ref('');
const selectedProduct = ref(null);
const specList = ref([]);
const availableLoaiThongSo = ref([]);
const isEditing = ref(false);
const initialFormState = { maThongSo: null, tenThongSo: '', giaTriThongSo: '', loaiThongSoId: '', tenLoaiThongSo: '', trangThai: 1 };
const form = ref({ ...initialFormState });
const allLoaiThongSo = ref([]);
const currentCategoryName = ref('');
const currentCategoryId = ref(null);
const isEditingLoaiThongSo = ref(false);
const initialLoaiThongSoForm = { loaiThongSoId: null, tenLoaiThongSo: '', maDanhMuc: null };
const loaiThongSoForm = ref({ ...initialLoaiThongSoForm });

// --- Modal Instances ---
let thongSoModal = null;
const thongSoModalRef = ref(null);
let loaiThongSoManagerModal = null;
const loaiThongSoManagerModalRef = ref(null);

// --- Computed Properties ---
const productGroups = computed(() => {
  const filtered = allProducts.value
    .filter(product => product.trangThai === 1)
    .filter(product => product.tenSanPham.toLowerCase().includes(searchQuery.value.toLowerCase()));
  
  return filtered.reduce((groups, product) => {
    const categoryName = product.tenDanhMuc || 'Chưa phân loại';
    if (!groups[categoryName]) {
      groups[categoryName] = [];
    }
    groups[categoryName].push(product);
    return groups;
  }, {});
});

const groupedSpecs = computed(() => {
  if (!specList.value) return {};
  return specList.value.reduce((groups, spec) => {
    const loai = spec.tenLoaiThongSo;
    if (!groups[loai]) { groups[loai] = []; }
    groups[loai].push(spec);
    return groups;
  }, {});
});

const currentLoaiThongSoList = computed(() => {
    if (!currentCategoryId.value) return [];
    return allLoaiThongSo.value.filter(lts => lts.maDanhMuc === currentCategoryId.value);
});

// --- Methods ---
const loadAllProducts = async () => {
  try {
    allProducts.value = await fetchAllAdminProducts();
  } catch (error) {
    showToast("Không thể tải danh sách sản phẩm.", "error");
  }
};

const selectProduct = async (product) => {
  selectedProduct.value = product;
  specList.value = []; 
  await loadSpecData();
};

const loadSpecData = async () => {
  if (!selectedProduct.value) return;
  try {
    specList.value = await fetchThongSoForSanPham(selectedProduct.value.maSanPham);
  } catch (error) {
    showToast("Lỗi khi tải thông số kỹ thuật.", "error");
  }
};

const openAddModal = async () => {
    isEditing.value = false;
    form.value = { ...initialFormState };
    try {
        availableLoaiThongSo.value = await fetchAvailableLoaiThongSo(selectedProduct.value.maSanPham);
        thongSoModal.show();
    } catch (error) {
        showToast('Không thể tải loại thông số. Sản phẩm có thể chưa được gán Danh mục.', 'error');
    }
};

const openEditModal = (spec) => {
    isEditing.value = true;
    form.value = { ...spec, trangThai: spec.trangThai ?? 1 };
    thongSoModal.show();
};

const handleSubmit = async () => {
    try {
        if (isEditing.value) {
            await updateThongSo(form.value.maThongSo, form.value);
            showToast('Cập nhật thông số thành công!');
        } else {
            const payload = { ...form.value, trangThai: 1 };
            await createThongSo(selectedProduct.value.maSanPham, payload);
            showToast('Thêm thông số thành công!');
        }
        thongSoModal.hide();
        await loadSpecData();
    } catch (error) {
        showToast('Thao tác thất bại.', 'error');
    }
};

const handleDelete = async (spec) => {
    if (confirm(`Bạn có chắc muốn xóa thông số "${spec.tenThongSo}"?`)) {
        try {
            await deleteThongSo(spec.maThongSo);
            await loadSpecData();
            showToast('Xóa thông số thành công!');
        } catch (error) {
            showToast('Không thể xóa thông số.', 'error');
        }
    }
};

async function openLoaiThongSoManager(maDanhMuc, tenDanhMuc) {
    currentCategoryId.value = maDanhMuc;
    currentCategoryName.value = tenDanhMuc;
    cancelEditLoaiThongSo();
    await loadAllLoaiThongSo();
    loaiThongSoManagerModal.show();
}

async function loadAllLoaiThongSo() {
    try {
        allLoaiThongSo.value = await fetchAllLoaiThongSo();
    } catch(e) { 
        showToast("Lỗi tải danh sách loại thông số", "error");
    }
}

function editLoaiThongSo(item) {
    isEditingLoaiThongSo.value = true;
    loaiThongSoForm.value = { ...item };
}

function cancelEditLoaiThongSo() {
    isEditingLoaiThongSo.value = false;
    loaiThongSoForm.value = { ...initialLoaiThongSoForm, maDanhMuc: currentCategoryId.value };
}

async function handleSaveLoaiThongSo() {
    loaiThongSoForm.value.maDanhMuc = currentCategoryId.value;
    if (!loaiThongSoForm.value.tenLoaiThongSo.trim()) {
        showToast("Tên loại thông số không được để trống.", "error");
        return;
    }
    try {
        if (isEditingLoaiThongSo.value) {
            await updateLoaiThongSo(loaiThongSoForm.value.loaiThongSoId, loaiThongSoForm.value);
            showToast('Cập nhật loại thông số thành công!');
        } else {
            await createLoaiThongSo(loaiThongSoForm.value);
            showToast('Thêm loại thông số thành công!');
        }
        await loadAllLoaiThongSo();
        cancelEditLoaiThongSo();
    } catch (e) { showToast("Thao tác thất bại", 'error'); }
}

async function handleDeleteLoaiThongSo(item) {
    if (confirm(`Bạn chắc chắn muốn xóa loại "${item.tenLoaiThongSo}"?`)) {
        try {
            await deleteLoaiThongSo(item.loaiThongSoId);
            await loadAllLoaiThongSo();
            showToast('Xóa loại thông số thành công!');
        } catch (e) { showToast("Xóa thất bại. Có thể loại này đã được sử dụng.", 'error'); }
    }
}

// --- Lifecycle ---
onMounted(async () => {
  thongSoModal = new Modal(thongSoModalRef.value);
  loaiThongSoManagerModal = new Modal(loaiThongSoManagerModalRef.value);
  await loadAllProducts(); 
});
</script>