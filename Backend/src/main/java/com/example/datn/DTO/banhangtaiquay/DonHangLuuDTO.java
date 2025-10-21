package com.example.datn.DTO.banhangtaiquay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonHangLuuDTO {
    
    private Integer maDonHang;
    private String soDienThoai;
    private String diaChiGiaoHang;
    private BigDecimal tongTien;
    private LocalDateTime ngayDat;
    private String ghiChu;
    private String tenNhanVien;
    private String username;
    private List<ChiTietDonHangLuuDTO> chiTietDonHangs;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietDonHangLuuDTO {
        private Integer id;
        private Integer soLuong;
        private BigDecimal gia;
        private String maSKU;
        private String tenSanPham;
        private String imei;
        private String loaiSanPham; // "sanpham" hoặc "phukien"
        private String thuocTinh; // Thuộc tính sản phẩm
        private String giaTriThuocTinh; // Giá trị thuộc tính
    }
}