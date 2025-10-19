package com.example.datn.DTO.dtoBanHangTaiQuay;

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
public class DonHangBanHangTaiQuayDTO {
    private Integer maDonHang;
    private Integer userId; // ID của nhân viên xử lý đơn hàng
    private LocalDateTime ngayDat;
    private Integer trangThai; // 1 = Chờ thanh toán, 2 = Đã thanh toán
    private BigDecimal tongTien;
    private String diaChiGiaoHang;
    private String soDienThoai;
    private String phuongThucThanhToan;
    private String ghiChu;
    private Integer userVoucherId;
    private List<ChiTietDonHangBanHangTaiQuayDTO> chiTietDonHangs;
}