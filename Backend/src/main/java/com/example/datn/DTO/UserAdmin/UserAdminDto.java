package com.example.datn.DTO.UserAdmin;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAdminDto {
    private Integer id;
    private String username;
    private String email;
    private String hoTen;
    private String tenHienThi;
    private Integer gioiTinh;
    private String soDienThoai;
    private String diaChiGiaoHang;
    private String trangThai;
    private String roleName;
}
