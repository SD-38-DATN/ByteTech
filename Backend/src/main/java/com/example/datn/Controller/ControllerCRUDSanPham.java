//package com.example.datn.Controller;
//
//
//
//
//
//
//        import com.example.datn.DTO.CRUDSanPhamAdmin.BienTheDTO;
//        import com.example.datn.DTO.CRUDSanPhamAdmin.SanPhamDTO;
//        import com.example.datn.DTO.CRUDSanPhamAdmin.ThuocTinhDTO;
//        import com.example.datn.Model.BienTheSanPham;
//        import com.example.datn.Model.SanPham;
//        import com.example.datn.Model.ThuocTinh;
//        import com.example.datn.Service.ServiceCRUDSanPham;
//        import lombok.RequiredArgsConstructor;
//        import org.springframework.http.ResponseEntity;
//        import org.springframework.web.bind.annotation.*;
//
//        import java.util.List;
//        import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/admin")
//@RequiredArgsConstructor
//public class ControllerCRUDSanPham {
//
//
//    private final ServiceCRUDSanPham serviceCRUDSanPham;
//
//    // 🟢 Lấy toàn bộ sản phẩm
//    @GetMapping("/dto")
//    public ResponseEntity<List<SanPhamDTO>> getAllSanPhamDTO() {
//        return ResponseEntity.ok(serviceCRUDSanPham.listAllDTO());
//    }
//
//    @GetMapping("/dto/{id}")
//    public ResponseEntity<SanPhamDTO> getSanPhamDTO(@PathVariable Integer id) {
//        Optional<SanPhamDTO> sp = serviceCRUDSanPham.getDTO(id);
//        return sp.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteSanPham(@PathVariable Integer id) {
//        serviceCRUDSanPham.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//
//
//
//    // 🟢 Thêm sản phẩm
//    @PostMapping("/sanpham")
//    public ResponseEntity<SanPham> createSanPham(@RequestBody SanPhamDTO dto) {
//        return ResponseEntity.ok(serviceCRUDSanPham.createSanPham(dto));
//    }
//
//    // 🟡 Sửa sản phẩm
//    @PutMapping("/sanpham/{id}")
//    public ResponseEntity<SanPham> updateSanPham(@PathVariable Integer id, @RequestBody SanPhamDTO dto) {
//        return ResponseEntity.ok(serviceCRUDSanPham.updateSanPham(id, dto));
//    }
//
//    // 🟢 Thêm biến thể cho sản phẩm
//    @PostMapping("/sanpham/{id}/bienthe")
//    public ResponseEntity<BienTheSanPham> createBienThe(@PathVariable Integer id, @RequestBody BienTheDTO dto) {
//        return ResponseEntity.ok(serviceCRUDSanPham.createBienThe(id, dto));
//    }
//
//    // 🟡 Sửa biến thể
//    @PutMapping("/bienthe/{maSKU}")
//    public ResponseEntity<BienTheSanPham> updateBienThe(@PathVariable String maSKU, @RequestBody BienTheDTO dto) {
//        return ResponseEntity.ok(serviceCRUDSanPham.updateBienThe(maSKU, dto));
//    }
//
//    // 🟢 Thêm thuộc tính cho biến thể
//    @PostMapping("/bienthe/{maSKU}/thuoctinh")
//    public ResponseEntity<ThuocTinh> createThuocTinh(@PathVariable String maSKU, @RequestBody ThuocTinhDTO dto) {
//        return ResponseEntity.ok(serviceCRUDSanPham.createThuocTinh(maSKU, dto));
//    }
//
//    // 🟡 Sửa thuộc tính
//    @PutMapping("/thuoctinh/{id}")
//    public ResponseEntity<ThuocTinh> updateThuocTinh(@PathVariable Integer id, @RequestBody ThuocTinhDTO dto) {
//        return ResponseEntity.ok(serviceCRUDSanPham.updateThuocTinh(id, dto));
//    }
//
//
//}


package com.example.datn.Controller;

import com.example.datn.DTO.CRUDSanPhamAdmin.SanPhamDTO;
import com.example.datn.DTO.IMEI.VariantSummaryDTO;
import com.example.datn.Model.SanPham;
import com.example.datn.Service.ServiceCRUDSanPham;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/products") // Sử dụng tên resource số nhiều: 'products'
@RequiredArgsConstructor
public class ControllerCRUDSanPham {

    private final ServiceCRUDSanPham serviceCRUDSanPham;

    // Lấy tất cả sản phẩm (bao gồm biến thể và thuộc tính)
    @GetMapping
    public ResponseEntity<List<SanPhamDTO>> getAllProducts() {
        return ResponseEntity.ok(serviceCRUDSanPham.getAllSanPhamDTOs());
    }

    // Lấy một sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<SanPhamDTO> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceCRUDSanPham.getSanPhamDTOById(id));
    }

    // Tạo sản phẩm mới (bao gồm biến thể và thuộc tính)
    @PostMapping
    public ResponseEntity<SanPhamDTO> createProduct(@RequestBody SanPhamDTO dto) {
        SanPham createdSanPham = serviceCRUDSanPham.createSanPham(dto);
        // Trả về DTO của sản phẩm vừa tạo
        SanPhamDTO createdSanPhamDto = serviceCRUDSanPham.getSanPhamDTOById(createdSanPham.getMaSanPham());

        // Trả về status 201 Created và location của resource mới
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSanPham.getMaSanPham())
                .toUri();

        return ResponseEntity.created(location).body(createdSanPhamDto);
    }

    // Cập nhật một sản phẩm (bao gồm thêm/sửa/xóa biến thể và thuộc tính)
    @PutMapping("/{id}")
    public ResponseEntity<SanPhamDTO> updateProduct(@PathVariable Integer id, @RequestBody SanPhamDTO dto) {
        SanPham updatedSanPham = serviceCRUDSanPham.updateSanPham(id, dto);
        SanPhamDTO updatedDto = serviceCRUDSanPham.getSanPhamDTOById(updatedSanPham.getMaSanPham());
        return ResponseEntity.ok(updatedDto);
    }

    // Xóa một sản phẩm (sẽ xóa cả biến thể và thuộc tính liên quan)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        serviceCRUDSanPham.deleteSanPham(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }

    // Xóa một biến thể cụ thể
    @DeleteMapping("/variants/{maSKU}")
    public ResponseEntity<Void> deleteVariant(@PathVariable String maSKU) {
        serviceCRUDSanPham.deleteBienThe(maSKU);
        return ResponseEntity.noContent().build();
    }

    // Xóa một thuộc tính cụ thể
    @DeleteMapping("/attributes/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Integer id) {
        serviceCRUDSanPham.deleteThuocTinh(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/variants-summary")
    public ResponseEntity<List<Map<String, String>>> getVariantsSummary() {
        List<Map<String, String>> summary = serviceCRUDSanPham.getAllSanPhamDTOs().stream()
                .flatMap(sp -> sp.getBienTheList().stream()
                        // Thêm điều kiện lọc ở đây
                        .filter(bt -> bt.getTrangThai() == 1)
                        .map(bt -> Map.of(
                                "maSKU", bt.getMaSKU(),
                                "tenHienThi", sp.getTenSanPham() + " - SKU: " + bt.getMaSKU()
                        ))
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/accessories/variants-summary")
    public ResponseEntity<List<Map<String, String>>> getAccessoryVariantsSummary() {
        return ResponseEntity.ok(serviceCRUDSanPham.getAccessoryVariantsSummary());
    }



}