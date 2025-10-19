package com.example.datn.Service.banhangtaiquay;

import com.example.datn.DTO.dtoBanHangTaiQuay.KhachHangBanHangTaiQuayDTO;
import com.example.datn.Model.Users;
import com.example.datn.Repository.BanHangTaiQuay.UsersBanHangTaiQuayRepository;
import com.example.datn.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangBanHangTaiQuayService {

    @Autowired
    private UsersBanHangTaiQuayRepository usersRepository;

    // Tìm kiếm khách hàng theo số điện thoại
    public List<KhachHangBanHangTaiQuayDTO> timKiemKhachHangTheoSoDienThoai(String soDienThoai) {
        List<Users> users = usersRepository.findBySoDienThoaiContainingIgnoreCase(soDienThoai);
        
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy tất cả khách hàng
    public List<KhachHangBanHangTaiQuayDTO> getAllKhachHang() {
        List<Users> users = usersRepository.findAll();
        
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Chuyển đổi Users sang DTO
    private KhachHangBanHangTaiQuayDTO convertToDTO(Users user) {
        return KhachHangBanHangTaiQuayDTO.builder()
                .maKhachHang(user.getId())
                .tenKhachHang(user.getTenHienThi() != null ? user.getTenHienThi() : user.getHoTen())
                .soDienThoai(user.getSoDienThoai())
                .email(user.getEmail())
                .diaChi(user.getDiaChiGiaoHang())
                .ghiChu(null) // Users model không có field ghiChu
                .build();
    }
}
