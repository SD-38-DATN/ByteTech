package com.example.datn.DTO.CRUDphukien;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BienThePhuKienDTO {
    private String maSKUPhuKien;
    private BigDecimal gia;
    private Integer soLuong;
    private Integer trangThai;

    // Nested list of attributes
    private List<ThuocTinhPhuKienDTO> thuocTinhPhuKienList;
}