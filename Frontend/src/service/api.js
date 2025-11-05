import axios from "axios";

// ==================== Khởi tạo axios ====================
const api = axios.create({
  baseURL: "http://localhost:8081/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// ==================== Utils ====================
// Decode JWT đơn giản (không cần jwt-decode lib)
function decodeJwt(token) {
  try {
    const base64Url = token.split(".")[1]; // payload
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    return JSON.parse(window.atob(base64));
  } catch (e) {
    return null;
  }
}

// Kiểm tra token còn hạn hay không
// export function validateToken() {
//   let token = localStorage.getItem("token");

//   // Nếu không có token hoặc token null/undefined/chuỗi rỗng -> xoá luôn
//   if (!token || token.trim() === "" || token === "null" || token === "undefined") {
//     localStorage.removeItem("token");
//     return null;
//   }

//   try {
//     const decoded = decodeJwt(token);
//     const now = Date.now() / 1000;

//     // Nếu decode lỗi hoặc đã hết hạn -> xoá luôn
//     if (!decoded || !decoded.exp || decoded.exp < now) {
//       console.warn("[AUTH] Token hết hạn hoặc không hợp lệ, xoá khỏi localStorage");
//       localStorage.removeItem("token");
//       return null;
//     }

//     // Token hợp lệ
//     return token;
//   } catch (e) {
//     console.error("[AUTH] Lỗi khi decode token:", e);
//     localStorage.removeItem("token");
//     return null;
//   }
// }

export function validateToken() {
  let token = localStorage.getItem("token");

  // Nếu không có token hoặc token null/undefined/chuỗi rỗng -> xoá hết
  if (
    !token ||
    token.trim() === "" ||
    token === "null" ||
    token === "undefined"
  ) {
    localStorage.clear();
    return null;
  }

  try {
    const decoded = decodeJwt(token);
    const now = Date.now() / 1000;

    // Nếu decode lỗi hoặc đã hết hạn -> xoá hết
    if (!decoded || !decoded.exp || decoded.exp < now) {
      console.warn(
        "[AUTH] Token hết hạn hoặc không hợp lệ, xoá toàn bộ localStorage"
      );
      localStorage.clear();
      return null;
    }

    // Kiểm tra quá 10 tiếng từ lúc login
    const loginTime = localStorage.getItem("loginTime");
    const TEN_HOURS = 10 * 60 * 60 * 1000;
    if (loginTime && Date.now() - parseInt(loginTime, 10) > TEN_HOURS) {
      console.warn("[AUTH] Đăng nhập quá 10 tiếng, xoá toàn bộ localStorage");
      localStorage.clear();
      return null;
    }

    // Token hợp lệ
    return token;
  } catch (e) {
    console.error("[AUTH] Lỗi khi decode token:", e);
    localStorage.clear();
    return null;
  }
}

// ==================== Interceptors ====================
api.interceptors.request.use(
  (config) => {
    // Mỗi khi có request API (một dạng hoạt động), reset bộ đếm
    resetIdleTimer();

    const token = validateToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.info("[AUTH] Using token:", token);
    } else {
      delete config.headers.Authorization;
      console.info("[AUTH] No token found");
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ==================== Tự động Đăng xuất khi không hoạt động ====================
// Giá trị cũ: 3600000 (tương đương 1 giờ)

// Giá trị mới: 1800000 (tương đương 30 phút)
let idleTimer = null;
const IDLE_TIMEOUT = 1800000; // 1 giờ = 60 * 60 * 1000 ms

// Hành động sẽ thực hiện khi hết giờ
const handleIdleLogout = async () => {
  // Chỉ logout nếu có token (người dùng đang đăng nhập)
  if (localStorage.getItem("token")) {
    console.warn("[IDLE] Người dùng không hoạt động, tự động đăng xuất.");
    alert(
      "Bạn đã không hoạt động trong một thời gian dài và sẽ được tự động đăng xuất."
    );

    try {
      await logoutApi(); // Gọi API để vô hiệu hóa token ở backend
    } catch (error) {
      console.error("Lỗi khi gọi API logout:", error);
    } finally {
      localStorage.removeItem("token"); // Xóa token ở client
      localStorage.clear(); // Xóa toàn bộ localStorage
      window.location.href = "/login"; // Chuyển hướng về trang đăng nhập
    }
  }
};

// Reset bộ đếm thời gian
const resetIdleTimer = () => {
  clearTimeout(idleTimer);
  idleTimer = setTimeout(handleIdleLogout, IDLE_TIMEOUT);
};

// Bắt đầu theo dõi hoạt động của người dùng
export const startIdleTimer = () => {
  // Lắng nghe các sự kiện hoạt động trên toàn trang
  const events = ["mousemove", "keydown", "click", "scroll"];
  events.forEach((event) => {
    window.addEventListener(event, resetIdleTimer, true);
  });

  // Khởi động bộ đếm lần đầu
  resetIdleTimer();
  console.log("[IDLE] Bắt đầu theo dõi không hoạt động.");
};

// Dọn dẹp
export const stopIdleTimer = () => {
  clearTimeout(idleTimer);
  const events = ["mousemove", "keydown", "click", "scroll"];
  events.forEach((event) => {
    window.removeEventListener(event, resetIdleTimer, true);
  });
  console.log("[IDLE] Dừng theo dõi không hoạt động.");
};

// ======= Auth =======
// Hàm này được gọi bởi handleIdleLogout ở trên
export const logoutApi = async () => {
  return await api.post("/auth/logout");
};

// Sửa lại hàm login để khởi động bộ đếm
// export const loginApi = async (credentials) => {
//   const res = await api.post("/auth/login", credentials);
//   if (res.data.token) {
//     localStorage.setItem("token", res.data.token);
//     startIdleTimer(); // Bắt đầu theo dõi khi login thành công
//   }
//   return res.data;
// };

export const loginApi = async (credentials) => {
  const res = await api.post("/auth/login", credentials);
  if (res.data.token) {
    localStorage.setItem("token", res.data.token);
    localStorage.setItem("loginTime", Date.now()); // lưu thời điểm login
    startIdleTimer();
  }
  return res.data;
};

// Sửa lại hàm callback Google để khởi động bộ đếm
// export const handleGoogleCallback = () => {
//   const params = new URLSearchParams(window.location.search);
//   const token = params.get("token");
//   if (token) {
//     localStorage.setItem("token", token);
//     startIdleTimer(); // Bắt đầu theo dõi khi login thành công
//     return token;
//   }
//   return null;
// };

export const handleGoogleCallback = () => {
  const params = new URLSearchParams(window.location.search);
  const token = params.get("token");
  if (token) {
    localStorage.setItem("token", token);
    localStorage.setItem("loginTime", Date.now()); // lưu thời điểm login
    startIdleTimer();
    return token;
  }
  return null;
};

// ======= Danh Mục =======
export const fetchDanhMuc = async () => {
  const res = await api.get("/danh-muc");
  return res.data;
};

// ======= Danh Mục Phụ Kiện =======
export const fetchDanhMucPhuKien = async () => {
  // Thay đổi endpoint API
  const res = await api.get("/danh-muc-phu-kien");
  return res.data;
};

export const getPhuKienByDanhMuc = async (maDanhMucPhuKien) => {
  const res = await api.get(`/phu-kien/by-danh-muc/${maDanhMucPhuKien}`);
  return res.data; // ✅ ĐÃ TRẢ VỀ DỮ LIỆU JSON TRỰC TIẾP
};

// ======= Products =======
export const fetchProducts = async () => {
  const res = await api.get("/");
  return res.data;
};

export const SanPham = () => api.get("/sanpham");

export const getDanhMuc = (maDanhMuc) => {
  return api.get(`/sanpham/${maDanhMuc}/DanhMucSanPham`);
};

export const searchSanPham = (tenSanPham) => {
  return api.get(`/sanpham/search`, {
    params: { tenSanPham },
  });
};

export const SanPhamChiTiet = (id) => api.get(`/sanpham/${id}`);

// ======= Users =======
export const fetchUsers = async () => {
  const res = await api.get("/users");
  return res.data;
};

export const fetchUserById = async (id) => {
  const res = await api.get(`/users/${id}`);
  return res.data;
};

export const createUser = async (user) => {
  const res = await api.post("/users", user);
  return res.data;
};

export const updateUser = async (id, user) => {
  const res = await api.put(`/users/${id}`, user);
  return res.data;
};

export const deleteUser = async (id) => {
  return await api.delete(`/users/${id}`);
};

export const getCurrentUser = async () => {
  const res = await api.get("/users/me");
  return res.data;
};

// =========gio hang==============
export const fetchCart = async () => {
  const res = await api.get("/cart");
  return res.data;
};

export const updateCartQuantity = (updates) => {
  return api.put("/cart/update", updates);
};

export const deleteCartItem = (chiTietId) => {
  return api.delete(`/cart/${chiTietId}`);
};

export const addToCart = (bienTheDTO) => {
  return api.post("/cart/add", bienTheDTO);
};

// ===========don hang===========
export const createOrder = async (order) => {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("Chưa đăng nhập, thiếu token");
  }

  const res = await api.post("/donhang/create", order, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return res.data;
};

// ===========voucher===========
export const fetchMyVouchers = async () => {
  const res = await api.get("/voucher/me");
  return res.data;
};

export const fetchActiveVouchers = async () => {
  const response = await api.get("/voucher/active");
  return response.data;
};

export const addUserVoucher = async (codeVoucher) => {
  // Body request là một object có key là "codeVoucher"
  const response = await api.post("/user-vouchers/add", { codeVoucher });
  return response.data; // Trả về message từ server { message: "..." } hoặc { error: "..." }
};

export const Voucherforme = async () => {
  const res = await api.get("/voucher/my-vouchers"); // ✅ đúng với controller bạn gửi
  return res.data;
};

// =========== feedback ===========
export const fetchFeedbackBySanPham = async (maSanPham) => {
  const res = await api.get(`/feedback/sanpham/${maSanPham}`);
  return res.data;
};

export const addFeedback = async (feedback) => {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("Chưa đăng nhập, thiếu token");
  }
  const res = await api.post("/feedback", feedback, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return res.data;
};

export const deleteFeedback = async (feedbackId) => {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("Chưa đăng nhập, thiếu token");
  }
  return await api.delete(`/feedback/${feedbackId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};
//=======feedback phu kien
export const fetchFeedbackByPhuKien = async (maPhuKien) => {
  const res = await api.get(`/feedback-phukien/phukien/${maPhuKien}`);
  return res.data;
};
export const addFeedbackPhuKien = async (feedback) => {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("Chưa đăng nhập, thiếu token");
  }

  // Gọi đến endpoint POST /api/feedback-phukien
  const res = await api.post("/feedback-phukien", feedback, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return res.data;
};
export const deleteFeedbackPhuKien = async (feedbackId) => {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("Chưa đăng nhập, thiếu token");
  }

  // Gọi đến endpoint DELETE /api/feedback-phukien/{id}
  return await api.delete(`/feedback-phukien/${feedbackId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
};

// truy van don hang
export const fetchMyPendingOrders = async () => {
  const response = await api.get("/donhang/me/cho-xac-nhan");
  return response.data; // Trả về mảng DonHangResponseDTO
};

export const xacNhanDonHang = async (maDonHang, items) => {
  const response = await api.put(`/donhang/${maDonHang}/xac-nhan`, { items });
  return response.data; // Trả về chuỗi "Đơn hàng #... đã được xác nhận..."
};

export const getAllPhuKien = () => api.get("/phu-kien");

// Lấy chi tiết phụ kiện theo id
export const PhuKienChiTiet = (id) => api.get(`/phu-kien/${id}`);

// Login bằng Google (Spring Security OAuth2)
export const loginWithGoogle = () => {
  window.location.href = "http://localhost:8081/oauth2/authorization/google";
};

// ========== Sản phẩm liên quan ==========
export const fetchSanPhamLienQuan = async (id) => {
  const res = await api.get(`/sanpham/${id}/lienquan`);
  return res.data;
};

//===================CRUD san pham======

// ==================== CRUD Sản phẩm (Admin) ====================

// Lấy toàn bộ sản phẩm
// export const fetchAllSanPhams = async () => (await api.get("/admin/dto")).data;
// export const createSanPham = async (data) => (await api.post("/admin/sanpham", data)).data;
// export const updateSanPham = async (id, data) => (await api.put(`/admin/sanpham/${id}`, data)).data;
// export const deleteSanPham = async (id) => (await api.delete(`/admin/${id}`)).data;

// // Biến thể
// export const createBienThe = async (spId, data) => (await api.post(`/admin/sanpham/${spId}/bienthe`, data)).data;
// export const updateBienThe = async (sku, data) => (await api.put(`/admin/bienthe/${sku}`, data)).data;

// // Thuộc tính
// export const createThuocTinh = async (sku, data) => (await api.post(`/admin/bienthe/${sku}/thuoctinh`, data)).data;
// export const updateThuocTinh = async (id, data) => (await api.put(`/admin/thuoctinh/${id}`, data)).data;

//================================================================================
//== BẮT ĐẦU PHẦN CẬP NHẬT CHO CRUD SẢN PHẨM ADMIN
//================================================================================

/**
 * Lấy toàn bộ sản phẩm (cho trang admin).
 * @returns {Promise<any>}
 */
export const fetchAllAdminProducts = async () => {
  // ✅ ĐÃ SỬA LẠI ĐƯỜNG DẪN: từ /admin/dto -> /admin/products
  return (await api.get("/admin/products")).data;
};

/**
 * Tạo một sản phẩm mới (bao gồm cả biến thể và thuộc tính).
 * @param {object} productData - DTO của sản phẩm.
 * @returns {Promise<any>}
 */
export const createAdminProduct = async (productData) => {
  // ✅ ĐÃ SỬA LẠI ĐƯỜNG DẪN: từ /admin/sanpham -> /admin/products
  return (await api.post("/admin/products", productData)).data;
};

/**
 * Cập nhật một sản phẩm đã có.
 * @param {number} id - ID của sản phẩm cần cập nhật.
 * @param {object} productData - DTO mới của sản phẩm.
 * @returns {Promise<any>}
 */
export const updateAdminProduct = async (id, productData) => {
  // ✅ ĐÃ SỬA LẠI ĐƯỜNG DẪN: từ /admin/sanpham/{id} -> /admin/products/{id}
  return (await api.put(`/admin/products/${id}`, productData)).data;
};

/**
 * Xóa một sản phẩm.
 * @param {number} id - ID của sản phẩm cần xóa.
 * @returns {Promise<any>}
 */
export const deleteAdminProduct = async (id) => {
  // ✅ ĐÃ SỬA LẠI ĐƯỜNG DẪN: từ /admin/{id} -> /admin/products/{id}
  return (await api.delete(`/admin/products/${id}`)).data;
};

/**
 * ✨ HÀM MỚI: Xóa một biến thể cụ thể.
 * @param {string} sku - Mã SKU của biến thể cần xóa.
 * @returns {Promise<any>}
 */
export const deleteAdminVariant = async (sku) => {
  return (await api.delete(`/admin/products/variants/${sku}`)).data;
};

/**
 * ✨ HÀM MỚI: Xóa một thuộc tính cụ thể.
 * @param {number} id - ID của thuộc tính cần xóa.
 * @returns {Promise<any>}
 */
export const deleteAdminAttribute = async (id) => {
  return (await api.delete(`/admin/products/attributes/${id}`)).data;
};

// ❌ CÁC HÀM CŨ NÀY ĐÃ ĐƯỢC LOẠI BỎ VÌ LOGIC ĐÃ GỘP VÀO create/updateAdminProduct
// export const createBienThe = async (spId, data) => (await api.post(`/admin/sanpham/${spId}/bienthe`, data)).data;
// export const updateBienThe = async (sku, data) => (await api.put(`/admin/bienthe/${sku}`, data)).data;
// export const createThuocTinh = async (sku, data) => (await api.post(`/admin/bienthe/${sku}/thuoctinh`, data)).data;
// export const updateThuocTinh = async (id, data) => (await api.put(`/admin/thuoctinh/${id}`, data)).data;

//... trong file src/service/api.js

// ================== IMEI API ==================
export const fetchAllImeis = () =>
  api.get("/admin/imeis").then((res) => res.data);
export const createImei = (payload) =>
  api.post("/admin/imeis", payload).then((res) => res.data);
export const updateImei = (id, payload) =>
  api.put(`/admin/imeis/${id}`, payload).then((res) => res.data);
export const deleteImei = (id) => api.delete(`/admin/imeis/${id}`);

// Hàm để lấy danh sách SKU gợi ý
export const fetchAllProductVariants = () =>
  api.get("/admin/products/variants-summary").then((res) => res.data);
// (Bạn cần tạo endpoint này ở backend để trả về list {sku, tenSanPham})

// ... trong file api.js

// ================== THÔNG SỐ KỸ THUẬT API ==================
export const fetchThongSoForSanPham = (maSanPham) =>
  api.get(`/admin/sanpham/${maSanPham}/thongso`).then((res) => res.data);
export const fetchAvailableLoaiThongSo = (maSanPham) =>
  api
    .get(`/admin/sanpham/${maSanPham}/available-loai-thongso`)
    .then((res) => res.data);
export const createThongSo = (maSanPham, payload) =>
  api.post(`/admin/sanpham/${maSanPham}/thongso`, payload);
export const updateThongSo = (maThongSo, payload) =>
  api.put(`/admin/thongso/${maThongSo}`, payload);
export const deleteThongSo = (maThongSo) =>
  api.delete(`/admin/thongso/${maThongSo}`);

// ... trong file api.js

// ================== LOẠI THÔNG SỐ API ==================
export const fetchAllLoaiThongSo = () =>
  api.get("/admin/loai-thongso").then((res) => res.data);
export const createLoaiThongSo = (payload) =>
  api.post("/admin/loai-thongso", payload);
export const updateLoaiThongSo = (id, payload) =>
  api.put(`/admin/loai-thongso/${id}`, payload);
export const deleteLoaiThongSo = (id) =>
  api.delete(`/admin/loai-thongso/${id}`);

// Giả sử bạn đã có API để lấy danh mục
export const fetchAllDanhMucs = () =>
  api.get("/admin/danhmuc").then((res) => res.data);

// ✅ HÀM MỚI (để tạo/sửa loại TS cho Phụ kiện)
export const createLoaiThongSoPhuKien = (payload) =>
  api.post("/admin/loai-thongso-phukien", payload);
export const updateLoaiThongSoPhuKien = (id, payload) =>
  api.put(`/admin/loai-thongso-phukien/${id}`, payload);
// Dùng lại deleteLoaiThongSo
// export const deleteLoaiThongSo = (id) => api.delete(`/admin/loai-thongso/${id}`);

// ✅ HÀM MỚI (để lấy tất cả Phụ kiện đang hoạt động)
export const fetchAllActivePhuKien = async () => {
  // Giả định bạn có endpoint này để lấy Phụ kiện có maDanhMucPhuKien
  return (await api.get("/phu-kien/active-all")).data;
};

// ==================== CRUD Khuyến Mại (Admin) ====================

export const fetchAllKhuyenMai = () => {
  return api.get("/khuyenmai").then((res) => res.data);
};

export const createKhuyenMai = (khuyenMaiData) => {
  return api.post("/khuyenmai", khuyenMaiData).then((res) => res.data);
};

export const updateKhuyenMai = (id, khuyenMaiData) => {
  return api.put(`/khuyenmai/${id}`, khuyenMaiData).then((res) => res.data);
};

export const deleteKhuyenMai = (id) => {
  return api.delete(`/khuyenmai/${id}`);
};

export const fetchAppliedVariantsForKhuyenMai = (khuyenMaiId) => {
  return api
    .get(`/khuyenmai/${khuyenMaiId}/applied-variants`)
    .then((res) => res.data);
};

// Cập nhật danh sách SKU áp dụng cho một Khuyến Mãi
export const applyKhuyenMaiToVariants = (khuyenMaiId, skuList) => {
  return api.put(`/khuyenmai/${khuyenMaiId}/apply-to-variants`, skuList);
};

// ==================== Bán Hàng Tại Quầy API ====================

// ===== UNIFIED API (TỐI ƯU NHẤT) =====

/**
 * API thống nhất: Cập nhật trạng thái IMEI (1 hoặc nhiều) với status int
 * @param {Object} data - Dữ liệu gửi lên
 * @param {string} data.imei - IMEI đơn lẻ (nếu cập nhật 1 IMEI)
 * @param {Array} data.imeiList - Danh sách IMEI (nếu cập nhật nhiều IMEI)
 * @param {number} data.status - Trạng thái: 0=bán, 1=còn hàng, 5=tạm giữ
 * @returns {Promise<Object>} Kết quả từ server
 */
export const updateImeiStatus = async (data) => {
  const res = await api.put("/banhangtaiquay/imei/update-status", data);
  return res.data;
};

/**
 * Chuyển IMEI về trạng thái "còn hàng" (status = 1)
 * @param {string|Array} imeiOrList - IMEI đơn lẻ hoặc danh sách IMEI
 * @returns {Promise<Object>} Kết quả từ server
 */
export const setImeiToStock = async (imeiOrList) => {
  const data = Array.isArray(imeiOrList)
    ? { imeiList: imeiOrList, status: 1 }
    : { imei: imeiOrList, status: 1 };
  return await updateImeiStatus(data);
};

/**
 * Chuyển IMEI về trạng thái "tạm giữ" khi thêm vào giỏ hàng (status = 5)
 * @param {string|Array} imeiOrList - IMEI đơn lẻ hoặc danh sách IMEI
 * @returns {Promise<Object>} Kết quả từ server
 */
export const setImeiToCart = async (imeiOrList) => {
  const data = Array.isArray(imeiOrList)
    ? { imeiList: imeiOrList, status: 5 }
    : { imei: imeiOrList, status: 5 };
  return await updateImeiStatus(data);
};

/**
 * Chuyển IMEI về trạng thái "đã bán" khi thanh toán (status = 0)
 * @param {string|Array} imeiOrList - IMEI đơn lẻ hoặc danh sách IMEI
 * @returns {Promise<Object>} Kết quả từ server
 */
export const setImeiToSold = async (imeiOrList) => {
  const data = Array.isArray(imeiOrList)
    ? { imeiList: imeiOrList, status: 0 }
    : { imei: imeiOrList, status: 0 };
  return await updateImeiStatus(data);
};

// ===== KHÁCH HÀNG API =====

/**
 * Tìm kiếm khách hàng theo số điện thoại
 * @param {string} phone - Số điện thoại cần tìm
 * @returns {Promise<Array>} Danh sách khách hàng tìm được
 */
export const searchCustomer = async (phone) => {
  const res = await api.get("/admin/ban-hang-tai-quay/khach-hang/tim-kiem", {
    params: { soDienThoai: phone },
  });
  return res.data;
};

// ===== SẢN PHẨM API =====

/**
 * Tìm kiếm sản phẩm theo SKU
 * @param {string} sku - SKU cần tìm
 * @returns {Promise<Array>} Danh sách sản phẩm tìm được
 */
// hiên tại không dùng đến
export const searchProductBySKU = async (sku) => {
  const res = await api.get("/banhangtaiquay/sanpham/search-sku", {
    params: { keyword: sku },
  });
  return res.data;
};

/**
 *  MỚI: Tìm kiếm sản phẩm CHỈ theo SKU (không tìm IMEI)
 * @param {string} sku - SKU cần tìm
 * @returns {Promise<Array>} Danh sách sản phẩm tìm được
 */
export const searchProductBySKUOnly = async (sku) => {
  const res = await api.get("/banhangtaiquay/sanpham/search-sku-only", {
    params: { sku: sku },
  });
  return res.data;
};

/**
 * Tìm kiếm sản phẩm theo IMEI 100%
 * @param {string} imei - IMEI cần tìm
 * @returns {Promise<Array>} Danh sách sản phẩm tìm được
 */
export const searchProductByIMEI = async (imei) => {
  const res = await api.get(`/banhangtaiquay/imei/tim-san-pham/${imei}`);
  return res.data;
};

/**
 * Test kết nối API
 * @returns {Promise<string>} Thông báo trạng thái API
 */
export const testAPI = async () => {
  const res = await api.get("/banhangtaiquay/sanpham/test");
  return res.data;
};

/**
 * Tìm kiếm sản phẩm kết hợp SKU + IMEI
 * @param {string} sku - SKU phải đúng 100%
 * @param {string} imei - IMEI bắt đầu với chuỗi tìm kiếm
 * @returns {Promise<Array>} Danh sách sản phẩm tìm được
 */
export const searchProductCombined = async (sku, imei) => {
  const res = await api.get("/banhangtaiquay/sanpham/search-combined", {
    params: {
      sku: sku, // SKU phải đúng 100%
      imei: imei, // IMEI bắt đầu với chuỗi tìm kiếm (không phải LIKE)
    },
  });
  return res.data;
};

// ===== IMEI API =====

/**
 * Load danh sách IMEI cho sản phẩm chính
 * @param {string} sku - Mã SKU của sản phẩm
 * @returns {Promise<Array>} Danh sách IMEI có sẵn
 */
export const loadIMEIForProduct = async (sku) => {
  const res = await api.get(`/banhangtaiquay/sanpham/${sku}/imei`);
  return res.data;
};

/**
 * Load danh sách IMEI cho phụ kiện
 * @param {string} sku - Mã SKU của phụ kiện
 * @returns {Promise<Array>} Danh sách IMEI có sẵn
 */
export const loadIMEIForAccessory = async (sku) => {
  const res = await api.get(`/banhangtaiquay/imei/lookup-by-sku/${sku}`);
  return res.data;
};

/**
 * Load danh sách IMEI với filter IMEI
 * @param {string} sku - Mã SKU của sản phẩm/phụ kiện
 * @param {string} imeiFilter - Chuỗi IMEI để filter (bắt đầu với)
 * @returns {Promise<Array>} Danh sách IMEI có sẵn với filter
 */
export const loadIMEIWithFilter = async (sku, imeiFilter) => {
  const res = await api.get(`/banhangtaiquay/sanpham/${sku}/imei/search`, {
    params: { imei: imeiFilter },
  });
  return res.data;
};

// ==================== VOUCHER BAN HANG TAI QUAY API ====================

/**
 * Lấy danh sách voucher hợp lệ cho bán hàng tại quầy
 * Điều kiện: trangThai = 1, ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc
 * @returns {Promise<Array>} Danh sách voucher hợp lệ
 */
export const fetchVouchersForBanHangTaiQuay = async () => {
  const res = await api.get("/banhangtaiquay/voucher/valid");
  return res.data;
};

/**
 * Lấy danh sách voucher hợp lệ cho bán hàng tại quầy theo tổng tiền đơn hàng
 * Điều kiện: trangThai = 1, ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc, dieuKienGiam <= tongTienDonHang
 * @param {number} tongTienDonHang - Tổng tiền đơn hàng
 * @returns {Promise<Array>} Danh sách voucher hợp lệ
 */
export const fetchVouchersForBanHangTaiQuayByAmount = async (
  tongTienDonHang
) => {
  const res = await api.get("/banhangtaiquay/voucher/valid-by-amount", {
    params: { tongTienDonHang },
  });
  return res.data;
};

/**
 * Kiểm tra voucher có hợp lệ không (theo điều kiện đơn hàng)
 * @param {string} codeVoucher - Mã voucher
 * @param {number} tongTienDonHang - Tổng tiền đơn hàng
 * @returns {Promise<Object>} Thông tin voucher và số tiền giảm
 */
export const validateVoucherForBanHang = async (
  codeVoucher,
  tongTienDonHang
) => {
  const res = await api.post("/banhangtaiquay/voucher/validate", {
    codeVoucher,
    tongTienDonHang,
  });
  return res.data;
};

// ==================== ĐƠN HÀNG BAN HANG TAI QUAY API ====================

/**
 * Lưu đơn hàng với trạng thái = 4 (IMEI status = 5)
 * @param {Object} orderData - Dữ liệu đơn hàng
 * @returns {Promise<Object>} Thông tin đơn hàng đã lưu
 */
export const luuDonHang = async (orderData) => {
  const res = await api.post("/banhangtaiquay/thanh-toan/luu-don", orderData);
  return res.data;
};

/**
 * Thanh toán đơn hàng - Trừ số lượng tồn, cập nhật trạng thái "Đã thanh toán"
 * @param {Object} orderData - Dữ liệu đơn hàng
 * @returns {Promise<Object>} Thông tin đơn hàng đã thanh toán
 */
export const thanhToanDonHang = async (orderData) => {
  const res = await api.post(
    "/banhangtaiquay/thanh-toan/thanh-toan",
    orderData
  );
  return res.data;
};

// ==================== ĐƠN HÀNG ĐÃ LƯU API ====================

/**
 * Lấy danh sách đơn hàng đã lưu (trạng thái = 5)
 * @param {number|null} userId - ID của user (null để lấy tất cả)
 * @returns {Promise<Array>} Danh sách đơn hàng đã lưu
 */
export const getDonHangLuu = async (userId = null) => {
  const params = userId ? { userId } : {};
  const res = await api.get("/banhangtaiquay/don-hang-luu", { params });
  return res.data;
};

/**
 * Lấy chi tiết đơn hàng đã lưu theo mã đơn hàng
 * @param {number} maDonHang - Mã đơn hàng
 * @returns {Promise<Object>} Chi tiết đơn hàng đã lưu
 */
export const getDonHangLuuByMaDonHang = async (maDonHang) => {
  const res = await api.get(`/banhangtaiquay/don-hang-luu/${maDonHang}`);
  return res.data;
};

/**
 * Đếm số lượng đơn hàng đã lưu
 * @param {number|null} userId - ID của user (null để đếm tất cả)
 * @returns {Promise<number>} Số lượng đơn hàng
 */
export const countDonHangLuu = async (userId = null) => {
  const params = userId ? { userId } : {};
  const res = await api.get("/banhangtaiquay/don-hang-luu/count", { params });
  return res.data;
};

/**
 * Lấy đơn hàng đã lưu của user hiện tại
 * @param {number} userId - ID của user hiện tại
 * @returns {Promise<Array>} Danh sách đơn hàng của user
 */
export const getMyDonHangLuu = async (userId) => {
  const res = await api.get("/banhangtaiquay/don-hang-luu/my-orders", {
    params: { userId },
  });
  return res.data;
};

/**
 * Xóa đơn hàng đã lưu (chuyển trạng thái 5 → 3 và IMEI về 1)
 * @param {number} maDonHang - Mã đơn hàng
 * @returns {Promise<string>} - Thông báo kết quả
 */
export const xoaDonHangLuu = async (maDonHang) => {
  const res = await api.delete(`/banhangtaiquay/don-hang-luu/${maDonHang}`);
  return res.data;
};

// ==================== THỐNG KÊ API ====================

/**
 * Lấy tổng doanh thu từ tất cả đơn hàng có trạng thái = 2
 * @returns {Promise<Object>} {tongDoanhThu, soLuongSanPham, soLuongDonHang}
 */
export const getTongDoanhThu = async () => {
  const res = await api.get("/thongke/tong-quan/tong-doanh-thu");
  return res.data;
};

/**
 * Lấy tổng doanh thu theo năm hiện tại từ đơn hàng có trạng thái = 2
 * @returns {Promise<Object>} {tongDoanhThu, soLuongSanPham, soLuongDonHang}
 */
export const getDoanhThuNamHienTai = async () => {
  const res = await api.get("/thongke/tong-quan/doanh-thu-nam-hien-tai");
  return res.data;
};

/**
 * Lấy danh sách các năm có đơn hàng (từ năm sớm nhất đến năm hiện tại)
 * @returns {Promise<Array<number>>} Danh sách năm
 */
export const getDanhSachNam = async () => {
  const res = await api.get("/thongke/tong-quan/danh-sach-nam");
  return res.data;
};

/**
 * Lấy thống kê với filter
 * @param {Object} params - Các tham số filter
 * @param {string} params.dayWeekFilter - "today", "yesterday", "this-week", "last-week" hoặc null
 * @param {number} params.month - Tháng (1-12) hoặc null
 * @param {number} params.year - Năm hoặc null
 * @param {string} params.dateFrom - Ngày bắt đầu (yyyy-MM-dd) hoặc null
 * @param {string} params.dateTo - Ngày kết thúc (yyyy-MM-dd) hoặc null
 * @returns {Promise<Object>} {tongDoanhThu, soLuongSanPham, soLuongDonHangThanhCong, soLuongDonHangHuy}
 */
export const getThongKeFilter = async (params) => {
  const res = await api.get("/thongke/tong-quan/filter", { params });
  return res.data;
};

/**
 * Lấy dữ liệu biểu đồ doanh thu theo filter
 * @param {Object} params - Các tham số filter (giống getThongKeFilter)
 * @returns {Promise<Object>} {labels: string[], values: number[]} - Dữ liệu cho biểu đồ
 */
export const getDoanhThuChartData = async (params) => {
  const res = await api.get("/thongke/tong-quan/chart-doanh-thu", { params });
  return res.data;
};

// Dòng cuối cùng của file (trước export default)
// Dòng cuối cùng của file

// Dòng cuối cùng của file (trước export default)
export default api;
