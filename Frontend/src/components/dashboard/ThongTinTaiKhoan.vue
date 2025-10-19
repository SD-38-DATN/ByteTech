<script setup>
import { ref, onMounted } from "vue";
import { getCurrentUser } from "../../service/api.js"; // sửa đường dẫn

const user = ref(null);

onMounted(async () => {
  try {
    const data = await getCurrentUser();
    user.value = data;
  } catch (err) {
    console.error("Không lấy được user:", err);
  }
});
</script>

<template>
  <div class="container my-5">
    <div v-if="user" class="card shadow-lg border-0 rounded-4 overflow-hidden">
      <!-- Header -->
      <div
        class="position-relative"
        style="height: 180px; background: linear-gradient(135deg, #6366f1, #8b5cf6, #ec4899);"
      >
        <div class="position-absolute bottom-0 start-0 ms-4 mb-n5">
          <img
            src="https://i.pravatar.cc/120"
            alt="Avatar"
            class="rounded-circle border border-4 border-white shadow"
            style="width: 120px; height: 120px; object-fit: cover;"
          />
        </div>
      </div>

      <!-- Body -->
      <div class="card-body mt-5 px-4">
        <!-- Username + Role -->
        <div class="d-flex align-items-center justify-content-between">
          <div>
            <h3 class="fw-bold mb-0">
              {{ user.user?.tenHienThi || "" }}
            </h3>

            <span
              class="badge bg-gradient text-white px-3 py-2 shadow-sm"
              style="background: linear-gradient(90deg, #6366f1, #8b5cf6);"
            >
              {{ user.role }}
            </span>
          </div>
        </div>

        <!-- Token Info -->
        <div class="row g-3 mt-4">
          <div class="col-md-6">
            <div class="p-3 bg-light rounded-3 shadow-sm">
              <p class="mb-1 text-secondary fw-semibold">Ngày cấp</p>
              <p class="mb-0 text-dark">
                {{ user.issuedAt ? new Date(user.issuedAt).toLocaleString() : "—" }}
              </p>
            </div>
          </div>
          <div class="col-md-6">
            <div class="p-3 bg-light rounded-3 shadow-sm">
              <p class="mb-1 text-secondary fw-semibold">Ngày hết hạn</p>
              <p class="mb-0 text-dark">
                {{ user.expiration ? new Date(user.expiration).toLocaleString() : "—" }}
              </p>
            </div>
          </div>
        </div>

        <!-- User Details -->
        <h5 class="fw-bold border-bottom pb-2 mt-4 mb-3">Chi tiết tài khoản</h5>
        <div class="row g-3">
          <div class="col-md-6">
            <div class="p-3 border rounded-3">
              <i class="bi bi-person-fill text-primary me-2"></i>
              <strong>Họ tên:</strong> {{ user.user?.hoTen || "—" }}
            </div>
          </div>
          <div class="col-md-6">
            <div class="p-3 border rounded-3">
              <i class="bi bi-envelope-fill text-danger me-2"></i>
              <strong>Email:</strong> {{ user.user?.email || "—" }}
            </div>
          </div>
          <div class="col-md-6">
            <div class="p-3 border rounded-3">
              <i class="bi bi-telephone-fill text-success me-2"></i>
              <strong>SĐT:</strong> {{ user.user?.soDienThoai || "—" }}
            </div>
          </div>
          <div class="col-md-6">
            <div class="p-3 border rounded-3">
              <i class="bi bi-geo-alt-fill text-warning me-2"></i>
              <strong>Địa chỉ:</strong> {{ user.user?.diaChiGiaoHang || "—" }}
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="d-flex justify-content-end mt-4">
          <button class="btn btn-primary px-4 rounded-pill shadow-sm">
            <i class="bi bi-pencil-square me-2"></i> Chỉnh sửa
          </button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-else class="text-center text-muted py-5">
      <div class="spinner-border text-primary mb-3"></div>
      <p class="fw-semibold">Đang tải thông tin người dùng...</p>
    </div>
  </div>
</template>
