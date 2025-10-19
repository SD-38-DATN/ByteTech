package com.example.datn.Repository;


import com.example.datn.Model.BienTheSanPham;
import com.example.datn.Model.GioHang;
import com.example.datn.Model.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {

    Optional<GioHangChiTiet> findByGioHangAndBienThe_MaSKU(GioHang gioHang, String maSKU);


    // BỔ SUNG PHƯƠNG THỨC NÀY
    Optional<GioHangChiTiet> findByGioHangAndBienThePhuKien_MaSKUPhuKien(GioHang gioHang, String maSKUPhuKien);

    List<GioHangChiTiet> findByGioHang_MaGioHang(Integer maGioHang);

    List<GioHangChiTiet> findByGioHang(GioHang gioHang);
}
