/**
 * Composable quản lý giỏ hàng bán hàng tại quầy
 * 
 * Chức năng chính:
 * - Quản lý giỏ hàng (thêm, xóa, cập nhật sản phẩm)
 * - Quản lý IMEI (cập nhật trạng thái khi thêm/xóa sản phẩm)
 * - Tính toán thành tiền, khuyến mãi
 * - Lưu trữ dữ liệu trong localStorage
 * 
 * @author DATN Team
 * @version 1.0.0
 */
import { ref, computed } from "vue";
import { 
  // Unified API - Chỉ sử dụng API thống nhất
  setImeiToStock,    // Chuyển IMEI về trạng thái "còn hàng" (status = 1)
  setImeiToCart,     // Chuyển IMEI về trạng thái "tạm giữ" (status = 5)
  setImeiToSold      // Chuyển IMEI về trạng thái "đã bán" (status = 0)
} from "../../service/api.js";

// State global - dùng chung cho toàn bộ component bán hàng
const gioHang = ref([]);
const isSwitchingOrder = ref(false); // Flag để tránh cập nhật IMEI không cần thiết

export function useGioHangBanHangTaiQuay() {
  
  /**
   * Thêm sản phẩm vào giỏ hàng
   * @param {Object} sanPham - Thông tin sản phẩm
   * @param {number} soLuong - Số lượng sản phẩm (mặc định = 1)
   * @param {Array} imeiList - Danh sách IMEI (nếu có)
   * @param {boolean} autoCreateOrder - Tự động tạo đơn hàng mới
   * @returns {Promise<void>}
   */
  async function themSanPham(sanPham, soLuong = 1, imeiList = [], autoCreateOrder = false) {
    console.log(
      "🛒 Thêm sản phẩm:",
      sanPham,
      "Số lượng:",
      soLuong,
      "IMEIs:",
      imeiList
    );
    console.log("🔍 Frontend: Chi tiết imeiList:", {
      isArray: Array.isArray(imeiList),
      length: imeiList ? imeiList.length : "null/undefined",
      content: imeiList,
    });
    // Debug thuộc tính (đã tắt)
    // console.log('🔍 Debug thuộc tính:', {
    //   thuocTinh: sanPham.thuocTinh,
    //   type: typeof sanPham.thuocTinh,
    //   stringified: JSON.stringify(sanPham.thuocTinh)
    // })

    // Cập nhật trạng thái IMEI thành 5 nếu có IMEI (sử dụng API thống nhất)
    if (imeiList && Array.isArray(imeiList) && imeiList.length > 0) {
      try {
        console.log("🔄 Frontend: Cập nhật trạng thái IMEI thành 5 (unified API)...");
        const imeiNumbers = imeiList.map(imei => imei.imei || imei);
        await setImeiToCart(imeiNumbers);
        console.log("✅ Frontend: Đã cập nhật trạng thái IMEI thành 5 (tạm giữ)");
      } catch (error) {
        console.error("❌ Frontend: Lỗi khi cập nhật trạng thái IMEI:", error);
        // Không dừng quá trình thêm sản phẩm, chỉ log lỗi
      }
    }

    // Kiểm tra sản phẩm đã có trong giỏ chưa (phân biệt theo maSKU và loại)
    const index = gioHang.value.findIndex((item) => {
      const itemSKU = item.maSKU || item.maSKUPhuKien;
      const sanPhamSKU = sanPham.maSKU || sanPham.maSKUPhuKien;
      return itemSKU === sanPhamSKU;
    });

    if (index !== -1) {
      // Đã có → Cộng thêm số lượng
      gioHang.value[index].soLuongMua += soLuong;

      // Nếu có IMEI mới, thêm vào danh sách
      if (imeiList && Array.isArray(imeiList) && imeiList.length > 0) {
        if (!gioHang.value[index].imeiList) {
          gioHang.value[index].imeiList = [];
        }
        // Thêm IMEI objects mới (tránh trùng lặp)
        imeiList.forEach((newImei) => {
          const exists = gioHang.value[index].imeiList.some(
            (existingImei) => existingImei.imei === newImei.imei
          );
          if (!exists) {
            gioHang.value[index].imeiList.push(newImei);
          }
        });
        // Cập nhật số lượng theo IMEI
        gioHang.value[index].soLuongMua = gioHang.value[index].imeiList.length;
        console.log(
          "✅ Đã thêm IMEI vào sản phẩm có sẵn:",
          gioHang.value[index].imeiList.length,
          "IMEI"
        );
      } else {
        console.log(
          "🔍 Frontend: Không có IMEI mới để thêm vào sản phẩm có sẵn"
        );
      }

      console.log("✅ Đã cộng thêm số lượng:", gioHang.value[index].soLuongMua);
    } else {
      // Chưa có → Thêm mới với cấu trúc chuẩn
      const itemMoi = {
        sanPham: sanPham, // Lưu đối tượng sản phẩm gốc
        soLuongMua: soLuong,
        thanhTien: sanPham.gia * soLuong,
        imeiList: imeiList || [], // Lưu danh sách IMEI đã chọn
      };

      // Thêm các thuộc tính sản phẩm vào item để dễ truy cập
      Object.assign(itemMoi, sanPham);
      
      // Đảm bảo imeiList không bị ghi đè
      itemMoi.imeiList = imeiList || [];

      // Xử lý tên sản phẩm cho phụ kiện
      if (sanPham.tenPhuKien && !sanPham.tenSanPham) {
        itemMoi.tenSanPham = sanPham.tenPhuKien;
      }

      // Xử lý maSKU cho phụ kiện
      if (sanPham.maSKUPhuKien && !sanPham.maSKU) {
        itemMoi.maSKU = sanPham.maSKUPhuKien;
      }

      // Cập nhật số lượng theo IMEI nếu có
      if (imeiList && Array.isArray(imeiList) && imeiList.length > 0) {
        itemMoi.soLuongMua = imeiList.length;
        console.log(
          "✅ Sản phẩm mới có IMEI:",
          imeiList.length,
          "IMEI objects"
        );
      } else {
        console.log("🔍 Frontend: Sản phẩm mới không có IMEI hoặc IMEI rỗng");
      }

      gioHang.value.push(itemMoi);
      console.log("✅ Đã thêm sản phẩm mới vào giỏ:", itemMoi);
      console.log("🔍 Frontend: Chi tiết itemMoi sau khi tạo:", {
        tenSanPham: itemMoi.tenSanPham,
        soLuongMua: itemMoi.soLuongMua,
        imeiListLength: itemMoi.imeiList ? itemMoi.imeiList.length : 'undefined',
        imeiList: itemMoi.imeiList
      });
    }

    // Cập nhật thành tiền
    capNhatThanhTien();
    
    // Emit event nếu cần tạo đơn hàng tự động
    if (autoCreateOrder) {
      console.log("🔄 Emit event để tạo đơn hàng tự động...");
      // Có thể emit event hoặc gọi callback nếu cần
    }
  }

  // Cập nhật số lượng sản phẩm
  function capNhatSoLuong(maSKU, soLuongMoi) {
    const item = gioHang.value.find((sp) => sp.maSKU === maSKU);
    if (item && soLuongMoi > 0) {
      const soLuongCu = item.soLuongMua;
      item.soLuongMua = soLuongMoi;
      capNhatThanhTien();
      
      // ✅ TỰ ĐỘNG LƯU vào localStorage khi cập nhật số lượng
      console.log("💾 Tự động lưu khi cập nhật số lượng sản phẩm");
      // Emit event để parent component lưu vào localStorage (chỉ khi thực sự có thay đổi)
      if (soLuongMoi !== soLuongCu) {
        window.dispatchEvent(new CustomEvent('gioHangChanged', { 
          detail: { action: 'capNhatSoLuong', maSKU, soLuongMoi } 
        }));
      }
    }
  }

  // Cập nhật số lượng dựa trên IMEI
  function capNhatSoLuongTheoImei(maSKU) {
    const item = gioHang.value.find((sp) => sp.maSKU === maSKU);
    if (item && item.imeiList) {
      const soLuongCu = item.soLuongMua;
      item.soLuongMua = item.imeiList.length;
      capNhatThanhTien();
      
      // ✅ TỰ ĐỘNG LƯU vào localStorage khi cập nhật số lượng theo IMEI
      console.log("💾 Tự động lưu khi cập nhật số lượng theo IMEI");
      // Emit event để parent component lưu vào localStorage (chỉ khi thực sự có thay đổi)
      if (item.soLuongMua !== soLuongCu) {
        window.dispatchEvent(new CustomEvent('gioHangChanged', { 
          detail: { action: 'capNhatSoLuongTheoImei', maSKU, soLuongMoi: item.soLuongMua } 
        }));
      }
      
      console.log(
        "✅ Đã cập nhật số lượng theo IMEI:",
        item.soLuongMua,
        "cho SKU:",
        maSKU
      );
    }
  }

  /**
   * Xóa sản phẩm khỏi giỏ hàng
   * @param {string} maSKU - Mã SKU của sản phẩm cần xóa
   * @returns {Promise<void>}
   */
  async function xoaSanPham(maSKU) {
    const index = gioHang.value.findIndex((item) => item.maSKU === maSKU);
    if (index !== -1) {
      const item = gioHang.value[index];
      
      // Cập nhật trạng thái IMEI về 1 nếu có IMEI (sử dụng API thống nhất)
      if (item.imeiList && Array.isArray(item.imeiList) && item.imeiList.length > 0) {
        try {
          console.log("🔄 Frontend: Cập nhật trạng thái IMEI về 1 khi xóa sản phẩm (unified API)...");
          const imeiNumbers = item.imeiList.map(imei => imei.imei || imei);
          await setImeiToStock(imeiNumbers);
          console.log("✅ Frontend: Đã cập nhật trạng thái IMEI về 1 (còn hàng)");
        } catch (error) {
          console.error("❌ Frontend: Lỗi khi cập nhật trạng thái IMEI về 1:", error);
          // Không dừng quá trình xóa sản phẩm, chỉ log lỗi
        }
      }
      
      gioHang.value.splice(index, 1);
      console.log("🗑️ Đã xóa sản phẩm:", maSKU);
      capNhatThanhTien();
    }
  }

  /**
   * Xóa IMEI cụ thể khỏi sản phẩm
   * @param {string} maSKU - Mã SKU của sản phẩm
   * @param {number} imeiIndex - Index của IMEI cần xóa
   * @returns {Promise<void>}
   */
  async function xoaImei(maSKU, imeiIndex) {
    const item = gioHang.value.find((sp) => sp.maSKU === maSKU);
    if (item && item.imeiList && item.imeiList.length > imeiIndex) {
      const imeiRemoved = item.imeiList.splice(imeiIndex, 1)[0];
      console.log("🗑️ Đã xóa IMEI:", imeiRemoved, "khỏi sản phẩm:", maSKU);

      // Cập nhật trạng thái IMEI về 1 khi xóa IMEI (sử dụng API thống nhất)
      if (imeiRemoved) {
        try {
          console.log("🔄 Frontend: Cập nhật trạng thái IMEI về 1 khi xóa IMEI (unified API)...");
          const imeiNumber = imeiRemoved.imei || imeiRemoved;
          await setImeiToStock(imeiNumber);
          console.log("✅ Frontend: Đã cập nhật trạng thái IMEI về 1 (còn hàng)");
        } catch (error) {
          console.error("❌ Frontend: Lỗi khi cập nhật trạng thái IMEI về 1:", error);
          // Không dừng quá trình xóa IMEI, chỉ log lỗi
        }
      }

      // Cập nhật số lượng dựa trên số IMEI còn lại
      item.soLuongMua = item.imeiList.length;

      // Nếu không còn IMEI nào, xóa sản phẩm khỏi giỏ
      if (item.imeiList.length === 0) {
        await xoaSanPham(maSKU);
        console.log("🗑️ Đã xóa sản phẩm vì không còn IMEI:", maSKU);
      } else {
        // Cập nhật thành tiền
        capNhatThanhTien();
        console.log(
          "✅ Đã cập nhật số lượng:",
          item.soLuongMua,
          "IMEI còn lại:",
          item.imeiList.length
        );
      }
    }
  }

  /**
   * Cập nhật thành tiền cho tất cả sản phẩm trong giỏ
   * @returns {void}
   */
  function capNhatThanhTien() {
    gioHang.value.forEach((item) => {
      item.thanhTien = item.gia * item.soLuongMua;
    });
  }

  /**
   * Xóa toàn bộ giỏ hàng và reset trạng thái IMEI
   * @param {boolean} skipImeiUpdate - Bỏ qua cập nhật trạng thái IMEI (dùng khi chuyển đơn hàng)
   * @returns {Promise<void>}
   */
  async function xoaToanBoGioHang(skipImeiUpdate = false) {
    // Cập nhật trạng thái tất cả IMEI về 1 trước khi xóa (chỉ khi không phải chuyển đơn hàng)
    const allImeis = [];
    gioHang.value.forEach(item => {
      if (item.imeiList && Array.isArray(item.imeiList) && item.imeiList.length > 0) {
        const imeiNumbers = item.imeiList.map(imei => imei.imei || imei);
        allImeis.push(...imeiNumbers);
      }
    });

    if (allImeis.length > 0 && !skipImeiUpdate && !isSwitchingOrder.value) {
      try {
        console.log("🔄 Frontend: Cập nhật trạng thái tất cả IMEI về 1 khi xóa toàn bộ giỏ hàng (unified API)...");
        await setImeiToStock(allImeis);
        console.log("✅ Frontend: Đã cập nhật trạng thái tất cả IMEI về 1 (còn hàng)");
      } catch (error) {
        console.error("❌ Frontend: Lỗi khi cập nhật trạng thái IMEI về 1:", error);
        // Không dừng quá trình xóa giỏ hàng, chỉ log lỗi
      }
    } else if (skipImeiUpdate || isSwitchingOrder.value) {
      console.log("⏭️ Bỏ qua cập nhật trạng thái IMEI vì đang chuyển đơn hàng");
    }

    gioHang.value = [];
    console.log("🗑️ Đã xóa toàn bộ giỏ hàng");
  }

  /**
   * Load sản phẩm từ đơn hàng đã lưu (THAY THẾ giỏ hàng hiện tại)
   * @param {Object} sanPham - Thông tin sản phẩm
   * @param {number} soLuong - Số lượng sản phẩm
   * @param {Array} imeiList - Danh sách IMEI
   * @returns {Promise<void>}
   */
  async function loadSanPhamTuDonHang(sanPham, soLuong, imeiList) {
    console.log(
      "📦 Load sản phẩm từ đơn hàng:",
      sanPham,
      "Số lượng:",
      soLuong,
      "IMEIs:",
      imeiList
    );
    
    // ✅ QUAN TRỌNG: Không cập nhật trạng thái IMEI khi load từ đơn hàng
    // IMEI đã được lưu với trạng thái "tạm giữ" khi đơn hàng được tạo
    // Chỉ cần load sản phẩm mà không thay đổi trạng thái IMEI
    if (imeiList && Array.isArray(imeiList) && imeiList.length > 0) {
      console.log("ℹ️ Frontend: Không cập nhật trạng thái IMEI khi load từ đơn hàng - IMEI đã ở trạng thái 'tạm giữ'");
      console.log("ℹ️ Frontend: IMEI sẽ chỉ được giải phóng khi đơn hàng bị xóa hoặc thanh toán");
    }

    const itemMoi = {
      sanPham: sanPham, // Lưu đối tượng sản phẩm gốc
      soLuongMua: soLuong,
      thanhTien: sanPham.gia * soLuong,
      imeiList: imeiList ? [...imeiList] : [], // ✅ Tạo bản sao mảng IMEI
    };

    // Thêm các thuộc tính sản phẩm vào item để dễ truy cập
    Object.assign(itemMoi, sanPham);
    
    // ✅ SỬA: Đảm bảo IMEI không bị ghi đè
    if (imeiList && imeiList.length > 0) {
      itemMoi.imeiList = [...imeiList];
    }

    // ✅ QUAN TRỌNG: THÊM sản phẩm vào giỏ hàng (không thay thế)
    // Kiểm tra xem sản phẩm đã tồn tại chưa để tránh trùng lặp
    const existingIndex = gioHang.value.findIndex(item => 
      item.maSKU === itemMoi.maSKU && 
      JSON.stringify(item.imeiList) === JSON.stringify(itemMoi.imeiList)
    );
    
    if (existingIndex === -1) {
      // Sản phẩm chưa tồn tại, thêm mới
      gioHang.value.push(itemMoi);
      console.log("✅ Đã thêm sản phẩm mới từ đơn hàng:", itemMoi);
    } else {
      // Sản phẩm đã tồn tại, cập nhật số lượng
      gioHang.value[existingIndex].soLuongMua += itemMoi.soLuongMua;
      gioHang.value[existingIndex].thanhTien = gioHang.value[existingIndex].sanPham.gia * gioHang.value[existingIndex].soLuongMua;
      console.log("✅ Đã cập nhật số lượng sản phẩm từ đơn hàng:", itemMoi);
    }

    // Cập nhật thành tiền
    capNhatThanhTien();
  }

  // ===== COMPUTED PROPERTIES =====
  
  /**
   * Tổng số lượng sản phẩm trong giỏ hàng
   * @returns {number} Tổng số lượng
   */
  const tongSoLuong = computed(() => {
    return gioHang.value.reduce((tong, item) => tong + item.soLuongMua, 0);
  });

  /**
   * Tổng tiền hàng (chưa trừ khuyến mãi)
   * @returns {number} Tổng tiền hàng
   */
  const tongTienHang = computed(() => {
    return gioHang.value.reduce((tong, item) => tong + item.thanhTien, 0);
  });

  /**
   * Tổng khuyến mãi (tạm thời = 0, có thể tính sau)
   * @returns {number} Tổng khuyến mãi
   */
  const tongKhuyenMai = computed(() => {
    return 0; // TODO: Tính khuyến mãi nếu có
  });

  /**
   * Tổng giảm giá (tạm thời = 0, có thể tính sau)
   * @returns {number} Tổng giảm giá
   */
  const tongGiam = computed(() => {
    return 0; // TODO: Tính giảm giá nếu có
  });

  /**
   * Tổng thanh toán cuối cùng (tiền hàng - giảm giá - khuyến mãi)
   * @returns {number} Tổng thanh toán
   */
  const tongThanhToan = computed(() => {
    return tongTienHang.value - tongKhuyenMai.value - tongGiam.value;
  });

  // ===== RETURN EXPORTS =====
  return {
    // State - Dữ liệu giỏ hàng
    gioHang,
    isSwitchingOrder,      // Flag để tránh cập nhật IMEI không cần thiết

    // Actions - Các hàm thao tác giỏ hàng
    themSanPham,           // Thêm sản phẩm vào giỏ
    loadSanPhamTuDonHang,  // Load sản phẩm từ đơn hàng đã lưu
    capNhatSoLuong,        // Cập nhật số lượng sản phẩm
    capNhatSoLuongTheoImei, // Cập nhật số lượng theo IMEI
    capNhatThanhTien,      // Cập nhật thành tiền
    xoaSanPham,            // Xóa sản phẩm khỏi giỏ
    xoaImei,               // Xóa IMEI cụ thể
    xoaToanBoGioHang,      // Xóa toàn bộ giỏ hàng

    // Computed - Các giá trị tính toán tự động
    tongSoLuong,           // Tổng số lượng sản phẩm
    tongTienHang,          // Tổng tiền hàng
    tongKhuyenMai,         // Tổng khuyến mãi
    tongGiam,              // Tổng giảm giá
    tongThanhToan,         // Tổng thanh toán cuối cùng
  };
}
