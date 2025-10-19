package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.DTO.dtoBanHangTaiQuay.*;
import com.example.datn.Service.banhangtaiquay.DonHangBanHangTaiQuayService;
import com.example.datn.Repository.BanHangTaiQuay.UsersBanHangTaiQuayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/banhangtaiquay/thanh-toan")
@RequiredArgsConstructor
public class ThanhToanBanHangTaiQuayController {
    
    private final DonHangBanHangTaiQuayService donHangService;
    private final UsersBanHangTaiQuayRepository usersRepository;
    
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
    
    /**
     * ✅ MỚI: Lấy User ID từ username
     * Giải quyết vấn đề hardcode ID = 1
     */
    @GetMapping("/get-user-id")
    public ResponseEntity<Map<String, Object>> getUserIdByUsername(@RequestParam String username) {
        try {
            System.out.println("🔍 DEBUG: Tìm User ID cho username: " + username);
            
            var user = usersRepository.findByUsername(username);
            if (user.isEmpty()) {
                System.out.println("❌ DEBUG: Không tìm thấy user với username: " + username);
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.get().getId());
            response.put("username", user.get().getUsername());
            response.put("email", user.get().getEmail());
            
            System.out.println("✅ DEBUG: Tìm thấy user - ID: " + user.get().getId() + ", Username: " + user.get().getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: Lỗi khi tìm User ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}