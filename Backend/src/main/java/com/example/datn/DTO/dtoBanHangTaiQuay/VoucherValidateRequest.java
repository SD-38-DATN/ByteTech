package com.example.datn.DTO.dtoBanHangTaiQuay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherValidateRequest {
    private String codeVoucher;
    private BigDecimal tongTienDonHang;
}
