package com.example.datn.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DanhMucPhuKien")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanhMucPhuKien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDanhMucPhuKien;

    private String tenDanhMucPhuKien;

    private String moTa;
}
