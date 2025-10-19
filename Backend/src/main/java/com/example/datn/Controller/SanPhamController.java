package com.example.datn.Controller;

import com.example.datn.DTO.TrangMuaHang.SanPhamChiTietDTO;
import com.example.datn.DTO.TrangMuaHang.TrangChuSanPham;
import com.example.datn.Service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sanpham")
@RequiredArgsConstructor
public class SanPhamController {


//    @PreAuthorize("hasRole('USER')")
//@PreAuthorize("hasRole('ADMIN')")
//@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    private final SanPhamService sanPhamService;

//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public List<TrangChuSanPham> getAllSanPham() {
        return sanPhamService.getAllSanPham();
    }




    @GetMapping("/{id}/DanhMucSanPham")
    public ResponseEntity<List<TrangChuSanPham>> getProductsByParentId(
            @PathVariable Integer id
    ) {
        List<TrangChuSanPham> products;

        if (id != null) {
            products = sanPhamService.getSanPhamByDanhMuc(id);
        } else {
            products = sanPhamService.getAllSanPham();
        }

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }

//    @GetMapping("/{id}/DanhMucSanPham")
//    public ResponseEntity<List<TrangChuSanPham>> getProductsByParentId(
//            @PathVariable Integer id,
//            // ✅ SỬA LẠI 1: Thêm @RequestParam để nhận bộ lọc
//            @RequestParam(required = false) Map<String, String> specs
//    ) {
//        List<TrangChuSanPham> products;
//
//        if (id != null) {
//            // ✅ SỬA LẠI 2: Gọi service với đủ 2 tham số (id và specs)
//            // Đồng thời đổi tên hàm cho rõ nghĩa
//            products = sanPhamService.getSanPhamByDanhMucAndSpecs(id, specs);
//        } else {
//            products = sanPhamService.getAllSanPham(); // Giả sử bạn có hàm này
//        }
//
//        if (products == null || products.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//
//        return ResponseEntity.ok(products);
//    }



    // API tìm sản phẩm theo tên
    @GetMapping("/search")
    public ResponseEntity<List<TrangChuSanPham>> searchSanPham(
            @RequestParam("tenSanPham") String tenSanPham
    ) {
        List<TrangChuSanPham> products = sanPhamService.getSanPhamByTen(tenSanPham);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }



    //    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/{id}")
    public SanPhamChiTietDTO getSanPhamChiTiet(@PathVariable int id) {
        return sanPhamService.getSanPhamChiTiet(id);
    }

    @GetMapping("/{id}/lienquan")
    public ResponseEntity<List<TrangChuSanPham>> getSanPhamLienQuan(@PathVariable int id) {
        return ResponseEntity.ok(sanPhamService.getSanPhamLienQuan(id));
    }

}
