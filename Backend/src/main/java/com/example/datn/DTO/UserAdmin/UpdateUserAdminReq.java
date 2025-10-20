package com.example.datn.DTO.UserAdmin;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserAdminReq {
    @Size(max = 100)
    private String tenHienThi;

    @Size(max = 100)
    private String hoTen;

    @Min(0)
    @Max(2)
    private Integer gioiTinh;

    @Pattern(regexp = "^[0-9+]{8,15}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @Size(max = 255)
    private String diaChiGiaoHang;

    @Email
    private String email;

    @Pattern(regexp = "ACTIVE|INACTIVE|BANNED")
    private String trangThai;

    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String newPassword;
}

