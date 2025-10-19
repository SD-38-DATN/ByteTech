package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.Service.banhangtaiquay.ImeiBanHangTaiQuayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banhangtaiquay/imei")
@RequiredArgsConstructor
public class ImeiBanHangTaiQuayController {
    
    private final ImeiBanHangTaiQuayService imeiBanHangTaiQuayService;
    
    // Tìm sản phẩm theo IMEI chính xác
    @GetMapping("/tim-san-pham/{imei}")
    public ResponseEntity<Object> timSanPhamTheoImei(@PathVariable String imei) {
        System.out.println("🔍 Controller: timSanPhamTheoImei được gọi với IMEI: " + imei);
        try {
            var imeiResponse = imeiBanHangTaiQuayService.timSanPhamTheoImei(imei);
            if (imeiResponse == null) {
                System.out.println("❌ Controller: Không tìm thấy IMEI: " + imei);
                return ResponseEntity.noContent().build();
            }
            System.out.println("✅ Controller: Tìm thấy IMEI: " + imei);
            return ResponseEntity.ok(imeiResponse);
        } catch (Exception e) {
            System.out.println("❌ Controller: Lỗi khi tìm IMEI: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi khi tìm sản phẩm theo IMEI: " + e.getMessage());
        }
    }

    // Lấy danh sách IMEI theo SKU (hỗ trợ cả sản phẩm và phụ kiện)
    @GetMapping("/lookup-by-sku/{sku}")
    public ResponseEntity<Object> lookupImeisBySku(@PathVariable String sku) {
        System.out.println("🔍 Controller: lookupImeisBySku được gọi với SKU: " + sku);
        try {
            var imeiList = imeiBanHangTaiQuayService.lookupImeisBySku(sku);
            if (imeiList == null || imeiList.isEmpty()) {
                System.out.println("❌ Controller: Không tìm thấy IMEI cho SKU: " + sku);
                return ResponseEntity.noContent().build();
            }
            System.out.println("✅ Controller: Tìm thấy " + imeiList.size() + " IMEI cho SKU: " + sku);
            return ResponseEntity.ok(imeiList);
        } catch (Exception e) {
            System.out.println("❌ Controller: Lỗi khi lookup IMEI: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi khi tìm IMEI theo SKU: " + e.getMessage());
        }
    }

    // ===== UNIFIED API ENDPOINTS (TỐI ƯU NHẤT) =====
    
    // API thống nhất: Cập nhật trạng thái IMEI (1 hoặc nhiều)
    @PutMapping("/update-status")
    public ResponseEntity<Object> updateImeiStatus(
            @RequestBody Map<String, Object> request) {
        
        String imei = (String) request.get("imei");
        List<String> imeiList = (List<String>) request.get("imeiList");
        Integer status = (Integer) request.get("status");
        
        // Validate status
        if (status == null || (status != 0 && status != 1 && status != 5)) {
            return ResponseEntity.badRequest().body("Status phải là 0 (thanh toán), 1 (còn hàng), hoặc 5 (tạm giữ)");
        }
        
        String statusText = getStatusText(status);
        System.out.println("🔄 Controller: Cập nhật trạng thái IMEI → " + status + " (" + statusText + ")");
        
        try {
            boolean success;
            int count = 0;
            
            if (imei != null && !imei.isEmpty()) {
                // Cập nhật 1 IMEI
                success = imeiBanHangTaiQuayService.capNhatTrangThaiImei(imei, status);
                count = 1;
            } else if (imeiList != null && !imeiList.isEmpty()) {
                // Cập nhật nhiều IMEI
                success = imeiBanHangTaiQuayService.capNhatTrangThaiNhieuImei(imeiList, status);
                count = imeiList.size();
            } else {
                return ResponseEntity.badRequest().body("Phải cung cấp 'imei' hoặc 'imeiList'");
            }
            
            if (success) {
                System.out.println("✅ Controller: Đã cập nhật " + count + " IMEI → " + status + " (" + statusText + ")");
                return ResponseEntity.ok(Map.of(
                    "message", "Đã cập nhật " + count + " IMEI thành " + statusText,
                    "status", status,
                    "count", count
                ));
            } else {
                System.out.println("❌ Controller: Không thể cập nhật IMEI");
                return ResponseEntity.badRequest().body("Không thể cập nhật trạng thái IMEI");
            }
        } catch (Exception e) {
            System.out.println("❌ Controller: Lỗi khi cập nhật trạng thái IMEI: " + e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật trạng thái IMEI: " + e.getMessage());
        }
    }
    
    // Helper method để lấy text của status
    private String getStatusText(int status) {
        switch (status) {
            case 0: return "thanh toán";
            case 1: return "còn hàng";
            case 5: return "tạm giữ";
            default: return "không xác định";
        }
    }
}