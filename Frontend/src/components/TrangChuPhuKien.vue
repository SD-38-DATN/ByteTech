<template>
  <div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <!-- Back -->
      <button
        class="btn btn-light rounded-circle shadow-sm"
        :disabled="currentPage === 1"
        @click="prevPage"
        aria-label="Trang trước"
      >
        ‹
      </button>

      <h2 class="mb-0 fw-bold shopee-title">
        Phụ Kiện Dành Cho Bạn
        <span class="ms-3 fs-6 text-muted">Trang {{ currentPage }} / {{ totalPages }}</span>
      </h2>

      <!-- Next -->
      <button
        class="btn btn-light rounded-circle shadow-sm"
        :disabled="currentPage === totalPages"
        @click="nextPage"
        aria-label="Trang sau"
      >
        ›
      </button>
    </div>

    <!-- Loading -->
    <div v-if="isLoading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
      <p class="mt-2">Đang tải danh sách phụ kiện...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">
      <p class="fw-bold">Đã xảy ra lỗi!</p>
      {{ error }}
    </div>

    <!-- Grid: hiển thị 10 item/trang -->
    <div v-else-if="phukien.length > 0" class="product-grid">
      <router-link
        v-for="item in pageItems"
        :key="item.maPhuKien"
        :to="`/phukien/${item.maPhuKien}`"
        class="product-card"
      >
        <div class="product-image-wrapper">
          <img
            :src="getImageSrc(item)"
            :alt="item.tenPhuKien"
            class="product-image"
            @error="handleImageError(item)"
          />
        </div>

        <div class="product-content">
          <div class="product-name">{{ item.tenPhuKien }}</div>
          <div class="product-desc">{{ item.moTa }}</div>
          <div class="product-desc">Số lượng: <span>{{ item.soLuong }}</span></div>

          <!-- Giá: nếu null thì hiện 'Liên hệ' -->
          <p class="product-price">
            {{ item.gia == null ? 'Liên hệ' : formatCurrency(item.gia) }}
          </p>

          <p class="product-category">
            Danh mục: {{ item.tenDanhMucPhuKien || "Chưa phân loại" }}
          </p>
        </div>
      </router-link>
    </div>

    <div v-else class="text-center py-5">
      <p class="text-muted">Hiện chưa có phụ kiện nào để hiển thị.</p>
    </div>
  </div>
</template>

<script>
import { getAllPhuKien } from "../service/api";

export default {
  name: "DanhSachPhuKien",
  data() {
    return {
      phukien: [],
      isLoading: true,
      error: null,

      // Phân trang
      currentPage: 1,
      perPage: 10, // 2 dòng x 5 cột
    };
  },
  async created() {
    await this.fetchData();
  },
  computed: {
    totalPages() {
      return Math.ceil(this.phukien.length / this.perPage) || 1;
    },
    pageItems() {
      const start = (this.currentPage - 1) * this.perPage;
      return this.phukien.slice(start, start + this.perPage);
    },
  },
  watch: {
    // Mỗi khi danh sách thay đổi, đưa về trang 1 để tránh lệch chỉ số
    phukien() {
      this.currentPage = 1;
    },
  },
  methods: {
    async fetchData() {
      this.isLoading = true;
      this.error = null;
      try {
        const response = await getAllPhuKien();
        const list = Array.isArray(response.data) ? response.data : [];
        // Gắn cờ hasError = false để xử lý lỗi ảnh
        this.phukien = list.map((p) => ({ ...p, hasError: false }));
      } catch (err) {
        console.error("Lỗi khi tải dữ liệu phụ kiện:", err);
        this.error = "Không thể tải phụ kiện.";
      } finally {
        this.isLoading = false;
      }
    },
    // Nút phân trang
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        // Không cuộn trang để giữ nguyên vị trí
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    // Hiển thị giá
    formatCurrency(value) {
      try {
        return new Intl.NumberFormat("vi-VN", {
          style: "currency",
          currency: "VND",
        }).format(value);
      } catch {
        return `${value?.toLocaleString?.("vi-VN") ?? value} đ`;
      }
    },
    // Ảnh: nếu lỗi thì chuyển placeholder
    getImageSrc(item) {
      return item.hasError
        ? "https://placehold.co/300x300/e0e0e0/666666?text=No+Image"
        : `/images/accessories/${item.maPhuKien}.jpg`;
    },
    handleImageError(item) {
      return () => {
        if (!item.hasError) {
          item.hasError = true;
        }
      };
    },
  },
};
</script>

<style scoped>
.shopee-title {
  font-size: 20px;
  font-weight: 500;
  color: rgba(0,0,0,.54);
  text-transform: uppercase;
  margin-bottom: 20px;
}

/* ✅ Lưới 5 cột cố định (2 dòng x 5 = 10 item/trang) */
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

/* Card sản phẩm */
.product-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  text-decoration: none;
  color: inherit;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

/* Ảnh */
.product-image-wrapper {
  width: 100%;
  padding-top: 100%;
  position: relative;
  overflow: hidden;
  background: #f8f9fa;
}

.product-image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Nội dung */
.product-content {
  padding: 12px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.product-name {
  font-size: 1rem;
  font-weight: bold;
  color: #333;
  margin: 0 0 4px 0;
  line-height: 1.3;
}

.product-desc {
  font-size: 14px;
  color: #6c757d;
  min-height: 40px;
}

.product-price {
  font-size: 1.1rem;
  font-weight: bold;
  color: #D70018;
  margin: 0 0 8px 0;
}

.product-category {
  font-size: 0.8rem;
  color: #757575;
  margin: 0 0 12px 0;
}

/* Nút trạng thái disabled */
.btn[disabled] {
  opacity: .5;
  cursor: not-allowed;
}

/* Responsive */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
@media (max-width: 992px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>