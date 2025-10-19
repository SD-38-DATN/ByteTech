package com.example.datn.DTO.TrangMuaHang;



import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrangChuPhuKien {

    @Id
    private Integer maPhuKien;

    private String tenPhuKien;
    private String moTa;
    private Integer soLuong;
    private BigDecimal gia;



    private String tenDanhMucPhuKien; // Tên danh mục phụ kiện
}