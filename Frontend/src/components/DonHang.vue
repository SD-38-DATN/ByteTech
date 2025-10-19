<template>
  
  <div class="d-flex vh-100 bg-body-tertiary">
    <!-- Sidebar -->
    <aside class="bg-white border-end p-3 d-flex flex-column shadow-sm overflow-auto" style="width: 280px; height: 100vh;">
      <!-- Profile -->
      <div class="text-center mb-4">
        <img src="https://i.pravatar.cc/90?img=32" alt="avatar" class="avatar-img rounded-circle mb-2 p-1 bg-white">
        <h6 class="fw-bold mb-0 text-dark">Quy Do</h6>
        <p class="small text-muted mb-1">096*****82</p>
        <span class="badge rounded-pill bg-danger-subtle text-danger-emphasis fw-semibold">S-NULL</span>
        <p class="small text-muted mt-2 fst-italic">Cập nhật lần sau 01/01/2026</p>
      </div>

      <!-- Sidebar Items -->
      <ul class="nav flex-column">
        <li class="nav-item mb-1" v-for="item in sidebarItems" :key="item.label">
          <a href="#"
             class="nav-link sidebar-link py-2 px-3 rounded d-flex align-items-center"
             :class="{'active bg-danger-subtle text-danger fw-bold': item.active, 'text-dark': !item.active}"
             @click.prevent="setActive(item)">
            <i :class="item.icon + ' me-2'"></i>{{ item.label }}
          </a>
        </li>
      </ul>

      <!-- Footer Sidebar -->
      <div class="mt-auto text-center p-3 border-top">
        <p class="small text-muted mb-2">Mua sắm dễ dàng - Ưu đãi ngập tràn</p>
        <div class="d-flex justify-content-center gap-2 mb-3">
          <img src="https://upload.wikimedia.org/wikipedia/commons/5/5f/Available_on_the_App_Store_%28black%29.png" alt="App Store" style="height: 28px">
          <img src="https://upload.wikimedia.org/wikipedia/commons/c/cd/Get_it_on_Google_play.svg" alt="Google Play" style="height: 28px">
        </div>
        <p class="small mb-1"><i class="bi bi-telephone me-1"></i> 1800-1234</p>
        <div class="d-flex justify-content-center gap-3 fs-5 text-muted">
          <i class="bi bi-facebook"></i>
          <i class="bi bi-instagram"></i>
          <i class="bi bi-youtube"></i>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-fill p-4 overflow-auto">
      <component :is="activeComponent.component" :title="activeComponent.label" />
    </main>
  </div>
</template>

<script>
import TongQuan from './dashboard/TongQuan.vue';
import LichSuMuaHang from './dashboard/LichSuMuaHang.vue';
import DashboardPlaceholder from './dashboard/DashboardPlaceholder.vue';
import ThongTinTaiKhoan from './dashboard/ThongTinTaiKhoan.vue';
import VoucherCuaToi from './dashboard/VoucherCuaToi.vue';

export default {
  name: "DonHang",
  components: {
    TongQuan,
    LichSuMuaHang,
    DashboardPlaceholder,
    ThongTinTaiKhoan,
    VoucherCuaToi
  },
  data() {
    return {
      activeComponent: {},
      sidebarItems: [
        { label: "Tổng quan", icon: "bi bi-house", component: 'TongQuan' },
        { label: "Lịch sử mua hàng", icon: "bi bi-bag", component: 'LichSuMuaHang' },
        { label: "Tra cứu bảo hành", icon: "bi bi-shield-check", component: 'DashboardPlaceholder' },
        { label: "Voucher cua ban", icon: "bi bi-gift", component: 'VoucherCuaToi' },
        { label: "Ưu đãi S-Student & S-Teacher", icon: "bi bi-person-badge", component: 'DashboardPlaceholder' },
        { label: "Thông tin tài khoản", icon: "bi bi-person", component: 'ThongTinTaiKhoan' },
        { label: "Tìm kiếm của tôi", icon: "bi bi-search", component: 'DashboardPlaceholder' },
        { label: "Chính sách bảo hành", icon: "bi bi-gear", component: 'DashboardPlaceholder' },
        { label: "Góp ý - Phản hồi - Hỗ trợ", icon: "bi bi-chat-left-text", component: 'DashboardPlaceholder' },
        { label: "Điều khoản sử dụng", icon: "bi bi-file-earmark-text", component: 'DashboardPlaceholder' },
        { label: "Đăng xuất", icon: "bi bi-box-arrow-right", component: 'DashboardPlaceholder' },
      ],
    };
  },
  created() {
    this.sidebarItems.forEach(item => item.active = false);

    const componentsMap = this.componentsMap;
    const tab = this.$route.query.tab || "TongQuan";

    if (componentsMap[tab]) {
      this.activeComponent = { label: tab, component: componentsMap[tab] };
      const match = this.sidebarItems.find(item => item.component === tab);
      if (match) match.active = true;
    } else {
      this.activeComponent = { label: "Tổng quan", component: TongQuan };
      this.sidebarItems[0].active = true;
    }
  },
  computed: {
    componentsMap() {
      return {
        TongQuan: this.$options.components.TongQuan,
        LichSuMuaHang: this.$options.components.LichSuMuaHang,
        ThongTinTaiKhoan: this.$options.components.ThongTinTaiKhoan,
        DashboardPlaceholder: this.$options.components.DashboardPlaceholder,
        VoucherCuaToi: this.$options.components.VoucherCuaToi
      };
    }
  },
  methods: {
    setActive(clickedItem) {
      this.sidebarItems.forEach(item => item.active = false);
      clickedItem.active = true;

      this.activeComponent = { label: clickedItem.label, component: this.componentsMap[clickedItem.component] };

      // ✅ Cập nhật query để reload vẫn giữ tab
      this.$router.push({ query: { tab: clickedItem.component } }).catch(() => {});
    },
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');
.d-flex { font-family: 'Inter', sans-serif; }
.avatar-img { box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.sidebar-link {
  font-weight: 500;
  transition: background-color 0.2s ease, color 0.2s ease;
  border-left: 3px solid transparent;
}
.sidebar-link:not(.active):hover {
  background-color: #f8f9fa;
  color: #000 !important;
}
.sidebar-link.active {
  border-left: 3px solid #e60012;
}
main::-webkit-scrollbar, aside::-webkit-scrollbar { width: 6px; }
main::-webkit-scrollbar-thumb, aside::-webkit-scrollbar-thumb { background: #d6d6d6; border-radius: 10px; }
main::-webkit-scrollbar-thumb:hover, aside::-webkit-scrollbar-thumb:hover { background: #bcbcbc; }
</style>
