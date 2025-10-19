package com.example.datn.Model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DonHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDonHang;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;   // tham chiếu đến bảng Users

    private LocalDateTime ngayDat = LocalDateTime.now();

//    private String trangThai = "Chờ xác nhận";


    private Integer trangThai = 0; // ✅ Mặc định là 0 nếu không được cung cấp giá trị khác


    private BigDecimal tongTien;

    private String diaChiGiaoHang;

    private String soDienThoai;

    private String phuongThucThanhToan;

    private String ghiChu;

    @OneToMany(mappedBy = "donHang")
    private List<ChiTietDonHang> chiTietDonHangs;

//    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>(); // ✅ KHỞI TẠO Ở ĐÂY

    // File: DonHang.java

    @ManyToOne
    @JoinColumn(name = "userVoucherId")
    private UserVoucher userVoucher ;
}
