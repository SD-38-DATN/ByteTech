package com.example.datn.Service.thongke;

import com.example.datn.DTO.thongke.ThongKeChartDoanhThuDTO;
import com.example.datn.Repository.thongke.ThongKeDonHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongKeChartService {

    private final ThongKeDonHangRepository thongKeRepository;


    /**
     * Lấy dữ liệu doanh thu để vẽ biểu đồ
     * - Nếu có dayWeekFilter=today: trả về theo giờ
     * - Nếu chỉ có year (không có month): trả về theo 12 tháng
     * - Nếu có month và year: trả về theo ngày trong tháng
     * - Các trường hợp khác: trả về theo ngày
     *
     * @param dayWeekFilter "today", "yesterday", "this-week", "last-week" hoặc null
     * @param month         Tháng (1-12) hoặc null
     * @param year          Năm hoặc null
     * @param dateFrom      Ngày bắt đầu (yyyy-MM-dd) hoặc null
     * @param dateTo        Ngày kết thúc (yyyy-MM-dd) hoặc null
     * @return DTO chứa labels và values (doanh thu)
     */
    public ThongKeChartDoanhThuDTO getDoanhThuChartData(
            String dayWeekFilter,
            Integer month,
            Integer year,
            String dateFrom,
            String dateTo
    ) {
        String finalDateFrom = null;
        String finalDateTo = null;
        Integer finalYear = year;
        Integer finalMonth = month;

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Xử lý dayWeekFilter (giống như getThongKeFilter)
        // Ưu tiên: nếu có dayWeekFilter=today và month cùng lúc, ưu tiên today (theo giờ)
        boolean isToday = false;
        boolean hasDayWeekFilter = false;
        if (dayWeekFilter != null && !dayWeekFilter.isEmpty()) {
            hasDayWeekFilter = true;
            switch (dayWeekFilter) {
                case "today":
                    finalDateFrom = now.format(formatter);
                    finalDateTo = now.format(formatter);
                    isToday = true;
                    // Bỏ qua month/year khi có today
                    finalMonth = null;
                    finalYear = null;
                    break;
                case "yesterday":
                    LocalDate yesterday = now.minusDays(1);
                    finalDateFrom = yesterday.format(formatter);
                    finalDateTo = yesterday.format(formatter);
                    break;
                case "this-week":
                    LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
                    finalDateFrom = startOfWeek.format(formatter);
                    finalDateTo = now.format(formatter);
                    break;
                case "last-week":
                    LocalDate lastWeekStart = now.minusDays(now.getDayOfWeek().getValue() + 6);
                    LocalDate lastWeekEnd = now.minusDays(now.getDayOfWeek().getValue());
                    finalDateFrom = lastWeekStart.format(formatter);
                    finalDateTo = lastWeekEnd.format(formatter);
                    break;
            }
        }

        List<Object[]> result;

        // Trường hợp 1: dayWeekFilter=today -> trả về theo giờ (ưu tiên cao nhất)
        if (isToday) {
            result = thongKeRepository.getDoanhThuChartDataTheoGio(
                    finalDateFrom,
                    finalDateTo
            );

            // Tạo map từ giờ -> doanh thu từ kết quả query
            java.util.Map<String, Double> hourRevenueMap = new java.util.HashMap<>();
            if (result != null && !result.isEmpty()) {
                for (Object[] row : result) {
                    String hour = (String) row[0];
                    BigDecimal doanhThu = (BigDecimal) row[1];
                    hourRevenueMap.put(hour, doanhThu != null ? doanhThu.doubleValue() : 0.0);
                }
            }

            // Tạo list cho 24 giờ (00-23), nếu không có dữ liệu thì value = 0
            List<ThongKeChartDoanhThuDTO.ChartDataItem> data = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                String hour = String.format("%02d", i);
                Double value = hourRevenueMap.getOrDefault(hour, 0.0);
                data.add(new ThongKeChartDoanhThuDTO.ChartDataItem(null, null, hour, value));
            }

            return new ThongKeChartDoanhThuDTO(data);
        }

        // Trường hợp 2: Chỉ có year (không có month và không có dayWeekFilter) -> trả về theo 12 tháng
        if (year != null && month == null && finalDateFrom == null && finalDateTo == null && !hasDayWeekFilter) {
            result = thongKeRepository.getDoanhThuChartDataTheoThang(year);

            // Tạo map từ tháng -> doanh thu từ kết quả query
            java.util.Map<String, Double> monthRevenueMap = new java.util.HashMap<>();
            if (result != null && !result.isEmpty()) {
                for (Object[] row : result) {
                    String monthYear = (String) row[0]; // "2024-01"
                    BigDecimal doanhThu = (BigDecimal) row[1];
                    monthRevenueMap.put(monthYear, doanhThu != null ? doanhThu.doubleValue() : 0.0);
                }
            }

            // Tạo list cho 12 tháng, nếu không có dữ liệu thì value = 0
            List<ThongKeChartDoanhThuDTO.ChartDataItem> data = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                String monthYear = String.format("%d-%02d", year, i);
                Double value = monthRevenueMap.getOrDefault(monthYear, 0.0);
                data.add(new ThongKeChartDoanhThuDTO.ChartDataItem(null, monthYear, null, value));
            }

            return new ThongKeChartDoanhThuDTO(data);
        }

        // Trường hợp 3: Có month và year (không có dayWeekFilter) -> trả về theo ngày trong tháng
        // Kiểm tra trước khi query để tránh query không cần thiết
        if (finalYear != null && finalMonth != null && !hasDayWeekFilter) {
            result = thongKeRepository.getDoanhThuChartData(
                    null, // Bỏ qua dateFrom khi có month+year
                    null, // Bỏ qua dateTo khi có month+year
                    finalYear,
                    finalMonth
            );

            // Tạo map từ ngày -> doanh thu từ kết quả query
            java.util.Map<String, Double> dateRevenueMap = new java.util.HashMap<>();
            if (result != null && !result.isEmpty()) {
                for (Object[] row : result) {
                    String dateStr = (String) row[0];
                    BigDecimal doanhThu = (BigDecimal) row[1];
                    dateRevenueMap.put(dateStr, doanhThu != null ? doanhThu.doubleValue() : 0.0);
                }
            }

            // Tính số ngày trong tháng
            LocalDate firstDayOfMonth = LocalDate.of(finalYear, finalMonth, 1);
            int daysInMonth = firstDayOfMonth.lengthOfMonth();

            // Tạo list cho TẤT CẢ các ngày trong tháng, kể cả ngày không có doanh thu
            List<ThongKeChartDoanhThuDTO.ChartDataItem> data = new ArrayList<>();
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = LocalDate.of(finalYear, finalMonth, day);
                String dateStr = date.format(formatter);
                Double value = dateRevenueMap.getOrDefault(dateStr, 0.0); // Nếu không có trong map, value = 0
                data.add(new ThongKeChartDoanhThuDTO.ChartDataItem(dateStr, null, null, value));
            }

            return new ThongKeChartDoanhThuDTO(data);
        }

        // Các trường hợp khác: query với các tham số đã có
        result = thongKeRepository.getDoanhThuChartData(
                finalDateFrom,
                finalDateTo,
                finalYear,
                finalMonth
        );

        // Các trường hợp khác (dateFrom/dateTo, v.v.)
        // Tạo map từ ngày -> doanh thu từ kết quả query
        java.util.Map<String, Double> dateRevenueMap = new java.util.HashMap<>();
        if (result != null && !result.isEmpty()) {
            for (Object[] row : result) {
                String dateStr = (String) row[0];
                BigDecimal doanhThu = (BigDecimal) row[1];
                dateRevenueMap.put(dateStr, doanhThu != null ? doanhThu.doubleValue() : 0.0);
            }
        }

        // Nếu có dateFrom và dateTo, tạo list cho tất cả các ngày trong khoảng
        if (finalDateFrom != null && finalDateTo != null) {
            LocalDate startDate = LocalDate.parse(finalDateFrom, formatter);
            LocalDate endDate = LocalDate.parse(finalDateTo, formatter);

            List<ThongKeChartDoanhThuDTO.ChartDataItem> data = new ArrayList<>();
            LocalDate currentDate = startDate;

            // Tạo list cho tất cả các ngày từ startDate đến endDate
            while (!currentDate.isAfter(endDate)) {
                String dateStr = currentDate.format(formatter);
                Double value = dateRevenueMap.getOrDefault(dateStr, 0.0);
                data.add(new ThongKeChartDoanhThuDTO.ChartDataItem(dateStr, null, null, value));
                currentDate = currentDate.plusDays(1);
            }

            return new ThongKeChartDoanhThuDTO(data);
        }

        // Trường hợp không có dateFrom/dateTo, trả về những gì query trả về
        if (result == null || result.isEmpty()) {
            return new ThongKeChartDoanhThuDTO(new ArrayList<>());
        }

        // Format: "2024-11-01" -> {date: "2024-11-01", value: ...}
        List<ThongKeChartDoanhThuDTO.ChartDataItem> data = result.stream()
                .map(row -> {
                    String dateStr = (String) row[0]; // "2024-11-01"
                    BigDecimal doanhThu = (BigDecimal) row[1];
                    Double value = doanhThu != null ? doanhThu.doubleValue() : 0.0;
                    return new ThongKeChartDoanhThuDTO.ChartDataItem(dateStr, null, null, value);
                })
                .collect(Collectors.toList());

        return new ThongKeChartDoanhThuDTO(data);
    }
}

