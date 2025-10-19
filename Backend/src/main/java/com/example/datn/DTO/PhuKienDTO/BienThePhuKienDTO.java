package com.example.datn.DTO.PhuKienDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BienThePhuKienDTO {
    private String maSKUPhuKien;
    private BigDecimal gia;
    private Integer soLuong;
    private Integer trangThai;
    private Map<String, String> thuocTinhPhuKien; // VD: {Màu: Đỏ, Size: L}
}