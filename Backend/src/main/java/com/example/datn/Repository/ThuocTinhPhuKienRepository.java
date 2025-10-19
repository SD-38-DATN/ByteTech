package com.example.datn.Repository;


import com.example.datn.Model.ThuocTinhPhuKien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThuocTinhPhuKienRepository extends JpaRepository<ThuocTinhPhuKien, Integer> {
    List<ThuocTinhPhuKien> findByBienThePhuKien_MaSKUPhuKien(String maSKUPhuKien);
}