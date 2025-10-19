package com.example.datn.DTO.DonHang;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ChiTietDonHangTraCuuDTO {
    private String tenSanPham;
    private String tenPhuKien;
    private String imei;
    private Integer soLuong;
    private BigDecimal gia;
}