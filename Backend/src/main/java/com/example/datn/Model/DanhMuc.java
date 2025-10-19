package com.example.datn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "DanhMuc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDanhMuc;

    private String tenDanhMuc;
    private String moTa;

    @OneToMany(mappedBy = "danhMuc")

    private List<SanPham> sanPhamList;

    @OneToMany(mappedBy = "danhMuc")
    private List<LoaiThongSo> loaiThongSoList;
}
