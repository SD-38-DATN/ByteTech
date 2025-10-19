<template>
  <div class="pro-login-page">
    <header class="page-header">
      <div class="header-content">
        <div class="header-left">
          <img src="/images/login/bannerLogin.png" alt="ByteTech Logo" class="logo" @click="$router.push('/')" />
          <h1 class="header-title">ByteTech</h1>
        </div>
        <a href="#" class="help-link">
          <i class="bi bi-question-circle"></i>
          <span>Bạn cần giúp đỡ?</span>
        </a>
      </div>
    </header>

    <main class="main-content">
      <div class="login-container">
        <div class="ad-image-side">
          <img src="/images/login/ad.png" alt="ByteTech Advertisement" class="ad-banner" />
          <div class="ad-overlay">
            <div class="ad-text">
              <h2>ByteTech Platform</h2>
              <p>Nền tảng thương mại điện tử thế hệ mới, dành cho tương lai.</p>
            </div>
          </div>
        </div>

        <div class="form-side">
          <h2 class="form-title">{{ isUserLogin ? 'Đăng Nhập' : 'Kênh Người Bán' }}</h2>
          <div class="login-tabs" :class="{ 'seller-active': !isUserLogin }">
            <button :class="{ 'active': isUserLogin }" @click="loginMethod = 'user'">
              Tài khoản
            </button>
            <button :class="{ 'active': !isUserLogin }" @click="loginMethod = 'seller'">
              Người Bán
            </button>
          </div>

          <transition name="form-swap" mode="out-in">
            <div v-if="isUserLogin" class="form-wrapper" key="user-form">
              <form @submit.prevent="loginUser">
                <div class="input-wrapper mb-3">
                  <i class="bi bi-person-fill input-icon"></i>
                  <input id="username" v-model="userForm.username" type="text" class="form-control" placeholder="Email/Số điện thoại/Tên đăng nhập" required />
                </div>

                <div class="input-wrapper mb-4">
                  <i class="bi bi-lock-fill input-icon"></i>
                  <input id="password" v-model="userForm.passwords" :type="userPasswordFieldType" class="form-control" placeholder="Mật khẩu" required />
                  <button type="button" class="toggle-password-btn" @click="toggleUserPasswordVisibility" aria-label="Toggle password visibility">
                    <i class="bi" :class="isUserPasswordVisible ? 'bi-eye-slash' : 'bi-eye'"></i>
                  </button>
                </div>

                <div class="d-flex justify-content-between align-items-center mb-4">
                  <div></div>
                  <a href="#" class="form-text text-decoration-none small link-secondary">Quên mật khẩu?</a>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-lg mb-3" :disabled="isUserLoading">
                  <span v-if="isUserLoading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                  <i v-if="!isUserLoading" class="bi bi-box-arrow-in-right"></i>
                  <span>{{ isUserLoading ? 'Đang xử lý...' : 'Đăng Nhập' }}</span>
                </button>

                <transition name="error-shake">
                  <div v-if="userError" class="error-message">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                    <span>{{ userError }}</span>
                  </div>
                </transition>
              </form>

              <div class="divider"><span>Hoặc đăng nhập với</span></div>

              <div class="social-buttons">
                 <button class="btn btn-social facebook">
                  <i class="bi bi-facebook social-icon"></i>
                  <span>Facebook</span>
                </button>
                <button class="btn btn-social google" @click="loginWithGoogle">
                  <i class="bi bi-google social-icon"></i>
                  <span>Google</span>
                </button>
              </div>

              <p class="text-center text-muted mt-4 small">
                Chưa có tài khoản? <router-link to="/register" class="fw-bold text-decoration-none link-primary">Đăng ký ngay</router-link>
              </p>
            </div>

            <div v-else class="form-wrapper" key="seller-form">
              <form @submit.prevent="loginSeller">
                <div class="input-wrapper mb-3">
                  <i class="bi bi-person-workspace input-icon"></i>
                  <input id="seller-username" v-model="sellerForm.username" type="text" class="form-control" placeholder="Email/Tên đăng nhập" required />
                </div>

                <div class="input-wrapper mb-4">
                  <i class="bi bi-lock-fill input-icon"></i>
                  <input id="seller-password" v-model="sellerForm.passwords" :type="sellerPasswordFieldType" class="form-control" placeholder="Mật khẩu" required />
                  <button type="button" class="toggle-password-btn" @click="toggleSellerPasswordVisibility" aria-label="Toggle password visibility">
                    <i class="bi" :class="isSellerPasswordVisible ? 'bi-eye-slash' : 'bi-eye'"></i>
                  </button>
                </div>
                 <div class="d-flex justify-content-end align-items-center mb-4">
                   <a href="#" class="form-text text-decoration-none small link-secondary">Quên mật khẩu?</a>
                 </div>

                <button type="submit" class="btn btn-primary w-100 btn-lg mb-3" :disabled="isSellerLoading">
                  <span v-if="isSellerLoading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                  <i v-if="!isSellerLoading" class="bi bi-box-arrow-in-right"></i>
                  <span>{{ isSellerLoading ? 'Đang xử lý...' : 'Đăng Nhập' }}</span>
                </button>

                <transition name="error-shake">
                  <div v-if="sellerError" class="error-message">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                    <span>{{ sellerError }}</span>
                  </div>
                </transition>
              </form>
            </div>
          </transition>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
// SCRIPT GIỮ NGUYÊN
import axios from "axios";
export default {
  name: "Login",
  data() {
    return {
      loginMethod: 'user',
      userForm: { username: "", passwords: "" },
      userError: null, isUserLoading: false, isUserPasswordVisible: false,
      sellerForm: { username: "", passwords: "" },
      sellerError: null, isSellerLoading: false, isSellerPasswordVisible: false,
    };
  },
  computed: {
    isUserLogin() { return this.loginMethod === 'user'; },
    userPasswordFieldType() { return this.isUserPasswordVisible ? 'text' : 'password'; },
    sellerPasswordFieldType() { return this.isSellerPasswordVisible ? 'text' : 'password'; }
  },
  methods: {
    saveAuthData(authData) { for (const key in authData) { localStorage.setItem(key, authData[key] || ""); } },
    loginWithGoogle() { window.location.href = "http://localhost:8081/oauth2/authorization/google"; },
    toggleUserPasswordVisibility() { this.isUserPasswordVisible = !this.isUserPasswordVisible; },
    async loginUser() {
      this.isUserLoading = true; this.userError = null;
      try {
        const res = await axios.post("http://localhost:8081/api/auth/login", this.userForm);
        if (res.data.role !== 'ROLE_USER') {
          this.userError = "Tài khoản của bạn không phải tài khoản người dùng."; return;
        }
        this.saveAuthData(res.data); this.$router.push("/");
      } catch (err) { this.userError = "Sai tên đăng nhập hoặc mật khẩu!";
      } finally { this.isUserLoading = false; }
    },
    toggleSellerPasswordVisibility() { this.isSellerPasswordVisible = !this.isSellerPasswordVisible; },
    async loginSeller() {
      this.isSellerLoading = true; this.sellerError = null;
      try {
        const res = await axios.post("http://localhost:8081/api/auth/login", this.sellerForm);
        const role = res.data.role;
        if (role === 'ROLE_ADMIN' || role === 'ROLE_STAFF') {
          this.saveAuthData(res.data); this.$router.push("/admin");
        } else {
          this.sellerError = "Bạn không có quyền truy cập Kênh Người Bán.";
        }
      } catch (err) { this.sellerError = "Sai tên đăng nhập hoặc mật khẩu!";
      } finally { this.isSellerLoading = false; }
    },
  },
  mounted() {
    const params = new URLSearchParams(window.location.search);
    const errorParam = params.get("error");
    if (errorParam) {
      this.userError = decodeURIComponent(errorParam);
      window.history.replaceState({}, document.title, window.location.pathname);
      return;
    }
    const token = params.get("token");
    if (token) {
      const googleUserData = {};
      params.forEach((value, key) => { googleUserData[key] = decodeURIComponent(value || ""); });
      this.saveAuthData(googleUserData); this.$router.push("/");
    }
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;700&display=swap');
@import url("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css");

/* --- BIẾN MÀU & FONT CHO GIAO DIỆN DARK MODE ĐẲNG CẤP --- */
:root {
  --font-family: 'Be Vietnam Pro', sans-serif;
  --app-orange: #ff6f00; /* Màu cam đậm hơn, nổi bật hơn */
  --text-primary: #e0e0e0;
  --text-secondary: #8e8e93;
  --bg-dark: #101012; /* Nền tối hơn 1 chút */
  --bg-card: #1c1c1e;
  --border-color: rgba(255, 255, 255, 0.1);
  --border-hover: rgba(255, 255, 255, 0.2);
  --error-color: #ff4d4f;
  --error-bg: rgba(255, 77, 79, 0.1);
}

/* --- BỐ CỤC TỔNG THỂ & NỀN --- */
.pro-login-page {
  font-family: var(--font-family);
  background-color: var(--bg-dark);
  color: var(--text-primary);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Hiệu ứng nền có chiều sâu hơn */
.main-content {
  flex-grow: 1;
  display: grid;
  place-items: center;
  padding: 2rem;
  position: relative;
  overflow: hidden;
  background-image:
    radial-gradient(ellipse 80% 80% at 50% -20%, rgba(255, 111, 0, 0.15), transparent),
    linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px);
  background-size: 100% 100%, 1.5rem 1.5rem;
}

/* --- HEADER --- */
.page-header {
  width: 100%;
  padding: 1rem 2rem;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 10;
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.logo {
  height: 40px;
  cursor: pointer;
  filter: drop-shadow(0 0 10px rgba(255, 111, 0, 0.3));
}
.header-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}
.help-link {
  color: var(--text-secondary);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: color 0.3s ease;
}
.help-link:hover {
  color: var(--app-orange);
}


/* --- KHUNG ĐĂNG NHẬP CHÍNH --- */
.login-container {
  display: grid;
  grid-template-columns: 0.8fr 1fr; /* Điều chỉnh tỉ lệ */
  max-width: 960px;
  width: 100%;
  background-color: var(--bg-card);
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,0.4);
  z-index: 1;
  overflow: hidden;
  /* Viền gradient công nghệ cao */
  border: 1px solid transparent;
  background-image: linear-gradient(var(--bg-card), var(--bg-card)),
                    linear-gradient(135deg, rgba(255, 111, 0, 0.3), rgba(255,255,255,0.05));
  background-origin: border-box;
  background-clip: padding-box, border-box;
}

/* --- PHẦN HÌNH ẢNH BÊN TRÁI --- */
.ad-image-side {
  position: relative;
  overflow: hidden;
}
.ad-banner {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.165, 0.84, 0.44, 1);
}
.login-container:hover .ad-banner {
  transform: scale(1.05);
}
/* Lớp phủ màu và chữ */
.ad-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, transparent 70%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 2.5rem;
  color: #fff;
}
.ad-text h2 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  text-shadow: 0 2px 10px rgba(0,0,0,0.5);
}
.ad-text p {
  font-size: 1rem;
  color: var(--text-primary);
  text-shadow: 0 2px 10px rgba(0,0,0,0.5);
}

.form-side {
  padding: 3rem;
  display: flex;
  flex-direction: column;
}

.form-title {
  font-size: 1.75rem;
  font-weight: 700;
  text-align: center;
  margin-bottom: 1rem;
  color: var(--text-primary);
}

/* --- TABS CHUYỂN ĐỔI --- */
.login-tabs {
  display: flex;
  position: relative;
  margin-bottom: 2rem;
  background-color: rgba(0,0,0,0.2);
  border-radius: 8px;
  padding: 4px;
}
.login-tabs button {
  flex: 1;
  padding: 0.75rem 0;
  border: none;
  background: none;
  font-size: 1rem;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  position: relative;
  z-index: 2;
  transition: color 0.4s ease;
}
.login-tabs button.active {
  color: #fff;
}

/* Thanh trượt động nền */
.login-tabs::after {
  content: '';
  position: absolute;
  top: 4px;
  bottom: 4px;
  left: 4px;
  width: calc(50% - 4px);
  background-color: var(--app-orange);
  border-radius: 6px;
  box-shadow: 0 0 15px rgba(255, 111, 0, 0.5);
  transition: transform 0.4s cubic-bezier(0.68, -0.55, 0.27, 1.55);
  z-index: 1;
}
.login-tabs.seller-active::after {
  transform: translateX(calc(100% + 4px));
}

/* --- FORM & INPUT --- */
.input-wrapper {
  position: relative;
}
/* Icon bên trong input */
.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-secondary);
  font-size: 1.2rem;
  transition: color 0.3s ease;
}
.form-control {
  height: 52px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 0 1rem 0 3rem; /* Tăng padding trái cho icon */
  width: 100%;
  font-size: 1rem;
  font-weight: 500;
  color: var(--text-primary);
  background-color: transparent;
  caret-color: var(--app-orange);
  transition: all 0.3s ease;
}
.form-control::placeholder {
  color: var(--text-secondary);
}
.form-control:hover {
  border-color: var(--border-hover);
}
/* Hiệu ứng focus-within cho cả wrapper */
.input-wrapper:focus-within .form-control {
  outline: none;
  border-color: var(--app-orange);
  box-shadow: 0 0 15px rgba(255, 111, 0, 0.2);
}
.input-wrapper:focus-within .input-icon {
  color: var(--app-orange);
}

.toggle-password-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: var(--text-secondary);
  font-size: 1.1rem;
  transition: color 0.3s ease;
}
.toggle-password-btn:hover i {
  color: var(--text-primary);
}


/* --- NÚT BẤM "NĂNG LƯỢNG" --- */
.btn-primary {
  height: 52px;
  border: none;
  border-radius: 8px;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  /* Gradient nền động */
  background-image: linear-gradient(45deg, var(--app-orange) 0%, #ff8f00 50%, var(--app-orange) 100%);
  background-size: 200% auto;
}
.btn-primary:hover:not(:disabled) {
  background-position: right center; /* Chuyển gradient */
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(255, 111, 0, 0.2);
}
.btn-primary:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 5px 10px rgba(255, 111, 0, 0.2);
}
.btn-primary:disabled {
  background-image: none;
  background-color: #555;
  color: #888;
  cursor: not-allowed;
}

/* --- THÀNH PHẦN KHÁC --- */
.link-secondary:hover { color: var(--text-primary); }
.link-primary { color: var(--app-orange) !important; }
.link-primary:hover { filter: brightness(1.2); text-decoration: underline !important; }

.error-message {
  display: flex;
  align-items: center;
  background-color: var(--error-bg);
  border: 1px solid var(--error-color);
  color: var(--error-color);
  padding: 0.8rem 1rem;
  border-radius: 8px;
  font-weight: 500;
  margin-top: 1.25rem;
}
.error-message i { margin-right: 0.75rem; }

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 1.75rem 0;
  color: var(--text-secondary);
  font-size: 0.8rem;
  text-transform: uppercase;
}
.divider::before, .divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid var(--border-color);
}
.divider span { padding: 0 1rem; }

/* Nút mạng xã hội được thiết kế lại */
.social-buttons { display: flex; gap: 1rem; }
.btn-social {
  flex: 1;
  height: 48px;
  background-color: transparent;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  color: var(--text-primary);
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.btn-social::before {
  content: '';
  position: absolute;
  inset: 0;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s ease;
  z-index: -1;
}
.btn-social.facebook::before { background-color: #1877F2; }
.btn-social.google::before { background-color: #DB4437; }
.btn-social:hover {
  border-color: transparent;
  color: #fff;
}
.btn-social:hover::before {
  transform: scaleX(1);
}
.social-icon { font-size: 1.3rem; }

/* --- RESPONSIVE & HIỆU ỨNG --- */
@media (max-width: 992px) {
  .login-container {
    grid-template-columns: 1fr;
    max-width: 480px;
  }
  .ad-image-side { display: none; }
  .form-side { padding: 2.5rem; }
  .page-header { padding: 1rem 1.5rem; }
}
@media (max-width: 576px) {
  .main-content { padding: 1rem; padding-top: 6rem; }
  .form-side { padding: 2rem 1.5rem; }
  .social-buttons { flex-direction: column; }
  .form-title { font-size: 1.5rem; }
  .header-title { display: none; }
}

/* Hiệu ứng chuyển form */
.form-swap-enter-active,
.form-swap-leave-active {
  transition: opacity 0.2s ease-in-out, transform 0.2s ease-in-out;
}
.form-swap-enter-from {
  opacity: 0;
  transform: translateY(15px);
}
.form-swap-leave-to {
  opacity: 0;
  transform: translateY(-15px);
}

/* Hiệu ứng rung cho thông báo lỗi */
@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}
.error-shake-enter-active {
  animation: shake 0.82s cubic-bezier(.36,.07,.19,.97) both;
}
</style>