package com.example.datn.DTO.TrangMuaHang;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
//    private Integer id;
//
//    private String tenHienThi;
//    private String username;
//    private String hoTen;
//    private Integer gioiTinh;
//    private String email;
//    private String soDienThoai;
//    private String diaChiGiaoHang;
private Integer id;

    @NotBlank(message = "Tên hiển thị không được để trống")
    @Size(max = 100, message = "Tên hiển thị không được quá 100 ký tự")
    private String tenHienThi;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 50, message = "Tên đăng nhập phải từ 4-50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 50, message = "Mật khẩu phải từ 6-50 ký tự")
    private String passwords;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String hoTen;

    @NotNull(message = "Giới tính không được để trống (1 = Nam, 0 = Nữ)")

    private Integer gioiTinh;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải gồm 10 số và bắt đầu bằng 0")
    private String soDienThoai;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Size(max = 255, message = "Địa chỉ không được quá 255 ký tự")
    private String diaChiGiaoHang;

    private Integer roleId;   // chỉ lấy ID role

    private String roleName;


    public UserDTO(Integer id,String tenHienThi, String username, String passwords ,String hoTen, Integer gioiTinh, String email, String soDienThoai, String diaChiGiaoHang, String roleName) {
        this.id = id;
        this.tenHienThi = tenHienThi;
        this.username = username;
        this.passwords = passwords;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.diaChiGiaoHang = diaChiGiaoHang;
        this.roleName = roleName;
    }





}
