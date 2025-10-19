package com.example.datn.DTO.TrangMuaHang;

// ThuocTinhDTO.java
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThuocTinhDTO {
    private String tenThuocTinh;        // ví dụ: Màu, Size
    private String tenThuocTinhBienThe; // ví dụ: Đỏ, 35

   // Constructor mới chỉ với 1 String để nối kiểu "Màu : Đen"
    public ThuocTinhDTO(String tenThuocTinh) {
        this.tenThuocTinh = tenThuocTinh;
    }
}
