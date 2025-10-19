package com.example.datn.DTO.TrangMuaHang;



import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AddUserVoucherRequest {
    @NotBlank(message = "Mã voucher không được để trống")
    private String codeVoucher;
}
