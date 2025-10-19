<template>
  <header class="shopee-header px-5 py-3">
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
        <div class="d-flex align-items-center gap-3 flex-wrap text-white">
          <div class="dropdown" v-if="token">
            <a href="#" class="fw-bold text-white text-decoration-none dropdown-toggle d-flex align-items-center"
              id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              <img :src="avatarUrl" alt="Avatar" class="rounded-circle me-2 user-avatar">
              <span>{{ tenHienThi }}</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 rounded-3 p-2"
              aria-labelledby="userDropdown">
              <li>
                <h6 class="dropdown-header text-muted text-truncate">
                  Chào mừng, {{ tenHienThi }}!
                </h6>
              </li>
              <li>
                <hr class="dropdown-divider my-1" />
              </li>
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
              <li>
                <hr class="dropdown-divider my-1" />
              </li>
              <li>
                <a class="dropdown-item rounded-2 text-danger" href="#" @click="handleAuth">
                  <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
                </a>
              </li>
            </ul>
          </div>
          <div v-else class="d-flex align-items-center">
            <router-link to="/dang-ky" class="btn text-white me-2">Đăng ký</router-link>
            <router-link to="/login" class="btn btn-warning text-dark fw-bold rounded-1">Đăng nhập</router-link>
          </div>
        </div>
      </div>
    </div>
    <div class="d-flex justify-content-between align-items-center flex-wrap">
      <a href="#" class="brand text-white text-decoration-none fs-3 fw-bold d-flex align-items-center gap-2">
        <span class="logo-placeholder"></span> Shopee Mini
      </a>
      <form action="#" method="get" class="d-flex flex-grow-1 mx-4 search-form" style="max-width: 800px;">
        <input type="text" name="tenSanPham" class="form-control me-2 rounded-1 shadow-sm"
          placeholder="🔍 Tìm kiếm sản phẩm" />
        <button class="btn btn-light rounded-1 px-4 fw-bold" type="submit">Tìm</button>
      </form>
      <div class="d-flex align-items-center gap-4 text-white fs-5">
        <a href="#" class="text-white position-relative hover-link">
          <i class="bi bi-heart"></i>
        </a>
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

  <div class="container mt-4" v-if="sanPham">
    <div class="back-btn mt-3">
      <router-link to="/" class="btn btn-outline-dark">← Quay lại trang chủ</router-link>
    </div>
    <br></br>

    <div class="row product-box">
      <div class="col-md-5 text-center">
        <img :src="mainImage" class="main-image" alt="Ảnh chính" />
        <div>
          <img v-for="(img, index) in thumbnails" :key="index" :src="img" class="thumbnail-img"
            :class="{ active: img === mainImage }" @click="changeImage(img)" />
        </div>
      </div>
      <div class="col-md-7">
        <h3 class="mb-3 text-uppercase text-primary">{{ sanPham.tenSanPham }}</h3>

        <div class="product-price-new">
          <span v-if="selectedVariant">
            <span class="price-label-new">Giá sản phẩm</span>
            <div class="price-values-new">
              <span class="discounted-price-new">
                {{ selectedVariant.gia.toLocaleString() + ' đ' }}
              </span>
              <span v-if="selectedVariant.maKhuyenMai" class="original-price-new">
                {{ selectedVariant.giaKhongKhuyenMai.toLocaleString() + ' đ' }}
              </span>
            </div>
          </span>
          <span v-else class="price-placeholder-new">
            Vui lòng chọn đầy đủ phiên bản
          </span>
        </div>

        <div class="mb-2">
          <span class="section-title">📦 Mã SKU:</span>
          <span>{{ selectedVariant ? selectedVariant.maSKU : '---' }}</span>
        </div>
        <div class="mb-3">
          <span class="section-title">📊 Tồn kho:</span>
          <span>{{ selectedVariant ? selectedVariant.soLuong : '---' }}</span>
        </div>

        <div v-for="(options, key) in attributes" :key="key" class="variant-group">
          <label class="variant-group-title">
            {{ key }}:
            <span class="selected-value-display">{{ selectedValues[key] || '' }}</span>
          </label>
          <div class="variant-options-container">
            <div 
              v-for="value in options" 
              :key="value" 
              class="variant-option" 
              :class="{
                selected: selectedValues[key] === value,
                disabled: !isOptionAvailable(key, value),
              }"
              @click="isOptionAvailable(key, value) && selectAttribute(key, value)"
            >
              <span class="option-value">{{ value }}</span>
              <span class="option-price" v-if="getPriceForOption(key, value)">
                {{ getPriceForOption(key, value).toLocaleString() + ' đ' }}
              </span>
              <i v-if="selectedValues[key] === value" class="bi bi-check-circle-fill checkmark-icon"></i>
            </div>
          </div>
        </div>

        <div class="reset-btn mb-3">
          <button type="button" class="btn btn-outline-secondary btn-sm" @click="resetSelection">
            🗘 Chọn lại
          </button>
        </div>

        <div class="mb-3 mt-3">
          <label class="form-label fw-semibold">Số lượng:</label>
          <div class="input-group" style="max-width: 150px;">
            <button type="button" class="btn btn-outline-secondary" @click="changeQuantity(-1)">-</button>
            <input type="number" v-model.number="quantity" min="1" class="form-control text-center" readonly />
            <button type="button" class="btn btn-outline-secondary" @click="changeQuantity(1)">+</button>
          </div>
        </div>

        <button type="button" class="btn btn-danger w-100 py-2 fs-5 fw-bold" @click="buyNow">
          🛒 THÊM VÀO GIỎ HÀNG
        </button>
      </div>
    </div>

    <div class="row mt-4">
      <div class="col-md-6" v-if="sanPham.loaiThongSoList && sanPham.loaiThongSoList.length > 0">
        <div class="thong-so-box">
          <h4 class="section-title">⚙️ Thông số kỹ thuật</h4>
          <div v-if="sanPham.loaiThongSoList.length > 0">
            <div v-for="loai in sanPham.loaiThongSoList" :key="loai.id" class="mb-4">
              <table class="table table-bordered align-middle thong-so-table">
                <tbody>
                  <tr v-for="(ts, idx) in loai.thongSoList.filter(ts => ts.trangThai === 1).slice(0, 3)" :key="idx">
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

      <div class="offcanvas offcanvas-end offcanvas-40" tabindex="-1" id="offcanvasThongSo"
        aria-labelledby="offcanvasThongSoLabel">
        <div class="offcanvas-header">
          <h5 id="offcanvasThongSoLabel">📑 Tất cả thông số kỹ thuật</h5>
          <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Đóng"></button>
        </div>
        <div class="offcanvas-body">
          <div v-for="loai in sanPham.loaiThongSoList" :key="loai.id" class="mb-4">
            <h6 class="fw-bold">{{ loai.tenLoai }}</h6>
            <table class="table table-bordered align-middle thong-so-table">
              <tbody>
                <tr v-for="(ts, idx) in loai.thongSoList" :key="idx">
                  <td class="w-25 bg-light fw-semibold">{{ ts.tenThongSo }}</td>
                  <td>{{ ts.giaTriThongSo }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="col-md-6" v-if="sanPham.phuKienList && sanPham.phuKienList.length > 0">
        <div class="phu-kien-box">
          <h4 class="section-title">🎁 Phụ kiện đi kèm</h4>
          <div class="row g-3">
            <div v-for="pk in sanPham.phuKienList" :key="pk.maPhuKien" class="col-6">
              <div class="phu-kien-item-card" @click="openPhuKienModal(pk)">
                <img src="https://placehold.co/250x150/667eea/ffffff?text=Phu+Kien" class="card-image"
                  alt="Phụ kiện">
                <div class="card-content">
                  <h5 class="card-title">{{ pk.tenPhuKien }}</h5>
                  <p class="card-price" v-if="pk.bienTheList && pk.bienTheList.length > 0">
                    Giá: {{ pk.bienTheList[0].gia.toLocaleString() }} đ
                  </p>
                  <p class="card-price" v-else>
                    Giá: Liên hệ
                  </p>
                  <div class="card-actions mt-2">
                    <router-link :to="`/phukien/${pk.maPhuKien}`" class="btn btn-sm btn-outline-primary"
                      @click.stop>
                      Xem chi tiết
                    </router-link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="feedback-box mt-4">
      <h4 class="section-title">💬 Đánh giá sản phẩm</h4>
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
        <div v-for="fb in displayedFeedbacks" :key="fb.id" class="feedback-item">
          <p>
            <strong>{{ fb.tenHienThi }}</strong> -
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
      <p v-else>Chưa có đánh giá nào cho sản phẩm này.</p>
    </div>

    <div v-if="sanPhamLienQuan.length > 0" class="related-products-section mt-5">
      <div class="section-header">
        <h4 class="section-title mb-4">🔗 Sản phẩm liên quan</h4>

        <div class="carousel-controls" v-if="totalPages > 1">
          <button class="carousel-btn prev-btn" @click="prevPage" :disabled="currentPage === 0"
            :class="{ disabled: currentPage === 0 }">
            <i class="bi bi-chevron-left"></i>
          </button>

          <div class="page-indicator">
            <span class="current-page">{{ currentPage + 1 }}</span>
            <span class="separator">/</span>
            <span class="total-pages">{{ totalPages }}</span>
          </div>

          <button class="carousel-btn next-btn" @click="nextPage" :disabled="currentPage === totalPages - 1"
            :class="{ disabled: currentPage === totalPages - 1 }">
            <i class="bi bi-chevron-right"></i>
          </button>
        </div>
      </div>

      <div v-if="isLoadingSanPhamLienQuan" class="text-center py-4">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Đang tải...</span>
        </div>
      </div>

      <div v-else-if="sanPhamLienQuanError" class="alert alert-warning">
        {{ sanPhamLienQuanError }}
      </div>

      <div v-else class="products-carousel-container">
        <div class="products-carousel" :style="carouselStyle">
          <div v-for="(pageProducts, pageIndex) in paginatedProducts" :key="pageIndex" class="carousel-page">
            <div class="product-grid">
              <router-link v-for="p in pageProducts" :key="p.maSanPham"
                :to="{ name: 'SanPhamChiTiet', params: { id: p.maSanPham } }" class="product-card position-relative">
                <div v-if="p.giaBienThe && p.loaiGiam === 1" class="discount-badge">
                  Giảm {{ Math.round(p.giaTriGiamKhuyenMai * 100) }}%
                </div>
                <div v-else-if="p.giaBienThe && p.loaiGiam === 0" class="discount-badge">
                  -{{ p.giaTriGiamKhuyenMai.toLocaleString() }}đ
                </div>

                <div class="product-image">
                  <img :src="`/images/products/${p.maSanPham}.jpg`" :alt="p.tenSanPham" class="img-fluid"
                    @error="handleImageError($event)" />
                </div>

                <div class="product-name">{{ p.tenSanPham }}</div>
                <div class="product-desc">{{ p.moTa }}</div>
                <div class="product-desc">Số lượng: <span>{{ p.soLuong }}</span></div>

                <div v-if="p.giaBienThe" class="product-price">
                  <span v-if="p.giaKhongKhuyenMaiBienThe === p.giaBienThe" class="text-danger fw-bold">
                    {{ p.giaBienThe.toLocaleString() }} đ
                  </span>

                  <template v-else>
                    <span class="text-danger fw-bold">
                      {{ p.giaBienThe.toLocaleString() }} đ
                    </span>
                    <span class="text-muted text-decoration-line-through ms-2 small">
                      {{ p.giaKhongKhuyenMaiBienThe.toLocaleString() }} đ
                    </span>
                  </template>
                </div>

                <div v-else class="product-price">
                  {{ p.gia.toLocaleString() }} đ
                </div>

                <div class="product-category">
                  Danh mục: <span>{{ p.tenDanhMuc }}</span>
                </div>
                <div class="text-muted small mt-2">👆 Xem chi tiết</div>
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>

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

    <div v-if="showPhuKienModal && selectedPhuKien" class="pk-modal-overlay" @click.self="closePhuKienModal">
      <div class="pk-modal-container">
        <div class="pk-modal-content">
          <div class="pk-modal-header">
            <h5 class="pk-modal-title">Chọn biến thể phụ kiện</h5>
            <button type="button" class="pk-close-btn" @click="closePhuKienModal">
              <i class="bi bi-x-lg"></i>
            </button>
          </div>
          <div class="pk-modal-body">
            <div class="pk-info-card">
              <img src="https://placehold.co/80x80/667eea/ffffff?text=PK" class="pk-image" alt="Phụ kiện">
              <div class="pk-details">
                <h6 class="pk-name">{{ selectedPhuKien.tenPhuKien }}</h6>
                <div class="pk-price">
                  {{ selectedPhuKienVariant ? selectedPhuKienVariant.gia.toLocaleString() + ' ₫' : 'Vui lòng chọn' }}
                </div>
                <div class="pk-stock" v-if="selectedPhuKienVariant">
                  <span v-if="selectedPhuKienVariant.soLuong > 0" class="text-success">
                    <i class="bi bi-check-circle-fill"></i> Còn hàng
                  </span>
                  <span v-else class="text-danger">
                    <i class="bi bi-x-circle-fill"></i> Hết hàng
                  </span>
                </div>
              </div>
            </div>

            <div class="pk-attributes-section">
              <div v-for="(options, key) in phuKienAttributes" :key="key" class="pk-attribute-group">
                <label class="pk-attribute-label">{{ key }}</label>
                <div class="pk-attribute-options">
                  <button v-for="value in options" :key="value" type="button" class="pk-attribute-option"
                    :class="{ 'is-selected': selectedPhuKienValues[key] === value }"
                    @click="selectPhuKienAttribute(key, value)">
                    {{ value }}
                  </button>
                </div>
              </div>
            </div>

            <div class="pk-quantity-section">
              <label class="pk-quantity-label">Số lượng</label>
              <div class="pk-quantity-controls">
                <button type="button" class="pk-quantity-btn" @click="changePhuKienQuantity(-1)"
                  :disabled="phuKienQuantity <= 1">-</button>
                <input type="number" class="pk-quantity-display" v-model.number="phuKienQuantity" readonly>
                <button type="button" class="pk-quantity-btn" @click="changePhuKienQuantity(1)"
                  :disabled="selectedPhuKienVariant && phuKienQuantity >= selectedPhuKienVariant.soLuong">+</button>
              </div>
              <small v-if="selectedPhuKienVariant" class="pk-stock-info">
                Còn {{ selectedPhuKienVariant.soLuong }} sản phẩm
              </small>
            </div>
          </div>
          <div class="pk-modal-footer">
            <button type="button" class="pk-btn-cancel" @click="closePhuKienModal">
              Hủy
            </button>
            <button type="button" class="pk-btn-add-cart" @click="buyPhuKienNow"
              :disabled="!selectedPhuKienVariant || selectedPhuKienVariant.soLuong === 0">
              <i class="bi bi-cart-plus"></i>
              <span>Thêm vào giỏ hàng</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="toast-container-fixed">
      <div v-for="toast in toasts" :key="toast.id" class="add-to-cart-toast-fixed" @click.stop>
        <div class="toast-icon-cart-fixed">
          <i class="bi bi-check-circle-fill"></i>
        </div>
        <div class="toast-content-fixed">
          <h5 class="toast-title-cart-fixed">Đã thêm vào giỏ hàng!</h5>
          <div class="toast-actions-fixed">
            <button class="btn btn-sm btn-light toast-btn-continue" @click.stop="handleContinueShopping(toast.id)"
              type="button">
              Mua tiếp
            </button>
            <button class="btn btn-sm btn-primary toast-btn-cart" @click.stop="handleViewCart(toast.id)"
              type="button">
              Xem giỏ hàng
            </button>
          </div>
        </div>
        <button class="toast-close-btn-fixed" @click.stop="handleCloseToast(toast.id)" type="button">
          ×
        </button>
        <div class="toast-timer-bar-fixed"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed, watch } from "vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";
import { addToCart, validateToken, fetchCart, fetchSanPhamLienQuan } from "../service/api.js";

export default {
  name: "ProductDetail",

  // ✅ Options API cho token/cart/avatar để giữ tương thích
  data() {
    return {
      token: localStorage.getItem("token"),
      cart: null,
      role: localStorage.getItem("role"),
      avatarUrl: "/images/default-avatar.png",
    };
  },

  computed: {
    tenHienThi() {
      const tk = validateToken();
      if (tk) {
        const ten = localStorage.getItem("tenHienThi");
        const username = localStorage.getItem("username");
        return ten || username || "Khách";
      }
      return null;
    },
    buttonText() {
      return validateToken() ? "Đăng xuất" : "Đăng nhập";
    },
    cartItemCount() {
      return this.cart?.chiTietList?.length || 0;
    },
  },

  async mounted() {
    try {
      this.cart = await fetchCart();
    } catch (e) {
      console.error("Lỗi khi tải giỏ hàng:", e);
    }
  },

  methods: {
    async handleAuth() {
      if (!this.token) {
        this.$router.push("/login");
        return;
      }
      try {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("username");
        localStorage.removeItem("tenHienThi");
        this.token = null;
        this.$router.push("/login");
      } catch (err) {
        console.error("⚠️ Lỗi logout:", err);
      }
    },
    handleImageError(e) {
      e.target.src = "https://placehold.co/400x300/e0e0e0/666666?text=No+Image";
      e.target.alt = "Hình ảnh không khả dụng";
    },
  },

  setup() {
    const route = useRoute();
    const router = useRouter();

    // ============= STATE CHÍNH =============
    const sanPham = ref(null);
    const selectedValues = reactive({});
    const selectedVariant = ref(null);
    const quantity = ref(1);
    const mainImage = ref("https://placehold.co/400x300/e0e0e0/666666?text=Image");
    const thumbnails = ref([]);
    const attributes = ref({});

    const getAccessoryVariantPrice = (bt) => {
      // Ưu tiên dùng giá khuyến mãi nếu có, fallback sang giaKhongKhuyenMai hoặc gia
      if (typeof bt.gia === 'number' && bt.maKhuyenMai) return bt.gia;
      if (typeof bt.giaKhongKhuyenMai === 'number') return bt.giaKhongKhuyenMai;
      return bt.gia ?? Infinity; // nếu không có giá, coi như rất lớn để loại khỏi min
    };

    // ============= SẢN PHẨM LIÊN QUAN =============
    const sanPhamLienQuan = ref([]);
    const isLoadingSanPhamLienQuan = ref(false);
    const sanPhamLienQuanError = ref(null);

    // ============= CAROUSEL =============
    const currentPage = ref(0);
    const itemsPerPage = 4;
    const totalPages = computed(() => Math.ceil(sanPhamLienQuan.value.length / itemsPerPage));
    const paginatedProducts = computed(() => {
      const pages = [];
      for (let i = 0; i < sanPhamLienQuan.value.length; i += itemsPerPage) {
        pages.push(sanPhamLienQuan.value.slice(i, i + itemsPerPage));
      }
      return pages;
    });
    const carouselStyle = computed(() => ({
      transform: `translateX(-${currentPage.value * 100}%)`,
      transition: "transform 0.3s ease-in-out",
    }));
    const nextPage = () => {
      if (currentPage.value < totalPages.value - 1) currentPage.value++;
    };
    const prevPage = () => {
      if (currentPage.value > 0) currentPage.value--;
    };

    // ============= FEEDBACK =============
    const feedbacks = ref([]);
    const newFeedback = reactive({ noiDung: "", danhGia: 0 });
    const initialFeedbackCount = 3;
    const visibleFeedbacksCount = ref(initialFeedbackCount);
    const showFeedbackSuccessToast = ref(false);
    const averageRating = computed(() => {
      if (!feedbacks.value.length) return 0;
      const sum = feedbacks.value.reduce((acc, fb) => acc + fb.danhGia, 0);
      return sum / feedbacks.value.length;
    });
    const displayedFeedbacks = computed(() =>
      feedbacks.value.slice(0, visibleFeedbacksCount.value)
    );

    const addFeedback = async () => {
      const token = localStorage.getItem("token");
      if (!newFeedback.noiDung || !newFeedback.danhGia) {
        alert("Vui lòng nhập nội dung và chọn số sao!");
        return;
      }
      if (!token) {
        alert("Bạn chưa đăng nhập!");
        return;
      }
      try {
        await axios.post(
          "http://localhost:8081/api/feedback",
          {
            maSanPham: sanPham.value.maSanPham,
            noiDung: newFeedback.noiDung,
            danhGia: newFeedback.danhGia,
          },
          { headers: { Authorization: `Bearer ${token}` } }
        );
        newFeedback.noiDung = "";
        newFeedback.danhGia = 0;
        await fetchFeedbacksFor(sanPham.value.maSanPham);
        showFeedbackSuccessToast.value = true;
        setTimeout(() => (showFeedbackSuccessToast.value = false), 5000);
      } catch (err) {
        console.error("Lỗi thêm feedback:", err);
      }
    };

    const deleteFeedback = async (id) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Bạn chưa đăng nhập!");
        return;
      }
      if (!confirm("Bạn có chắc muốn xóa đánh giá này?")) return;
      try {
        await axios.delete(`http://localhost:8081/api/feedback/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        await fetchFeedbacksFor(sanPham.value.maSanPham);
        alert("✅ Xóa đánh giá thành công!");
      } catch (err) {
        console.error("Lỗi xóa feedback:", err);
      }
    };

    const isMyFeedback = (fb) => {
      const username = localStorage.getItem("username");
      return fb.username === username;
    };
    const formatDate = (dateString) => {
      if (!dateString) return "";
      const d = new Date(dateString);
      return d.toLocaleString("vi-VN");
    };
    const loadMoreFeedbacks = () => (visibleFeedbacksCount.value += 3);
    const collapseFeedbacks = () => (visibleFeedbacksCount.value = initialFeedbackCount);

    // ============= TOAST GIỎ HÀNG =============
    const toasts = ref([]);
    const showSuccessToast = () => {
      const id = Date.now() + Math.random();
      toasts.value.push({ id });
      setTimeout(() => handleCloseToast(id), 8000);
    };
    const handleContinueShopping = (id) => {
      toasts.value = toasts.value.filter((t) => t.id !== id);
    };
    const handleViewCart = (id) => {
      toasts.value = toasts.value.filter((t) => t.id !== id);
      router.push("/giohang");
    };
    const handleCloseToast = (id) => {
      toasts.value = toasts.value.filter((t) => t.id !== id);
    };

    // ============= BIẾN THỂ SẢN PHẨM CHÍNH =============
    const selectDefaultVariant = () => {
      const list = sanPham.value?.bienTheList || [];
      if (!list.length) {
        selectedVariant.value = null;
        return;
      }
      // Không tự động chọn nữa để người dùng tự quyết định
    };
    
    // ✅ HÀM MỚI: Lấy giá cho một tùy chọn cụ thể
    const getPriceForOption = (key, value) => {
        if (!sanPham.value || !sanPham.value.bienTheList) return null;

        // 1. Tạo một lựa chọn cơ sở: tất cả các thuộc tính đã được chọn NGOẠI TRỪ thuộc tính hiện tại đang đánh giá.
        const otherSelections = { ...selectedValues };
        delete otherSelections[key];

        // 2. Tìm tất cả các biến thể khớp với tùy chọn chúng ta đang xem (key, value)
        //    VÀ cũng khớp với tất cả các lựa chọn *khác* mà người dùng đã thực hiện.
        const potentialMatches = sanPham.value.bienTheList.filter(variant => {
            // Kiểm tra xem biến thể có tùy chọn mục tiêu không (ví dụ: 'Dung lượng': '128GB')
            const hasCurrentOption = variant.thuocTinhList.some(
                tt => tt.tenThuocTinh === key && tt.giaTriThuocTinh === value
            );
            if (!hasCurrentOption) return false;

            // Kiểm tra xem nó có tương thích với các lựa chọn khác không (ví dụ: 'Màu sắc': 'Xanh')
            const matchesOthers = Object.entries(otherSelections).every(([otherKey, otherValue]) =>
                variant.thuocTinhList.some(
                    tt => tt.tenThuocTinh === otherKey && tt.giaTriThuocTinh === otherValue
                )
            );
            return matchesOthers;
        });

        if (potentialMatches.length === 0) return null;

        // 3. Từ danh sách các kết quả phù hợp, tìm biến thể có giá thấp nhất.
        const cheapestVariant = potentialMatches.reduce((min, current) => {
            return current.gia < min.gia ? current : min;
        });
        
        // 4. Trả về giá cuối cùng của nó (API nên trả về giá đã khuyến mãi trong trường `gia`)
        return cheapestVariant.gia;
    };

    const selectAttribute = (key, value) => {
      if (selectedValues[key] === value) {
        delete selectedValues[key]; // Bỏ chọn nếu click lại
      } else {
        selectedValues[key] = value;
      }
      updateSelectedVariant();
    };

    const isOptionAvailable = (key, value) => {
      // Một tùy chọn có sẵn nếu có ít nhất một biến thể chứa nó,
      // đồng thời cũng tương thích với các lựa chọn *khác* đã được thực hiện.
      const otherSelections = { ...selectedValues };
      delete otherSelections[key]; // Không xét thuộc tính đang kiểm tra

      const list = sanPham.value?.bienTheList || [];
      return list.some((variant) => {
        // Biến thể này có chứa tùy chọn (key, value) đang xét không?
        const hasOption = (variant.thuocTinhList || []).some(
          (tt) => tt.tenThuocTinh === key && tt.giaTriThuocTinh === value
        );

        if (!hasOption) return false;

        // Biến thể này có tương thích với các lựa chọn khác không?
        const isCompatible = Object.entries(otherSelections).every(([otherKey, otherValue]) =>
          (variant.thuocTinhList || []).some(
            (tt) => tt.tenThuocTinh === otherKey && tt.giaTriThuocTinh === otherValue
          )
        );

        return isCompatible;
      });
    };

    const updateSelectedVariant = () => {
      const requiredKeys = Object.keys(attributes.value);
      const ok = requiredKeys.every((k) => selectedValues[k]);
      if (!ok) {
        selectedVariant.value = null;
        return;
      }
      const list = sanPham.value?.bienTheList || [];
      selectedVariant.value =
        list.find((bt) =>
          Object.entries(selectedValues).every(([k, v]) =>
            (bt.thuocTinhList || []).some(
              (tt) => tt.tenThuocTinh === k && tt.giaTriThuocTinh === v
            )
          )
        ) || null;

      if (selectedVariant.value) {
        mainImage.value = `/images/${sanPham.value.maSanPham}/${selectedVariant.value.maSKU}-1.png`;
        quantity.value = 1;
      }
    };

    const resetSelection = () => {
      Object.keys(selectedValues).forEach((k) => delete selectedValues[k]);
      selectedVariant.value = null;
      quantity.value = 1;
      const first = sanPham.value?.bienTheList?.[0];
      if (first) {
        mainImage.value = `/images/${sanPham.value.maSanPham}/${first.maSKU}-1.png`;
      }
    };

    const changeQuantity = (delta) => {
      if (!selectedVariant.value) return;
      const newQty = quantity.value + delta;
      if (newQty >= 1 && newQty <= selectedVariant.value.soLuong) {
        quantity.value = newQty;
      }
    };
    const changeImage = (src) => (mainImage.value = src);

    // ============= MUA HÀNG =============
    const buyNow = async () => {
      if (!selectedVariant.value) {
        alert("Vui lòng chọn đầy đủ thuộc tính sản phẩm!");
        return;
      }
      if (selectedVariant.value.soLuong === 0) {
        alert("Sản phẩm này đã hết hàng.");
        return;
      }
      try {
        const bienTheDTO = { maSKU: selectedVariant.value.maSKU, soLuong: quantity.value };
        await addToCart(bienTheDTO);
        showSuccessToast();
      } catch (error) {
        console.error("Lỗi thêm vào giỏ hàng:", error);
        alert(`Thêm vào giỏ hàng thất bại! ${error.response?.data || error.message}`);
      }
    };

    // 🔥 ============= MODAL PHỤ KIỆN - PHẦN BỔ SUNG MỚI ============= 🔥
    const showPhuKienModal = ref(false);
    const selectedPhuKien = ref(null);
    const selectedPhuKienValues = reactive({});
    const selectedPhuKienVariant = ref(null);
    const phuKienQuantity = ref(1);
    const phuKienAttributes = ref({});

    const openPhuKienModal = (pk) => {
      selectedPhuKien.value = pk;
      phuKienQuantity.value = 1;
      for (const key in selectedPhuKienValues) delete selectedPhuKienValues[key];
      selectedPhuKienVariant.value = null;
      const pkAttrMap = {};
      (pk.bienTheList || []).forEach((bt) => {
        (bt.thuocTinhList || []).forEach((tt) => {
          const key = tt.tenThuocTinh;
          const value = tt.giaTriThuocTinh;
          (pkAttrMap[key] ||= new Set()).add(value);
        });
      });
      phuKienAttributes.value = {};
      for (const key in pkAttrMap) {
        phuKienAttributes.value[key] = Array.from(pkAttrMap[key]);
      }
      if (pk.bienTheList && pk.bienTheList.length > 0) {
        const cheapest = pk.bienTheList.reduce((min, v) => {
          const pv = getAccessoryVariantPrice(v);
          const pm = getAccessoryVariantPrice(min);
          return pv < pm ? v : min;
        }, pk.bienTheList[0]);
        selectedPhuKienVariant.value = cheapest;
        (cheapest.thuocTinhList || []).forEach((tt) => {
          selectedPhuKienValues[tt.tenThuocTinh] = tt.giaTriThuocTinh;
        });
      }
      showPhuKienModal.value = true;
    };

    const closePhuKienModal = () => {
      showPhuKienModal.value = false;
      selectedPhuKien.value = null;
    };

    const selectPhuKienAttribute = (key, value) => {
      selectedPhuKienValues[key] = value;
      updateSelectedPhuKienVariant();
    };

    const updateSelectedPhuKienVariant = () => {
      if (!selectedPhuKien.value) return;
      const requiredKeys = Object.keys(phuKienAttributes.value);
      const selectedKeys = Object.keys(selectedPhuKienValues);
      if (selectedKeys.length < requiredKeys.length) {
        selectedPhuKienVariant.value = null;
        return;
      }
      selectedPhuKienVariant.value = (selectedPhuKien.value.bienTheList || []).find((bt) =>
        requiredKeys.every((key) => {
          const thuocTinhCuaBienThe = (bt.thuocTinhList || []).find(
            (tt) => tt.tenThuocTinh === key
          );
          return (
            thuocTinhCuaBienThe &&
            thuocTinhCuaBienThe.giaTriThuocTinh === selectedPhuKienValues[key]
          );
        })
      ) || null;
    };

    const changePhuKienQuantity = (delta) => {
      if (!selectedPhuKienVariant.value) return;
      const newQty = phuKienQuantity.value + delta;
      if (newQty >= 1 && newQty <= selectedPhuKienVariant.value.soLuong) {
        phuKienQuantity.value = newQty;
      }
    };

    const buyPhuKienNow = async () => {
      if (!selectedPhuKienVariant.value) {
        alert("Vui lòng chọn đầy đủ thuộc tính cho phụ kiện!");
        return;
      }
      if (selectedPhuKienVariant.value.soLuong === 0) {
        alert("Phụ kiện này đã hết hàng.");
        return;
      }
      try {
        const bienTheDTO = {
          maSKU: selectedPhuKienVariant.value.maSKUPhuKien,
          soLuong: phuKienQuantity.value,
        };
        await addToCart(bienTheDTO);
        closePhuKienModal();
        showSuccessToast();
        console.log('✅ Accessory added to cart successfully');
      } catch (error) {
        console.error("❌ Error adding accessory to cart:", error);
        alert(`❌ Thêm phụ kiện thất bại! ${error.response?.data || error.message}`);
      }
    };

    watch(
      showPhuKienModal,
      (isModalOpen) => {
        document.body.style.overflow = isModalOpen ? "hidden" : "";
      },
      { immediate: true }
    );
    // ============= GUARD CHỐNG RACE + RESET STATE =============
    const activeRequestId = ref(0);
    const resetState = () => {
      sanPham.value = null;
      Object.keys(selectedValues).forEach((k) => delete selectedValues[k]);
      selectedVariant.value = null;
      quantity.value = 1;
      mainImage.value = "https://placehold.co/400x300/e0e0e0/666666?text=Loading...";
      thumbnails.value = [];
      attributes.value = {};
      sanPhamLienQuan.value = [];
      currentPage.value = 0;
      feedbacks.value = [];
      visibleFeedbacksCount.value = initialFeedbackCount;
    };

    // ============= API CALLS =============
    const fetchSanPhamChiTiet = async (productId) => {
      if (!productId) return;
      resetState();
      const reqId = ++activeRequestId.value;
      try {
        const res = await axios.get(`http://localhost:8081/api/sanpham/${productId}`);
        if (reqId !== activeRequestId.value) return;
        sanPham.value = res.data || null;
        const list = sanPham.value?.bienTheList?.filter((bt) => bt.trangThai === 1) || [];
        sanPham.value.bienTheList = list;
        const attrMap = {};
        list.forEach((bt) => {
          (bt.thuocTinhList || []).forEach((tt) => {
            const key = tt.tenThuocTinh;
            const val = tt.giaTriThuocTinh;
            (attrMap[key] ||= new Set()).add(val);
          });
        });
        attributes.value = {};
        for (const k in attrMap) attributes.value[k] = Array.from(attrMap[k]);
        if (list.length) {
          thumbnails.value = list.map(
            (bt) => `/images/${sanPham.value.maSanPham}/${bt.maSKU}-1.png`
          );
          mainImage.value = thumbnails.value[0] || mainImage.value;
        } else {
          thumbnails.value = [];
          mainImage.value = `https://placehold.co/400x300/e0e0e0/666666?text=${sanPham.value?.tenSanPham || "Image"}`;
        }
        selectDefaultVariant();
        await fetchFeedbacksFor(productId);
        await loadSanPhamLienQuan(sanPham.value?.maSanPham);
      } catch (err) {
        console.error("Lỗi khi lấy sản phẩm:", err);
      }
    };

    const fetchFeedbacksFor = async (productId) => {
      try {
        const res = await axios.get(
          `http://localhost:8081/api/feedback/sanpham/${productId}`
        );
        feedbacks.value = Array.isArray(res.data) ? res.data : [];
      } catch (err) {
        console.error("Lỗi lấy feedback:", err);
        feedbacks.value = [];
      }
    };

    const loadSanPhamLienQuan = async (productId) => {
      if (!productId) return;
      try {
        isLoadingSanPhamLienQuan.value = true;
        sanPhamLienQuanError.value = null;
        const data = await fetchSanPhamLienQuan(productId);
        sanPhamLienQuan.value = Array.isArray(data) ? data : [];
        currentPage.value = 0;
      } catch (error) {
        console.error("❌ Error loading related products:", error);
        sanPhamLienQuan.value = [];
        sanPhamLienQuanError.value = "Không tải được sản phẩm liên quan.";
      } finally {
        isLoadingSanPhamLienQuan.value = false;
      }
    };

    watch(
      () => route.params.id,
      async (newId, oldId) => {
        if (!newId || newId === oldId) return;
        await fetchSanPhamChiTiet(newId);
        window.scrollTo(0, 0); // Cuộn lên đầu trang khi chuyển sản phẩm
      },
      { immediate: true }
    );

    return {
      sanPham, selectedValues, selectedVariant, quantity, mainImage, thumbnails, attributes, sanPhamLienQuan,
      isLoadingSanPhamLienQuan, sanPhamLienQuanError, currentPage, itemsPerPage, totalPages, paginatedProducts,
      carouselStyle, nextPage, prevPage, feedbacks, newFeedback, averageRating, displayedFeedbacks,
      addFeedback, deleteFeedback, isMyFeedback, formatDate, initialFeedbackCount, visibleFeedbacksCount,
      loadMoreFeedbacks, collapseFeedbacks, showFeedbackSuccessToast, toasts, showSuccessToast,
      handleContinueShopping, handleViewCart, handleCloseToast, selectAttribute, isOptionAvailable,
      resetSelection, changeQuantity, changeImage, buyNow, selectDefaultVariant, getPriceForOption,
      showPhuKienModal, selectedPhuKien, selectedPhuKienValues, selectedPhuKienVariant, phuKienQuantity,
      phuKienAttributes, openPhuKienModal, closePhuKienModal, selectPhuKienAttribute, changePhuKienQuantity,
      buyPhuKienNow,
    };
  },
};
</script>

<style scoped>
/* ✅ GLOBAL STYLES */
:global(body.modal-open) {
  overflow: hidden;
}

/* ✅ HEADER STYLES */
.shopee-header {
  background-color: #ee4d2d;
}

.top-bar a,
.top-bar span {
  transition: opacity 0.2s ease;
}

.top-bar a:hover {
  opacity: 0.8;
}

.search-form {
  max-width: 600px;
}

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

/* ✅ NEW VARIANT SELECTION STYLES - Phong cách CellphoneS */
.variant-group {
  margin-bottom: 1.25rem;
}

.variant-group-title {
  font-weight: 600;
  font-size: 1rem;
  margin-bottom: 0.75rem;
  color: #333;
  display: block;
}

.selected-value-display {
  color: #ee4d2d;
  font-weight: bold;
  margin-left: 8px;
}

.variant-options-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.variant-option {
  flex-grow: 1;
  min-width: 120px;
  padding: 0.75rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  text-align: center;
  position: relative;
  background-color: #fff;
}

.variant-option:hover {
  border-color: #d70018;
}

.variant-option.selected {
  border-color: #d70018;
  background-color: #fff7f4;
}

.variant-option.disabled {
  background-color: #f8f9fa;
  border-color: #e9ecef;
  cursor: not-allowed;
  color: #adb5bd;
}

.variant-option.disabled .option-value {
  text-decoration: line-through;
}

.option-value {
  display: block;
  font-weight: 500;
  font-size: 0.95rem;
  color: #212529;
}

.option-price {
  display: block;
  font-size: 0.85rem;
  font-weight: bold;
  color: #d70018;
  margin-top: 4px;
}

.variant-option.disabled .option-price,
.variant-option.disabled .option-value {
  color: #ced4da;
}

.checkmark-icon {
  position: absolute;
  top: 5px;
  right: 5px;
  font-size: 1rem;
  color: #d70018;
  background-color: white;
  border-radius: 50%;
}

/* ✅ PRICE STYLES */
.product-price-new {
  background-color: #f3f8fe;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 1.5rem;
}

.price-label-new {
  font-size: 14px;
  color: #555;
}

.price-values-new {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-top: 4px;
}

.discounted-price-new {
  font-size: 28px;
  font-weight: 700;
  color: #d70018;
}

.original-price-new {
  font-size: 18px;
  color: #888;
  text-decoration: line-through;
}

.price-placeholder-new {
  color: #888;
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

/* ✅ ACCESSORY STYLES */
.phu-kien-box {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  height: 100%;
}

.phu-kien-item-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  background-color: #ffffff;
  height: 100%;
}

.phu-kien-item-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.phu-kien-item-card .card-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 6px;
  margin-bottom: 0.75rem;
}

.phu-kien-item-card .card-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.phu-kien-item-card .card-price {
  font-size: 1rem;
  font-weight: 700;
  color: #d0011b;
}

/* ✅ ACCESSORY MODAL */
.pk-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.pk-modal-container {
  width: 90%;
  max-width: 500px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  animation: zoomIn 0.3s ease-out;
}

.pk-modal-content {
  display: flex;
  flex-direction: column;
}

.pk-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
}

.pk-modal-title {
  font-size: 1.25rem;
  font-weight: 600;
}

.pk-close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #888;
  cursor: pointer;
  transition: color 0.2s;
}

.pk-close-btn:hover {
  color: #333;
}

.pk-modal-body {
  padding: 1.5rem;
}

.pk-info-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.pk-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}

.pk-details {
  flex-grow: 1;
}

.pk-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.pk-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #ee4d2d;
}

.pk-attributes-section {
  margin-bottom: 1.5rem;
}

.pk-attribute-group {
  margin-bottom: 1rem;
}

.pk-attribute-label {
  font-weight: 600;
  display: block;
  margin-bottom: 0.5rem;
}

.pk-attribute-options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.pk-attribute-option {
  padding: 0.5rem 1rem;
  border: 1px solid #ccc;
  border-radius: 20px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.2s;
}

.pk-attribute-option:hover {
  border-color: #ee4d2d;
}

.pk-attribute-option.is-selected {
  background-color: #ee4d2d;
  color: #fff;
  border-color: #ee4d2d;
}

.pk-quantity-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.pk-quantity-label {
  font-weight: 600;
}

.pk-quantity-controls {
  display: flex;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.pk-quantity-btn {
  background-color: #f0f0f0;
  border: none;
  padding: 0.25rem 0.75rem;
  cursor: pointer;
}

.pk-quantity-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pk-quantity-display {
  width: 50px;
  text-align: center;
  border: none;
  background: #fff;
  font-weight: 500;
}

.pk-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #eee;
}

.pk-btn-cancel,
.pk-btn-add-cart {
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}

.pk-btn-cancel {
  background-color: #f0f0f0;
  color: #333;
  border: none;
}

.pk-btn-add-cart {
  background-color: #ee4d2d;
  color: #fff;
  border: none;
}

.pk-btn-add-cart:disabled {
  background-color: #ccc;
  cursor: not-allowed;
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
  /* Cực kỳ cao */
  pointer-events: auto !important;
  /* Luôn có thể tương tác */
}

.add-to-cart-toast-fixed {
  display: flex !important;
  align-items: center !important;
  gap: 10px !important;
  background: #fff !important;
  border: 1px solid #ccc !important;
  padding: 12px 16px !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1) !important;
  min-width: 320px !important;
  animation: fadeInFixed 0.3s ease !important;
  position: relative !important;
  z-index: 999999 !important;
  /* Cực kỳ cao */
  pointer-events: auto !important;
  /* Luôn có thể tương tác */
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
  /* Luôn có thể tương tác */
}

/* ✅ NÚT TOAST - Đảm bảo 100% có thể click */
.toast-btn-continue,
.toast-btn-cart {
  pointer-events: auto !important;
  /* Luôn có thể click */
  cursor: pointer !important;
  /* Luôn hiện con trỏ */
  z-index: 999999 !important;
  /* Z-index cực cao */
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
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1) !important;
}

.toast-btn-continue:active,
.toast-btn-cart:active {
  transform: translateY(0) !important;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1) !important;
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
  /* Luôn có thể click */
  z-index: 999999 !important;
  /* Z-index cực cao */
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
  /* Timer không cần click */
}

/* ✅ RELATED PRODUCTS CAROUSEL SECTION */
.related-products-section {
  margin-top: 2rem;
  padding: 1.5rem;
  background-color: #f8f9fa;
  border-radius: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.carousel-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.carousel-btn {
  width: 40px;
  height: 40px;
  border: 2px solid #ee4d2d;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1.2rem;
  color: #ee4d2d;
}

.carousel-btn:hover:not(.disabled) {
  background: #ee4d2d;
  color: white;
  transform: scale(1.05);
}

.carousel-btn.disabled {
  border-color: #ddd;
  color: #ddd;
  cursor: not-allowed;
  opacity: 0.5;
}

.page-indicator {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-weight: 600;
  color: #2c3e50;
  font-size: 1rem;
}

.current-page {
  color: #ee4d2d;
  font-size: 1.1rem;
}

.products-carousel-container {
  overflow: hidden;
  border-radius: 8px;
}

.products-carousel {
  display: flex;
  width: 100%;
  transition: transform 0.3s ease-in-out;
}

.carousel-page {
  min-width: 100%;
  flex-shrink: 0;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

.product-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.08);
  padding: 0.75rem;
  text-decoration: none;
  color: inherit;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  position: relative;
  border: 1px solid #f0f0f0;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  text-decoration: none;
  color: inherit;
  border-color: #ee4d2d;
}

.product-image {
  position: relative;
  width: 100%;
  padding-top: 75%;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 0.5rem;
  background: #f8f9fa;
}

.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.25rem;
  line-height: 1.3;
  display: -webkit-box;
 
  overflow: hidden;
  min-height: 2.3rem;
}

.product-desc {
  font-size: 0.75rem;
  color: #666;
  margin-bottom: 0.25rem;
  line-height: 1.3;
  display: -webkit-box;
 
  overflow: hidden;
}

.product-price {
  font-size: 1rem;
  font-weight: bold;
  color: #e53935;
  margin: 0.25rem 0;
}

.product-category {
  font-size: 0.75rem;
  color: #666;
  margin-bottom: 0.25rem;
  font-weight: 500;
}

.discount-badge {
  position: absolute;
  top: 0.5rem;
  left: 0.5rem;
  background: linear-gradient(135deg, #d0011b, #b80012);
  color: #fff;
  font-size: 0.7rem;
  font-weight: bold;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  z-index: 10;
  box-shadow: 0 2px 6px rgba(208, 1, 27, 0.3);
}

.text-muted.small {
  font-size: 0.75rem !important;
  font-weight: 500;
  color: #666 !important;
  text-align: center;
  margin-top: auto;
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
  from {
    width: 100%;
  }

  to {
    width: 0;
  }
}

@keyframes countdown {
  from {
    width: 100%;
  }

  to {
    width: 0%;
  }
}

@keyframes zoomIn {
  from {
    transform: scale(0.8);
    opacity: 0;
  }

  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* ✅ RESPONSIVE DESIGN */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
  }
}

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

  .section-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
  }

  .carousel-controls {
    align-self: flex-end;
  }

  .related-products-section {
    padding: 1rem;
    margin-top: 1.5rem;
  }

  .section-title {
    font-size: 1.3rem;
  }
}

@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .carousel-btn {
    width: 35px;
    height: 35px;
    font-size: 1rem;
  }
}

.offcanvas.offcanvas-40 {
  width: 40% !important;
  max-width: none;
}
</style>