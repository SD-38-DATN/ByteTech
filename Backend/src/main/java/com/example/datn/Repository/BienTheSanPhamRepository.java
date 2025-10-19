package com.example.datn.Repository;


import com.example.datn.DTO.AdminKhuyenMai.VariantSummaryDTO;
import com.example.datn.Model.BienTheSanPham;
import com.example.datn.Model.KhuyenMai;
import com.example.datn.Model.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BienTheSanPhamRepository extends JpaRepository<BienTheSanPham, String> {
    List<BienTheSanPham> findBySanPham_MaSanPham(Integer maSanPham);

    Optional<BienTheSanPham> findByMaSKU(String maSKU);


    List<BienTheSanPham> findByKhuyenMai(KhuyenMai khuyenMai);

    @Query("SELECT new com.example.datn.DTO.AdminKhuyenMai.VariantSummaryDTO(b.maSKU, s.tenSanPham) " +
            "FROM BienTheSanPham b JOIN b.sanPham s")
    List<VariantSummaryDTO> findAllVariantSummaries();

}
