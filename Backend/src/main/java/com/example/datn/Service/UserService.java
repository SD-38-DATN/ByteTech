package com.example.datn.Service;




import com.example.datn.DTO.TrangMuaHang.UserDTO;
import com.example.datn.Model.Role;
import com.example.datn.Model.Users;

import com.example.datn.Repository.RoleRepository;
import com.example.datn.Repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository userRepository;
    private final RoleRepository roleRepository;

    // Entity -> DTO
    private UserDTO toDTO(Users e) {
        return new UserDTO(
                e.getId(),
                e.getTenHienThi(),
                e.getUsername(),
                e.getHoTen(),
                e.getGioiTinh(),
                e.getEmail(),
                e.getSoDienThoai(),
                e.getDiaChiGiaoHang(),
                e.getRole() != null ? e.getRole().getRoleName() : null
        );
    }

    // DTO -> Entity
    private Users toEntity(UserDTO dto) {
        Users e = new Users();
        e.setId(dto.getId());
        e.setTenHienThi((dto.getTenHienThi()));
        e.setUsername(dto.getUsername());
        e.setHoTen(dto.getHoTen());
        e.setGioiTinh(dto.getGioiTinh());
        e.setEmail(dto.getEmail());
        e.setSoDienThoai(dto.getSoDienThoai());
        e.setDiaChiGiaoHang(dto.getDiaChiGiaoHang());

        if (dto.getRoleId() != null) {
            Role role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role id = " + dto.getRoleId()));
            e.setRole(role);
        }
        return e;
    }

    // CRUD
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UserDTO findById(Integer id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user id = " + id));
    }

    public UserDTO save(UserDTO dto) {
        return toDTO(userRepository.save(toEntity(dto)));
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    // Phân trang
    public Page<UserDTO> getPage(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size))
                .map(this::toDTO);
    }

    // Tìm theo username
    public UserDTO findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với username = " + username));
    }

}
