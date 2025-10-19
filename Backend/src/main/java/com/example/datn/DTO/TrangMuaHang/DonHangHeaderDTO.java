package com.example.datn.DTO.TrangMuaHang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonHangHeaderDTO {
    private String tenKhachHang;
    private Integer maDonHang;
    private String trangThai;
    private BigDecimal tongTien;
    private BigDecimal tongTienTruocGiam;
    private BigDecimal soTienGiam;
    private String diaChiGiaoHang;
    private String soDienThoai;
    private LocalDateTime ngayDat;

    private Integer voucherId;
    private String maVoucher;
    private String tenVoucher;
    private String moTa;
    private Integer soLanSuDung;
    private String loaiGiam;
    private BigDecimal giaTriGiam;
    private BigDecimal dieuKienGiam;
    private BigDecimal giamToiDa;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;


}