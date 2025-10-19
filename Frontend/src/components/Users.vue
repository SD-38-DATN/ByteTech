<template>
  <div class="container mt-4">
    <h2 class="mb-3">Quản lý Users</h2>

    <!-- Danh sách Users -->
    <table class="table table-bordered">
      <thead>
        <tr>
          <th>ID</th>
          <th>Username</th>
          <th>Họ tên</th>
          <th>Email</th>
          <th>SĐT</th>
          <th>Role</th>
          <th>Hành động</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{ u.id }}</td>
          <td>{{ u.username }}</td>
          <td>{{ u.hoTen }}</td>
          <td>{{ u.email }}</td>
          <td>{{ u.soDienThoai }}</td>
          <td>{{ u.roleName }}</td>
          <td>
            <button class="btn btn-info btn-sm me-2" @click="viewUser(u.id)">Xem</button>
            <button class="btn btn-danger btn-sm" @click="removeUser(u.id)">Xóa</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Form thêm mới -->
    <div class="card p-3 mt-4">
      <h5>Thêm User</h5>
      <form @submit.prevent="addUser">
        <div class="mb-2">
          <input v-model="newUser.username" class="form-control" placeholder="Username" />
        </div>
        <div class="mb-2">
          <input v-model="newUser.hoTen" class="form-control" placeholder="Họ tên" />
        </div>
        <div class="mb-2">
          <input v-model="newUser.email" class="form-control" placeholder="Email" />
        </div>
        <div class="mb-2">
          <input v-model="newUser.soDienThoai" class="form-control" placeholder="Số điện thoại" />
        </div>
        <div class="mb-2">
          <input v-model="newUser.diaChiGiaoHang" class="form-control" placeholder="Địa chỉ giao hàng" />
        </div>
        <div class="mb-2">
          <input v-model.number="newUser.roleId" class="form-control" placeholder="Role ID" />
        </div>
        <button type="submit" class="btn btn-success w-100">Thêm mới</button>
      </form>
    </div>

    <!-- Chi tiết User -->
    <div v-if="selectedUser" class="card p-3 mt-4">
      <h5>Chi tiết User</h5>
      <p><b>ID:</b> {{ selectedUser.id }}</p>
      <div class="mb-2">
        <label>Họ tên</label>
        <input v-model="selectedUser.hoTen" class="form-control" />
      </div>
      <div class="mb-2">
        <label>Email</label>
        <input v-model="selectedUser.email" class="form-control" />
      </div>
      <div class="mb-2">
        <label>Số điện thoại</label>
        <input v-model="selectedUser.soDienThoai" class="form-control" />
      </div>
      <button class="btn btn-primary w-100" @click="editUser">Cập nhật</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import {
  fetchUsers,
  fetchUserById,
  createUser,
  updateUser,
  deleteUser,
} from "../service/api.js";

const users = ref([]);
const selectedUser = ref(null);

const newUser = ref({
  username: "",
  hoTen: "",
  gioiTinh: 1,
  email: "",
  soDienThoai: "",
  diaChiGiaoHang: "",
  roleId: 1,
});

// load danh sách
const loadUsers = async () => {
  users.value = await fetchUsers();
};

// xem chi tiết
const viewUser = async (id) => {
  selectedUser.value = await fetchUserById(id);
};

// thêm mới
const addUser = async () => {
  await createUser(newUser.value);
  await loadUsers();
  resetForm();
};

// cập nhật
const editUser = async () => {
  if (!selectedUser.value) return;
  await updateUser(selectedUser.value.id, selectedUser.value);
  await loadUsers();
};

// xóa
const removeUser = async (id) => {
  await deleteUser(id);
  await loadUsers();
};

// reset form
const resetForm = () => {
  newUser.value = {
    username: "",
    hoTen: "",
    gioiTinh: 1,
    email: "",
    soDienThoai: "",
    diaChiGiaoHang: "",
    roleId: 1,
  };
};

onMounted(() => {
  loadUsers();
});
</script>
