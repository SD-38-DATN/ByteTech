package com.example.datn.Model;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "IMEI")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IMEI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "maSKU")
    private BienTheSanPham bienTheSanPham; // Liên kết tới biến thể sản phẩm


    @ManyToOne
    @JoinColumn(name = "maSKUPhuKien")
    private BienThePhuKien bienThePhuKien; // Liên kết tới biến thể sản phẩm

    @Column(name = "imei")
    private String imei; // Số IMEI/Serial, phải là duy nhất

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngayNhapKho")
    private LocalDateTime ngayNhapKho;

    @Column(name = "trangThai")
    private Integer trangThai; // 1 = Trong kho, 0 = Đã bán, etc.


}