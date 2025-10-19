package com.example.datn.Controller;


import com.example.datn.DTO.CRUDphukien.PhuKienDTO;
import com.example.datn.Model.PhuKien;
import com.example.datn.Service.ServiceCRUDPhuKien;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/accessories") // Đổi tên resource thành "accessories" (phụ kiện)
@RequiredArgsConstructor
public class ControllerCRUDPhuKien {

    private final ServiceCRUDPhuKien serviceCRUDPhuKien;

    // Lấy tất cả phụ kiện (bao gồm biến thể và thuộc tính)
    @GetMapping
    public ResponseEntity<List<PhuKienDTO>> getAllAccessories() {
        return ResponseEntity.ok(serviceCRUDPhuKien.getAllPhuKienDTOs());
    }

    // Lấy một phụ kiện theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PhuKienDTO> getAccessoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceCRUDPhuKien.getPhuKienDTOById(id));
    }

    // Tạo phụ kiện mới (bao gồm biến thể và thuộc tính)
    @PostMapping
    public ResponseEntity<PhuKienDTO> createAccessory(@RequestBody PhuKienDTO dto) {
        PhuKien createdPhuKien = serviceCRUDPhuKien.createPhuKien(dto);
        PhuKienDTO createdDto = serviceCRUDPhuKien.getPhuKienDTOById(createdPhuKien.getMaPhuKien());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPhuKien.getMaPhuKien())
                .toUri();

        return ResponseEntity.created(location).body(createdDto);
    }

    // Cập nhật một phụ kiện (bao gồm thêm/sửa/xóa biến thể và thuộc tính)
    @PutMapping("/{id}")
    public ResponseEntity<PhuKienDTO> updateAccessory(@PathVariable Integer id, @RequestBody PhuKienDTO dto) {
        PhuKien updatedPhuKien = serviceCRUDPhuKien.updatePhuKien(id, dto);
        PhuKienDTO updatedDto = serviceCRUDPhuKien.getPhuKienDTOById(updatedPhuKien.getMaPhuKien());
        return ResponseEntity.ok(updatedDto);
    }

    // Xóa một phụ kiện (sẽ xóa cả biến thể và thuộc tính liên quan)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccessory(@PathVariable Integer id) {
        serviceCRUDPhuKien.deletePhuKien(id);
        return ResponseEntity.noContent().build();
    }

    // Xóa một biến thể phụ kiện cụ thể
    @DeleteMapping("/variants/{maSKU}")
    public ResponseEntity<Void> deleteAccessoryVariant(@PathVariable String maSKU) {
        serviceCRUDPhuKien.deleteBienThePhuKien(maSKU);
        return ResponseEntity.noContent().build();
    }

    // Xóa một thuộc tính phụ kiện cụ thể
    @DeleteMapping("/attributes/{id}")
    public ResponseEntity<Void> deleteAccessoryAttribute(@PathVariable Integer id) {
        serviceCRUDPhuKien.deleteThuocTinhPhuKien(id);
        return ResponseEntity.noContent().build();
    }
}