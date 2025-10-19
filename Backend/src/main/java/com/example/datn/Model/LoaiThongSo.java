package com.example.datn.Model;



import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "LoaiThongSo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoaiThongSo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer  loaiThongSoId;

    @Column(nullable = false, length = 255)
    private String tenLoaiThongSo; // Ví dụ: "Kích thước", "Chất liệu"

    // Quan hệ với SanPham (nhiều loại thông số thuộc 1 sản phẩm)
    @ManyToOne
    @JoinColumn(name = "maDanhMuc")
    private DanhMuc danhMuc;


    @ManyToOne
    @JoinColumn(name = "maDanhMucPhuKien")
    private DanhMucPhuKien danhMucPhuKien;


    // Quan hệ với ThongSoKyThuat (1 loại thông số có nhiều giá trị chi tiết)
    @OneToMany(mappedBy = "loaiThongSo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ThongSoKyThuat> thongSoKyThuatList;
}
