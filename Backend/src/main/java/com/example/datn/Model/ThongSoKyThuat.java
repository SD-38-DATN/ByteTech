package com.example.datn.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ThongSoKyThuat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThongSoKyThuat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maThongSo;

    private String tenThongSo;
    private String giaTriThongSo;

    private Integer trangThai ;

    @ManyToOne
    @JoinColumn(name = "loaiThongSoId")
//    @JsonBackReference // Giúp tránh lỗi lặp vô hạn khi serialize JSON
    private LoaiThongSo loaiThongSo;


    @ManyToOne
    @JoinColumn(name = "maSanPham")
//    @JsonBackReference // Giúp tránh lỗi lặp vô hạn khi serialize JSON
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "maPhuKien") // Tên cột foreign key trong bảng ThongSoKyThuat
    private PhuKien phuKien;

}
