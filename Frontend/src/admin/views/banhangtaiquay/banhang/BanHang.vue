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
        <div
          v-if="donHangHienTaiId && danhSachDonHang.length > 0"
          class="current-order-info"
        >
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
              📋 Danh sách đơn hàng ({{
                danhSachDonHang.filter((dh) => !dh.hidden).length
              }}) - Chưa lưu:
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
              v-for="(donHang, index) in danhSachDonHang.filter(
                (dh) => !dh.hidden
              )"
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
          ref="thanhToanRef"
          @save="handleSave"
          @submit="handleSubmit"
        />
      </div>

      <!-- Panel phải -->
      <div class="right-panel">
        <div class="add-product-bar">
          <button class="btn-add-product" @click="moModalChonSanPham">
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

import {
  luuDonHang,
  thanhToanDonHang,
  xoaDonHangLuu,
  setImeiToStock,
  getCurrentUser,
} from "../../../../service/api.js";

const isModalOpen = ref(false);

// Quản lý đơn hàng
const danhSachDonHang = ref([]);
const donHangHienTaiId = ref(null);

// Ref để truy cập component ThanhToan
const thanhToanRef = ref(null);

// Quản lý thông tin khách hàng
const thongTinKhachHangHienTai = ref({
  tenKhachHang: "",
  soDienThoai: "",
  diaChi: "",
  customerInfo: null,
});

// Sử dụng composable giỏ hàng
const {
  themSanPham,
  loadSanPhamTuDonHang,
  xoaToanBoGioHang,
  capNhatThanhTien,
  tongSoLuong,
  tongTienHang,
  tongThanhToan,
  gioHang,
  isSwitchingOrder,
} = useGioHangBanHangTaiQuay();

// Khởi tạo từ localStorage
function initFromLocalStorage() {
  const savedOrders = localStorage.getItem("danhSachDonHang");
  if (savedOrders) {
    danhSachDonHang.value = JSON.parse(savedOrders);
    console.log("🔍 DEBUG initFromLocalStorage - Đã load danhSachDonHang:", danhSachDonHang.value.length, "đơn hàng");
  }
  
  // ✅ QUAN TRỌNG: Chỉ load đơn hàng từ localStorage nếu KHÔNG có loadOrder trong URL
  // Vì nếu có loadOrder trong URL, chúng ta sẽ load đơn hàng từ URL thay vì từ localStorage
  const urlParams = new URLSearchParams(window.location.search);
  const loadOrder = urlParams.get("loadOrder");
  
  if (!loadOrder) {
    // Chỉ load đơn hàng từ localStorage nếu không có loadOrder trong URL
    const currentOrderId = localStorage.getItem("donHangHienTaiId");
    if (currentOrderId) {
      donHangHienTaiId.value = currentOrderId;
      loadDonHangHienTai();
      console.log("🔍 DEBUG initFromLocalStorage - Đã load đơn hàng từ localStorage:", currentOrderId);
    }
  } else {
    console.log("🔍 DEBUG initFromLocalStorage - Bỏ qua load từ localStorage vì có loadOrder trong URL:", loadOrder);
  }
}

// Lưu vào localStorage
function saveToLocalStorage() {
  localStorage.setItem(
    "danhSachDonHang",
    JSON.stringify(danhSachDonHang.value)
  );
  localStorage.setItem("donHangHienTaiId", donHangHienTaiId.value || "");
}

//  Lắng nghe event xóa sản phẩm để cập nhật đơn hàng ngay lập tức
function setupCartItemDeletedListener() {
  window.addEventListener("cart-item-deleted", (event) => {
    const { maSKU, remainingItems } = event.detail;
    // Cập nhật đơn hàng ngay lập tức khi xóa sản phẩm
    if (donHangHienTaiId.value) {
      capNhatDonHangHienTai();
    }
  });
}

// Load đơn hàng hiện tại
async function loadDonHangHienTai() {
  if (!donHangHienTaiId.value) return;

  const donHang = danhSachDonHang.value.find(
    (dh) => dh.id === donHangHienTaiId.value
  );
  if (donHang) {
    // ✅ SỬA LỖI: Xóa hoàn toàn giỏ hàng trước khi load (đảm bảo không cộng dồn)
    gioHang.value = [];

    // ✅ SỬA LỖI: Load sản phẩm từ đơn hàng với xử lý cấu trúc dữ liệu đúng
    if (donHang.gioHang && donHang.gioHang.length > 0) {
      for (let index = 0; index < donHang.gioHang.length; index++) {
        const item = donHang.gioHang[index];

        // Kiểm tra cấu trúc dữ liệu (tương tự như trong chonDonHang)
        if (item && item.sanPham && item.sanPham.tenSanPham) {
          // Dạng 1: có thuộc tính sanPham
          await loadSanPhamTuDonHang(
            item.sanPham,
            item.soLuongMua,
            item.imeiList
          );
        } else if (item && item.tenSanPham) {
          // Dạng 2: sản phẩm đã được flatten
          await loadSanPhamTuDonHang(item, item.soLuongMua, item.imeiList);
        }
      }
    } else {
      // ✅ Đảm bảo giỏ hàng trống khi đơn hàng không có sản phẩm
      gioHang.value = [];
    }

    if (
      donHang.thongTinKhachHang &&
      (donHang.thongTinKhachHang.tenKhachHang ||
        donHang.thongTinKhachHang.soDienThoai ||
        donHang.thongTinKhachHang.diaChi)
    ) {
      thongTinKhachHangHienTai.value = { ...donHang.thongTinKhachHang };
    } else {
      // Reset thông tin khách hàng nếu đơn hàng chưa có
      thongTinKhachHangHienTai.value = {
        tenKhachHang: "",
        soDienThoai: "",
        diaChi: "",
        customerInfo: null,
      };
    }
  }
}

// Khởi tạo khi component mount
initFromLocalStorage();
setupCartItemDeletedListener();

//  Xử lý URL parameter loadOrder
function handleLoadOrderFromURL() {
  const urlParams = new URLSearchParams(window.location.search);
  const loadOrder = urlParams.get("loadOrder");

  if (loadOrder) {
    // ✅ QUAN TRỌNG: Nếu có loadOrder từ URL, KHÔNG load đơn hàng từ localStorage trước
    // Vì chúng ta muốn load đơn hàng từ URL (đơn B), không phải đơn hàng đã lưu trong localStorage (có thể là đơn A)
    // Reset donHangHienTaiId để tránh lưu nhầm thông tin khi chuyển đơn hàng
    donHangHienTaiId.value = null;
    
    // Tìm đơn hàng trong localStorage
    const donHang = danhSachDonHang.value.find((dh) => dh.id === loadOrder);
    if (donHang) {
      console.log(`🔍 DEBUG handleLoadOrderFromURL - Đang load đơn hàng từ URL: ${loadOrder}`);
      chonDonHang(loadOrder);
    } else {
      // ✅ Cải thiện: Thông báo nếu không tìm thấy đơn hàng
      console.warn(
        `⚠️ Không tìm thấy đơn hàng với ID: ${loadOrder} trong localStorage`
      );
    }
    // Xóa parameter khỏi URL
    const newUrl = window.location.pathname;
    window.history.replaceState({}, document.title, newUrl);
  }
}

// Gọi function xử lý URL parameter
handleLoadOrderFromURL();

// Tự động cập nhật đơn hàng khi giỏ hàng thay đổi (với debounce)
let watcherTimeout = null;
watch(
  gioHang,
  (newGioHang, oldGioHang) => {
    // Cập nhật khi có đơn hàng hiện tại và có bất kỳ thay đổi nào
    if (donHangHienTaiId.value) {
      const hasLengthChange = newGioHang.length !== oldGioHang?.length;
      const hasContentChange =
        JSON.stringify(newGioHang) !== JSON.stringify(oldGioHang);

      if (hasLengthChange || hasContentChange) {
        //  QUAN TRỌNG: Chỉ cập nhật khi không phải đang chuyển đơn hàng
        if (!isSwitchingOrder.value) {
          //  QUAN TRỌNG: Đặc biệt xử lý khi giỏ hàng trống
          if (newGioHang.length === 0) {
            capNhatDonHangHienTai();
          } else {
            // Debounce cho trường hợp có sản phẩm
            if (watcherTimeout) {
              clearTimeout(watcherTimeout);
            }
            watcherTimeout = setTimeout(() => {
              capNhatDonHangHienTai();
            }, 100); // 100ms debounce
          }
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
async function handleSave(paymentData = {}) {
  if (gioHang.value.length === 0) {
    alert("⚠️ Giỏ hàng trống! Vui lòng thêm sản phẩm.");
    return;
  }

  // ✅ VALIDATION: Kiểm tra số điện thoại khách hàng
  if (
    !thongTinKhachHangHienTai.value.soDienThoai ||
    thongTinKhachHangHienTai.value.soDienThoai.trim() === ""
  ) {
    alert("⚠️ Vui lòng nhập số điện thoại khách hàng!");
    return;
  }

  // ✅ VALIDATION: Kiểm tra tên khách hàng bắt buộc
  if (
    !thongTinKhachHangHienTai.value.tenKhachHang ||
    thongTinKhachHangHienTai.value.tenKhachHang.trim() === ""
  ) {
    alert("⚠️ Vui lòng nhập TÊN khách hàng trước khi lưu đơn!");
    return;
  }

  // ✅ THÊM: Xác nhận lưu đơn hàng với tên khách hàng
  const tenKhachHang = thongTinKhachHangHienTai.value.tenKhachHang;
  const confirmLuuDon = confirm(
    `💾 Bạn có chắc muốn lưu đơn hàng?\n\n` +
    `👤 Khách hàng: ${tenKhachHang}\n` +
    `📞 SĐT: ${thongTinKhachHangHienTai.value.soDienThoai}\n` +
    `💰 Tổng tiền: ${formatCurrency(tongThanhToan.value)}\n\n` +
    `Đơn hàng sẽ được lưu với trạng thái "Chờ thanh toán".`
  );
  if (!confirmLuuDon) return;

  try {
    // ✅ TỐI ƯU: Gọi API /users/me để lấy user.id trực tiếp
    let actualUserId = 1; // Fallback mặc định
    try {
      const response = await getCurrentUser();
      actualUserId = response?.user?.id || 1;
    } catch (error) {
      console.error("❌ ERROR: Lỗi khi lấy thông tin user:", error);
      alert("⚠️ Không thể lấy thông tin user. Vui lòng đăng nhập lại!");
      return;
    }

    // Chuẩn bị dữ liệu đơn hàng
    const orderData = {
      userId: parseInt(actualUserId) || 1, // Convert to Integer, fallback to 1
      maDonHang: getCurrentOrderMaDonHang(), // Gửi mã đơn hàng hiện tại nếu có
      isUpdate: getCurrentOrderMaDonHang() != null, // true nếu có đơn hàng hiện tại, false nếu tạo mới
      tongTien: parseFloat(tongThanhToan.value) || 0, // Lưu đơn: Sử dụng tổng tiền gốc, KHÔNG áp dụng voucher
      diaChiGiaoHang: thongTinKhachHangHienTai.value.diaChi || "",
      tenNguoiNhan: thongTinKhachHangHienTai.value.tenKhachHang || "", // ✅ SỬA: Thêm tenNguoiNhan
      soDienThoai: thongTinKhachHangHienTai.value.soDienThoai || "",
      phuongThucThanhToan: "", // Lưu đơn: KHÔNG lưu phương thức thanh toán
      ghiChu: paymentData.ghiChu || "", // Lưu đơn: Vẫn lưu ghi chú
      userVoucherId: null, // Lưu đơn: KHÔNG áp dụng voucher
      // ✅ THÊM: Các trường mới để xử lý số lượng
      updateProductQuantities: false, // Lưu đơn: KHÔNG trừ số lượng sản phẩm/phụ kiện
      updateVoucherQuantities: false, // Lưu đơn: KHÔNG trừ số lượng voucher
      chiTietDonHangs: gioHang.value.flatMap((item) => {
        const isSanPham = item.maSKU && !item.maSKUPhuKien;

        // ✅ QUAN TRỌNG: Mỗi IMEI tạo 1 dòng riêng với soLuong=1
        if (item.imeiList && item.imeiList.length > 0) {
          // Tạo 1 dòng cho mỗi IMEI
          return item.imeiList.map((imei) => {
            let imeiId = null;
            // ✅ SỬA: IMEI object có thuộc tính 'id' (integer) từ API
            if (typeof imei === "object" && imei.id) {
              imeiId = imei.id; // Sử dụng ID thực tế từ database
            }

            return {
              maSKU: item.maSKU,
              maSKUPhuKien: item.maSKUPhuKien,
              imeiId: imeiId,
              imei: imei.imei || imei, // ✅ THÊM: Gửi số IMEI để backend tìm
              soLuong: 1, // ✅ QUAN TRỌNG: Mỗi IMEI = 1 dòng với soLuong=1
              gia: parseFloat(item.gia) || 0,
              loaiSanPham: isSanPham ? "sanpham" : "phukien",
              // ✅ THÊM: Thông tin chi tiết để backend biết trừ đúng bảng
              isSanPham: isSanPham || false,
              isPhuKien: !isSanPham || false,
              tableType: isSanPham ? "bien_the_san_pham" : "bien_the_phu_kien",
            };
          });
        } else {
          // Không có IMEI - tạo 1 dòng với soLuong=item.soLuongMua

          return [
            {
              maSKU: item.maSKU,
              maSKUPhuKien: item.maSKUPhuKien,
              imeiId: null,
              soLuong: item.soLuongMua, // ✅ Có thể > 1 nếu không có IMEI
              gia: parseFloat(item.gia) || 0,
              loaiSanPham: isSanPham ? "sanpham" : "phukien",
              // ✅ THÊM: Thông tin chi tiết để backend biết trừ đúng bảng
              isSanPham: isSanPham || false,
              isPhuKien: !isSanPham || false,
              tableType: isSanPham ? "bien_the_san_pham" : "bien_the_phu_kien",
            },
          ];
        }
      }),
    };

    // Gọi API lưu đơn hàng
    // const { luuDonHang } = await import("@/service/api");
    console.log("🔍 DEBUG: Dữ liệu gửi lên server:", JSON.stringify(orderData, null, 2));
    
    // ✅ VALIDATION: Kiểm tra dữ liệu trước khi gửi
    if (!orderData.chiTietDonHangs || orderData.chiTietDonHangs.length === 0) {
      alert("❌ Không có sản phẩm nào trong đơn hàng!");
      return;
    }
    
    // Kiểm tra từng sản phẩm
    for (const item of orderData.chiTietDonHangs) {
      if (item.loaiSanPham === "sanpham" && !item.maSKU) {
        alert("❌ Sản phẩm chính thiếu mã SKU!");
        return;
      }
      if (item.loaiSanPham === "phukien" && !item.maSKUPhuKien) {
        alert("❌ Phụ kiện thiếu mã SKU!");
        return;
      }
    }
    
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

    // Xóa đơn hàng khỏi danh sách
    const index = danhSachDonHang.value.findIndex(
      (dh) => dh.id === donHangHienTaiId.value
    );
    if (index !== -1) {
      danhSachDonHang.value.splice(index, 1);
    }

    // Reset đơn hàng hiện tại
    donHangHienTaiId.value = null;

    // Xóa giỏ hàng (KHÔNG cập nhật trạng thái IMEI vì backend đã xử lý)
    await xoaToanBoGioHang(true);

    // Lưu vào localStorage
    saveToLocalStorage();

    // ✅ THÊM: Thông báo thành công với tên khách hàng
    const tenKhachHang = thongTinKhachHangHienTai.value.tenKhachHang || "Khách hàng";
    alert(`✅ Đã lưu đơn hàng thành công!\n\n📋 Mã đơn hàng: ${result.maDonHang}\n👤 Khách hàng: ${tenKhachHang}\n💰 Tổng tiền: ${formatCurrency(tongThanhToan.value)}`);

    // Kiểm tra xem còn đơn hàng nào không
    if (danhSachDonHang.value.length > 0) {
      // Chuyển sang đơn hàng khác
      const donHangKhac = danhSachDonHang.value[0]; // Lấy đơn hàng đầu tiên
      await chonDonHang(donHangKhac.id);
      // ✅ KHÔNG reset thông tin khách hàng vì chonDonHang đã load thông tin từ đơn hàng mới
    } else {
      // Chỉ reset thông tin khách hàng khi không còn đơn hàng nào
      thongTinKhachHangHienTai.value = {
        tenKhachHang: "",
        soDienThoai: "",
        diaChi: "",
        customerInfo: null,
      };
    }

    // Clear form trong component ThanhToan
    if (thanhToanRef.value) {
      thanhToanRef.value.clearForm();
    }
  } catch (error) {
    console.error("❌ Lỗi khi lưu đơn hàng:", error);
    console.error("❌ Chi tiết lỗi:", error.response?.data);
    console.error("❌ Status code:", error.response?.status);
    console.error("❌ Full error response:", error.response);
    
    // Try to get more detailed error information
    let errorMessage = "Lỗi không xác định";
    if (error.response?.data) {
      if (typeof error.response.data === 'string') {
        errorMessage = error.response.data;
      } else if (error.response.data.message) {
        errorMessage = error.response.data.message;
      } else if (error.response.data.error) {
        errorMessage = error.response.data.error;
      }
    } else if (error.message) {
      errorMessage = error.message;
    }
    
    alert("❌ Lỗi khi lưu đơn hàng: " + errorMessage);
  }
}

// Xử lý chốt đơn
async function handleSubmit(paymentData = {}) {
  // bắt buộc giỏ hang có dư liệu
  if (gioHang.value.length === 0) {
    alert("⚠️ Giỏ hàng trống! Vui lòng thêm sản phẩm.");
    return;
  }

  //  VALIDATION: Kiểm tra số điện thoại khách hàng
  if (
    !thongTinKhachHangHienTai.value.soDienThoai ||
    thongTinKhachHangHienTai.value.soDienThoai.trim() === ""
  ) {
    alert("⚠️ Vui lòng nhập số điện thoại khách hàng!");
    return;
  }

  // ✅ VALIDATION: Kiểm tra tên khách hàng bắt buộc
  if (
    !thongTinKhachHangHienTai.value.tenKhachHang ||
    thongTinKhachHangHienTai.value.tenKhachHang.trim() === ""
  ) {
    alert("⚠️ Vui lòng nhập TÊN khách hàng trước khi thanh toán!");
    return;
  }

  // ✅ CẬP NHẬT: Xác nhận thanh toán với tên khách hàng
  const tenKhachHang = thongTinKhachHangHienTai.value.tenKhachHang;
  const confirmThanhToan = confirm(
    `⚠️ Bạn có chắc muốn thanh toán đơn hàng?\n\n` +
    `👤 Khách hàng: ${tenKhachHang}\n` +
    `📞 SĐT: ${thongTinKhachHangHienTai.value.soDienThoai}\n` +
    `💰 Tổng tiền: ${formatCurrency(tongThanhToan.value)}\n\n` +
    `Sau khi thanh toán, sẽ trừ kho và IMEI sẽ chuyển sang trạng thái 'Đã bán' và không thể hoàn tác!`
  );
  if (!confirmThanhToan) return;

  try {
    // ✅ TỐI ƯU: Gọi API /users/me để lấy user.id trực tiếp
    let actualUserId = 1; // Fallback mặc định
    try {
      const response = await getCurrentUser();
      actualUserId = response?.user?.id || 1;
    } catch (error) {
      console.error("❌ ERROR: Lỗi khi lấy thông tin user:", error);
      alert("⚠️ Không thể lấy thông tin user. Vui lòng đăng nhập lại!");
      return;
    }

    //  KIỂM TRA: Đơn hàng hiện tại có phải là đơn đã lưu không?
    const donHangCanThanhToan = danhSachDonHang.value.find(
      (dh) => dh.id === donHangHienTaiId.value
    );
    const maDonHangDaLuu = donHangCanThanhToan?.maDonHang || null;

    // Chuẩn bị dữ liệu đơn hàng
    const orderData = {
      userId: parseInt(actualUserId) || 1, // Convert to Integer, fallback to 1
      maDonHang: maDonHangDaLuu, // ✅ QUAN TRỌNG: Truyền maDonHang nếu là đơn đã lưu
      tongTien: parseFloat(paymentData.canThanhToan || tongThanhToan.value) || 0, // Sử dụng tổng tiền sau voucher
      diaChiGiaoHang: thongTinKhachHangHienTai.value.diaChi || "",
      tenNguoiNhan: thongTinKhachHangHienTai.value.tenKhachHang || "", // ✅ SỬA: Thêm tenNguoiNhan
      soDienThoai: thongTinKhachHangHienTai.value.soDienThoai || "",
      phuongThucThanhToan: paymentData.phuongThuc || "tienmat", // Sử dụng phương thức từ form
      ghiChu: paymentData.ghiChu || "", // Sử dụng ghi chú từ form
      userVoucherId: paymentData.voucherApplied
        ? paymentData.voucherInfo?.id
        : null, // Xử lý voucher
      // ✅ THÊM: Các trường mới để xử lý số lượng
      updateProductQuantities: true, // Thanh toán: TRỪ số lượng sản phẩm/phụ kiện
      updateVoucherQuantities: paymentData.voucherApplied || false, // Thanh toán: TRỪ số lượng voucher nếu áp dụng
      chiTietDonHangs: gioHang.value.flatMap((item) => {
        const isSanPham = item.maSKU && !item.maSKUPhuKien;

        // QUAN TRỌNG: Mỗi IMEI tạo 1 dòng riêng với soLuong=1
        if (item.imeiList && item.imeiList.length > 0) {
          // Tạo 1 dòng cho mỗi IMEI
          return item.imeiList.map((imei) => {
            let imeiId = null;
            //  SỬA: IMEI object có thuộc tính 'id' (integer) từ API
            if (typeof imei === "object" && imei.id) {
              imeiId = imei.id; // Sử dụng ID thực tế từ database
            }

            return {
              maSKU: item.maSKU,
              maSKUPhuKien: item.maSKUPhuKien,
              imeiId: imeiId,
              imei: imei.imei || imei, //  THÊM: Gửi số IMEI để backend tìm
              soLuong: 1, //  QUAN TRỌNG: Mỗi IMEI = 1 dòng với soLuong=1
              gia: parseFloat(item.gia) || 0,
              loaiSanPham: isSanPham ? "sanpham" : "phukien",
              //  THÊM: Thông tin chi tiết để backend biết trừ đúng bảng
              isSanPham: isSanPham,
              isPhuKien: !isSanPham,
              tableType: isSanPham ? "bien_the_san_pham" : "bien_the_phu_kien",
            };
          });
        } else {
          // Không có IMEI - tạo 1 dòng với soLuong=item.soLuongMua

          return [
            {
              maSKU: item.maSKU,
              maSKUPhuKien: item.maSKUPhuKien,
              imeiId: null,
              soLuong: item.soLuongMua, //  Có thể > 1 nếu không có IMEI
              gia: parseFloat(item.gia) || 0,
              loaiSanPham: isSanPham ? "sanpham" : "phukien",
              //  THÊM: Thông tin chi tiết để backend biết trừ đúng bảng
              isSanPham: isSanPham,
              isPhuKien: !isSanPham,
              tableType: isSanPham ? "bien_the_san_pham" : "bien_the_phu_kien",
            },
          ];
        }
      }),
    };

    // Gọi API thanh toán đơn hàng
    const result = await thanhToanDonHang(orderData);

    // Cập nhật đơn hàng hiện tại với mã đơn hàng từ server
    const donHangHienTai = danhSachDonHang.value.find(
      (dh) => dh.id === donHangHienTaiId.value
    );

    // Xóa đơn hàng khỏi danh sách
    const index = danhSachDonHang.value.findIndex(
      (dh) => dh.id === donHangHienTaiId.value
    );
    if (index !== -1) {
      danhSachDonHang.value.splice(index, 1);
    }

    // Reset đơn hàng hiện tại
    donHangHienTaiId.value = null;

    // Xóa giỏ hàng (KHÔNG cập nhật trạng thái IMEI vì backend đã xử lý)
    await xoaToanBoGioHang(true);

    // Lưu vào localStorage
    saveToLocalStorage();

    // ✅ CẬP NHẬT: Thông báo thanh toán với tên khách hàng (lấy trước khi reset)
    const tenKhachHang = thongTinKhachHangHienTai.value.tenKhachHang || "Khách hàng";
    const soDienThoai = thongTinKhachHangHienTai.value.soDienThoai || "";
    const tongTienThanhToan = paymentData.canThanhToan || tongThanhToan.value;
    
    alert(
      `✅ Đã thanh toán đơn hàng thành công!\n\n` +
      `📋 Mã đơn hàng: ${result.maDonHang}\n` +
      `👤 Khách hàng: ${tenKhachHang}\n` +
      `📞 SĐT: ${soDienThoai}\n` +
      `💰 Tổng thanh toán: ${formatCurrency(tongTienThanhToan)}\n` +
      `💳 Phương thức: ${paymentData.phuongThuc || "Tiền mặt"}\n\n` +
      `✅ Đã trừ kho và IMEI đã chuyển sang trạng thái 'Đã bán'.`
    );

    // Kiểm tra xem còn đơn hàng nào không
    if (danhSachDonHang.value.length > 0) {
      // Chuyển sang đơn hàng khác
      const donHangKhac = danhSachDonHang.value[0]; // Lấy đơn hàng đầu tiên
      await chonDonHang(donHangKhac.id);
      // ✅ KHÔNG reset thông tin khách hàng vì chonDonHang đã load thông tin từ đơn hàng mới
    } else {
      // Chỉ reset thông tin khách hàng khi không còn đơn hàng nào
      thongTinKhachHangHienTai.value = {
        tenKhachHang: "",
        soDienThoai: "",
        diaChi: "",
        customerInfo: null,
      };
    }

    // Clear form trong component ThanhToan
    if (thanhToanRef.value) {
      thanhToanRef.value.clearForm();
    }
  } catch (error) {
    alert("❌ Lỗi khi thanh toán đơn hàng: " + error.message);
  }
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
    capNhatDonHangHienTai();
  }

  // Tạo đơn hàng mới

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
      customerInfo: null,
    },
  };

  danhSachDonHang.value.push(donHangMoi);
  donHangHienTaiId.value = donHangMoi.id;

  // ✅ RESET thông tin khách hàng hiển thị
  thongTinKhachHangHienTai.value = {
    tenKhachHang: "",
    soDienThoai: "",
    diaChi: "",
    customerInfo: null,
  };

  // Lưu vào localStorage
  saveToLocalStorage();
  // Xóa giỏ hàng hiện tại
  await xoaToanBoGioHang();
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
    try {
      // ✅ PHÂN LOẠI ĐƠN HÀNG: Đã lưu vs chưa lưu
      const savedOrders = []; // đơn đã lưu
      const draftOrders = []; // đơn chưa lưu

      danhSachDonHang.value.forEach((donHang) => {
        const isSavedOrder =
          (donHang.trangThai === "saved" && donHang.maDonHang) ||
          (donHang.id && donHang.id.toString().startsWith("saved_"));

        if (isSavedOrder) {
          savedOrders.push(donHang);
        } else {
          draftOrders.push(donHang);
        }
      });

      //  XỬ LÝ ĐƠN HÀNG ĐÃ LƯU: Gọi API xóa từng đơn hàng
      if (savedOrders.length > 0) {
        // xoaDonHangLuu đã được import trực tiếp
        for (const donHang of savedOrders) {
          try {
            // Lấy mã đơn hàng từ maDonHang hoặc từ ID
            let maDonHangToDelete = donHang.maDonHang;
            if (
              !maDonHangToDelete &&
              donHang.id &&
              donHang.id.toString().startsWith("saved_")
            ) {
              maDonHangToDelete = parseInt(
                donHang.id.toString().replace("saved_", "")
              );
            }

            if (maDonHangToDelete) {
              await xoaDonHangLuu(maDonHangToDelete);
            }
          } catch (error) {
            // Tiếp tục xóa đơn hàng khác dù có lỗi
          }
        }
      }

      // XỬ LÝ ĐƠN HÀNG CHƯA LƯU: Cập nhật trạng thái IMEI về "còn hàng"
      if (draftOrders.length > 0) {
        const allImeis = [];
        draftOrders.forEach((donHang) => {
          if (donHang.gioHang && donHang.gioHang.length > 0) {
            donHang.gioHang.forEach((item) => {
              if (
                item.imeiList &&
                Array.isArray(item.imeiList) &&
                item.imeiList.length > 0
              ) {
                const imeiNumbers = item.imeiList.map(
                  (imei) => imei.imei || imei
                );
                allImeis.push(...imeiNumbers);
              }
            });
          }
        });

        if (allImeis.length > 0) {
          try {
            // setImeiToStock đã được import trực tiếp
            await setImeiToStock(allImeis);
          } catch (error) {
            console.error("❌ Lỗi khi cập nhật trạng thái IMEI:", error);
            // Vẫn tiếp tục xóa đơn hàng dù có lỗi
          }
        }
      }
      //  XÓA TẤT CẢ ĐƠN HÀNG KHỎI DANH SÁCH LOCAL
      danhSachDonHang.value = [];
      donHangHienTaiId.value = null;
      await xoaToanBoGioHang();
      saveToLocalStorage();

      alert(
        "✅ Đã xóa tất cả đơn hàng!\n\nTất cả IMEI đã được chuyển về trạng thái 'Còn hàng'."
      );
    } catch (error) {
      alert("❌ Lỗi khi xóa tất cả đơn hàng: " + error.message);
    }
  }
}

// Chọn đơn hàng
async function chonDonHang(donHangId) {
  const donHang = danhSachDonHang.value.find((dh) => dh.id === donHangId);
  if (!donHang) return;

  //  QUAN TRỌNG: Luôn lưu đơn hàng hiện tại trước khi chuyển
  if (donHangHienTaiId.value && donHangHienTaiId.value !== donHangId) {
    //  QUAN TRỌNG: Luôn lưu đơn hàng hiện tại, kể cả khi giỏ hàng trống
    // Đảm bảo tất cả thay đổi được lưu trước khi chuyển đơn hàng
    capNhatDonHangHienTai();

    //  QUAN TRỌNG: Đảm bảo localStorage được cập nhật ngay lập tức
    saveToLocalStorage();
  }
  //  QUAN TRỌNG: Set flag để tránh watcher cập nhật đơn hàng sai
  isSwitchingOrder.value = true;
  // Cập nhật đơn hàng hiện tại
  donHangHienTaiId.value = donHangId;
  await xoaToanBoGioHang(true); // Skip IMEI update khi chuyển đơn hàng

  //  QUAN TRỌNG: Load sản phẩm từ đơn hàng được chọn nếu nó có sản phẩm
  if (donHang.gioHang && donHang.gioHang.length > 0) {
    for (let index = 0; index < donHang.gioHang.length; index++) {
      const item = donHang.gioHang[index];

      // Kiểm tra cấu trúc dữ liệu
      if (item && item.sanPham && item.sanPham.tenSanPham) {
        // Dạng 1: có thuộc tính sanPham
        await loadSanPhamTuDonHang(
          item.sanPham,
          item.soLuongMua,
          item.imeiList
        );
      } else if (item && item.tenSanPham) {
        // Dạng 2: sản phẩm đã được flatten
        await loadSanPhamTuDonHang(item, item.soLuongMua, item.imeiList);
      }
    }
  } else {
    //  QUAN TRỌNG: Đảm bảo giỏ hàng trống khi đơn hàng không có sản phẩm
    gioHang.value = [];
  }

  // ✅ SỬA LỖI: Load thông tin khách hàng từ đơn hàng (đảm bảo không bị mất)
  console.log("🔍 DEBUG chonDonHang - donHang.thongTinKhachHang:", donHang.thongTinKhachHang);
  
  if (donHang.thongTinKhachHang) {
    // Nếu đơn hàng có thông tin khách hàng, load toàn bộ
    thongTinKhachHangHienTai.value = {
      tenKhachHang: donHang.thongTinKhachHang.tenKhachHang || "",
      soDienThoai: donHang.thongTinKhachHang.soDienThoai || "",
      diaChi: donHang.thongTinKhachHang.diaChi || "",
      customerInfo: donHang.thongTinKhachHang.customerInfo || null,
    };
    console.log("✅ DEBUG - Đã load thông tin khách hàng:", thongTinKhachHangHienTai.value);
  } else {
    // Nếu đơn hàng không có thông tin khách hàng, reset về trống
    console.warn("⚠️ DEBUG - Đơn hàng không có thongTinKhachHang!");
    thongTinKhachHangHienTai.value = {
      tenKhachHang: "",
      soDienThoai: "",
      diaChi: "",
      customerInfo: null,
    };
  }

  // Lưu vào localStorage
  saveToLocalStorage();
  // Reset flag sau khi hoàn thành chuyển đơn hàng
  isSwitchingOrder.value = false;
}

// Cập nhật đơn hàng hiện tại với giỏ hàng
function capNhatDonHangHienTai() {
  if (!donHangHienTaiId.value) {
    console.warn("⚠️ DEBUG capNhatDonHangHienTai - Không có donHangHienTaiId");
    return;
  }

  const donHang = danhSachDonHang.value.find(
    (dh) => dh.id === donHangHienTaiId.value
  );
  if (donHang) {
    console.log(`🔍 DEBUG capNhatDonHangHienTai - Đang cập nhật đơn hàng: ${donHangHienTaiId.value}`);
    console.log("🔍 DEBUG capNhatDonHangHienTai - Thông tin KH hiện tại:", thongTinKhachHangHienTai.value);
    
    //  QUAN TRỌNG: Luôn cập nhật giỏ hàng, kể cả khi trống
    //  SỬA LỖI: Đảm bảo thông tin loại sản phẩm, thuộc tính và giá được lưu
    const gioHangWithLoai = gioHang.value.map((item) => {
      const itemCopy = JSON.parse(JSON.stringify(item));

      // Đảm bảo loại sản phẩm được lưu
      if (!itemCopy.loai) {
        if (itemCopy.maSKUPhuKien && !itemCopy.maSKU) {
          itemCopy.loai = "Phụ kiện";
        } else if (
          itemCopy.maSKU &&
          (itemCopy.maSKU.includes("PK-") || itemCopy.maSKU.includes("ANK-"))
        ) {
          itemCopy.loai = "Phụ kiện";
        } else {
          itemCopy.loai = "Sản phẩm chính";
        }
      }

      // SỬA LỖI: Đảm bảo thuộc tính được lưu cho phụ kiện
      if (!itemCopy.thuocTinh && itemCopy.loai === "Phụ kiện") {
        if (itemCopy.thuocTinhPhuKien) {
          itemCopy.thuocTinh = itemCopy.thuocTinhPhuKien;
        } else {
          itemCopy.thuocTinh = "N/A";
        }
      }

      // SỬA LỖI: Đảm bảo giá được lưu cho phụ kiện
      if (!itemCopy.gia && itemCopy.loai === "Phụ kiện") {
        if (itemCopy.giaPhuKien) {
          itemCopy.gia = itemCopy.giaPhuKien;
        } else {
          itemCopy.gia = 0;
        }
      }

      return itemCopy;
    });

    donHang.gioHang = gioHangWithLoai;
    donHang.tongTien = tongThanhToan.value;

    if (gioHang.value.length > 0) {
      donHang.gioHang.forEach((item, index) => {});
    }
    donHang.ngayCapNhat = new Date().toISOString();

    // Lưu thông tin khách hàng hiện tại
    const customerInfoToSave = { ...thongTinKhachHangHienTai.value };
    donHang.thongTinKhachHang = customerInfoToSave;
    
    console.log(`✅ DEBUG capNhatDonHangHienTai - Đã lưu thông tin KH cho đơn ${donHangHienTaiId.value}:`, customerInfoToSave);
    console.log("🔍 DEBUG capNhatDonHangHienTai - Đơn hàng sau khi cập nhật:", {
      id: donHang.id,
      maDonHang: donHang.maDonHang,
      thongTinKhachHang: donHang.thongTinKhachHang
    });

    saveToLocalStorage();
  } else {
    console.warn(`⚠️ DEBUG capNhatDonHangHienTai - Không tìm thấy đơn hàng với ID: ${donHangHienTaiId.value}`);
  }
}

// Xóa đơn hàng
async function xoaDonHang(donHangId) {
  const donHang = danhSachDonHang.value.find((dh) => dh.id === donHangId);
  if (!donHang) return;

  //  KIỂM TRA: Đơn hàng đã lưu hay chưa lưu
  //  KIỂM TRA: Đơn hàng đã lưu (có maDonHang thực tế hoặc ID bắt đầu bằng "saved_")
  const isSavedOrder =
    (donHang.trangThai === "saved" && donHang.maDonHang) ||
    (donHang.id && donHang.id.toString().startsWith("saved_"));

  const confirmMessage = isSavedOrder
    ? `⚠️ Bạn có chắc muốn xóa đơn hàng đã lưu này?\n\nĐơn hàng sẽ bị xóa vĩnh viễn và không thể khôi phục.\n\nTất cả IMEI trong đơn hàng sẽ được chuyển về trạng thái "Còn hàng".`
    : `⚠️ Bạn có chắc muốn xóa đơn hàng này?\n\nĐơn hàng sẽ bị xóa vĩnh viễn và không thể khôi phục.\n\nTất cả IMEI trong đơn hàng sẽ được chuyển về trạng thái "Còn hàng".`;

  const confirmDelete = confirm(confirmMessage);
  if (!confirmDelete) return;

  try {
    // XỬ LÝ ĐƠN HÀNG ĐÃ LƯU: Gọi API xóa đơn hàng
    if (isSavedOrder) {
      //  LẤY MÃ ĐƠN HÀNG: Từ maDonHang hoặc từ ID (saved_123 -> 123)
      let maDonHangToDelete = donHang.maDonHang;
      if (
        !maDonHangToDelete &&
        donHang.id &&
        donHang.id.toString().startsWith("saved_")
      ) {
        // Lấy số từ ID: saved_123 -> 123
        maDonHangToDelete = parseInt(
          donHang.id.toString().replace("saved_", "")
        );
      }
      // xoaDonHangLuu đã được import trực tiếp
      const result = await xoaDonHangLuu(maDonHangToDelete);
    } else {
      //  XỬ LÝ ĐƠN HÀNG CHƯA LƯU: Cập nhật trạng thái IMEI về "còn hàng"
      if (donHang.gioHang && donHang.gioHang.length > 0) {
        // Thu thập tất cả IMEI từ đơn hàng
        const allImeis = [];
        donHang.gioHang.forEach((item) => {
          if (
            item.imeiList &&
            Array.isArray(item.imeiList) &&
            item.imeiList.length > 0
          ) {
            const imeiNumbers = item.imeiList.map((imei) => imei.imei || imei);
            allImeis.push(...imeiNumbers);
          }
        });
        if (allImeis.length > 0) {
          try {
            // setImeiToStock đã được import trực tiếp
            await setImeiToStock(allImeis);
          } catch (error) {
            console.error("❌ Lỗi khi cập nhật trạng thái IMEI:", error);
            // Vẫn tiếp tục xóa đơn hàng dù có lỗi
          }
        }
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

    alert(
      "✅ Đã xóa đơn hàng thành công!\n\nTất cả IMEI đã được chuyển về trạng thái 'Còn hàng'."
    );
  } catch (error) {
    console.error("❌ Lỗi khi xóa đơn hàng:", error);
    alert("❌ Lỗi khi xóa đơn hàng: " + error.message);
  }
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
  return danhSachDonHang.value.filter(
    (dh) => dh.trangThai === "draft" && !dh.hidden
  ).length;
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
  const { sanPham, soLuong, imeiList } = data;

  // Thêm sản phẩm vào giỏ hàng
  await themSanPham(sanPham, soLuong, imeiList);
  //  TỰ ĐỘNG LƯU vào localStorage ngay sau khi thêm sản phẩm
  capNhatDonHangHienTai();
  // Đóng modal
  isModalOpen.value = false;
}

// Mở modal chọn sản phẩm
function moModalChonSanPham() {
  // SỬA LỖI: Kiểm tra nếu chưa có đơn hàng nào, yêu cầu tạo đơn hàng trước
  if (!donHangHienTaiId.value) {
    // Hiển thị thông báo yêu cầu tạo đơn hàng
    alert(
      "⚠️ Vui lòng tạo đơn hàng trước khi thêm sản phẩm!\n\nNhấn nút 'Tạo đơn hàng mới' để bắt đầu."
    );
    return; // Dừng xử lý, không mở modal
  }

  isModalOpen.value = true;
}

// Cập nhật thông tin khách hàng
function capNhatThongTinKhachHang(customerData) {
  if (!donHangHienTaiId.value) {
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
      customerInfo: customerData.customerInfo || null,
    };
    // Cập nhật thông tin hiển thị
    thongTinKhachHangHienTai.value = { ...donHang.thongTinKhachHang };

    // Lưu vào localStorage
    saveToLocalStorage();
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
