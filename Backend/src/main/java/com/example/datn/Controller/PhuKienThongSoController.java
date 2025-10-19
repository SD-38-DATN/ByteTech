package com.example.datn.Controller;

import com.example.datn.DTO.CRUDthongso.LoaiThongSoDTO;
import com.example.datn.DTO.CRUDthongso.ThongSoKyThuatDTO;
import com.example.datn.Service.ThongSoAdmin.PhuKienThongSoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/phukien") // Base path cho phụ kiện
@RequiredArgsConstructor
public class PhuKienThongSoController {

    private final PhuKienThongSoService phuKienThongSoService;

    // Lấy tất cả thông số của một phụ kiện
    @GetMapping("/{maPhuKien}/thongso")
    public ResponseEntity<List<ThongSoKyThuatDTO>> getThongSoForPhuKien(@PathVariable Integer maPhuKien) {
        return ResponseEntity.ok(phuKienThongSoService.getThongSoForPhuKien(maPhuKien));
    }

    // Lấy các loại thông số có thể thêm cho phụ kiện
    @GetMapping("/{maPhuKien}/available-loai-thongso")
    public ResponseEntity<List<LoaiThongSoDTO>> getAvailableLoaiThongSo(@PathVariable Integer maPhuKien) {
        return ResponseEntity.ok(phuKienThongSoService.getAvailableLoaiThongSo(maPhuKien));
    }

    // Tạo thông số mới cho phụ kiện
    @PostMapping("/{maPhuKien}/thongso")
    public ResponseEntity<?> createThongSo(@PathVariable Integer maPhuKien, @RequestBody ThongSoKyThuatDTO dto) {
        phuKienThongSoService.createThongSo(maPhuKien, dto);
        return new ResponseEntity<>("Tạo thông số cho phụ kiện thành công", HttpStatus.CREATED);
    }
}