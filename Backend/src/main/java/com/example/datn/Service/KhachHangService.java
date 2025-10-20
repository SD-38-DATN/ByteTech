package com.example.datn.Service;

import com.example.datn.Config.NotFoundException;
import com.example.datn.DTO.UserAdmin.CreateUserAdminReq;
import com.example.datn.DTO.UserAdmin.UpdateUserAdminReq;
import com.example.datn.DTO.UserAdmin.UserAdminDto;
import com.example.datn.Exception.BadRequestException;
import com.example.datn.Exception.ConflictException;
import com.example.datn.Mapper.UserAdminMapper;
import com.example.datn.Model.Role;
import com.example.datn.Model.Users;
import com.example.datn.Repository.KhachHangRepository;
import com.example.datn.Repository.RoleRepository;
import com.example.datn.Repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Stream;

import static com.example.datn.Repository.spec.KhachHangSpecs.*;

@Service
@RequiredArgsConstructor
public class KhachHangService {
    private final KhachHangRepository repo;
    private final PasswordEncoder passwordEncoder; // BCrypt
    private final UsersRepository usersRepo;
    private final RoleRepository roleRepo;

    @Transactional(readOnly = true)
    public Page<UserAdminDto> find(String search, String gender, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Specification<Users> spec = Stream.of(
                roleIsUser(),            // ⬅️ chỉ khách hàng
                searchLike(search),
                genderEq(gender),
                statusEq(status)
        ).filter(Objects::nonNull).reduce(Specification::and).orElse(null);

        return repo.findAll(spec, pageable).map(UserAdminMapper::toDto);
    }

    @Transactional
    public UserAdminDto updateStatus(Integer id, String nextStatus) {
        Users u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại: id=" + id));
        u.setTrangThai(nextStatus);
        return UserAdminMapper.toDto(repo.save(u));
    }


    @Transactional(readOnly = true)
    public UserAdminDto getOne(Integer id) {
        Users u = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));
        return UserAdminMapper.toDto(u);
    }


    @Transactional
    public UserAdminDto updateInfo(Integer id, UpdateUserAdminReq req) {
        Users u = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));

        if (u.getRole() == null || !"USER".equalsIgnoreCase(u.getRole().getRoleName()))
            throw new BadRequestException("Chỉ cập nhật tài khoản khách hàng");

        // kiểm tra trùng email nếu có đổi
        if (req.getEmail() != null && !req.getEmail().equalsIgnoreCase(u.getEmail())) {
            usersRepo.findByEmail(req.getEmail()).ifPresent(x -> {
                throw new ConflictException("Email đã được sử dụng");
            });
            u.setEmail(req.getEmail());
        }

        if (req.getTenHienThi() != null) u.setTenHienThi(req.getTenHienThi());
        if (req.getHoTen() != null) u.setHoTen(req.getHoTen());
        if (req.getGioiTinh() != null) u.setGioiTinh(req.getGioiTinh());
        if (req.getSoDienThoai() != null) u.setSoDienThoai(req.getSoDienThoai());
        if (req.getDiaChiGiaoHang() != null) u.setDiaChiGiaoHang(req.getDiaChiGiaoHang());
        if (req.getTrangThai() != null) u.setTrangThai(req.getTrangThai());

        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            u.setPasswords(passwordEncoder.encode(req.getNewPassword()));
        }
        return UserAdminMapper.toDto(repo.save(u));
    }

    @Transactional
    public UserAdminDto create(CreateUserAdminReq req) {
        usersRepo.findByUsername(req.getUsername()).ifPresent(x -> {
            throw new ConflictException("Username đã tồn tại");
        });
        usersRepo.findByEmail(req.getEmail()).ifPresent(x -> {
            throw new ConflictException("Email đã tồn tại");
        });

        Users u = new Users();
        u.setUsername(req.getUsername());
        u.setPasswords(passwordEncoder.encode(req.getPasswords()));
        u.setEmail(req.getEmail());
        u.setTenHienThi(req.getTenHienThi());
        u.setHoTen(req.getHoTen());
        u.setGioiTinh(req.getGioiTinh());
        u.setSoDienThoai(req.getSoDienThoai());
        u.setDiaChiGiaoHang(req.getDiaChiGiaoHang());
        u.setTrangThai(req.getTrangThai() == null ? "ACTIVE" : req.getTrangThai());

        Role userRole = roleRepo.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy role USER"));
        u.setRole(userRole);

        return UserAdminMapper.toDto(repo.save(u));
    }
}
