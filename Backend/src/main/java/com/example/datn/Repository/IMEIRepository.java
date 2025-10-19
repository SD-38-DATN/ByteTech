package com.example.datn.Repository;

import com.example.datn.Model.IMEI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMEIRepository extends JpaRepository<IMEI , Integer> {

    Optional<IMEI> findByImei(String imei);



    boolean existsByImei(String imei);

    // Tìm IMEI theo mã (trừ id hiện tại, dùng cho update)
    Optional<IMEI> findByImeiAndIdNot(String imei, Integer id);

    // --- Thêm 2 phương thức mới ---
    long countByBienTheSanPham_MaSKU(String maSKU);
    long countByBienThePhuKien_MaSKUPhuKien(String maSKUPhuKien);
}
