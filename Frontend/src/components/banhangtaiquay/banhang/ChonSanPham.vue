<template>
  <div class="modal-overlay">
    <div class="modal-container" @click.stop>
      <!-- Header -->
      <div class="modal-header">
        <h3>🛍️ Chọn sản phẩm</h3>
        <button class="btn-close" @click="closeModal">✕</button>
      </div>

      <!-- Search Section -->
      <div class="search-section">
        <div class="search-row">
          <div class="search-field">
            <label class="field-label">🔍 Tìm kiếm SKU:</label>
            <input
              v-model="searchSKU"
              @input="performSearch"
              placeholder="Nhập mã SKU..."
              class="input-field"
            />
          </div>
          <div class="search-field">
            <label class="field-label">📱 Tìm kiếm IMEI:</label>
            <input
              v-model="searchIMEI"
              @input="performSearch"
              placeholder="Nhập IMEI..."
              class="input-field"
            />
          </div>
        </div>
      </div>

      <!-- Products Table -->
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th width="6%">STT</th>
              <th width="12%">MÃ SKU</th>
              <th width="20%">TÊN SẢN PHẨM</th>
              <th width="10%">GIÁ</th>
              <th width="15%">THUỘC TÍNH</th>
              <th width="8%">LOẠI</th>
              <th width="7%">SỐ LƯỢNG</th>
              <th width="7%">SỐ IMEI</th>
            </tr>
          </thead>
          <tbody>
            <template
              v-for="(sp, index) in filteredProducts"
              :key="sp.maSKU || sp.maSKUPhuKien || index"
            >
              <!-- Dòng sản phẩm chính -->
              <tr
                @click="toggleIMEI(sp.maSKU || sp.maSKUPhuKien)"
                class="product-row"
                :class="{
                  expanded: expandedSKU === (sp.maSKU || sp.maSKUPhuKien),
                }"
              >
                <td>{{ index + 1 }}</td>
                <td>
                  <span class="sku-badge">{{
                    sp.maSKU || sp.maSKUPhuKien || "N/A"
                  }}</span>
                </td>
                <td class="text-left">
                  {{ sp.tenSanPham || sp.tenPhuKien || "N/A" }}
                </td>
                <td class="text-right">
                  {{ formatCurrency(sp.sanPham?.gia || sp.gia || 0) }}
                </td>
                <td>{{ sp.sanPham?.thuocTinh || sp.thuocTinh || "N/A" }}</td>
                <td class="loai-cell">
                  <span
                    class="loai-text"
                    :class="{
                      'loai-san-pham': sp.maSKU && !sp.maSKUPhuKien,
                      'loai-phu-kien': sp.maSKUPhuKien && !sp.maSKU,
                    }"
                  >
                    {{ sp.maSKUPhuKien ? "Phụ kiện" : "Sản phẩm chính" }}
                  </span>
                </td>
                <td>
                  <span class="qty-badge">{{ sp.soLuong || 0 }}</span>
                </td>
                <td>
                  <span class="imei-count-badge">{{ getImeiCount(sp) }}</span>
                </td>
              </tr>

              <!-- Dòng hiển thị danh sách IMEI (expand) -->
              <tr
                v-if="expandedSKU === (sp.maSKU || sp.maSKUPhuKien)"
                class="imei-row"
              >
                <td colspan="8" class="imei-container">
                  <div v-if="loadingIMEI" class="loading">
                    <span>⏳ Đang tải danh sách IMEI...</span>
                  </div>
                  <div v-else-if="imeiList.length === 0" class="empty-imei">
                    <span>📦 Không có IMEI nào</span>
                  </div>
                  <div v-else class="imei-list">
                    <div class="imei-header">
                      <h4>
                        Danh sách IMEI -
                        {{ sp.tenSanPham || sp.tenPhuKien || "N/A" }}
                      </h4>
                      <span class="selected-count"
                        >Đã chọn: {{ selectedIMEIs.length }}</span
                      >
                    </div>
                    <div class="deselect-row">
                      <button class="btn-deselect" @click.stop="boChonTatCa">
                        Bỏ chọn tất cả
                      </button>
                      <button
                        class="btn-chon-san-pham"
                        @click.stop="chonSanPham(sp)"
                      >
                        ✅ Chọn sản phẩm ({{
                          selectedIMEIs.length || imeiList.length || 1
                        }})
                      </button>
                    </div>
                    <table class="imei-table">
                      <thead>
                        <tr>
                          <th width="10%">Chọn</th>
                          <th width="70%">IMEI</th>
                          <th width="20%">Trạng thái</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr
                          v-for="(imei, imeiIndex) in imeiList"
                          :key="imeiIndex"
                          class="imei-row-item"
                        >
                          <td>
                            <input
                              type="checkbox"
                              :value="imei.imei"
                              v-model="selectedIMEIs"
                              class="imei-checkbox"
                            />
                          </td>
                          <td class="imei-text">{{ imei.imei }}</td>
                          <td>
                            <span
                              class="status-badge"
                              :class="getStatusClass(imei.trangThai)"
                            >
                              {{ getStatusText(imei.trangThai) }}
                            </span>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
        <!-- ✅ HIỂN THỊ KHI CHƯA NHẬP GÌ (ƯU TIÊN CAO NHẤT) -->
        <div
          v-if="searchSKU.trim() === '' && searchIMEI.trim() === ''"
          class="search-prompt"
        >
          <div class="search-prompt-icon">🔍</div>
          <p class="search-prompt-text">
            Nhập masku hay imei dữ liệu để tìm....
          </p>
        </div>

        <!-- ✅ HIỂN THỊ LỖI IMEI TRÊN BẢNG (CHỈ KHI CÓ INPUT) -->
        <div v-else-if="errorMessage" class="search-prompt error-prompt">
          <p class="search-prompt-text">{{ errorMessage }}</p>
        </div>
        <!-- Loading state -->
        <div v-if="isLoading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu...</p>
        </div>
      </div>

      <!-- Footer with action buttons -->
      <div class="modal-footer">
        <button class="btn-cancel" @click="closeModal">❌ Hủy</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from "vue";
import {
  searchProductBySKU,
  searchProductBySKUOnly,
  searchProductByIMEI,
  searchProductCombined,
  testAPI,
  loadIMEIForProduct,
  loadIMEIForAccessory,
  loadIMEIWithFilter as apiLoadIMEIWithFilter,
} from "../../../service/api.js";
import api from "../../../service/api.js";

// Props
const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
});

// Emits
const emit = defineEmits(["close", "chonSanPham"]);

// State
const searchSKU = ref("");
const searchIMEI = ref("");
const searchResults = ref([]);
const isLoading = ref(false);
const expandedSKU = ref(null);
const imeiList = ref([]);
const loadingIMEI = ref(false);
const selectedIMEIs = ref([]);
//  Đã xóa productCache theo yêu cầu

const errorMessage = ref(""); // thêm biến lỗi
// Computed
const filteredProducts = computed(() => {
  return searchResults.value;
});

// Watchers
watch(imeiList, () => {}, { deep: true });

// Methods
function closeModal() {
  emit("close");
}

// hàm chức năng tìm cho 2 o input
async function performSearch() {
  const sku = searchSKU.value.trim();
  const imei = searchIMEI.value.trim();

  // XÓA LỖI CŨ KHI BẮT ĐẦU TÌM KIẾM MỚI
  errorMessage.value = "";

  if (sku.length === 0 && imei.length === 0) {
    searchResults.value = [];
    return;
  }

  isLoading.value = true;

  try {
    // LOGIC MỚI: Xử lý theo yêu cầu
    if (sku.length > 0 && imei.length > 0) {
      // Cả 2 ô có dữ liệu → Tìm kết hợp
      await handleSearchCombined();
    } else if (sku.length > 0) {
      // Chỉ có SKU → Tìm theo SKU
      await handleSearchSKU();
    } else if (imei.length > 0) {
      // Chỉ có IMEI → Tìm theo IMEI
      await handleSearchIMEI();
    }
  } finally {
    isLoading.value = false;
  }
}

// hàm tìm cho ma sku chi cho nhhập masku
async function handleSearchSKU() {
  const sku = searchSKU.value.trim();
  if (sku.length === 0) {
    searchResults.value = [];
    return;
  }

  try {
    // để sau nay dung kết hợp 1 ô tìm theo 2 điều kiên (ten, masku, hay imei)
    // const data = await searchProductBySKU(sku);

    // hàm chi cho nhập masku
    const data = await searchProductBySKUOnly(sku);

    if (data && data.length > 0) {
      searchResults.value = data;
      //  Đã xóa caching logic theo yêu cầu

      // Tự động mở row đầu tiên và load toàn bộ IMEI
      await nextTick();
      const firstProduct = data[0];
      if (firstProduct) {
        const sku = firstProduct.maSKU || firstProduct.maSKUPhuKien;
        if (sku) {
          await toggleIMEI(sku);
        }
      }
    } else {
      // SKU không tìm thấy
      searchResults.value = [];
      //  HIỂN THỊ LỖI TRÊN BẢNG
      errorMessage.value = `Không tìm thấy sản phẩm với mã SKU: ${sku}`;
    }
  } catch (err) {
    searchResults.value = [];
    //  XỬ LÝ LỖI TỪ JAVA BACKEND
    let errorMsg = `Lỗi khi tìm kiếm SKU: ${sku}`;

    if (err.response && err.response.data) {
      // LẤY THÔNG BÁO LỖI TỪ JAVA BACKEND
      errorMsg = err.response.data;
    }
    // HIỂN THỊ LỖI TRÊN BẢNG
    errorMessage.value = errorMsg;
    searchResults.value = [];
  } finally {
    isLoading.value = false;
  }
}

async function handleSearchIMEI() {
  const imei = searchIMEI.value.trim();

  errorMessage.value = "";

  if (imei.length === 0) {
    searchResults.value = [];
    return;
  }

  try {
    // ✅ YÊU CẦU: Tìm kiếm IMEI chính xác 100%
    const data = await searchProductByIMEI(imei);

    if (data) {
      //  YÊU CẦU: Chỉ hiển thị đúng 1 sản phẩm
      searchResults.value = [data]; // Wrap trong array để hiển thị

      // Tự động mở row và load IMEI
      await nextTick();
      const sku = data.maSKU || data.maSKUPhuKien;
      if (sku) {
        await toggleIMEI(sku);
      }
    } else {
      // IMEI không tìm thấy
      searchResults.value = [];
    }
  } catch (err) {
    searchResults.value = [];
    // NHẬN LỖI TỪ JAVA BACKEND VÀ HIỂN THỊ TRÊN BẢNG
    let errorMsg = `Lỗi khi tìm kiếm IMEI: ${imei}`;

    if (err.response && err.response.data) {
      //  LẤY THÔNG BÁO LỖI TỪ JAVA BACKEND
      errorMsg = err.response.data;
    }
    //  HIỂN THỊ LỖI TRÊN BẢNG
    errorMessage.value = errorMsg;
  }
}

/**
 * Tìm kiếm kết hợp SKU + IMEI
 * ✅ YÊU CẦU: SKU đúng + IMEI gần đúng
 */
async function handleSearchCombined() {
  const sku = searchSKU.value.trim();
  const imei = searchIMEI.value.trim();

  if (sku.length === 0 || imei.length === 0) {
    return;
  }

  try {
    //  YÊU CẦU: Tìm kiếm kết hợp SKU đúng + IMEI gần đúng
    const data = await searchProductCombined(sku, imei);

    if (data && data.length > 0) {
      searchResults.value = data;

      // Tự động mở row đầu tiên và load IMEI
      await nextTick();
      const firstProduct = data[0];
      if (firstProduct) {
        const sku = firstProduct.maSKU || firstProduct.maSKUPhuKien;
        if (sku) {
          await toggleIMEI(sku);
        }
      }
    } else {
      // Không tìm thấy sản phẩm kết hợp
      searchResults.value = [];

      // ✅HIỂN THỊ LỖI TRÊN BẢNG
      errorMessage.value = `Không tìm thấy sản phẩm kết hợp với SKU: ${sku} và IMEI: ${imei}`;
    }
  } catch (err) {
    searchResults.value = [];

    //  XỬ LÝ LỖI TỪ JAVA BACKEND
    let errorMsg = `Lỗi khi tìm kiếm kết hợp SKU: ${sku} và IMEI: ${imei}`;

    if (err.response && err.response.data) {
      //  LẤY THÔNG BÁO LỖI TỪ JAVA BACKEND
      errorMsg = err.response.data;
    }

    // ✅ HIỂN THỊ LỖI TRÊN BẢNG
    errorMessage.value = errorMsg;
  }
}

async function toggleIMEI(maSKU) {
  if (expandedSKU.value === maSKU) {
    expandedSKU.value = null;
    imeiList.value = [];
    selectedIMEIs.value = [];
  } else {
    expandedSKU.value = maSKU;
    await loadIMEIList(maSKU);
  }
}

/**
 * Load danh sách IMEI cho sản phẩm/phụ kiện
 * @param {string} maSKU - Mã SKU của sản phẩm
 * @returns {Promise<void>}
 */
async function loadIMEIList(maSKU) {
  loadingIMEI.value = true;
  imeiList.value = [];

  try {
    // ✅ KIỂM TRA: Có filter IMEI không?
    const imeiFilter = searchIMEI.value.trim();
    const hasImeiFilter = imeiFilter.length > 0;

    if (hasImeiFilter) {
      // YÊU CẦU: Load IMEI với filter khi có filter IMEI (chỉ IMEI hoặc kết hợp)
      await loadIMEIWithFilter(maSKU, imeiFilter);
    } else {
      //  YÊU CẦU: Load toàn bộ IMEI khi chỉ tìm SKU
      await loadAllIMEI(maSKU);
    }
  } catch (err) {
    imeiList.value = [];
  } finally {
    loadingIMEI.value = false;
  }
}

/**
 * Load toàn bộ IMEI (tìm kiếm thường)
 */
async function loadAllIMEI(maSKU) {
  // Thử load IMEI cho cả sản phẩm chính và phụ kiện

  // Thử load IMEI cho sản phẩm chính trước
  try {
    const data = await loadIMEIForProduct(maSKU);

    if (data && data.length > 0) {
      imeiList.value = data;
      return;
    }
  } catch (err) {}

  // Nếu không tìm thấy cho sản phẩm chính, thử phụ kiện
  try {
    const data = await loadIMEIForAccessory(maSKU);

    if (data && data.length > 0) {
      imeiList.value = data;
      return;
    }
  } catch (err) {}

  // Nếu không tìm thấy IMEI nào
  imeiList.value = [];
}

/**
 * Load IMEI với filter (tìm kiếm kết hợp load danh sánh tho điều kiênk)
 */
async function loadIMEIWithFilter(maSKU, imeiFilter) {
  try {
    //  Sử dụng API từ api.js
    const data = await apiLoadIMEIWithFilter(maSKU, imeiFilter);

    if (data && data.length > 0) {
      imeiList.value = data;
    } else {
      imeiList.value = [];
    }
  } catch (error) {
    // Fallback: Load toàn bộ IMEI
    await loadAllIMEI(maSKU);
  }
}

/**
 * Chọn sản phẩm và thêm vào giỏ hàng
 * @param {Object} sp - Thông tin sản phẩm
 * @returns {Promise<void>}
 */
async function chonSanPham(sp) {
  // Đảm bảo IMEI list đã được load trước khi chọn
  const sku = sp.maSKU || sp.maSKUPhuKien;

  // Luôn load IMEI list trước khi chọn sản phẩm
  await loadIMEIList(sku);
  // Nếu không có IMEI được chọn, sử dụng tất cả IMEI có sẵn
  let imeiListToUse = selectedIMEIs.value;
  let soLuongToUse = selectedIMEIs.value.length;

  if (selectedIMEIs.value.length === 0) {
    // Nếu không chọn IMEI cụ thể, sử dụng tất cả IMEI có sẵn
    if (imeiList.value.length > 0) {
      // Sử dụng toàn bộ object IMEI, không chỉ chuỗi
      imeiListToUse = imeiList.value;
      soLuongToUse = imeiList.value.length;
    } else {
      // Nếu không có IMEI nào, thêm sản phẩm với số lượng 1
      imeiListToUse = [];
      soLuongToUse = 1;
    }
  } else {
    // Nếu có IMEI được chọn cụ thể, tìm object IMEI tương ứng
    imeiListToUse = imeiList.value.filter((imeiObj) =>
      selectedIMEIs.value.includes(imeiObj.imei)
    );
    soLuongToUse = imeiListToUse.length;
  }

  // Gửi kèm danh sách IMEI đã chọn
  const dataToEmit = {
    sanPham: sp,
    soLuong: soLuongToUse,
    imeiList: imeiListToUse,
  };

  emit("chonSanPham", dataToEmit);
  selectedIMEIs.value = [];

  // Clear search fields để có thể tìm sản phẩm khác
  searchSKU.value = "";
  searchIMEI.value = "";
  searchResults.value = [];
  expandedSKU.value = null;
}

function boChonTatCa() {
  selectedIMEIs.value = [];
}

function getStatusClass(status) {
  switch (status) {
    case 1:
      return "status-available";
    case 2:
      return "status-sold";
    case 3:
      return "status-reserved";
    default:
      return "status-unknown";
  }
}

function getStatusText(status) {
  switch (status) {
    case 1:
      return "Có sẵn";
    case 2:
      return "Đã bán";
    case 3:
      return "Đã đặt";
    default:
      return "Không xác định";
  }
}

function formatCurrency(value) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
}

function getImeiCount(sp) {
  // Nếu đang mở IMEI list cho sản phẩm này, hiển thị số IMEI thực tế
  if (
    expandedSKU.value === (sp.maSKU || sp.maSKUPhuKien) &&
    imeiList.value.length > 0
  ) {
    return imeiList.value.length;
  }
  // Nếu không, hiển thị số lượng từ dữ liệu sản phẩm
  return sp.soLuong || 0;
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 12px;
  width: 1000px;
  height: 700px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.modal-header h3 {
  margin: 0;
  color: #495057;
  font-size: 20px;
  font-weight: 600;
}

.btn-close {
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #c82333;
  transform: scale(1.1);
}

.search-section {
  padding: 20px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  flex-shrink: 0;
}

.search-row {
  display: flex;
  gap: 16px;
}

.search-field {
  flex: 1;
}

.field-label {
  display: block;
  color: #495057;
  font-weight: 500;
  margin-bottom: 6px;
}

.input-field {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.input-field:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.table-container {
  flex: 1;
  overflow-y: auto;
  padding: 0 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th,
td {
  border: 1px solid #dee2e6;
  padding: 12px 8px;
  text-align: center;
}

th {
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
  font-weight: 600;
  color: #495057;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.product-row {
  cursor: pointer;
  transition: all 0.2s;
}

.product-row:hover {
  background: #f8f9fa;
}

.product-row.expanded {
  background: #e3f2fd;
}

.text-left {
  text-align: left !important;
  padding-left: 12px;
}

.text-right {
  text-align: right !important;
  padding-right: 12px;
}

.sku-badge,
.qty-badge,
.imei-count-badge {
  background: #007bff;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.imei-row {
  background: #f8f9fa;
}

.imei-container {
  padding: 20px;
  text-align: left;
  width: 100%;
}

.loading,
.empty-imei {
  text-align: center;
  padding: 20px;
  color: #6c757d;
  font-style: italic;
}

.imei-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0;
}

.imei-header h4 {
  margin: 0;
  color: #495057;
  font-size: 16px;
  font-weight: 600;
}

.selected-count {
  background: #28a745;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.deselect-row {
  margin-bottom: 16px;
  padding: 0;
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn-deselect {
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-deselect:hover {
  background: #5a6268;
}

.btn-chon-san-pham {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(40, 167, 69, 0.3);
}

.btn-chon-san-pham:hover {
  background: linear-gradient(135deg, #20c997 0%, #1e7e34 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(40, 167, 69, 0.4);
}

.imei-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  margin: 0;
  table-layout: fixed;
  min-width: 100%;
}

.imei-table th,
.imei-table td {
  border: 1px solid #dee2e6;
  padding: 8px;
  text-align: center;
}

.imei-table th {
  background: #e9ecef;
  font-weight: 600;
  color: #495057;
}

.imei-row-item:hover {
  background: #f8f9fa;
}

.imei-checkbox {
  transform: scale(1.2);
}

.imei-text {
  font-family: "Courier New", monospace;
  color: #495057;
  font-weight: 500;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.status-available {
  background: #d4edda;
  color: #155724;
}

.status-sold {
  background: #f8d7da;
  color: #721c24;
}

.status-reserved {
  background: #fff3cd;
  color: #856404;
}

.status-unknown {
  background: #e2e3e5;
  color: #6c757d;
}

/* Loại Cell */
.loai-cell {
  text-align: center !important;
  padding: 8px !important;
  vertical-align: middle;
}

.loai-text {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 12px;
  display: inline-block;
}

/* Sản phẩm chính */
.loai-san-pham {
  background: #e3f2fd;
  color: #1976d2;
  border: 1px solid #bbdefb;
}

/* Phụ kiện */
.loai-phu-kien {
  background: #f3e5f5;
  color: #7b1fa2;
  border: 1px solid #ce93d8;
}

.empty-state,
.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.search-hint {
  margin-top: 20px;
  padding: 15px;
  background: #e7f3ff;
  border: 1px solid #b3d9ff;
  border-radius: 8px;
  text-align: left;
}

.search-hint p {
  margin: 5px 0;
  font-size: 13px;
  color: #0066cc;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* Modal Footer */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
  flex-shrink: 0;
}

.btn-cancel,
.btn-confirm {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel {
  background: #6c757d;
  color: white;
}

.btn-cancel:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.btn-confirm {
  background: #28a745;
  color: white;
}

.btn-confirm:hover {
  background: #218838;
  transform: translateY(-1px);
}

/* ✅ CSS ĐẸP CHO THÔNG BÁO TÌM KIẾM */
.search-prompt {
  text-align: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  margin: 20px;
  border: 2px dashed #dee2e6;
  transition: all 0.3s ease;
}

.search-prompt:hover {
  border-color: #007bff;
  background: linear-gradient(135deg, #e3f2fd 0%, #f8f9fa 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.1);
}

.search-prompt-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.7;
  animation: pulse 2s infinite;
}

.search-prompt-text {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #495057;
  letter-spacing: 0.5px;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 0.7;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
}

/* ✅ CSS CHO THÔNG BÁO LỖI */
.error-prompt {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%) !important;
  border: 2px dashed #dc3545 !important;
  color: #721c24;
}

.error-prompt:hover {
  border-color: #dc3545 !important;
  background: linear-gradient(135deg, #f5c6cb 0%, #f1b0b7 100%) !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.2);
}

.error-prompt .search-prompt-icon {
  color: #dc3545;
}

.error-prompt .search-prompt-text {
  color: #721c24;
  font-weight: 600;
}
</style>
