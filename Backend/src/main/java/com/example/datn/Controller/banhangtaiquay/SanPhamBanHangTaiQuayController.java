package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.SanPhamBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.ImeiBanHangTaiQuayDTO;
import com.example.datn.Service.banhangtaiquay.SanPhamBanHangTaiQuayService;
import com.example.datn.Service.banhangtaiquay.ImeiBanHangTaiQuayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banhangtaiquay/sanpham")
@RequiredArgsConstructor
public class SanPhamBanHangTaiQuayController {

    private final SanPhamBanHangTaiQuayService sanPhamBanHangTaiQuayService;
    private final ImeiBanHangTaiQuayService imeiBanHangTaiQuayService;

    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("SanPhamBanHangTaiQuayController hoạt động bình thường!");
    }

    // Tìm kiếm sản phẩm (hỗ trợ cả SKU và IMEI)
    @GetMapping("/search-sku")
    public ResponseEntity<List<Object>> searchSanPham(@RequestParam String keyword) {
        System.out.println("🔍 Controller: searchSanPham được gọi với keyword: " + keyword);
        try {
            List<Object> sanPhamList = sanPhamBanHangTaiQuayService.timKiemTheoImeiHoacSKU(keyword);
            System.out.println("✅ Controller: Trả về " + (sanPhamList != null ? sanPhamList.size() : "null") + " sản phẩm");
            return ResponseEntity.ok(sanPhamList);
        } catch (Exception e) {
            System.err.println("❌ Controller: Lỗi trong searchSanPham: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ MỚI: Tìm kiếm sản phẩm CHỈ theo SKU (không tìm IMEI)
    @GetMapping("/search-sku-only")
    public ResponseEntity<List<Object>> searchSanPhamBySKUOnly(@RequestParam String sku) {
        System.out.println("🔍 Controller: searchSanPhamBySKUOnly được gọi với SKU: " + sku);
        try {
            List<Object> sanPhamList = sanPhamBanHangTaiQuayService.timKiemSanPhamVaPhuKien(sku);
            System.out.println("✅ Controller: Trả về " + (sanPhamList != null ? sanPhamList.size() : "null") + " sản phẩm (chỉ SKU)");
            if (sanPhamList != null && !sanPhamList.isEmpty()) {
                System.out.println("🔍 Controller: Chi tiết kết quả:");
                for (int i = 0; i < sanPhamList.size(); i++) {
                    System.out.println("  - Sản phẩm " + (i+1) + ": " + sanPhamList.get(i));
                }
            } else {
                System.out.println("❌ Controller: Không tìm thấy sản phẩm nào với SKU: " + sku);
            }
            return ResponseEntity.ok(sanPhamList);
        } catch (Exception e) {
            System.err.println("❌ Controller: Lỗi trong searchSanPhamBySKUOnly: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Tìm kiếm cả sản phẩm và phụ kiện
    @GetMapping("/search-all")
    public ResponseEntity<List<Object>> searchSanPhamVaPhuKien(@RequestParam String keyword) {
        System.out.println("🔍 Controller: searchSanPhamVaPhuKien được gọi với keyword: " + keyword);
        try {
            List<Object> result = sanPhamBanHangTaiQuayService.timKiemSanPhamVaPhuKien(keyword);
            System.out.println("✅ Controller: Trả về " + result.size() + " kết quả (sản phẩm + phụ kiện)");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("❌ Controller: Lỗi trong searchSanPhamVaPhuKien: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Tìm kiếm kết hợp SKU + IMEI
    @GetMapping("/search-combined")
    public ResponseEntity<List<SanPhamBanHangTaiQuayDTO>> searchCombined(
            @RequestParam String sku,
            @RequestParam String imei) {
        List<SanPhamBanHangTaiQuayDTO> sanPhamList = sanPhamBanHangTaiQuayService.timKiemKetHop(sku, imei);
        return ResponseEntity.ok(sanPhamList);
    }

    // Lấy list IMEI của sản phẩm theo mã SKU
    @GetMapping("/{maSKU}/imei")
    public ResponseEntity<List<ImeiBanHangTaiQuayDTO>> getImeiListBySku(@PathVariable String maSKU) {
        System.out.println("🔍 Controller: API được gọi với SKU: " + maSKU);
        List<ImeiBanHangTaiQuayDTO> imeiList = imeiBanHangTaiQuayService.getImeiKhachDung(maSKU);
        System.out.println("🔍 Controller: Trả về " + (imeiList != null ? imeiList.size() : "null") + " IMEI items");
        return ResponseEntity.ok(imeiList);
    }

    // Lấy list IMEI của sản phẩm theo mã SKU với filter IMEI
    @GetMapping("/{maSKU}/imei/search")
    public ResponseEntity<List<ImeiBanHangTaiQuayDTO>> getImeiListBySkuWithFilter(
            @PathVariable String maSKU,
            @RequestParam(required = false) String imei) {
        List<ImeiBanHangTaiQuayDTO> imeiList = imeiBanHangTaiQuayService.getImeiListBySkuMaBT(maSKU, imei);
        return ResponseEntity.ok(imeiList);
    }
}