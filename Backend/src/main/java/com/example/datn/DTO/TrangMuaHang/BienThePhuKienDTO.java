package com.example.datn.DTO.TrangMuaHang;

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
    private int soLuong;

    private Integer trangThai ;
//    private List<ThuocTinhDTO> thuocTinhList;

    private Map<String, String> thuocTinhPhuKien;
}
