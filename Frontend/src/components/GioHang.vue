<!-- <template>
  <div class="cart-container">
    <h2 class="cart-title">Giỏ hàng của bạn</h2>

    <div v-if="cart && cart.chiTietList.length">
      <div class="cart-info">
        <p><strong>Người dùng:</strong> {{ cart.userId }}</p>
        <p><strong>Ngày tạo:</strong> {{ cart.ngayTao }}</p>
        <p class="cart-total"><strong>Tổng tiền:</strong> {{ totalPrice | currency }}</p>
      </div>

      <div v-for="item in cart.chiTietList" :key="item.id" class="cart-item">
        <input type="checkbox" v-model="item.selected" class="select-item" />

        <div class="item-image">
          <img src="https://via.placeholder.com/100" alt="product" />
        </div>

        <div class="item-details">
          <h3 class="item-title">{{ item.tenSanPham }}</h3>
          <p class="item-sku">SKU: {{ item.maSKU }}</p>

          <ul class="item-attributes">
            <li v-for="(value, key) in item.thuocTinh" :key="key">
              <span class="attr-key">{{ key }}:</span> {{ value }}
            </li>
          </ul>

          <div class="quantity-control">
            <button @click="decreaseQty(item)">-</button>
            <span>{{ item.soLuong }}</span>
            <button @click="increaseQty(item)">+</button>
          </div>

          <div class="item-prices">
            <p class="unit-price">Đơn giá: {{ item.gia | currency }}</p>
            <p class="total-price">Thành tiền: {{ (item.gia * item.soLuong) | currency }}</p>
          </div>

          <!-- Nút xóa riêng cho từng sản phẩm -->
          <!-- <button class="btn-delete" @click="removeItem(item)">Xóa</button>
        </div>
      </div>

      <div class="order-action">
        <button class="btn-create" @click="createOrder">
          Tạo đơn hàng từ sản phẩm đã chọn
        </button>
      </div>
    </div>

    <div v-else class="empty-cart">
      <p>Giỏ hàng trống hoặc không thể tải dữ liệu.</p>
    </div>
  </div>
</template>

<script>
import { fetchCart, updateCartQuantity, deleteCartItem } from "../service/api.js";

export default {
  name: "Cart",
  data() {
    return {
      cart: null
    };
  },
  

  
  mounted() {
    this.loadCart();
  },
  computed: {
    totalPrice() {
      if (!this.cart) return 0;
      return this.cart.chiTietList
        .filter(item => item.selected)
        .reduce((sum, item) => sum + item.gia * item.soLuong, 0);
    },
    selectedItems() {
      if (!this.cart) return [];
      return this.cart.chiTietList.filter(item => item.selected);
    }
  },
  methods: {
    async loadCart() {
      try {
        this.cart = await fetchCart();
        this.cart.chiTietList.forEach(item => item.selected = false);
      } catch (error) {
        console.error("Lấy giỏ hàng thất bại", error);
      }
    },

    async increaseQty(item) {
  if (item.soLuongTon && item.soLuong < item.soLuongTon) {
    item.soLuong++;
    await this.updateQtyBackend(item);
  } else {
    alert(`Bạn chỉ có thể mua tối đa ${item.soLuongTon} sản phẩm này.`);
  }
},


    async decreaseQty(item) {
      if (item.soLuong > 1) {
        item.soLuong--;
        await this.updateQtyBackend(item);
      }
    },

    async updateQtyBackend(item) {
      try {
        await updateCartQuantity([{ id: item.id, soLuong: item.soLuong }]);
      } catch (err) {
        console.error("Lỗi update số lượng:", err);
      }
    },

    async removeItem(item) {
      if (confirm(`Bạn có chắc muốn xóa ${item.tenSanPham} khỏi giỏ hàng?`)) {
        try {
          await deleteCartItem(item.id); // Gọi API backend xóa chi tiết
          this.cart.chiTietList = this.cart.chiTietList.filter(i => i.id !== item.id);
        } catch (err) {
          console.error("Lỗi xóa chi tiết giỏ hàng:", err);
        }
      }
    },

    createOrder() {
      if (this.selectedItems.length === 0) {
        alert("Vui lòng chọn ít nhất một sản phẩm để tạo đơn hàng.");
        return;
      }
      console.log("Sản phẩm được chọn để tạo đơn:", this.selectedItems);

    // TODO: gọi API tạo đơn hàng
    this.$router.push({
        name: "XacNhanDonHang",
        query: { items: JSON.stringify(this.selectedItems) }
      });
    }
  },
  filters: {
    currency(value) {
      if (!value) return '';
      return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    }
  }
};
</script>

<style scoped>
.cart-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.cart-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.cart-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.cart-total {
  font-size: 1.2rem;
  color: #e53935;
  font-weight: bold;
}

.cart-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  border: 1px solid #ddd;
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 10px;
  background-color: #fff;
  transition: box-shadow 0.3s ease;
}
.cart-item:hover {
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.select-item {
  margin-top: 10px;
}

.item-image img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
}

.item-details {
  flex: 1;
}

.item-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
}

.item-sku {
  font-size: 0.85rem;
  color: #777;
  margin-bottom: 8px;
}

.item-attributes {
  list-style: none;
  padding: 0;
  margin-bottom: 10px;
}

.item-attributes li {
  font-size: 0.9rem;
  margin-bottom: 3px;
}

.attr-key {
  font-weight: 600;
}

.quantity-control {
  display: flex;
  align-items: center;
  margin: 10px 0;
}

.quantity-control button {
  width: 35px;
  height: 35px;
  border: 1px solid #ccc;
  background-color: #f8f8f8;
  cursor: pointer;
  border-radius: 5px;
  font-weight: bold;
  font-size: 1rem;
  transition: background 0.2s;
}

.quantity-control button:hover {
  background-color: #e0e0e0;
}

.quantity-control span {
  margin: 0 10px;
  min-width: 25px;
  text-align: center;
  font-weight: bold;
}

.item-prices {
  display: flex;
  gap: 20px;
  font-size: 0.95rem;
}

.unit-price {
  color: #555;
}

.total-price {
  color: #e53935;
  font-weight: bold;
}

.btn-delete {
  background-color: #f44336;
  color: #fff;
  border: none;
  padding: 5px 15px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
  transition: background 0.3s;
}
.btn-delete:hover {
  background-color: #d32f2f;
}

.order-action {
  text-align: right;
  margin-top: 20px;
}

.btn-create {
  background-color: #e53935;
  color: #fff;
  border: none;
  padding: 10px 25px;
  font-size: 1rem;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}
.btn-create:hover {
  background-color: #d32f2f;
}

.empty-cart {
  text-align: center;
  font-size: 1.1rem;
  color: #555;
  margin-top: 50px;
}
</style> --> -->










<template>
  <div class="cart-page">
    <div class="cart-container">
      <h1 class="cart-main-title">Giỏ Hàng Của Bạn</h1>

      <div v-if="cart && cart.chiTietList.length" class="cart-content">
        <div class="cart-header">
          <span class="header-product">Sản phẩm</span>
          <span class="header-price">Đơn giá</span>
          <span class="header-quantity">Số lượng</span>
          <span class="header-total">Thành tiền</span>
          <span class="header-action"></span>
        </div>

        <div class="cart-items-list">
          <div v-for="item in cart.chiTietList" :key="item.id" class="cart-item">
            <div class="item-product">
              <input type="checkbox" v-model="item.selected" class="item-select-box" />
              <img src="https://via.placeholder.com/100" alt="product" class="item-image" />
              <div class="item-details">
                <h3 class="item-title">{{ item.tenSanPham }}</h3>
                <p class="item-sku">SKU: {{ item.maSKU }}</p>
                <div class="item-attributes">
                  <span v-for="(value, key) in item.thuocTinh" :key="key" class="attribute-tag">
                    {{ key }}: {{ value }}
                  </span>
                </div>
              </div>
            </div>

            <div class="item-price">
              <span>{{ formatCurrency(item.gia) }}</span>
            </div>

            <div class="item-quantity">
              <div class="quantity-control">
                <button @click="decreaseQty(item)" :disabled="item.soLuong <= 1">-</button>
                <span>{{ item.soLuong }}</span>
                <button @click="increaseQty(item)">+</button>
              </div>
            </div>

            <div class="item-total">
              <span>{{ formatCurrency(item.gia * item.soLuong) }}</span>
            </div>
            
            <div class="item-action">
              <button @click="removeItem(item)" class="btn-delete" title="Xóa sản phẩm">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" 
                     viewBox="0 0 24 24" fill="none" stroke="currentColor" 
                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6
                           m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <div class="cart-summary">
          <div class="summary-total">
            Tổng thanh toán ({{ selectedItems.length }} sản phẩm):
            <span class="total-price-value">{{ formatCurrency(totalPrice) }}</span>
          </div>
          <button class="btn-create" @click="createOrder" :disabled="selectedItems.length === 0">
            Tạo Đơn Hàng
          </button>
        </div>
      </div>
      
      <div v-else class="empty-cart">
        <img src="https://deo.shopeemobile.com/shopee/shopee-pcmall-live-sg/assets/9bdd8040b334d31946f49e36beaf32db.png" 
             alt="Empty cart" class="empty-cart-img" />
        <p>Giỏ hàng của bạn chưa có sản phẩm nào</p>
      </div>
    </div>
  </div>
</template>

<script>
import { fetchCart, updateCartQuantity, deleteCartItem } from "../service/api.js";

export default {
  name: "Cart",
  data() {
    return {
      cart: null
    };
  },
  
  mounted() {
    this.loadCart();
  },
  computed: {
    totalPrice() {
      if (!this.cart) return 0;
      return this.cart.chiTietList
        .filter(item => item.selected)
        .reduce((sum, item) => sum + item.gia * item.soLuong, 0);
    },
    selectedItems() {
      if (!this.cart) return [];
      return this.cart.chiTietList.filter(item => item.selected);
    }
  },
  methods: {
    async loadCart() {
      try {
        this.cart = await fetchCart();
        this.cart.chiTietList.forEach(item => this.$set(item, 'selected', false));
      } catch (error) {
        console.error("Lấy giỏ hàng thất bại", error);
      }
    },

    async increaseQty(item) {
      if (item.soLuongTon && item.soLuong < item.soLuongTon) {
        item.soLuong++;
        await this.updateQtyBackend(item);
      } else {
        alert(`Bạn chỉ có thể mua tối đa ${item.soLuongTon} sản phẩm này.`);
      }
    },

    async decreaseQty(item) {
      if (item.soLuong > 1) {
        item.soLuong--;
        await this.updateQtyBackend(item);
      }
    },

    async updateQtyBackend(item) {
      try {
        await updateCartQuantity([{ id: item.id, soLuong: item.soLuong }]);
      } catch (err) {
        console.error("Lỗi update số lượng:", err);
      }
    },

    async removeItem(item) {
      if (confirm(`Bạn có chắc muốn xóa ${item.tenSanPham} khỏi giỏ hàng?`)) {
        try {
          await deleteCartItem(item.id);
          this.cart.chiTietList = this.cart.chiTietList.filter(i => i.id !== item.id);
        } catch (err) {
          console.error("Lỗi xóa chi tiết giỏ hàng:", err);
        }
      }
    },

    createOrder() {
      if (this.selectedItems.length === 0) {
        alert("Vui lòng chọn ít nhất một sản phẩm để tạo đơn hàng.");
        return;
      }
      console.log("Sản phẩm được chọn để tạo đơn:", this.selectedItems);

      this.$router.push({
        name: "XacNhanDonHang",
        query: { items: JSON.stringify(this.selectedItems) }
      });
    },

    // ✅ Hàm định dạng tiền tệ VNĐ
    formatCurrency(value) {
      if (!value) return "0 VNĐ";
      return new Intl.NumberFormat("vi-VN").format(value) + " VNĐ";
    }
  }
};
</script>

<style scoped>
:root {
  --primary-color: #d70018;
  --text-color: #333;
  --subtext-color: #666;
  --border-color: #e5e5e5;
  --background-color: #f4f4f4;
  --white-color: #fff;
  --disabled-color: #c7c7c7;
}

.cart-page {
  background-color: var(--background-color);
  padding: 32px 16px;
  min-height: 100vh;
}

.cart-container {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-main-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 24px;
}

.cart-content {
  background-color: var(--white-color);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
  padding: 24px;
}

.cart-header {
  display: grid;
  grid-template-columns: 2.5fr 1fr 1.5fr 1fr 0.5fr;
  padding-bottom: 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
  font-weight: 600;
  color: var(--subtext-color);
}

.header-price, .header-quantity, .header-total {
  text-align: center;
}

.cart-items-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.cart-item {
  display: grid;
  grid-template-columns: 2.5fr 1fr 1.5fr 1fr 0.5fr;
  align-items: center;
}

.item-product {
  display: flex;
  align-items: center;
  gap: 16px;
}

.item-select-box {
  width: 18px;
  height: 18px;
}

.item-image {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.item-title {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.item-sku {
  font-size: 0.85rem;
  color: var(--subtext-color);
  margin-bottom: 8px;
}

.attribute-tag {
  background-color: #f0f0f0;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  display: inline-block;
  margin-right: 6px;
}

.item-price, .item-total, .item-action {
  text-align: center;
}

.item-total span {
  font-weight: 700;
  color: var(--primary-color);
}

.quantity-control {
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  width: fit-content;
  margin: auto;
}

.quantity-control button {
  width: 32px;
  height: 32px;
  border: none;
  background-color: #fafafa;
  cursor: pointer;
  font-size: 1.2rem;
}

.quantity-control button:hover:not(:disabled) {
  background-color: #f0f0f0;
}
.quantity-control button:disabled {
  cursor: not-allowed;
  color: var(--disabled-color);
}

.quantity-control span {
  width: 40px;
  text-align: center;
  font-weight: 600;
  border-left: 1px solid var(--border-color);
  border-right: 1px solid var(--border-color);
}

.btn-delete {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: var(--subtext-color);
  transition: color 0.2s ease;
}

.btn-delete:hover {
  color: var(--primary-color);
}

.cart-summary {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
  gap: 24px;
}

.summary-total {
  font-size: 1.1rem;
  font-weight: 500;
}

.total-price-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-color);
  margin-left: 12px;
}

.btn-create {
  background-color: var(--primary-color);
  color: var(--white-color);
  border: none;
  padding: 12px 32px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-create:hover:not(:disabled) {
  background-color: #a70012;
}

.btn-create:disabled {
  background-color: var(--disabled-color);
  cursor: not-allowed;
}

.empty-cart {
  text-align: center;
  padding: 60px 20px;
  background-color: var(--white-color);
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.empty-cart-img {
  width: 120px;
  margin-bottom: 24px;
}

.empty-cart p {
  font-size: 1.2rem;
  color: var(--subtext-color);
}
</style>
