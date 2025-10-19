package com.example.datn.DTO.TrangMuaHang;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTO {
    private Integer id;
    private String codeVoucher;
    private String tenVoucher;
    private Integer soLanSuDung;
    private String moTa;
    private Integer loaiGiam;

    private BigDecimal giaTriGiam;
    private BigDecimal dieuKienGiam;
    private BigDecimal giamToiDa;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;

    private Integer trangThai;
}
