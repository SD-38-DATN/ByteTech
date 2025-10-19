package com.example.datn.Model;



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietDonHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "maDonHang")
    private DonHang donHang;   // tham chiếu đến DonHang

    @ManyToOne
    @JoinColumn(name = "maSKU")
    private BienTheSanPham bienTheSanPham;  // tham chiếu đến biến thể sản phẩm

    @ManyToOne
    @JoinColumn(name = "maSKUPhuKien")
    private BienThePhuKien bienThePhuKien;   // tham chiếu biến thể sản phẩm


    @ManyToOne
    @JoinColumn(name = "imei_id")
    private IMEI imei; // Liên kết tới sản phẩm cụ thể qua IMEI

    private Integer soLuong;

    private BigDecimal gia;


}
