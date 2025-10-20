package com.example.datn.DTO.UserAdmin;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserAdminReq {
    @NotBlank(message = "username không được để trống")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "password không được để trống")
    @Size(min = 6, message = "password tối thiểu 6 ký tự")
    private String passwords;

    @Email(message = "email không hợp lệ")
    @NotBlank(message = "email không được để trống")
    private String email;

    @Size(max = 100)
    private String tenHienThi;

    @Size(max = 100)
    private String hoTen;

    @Min(0) @Max(2) // 0=Nữ,1=Nam,2=Khác
    private Integer gioiTinh;

    @Pattern(regexp = "^[0-9+]{8,15}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @Size(max = 255)
    private String diaChiGiaoHang;

    @Pattern(regexp = "ACTIVE|INACTIVE|BANNED", message = "Trạng thái không hợp lệ")
    private String trangThai;
}

