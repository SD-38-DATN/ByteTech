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
  setImeiToStock, // Chuyển IMEI về trạng thái "còn hàng" (status = 1)
  setImeiToCart, // Chuyển IMEI về trạng thái "tạm giữ" (status = 5)
  setImeiToSold, // Chuyển IMEI về trạng thái "đã bán" (status = 0)
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
  async function themSanPham(
    sanPham,
    soLuong = 1,
    imeiList = [],
    autoCreateOrder = false
  ) {
    // Cập nhật trạng thái IMEI thành 5 nếu có IMEI (sử dụng API thống nhất)
    if (imeiList && Array.isArray(imeiList) && imeiList.length > 0) {
      try {
        const imeiNumbers = imeiList.map((imei) => imei.imei || imei);
        await setImeiToCart(imeiNumbers);
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
      }
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

      // SỬA LỖI: Xử lý maSKU cho phụ kiện - KHÔNG ghi đè maSKU
      // Phụ kiện phải có maSKUPhuKien và maSKU = null để logic phân biệt hoạt động
      if (sanPham.maSKUPhuKien && !sanPham.maSKU) {
        itemMoi.maSKUPhuKien = sanPham.maSKUPhuKien;
        itemMoi.maSKU = null; // Đảm bảo maSKU là null cho phụ kiện
      }

      //  SỬA LỖI: Xử lý thuộc tính cho phụ kiện - kiểm tra cấu trúc lồng nhau
      if (sanPham.sanPham && sanPham.sanPham.thuocTinh) {
        itemMoi.thuocTinh = sanPham.sanPham.thuocTinh;
      } else if (sanPham.thuocTinhPhuKien) {
        itemMoi.thuocTinh = sanPham.thuocTinhPhuKien;
      } else if (sanPham.thuocTinh) {
        itemMoi.thuocTinh = sanPham.thuocTinh;
      } else if (sanPham.thuocTinhSanPham) {
        itemMoi.thuocTinh = sanPham.thuocTinhSanPham;
      } else if (!itemMoi.thuocTinh) {
        // Nếu không có thuộc tính, đặt giá trị mặc định
        itemMoi.thuocTinh = "N/A";
      }

      // SỬA LỖI: Xử lý giá cho phụ kiện - kiểm tra cấu trúc lồng nhau
      if (sanPham.sanPham && sanPham.sanPham.gia) {
        itemMoi.gia = sanPham.sanPham.gia;
      } else if (sanPham.giaPhuKien) {
        itemMoi.gia = sanPham.giaPhuKien;
      } else if (sanPham.gia) {
        itemMoi.gia = sanPham.gia;
      } else if (sanPham.giaSanPham) {
        itemMoi.gia = sanPham.giaSanPham;
      }

      //  SỬA LỖI: Đảm bảo giá không bị null/undefined
      if (!itemMoi.gia || isNaN(itemMoi.gia)) {
        itemMoi.gia = 0;
      }

      //SỬA LỖI: Cập nhật lại thành tiền với giá đã sửa
      itemMoi.thanhTien = itemMoi.gia * itemMoi.soLuongMua;

      //  SỬA LỖI: Xử lý loại sản phẩm cho phụ kiện
      if (sanPham.maSKUPhuKien && !sanPham.maSKU) {
        itemMoi.loai = "Phụ kiện";
        console.log(
          "✅ Thêm sản phẩm: Xác định loại dựa trên maSKUPhuKien: Phụ kiện"
        );
      } else if (
        itemMoi.maSKU &&
        (itemMoi.maSKU.includes("PK-") || itemMoi.maSKU.includes("ANK-"))
      ) {
        itemMoi.loai = "Phụ kiện";
      } else {
        itemMoi.loai = "Sản phẩm chính";
      }

      // Cập nhật số lượng theo IMEI nếu có
      if (imeiList && Array.isArray(imeiList) && imeiList.length > 0) {
        itemMoi.soLuongMua = imeiList.length;
      }
      gioHang.value.push(itemMoi);
    }

    // Cập nhật thành tiền
    capNhatThanhTien();
  }

  // Cập nhật số lượng sản phẩm
  function capNhatSoLuong(maSKU, soLuongMoi) {
    //  SỬA LỖI: Tìm sản phẩm theo cả maSKU và maSKUPhuKien
    const item = gioHang.value.find(
      (sp) => sp.maSKU === maSKU || sp.maSKUPhuKien === maSKU
    );
    if (item && soLuongMoi > 0) {
      const soLuongCu = item.soLuongMua;
      item.soLuongMua = soLuongMoi;
      capNhatThanhTien();

      //  TỰ ĐỘNG LƯU vào localStorage khi cập nhật số lượng
      // Emit event để parent component lưu vào localStorage (chỉ khi thực sự có thay đổi)
      if (soLuongMoi !== soLuongCu) {
        window.dispatchEvent(
          new CustomEvent("gioHangChanged", {
            detail: { action: "capNhatSoLuong", maSKU, soLuongMoi },
          })
        );
      }
    }
  }

  // Cập nhật số lượng dựa trên IMEI
  function capNhatSoLuongTheoImei(maSKU) {
    //  SỬA LỖI: Tìm sản phẩm theo cả maSKU và maSKUPhuKien
    const item = gioHang.value.find(
      (sp) => sp.maSKU === maSKU || sp.maSKUPhuKien === maSKU
    );
    if (item && item.imeiList) {
      const soLuongCu = item.soLuongMua;
      item.soLuongMua = item.imeiList.length;
      capNhatThanhTien();

      // ✅ TỰ ĐỘNG LƯU vào localStorage khi cập nhật số lượng theo IMEI

      // Emit event để parent component lưu vào localStorage (chỉ khi thực sự có thay đổi)
      if (item.soLuongMua !== soLuongCu) {
        window.dispatchEvent(
          new CustomEvent("gioHangChanged", {
            detail: {
              action: "capNhatSoLuongTheoImei",
              maSKU,
              soLuongMoi: item.soLuongMua,
            },
          })
        );
      }
    }
  }

  /**
   * Xóa sản phẩm khỏi giỏ hàng
   * @param {string} maSKU - Mã SKU của sản phẩm cần xóa (có thể là maSKU hoặc maSKUPhuKien)
   * @returns {Promise<void>}
   */
  async function xoaSanPham(maSKU) {
    //  SỬA LỖI: Tìm sản phẩm theo cả maSKU và maSKUPhuKien
    const index = gioHang.value.findIndex(
      (item) => item.maSKU === maSKU || item.maSKUPhuKien === maSKU
    );
    if (index !== -1) {
      const item = gioHang.value[index];

      // Cập nhật trạng thái IMEI về 1 nếu có IMEI (sử dụng API thống nhất)
      if (
        item.imeiList &&
        Array.isArray(item.imeiList) &&
        item.imeiList.length > 0
      ) {
        try {
          const imeiNumbers = item.imeiList.map((imei) => imei.imei || imei);
          await setImeiToStock(imeiNumbers);
        } catch (error) {
          // Không dừng quá trình xóa sản phẩm, chỉ log lỗi
        }
      }
      gioHang.value.splice(index, 1);
      capNhatThanhTien();

      //  SỬA LỖI: Đảm bảo đơn hàng được cập nhật ngay lập tức khi xóa sản phẩm
      // Trigger event để component cha biết cần cập nhật đơn hàng

      // Dispatch custom event để thông báo cho component cha
      if (typeof window !== "undefined") {
        window.dispatchEvent(
          new CustomEvent("cart-item-deleted", {
            detail: { maSKU, remainingItems: gioHang.value.length },
          })
        );
      }
    }
  }

  /**
   * Xóa IMEI cụ thể khỏi sản phẩm
   * @param {string} maSKU - Mã SKU của sản phẩm (có thể là maSKU hoặc maSKUPhuKien)
   * @param {number} imeiIndex - Index của IMEI cần xóa
   * @returns {Promise<void>}
   */
  async function xoaImei(maSKU, imeiIndex) {
    // SỬA LỖI: Tìm sản phẩm theo cả maSKU và maSKUPhuKien
    const item = gioHang.value.find(
      (sp) => sp.maSKU === maSKU || sp.maSKUPhuKien === maSKU
    );
    if (item && item.imeiList && item.imeiList.length > imeiIndex) {
      const imeiRemoved = item.imeiList.splice(imeiIndex, 1)[0];

      // Cập nhật trạng thái IMEI về 1 khi xóa IMEI (sử dụng API thống nhất)
      if (imeiRemoved) {
        try {
          const imeiNumber = imeiRemoved.imei || imeiRemoved;
          await setImeiToStock(imeiNumber);
        } catch (error) {
          // Không dừng quá trình xóa IMEI, chỉ log lỗi
        }
      }

      // Cập nhật số lượng dựa trên số IMEI còn lại
      item.soLuongMua = item.imeiList.length;

      // Nếu không còn IMEI nào, xóa sản phẩm khỏi giỏ
      if (item.imeiList.length === 0) {
        await xoaSanPham(maSKU);
      } else {
        // Cập nhật thành tiền
        capNhatThanhTien();
      }
    }
  }

  /**
   * Cập nhật thành tiền cho tất cả sản phẩm trong giỏ
   * @returns {void}
   */
  function capNhatThanhTien() {
    gioHang.value.forEach((item) => {
      //  SỬA LỖI: Đảm bảo giá hợp lệ trước khi tính toán
      const gia = item.gia || 0;
      const soLuong = item.soLuongMua || 0;

      // Kiểm tra và sửa giá nếu cần
      if (isNaN(gia) || gia < 0) {
        item.gia = 0;
        item.thanhTien = 0;
      } else {
        item.thanhTien = gia * soLuong;
      }
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
    gioHang.value.forEach((item) => {
      if (
        item.imeiList &&
        Array.isArray(item.imeiList) &&
        item.imeiList.length > 0
      ) {
        const imeiNumbers = item.imeiList.map((imei) => imei.imei || imei);
        allImeis.push(...imeiNumbers);
      }
    });

    if (allImeis.length > 0 && !skipImeiUpdate && !isSwitchingOrder.value) {
      try {
        await setImeiToStock(allImeis);
      } catch (error) {
        // Không dừng quá trình xóa giỏ hàng, chỉ log lỗi
      }
    } else if (skipImeiUpdate || isSwitchingOrder.value) {
    }
    gioHang.value = [];
  }

  /**
   * Load sản phẩm từ đơn hàng đã lưu (THAY THẾ giỏ hàng hiện tại)
   * @param {Object} sanPham - Thông tin sản phẩm
   * @param {number} soLuong - Số lượng sản phẩm
   * @param {Array} imeiList - Danh sách IMEI
   * @returns {Promise<void>}
   */
  async function loadSanPhamTuDonHang(sanPham, soLuong, imeiList) {
    const itemMoi = {
      sanPham: sanPham, // Lưu đối tượng sản phẩm gốc
      soLuongMua: soLuong,
      thanhTien: sanPham.gia * soLuong,
      imeiList: imeiList ? [...imeiList] : [], // ✅ Tạo bản sao mảng IMEI
    };

    // Thêm các thuộc tính sản phẩm vào item để dễ truy cập
    Object.assign(itemMoi, sanPham);

    //  SỬA: Đảm bảo IMEI không bị ghi đè
    if (imeiList && imeiList.length > 0) {
      itemMoi.imeiList = [...imeiList];
    }

    //  SỬA LỖI: Xử lý thuộc tính cho phụ kiện khi load từ đơn hàng - kiểm tra cấu trúc lồng nhau
    if (sanPham.sanPham && sanPham.sanPham.thuocTinh) {
      itemMoi.thuocTinh = sanPham.sanPham.thuocTinh;
    } else if (sanPham.thuocTinhPhuKien) {
      itemMoi.thuocTinh = sanPham.thuocTinhPhuKien;
    } else if (sanPham.thuocTinh) {
      // Nếu đã có thuộc tính từ đơn hàng, sử dụng nó
      itemMoi.thuocTinh = sanPham.thuocTinh;
    } else if (sanPham.thuocTinhSanPham) {
      itemMoi.thuocTinh = sanPham.thuocTinhSanPham;
    } else if (!itemMoi.thuocTinh) {
      // Nếu không có thuộc tính, đặt giá trị mặc định
      itemMoi.thuocTinh = "N/A";
    }

    // ✅ SỬA LỖI: Xử lý giá cho phụ kiện khi load từ đơn hàng - kiểm tra cấu trúc lồng nhau
    if (sanPham.sanPham && sanPham.sanPham.gia) {
      itemMoi.gia = sanPham.sanPham.gia;
    } else if (sanPham.giaPhuKien) {
      itemMoi.gia = sanPham.giaPhuKien;
    } else if (sanPham.gia) {
      // Nếu đã có giá từ đơn hàng, sử dụng nó
      itemMoi.gia = sanPham.gia;
    } else if (sanPham.giaSanPham) {
      itemMoi.gia = sanPham.giaSanPham;
    }

    //  SỬA LỖI: Đảm bảo giá không bị null/undefined khi load từ đơn hàng
    if (!itemMoi.gia || isNaN(itemMoi.gia)) {
      itemMoi.gia = 0;
    }

    //  SỬA LỖI: Cập nhật lại thành tiền với giá đã sửa
    itemMoi.thanhTien = itemMoi.gia * itemMoi.soLuongMua;

    // SỬA LỖI: Xử lý loại sản phẩm cho phụ kiện khi load từ đơn hàng
    // Ưu tiên sử dụng thông tin loại đã được lưu trong đơn hàng
    if (sanPham.loai) {
      // Nếu đã có thông tin loại từ đơn hàng, sử dụng nó
      itemMoi.loai = sanPham.loai;
    } else if (sanPham.maSKUPhuKien && !sanPham.maSKU) {
      itemMoi.loai = "Phụ kiện";
    } else if (
      itemMoi.maSKU &&
      (itemMoi.maSKU.includes("PK-") || itemMoi.maSKU.includes("ANK-"))
    ) {
      // Kiểm tra pattern mã SKU để xác định phụ kiện
      itemMoi.loai = "Phụ kiện";
    } else {
      itemMoi.loai = "Sản phẩm chính";
    }

    // SỬA LỖI: Đảm bảo maSKU và maSKUPhuKien được set đúng cho phụ kiện
    // Phụ kiện phải có maSKUPhuKien và maSKU = null để logic phân biệt hoạt động
    if (itemMoi.loai === "Phụ kiện" && sanPham.maSKUPhuKien && !sanPham.maSKU) {
      itemMoi.maSKUPhuKien = sanPham.maSKUPhuKien;
      itemMoi.maSKU = null; // Đảm bảo maSKU là null cho phụ kiện
    }

    // ✅ SỬA LỖI: Khi load từ đơn hàng đã lưu, THAY THẾ hoàn toàn thay vì cộng dồn
    // Kiểm tra xem sản phẩm đã tồn tại chưa để tránh trùng lặp
    //  SỬA LỖI: Kiểm tra cả maSKU và maSKUPhuKien
    const existingIndex = gioHang.value.findIndex((item) => {
      const skuMatch = item.maSKU
        ? item.maSKU === itemMoi.maSKU
        : item.maSKUPhuKien === itemMoi.maSKUPhuKien;
      return (
        skuMatch &&
        JSON.stringify(item.imeiList) === JSON.stringify(itemMoi.imeiList)
      );
    });

    if (existingIndex === -1) {
      // Sản phẩm chưa tồn tại, thêm mới
      gioHang.value.push(itemMoi);
    } else {
      // ✅ QUAN TRỌNG: Khi load từ đơn hàng, THAY THẾ hoàn toàn thay vì cộng dồn
      // Điều này tránh việc tổng tiền bị cộng dồn khi load lại đơn hàng lần 2
      gioHang.value[existingIndex] = itemMoi;
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
    isSwitchingOrder, // Flag để tránh cập nhật IMEI không cần thiết

    // Actions - Các hàm thao tác giỏ hàng
    themSanPham, // Thêm sản phẩm vào giỏ
    loadSanPhamTuDonHang, // Load sản phẩm từ đơn hàng đã lưu
    capNhatSoLuong, // Cập nhật số lượng sản phẩm
    capNhatSoLuongTheoImei, // Cập nhật số lượng theo IMEI
    capNhatThanhTien, // Cập nhật thành tiền
    xoaSanPham, // Xóa sản phẩm khỏi giỏ
    xoaImei, // Xóa IMEI cụ thể
    xoaToanBoGioHang, // Xóa toàn bộ giỏ hàng

    // Computed - Các giá trị tính toán tự động
    tongSoLuong, // Tổng số lượng sản phẩm
    tongTienHang, // Tổng tiền hàng
    tongKhuyenMai, // Tổng khuyến mãi
    tongGiam, // Tổng giảm giá
    tongThanhToan, // Tổng thanh toán cuối cùng
  };
}
