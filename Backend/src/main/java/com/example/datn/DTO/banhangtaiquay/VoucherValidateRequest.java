package com.example.datn.DTO.banhangtaiquay;

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
