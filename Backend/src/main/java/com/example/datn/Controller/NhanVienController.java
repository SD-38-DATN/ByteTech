package com.example.datn.Controller;

import com.example.datn.DTO.TrangMuaHang.UserDTO;
import com.example.datn.Model.Users;
import com.example.datn.Service.NhanVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nhanvien")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService nhanVienService;

    // LẤY DANH SÁCH NHÂN VIÊN
     //  http://localhost:8081/api/nhanvien/tatca

    @GetMapping("/tatca")
    public ResponseEntity<List<UserDTO>> getAllNhanVien() {
        return ResponseEntity.ok(nhanVienService.getAll());
    }
    //  TÌM KIẾM NHÂN VIÊN
   // http://localhost:8081/api/nhanvien/timkiem?
    //    keyword={{$random.alphanumeric(8)}}

    @GetMapping("/timkiem")
    public ResponseEntity<List<UserDTO>> searchNhanVien(@RequestParam String keyword) {
        return ResponseEntity.ok(nhanVienService.search(keyword));
    }
    // 3. THÊM NHÂN VIÊN MỚI
   // http://localhost:8081/api/nhanvien/them
//{
//  "id": 1,
//  "tenHienThi": "Nguyễn Văn Minh",
//  "username": "minhnguyen",
//  "passwords": "Minh@12345",
//  "hoTen": "Nguyễn Văn Minh",
//  "gioiTinh": 1,
//  "email": "minh.nguyen@example.com",
//  "soDienThoai": "0905123456",
//  "diaChiGiaoHang": "123 Đường Nguyễn Trãi, Quận 1, TP. Hồ Chí Minh",
//  "roleId": 3,
//  "roleName": "Nhân viên"
//}
    @PostMapping("/them")
    public ResponseEntity<?> createNhanVien(@Valid @RequestBody UserDTO dto) {
        try {
            Users created = nhanVienService.create(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //  4. SỬA THÔNG TIN NHÂN VIÊN
     //Ví dụ gọi: http://localhost:8080/api/nhanvien/sua/5

    @PutMapping("/sua/{id}")
    //http://localhost:8081/api/nhanvien/sua/{{id}}
    public ResponseEntity<?> updateNhanVien(@PathVariable Integer id, @Valid @RequestBody UserDTO dto) {
        try {
            Users updated = nhanVienService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  5. XÓA NHÂN VIÊN
     //http://localhost:8080/api/nhanvien/xoa/5

    @DeleteMapping("/xoa/{id}")
    //http://localhost:8081/api/nhanvien/xoa/{{id}}
    public ResponseEntity<?> deleteNhanVien(@PathVariable Integer id) {
        try {
            nhanVienService.delete(id);
            return ResponseEntity.ok("Xóa nhân viên thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
