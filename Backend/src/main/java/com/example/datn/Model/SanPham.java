package com.example.datn.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SanPham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSanPham;

    private String tenSanPham;
    private String moTa;

    private String thuongHieu;
    private Integer soLuong;
    private BigDecimal gia;
    private Integer trangThai;

    @ManyToOne
    @JoinColumn(name = "maDanhMuc")
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

//    @OneToMany(mappedBy = "sanPham")
//    private List<BienTheSanPham> bienTheSanPhamList ;
// Thêm: cascade = CascadeType.ALL, orphanRemoval = true
@OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
@JsonManagedReference
private List<BienTheSanPham> bienTheSanPhamList = new ArrayList<>();

    // Thêm đoạn này để liên kết với bảng ThongSoKyThuat



    // Thêm đoạn này để liên kết với bảng PhuKien
    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PhuKien> phuKienList;
    // ==========================================================
    @Transient
    public String getGiaHienThi() {
        if (gia == null) return "0 đ";
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
        formatter.setGroupingUsed(true);
        formatter.setGroupingSize(3);
        // Thay dấu phẩy bằng dấu chấm
        return formatter.format(gia).replace(",", ".") + " đ";
    }
}
