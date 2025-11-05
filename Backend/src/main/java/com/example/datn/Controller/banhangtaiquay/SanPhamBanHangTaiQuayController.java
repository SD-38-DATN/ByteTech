package com.example.datn.Controller.banhangtaiquay;

import com.example.datn.DTO.banhangtaiquay.ImeiBanHangTaiQuayDTO;
import com.example.datn.DTO.banhangtaiquay.SanPhamBanHangTaiQuayDTO;
import com.example.datn.Service.banhangtaiquay.ImeiBanHangTaiQuayService;
import com.example.datn.Service.banhangtaiquay.SanPhamBanHangTaiQuayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> searchSanPham(@RequestParam String keyword) {
        System.out.println("🔍 Controller: searchSanPham được gọi với keyword: " + keyword);
        try {
            List<Object> sanPhamList = sanPhamBanHangTaiQuayService.timKiemTheoImeiHoacSKU(keyword);
            System.out.println("✅ Controller: Trả về " + (sanPhamList != null ? sanPhamList.size() : "null") + " sản phẩm");
            return ResponseEntity.ok(sanPhamList);
        } catch (Exception e) {
            System.err.println("❌ Controller: Lỗi trong searchSanPham: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage());
        }
    }

    //  Tìm kiếm sản phẩm CHỈ theo SKU (không tìm IMEI) - CÓ XỬ LÝ LỖI (đã kiểm tra)
    @GetMapping("/search-sku-only")
    public ResponseEntity<?> searchSanPhamBySKUOnly(@RequestParam String sku) {
        try {
            List<Object> sanPhamList = sanPhamBanHangTaiQuayService.timKiemSanPhamVaPhuKien(sku);
            if (sanPhamList == null || sanPhamList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy sản phẩm với mã SKU Nào hợp lệ: " + sku);
            }
            return ResponseEntity.ok(sanPhamList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Lỗi hệ thống khi tìm SKU: " + sku);
        }
    }

    // Tìm kiếm cả sản phẩm và phụ kiện
    @GetMapping("/search-all")
    public ResponseEntity<?> searchSanPhamVaPhuKien(@RequestParam String keyword) {
        System.out.println("🔍 Controller: searchSanPhamVaPhuKien được gọi với keyword: " + keyword);
        try {
            List<Object> result = sanPhamBanHangTaiQuayService.timKiemSanPhamVaPhuKien(keyword);
            System.out.println("✅ Controller: Trả về " + result.size() + " kết quả (sản phẩm + phụ kiện)");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("❌ Controller: Lỗi trong searchSanPhamVaPhuKien: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage());
        }
    }

    //  Tìm kiếm kết hợp SKU + IMEI - CÓ XỬ LÝ LỖI (đã kiểm tra)
    @GetMapping("/search-combined")
    public ResponseEntity<?> searchCombined(
            @RequestParam String sku,
            @RequestParam String imei) {
        try {
            List<SanPhamBanHangTaiQuayDTO> sanPhamList = sanPhamBanHangTaiQuayService.timKiemKetHop(sku, imei);

            if (sanPhamList == null || sanPhamList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy sản phẩm  với SKU là : " + sku + " " + " và IMEI : " + imei);
            }

            return ResponseEntity.ok(sanPhamList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Lỗi hệ thống khi tìm kết hợp SKU: " + sku + " và IMEI: " + imei);
        }
    }

    // Lấy list IMEI của sản phẩm theo mã SKU
    @GetMapping("/{maSKU}/imei")
    public ResponseEntity<?> getImeiListBySku(@PathVariable String maSKU) {
        try {
            List<ImeiBanHangTaiQuayDTO> imeiList = imeiBanHangTaiQuayService.getImeiKhachDung(maSKU);
            return ResponseEntity.ok(imeiList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy danh sách IMEI: " + e.getMessage());
        }
    }

    // Lấy list IMEI của sản phẩm theo mã SKU với filter IMEI
    @GetMapping("/{maSKU}/imei/search")
    public ResponseEntity<?> getImeiListBySkuWithFilter(
            @PathVariable String maSKU,
            @RequestParam(required = false) String imei) {
        try {
            List<ImeiBanHangTaiQuayDTO> imeiList = imeiBanHangTaiQuayService.getImeiListBySkuMaBT(maSKU, imei);
            return ResponseEntity.ok(imeiList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy danh sách IMEI với filter: " + e.getMessage());
        }
    }
}