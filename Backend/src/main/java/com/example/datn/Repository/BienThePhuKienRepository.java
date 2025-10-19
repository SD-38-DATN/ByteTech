package com.example.datn.Repository;


import com.example.datn.Model.BienThePhuKien;
import com.example.datn.Model.BienTheSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BienThePhuKienRepository extends JpaRepository<BienThePhuKien, String> {

     Optional<BienThePhuKien> findByMaSKUPhuKien(String maSKU);

     List<BienThePhuKien> findByPhuKien_MaPhuKien(Integer maPhuKien);
}
