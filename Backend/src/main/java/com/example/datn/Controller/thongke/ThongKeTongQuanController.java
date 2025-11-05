package com.example.datn.Controller.thongke;

import com.example.datn.DTO.thongke.ThongKeChartDoanhThuDTO;
import com.example.datn.DTO.thongke.ThongKeFilterDTO;
import com.example.datn.DTO.thongke.ThongKeTongQuanDTO;
import com.example.datn.Service.thongke.ThongKeChartService;
import com.example.datn.Service.thongke.ThongKeToangQuanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/thongke/tong-quan")
@RequiredArgsConstructor
public class ThongKeTongQuanController {

    private final ThongKeToangQuanService thongKeService;
    private final ThongKeChartService thongKeChartService;

    /**
     * API lấy tổng doanh thu từ tất cả đơn hàng có trạng thái = 1
     *
     * @return Tổng doanh thu, số lượng sản phẩm đã bán và số lượng đơn hàng
     */
    @GetMapping("/tong-doanh-thu")
    public ResponseEntity<ThongKeTongQuanDTO> getTongDoanhThu() {
        return ResponseEntity.ok(thongKeService.getThongKeTongQuan());
    }

    /**
     * API lấy tổng doanh thu theo năm hiện tại từ đơn hàng có trạng thái = 1
     *
     * @return Tổng doanh thu năm hiện tại, số lượng sản phẩm đã bán và số lượng đơn hàng
     */
    @GetMapping("/doanh-thu-nam-hien-tai")
    public ResponseEntity<ThongKeTongQuanDTO> getDoanhThuTheoNamHienTai() {
        return ResponseEntity.ok(thongKeService.getThongKeTheoNam());
    }

    /**
     * API lấy danh sách các năm có đơn hàng
     *
     * @return Danh sách năm
     */
    @GetMapping("/danh-sach-nam")
    public ResponseEntity<List<Integer>> getDanhSachNam() {
        return ResponseEntity.ok(thongKeService.getDanhSachNam());
    }

    /**
     * API lấy thống kê với filter
     *
     * @param dayWeekFilter "today", "yesterday", "this-week", "last-week" hoặc null
     * @param month         Tháng (1-12) hoặc null
     * @param year          Năm hoặc null
     * @param dateFrom      Ngày bắt đầu (yyyy-MM-dd) hoặc null
     * @param dateTo        Ngày kết thúc (yyyy-MM-dd) hoặc null
     * @return Thống kê đã lọc
     */
    @GetMapping("/filter")
    public ResponseEntity<ThongKeFilterDTO> getThongKeFilter(
            @RequestParam(required = false) String dayWeekFilter,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo
    ) {
        return ResponseEntity.ok(thongKeService.getThongKeFilter(
                dayWeekFilter,
                month,
                year,
                dateFrom,
                dateTo
        ));
    }

    /**
     * API lấy dữ liệu biểu đồ doanh thu theo filter
     * Chỉ lấy đơn hàng có trạng thái = 2 (Đã giao hàng)
     *
     * @param dayWeekFilter "today", "yesterday", "this-week", "last-week" hoặc null
     * @param month         Tháng (1-12) hoặc null
     * @param year          Năm hoặc null
     * @param dateFrom      Ngày bắt đầu (yyyy-MM-dd) hoặc null
     * @param dateTo        Ngày kết thúc (yyyy-MM-dd) hoặc null
     * @return DTO chứa labels (ngày) và values (doanh thu) để vẽ biểu đồ
     */
    @GetMapping("/chart-doanh-thu")
    public ResponseEntity<ThongKeChartDoanhThuDTO> getChartDoanhThu(
            @RequestParam(required = false) String dayWeekFilter,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo
    ) {
        return ResponseEntity.ok(thongKeChartService.getDoanhThuChartData(
                dayWeekFilter,
                month,
                year,
                dateFrom,
                dateTo
        ));
    }
}

