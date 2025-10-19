<template>
  <div class="shopee-page">
    <header class="shopee-header px-5 py-3">
      <div
        class="d-flex justify-content-between align-items-center top-bar mb-2 flex-wrap py-2"
        style="font-size: 1.05rem; font-weight: 500;"
      >
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
        <div class="d-flex align-items-center gap-3 flex-wrap text-white">
          <a 
            href="#" 
            class="text-white text-decoration-none" 
            @click.prevent="$router.push('/admin')"
          >
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
            <div class="dropdown" v-if="tenHienThi" ref="userDropdown" >
              <a
                href="#"
                class="fw-bold text-white text-decoration-none d-flex align-items-center"
                @click.prevent="isDropdownOpen = !isDropdownOpen"
              >
                <img :src="avatarUrl" alt="Avatar" class="rounded-circle me-2 user-avatar" />
                <span>{{ tenHienThi }}</span>
                <i class="bi ms-1" :class="isDropdownOpen ? 'bi-caret-up-fill' : 'bi-caret-down-fill'"></i>
              </a>

              <ul
                v-if="isDropdownOpen"
                class="dropdown-menu dropdown-menu-end shadow-lg border-0 rounded-3 p-2 show"
                style="display:block; position:absolute;"
              >
                <li>
                  <h6 class="dropdown-header text-muted text-truncate">
                    Chào mừng, {{ tenHienThi }}!
                  </h6>
                </li>
                <li><hr class="dropdown-divider my-1" /></li>
                <li>
                  <router-link 
                    :to="{ path: '/donhang', query: { tab: 'ThongTinTaiKhoan' } }" 
                    class="dropdown-item rounded-2"
                  >
                    <i class="bi bi-person-circle me-2"></i> Thông tin cá nhân
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

            <div v-else class="d-flex align-items-center">
              <router-link to="/dang-ky" class="btn text-white me-2">Đăng ký</router-link>
              <router-link to="/login" class="btn btn-warning text-dark fw-bold rounded-1">Đăng nhập</router-link>
            </div>
          </div>
        </div>
      </div>
      
      <div class="d-flex justify-content-between align-items-center flex-wrap">
        <a href="/" class="brand text-white text-decoration-none fs-3 fw-bold d-flex align-items-center gap-2">
          <span class="logo-placeholder"></span> ByteTech
        </a>

        <form @submit.prevent="goToSearch" class="d-flex flex-grow-1 mx-4 search-form" style="max-width: 800px;">
          <input
            type="text"
            v-model="searchQuery"
            class="form-control me-2 rounded-1 shadow-sm"
            placeholder="🔍 Tìm kiếm sản phẩm"
          />
          <button class="btn btn-light rounded-1 px-4 fw-bold" type="submit">Tìm</button>
        </form>

        <div class="d-flex align-items-center gap-4 text-white fs-5">
          <a href="#" class="text-white position-relative hover-link">
            <i class="bi bi-heart"></i>
          </a>

       <router-link to="/giohang" class="cart-link" aria-label="Giỏ hàng">
  <span class="cart-btn">
    <i class="bi bi-cart3"></i>
    <span v-if="cartItemCount > 0" class="cart-badge">{{ cartItemCount }}</span>
  </span>
</router-link>
          

          <a href="#" class="text-white hover-link"><i class="bi bi-gear-fill"></i></a>
        </div>
      </div>
    </header>

    <div class="bg-white shadow-sm py-2">
      <div class="container d-flex justify-content-around flex-wrap custom-nav">
        <router-link to="/" class="text-dark">🏠 Trang chủ</router-link>
        <router-link to="/voucher" class="text-dark">🔥 Voucher </router-link>
        <router-link to="/danh-muc" class="text-dark position-relative" 	
                      @mouseenter="open = true" @mouseleave="open = false">
          📚 Danh mục
          <ul class="dropdown-menu show" 
              v-if="open" 
              style="max-height: 300px; overflow-y: auto; left: 0; right: auto; margin-top: 0.5rem;">
            <li v-for="dm in danhMuc" :key="dm.id">
              <router-link class="dropdown-item text-center" :to="`/danh-muc/${dm.id}`">
                {{ dm.ten }}
              </router-link>
            </li>
          </ul>
        </router-link>
        
        <router-link to="/donhang" class="text-dark">📦 Đơn hàng</router-link>
        <router-link to="/duyet-don" class="text-dark">🛠 Duyệt đơn hàng</router-link>
        <router-link to="/chat" class="text-dark">💬 Chat</router-link>
      </div>
    </div>

    <div class="container my-4">
      <div class="row g-3">
        <div class="col-md-8">
          <div
            id="shopeeBanner"
            class="carousel slide carousel-fade shadow rounded-3 overflow-hidden"
            data-bs-ride="carousel"
            data-bs-interval="3000"
          >
            <div class="carousel-indicators">
              <button type="button" data-bs-target="#shopeeBanner" data-bs-slide-to="0" class="active"
                aria-current="true" aria-label="Slide 1"></button>
              <button type="button" data-bs-target="#shopeeBanner" data-bs-slide-to="1" aria-label="Slide 2"></button>
              <button type="button" data-bs-target="#shopeeBanner" data-bs-slide-to="2" aria-label="Slide 3"></button>
              <button type="button" data-bs-target="#shopeeBanner" data-bs-slide-to="3" aria-label="Slide 4"></button>
              <button type="button" data-bs-target="#shopeeBanner" data-bs-slide-to="4" aria-label="Slide 5"></button>
              <button type="button" data-bs-target="#shopeeBanner" data-bs-slide-to="5" aria-label="Slide 6"></button>
              <button type="button" data-bs-target="#shopeeBanner" data-bs-slide-to="6" aria-label="Slide 7"></button>
              <button type="button" data-bs-target="#shopeeBanner" data-bs-slide-to="7" aria-label="Slide 8"></button>
            </div>
            <div class="carousel-inner">
              <div class="carousel-item active">
                <img src="/images/banner/banner4.jpg" class="d-block w-100 banner-img" alt="Banner 1">
              </div>
              <div class="carousel-item">
                <img src="/images/banner/banner2.webp" class="d-block w-100 banner-img" alt="Banner 2">
              </div>
              <div class="carousel-item">
                <img src="/images/banner/banner3.jpg" class="d-block w-100 banner-img" alt="Banner 3">
              </div>
              <div class="carousel-item">
                <img src="/images/banner/banner1.jpg" class="d-block w-100 banner-img" alt="Banner 4">
              </div>
              <div class="carousel-item">
                <img src="/images/banner/banner1.jpg" class="d-block w-100 banner-img" alt="Banner 5">
              </div>
              <div class="carousel-item">
                <img src="/images/banner/banner5.jpeg" class="d-block w-100 banner-img" alt="Banner 6">
              </div>
              <div class="carousel-item">
                <img src="/images/banner/banner1.jpg" class="d-block w-100 banner-img" alt="Banner 7">
              </div>
              <div class="carousel-item">
                <img src="/images/banner/banner1.jpg" class="d-block w-100 banner-img" alt="Banner 8">
              </div>
            </div>
            <button class="carousel-control-prev" type="button" data-bs-target="#shopeeBanner" data-bs-slide="prev">
              <span class="carousel-control-prev-icon bg-dark rounded-circle p-2"></span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#shopeeBanner" data-bs-slide="next">
              <span class="carousel-control-next-icon bg-dark rounded-circle p-2"></span>
            </button>
          </div>
        </div>
        <div class="col-md-4 d-flex flex-column gap-3">
          <img src="/images/banner/banner1.jpg" class="side-banner rounded-3 shadow-sm" alt="Side banner 1">
          <img src="/images/banner/banner1.jpg" class="side-banner rounded-3 shadow-sm" alt="Side banner 2">
        </div>
      </div>
    </div>

    <div class="container py-4">
      <h5 class="fw-bold mb-4">DANH MỤC</h5>
      <div id="carouselDanhMuc" class="carousel slide carousel-category" data-bs-ride="carousel">
        <div class="carousel-inner">
          <div class="carousel-item active">
              <div class="row row-cols-5 g-4 text-center">
                <div class="col">
                  <router-link
                    :to="{ name: 'DanhMucDienThoai', params: { maDanhMuc: 1 } }"
                    class="text-decoration-none text-dark d-block category-box"
                  >
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 1</div>
                  </router-link>
                </div>
                <div class="col">
                  <router-link
                    :to="{ name: 'DanhMucDienThoai', params: { maDanhMuc: 2 } }"
                    class="text-decoration-none text-dark d-block category-box"
                  >
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 2</div>
                  </router-link>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 3</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 4</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 5</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 6</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 7</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 8</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 9</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 10</div>
                  </a>
                </div>
              </div>
            </div>
          <div class="carousel-item">
              <div class="row row-cols-5 g-4 text-center">
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 11</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 12</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 13</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 14</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 15</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 16</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 17</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 18</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 19</div>
                  </a>
                </div>
                <div class="col">
                  <a href="#" class="text-decoration-none text-dark d-block category-box">
                    <div class="cat-avatar rounded-circle bg-white"></div>
                    <div class="mt-2 small fw-medium text-wrap">Danh mục 20</div>
                  </a>
                </div>
              </div>
            </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#carouselDanhMuc" data-bs-slide="prev">
          <span class="carousel-control-prev-icon"></span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#carouselDanhMuc" data-bs-slide="next">
          <span class="carousel-control-next-icon"></span>
        </button>
      </div>
    </div>

    <div class="container py-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <button
          class="btn btn-light rounded-circle shadow-sm"
          :disabled="currentPage === 1"
          @click="prevPage"
          aria-label="Trang trước"
        >‹</button>

        <h2 class="shopee-title m-0">
          Danh sách sản phẩm
          <span class="ms-3 fs-6 text-muted">Trang {{ currentPage }} / {{ totalPages }}</span>
        </h2>

        <button
          class="btn btn-light rounded-circle shadow-sm"
          :disabled="currentPage === totalPages"
          @click="nextPage"
          aria-label="Trang sau"
        >›</button>
      </div>

      <div class="product-grid">
        <router-link
          v-for="p in pageProducts"
          :key="p.maSanPham"
          :to="{ name: 'SanPhamChiTiet', params: { id: p.maSanPham } }"
          class="product-card position-relative"
        >
          <div v-if="p.giaBienThe && p.loaiGiam === 1" class="discount-badge">
            Giảm {{ Math.round(p.giaTriGiamKhuyenMai * 100) }}%
          </div>
          <div v-else-if="p.giaBienThe && p.loaiGiam === 0" class="discount-badge">
            -{{ p.giaTriGiamKhuyenMai.toLocaleString() }}đ
          </div>

          <div class="product-image">
            <img
              :src="`/images/products/${p.maSanPham}.jpg`"
              :alt="p.tenSanPham"
              class="img-fluid"
              @error="handleImageError($event)"
            />
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
          <!-- <div v-else class="product-price">
            {{ p.gia.toLocaleString() }} đ
          </div> -->
          <div v-else class="product-price">
  {{ (p.gia || 0).toLocaleString() }} đ
</div>

          <div class="product-category">
            Danh mục: <span>{{ p.tenDanhMuc }}</span>
          </div>
          <div class="text-muted small mt-2">👆 Xem chi tiết</div>
        </router-link>
      </div>
    </div>
    <TrangChuPhuKien />

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
import { SanPham, logoutApi, fetchCart, validateToken } from "../service/api";
import TrangChuPhuKien from "@/components/TrangChuPhuKien.vue";

export default {
  name: "TrangChu",
  components: {
    TrangChuPhuKien,
  },
  
  data() {
    return {
      products: [],
      phukien: [],
      imageExists: {}, 
      token: localStorage.getItem("token"),
      cart: null,
      showOrderSuccessToast: false,
      successfulOrderCode: '',
      searchQuery: "",
      currentPage: 1,
      perPage: 10,
      open: false,
      // SỬA LỖI: Thêm các biến còn thiếu vào data
      isDropdownOpen: false, 
      role: localStorage.getItem("role"), // Lấy role từ localStorage
      avatarUrl: '/images/avatar/default.png', // Ảnh đại diện mặc định
      danhMuc: [
        { id: 1, ten: "Điện thoại" },
        { id: 2, ten: "Laptop" },
        { id: 3, ten: "Máy tính bảng" },
        { id: 4, ten: "Phụ kiện" },
        { id: 5, ten: "Thời trang nam" },
        { id: 6, ten: "Thời trang nữ" },
        { id: 7, ten: "Giày dép" },
        { id: 8, ten: "Đồng hồ" },
        { id: 9, ten: "Trang sức" },
        { id: 10, ten: "Mỹ phẩm" },
        { id: 11, ten: "Đồ gia dụng" },
        { id: 12, ten: "Đồ chơi" },
        { id: 13, ten: "Sách" },
        { id: 14, ten: "Nhạc cụ" },
        { id: 15, ten: "Thể thao" },
        { id: 16, ten: "Xe cộ" },
        { id: 17, ten: "Balo - Túi xách" },
        { id: 18, ten: "Thiết bị văn phòng" },
        { id: 19, ten: "Sản phẩm số" },
        { id: 20, ten: "Khác" }
      ]
    };
  },
  
  async created() {
    try {
      const res = await SanPham();
      this.products = res.data.map(p => ({
        ...p,
        id: p.maSanPham
      }));
      this.products.forEach(p => {
        this.$set(this.imageExists, p.maSanPham, true);
      });
    } catch (err) {
      console.error("Lỗi load sản phẩm:", err);
    }
  },
  
  computed: {
    totalPages() {
      return Math.ceil(this.products.length / this.perPage) || 1;
    },
    pageProducts() {
      const start = (this.currentPage - 1) * this.perPage;
      return this.products.slice(start, start + this.perPage);
    },
    productRows() {
      const row1 = this.pageProducts.slice(0, 5);
      const row2 = this.pageProducts.slice(5, 10);
      return [row1, row2];
    },
    tenHienThi() {
      const token = validateToken(); 
      if (token) {
        const tenHienThi = localStorage.getItem("tenHienThi");
        return tenHienThi && tenHienThi.trim() !== "" ? tenHienThi : null;
      }
      return null;
    },
    buttonText() {
      const token = validateToken(); 
      return token ? "Đăng xuất" : "Đăng nhập";
    },
    cartItemCount() {
      if (!this.cart || !this.cart.chiTietList) return 0;
      return this.cart.chiTietList.length;
    }
  },
  
  async mounted() {
    try {
      this.cart = await fetchCart();
    } catch (e) { console.error(e); }

    const query = this.$route.query;
    if (query.order_success === 'true' && query.order_code) {
      this.successfulOrderCode = query.order_code;
      this.showOrderSuccessToast = true;

      setTimeout(() => {
        this.showOrderSuccessToast = false;
      }, 5000);

      this.$router.replace({ query: null });
    }

    // SỬA LỖI: Thêm trình lắng nghe sự kiện click toàn cục
    document.addEventListener('click', this.closeDropdownOnClickOutside);
  },

  // SỬA LỖI: Gỡ bỏ trình lắng nghe khi component bị hủy để tránh rò rỉ bộ nhớ
  beforeDestroy() {
    document.removeEventListener('click', this.closeDropdownOnClickOutside);
  },

  methods: {
    // SỬA LỖI: Thêm phương thức để đóng dropdown khi click ra ngoài
    closeDropdownOnClickOutside(event) {
      // Kiểm tra nếu dropdown đang mở và người dùng không click vào phần tử dropdown
      if (this.isDropdownOpen && this.$refs.userDropdown && !this.$refs.userDropdown.contains(event.target)) {
        this.isDropdownOpen = false;
      }
    },
    goToSearch() {
      if (this.searchQuery.trim() !== "") {
        this.$router.push({
          name: "SearchSanPham",
          query: { tenSanPham: this.searchQuery }
        });
      }
    },
    toggleDropdown() {
      this.open = !this.open;
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        const y = window.scrollY;
        this.currentPage++;
        this.$nextTick(() => window.scrollTo({ top: y, left: 0, behavior: 'auto' }));
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        const y = window.scrollY;
        this.currentPage--;
        this.$nextTick(() => window.scrollTo({ top: y, left: 0, behavior: 'auto' }));
      }
    },
    handleImageError(maSanPham) {
      this.$set(this.imageExists, maSanPham, false);
    },
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
        console.log("🗑️ Dữ liệu người dùng đã xóa khỏi localStorage");

        console.log("📦 localStorage hiện tại:");
        for (let i = 0; i < localStorage.length; i++) {
          const key = localStorage.key(i);
          console.log(`${key}: ${localStorage.getItem(key)}`);
        }

        this.token = null;
        this.$router.push("/login");
      }
    }
  }
};
</script>

<style scoped>
/* Áp dụng nền + font cho component (thay vì body vì scoped) */
.shopee-page {
  background-color: #f8f9fa;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* Header */
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
  width: 24px;
  height: 24px;
  object-fit: cover;
}

/* Banners giả lập */
.banner-placeholder {
  width: 100%;
  height: 450px;
  background: #e9ecef;
}
.side-banner-placeholder {
  width: 100%;
  height: 215px;
  background: #e9ecef;
  border-radius: 12px;
}

/* Danh mục */
.carousel-category .carousel-control-prev,
.carousel-category .carousel-control-next {
  width: 50px;
  height: 50px;
  top: 50%;
  transform: translateY(-50%);
  background-color: #ffffff;
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
  opacity: 1;
  transition: all 0.3s;
}
.carousel-category .carousel-control-prev:hover,
.carousel-category .carousel-control-next:hover {
  background-color: #f0532d;
}
.carousel-category .carousel-control-prev-icon,
.carousel-category .carousel-control-next-icon {
  filter: invert(1);
}
.carousel-category .category-box:hover {
  transform: translateY(-5px);
  transition: all 0.3s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
.cat-avatar {
  width: 80px;
  height: 80px;
  border: 1px solid #eee;
}

/* Navigation */
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

/* Product grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.product-card {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  text-decoration: none;
  color: inherit;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}
.product-image .img-placeholder {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  background: #e9ecef;
}
.product-name {
  font-size: 16px;
  font-weight: 600;
}
.product-desc {
  font-size: 14px;
  color: #6c757d;
  min-height: 40px;
}
.product-price {
  color: #ee4d2d;
  font-weight: bold;
  font-size: 16px;
}
.product-category {
  font-size: 13px;
  color: #888;
}

/* Footer */
.shopee-footer {
  background-color: #fff;
  padding: 20px;
  border-top: 1px solid #eee;
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-top: 40px;
}

/* Dropdown */
.dropdown-menu a.dropdown-item {
  color: #212529 !important;
  font-weight: 500;
  text-decoration: none !important;
}
.dropdown-menu a.dropdown-item:hover {
  background-color: #f53d2d;
  color: white !important;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.product-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.08);
  padding: 12px;
  text-decoration: none;
  color: inherit;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.12);
}

.product-image {
  position: relative;
  width: 100%;
  padding-top: 100%; /* tạo khung vuông */
  overflow: hidden;
  border-radius: 10px;
  margin-bottom: 10px;
}

.product-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* giữ tỷ lệ, không méo */
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-name {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.3;
  display: -webkit-box;
  overflow: hidden;
}

.product-desc {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 6px;
}

.product-price {
  font-size: 1.1rem;
  font-weight: bold;
  color: #e53935;
  margin-top: auto; /* đẩy xuống dưới */
}

.product-category {
  font-size: 0.85rem;
  color: #999;
}

.banner-img {
  height: 450px;
  object-fit: cover;
}
.side-banner {
  height: 215px;
  width: 100%;
  object-fit: cover;
}

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

@keyframes countdown {
  from { width: 100%; }
  to { width: 0%; }
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

.product-card {
  position: relative;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  padding: 12px;
  transition: transform 0.2s ease;
}
.product-card:hover {
  transform: translateY(-4px);
}

.discount-badge {
  position: absolute;
  top: 0;
  left: 0;
  background: #d0011b; 
  color: #fff;
  font-size: 0.85rem;
  font-weight: bold;
  padding: 4px 10px;
  border-top-left-radius: 6px;
  border-bottom-right-radius: 6px;
  z-index: 10;
}

.discount-badge::after {
  content: "";
  position: absolute;
  bottom: -6px;
  left: 0;
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-top: 6px solid #b80012; 
}

.product-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  align-items: stretch;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  text-decoration: none;
  color: inherit;
  transition: box-shadow .2s ease;
}
.product-card:hover {
  box-shadow: 0 6px 20px rgba(0,0,0,.08);
}

.btn[disabled] {
  opacity: .5;
  cursor: not-allowed;
}

:root {
  /* Đồng bộ với header cam – tùy chỉnh nếu cần */
  --brand-orange: #FF5A3D;     /* cam chính */
  --brand-orange-600: #E74C2E; /* cam đậm hover */
  --on-brand: #ffffff;         /* màu chữ/icon trên nền cam */
}

/* Link và nút chứa icon */
.cart-link {
  color: var(--on-brand);
  text-decoration: none;
}

.cart-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;      /* kích thước nút nền */
  height: 42px;
  border-radius: 12px;
  background: rgba(255,255,255,0.12);           /* nền trong suốt nhẹ để hài hòa */
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.18);
  transition: all 0.2s ease;
}

.cart-link:hover .cart-btn {
  background: rgba(255,255,255,0.18);
  transform: translateY(-1px);
}

/* Icon giỏ hàng to, rõ ràng */
.cart-btn .bi-cart3 {
  font-size: 30px;  /* 28–32px tùy ý */
  line-height: 1;
}

/* Badge kiểu pill, nổi bật nhưng tinh tế */
.cart-badge {
  position: absolute;
  top: -6px;
  right: -8px;
  padding: 2px 8px;
  min-width: 22px;
  height: 22px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: #1f2937;         /* chữ xám đậm để đọc tốt trên nền sáng */
  background: #ffffff;    /* badge nền trắng cho sạch */
  box-shadow:
    0 2px 6px rgba(0,0,0,0.12),          /* đổ bóng mềm */
    inset 0 0 0 2px var(--brand-orange); /* viền cam đồng bộ header */
}


.cart-dot {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: inset 0 0 0 2px var(--brand-orange);
}


</style>