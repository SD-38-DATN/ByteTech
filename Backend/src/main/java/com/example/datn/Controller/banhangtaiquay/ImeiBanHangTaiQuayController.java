package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.Service.banhangtaiquay.ImeiBanHangTaiQuayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banhangtaiquay/imei")
@RequiredArgsConstructor
public class ImeiBanHangTaiQuayController {

    private final ImeiBanHangTaiQuayService imeiBanHangTaiQuayService;

    // Tìm sản phẩm theo IMEI chính xác (đã kiểm tra)
    @GetMapping("/tim-san-pham/{imei}")
    public ResponseEntity<?> timSanPhamTheoImei(@PathVariable String imei) {
        try {
            var imeiResponse = imeiBanHangTaiQuayService.timSanPhamTheoImei(imei);
            if (imeiResponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy sản phẩm với IMEI: " + imei);
            }
            return ResponseEntity.ok(imeiResponse);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.badRequest().body(ex.getReason());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi hệ thống khi tìm IMEI: " + imei);
        }
    }

    // Lấy danh sách IMEI theo SKU (hỗ trợ cả sản phẩm và phụ kiện) hiên hiện tại không dùng đanh hoo
    @GetMapping("/lookup-by-sku/{sku}")
    public ResponseEntity<Object> lookupImeisBySku(@PathVariable String sku) {

        try {
            var imeiList = imeiBanHangTaiQuayService.lookupImeisBySku(sku);
            if (imeiList == null || imeiList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(imeiList);
        } catch (Exception e) {
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
                return ResponseEntity.ok(Map.of(
                        "message", "Đã cập nhật " + count + " IMEI thành " + statusText,
                        "status", status,
                        "count", count
                ));
            } else {

                return ResponseEntity.badRequest().body("Không thể cập nhật trạng thái IMEI");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật trạng thái IMEI: " + e.getMessage());
        }
    }

    // Helper method để lấy text của status
    private String getStatusText(int status) {
        switch (status) {
            case 0:
                return "thanh toán";
            case 1:
                return "còn hàng";
            case 5:
                return "tạm giữ";
            default:
                return "không xác định";
        }
    }
}