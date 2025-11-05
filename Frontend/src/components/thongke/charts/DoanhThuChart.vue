<template>
  <div class="doanh-thu-chart-container">
    <div v-if="loading" class="chart-loading">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
    </div>
    <div v-else-if="error" class="chart-error">
      <p class="text-danger mb-0">{{ error }}</p>
    </div>
    <div v-else class="chart-wrapper">
      <Line :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { Line } from "vue-chartjs";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from "chart.js";
import { getDoanhThuChartData } from "../../../service/api";

// Đăng ký các component của Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

// Props nhận từ component cha (filter values)
const props = defineProps({
  filterParams: {
    type: Object,
    default: () => ({}),
  },
});

// State
const loading = ref(false);
const error = ref(null);
const chartData = ref({
  labels: [],
  datasets: [
    {
      label: "Doanh Thu",
      data: [],
      borderColor: "#0d6efd",
      backgroundColor: "rgba(13, 110, 253, 0.1)",
      tension: 0.4,
      fill: true,
    },
  ],
});

// Helper function để kiểm tra xem label có phải là format ngày không (dd/mm/yyyy)
const isDateFormat = (label) => {
  if (!label) return false;
  // Format: dd/mm/yyyy (ví dụ: 01/11/2024)
  const datePattern = /^\d{2}\/\d{2}\/\d{4}$/;
  return datePattern.test(label);
};

// Chart options với styling đẹp hơn
const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 1500,
    easing: "easeInOutQuart",
  },
  interaction: {
    mode: "index",
    intersect: false,
  },
  plugins: {
    legend: {
      display: true,
      position: "top",
      labels: {
        usePointStyle: true,
        pointStyle: "circle",
        padding: 15,
        font: {
          size: 12,
          weight: "500",
          family: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
        },
        color: "#6c757d",
      },
    },
    tooltip: {
      enabled: true,
      backgroundColor: "rgba(0, 0, 0, 0.8)",
      padding: 12,
      titleFont: {
        size: 13,
        weight: "600",
        family: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
      },
      bodyFont: {
        size: 13,
        weight: "500",
        family: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
      },
      titleColor: "#ffffff",
      bodyColor: "#ffffff",
      borderColor: "rgba(255, 255, 255, 0.1)",
      borderWidth: 1,
      cornerRadius: 8,
      displayColors: true,
      usePointStyle: true,
      callbacks: {
        title: function (context) {
          return context[0].label || "";
        },
        label: function (context) {
          return (
            "Doanh Thu: " +
            new Intl.NumberFormat("vi-VN", {
              style: "currency",
              currency: "VND",
            }).format(context.parsed.y)
          );
        },
      },
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        color: "#6c757d",
        font: {
          size: 11,
          weight: "500",
          family: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
        },
        padding: 8,
        callback: function (value) {
          return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
            notation: "compact",
          }).format(value);
        },
      },
      grid: {
        color: "rgba(0, 0, 0, 0.06)",
        drawBorder: false,
        lineWidth: 1,
      },
      border: {
        display: false,
      },
    },
    x: {
      ticks: {
        color: "#6c757d",
        font: {
          size: 11,
          weight: "500",
          family: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
        },
        maxRotation: 45,
        minRotation: 45,
        padding: 8,
        // Chỉ hiển thị một số ticks nhất định khi là chế độ ngày
        callback: function(value, index, ticks) {
          const label = this.getLabelForValue(value);
          
          // Kiểm tra xem label có phải format ngày không
          const isDate = isDateFormat(label);
          
          // Nếu không phải chế độ ngày, hiển thị tất cả
          if (!isDate) {
            return label;
          }
          
          const totalLabels = ticks.length;
          
          // Nếu ít hơn 8 labels, hiển thị tất cả
          if (totalLabels <= 8) {
            return label;
          }
          
          // Luôn hiển thị tick đầu tiên (index 0)
          if (index === 0) {
            return label;
          }
          
          // Luôn hiển thị tick cuối cùng
          if (index === totalLabels - 1) {
            return label;
          }
          
          // Hiển thị tick ở giữa
          if (index === Math.floor(totalLabels / 2)) {
            return label;
          }
          
          // Hiển thị một số ticks ở giữa cách đều
          // Tính số bước để hiển thị khoảng 5-6 ticks ở giữa
          const step = Math.ceil(totalLabels / 6);
          
          // Hiển thị ticks ở các vị trí cách đều
          if (index % step === 0) {
            return label;
          }
          
          // Không hiển thị tick này
          return "";
        },
        maxTicksLimit: 10, // Giới hạn số lượng ticks tối đa
      },
      grid: {
        display: false,
      },
      border: {
        display: false,
      },
    },
  },
});

// Hàm fetch dữ liệu biểu đồ
const fetchChartData = async () => {
  loading.value = true;
  error.value = null;

  try {
    // Chuẩn bị params (loại bỏ null/empty)
    const params = { ...props.filterParams };
    Object.keys(params).forEach((key) => {
      if (
        params[key] === null ||
        params[key] === "" ||
        params[key] === undefined
      ) {
        delete params[key];
      }
    });

    // Gọi API
    const data = await getDoanhThuChartData(params);

    if (data && data.data && Array.isArray(data.data) && data.data.length > 0) {
      // Extract labels và values từ format mới
      const labels = [];
      const values = [];

      data.data.forEach((item) => {
        // Xác định label dựa trên date/month/hour
        if (item.date) {
          // Format ngày: "2025-11-01" -> "01/11/2025"
          const dateParts = item.date.split("-");
          labels.push(`${dateParts[2]}/${dateParts[1]}/${dateParts[0]}`);
        } else if (item.month) {
          // Format tháng: "2025-01" -> "Tháng 1/2025"
          const monthParts = item.month.split("-");
          const monthNum = parseInt(monthParts[1]);
          labels.push(`Tháng ${monthNum}/${monthParts[0]}`);
        } else if (item.hour !== undefined && item.hour !== null) {
          // Format giờ: "00" -> "00:00"
          labels.push(`${item.hour}:00`);
        }

        values.push(item.value || 0);
      });

      chartData.value = {
        labels: labels,
        datasets: [
          {
            label: "Doanh Thu",
            data: values,
            borderColor: "#3b82f6",
            backgroundColor: "rgba(59, 130, 246, 0.15)",
            tension: 0.5,
            fill: true,
            pointRadius: 5,
            pointHoverRadius: 8,
            pointBackgroundColor: "#ffffff",
            pointBorderColor: "#3b82f6",
            pointBorderWidth: 3,
            pointHoverBackgroundColor: "#3b82f6",
            pointHoverBorderColor: "#ffffff",
            pointHoverBorderWidth: 3,
            borderWidth: 3,
            stepped: false,
            spanGaps: false,
          },
        ],
      };
    } else {
      // Nếu không có dữ liệu, hiển thị biểu đồ trống
      chartData.value = {
        labels: [],
        datasets: [
          {
            label: "Doanh Thu",
            data: [],
            borderColor: "#0d6efd",
            backgroundColor: "rgba(13, 110, 253, 0.1)",
            tension: 0.4,
            fill: true,
          },
        ],
      };
    }
  } catch (err) {
    console.error("Lỗi khi lấy dữ liệu biểu đồ:", err);
    error.value = "Không thể tải dữ liệu biểu đồ";
  } finally {
    loading.value = false;
  }
};

// Watch filter params để tự động cập nhật biểu đồ
watch(
  () => props.filterParams,
  () => {
    fetchChartData();
  },
  { deep: true }
);

// Load dữ liệu khi component mount
onMounted(() => {
  fetchChartData();
});
</script>

<style scoped>
.doanh-thu-chart-container {
  height: 280px;
  position: relative;
  padding: 10px 5px;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0.98), rgba(255, 255, 255, 1));
  border-radius: 8px;
}

.chart-wrapper {
  height: 100%;
  width: 100%;
  position: relative;
}

.chart-loading,
.chart-error {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 250px;
}

.chart-error {
  color: #dc3545;
  font-size: 14px;
  font-weight: 500;
}

/* Cải thiện styling cho chart canvas */
.doanh-thu-chart-container :deep(canvas) {
  max-height: 100% !important;
  width: 100% !important;
}

/* Tùy chỉnh scrollbar nếu cần */
.doanh-thu-chart-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.doanh-thu-chart-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.doanh-thu-chart-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.doanh-thu-chart-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Thêm hiệu ứng hover cho chart container */
.doanh-thu-chart-container:hover {
  background: linear-gradient(to bottom, rgba(255, 255, 255, 1), rgba(248, 250, 252, 1));
  transition: background 0.3s ease;
}
</style>
