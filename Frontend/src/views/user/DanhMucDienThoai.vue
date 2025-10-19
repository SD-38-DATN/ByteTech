<template>
  <div class="container py-4">
    <h4 class="fw-bold mb-4">
      Sản phẩm theo danh mục: {{ categoryName }}
    </h4>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-2">Đang tải sản phẩm...</p>
    </div>

    <div v-else-if="products.length === 0" class="alert alert-info">
      Không có sản phẩm nào trong danh mục này.
    </div>

    <div v-else class="row">
      <div class="col-lg-3">
        <div class="filter-sidebar">
          <h5 class="fw-bold mb-3">Bộ lọc</h5>

          <div class="mb-4">
            <label for="priceFilter" class="form-label fw-bold">Lọc theo giá:</label>
            <select id="priceFilter" class="form-select" v-model="selectedPriceFilter">
              <option value="all">Tất cả mức giá</option>
              <option value="under_2m">Dưới 2 triệu</option>
              <option value="2m_to_5m">Từ 2 - 5 triệu</option>
              <option value="10m_to_20m">Từ 10 - 20 triệu</option>
              <option value="20m_to_35m">Từ 20 - 35 triệu</option>
              <option value="over_35m">Từ 35 triệu trở lên</option>
            </select>
          </div>

          <div class="mb-4" v-if="uniqueBrands.length > 0">
            <label class="form-label fw-bold">Lọc theo thương hiệu:</label>
            <div class="brand-filter-list">
              <div v-for="brand in uniqueBrands" :key="brand" class="form-check mb-2">
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
          
          <div 
            v-for="(options, attributeName) in availableFilters" 
            :key="attributeName" 
            class="mb-4"
          >
             <label class="form-label fw-bold">Lọc theo {{ attributeName }}:</label>
             <div class="brand-filter-list">
                <div v-for="option in options" :key="option" class="form-check mb-2">
                    <input 
                        class="form-check-input"
                        type="checkbox"
                        :value="option"
                        :id="`attr-${attributeName}-${option}`.replace(/\s+/g, '')"
                        v-model="selectedAttributes[attributeName]"
                    />
                    <label class="form-check-label" :for="`attr-${attributeName}-${option}`.replace(/\s+/g, '')">
                        {{ option }}
                    </label>
                </div>
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
              Giảm {{ p.giaTriGiamKhuyenMai * 100 }}%
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
            <div class="product-desc">
              Số lượng: <span>{{ p.soLuong }}</span>
            </div>

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
import axios from "axios";

const route = useRoute();
const products = ref([]);
const loading = ref(true);
const preloadedImages = ref({});

// --- Refs cho các bộ lọc ---
const selectedPriceFilter = ref("all");
const selectedBrands = ref([]);
// ✅ MỚI: Ref để lưu trữ các thuộc tính được chọn
const selectedAttributes = ref({});

const categoryName = computed(() => {
  return products.value.length > 0 ? products.value[0].tenDanhMuc : "Chưa xác định";
});

// Định nghĩa các hàm lọc cho từng khoảng giá
const priceFilters = {
  under_2m: (price) => price < 2000000,
  "2m_to_5m": (price) => price >= 2000000 && price <= 5000000,
  "10m_to_20m": (price) => price >= 10000000 && price <= 20000000,
  "20m_to_35m": (price) => price >= 20000000 && price <= 35000000,
  over_35m: (price) => price >= 35000000,
};

// Computed property để lấy ra danh sách các thương hiệu duy nhất
const uniqueBrands = computed(() => {
  if (!products.value) return [];
  const brands = new Set(products.value.map(p => p.thuongHieu));
  return Array.from(brands).sort();
});

// ✅ MỚI: Computed property để lấy ra tất cả các bộ lọc thuộc tính có sẵn
const availableFilters = computed(() => {
    const filters = {};
    products.value.forEach(product => {
        // API trả về mảng chiTietThuocTinh cho mỗi sản phẩm
        (product.chiTietThuocTinh || []).forEach(attr => {
            const { tenThuocTinh, tenThuocTinhBienThe } = attr;
            if (tenThuocTinh && tenThuocTinhBienThe) {
                if (!filters[tenThuocTinh]) {
                    filters[tenThuocTinh] = new Set();
                }
                filters[tenThuocTinh].add(tenThuocTinhBienThe);
            }
        });
    });

    // Chuyển Set thành mảng đã sắp xếp và khởi tạo selectedAttributes nếu chưa có
    for (const key in filters) {
        filters[key] = Array.from(filters[key]).sort();
        if (!selectedAttributes.value[key]) {
             selectedAttributes.value[key] = [];
        }
    }
    return filters;
});


// ✅ CẬP NHẬT: Computed property để lọc theo giá, thương hiệu, và thuộc tính
const filteredProducts = computed(() => {
  let tempProducts = products.value;

  // 1. Lọc theo giá
  if (selectedPriceFilter.value !== 'all') {
    const filterFunction = priceFilters[selectedPriceFilter.value];
    if (filterFunction) {
      tempProducts = tempProducts.filter(p => {
        const price = p.giaBienThe ?? p.gia;
        return filterFunction(price);
      });
    }
  }

  // 2. Lọc theo thương hiệu
  if (selectedBrands.value.length > 0) {
    tempProducts = tempProducts.filter(p => 
      selectedBrands.value.includes(p.thuongHieu)
    );
  }

  // ✅ MỚI: 3. Lọc theo thuộc tính
  const activeAttributeFilters = Object.keys(selectedAttributes.value).filter(
      key => selectedAttributes.value[key] && selectedAttributes.value[key].length > 0
  );

  if (activeAttributeFilters.length > 0) {
      tempProducts = tempProducts.filter(p => {
          // Sản phẩm phải thỏa mãn TẤT CẢ các nhóm thuộc tính đang được lọc (AND)
          return activeAttributeFilters.every(attributeName => {
              const selectedValues = selectedAttributes.value[attributeName];
              // Sản phẩm chỉ cần có MỘT trong các giá trị được chọn của thuộc tính đó (OR)
              return (p.chiTietThuocTinh || []).some(attr => 
                  attr.tenThuocTinh === attributeName && selectedValues.includes(attr.tenThuocTinhBienThe)
              );
          });
      });
  }

  return tempProducts;
});

// Preload ảnh
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

const fetchProducts = async (maDanhMuc, showLoading = false) => {
  if (showLoading) loading.value = true;
  try {
    const res = await axios.get(`/sanpham/${maDanhMuc}/DanhMucSanPham`);
    products.value = res.data;
    await Promise.all(products.value.map((p) => preloadImage(p.maSanPham)));
  } catch (err) {
    console.error("Lỗi tải sản phẩm:", err);
    products.value = [];
  } finally {
    if (showLoading) loading.value = false;
  }
};

onMounted(() => {
  if (route.params.maDanhMuc) {
    fetchProducts(route.params.maDanhMuc, true);
  }
});

watch(
  () => route.params.maDanhMuc,
  (newVal, oldVal) => {
    if (newVal && newVal !== oldVal) {
      // ✅ CẬP NHẬT: Reset tất cả bộ lọc khi chuyển danh mục
      selectedPriceFilter.value = "all";
      selectedBrands.value = []; 
      selectedAttributes.value = {}; // Reset bộ lọc thuộc tính
      fetchProducts(newVal, false);
    }
  }
);
</script>

<style scoped>
/* (Giữ nguyên phần style của bạn) */
.filter-sidebar {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 12px;
  position: sticky;
  top: 20px;
}
.brand-filter-list {
    max-height: 250px;
    overflow-y: auto;
    padding-right: 10px;
}
.form-check-label {
    cursor: pointer;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.25rem; /* 20px */
}

.product-card {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
  padding: 12px;
  text-decoration: none;
  color: inherit;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.product-image {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 1:1 Aspect Ratio */
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
  object-fit: cover;
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
}

.product-desc {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 8px;
}

.product-price {
  font-size: 1.1rem;
  font-weight: bold;
  color: #e53935;
  margin-top: auto; /* Push price to the bottom */
}

.product-category {
  font-size: 0.85rem;
  color: #999;
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
  border-top-left-radius: 12px;
  border-bottom-right-radius: 12px;
  z-index: 10;
}
</style>