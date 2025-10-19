<template>
  <div class="container py-4">
    <h4 class="fw-bold mb-4">
      Kết quả tìm kiếm: "{{ keyword }}"
    </h4>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Đang tìm sản phẩm...</p>
    </div>

    <div v-else-if="products.length === 0" class="alert alert-info">
      Không tìm thấy sản phẩm nào khớp với từ khóa "{{ keyword }}".
    </div>

    <div v-else class="row">
      <div class="col-lg-3">
        <div class="filter-sidebar">
          <h5 class="filter-header">
            <i class="bi bi-funnel-fill me-2"></i> Bộ lọc tìm kiếm
          </h5>

          <div class="filter-group">
            <label for="priceFilter" class="form-label fw-bold">Khoảng giá</label>
            <select id="priceFilter" class="form-select" v-model="selectedPriceFilter">
              <option value="all">Tất cả</option>
              <option value="under_2m">Dưới 2 triệu</option>
              <option value="2m_to_5m">Từ 2 - 5 triệu</option>
              <option value="10m_to_20m">Từ 10 - 20 triệu</option>
              <option value="20m_to_35m">Từ 20 - 35 triệu</option>
              <option value="over_35m">Từ 35 triệu trở lên</option>
            </select>
          </div>

          <div class="filter-group">
            <label class="form-label fw-bold">Thương hiệu</label>
            <div v-if="uniqueBrands.length === 0" class="text-muted small">
              Không có thương hiệu để lọc.
            </div>
            <div v-for="brand in uniqueBrands" :key="brand" class="form-check">
              <input
                class="form-check-input"
                type="checkbox"
                :value="brand"
                :id="'brand-' + brand.replace(/\s+/g, '')"
                v-model="selectedBrands"
              />
              <label class="form-check-label" :for="'brand-' + brand.replace(/\s+/g, '')">
                {{ brand }}
              </label>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-9">
        <div v-if="filteredProducts.length === 0" class="alert alert-warning mt-4 mt-lg-0">
          Không tìm thấy sản phẩm nào phù hợp với bộ lọc của bạn.
        </div>
        <div v-else class="product-grid">
          <router-link
            v-for="p in filteredProducts"
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
                :src="preloadedImages[p.maSanPham] || '/images/products/no-image.jpg'"
                :alt="p.tenSanPham"
                class="img-fluid"
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
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useRoute } from "vue-router";
import { searchSanPham } from "@/service/api";

const route = useRoute();
const keyword = ref(route.query.tenSanPham || "");
const products = ref([]);
const loading = ref(true);
const preloadedImages = ref({});

// --- State để lọc ---
const selectedPriceFilter = ref("all");
const selectedBrands = ref([]);

// --- Computed Properties cho việc lọc ---
const uniqueBrands = computed(() => {
  const brands = products.value.map(p => p.thuongHieu);
  return [...new Set(brands)];
});

const priceFilters = {
  under_2m: (price) => price < 2000000,
  "2m_to_5m": (price) => price >= 2000000 && price <= 5000000,
  "10m_to_20m": (price) => price >= 10000000 && price <= 20000000,
  "20m_to_35m": (price) => price >= 20000000 && price <= 35000000,
  over_35m: (price) => price >= 35000000,
};

const filteredProducts = computed(() => {
  let tempProducts = products.value;

  if (selectedBrands.value.length > 0) {
    tempProducts = tempProducts.filter(p => selectedBrands.value.includes(p.thuongHieu));
  }

  if (selectedPriceFilter.value !== "all") {
    const filterFn = priceFilters[selectedPriceFilter.value];
    if (filterFn) {
      tempProducts = tempProducts.filter(p => {
        const price = p.giaBienThe ?? p.gia;
        return filterFn(price);
      });
    }
  }

  return tempProducts;
});

// --- Functions ---
const preloadImage = (maSanPham) => {
  return new Promise((resolve) => {
    const img = new Image();
    img.src = `/images/products/${maSanPham}.jpg`;
    img.onload = () => {
      preloadedImages.value[maSanPham] = img.src;
      resolve();
    };
    img.onerror = () => {
      preloadedImages.value[maSanPham] = "/images/products/no-image.jpg";
      resolve();
    };
  });
};

const fetchProducts = async (kw, showLoading = false) => {
  if (showLoading) loading.value = true;
  try {
    const res = await searchSanPham(kw);
    products.value = res.data;
    await Promise.all(products.value.map((p) => preloadImage(p.maSanPham)));
  } catch (err) {
    console.error("Lỗi tìm kiếm sản phẩm:", err);
    products.value = [];
  } finally {
    if (showLoading) loading.value = false;
  }
};

// --- Lifecycle và Watchers ---
onMounted(() => {
  if (keyword.value) {
    fetchProducts(keyword.value, true);
  }
});

watch(
  () => route.query.tenSanPham,
  (newVal, oldVal) => {
    if (newVal && newVal !== oldVal) {
      keyword.value = newVal;
      selectedPriceFilter.value = "all";
      selectedBrands.value = [];
      fetchProducts(newVal, true);
    }
  }
);
</script>

<style scoped>
/* CSS cho bố cục sidebar */
.filter-sidebar {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.filter-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #dee2e6;
  font-size: 1.1rem;
}

.filter-group {
  margin-bottom: 24px;
}

.filter-group .form-check {
  margin-bottom: 8px;
}

/* Style cho danh sách sản phẩm */
.product-grid {
  display: grid;
  /* Chỉnh lại số cột cho phù hợp với không gian còn lại */
  grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
  gap: 1.25rem;
}

/* Các style khác giữ nguyên */
.product-card { background: #fff; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.08); padding: 12px; text-decoration: none; color: inherit; transition: .2s; }
.product-card:hover { transform: translateY(-5px); box-shadow: 0 8px 16px rgba(0,0,0,0.12); }
.product-image { position: relative; width: 100%; padding-top: 100%; overflow: hidden; border-radius: 10px; margin-bottom: 10px; }
.product-image img { position: absolute; top:0; left:0; width:100%; height:100%; object-fit:cover; transition:.3s; }
.product-card:hover .product-image img { transform: scale(1.05); }
.product-name { font-size: 1rem; font-weight: 600; margin-bottom: 6px; }
.product-desc { font-size: .85rem; color:#666; margin-bottom: 8px; }
.product-price { font-size: 1.1rem; font-weight: bold; color: #e53935; margin-top: auto; }
.product-category { font-size: .85rem; color: #999; }
.discount-badge { position:absolute; top:0; left:0; background:#d0011b; color:#fff; font-size:.85rem; font-weight:bold; padding:4px 10px; border-top-left-radius:12px; border-bottom-right-radius:12px; z-index:10; }
</style>