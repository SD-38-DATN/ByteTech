package com.example.datn.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "KhuyenMai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKhuyenMai;

    private String tenKhuyenMai;
    private String moTa;


    private Integer loaiGiam;
    private Double giaTriGiam;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer trangThai; // 1=active, 0=inactive

    @OneToMany(mappedBy = "khuyenMai")
    @JsonIgnore
    private List<BienTheSanPham> bienTheSanPhamList;
}
