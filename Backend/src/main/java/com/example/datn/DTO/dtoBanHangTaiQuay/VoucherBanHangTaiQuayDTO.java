package com.example.datn.DTO.dtoBanHangTaiQuay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherBanHangTaiQuayDTO {
    private Integer id;
    private String codeVoucher;
    private String tenVoucher;
    private Integer soLanSuDung;
    private String moTa;
    private Integer loaiGiam; // 1 = %, 0 = tiền
    private BigDecimal giaTriGiam;
    private BigDecimal dieuKienGiam;
    private BigDecimal giamToiDa;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer trangThai;
}