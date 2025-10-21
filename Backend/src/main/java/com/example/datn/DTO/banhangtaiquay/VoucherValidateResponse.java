package com.example.datn.DTO.banhangtaiquay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherValidateResponse {
    private boolean success;
    private String message;
    private BigDecimal soTienGiam;
    private VoucherBanHangTaiQuayDTO voucher;
    
    public static VoucherValidateResponse success(BigDecimal soTienGiam, VoucherBanHangTaiQuayDTO voucher) {
        return new VoucherValidateResponse(true, "Voucher hợp lệ", soTienGiam, voucher);
    }
    
    public static VoucherValidateResponse error(String message) {
        return new VoucherValidateResponse(false, message, BigDecimal.ZERO, null);
    }
}
