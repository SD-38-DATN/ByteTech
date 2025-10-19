<template>
  <div class="banhang-page">
    <h3 class="title">🛒 BÁN HÀNG TẠI QUẦY</h3>

    <!-- Quản lý đơn hàng -->
    <div class="order-management">
      <div class="order-management-row">
        <!-- Nút tạo đơn hàng mới -->
        <div class="order-controls">
          <button class="btn-create-new-order" @click="handleTaoDonHangMoi">
            🆕 Tạo đơn
          </button>
        </div>

        <!-- Đơn hàng đang được chọn -->
        <div v-if="donHangHienTaiId && danhSachDonHang.length > 0" class="current-order-info">
          <div class="current-order-header">
            <h4>📋 Đơn hàng đang xử lý</h4>
            <span class="current-order-number"
              >Đơn #{{ getCurrentOrderNumber() }}</span
            >
          </div>
          <div class="current-order-details">
            <div class="order-detail-item">
              <span class="detail-label">Mã đơn hàng:</span>
              <span class="detail-value">
                {{ getCurrentOrderMaDonHang() || "Chưa có" }}
              </span>
            </div>
            <div class="order-detail-item">
              <span class="detail-label">Trạng thái:</span>
              <span class="detail-value" :class="getCurrentOrderStatus()">
                {{
                  getCurrentOrderStatus() === "draft"
                    ? "📝 Chưa lưu"
                    : "✅ Đã lưu"
                }}
              </span>
            </div>
          </div>
        </div>

        <!-- Trạng thái trống khi không có đơn hàng -->
        <div v-if="danhSachDonHang.length === 0" class="empty-state">
          <div class="empty-state-content">
            <div class="empty-icon">📋</div>
            <h3>Chưa có đơn hàng nào</h3>
            <p>Nhấn "Tạo đơn" để bắt đầu bán hàng</p>
          </div>
        </div>

        <!-- Danh sách đơn hàng -->
        <div v-if="danhSachDonHang.length > 0" class="orders-section">
          <div class="orders-header">
            <h4>
              📋 Danh sách đơn hàng ({{ danhSachDonHang.filter(dh => !dh.hidden).length }}) - Chưa lưu:
              {{ getDonHangChuaLuuCount() }}
            </h4>
            <div class="order-actions">
              <button
                class="btn-clear-all"
                @click="xoaTatCaDonHang"
                v-if="danhSachDonHang.length > 0"
              >
                🗑️ Xóa tất cả
              </button>
            </div>
          </div>

          <div class="orders-list">
            <div
              v-for="(donHang, index) in danhSachDonHang.filter(dh => !dh.hidden)"
              :key="donHang.id"
              class="order-item"
              :class="{ active: donHang.id === donHangHienTaiId }"
              @click="chonDonHang(donHang.id)"
            >
              <div class="order-info">
                <span class="order-number">Đơn #{{ index + 1 }}</span>
                <div class="order-actions">
                  <span class="order-status" :class="donHang.trangThai">
                    {{
                      donHang.trangThai === "draft"
                        ? "📝 Chưa lưu"
                        : "✅ Đã lưu"
                    }}
                  </span>
                  <button
                    class="btn-delete-order"
                    @click.stop="xoaDonHang(donHang.id)"
                    title="Xóa đơn hàng"
                  >
                    🗑️
                  </button>
                </div>
              </div>
              <div class="order-summary">
                <span class="order-items"
                  >{{ donHang.gioHang.length }} sản phẩm</span
                >
                <span class="order-total">{{
                  formatCurrency(donHang.tongTien)
                }}</span>
              </div>
              <div class="order-code">
                <span class="code-label">Mã đơn:</span>
                <span class="code-value">{{
                  donHang.maDonHang || "Chưa có"
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="content">
      <!-- Panel trái -->
      <div class="left-panel">
        <div class="info-row">
          <div class="info-section full">
            <ThongTinKhachHang 
              :customer-info="thongTinKhachHangHienTai"
              @customer-updated="capNhatThongTinKhachHang"
            />
          </div>
        </div>

        <ThanhToan 
          @save="handleSave"
          @submit="handleSubmit"
          @print="handlePrint"
        />
      </div>

      <!-- Panel phải -->
      <div class="right-panel">
        <div class="add-product-bar">
          <button class="btn-add-product" @click="isModalOpen = true">
            ➕ Thêm sản phẩm
          </button>
        </div>

        <div class="table-wrapper">
          <GioHangTable />
        </div>

        <!-- Thông tin tổng kết -->
        <div class="summary-info">
          <div class="summary-row">
            <span class="summary-label">Số lượng</span>
            <span class="summary-value">{{ tongSoLuong }}</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">Tổng tiền hàng</span>
            <span class="summary-value">{{
              formatCurrency(tongTienHang)
            }}</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">Tổng KM</span>
            <span class="summary-value">{{
              formatCurrency(tongKhuyenMai)
            }}</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">Tổng giảm</span>
            <span class="summary-value">{{ formatCurrency(tongGiam) }}</span>
          </div>
          <div class="summary-row highlight">
            <span class="summary-label">Thanh toán</span>
            <span class="summary-value">{{
              formatCurrency(tongThanhToan)
            }}</span>
          </div>
        </div>

        <ChonSanPham
          v-if="isModalOpen"
          @close="isModalOpen = false"
          @chonSanPham="handleChonSanPham"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import ThongTinKhachHang from "@/components/banhangtaiquay/banhang/ThongTinKhachHang.vue";
import GioHangTable from "@/components/banhangtaiquay/banhang/GioHangTable.vue";
import ChonSanPham from "@/components/banhangtaiquay/banhang/ChonSanPham.vue";
import ThanhToan from "@/components/banhangtaiquay/banhang/ThanhToan.vue";
import { useGioHangBanHangTaiQuay } from "@/components/banhangtaiquay/useGioHangBanHangTaiQuay";

const isModalOpen = ref(false);

// Quản lý đơn hàng
const danhSachDonHang = ref([]);
const donHangHienTaiId = ref(null);

// Quản lý thông tin khách hàng
const thongTinKhachHangHienTai = ref({
  tenKhachHang: "",
  soDienThoai: "",
  diaChi: "",
  customerInfo: null
});

// Sử dụng composable giỏ hàng
const {
  themSanPham,
  loadSanPhamTuDonHang,
  xoaToanBoGioHang,
  capNhatThanhTien,
  tongSoLuong,
  tongTienHang,
  tongKhuyenMai,
  tongGiam,
  tongThanhToan,
  gioHang,
  isSwitchingOrder,
} = useGioHangBanHangTaiQuay();

// Khởi tạo từ localStorage
function initFromLocalStorage() {
  const savedOrders = localStorage.getItem("danhSachDonHang");
  if (savedOrders) {
    danhSachDonHang.value = JSON.parse(savedOrders);
    console.log(
      "📦 Đã tải",
      danhSachDonHang.value.length,
      "đơn hàng từ localStorage"
    );
    
    // Debug: Kiểm tra thông tin khách hàng trong từng đơn hàng
    danhSachDonHang.value.forEach((donHang, index) => {
      console.log(`📋 Đơn hàng ${index + 1} (${donHang.id}):`);
      console.log(`  - Sản phẩm: ${donHang.gioHang?.length || 0} items`);
      console.log(`  - Cấu trúc gioHang:`, donHang.gioHang);
      console.log(`  - Thông tin khách hàng:`, donHang.thongTinKhachHang);
    });
  }

  const currentOrderId = localStorage.getItem("donHangHienTaiId");
  if (currentOrderId) {
    donHangHienTaiId.value = currentOrderId;
    console.log("🔄 Load đơn hàng hiện tại:", currentOrderId);
    loadDonHangHienTai();
  }
}

// Lưu vào localStorage
function saveToLocalStorage() {
  localStorage.setItem(
    "danhSachDonHang",
    JSON.stringify(danhSachDonHang.value)
  );
  localStorage.setItem("donHangHienTaiId", donHangHienTaiId.value || "");
  console.log("💾 Đã lưu đơn hàng vào localStorage");
}

// Load đơn hàng hiện tại
async function loadDonHangHienTai() {
  if (!donHangHienTaiId.value) return;

  const donHang = danhSachDonHang.value.find(
    (dh) => dh.id === donHangHienTaiId.value
  );
  if (donHang) {
    
    // ✅ Chỉ xóa UI giỏ hàng, KHÔNG reset IMEI status
    gioHang.value = [];

    // Load sản phẩm từ đơn hàng (sử dụng function riêng để không cộng thêm)
    for (const item of donHang.gioHang) {
      await loadSanPhamTuDonHang(item.sanPham, item.soLuongMua, item.imeiList);
    }

    // ✅ LOAD THÔNG TIN KHÁCH HÀNG TỪ ĐƠN HÀNG
    console.log("🔍 DEBUG: Kiểm tra thông tin khách hàng của đơn hàng:", donHang.id);
    console.log("🔍 DEBUG: donHang.thongTinKhachHang:", donHang.thongTinKhachHang);
    
    if (donHang.thongTinKhachHang && (donHang.thongTinKhachHang.tenKhachHang || donHang.thongTinKhachHang.soDienThoai || donHang.thongTinKhachHang.diaChi)) {
      thongTinKhachHangHienTai.value = { ...donHang.thongTinKhachHang };
      console.log("👤 Đã load thông tin khách hàng từ đơn hàng:", donHang.thongTinKhachHang);
    } else {
      // Reset thông tin khách hàng nếu đơn hàng chưa có
      thongTinKhachHangHienTai.value = {
        tenKhachHang: "",
        soDienThoai: "",
        diaChi: "",
        customerInfo: null
      };
      console.log("🔄 Reset thông tin khách hàng cho đơn hàng mới");
    }

    console.log(
      "🔄 Đã load đơn hàng:",
      donHang.id,
      "với",
      donHang.gioHang.length,
      "sản phẩm"
    );
  }
}

// Khởi tạo khi component mount
initFromLocalStorage();

// ✅ Tự động cập nhật đơn hàng khi giỏ hàng thay đổi (với debounce)
let watcherTimeout = null;
watch(
  gioHang,
  (newGioHang, oldGioHang) => {
    // Cập nhật khi có đơn hàng hiện tại và có bất kỳ thay đổi nào
    if (donHangHienTaiId.value) {
      const hasLengthChange = newGioHang.length !== oldGioHang?.length;
      const hasContentChange = JSON.stringify(newGioHang) !== JSON.stringify(oldGioHang);
      
      if (hasLengthChange || hasContentChange) {
        console.log(
          "🔄 Giỏ hàng thay đổi:",
          oldGioHang?.length || 0,
          "→",
          newGioHang.length,
          "sản phẩm"
        );
        console.log("🔍 DEBUG: Đơn hàng hiện tại:", donHangHienTaiId.value);
        
        // ✅ QUAN TRỌNG: Chỉ cập nhật khi không phải đang chuyển đơn hàng
        if (!isSwitchingOrder.value) {
          // ✅ QUAN TRỌNG: Đặc biệt xử lý khi giỏ hàng trống
          if (newGioHang.length === 0) {
            console.log("📦 Giỏ hàng trống - cập nhật đơn hàng ngay lập tức");
            capNhatDonHangHienTai();
          } else {
            // Debounce cho trường hợp có sản phẩm
            if (watcherTimeout) {
              clearTimeout(watcherTimeout);
            }
            watcherTimeout = setTimeout(() => {
              console.log("💾 Tự động lưu đơn hàng do thay đổi giỏ hàng");
              capNhatDonHangHienTai();
            }, 100); // 100ms debounce
          }
        } else {
          console.log("⏭️ Bỏ qua cập nhật đơn hàng vì đang chuyển đơn hàng");
        }
      }
    }
  },
  { deep: true }
);

// Format currency
const formatCurrency = (amount) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount || 0);
};

// Xử lý lưu đơn
async function handleSave() {
  if (gioHang.value.length === 0) {
    alert("⚠️ Giỏ hàng trống! Vui lòng thêm sản phẩm.");
    return;
  }

  // ✅ VALIDATION: Kiểm tra số điện thoại khách hàng
  if (!thongTinKhachHangHienTai.value.soDienThoai || thongTinKhachHangHienTai.value.soDienThoai.trim() === '') {
    alert("⚠️ Vui lòng nhập số điện thoại khách hàng!");
    return;
  }

  try {
    // Lấy thông tin nhân viên từ token
    const token = localStorage.getItem("token");
    if (!token) {
      alert("⚠️ Bạn cần đăng nhập để lưu đơn hàng!");
      return;
    }

    // Decode token để lấy userId
    const decoded = JSON.parse(atob(token.split('.')[1]));
    console.log("🔍 DEBUG Token decoded:", decoded);
    console.log("🔍 DEBUG Available keys:", Object.keys(decoded));
    const userId = decoded.userId || decoded.id || decoded.sub;
    console.log("🔍 DEBUG Extracted userId:", userId);

    if (!userId) {
      alert("❌ Không thể lấy thông tin user từ token. Vui lòng đăng nhập lại!");
      return;
    }

    // Chuẩn bị dữ liệu đơn hàng
    const orderData = {
      userId: parseInt(userId) || 1, // Convert to Integer, fallback to 1
      maDonHang: getCurrentOrderMaDonHang(), // Gửi mã đơn hàng hiện tại nếu có
      isUpdate: getCurrentOrderMaDonHang() != null, // true nếu có đơn hàng hiện tại, false nếu tạo mới
      tongTien: tongThanhToan.value,
      diaChiGiaoHang: thongTinKhachHangHienTai.value.diaChi || "",
      soDienThoai: thongTinKhachHangHienTai.value.soDienThoai || "",
      phuongThucThanhToan: "tienmat", // Mặc định
      ghiChu: "",
      userVoucherId: null, // TODO: Xử lý voucher
      chiTietDonHangs: gioHang.value.flatMap(item => {
        const isSanPham = item.maSKU && !item.maSKUPhuKien;
        
        console.log("🔍 DEBUG: Xử lý chi tiết đơn hàng (LƯU):", {
          maSKU: item.maSKU,
          maSKUPhuKien: item.maSKUPhuKien,
          isSanPham: isSanPham,
          imeiListLength: item.imeiList ? item.imeiList.length : 0,
          loaiSanPham: isSanPham ? "sanpham" : "phukien"
        });
        
        // ✅ QUAN TRỌNG: Mỗi IMEI tạo 1 dòng riêng với soLuong=1
        if (item.imeiList && item.imeiList.length > 0) {
          console.log("🔍 DEBUG CHI TIẾT IMEI (LƯU):", {
            imeiList: item.imeiList,
            firstImei: item.imeiList[0],
            firstImeiStringified: JSON.stringify(item.imeiList[0]),
            allImeiStringified: JSON.stringify(item.imeiList)
          });
          
          // Tạo 1 dòng cho mỗi IMEI
          return item.imeiList.map(imei => {
            let imeiId = null;
            // ✅ SỬA: IMEI object có thuộc tính 'id' (integer) từ API
            if (typeof imei === 'object' && imei.id) {
              imeiId = imei.id; // Sử dụng ID thực tế từ database
            }
            
            console.log("🔍 DEBUG: Tạo dòng cho IMEI:", {
              imei: imei,
              imeiId: imeiId,
              soLuong: 1 // ✅ Luôn = 1 cho mỗi IMEI
            });
            
            return {
              maSKU: item.maSKU,
              maSKUPhuKien: item.maSKUPhuKien,
              imeiId: imeiId,
              soLuong: 1, // ✅ QUAN TRỌNG: Mỗi IMEI = 1 dòng với soLuong=1
              gia: item.gia,
              loaiSanPham: isSanPham ? "sanpham" : "phukien"
            };
          });
        } else {
          // Không có IMEI - tạo 1 dòng với soLuong=item.soLuongMua
          console.log("🔍 DEBUG: Không có IMEI - tạo 1 dòng:", {
            soLuong: item.soLuongMua
          });
          
          return [{
            maSKU: item.maSKU,
            maSKUPhuKien: item.maSKUPhuKien,
            imeiId: null,
            soLuong: item.soLuongMua, // ✅ Có thể > 1 nếu không có IMEI
            gia: item.gia,
            loaiSanPham: isSanPham ? "sanpham" : "phukien"
          }];
        }
      })
    };

    // Gọi API lưu đơn hàng
    const { luuDonHang } = await import('@/service/api');
    const result = await luuDonHang(orderData);

    // Cập nhật đơn hàng hiện tại với mã đơn hàng từ server
  const donHangHienTai = danhSachDonHang.value.find(
    (dh) => dh.id === donHangHienTaiId.value
  );

  if (donHangHienTai) {
      donHangHienTai.maDonHang = result.maDonHang;
    donHangHienTai.trangThai = "saved";
    donHangHienTai.ngayCapNhat = new Date().toISOString();
    donHangHienTai.thongTinKhachHang = { ...thongTinKhachHangHienTai.value };
    }

    // ✅ QUAN TRỌNG: Xóa đơn hàng khỏi bộ nhớ web sau khi lưu thành công
    console.log("🗑️ Xóa đơn hàng khỏi bộ nhớ web sau khi lưu thành công");
    
    // Xóa đơn hàng khỏi danh sách
    const index = danhSachDonHang.value.findIndex((dh) => dh.id === donHangHienTaiId.value);
    if (index !== -1) {
      danhSachDonHang.value.splice(index, 1);
    }
    
    // Reset đơn hàng hiện tại
    donHangHienTaiId.value = null;
    
    // Xóa giỏ hàng (KHÔNG cập nhật trạng thái IMEI vì backend đã xử lý)
    await xoaToanBoGioHang(true);
    
    // Lưu vào localStorage
    saveToLocalStorage();

    console.log("💾 Đã lưu đơn hàng:", result);
    alert("✅ Đã lưu đơn hàng thành công!");
    
  } catch (error) {
    console.error("❌ Lỗi khi lưu đơn hàng:", error);
    alert("❌ Lỗi khi lưu đơn hàng: " + error.message);
  }
}

// Xử lý chốt đơn
async function handleSubmit() {
  if (gioHang.value.length === 0) {
    alert("⚠️ Giỏ hàng trống! Vui lòng thêm sản phẩm.");
    return;
  }

  // ✅ VALIDATION: Kiểm tra số điện thoại khách hàng
  if (!thongTinKhachHangHienTai.value.soDienThoai || thongTinKhachHangHienTai.value.soDienThoai.trim() === '') {
    alert("⚠️ Vui lòng nhập số điện thoại khách hàng!");
    return;
  }

  // Xác nhận thanh toán
  const confirmThanhToan = confirm("⚠️ Bạn có chắc muốn thanh toán đơn hàng?\n\nSau khi thanh toán, sẽ trừ kho và IMEI sẽ chuyển sang trạng thái 'Đã bán' và không thể hoàn tác!");
  if (!confirmThanhToan) return;

  try {
    // Lấy thông tin nhân viên từ token
    const token = localStorage.getItem("token");
    if (!token) {
      alert("⚠️ Bạn cần đăng nhập để chốt đơn hàng!");
      return;
    }

    // Decode token để lấy userId
    const decoded = JSON.parse(atob(token.split('.')[1]));
    console.log("🔍 DEBUG Token decoded (thanhToan):", decoded);
    console.log("🔍 DEBUG Available keys (thanhToan):", Object.keys(decoded));
    const userId = decoded.userId || decoded.id || decoded.sub;
    console.log("🔍 DEBUG Extracted userId (thanhToan):", userId);

    if (!userId) {
      alert("❌ Không thể lấy thông tin user từ token. Vui lòng đăng nhập lại!");
      return;
    }

    // Chuẩn bị dữ liệu đơn hàng
    const orderData = {
      userId: parseInt(userId) || 1, // Convert to Integer, fallback to 1
      tongTien: tongThanhToan.value,
      diaChiGiaoHang: thongTinKhachHangHienTai.value.diaChi || "",
      soDienThoai: thongTinKhachHangHienTai.value.soDienThoai || "",
      phuongThucThanhToan: "tienmat", // Mặc định
      ghiChu: "",
      userVoucherId: null, // TODO: Xử lý voucher
      chiTietDonHangs: gioHang.value.flatMap(item => {
        const isSanPham = item.maSKU && !item.maSKUPhuKien;
        
        console.log("🔍 DEBUG: Xử lý chi tiết đơn hàng (THANH TOÁN):", {
          maSKU: item.maSKU,
          maSKUPhuKien: item.maSKUPhuKien,
          isSanPham: isSanPham,
          imeiListLength: item.imeiList ? item.imeiList.length : 0,
          loaiSanPham: isSanPham ? "sanpham" : "phukien"
        });
        
        // ✅ QUAN TRỌNG: Mỗi IMEI tạo 1 dòng riêng với soLuong=1
        if (item.imeiList && item.imeiList.length > 0) {
          console.log("🔍 DEBUG CHI TIẾT IMEI (THANH TOÁN):", {
            imeiList: item.imeiList,
            firstImei: item.imeiList[0],
            firstImeiStringified: JSON.stringify(item.imeiList[0]),
            allImeiStringified: JSON.stringify(item.imeiList)
          });
          
          // Tạo 1 dòng cho mỗi IMEI
          return item.imeiList.map(imei => {
            let imeiId = null;
            // ✅ SỬA: IMEI object có thuộc tính 'id' (integer) từ API
            if (typeof imei === 'object' && imei.id) {
              imeiId = imei.id; // Sử dụng ID thực tế từ database
            }
            
            console.log("🔍 DEBUG: Tạo dòng cho IMEI (THANH TOÁN):", {
              imei: imei,
              imeiId: imeiId,
              soLuong: 1 // ✅ Luôn = 1 cho mỗi IMEI
            });
            
            return {
              maSKU: item.maSKU,
              maSKUPhuKien: item.maSKUPhuKien,
              imeiId: imeiId,
              soLuong: 1, // ✅ QUAN TRỌNG: Mỗi IMEI = 1 dòng với soLuong=1
              gia: item.gia,
              loaiSanPham: isSanPham ? "sanpham" : "phukien"
            };
          });
        } else {
          // Không có IMEI - tạo 1 dòng với soLuong=item.soLuongMua
          console.log("🔍 DEBUG: Không có IMEI - tạo 1 dòng (THANH TOÁN):", {
            soLuong: item.soLuongMua
          });
          
          return [{
            maSKU: item.maSKU,
            maSKUPhuKien: item.maSKUPhuKien,
            imeiId: null,
            soLuong: item.soLuongMua, // ✅ Có thể > 1 nếu không có IMEI
            gia: item.gia,
            loaiSanPham: isSanPham ? "sanpham" : "phukien"
          }];
        }
      })
    };

    // Gọi API thanh toán đơn hàng
    const { thanhToanDonHang } = await import('@/service/api');
    const result = await thanhToanDonHang(orderData);

    // Cập nhật đơn hàng hiện tại với mã đơn hàng từ server
    const donHangHienTai = danhSachDonHang.value.find(
      (dh) => dh.id === donHangHienTaiId.value
    );

    // ✅ QUAN TRỌNG: Xóa đơn hàng khỏi bộ nhớ web sau khi thanh toán thành công
    console.log("🗑️ Xóa đơn hàng khỏi bộ nhớ web sau khi thanh toán thành công");
    
    // Xóa đơn hàng khỏi danh sách
    const index = danhSachDonHang.value.findIndex((dh) => dh.id === donHangHienTaiId.value);
    if (index !== -1) {
      danhSachDonHang.value.splice(index, 1);
    }
    
    // Reset đơn hàng hiện tại
    donHangHienTaiId.value = null;
    
    // Xóa giỏ hàng (KHÔNG cập nhật trạng thái IMEI vì backend đã xử lý)
    await xoaToanBoGioHang(true);
    
    // Lưu vào localStorage
    saveToLocalStorage();

    console.log("✅ Đã thanh toán đơn hàng:", result);
    
    // Kiểm tra xem còn đơn hàng nào không
    if (danhSachDonHang.value.length > 0) {
      // Chuyển sang đơn hàng khác
      const donHangKhac = danhSachDonHang.value[0]; // Lấy đơn hàng đầu tiên
      console.log("🔄 Chuyển sang đơn hàng khác:", donHangKhac.id);
      await chonDonHang(donHangKhac.id);
    } else {
      // Không còn đơn hàng nào, để trống như hình ảnh
      console.log("🆕 Không còn đơn hàng nào - để trống như hình ảnh");
    }
    
    alert("✅ Đã thanh toán đơn hàng thành công!\n\nĐã trừ kho và IMEI đã chuyển sang trạng thái 'Đã bán'.");
    
  } catch (error) {
    console.error("❌ Lỗi khi thanh toán đơn hàng:", error);
    alert("❌ Lỗi khi thanh toán đơn hàng: " + error.message);
  }
}

// Xử lý in hóa đơn
function handlePrint() {
  console.log("🖨 In hóa đơn...");
  // TODO: In hóa đơn
}

// Tạo đơn hàng mới
async function handleTaoDonHangMoi() {
  // Kiểm tra giới hạn 10 đơn hàng chưa lưu
  const donHangChuaLuu = danhSachDonHang.value.filter(
    (dh) => dh.trangThai === "draft"
  );
  if (donHangChuaLuu.length >= 10) {
    alert(
      "⚠️ Đã đạt giới hạn tối đa 10 đơn hàng chưa lưu!\n\nVui lòng lưu hoặc xóa một đơn hàng chưa lưu trước khi tạo đơn mới."
    );
    return;
  }

  if (gioHang.value.length > 0) {
    const confirmCreate = confirm(
      "⚠️ Bạn có chắc muốn tạo đơn hàng mới?\n\nĐơn hàng hiện tại sẽ được lưu tự động."
    );
    if (!confirmCreate) return;

    // Lưu đơn hàng hiện tại trước khi tạo mới
    console.log("💾 Lưu đơn hàng hiện tại trước khi tạo mới...");
    capNhatDonHangHienTai();
  }

  // Tạo đơn hàng mới
  await taoDonHangMoiTuDong();

  // Xóa giỏ hàng hiện tại
  await xoaToanBoGioHang();

  console.log("🆕 Đã tạo đơn hàng mới:", donHangHienTaiId.value);
  console.log("📦 Đơn hàng mới có:", 0, "sản phẩm");
  alert(
    "✅ Đã tạo đơn hàng mới!\n\nBạn có thể bắt đầu thêm sản phẩm cho khách hàng mới."
  );
}


// Xóa tất cả đơn hàng
async function xoaTatCaDonHang() {
  if (
    confirm(
      "⚠️ Bạn có chắc muốn xóa TẤT CẢ đơn hàng?\n\nHành động này không thể hoàn tác!\n\nTất cả IMEI trong các đơn hàng sẽ được chuyển về trạng thái 'Còn hàng'."
    )
  ) {
    // ✅ QUAN TRỌNG: Cập nhật trạng thái tất cả IMEI trong tất cả đơn hàng về "còn hàng" trước khi xóa
    console.log("🔄 Cập nhật trạng thái IMEI của tất cả đơn hàng trước khi xóa...");
    
    const allImeis = [];
    danhSachDonHang.value.forEach(donHang => {
      if (donHang.gioHang && donHang.gioHang.length > 0) {
        donHang.gioHang.forEach(item => {
          if (item.imeiList && Array.isArray(item.imeiList) && item.imeiList.length > 0) {
            const imeiNumbers = item.imeiList.map(imei => imei.imei || imei);
            allImeis.push(...imeiNumbers);
          }
        });
      }
    });

    if (allImeis.length > 0) {
      try {
        console.log(`🔄 Cập nhật ${allImeis.length} IMEI về trạng thái "còn hàng"...`);
        const { setImeiToStock } = await import("@/service/api.js");
        await setImeiToStock(allImeis);
        console.log("✅ Đã cập nhật tất cả IMEI về trạng thái 'còn hàng'");
      } catch (error) {
        console.error("❌ Lỗi khi cập nhật trạng thái IMEI:", error);
        // Vẫn tiếp tục xóa đơn hàng dù có lỗi
      }
    } else {
      console.log("ℹ️ Không có IMEI nào để cập nhật");
    }

    danhSachDonHang.value = [];
    donHangHienTaiId.value = null;
    await xoaToanBoGioHang();
    saveToLocalStorage();
    console.log("🗑️ Đã xóa tất cả đơn hàng");
    alert("✅ Đã xóa tất cả đơn hàng!\n\nTất cả IMEI đã được chuyển về trạng thái 'Còn hàng'.");
  }
}

// Chọn đơn hàng
async function chonDonHang(donHangId) {
  const donHang = danhSachDonHang.value.find((dh) => dh.id === donHangId);
  if (!donHang) return;

  console.log("🔄 Bắt đầu chuyển sang đơn hàng:", donHangId);
  console.log(
    "📦 Đơn hàng có",
    donHang.gioHang ? donHang.gioHang.length : 0,
    "sản phẩm"
  );

  // ✅ QUAN TRỌNG: Luôn lưu đơn hàng hiện tại trước khi chuyển
  if (donHangHienTaiId.value && donHangHienTaiId.value !== donHangId) {
    console.log("💾 Tự động lưu đơn hàng hiện tại trước khi chuyển:", donHangHienTaiId.value);
    console.log("📦 Giỏ hàng hiện tại có:", gioHang.value.length, "sản phẩm");
    
    // ✅ QUAN TRỌNG: Luôn lưu đơn hàng hiện tại, kể cả khi giỏ hàng trống
    // Đảm bảo tất cả thay đổi được lưu trước khi chuyển đơn hàng
    capNhatDonHangHienTai();
    console.log("✅ Đã tự động lưu đơn hàng hiện tại");
    console.log("ℹ️ IMEI của đơn hàng hiện tại vẫn ở trạng thái 'tạm giữ' - không giải phóng");
    
    // ✅ QUAN TRỌNG: Đảm bảo localStorage được cập nhật ngay lập tức
    saveToLocalStorage();
    console.log("💾 Đã lưu vào localStorage ngay lập tức");
  } else {
    console.log("⚠️ Không lưu đơn hàng hiện tại vì đang chuyển sang chính nó");
  }

  // ✅ QUAN TRỌNG: Set flag để tránh watcher cập nhật đơn hàng sai
  isSwitchingOrder.value = true;

  // Cập nhật đơn hàng hiện tại
  donHangHienTaiId.value = donHangId;

  // ✅ QUAN TRỌNG: Xóa giỏ hàng hiện tại trước khi load sản phẩm từ đơn hàng
  console.log("🗑️ Xóa giỏ hàng hiện tại trước khi load từ đơn hàng");
  console.log("🔍 DEBUG: Giỏ hàng trước khi xóa:", gioHang.value.length, "sản phẩm");
  await xoaToanBoGioHang(true); // Skip IMEI update khi chuyển đơn hàng
  console.log("🔍 DEBUG: Giỏ hàng sau khi xóa:", gioHang.value.length, "sản phẩm");

  // ✅ QUAN TRỌNG: Load sản phẩm từ đơn hàng được chọn nếu nó có sản phẩm
  if (donHang.gioHang && donHang.gioHang.length > 0) {
    console.log("📥 Đang load sản phẩm từ đơn hàng:", donHangId);
    console.log("📦 Số lượng sản phẩm cần load:", donHang.gioHang.length);
    console.log("🔍 DEBUG: Chi tiết sản phẩm trong đơn hàng:", donHang.gioHang);
    
    // ✅ QUAN TRỌNG: Không cập nhật IMEI khi load từ đơn hàng
    // IMEI đã được lưu với trạng thái "tạm giữ" khi đơn hàng được tạo
    // Chỉ cần load sản phẩm mà không thay đổi trạng thái IMEI
    console.log("ℹ️ Không cập nhật trạng thái IMEI khi load từ đơn hàng - IMEI đã ở trạng thái 'tạm giữ'");
    console.log("ℹ️ IMEI sẽ chỉ được giải phóng khi đơn hàng bị xóa hoặc thanh toán");
    
    for (let index = 0; index < donHang.gioHang.length; index++) {
      const item = donHang.gioHang[index];
      console.log(`📦 Load sản phẩm ${index + 1}:`, item);
      
      // Kiểm tra cấu trúc dữ liệu
      if (item && item.sanPham && item.sanPham.tenSanPham) {
        // Dạng 1: có thuộc tính sanPham
        console.log(`✅ Sản phẩm hợp lệ (dạng 1):`, item.sanPham.tenSanPham, "x", item.soLuongMua);
        await loadSanPhamTuDonHang(item.sanPham, item.soLuongMua, item.imeiList);
      } else if (item && item.tenSanPham) {
        // Dạng 2: sản phẩm đã được flatten
        console.log(`✅ Sản phẩm hợp lệ (dạng 2):`, item.tenSanPham, "x", item.soLuongMua);
        await loadSanPhamTuDonHang(item, item.soLuongMua, item.imeiList);
      } else {
        console.log(`❌ Sản phẩm không hợp lệ:`, item);
      }
    }
    
    console.log("✅ Đã load xong", donHang.gioHang.length, "sản phẩm vào giỏ hàng");
    console.log("🔍 DEBUG: Giỏ hàng sau khi load:", gioHang.value.length, "sản phẩm");
    console.log("🔍 DEBUG: Chi tiết giỏ hàng sau khi load:", gioHang.value);
  } else {
    console.log("⚠️ Đơn hàng không có sản phẩm - giỏ hàng sẽ trống");
    // ✅ QUAN TRỌNG: Đảm bảo giỏ hàng trống khi đơn hàng không có sản phẩm
    gioHang.value = [];
  }
  
  // Cập nhật thông tin khách hàng cho đơn hàng mới
  thongTinKhachHangHienTai.value = {
    ...thongTinKhachHangHienTai.value,
    ...donHang.thongTinKhachHang,
  };

  // Lưu vào localStorage
  saveToLocalStorage();

  // Reset flag sau khi hoàn thành chuyển đơn hàng
  isSwitchingOrder.value = false;

  console.log("🔄 Hoàn thành chuyển sang đơn hàng:", donHangId);
}


// Debug function để kiểm tra trạng thái IMEI
function debugImeiStatus() {
  console.log("🔍 DEBUG IMEI STATUS:");
  console.log("📦 Đơn hàng hiện tại:", donHangHienTaiId.value);
  console.log("🛒 Giỏ hàng có:", gioHang.value.length, "sản phẩm");
  
  if (gioHang.value.length > 0) {
    console.log("✅ IMEI nên ở trạng thái 5 (tạm giữ)");
    gioHang.value.forEach((item, index) => {
      if (item.imeiList && item.imeiList.length > 0) {
        console.log(`📱 Sản phẩm ${index + 1} có ${item.imeiList.length} IMEI`);
      }
    });
  } else {
    console.log("⚠️ IMEI nên ở trạng thái 1 (còn hàng) - vì giỏ hàng trống");
  }
}

// Cập nhật đơn hàng hiện tại với giỏ hàng
function capNhatDonHangHienTai() {
  if (!donHangHienTaiId.value) return;

  const donHang = danhSachDonHang.value.find(
    (dh) => dh.id === donHangHienTaiId.value
  );
  if (donHang) {
    console.log("💾 Đang cập nhật đơn hàng:", donHang.id);
    console.log("📦 Giỏ hàng hiện tại có:", gioHang.value.length, "sản phẩm");

    // ✅ QUAN TRỌNG: Luôn cập nhật giỏ hàng, kể cả khi trống
    donHang.gioHang = JSON.parse(JSON.stringify(gioHang.value));
    donHang.tongTien = tongThanhToan.value;
    
    if (gioHang.value.length > 0) {
      console.log("💾 Đã lưu", donHang.gioHang.length, "sản phẩm vào đơn hàng:", donHang.id);
      console.log("🔍 DEBUG: Cấu trúc gioHang sau khi lưu:", donHang.gioHang);
      console.log("🔍 DEBUG: Chi tiết từng sản phẩm được lưu:");
      donHang.gioHang.forEach((item, index) => {
        console.log(`  Sản phẩm ${index + 1}:`, {
          tenSanPham: item.tenSanPham || item.tenPhuKien,
          maSKU: item.maSKU || item.maSKUPhuKien,
          soLuongMua: item.soLuongMua,
          imeiCount: item.imeiList?.length || 0,
          keys: Object.keys(item)
        });
      });
      
      // ✅ Debug: Kiểm tra IMEI được lưu
      console.log("💾 Debug IMEI được lưu:", donHang.gioHang.map(item => ({
        sku: item.maSKU,
        imeiCount: item.imeiList?.length || 0,
        imeiList: item.imeiList
      })));
    } else {
      console.log("📦 Giỏ hàng trống - cập nhật đơn hàng với giỏ hàng trống");
    }
    
    donHang.ngayCapNhat = new Date().toISOString();
    
    // Lưu thông tin khách hàng hiện tại
    donHang.thongTinKhachHang = { ...thongTinKhachHangHienTai.value };

    console.log(
      "📦 Đơn hàng sau khi cập nhật có:",
      donHang.gioHang.length,
      "sản phẩm"
    );

    saveToLocalStorage();
    console.log("✅ Đã cập nhật đơn hàng hiện tại:", donHang.id);
    console.log("💾 Đã lưu vào localStorage");
    console.log("🔍 DEBUG: Kiểm tra đơn hàng sau khi lưu:", donHang);
  }
}

// Xóa đơn hàng
async function xoaDonHang(donHangId) {
  const donHang = danhSachDonHang.value.find((dh) => dh.id === donHangId);
  if (!donHang) return;

  const confirmDelete = confirm(
    `⚠️ Bạn có chắc muốn xóa đơn hàng này?\n\nĐơn hàng sẽ bị xóa vĩnh viễn và không thể khôi phục.\n\nTất cả IMEI trong đơn hàng sẽ được chuyển về trạng thái "Còn hàng".`
  );
  if (!confirmDelete) return;

  // ✅ QUAN TRỌNG: Cập nhật trạng thái tất cả IMEI trong đơn hàng về "còn hàng" trước khi xóa
  if (donHang.gioHang && donHang.gioHang.length > 0) {
    console.log("🔄 Cập nhật trạng thái IMEI của đơn hàng trước khi xóa...");
    
    // Thu thập tất cả IMEI từ đơn hàng
    const allImeis = [];
    donHang.gioHang.forEach(item => {
      if (item.imeiList && Array.isArray(item.imeiList) && item.imeiList.length > 0) {
        const imeiNumbers = item.imeiList.map(imei => imei.imei || imei);
        allImeis.push(...imeiNumbers);
      }
    });

    if (allImeis.length > 0) {
      try {
        console.log(`🔄 Cập nhật ${allImeis.length} IMEI về trạng thái "còn hàng"...`);
        const { setImeiToStock } = await import("@/service/api.js");
        await setImeiToStock(allImeis);
        console.log("✅ Đã cập nhật tất cả IMEI về trạng thái 'còn hàng'");
      } catch (error) {
        console.error("❌ Lỗi khi cập nhật trạng thái IMEI:", error);
        // Vẫn tiếp tục xóa đơn hàng dù có lỗi
      }
    } else {
      console.log("ℹ️ Đơn hàng không có IMEI nào để cập nhật");
    }
  }

  // Xóa đơn hàng khỏi danh sách
  const index = danhSachDonHang.value.findIndex((dh) => dh.id === donHangId);
  danhSachDonHang.value.splice(index, 1);

  // Nếu đang xử lý đơn hàng bị xóa, chuyển về đơn hàng khác hoặc xóa giỏ hàng
  if (donHangHienTaiId.value === donHangId) {
    if (danhSachDonHang.value.length > 0) {
      // Chuyển về đơn hàng đầu tiên
      chonDonHang(danhSachDonHang.value[0].id);
    } else {
      // Không còn đơn hàng nào, xóa giỏ hàng
      donHangHienTaiId.value = null;
      await xoaToanBoGioHang();
    }
  }

  // Lưu vào localStorage
  saveToLocalStorage();

  console.log("🗑️ Đã xóa đơn hàng:", donHangId);
  alert("✅ Đã xóa đơn hàng thành công!\n\nTất cả IMEI đã được chuyển về trạng thái 'Còn hàng'.");
}

// Lấy số thứ tự đơn hàng hiện tại
function getCurrentOrderNumber() {
  if (!donHangHienTaiId.value) return 0;
  const index = danhSachDonHang.value.findIndex(
    (dh) => dh.id === donHangHienTaiId.value
  );
  return index + 1;
}

// Lấy trạng thái đơn hàng hiện tại
function getCurrentOrderStatus() {
  if (!donHangHienTaiId.value) return "draft";
  const donHang = danhSachDonHang.value.find(
    (dh) => dh.id === donHangHienTaiId.value
  );
  return donHang ? donHang.trangThai : "draft";
}

// Đếm số đơn hàng chưa lưu (không tính đơn hàng đã ẩn)
function getDonHangChuaLuuCount() {
  return danhSachDonHang.value.filter((dh) => dh.trangThai === "draft" && !dh.hidden).length;
}

// Lấy mã đơn hàng hiện tại
function getCurrentOrderMaDonHang() {
  if (!donHangHienTaiId.value) return null;
  const donHang = danhSachDonHang.value.find(
    (dh) => dh.id === donHangHienTaiId.value
  );
  return donHang ? donHang.maDonHang : null;
}

// Xử lý khi chọn sản phẩm từ modal
async function handleChonSanPham(data) {
  console.log("✅ Nhận dữ liệu:", data);

  const { sanPham, soLuong, imeiList } = data;

  // Kiểm tra nếu chưa có đơn hàng nào, tự động tạo đơn hàng mới
  if (!donHangHienTaiId.value) {
    console.log("🆕 Chưa có đơn hàng, tự động tạo đơn hàng mới...");
    await taoDonHangMoiTuDong();
  }

  // Thêm sản phẩm vào giỏ hàng
  await themSanPham(sanPham, soLuong, imeiList);

  // ✅ TỰ ĐỘNG LƯU vào localStorage ngay sau khi thêm sản phẩm
  capNhatDonHangHienTai();
  console.log("💾 Đã tự động lưu sản phẩm vào trình duyệt");

  // Đóng modal
  isModalOpen.value = false;

  console.log(`✅ Đã thêm ${soLuong} ${sanPham.tenSanPham} vào giỏ hàng!`);
  if (imeiList && imeiList.length > 0) {
    console.log("📱 Với IMEIs:", imeiList);
  }
}

// Tạo đơn hàng mới tự động (không hiển thị thông báo)
async function taoDonHangMoiTuDong() {
  console.log("🆕 Tự động tạo đơn hàng mới...");
  
  // Tạo đơn hàng mới
  const donHangMoi = {
    id: Date.now().toString(),
    maDonHang: null, // Mã đơn hàng từ SQL (null nếu chưa lưu)
    gioHang: [],
    tongTien: 0,
    trangThai: "draft",
    ngayTao: new Date().toISOString(),
    ngayCapNhat: new Date().toISOString(),
    // ✅ THÊM: Thông tin khách hàng trống cho đơn hàng mới
    thongTinKhachHang: {
      tenKhachHang: "",
      soDienThoai: "",
      diaChi: "",
      customerInfo: null
    }
  };

  danhSachDonHang.value.push(donHangMoi);
  donHangHienTaiId.value = donHangMoi.id;

  // ✅ RESET thông tin khách hàng hiển thị
  thongTinKhachHangHienTai.value = {
    tenKhachHang: "",
    soDienThoai: "",
    diaChi: "",
    customerInfo: null
  };

  // Lưu vào localStorage
  saveToLocalStorage();

  console.log("✅ Đã tự động tạo đơn hàng mới:", donHangMoi.id);
  console.log("🔄 Đã reset thông tin khách hàng cho đơn hàng mới");
}

// Cập nhật thông tin khách hàng
function capNhatThongTinKhachHang(customerData) {
  console.log("👤 Cập nhật thông tin khách hàng:", customerData);
  
  if (!donHangHienTaiId.value) {
    console.log("⚠️ Chưa có đơn hàng hiện tại");
    return;
  }

  const donHang = danhSachDonHang.value.find(
    (dh) => dh.id === donHangHienTaiId.value
  );
  
  if (donHang) {
    // Lưu thông tin khách hàng vào đơn hàng
    donHang.thongTinKhachHang = {
      tenKhachHang: customerData.tenKhachHang || "",
      soDienThoai: customerData.soDienThoai || "",
      diaChi: customerData.diaChi || "",
      customerInfo: customerData.customerInfo || null
    };
    
    // Cập nhật thông tin hiển thị
    thongTinKhachHangHienTai.value = { ...donHang.thongTinKhachHang };
    
    // Lưu vào localStorage
    saveToLocalStorage();
    
    console.log("✅ Đã lưu thông tin khách hàng cho đơn hàng:", donHang.id);
  }
}
</script>

<style scoped>
/* Tổng thể trang */
.banhang-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 12px;
  background: #f5f7fa;
  font-family: "Inter", sans-serif;
  /* ✅ Bỏ thanh cuộn dọc toàn trang */
  overflow-y: hidden;
}

.title {
  text-align: center;
  color: #333;
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 24px;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Quản lý đơn hàng */
.order-management {
  /* Layout */
  padding: 12px; /* ✅ Giảm từ 16px */
  margin-bottom: 12px; /* ✅ Giảm từ 16px */
  
  /* Giao diện */
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.order-management-row {
  /* Layout */
  display: flex;
  align-items: flex-start;
  gap: 12px; /* ✅ Giảm từ 20px */
  justify-content: space-between;
}

.order-controls {
  display: flex;
  justify-content: flex-start;
  flex-shrink: 0;
}

.btn-create-new-order {
  /* Layout */
  padding: 8px 16px; /* ✅ Giảm từ 12px 24px */
  
  /* Giao diện */
  background: linear-gradient(90deg, #8b5cf6, #7c3aed);
  color: white;
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(139, 92, 246, 0.3);
  
  /* Typography */
  font-size: 13px; /* ✅ Giảm từ 14px */
  font-weight: 600;
  
  /* Tương tác */
  cursor: pointer;
  transition: all 0.25s ease;
}

.btn-create-new-order:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(139, 92, 246, 0.4);
}

/* Đơn hàng đang được chọn */
.current-order-info {
  /* Layout */
  padding: 8px;
  margin-bottom: 12px;
  flex: 0 0 250px; /* ✅ Hẹp hơn */
  max-width: 250px; /* ✅ Hẹp hơn */
  
  /* Giao diện */
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border: 2px solid #3b82f6;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
  
  /* ✅ Nhỏ hơn về chiều cao */
  min-height: 60px; /* ✅ Giảm từ 80px */
  max-height: 80px; /* ✅ Giảm từ 100px */
}

.current-order-header {
  /* Layout */
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px; /* ✅ Giảm khoảng cách */
  
  /* ✅ Nhỏ hơn về chiều cao */
  min-height: 20px;
}

.current-order-header h4 {
  /* Layout */
  margin: 0;
  white-space: nowrap;
  
  /* Typography */
  color: #1e40af;
  font-size: 11px; /* ✅ Nhỏ hơn */
  font-weight: 600;
}

.current-order-number {
  background: #3b82f6;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.current-order-details {
  /* Layout */
  display: flex;
  gap: 6px; /* ✅ Giảm khoảng cách */
  flex-wrap: wrap;
  justify-content: flex-start;
  
  /* Typography */
  font-size: 10px; /* ✅ Nhỏ hơn */
  
  /* ✅ Nhỏ hơn về chiều cao */
  min-height: 30px;
  max-height: 40px;
}

.order-detail-item {
  /* Layout */
  display: flex;
  align-items: center;
  gap: 3px; /* ✅ Giảm khoảng cách */
  
  /* ✅ Nhỏ hơn về chiều cao */
  min-height: 16px;
}

.detail-label {
  /* Typography */
  font-size: 10px; /* ✅ Nhỏ hơn */
  color: #64748b;
  font-weight: 500;
}

.detail-value {
  /* Typography */
  font-size: 10px; /* ✅ Nhỏ hơn */
  font-weight: 600;
  color: #1e293b;
}

.detail-value.draft {
  color: #f59e0b;
}

.detail-value.saved {
  color: #10b981;
}

/* Layout chính */
.content {
  /* Layout */
  display: grid;
  grid-template-columns: 28% 72%;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

/* Panel trái */
.left-panel {
  /* Layout */
  display: flex;
  flex-direction: column;
  gap: 12px;
  
  /* Kích thước */
  width: 100%;
  min-width: 0;
  flex-shrink: 0;
  
  /* Cuộn */
  overflow-y: auto;
  max-height: 85vh;
}

/* Đã di chuyển CSS cho order-controls lên trên */

/* Trạng thái trống */
.empty-state {
  /* Layout */
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 10px; /* ✅ Giảm một nửa */
  margin: 10px 0; /* ✅ Giảm một nửa */
  
  /* Giao diện */
  background: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
}

.empty-state-content {
  /* Layout */
  text-align: center;
  
  /* Giao diện */
  color: #64748b;
}

.empty-icon {
  /* Layout */
  font-size: 24px; /* ✅ Giảm một nửa */
  margin-bottom: 8px; /* ✅ Giảm một nửa */
}

.empty-state h3 {
  /* Layout */
  margin: 0 0 4px 0; /* ✅ Giảm một nửa */
  
  /* Typography */
  color: #374151;
  font-size: 14px; /* ✅ Giảm một nửa */
  font-weight: 600;
}

.empty-state p {
  /* Layout */
  margin: 0;
  
  /* Typography */
  color: #64748b;
  font-size: 12px; /* ✅ Giảm một nửa */
}

/* Danh sách đơn hàng */
.orders-section {
  flex: 1;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 12px;
  /* ✅ Đảm bảo không có thanh cuộn dọc, cho phép cuộn ngang */
  overflow-y: hidden;
  overflow-x: auto;
  /* ✅ Mở rộng để có nhiều không gian hơn */
  min-width: 0;
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.orders-header h4 {
  margin: 0;
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

.btn-toggle-orders {
  padding: 4px 8px;
  background: #e5e7eb;
  color: #374151;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-toggle-orders:hover {
  background: #d1d5db;
}

.orders-list {
  /* Layout */
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  gap: 6px; /* ✅ Giảm khoảng cách */
  
  /* Kích thước */
  height: 80px; /* ✅ Giảm thêm chiều cao */
  width: 100%; /* ✅ Mở rộng đến hết phần "Xóa tất cả" */
  max-width: none; /* ✅ Không giới hạn chiều rộng */
  
  /* Padding */
  padding: 4px 0 8px 0;
  
  /* Cuộn - Chỉ cuộn khi có nhiều hơn 4 đơn hàng */
  overflow-x: auto;
  overflow-y: hidden;
  scroll-behavior: smooth;
  
  /* Scrollbar */
  scrollbar-width: thin;
  scrollbar-color: #cbd5e0 #f7fafc;
}

/* ✅ Custom scrollbar cho Webkit browsers - THANH CUỘN NGANG */
.orders-list::-webkit-scrollbar {
  height: 8px; /* ✅ Chiều cao thanh cuộn ngang */
  width: auto; /* ✅ Chiều rộng tự động */
}

.orders-list::-webkit-scrollbar-track {
  background: #f7fafc;
  border-radius: 4px;
}

.orders-list::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 4px;
  transition: background 0.2s ease;
}

.orders-list::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* ✅ Đảm bảo thanh cuộn ngang hiển thị rõ ràng */
.orders-list::-webkit-scrollbar-corner {
  background: #f7fafc;
}

.order-item {
  /* Kích thước */
  width: calc(25% - 4.5px); /* ✅ Chia đều cho 4 đơn hàng (25% mỗi đơn) */
  min-width: 120px; /* ✅ Chiều rộng tối thiểu */
  max-width: calc(25% - 4.5px); /* ✅ Cố định chiều rộng */
  flex-shrink: 0; /* ✅ Không co lại */
  
  /* Giao diện */
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 4px; /* ✅ Giảm thêm */
  
  /* ✅ Giảm chiều cao */
  min-height: 60px; /* ✅ Giảm từ 80px */
  max-height: 80px; /* ✅ Giảm từ 100px */
  
  /* Tương tác */
  cursor: pointer;
  transition: all 0.2s ease;
}

.order-item:hover {
  border-color: #3b82f6;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1);
}

.order-item.active {
  border-color: #3b82f6;
  background: #eff6ff;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.15);
}

.order-info {
  /* Layout */
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px; /* ✅ Giảm thêm */
}

.order-actions {
  /* Layout */
  display: flex;
  align-items: center;
  gap: 4px; /* ✅ Giảm từ 8px */
}

.order-number {
  /* Typography */
  font-weight: 600;
  color: #374151;
  font-size: 11px; /* ✅ Giảm từ 13px */
}

.order-status {
  /* Layout */
  padding: 1px 4px; /* ✅ Giảm từ 2px 6px */
  border-radius: 3px; /* ✅ Giảm từ 4px */
  
  /* Typography */
  font-size: 9px; /* ✅ Giảm từ 11px */
  font-weight: 500;
}

.order-status.draft {
  background: #fef3c7;
  color: #92400e;
}

.order-status.saved {
  background: #d1fae5;
  color: #065f46;
}

.order-summary {
  /* Layout */
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px; /* ✅ Thêm margin */
  
  /* Typography */
  font-size: 10px; /* ✅ Giảm từ 12px */
  color: #6b7280;
}

.order-items {
  /* Typography */
  font-weight: 500;
}

.order-total {
  /* Typography */
  font-weight: 600;
  color: #374151;
}

.order-code {
  /* Layout */
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 2px; /* ✅ Giảm từ 4px */
  padding-top: 2px; /* ✅ Giảm từ 4px */
  
  /* Typography */
  font-size: 9px; /* ✅ Giảm từ 11px */
  color: #6b7280;
  border-top: 1px solid #f3f4f6;
}

.code-label {
  font-weight: 500;
  color: #9ca3af;
}

.code-value {
  font-weight: 600;
  color: #374151;
  font-family: "Courier New", monospace;
}

.btn-delete-order {
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  width: 24px;
  height: 24px;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.btn-delete-order:hover {
  background: #c82333;
  transform: scale(1.1);
}

.info-row {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
}

.info-section.small {
  background: #fff;
  padding: 8px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.info-section.full {
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  width: 100%;
  min-height: 120px;
}

/* Panel phải */
.right-panel {
  /* Layout */
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  
  /* Giao diện */
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* Thanh thêm sản phẩm */
.add-product-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-bottom: 1px solid #e9ecef;
  background: #f8fafc;
}

.btn-add-product {
  padding: 8px 14px;
  background: linear-gradient(90deg, #20c997, #2ecc71);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 2px 6px rgba(32, 201, 151, 0.3);
}

.btn-add-product:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(32, 201, 151, 0.4);
}

.btn-create-order {
  padding: 8px 14px;
  background: linear-gradient(90deg, #8b5cf6, #7c3aed);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 2px 6px rgba(139, 92, 246, 0.3);
  margin-left: 8px;
}

.btn-create-order:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(139, 92, 246, 0.4);
}

/* Thông tin tổng kết */
.summary-info {
  /* Layout */
  display: flex;
  gap: 4px;
  justify-content: space-between;
  padding: 8px;
  margin-top: 4px;
  
  /* Giao diện */
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  
  /* Đảm bảo hiển thị */
  min-height: 60px;
  flex-shrink: 0;
  
  /* Bỏ thanh cuộn ngang */
  overflow-x: hidden;
  flex-wrap: wrap;
}

.summary-row {
  /* Layout */
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 6px;
  text-align: center;
  
  /* Giao diện */
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  min-height: 50px;
  
  /* Tương tác */
  transition: all 0.2s ease;
}

.summary-row:hover {
  border-color: #3b82f6;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1);
}

.summary-row.highlight {
  background: #eff6ff;
  border: 2px solid #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

.summary-label {
  color: #64748b;
  font-size: 11px;
  font-weight: 500;
  margin-bottom: 2px;
  text-align: center;
  line-height: 1.2;
}

.summary-value {
  color: #1e293b;
  font-size: 14px;
  font-weight: 700;
  text-align: center;
  line-height: 1.2;
}

.summary-row.highlight .summary-label {
  color: #1e40af;
  font-weight: 600;
}

.summary-row.highlight .summary-value {
  color: #1e40af;
  font-size: 16px;
  font-weight: 800;
}

/* Nút hành động */
.action-buttons {
  display: flex;
  gap: 8px;
  margin-top: 16px;
  justify-content: center;
}

.btn-save,
.btn-submit,
.btn-print {
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}

.btn-save {
  background: linear-gradient(90deg, #3b82f6, #2563eb);
  color: white;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
}

.btn-save:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(59, 130, 246, 0.4);
}

.btn-submit {
  background: linear-gradient(90deg, #10b981, #059669);
  color: white;
  box-shadow: 0 2px 6px rgba(16, 185, 129, 0.3);
}

.btn-submit:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(16, 185, 129, 0.4);
}

.btn-print {
  background: linear-gradient(90deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 2px 6px rgba(245, 158, 11, 0.3);
}

.btn-print:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(245, 158, 11, 0.4);
}

/* Nút xóa tất cả đơn hàng */
.btn-clear-all {
  background: linear-gradient(90deg, #dc2626, #b91c1c);
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(220, 38, 38, 0.3);
  margin-left: 8px;
}

.btn-clear-all:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(220, 38, 38, 0.4);
}

/* Bảng sản phẩm */
.table-wrapper {
  flex: 1;
  /* ✅ Bỏ thanh cuộn dọc */
  overflow-y: hidden;
  padding: 8px;
}

/* Thống kê */
.statistics-bar {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  background: #f9fafb;
  border-top: 1px solid #dee2e6;
}

.stat-item {
  padding: 12px;
  text-align: center;
  border-right: 1px solid #e9ecef;
  background: #fff;
}

.stat-item:last-child {
  border-right: none;
}

.stat-label {
  font-size: 13px;
  color: #6c757d;
  display: block;
}

.stat-value {
  font-size: 15px;
  font-weight: 700;
  color: #212529;
  margin-top: 3px;
}

.stat-item.highlight {
  background: #fff8e1;
}

.stat-item.highlight .stat-value {
  color: #e67e22;
}

/* Nút hành động */
.action-buttons {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  padding: 10px;
  border-top: 1px solid #dee2e6;
  background: #f8fafc;
}

.action-buttons button {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s;
}

.btn-save {
  background: #6c757d;
  color: #fff;
}
.btn-save:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.btn-submit {
  background: #28a745;
  color: #fff;
}
.btn-submit:hover {
  background: #218838;
  transform: translateY(-1px);
}

.btn-print {
  background: #007bff;
  color: #fff;
}
.btn-print:hover {
  background: #0056b3;
  transform: translateY(-1px);
}
</style>
