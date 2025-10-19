package com.example.datn.DTO.TrangMuaHang;

import com.example.datn.DTO.TrangMuaHang.LocThongSo.ThongSoKyThuatDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class TrangChuSanPham {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer maSanPham;
//
//    private String tenSanPham;
//    private String moTa;
//    private Integer soLuong;
//    private BigDecimal gia;
//
//
//// danh muc
//    private Integer maDanhMuc;
//    private String tenDanhMuc;
//
//
//}
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrangChuSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSanPham;

    private String tenSanPham;
    private String moTa;

    private String thuongHieu;
    private Integer soLuong;
    private BigDecimal gia;

    // danh muc
    private Integer maDanhMuc;
    private String tenDanhMuc;

    // ===== Thông tin biến thể nếu có khuyến mãi =====
    private String maSKU;
    private BigDecimal giaKhongKhuyenMaiBienThe;
    private BigDecimal giaBienThe;  // giá sau KM
    private BigDecimal giaTriGiamKhuyenMai;
    private Integer loaiGiam; // 1 = %, 0 = tiền


    private List<LocThuocTinhDTO> chiTietThuocTinh;

    // ✅ DÒNG MỚI: Thêm trường này để chứa thông số kỹ thuật
    private List<ThongSoKyThuatDTO> chiTietThongSo;

}