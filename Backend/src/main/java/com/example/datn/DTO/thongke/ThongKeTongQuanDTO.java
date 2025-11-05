package com.example.datn.DTO.thongke;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeTongQuanDTO {
    private BigDecimal tongDoanhThu;
    private Long soLuongSanPham;
    private Long soLuongDonHang;
}

