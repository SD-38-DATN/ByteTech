package com.example.datn.Model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "FeedBack")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Người dùng gửi phản hồi
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    // Sản phẩm được phản hồi
    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;


    @ManyToOne
    @JoinColumn(name = "maPhuKien")
    private PhuKien phuKien;




    // Nội dung phản hồi
    @Column(name = "noiDung", length = 1000)
    private String noiDung;

    // Điểm đánh giá 1-5
    @Column(name = "danhGia")
    private Integer danhGia;

    // Ngày đánh giá
    @Column(name = "ngayDanhGia")
    private LocalDateTime ngayDanhGia ;

    @PrePersist
    public void prePersist() {
        if (ngayDanhGia == null) {
            ngayDanhGia = LocalDateTime.now();
        }
    }
}
