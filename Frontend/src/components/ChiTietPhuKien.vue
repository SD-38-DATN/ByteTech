<template>
  <div class="shopee-page">
    <!-- ✅ HEADER SECTION - Đồng bộ từ TrangChu.vue -->
    <header class="shopee-header px-5 py-3">
      <!-- Order Success Toast -->
      <div v-if="showOrderSuccessToast" class="order-success-toast">
        <div class="toast-icon">
          <i class="bi bi-patch-check-fill"></i>
        </div>
        <div class="toast-content">
          <h4>Đặt hàng thành công!</h4>
          <p>Mã đơn hàng: <strong>{{ successfulOrderCode }}</strong></p>
        </div>
        <div class="timer-bar"></div>
      </div>

      <!-- Top bar -->
      <div class="d-flex justify-content-between align-items-center top-bar mb-2 flex-wrap py-2"
        style="font-size: 1.05rem; font-weight: 500;">
        <div class="d-flex align-items-center gap-3 flex-wrap text-white">
          <a href="#" class="text-white text-decoration-none">
            <i class="bi bi-shop-window me-1"></i> Kênh Người Bán
          </a>
          <a href="#" class="text-white text-decoration-none">
            <i class="bi bi-cloud-arrow-down me-1"></i> Tải ứng dụng
          </a>
          <span class="mx-2">|</span>
          <span>Kết nối:</span>
          <a href="#" class="text-white fs-5"><i class="bi bi-facebook"></i></a>
          <a href="#" class="text-white fs-5"><i class="bi bi-instagram"></i></a>
          <a href="#" class="text-white fs-5"><i class="bi bi-tiktok"></i></a>
          <a href="#" class="text-white fs-5"><i class="bi bi-youtube"></i></a>
        </div>

        <div class="d-flex align-items-center gap-3 flex-wrap text-white">
          <a href="#" class="text-white text-decoration-none">
            <i class="bi bi-bell-fill me-1"></i> Thông báo
          </a>
          <a href="#" class="text-white text-decoration-none">
            <i class="bi bi-question-circle-fill me-1"></i> Hỗ trợ
          </a>
          <div class="text-white">
            <i class="bi bi-translate me-1"></i> Tiếng Việt
          </div>
          
          <!-- Authentication Section -->
          <div class="d-flex align-items-center gap-3 flex-wrap text-white">
            <!-- Nếu có token và có tên hiển thị -->
            <div class="dropdown" v-if="tenHienThi">
              <a href="#" class="fw-bold text-white text-decoration-none dropdown-toggle d-flex align-items-center"
                id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                <img :src="avatarUrl" alt="Avatar" class="rounded-circle me-2 user-avatar" />
                <span>{{ tenHienThi }}</span>
              </a>
              <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 rounded-3 p-2"
                aria-labelledby="userDropdown">
                <li>
                  <h6 class="dropdown-header text-muted text-truncate">
                    Chào mừng, {{ tenHienThi }}!
                  </h6>
                </li>
                <li><hr class="dropdown-divider my-1" /></li>
                <li>
                  <router-link to="/me" class="dropdown-item rounded-2">
                    <i class="bi bi-person-circle me-2"></i> Tài khoản của tôi
                  </router-link>
                </li>
                <li>
                  <router-link to="/donhang" class="dropdown-item rounded-2">
                    <i class="bi bi-box-seam me-2"></i> Đơn mua
                  </router-link>
                </li>
                <li>
                  <a class="dropdown-item rounded-2" href="#">
                    <i class="bi bi-heart me-2"></i> Sản phẩm yêu thích
                  </a>
                </li>
                <li v-if="role === 'admin'">
                  <hr class="dropdown-divider my-1" />
                  <router-link to="/duyet-don" class="dropdown-item rounded-2">
                    <i class="bi bi-speedometer2 me-2"></i> Trang quản trị
                  </router-link>
                </li>
                <li><hr class="dropdown-divider my-1" /></li>
                <li>
                  <a class="dropdown-item rounded-2 text-danger" href="#" @click="handleAuth">
                    <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
                  </a>
                </li>
              </ul>
            </div>

            <!-- Nếu không có token hoặc không có tên hiển thị -->
            <div v-else class="d-flex align-items-center">
              <router-link to="/dang-ky" class="btn text-white me-2">Đăng ký</router-link>
              <router-link to="/login" class="btn btn-warning text-dark fw-bold rounded-1">Đăng nhập</router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- Main header -->
      <div class="d-flex justify-content-between align-items-center flex-wrap">
        <!-- Logo -->
        <router-link to="/" class="brand text-white text-decoration-none fs-3 fw-bold d-flex align-items-center gap-2">
          <span class="logo-placeholder"></span> Shopee Mini
        </router-link>

        <!-- Search -->
        <form action="#" method="get" class="d-flex flex-grow-1 mx-4 search-form" style="max-width: 800px;">
          <input type="text" name="tenSanPham" class="form-control me-2 rounded-1 shadow-sm"
            placeholder="🔍 Tìm kiếm sản phẩm" />
          <button class="btn btn-light rounded-1 px-4 fw-bold" type="submit">Tìm</button>
        </form>

        <!-- Icons -->
        <div class="d-flex align-items-center gap-4 text-white fs-5">
          <a href="#" class="text-white position-relative hover-link">
            <i class="bi bi-heart"></i>
          </a>

          <!-- Nút giỏ hàng chuyển hướng -->
          <router-link to="/giohang" class="text-white position-relative hover-link">
            <i class="bi bi-cart3"></i>
            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-warning text-dark">
              {{ cartItemCount }}
            </span>
          </router-link>

          <button class="btn btn-outline-light" @click="$router.push('/giohang')">
            Xem giỏ hàng
          </button>

          <a href="#" class="text-white hover-link"><i class="bi bi-gear-fill"></i></a>
        </div>
      </div>
    </header>

    <!-- ✅ NAVIGATION -->
    <div class="bg-white shadow-sm py-2">
      <div class="container d-flex justify-content-around flex-wrap custom-nav">
        <router-link to="/" class="text-dark">🏠 Trang chủ</router-link>
        <router-link to="/voucher" class="text-dark">🔥 Voucher</router-link>
        <router-link to="/danh-muc" class="text-dark">📚 Danh mục</router-link>
        <router-link to="/me" class="text-dark">👤 Tài khoản</router-link>
        <router-link to="/donhang" class="text-dark">📦 Đơn hàng</router-link>
        <router-link to="/duyet-don" class="text-dark">🛠 Duyệt đơn hàng</router-link>
        <router-link to="/chat" class="text-dark">💬 Chat</router-link>
      </div>
    </div>

    <!-- ✅ MAIN CONTENT - Chi tiết phụ kiện -->
    <!-- Feedback Success Toast -->
    <div v-if="showFeedbackSuccessToast" class="feedback-success-toast">
      <div class="toast-icon">
        <i class="bi bi-check-circle-fill"></i>
      </div>
      <div class="toast-content">
        <h4>Gửi đánh giá thành công!</h4>
        <p>Cảm ơn bạn đã chia sẻ trải nghiệm.</p>
      </div>
      <div class="toast-timer"></div>
    </div>

    <div class="container mt-4" v-if="phuKien">
      <div class="container mt-4" v-if="phuKien">
  <div class="back-btn mt-3 mb-4">
    <button class="btn btn-outline-dark" @click="goBack">
      ← Quay lại
    </button>
  </div>
</div>


      <!-- Accessory Information -->
      <div class="row product-box">
        <div class="col-md-5 text-center">
          <img :src="mainImage" class="main-image" alt="Ảnh chính" />
          <div>
            <img v-for="(img, index) in thumbnails" :key="index" :src="img" class="thumbnail-img"
              :class="{ active: img === mainImage }" @click="changeImage(img)" />
          </div>
        </div>
        <div class="col-md-7">
          <h3 class="mb-3 text-uppercase text-primary">{{ phuKien.tenPhuKien }}</h3>
          
          <!-- Product Description -->
          <div class="mb-3">
            <span class="section-title">📝 Mô tả:</span>
            <p class="text-muted">{{ phuKien.moTa || 'Không có mô tả' }}</p>
          </div>
          
          <div class="mb-2">
            <span class="section-title">💰 Giá:</span>
            <span class="text-danger fw-bold">
              {{ selectedVariant ? selectedVariant.gia.toLocaleString() + ' đ' : 'Vui lòng chọn thuộc tính' }}
            </span>
          </div>
          <div class="mb-2">
            <span class="section-title">📦 Mã SKU:</span>
            <span>{{ selectedVariant ? selectedVariant.maSKUPhuKien : '---' }}</span>
          </div>
          <div class="mb-3">
            <span class="section-title">📊 Tồn kho:</span>
            <span>{{ selectedVariant ? selectedVariant.soLuong : '---' }}</span>
          </div>
          
          <!-- Product Attributes -->
          <div v-for="(options, key) in attributes" :key="key" class="mb-3">
            <label class="form-label fw-semibold">{{ key }}</label>
            <div>
              <button v-for="value in options" :key="value" type="button" class="thuoc-tinh-btn" :class="{
                selected: selectedValues[key] === value,
                readonly: !isOptionAvailable(key, value)
              }" @click="selectAttribute(key, value)">
                {{ value }}
              </button>
            </div>
            <small class="text-muted">Đã chọn: <span>{{ selectedValues[key] || '---' }}</span></small>
          </div>
          
          <div class="reset-btn">
            <button type="button" class="btn btn-outline-secondary btn-sm" @click="resetSelection">
              🗘 Chọn lại biến thể
            </button>
          </div>
          
          <!-- Quantity Selection -->
          <div class="mb-3 mt-3">
            <label class="form-label">Số lượng:</label>
            <div class="input-group">
              <button type="button" class="btn btn-outline-secondary" @click="changeQuantity(-1)">-</button>
              <input type="number" v-model.number="quantity" min="1" class="form-control text-center" readonly />
              <button type="button" class="btn btn-outline-secondary" @click="changeQuantity(1)">+</button>
            </div>
          </div>
          
          <button type="button" class="btn btn-danger w-100" @click="buyNow">🛒 Thêm vào giỏ hàng</button>
        </div>
      </div>

      <!-- Technical Specifications -->
      <div class="row mt-4">
        <div class="col-md-12">
          <div v-if="phuKien.loaiThongSoList && phuKien.loaiThongSoList.length > 0">
            <div class="thong-so-box">
              <h4 class="section-title">⚙️ Thông số kỹ thuật</h4>
              <div v-if="phuKien.loaiThongSoList.length > 0">
                <div v-for="loai in phuKien.loaiThongSoList" :key="loai.tenLoaiThongSo" class="mb-4">
                  <h6 class="fw-bold mb-3">{{ loai.tenLoaiThongSo }}</h6>
                  <table class="table table-bordered align-middle thong-so-table">
                    <tbody>
                      <tr v-for="(ts, idx) in loai.thongSoList
                        .filter(ts => ts.trangThai === 1 )
                        .slice(0, 3)" :key="idx">
                        <td class="w-25 bg-light fw-semibold">{{ ts.tenThongSo }}</td>
                        <td>{{ ts.giaTriThongSo }}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
              <button class="btn btn-outline-primary" type="button" data-bs-toggle="offcanvas"
                data-bs-target="#offcanvasThongSo">
                Xem tất cả thông số
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Specifications Offcanvas -->
      <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasThongSo" aria-labelledby="offcanvasThongSoLabel"
        style="--bs-offcanvas-width: 40%;">
        <div class="offcanvas-header border-bottom">
          <h5 class="offcanvas-title fw-bold" id="offcanvasThongSoLabel">
            Thông số kỹ thuật chi tiết
          </h5>
          <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Đóng"></button>
        </div>
        <div class="offcanvas-body">
          <div v-for="loai in phuKien.loaiThongSoList" :key="loai.tenLoaiThongSo" class="mb-4">
            <h6 class="fw-bold mb-3">{{ loai.tenLoaiThongSo }}</h6>
            <div class="table-responsive">
              <table class="table table-bordered align-middle thong-so-table">
                <tbody>
                  <tr v-for="ts in loai.thongSoList.filter(ts => ts.trangThai === 1 || ts.trangThai === null)" :key="ts.tenThongSo">
                    <td class="w-25 bg-light fw-semibold">{{ ts.tenThongSo }}</td>
                    <td>{{ ts.giaTriThongSo }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- Feedback Section -->
      <div class="feedback-box mt-4">
        <h4 class="section-title">💬 Đánh giá phụ kiện</h4>
        <div class="mb-3">
          <span v-if="feedbacks.length > 0" class="fw-bold text-warning">
            ⭐ {{ averageRating.toFixed(1) }} / 5 ({{ feedbacks.length }} lượt đánh giá)
          </span>
          <span v-else class="text-muted">Chưa có đánh giá</span>
        </div>
        <button class="btn btn-primary mb-3" data-bs-toggle="modal" data-bs-target="#feedbackModal">
          ➕ Tạo đánh giá
        </button>
        <div v-if="feedbacks.length > 0">
          <div v-for="fb in displayedFeedbacks" :key="fb.userId + '_' + fb.ngayDanhGia" class="feedback-item">
            <p>
              <strong>{{ fb.tenHienThi || 'Người dùng ' + fb.userId }}</strong> -
              <span class="text-warning">{{ "⭐".repeat(fb.danhGia) }}</span>
            </p>
            <p>{{ fb.noiDung }}</p>
            <small class="text-muted">{{ formatDate(fb.ngayDanhGia) }}</small>
            <div>
              <button v-if="isMyFeedback(fb)" class="btn btn-sm btn-outline-danger mt-1" @click="deleteFeedback(fb.id)">
                Xóa
              </button>
            </div>
            <hr />
          </div>
          <div v-if="feedbacks.length > initialFeedbackCount" class="text-center mt-3">
            <button v-if="visibleFeedbacksCount < feedbacks.length" class="btn btn-sm btn-outline-primary me-2"
              @click="loadMoreFeedbacks">
              Xem thêm đánh giá
            </button>
            <button v-if="visibleFeedbacksCount > initialFeedbackCount" class="btn btn-sm btn-outline-secondary"
              @click="collapseFeedbacks">
              Thu gọn
            </button>
          </div>
        </div>
        <p v-else>Chưa có đánh giá nào cho phụ kiện này.</p>
      </div>

      <!-- Feedback Modal -->
      <div class="modal fade" id="feedbackModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">✍️ Thêm đánh giá</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
            </div>
            <div class="modal-body">
              <textarea v-model="newFeedback.noiDung" class="form-control" rows="3"
                placeholder="Nhập nội dung đánh giá..."></textarea>
              <div class="rating-stars mt-3">
                <span v-for="i in 5" :key="i" class="star" :class="{ active: i <= newFeedback.danhGia }"
                  @click="newFeedback.danhGia = i">★</span>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
              <button class="btn btn-primary" @click="addFeedback" data-bs-dismiss="modal">Gửi đánh giá</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-else class="container mt-5">
      <div class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
        <p class="text-muted mt-3">Đang tải thông tin phụ kiện...</p>
      </div>
    </div>

    <!-- ✅ FIXED TOAST CONTAINER - Hoàn toàn có thể click được -->
    <div class="toast-container-fixed">
      <div v-for="toast in toasts" :key="toast.id" 
           class="add-to-cart-toast-fixed" 
           @click.stop>
        <div class="toast-icon-cart-fixed">
          <i class="bi bi-check-circle-fill"></i>
        </div>
        <div class="toast-content-fixed">
          <h5 class="toast-title-cart-fixed">Đã thêm vào giỏ hàng!</h5>
          <div class="toast-actions-fixed">
            <button 
              class="btn btn-sm btn-light toast-btn-continue" 
              @click.stop="handleContinueShopping(toast.id)"
              type="button"
            >
              Mua tiếp
            </button>
            <button 
              class="btn btn-sm btn-primary toast-btn-cart" 
              @click.stop="handleViewCart(toast.id)"
              type="button"
            >
              Xem giỏ hàng
            </button>
          </div>
        </div>
        <button 
          class="toast-close-btn-fixed" 
          @click.stop="handleCloseToast(toast.id)"
          type="button"
        >
          ×
        </button>
        <div class="toast-timer-bar-fixed"></div>
      </div>
    </div>

    

    <!-- ✅ FOOTER SECTION - Đồng bộ từ TrangChu.vue -->
    <footer class="shopee-footer mt-5 pt-5 border-top">
      <div class="container">
        <div class="row row-cols-2 row-cols-md-4 g-4">
          <div class="col">
            <h6 class="text-uppercase fw-bold mb-3">CHĂM SÓC KHÁCH HÀNG</h6>
            <ul class="list-unstyled small">
              <li><a href="#" class="text-decoration-none text-muted">Trung Tâm Trợ Giúp</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Hướng Dẫn Mua Hàng</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Thanh Toán</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Vận Chuyển</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Trả Hàng & Hoàn Tiền</a></li>
            </ul>
          </div>

          <div class="col">
            <h6 class="text-uppercase fw-bold mb-3">VỀ SHOPEE MINI</h6>
            <ul class="list-unstyled small">
              <li><a href="#" class="text-decoration-none text-muted">Giới Thiệu Về Dự Án</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Điều Khoản</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Chính Sách Bảo Mật</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Liên Hệ</a></li>
            </ul>
          </div>

          <div class="col">
            <h6 class="text-uppercase fw-bold mb-3">THEO DÕI CHÚNG TÔI</h6>
            <ul class="list-unstyled small">
              <li><a href="#" class="text-decoration-none text-muted">Facebook</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Instagram</a></li>
              <li><a href="#" class="text-decoration-none text-muted">TikTok</a></li>
              <li><a href="#" class="text-decoration-none text-muted">Youtube</a></li>
            </ul>
          </div>

          <div class="col">
            <h6 class="text-uppercase fw-bold mb-3">LIÊN HỆ</h6>
            <ul class="list-unstyled small">
              <li class="text-muted">📍 FPT Polytechnic, Việt Nam</li>
              <li class="text-muted">📞 0123 456 789</li>
              <li class="text-muted">📧 support@shopee-mini.vn</li>
            </ul>
          </div>
        </div>

        <hr class="my-4" />

        <div class="text-center small text-muted">
          © 2025 <strong>Shopee Mini</strong>. Thiết kế bởi <strong>bạn</strong> & <strong>ChatGPT</strong>. Mọi quyền được bảo
          lưu.
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  addToCart,
  validateToken,
  fetchCart,
  PhuKienChiTiet,
  addFeedbackPhuKien,
  deleteFeedbackPhuKien,
  logoutApi,
} from "../service/api.js";

export default {
  name: "ChiTietPhuKien",

  data() {
    return {
      // ✅ Header data
      token: localStorage.getItem("token"),
      cart: null,
      role: localStorage.getItem("role"),
      avatarUrl: localStorage.getItem("avatarUrl") || "https://placehold.co/40x40?text=AV",
      showOrderSuccessToast: false,
      successfulOrderCode: ''
    };
  },

  
  computed: {
    // ✅ Header computed properties
    tenHienThi() {
      const token = validateToken();
      if (token) {
        const tenHienThi = localStorage.getItem("tenHienThi");
        return tenHienThi && tenHienThi.trim() !== "" ? tenHienThi : null;
      }
      return null;
    },
    
    buttonText() {
      return validateToken() ? "Đăng xuất" : "Đăng nhập";
    },
    
    cartItemCount() {
      if (!this.cart || !this.cart.chiTietList) return 0;
      return this.cart.chiTietList.length;
    },
  },

  async mounted() {
    // ✅ Header initialization
    if (validateToken()) {
      try {
        this.cart = await fetchCart();
      } catch (e) {
        console.error("Lỗi khi tải giỏ hàng:", e);
      }
    }

    // Check for order success notification
    const query = this.$route.query;
    if (query.order_success === 'true' && query.order_code) {
      this.successfulOrderCode = query.order_code;
      this.showOrderSuccessToast = true;

      setTimeout(() => {
        this.showOrderSuccessToast = false;
      }, 5000);

      this.$router.replace({ query: null });
    }
  },

  methods: {
    // ✅ Header methods
    async handleAuth() {
      if (!this.token) {
        this.$router.push("/login");
        return;
      }

      try {
        await logoutApi();
        console.log("✅ Logout backend thành công");
      } catch (err) {
        console.error("⚠️ Lỗi logout backend:", err);
      } finally {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("username");
        localStorage.removeItem("tenHienThi");
        console.log("🗑️ Token đã xóa khỏi localStorage");

        this.token = null;
        this.$router.push("/login");
      }
    },
  },

  setup() {
    const route = useRoute();
    const router = useRouter();
    const token = localStorage.getItem("token");

    // ✅ Product Data
    const phuKien = ref(null);
    const selectedValues = reactive({});
    const selectedVariant = ref(null);
    const quantity = ref(1);
    const mainImage = ref("placeholder-main.jpg");
    const thumbnails = ref([]);
    const attributes = ref({});
    
    // ✅ Feedback Data
    const feedbacks = ref([]);
    const newFeedback = reactive({ noiDung: "", danhGia: 0 });
    const initialFeedbackCount = 3;
    const visibleFeedbacksCount = ref(initialFeedbackCount);
    const showFeedbackSuccessToast = ref(false);
    let feedbackToastTimer = null;

    // ✅ FIXED TOAST SYSTEM
    const toasts = ref([]);

const goBack = () => {
  if (window.history.length > 1) {
    router.back(); // Quay lại trang trước
  } else {
    router.push('/'); // Nếu không có lịch sử, quay về trang chủ
  }
};


onMounted(async () => {
  await fetchPhuKienChiTiet();
  window.scrollTo({ top: 0, behavior: 'smooth' }); // Cuộn lên đầu trang
});

    const showSuccessToast = () => {
      const id = Date.now() + Math.random();
      console.log('🎯 Creating new toast with ID:', id);
      
      toasts.value.push({ id });
      console.log('📋 Current toasts:', toasts.value);

      setTimeout(() => {
        handleCloseToast(id);
      }, 8000);
    };

    const handleContinueShopping = (id) => {
      console.log('🛍️ Continue shopping clicked for toast ID:', id);
      toasts.value = toasts.value.filter((t) => t.id !== id);
      console.log('✅ Toast removed successfully, user continues shopping');
    };

    const handleViewCart = (id) => {
      console.log('🛒 View cart clicked for toast ID:', id);
      toasts.value = toasts.value.filter((t) => t.id !== id);
      console.log('🔄 Navigating to cart page...');
      router.push("/giohang");
    };

    const handleCloseToast = (id) => {
      console.log('❌ Close toast clicked for ID:', id);
      toasts.value = toasts.value.filter((t) => t.id !== id);
      console.log('✅ Toast closed successfully');
    };

    // ✅ Computed Properties
    const averageRating = computed(() => {
      if (feedbacks.value.length === 0) return 0;
      const sum = feedbacks.value.reduce((acc, fb) => acc + fb.danh  , 0);
      return sum / feedbacks.value.length;
    });

    const displayedFeedbacks = computed(() => {
      return feedbacks.value.slice(0, visibleFeedbacksCount.value);
    });

    const loadMoreFeedbacks = () => {
      visibleFeedbacksCount.value += 3;
    };

    const collapseFeedbacks = () => {
      visibleFeedbacksCount.value = initialFeedbackCount;
    };

    // ✅ Feedback Functions
    const addFeedback = async () => {
      if (!newFeedback.noiDung || !newFeedback.danhGia) {
        alert("⚠️ Vui lòng nhập nội dung và chọn số sao!");
        return;
      }
      if (!token) {
        alert("❌ Bạn chưa đăng nhập!");
        router.push("/login");
        return;
      }
      try {
        await addFeedbackPhuKien({
          maPhuKien: phuKien.value.maPhuKien,
          noiDung: newFeedback.noiDung,
          danhGia: newFeedback.danhGia,
        });

        newFeedback.noiDung = "";
        newFeedback.danhGia = 0;
        
        fetchPhuKienChiTiet();

        showFeedbackSuccessToast.value = true;
        if (feedbackToastTimer) clearTimeout(feedbackToastTimer);
        feedbackToastTimer = setTimeout(() => {
          showFeedbackSuccessToast.value = false;
        }, 5000);
      } catch (err) {
        console.error("Lỗi thêm feedback:", err);
        alert(`❌ Gửi đánh giá thất bại: ${err.response?.data?.message || err.message}`);
      }
    };

    const deleteFeedback = async (id) => {
      if (!confirm("Bạn có chắc muốn xóa đánh giá này?")) return;
      try {
        await deleteFeedbackPhuKien(id);
        fetchPhuKienChiTiet();
        alert("✅ Xóa đánh giá thành công!");
      } catch (err) {
        console.error("Lỗi xóa feedback:", err);
      }
    };

    const isMyFeedback = (fb) => {
      const currentUserId = parseInt(localStorage.getItem("userId"));
      return fb.userId === currentUserId;
    };

    const formatDate = (dateString) => {
      if (!dateString) return "";
      return new Date(dateString).toLocaleString("vi-VN");
    };

    // ✅ API Functions
    const fetchPhuKienChiTiet = async () => {
      try {
        const res = await PhuKienChiTiet(route.params.id);
        phuKien.value = res.data;
        
        phuKien.value.bienTheList = (phuKien.value.bienTheList || []).filter(
          (bt) => bt.trangThai === 1
        );

        feedbacks.value = phuKien.value.feedbackList || [];

        const attrMap = {};
        phuKien.value.bienTheList.forEach((bt) => {
          (bt.thuocTinhList || []).forEach((tt) => {
            const key = tt.tenThuocTinh;
            const value = tt.giaTriThuocTinh;
            if (!attrMap[key]) attrMap[key] = new Set();
            attrMap[key].add(value);
          });
        });

        for (const key in attrMap) attributes.value[key] = Array.from(attrMap[key]);

        if (phuKien.value.bienTheList.length > 0) {
          thumbnails.value = phuKien.value.bienTheList.map(
            (bt) => `/images/phukien/${phuKien.value.maPhuKien}/${bt.maSKUPhuKien}-1.png`
          );
          mainImage.value = thumbnails.value[0];
        }

        selectMinPriceVariant();

      } catch (error) {
        console.error("Lỗi khi lấy chi tiết phụ kiện:", error);
      }
    };

    // ✅ Product Functions
    const selectAttribute = (key, value) => {
      if (selectedValues[key] === value) {
        delete selectedValues[key];
      } else {
        selectedValues[key] = value;
      }
      updateSelectedVariant();
    };

    const isOptionAvailable = (key, value) => {
      if (!phuKien.value) return false;
      const tempSelection = { ...selectedValues, [key]: value };
      return phuKien.value.bienTheList.some((variant) => {
        return Object.keys(tempSelection).every((attrKey) => {
          return (variant.thuocTinhList || []).some(
            (tt) =>
              tt.tenThuocTinh === attrKey &&
              tt.giaTriThuocTinh === tempSelection[attrKey]
          );
        });
      });
    };

    const updateSelectedVariant = () => {
      if (!phuKien.value) return;
      const requiredKeys = Object.keys(attributes.value);
      const allOptionsSelected = requiredKeys.every((key) => selectedValues[key]);

      if (!allOptionsSelected) {
        selectedVariant.value = null;
        return;
      }

      selectedVariant.value =
        phuKien.value.bienTheList.find((bt) =>
          Object.entries(selectedValues).every(([k, v]) =>
            (bt.thuocTinhList || []).some(
              (tt) => tt.tenThuocTinh === k && tt.giaTriThuocTinh === v
            )
          )
        ) || null;

      if (selectedVariant.value) {
        mainImage.value = `/images/phukien/${phuKien.value.maPhuKien}/${selectedVariant.value.maSKUPhuKien}-1.png`;
        quantity.value = 1;
      }
    };

    const resetSelection = () => {
      for (const key in selectedValues) delete selectedValues[key];
      selectedVariant.value = null;
      quantity.value = 1;
      if (phuKien.value && phuKien.value.bienTheList.length > 0) {
        mainImage.value = `/images/phukien/${phuKien.value.maPhuKien}/${phuKien.value.bienTheList[0].maSKUPhuKien}-1.png`;
      }
    };

    const changeQuantity = (delta) => {
      if (!selectedVariant.value) return;
      const newQty = quantity.value + delta;
      if (newQty >= 1 && newQty <= selectedVariant.value.soLuong) {
        quantity.value = newQty;
      }
    };

    const changeImage = (src) => {
      mainImage.value = src;
    };

    const selectMinPriceVariant = () => {
      if (!phuKien.value || !phuKien.value.bienTheList || phuKien.value.bienTheList.length === 0) {
        return;
      }

      const minPriceVariant = phuKien.value.bienTheList.reduce((min, current) => {
        return current.gia < min.gia ? current : min;
      });

      const newSelectedValues = {};
      (minPriceVariant.thuocTinhList || []).forEach((tt) => {
        newSelectedValues[tt.tenThuocTinh] = tt.giaTriThuocTinh;
      });

      Object.keys(selectedValues).forEach(key => delete selectedValues[key]);
      Object.assign(selectedValues, newSelectedValues);

      selectedVariant.value = minPriceVariant;

      if (minPriceVariant) {
        mainImage.value = `/images/phukien/${phuKien.value.maPhuKien}/${minPriceVariant.maSKUPhuKien}-1.png`;
      }

      console.log('Đã tự động chọn biến thể giá thấp nhất:', minPriceVariant.gia.toLocaleString() + ' đ');
    };

    const buyNow = async () => {
      console.log('🛒 Buy now clicked for accessory');
      
      if (!selectedVariant.value) {
        alert("⚠️ Vui lòng chọn đầy đủ thuộc tính phụ kiện!");
        return;
      }
      if (selectedVariant.value.soLuong === 0) {
        alert("❌ Phụ kiện này đã hết hàng.");
        return;
      }
      
      try {
        const bienTheDTO = {
          maSKU: selectedVariant.value.maSKUPhuKien,
          soLuong: quantity.value,
        };
        
        console.log('📦 Adding accessory to cart:', bienTheDTO);
        await addToCart(bienTheDTO);
        
        console.log('✅ Added to cart successfully, showing toast');
        showSuccessToast();
        
        quantity.value = 1;
      } catch (error) {
        console.error("❌ Lỗi thêm vào giỏ hàng:", error);
        alert(`❌ Thêm vào giỏ hàng thất bại! ${error.response?.data || error.message}`);
      }
    };

    // ✅ Initialize component
    onMounted(fetchPhuKienChiTiet);

    // ✅ Return all reactive data and functions
    return {
      // Product data
      phuKien,
      selectedValues,
      selectedVariant,
      quantity,
      mainImage,
      thumbnails,
      attributes,
      
      // Feedback data
      feedbacks,
      newFeedback,
      averageRating,
      displayedFeedbacks,
      initialFeedbackCount,
      visibleFeedbacksCount,
      showFeedbackSuccessToast,
      
      // Toast data
      toasts,
      
      // Product functions
      selectAttribute,
      isOptionAvailable,
      resetSelection,
      changeQuantity,
      changeImage,
      buyNow,
      selectMinPriceVariant,
      
      // Feedback functions
      addFeedback,
      deleteFeedback,
      isMyFeedback,
      formatDate,
      loadMoreFeedbacks,
      collapseFeedbacks,
      
      // Toast functions
      showSuccessToast,
      handleContinueShopping,
      handleViewCart,
      handleCloseToast,
      goBack,
    };
  },
};
</script>

<style scoped>
/* ✅ GLOBAL STYLES */
:global(body.modal-open) {
  overflow: hidden;
}

/* ✅ SHOPEE PAGE STYLES */
.shopee-page {
  background-color: #f8f9fa;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* ✅ HEADER STYLES - Đồng bộ từ TrangChu.vue */
.shopee-header {
  background: linear-gradient(90deg, #f53d2d, #f63);
  color: white;
  font-size: 15px;
}

.shopee-header a {
  color: white;
  text-decoration: none;
  transition: all 0.2s ease-in-out;
}

.shopee-header a:hover,
.shopee-header .hover-link:hover {
  color: #ffd5bd;
  text-decoration: underline;
}

.shopee-title {
  font-size: 24px;
  font-weight: bold;
  color: #ee4d2d;
}

/* Logo giả lập */
.logo-placeholder {
  width: 40px;
  height: 40px;
  display: inline-block;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 8px;
}

/* User avatar */
.user-avatar {
  width: 2rem;
  height: 2rem;
  object-fit: cover;
  border: 2px solid #fff;
  transition: border-color 0.2s ease;
}

.dropdown-toggle::after {
  vertical-align: middle;
}

.dropdown-item {
  transition: background-color 0.2s ease, color 0.2s ease;
}

.dropdown-item:hover {
  background-color: #f8f9fa;
  color: #ee4d2d;
}

.hover-link:hover {
  opacity: 0.8;
}

/* ✅ NAVIGATION STYLES */
.custom-nav a {
  position: relative;
  padding: 10px 16px;
  font-weight: 600;
  color: #333;
  text-decoration: none;
  transition: color 0.3s ease;
}

.custom-nav a::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 5px;
  width: 0;
  height: 2px;
  background-color: #ff5722;
  transition: width 0.3s ease;
}

.custom-nav a:hover {
  color: #ff5722;
}

.custom-nav a:hover::after,
.custom-nav a.active::after {
  width: 100%;
}

.custom-nav a.active {
  color: #ff5722;
}

@media (max-width: 768px) {
  .custom-nav {
    flex-direction: column;
    gap: 0.75rem;
    text-align: center;
  }
}

/* ✅ PRODUCT STYLES */
.product-box {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  padding: 2rem;
}

.main-image {
  width: 100%;
  max-width: 400px;
  height: auto;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.thumbnail-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border: 2px solid #ddd;
  border-radius: 4px;
  margin: 0 5px;
  cursor: pointer;
  transition: transform 0.2s, border-color 0.2s;
}

.thumbnail-img:hover,
.thumbnail-img.active {
  border-color: #ee4d2d;
  transform: scale(1.1);
}

.section-title {
  font-weight: bold;
  font-size: 1.1em;
  color: #555;
  margin-right: 0.5rem;
}

.thuoc-tinh-btn {
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.thuoc-tinh-btn:hover {
  background-color: #e0e0e0;
}

.thuoc-tinh-btn.selected {
  background-color: #ee4d2d;
  color: #fff;
  border-color: #ee4d2d;
}

.thuoc-tinh-btn.readonly {
  background-color: #f8f8f8;
  color: #aaa;
  cursor: not-allowed;
  border-color: #ddd;
}

/* ✅ FEEDBACK STYLES */
.feedback-box {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  padding: 2rem;
}

.rating-stars .star {
  font-size: 2rem;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}

.rating-stars .star.active {
  color: #ffc107;
}

.feedback-item {
  padding: 1rem 0;
}

/* ✅ FEEDBACK SUCCESS TOAST */
.feedback-success-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  padding: 16px;
  z-index: 2000;
  overflow: hidden;
  border-left: 5px solid #28a745;
}

.feedback-success-toast .toast-icon {
  font-size: 24px;
  color: #28a745;
  margin-right: 16px;
}

.feedback-success-toast .toast-content h4 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #333;
}

.feedback-success-toast .toast-content p {
  margin: 4px 0 0;
  font-size: 0.9rem;
  color: #666;
}

.feedback-success-toast .toast-timer {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2, #4caf50);
  animation: countdown 5s linear forwards;
}

/* ✅ SPECIFICATIONS STYLES */
.thong-so-box {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  padding: 2rem;
}

.thong-so-table {
  margin-bottom: 1rem;
}

/* ✅ ORDER SUCCESS TOAST */
@keyframes slideInFromRight {
  from {
    transform: translateX(110%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.order-success-toast {
  position: fixed;
  top: 80px; 
  right: 20px;
  z-index: 10001; 
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  padding: 16px;
  max-width: 400px;
  overflow: hidden; 
  border-left: 5px solid #0d6efd;
  animation: slideInFromRight 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94) both;
}

.order-success-toast .toast-icon {
  font-size: 32px;
  color: #0d6efd;
  margin-right: 16px;
}

.order-success-toast .toast-content h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.order-success-toast .toast-content p {
  margin: 0;
  font-size: 14px;
  color: #6c757d;
}

.order-success-toast .timer-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 4px;
  width: 100%;
  background-color: #0d6efd;
  animation: countdown 5s linear forwards;
}

/* ✅ FIXED TOAST SYSTEM - 100% Clickable */
.toast-container-fixed {
  position: fixed !important;
  top: 80px !important;
  right: 20px !important;
  display: flex !important;
  flex-direction: column !important;
  gap: 12px !important;
  z-index: 999999 !important;
  pointer-events: auto !important;
}

.add-to-cart-toast-fixed {
  display: flex !important;
  align-items: center !important;
  gap: 10px !important;
  background: #fff !important;
  border: 1px solid #ccc !important;
  padding: 12px 16px !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1) !important;
  min-width: 320px !important;
  animation: fadeInFixed 0.3s ease !important;
  position: relative !important;
  z-index: 999999 !important;
  pointer-events: auto !important;
}

.toast-icon-cart-fixed {
  color: #28a745 !important;
  font-size: 22px !important;
  pointer-events: auto !important;
}

.toast-content-fixed {
  flex-grow: 1 !important;
  pointer-events: auto !important;
}

.toast-title-cart-fixed {
  font-size: 16px !important;
  margin: 0 !important;
  font-weight: 600 !important;
  color: #333 !important;
  pointer-events: auto !important;
}

.toast-actions-fixed {
  display: flex !important;
  gap: 8px !important;
  margin-top: 6px !important;
  pointer-events: auto !important;
}

.toast-btn-continue,
.toast-btn-cart {
  pointer-events: auto !important;
  cursor: pointer !important;
  z-index: 999999 !important;
  position: relative !important;
  background-color: var(--bs-btn-bg) !important;
  border: var(--bs-btn-border-width) solid var(--bs-btn-border-color) !important;
  color: var(--bs-btn-color) !important;
  padding: 0.25rem 0.5rem !important;
  font-size: 0.875rem !important;
  border-radius: 0.375rem !important;
  text-decoration: none !important;
  transition: all 0.15s ease-in-out !important;
}

.toast-btn-continue:hover,
.toast-btn-cart:hover {
  transform: translateY(-1px) !important;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1) !important;
}

.toast-btn-continue:active,
.toast-btn-cart:active {
  transform: translateY(0) !important;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1) !important;
}

.toast-close-btn-fixed {
  background: transparent !important;
  border: none !important;
  font-size: 20px !important;
  cursor: pointer !important;
  color: #666 !important;
  position: absolute !important;
  top: 8px !important;
  right: 8px !important;
  width: 24px !important;
  height: 24px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  pointer-events: auto !important;
  z-index: 999999 !important;
}

.toast-close-btn-fixed:hover {
  color: #333 !important;
  background-color: #f0f0f0 !important;
  border-radius: 50% !important;
}

.toast-timer-bar-fixed {
  position: absolute !important;
  bottom: 0 !important;
  left: 0 !important;
  height: 3px !important;
  width: 100% !important;
  background: #007bff !important;
  animation: timerFixed 8s linear forwards !important;
  pointer-events: none !important;
}

/* ✅ FOOTER STYLES - Đồng bộ từ TrangChu.vue */
.shopee-footer {
  background-color: #fff;
  padding: 20px;
  border-top: 1px solid #eee;
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 40px;
}

.shopee-footer h6 {
  color: #333;
  font-weight: bold;
  text-transform: uppercase;
}

.shopee-footer a {
  color: #999;
  text-decoration: none;
  transition: color 0.3s ease;
}

.shopee-footer a:hover {
  color: #ee4d2d;
}

.shopee-footer .list-unstyled li {
  margin-bottom: 0.5rem;
}

/* ✅ ANIMATIONS */
@keyframes fadeInFixed {
  from { 
    opacity: 0; 
    transform: translateX(30px); 
  }
  to { 
    opacity: 1; 
    transform: translateX(0); 
  }
}

@keyframes timerFixed {
  from { width: 100%; }
  to { width: 0; }
}

@keyframes countdown {
  from { width: 100%; }
  to { width: 0%; }
}

/* ✅ RESPONSIVE */
@media (max-width: 768px) {
  .toast-container-fixed {
    right: 10px !important;
    left: 10px !important;
    top: 60px !important;
  }
  
  .add-to-cart-toast-fixed {
    min-width: auto !important;
    max-width: 100% !important;
  }
  
  .toast-actions-fixed {
    flex-direction: column !important;
    gap: 4px !important;
  }
  
  .toast-btn-continue,
  .toast-btn-cart {
    width: 100% !important;
    text-align: center !important;
  }

  .product-box {
    padding: 1rem;
  }
  
  .main-image {
    max-width: 300px;
  }
}
</style>