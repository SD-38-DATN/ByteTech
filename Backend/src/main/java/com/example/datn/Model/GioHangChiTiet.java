package com.example.datn.Model;



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "GioHangChiTiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "maGioHang")
    private GioHang gioHang;

    @ManyToOne
    @JoinColumn(name = "maSKU")
    private BienTheSanPham bienThe;   // tham chiếu biến thể sản phẩm

    @ManyToOne
    @JoinColumn(name = "maSKUPhuKien")
    private BienThePhuKien bienThePhuKien;   // tham chiếu biến thể sản phẩm

    private Integer soLuong;

    private BigDecimal gia;   // giá lúc thêm vào giỏ
}
