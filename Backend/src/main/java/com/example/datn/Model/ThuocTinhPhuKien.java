//package com.example.datn.Model;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Table(name = "ThuocTinhPhuKien")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class ThuocTinhPhuKien {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer maThuocTinhPhuKien;
//
//    private String tenThuocTinh;
//    private String giaTriThuocTinh;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "maSKUPhuKien")
////    @JsonBackReference("bienthe-thuoctinh")
//    private BienThePhuKien bienThePhuKien;
//}
package com.example.datn.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ThuocTinhPhuKien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThuocTinhPhuKien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maThuocTinhPhuKien;

    private String tenThuocTinh;
    private String giaTriThuocTinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSKUPhuKien")
    @JsonBackReference("bienthe-thuoctinh") // Ngăn lặp lại thông tin BienThePhuKien
    private BienThePhuKien bienThePhuKien;
}