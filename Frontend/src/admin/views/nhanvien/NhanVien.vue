<template>
  <div class="container mt-4">
    <h1 class="text-center mb-4">Trang Nhân Viên</h1>

    <!-- Thanh tìm kiếm -->
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div class="input-group w-50">
        <input
          type="text"
          class="form-control"
          placeholder="Nhập mã, tên, SĐT hoặc email..."
          v-model="keyword"
        />
        <button class="btn btn-dark" @click="searchNhanVien">Tìm</button>
      </div>
      <button
        class="btn btn-primary"
        data-bs-toggle="modal"
        data-bs-target="#addNhanVienModal"
      >
        Thêm nhân viên
      </button>
    </div>

    <!-- Bảng danh sách -->
    <table class="table table-bordered table-striped align-middle text-center">
      <thead class="table-dark">
        <tr>
          <th>STT</th>
          <th>Tên</th>
          <th>SĐT</th>
          <th>Email</th>
          <th>Chức vụ</th>
          <th>Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(nv, index) in nhanViens" :key="nv.id">
          <td>{{ index + 1 }}</td>
          <td>{{ nv.hoTen }}</td>
          <td>{{ nv.soDienThoai }}</td>
          <td>{{ nv.email }}</td>
          <td>{{ nv.roleName }}</td>
          <td>
            <button class="btn btn-info btn-sm me-1" @click="viewNhanVien(nv)">
              Chi tiết
            </button>
            <button
              class="btn btn-warning btn-sm me-1"
              @click="editNhanVien(nv)"
            >
              Sửa
            </button>
            <button
              class="btn btn-danger btn-sm"
              @click="deleteNhanVien(nv.id)"
            >
              Xóa
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal thêm nhân viên -->
    <div
      class="modal fade"
      id="addNhanVienModal"
      tabindex="-1"
      aria-labelledby="addNhanVienModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title">Thêm nhân viên mới</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
            ></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label>Tên hiển thị</label>
                <input
                  v-model.trim="newNV.tenHienThi"
                  class="form-control"
                  maxlength="50"
                  @input="newNV.tenHienThi = newNV.tenHienThi.slice(0, 50)"
                />
              </div>
              <div class="col-md-6">
                <label>Tên đăng nhập</label>
                <input
                  v-model.trim="newNV.username"
                  class="form-control"
                  maxlength="50"
                  @input="newNV.username = newNV.username.slice(0, 50)"
                />
              </div>
              <div class="col-md-6">
                <label>Mật khẩu</label>
                <input
                  v-model.trim="newNV.passwords"
                  class="form-control"
                  type="password"
                  maxlength="6"
                  @input="newNV.passwords = newNV.passwords.slice(0, 6)"
                />
              </div>
              <div class="col-md-6">
                <label>Họ tên</label>
                <input
                  v-model.trim="newNV.hoTen"
                  class="form-control"
                  type="text"
                  maxlength="50"
                  @input="newNV.hoTen = newNV.hoTen.slice(0, 50)"
                />
              </div>
              <div class="col-md-6">
                <label>Giới tính</label>
                <select v-model="newNV.gioiTinh" class="form-select">
                  <option :value="1">Nam</option>
                  <option :value="0">Nữ</option>
                </select>
              </div>
              <div class="col-md-6">
                <label>Email</label>
                <input
                  v-model.trim="newNV.email"
                  class="form-control"
                  type="text"
                  maxlength="50"
                  @input="
                  newNV.email = newNV.email.slice(0, 50)"
                />
              </div>
              <div class="col-md-6">
                <label>Số điện thoại</label>
                <input
                  v-model.trim="newNV.soDienThoai"
                  class="form-control"
                  maxlength="10"
                   @paste="handlePasteSoDienThoai($event, 'edit')"
                  @input="newNV.soDienThoai = newNV.soDienThoai
                      .replace(/[^0-9]/g, '')
                      .slice(0, 10)
                  "
                />
              </div>
              <div class="col-md-6">
                <label>Địa chỉ</label>
                <input
                  v-model.trim="newNV.diaChiGiaoHang"
                  class="form-control"
                  type="text"
                  maxlength="50"
                  @input="
                    newNV.diaChiGiaoHang = newNV.diaChiGiaoHang.slice(0, 50)
                  "
                />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" data-bs-dismiss="modal">
              Hủy
            </button>
            <button class="btn btn-success" @click="addNhanVien">Thêm</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal sửa nhân viên -->
    <div
      class="modal fade"
      id="editNhanVienModal"
      tabindex="-1"
      aria-labelledby="editNhanVienModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-warning text-white">
            <h5 class="modal-title">Cập nhật nhân viên</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
            ></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6">
                <label>Tên hiển thị</label>
                <input
                  v-model.trim="editNV.tenHienThi"
                  class="form-control"
                  maxlength="50"
                  @input="editNV.tenHienThi = editNV.tenHienThi.slice(0, 50)"/>
              </div>
              <div class="col-md-6">
                <label>Tên đăng nhập</label>
                <input
                  v-model.trim="editNV.username"
                  class="form-control"
                  maxlength="50"
                  @input="editNV.username = editNV.username.slice(0, 50)"/>
              </div>
              <div class="col-md-6">
                <label>Mật khẩu</label>
                <input
                  v-model.trim="editNV.passwords"
                  class="form-control"
                  type="password"
                  maxlength="6"
                  @input="editNV.passwords = editNV.passwords.slice(0, 6)"/>
              </div>
              <div class="col-md-6">
                <label>Họ tên</label>
                <input
                  v-model.trim="editNV.hoTen"
                  class="form-control"
                  maxlength="50"
                  @input="editNV.hoTen = editNV.hoTen.slice(0, 50)"/>
              </div>
              <div class="col-md-6">
                <label>Giới tính</label>
                <select v-model="editNV.gioiTinh" class="form-select">
                  <option :value="1">Nam</option>
                  <option :value="0">Nữ</option>
                </select>
              </div>
              <div class="col-md-6">
                <label>Email</label>
                <input
                  v-model.trim="editNV.email"
                  class="form-control"
                  maxlength="50"
                  @input="editNV.email = editNV.email.slice(0, 50)"/>
              </div>
              <div class="col-md-6">
                <label>Số điện thoại</label>
                <input
                  v-model.trim="editNV.soDienThoai"
                  class="form-control"
                  maxlength="10"
                   @paste="handlePasteSoDienThoai($event, 'edit')"
                   @input="
                    editNV.soDienThoai = editNV.soDienThoai
                      .replace(/[^0-9]/g, '')
                      .slice(0, 10)"/> 
              </div>
              <div class="col-md-6">
                <label>Địa chỉ</label>
                <input
                  v-model.trim="editNV.diaChiGiaoHang"
                  class="form-control"
                  maxlength="50"
                  @input="
                    editNV.diaChiGiaoHang = editNV.diaChiGiaoHang.slice(0, 50)"/>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" data-bs-dismiss="modal">
              Hủy
            </button>
            <button class="btn btn-success" @click="updateNhanVien">
              Cập nhật
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chi tiết nhân viên -->
    <div
      class="modal fade"
      id="viewNhanVienModal"
      tabindex="-1"
      aria-labelledby="viewNhanVienModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title">Chi tiết nhân viên</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
            ></button>
          </div>

          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-6" v-for="(value, key) in viewNV" :key="key">
                <label class="form-label fw-semibold">{{
                  formatLabel(key)
                }}</label>
                <input
                  :value="formatValue(key, value)"
                  class="form-control"
                  disabled
                />
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn btn-secondary" data-bs-dismiss="modal">
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import * as bootstrap from "bootstrap";

export default {
  name: "NhanVienView",
  data() {
    return {
      nhanViens: [],
      keyword: "",
      newNV: {
        tenHienThi: "",
        username: "",
        passwords: "",
        hoTen: "",
        gioiTinh: 1,
        email: "",
        soDienThoai: "",
        diaChiGiaoHang: "",
        roleId: 3,
        roleName: "Nhân viên",
      },
      editNV: {},
      viewNV: {},
    };
  },
  mounted() {
    this.getAllNhanVien();
  },
  methods: {
    async getAllNhanVien() {
      const res = await axios.get("http://localhost:8081/api/nhanvien/tatca");
      this.nhanViens = res.data;
    },
    async addNhanVien() {
      if (!this.newNV.tenHienThi?.trim())
        return alert("Tên hiển thị không được để trống!");
      if (!this.newNV.username?.trim())
        return alert("Tên đăng nhập không được để trống!");
      if (!this.newNV.passwords?.trim())
        return alert("Mật khẩu không được để trống!");
      if (!this.newNV.hoTen?.trim())
        return alert("Họ tên không được để trống!");
      if (!this.newNV.email?.trim()) return alert("Email không được để trống!");
      if (!this.newNV.soDienThoai?.trim())
        return alert("Số điện thoại không được để trống!");
      if (!this.newNV.diaChiGiaoHang?.trim())
        return alert("Địa chỉ không được để trống!");

      // --- Kiểm tra độ dài ---
      if ((this.newNV.tenHienThi.trim().length || 0) > 50)
        return alert("Tên hiển thị tối đa 50 ký tự!");
      if ((this.newNV.username.trim().length || 0) > 50)
        return alert("Tên đăng nhập tối đa 50 ký tự!");
      if ((this.newNV.passwords.trim().length || 0) < 6)
        return alert("Mật khẩu phải có ít nhất 6 ký tự!");
      if ((this.newNV.hoTen.trim().length || 0) > 50)
        return alert("Họ tên tối đa 50 ký tự!");
      if ((this.newNV.email.trim().length || 0) > 50)
        return alert("Email tối đa 50 ký tự!");
      if ((this.newNV.diaChiGiaoHang.trim().length || 0) > 50)
        return alert("Địa chỉ tối đa 50 ký tự!");

      //  Kiểm tra trùng username
      const isUsernameExists = this.nhanViens.some(
        (nv) => nv.username === this.newNV.username
      );
      if (isUsernameExists) {
        alert("Tên đăng nhập đã tồn tại!");
        return;
      }

      if (!this.newNV.passwords?.trim()) {
        alert("Mật khẩu không được để trống!");
        return;
      }
      if (!this.newNV.hoTen?.trim()) {
        alert("Họ tên không được để trống!");
        return;
      }
      if (!this.newNV.email?.trim()) {
        alert("Email không được để trống!");
        return;
      }

      // Kiểm tra trùng email
      const isEmailExists = this.nhanViens.some(
        (nv) => nv.email === this.newNV.email
      );
      if (isEmailExists) {
        alert("Email đã tồn tại!");
        return;
      }

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.newNV.email)) {
        alert("Email không hợp lệ!");
        return;
      }

      if (!this.newNV.soDienThoai?.trim()) {
        alert("Số điện thoại không được để trống!");
        return;
      }

      const phoneRegex = /^(0\d{9})$/;
      if (!phoneRegex.test(this.newNV.soDienThoai.trim())) {
        alert("Số điện thoại không hợp lệ! (phải có 10 số và bắt đầu bằng 0)");
        return;
      }

      // Kiểm tra trùng số điện thoại
      const isPhoneExists = this.nhanViens.some(
        (nv) => nv.soDienThoai === this.newNV.soDienThoai
      );
      if (isPhoneExists) {
        alert("Số điện thoại đã tồn tại!");
        return;
      }

      if (!this.newNV.diaChiGiaoHang?.trim()) {
        alert("Địa chỉ không được để trống!");
        return;
      }

      try {
        const payload = {
          tenHienThi: this.newNV.tenHienThi,
          username: this.newNV.username,
          passwords: this.newNV.passwords,
          hoTen: this.newNV.hoTen,
          gioiTinh: this.newNV.gioiTinh,
          email: this.newNV.email,
          soDienThoai: this.newNV.soDienThoai,
          diaChiGiaoHang: this.newNV.diaChiGiaoHang,
          roleId: 3,
          roleName: "Nhân viên",
        };

        const res = await axios.post(
          "http://localhost:8081/api/nhanvien/them",
          payload
        );

        alert("Thêm nhân viên thành công!");
        this.getAllNhanVien();

        // Reset form
        this.newNV = {
          tenHienThi: "",
          username: "",
          passwords: "",
          hoTen: "",
          gioiTinh: 1,
          email: "",
          soDienThoai: "",
          diaChiGiaoHang: "",
          roleId: 3,
          roleName: "Nhân viên",
        };

        // Đóng modal
        document.querySelector("#addNhanVienModal .btn-close").click();
      } catch (err) {
        console.error(" Lỗi khi thêm nhân viên:", err.response || err);
        if (err.response?.status === 400) {
          alert(
            "Dữ liệu không hợp lệ hoặc bị trùng (username/email/số điện thoại)!"
          );
        } else {
          alert(
            "Lỗi khi thêm: " + (err.response?.data?.message || err.message)
          );
        }
      }
    },

    async searchNhanVien() {
      const res = await axios.get(
        `http://localhost:8081/api/nhanvien/timkiem?keyword=${this.keyword}`
      );
      this.nhanViens = res.data;
    },
    async deleteNhanVien(id) {
      if (confirm("Bạn có chắc muốn xóa?")) {
        await axios.delete(`http://localhost:8081/api/nhanvien/xoa/${id}`);
        alert("Xóa thành công!");
        this.getAllNhanVien();
      }
    },
    editNhanVien(nv) {
      this.editNV = { ...nv };
      const modal = new bootstrap.Modal(
        document.getElementById("editNhanVienModal")
      );
      modal.show();
    },
    async updateNhanVien() {
      // --- Kiểm tra trống ---
      if (!this.editNV.tenHienThi.trim())
        return alert("Tên hiển thị không được để trống!");
      if (!this.editNV.username.trim())
        return alert("Tên đăng nhập không được để trống!");
      if (!this.editNV.passwords.trim())
        return alert("Mật khẩu không được để trống!");
      if (!this.editNV.hoTen.trim())
        return alert("Họ tên không được để trống!");
      if (!this.editNV.email.trim()) return alert("Email không được để trống!");
      if (!this.editNV.soDienThoai.trim())
        return alert("Số điện thoại không được để trống!");
      if (!this.editNV.diaChiGiaoHang.trim())
        return alert("Địa chỉ không được để trống!");

      // --- Kiểm tra độ dài ---
      if ((this.editNV.tenHienThi.trim().length || 0) > 50)
        return alert("Tên hiển thị tối đa 50 ký tự!");
      if ((this.editNV.username.trim().length || 0) > 50)
        return alert("Tên đăng nhập tối đa 50 ký tự!");
      if ((this.editNV.passwords.trim().length || 0) < 6)
        return alert("Mật khẩu phải có ít nhất 6 ký tự!");
      if ((this.editNV.hoTen.trim().length || 0) > 50)
        return alert("Họ tên tối đa 50 ký tự!");
      if ((this.editNV.email.trim().length || 0) > 50)
        return alert("Email tối đa 50 ký tự!");
      if ((this.editNV.diaChiGiaoHang.trim().length || 0) > 50)
        return alert("Địa chỉ tối đa 50 ký tự!");

      //  Kiểm tra dữ liệu trống hoặc toàn khoảng trắng
      if (!this.editNV.tenHienThi?.trim()) {
        alert("Tên hiển thị không được để trống!");
        return;
      }
      if (!this.editNV.username?.trim()) {
        alert("Tên đăng nhập không được để trống!");
        return;
      }
      if (!this.editNV.passwords?.trim()) {
        alert("Mật khẩu không được để trống!");
        return;
      }
      if (!this.editNV.hoTen?.trim()) {
        alert("Họ tên không được để trống!");
        return;
      }
      if (!this.editNV.email?.trim()) {
        alert("Email không được để trống!");
        return;
      }

      //Kiểm tra định dạng email
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.editNV.email.trim())) {
        alert("Email không hợp lệ!");
        return;
      }

      if (!this.editNV.soDienThoai?.trim()) {
        alert("Số điện thoại không được để trống!");
        return;
      }

      //  Kiểm tra định dạng số điện thoại
      const phoneRegex = /^(0\d{9})$/;
      if (!phoneRegex.test(this.editNV.soDienThoai.trim())) {
        alert("Số điện thoại không hợp lệ! (phải có 10 số và bắt đầu bằng 0)");
        return;
      }

      if (!this.editNV.diaChiGiaoHang?.trim()) {
        alert("Địa chỉ không được để trống!");
        return;
      }// Kiểm tra trùng email 
const isEmailExists = this.nhanViens.some(
  (nv) => nv.email === this.editNV.email && nv.id !== this.editNV.id
);
if (isEmailExists) {
  alert("Email đã tồn tại!");
  return;
}

// Kiểm tra trùng số điện thoại
const isPhoneExists = this.nhanViens.some(
  (nv) => nv.soDienThoai === this.editNV.soDienThoai && nv.id !== this.editNV.id
);
if (isPhoneExists) {
  alert("Số điện thoại đã tồn tại!");
  return;
}


      try {
        await axios.put(
          `http://localhost:8081/api/nhanvien/sua/${this.editNV.id}`,
          this.editNV
        );
        alert("Cập nhật thành công!");
        this.getAllNhanVien();
        document.querySelector("#editNhanVienModal .btn-close").click();
      } catch (err) {
        alert("Lỗi khi cập nhật: " + (err.response?.data || err.message));
      }
    },

    // Xem chi tiết
    viewNhanVien(nv) {
      this.viewNV = { ...nv };
      const modal = new bootstrap.Modal(
        document.getElementById("viewNhanVienModal")
      );
      modal.show();
    },

    // Format label (map key -> label đọc được)
    formatLabel(key) {
      const map = {
        tenHienThi: "Tên hiển thị",
        username: "Tên đăng nhập",
        passwords: "Mật khẩu",
        hoTen: "Họ tên",
        gioiTinh: "Giới tính",
        email: "Email",
        soDienThoai: "Số điện thoại",
        diaChiGiaoHang: "Địa chỉ giao hàng",
        roleId: "Mã vai trò",
        roleName: "Tên vai trò",
        id: "ID",
      };
      return map[key] || key.charAt(0).toUpperCase() + key.slice(1);
    },

    // Format value nhỏ: hiển thị "Nam"/"Nữ" thay vì 1/0
    formatValue(key, value) {
      if (key === "gioiTinh") return value === 1 ? "Nam" : "Nữ";
      return value === null || value === undefined ? "" : value;
    },
  },
};
</script>

<style>
body {
  background-color: #f5f7fb;
}
.table th,
.table td {
  vertical-align: middle;
}
/* style modal xem chi tiết */
#viewNhanVienModal .form-control[disabled] {
  background-color: #f4f6f8;
  color: #333;
  font-weight: 500;
}
#viewNhanVienModal .form-label {
  text-transform: capitalize;
  margin-bottom: 0.25rem;
}
</style>
