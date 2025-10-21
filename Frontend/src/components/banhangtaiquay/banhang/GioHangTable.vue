<template>
  <table>
    <thead>
      <tr>
        <th width="5%">STT</th>
        <th width="9%">Mã SKU</th>
        <th width="16%">Tên sản phẩm</th>
        <th width="12%">Thuộc tính</th>
        <th width="8%">Loại</th>
        <th width="9%">Đơn giá</th>
        <th width="7%">Số lượng</th>
        <th width="8%">IMEI</th>
        <th width="9%">Thành tiền</th>
        <th width="5%">Xóa</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(item, index) in gioHang" :key="item.maSKU || item.maSKUPhuKien">
        <td>{{ index + 1 }}</td>
        <td>{{ item.maSKU || item.maSKUPhuKien }}</td>
        <td class="text-left">{{ item.tenSanPham }}</td>
        <td class="thuoc-tinh-cell">
          <div
            v-if="
              item.thuocTinh &&
              typeof item.thuocTinh === 'string' &&
              item.thuocTinh.trim() !== ''
            "
            class="thuoc-tinh-string"
          >
            <span class="thuoc-tinh-text">{{ item.thuocTinh }}</span>
          </div>
          <div
            v-else-if="
              item.thuocTinh &&
              typeof item.thuocTinh === 'object' &&
              Object.keys(item.thuocTinh).length > 0
            "
            class="thuoc-tinh-list"
          >
            <div
              v-for="(value, key) in item.thuocTinh"
              :key="key"
              class="thuoc-tinh-item"
            >
              <span class="thuoc-tinh-text">{{ key }}: {{ value }}</span>
            </div>
          </div>
          <div v-else class="no-thuoc-tinh">
            <span class="no-thuoc-tinh-text">N/A</span>
          </div>
        </td>
        <td class="loai-cell">
          <span 
            class="loai-text" 
            :class="{
              'loai-san-pham': (item.loai || 'Sản phẩm chính') === 'Sản phẩm chính',
              'loai-phu-kien': (item.loai || 'Sản phẩm chính') === 'Phụ kiện'
            }"
          >
            {{ item.loai || 'Sản phẩm chính' }}
          </span>
        </td>
        <td>{{ formatCurrency(item.gia || 0) }}</td>
        <td class="so-luong-cell">
          <span class="so-luong-text">{{
            item.imeiList ? item.imeiList.length : item.soLuongMua
          }}</span>
          <div class="so-luong-note"></div>
        </td>
        <td class="imei-cell">
          
          <button 
            class="btn-view-imei" 
            @click="xemImei(item)"
            :disabled="!item.imeiList || item.imeiList.length === 0"
            :title="item.imeiList && item.imeiList.length > 0 ? `Xem ${item.imeiList.length} IMEI` : 'Không có IMEI'"
          >
            📱 {{ item.imeiList ? item.imeiList.length : 0 }}
          </button>
        </td>
        <td class="font-bold">{{ formatCurrency(item.thanhTien || 0) }}</td>
        <td>
          <button class="btn-delete" @click="xoaSanPham(item.maSKU || item.maSKUPhuKien)">🗑️</button>
        </td>
      </tr>
    </tbody>
  </table>

  <!-- Nếu giỏ hàng rỗng -->
  <div v-if="gioHang.length === 0" class="empty-cart">
    <p class="empty-text">🛒 Giỏ hàng trống</p>
    <p class="empty-hint">Quét IMEI hoặc click "➕ Thêm sản phẩm" để bắt đầu</p>
  </div>

  <!-- Modal hiển thị IMEI -->
  <div v-if="showImeiModal" class="imei-modal-overlay" @click="closeImeiModal">
    <div class="imei-modal-container" @click.stop>
      <div class="imei-modal-header">
        <h3>📱 Danh sách IMEI - {{ selectedItem?.tenSanPham || 'N/A' }}</h3>
        <button class="btn-close-modal" @click="closeImeiModal">✕</button>
      </div>
      
      <div class="imei-modal-content">
        <div v-if="selectedItem?.imeiList && selectedItem.imeiList.length > 0" class="imei-list-container">
          <div class="imei-summary">
            <span class="imei-count">Tổng cộng: {{ selectedItem.imeiList.length }} IMEI</span>
          </div>
          
          <div class="imei-grid">
            <div 
              v-for="(imei, index) in selectedItem.imeiList" 
              :key="index"
              class="imei-card"
            >
              <div class="imei-info">
                <span class="imei-number">{{ typeof imei === 'string' ? imei : imei.imei }}</span>
                <span v-if="typeof imei === 'object' && imei.trangThai" class="imei-status">
                  {{ imei.trangThai === 1 ? 'Hoạt động' : 'Không hoạt động' }}
                </span>
              </div>
              <button 
                class="btn-remove-imei-card"
                @click="xoaImei(selectedItem.maSKU || selectedItem.maSKUPhuKien, index)"
                title="Xóa IMEI"
              >
                ✕
              </button>
            </div>
          </div>
        </div>
        
        <div v-else class="no-imei-message">
          <p>📦 Không có IMEI nào</p>
        </div>
      </div>
      
      <div class="imei-modal-footer">
        <button class="btn-close" @click="closeImeiModal">Đóng</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useGioHangBanHangTaiQuay } from "@/components/banhangtaiquay/useGioHangBanHangTaiQuay";

const { gioHang, capNhatSoLuong, capNhatSoLuongTheoImei, xoaSanPham, xoaImei } =
  useGioHangBanHangTaiQuay();


// State cho modal IMEI
const showImeiModal = ref(false);
const selectedItem = ref(null);


function formatCurrency(value) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
}

function xemImei(item) {
  selectedItem.value = item;
  showImeiModal.value = true;
}

function closeImeiModal() {
  showImeiModal.value = false;
  selectedItem.value = null;
}
</script>

<style scoped>
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
}

tbody tr {
  transition: all 0.2s;
}

tbody tr:hover {
  background: #f8f9fa;
  transform: scale(1.01);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.text-left {
  text-align: left !important;
  padding-left: 12px;
}

.font-bold {
  font-weight: 700;
  color: #28a745;
}

.quantity-input-small {
  width: 60px;
  padding: 6px;
  text-align: center;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;
}

.quantity-input-small:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.btn-delete {
  padding: 6px 10px;
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.3);
}

.btn-delete:hover {
  background: linear-gradient(135deg, #c82333 0%, #bd2130 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.4);
}

.btn-delete:active {
  transform: translateY(0);
}

.empty-cart {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  margin: 20px 0;
}

.empty-text {
  font-size: 20px;
  color: #6c757d;
  margin-bottom: 10px;
  font-weight: 600;
}

.empty-hint {
  font-size: 14px;
  color: #adb5bd;
  font-style: italic;
}

/* IMEI Cell */
.imei-cell {
  text-align: center !important;
  padding: 8px !important;
  vertical-align: middle;
}

.btn-view-imei {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(0, 123, 255, 0.3);
  min-width: 60px;
}

.btn-view-imei:hover:not(:disabled) {
  background: linear-gradient(135deg, #0056b3 0%, #004085 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.4);
}

.btn-view-imei:disabled {
  background: #6c757d;
  cursor: not-allowed;
  opacity: 0.6;
}

/* IMEI Modal */
.imei-modal-overlay {
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

.imei-modal-container {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.imei-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.imei-modal-header h3 {
  margin: 0;
  color: #495057;
  font-size: 18px;
  font-weight: 600;
}

.btn-close-modal {
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

.btn-close-modal:hover {
  background: #c82333;
  transform: scale(1.1);
}

.imei-modal-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}

.imei-list-container {
  max-height: 400px;
  overflow-y: auto;
}

.imei-summary {
  margin-bottom: 16px;
  padding: 12px;
  background: #e3f2fd;
  border-radius: 8px;
  text-align: center;
}

.imei-count {
  font-size: 16px;
  font-weight: 600;
  color: #1976d2;
}

.imei-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 12px;
}

.imei-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.2s ease;
}

.imei-card:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.imei-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.imei-number {
  font-family: "Courier New", monospace;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  word-break: break-all;
}

.imei-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #d4edda;
  color: #155724;
  font-weight: 500;
}

.btn-remove-imei-card {
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 8px;
  transition: all 0.2s ease;
}

.btn-remove-imei-card:hover {
  background: #c82333;
  transform: scale(1.1);
}

.no-imei-message {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.no-imei-message p {
  font-size: 16px;
  font-style: italic;
}

.imei-modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 20px 24px;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

.btn-close {
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-close:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

/* Thuộc tính Cell */
.thuoc-tinh-cell {
  text-align: left !important;
  padding: 8px !important;
  vertical-align: top;
}

/* Thuộc tính dạng string */
.thuoc-tinh-string {
  background: #e3f2fd;
  border: 1px solid #bbdefb;
  border-radius: 4px;
  padding: 6px 8px;
  font-size: 12px;
}

.thuoc-tinh-string .thuoc-tinh-text {
  color: #1976d2;
  font-weight: 500;
  word-break: break-word;
  line-height: 1.4;
}

/* Thuộc tính dạng object */
.thuoc-tinh-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-height: 80px;
  overflow-y: auto;
}

.thuoc-tinh-item {
  display: flex;
  align-items: center;
  background: #e3f2fd;
  border: 1px solid #bbdefb;
  border-radius: 4px;
  padding: 4px 6px;
  font-size: 12px;
}

.thuoc-tinh-text {
  flex: 1;
  color: #1976d2;
  font-weight: 500;
  word-break: break-word;
}

.no-thuoc-tinh {
  text-align: center;
  padding: 8px;
  color: #6c757d;
  font-style: italic;
  font-size: 12px;
}

.no-thuoc-tinh-text {
  color: #adb5bd;
}

/* Số lượng Cell */
.so-luong-cell {
  text-align: center !important;
  padding: 8px !important;
  vertical-align: middle;
}

.so-luong-text {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  display: block;
}

.so-luong-note {
  font-size: 10px;
  color: #6c757d;
  font-style: italic;
  margin-top: 2px;
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
</style>

