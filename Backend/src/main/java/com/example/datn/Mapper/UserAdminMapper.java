package com.example.datn.Mapper;

import com.example.datn.DTO.UserAdmin.UserAdminDto;
import com.example.datn.Model.Users;

public class UserAdminMapper {
    public static UserAdminDto toDto(Users u) {
        if (u == null) return null;
        return UserAdminDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .hoTen(u.getHoTen())
                .tenHienThi(u.getTenHienThi())
                .gioiTinh(u.getGioiTinh())
                .soDienThoai(u.getSoDienThoai())
                .diaChiGiaoHang(u.getDiaChiGiaoHang())
                .trangThai(u.getTrangThai())
                .roleName(u.getRole() != null ? u.getRole().getRoleName() : "USER")
                .build();
    }
}
