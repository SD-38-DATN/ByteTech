package com.example.datn.Controller;


import com.example.datn.DTO.CRUDthongso.LoaiThongSoDetailDTO;
import com.example.datn.Service.ThongSoAdmin.LoaiThongSoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loai-thongso")
@RequiredArgsConstructor
public class LoaiThongSoController {

    private final LoaiThongSoService loaiThongSoService;

    @GetMapping
    public ResponseEntity<List<LoaiThongSoDetailDTO>> getAll() {
        return ResponseEntity.ok(loaiThongSoService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LoaiThongSoDetailDTO dto) {
        loaiThongSoService.create(dto);
        return new ResponseEntity<>("Tạo mới thành công", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody LoaiThongSoDetailDTO dto) {
        loaiThongSoService.update(id, dto);
        return ResponseEntity.ok("Cập nhật thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        loaiThongSoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}