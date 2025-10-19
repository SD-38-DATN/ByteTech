package com.example.datn.DTO.TrangMuaHang.LocThongSo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongSoKyThuatDTO {
    private String tenLoaiThongSo; // Ví dụ: "RAM"
    private String giaTriThongSo;   // Ví dụ: "16GB"
}