package com.example.datn.Model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "UserVoucher")
public class UserVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // mapping tới bảng Users
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

    // mapping tới bảng Voucher
    @ManyToOne
    @JoinColumn(name = "voucherId")
    private Voucher voucher;

    @Column(name = "soLanSuDung")
    private Integer soLanSuDung;

    @Column(name = "ngayNhan")
    private LocalDateTime ngayNhan = LocalDateTime.now();


    private Integer trangThai ;

    @OneToMany(mappedBy = "userVoucher")
    private List<DonHang> donHangList;
}

