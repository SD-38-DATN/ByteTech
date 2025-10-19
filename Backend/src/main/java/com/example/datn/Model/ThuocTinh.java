package com.example.datn.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ThuocTinh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuocTinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maThuocTinh;

    private String tenThuocTinh;
    private String tenThuocTinhBienThe;
    private Integer trangThai;

    @ManyToOne
    @JoinColumn(name = "maSKU")
    private BienTheSanPham bienTheSanPham;
}
