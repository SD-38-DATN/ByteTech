package com.example.datn.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    private Integer gioiTinh; // 1=Nam, 0=Nữ
    private String email;
    private String soDienThoai;
    @Column(name = "diaChiGiaoHang")
    private String diaChiGiaoHang;

    //private Integer roleID; // 1 = user, 2 = admin, 3 = nhân viên


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
        // Trả về role dạng GrantedAuthority
        return Collections.singleton(() -> "ROLE_" + role.getRoleName());
    }


    @Override
    public String getPassword() {
        return passwords;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Các phương thức còn lại (mặc định true)
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
