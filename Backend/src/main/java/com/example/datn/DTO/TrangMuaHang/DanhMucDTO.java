package com.example.datn.DTO.TrangMuaHang;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DanhMucDTO {
    private Integer maDanhMuc;
    private String tenDanhMuc;
    private String moTa;
    private Long tongSanPham;

    public DanhMucDTO(Integer maDanhMuc, String tenDanhMuc, String moTa, Long tongSanPham) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.moTa = moTa;
        this.tongSanPham = tongSanPham;
    }

    // getters và setters
}
