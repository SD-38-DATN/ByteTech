package com.example.datn.DTO.dtoBanHangTaiQuay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHangBanHangTaiQuayDTO {
    private Integer id;
    private Integer maDonHang;
    private String maSKU; // Sản phẩm chính
    private String maSKUPhuKien; // Phụ kiện
    private Integer imeiId;
    private Integer soLuong;
    private BigDecimal gia;
    private String loaiSanPham; // "sanpham" hoặc "phukien"
}
