//package com.example.datn.Model;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@Entity
//@Table(name = "PhuKien")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class PhuKien {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer maPhuKien;
//
//    private String tenPhuKien;
//    private String moTa;
//
//    private String thuongHieu ;
//
//    private Integer soLuong;
//    private BigDecimal gia;
//    private Integer trangThai;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "maSanPham")
////    @JsonBackReference("sanpham-phukien")
//    private SanPham sanPham;
//
//    @OneToMany(mappedBy = "phuKien", cascade = CascadeType.ALL, orphanRemoval = true)
////    @JsonManagedReference("phukien-bienthe")
//    private List<BienThePhuKien> bienThePhuKienList;
//
//    @ManyToOne
//    @JoinColumn(name = "maDanhMucPhuKien")
//    private DanhMucPhuKien danhMucPhuKien;
//}
package com.example.datn.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PhuKien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhuKien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maPhuKien;

    private String tenPhuKien;
    private String moTa;
    private String thuongHieu;
    private Integer soLuong;
    private BigDecimal gia;
    private Integer trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSanPham")
    @JsonBackReference("sanpham-phukien") // Ngăn lặp lại thông tin SanPham
    private SanPham sanPham;

//    @OneToMany(mappedBy = "phuKien", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @JsonManagedReference("phukien-bienthe") // Cho phép hiển thị danh sách biến thể
//    private List<BienThePhuKien> bienThePhuKienList;

    @OneToMany(
            mappedBy = "phuKien",
            cascade = CascadeType.ALL, // ✅ THÊM DÒNG NÀY
            orphanRemoval = true       // ✅ THÊM DÒNG NÀY
    )
     @JsonManagedReference("phukien-bienthe") // Cho phép hiển thị danh sách biến thể
    private List<BienThePhuKien> bienThePhuKienList = new ArrayList<>(); // Khởi tạo list

    @ManyToOne
    @JoinColumn(name = "maDanhMucPhuKien")
    private DanhMucPhuKien danhMucPhuKien;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user; // <-- THÊM CÁI NÀY
}
