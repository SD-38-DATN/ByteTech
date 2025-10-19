package com.example.datn.DTO.TrangMuaHang;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Integer id;

    private String tenHienThi;
    private String username;
    private String hoTen;
    private Integer gioiTinh;
    private String email;
    private String soDienThoai;
    private String diaChiGiaoHang;
    private Integer roleId;   // chỉ lấy ID role

    private String roleName;

    public UserDTO(Integer id,String tenHienThi, String username, String hoTen, Integer gioiTinh, String email, String soDienThoai, String diaChiGiaoHang, String roleName) {
        this.id = id;
        this.tenHienThi = tenHienThi;
        this.username = username;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.diaChiGiaoHang = diaChiGiaoHang;
        this.roleName = roleName;
    }





}
