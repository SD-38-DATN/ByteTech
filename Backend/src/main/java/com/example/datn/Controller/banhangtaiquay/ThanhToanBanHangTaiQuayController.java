package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.ChotDonRequest;
import com.example.datn.DTO.banhangtaiquay.DonHangBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.LuuDonRequest;
import com.example.datn.Repository.banhangtaiquay.UsersBanHangTaiQuayRepository;
import com.example.datn.Service.banhangtaiquay.ThanhToanBanHangTaiQuayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/banhangtaiquay/thanh-toan")
@RequiredArgsConstructor
public class ThanhToanBanHangTaiQuayController {

    private final ThanhToanBanHangTaiQuayService donHangService;
    private final UsersBanHangTaiQuayRepository usersRepository;

    /**
     * Lưu đơn hàng với trạng thái = 4
     * IMEI status = 5 (tạm giữ)
     */
    @PostMapping("/luu-don")
    public ResponseEntity<?> luuDon(@RequestBody LuuDonRequest request) {
        try {
            DonHangBanHangTaiQuayDTO result = donHangService.luuDon(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi lưu đơn hàng");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("details", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(errorResponse);
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