package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.DTO.dtoBanHangTaiQuay.*;
import com.example.datn.Service.banhangtaiquay.DonHangBanHangTaiQuayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banhangtaiquay/thanh-toan")
@RequiredArgsConstructor
public class ThanhToanBanHangTaiQuayController {
    
    private final DonHangBanHangTaiQuayService donHangService;
    
    /**
     * Lưu đơn hàng với trạng thái = 4
     * IMEI status = 5 (tạm giữ)
     */
    @PostMapping("/luu-don")
    public ResponseEntity<DonHangBanHangTaiQuayDTO> luuDon(@RequestBody LuuDonRequest request) {
        try {
            System.out.println("🔍 DEBUG Controller: LuuDonRequest = " + request);
            System.out.println("🔍 DEBUG Controller: UserId = " + request.getUserId());
            System.out.println("🔍 DEBUG Controller: TongTien = " + request.getTongTien());
            System.out.println("🔍 DEBUG Controller: ChiTietDonHangs size = " + (request.getChiTietDonHangs() != null ? request.getChiTietDonHangs().size() : "null"));
            
            DonHangBanHangTaiQuayDTO result = donHangService.luuDon(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("❌ ERROR in luuDon: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Thanh toán đơn hàng - Trừ số lượng tồn, cập nhật trạng thái "Đã thanh toán"
     * IMEI status = 0 (đã bán)
     */
    @PostMapping("/thanh-toan")
    public ResponseEntity<DonHangBanHangTaiQuayDTO> thanhToan(@RequestBody ChotDonRequest request) {
        try {
            DonHangBanHangTaiQuayDTO result = donHangService.thanhToan(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}