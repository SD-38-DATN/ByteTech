package com.example.datn.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BienTheSanPham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BienTheSanPham {
    @Id
    private String maSKU;

    private BigDecimal gia;


    private BigDecimal giaKhongKhuyenMai;
    private Integer soLuong;
    private Integer trangThai;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "maKhuyenMai")
    private KhuyenMai khuyenMai;

//    @OneToMany(mappedBy = "bienTheSanPham")
//    private List<ThuocTinh> thuocTinhList;

    @OneToMany(mappedBy = "bienTheSanPham", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ThuocTinh> thuocTinhList = new ArrayList<>(); // Khởi tạo danh sách để không bao giờ bị null


    @OneToMany(mappedBy = "bienThe")
    private List<GioHangChiTiet> gioHangChiTietList;

    @OneToMany(mappedBy = "bienTheSanPham")
    private List<ChiTietDonHang> chiTietDonHangList;
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
