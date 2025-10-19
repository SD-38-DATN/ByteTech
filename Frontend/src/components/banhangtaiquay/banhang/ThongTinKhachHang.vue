<template>
  <div class="customer-info">
    <h4>👤 Thông tin khách hàng</h4>
    <div class="form-group">
      <input
        type="tel"
        v-model="phoneNumber"
        @input="searchCustomer"
        placeholder="Nhập số điện thoại tim..."
        :disabled="isLoading"
      />
      <span v-if="isLoading">⏳</span>
    </div>

    <!-- Form luôn hiển thị -->
    <div class="customer-form">
      <div class="form-group">
        <label>Tên khách hàng:</label>
        <input
          type="text"
          v-model="tenKhachHang"
          placeholder="Nhập tên khách hàng..."
          class="input-field"
        />
      </div>

      <div class="form-group">
        <label>Số điện thoại:</label>
        <input
          type="tel"
          v-model="soDienThoai"
          placeholder="Nhập số điện thoại..."
          class="input-field"
          readonly
        />
      </div>

      <div class="form-group">
        <label>Địa chỉ:</label>
        <input
          type="text"
          v-model="diaChi"
          placeholder="Nhập địa chỉ..."
          class="input-field"
        />
      </div>

      <!-- Trạng thái khách hàng -->
      <div v-if="phoneNumber" class="customer-status">
        <span v-if="customerInfo" class="status-old">Khách hàng cũ</span>
        <span v-else-if="!isPhoneValid" class="status-invalid"
          >Số không hợp lệ</span
        >
        <span v-else class="status-new">Khách hàng mới</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { searchCustomer as apiSearchCustomer } from "../../../service/api.js";

const props = defineProps({
  customerInfo: {
    type: Object,
    default: () => ({
      tenKhachHang: "",
      soDienThoai: "",
      diaChi: "",
      customerInfo: null
    })
  }
});

const emit = defineEmits(["customerSelected", "customerUpdated"]);

// State chính
const phoneNumber = ref("");
const customerInfo = ref(null);
const isLoading = ref(false);
const showNotification = ref(false);
const notification = ref("");
const notificationType = ref("");
const isPhoneValid = ref(true);

// Dữ liệu khách hàng mới
const newCustomer = ref({
  tenKhachHang: "",
  soDienThoai: "",
  diaChi: "",
});

// Computed cho v-model
const tenKhachHang = computed({
  get() {
    return customerInfo.value
      ? customerInfo.value.tenKhachHang
      : newCustomer.value.tenKhachHang;
  },
  set(value) {
    if (customerInfo.value) customerInfo.value.tenKhachHang = value;
    else newCustomer.value.tenKhachHang = value;
  },
});

const soDienThoai = computed({
  get() {
    return customerInfo.value
      ? customerInfo.value.soDienThoai
      : newCustomer.value.soDienThoai;
  },
  set(value) {
    if (customerInfo.value) customerInfo.value.soDienThoai = value;
    else newCustomer.value.soDienThoai = value;
  },
});

const diaChi = computed({
  get() {
    return customerInfo.value
      ? customerInfo.value.diaChi
      : newCustomer.value.diaChi;
  },
  set(value) {
    if (customerInfo.value) customerInfo.value.diaChi = value;
    else newCustomer.value.diaChi = value;
  },
});

// Watch props để cập nhật khi chuyển đơn hàng
watch(() => props.customerInfo, (newCustomerInfo) => {
  console.log("👤 Props customerInfo thay đổi:", newCustomerInfo);
  
  if (newCustomerInfo && newCustomerInfo.soDienThoai) {
    phoneNumber.value = newCustomerInfo.soDienThoai;
    customerInfo.value = newCustomerInfo.customerInfo;
    newCustomer.value = {
      tenKhachHang: newCustomerInfo.tenKhachHang || "",
      soDienThoai: newCustomerInfo.soDienThoai || "",
      diaChi: newCustomerInfo.diaChi || ""
    };
    console.log("👤 Đã load thông tin khách hàng từ props:", newCustomer.value);
  } else {
    // Reset nếu không có thông tin khách hàng
    phoneNumber.value = "";
    customerInfo.value = null;
    newCustomer.value = {
      tenKhachHang: "",
      soDienThoai: "",
      diaChi: ""
    };
    console.log("👤 Reset thông tin khách hàng vì props trống");
  }
}, { immediate: true, deep: true });

// Hàm tìm khách hàng theo số điện thoại
async function searchCustomer() {
  const phone = phoneNumber.value.trim();

  if (!phone) {
    // Xóa dữ liệu khi chưa nhập gì
    customerInfo.value = null;
    newCustomer.value = { tenKhachHang: "", soDienThoai: "", diaChi: "" };
    emit("customerSelected", null);
    return;
  }

  // Kiểm tra định dạng số điện thoại
  if (!/^[0-9]{10,11}$/.test(phone)) {
    isPhoneValid.value = false;
    customerInfo.value = null;
    newCustomer.value = { tenKhachHang: "", soDienThoai: "", diaChi: "" };
    emit("customerSelected", null);
    return;
  }

  isPhoneValid.value = true;
  isLoading.value = true;
  clearNotification();

  try {
    const data = await apiSearchCustomer(phone);

    if (data && data.length > 0) {
      // Có khách hàng cũ
      customerInfo.value = data[0];
      newCustomer.value = { ...customerInfo.value };
      showNotificationMessage("Tìm thấy khách hàng cũ", "success");
      emit("customerSelected", customerInfo.value);
    } else {
      // Khách hàng mới
      customerInfo.value = null;
      newCustomer.value.soDienThoai = phone;
      showNotificationMessage(
        "Khách hàng mới - Vui lòng nhập thông tin",
        "info"
      );
      emit("customerSelected", null);
    }
  } catch (err) {
    console.error("Lỗi tìm kiếm khách hàng:", err);
    showNotificationMessage("Lỗi tìm kiếm khách hàng", "error");
  } finally {
    isLoading.value = false;
  }
}

// Thông báo
function showNotificationMessage(message, type) {
  notification.value = message;
  notificationType.value = type;
  showNotification.value = true;
  setTimeout(clearNotification, 3000);
}

function clearNotification() {
  showNotification.value = false;
  notification.value = "";
  notificationType.value = "";
}

// Emit thông tin khách hàng khi có thay đổi
function emitCustomerUpdate() {
  const customerData = {
    tenKhachHang: tenKhachHang.value,
    soDienThoai: soDienThoai.value,
    diaChi: diaChi.value,
    customerInfo: customerInfo.value
  };
  
  console.log("👤 Emit customer update:", customerData);
  emit("customerUpdated", customerData);
}

// Watch các field để emit khi có thay đổi
watch([tenKhachHang, soDienThoai, diaChi], () => {
  emitCustomerUpdate();
}, { deep: true });
</script>

<style scoped>
.customer-info {
  /* Layout */
  padding: 12px;
  margin-bottom: 6px;
  
  /* Giao diện */
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.customer-info h4 {
  /* Layout */
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 8px;
  
  /* Typography */
  color: #374151;
  font-size: 13px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 6px;
}

.form-group label {
  /* Layout */
  display: block;
  margin-bottom: 3px;
  
  /* Typography */
  color: #374151;
  font-size: 11px;
  font-weight: 500;
}

.form-group input {
  /* Layout */
  width: 100%;
  padding: 6px 10px;
  box-sizing: border-box;
  
  /* Giao diện */
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  font-size: 11px;
  
  /* Tương tác */
  transition: all 0.2s ease;
}

.form-group input:focus {
  /* Focus state */
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* Thông báo */
.alert {
  /* Layout */
  padding: 8px 12px;
  margin: 8px 0;
  border-radius: 6px;
  
  /* Typography */
  font-size: 12px;
  font-weight: 500;
}

.alert.success {
  /* Giao diện */
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.alert.error {
  /* Giao diện */
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.alert.info {
  /* Giao diện */
  background: #dbeafe;
  color: #1e40af;
  border: 1px solid #bfdbfe;
}

/* Form khách hàng */
.customer-form {
  /* Layout */
  padding: 8px;
  margin-top: 6px;
  
  /* Giao diện */
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
}

.customer-status {
  /* Layout */
  margin-top: 6px;
  padding: 4px 8px;
  text-align: center;
  border-radius: 3px;
  
  /* Typography */
  font-size: 10px;
  font-weight: 500;
}

.status-old {
  /* Giao diện */
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.status-new {
  /* Giao diện */
  background: #dbeafe;
  color: #1e40af;
  border: 1px solid #bfdbfe;
}

.status-invalid {
  /* Giao diện */
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}
</style>
