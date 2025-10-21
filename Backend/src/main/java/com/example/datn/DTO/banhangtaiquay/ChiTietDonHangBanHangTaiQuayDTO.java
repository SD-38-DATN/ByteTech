package com.example.datn.DTO.banhangtaiquay;

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
    private Object imeiId; // Có thể là Integer hoặc String (temp ID)
    private String imei; // Số IMEI
    private Integer soLuong;
    private BigDecimal gia;
    private String loaiSanPham; // "sanpham" hoặc "phukien"
}
