package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.VoucherBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.VoucherValidateRequest;
import com.example.datn.DTO.banhangtaiquay.VoucherValidateResponse;
import com.example.datn.Service.banhangtaiquay.VoucherBanHangTaiQuayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banhangtaiquay/voucher")
@RequiredArgsConstructor
public class VoucherBanHangTaiQuayController {
    
    private final VoucherBanHangTaiQuayService voucherService;
    
    /**
     * Lấy danh sách voucher hợp lệ cho bán hàng tại quầy
     * Điều kiện:
     * 1. trangThai = 1
     * 2. Ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc
     * 3. dieuKienGiam > 0
     */
    @GetMapping("/valid")
    public ResponseEntity<List<VoucherBanHangTaiQuayDTO>> getValidVouchers() {
        try {
            List<VoucherBanHangTaiQuayDTO> vouchers = voucherService.getValidVouchers();
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Lấy danh sách voucher hợp lệ cho bán hàng tại quầy theo tổng tiền đơn hàng
     * Điều kiện:
     * 1. trangThai = 1
     * 2. Ngày hiện tại trong khoảng ngayBatDau và ngayKetThuc
     * 3. dieuKienGiam > 0
     * 4. dieuKienGiam <= tongTienDonHang
     */
    @GetMapping("/valid-by-amount")
    public ResponseEntity<List<VoucherBanHangTaiQuayDTO>> getValidVouchersByAmount(
            @RequestParam("tongTienDonHang") java.math.BigDecimal tongTienDonHang) {
        try {
            List<VoucherBanHangTaiQuayDTO> vouchers = voucherService.getValidVouchersByAmount(tongTienDonHang);
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Kiểm tra voucher có hợp lệ với đơn hàng không
     * Điều kiện:
     * 1. Voucher phải hợp lệ (trangThai = 1, trong thời gian, dieuKienGiam > 0)
     * 2. Tổng tiền đơn hàng >= dieuKienGiam
     * 3. Tính toán số tiền giảm
     */
    @PostMapping("/validate")
    public ResponseEntity<VoucherValidateResponse> validateVoucher(@RequestBody VoucherValidateRequest request) {
        try {
            VoucherValidateResponse response = voucherService.validateVoucher(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(VoucherValidateResponse.error("Lỗi server: " + e.getMessage()));
        }
    }
}