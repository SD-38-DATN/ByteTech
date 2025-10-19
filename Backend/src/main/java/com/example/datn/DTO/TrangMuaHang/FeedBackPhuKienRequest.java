package com.example.datn.DTO.TrangMuaHang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackPhuKienRequest {
    private Integer maPhuKien; // Thay maSanPham bằng maPhuKien
    private String noiDung;
    private int danhGia;
}