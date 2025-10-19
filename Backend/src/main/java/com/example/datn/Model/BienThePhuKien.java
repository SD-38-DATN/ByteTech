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
//@Table(name = "BienThePhuKien")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class BienThePhuKien {
//    @Id
//    private String maSKUPhuKien;
//
//    private BigDecimal gia;
//    private Integer soLuong;
//    private Integer trangThai;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "maPhuKien")
////    @JsonBackReference("phukien-bienthe")
//    private PhuKien phuKien;
//
//    @OneToMany(mappedBy = "bienThePhuKien", cascade = CascadeType.ALL, orphanRemoval = true)
////    @JsonManagedReference("bienthe-thuoctinh")
//    private List<ThuocTinhPhuKien> thuocTinhPhuKienList;
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
@Table(name = "BienThePhuKien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BienThePhuKien {
    @Id
    private String maSKUPhuKien;

    private BigDecimal gia;
    private Integer soLuong;
    private Integer trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhuKien")
    @JsonBackReference("phukien-bienthe") // Ngăn lặp lại thông tin PhuKien
    private PhuKien phuKien;

//    @OneToMany(mappedBy = "bienThePhuKien", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @JsonManagedReference("bienthe-thuoctinh") // Cho phép hiển thị danh sách thuộc tính
//    private List<ThuocTinhPhuKien> thuocTinhPhuKienList;


    @OneToMany(
            mappedBy = "bienThePhuKien",
            cascade = CascadeType.ALL, // ✅ THÊM DÒNG NÀY
            orphanRemoval = true       // ✅ THÊM DÒNG NÀY
    )
    @JsonManagedReference("bienthe-thuoctinh") // Cho phép hiển thị danh sách thuộc tính
    private List<ThuocTinhPhuKien> thuocTinhPhuKienList = new ArrayList<>(); // Khởi tạo list
}