<template>
  <div class="container-fluid px-4 py-4 bg-light min-vh-100">
    <!-- HEADER -->
    <header
      class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center bg-white p-4 rounded-4 shadow-sm mb-4">
      <div class="mb-3 mb-md-0">
        <h1 class="h4 fw-bold text-success mb-1">Quản lý Khách hàng</h1>
      </div>
    </header>

    <!-- Thống kê -->
    <div class="row row-cols-1 row-cols-md-3 g-3 mb-4">
      <div class="col">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-body d-flex align-items-center">
            <div class="bg-success-subtle text-success p-3 rounded-3 me-3">
              <i class="bi bi-people fs-3"></i>
            </div>
            <div>
              <div class="fw-semibold">Tổng khách hàng</div>
              <div class="fs-4 fw-bold text-success">{{ paging.total }}</div>
              <small class="text-secondary">Trong hệ thống</small>
            </div>
          </div>
        </div>
      </div>

      <div class="col">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-body d-flex align-items-center">
            <div class="bg-primary-subtle text-primary p-3 rounded-3 me-3">
              <i class="bi bi-shield-check fs-3"></i>
            </div>
            <div>
              <div class="fw-semibold">Đang hoạt động</div>
              <div class="fs-4 fw-bold text-primary">{{ statusSummary.active }}</div>
              <small class="text-secondary">Trên trang hiện tại</small>
            </div>
          </div>
        </div>
      </div>

      <div class="col">
        <div class="card border-0 shadow-sm h-100">
          <div class="card-body d-flex align-items-center">
            <div class="bg-warning-subtle text-warning p-3 rounded-3 me-3">
              <i class="bi bi-shield-lock fs-3"></i>
            </div>
            <div>
              <div class="fw-semibold">Ngừng hoạt động</div>
              <div class="fs-4 fw-bold text-warning">{{ statusSummary.inactive }}</div>
              <small class="text-secondary">Trên trang hiện tại</small>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Thanh lọc -->
    <div class="filter-card">
      <div class="filter-left">
        <div class="search-wrap">
          <input
            v-model.trim="query.search"
            class="search-input"
            placeholder="Tìm theo tên/hiển thị/SĐT/email"
            @keyup.enter="doSearch"
          />
          <button class="search-btn" @click="doSearch">
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>

      <div class="filter-right">
        <div class="select-wrap">
          <span class="lbl">Giới tính:</span>
          <select v-model="query.gender" @change="doSearch">
            <option value="">Tất cả</option>
            <option value="1">Nam</option>
            <option value="0">Nữ</option>
            <option value="2">Khác</option>
          </select>
        </div>

        <div class="select-wrap">
          <span class="lbl">Trạng thái:</span>
          <select v-model="query.status" @change="doSearch">
            <option value="">Tất cả</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="INACTIVE">INACTIVE</option>
            <option value="BANNED">BANNED</option>
          </select>
        </div>

        <button class="excel-btn" :disabled="items.length===0" @click="exportExcel" title="Xuất CSV">
          <i class="bi bi-file-earmark-excel"></i>
          <span>Xuất Excel</span>
        </button>

        <button class="btn btn-success" @click="openCreate" title="Thêm khách hàng">
          <i class="bi bi-person-plus"></i>
          <span class="ms-1">Thêm khách hàng</span>
        </button>
      </div>
    </div>

    <!-- BẢNG -->
    <div class="card border-0 shadow-sm overflow-hidden">
      <div v-if="tableError" class="alert alert-danger m-3">
        {{ tableError }}
      </div>

      <div class="table-responsive">
        <table class="table align-middle mb-0 table-hover">
          <thead class="table-success sticky-top">
            <tr>
              <th style="width:72px">STT</th>
              <th>Email</th>
              <th>Tên hiển thị / Họ tên</th>
              <th>Giới tính</th>
              <th>Số điện thoại</th>
              <th>Địa chỉ</th>
              <th>Trạng thái</th>
              <th class="text-end" style="width:210px">Thao tác</th>
            </tr>
          </thead>

          <tbody v-if="loading">
            <tr>
              <td colspan="8" class="text-center py-5">
                <div class="d-flex flex-column align-items-center gap-2">
                  <div class="spinner-border text-success" role="status"></div>
                  <div class="fw-semibold">Đang tải dữ liệu…</div>
                  <div class="text-secondary small">Vui lòng chờ trong giây lát.</div>
                </div>
              </td>
            </tr>
          </tbody>

          <tbody v-else>
            <tr v-for="(u, i) in items" :key="u.id">
              <td class="text-secondary fw-semibold">{{ (paging.page-1)*paging.size + i + 1 }}</td>
              <td class="fw-medium">{{ u.email }}</td>
              <td>
                <div class="d-flex align-items-center gap-2">
                  <div class="avatar-compact">
                    {{ (u.hoTen || u.tenHienThi || 'U').charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <div class="fw-semibold">{{ u.tenHienThi || '-' }}</div>
                    <small class="text-secondary">{{ u.hoTen || '-' }}</small>
                  </div>
                </div>
              </td>
              <td>{{ genderLabel(u.gioiTinh) }}</td>
              <td>{{ u.soDienThoai || '-' }}</td>
              <td>{{ u.diaChiGiaoHang || '-' }}</td>
              <td>
                <span
                  class="badge px-3 py-2 border rounded-pill"
                  :class="statusBadgeClass(u.trangThai)">
                  {{ statusLabel(u.trangThai) }}
                </span>
              </td>
              <td class="text-end">
                <div class="btn-group btn-group-sm">
                  <button class="btn btn-outline-success" @click="viewUser(u)" title="Xem / Sửa">
                    <i class="bi bi-eye"></i>
                  </button>
                  <button
                    class="btn btn-outline-warning"
                    @click="toggleStatus(u)"
                    title="Đổi trạng thái">
                    <i class="bi" :class="u.trangThai==='ACTIVE' ? 'bi-toggle-on' : 'bi-toggle-off'"></i>
                  </button>
                </div>
              </td>
            </tr>

            <tr v-if="items.length===0">
              <td colspan="8" class="text-center py-5 text-secondary">
                <i class="bi bi-inboxes fs-3 d-block mb-2 text-success"></i>
                Không có dữ liệu phù hợp. Hãy đổi bộ lọc hoặc từ khóa.
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- PHÂN TRANG -->
      <div class="card-body bg-light-subtle border-top">
        <div class="d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
          <div class="text-secondary small">
            Hiển thị
            <strong>{{ shownFrom }}</strong>–<strong>{{ shownTo }}</strong>
            trong tổng <strong>{{ paging.total }}</strong> khách hàng
          </div>

          <nav aria-label="Page navigation">
            <ul class="pagination pagination-sm mb-0">
              <li class="page-item" :class="{ disabled: paging.page===1 }">
                <button class="page-link" @click="go(1)" aria-label="First">
                  <i class="bi bi-chevron-double-left"></i>
                </button>
              </li>
              <li class="page-item" :class="{ disabled: paging.page===1 }">
                <button class="page-link" @click="go(paging.page-1)" aria-label="Previous">
                  <i class="bi bi-chevron-left"></i>
                </button>
              </li>
              <li v-for="p in pagesWindow" :key="p" class="page-item" :class="{ active: p===paging.page }">
                <button class="page-link" @click="go(p)">{{ p }}</button>
              </li>
              <li class="page-item" :class="{ disabled: paging.page===paging.pages }">
                <button class="page-link" @click="go(paging.page+1)" aria-label="Next">
                  <i class="bi bi-chevron-right"></i>
                </button>
              </li>
              <li class="page-item" :class="{ disabled: paging.page===paging.pages }">
                <button class="page-link" @click="go(paging.pages)" aria-label="Last">
                  <i class="bi bi-chevron-double-right"></i>
                </button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>

    <!-- Modal Xem/Sửa -->
    <div class="modal fade" id="viewModal" tabindex="-1" ref="viewModalEl">
      <div class="modal-dialog modal-dialog-centered modal-md">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-0 pb-0">
            <h5 class="modal-title fw-semibold">Thông tin khách hàng</h5>
            <button type="button" class="btn-close" @click="hideModal"></button>
          </div>

          <div class="modal-body" v-if="current">
            <div v-if="!editing" class="row g-3">
              <div class="col-12">
                <div class="detail-line">
                  <span class="detail-label">Username</span>
                  <span class="detail-value">{{ current.username }}</span>
                </div>
              </div>
              <div class="col-12">
                <div class="detail-line">
                  <span class="detail-label">Email</span>
                  <span class="detail-value">{{ current.email }}</span>
                </div>
              </div>
              <div class="col-12">
                <div class="detail-line">
                  <span class="detail-label">Tên hiển thị</span>
                  <span class="detail-value">{{ current.tenHienThi || '-' }}</span>
                </div>
              </div>
              <div class="col-12">
                <div class="detail-line">
                  <span class="detail-label">Họ tên</span>
                  <span class="detail-value">{{ current.hoTen || '-' }}</span>
                </div>
              </div>
              <div class="col-6">
                <div class="detail-line">
                  <span class="detail-label">Giới tính</span>
                  <span class="detail-value">{{ genderLabel(current.gioiTinh) }}</span>
                </div>
              </div>
              <div class="col-6">
                <div class="detail-line">
                  <span class="detail-label">SĐT</span>
                  <span class="detail-value">{{ current.soDienThoai || '-' }}</span>
                </div>
              </div>
              <div class="col-12">
                <div class="detail-line">
                  <span class="detail-label">Địa chỉ</span>
                  <span class="detail-value">{{ current.diaChiGiaoHang || '-' }}</span>
                </div>
              </div>
              <div class="col-6">
                <div class="detail-line">
                  <span class="detail-label">Trạng thái</span>
                  <span class="detail-value">{{ statusLabel(current.trangThai) }}</span>
                </div>
              </div>
              <div class="col-6">
                <div class="detail-line">
                  <span class="detail-label">Role</span>
                  <span class="detail-value">{{ current.roleName || 'USER' }}</span>
                </div>
              </div>
            </div>

            <!-- Sửa -->
            <div v-else class="row g-3">
              <div class="col-12">
                <label class="form-label">Username</label>
                <input class="form-control" :value="edit.username" disabled />
              </div>
              <div class="col-12">
                <label class="form-label">Email</label>
                <input v-model.trim="edit.email" class="form-control" />
                <div v-if="errorsEdit.email" class="text-danger small mt-1">{{ errorsEdit.email }}</div>
              </div>
              <div class="col-12">
                <label class="form-label">Tên hiển thị</label>
                <input v-model.trim="edit.tenHienThi" class="form-control" />
              </div>
              <div class="col-12">
                <label class="form-label">Họ tên</label>
                <input v-model.trim="edit.hoTen" class="form-control" />
              </div>
              <div class="col-6">
                <label class="form-label">Giới tính</label>
                <select v-model="edit.gioiTinh" class="form-select">
                  <option :value="1">Nam</option>
                  <option :value="0">Nữ</option>
                  <option :value="2">Khác</option>
                </select>
              </div>
              <div class="col-6">
                <label class="form-label">SĐT</label>
                <input v-model.trim="edit.soDienThoai" class="form-control" />
              </div>
              <div class="col-12">
                <label class="form-label">Địa chỉ</label>
                <input v-model.trim="edit.diaChiGiaoHang" class="form-control" />
              </div>
              <div class="col-6">
                <label class="form-label">Trạng thái</label>
                <select v-model="edit.trangThai" class="form-select">
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="INACTIVE">INACTIVE</option>
                  <option value="BANNED">BANNED</option>
                </select>
              </div>
              <div class="col-6">
                <label class="form-label">Đặt mật khẩu mới (tuỳ chọn)</label>
                <input v-model.trim="edit.newPassword" type="password" class="form-control" placeholder="Để trống nếu không đổi" />
                <div v-if="errorsEdit.newPassword" class="text-danger small mt-1">{{ errorsEdit.newPassword }}</div>
              </div>

              <div v-if="errorsEdit.api" class="text-danger small mt-1 text-center">
                {{ errorsEdit.api }}
              </div>
            </div>
          </div>

          <div class="modal-footer border-0 pt-0" v-if="current">
            <button class="btn btn-light" @click="hideModal">Đóng</button>
            <button v-if="!editing" class="btn btn-primary" @click="editing = true">Sửa</button>
            <template v-else>
              <button class="btn btn-secondary" @click="cancelEdit">Huỷ</button>
              <button class="btn btn-success" @click="saveEdit">Lưu</button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Tạo Mới -->
    <div class="modal fade" id="createModal" tabindex="-1" ref="createModalEl">
      <div class="modal-dialog modal-dialog-centered modal-md">
        <div class="modal-content border-0 shadow-lg rounded-4">
          <div class="modal-header border-0 pb-0">
            <h5 class="modal-title fw-semibold">Thêm khách hàng</h5>
            <button type="button" class="btn-close" @click="hideCreate"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-12">
                <label class="form-label">Username <span class="text-danger">*</span></label>
                <input v-model.trim="create.username" class="form-control" placeholder="username duy nhất" />
                <div v-if="errorsCreate.username" class="text-danger small mt-1">{{ errorsCreate.username }}</div>
              </div>
              <div class="col-12">
                <label class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                <input v-model.trim="create.password" type="password" class="form-control" placeholder="ít nhất 6 ký tự" />
                <div v-if="errorsCreate.password" class="text-danger small mt-1">{{ errorsCreate.password }}</div>
              </div>
              <div class="col-12">
                <label class="form-label">Email</label>
                <input v-model.trim="create.email" type="email" class="form-control" />
                <div v-if="errorsCreate.email" class="text-danger small mt-1">{{ errorsCreate.email }}</div>
              </div>
              <div class="col-12">
                <label class="form-label">Tên hiển thị</label>
                <input v-model.trim="create.tenHienThi" class="form-control" />
              </div>
              <div class="col-12">
                <label class="form-label">Họ tên</label>
                <input v-model.trim="create.hoTen" class="form-control" />
              </div>
              <div class="col-6">
                <label class="form-label">Giới tính</label>
                <select v-model="create.gioiTinh" class="form-select">
                  <option :value="1">Nam</option>
                  <option :value="0">Nữ</option>
                  <option :value="2">Khác</option>
                </select>
              </div>
              <div class="col-6">
                <label class="form-label">SĐT</label>
                <input v-model.trim="create.soDienThoai" class="form-control" />
              </div>
              <div class="col-12">
                <label class="form-label">Địa chỉ</label>
                <input v-model.trim="create.diaChiGiaoHang" class="form-control" />
              </div>
              <div class="col-12">
                <label class="form-label">Trạng thái</label>
                <select v-model="create.trangThai" class="form-select">
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="INACTIVE">INACTIVE</option>
                  <option value="BANNED">BANNED</option>
                </select>
              </div>

              <div v-if="errorsCreate.api" class="text-danger small mt-1 text-center">
                {{ errorsCreate.api }}
              </div>
            </div>
          </div>
          <div class="modal-footer border-0 pt-0">
            <button class="btn btn-light" @click="hideCreate">Huỷ</button>
            <button class="btn btn-success" :disabled="creating" @click="saveCreate">
              {{ creating ? 'Đang tạo…' : 'Tạo mới' }}
            </button>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import api from "@/service/api";

export default {
  name: "AdminCustomers",
  data() {
    return {
      loading: false,
      tableError: "",
      items: [],
      current: null,
      edit: null,
      editing: false,

      // Validate errors
      errorsCreate: {},
      errorsEdit: {},

      // create state
      create: {
        username: "",
        password: "",
        email: "",
        tenHienThi: "",
        hoTen: "",
        gioiTinh: 1,
        soDienThoai: "",
        diaChiGiaoHang: "",
        trangThai: "ACTIVE"
      },
      creating: false,

      query: { search: "", gender: "", status: "" },
      paging: { page: 1, size: 10, total: 0, pages: 1 },
    };
  },
  computed: {
    shownFrom() { return this.paging.total ? (this.paging.page-1)*this.paging.size + 1 : 0; },
    shownTo()   { const v = this.paging.page*this.paging.size; return v>this.paging.total?this.paging.total:v; },
    pagesWindow() {
      const out = [];
      const start = Math.max(1, this.paging.page-2);
      const end = Math.min(this.paging.pages, start+4);
      for (let p=start; p<=end; p++) out.push(p);
      return out;
    },
    statusSummary() {
      return this.items.reduce(
        (acc, it) => {
          if (it.trangThai === "ACTIVE") acc.active += 1;
          if (it.trangThai === "INACTIVE") acc.inactive += 1;
          return acc;
        },
        { active: 0, inactive: 0 }
      );
    },
  },
  methods: {
    statusBadgeClass(s) {
      if (s === "ACTIVE") return "bg-success-subtle text-success border-success";
      if (s === "INACTIVE") return "bg-secondary-subtle text-secondary border-secondary";
      if (s === "BANNED") return "bg-danger-subtle text-danger border-danger";
      return "bg-light text-body border-light";
    },
    async fetchData() {
      this.loading = true;
      this.tableError = "";
      try {
        const params = {
          page: this.paging.page - 1,
          size: this.paging.size,
          search: this.query.search || undefined,
          gender: this.query.gender || undefined,
          status: this.query.status || undefined,
        };
        const res = await api.get(`/admin/users`, { params });
        const payload = res.data?.data ?? res.data ?? {};
        const content = payload.content ?? payload.items ?? [];
        this.items = content;
        this.paging.total = payload.totalElements ?? payload.total ?? content.length;
        this.paging.pages = payload.totalPages ?? Math.max(1, Math.ceil(this.paging.total/this.paging.size));
      } catch (e) {
        console.error(e);
        this.tableError =
          e?.response?.data?.message ||
          e?.response?.data?.error ||
          "Không tải được danh sách khách hàng.";
        this.items = [];
        this.paging.total = 0; this.paging.pages = 1;
      } finally { this.loading = false; }
    },
    doSearch() { this.paging.page = 1; this.fetchData(); },
    go(p){ if(p<1 || p>this.paging.pages || p===this.paging.page) return; this.paging.page=p; this.fetchData(); },
    genderLabel(g){ if(g===1||g==='1') return 'Nam'; if(g===0||g==='0') return 'Nữ'; return 'Khác'; },
    statusLabel(s){
      if (s === 'ACTIVE') return 'ACTIVE';
      if (s === 'INACTIVE') return 'INACTIVE';
      if (s === 'BANNED') return 'BANNED';
      return s || '-';
    },

    async toggleStatus(u){
      // Quy ước: ACTIVE <-> INACTIVE; nếu đang BANNED thì hỏi gỡ ban -> ACTIVE
      let next = null;
      if (u.trangThai === 'BANNED') {
        if (confirm('Tài khoản đang BANNED. Bạn có muốn gỡ ban và chuyển về ACTIVE?')) next = 'ACTIVE';
        else return;
      } else {
        next = (u.trangThai === 'ACTIVE') ? 'INACTIVE' : 'ACTIVE';
      }
      try{
        const res = await api.put(`/admin/users/${u.id}/status`, { status: next });
        const updated = res.data?.data ?? res.data;
        u.trangThai = updated?.trangThai ?? next;
      }catch(e){
        console.error(e);
        const msg = e?.response?.data?.message || "Không cập nhật được trạng thái";
        alert(msg);
      }
    },

    viewUser(u){
      this.current = u;
      this.editing = false;
      this.errorsEdit = {};
      this.edit = {
        id: u.id,
        username: u.username,
        tenHienThi: u.tenHienThi,
        hoTen: u.hoTen,
        gioiTinh: u.gioiTinh,
        soDienThoai: u.soDienThoai,
        diaChiGiaoHang: u.diaChiGiaoHang,
        email: u.email,
        trangThai: u.trangThai,
        newPassword: ""
      };
      const el = this.$refs.viewModalEl;
      if (!el) return;
      el.style.display = "block";
      el.classList.add("show");
      el.removeAttribute("aria-hidden");
      el.setAttribute("aria-modal", "true");
    },
    hideModal(){
      const el = this.$refs.viewModalEl;
      if (!el) return;
      el.style.display = "none";
      el.classList.remove("show");
      el.setAttribute("aria-hidden", "true");
      el.removeAttribute("aria-modal");
      this.current = null;
      this.edit = null;
      this.editing = false;
      this.errorsEdit = {};
    },
    cancelEdit(){
      this.editing = false;
      this.errorsEdit = {};
      this.edit.newPassword = "";
    },
    validateEdit(){
      const errs = {};
      if (this.edit.email && !/^\S+@\S+\.\S+$/.test(this.edit.email)) errs.email = "Email không hợp lệ";
      if (this.edit.newPassword && this.edit.newPassword.length < 6) errs.newPassword = "Mật khẩu mới phải ≥ 6 ký tự";
      return errs;
    },
    async saveEdit(){
      this.errorsEdit = this.validateEdit();
      if (Object.keys(this.errorsEdit).length) return;

      try{
        const payload = {
          tenHienThi: this.edit.tenHienThi,
          hoTen: this.edit.hoTen,
          gioiTinh: this.edit.gioiTinh,
          soDienThoai: this.edit.soDienThoai,
          diaChiGiaoHang: this.edit.diaChiGiaoHang,
          email: this.edit.email,
          trangThai: this.edit.trangThai,
          newPassword: this.edit.newPassword || null
        };
        const res = await api.put(`/admin/users/${this.edit.id}`, payload);
        const updated = res.data?.data ?? res.data;
        const idx = this.items.findIndex(x => x.id === updated.id);
        if (idx >= 0) this.items.splice(idx, 1, updated);
        this.current = updated;
        this.editing = false;
        this.edit.newPassword = "";
      }catch(e){
        console.error(e);
        this.errorsEdit.api = e?.response?.data?.message || "Không thể lưu. Vui lòng thử lại.";
      }
    },

    // ===== Tạo mới =====
    openCreate(){
      this.create = {
        username: "",
        password: "",
        email: "",
        tenHienThi: "",
        hoTen: "",
        gioiTinh: 1,
        soDienThoai: "",
        diaChiGiaoHang: "",
        trangThai: "ACTIVE"
      };
      this.errorsCreate = {};
      const el = this.$refs.createModalEl;
      if (!el) return;
      el.style.display = "block";
      el.classList.add("show");
      el.removeAttribute("aria-hidden");
      el.setAttribute("aria-modal", "true");
    },
    hideCreate(){
      const el = this.$refs.createModalEl;
      if (!el) return;
      el.style.display = "none";
      el.classList.remove("show");
      el.setAttribute("aria-hidden", "true");
      el.removeAttribute("aria-modal");
      this.errorsCreate = {};
    },
    validateCreate(){
      const errs = {};
      if (!this.create.username) errs.username = "Username không được để trống";
      if (!this.create.password) errs.password = "Mật khẩu không được để trống";
      else if (this.create.password.length < 6) errs.password = "Mật khẩu phải ≥ 6 ký tự";
      if (this.create.email && !/^\S+@\S+\.\S+$/.test(this.create.email)) errs.email = "Email không hợp lệ";
      return errs;
    },
    async saveCreate(){
      this.errorsCreate = this.validateCreate();
      if (Object.keys(this.errorsCreate).length) return;

      this.creating = true;
      try{
        const payload = {
          username: this.create.username,
          passwords: this.create.password, // BE: field là "passwords"
          email: this.create.email,
          tenHienThi: this.create.tenHienThi,
          hoTen: this.create.hoTen,
          gioiTinh: this.create.gioiTinh,
          soDienThoai: this.create.soDienThoai,
          diaChiGiaoHang: this.create.diaChiGiaoHang,
          trangThai: this.create.trangThai
        };
        const res = await api.post(`/admin/users`, payload);
        const created = res.data?.data ?? res.data;
        this.items.unshift(created);
        this.paging.total += 1;
        this.hideCreate();
      }catch(e){
        console.error(e);
        this.errorsCreate.api =
          e?.response?.data?.message ||
          e?.response?.data?.error ||
          "Không thể tạo khách hàng. Vui lòng kiểm tra dữ liệu hoặc thử lại.";
      }finally{
        this.creating = false;
      }
    },

    exportExcel(){
      const header = ["STT","Email","Tên hiển thị","Họ tên","Giới tính","SĐT","Địa chỉ","Trạng thái"];
      const rows = this.items.map((u,i)=>[
        (this.paging.page-1)*this.paging.size + i + 1,
        u.email ?? "",
        u.tenHienThi ?? "",
        u.hoTen ?? "",
        this.genderLabel(u.gioiTinh),
        u.soDienThoai ?? "",
        u.diaChiGiaoHang ?? "",
        this.statusLabel(u.trangThai),
      ]);
      const csv = [header, ...rows].map(r=>r.map(c=>`"${String(c).replace(/"/g,'""')}"`).join(",")).join("\r\n");
      const blob = new Blob([`\uFEFF${csv}`], {type:"text/csv;charset=utf-8;"});
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a"); a.href=url; a.download=`khach_hang_${new Date().toISOString().slice(0,10)}.csv`; a.click();
      URL.revokeObjectURL(url);
    }
  },
  mounted(){ this.fetchData(); }
};
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css");

.avatar-compact{
  width: 36px; height: 36px; border-radius: 50%;
  background: #ecfdf3; color: #d70018; display: inline-flex;
  align-items: center; justify-content: center; font-weight: 700;
}

.filter-card{
  background:#fff; border:1px solid #eef0f3; border-radius:14px; padding:16px; display:flex;
  align-items:center; gap:14px; justify-content:space-between; margin-bottom:16px;
}
.filter-left{ flex:1; }
.search-wrap{ display:flex; align-items:center; gap:8px; }
.search-input{
  width:100%; height:44px; border:1px solid #e3e6ec; border-radius:12px; padding:0 14px;
  outline:none; font-size:14px; transition:.15s;
}
.search-input:focus{ border-color:#b5c9ff; box-shadow:0 0 0 3px rgba(111,156,255,.15); }
.search-btn{
  width:44px; height:44px; border:none; border-radius:10px; background:#d70018; color:#fff; cursor:pointer;
  display:grid; place-items:center;
}

.filter-right{ display:flex; align-items:center; gap:12px; }
.select-wrap{
  display:flex; align-items:center; gap:8px; background:#fff; border:1px solid #e3e6ec; border-radius:12px; padding:8px 10px;
}
.select-wrap .lbl{ color:#555; font-size:14px; }
.select-wrap select{
  border:none; outline:none; padding:6px 8px; border-radius:8px; background:#f4f6fa; font-size:14px;
}

.excel-btn{
  display:flex; align-items:center; gap:8px; border:none; border-radius:10px; padding:10px 14px; cursor:pointer;
  background:#19c37d; color:#fff; font-weight:600;
}

.detail-line{
  display:flex; flex-direction:column; padding:12px; border-radius:12px; background:#f5fbf8;
}
.detail-label{
  font-size:.75rem; letter-spacing:.5px; text-transform:uppercase; color:#6b7b80; font-weight:600;
}
.detail-value{ font-weight:600; color:#124f32; }

thead.sticky-top { top: 0; z-index: 5; }
</style>
