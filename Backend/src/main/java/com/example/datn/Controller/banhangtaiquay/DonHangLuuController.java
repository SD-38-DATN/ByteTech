package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.DonHangLuuDTO;
import com.example.datn.Service.banhangtaiquay.DonHangLuuService;
import com.example.datn.Service.thongke.ThongKeChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banhangtaiquay/don-hang-luu")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DonHangLuuController {

    private final DonHangLuuService donHangLuuService;
    private final ThongKeChartService ThongKeChartService;

    /**
     * Lấy danh sách đơn hàng đã lưu
     *
     * @param userId - ID của user (optional, null để lấy tất cả)
     * @return ResponseEntity<List<DonHangLuuDTO>> - Danh sách đơn hàng đã lưu
     */
    @GetMapping
    public ResponseEntity<List<DonHangLuuDTO>> getDonHangLuu(
            @RequestParam(required = false) Integer userId) {

        try {
            List<DonHangLuuDTO> donHangs = donHangLuuService.getDonHangLuu(userId);

            return ResponseEntity.ok(donHangs);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lấy chi tiết đơn hàng đã lưu theo mã đơn hàng
     *
     * @param maDonHang - Mã đơn hàng
     * @return ResponseEntity<DonHangLuuDTO> - Chi tiết đơn hàng
     */
    @GetMapping("/{maDonHang}")
    public ResponseEntity<DonHangLuuDTO> getDonHangLuuByMaDonHang(
            @PathVariable Integer maDonHang) {

        try {
            DonHangLuuDTO donHang = donHangLuuService.getDonHangLuuByMaDonHang(maDonHang);

            if (donHang == null) {

                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(donHang);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Đếm số lượng đơn hàng đã lưu
     *
     * @param userId - ID của user (optional, null để đếm tất cả)
     * @return ResponseEntity<Long> - Số lượng đơn hàng
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDonHangLuu(
            @RequestParam(required = false) Integer userId) {

        try {
            Long count = donHangLuuService.countDonHangLuu(userId);

            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lấy danh sách đơn hàng đã lưu của user hiện tại
     *
     * @param userId - ID của user hiện tại
     * @return ResponseEntity<List<DonHangLuuDTO>> - Danh sách đơn hàng của user
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<DonHangLuuDTO>> getMyDonHangLuu(
            @RequestParam Integer userId) {

        try {
            List<DonHangLuuDTO> donHangs = donHangLuuService.getDonHangLuu(userId);
            return ResponseEntity.ok(donHangs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Xóa đơn hàng đã lưu (chuyển trạng thái 5 → 3 và IMEI về 1)
     *
     * @param maDonHang - Mã đơn hàng
     * @return ResponseEntity - Kết quả xóa
     */
    @DeleteMapping("/{maDonHang}")
    public ResponseEntity<String> xoaDonHangLuu(@PathVariable Integer maDonHang) {

        try {
            donHangLuuService.xoaDonHangLuu(maDonHang);
            return ResponseEntity.ok("Xóa đơn hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Có lỗi xảy ra khi xóa đơn hàng: " + e.getMessage());
        }
    }
}
