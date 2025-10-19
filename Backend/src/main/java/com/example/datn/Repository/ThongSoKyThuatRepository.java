package com.example.datn.Repository;

import com.example.datn.Model.ThongSoKyThuat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThongSoKyThuatRepository extends JpaRepository<ThongSoKyThuat, Integer> {
    List<ThongSoKyThuat> findByLoaiThongSo_LoaiThongSoId(Integer loaiThongSoId);

    List<ThongSoKyThuat> findBySanPham_MaSanPham(Integer maSanPham);

    List<ThongSoKyThuat> findByPhuKien_MaPhuKien(Integer maPhuKien);
    boolean existsByLoaiThongSo_LoaiThongSoId(Integer loaiThongSoId);
}
