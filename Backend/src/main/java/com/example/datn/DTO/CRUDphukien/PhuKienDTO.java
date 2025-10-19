package com.example.datn.DTO.CRUDphukien;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Builder // <-- THÊM ANNOTATION NÀY
@Data
@NoArgsConstructor // ✅ THÊM ANNOTATION NÀY VÀO
@AllArgsConstructor


public class PhuKienDTO {
    private Integer maPhuKien;
    private String tenPhuKien;
    private String thuongHieu;
    private String moTa;
    private Integer soLuong;
    private BigDecimal gia;
    private Integer trangThai;

    // Foreign Keys
    private Integer maSanPham;
    private Integer maDanhMucPhuKien;
    private Integer userId;

    // Additional data for display
    private String tenDanhMucPhuKien;

    // Nested list of variants
    private List<BienThePhuKienDTO> bienThePhuKienList;

    public PhuKienDTO(Integer maPhuKien, String tenPhuKien, String thuongHieu, Integer soLuong, BigDecimal gia) {
        this.maPhuKien = maPhuKien;
        this.tenPhuKien = tenPhuKien;
        this.thuongHieu = thuongHieu;
        this.soLuong = soLuong;
        this.gia = gia;
    }
}