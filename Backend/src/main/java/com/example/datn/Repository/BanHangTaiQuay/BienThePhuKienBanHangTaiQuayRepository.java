package com.example.datn.Repository.BanHangTaiQuay;

import com.example.datn.Model.BienThePhuKien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BienThePhuKienBanHangTaiQuayRepository extends JpaRepository<BienThePhuKien, String> {

    Optional<BienThePhuKien> findByMaSKUPhuKien(String maSKU);

    List<BienThePhuKien> findByPhuKien_MaPhuKien(Integer maPhuKien);
    // Tìm biến thể phụ kiện theo maSKU với thuộc tính (chỉ trạng thái = 1)
    @Query("SELECT b FROM BienThePhuKien b LEFT JOIN FETCH b.thuocTinhPhuKienList WHERE b.maSKUPhuKien = :maSKU AND b.trangThai = 1")
    Optional<BienThePhuKien> findByMaSKUPhuKienWithThuocTinh(String maSKU);
    
    // Tìm tất cả biến thể phụ kiện với thuộc tính (chỉ trạng thái = 1)
    @Query("SELECT DISTINCT b FROM BienThePhuKien b LEFT JOIN FETCH b.thuocTinhPhuKienList WHERE b.trangThai = 1")
    List<BienThePhuKien> findAllWithThuocTinh();
    
    // Tìm biến thể phụ kiện theo maSKU chứa keyword với thuộc tính (chỉ trạng thái = 1)
    @Query("SELECT DISTINCT b FROM BienThePhuKien b LEFT JOIN FETCH b.thuocTinhPhuKienList WHERE LOWER(b.maSKUPhuKien) LIKE LOWER(CONCAT('%', :maSKU, '%')) AND b.trangThai = 1")
    List<BienThePhuKien> findByMaSKUPhuKienContainingIgnoreCaseWithThuocTinh(String maSKU);
    
    // ✅ MỚI: Tìm biến thể phụ kiện theo maSKU chính xác với thuộc tính (chỉ trạng thái = 1)
    @Query("SELECT DISTINCT b FROM BienThePhuKien b LEFT JOIN FETCH b.thuocTinhPhuKienList WHERE b.maSKUPhuKien = :maSKU AND b.trangThai = 1")
    List<BienThePhuKien> findByMaSKUPhuKienExactWithThuocTinh(String maSKU);
    
    // Native query để debug thuộc tính phụ kiện
    @Query(value = "SELECT t.tenThuocTinh, t.giaTriThuocTinh FROM ThuocTinhPhuKien t WHERE t.maSKUPhuKien = :maSKU", nativeQuery = true)
    List<Object[]> findThuocTinhPhuKienByMaSKU(String maSKU);
}
