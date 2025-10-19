package com.example.datn.Controller;

import com.example.datn.DTO.AdminKhuyenMai.VariantSummaryDTO;
import com.example.datn.Model.KhuyenMai;
import com.example.datn.Service.KhuyenMaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khuyenmai")
@RequiredArgsConstructor

public class KhuyenMaiController {


    private  final KhuyenMaiService khuyenMaiService;

    // API lấy tất cả khuyến mại
    @GetMapping
    public List<KhuyenMai> getAllKhuyenMai() {
        return khuyenMaiService.getAllKhuyenMai();
    }

    // API lấy khuyến mại theo ID
    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMai> getKhuyenMaiById(@PathVariable Integer id) {
        return khuyenMaiService.getKhuyenMaiById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // API tạo khuyến mại mới
    @PostMapping
    public KhuyenMai createKhuyenMai(@RequestBody KhuyenMai khuyenMai) {
        if(khuyenMai.getTrangThai() == null) {
            khuyenMai.setTrangThai(1); // Mặc định là active
        }
        return khuyenMaiService.saveKhuyenMai(khuyenMai);
    }

    // API cập nhật khuyến mại
    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMai> updateKhuyenMai(@PathVariable Integer id, @RequestBody KhuyenMai khuyenMaiDetails) {
        try {
            KhuyenMai updatedKhuyenMai = khuyenMaiService.updateKhuyenMai(id, khuyenMaiDetails);
            return ResponseEntity.ok(updatedKhuyenMai);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // API xóa khuyến mại (xóa mềm)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKhuyenMai(@PathVariable Integer id) {
        try {
            khuyenMaiService.deleteKhuyenMai(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }



    // ... bên trong class KhuyenMaiController

    // API để lấy danh sách SKU đã áp dụng cho một khuyến mại
    @GetMapping("/{id}/applied-variants")
    public ResponseEntity<List<String>> getAppliedVariants(@PathVariable Integer id) {
        List<String> skus = khuyenMaiService.getAppliedVariantSkus(id);
        return ResponseEntity.ok(skus);
    }

    // API để cập nhật danh sách sản phẩm áp dụng khuyến mại
    @PutMapping("/{id}/apply-to-variants")
    public ResponseEntity<Void> applyToVariants(@PathVariable Integer id, @RequestBody List<String> skus) {
        khuyenMaiService.applyKhuyenMaiToVariants(id, skus);
        return ResponseEntity.ok().build();
    }


}