package com.example.datn.Controller;


import com.example.datn.DTO.CRUDthongso.LoaiThongSoDTO;
import com.example.datn.DTO.CRUDthongso.ThongSoKyThuatDTO;
import com.example.datn.Service.ThongSoAdmin.ThongSoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ThongSoController {

    private final ThongSoService thongSoService;

    // Lấy tất cả thông số của một sản phẩm




        @GetMapping("/sanpham/{maSanPham}/thongso")
        public ResponseEntity<List<ThongSoKyThuatDTO>> getThongSoForSanPham(@PathVariable Integer maSanPham) {
            return ResponseEntity.ok(thongSoService.getThongSoForSanPham(maSanPham));
        }

        @GetMapping("/sanpham/{maSanPham}/available-loai-thongso")
        public ResponseEntity<List<LoaiThongSoDTO>> getAvailableLoaiThongSo(@PathVariable Integer maSanPham) {
            return ResponseEntity.ok(thongSoService.getAvailableLoaiThongSo(maSanPham));
        }

        @PostMapping("/sanpham/{maSanPham}/thongso")
        public ResponseEntity<?> createThongSo(@PathVariable Integer maSanPham, @RequestBody ThongSoKyThuatDTO dto) {
            thongSoService.createThongSo(maSanPham, dto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        @PutMapping("/thongso/{maThongSo}")
        public ResponseEntity<?> updateThongSo(@PathVariable Integer maThongSo, @RequestBody ThongSoKyThuatDTO dto) {
            thongSoService.updateThongSo(maThongSo, dto);
            return ResponseEntity.ok().build();
        }

        @DeleteMapping("/thongso/{maThongSo}")
        public ResponseEntity<Void> deleteThongSo(@PathVariable Integer maThongSo) {
            thongSoService.deleteThongSo(maThongSo);
            return ResponseEntity.noContent().build();
        }






}