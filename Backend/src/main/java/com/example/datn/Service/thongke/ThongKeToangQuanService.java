package com.example.datn.Service.thongke;

import com.example.datn.DTO.thongke.ThongKeFilterDTO;
import com.example.datn.DTO.thongke.ThongKeTongQuanDTO;
import com.example.datn.Repository.thongke.ThongKeDonHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThongKeToangQuanService {

    private final ThongKeDonHangRepository thongKeRepository;

    /**
     * Lấy thống kê tổng quan từ tất cả đơn hàng có trạng thái = 1
     *
     * @return DTO chứa tổng doanh thu, số lượng sản phẩm và số lượng đơn hàng
     */
    public ThongKeTongQuanDTO getThongKeTongQuan() {
        List<Object[]> result = thongKeRepository.getThongKeTongQuan();

        if (result == null || result.isEmpty()) {
            return new ThongKeTongQuanDTO(BigDecimal.ZERO, 0L, 0L);
        }

        Object[] row = result.get(0);
        BigDecimal tongDoanhThu = row[0] != null ? ((java.math.BigDecimal) row[0]) : BigDecimal.ZERO;
        Long soLuongSanPham = row[1] != null ? ((Number) row[1]).longValue() : 0L;
        Long soLuongDonHang = row[2] != null ? ((Number) row[2]).longValue() : 0L;

        return new ThongKeTongQuanDTO(tongDoanhThu, soLuongSanPham, soLuongDonHang);
    }

    /**
     * Lấy thống kê tổng quan theo năm hiện tại từ đơn hàng có trạng thái = 1
     *
     * @return DTO chứa tổng doanh thu, số lượng sản phẩm và số lượng đơn hàng
     */
    public ThongKeTongQuanDTO getThongKeTheoNam() {
        int currentYear = LocalDate.now().getYear();
        List<Object[]> result = thongKeRepository.getThongKeTheoNam(currentYear);

        if (result == null || result.isEmpty() || result.get(0) == null) {
            return new ThongKeTongQuanDTO(BigDecimal.ZERO, 0L, 0L);
        }

        Object[] row = result.get(0);
        BigDecimal tongDoanhThu = row[0] != null ? ((java.math.BigDecimal) row[0]) : BigDecimal.ZERO;
        Long soLuongSanPham = row[1] != null ? ((Number) row[1]).longValue() : 0L;
        Long soLuongDonHang = row[2] != null ? ((Number) row[2]).longValue() : 0L;

        return new ThongKeTongQuanDTO(tongDoanhThu, soLuongSanPham, soLuongDonHang);
    }

    /**
     * Lấy danh sách các năm có đơn hàng (từ năm sớm nhất đến năm hiện tại)
     *
     * @return Danh sách năm
     */
    public List<Integer> getDanhSachNam() {
        return thongKeRepository.getDanhSachNam();
    }

    /**
     * Lấy thống kê với filter theo các điều kiện
     *
     * @param dayWeekFilter "today", "yesterday", "this-week", "last-week" hoặc null
     * @param month         Tháng (1-12) hoặc null
     * @param year          Năm hoặc null
     * @param dateFrom      Ngày bắt đầu (yyyy-MM-dd) hoặc null
     * @param dateTo        Ngày kết thúc (yyyy-MM-dd) hoặc null
     * @return DTO chứa thống kê đã lọc
     */
    public ThongKeFilterDTO getThongKeFilter(
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

        // Xử lý dayWeekFilter
        if (dayWeekFilter != null && !dayWeekFilter.isEmpty()) {
            switch (dayWeekFilter) {
                case "today":
                    finalDateFrom = now.format(formatter);
                    finalDateTo = now.format(formatter);
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

        // Gọi repository
        List<Object[]> result = thongKeRepository.getThongKeFilter(
                finalDateFrom,
                finalDateTo,
                finalYear,
                finalMonth
        );

        if (result == null || result.isEmpty()) {
            return new ThongKeFilterDTO(BigDecimal.ZERO, 0L, 0L, 0L);
        }

        Object[] row = result.get(0);
        BigDecimal tongDoanhThu = row[0] != null ? ((java.math.BigDecimal) row[0]) : BigDecimal.ZERO;
        Long soLuongSanPham = row[1] != null ? ((Number) row[1]).longValue() : 0L;
        Long soLuongDonHangThanhCong = row[2] != null ? ((Number) row[2]).longValue() : 0L;
        Long soLuongDonHangHuy = row[3] != null ? ((Number) row[3]).longValue() : 0L;

        return new ThongKeFilterDTO(
                tongDoanhThu,
                soLuongSanPham,
                soLuongDonHangThanhCong,
                soLuongDonHangHuy
        );
    }
}

