package com.example.datn.Repository.banhangtaiquay;


import com.example.datn.DTO.AdminKhuyenMai.VariantSummaryDTO;
import com.example.datn.Model.BienTheSanPham;
import com.example.datn.Model.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BienTheSanPhamBanHangTaiQuayRepository extends JpaRepository<BienTheSanPham, String> {
    List<BienTheSanPham> findBySanPham_MaSanPham(Integer maSanPham);

    Optional<BienTheSanPham> findByMaSKU(String maSKU);


    List<BienTheSanPham> findByKhuyenMai(KhuyenMai khuyenMai);

    @Query("SELECT new com.example.datn.DTO.AdminKhuyenMai.VariantSummaryDTO(b.maSKU, s.tenSanPham) " +
            "FROM BienTheSanPham b JOIN b.sanPham s")
    List<VariantSummaryDTO> findAllVariantSummaries();

    // Thêm method cho bán hàng tại quầy
    List<BienTheSanPham> findByMaSKUContainingIgnoreCase(String maSKU);
    
    // Load BienTheSanPham với đầy đủ thuộc tính (chỉ trạng thái = 1)
    @Query("SELECT b FROM BienTheSanPham b LEFT JOIN FETCH b.thuocTinhList WHERE b.maSKU = :maSKU AND b.trangThai = 1")
    Optional<BienTheSanPham> findByMaSKUWithThuocTinh(String maSKU);
    
    // Native query để kiểm tra thuộc tính
    @Query(value = "SELECT t.tenThuocTinh, t.tenThuocTinhBienThe FROM ThuocTinh t WHERE t.maSKU = :maSKU", nativeQuery = true)
    List<Object[]> findThuocTinhByMaSKU(String maSKU);
    
    // Load tất cả BienTheSanPham với đầy đủ thuộc tính (chỉ trạng thái = 1)
    @Query("SELECT DISTINCT b FROM BienTheSanPham b LEFT JOIN FETCH b.thuocTinhList WHERE b.trangThai = 1")
    List<BienTheSanPham> findAllWithThuocTinh();
    
    // Tìm kiếm BienTheSanPham theo SKU với đầy đủ thuộc tính (chỉ trạng thái = 1)
    @Query("SELECT DISTINCT b FROM BienTheSanPham b LEFT JOIN FETCH b.thuocTinhList WHERE LOWER(b.maSKU) LIKE LOWER(CONCAT('%', :maSKU, '%')) AND b.trangThai = 1")
    List<BienTheSanPham> findByMaSKUContainingIgnoreCaseWithThuocTinh(String maSKU);
    
    // ✅ MỚI: Tìm kiếm BienTheSanPham theo SKU chính xác với đầy đủ thuộc tính (chỉ trạng thái = 1)
    @Query("SELECT DISTINCT b FROM BienTheSanPham b LEFT JOIN FETCH b.thuocTinhList WHERE b.maSKU = :maSKU AND b.trangThai = 1")
    List<BienTheSanPham> findByMaSKUExactWithThuocTinh(String maSKU);

}
