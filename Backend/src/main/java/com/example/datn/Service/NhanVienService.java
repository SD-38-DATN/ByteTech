package com.example.datn.Service;

import com.example.datn.DTO.TrangMuaHang.UserDTO;
import com.example.datn.Model.Role;
import com.example.datn.Model.Users;
import com.example.datn.Repository.RoleRepository;
import com.example.datn.Repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NhanVienService {
    @Autowired
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;

    // Thêm nhân viên
    public Users create(UserDTO dto) {

        // Kiểm tra trùng toàn bộ các trường
        if (usersRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (usersRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (usersRepository.existsBySoDienThoai(dto.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        if (usersRepository.existsByTenHienThi(dto.getTenHienThi())) {
            throw new RuntimeException("Tên hiển thị đã tồn tại");
        }

        // Tạo mới nhân viên
        Users user = new Users();
        user.setTenHienThi(dto.getTenHienThi());
        user.setUsername(dto.getUsername());
        user.setPasswords(dto.getPasswords());
        user.setHoTen(dto.getHoTen());
        user.setGioiTinh(dto.getGioiTinh());
        user.setEmail(dto.getEmail());
        user.setSoDienThoai(dto.getSoDienThoai());
        user.setDiaChiGiaoHang(dto.getDiaChiGiaoHang());

        // Role = NHANVIEN
        Role role = roleRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("Role nhân viên không tồn tại"));
        user.setRole(role);

        return usersRepository.save(user);
    }

    // Sửa nhân viên
    public Users update(Integer id, UserDTO dto) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        // Kiểm tra trùng (trừ chính mình)
        if (usersRepository.existsByUsernameAndIdNot(dto.getUsername(), id)) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (usersRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (usersRepository.existsBySoDienThoaiAndIdNot(dto.getSoDienThoai(), id)) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        if (usersRepository.existsByTenHienThiAndIdNot(dto.getTenHienThi(), id)) {
            throw new RuntimeException("Tên hiển thị đã tồn tại");
        }

        user.setTenHienThi(dto.getTenHienThi());
        user.setUsername(dto.getUsername());
        user.setPasswords(dto.getPasswords());
        user.setHoTen(dto.getHoTen());
        user.setGioiTinh(dto.getGioiTinh());
        user.setEmail(dto.getEmail());
        user.setSoDienThoai(dto.getSoDienThoai());
        user.setDiaChiGiaoHang(dto.getDiaChiGiaoHang());

        return usersRepository.save(user);
    }

    // Xóa nhân viên
    public void delete(Integer id) {
        if (!usersRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nhân viên");
        }
        usersRepository.deleteById(id);
    }

    // Lấy tất cả nhân viên
    public List<UserDTO> getAll() {
        return usersRepository.findAllNhanVien().stream()
                .map(u -> new UserDTO(
                        u.getId(),
                        u.getTenHienThi(),
                        u.getUsername(),
                        u.getPasswords(),
                        u.getHoTen(),
                        u.getGioiTinh(),
                        u.getEmail(),
                        u.getSoDienThoai(),
                        u.getDiaChiGiaoHang(),
                        u.getRole().getRoleName()
                )).collect(Collectors.toList());
    }

    // Tìm kiếm nhân viên
    public List<UserDTO> search(String keyword) {
        return usersRepository.searchNhanVien(keyword).stream()
                .map(u -> new UserDTO(
                        u.getId(),
                        u.getTenHienThi(),
                        u.getUsername(),
                        u.getPasswords(),
                        u.getHoTen(),
                        u.getGioiTinh(),
                        u.getEmail(),
                        u.getSoDienThoai(),
                        u.getDiaChiGiaoHang(),
                        u.getRole().getRoleName()
                )).collect(Collectors.toList());
    }
}
