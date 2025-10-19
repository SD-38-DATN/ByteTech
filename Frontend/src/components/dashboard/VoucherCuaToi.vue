
<script setup="">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
// Hãy sửa lại đường dẫn cho đúng service thực tế của bạn:
import { Voucherforme } from '../../service/api'; // ← trỏ đúng file service của bạn

const router = useRouter();

// --- STATE MANAGEMENT ---
const allVouchers = ref([]);
const loading = ref(true);
const error = ref(null);
const nowTs = ref(Date.now());
let timer = null;

// UI State
const searchTerm = ref('');
const filterStatus = ref('all'); // 'all', 'active', 'exhausted', 'expired'
const sortBy = ref('endDate-asc'); // 'endDate-asc', 'discount-desc', 'name-asc'
const copiedCode = ref(null); // Lưu mã voucher vừa được sao chép

// --- HELPERS & FORMATTERS ---
const formatCurrency = (num) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num || 0);

const isDateOnly = (s) => typeof s === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(s);

const toTimestamp = (dateStr, isEndDate = false) => {
    if (! dateStr) return NaN;
    const isOnlyDate = isDateOnly(dateStr);

    if (isOnlyDate) {
        const [y, m, d] = dateStr.split('-').map(Number);
        return isEndDate
            ? new Date(y, m - 1, d, 23, 59, 59, 999).getTime()
            : new Date(y, m - 1, d, 0, 0, 0).getTime();
    }

    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return NaN;

    if (isEndDate && d.getHours() === 0 && d.getMinutes() === 0 && d.getSeconds() === 0) {
        d.setHours(23, 59, 59, 999);
    }
    return d.getTime();
};

const formatDateTime = (dateStr) => {
    if (! dateStr) return 'N/A';
    const ts = toTimestamp(dateStr);
    if (isNaN(ts)) return 'N/A';
    return new Date(ts).toLocaleString('vi-VN', {
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit'
    });
};

// --- LOGIC: VOUCHER COMPUTATIONS (OPTIMIZED) ---
const processedVouchers = computed(() => {
    // 1. Map and pre-calculate properties for each voucher
    let mapped = allVouchers.value.map(v => {
        // Calculate remaining uses
        const maxUses = v.soLanSuDungToiDa ?? v.soLanSuDung;
        const used = v.soLanSuDungDaDung ?? 0;
        const remainingUses = typeof maxUses === 'number' ? Math.max(0, maxUses - used) : Infinity;

        // Resolve status
        let status = { key: 'unknown', label: 'Không xác định', overlay: 'LỖI' };
        if (remainingUses <= 0 && typeof maxUses === 'number') {
            status = { key: 'exhausted', label: 'Hết lượt', overlay: 'HẾT LƯỢT' };
        } else if (v.trangThai === 1) {
            status = { key: 'active', label: 'Có thể dùng', overlay: null };
        } else if (v.trangThai === 0) {
            status = { key: 'expired', label: 'Hết hạn', overlay: 'HẾT HẠN' };
        }

        // Calculate discount value for sorting
        const discountValue = v.loaiGiam === 1
            ? Math.min(v.giaTriGiam * (v.dieuKienGiam || 1000000), v.giamToiDa || Infinity) // Approximate value
            : v.giaTriGiam;

        // Countdown
        const endTs = toTimestamp(v.ngayKetThuc, true);
        const diffMs = endTs - nowTs.value;
        let countdownText = `HSD: ${new Date(endTs).toLocaleDateString('vi-VN')}`;
        if (diffMs > 0) {
            const days = Math.floor(diffMs / 86400000);
            const hours = Math.floor((diffMs % 86400000) / 3600000);
            const mins = Math.floor((diffMs % 3600000) / 60000);
            const secs = Math.floor((diffMs % 60000) / 1000);
            if (days >= 1) countdownText = `Còn ${days} ngày ${String(hours).padStart(2, '0')} giờ`;
            else countdownText = `Còn ${String(hours).padStart(2, '0')}:${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
        }

        return {
            ...v,
            _computed: {
                status,
                remainingUses,
                remainingText: (typeof maxUses === 'number' && typeof used === 'number')
                    ? `${used}/${maxUses} (còn ${remainingUses})`
                    : (typeof maxUses === 'number' ? `Tối đa ${maxUses}` : 'Không giới hạn'),
                discountDetails: v.loaiGiam === 1 ? `Giảm ${Math.round((v.giaTriGiam || 0) * 100)}%` : `Giảm ${formatCurrency(v.giaTriGiam)}`,
                discountValue,
                endTs,
                countdownText
            }
        };
    });

    // 2. Filter
    if (filterStatus.value !== 'all') {
        mapped = mapped.filter(v => v._computed.status.key === filterStatus.value);
    }

    if (searchTerm.value) {
        const lowerSearchTerm = searchTerm.value.toLowerCase();
        mapped = mapped.filter(v =>
            v.tenVoucher.toLowerCase().includes(lowerSearchTerm) ||
            (v.moTa || '').toLowerCase().includes(lowerSearchTerm)
        );
    }

    // 3. Sort
    mapped.sort((a, b) => {
        switch (sortBy.value) {
            case 'endDate-asc':
                return a._computed.endTs - b._computed.endTs;
            case 'discount-desc':
                return b._computed.discountValue - a._computed.discountValue;
            case 'name-asc':
                return a.tenVoucher.localeCompare(b.tenVoucher, 'vi');
            default:
                return 0;
        }
    });

    return mapped;
});

// --- ACTIONS ---
const fetchData = async () => {
    loading.value = true;
    error.value = null;
    try {
        const res = await Voucherforme();
        allVouchers.value = Array.isArray(res) ? res : (res?.data ?? []);
    } catch (err) {
        console.error('Lỗi khi tải voucher:', err);
        error.value = 'Không thể tải dữ liệu. Vui lòng thử lại sau.';
    } finally {
        loading.value = false;
    }
};

const useVoucher = (voucher) => {
    if (voucher._computed.status.key !== 'active') return;
    router.push({
        path: '/giohang', // Sửa lại path thực tế của bạn
        query: { voucher: voucher.codeVoucher }
    });
};

const copyCode = async (voucher) => {
    if (! navigator.clipboard) {
        console.error('Clipboard API không khả dụng');
        copiedCode.value = 'error';
        setTimeout(() => { copiedCode.value = null }, 2000);
        return;
    }
    try {
        await navigator.clipboard.writeText(voucher.codeVoucher);
        copiedCode.value = voucher.codeVoucher;
        setTimeout(() => { copiedCode.value = null }, 2000);
    } catch (err) {
        console.error('Sao chép thất bại: ', err);
        copiedCode.value = 'error';
        setTimeout(() => { copiedCode.value = null }, 2000);
    }
};

// --- LIFECYCLE HOOKS ---
onMounted(() => {
    fetchData();
    timer = setInterval(() => {
        nowTs.value = Date.now();
    }, 1000);
});

onUnmounted(() => {
    if (timer) clearInterval(timer);
});
</script>
<template>
    <div class="page-container">
        <header class="page-header">
            <h1 class="page-title">
                🎟️ Kho Voucher Của Bạn
            </h1>
            <p class="page-subtitle">
                Quản lý và sử dụng các ưu đãi độc quyền dành riêng cho bạn.
            </p>
        </header>
        <!-- Controls: Search, Filter, Sort -->
        <div class="controls-bar">
            <div class="control-group search-group">
                <label for="search-voucher" class="control-label">
                    Tìm kiếm
                </label>
                <div class="input-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                        <path fill-rule="evenodd"
                            d="M9 3.5a5.5 5.5 0 100 11 5.5 5.5 0 000-11zM2 9a7 7 0 1112.452 4.391l3.328 3.329a.75.75 0 11-1.06 1.06l-3.329-3.328A7 7 0 012 9z"
                            clip-rule="evenodd"></path>
                    </svg>
                    <input class="control-input" type="text" v-model.trim="searchTerm"
                        placeholder="Tên voucher, mô tả..." />
                </div>
            </div>
            <div class="control-group">
                <label for="filter-status" class="control-label">
                    Trạng thái
                </label>
                <select class="control-select" v-model="filterStatus">
                    <option value="all">
                        Tất cả
                    </option>
                    <option value="active">
                        Có thể dùng
                    </option>
                    <option value="exhausted">
                        Hết lượt
                    </option>
                    <option value="expired">
                        Hết hạn
                    </option>
                </select>
            </div>
            <div class="control-group">
                <label for="sort-by" class="control-label">
                    Sắp xếp
                </label>
                <select class="control-select" v-model="sortBy">
                    <option value="endDate-asc">
                        Hạn dùng gần nhất
                    </option>
                    <option value="discount-desc">
                        Ưu đãi tốt nhất
                    </option>
                    <option value="name-asc">
                        Tên A-Z
                    </option>
                </select>
            </div>
        </div>
        <!-- Main Content Area -->
        <main>
            <!-- Loading State -->
            <div v-if="loading" class="grid-container">
                <div v-for="i in 6" :key="i" class="ticket-skeleton">
                    <div class="skeleton-left"></div>
                    <div class="skeleton-right">
                        <div class="h-4 skeleton-line w-3/4"></div>
                        <div class="h-3 skeleton-line w-1/2"></div>
                        <div class="skeleton-footer">
                            <div class="h-3 skeleton-line w-1/3"></div>
                            <div class="skeleton-button"></div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Error State -->
            <div v-else-if="error" class="error-box message-box">
                <div class="message-icon">
                    ⚠️
                </div>
                <h3 class="message-title">
                    Rất tiếc, đã xảy ra lỗi
                </h3>
                <p class="message-text">
                    {{ error }}
                </p>
                <button @click="fetchData" class="btn-primary">
                    Tải lại trang
                </button>
            </div>
            <!-- Content Grid -->
            <div v-else-if="processedVouchers.length > 0" class="grid-container">
                <div v-for="voucher in processedVouchers" :key="voucher.id" class="ticket"
                    :class="[voucher._computed.status.key]" :aria-label="`Voucher ${voucher.tenVoucher}`">
                    <div v-if="voucher._computed.status.overlay" class="ticket-overlay" aria-hidden="true">
                        <span>
                            {{ voucher._computed.status.overlay }}
                        </span>
                    </div>
                    <!-- Left Side: Discount -->
                    <div class="ticket-left" :class="voucher.loaiGiam === 1 ? 'bg-percent' : 'bg-direct'">
                        <div class="discount-value">
                            {{ voucher.loaiGiam === 1
                                ? Math.round((voucher.giaTriGiam || 0) * 100) + '%'
                                : Math.round((voucher.giaTriGiam || 0) / 1000) + 'K' }}
                        </div>
                    </div>
                    <div class="ticket-separator" aria-hidden="true"></div>
                    <!-- Right Side: Details -->
                    <div class="ticket-right">
                        <header class="voucher-header">
                            <h3 class="voucher-title">
                                {{ voucher.tenVoucher }}
                            </h3>
                            <p v-if="voucher.moTa" class="voucher-description">
                                {{ voucher.moTa }}
                            </p>
                        </header>
                        <div class="details-section">
                            <span class="detail-item status-badge" :class="voucher._computed.status.key">
                                {{ voucher._computed.status.label }}
                            </span>
                            <span class="detail-item">
                                {{ voucher._computed.discountDetails }}
                            </span>
                            <span v-if="voucher.loaiGiam === 1 && voucher.giamToiDa > 0" class="detail-item">
                                Tối đa: {{ formatCurrency(voucher.giamToiDa) }}
                            </span>
                            <span v-if="voucher.dieuKienGiam > 0" class="detail-item">
                                Đơn từ: {{ formatCurrency(voucher.dieuKienGiam) }}
                            </span>
                            <span class="detail-item">
                                Lượt dùng: {{ voucher.soLanSuDung }}
                            </span>
                        </div>
                        <footer class="voucher-footer">
                            <div class="countdown">
                                ⏳ {{ voucher._computed.countdownText }}
                            </div>
                            <button class="btn-use" :disabled="voucher._computed.status.key !== 'active'"
                                @click="useVoucher(voucher)">
                                Dùng ngay
                            </button>
                        </footer>
                    </div>
                </div>
            </div>
            <!-- Empty State -->
            <div v-else class="empty-box message-box">
                <div class="message-icon">
                    🤷
                </div>
                <h3 class="message-title">
                    Không tìm thấy voucher nào
                </h3>
                <p class="message-text">
                    Có vẻ như bạn chưa có voucher nào, hoặc không có voucher nào khớp với bộ lọc.
                </p>
                <button @click="router.push('/')" class="btn-primary">
                    Khám phá sản phẩm
                </button>
            </div>
        </main>
    </div>
</template>
<style scoped>
/* --- SETUP & VARIABLES --- */
@import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap');

.page-container {
    --font-family: 'Be Vietnam Pro', sans-serif;

    /* Light Theme */
    --bg-page-light: #f8fafc;
    /* slate-50 */
    --bg-card-light: #ffffff;
    --text-primary-light: #1e293b;
    /* slate-800 */
    --text-secondary-light: #64748b;
    /* slate-500 */
    --text-tertiary-light: #94a3b8;
    /* slate-400 */
    --border-light: #e2e8f0;
    /* slate-200 */
    --shadow-light: rgba(30, 41, 59, 0.07);

    /* Dark Theme */
    --bg-page-dark: #0f172a;
    /* slate-900 */
    --bg-card-dark: #1e293b;
    /* slate-800 */
    --text-primary-dark: #f1f5f9;
    /* slate-100 */
    --text-secondary-dark: #94a3b8;
    /* slate-400 */
    --text-tertiary-dark: #64748b;
    /* slate-500 */
    --border-dark: #334155;
    /* slate-700 */
    --shadow-dark: rgba(0, 0, 0, 0.2);

    /* Accent Colors */
    --c-percent-start: #f97316;
    /* orange-500 */
    --c-percent-end: #dc2626;
    /* red-600 */
    --c-direct-start: #2563eb;
    /* blue-600 */
    --c-direct-end: #7c3aed;
    /* violet-600 */
    --c-success: #16a34a;
    /* green-600 */
    --c-disabled-bg: #e2e8f0;
    --c-disabled-text: #94a3b8;
    --c-focus-ring: #60a5fa;
    /* blue-400 */

    /* Sizing */
    --radius-lg: 12px;
    --radius-md: 8px;
    --radius-sm: 6px;
    --radius-full: 999px;

    /* Apply Theme */
    --bg-page: var(--bg-page-light);
    --bg-card: var(--bg-card-light);
    --text-primary: var(--text-primary-light);
    --text-secondary: var(--text-secondary-light);
    --text-tertiary: var(--text-tertiary-light);
    --border-color: var(--border-light);
    --shadow-color: var(--shadow-light);
}

@media (prefers-color-scheme: dark) {
    .page-container {
        --bg-page: var(--bg-page-dark);
        --bg-card: var(--bg-card-dark);
        --text-primary: var(--text-primary-dark);
        --text-secondary: var(--text-secondary-dark);
        --text-tertiary: var(--text-tertiary-dark);
        --border-color: var(--border-dark);
        --shadow-color: var(--shadow-dark);
    }
}

/* --- BASE & LAYOUT --- */
.page-container {
    font-family: var(--font-family);
    padding: 16px;
    background-color: var(--bg-page);
    min-height: 100vh;
}

@media (min-width: 768px) {
    .page-container {
        padding: 24px;
    }
}

.page-header {
    text-align: center;
    margin-bottom: 24px;
}

.page-title {
    font-size: 2rem;
    font-weight: 800;
    color: var(--text-primary);
    letter-spacing: -0.5px;
}

.page-subtitle {
    font-size: 1rem;
    color: var(--text-secondary);
    margin-top: 4px;
    max-width: 500px;
    margin-left: auto;
    margin-right: auto;
}

.grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 20px;
    max-width: 1400px;
    margin: 0 auto;
}

@media (min-width: 1024px) {
    .grid-container {
        grid-template-columns: repeat(3, 1fr);
    }
}

/* --- CONTROLS BAR --- */
.controls-bar {
    display: grid;
    grid-template-columns: 1fr;
    gap: 12px;
    max-width: 1400px;
    margin: 0 auto 24px auto;
    padding: 12px;
    background-color: var(--bg-card);
    border-radius: var(--radius-lg);
    border: 1px solid var(--border-color);
}

@media (min-width: 768px) {
    .controls-bar {
        grid-template-columns: 2fr 1fr 1fr;
        padding: 16px;
    }
}

.control-group {
    display: flex;
    flex-direction: column;
}

.control-label {
    font-size: 0.75rem;
    font-weight: 600;
    color: var(--text-secondary);
    margin-bottom: 4px;
}

.input-wrapper {
    position: relative;
}

.input-wrapper svg {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
    width: 16px;
    height: 16px;
    color: var(--text-tertiary);
}

.control-input {
    padding: 8px 10px 8px 34px;
}

.control-select {
    padding: 8px 10px;
}

.control-input,
.control-select {
    width: 100%;
    font-family: inherit;
    font-size: 0.9rem;
    background-color: var(--bg-page);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-md);
    color: var(--text-primary);
    transition: border-color 0.2s, box-shadow 0.2s;
    -webkit-appearance: none;
    appearance: none;
}

.control-select {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%2364748b' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e");
    background-position: right 0.5rem center;
    background-repeat: no-repeat;
    background-size: 1.1em 1.1em;
    padding-right: 2.5rem;
}

.control-input:focus,
.control-select:focus {
    outline: none;
    border-color: var(--c-focus-ring);
    box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

/* --- SKELETON & MESSAGES --- */
.message-box {
    text-align: center;
    padding: 40px 24px;
    background-color: var(--bg-card);
    border-radius: var(--radius-lg);
    border: 1px solid var(--border-color);
    max-width: 500px;
    margin: 40px auto;
}

.message-icon {
    font-size: 2.5rem;
    line-height: 1;
    margin-bottom: 12px;
}

.message-title {
    font-size: 1.25rem;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: 8px;
}

.message-text {
    color: var(--text-secondary);
    margin-bottom: 20px;
    font-size: 0.95rem;
}

.btn-primary {
    background-color: var(--c-direct-end);
    color: white;
    padding: 10px 20px;
    border-radius: var(--radius-full);
    font-weight: 600;
    border: none;
    cursor: pointer;
    transition: all 0.2s ease;
}

.btn-primary:hover {
    opacity: 0.9;
    transform: translateY(-2px);
}

.error-box .message-title {
    color: var(--c-percent-end);
}

@keyframes shimmer {
    100% {
        transform: translateX(100%);
    }
}

.ticket-skeleton {
    display: flex;
    background-color: var(--bg-card);
    border-radius: var(--radius-lg);
    overflow: hidden;
    border: 1px solid var(--border-color);
}

.skeleton-left {
    width: 80px;
    background-color: var(--border-color);
}

.skeleton-right {
    flex-grow: 1;
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.skeleton-line,
.skeleton-button {
    background: var(--border-color);
    border-radius: var(--radius-sm);
    position: relative;
    overflow: hidden;
}

.skeleton-line::after,
.skeleton-button::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    transform: translateX(-100%);
    background-image: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    animation: shimmer 1.5s infinite;
}

.h-4 {
    height: 16px;
}

.h-3 {
    height: 12px;
}

.w-3\/4 {
    width: 75%;
}

.w-1\/2 {
    width: 50%;
}

.w-1\/3 {
    width: 33.33%;
}

.skeleton-footer {
    margin-top: auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.skeleton-button {
    width: 80px;
    height: 28px;
    border-radius: var(--radius-full);
}

/* --- TICKET DESIGN --- */
.ticket {
    display: flex;
    background-color: var(--bg-card);
    border-radius: var(--radius-lg);
    box-shadow: 0 4px 10px var(--shadow-color);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    overflow: hidden;
    position: relative;
    border: 1px solid var(--border-color);
}

.ticket:hover:not(.expired):not(.exhausted):not(.unknown) {
    transform: translateY(-3px);
    box-shadow: 0 8px 18px var(--shadow-color);
}

@media (prefers-reduced-motion: reduce) {
    .ticket:hover {
        transform: none;
    }
}

.ticket-overlay {
    position: absolute;
    inset: 0;
    background: rgba(255, 255, 255, 0.25);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 10;
    backdrop-filter: none;
    pointer-events: none;
}

.ticket-overlay span {
    transform: rotate(-10deg);
    font-size: 1.5rem;
    font-weight: 800;
    color: var(--text-tertiary);
    border: 2px dashed var(--text-tertiary);
    padding: 4px 12px;
    border-radius: var(--radius-md);
}

@media (prefers-color-scheme: dark) {
    .ticket-overlay {
        background: rgba(30, 41, 59, 0.6);
    }
}

/* Ticket Left Side */
.ticket-left {
    width: 80px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: white;
    padding: 12px 6px;
    text-align: center;
    transition: filter 0.2s ease;
}

.ticket.expired .ticket-left,
.ticket.exhausted .ticket-left {
    filter: grayscale(30%) brightness(0.9);
}

.bg-percent {
    background: linear-gradient(135deg, var(--c-percent-start), var(--c-percent-end));
}

.bg-direct {
    background: linear-gradient(135deg, var(--c-direct-start), var(--c-direct-end));
}

.discount-value {
    font-size: 1.75rem;
    font-weight: 800;
    line-height: 1;
    white-space: nowrap;
}

/* Ticket Separator */
.ticket-separator {
    width: 2px;
    background-image: repeating-linear-gradient(to bottom, var(--border-color) 0, var(--border-color) 5px, transparent 5px, transparent 10px);
    position: relative;
    margin: 0 2px;
}

.ticket-separator::before,
.ticket-separator::after {
    content: '';
    position: absolute;
    width: 14px;
    height: 14px;
    background-color: var(--bg-page);
    border-radius: 50%;
    left: 50%;
    transform: translateX(-50%);
    box-shadow: inset 0 0 0 1px var(--border-color);
}

.ticket-separator::before {
    top: -7px;
}

.ticket-separator::after {
    bottom: -7px;
}

/* Ticket Right Side */
.ticket-right {
    flex-grow: 1;
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    color: var(--text-primary);
}

.voucher-header .voucher-title {
    font-size: 1rem;
    font-weight: 700;
    margin: 0 0 2px 0;
}

.voucher-header .voucher-description {
    font-size: 0.8rem;
    color: var(--text-secondary);
    margin: 0;
    line-height: 1.4;
    display: -webkit-box;
    overflow: hidden;
}

/* Details Section */
.details-section {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    padding-top: 8px;
    border-top: 1px dashed var(--border-color);
}

.detail-item {
    font-size: 0.7rem;
    font-weight: 600;
    color: var(--text-secondary);
    background: var(--bg-page);
    padding: 3px 8px;
    border-radius: var(--radius-full);
    border: 1px solid var(--border-color);
}

.status-badge {
    color: white;
    font-weight: 700;
}

.status-badge.active {
    background-color: var(--c-success);
    border-color: var(--c-success);
}

.status-badge.exhausted,
.status-badge.expired {
    background-color: var(--text-tertiary);
    border-color: var(--text-tertiary);
}

/* Footer */
.voucher-footer {
    margin-top: auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 8px;
    border-top: 1px dashed var(--border-color);
}

.countdown {
    font-size: 0.75rem;
    color: var(--text-secondary);
    font-weight: 500;
}

.btn-use {
    border: none;
    color: white;
    padding: 6px 14px;
    border-radius: var(--radius-full);
    font-size: 0.8rem;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s ease;
}

.ticket .bg-percent+.ticket-separator+.ticket-right .btn-use {
    background-color: var(--c-percent-end);
}

.ticket .bg-direct+.ticket-separator+.ticket-right .btn-use {
    background-color: var(--c-direct-start);
}

.btn-use:hover:not(:disabled) {
    transform: scale(1.05);
}

.btn-use:disabled {
    background: var(--c-disabled-bg);
    color: var(--c-disabled-text);
    cursor: not-allowed;
    transform: none;
}

@media (prefers-color-scheme: dark) {
    .btn-use:disabled {
        background-color: var(--border-dark);
        color: var(--text-tertiary);
    }
}

/* State Modifiers */
.ticket.exhausted,
.ticket.expired,
.ticket.unknown {
    opacity: 1; /* Was 0.75, set to 1 to remove global dimming */
}

.ticket.exhausted:hover,
.ticket.expired:hover,
.ticket.unknown:hover {
    transform: none;
    box-shadow: 0 4px 10px var(--shadow-color);
}
</style>
