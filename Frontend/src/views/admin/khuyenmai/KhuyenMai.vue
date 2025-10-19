<!-- <script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { 
  fetchAllKhuyenMai, createKhuyenMai, updateKhuyenMai, deleteKhuyenMai,
  fetchAllProductVariants, fetchAppliedVariantsForKhuyenMai, applyKhuyenMaiToVariants
} from '@/service/api';

// === STATE ===
const khuyenMaiList = ref([]);
const allVariants = ref([]);
const loading = ref({ list: true, variants: false });
const isSaving = ref(false);

const isModalVisible = ref(false);
const isEditing = ref(false);
const initialFormState = {
  maKhuyenMai: null, tenKhuyenMai: '', moTa: '', loaiGiam: 1,
  giaTriGiam: 0.1, ngayBatDau: '', ngayKetThuc: '', trangThai: 1,
};
const currentKhuyenMaiInForm = ref({ ...initialFormState });

const selectedKhuyenMai = ref(null);
const appliedSkus = ref(new Set());
const dirtySkus = ref(false);

const notification = ref({ show: false, message: '', type: 'success' });

// === COMPUTED ===
const giaTriGiamProps = computed(() => {
  if (currentKhuyenMaiInForm.value.loaiGiam === 1) { // Giảm theo %
    return { min: 0.01, max: 1, step: 0.01, placeholder: 'Nhập từ 0.01 (1%) đến 1 (100%)' };
  }
  return { min: 0, step: 1000, placeholder: 'Nhập số tiền giảm' }; // Giảm theo tiền
});

// === API & DATA LOGIC ===
const loadInitialData = async () => {
  loading.value.list = true;
  try {
    const [khuyenMaiRes, variantsRes] = await Promise.all([
      fetchAllKhuyenMai(),
      fetchAllProductVariants(),
    ]);
    khuyenMaiList.value = khuyenMaiRes;
    allVariants.value = variantsRes;
    if (khuyenMaiList.value.length > 0) {
      await selectKhuyenMai(khuyenMaiList.value[0]);
    }
  } catch (error) {
    showNotification("Không thể tải dữ liệu từ server.", 'error');
  } finally {
    loading.value.list = false;
  }
};

onMounted(loadInitialData);

const selectKhuyenMai = async (km) => {
    if (dirtySkus.value && !confirm('Bạn có thay đổi chưa lưu. Bạn có muốn bỏ qua?')) {
        return;
    }
    selectedKhuyenMai.value = km;
    loading.value.variants = true;
    try {
        const skus = await fetchAppliedVariantsForKhuyenMai(km.maKhuyenMai);
        appliedSkus.value = new Set(skus);
        dirtySkus.value = false;
    } catch (error) {
        showNotification('Lỗi khi tải danh sách sản phẩm áp dụng.', 'error');
    } finally {
        loading.value.variants = false;
    }
};

// === FORM & MODAL LOGIC ===
const openAddModal = () => {
  isEditing.value = false;
  currentKhuyenMaiInForm.value = { ...initialFormState, ngayBatDau: formatDateForInput(new Date()) };
  isModalVisible.value = true;
};

const openEditModal = (km) => {
  isEditing.value = true;
  currentKhuyenMaiInForm.value = { 
    ...km,
    ngayBatDau: formatDateForInput(km.ngayBatDau),
    ngayKetThuc: formatDateForInput(km.ngayKetThuc),
  };
  isModalVisible.value = true;
};

const closeModal = () => isModalVisible.value = false;

// === CRUD OPERATIONS ===
const handleSave = async () => {
  const form = currentKhuyenMaiInForm.value;
  if (!form.tenKhuyenMai?.trim()) return showNotification("Tên khuyến mại không được để trống.", 'error');
  if (!form.ngayKetThuc) return showNotification("Ngày kết thúc không được để trống.", 'error');
  if (form.ngayKetThuc < form.ngayBatDau) return showNotification("Ngày kết thúc không được nhỏ hơn ngày bắt đầu.", 'error');
  
  const giaTriGiam = parseFloat(form.giaTriGiam);
  if (isNaN(giaTriGiam) || giaTriGiam < 0) return showNotification("Giá trị giảm không hợp lệ.", 'error');

  if (form.loaiGiam === 1 && (giaTriGiam <= 0 || giaTriGiam > 1)) {
    return showNotification("Giá trị giảm (%) phải là số thập phân trong khoảng (0, 1].", 'error');
  }

  isSaving.value = true;
  const payload = { ...form, giaTriGiam,
    ngayBatDau: `${form.ngayBatDau}T00:00:00`,
    ngayKetThuc: `${form.ngayKetThuc}T23:59:59`,
  };
    
  try {
    if (isEditing.value) {
      await updateKhuyenMai(payload.maKhuyenMai, payload);
    } else {
      await createKhuyenMai(payload);
    }
    showNotification(isEditing.value ? "Cập nhật thành công!" : "Thêm mới thành công!");
    closeModal();
    loadInitialData();
  } catch (error) {
    showNotification(error.response?.data?.message || "Đã có lỗi xảy ra.", 'error');
  } finally {
    isSaving.value = false;
  }
};

const handleApplyChanges = async () => {
    if (!selectedKhuyenMai.value) return;
    isSaving.value = true;
    try {
        const skuList = Array.from(appliedSkus.value);
        await applyKhuyenMaiToVariants(selectedKhuyenMai.value.maKhuyenMai, skuList);
        showNotification("Cập nhật danh sách sản phẩm thành công!");
        dirtySkus.value = false;
    } catch (error) {
        showNotification("Có lỗi xảy ra khi cập nhật.", 'error');
    } finally {
        isSaving.value = false;
    }
};

const handleDelete = async (km) => {
  if (confirm(`Bạn có chắc chắn muốn xóa khuyến mại "${km.tenKhuyenMai}" không?`)) {
    try {
      await deleteKhuyenMai(km.maKhuyenMai);
      showNotification("Xóa khuyến mại thành công!");
      await loadInitialData();
    } catch (error) {
      showNotification("Không thể xóa khuyến mại.", 'error');
    }
  }
};

// === UTILS ===
const showNotification = (message, type = 'success') => {
  notification.value = { show: true, message, type };
  setTimeout(() => { notification.value.show = false; }, 3000);
};
const formatDate = (d) => d ? new Intl.DateTimeFormat('vi-VN').format(new Date(d)) : '';
const formatDateForInput = (d) => d ? new Date(d).toISOString().split('T')[0] : '';
const formatValue = (v, t) => {
    if (t === 1) return `${(v * 100).toFixed(0)}%`;
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
};
</script>

<template>
  <transition name="fade">
    <div v-if="notification.show" :class="['notification', notification.type]">
      {{ notification.message }}
    </div>
  </transition>

  <div class="page-container">
    <div class="panel promotions-panel">
      <header class="panel-header">
        <h2>Danh sách khuyến mại</h2>
        <button @click="openAddModal" class="btn btn-primary btn-sm">
          <i class="fas fa-plus"></i> Thêm
        </button>
      </header>
      <div v-if="loading.list" class="loading-overlay">Đang tải...</div>
      <ul v-else class="item-list">
        <li v-for="km in khuyenMaiList" :key="km.maKhuyenMai" 
            @click="selectKhuyenMai(km)"
            :class="{ active: selectedKhuyenMai?.maKhuyenMai === km.maKhuyenMai }">
          
          <div class="card-content">
            <div class="card-header">
                <span class="item-title">{{ km.tenKhuyenMai }}</span>
                <span :class="['status-badge', km.trangThai === 1 ? 'status-active' : 'status-inactive']">
                    {{ km.trangThai === 1 ? 'Hoạt động' : 'Tạm dừng' }}
                </span>
            </div>
            <p class="card-description">{{ km.moTa }}</p>
            <div class="card-details">
                <div class="detail-item">
                    <strong>Giá trị:</strong>
                    <span>{{ formatValue(km.giaTriGiam, km.loaiGiam) }} ({{ km.loaiGiam === 1 ? 'Phần trăm' : 'Tiền' }})</span>
                </div>
                <div class="detail-item">
                    <strong>Hiệu lực:</strong>
                    <span>{{ formatDate(km.ngayBatDau) }} - {{ formatDate(km.ngayKetThuc) }}</span>
                </div>
            </div>
          </div>
          <div class="card-actions">
            <button @click.stop="openEditModal(km)" class="btn-icon" title="Sửa"><i class="fas fa-edit"></i></button>
            <button @click.stop="handleDelete(km)" class="btn-icon danger" title="Xóa"><i class="fas fa-trash-alt"></i></button>
          </div>
        </li>
      </ul>
    </div>

    <div class="panel variants-panel">
      <header class="panel-header">
        <h2>
          Sản phẩm áp dụng cho: 
          <strong v-if="selectedKhuyenMai">{{ selectedKhuyenMai.tenKhuyenMai }}</strong>
        </h2>
        <button v-if="dirtySkus" @click="handleApplyChanges" class="btn btn-success btn-sm" :disabled="isSaving">
            <i v-if="isSaving" class="fas fa-spinner fa-spin"></i>
            Lưu thay đổi
        </button>
      </header>
      <div v-if="loading.variants" class="loading-overlay">Đang tải...</div>
      <div v-else-if="!selectedKhuyenMai" class="placeholder">Chọn một khuyến mại để xem sản phẩm.</div>
      <div v-else class="variant-list">
        <div v-for="variant in allVariants" :key="variant.maSKU" class="variant-item">
          <input type="checkbox" :id="variant.maSKU" 
                 :checked="appliedSkus.has(variant.maSKU)"
                 @change="appliedSkus.has(variant.maSKU) ? appliedSkus.delete(variant.maSKU) : appliedSkus.add(variant.maSKU); dirtySkus = true;">
          <label :for="variant.maSKU">{{ variant.tenHienThi }}</label>
        </div>
      </div>
    </div>
  </div>

  <transition name="modal-fade">
    <div v-if="isModalVisible" class="modal-overlay" @click.self="closeModal">
        <div class="modal-content">
        <h2 class="modal-title">{{ isEditing ? 'Chỉnh Sửa Khuyến Mại' : 'Thêm Khuyến Mại Mới' }}</h2>
        <form @submit.prevent="handleSave">
            <div class="form-group">
            <label for="tenKM">Tên khuyến mại</label>
            <input id="tenKM" type="text" v-model="currentKhuyenMaiInForm.tenKhuyenMai" required />
            </div>
            <div class="form-group">
            <label for="moTaKM">Mô tả</label>
            <textarea id="moTaKM" v-model="currentKhuyenMaiInForm.moTa" rows="2"></textarea>
            </div>
            <div class="form-row">
            <div class="form-group half-width">
                <label for="loaiGiam">Loại giảm</label>
                <select id="loaiGiam" v-model.number="currentKhuyenMaiInForm.loaiGiam">
                <option :value="1">Giảm theo %</option>
                <option :value="0">Giảm tiền (VND)</option>
                </select>
            </div>
            <div class="form-group half-width">
                <label for="giaTriGiam">Giá trị giảm</label>
                <input id="giaTriGiam" type="number" 
                       :min="giaTriGiamProps.min" :max="giaTriGiamProps.max" :step="giaTriGiamProps.step"
                       :placeholder="giaTriGiamProps.placeholder"
                       v-model.number="currentKhuyenMaiInForm.giaTriGiam" required />
            </div>
            </div>
            <div class="form-row">
            <div class="form-group half-width">
                <label for="ngayBatDau">Ngày bắt đầu</label>
                <input id="ngayBatDau" type="date" v-model="currentKhuyenMaiInForm.ngayBatDau" required />
            </div>
            <div class="form-group half-width">
                <label for="ngayKetThuc">Ngày kết thúc</label>
                <input id="ngayKetThuc" type="date" v-model="currentKhuyenMaiInForm.ngayKetThuc" required />
            </div>
            </div>
            <div class="form-actions">
            <button type="button" @click="closeModal" class="btn btn-secondary">Hủy</button>
            <button type="submit" class="btn btn-success" :disabled="isSaving">
                <i v-if="isSaving" class="fas fa-spinner fa-spin"></i>
                {{ isSaving ? 'Đang lưu...' : 'Lưu Lại' }}
            </button>
            </div>
        </form>
        </div>
    </div>
  </transition>
</template>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');

.page-container {
  display: flex; height: calc(100vh - 4rem); gap: 1.5rem;
  padding: 1.5rem; font-family: system-ui, -apple-system, sans-serif;
  background-color: #f8f9fa;
}
.panel {
  display: flex; flex-direction: column; background: #fff;
  border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.07);
  overflow: hidden; border: 1px solid #dee2e6;
}
/* === THAY ĐỔI DUY NHẤT TẠI ĐÂY === */
.promotions-panel { flex: 3; min-width: 400px; }
.variants-panel { flex: 2; }

.panel-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 1rem 1.5rem; border-bottom: 1px solid #dee2e6;
  flex-shrink: 0; background-color: #fff;
}
.panel-header h2 { margin: 0; font-size: 1.15rem; color: #212529; font-weight: 600; }
.panel-header h2 strong { color: #0d6efd; }
.loading-overlay, .placeholder {
  display: flex; justify-content: center; align-items: center;
  height: 100%; color: #6c757d; font-size: 1rem;
}

.item-list { list-style: none; padding: 0.5rem; margin: 0; overflow-y: auto; }
.item-list li {
  display: flex; align-items: stretch; border: 1px solid transparent;
  border-radius: 8px; margin-bottom: 0.5rem; cursor: pointer;
  transition: all 0.2s ease; background-color: #fff;
}
.item-list li:hover { border-color: #a6c9ff; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.item-list li.active { border-color: #0d6efd; background-color: #f0f6ff; }

.card-content { flex-grow: 1; padding: 1rem; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
.item-title { font-weight: 600; color: #212529; font-size: 1rem; }
.card-description { font-size: 0.85rem; color: #6c757d; margin: 0 0 0.75rem 0;
  display: -webkit-box;   -webkit-box-orient: vertical; overflow: hidden;
}
.card-details { font-size: 0.8rem; color: #495057; display: flex; flex-direction: column; gap: 0.25rem; }
.detail-item { display: flex; justify-content: space-between; }
.card-actions {
  display: flex; flex-direction: column; justify-content: center;
  gap: 0.5rem; padding: 1rem; border-left: 1px solid #f0f0f0;
}

.btn { padding: 0.5rem 1rem; border: none; cursor: pointer; border-radius: 5px; color: white; font-size: 0.9rem; display: inline-flex; align-items: center; justify-content: center; gap: 0.5rem; transition: all 0.2s ease-in-out; }
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-primary { background-color: #0d6efd; } .btn-warning { background-color: #ffc107; color: #212529; }
.btn-danger { background-color: #dc3545; } .btn-success { background-color: #198754; }
.btn-secondary { background-color: #6c757d; } .btn-info { background-color: #0dcaf0; }
.btn-sm { padding: 0.4rem 0.8rem; font-size: 0.8rem; }
.btn-icon { background: none; border: none; color: #6c757d; cursor: pointer; padding: 0.5rem; border-radius: 50%; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; }
.btn-icon:hover { background-color: #e9ecef; }
.btn-icon.danger:hover { color: #dc3545; }

.status-badge { padding: 0.25rem 0.6rem; border-radius: 50px; font-size: 0.7rem; font-weight: 700; text-transform: uppercase; }
.status-active { background-color: #d1e7dd; color: #0f5132; }
.status-inactive { background-color: #e2e3e5; color: #41464b; }

.variant-list { overflow-y: auto; padding: 0.5rem 1.5rem; }
.variant-item { display: flex; align-items: center; padding: 0.75rem 0.5rem; border-bottom: 1px solid #f0f0f0; }
.variant-item:last-child { border-bottom: none; }
.variant-item input { margin-right: 1rem; width: 1.1em; height: 1.1em; accent-color: #0d6efd; }
.variant-item label { cursor: pointer; flex: 1; user-select: none; }

.modal-overlay { position: fixed; inset: 0; background-color: rgba(0,0,0,0.6); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background-color: #fff; padding: 2rem; border-radius: 8px; width: 90%; max-width: 500px; box-shadow: 0 5px 15px rgba(0,0,0,0.3); }
.modal-title { margin-top: 0; margin-bottom: 1.5rem; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; font-weight: 500; color: #495057; }
.form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.75rem; box-sizing: border-box; border: 1px solid #dee2e6; border-radius: 5px; }
.form-row { display: flex; gap: 1rem; }
.half-width { flex: 1; }
.form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 2rem; padding-top: 1rem; border-top: 1px solid #dee2e6; }

.notification { position: fixed; bottom: 20px; right: 20px; padding: 1rem 1.5rem; border-radius: 5px; color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.15); z-index: 2000; }
.notification.success { background-color: #198754; }
.notification.error { background-color: #dc3545; }
.fade-enter-active, .fade-leave-active { transition: all 0.5s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(20px); }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
</style> -->


<script setup>
import { ref, onMounted, computed, watch } from 'vue';
// Đảm bảo bạn import các hàm từ file api.js đã cấu hình interceptor
import { 
  fetchAllKhuyenMai, createKhuyenMai, updateKhuyenMai, deleteKhuyenMai,
  fetchAllProductVariants, fetchAppliedVariantsForKhuyenMai, applyKhuyenMaiToVariants
} from '@/service/api'; // Đường dẫn tới file api.js

// === STATE ===
const khuyenMaiList = ref([]);
const allVariants = ref([]);
const loading = ref({ list: true, variants: false });
const isSaving = ref(false);

const isModalVisible = ref(false);
const isEditing = ref(false);
const initialFormState = {
  maKhuyenMai: null, tenKhuyenMai: '', moTa: '', loaiGiam: 1,
  giaTriGiam: 0.1, ngayBatDau: '', ngayKetThuc: '', trangThai: 1,
};
const currentKhuyenMaiInForm = ref({ ...initialFormState });

const selectedKhuyenMai = ref(null);
const appliedSkus = ref(new Set());
const dirtySkus = ref(false);

const notification = ref({ show: false, message: '', type: 'success' });

// === COMPUTED ===
const giaTriGiamProps = computed(() => {
  if (currentKhuyenMaiInForm.value.loaiGiam === 1) { // Giảm theo %
    return { min: 0.01, max: 1, step: 0.01, placeholder: 'Nhập từ 0.01 (1%) đến 1 (100%)' };
  }
  return { min: 0, step: 1000, placeholder: 'Nhập số tiền giảm' }; // Giảm theo tiền
});

// === API & DATA LOGIC ===
const loadInitialData = async () => {
  loading.value.list = true;
  try {
    const [khuyenMaiRes, variantsRes] = await Promise.all([
      fetchAllKhuyenMai(),
      fetchAllProductVariants(),
    ]);
    khuyenMaiList.value = khuyenMaiRes;
    allVariants.value = variantsRes;
    if (khuyenMaiList.value.length > 0) {
      await selectKhuyenMai(khuyenMaiList.value[0]);
    }
  } catch (error) {
    showNotification("Không thể tải dữ liệu từ server. Vui lòng kiểm tra lại đăng nhập.", 'error');
    console.error("Lỗi tải dữ liệu:", error);
  } finally {
    loading.value.list = false;
  }
};

onMounted(loadInitialData);

const selectKhuyenMai = async (km) => {
    if (dirtySkus.value && !confirm('Bạn có thay đổi chưa lưu. Bạn có muốn bỏ qua?')) {
        return;
    }
    selectedKhuyenMai.value = km;
    loading.value.variants = true;
    try {
        const skus = await fetchAppliedVariantsForKhuyenMai(km.maKhuyenMai);
        appliedSkus.value = new Set(skus);
        dirtySkus.value = false;
    } catch (error) {
        showNotification('Lỗi khi tải danh sách sản phẩm áp dụng.', 'error');
    } finally {
        loading.value.variants = false;
    }
};

// === FORM & MODAL LOGIC ===
const openAddModal = () => {
  isEditing.value = false;
  currentKhuyenMaiInForm.value = { ...initialFormState, ngayBatDau: formatDateForInput(new Date()) };
  isModalVisible.value = true;
};

const openEditModal = (km) => {
  isEditing.value = true;
  currentKhuyenMaiInForm.value = { 
    ...km,
    ngayBatDau: formatDateForInput(km.ngayBatDau),
    ngayKetThuc: formatDateForInput(km.ngayKetThuc),
  };
  isModalVisible.value = true;
};

const closeModal = () => isModalVisible.value = false;

// === CRUD OPERATIONS ===
const handleSave = async () => {
  const form = currentKhuyenMaiInForm.value;
  if (!form.tenKhuyenMai?.trim()) return showNotification("Tên khuyến mại không được để trống.", 'error');
  if (!form.ngayKetThuc) return showNotification("Ngày kết thúc không được để trống.", 'error');
  if (form.ngayKetThuc < form.ngayBatDau) return showNotification("Ngày kết thúc không được nhỏ hơn ngày bắt đầu.", 'error');
  
  const giaTriGiam = parseFloat(form.giaTriGiam);
  if (isNaN(giaTriGiam) || giaTriGiam < 0) return showNotification("Giá trị giảm không hợp lệ.", 'error');

  if (form.loaiGiam === 1 && (giaTriGiam <= 0 || giaTriGiam > 1)) {
    return showNotification("Giá trị giảm (%) phải là số thập phân trong khoảng (0, 1].", 'error');
  }

  isSaving.value = true;
  const payload = { ...form, giaTriGiam,
    ngayBatDau: `${form.ngayBatDau}T00:00:00`,
    ngayKetThuc: `${form.ngayKetThuc}T23:59:59`,
  };
    
  try {
    if (isEditing.value) {
      await updateKhuyenMai(payload.maKhuyenMai, payload);
    } else {
      await createKhuyenMai(payload);
    }
    showNotification(isEditing.value ? "Cập nhật thành công!" : "Thêm mới thành công!");
    closeModal();
    loadInitialData();
  } catch (error) {
    showNotification(error.response?.data?.message || "Đã có lỗi xảy ra.", 'error');
  } finally {
    isSaving.value = false;
  }
};

const handleApplyChanges = async () => {
    if (!selectedKhuyenMai.value) return;
    isSaving.value = true;
    try {
        const skuList = Array.from(appliedSkus.value);
        await applyKhuyenMaiToVariants(selectedKhuyenMai.value.maKhuyenMai, skuList);
        showNotification("Cập nhật danh sách sản phẩm thành công!");
        dirtySkus.value = false;
    } catch (error) {
        showNotification("Có lỗi xảy ra khi cập nhật.", 'error');
    } finally {
        isSaving.value = false;
    }
};

const handleDelete = async (km) => {
  if (confirm(`Bạn có chắc chắn muốn xóa khuyến mại "${km.tenKhuyenMai}" không?`)) {
    try {
      await deleteKhuyenMai(km.maKhuyenMai);
      showNotification("Xóa khuyến mại thành công!");
      await loadInitialData();
    } catch (error) {
      showNotification("Không thể xóa khuyến mại.", 'error');
    }
  }
};

// === UTILS ===
const showNotification = (message, type = 'success') => {
  notification.value = { show: true, message, type };
  setTimeout(() => { notification.value.show = false; }, 3000);
};
const formatDate = (d) => d ? new Intl.DateTimeFormat('vi-VN').format(new Date(d)) : '';
const formatDateForInput = (d) => d ? new Date(d).toISOString().split('T')[0] : '';
const formatValue = (v, t) => {
    if (t === 1) return `${(v * 100).toFixed(0)}%`;
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(v);
};
</script>

<template>
  <transition name="fade">
    <div v-if="notification.show" :class="['notification', notification.type]">
      {{ notification.message }}
    </div>
  </transition>

  <div class="page-container">
    <div class="panel promotions-panel">
      <header class="panel-header">
        <h2>Danh sách khuyến mại</h2>
        <button @click="openAddModal" class="btn btn-primary btn-sm">
          <i class="fas fa-plus"></i> Thêm
        </button>
      </header>
      <div v-if="loading.list" class="loading-overlay">Đang tải...</div>
      <ul v-else class="item-list">
        <li v-for="km in khuyenMaiList" :key="km.maKhuyenMai" 
            @click="selectKhuyenMai(km)"
            :class="{ active: selectedKhuyenMai?.maKhuyenMai === km.maKhuyenMai }">
          
          <div class="card-content">
            <div class="card-header">
                <span class="item-title">{{ km.tenKhuyenMai }}</span>
                <span :class="['status-badge', km.trangThai === 1 ? 'status-active' : 'status-inactive']">
                    {{ km.trangThai === 1 ? 'Hoạt động' : 'Tạm dừng' }}
                </span>
            </div>
            <p class="card-description">{{ km.moTa }}</p>
            <div class="card-details">
                <div class="detail-item">
                    <strong>Giá trị:</strong>
                    <span>{{ formatValue(km.giaTriGiam, km.loaiGiam) }} ({{ km.loaiGiam === 1 ? 'Phần trăm' : 'Tiền' }})</span>
                </div>
                <div class="detail-item">
                    <strong>Hiệu lực:</strong>
                    <span>{{ formatDate(km.ngayBatDau) }} - {{ formatDate(km.ngayKetThuc) }}</span>
                </div>
            </div>
          </div>
          <div class="card-actions">
            <button @click.stop="openEditModal(km)" class="btn-icon" title="Sửa"><i class="fas fa-edit"></i></button>
            <button @click.stop="handleDelete(km)" class="btn-icon danger" title="Xóa"><i class="fas fa-trash-alt"></i></button>
          </div>
        </li>
      </ul>
    </div>

    <div class="panel variants-panel">
      <header class="panel-header">
        <h2>
          Sản phẩm áp dụng cho: 
          <strong v-if="selectedKhuyenMai">{{ selectedKhuyenMai.tenKhuyenMai }}</strong>
        </h2>
        <button v-if="dirtySkus" @click="handleApplyChanges" class="btn btn-success btn-sm" :disabled="isSaving">
            <i v-if="isSaving" class="fas fa-spinner fa-spin"></i>
            Lưu thay đổi
        </button>
      </header>
      <div v-if="loading.variants" class="loading-overlay">Đang tải...</div>
      <div v-else-if="!selectedKhuyenMai" class="placeholder">Chọn một khuyến mại để xem sản phẩm.</div>
      <div v-else class="variant-list">
        <div v-for="variant in allVariants" :key="variant.maSKU" class="variant-item">
          <input type="checkbox" :id="variant.maSKU" 
                 :checked="appliedSkus.has(variant.maSKU)"
                 @change="appliedSkus.has(variant.maSKU) ? appliedSkus.delete(variant.maSKU) : appliedSkus.add(variant.maSKU); dirtySkus = true;">
          <label :for="variant.maSKU">{{ variant.tenHienThi }}</label>
        </div>
      </div>
    </div>
  </div>

  <transition name="modal-fade">
    <div v-if="isModalVisible" class="modal-overlay" @click.self="closeModal">
        <div class="modal-content">
        <h2 class="modal-title">{{ isEditing ? 'Chỉnh Sửa Khuyến Mại' : 'Thêm Khuyến Mại Mới' }}</h2>
        <form @submit.prevent="handleSave">
            <div class="form-group">
            <label for="tenKM">Tên khuyến mại</label>
            <input id="tenKM" type="text" v-model="currentKhuyenMaiInForm.tenKhuyenMai" required />
            </div>
            <div class="form-group">
            <label for="moTaKM">Mô tả</label>
            <textarea id="moTaKM" v-model="currentKhuyenMaiInForm.moTa" rows="2"></textarea>
            </div>
            <div class="form-row">
            <div class="form-group half-width">
                <label for="loaiGiam">Loại giảm</label>
                <select id="loaiGiam" v-model.number="currentKhuyenMaiInForm.loaiGiam">
                <option :value="1">Giảm theo %</option>
                <option :value="0">Giảm tiền (VND)</option>
                </select>
            </div>
            <div class="form-group half-width">
                <label for="giaTriGiam">Giá trị giảm</label>
                <input id="giaTriGiam" type="number" 
                       :min="giaTriGiamProps.min" :max="giaTriGiamProps.max" :step="giaTriGiamProps.step"
                       :placeholder="giaTriGiamProps.placeholder"
                       v-model.number="currentKhuyenMaiInForm.giaTriGiam" required />
            </div>
            </div>
            <div class="form-row">
            <div class="form-group half-width">
                <label for="ngayBatDau">Ngày bắt đầu</label>
                <input id="ngayBatDau" type="date" v-model="currentKhuyenMaiInForm.ngayBatDau" required />
            </div>
            <div class="form-group half-width">
                <label for="ngayKetThuc">Ngày kết thúc</label>
                <input id="ngayKetThuc" type="date" v-model="currentKhuyenMaiInForm.ngayKetThuc" required />
            </div>
            </div>
            <div class="form-actions">
            <button type="button" @click="closeModal" class="btn btn-secondary">Hủy</button>
            <button type="submit" class="btn btn-success" :disabled="isSaving">
                <i v-if="isSaving" class="fas fa-spinner fa-spin"></i>
                {{ isSaving ? 'Đang lưu...' : 'Lưu Lại' }}
            </button>
            </div>
        </form>
        </div>
    </div>
  </transition>
</template>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');

.page-container {
  display: flex; height: calc(100vh - 4rem); gap: 1.5rem;
  padding: 1.5rem; font-family: system-ui, -apple-system, sans-serif;
  background-color: #f8f9fa;
}
.panel {
  display: flex; flex-direction: column; background: #fff;
  border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.07);
  overflow: hidden; border: 1px solid #dee2e6;
}
.promotions-panel { flex: 3; min-width: 400px; }
.variants-panel { flex: 2; }

.panel-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 1rem 1.5rem; border-bottom: 1px solid #dee2e6;
  flex-shrink: 0; background-color: #fff;
}
.panel-header h2 { margin: 0; font-size: 1.15rem; color: #212529; font-weight: 600; }
.panel-header h2 strong { color: #0d6efd; }
.loading-overlay, .placeholder {
  display: flex; justify-content: center; align-items: center;
  height: 100%; color: #6c757d; font-size: 1rem;
}

.item-list { list-style: none; padding: 0.5rem; margin: 0; overflow-y: auto; }
.item-list li {
  display: flex; align-items: stretch; border: 1px solid transparent;
  border-radius: 8px; margin-bottom: 0.5rem; cursor: pointer;
  transition: all 0.2s ease; background-color: #fff;
}
.item-list li:hover { border-color: #a6c9ff; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.item-list li.active { border-color: #0d6efd; background-color: #f0f6ff; }

.card-content { flex-grow: 1; padding: 1rem; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
.item-title { font-weight: 600; color: #212529; font-size: 1rem; }
.card-description { font-size: 0.85rem; color: #6c757d; margin: 0 0 0.75rem 0;
  display: -webkit-box;   -webkit-box-orient: vertical; overflow: hidden;
}
.card-details { font-size: 0.8rem; color: #495057; display: flex; flex-direction: column; gap: 0.25rem; }
.detail-item { display: flex; justify-content: space-between; }
.card-actions {
  display: flex; flex-direction: column; justify-content: center;
  gap: 0.5rem; padding: 1rem; border-left: 1px solid #f0f0f0;
}

.btn { padding: 0.5rem 1rem; border: none; cursor: pointer; border-radius: 5px; color: white; font-size: 0.9rem; display: inline-flex; align-items: center; justify-content: center; gap: 0.5rem; transition: all 0.2s ease-in-out; }
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-primary { background-color: #0d6efd; } .btn-warning { background-color: #ffc107; color: #212529; }
.btn-danger { background-color: #dc3545; } .btn-success { background-color: #198754; }
.btn-secondary { background-color: #6c757d; } .btn-info { background-color: #0dcaf0; }
.btn-sm { padding: 0.4rem 0.8rem; font-size: 0.8rem; }
.btn-icon { background: none; border: none; color: #6c757d; cursor: pointer; padding: 0.5rem; border-radius: 50%; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; }
.btn-icon:hover { background-color: #e9ecef; }
.btn-icon.danger:hover { color: #dc3545; }

.status-badge { padding: 0.25rem 0.6rem; border-radius: 50px; font-size: 0.7rem; font-weight: 700; text-transform: uppercase; }
.status-active { background-color: #d1e7dd; color: #0f5132; }
.status-inactive { background-color: #e2e3e5; color: #41464b; }

.variant-list { overflow-y: auto; padding: 0.5rem 1.5rem; }
.variant-item { display: flex; align-items: center; padding: 0.75rem 0.5rem; border-bottom: 1px solid #f0f0f0; }
.variant-item:last-child { border-bottom: none; }
.variant-item input { margin-right: 1rem; width: 1.1em; height: 1.1em; accent-color: #0d6efd; }
.variant-item label { cursor: pointer; flex: 1; user-select: none; }

.modal-overlay { position: fixed; inset: 0; background-color: rgba(0,0,0,0.6); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background-color: #fff; padding: 2rem; border-radius: 8px; width: 90%; max-width: 500px; box-shadow: 0 5px 15px rgba(0,0,0,0.3); }
.modal-title { margin-top: 0; margin-bottom: 1.5rem; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; font-weight: 500; color: #495057; }
.form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.75rem; box-sizing: border-box; border: 1px solid #dee2e6; border-radius: 5px; }
.form-row { display: flex; gap: 1rem; }
.half-width { flex: 1; }
.form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 2rem; padding-top: 1rem; border-top: 1px solid #dee2e6; }

.notification { position: fixed; bottom: 20px; right: 20px; padding: 1rem 1.5rem; border-radius: 5px; color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.15); z-index: 2000; }
.notification.success { background-color: #198754; }
.notification.error { background-color: #dc3545; }
.fade-enter-active, .fade-leave-active { transition: all 0.5s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(20px); }
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
</style>