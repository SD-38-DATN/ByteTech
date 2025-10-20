package com.example.datn.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String tenHienThi;
    private String username;
    private String passwords;
    private String hoTen;
    private Integer gioiTinh; // 1=Nam, 0=Nữ, (hỗ trợ "Khác" khi !=0/1 hoặc null)
    private String email;
    private String soDienThoai;

    @Column(name = "diaChiGiaoHang")
    private String diaChiGiaoHang;

    @Column(name = "trangThai", nullable = false)
    private String trangThai = "ACTIVE";

    @ManyToOne
    @JoinColumn(name = "roleID")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<SanPham> sanPhamList;

    @OneToMany(mappedBy = "user")
    private List<GioHang> gioHangList;

    @OneToMany(mappedBy = "user")
    private List<DonHang> donHangList;

    @OneToMany(mappedBy = "user")
    private List<UserVoucher> userVoucherList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = (role != null && role.getRoleName() != null) ? role.getRoleName() : "USER";
        return Collections.singleton(() -> "ROLE_" + roleName);
    }

    @Override public String getPassword() { return passwords; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
